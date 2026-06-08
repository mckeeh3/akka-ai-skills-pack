package ai.first.application.coreapp.audit;

import static akka.javasdk.testkit.TestModelProvider.AutonomousAgentTools.completeTask;
import static akka.javasdk.testkit.TestModelProvider.AutonomousAgentTools.failTask;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class AuditTraceSummaryAutonomousAgentTest extends TestKitSupport {
  private final TestModelProvider model = new TestModelProvider();

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withAdditionalConfig("akka.javasdk.agent.openai.api-key = n/a")
        .withModelProvider(AuditTraceSummaryAutonomousAgent.class, model);
  }

  @Test
  void completesTypedAuditTraceSummaryTaskThroughAutonomousAgentTestModelProvider() {
    model.fixedResponse(new TestModelProvider.AiResponse(completeTask(result())));

    var taskId = componentClient
        .forAutonomousAgent(AuditTraceSummaryAutonomousAgent.class, "audit-trace-summary-test-agent")
        .runSingleTask(AuditTraceSummaryTasks.summarizeAuditWindowInstructions(request("audit-summary-1")));

    Awaitility.await().ignoreExceptions().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
      var snapshot = componentClient.forTask(taskId).get(AuditTraceSummaryTasks.SUMMARIZE_AUDIT_WINDOW);
      var result = snapshot.result().orElseThrow();
      assertEquals("audit-summary-1", result.summaryTaskId());
      assertEquals("tenant-1", result.tenantId());
      assertTrue(result.executiveSummary().contains("AutonomousAgent"));
      assertTrue(result.noDirectMutation());
      assertFalse(result.toString().toLowerCase().contains("api_key"));
      assertFalse(result.toString().toLowerCase().contains("secret="));
    });
  }

  @Test
  void failTaskMapsToFailedSnapshotWithoutModelLessSuccess() {
    model.fixedResponse(new TestModelProvider.AiResponse(failTask("provider/model/auditTraceSummaryEvidence.read unavailable; fail closed before audit summary findings")));

    var taskId = componentClient
        .forAutonomousAgent(AuditTraceSummaryAutonomousAgent.class, "audit-trace-summary-fail-test-agent")
        .runSingleTask(AuditTraceSummaryTasks.SUMMARIZE_AUDIT_WINDOW.instructions("Run audit summary; fail closed if provider/model/evidence is unavailable."));

    Awaitility.await().ignoreExceptions().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
      var snapshot = componentClient.forTask(taskId).get(AuditTraceSummaryTasks.SUMMARIZE_AUDIT_WINDOW);
      assertEquals(akka.javasdk.agent.task.TaskStatus.FAILED, snapshot.status());
      assertTrue(snapshot.failureReason().orElse("").contains("fail closed"));
      assertTrue(snapshot.result().isEmpty(), "failed AutonomousAgent tasks must not expose fake or model-less audit summary success results");
    });
  }

  private static AuditTraceSummaryTasks.AuditTraceSummaryRequest request(String summaryTaskId) {
    return new AuditTraceSummaryTasks.AuditTraceSummaryRequest(
        summaryTaskId,
        "tenant-1",
        null,
        "membership-1",
        "admin-1",
        Instant.parse("2026-05-01T00:00:00Z"),
        Instant.parse("2026-05-08T00:00:00Z"),
        List.of("admin_audit", "provider_readiness", "agent_work"),
        "corr-audit-summary-test",
        "audit.trace.summary_task.start",
        "test governed runtime context; advisory only, redacted, no direct mutation",
        List.of("auditTraceSummaryEvidence.read", "readSkill:audit-trace-summary-review"));
  }

  private static AuditTraceSummaryResult result() {
    var finding = new AuditTraceSummaryResult.AuditTraceSummaryFinding(
        "finding-1",
        "provider_readiness",
        "warning",
        "Provider fail-closed evidence",
        "Provider readiness traces show blocked model-backed work that needs authorized review.",
        List.of("auditTraceSummaryEvidence.read"),
        List.of("trace-provider-blocked"),
        "Review provider configuration and retry only after secure backend configuration is restored.",
        0.86,
        true);
    return new AuditTraceSummaryResult(
        "audit-summary-1",
        "tenant-1",
        null,
        "2026-05-01T00:00:00Z",
        "2026-05-08T00:00:00Z",
        "corr-audit-summary-test",
        AuditTraceSummaryResult.OverallRisk.REVIEW_REQUIRED,
        "Model-backed AutonomousAgent audit summary completed; human Audit/Trace review is required.",
        List.of(finding),
        List.of(finding),
        List.of(),
        List.of(),
        List.of("Open provider blocked attention and cited trace refs"),
        "Unauthorized or sensitive evidence is represented as not_found_or_redacted.",
        "JWTs, credential material, hidden prompt content, invitation tokens, and cross-tenant evidence were omitted.",
        true,
        "2026-05-08T00:00:01Z",
        List.of("auditTraceSummaryEvidence.read"),
        List.of("trace-provider-blocked"),
        List.of("modelRef:audit-trace"));
  }
}
