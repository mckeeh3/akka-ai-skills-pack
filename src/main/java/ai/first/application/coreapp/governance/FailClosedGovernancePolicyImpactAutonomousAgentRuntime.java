package ai.first.application.coreapp.governance;

import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.domain.coreapp.governance.GovernancePolicyImpactTask;
import java.util.List;

/** Fail-closed runtime used before Akka AutonomousAgent provider/runtime binding is available. */
public final class FailClosedGovernancePolicyImpactAutonomousAgentRuntime implements GovernancePolicyImpactAutonomousAgentRuntime {
  @Override
  public StartOutcome start(AuthContextResolver.ResolvedMe actor, GovernancePolicyImpactTask starterTask, String evidenceRequest, String correlationId) {
    return StartOutcome.blocked(
        "Governance/Policy impact AutonomousAgent provider/runtime configuration is not bound to Akka ComponentClient; the starter fails closed instead of returning deterministic, simulated, fake, or model-less policy impact findings.",
        "blocked_provider_or_runtime",
        List.of("trace-governance-policy-impact-runtime-unbound-" + stableSuffix(starterTask.impactTaskId() + ":" + correlationId)));
  }

  @Override
  public Projection project(GovernancePolicyImpactTask starterTask, String correlationId) {
    if (starterTask.autonomousAgentTaskId() == null || starterTask.autonomousAgentTaskId().isBlank()) return Projection.unchanged();
    return new Projection(
        GovernancePolicyImpactTask.Status.BLOCKED_PROVIDER_OR_RUNTIME,
        starterTask.progressPercent(),
        "Governance/Policy impact AutonomousAgent task cannot be queried because the Akka ComponentClient/provider runtime is unavailable; fail closed with no fake success.",
        "blocked_provider_or_runtime",
        null,
        starterTask.evidenceRefs(),
        List.of(),
        List.of("trace-governance-policy-impact-query-unbound-" + stableSuffix(starterTask.impactTaskId() + ":" + correlationId)));
  }

  private static String stableSuffix(String value) {
    return Integer.toUnsignedString(java.util.Objects.requireNonNullElse(value, "governance-policy-impact-autonomous-agent").hashCode(), 36);
  }
}
