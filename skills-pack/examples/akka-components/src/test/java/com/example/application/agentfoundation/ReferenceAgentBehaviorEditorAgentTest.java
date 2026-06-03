package com.example.application.agentfoundation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.JsonSupport;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import com.example.domain.agentfoundation.ReferenceAgentDefinition;
import com.example.domain.agentfoundation.ReferenceAgentSkillManifest;
import com.example.domain.agentfoundation.ReferenceBehaviorChangeRequest;
import com.example.domain.agentfoundation.ReferenceBehaviorEditRisk;
import com.example.domain.agentfoundation.ReferencePromptVersion;
import com.example.domain.agentfoundation.ReferenceSkillDocument;
import com.example.domain.agentfoundation.ReferenceSkillVersion;
import com.example.domain.agentfoundation.ReferenceToolPermissionBoundary;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ReferenceAgentBehaviorEditorAgentTest extends TestKitSupport {

  private final TestModelProvider behaviorEditorModel = new TestModelProvider();

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withAdditionalConfig("akka.javasdk.agent.openai.api-key = n/a")
        .withModelProvider(ReferenceAgentBehaviorEditorAgent.class, behaviorEditorModel);
  }

  @Test
  void structuredProposalIntentCanBeHandedToDeterministicHelper() {
    behaviorEditorModel.fixedResponse(
        JsonSupport.encodeToString(
            new ReferenceAgentBehaviorEditorAgent.BehaviorEditProposalIntent(
                ReferenceAgentFoundationFixtures.TENANT_ID,
                ReferenceAgentFoundationFixtures.BEHAVIOR_REQUEST_ID,
                ReferenceAgentFoundationFixtures.AGENT_ID,
                "prompt",
                ReferenceAgentFoundationFixtures.PROMPT_DOCUMENT_ID,
                "Clarify that rainy-day suggestions should be concise and friendly.",
                "low",
                false,
                java.util.List.of(),
                false,
                "create_draft",
                "Wording-only proposal; backend helper must create a draft and leave active prompt unchanged.",
                "corr-behavior-safe-wording")));

    var intent =
        componentClient
            .forAgent()
            .inSession("reference-behavior-editor-agent-safe")
            .method(ReferenceAgentBehaviorEditorAgent::draftProposal)
            .invoke(safePromptDraftRequest());

    assertEquals("prompt", intent.targetArtifactType());
    assertEquals("low", intent.riskClassification());
    assertFalse(intent.authorityExpansionDetected());

    var traceSink = new ReferenceTraceSink();
    var proposal = editor(traceSink).propose(ReferenceAgentFoundationFixtures.authContext(), toChangeRequest(intent));

    assertEquals(ReferenceBehaviorEditRisk.LOW, proposal.risk());
    assertEquals("create_draft", proposal.recommendedNextAction());
    assertEquals("prompt", proposal.proposedDiffs().getFirst().artifactType());
    assertEquals(1, traceSink.behaviorEditTraces().size());
  }

  @Test
  void authorityExpansionIntentIsStillValidatedByHelperAndRequiresDecisionCard() {
    behaviorEditorModel.fixedResponse(
        JsonSupport.encodeToString(
            new ReferenceAgentBehaviorEditorAgent.BehaviorEditProposalIntent(
                ReferenceAgentFoundationFixtures.TENANT_ID,
                "behavior-request-tool-expansion",
                ReferenceAgentFoundationFixtures.AGENT_ID,
                "tool_boundary",
                ReferenceAgentFoundationFixtures.TOOL_BOUNDARY_ID,
                "Allow the agent to call createBookingHold after recommending an activity.",
                "high",
                true,
                java.util.List.of("tool", "external_side_effect"),
                true,
                "create_decision_card",
                "ToolPermissionBoundary expansion can broaden side-effect authority and needs review.",
                "corr-behavior-tool-expansion")));

    var intent =
        componentClient
            .forAgent()
            .inSession("reference-behavior-editor-agent-tool-expansion")
            .method(ReferenceAgentBehaviorEditorAgent::draftProposal)
            .invoke(toolBoundaryDraftRequest());

    assertTrue(intent.authorityExpansionDetected());
    assertEquals("create_decision_card", intent.recommendedNextAction());

    var proposal = editor(new ReferenceTraceSink()).propose(ReferenceAgentFoundationFixtures.authContext(), toChangeRequest(intent));

    assertEquals(ReferenceBehaviorEditRisk.HIGH, proposal.risk());
    assertTrue(proposal.decisionCardRequired());
    assertTrue(proposal.expansionTypes().contains("tool"));
    assertEquals("tool_boundary", proposal.proposedDiffs().getFirst().artifactType());
  }

  private static ReferenceAgentBehaviorEditorAgent.BehaviorEditDraftRequest safePromptDraftRequest() {
    return new ReferenceAgentBehaviorEditorAgent.BehaviorEditDraftRequest(
        ReferenceAgentFoundationFixtures.TENANT_ID,
        ReferenceAgentFoundationFixtures.BEHAVIOR_REQUEST_ID,
        "account-admin-1",
        ReferenceAgentFoundationFixtures.AGENT_ID,
        "Clarify that rainy-day suggestions should be concise and friendly.",
        "prompt",
        ReferenceAgentFoundationFixtures.PROMPT_DOCUMENT_ID,
        "corr-behavior-safe-wording");
  }

  private static ReferenceAgentBehaviorEditorAgent.BehaviorEditDraftRequest toolBoundaryDraftRequest() {
    return new ReferenceAgentBehaviorEditorAgent.BehaviorEditDraftRequest(
        ReferenceAgentFoundationFixtures.TENANT_ID,
        "behavior-request-tool-expansion",
        "account-admin-1",
        ReferenceAgentFoundationFixtures.AGENT_ID,
        "Allow the agent to call createBookingHold after recommending an activity.",
        "tool_boundary",
        ReferenceAgentFoundationFixtures.TOOL_BOUNDARY_ID,
        "corr-behavior-tool-expansion");
  }

  private static ReferenceBehaviorChangeRequest toChangeRequest(
      ReferenceAgentBehaviorEditorAgent.BehaviorEditProposalIntent intent) {
    return new ReferenceBehaviorChangeRequest(
        intent.tenantId(),
        intent.requestId(),
        "account-admin-1",
        intent.targetAgentDefinitionId(),
        intent.targetArtifactType(),
        intent.targetArtifactId(),
        intent.proposedDiffSummary(),
        Set.copyOf(intent.expansionTypes()),
        intent.correlationId());
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
