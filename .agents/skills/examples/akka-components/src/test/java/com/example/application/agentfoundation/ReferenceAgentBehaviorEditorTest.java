package com.example.application.agentfoundation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.domain.agentfoundation.ReferenceAgentDefinition;
import com.example.domain.agentfoundation.ReferenceAgentSkillManifest;
import com.example.domain.agentfoundation.ReferenceBehaviorEditRisk;
import com.example.domain.agentfoundation.ReferenceBehaviorEditTrace;
import com.example.domain.agentfoundation.ReferencePromptVersion;
import com.example.domain.agentfoundation.ReferenceSkillDocument;
import com.example.domain.agentfoundation.ReferenceSkillVersion;
import com.example.domain.agentfoundation.ReferenceToolPermissionBoundary;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ReferenceAgentBehaviorEditorTest {

  @Test
  void safeWordingPromptProposalCreatesProposedDiffOnly() {
    var traceSink = new ReferenceTraceSink();
    var editor = editor(traceSink);

    var proposal =
        editor.propose(
            ReferenceAgentFoundationFixtures.authContext(),
            ReferenceAgentFoundationFixtures.safeWordingChangeRequest());

    assertEquals(ReferenceBehaviorEditRisk.LOW, proposal.risk());
    assertFalse(proposal.authorityExpansionDetected());
    assertEquals("create_draft", proposal.recommendedNextAction());
    assertEquals(1, proposal.proposedDiffs().size());
    var proposedDiff = proposal.proposedDiffs().getFirst();
    assertEquals("prompt", proposedDiff.artifactType());
    assertEquals(ReferenceAgentFoundationFixtures.PROMPT_DOCUMENT_ID, proposedDiff.artifactId());
    assertEquals(ReferenceAgentFoundationFixtures.PROMPT_VERSION_ID, proposedDiff.currentVersionId());
    assertTrue(proposedDiff.proposedVersionId().startsWith("prompt-draft-"));
    assertTrue(proposedDiff.proposedDiff().contains("Proposed diff"));
    assertTrue(proposedDiff.summary().contains("PromptDocument"));
  }

  @Test
  void skillGuidanceProposalSelectsSkillDocumentAndCreatesDraftDiff() {
    var traceSink = new ReferenceTraceSink();
    var editor = editor(traceSink);

    var proposal =
        editor.propose(
            ReferenceAgentFoundationFixtures.authContext(),
            ReferenceAgentFoundationFixtures.skillEditRequest());

    assertEquals(ReferenceBehaviorEditRisk.MEDIUM, proposal.risk());
    assertEquals(1, proposal.proposedDiffs().size());
    var proposedDiff = proposal.proposedDiffs().getFirst();
    assertEquals("skill", proposedDiff.artifactType());
    assertEquals(ReferenceAgentFoundationFixtures.ASSIGNED_SKILL_ID, proposedDiff.artifactId());
    assertEquals("skill-version-active", proposedDiff.currentVersionId());
    assertTrue(proposedDiff.proposedVersionId().startsWith("skill-draft-"));
    assertTrue(proposal.rationale().contains("SkillDocument"));
  }

  @Test
  void affectedArtifactSelectionUsesRequestedArtifactType() {
    var traceSink = new ReferenceTraceSink();
    var editor = editor(traceSink);

    var promptProposal =
        editor.propose(
            ReferenceAgentFoundationFixtures.authContext(),
            ReferenceAgentFoundationFixtures.safeWordingChangeRequest());
    var skillProposal =
        editor.propose(
            ReferenceAgentFoundationFixtures.authContext(),
            ReferenceAgentFoundationFixtures.skillEditRequest());

    assertEquals("prompt", promptProposal.proposedDiffs().getFirst().artifactType());
    assertEquals("skill", skillProposal.proposedDiffs().getFirst().artifactType());
    assertEquals(
        ReferenceAgentFoundationFixtures.PROMPT_DOCUMENT_ID,
        promptProposal.proposedDiffs().getFirst().artifactId());
    assertEquals(
        ReferenceAgentFoundationFixtures.ASSIGNED_SKILL_ID,
        skillProposal.proposedDiffs().getFirst().artifactId());
  }

  @Test
  void proposalDoesNotMutateActivePromptOrSkillVersions() {
    var traceSink = new ReferenceTraceSink();
    var promptVersions = promptVersions();
    var skillVersions = skillVersions();
    var activePromptBefore = promptVersions.get(ReferenceAgentFoundationFixtures.PROMPT_VERSION_ID);
    var activeSkillBefore = skillVersions.get("skill-version-active");
    var editor =
        new ReferenceAgentBehaviorEditor(
            agentDefinitions(), promptVersions, skillDocuments(), skillVersions, traceSink);

    editor.propose(
        ReferenceAgentFoundationFixtures.authContext(),
        ReferenceAgentFoundationFixtures.safeWordingChangeRequest());
    editor.propose(
        ReferenceAgentFoundationFixtures.authContext(),
        ReferenceAgentFoundationFixtures.skillEditRequest());

    // No active mutation: proposed diffs create draft ids but leave current active records untouched.
    assertSame(activePromptBefore, promptVersions.get(ReferenceAgentFoundationFixtures.PROMPT_VERSION_ID));
    assertSame(activeSkillBefore, skillVersions.get("skill-version-active"));
    assertEquals(ReferencePromptVersion.VersionStatus.ACTIVE, activePromptBefore.status());
    assertEquals(ReferenceSkillVersion.VersionStatus.ACTIVE, activeSkillBefore.status());
  }

  @Test
  void behaviorEditTraceCreatedForEachPromptAndSkillProposal() {
    var traceSink = new ReferenceTraceSink();
    var editor = editor(traceSink);

    editor.propose(
        ReferenceAgentFoundationFixtures.authContext(),
        ReferenceAgentFoundationFixtures.safeWordingChangeRequest());
    editor.propose(
        ReferenceAgentFoundationFixtures.authContext(),
        ReferenceAgentFoundationFixtures.skillEditRequest());

    assertEquals(2, traceSink.behaviorEditTraces().size());
    assertTrue(
        traceSink.behaviorEditTraces().stream()
            .allMatch(
                trace ->
                    trace.event() == ReferenceBehaviorEditTrace.TraceEvent.PROPOSAL_CREATED
                        && trace.safeSummary().equals("create_draft")));
    assertTrue(
        traceSink.behaviorEditTraces().stream()
            .anyMatch(trace -> trace.risk() == ReferenceBehaviorEditRisk.LOW));
    assertTrue(
        traceSink.behaviorEditTraces().stream()
            .anyMatch(trace -> trace.risk() == ReferenceBehaviorEditRisk.MEDIUM));
  }

  @Test
  void lowRiskManifestMetadataChangeRemainsDraftProposal() {
    var traceSink = new ReferenceTraceSink();
    var editor = editor(traceSink);

    var proposal =
        editor.propose(
            ReferenceAgentFoundationFixtures.authContext(),
            ReferenceAgentFoundationFixtures.manifestMetadataChangeRequest());

    assertEquals(ReferenceBehaviorEditRisk.LOW, proposal.risk());
    assertFalse(proposal.authorityExpansionDetected());
    assertFalse(proposal.decisionCardRequired());
    assertEquals("create_draft", proposal.recommendedNextAction());
    assertEquals("manifest", proposal.proposedDiffs().getFirst().artifactType());
    assertEquals(
        ReferenceAgentFoundationFixtures.SKILL_MANIFEST_ID,
        proposal.proposedDiffs().getFirst().artifactId());
    assertTrue(proposal.rationale().contains("metadata"));
  }

  @Test
  void manifestSkillAssignmentAuthorityExpansionRequiresDecisionCard() {
    var traceSink = new ReferenceTraceSink();
    var editor = editor(traceSink);

    var proposal =
        editor.propose(
            ReferenceAgentFoundationFixtures.authContext(),
            ReferenceAgentFoundationFixtures.manifestAdditionRequest());

    assertEquals(ReferenceBehaviorEditRisk.HIGH, proposal.risk());
    assertTrue(proposal.authorityExpansionDetected());
    assertTrue(proposal.expansionTypes().contains("skill_assignment"));
    assertTrue(proposal.decisionCardRequired());
    assertEquals("create_decision_card", proposal.recommendedNextAction());
    assertEquals("manifest", proposal.proposedDiffs().getFirst().artifactType());
    assertTrue(proposal.rationale().contains("decision card"));
  }

  @Test
  void toolBoundaryExpansionRequiresDecisionCardReview() {
    var traceSink = new ReferenceTraceSink();
    var editor = editor(traceSink);

    var proposal =
        editor.propose(
            ReferenceAgentFoundationFixtures.authContext(),
            ReferenceAgentFoundationFixtures.toolBoundaryExpansionRequest());

    assertEquals(ReferenceBehaviorEditRisk.HIGH, proposal.risk());
    assertTrue(proposal.authorityExpansionDetected());
    assertTrue(proposal.expansionTypes().contains("tool"));
    assertTrue(proposal.expansionTypes().contains("external_side_effect"));
    assertTrue(proposal.decisionCardRequired());
    assertEquals("tool_boundary", proposal.proposedDiffs().getFirst().artifactType());
    assertTrue(proposal.proposedDiffs().getFirst().summary().contains("active boundary unchanged"));
  }

  @Test
  void directAuthorityExpansionOnAgentDefinitionRequiresDecisionCard() {
    var traceSink = new ReferenceTraceSink();
    var editor = editor(traceSink);

    var proposal =
        editor.propose(
            ReferenceAgentFoundationFixtures.authContext(),
            ReferenceAgentFoundationFixtures.authorityExpansionRequest());

    assertEquals(ReferenceBehaviorEditRisk.HIGH, proposal.risk());
    assertTrue(proposal.decisionCardRequired());
    assertTrue(proposal.expansionTypes().contains("approval"));
    assertTrue(proposal.expansionTypes().contains("autonomy"));
    assertEquals("create_decision_card", proposal.recommendedNextAction());
    assertEquals("agent_definition", proposal.proposedDiffs().getFirst().artifactType());
  }

  @Test
  void promptTextAloneCannotGrantAuthority() {
    var traceSink = new ReferenceTraceSink();
    var editor = editor(traceSink);

    var proposal =
        editor.propose(
            ReferenceAgentFoundationFixtures.authContext(),
            ReferenceAgentFoundationFixtures.promptTextAuthorityExpansionRequest());

    assertEquals(ReferenceBehaviorEditRisk.HIGH, proposal.risk());
    assertTrue(proposal.authorityExpansionDetected());
    assertTrue(proposal.decisionCardRequired());
    assertEquals("create_decision_card", proposal.recommendedNextAction());
    assertTrue(proposal.rationale().contains("cannot grant tool, data, or approval authority"));
    assertEquals("prompt", proposal.proposedDiffs().getFirst().artifactType());
  }

  @Test
  void crossTenantRequestIsDeniedAndTracedWithoutProposedDiff() {
    var traceSink = new ReferenceTraceSink();
    var editor = editor(traceSink);

    var proposal =
        editor.propose(
            ReferenceAgentFoundationFixtures.authContext(),
            ReferenceAgentFoundationFixtures.crossTenantBehaviorChangeRequest());

    assertEquals(ReferenceBehaviorEditRisk.BLOCKED, proposal.risk());
    assertTrue(proposal.proposedDiffs().isEmpty());
    assertEquals("deny", proposal.recommendedNextAction());
    assertEquals(ReferenceBehaviorEditTrace.TraceEvent.DENIED, traceSink.behaviorEditTraces().getFirst().event());
  }

  private static ReferenceAgentBehaviorEditor editor(ReferenceTraceSink traceSink) {
    return new ReferenceAgentBehaviorEditor(
        agentDefinitions(),
        promptVersions(),
        skillDocuments(),
        skillVersions(),
        manifests(),
        toolBoundaries(),
        traceSink);
  }

  private static Map<String, ReferenceAgentDefinition> agentDefinitions() {
    return Map.of(
        ReferenceAgentFoundationFixtures.AGENT_ID,
        ReferenceAgentFoundationFixtures.activeAgent(),
        ReferenceAgentFoundationFixtures.DISABLED_AGENT_ID,
        ReferenceAgentFoundationFixtures.disabledAgent());
  }

  private static Map<String, ReferencePromptVersion> promptVersions() {
    return Map.of(
        ReferenceAgentFoundationFixtures.PROMPT_VERSION_ID,
        ReferenceAgentFoundationFixtures.activePromptVersion());
  }

  private static Map<String, ReferenceSkillDocument> skillDocuments() {
    return Map.of(
        ReferenceAgentFoundationFixtures.ASSIGNED_SKILL_ID,
        ReferenceAgentFoundationFixtures.activeAssignedSkillDocument(),
        ReferenceAgentFoundationFixtures.UNASSIGNED_SKILL_ID,
        ReferenceAgentFoundationFixtures.unassignedSkillDocument());
  }

  private static Map<String, ReferenceSkillVersion> skillVersions() {
    return Map.of(
        "skill-version-active",
        ReferenceAgentFoundationFixtures.activeAssignedSkillVersion(),
        "skill-version-unassigned",
        ReferenceAgentFoundationFixtures.unassignedSkillVersion());
  }

  private static Map<String, ReferenceAgentSkillManifest> manifests() {
    return Map.of(
        ReferenceAgentFoundationFixtures.SKILL_MANIFEST_ID,
        ReferenceAgentFoundationFixtures.activeManifest());
  }

  private static Map<String, ReferenceToolPermissionBoundary> toolBoundaries() {
    return Map.of(
        ReferenceAgentFoundationFixtures.TOOL_BOUNDARY_ID,
        ReferenceAgentFoundationFixtures.activeToolBoundary());
  }
}
