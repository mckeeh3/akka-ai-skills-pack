package ai.first.application.coreapp.useradmin;

import ai.first.application.coreapp.useradmin.AccessReviewAutonomousAgentResult;
import ai.first.domain.coreapp.useradmin.AccessReviewTask;
import java.util.List;
import ai.first.application.foundation.identity.AuthContextResolver;

/** Governed runtime adapter for starting/querying User Admin access-review Akka AutonomousAgent tasks. */
public interface AccessReviewAutonomousAgentRuntime {
  StartOutcome start(AuthContextResolver.ResolvedMe actor, AccessReviewTask starterTask, String correlationId);

  Projection project(AccessReviewTask starterTask, String correlationId);

  default void cancel(AccessReviewTask starterTask, String reason, String correlationId) {}

  record StartOutcome(String autonomousAgentTaskId, AccessReviewTask.Status status, int progressPercent, String summary, String blockerCode, List<String> traceIds) {
    public StartOutcome {
      traceIds = List.copyOf(traceIds == null ? List.of() : traceIds);
    }

    public static StartOutcome queued(String autonomousAgentTaskId, String summary, List<String> traceIds) {
      return new StartOutcome(autonomousAgentTaskId, AccessReviewTask.Status.QUEUED, 5, summary, null, traceIds);
    }

    public static StartOutcome blocked(String summary, String blockerCode, List<String> traceIds) {
      return new StartOutcome(null, AccessReviewTask.Status.BLOCKED_PROVIDER_OR_RUNTIME, 0, summary, blockerCode, traceIds);
    }
  }

  record Projection(AccessReviewTask.Status status, int progressPercent, String summary, String blockerCode, AccessReviewAutonomousAgentResult result, List<String> evidenceRefs, List<String> recommendationRefs, List<String> traceIds) {
    public Projection {
      evidenceRefs = List.copyOf(evidenceRefs == null ? List.of() : evidenceRefs);
      recommendationRefs = List.copyOf(recommendationRefs == null ? List.of() : recommendationRefs);
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
