package com.example.application;

import static akka.javasdk.testkit.TestModelProvider.AutonomousAgentTools.completeTask;
import static akka.javasdk.testkit.TestModelProvider.AutonomousAgentTools.startScriptedConversation;
import static akka.javasdk.testkit.TestModelProvider.AutonomousAgentTools.submitTurn;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.agent.task.TaskStatus;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import akka.javasdk.testkit.TestModelProvider.AutonomousAgentTools.ParticipantRef;
import akka.javasdk.testkit.TestModelProvider.AutonomousAgentTools.ScriptStep;
import com.example.api.AutonomousReviewModerationEndpoint;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class ReviewModeratorAutonomousAgentIntegrationTest extends TestKitSupport {

  private final TestModelProvider moderatorModel = new TestModelProvider();
  private final TestModelProvider technicalModel = new TestModelProvider();
  private final TestModelProvider policyModel = new TestModelProvider();

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withAdditionalConfig("akka.javasdk.agent.openai.api-key = n/a")
        .withModelProvider(ReviewModeratorAutonomousAgent.class, moderatorModel)
        .withModelProvider(TechnicalReviewPanelistAutonomousAgent.class, technicalModel)
        .withModelProvider(PolicyReviewPanelistAutonomousAgent.class, policyModel);
  }

  @Test
  void endpointStartsModeratorThatRunsScriptedConversationAndCompletesTypedReview() {
    moderatorModel
        .whenMessage(
            message -> message.contains("policy automation proposal") && !message.contains("Continue working"))
        .reply(
            startScriptedConversation(
                "Review policy automation proposal",
                List.of(
                    new ParticipantRef(
                        "technical-review-panelist-autonomous-agent", "technical-reviewer"),
                    new ParticipantRef(
                        "policy-review-panelist-autonomous-agent", "policy-reviewer")),
                List.of(
                    new ScriptStep(
                        "technical-reviewer",
                        "Assess implementation risk and missing technical evidence."),
                    new ScriptStep(
                        "policy-reviewer", "Assess approval needs and authority boundaries."))));

    technicalModel.fixedResponse(
        new TestModelProvider.AiResponse(
            submitTurn(
                "The proposal is technically feasible if rollout is limited and monitored with audit events.")));

    policyModel.fixedResponse(
        new TestModelProvider.AiResponse(
            submitTurn(
                "The proposal requires an explicit approval boundary before any policy-changing side effect.")));

    moderatorModel
        .whenMessage(message -> message.contains("Continue working"))
        .reply(
            completeTask(
                new ReviewModerationTasks.ModeratedReview(
                    "policy automation proposal",
                    "Approve for a limited pilot only after adding approval and audit controls.",
                    List.of(
                        "Technical reviewer: feasible with monitored rollout and audit events.",
                        "Policy reviewer: policy-changing side effects require explicit approval."),
                    true)));

    var response =
        httpClient
            .POST("/autonomous/review-moderation")
            .withRequestBody(
                new AutonomousReviewModerationEndpoint.ReviewRequest(
                    "policy automation proposal"))
            .responseBodyAs(AutonomousReviewModerationEndpoint.ReviewTaskResponse.class)
            .invoke();

    assertTrue(response.status().isSuccess());
    assertFalse(response.body().taskId().isBlank());
    assertFalse(response.body().agentInstanceId().isBlank());
    assertEquals("review-moderator-autonomous-agent", response.body().agentComponentId());

    Awaitility.await()
        .ignoreExceptions()
        .atMost(30, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var snapshot =
                  componentClient.forTask(response.body().taskId()).get(ReviewModerationTasks.REVIEW);
              assertEquals(TaskStatus.COMPLETED, snapshot.status());
              var result = snapshot.result().orElseThrow();
              assertEquals("policy automation proposal", result.subject());
              assertTrue(result.approved());
              assertEquals(2, result.reviewerFindings().size());
              assertTrue(result.assessment().contains("limited pilot"));
            });
  }
}
