package ai.first.application.coreapp.useradmin;

import java.util.List;

/** Typed result returned by the User Admin Access Review Akka AutonomousAgent task. */
public record AccessReviewAutonomousAgentResult(
    String taskId,
    String tenantId,
    String customerId,
    String summary,
    List<AccessReviewFinding> findings,
    List<AccessReviewRecommendation> recommendations,
    List<String> evidenceRefs,
    List<String> traceIds,
    String safety) {
  public AccessReviewAutonomousAgentResult {
    findings = List.copyOf(findings == null ? List.of() : findings);
    recommendations = List.copyOf(recommendations == null ? List.of() : recommendations);
    evidenceRefs = List.copyOf(evidenceRefs == null ? List.of() : evidenceRefs);
    traceIds = List.copyOf(traceIds == null ? List.of() : traceIds);
  }

  public record AccessReviewFinding(String findingId, String severity, String summary, List<String> evidenceRefs) {
    public AccessReviewFinding {
      evidenceRefs = List.copyOf(evidenceRefs == null ? List.of() : evidenceRefs);
    }
  }

  public record AccessReviewRecommendation(String recommendationId, String summary, String requiredHumanAction, List<String> evidenceRefs) {
    public AccessReviewRecommendation {
      evidenceRefs = List.copyOf(evidenceRefs == null ? List.of() : evidenceRefs);
    }
  }
}
