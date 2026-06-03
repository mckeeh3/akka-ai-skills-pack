package com.example.application;

import java.util.List;

/** Fail-closed runtime used before Audit/Trace Summary Akka AutonomousAgent provider/runtime binding is available. */
public class FailClosedAuditTraceSummaryAutonomousAgentRuntime implements AuditTraceSummaryAutonomousAgentRuntime {
  @Override
  public StartOutcome start(AuditTraceSummaryTasks.AuditTraceSummaryRequest request) {
    return StartOutcome.blocked(
        "Audit/Trace summary AutonomousAgent provider/runtime/tool-boundary/evidence configuration is unavailable; fail-closed with no deterministic, fixture, fake, canned, or model-less audit summary success.",
        "blocked_provider_or_runtime",
        List.of("trace-audit-summary-runtime-blocked-" + stableSuffix(request.summaryTaskId() + request.correlationId())));
  }

  @Override
  public Projection project(AuditTraceSummaryTaskProjection projection) {
    return new Projection(
        AuditTraceSummaryTaskProjection.Status.BLOCKED_PROVIDER_OR_RUNTIME,
        projection.progressPercent(),
        "Audit/Trace summary AutonomousAgent task state cannot be read from Akka ComponentClient; fail-closed with no fake success.",
        "blocked_provider_or_runtime",
        null,
        projection.evidenceRefs(),
        List.of(),
        List.of("trace-audit-summary-query-blocked-" + stableSuffix(projection.summaryTaskId())));
  }

  private static String stableSuffix(String value) {
    return Integer.toUnsignedString(java.util.Objects.requireNonNullElse(value, "audit-trace-summary").hashCode(), 36);
  }
}
