package com.example.application;

import static akka.javasdk.testkit.TestModelProvider.AutonomousAgentTools.completeTask;
import static akka.javasdk.testkit.TestModelProvider.AutonomousAgentTools.failTask;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.agent.task.TaskStatus;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class AuditTraceSummaryAutonomousAgentIntegrationTest extends TestKitSupport {
  private final TestModelProvider model = new TestModelProvider();

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withAdditionalConfig("akka.javasdk.agent.openai.api-key = n/a")
        .withModelProvider(AuditTraceSummaryAutonomousAgent.class, model);
  }

  @Test
  void completesTypedAuditTraceSummaryTaskThroughConcreteAutonomousAgentAndComponentClientRuntime() {
    model.fixedResponse(new TestModelProvider.AiResponse(completeTask(result("audit-summary-runtime"))));
    var runtime = new ComponentClientAuditTraceSummaryAutonomousAgentRuntime(componentClient, true, true, true);

    var start = runtime.start(request("audit-summary-runtime"));

    assertEquals(AuditTraceSummaryTaskProjection.Status.QUEUED, start.status());
    assertFalse(start.autonomousAgentTaskId().isBlank());
    assertTrue(start.traceIds().stream().anyMatch(trace -> trace.startsWith("autonomous_task:")));

    var projection = new AuditTraceSummaryTaskProjection(
        "audit-summary-runtime",
        start.autonomousAgentTaskId(),
        "tenant-1",
        null,
        "membership-1",
        "admin-1",
        "idem-1",
        start.status(),
        start.progressPercent(),
        start.summary(),
        null,
        null,
        List.of("auditTraceSummaryEvidence.read"),
        List.of(),
        start.traceIds(),
        null,
        null,
        Instant.parse("2026-05-08T00:00:00Z"));

    Awaitility.await().ignoreExceptions().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
      var projected = runtime.project(projection);
      assertEquals(AuditTraceSummaryTaskProjection.Status.COMPLETED_REVIEW_REQUIRED, projected.status());
      assertEquals("audit-summary-runtime", projected.result().summaryTaskId());
      assertTrue(projected.result().noDirectMutation());
      assertTrue(projected.traceIds().contains("worker.task.completed_review_required"));
      assertTrue(projected.traceIds().contains("workflow.audit_trace.summary_completed_review_required"));
      assertFalse(projected.result().toString().toLowerCase().contains("api_key"));
      assertFalse(projected.result().toString().toLowerCase().contains("secret="));
    });
  }

  @Test
  void failedProviderPathDoesNotExposeModelLessAuditSummarySuccess() {
    model.fixedResponse(new TestModelProvider.AiResponse(failTask("provider/model/auditTraceSummaryEvidence.read unavailable; fail closed before audit summary findings")));

    var taskId = componentClient
        .forAutonomousAgent(AuditTraceSummaryAutonomousAgent.class, "audit-trace-summary-fail-test-agent")
        .runSingleTask(AuditTraceSummaryTasks.SUMMARIZE_AUDIT_WINDOW.instructions("Run audit summary; fail closed if provider/model/evidence is unavailable."));

    Awaitility.await().ignoreExceptions().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
      var snapshot = componentClient.forTask(taskId).get(AuditTraceSummaryTasks.SUMMARIZE_AUDIT_WINDOW);
      assertEquals(TaskStatus.FAILED, snapshot.status());
      assertTrue(snapshot.failureReason().orElse("").contains("fail closed"));
      assertTrue(snapshot.result().isEmpty(), "failed AutonomousAgent tasks must not expose fake or model-less audit summary success results");
    });
  }

  @Test
  void missingRuntimeConfigBlocksWithoutCallingFakeSuccess() {
    var runtime = new ComponentClientAuditTraceSummaryAutonomousAgentRuntime(componentClient, false, true, true);
    var blocked = runtime.start(request("audit-summary-blocked"));

    assertEquals(AuditTraceSummaryTaskProjection.Status.BLOCKED_PROVIDER_OR_RUNTIME, blocked.status());
    assertEquals("blocked_provider_or_runtime", blocked.blockerCode());
    assertTrue(blocked.summary().contains("failed closed"));
    assertTrue(blocked.summary().contains("No deterministic/model-less fake success"));
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
        AuditTraceSummaryTasks.START_CAPABILITY,
        "modelProviderAlias=test; ToolPermissionBoundary grants auditTraceSummaryEvidence.read, readSkill, readReferenceDoc; advisory only; redaction required; no direct mutation",
        List.of("auditTraceSummaryEvidence.read", "readSkill:audit-trace-summary-review", "readReferenceDoc:audit-trace-summary-review"));
  }

  static AuditTraceSummaryResult result(String summaryTaskId) {
    var finding = new AuditTraceSummaryResult.Finding(
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
        summaryTaskId,
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
