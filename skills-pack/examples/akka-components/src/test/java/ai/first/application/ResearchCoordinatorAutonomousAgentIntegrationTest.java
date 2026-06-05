package ai.first.application;

import static akka.javasdk.testkit.TestModelProvider.AutonomousAgentTools.completeTask;
import static akka.javasdk.testkit.TestModelProvider.AutonomousAgentTools.delegateTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.agent.task.TaskStatus;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import ai.first.api.AutonomousResearchEndpoint;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class ResearchCoordinatorAutonomousAgentIntegrationTest extends TestKitSupport {

  private final TestModelProvider coordinatorModel = new TestModelProvider();
  private final TestModelProvider workerModel = new TestModelProvider();

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withAdditionalConfig("akka.javasdk.agent.openai.api-key = n/a")
        .withModelProvider(ResearchCoordinatorAutonomousAgent.class, coordinatorModel)
        .withModelProvider(ResearchWorkerAutonomousAgent.class, workerModel);
  }

  @Test
  void endpointStartsCoordinatorThatDelegatesToWorkerAndCompletesTypedBrief() {
    coordinatorModel
        .whenMessage(
            message -> message.contains("quantum computing") && !message.contains("Continue working"))
        .reply(
            delegateTo(
                ResearchCoordinationTasks.FINDINGS,
                ResearchWorkerAutonomousAgent.class,
                "Research quantum computing adoption signals."));

    workerModel.fixedResponse(
        new TestModelProvider.AiResponse(
            completeTask(
                new ResearchCoordinationTasks.ResearchFindings(
                    "quantum computing",
                    List.of(
                        "Cloud access has made early experimentation easier.",
                        "Error correction remains a major production-readiness constraint."),
                    List.of("industry-roadmap", "lab-report")))));

    coordinatorModel
        .whenMessage(message -> message.contains("Continue working"))
        .reply(
            completeTask(
                new ResearchCoordinationTasks.ResearchBrief(
                    "Quantum Computing Adoption Brief",
                    "Quantum computing adoption is increasing through cloud experimentation, but production use remains gated by error correction progress.",
                    List.of(
                        "Cloud access has made early experimentation easier.",
                        "Error correction remains a major production-readiness constraint."))));

    var response =
        httpClient
            .POST("/autonomous/research")
            .withRequestBody(new AutonomousResearchEndpoint.ResearchRequest("quantum computing"))
            .responseBodyAs(AutonomousResearchEndpoint.ResearchTaskResponse.class)
            .invoke();

    assertTrue(response.status().isSuccess());
    assertFalse(response.body().taskId().isBlank());
    assertFalse(response.body().agentInstanceId().isBlank());
    assertEquals("research-coordinator-autonomous-agent", response.body().agentComponentId());

    Awaitility.await()
        .ignoreExceptions()
        .atMost(30, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var snapshot =
                  componentClient.forTask(response.body().taskId()).get(ResearchCoordinationTasks.BRIEF);
              assertEquals(TaskStatus.COMPLETED, snapshot.status());
              var result = snapshot.result().orElseThrow();
              assertEquals("Quantum Computing Adoption Brief", result.title());
              assertEquals(2, result.keyFindings().size());
              assertTrue(result.summary().contains("cloud experimentation"));
            });
  }
}
