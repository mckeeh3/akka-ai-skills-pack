package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class AuditTraceSummaryRuntimeGuardrailTest {
  @Test
  void resultRuleRejectsSecretsRawPromptsAndMissingEvidence() {
    var rule = new AuditTraceSummaryResultRule();
    assertEquals("Accepted", rule.onComplete(AuditTraceSummaryAutonomousAgentIntegrationTest.result("safe")).getClass().getSimpleName());

    var unsafeFinding = new AuditTraceSummaryResult.Finding(
        "finding-unsafe", "provider_readiness", "critical", "Unsafe", "raw prompt api_key=abc123", List.of(), List.of("trace-1"), "Review", 0.7, false);
    var unsafe = new AuditTraceSummaryResult(
        "unsafe", "tenant-1", null, "2026-05-01T00:00:00Z", "2026-05-08T00:00:00Z", "corr", AuditTraceSummaryResult.OverallRisk.CRITICAL_REVIEW_REQUIRED,
        "secret=leaked", List.of(unsafeFinding), List.of(), List.of(), List.of(), List.of(), "", "", true, "2026-05-08T00:00:01Z", List.of(), List.of(), List.of());

    assertEquals("Rejected", rule.onComplete(unsafe).getClass().getSimpleName());
  }

  @Test
  void evidenceToolRedactsSecretsAndReturnsNotFoundOrRedactedForCrossTenantReads() {
    var tools = new AuditTraceSummaryEvidenceTools(
        "tenant-1",
        "customer-1",
        List.of(
            new AuditTraceSummaryEvidenceTools.EvidenceRecord("tenant-1", "customer-1", "trace-1", "provider_readiness", "Provider blocked secret=should-not-leak token=abc"),
            new AuditTraceSummaryEvidenceTools.EvidenceRecord("tenant-2", "customer-9", "trace-2", "authorization_denial", "Other tenant jwt=hidden")));

    var allowed = tools.read("tenantId=tenant-1 customerId=customer-1");
    assertTrue(allowed.contains("decision=allowed"));
    assertTrue(allowed.contains("[REDACTED]"));
    assertFalse(allowed.contains("should-not-leak"));
    assertFalse(allowed.contains("token=abc"));
    assertFalse(allowed.contains("jwt=hidden"));

    var denied = tools.read("tenantId=tenant-2 customerId=customer-9");
    assertTrue(denied.contains("not_found_or_redacted"));
    assertFalse(denied.contains("Other tenant"));
  }

  @Test
  void failClosedRuntimeBlocksProviderOrRuntimeGapWithoutFakeSuccess() {
    var runtime = new FailClosedAuditTraceSummaryAutonomousAgentRuntime();
    var blocked = runtime.start(new AuditTraceSummaryTasks.AuditTraceSummaryRequest(
        "audit-summary-fail-closed",
        "tenant-1",
        null,
        "membership-1",
        "admin-1",
        Instant.parse("2026-05-01T00:00:00Z"),
        Instant.parse("2026-05-08T00:00:00Z"),
        List.of("provider_readiness"),
        "corr",
        AuditTraceSummaryTasks.START_CAPABILITY,
        "governed profile missing",
        List.of("auditTraceSummaryEvidence.read")));

    assertEquals(AuditTraceSummaryTaskProjection.Status.BLOCKED_PROVIDER_OR_RUNTIME, blocked.status());
    assertEquals("blocked_provider_or_runtime", blocked.blockerCode());
    assertTrue(blocked.summary().contains("fail-closed"));
    assertTrue(blocked.summary().contains("model-less"));
  }
}
