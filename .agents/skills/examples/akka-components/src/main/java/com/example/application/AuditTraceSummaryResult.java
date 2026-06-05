package com.example.application;

import java.util.List;

/** Typed advisory result returned by the Audit/Trace Summary Akka AutonomousAgent task. */
public record AuditTraceSummaryResult(
    String summaryTaskId,
    String tenantId,
    String customerId,
    String windowStart,
    String windowEnd,
    String correlationId,
    OverallRisk overallRisk,
    String executiveSummary,
    List<Finding> findings,
    List<Finding> providerReadinessFindings,
    List<Finding> authorizationDenialFindings,
    List<Finding> agentWorkFindings,
    List<String> attentionRecommendations,
    String omittedEvidenceSummary,
    String redactionSummary,
    boolean noDirectMutation,
    String generatedAt,
    List<String> evidenceRefs,
    List<String> traceRefs,
    List<String> modelRuntimeRefs) {
  public AuditTraceSummaryResult {
    findings = List.copyOf(findings == null ? List.of() : findings);
    providerReadinessFindings = List.copyOf(providerReadinessFindings == null ? List.of() : providerReadinessFindings);
    authorizationDenialFindings = List.copyOf(authorizationDenialFindings == null ? List.of() : authorizationDenialFindings);
    agentWorkFindings = List.copyOf(agentWorkFindings == null ? List.of() : agentWorkFindings);
    attentionRecommendations = List.copyOf(attentionRecommendations == null ? List.of() : attentionRecommendations);
    evidenceRefs = List.copyOf(evidenceRefs == null ? List.of() : evidenceRefs);
    traceRefs = List.copyOf(traceRefs == null ? List.of() : traceRefs);
    modelRuntimeRefs = List.copyOf(modelRuntimeRefs == null ? List.of() : modelRuntimeRefs);
  }

  public enum OverallRisk {
    CLEAR,
    WATCH,
    REVIEW_REQUIRED,
    CRITICAL_REVIEW_REQUIRED
  }

  public record Finding(
      String findingId,
      String category,
      String severity,
      String title,
      String safeSummary,
      List<String> evidenceRefs,
      List<String> traceRefs,
      String recommendedReviewerAction,
      double confidence,
      boolean redactionApplied) {
    public Finding {
      evidenceRefs = List.copyOf(evidenceRefs == null ? List.of() : evidenceRefs);
      traceRefs = List.copyOf(traceRefs == null ? List.of() : traceRefs);
    }
  }
}
