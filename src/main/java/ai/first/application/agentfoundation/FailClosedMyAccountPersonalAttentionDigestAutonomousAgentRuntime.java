package ai.first.application.agentfoundation;

import ai.first.domain.foundation.identity.Account;
import ai.first.application.security.AuthContextResolver;
import ai.first.domain.security.MyAccountPersonalAttentionDigestTask;
import java.util.List;

/** Fail-closed runtime used before My Account personal attention digest Akka AutonomousAgent provider/runtime binding is available. */
public final class FailClosedMyAccountPersonalAttentionDigestAutonomousAgentRuntime implements MyAccountPersonalAttentionDigestAutonomousAgentRuntime {
  @Override
  public StartOutcome start(AuthContextResolver.ResolvedMe actor, MyAccountPersonalAttentionDigestTask starterTask, String correlationId) {
    return StartOutcome.blocked(
        "My Account personal attention digest AutonomousAgent provider/runtime configuration is not bound to Akka ComponentClient; the starter fails closed instead of returning deterministic, fake, or model-less personal attention digest success.",
        "blocked_provider_or_runtime",
        List.of("trace-my-account-personal-attention-digest-runtime-unbound-" + stableSuffix(starterTask.digestTaskId() + ":" + correlationId)));
  }

  @Override
  public Projection project(MyAccountPersonalAttentionDigestTask starterTask, String correlationId) {
    if (starterTask.autonomousAgentTaskId() == null || starterTask.autonomousAgentTaskId().isBlank()) return Projection.unchanged();
    return new Projection(
        MyAccountPersonalAttentionDigestTask.Status.BLOCKED_PROVIDER_OR_RUNTIME,
        starterTask.progressPercent(),
        "My Account personal attention digest AutonomousAgent task cannot be queried because the Akka ComponentClient/provider runtime is unavailable; fail closed with no fake success.",
        "blocked_provider_or_runtime",
        null,
        starterTask.authorizedAttentionCount(),
        starterTask.evidenceRefs(),
        List.of(),
        List.of("trace-my-account-personal-attention-digest-query-unbound-" + stableSuffix(starterTask.digestTaskId() + ":" + correlationId)));
  }

  private static String stableSuffix(String value) {
    return Integer.toUnsignedString(java.util.Objects.requireNonNullElse(value, "my-account-personal-attention-digest-autonomous-agent").hashCode(), 36);
  }
}
