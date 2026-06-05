package ai.first.application;

import static akka.javasdk.testkit.TestModelProvider.AutonomousAgentTools.completeTask;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.agent.task.TaskStatus;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import ai.first.api.AutonomousApprovalPipelineEndpoint;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class ApprovalPipelineAutonomousAgentIntegrationTest extends TestKitSupport {

  private final TestModelProvider pipelineModel = new TestModelProvider();

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withAdditionalConfig("akka.javasdk.agent.openai.api-key = n/a")
        .withModelProvider(ApprovalPipelineAutonomousAgent.class, pipelineModel);
  }

  @Test
  void completedExternalApprovalReleasesDependentAutonomousPublishTask() {
    pipelineModel
        .whenMessage(message -> message.contains("Investigate approval-gated request"))
        .reply(
            completeTask(
                new ApprovalPipelineTasks.InvestigationResult(
                    "Proceed after reviewer approval.",
                    List.of("risk-review-17", "tenant-export-policy"))));

    pipelineModel
        .whenMessage(message -> message.contains("Publish approved request"))
        .reply(
            completeTask(
                new ApprovalPipelineTasks.PublishedDecision(
                    "Approved export", "Export published after reviewer approval.", true)));

    var response = startPipeline("customer export for account acct-123");

    Awaitility.await()
        .ignoreExceptions()
        .atMost(15, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var snapshot =
                  componentClient
                      .forTask(response.investigationTaskId())
                      .get(ApprovalPipelineTasks.INVESTIGATE);
              assertEquals(TaskStatus.COMPLETED, snapshot.status());
              assertEquals("Proceed after reviewer approval.", snapshot.result().orElseThrow().recommendation());
            });

    componentClient.forTask(response.approvalTaskId()).assign("reviewer-1");
    componentClient
        .forTask(response.approvalTaskId())
        .complete(
            ApprovalPipelineTasks.APPROVAL,
            new ApprovalPipelineTasks.ApprovalDecision(
                "reviewer-1", "approved", "Evidence is sufficient."));
    componentClient
        .forAutonomousAgent(
            ApprovalPipelineAutonomousAgent.class, response.publishAgentInstanceId())
        .assignTasks(response.publishTaskId());

    Awaitility.await()
        .ignoreExceptions()
        .atMost(15, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var approval =
                  componentClient
                      .forTask(response.approvalTaskId())
                      .get(ApprovalPipelineTasks.APPROVAL);
              assertEquals(TaskStatus.COMPLETED, approval.status());
              assertEquals("reviewer-1", approval.result().orElseThrow().reviewer());

              var publish =
                  componentClient
                      .forTask(response.publishTaskId())
                      .get(ApprovalPipelineTasks.PUBLISH);
              assertEquals(TaskStatus.COMPLETED, publish.status());
              var result = publish.result().orElseThrow();
              assertTrue(result.published());
              assertEquals("Approved export", result.title());
            });
  }

  @Test
  void failedExternalApprovalPreventsDependentAutonomousPublishTaskFromRunning() {
    pipelineModel
        .whenMessage(message -> message.contains("Investigate approval-gated request"))
        .reply(
            completeTask(
                new ApprovalPipelineTasks.InvestigationResult(
                    "Proceed only with explicit approval.", List.of("approval-policy"))));

    var response = startPipeline("high-risk support export");

    Awaitility.await()
        .ignoreExceptions()
        .atMost(15, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var snapshot =
                  componentClient
                      .forTask(response.investigationTaskId())
                      .get(ApprovalPipelineTasks.INVESTIGATE);
              assertEquals(TaskStatus.COMPLETED, snapshot.status());
            });

    componentClient.forTask(response.approvalTaskId()).assign("reviewer-2");
    componentClient.forTask(response.approvalTaskId()).fail("approval denied by reviewer");

    Awaitility.await()
        .ignoreExceptions()
        .atMost(15, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var approval =
                  componentClient
                      .forTask(response.approvalTaskId())
                      .get(ApprovalPipelineTasks.APPROVAL);
              assertEquals(TaskStatus.FAILED, approval.status());
              assertTrue(approval.failureReason().orElseThrow().contains("approval denied"));

              var publish =
                  componentClient
                      .forTask(response.publishTaskId())
                      .get(ApprovalPipelineTasks.PUBLISH);
              assertTrue(
                  publish.status() == TaskStatus.CANCELLED || publish.status() == TaskStatus.PENDING,
                  "failed approval must not let the dependent publish task complete");
              assertTrue(publish.result().isEmpty());
            });
  }

  private AutonomousApprovalPipelineEndpoint.ApprovalPipelineResponse startPipeline(String topic) {
    var response =
        httpClient
            .POST("/autonomous/approval-pipeline")
            .withRequestBody(new AutonomousApprovalPipelineEndpoint.StartApprovalPipeline(topic))
            .responseBodyAs(AutonomousApprovalPipelineEndpoint.ApprovalPipelineResponse.class)
            .invoke();

    assertTrue(response.status().isSuccess());
    assertFalse(response.body().investigationTaskId().isBlank());
    assertFalse(response.body().approvalTaskId().isBlank());
    assertFalse(response.body().publishTaskId().isBlank());
    assertFalse(response.body().investigationAgentInstanceId().isBlank());
    assertFalse(response.body().publishAgentInstanceId().isBlank());
    assertEquals("approval-pipeline-autonomous-agent", response.body().agentComponentId());
    return response.body();
  }
}
