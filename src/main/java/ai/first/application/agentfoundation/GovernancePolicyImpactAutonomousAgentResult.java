package ai.first.application.agentfoundation;

import java.util.List;

/** Typed advisory result returned by the Governance/Policy Impact Akka AutonomousAgent task. */
public record GovernancePolicyImpactAutonomousAgentResult(
    String resultId,
    String impactTaskId,
    String proposalId,
    String tenantId,
    String customerId,
    RiskLevel overallRisk,
    ReviewState reviewState,
    String summary,
    List<ImpactFinding> impactFindings,
    List<ApprovalGateFinding> approvalGateFindings,
    List<String> tenantIsolationFindings,
    List<String> redactionFindings,
    List<String> providerRuntimeFindings,
    List<String> requiredHumanDecisions,
    List<String> traceRefs,
    List<String> sourceRefs,
    List<String> redactionHints,
    boolean noDirectMutation,
    boolean activationBlockedUntilHumanDecision) {
  public GovernancePolicyImpactAutonomousAgentResult {
    impactFindings = List.copyOf(impactFindings == null ? List.of() : impactFindings);
    approvalGateFindings = List.copyOf(approvalGateFindings == null ? List.of() : approvalGateFindings);
    tenantIsolationFindings = List.copyOf(tenantIsolationFindings == null ? List.of() : tenantIsolationFindings);
    redactionFindings = List.copyOf(redactionFindings == null ? List.of() : redactionFindings);
    providerRuntimeFindings = List.copyOf(providerRuntimeFindings == null ? List.of() : providerRuntimeFindings);
    requiredHumanDecisions = List.copyOf(requiredHumanDecisions == null ? List.of() : requiredHumanDecisions);
    traceRefs = List.copyOf(traceRefs == null ? List.of() : traceRefs);
    sourceRefs = List.copyOf(sourceRefs == null ? List.of() : sourceRefs);
    redactionHints = List.copyOf(redactionHints == null ? List.of() : redactionHints);
  }

  public enum RiskLevel { LOW, MEDIUM, HIGH, CRITICAL, BLOCKED }
  public enum ReviewState { IMPACT_READY, BLOCKED_PROVIDER_OR_RUNTIME, FAILED, NEEDS_HUMAN_REVIEW }

  public record ImpactFinding(
      String findingId,
      String category,
      RiskLevel severity,
      List<String> affectedCapabilityIds,
      List<String> affectedArtifactRefs,
      List<String> evidenceRefs,
      String redactionLevel,
      String recommendedHumanAction) {
    public ImpactFinding {
      affectedCapabilityIds = List.copyOf(affectedCapabilityIds == null ? List.of() : affectedCapabilityIds);
      affectedArtifactRefs = List.copyOf(affectedArtifactRefs == null ? List.of() : affectedArtifactRefs);
      evidenceRefs = List.copyOf(evidenceRefs == null ? List.of() : evidenceRefs);
    }
  }

  public record ApprovalGateFinding(String findingId, String requiredApprovalCapabilityId, String activationPrerequisite, String rollbackRequirement, String deniedModelOwnedAuthority, List<String> evidenceRefs) {
    public ApprovalGateFinding {
      evidenceRefs = List.copyOf(evidenceRefs == null ? List.of() : evidenceRefs);
    }
  }
}
