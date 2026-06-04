package ai.first.application.agentfoundation;

import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.domain.security.AuditTraceSummaryTask;
import java.util.List;

/** Governed runtime adapter for starting/querying Audit/Trace Summary Akka AutonomousAgent tasks. */
public interface AuditTraceSummaryAutonomousAgentRuntime {
  StartOutcome start(AuthContextResolver.ResolvedMe actor, AuditTraceSummaryTask starterTask, String correlationId);

  Projection project(AuditTraceSummaryTask starterTask, String correlationId);

  default void cancel(AuditTraceSummaryTask starterTask, String reason, String correlationId) {}

  record StartOutcome(String autonomousAgentTaskId, AuditTraceSummaryTask.Status status, int progressPercent, String summary, String blockerCode, List<String> traceIds) {
    public StartOutcome {
      traceIds = List.copyOf(traceIds == null ? List.of() : traceIds);
    }

    public static StartOutcome queued(String autonomousAgentTaskId, String summary, List<String> traceIds) {
      return new StartOutcome(autonomousAgentTaskId, AuditTraceSummaryTask.Status.QUEUED, 5, summary, null, traceIds);
    }

    public static StartOutcome blocked(String summary, String blockerCode, List<String> traceIds) {
      return new StartOutcome(null, AuditTraceSummaryTask.Status.BLOCKED_PROVIDER_OR_RUNTIME, 0, summary, blockerCode, traceIds);
    }
  }

  record Projection(AuditTraceSummaryTask.Status status, int progressPercent, String summary, String blockerCode, AuditTraceSummaryResult result, List<String> evidenceRefs, List<String> findingRefs, List<String> traceIds) {
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
