package ai.first.application.agentfoundation;

import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.domain.security.AuditTraceSummaryTask;
import java.util.List;

/** Fail-closed runtime used before Audit/Trace Summary Akka AutonomousAgent provider/runtime binding is available. */
public final class FailClosedAuditTraceSummaryAutonomousAgentRuntime implements AuditTraceSummaryAutonomousAgentRuntime {
  @Override
  public StartOutcome start(AuthContextResolver.ResolvedMe actor, AuditTraceSummaryTask starterTask, String correlationId) {
    return StartOutcome.blocked(
        "Audit/Trace summary AutonomousAgent provider/runtime configuration is not bound to Akka ComponentClient; the starter fails closed instead of returning deterministic, fake, or model-less audit summary success.",
        "blocked_provider_or_runtime",
        List.of("trace-audit-trace-summary-runtime-unbound-" + stableSuffix(starterTask.taskId() + ":" + correlationId)));
  }

  @Override
  public Projection project(AuditTraceSummaryTask starterTask, String correlationId) {
    if (starterTask.autonomousAgentTaskId() == null || starterTask.autonomousAgentTaskId().isBlank()) return Projection.unchanged();
    return new Projection(
        AuditTraceSummaryTask.Status.BLOCKED_PROVIDER_OR_RUNTIME,
        starterTask.progressPercent(),
        "Audit/Trace summary AutonomousAgent task cannot be queried because the Akka ComponentClient/provider runtime is unavailable; fail closed with no fake success.",
        "blocked_provider_or_runtime",
        null,
        starterTask.evidenceRefs(),
        List.of(),
        List.of("trace-audit-trace-summary-query-unbound-" + stableSuffix(starterTask.taskId() + ":" + correlationId)));
  }

  private static String stableSuffix(String value) {
    return Integer.toUnsignedString(java.util.Objects.requireNonNullElse(value, "audit-trace-summary-autonomous-agent").hashCode(), 36);
  }
}
