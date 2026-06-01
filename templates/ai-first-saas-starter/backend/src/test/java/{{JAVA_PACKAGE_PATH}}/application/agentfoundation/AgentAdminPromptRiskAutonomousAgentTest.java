package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import static akka.javasdk.testkit.TestModelProvider.AutonomousAgentTools.completeTask;
import static akka.javasdk.testkit.TestModelProvider.AutonomousAgentTools.failTask;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.PromptRiskReviewTask;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class AgentAdminPromptRiskAutonomousAgentTest extends TestKitSupport {
  private final TestModelProvider model = new TestModelProvider();

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withAdditionalConfig("akka.javasdk.agent.openai.api-key = n/a")
        .withModelProvider(AgentAdminPromptRiskAutonomousAgent.class, model);
  }

  @Test
  void completesTypedPromptRiskTaskThroughAutonomousAgentTestModelProvider() {
    model.fixedResponse(new TestModelProvider.AiResponse(completeTask(new PromptRiskAutonomousAgentResult(
        "prompt-risk-1",
        "tenant-1",
        null,
        AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID,
        "proposal-1",
        "Model-backed AutonomousAgent prompt-risk advisory result completed; Agent Admin human review is required.",
        PromptRiskAutonomousAgentResult.RiskLevel.HIGH,
        List.of(new PromptRiskAutonomousAgentResult.PromptRiskFinding("finding-1", PromptRiskAutonomousAgentResult.RiskLevel.HIGH, PromptRiskReviewTask.ArtifactKind.TOOL_PERMISSION_BOUNDARY, "agent-admin-tool-boundary", "ToolPermissionBoundary expansion", "Proposed side-effecting tool exposure requires human approval and activation remains blocked.", List.of("agentAdminEvidence.read"), true)),
        List.of(new PromptRiskAutonomousAgentResult.PromptRiskRecommendation("rec-1", "Reject or narrow tool-boundary expansion", "Side-effecting tool grants need separate approval and idempotency evidence.", true, List.of("agentAdminEvidence.read"))),
        List.of("ToolPermissionBoundary expansion requires human review"),
        List.of("agentAdminEvidence.read", "readSkill:agent-admin-prompt-risk-review"),
        List.of("trace-prompt", "trace-model"),
        "This result is advisory only; Agent Admin human review is required before activation or behavior changes."))));

    var taskId = componentClient
        .forAutonomousAgent(AgentAdminPromptRiskAutonomousAgent.class, "agent-admin-prompt-risk-test-agent")
        .runSingleTask(AgentAdminPromptRiskTasks.promptRiskInstructions(new AgentAdminPromptRiskTasks.PromptRiskReviewRequest(
            "prompt-risk-1",
            "tenant-1",
            null,
            AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID,
            "proposal-1",
            "admin-1",
            "corr-aa-test",
            AgentAdminPromptRiskReviewService.START_CAPABILITY,
            "test governed runtime context; advisory only and no direct mutation",
            List.of(new PromptRiskReviewTask.BehaviorArtifactDelta(PromptRiskReviewTask.ArtifactKind.TOOL_PERMISSION_BOUNDARY, "agent-admin-tool-boundary", 1, 2, "add side-effecting grant", "diff:proposal-1", "before", "after")),
            List.of("agentAdminEvidence.read"))));

    Awaitility.await().ignoreExceptions().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
      var snapshot = componentClient.forTask(taskId).get(AgentAdminPromptRiskTasks.PROMPT_RISK_REVIEW);
      var result = snapshot.result().orElseThrow();
      assertEquals("prompt-risk-1", result.taskId());
      assertEquals("tenant-1", result.tenantId());
      assertEquals("proposal-1", result.proposalId());
      assertTrue(result.summary().contains("AutonomousAgent"));
      assertTrue(result.safety().contains("advisory"));
      assertTrue(result.safety().contains("human review"));
      assertFalse(result.toString().toLowerCase().contains("api_key"));
    });
  }

  @Test
  void failTaskMapsToFailedSnapshotWithoutModelLessSuccess() {
    model.fixedResponse(new TestModelProvider.AiResponse(failTask("provider/model/tool-boundary unavailable; fail closed before prompt-risk findings")));

    var taskId = componentClient
        .forAutonomousAgent(AgentAdminPromptRiskAutonomousAgent.class, "agent-admin-prompt-risk-fail-test-agent")
        .runSingleTask(AgentAdminPromptRiskTasks.PROMPT_RISK_REVIEW.instructions("Run prompt-risk review for prompt-risk-fail; fail closed if provider/model/tool-boundary is unavailable."));

    Awaitility.await().ignoreExceptions().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
      var snapshot = componentClient.forTask(taskId).get(AgentAdminPromptRiskTasks.PROMPT_RISK_REVIEW);
      assertEquals(akka.javasdk.agent.task.TaskStatus.FAILED, snapshot.status());
      assertTrue(snapshot.failureReason().orElse("").contains("fail closed"));
      assertTrue(snapshot.result().isEmpty(), "failed AutonomousAgent tasks must not expose fake or model-less prompt-risk success results");
    });
  }
}
