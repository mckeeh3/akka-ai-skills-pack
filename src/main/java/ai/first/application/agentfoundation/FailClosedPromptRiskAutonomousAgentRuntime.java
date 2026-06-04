package ai.first.application.agentfoundation;

import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.domain.agentfoundation.PromptRiskReviewTask;
import java.util.List;

/** Fail-closed runtime used before Akka AutonomousAgent provider/runtime binding is available. */
public final class FailClosedPromptRiskAutonomousAgentRuntime implements PromptRiskAutonomousAgentRuntime {
  @Override
  public StartOutcome start(AuthContextResolver.ResolvedMe actor, PromptRiskReviewTask starterTask, String correlationId) {
    return StartOutcome.blocked(
        "Agent Admin prompt-risk AutonomousAgent provider/runtime configuration is not bound to Akka ComponentClient; the starter fails closed instead of returning deterministic, fake, or model-less prompt-risk findings.",
        "blocked_provider_or_runtime",
        List.of("trace-agent-admin-prompt-risk-runtime-unbound-" + stableSuffix(starterTask.taskId() + ":" + correlationId)));
  }

  @Override
  public Projection project(PromptRiskReviewTask starterTask, String correlationId) {
    if (starterTask.autonomousAgentTaskId() == null || starterTask.autonomousAgentTaskId().isBlank()) return Projection.unchanged();
    return new Projection(
        PromptRiskReviewTask.Status.BLOCKED_PROVIDER_OR_RUNTIME,
        starterTask.progressPercent(),
        "Agent Admin prompt-risk AutonomousAgent task cannot be queried because the Akka ComponentClient/provider runtime is unavailable; fail closed with no fake success.",
        "blocked_provider_or_runtime",
        null,
        starterTask.evidenceRefs(),
        List.of(),
        List.of("trace-agent-admin-prompt-risk-query-unbound-" + stableSuffix(starterTask.taskId() + ":" + correlationId)));
  }

  private static String stableSuffix(String value) {
    return Integer.toUnsignedString(java.util.Objects.requireNonNullElse(value, "agent-admin-prompt-risk-autonomous-agent").hashCode(), 36);
  }
}
