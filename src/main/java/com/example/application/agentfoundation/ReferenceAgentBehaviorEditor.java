package com.example.application.agentfoundation;

import com.example.domain.agentfoundation.ReferenceAgentDefinition;
import com.example.domain.agentfoundation.ReferenceAuthContext;
import com.example.domain.agentfoundation.ReferenceBehaviorChangeRequest;
import com.example.domain.agentfoundation.ReferenceBehaviorEditProposal;
import com.example.domain.agentfoundation.ReferenceBehaviorEditRisk;
import com.example.domain.agentfoundation.ReferenceBehaviorEditTrace;
import com.example.domain.agentfoundation.ReferencePromptVersion;
import com.example.domain.agentfoundation.ReferenceProposedDocumentDiff;
import com.example.domain.agentfoundation.ReferenceSkillDocument;
import com.example.domain.agentfoundation.ReferenceSkillVersion;
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
  private final ReferenceTraceSink traceSink;

  public ReferenceAgentBehaviorEditor(
      Map<String, ReferenceAgentDefinition> agentDefinitions,
      Map<String, ReferencePromptVersion> promptVersions,
      Map<String, ReferenceSkillDocument> skillDocuments,
      Map<String, ReferenceSkillVersion> skillVersions,
      ReferenceTraceSink traceSink) {
    this.agentDefinitions = Map.copyOf(agentDefinitions);
    this.promptVersions = Map.copyOf(promptVersions);
    this.skillDocuments = Map.copyOf(skillDocuments);
    this.skillVersions = Map.copyOf(skillVersions);
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
          default -> denied(request, "unsupported behavior artifact type for prompt/skill diff helper");
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
    return allowedProposal(
        request,
        List.of(diff),
        ReferenceBehaviorEditRisk.MEDIUM,
        "SkillDocument guidance change affects agent behavior but does not grant authority by text.");
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
        request.requestsAuthorityExpansion(),
        request.requestedExpansionTypes(),
        risk.decisionCardRequired(),
        rationale,
        "create_draft",
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
