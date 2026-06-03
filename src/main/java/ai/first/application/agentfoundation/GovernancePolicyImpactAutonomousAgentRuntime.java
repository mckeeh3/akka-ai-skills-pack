package ai.first.application.agentfoundation;

import ai.first.application.security.AuthContextResolver;
import ai.first.domain.security.GovernancePolicyImpactTask;
import java.util.List;

/** Governed runtime adapter for starting/querying Governance/Policy impact Akka AutonomousAgent tasks. */
public interface GovernancePolicyImpactAutonomousAgentRuntime {
  StartOutcome start(AuthContextResolver.ResolvedMe actor, GovernancePolicyImpactTask starterTask, String evidenceRequest, String correlationId);

  Projection project(GovernancePolicyImpactTask starterTask, String correlationId);

  default void cancel(GovernancePolicyImpactTask starterTask, String reason, String correlationId) {}

  record StartOutcome(String autonomousAgentTaskId, GovernancePolicyImpactTask.Status status, int progressPercent, String summary, String blockerCode, List<String> traceIds) {
    public StartOutcome {
      traceIds = List.copyOf(traceIds == null ? List.of() : traceIds);
    }

    public static StartOutcome queued(String autonomousAgentTaskId, String summary, List<String> traceIds) {
      return new StartOutcome(autonomousAgentTaskId, GovernancePolicyImpactTask.Status.QUEUED, 5, summary, null, traceIds);
    }

    public static StartOutcome blocked(String summary, String blockerCode, List<String> traceIds) {
      return new StartOutcome(null, GovernancePolicyImpactTask.Status.BLOCKED_PROVIDER_OR_RUNTIME, 0, summary, blockerCode, traceIds);
    }
  }

  record Projection(GovernancePolicyImpactTask.Status status, int progressPercent, String summary, String blockerCode, GovernancePolicyImpactAutonomousAgentResult result, List<String> evidenceRefs, List<String> findingRefs, List<String> traceIds) {
    public Projection {
      evidenceRefs = List.copyOf(evidenceRefs == null ? List.of() : evidenceRefs);
      findingRefs = List.copyOf(findingRefs == null ? List.of() : findingRefs);
      traceIds = List.copyOf(traceIds == null ? List.of() : traceIds);
    }

    public static Projection unchanged() {
      return new Projection(null, 0, null, null, null, List.of(), List.of(), List.of());
    }

    public boolean changed() {
      return status != null;
    }
  }
}
