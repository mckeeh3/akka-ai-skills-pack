package ai.first.domain.agentfoundation;

import java.util.List;

/** Deterministic reference evaluation run used by closed-loop improvement examples. */
public record ReferenceEvaluationRun(
    String tenantId,
    String evaluationRunId,
    String targetAgentDefinitionId,
    String sourceWorkTraceId,
    String rubricId,
    EvaluationStatus status,
    boolean passed,
    int score,
    List<String> findingIds,
    String correlationId) {

  public ReferenceEvaluationRun {
    findingIds = List.copyOf(findingIds);
  }

  public enum EvaluationStatus {
    QUEUED,
    RUNNING,
    COMPLETED,
    FAILED,
    CANCELED
  }
}
