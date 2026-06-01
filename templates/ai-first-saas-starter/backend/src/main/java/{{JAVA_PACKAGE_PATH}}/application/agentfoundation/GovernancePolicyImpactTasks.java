package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import akka.javasdk.agent.task.Task;
import java.util.List;

/** Akka AutonomousAgent task definitions for Governance/Policy impact analysis. */
public final class GovernancePolicyImpactTasks {
  public static final Task<GovernancePolicyImpactAutonomousAgentResult> IMPACT_ANALYSIS = Task
      .name("GovernancePolicyImpactAnalysis")
      .description("Analyze proposed Governance/Policy changes and produce advisory impact findings for human review")
      .resultConformsTo(GovernancePolicyImpactAutonomousAgentResult.class)
      .rules(GovernancePolicyImpactAutonomousAgentResultRule.class);

  private GovernancePolicyImpactTasks() {}

  public static Task<GovernancePolicyImpactAutonomousAgentResult> impactInstructions(GovernancePolicyImpactRequest request) {
    return IMPACT_ANALYSIS.instructions("""
        Run a governed Governance/Policy policy-change impact analysis.

        Scope:
        - impactTaskId: %s
        - proposalId: %s
        - targetPolicyId: %s
        - tenantId: %s
        - customerId: %s
        - startedByAccountId: %s
        - correlationId: %s
        - governedCapability: %s

        Affected capabilities: %s
        Affected artifacts: %s
        Evidence request: %s
        Governed runtime context: %s
        Evidence/tool references available for this first slice: %s

        Required output:
        - Return only the structured GovernancePolicyImpactAutonomousAgentResult.
        - resultId, impactTaskId, proposalId, tenantId, and customerId must match the scope above.
        - Findings are advisory policy impact evidence only; never approve, reject, activate, roll back, mutate roles, mutate users, mutate provider config, expand ToolPermissionBoundary, or change policy state.
        - Cite evidenceRefs/sourceRefs and traceRefs for each finding, including governancePolicyEvidence.read, readSkill/readReferenceDoc, prompt/skill/reference/model/tool traces, proposal refs, and AutonomousAgent work traces used.
        - Redact raw prompts, hidden prompt text, provider credentials, JWTs, raw tool payloads, support-only data, and cross-tenant/customer data.
        - Set noDirectMutation=true and activationBlockedUntilHumanDecision=true.
        - If provider, governed profile, model config, tool grants, runtime binding, or evidence access is unavailable, fail closed with blocked_provider_or_runtime and do not fabricate deterministic, simulated, fake, canned, fixture, or model-less successful impact findings.
        """.formatted(
            request.impactTaskId(),
            request.proposalId(),
            request.targetPolicyId() == null ? "" : request.targetPolicyId(),
            request.tenantId(),
            request.customerId() == null ? "" : request.customerId(),
            request.startedByAccountId(),
            request.correlationId(),
            request.capabilityId(),
            String.join(", ", request.affectedCapabilityIds()),
            String.join(", ", request.affectedArtifactRefs()),
            safe(request.evidenceRequest()),
            safe(request.governedRuntimeContext()),
            String.join(", ", request.evidenceRefs())));
  }

  private static String safe(String value) {
    return value == null ? "" : value.replaceAll("(?i)(api[_-]?key|secret|token|jwt)\\s*[:=]\\s*\\S+", "$1=[REDACTED]");
  }

  public record GovernancePolicyImpactRequest(
      String impactTaskId,
      String proposalId,
      String targetPolicyId,
      String tenantId,
      String customerId,
      String startedByAccountId,
      String correlationId,
      String capabilityId,
      String evidenceRequest,
      String governedRuntimeContext,
      List<String> affectedCapabilityIds,
      List<String> affectedArtifactRefs,
      List<String> evidenceRefs) {
    public GovernancePolicyImpactRequest {
      affectedCapabilityIds = List.copyOf(affectedCapabilityIds == null ? List.of() : affectedCapabilityIds);
      affectedArtifactRefs = List.copyOf(affectedArtifactRefs == null ? List.of() : affectedArtifactRefs);
      evidenceRefs = List.copyOf(evidenceRefs == null ? List.of() : evidenceRefs);
    }
  }
}
