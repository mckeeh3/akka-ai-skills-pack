package ai.first.application.agentfoundation;

import ai.first.application.security.AuthContextResolver;
import ai.first.domain.agentfoundation.PromptRiskReviewTask;
import java.util.List;

/** Governed runtime adapter for starting/querying Agent Admin prompt-risk Akka AutonomousAgent tasks. */
public interface PromptRiskAutonomousAgentRuntime {
  StartOutcome start(AuthContextResolver.ResolvedMe actor, PromptRiskReviewTask starterTask, String correlationId);

  Projection project(PromptRiskReviewTask starterTask, String correlationId);

  default void cancel(PromptRiskReviewTask starterTask, String reason, String correlationId) {}

  record StartOutcome(String autonomousAgentTaskId, PromptRiskReviewTask.Status status, int progressPercent, String summary, String blockerCode, List<String> traceIds) {
    public StartOutcome {
      traceIds = List.copyOf(traceIds == null ? List.of() : traceIds);
    }

    public static StartOutcome queued(String autonomousAgentTaskId, String summary, List<String> traceIds) {
      return new StartOutcome(autonomousAgentTaskId, PromptRiskReviewTask.Status.QUEUED, 5, summary, null, traceIds);
    }

    public static StartOutcome blocked(String summary, String blockerCode, List<String> traceIds) {
      return new StartOutcome(null, PromptRiskReviewTask.Status.BLOCKED_PROVIDER_OR_RUNTIME, 0, summary, blockerCode, traceIds);
    }
  }

  record Projection(PromptRiskReviewTask.Status status, int progressPercent, String summary, String blockerCode, PromptRiskAutonomousAgentResult result, List<String> evidenceRefs, List<String> findingRefs, List<String> traceIds) {
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
