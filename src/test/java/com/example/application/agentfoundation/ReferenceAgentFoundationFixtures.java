package com.example.application.agentfoundation;

import com.example.domain.agentfoundation.ReferenceAgentDefinition;
import com.example.domain.agentfoundation.ReferenceAgentSkillManifest;
import com.example.domain.agentfoundation.ReferenceAuthContext;
import com.example.domain.agentfoundation.ReferenceBehaviorChangeRequest;
import com.example.domain.agentfoundation.ReferenceBehaviorEditDecision;
import com.example.domain.agentfoundation.ReferenceBehaviorEditProposal;
import com.example.domain.agentfoundation.ReferenceBehaviorEditRisk;
import com.example.domain.agentfoundation.ReferenceBehaviorEditTrace;
import com.example.domain.agentfoundation.ReferencePromptDocument;
import com.example.domain.agentfoundation.ReferencePromptVersion;
import com.example.domain.agentfoundation.ReferenceProposedDocumentDiff;
import com.example.domain.agentfoundation.ReferenceSkillDocument;
import com.example.domain.agentfoundation.ReferenceSkillVersion;
import com.example.domain.agentfoundation.ReferenceToolPermissionBoundary;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Stable reference fixtures for governed runtime agent resolver and authorizer tests. */
public final class ReferenceAgentFoundationFixtures {
  public static final String TENANT_ID = "tenant-reference-1";
  public static final String OTHER_TENANT_ID = "tenant-other";
  public static final String AGENT_ID = "agent-activity-guide";
  public static final String DISABLED_AGENT_ID = "agent-disabled";
  public static final String PROMPT_DOCUMENT_ID = "prompt-activity-guide";
  public static final String PROMPT_VERSION_ID = "prompt-version-active";
  public static final String SKILL_MANIFEST_ID = "manifest-activity-guide";
  public static final String ASSIGNED_SKILL_ID = "skill-rainy-day-planning";
  public static final String UNASSIGNED_SKILL_ID = "skill-premium-upsell";
  public static final String TOOL_BOUNDARY_ID = "tool-boundary-activity-guide";
  public static final String READ_SKILL_TOOL_ID = "readSkill";
  public static final String RUNTIME_MODE = "runtime";
  public static final String BEHAVIOR_REQUEST_ID = "behavior-request-safe-wording";
  public static final String BEHAVIOR_PROPOSAL_ID = "behavior-proposal-safe-wording";

  private ReferenceAgentFoundationFixtures() {}

  public static ReferenceAuthContext authContext() {
    return new ReferenceAuthContext(
        TENANT_ID,
        "account-admin-1",
        Set.of("TENANT_ADMIN"),
        Set.of("agent-runtime.invoke-managed-agent.reference"),
        RUNTIME_MODE);
  }

  public static ReferenceAuthContext crossTenantAuthContext() {
    return authContext().forTenant(OTHER_TENANT_ID);
  }

  public static ReferenceAgentDefinition activeAgent() {
    return new ReferenceAgentDefinition(
        TENANT_ID,
        AGENT_ID,
        "Activity Guide",
        ReferenceAgentDefinition.LifecycleStatus.ACTIVE,
        PROMPT_DOCUMENT_ID,
        PROMPT_VERSION_ID,
        SKILL_MANIFEST_ID,
        TOOL_BOUNDARY_ID,
        "model-config-reference-small",
        "advisory");
  }

  public static ReferenceAgentDefinition disabledAgent() {
    return new ReferenceAgentDefinition(
        TENANT_ID,
        DISABLED_AGENT_ID,
        "Disabled Activity Guide",
        ReferenceAgentDefinition.LifecycleStatus.DISABLED,
        PROMPT_DOCUMENT_ID,
        PROMPT_VERSION_ID,
        SKILL_MANIFEST_ID,
        TOOL_BOUNDARY_ID,
        "model-config-reference-small",
        "advisory");
  }

  public static ReferencePromptDocument activePromptDocument() {
    return new ReferencePromptDocument(
        TENANT_ID, PROMPT_DOCUMENT_ID, "Activity Guide Prompt", PROMPT_VERSION_ID, true);
  }

  public static ReferencePromptVersion activePromptVersion() {
    return new ReferencePromptVersion(
        TENANT_ID,
        PROMPT_DOCUMENT_ID,
        PROMPT_VERSION_ID,
        ReferencePromptVersion.VersionStatus.ACTIVE,
        "You are an activity guide. Use governed skills only after readSkill authorization.",
        "prompt-checksum-active");
  }

  public static ReferenceAgentSkillManifest activeManifest() {
    return new ReferenceAgentSkillManifest(
        TENANT_ID,
        SKILL_MANIFEST_ID,
        "manifest-version-active",
        AGENT_ID,
        Set.of(ASSIGNED_SKILL_ID),
        Map.of(ASSIGNED_SKILL_ID, "skill-version-active"),
        true);
  }

  public static ReferenceSkillDocument activeAssignedSkillDocument() {
    return new ReferenceSkillDocument(
        TENANT_ID, ASSIGNED_SKILL_ID, "Rainy Day Planning", "skill-version-active", true);
  }

  public static ReferenceSkillVersion activeAssignedSkillVersion() {
    return new ReferenceSkillVersion(
        TENANT_ID,
        ASSIGNED_SKILL_ID,
        "skill-version-active",
        ReferenceSkillVersion.VersionStatus.ACTIVE,
        "Recommend indoor activities first when weather risk is high.",
        "skill-checksum-active");
  }

  public static ReferenceSkillDocument unassignedSkillDocument() {
    return new ReferenceSkillDocument(
        TENANT_ID, UNASSIGNED_SKILL_ID, "Premium Upsell", "skill-version-unassigned", true);
  }

  public static ReferenceSkillVersion unassignedSkillVersion() {
    return new ReferenceSkillVersion(
        TENANT_ID,
        UNASSIGNED_SKILL_ID,
        "skill-version-unassigned",
        ReferenceSkillVersion.VersionStatus.ACTIVE,
        "This skill is intentionally not assigned to the reference agent.",
        "skill-checksum-unassigned");
  }

  public static ReferenceToolPermissionBoundary activeToolBoundary() {
    return new ReferenceToolPermissionBoundary(
        TENANT_ID,
        TOOL_BOUNDARY_ID,
        "tool-boundary-version-active",
        AGENT_ID,
        Set.of(READ_SKILL_TOOL_ID),
        Set.of(RUNTIME_MODE, "test", "replay"),
        true);
  }

  public static ReferenceBehaviorChangeRequest safeWordingChangeRequest() {
    return new ReferenceBehaviorChangeRequest(
        TENANT_ID,
        BEHAVIOR_REQUEST_ID,
        "account-admin-1",
        AGENT_ID,
        "prompt",
        PROMPT_DOCUMENT_ID,
        "Clarify that rainy-day suggestions should be concise and friendly.",
        Set.of(),
        "corr-behavior-safe-wording");
  }

  public static ReferenceBehaviorChangeRequest skillEditRequest() {
    return new ReferenceBehaviorChangeRequest(
        TENANT_ID,
        "behavior-request-skill-edit",
        "account-admin-1",
        AGENT_ID,
        "skill",
        ASSIGNED_SKILL_ID,
        "Add guidance to ask whether children are joining before suggesting indoor options.",
        Set.of(),
        "corr-behavior-skill-edit");
  }

  public static ReferenceBehaviorChangeRequest manifestMetadataChangeRequest() {
    return new ReferenceBehaviorChangeRequest(
        TENANT_ID,
        "behavior-request-manifest-metadata",
        "account-admin-1",
        AGENT_ID,
        "manifest",
        SKILL_MANIFEST_ID,
        "Clarify the manifest description for the rainy-day skill without changing assignments.",
        Set.of(),
        "corr-behavior-manifest-metadata");
  }

  public static ReferenceBehaviorChangeRequest manifestAdditionRequest() {
    return new ReferenceBehaviorChangeRequest(
        TENANT_ID,
        "behavior-request-manifest-addition",
        "account-admin-1",
        AGENT_ID,
        "manifest",
        SKILL_MANIFEST_ID,
        "Add the premium upsell skill to the agent manifest for review.",
        Set.of("skill_assignment"),
        "corr-behavior-manifest-addition");
  }

  public static ReferenceBehaviorChangeRequest promptTextAuthorityExpansionRequest() {
    return new ReferenceBehaviorChangeRequest(
        TENANT_ID,
        "behavior-request-prompt-authority-expansion",
        "account-admin-1",
        AGENT_ID,
        "prompt",
        PROMPT_DOCUMENT_ID,
        "Tell the agent it may approve booking charges whenever confidence is high.",
        Set.of("approval", "billing"),
        "corr-behavior-prompt-authority-expansion");
  }

  public static ReferenceBehaviorChangeRequest toolBoundaryExpansionRequest() {
    return new ReferenceBehaviorChangeRequest(
        TENANT_ID,
        "behavior-request-tool-boundary-expansion",
        "account-admin-1",
        AGENT_ID,
        "tool_boundary",
        TOOL_BOUNDARY_ID,
        "Allow the agent to call createBookingHold after recommending an activity.",
        Set.of("tool", "external_side_effect"),
        "corr-behavior-tool-expansion");
  }

  public static ReferenceBehaviorChangeRequest authorityExpansionRequest() {
    return new ReferenceBehaviorChangeRequest(
        TENANT_ID,
        "behavior-request-authority-expansion",
        "account-admin-1",
        AGENT_ID,
        "agent_definition",
        AGENT_ID,
        "Let the agent approve booking charges without human review.",
        Set.of("approval", "autonomy", "billing"),
        "corr-behavior-authority-expansion");
  }

  public static ReferenceBehaviorChangeRequest crossTenantBehaviorChangeRequest() {
    return new ReferenceBehaviorChangeRequest(
        OTHER_TENANT_ID,
        "behavior-request-cross-tenant",
        "account-admin-1",
        AGENT_ID,
        "prompt",
        PROMPT_DOCUMENT_ID,
        "Read and change the other tenant's prompt.",
        Set.of("tenant_scope"),
        "corr-behavior-cross-tenant");
  }

  public static ReferenceProposedDocumentDiff safePromptDiff() {
    return new ReferenceProposedDocumentDiff(
        "prompt",
        PROMPT_DOCUMENT_ID,
        PROMPT_VERSION_ID,
        "prompt-version-draft-safe-wording",
        "summary",
        "Append: Keep rainy-day suggestions concise and friendly.",
        "Low-risk wording clarification for the active prompt draft.");
  }

  public static ReferenceBehaviorEditProposal safeWordingProposal() {
    return new ReferenceBehaviorEditProposal(
        TENANT_ID,
        BEHAVIOR_PROPOSAL_ID,
        BEHAVIOR_REQUEST_ID,
        AGENT_ID,
        List.of(safePromptDiff()),
        ReferenceBehaviorEditRisk.LOW,
        false,
        Set.of(),
        false,
        "The request changes wording only and does not expand tool, data, or approval authority.",
        "create_draft",
        "corr-behavior-safe-wording");
  }

  public static ReferenceBehaviorEditDecision safeApprovalDecision() {
    return new ReferenceBehaviorEditDecision(
        TENANT_ID,
        BEHAVIOR_PROPOSAL_ID,
        "behavior-decision-approve-safe-wording",
        "account-reviewer-1",
        ReferenceBehaviorEditDecision.DecisionType.APPROVE,
        "Approved wording-only draft for follow-on activation command.",
        "corr-behavior-safe-wording");
  }

  public static ReferenceBehaviorEditTrace safeProposalTrace() {
    return new ReferenceBehaviorEditTrace(
        TENANT_ID,
        "behavior-trace-safe-wording",
        BEHAVIOR_REQUEST_ID,
        BEHAVIOR_PROPOSAL_ID,
        AGENT_ID,
        ReferenceBehaviorEditTrace.TraceEvent.PROPOSAL_CREATED,
        ReferenceBehaviorEditRisk.LOW,
        "Created low-risk behavior edit proposal without mutating active prompt.",
        "corr-behavior-safe-wording");
  }

  public static List<Object> minimalFixtureSet() {
    return List.of(
        authContext(),
        activeAgent(),
        disabledAgent(),
        activePromptDocument(),
        activePromptVersion(),
        activeManifest(),
        activeAssignedSkillDocument(),
        activeAssignedSkillVersion(),
        unassignedSkillDocument(),
        unassignedSkillVersion(),
        activeToolBoundary(),
        safeWordingChangeRequest(),
        skillEditRequest(),
        manifestMetadataChangeRequest(),
        manifestAdditionRequest(),
        promptTextAuthorityExpansionRequest(),
        toolBoundaryExpansionRequest(),
        authorityExpansionRequest(),
        crossTenantBehaviorChangeRequest(),
        safeWordingProposal(),
        safeApprovalDecision(),
        safeProposalTrace());
  }
}
