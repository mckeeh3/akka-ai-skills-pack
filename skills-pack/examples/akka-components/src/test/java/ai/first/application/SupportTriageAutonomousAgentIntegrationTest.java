package ai.first.application;

import static akka.javasdk.testkit.TestModelProvider.AutonomousAgentTools.completeTask;
import static akka.javasdk.testkit.TestModelProvider.AutonomousAgentTools.handoffTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.agent.task.TaskStatus;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import ai.first.api.AutonomousSupportTriageEndpoint;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class SupportTriageAutonomousAgentIntegrationTest extends TestKitSupport {

  private final TestModelProvider triageModel = new TestModelProvider();
  private final TestModelProvider billingModel = new TestModelProvider();

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withAdditionalConfig("akka.javasdk.agent.openai.api-key = n/a")
        .withModelProvider(SupportTriageAutonomousAgent.class, triageModel)
        .withModelProvider(BillingSupportSpecialistAutonomousAgent.class, billingModel);
  }

  @Test
  void triageHandsOffSameTaskAndSpecialistCompletesTypedResolution() {
    triageModel.fixedResponse(
        new TestModelProvider.AiResponse(
            handoffTo(
                BillingSupportSpecialistAutonomousAgent.class,
                "Billing dispute: customer reports duplicate charge on invoice INV-4242.")));

    billingModel.fixedResponse(
        new TestModelProvider.AiResponse(
            completeTask(
                new HandoffTriageTasks.SupportResolution(
                    "billing",
                    "Duplicate charge on invoice INV-4242 was verified and a refund was issued.",
                    true))));

    var response =
        httpClient
            .POST("/autonomous/support-triage")
            .withRequestBody(
                new AutonomousSupportTriageEndpoint.SupportRequest(
                    "I was charged twice on invoice INV-4242. Please fix it."))
            .responseBodyAs(AutonomousSupportTriageEndpoint.SupportTaskResponse.class)
            .invoke();

    assertTrue(response.status().isSuccess());
    assertFalse(response.body().taskId().isBlank());
    assertFalse(response.body().agentInstanceId().isBlank());
    assertEquals("support-triage-autonomous-agent", response.body().agentComponentId());

    Awaitility.await()
        .ignoreExceptions()
        .atMost(30, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var snapshot =
                  componentClient.forTask(response.body().taskId()).get(HandoffTriageTasks.RESOLVE);
              assertEquals(TaskStatus.COMPLETED, snapshot.status());
              var result = snapshot.result().orElseThrow();
              assertEquals("billing", result.category());
              assertTrue(result.resolved());
              assertTrue(result.resolution().contains("refund"));
            });
  }
}
