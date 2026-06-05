package ai.first.application.agentfoundation;

import ai.first.domain.agentfoundation.ReferenceAgentDefinition;
import ai.first.domain.agentfoundation.ReferenceAgentSkillManifest;
import ai.first.domain.agentfoundation.ReferenceAuthContext;
import ai.first.domain.agentfoundation.ReferenceBehaviorChangeRequest;
import ai.first.domain.agentfoundation.ReferenceBehaviorEditProposal;
import ai.first.domain.agentfoundation.ReferenceBehaviorEditRisk;
import ai.first.domain.agentfoundation.ReferenceBehaviorEditTrace;
import ai.first.domain.agentfoundation.ReferencePromptVersion;
import ai.first.domain.agentfoundation.ReferenceProposedDocumentDiff;
import ai.first.domain.agentfoundation.ReferenceSkillDocument;
import ai.first.domain.agentfoundation.ReferenceSkillVersion;
import ai.first.domain.agentfoundation.ReferenceToolPermissionBoundary;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Deterministic reference helper for AgentBehaviorEditorAgent-style prompt/skill proposals.
 * It creates proposed diff records only; it never mutates active PromptDocument or SkillDocument state.
 */
public final class ReferenceAgentBehaviorEditor {
  private final Map<String, ReferenceAgentDefinition> agentDefinitions;
  private final Map<String, ReferencePromptVersion> promptVersions;
  private final Map<String, ReferenceSkillDocument> skillDocuments;
  private final Map<String, ReferenceSkillVersion> skillVersions;
  private final Map<String, ReferenceAgentSkillManifest> manifests;
  private final Map<String, ReferenceToolPermissionBoundary> toolBoundaries;
  private final ReferenceTraceSink traceSink;

  public ReferenceAgentBehaviorEditor(
      Map<String, ReferenceAgentDefinition> agentDefinitions,
      Map<String, ReferencePromptVersion> promptVersions,
      Map<String, ReferenceSkillDocument> skillDocuments,
      Map<String, ReferenceSkillVersion> skillVersions,
      ReferenceTraceSink traceSink) {
    this(agentDefinitions, promptVersions, skillDocuments, skillVersions, Map.of(), Map.of(), traceSink);
  }

  public ReferenceAgentBehaviorEditor(
      Map<String, ReferenceAgentDefinition> agentDefinitions,
      Map<String, ReferencePromptVersion> promptVersions,
      Map<String, ReferenceSkillDocument> skillDocuments,
      Map<String, ReferenceSkillVersion> skillVersions,
      Map<String, ReferenceAgentSkillManifest> manifests,
      Map<String, ReferenceToolPermissionBoundary> toolBoundaries,
      ReferenceTraceSink traceSink) {
    this.agentDefinitions = Map.copyOf(agentDefinitions);
    this.promptVersions = Map.copyOf(promptVersions);
    this.skillDocuments = Map.copyOf(skillDocuments);
    this.skillVersions = Map.copyOf(skillVersions);
    this.manifests = Map.copyOf(manifests);
    this.toolBoundaries = Map.copyOf(toolBoundaries);
    this.traceSink = traceSink;
  }

  public ReferenceBehaviorEditProposal propose(
      ReferenceAuthContext authContext, ReferenceBehaviorChangeRequest request) {
    if (!authContext.tenantId().equals(request.tenantId())) {
      return denied(request, "cross-tenant behavior edit request denied");
    }

    var agent = agentDefinitions.get(request.targetAgentDefinitionId());
    if (agent == null || !agent.tenantId().equals(request.tenantId())) {
      return denied(request, "target AgentDefinition denied");
    }
    if (!agent.activeForRuntime()) {
      return denied(request, "target AgentDefinition is not active");
    }

    var proposal =
        switch (request.targetArtifactType()) {
          case "prompt" -> proposePromptDiff(request, agent);
          case "skill" -> proposeSkillDiff(request, agent);
          case "manifest" -> proposeManifestDiff(request, agent);
          case "tool_boundary" -> proposeToolBoundaryDiff(request, agent);
          case "agent_definition" -> proposeAgentDefinitionDiff(request, agent);
          default -> denied(request, "unsupported behavior artifact type for behavior edit helper");
        };
    recordProposalTrace(proposal, proposal.recommendedNextAction());
    return proposal;
  }

  private ReferenceBehaviorEditProposal proposePromptDiff(
      ReferenceBehaviorChangeRequest request, ReferenceAgentDefinition agent) {
    var activePrompt = promptVersions.get(agent.activePromptVersionId());
    if (activePrompt == null
        || !activePrompt.tenantId().equals(request.tenantId())
        || !activePrompt.promptDocumentId().equals(request.targetArtifactId())
        || !activePrompt.activeForRuntime()) {
      return denied(request, "active PromptDocument version denied");
    }

    var diff =
        new ReferenceProposedDocumentDiff(
            "prompt",
            activePrompt.promptDocumentId(),
            activePrompt.promptVersionId(),
            draftVersionId("prompt", request.requestId()),
            "summary",
            "Proposed diff: append clarification - " + request.requestedChange(),
            "PromptDocument wording proposal; creates a draft version and leaves active prompt unchanged.");
    if (request.requestsAuthorityExpansion()) {
      return allowedProposal(
          request,
          List.of(diff),
          ReferenceBehaviorEditRisk.HIGH,
          "PromptDocument text can propose authority expansion for review, but cannot grant tool, data, or approval authority.");
    }
    return allowedProposal(
        request,
        List.of(diff),
        ReferenceBehaviorEditRisk.LOW,
        "PromptDocument change is wording-only and does not expand tool, data, or approval authority.");
  }

  private ReferenceBehaviorEditProposal proposeSkillDiff(
      ReferenceBehaviorChangeRequest request, ReferenceAgentDefinition agent) {
    var skillDocument = skillDocuments.get(request.targetArtifactId());
    if (skillDocument == null || !skillDocument.tenantId().equals(request.tenantId()) || !skillDocument.active()) {
      return denied(request, "active SkillDocument denied");
    }
    var activeSkill = skillVersions.get(skillDocument.activeSkillVersionId());
    if (activeSkill == null
        || !activeSkill.tenantId().equals(request.tenantId())
        || !activeSkill.skillDocumentId().equals(skillDocument.skillDocumentId())
        || !activeSkill.activeForRuntime()) {
      return denied(request, "active SkillDocument version denied");
    }

    var assignedToAgent = agent.skillManifestId() != null;
    var diff =
        new ReferenceProposedDocumentDiff(
            "skill",
            skillDocument.skillDocumentId(),
            activeSkill.skillVersionId(),
            draftVersionId("skill", request.requestId()),
            "summary",
            "Proposed diff: update governed skill guidance - " + request.requestedChange(),
            (assignedToAgent ? "Assigned" : "Candidate")
                + " SkillDocument proposal; creates a draft version and leaves active skill unchanged.");
    if (request.requestsAuthorityExpansion()) {
      return allowedProposal(
          request,
          List.of(diff),
          ReferenceBehaviorEditRisk.HIGH,
          "SkillDocument text can propose authority expansion for review, but cannot grant tool, data, or approval authority.");
    }
    return allowedProposal(
        request,
        List.of(diff),
        ReferenceBehaviorEditRisk.MEDIUM,
        "SkillDocument guidance change affects agent behavior but does not grant authority by text.");
  }

  private ReferenceBehaviorEditProposal proposeManifestDiff(
      ReferenceBehaviorChangeRequest request, ReferenceAgentDefinition agent) {
    var manifest = manifests.get(request.targetArtifactId());
    if (manifest == null
        || !manifest.tenantId().equals(request.tenantId())
        || !manifest.agentDefinitionId().equals(agent.agentDefinitionId())
        || !manifest.active()
        || !request.targetArtifactId().equals(agent.skillManifestId())) {
      return denied(request, "active AgentSkillManifest denied");
    }

    var risk = request.requestsAuthorityExpansion() ? ReferenceBehaviorEditRisk.HIGH : ReferenceBehaviorEditRisk.LOW;
    var diff =
        new ReferenceProposedDocumentDiff(
            "manifest",
            manifest.skillManifestId(),
            manifest.skillManifestVersionId(),
            draftVersionId("manifest", request.requestId()),
            "summary",
            "Proposed diff: update AgentSkillManifest - " + request.requestedChange(),
            request.requestsAuthorityExpansion()
                ? "AgentSkillManifest skill assignment or authority expansion proposal; decision-card review required."
                : "Low-risk AgentSkillManifest metadata proposal; leaves active manifest unchanged.");
    return allowedProposal(
        request,
        List.of(diff),
        risk,
        request.requestsAuthorityExpansion()
            ? "AgentSkillManifest changes that add skills or authority are authority expansion and require a decision card."
            : "AgentSkillManifest metadata change does not expand tool, data, skill, or approval authority.");
  }

  private ReferenceBehaviorEditProposal proposeToolBoundaryDiff(
      ReferenceBehaviorChangeRequest request, ReferenceAgentDefinition agent) {
    var boundary = toolBoundaries.get(request.targetArtifactId());
    if (boundary == null
        || !boundary.tenantId().equals(request.tenantId())
        || !boundary.agentDefinitionId().equals(agent.agentDefinitionId())
        || !boundary.active()
        || !request.targetArtifactId().equals(agent.toolBoundaryId())) {
      return denied(request, "active ToolPermissionBoundary denied");
    }

    var diff =
        new ReferenceProposedDocumentDiff(
            "tool_boundary",
            boundary.toolBoundaryId(),
            boundary.boundaryVersionId(),
            draftVersionId("tool-boundary", request.requestId()),
            "summary",
            "Proposed diff: update ToolPermissionBoundary - " + request.requestedChange(),
            "ToolPermissionBoundary authority expansion proposal; decision-card review required and active boundary unchanged.");
    return allowedProposal(
        request,
        List.of(diff),
        ReferenceBehaviorEditRisk.HIGH,
        "ToolPermissionBoundary changes can broaden tool/data/side-effect authority and require decision-card approval.");
  }

  private ReferenceBehaviorEditProposal proposeAgentDefinitionDiff(
      ReferenceBehaviorChangeRequest request, ReferenceAgentDefinition agent) {
    var diff =
        new ReferenceProposedDocumentDiff(
            "agent_definition",
            agent.agentDefinitionId(),
            agent.agentDefinitionId(),
            draftVersionId("agent-definition", request.requestId()),
            "summary",
            "Proposed diff: update AgentDefinition authority - " + request.requestedChange(),
            "AgentDefinition authority expansion proposal; decision-card review required and active runtime state unchanged.");
    return allowedProposal(
        request,
        List.of(diff),
        ReferenceBehaviorEditRisk.HIGH,
        "AgentDefinition approval, autonomy, billing, model, role, or tenant-scope authority changes require decision-card approval.");
  }

  private ReferenceBehaviorEditProposal allowedProposal(
      ReferenceBehaviorChangeRequest request,
      List<ReferenceProposedDocumentDiff> proposedDiffs,
      ReferenceBehaviorEditRisk risk,
      String rationale) {
    return new ReferenceBehaviorEditProposal(
        request.tenantId(),
        proposalId(request.requestId()),
        request.requestId(),
        request.targetAgentDefinitionId(),
        proposedDiffs,
        risk,
        risk.authorityReviewRequired() || request.requestsAuthorityExpansion(),
        request.requestedExpansionTypes(),
        risk.decisionCardRequired(),
        rationale,
        risk.decisionCardRequired() ? "create_decision_card" : "create_draft",
        request.correlationId());
  }

  private ReferenceBehaviorEditProposal denied(ReferenceBehaviorChangeRequest request, String reason) {
    var proposal =
        new ReferenceBehaviorEditProposal(
            request.tenantId(),
            proposalId(request.requestId()),
            request.requestId(),
            request.targetAgentDefinitionId(),
            List.of(),
            ReferenceBehaviorEditRisk.BLOCKED,
            true,
            request.requestedExpansionTypes().isEmpty()
                ? Set.of("tenant_scope")
                : request.requestedExpansionTypes(),
            true,
            reason,
            "deny",
            request.correlationId());
    traceSink.recordBehaviorEdit(
        new ReferenceBehaviorEditTrace(
            proposal.tenantId(),
            "behavior-trace-" + proposal.proposalId(),
            proposal.requestId(),
            proposal.proposalId(),
            proposal.targetAgentDefinitionId(),
            ReferenceBehaviorEditTrace.TraceEvent.DENIED,
            proposal.risk(),
            reason,
            proposal.correlationId()));
    return proposal;
  }

  private void recordProposalTrace(ReferenceBehaviorEditProposal proposal, String safeSummary) {
    if ("deny".equals(proposal.recommendedNextAction())) {
      return;
    }
    traceSink.recordBehaviorEdit(
        new ReferenceBehaviorEditTrace(
            proposal.tenantId(),
            "behavior-trace-" + proposal.proposalId(),
            proposal.requestId(),
            proposal.proposalId(),
            proposal.targetAgentDefinitionId(),
            ReferenceBehaviorEditTrace.TraceEvent.PROPOSAL_CREATED,
            proposal.risk(),
            safeSummary,
            proposal.correlationId()));
  }

  private static String proposalId(String requestId) {
    return "behavior-proposal-" + requestId;
  }

  private static String draftVersionId(String artifactType, String requestId) {
    return artifactType + "-draft-" + requestId;
  }
}
