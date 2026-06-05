package ai.first.application.coreapp.agentadmin;

import ai.first.domain.coreapp.agentadmin.PromptRiskReviewTask.ArtifactKind;
import java.util.List;

/** Typed advisory result returned by the Agent Admin Prompt-Risk Akka AutonomousAgent task. */
public record PromptRiskAutonomousAgentResult(
    String taskId,
    String tenantId,
    String customerId,
    String targetAgentDefinitionId,
    String proposalId,
    String summary,
    RiskLevel overallRisk,
    List<PromptRiskFinding> findings,
    List<PromptRiskRecommendation> recommendations,
    List<String> requiredHumanReviewReasons,
    List<String> evidenceRefs,
    List<String> traceIds,
    String safety) {
  public PromptRiskAutonomousAgentResult {
    findings = List.copyOf(findings == null ? List.of() : findings);
    recommendations = List.copyOf(recommendations == null ? List.of() : recommendations);
    requiredHumanReviewReasons = List.copyOf(requiredHumanReviewReasons == null ? List.of() : requiredHumanReviewReasons);
    evidenceRefs = List.copyOf(evidenceRefs == null ? List.of() : evidenceRefs);
    traceIds = List.copyOf(traceIds == null ? List.of() : traceIds);
  }

  public enum RiskLevel { LOW, MEDIUM, HIGH, CRITICAL, BLOCKED }

  public record PromptRiskFinding(
      String findingId,
      RiskLevel riskLevel,
      ArtifactKind artifactKind,
      String artifactId,
      String category,
      String browserSafeDescription,
      List<String> evidenceRefs,
      boolean requiresHumanReview) {
    public PromptRiskFinding {
      evidenceRefs = List.copyOf(evidenceRefs == null ? List.of() : evidenceRefs);
    }
  }

  public record PromptRiskRecommendation(String recommendationId, String action, String rationale, boolean blocksActivation, List<String> evidenceRefs) {
    public PromptRiskRecommendation {
      evidenceRefs = List.copyOf(evidenceRefs == null ? List.of() : evidenceRefs);
    }
  }
}
