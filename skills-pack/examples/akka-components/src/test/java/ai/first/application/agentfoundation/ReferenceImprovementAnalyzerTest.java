package ai.first.application.agentfoundation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.first.domain.agentfoundation.ReferenceAgentDefinition;
import ai.first.domain.agentfoundation.ReferenceAgentSkillManifest;
import ai.first.domain.agentfoundation.ReferenceAgentWorkTrace;
import ai.first.domain.agentfoundation.ReferenceBehaviorEditRisk;
import ai.first.domain.agentfoundation.ReferenceBehaviorEditTrace;
import ai.first.domain.agentfoundation.ReferenceEvaluationFinding;
import ai.first.domain.agentfoundation.ReferenceEvaluationRun;
import ai.first.domain.agentfoundation.ReferenceImprovementProposal;
import ai.first.domain.agentfoundation.ReferenceImprovementTrace;
import ai.first.domain.agentfoundation.ReferencePromptVersion;
import ai.first.domain.agentfoundation.ReferenceSkillDocument;
import ai.first.domain.agentfoundation.ReferenceSkillVersion;
import ai.first.domain.agentfoundation.ReferenceToolPermissionBoundary;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ReferenceImprovementAnalyzerTest {

  @Test
  void normalizesTenantScopedFindingsWithSourceTraceLinks() {
    var traceSink = new ReferenceTraceSink();
    var analyzer = analyzer(traceSink);
    var crossTenantFinding =
        new ReferenceEvaluationFinding(
            ReferenceAgentFoundationFixtures.OTHER_TENANT_ID,
            "evaluation-finding-cross-tenant",
            ReferenceAgentFoundationFixtures.EVALUATION_RUN_ID,
            ReferenceEvaluationFinding.FindingCategory.USER_VALUE,
            ReferenceEvaluationFinding.FindingSeverity.MEDIUM,
            0.9,
            ReferenceAgentFoundationFixtures.AGENT_ID,
            "prompt",
            ReferenceAgentFoundationFixtures.PROMPT_DOCUMENT_ID,
            "prompt_wording_change",
            List.of(ReferenceAgentFoundationFixtures.SOURCE_WORK_TRACE_ID),
            "Cross-tenant finding must not be normalized.",
            "corr-cross-tenant");

    var normalized =
        analyzer.normalizeFindings(
            ReferenceAgentFoundationFixtures.authContext(),
            ReferenceAgentFoundationFixtures.failedEvaluationRun(),
            List.of(ReferenceAgentFoundationFixtures.failedEvaluationFinding(), crossTenantFinding));

    assertEquals(1, normalized.size());
    assertEquals(ReferenceAgentFoundationFixtures.EVALUATION_FINDING_ID, normalized.getFirst().findingId());
    assertEquals(
        ReferenceAgentFoundationFixtures.SOURCE_WORK_TRACE_ID,
        normalized.getFirst().sourceTraceIds().getFirst());
  }

  @Test
  void createsDraftImprovementProposalLinkedToBehaviorEditProposal() {
    var traceSink = new ReferenceTraceSink();
    var analyzer = analyzer(traceSink);

    var proposal =
        analyzer.proposeImprovement(
            ReferenceAgentFoundationFixtures.authContext(),
            ReferenceAgentFoundationFixtures.failedEvaluationRun(),
            List.of(ReferenceAgentFoundationFixtures.failedEvaluationFinding()));

    assertEquals(ReferenceImprovementProposal.ImprovementStatus.DRAFT, proposal.status());
    assertEquals(
        "improvement-proposal-from-" + ReferenceAgentFoundationFixtures.EVALUATION_RUN_ID,
        proposal.improvementProposalId());
    assertEquals(List.of(ReferenceAgentFoundationFixtures.EVALUATION_RUN_ID), proposal.evaluationRunIds());
    assertEquals(List.of(ReferenceAgentFoundationFixtures.EVALUATION_FINDING_ID), proposal.findingIds());
    assertTrue(proposal.behaviorEditProposalId().startsWith("behavior-proposal-behavior-request-from-"));
    assertEquals("prompt_wording_change", proposal.proposedChangeKind());
    assertTrue(proposal.expectedOutcome().contains("behavior edit proposal"));
  }

  @Test
  void recordsSourceTraceLinkageAndImprovementTraceFacts() {
    var traceSink = new ReferenceTraceSink();
    var analyzer = analyzer(traceSink);

    var proposal =
        analyzer.proposeImprovement(
            ReferenceAgentFoundationFixtures.authContext(),
            ReferenceAgentFoundationFixtures.failedEvaluationRun(),
            List.of(ReferenceAgentFoundationFixtures.failedEvaluationFinding()));

    assertTrue(proposal.expectedOutcome().contains("source trace"));
    assertTrue(
        traceSink.improvementTraces().stream()
            .anyMatch(trace -> trace.event() == ReferenceImprovementTrace.TraceEvent.FINDING_NORMALIZED));
    assertTrue(
        traceSink.improvementTraces().stream()
            .anyMatch(trace -> trace.event() == ReferenceImprovementTrace.TraceEvent.BEHAVIOR_EDIT_LINKED));
    assertTrue(
        traceSink.improvementTraces().stream()
            .anyMatch(trace -> trace.event() == ReferenceImprovementTrace.TraceEvent.PROPOSAL_CREATED));
    assertTrue(
        traceSink.improvementTraces().stream()
            .allMatch(trace -> trace.sourceWorkTraceId().equals(ReferenceAgentFoundationFixtures.SOURCE_WORK_TRACE_ID)));
  }

  @Test
  void noDirectActivationOrActivePromptMutationOccurs() {
    var traceSink = new ReferenceTraceSink();
    var promptVersions = promptVersions();
    var activePromptBefore = promptVersions.get(ReferenceAgentFoundationFixtures.PROMPT_VERSION_ID);
    var analyzer = analyzer(traceSink, promptVersions);

    var proposal =
        analyzer.proposeImprovement(
            ReferenceAgentFoundationFixtures.authContext(),
            ReferenceAgentFoundationFixtures.failedEvaluationRun(),
            List.of(ReferenceAgentFoundationFixtures.failedEvaluationFinding()));

    assertEquals(ReferenceImprovementProposal.ImprovementStatus.DRAFT, proposal.status());
    assertTrue(proposal.replaySimulationIds().isEmpty());
    assertTrue(proposal.expectedOutcome().contains("no direct activation"));
    assertTrue(proposal.expectedOutcome().contains("no behavior-edit control bypass"));
    assertSame(activePromptBefore, promptVersions.get(ReferenceAgentFoundationFixtures.PROMPT_VERSION_ID));
    assertEquals(ReferencePromptVersion.VersionStatus.ACTIVE, activePromptBefore.status());
    assertFalse(traceSink.behaviorEditTraces().isEmpty());
  }

  @Test
  void cannotBypassBehaviorEditRiskClassificationForAuthorityExpansion() {
    var traceSink = new ReferenceTraceSink();
    var analyzer = analyzer(traceSink);
    var toolBoundaryFinding =
        new ReferenceEvaluationFinding(
            ReferenceAgentFoundationFixtures.TENANT_ID,
            "evaluation-finding-tool-boundary-expansion",
            ReferenceAgentFoundationFixtures.EVALUATION_RUN_ID,
            ReferenceEvaluationFinding.FindingCategory.TOOL_USE,
            ReferenceEvaluationFinding.FindingSeverity.HIGH,
            0.93,
            ReferenceAgentFoundationFixtures.AGENT_ID,
            "tool_boundary",
            ReferenceAgentFoundationFixtures.TOOL_BOUNDARY_ID,
            "tool_boundary_change",
            List.of(ReferenceAgentFoundationFixtures.SOURCE_WORK_TRACE_ID),
            "The agent needs a new side-effecting booking-hold tool to improve task completion.",
            "corr-improvement-tool-boundary");

    var proposal =
        analyzer.proposeImprovement(
            ReferenceAgentFoundationFixtures.authContext(),
            ReferenceAgentFoundationFixtures.failedEvaluationRun(),
            List.of(toolBoundaryFinding));

    assertEquals(ReferenceImprovementProposal.ImprovementStatus.DRAFT, proposal.status());
    assertTrue(proposal.behaviorEditProposalId().contains(toolBoundaryFinding.findingId()));
    assertTrue(proposal.expectedOutcome().contains("no behavior-edit control bypass"));
    assertTrue(
        traceSink.behaviorEditTraces().stream()
            .anyMatch(
                trace ->
                    trace.event() == ReferenceBehaviorEditTrace.TraceEvent.PROPOSAL_CREATED
                        && trace.risk() == ReferenceBehaviorEditRisk.HIGH
                        && trace.safeSummary().equals("create_decision_card")));
    assertTrue(
        traceSink.improvementTraces().stream()
            .anyMatch(
                trace ->
                    trace.event() == ReferenceImprovementTrace.TraceEvent.BEHAVIOR_EDIT_LINKED
                        && trace.safeSummary().contains("cannot bypass behavior-edit risk classification")));
  }

  @Test
  void rejectsPassedEvaluationRunsInsteadOfCreatingProposal() {
    var traceSink = new ReferenceTraceSink();
    var analyzer = analyzer(traceSink);
    var passedRun =
        new ReferenceEvaluationRun(
            ReferenceAgentFoundationFixtures.TENANT_ID,
            "evaluation-run-passed",
            ReferenceAgentFoundationFixtures.AGENT_ID,
            ReferenceAgentFoundationFixtures.SOURCE_WORK_TRACE_ID,
            "rubric-concise-helpful-activity-answer",
            ReferenceEvaluationRun.EvaluationStatus.COMPLETED,
            true,
            96,
            List.of(),
            "corr-improvement-passed");

    assertThrows(
        IllegalArgumentException.class,
        () ->
            analyzer.proposeImprovement(
                ReferenceAgentFoundationFixtures.authContext(),
                passedRun,
                List.of(ReferenceAgentFoundationFixtures.failedEvaluationFinding())));
  }

  private static ReferenceImprovementAnalyzer analyzer(ReferenceTraceSink traceSink) {
    return analyzer(traceSink, promptVersions());
  }

  private static ReferenceImprovementAnalyzer analyzer(
      ReferenceTraceSink traceSink, Map<String, ReferencePromptVersion> promptVersions) {
    return new ReferenceImprovementAnalyzer(
        editor(traceSink, promptVersions), sourceWorkTraces(), traceSink);
  }

  private static ReferenceAgentBehaviorEditor editor(
      ReferenceTraceSink traceSink, Map<String, ReferencePromptVersion> promptVersions) {
    return new ReferenceAgentBehaviorEditor(
        agentDefinitions(),
        promptVersions,
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

  private static Map<String, ReferenceAgentWorkTrace> sourceWorkTraces() {
    return Map.of(
        ReferenceAgentFoundationFixtures.SOURCE_WORK_TRACE_ID,
        ReferenceAgentFoundationFixtures.sourceAgentWorkTrace());
  }
}
