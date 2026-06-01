package com.example.application;

import java.util.List;

/** Governed runtime adapter for starting/querying Audit/Trace Summary Akka AutonomousAgent tasks. */
public interface AuditTraceSummaryAutonomousAgentRuntime {
  StartOutcome start(AuditTraceSummaryTasks.AuditTraceSummaryRequest request);

  Projection project(AuditTraceSummaryTaskProjection projection);

  default void cancel(AuditTraceSummaryTaskProjection projection, String reason) {}

  record StartOutcome(String autonomousAgentTaskId, AuditTraceSummaryTaskProjection.Status status, int progressPercent, String summary, String blockerCode, List<String> traceIds) {
    public StartOutcome {
      traceIds = List.copyOf(traceIds == null ? List.of() : traceIds);
    }

    public static StartOutcome queued(String autonomousAgentTaskId, String summary, List<String> traceIds) {
      return new StartOutcome(autonomousAgentTaskId, AuditTraceSummaryTaskProjection.Status.QUEUED, 5, summary, null, traceIds);
    }

    public static StartOutcome blocked(String summary, String blockerCode, List<String> traceIds) {
      return new StartOutcome(null, AuditTraceSummaryTaskProjection.Status.BLOCKED_PROVIDER_OR_RUNTIME, 0, summary, blockerCode, traceIds);
    }
  }

  record Projection(AuditTraceSummaryTaskProjection.Status status, int progressPercent, String summary, String blockerCode, AuditTraceSummaryResult result, List<String> evidenceRefs, List<String> findingRefs, List<String> traceIds) {
    public Projection {
      evidenceRefs = List.copyOf(evidenceRefs == null ? List.of() : evidenceRefs);
      findingRefs = List.copyOf(findingRefs == null ? List.of() : findingRefs);
      traceIds = List.copyOf(traceIds == null ? List.of() : traceIds);
    }
  }
}
