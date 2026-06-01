package {{JAVA_BASE_PACKAGE}}.application.security;

import {{JAVA_BASE_PACKAGE}}.domain.security.AccessReviewTask;
import java.util.List;

/** Fail-closed runtime used before Akka AutonomousAgent provider/runtime binding is available. */
public final class FailClosedAccessReviewAutonomousAgentRuntime implements AccessReviewAutonomousAgentRuntime {
  @Override
  public StartOutcome start(AuthContextResolver.ResolvedMe actor, AccessReviewTask starterTask, String correlationId) {
    return StartOutcome.blocked(
        "Access-review AutonomousAgent provider/runtime configuration is not bound to Akka ComponentClient; the starter fails closed instead of returning deterministic or model-less recommendations.",
        "blocked_provider_or_runtime",
        List.of("trace-autonomous-agent-runtime-unbound-" + stableSuffix(starterTask.taskId() + ":" + correlationId)));
  }

  @Override
  public Projection project(AccessReviewTask starterTask, String correlationId) {
    if (starterTask.autonomousAgentTaskId() == null || starterTask.autonomousAgentTaskId().isBlank()) return Projection.unchanged();
    return new Projection(
        AccessReviewTask.Status.BLOCKED_PROVIDER_OR_RUNTIME,
        starterTask.progressPercent(),
        "Access-review AutonomousAgent task cannot be queried because the Akka ComponentClient/provider runtime is unavailable; fail closed with no fake success.",
        "blocked_provider_or_runtime",
        null,
        starterTask.evidenceRefs(),
        List.of(),
        List.of("trace-autonomous-agent-query-unbound-" + stableSuffix(starterTask.taskId() + ":" + correlationId)));
  }

  private static String stableSuffix(String value) {
    return Integer.toUnsignedString(java.util.Objects.requireNonNullElse(value, "access-review-autonomous-agent").hashCode(), 36);
  }
}
