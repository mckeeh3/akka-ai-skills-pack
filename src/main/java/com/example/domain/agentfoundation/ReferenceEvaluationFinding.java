package com.example.domain.agentfoundation;

import java.util.List;

/** One normalized evaluator finding with evidence links and an improvement suggestion. */
public record ReferenceEvaluationFinding(
    String tenantId,
    String findingId,
    String evaluationRunId,
    FindingCategory category,
    FindingSeverity severity,
    double confidence,
    String affectedAgentDefinitionId,
    String affectedArtifactType,
    String affectedArtifactId,
    String suggestedImprovementKind,
    List<String> sourceTraceIds,
    String safeSummary,
    String correlationId) {

  public ReferenceEvaluationFinding {
    sourceTraceIds = List.copyOf(sourceTraceIds);
  }

  public enum FindingCategory {
    CORRECTNESS,
    SAFETY,
    POLICY,
    TONE,
    TOOL_USE,
    PROMPT_ADHERENCE,
    SKILL_USE,
    USER_VALUE
  }

  public enum FindingSeverity {
    INFO,
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
  }
}
