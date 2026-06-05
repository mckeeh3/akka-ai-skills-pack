package ai.first.application.agentfoundation;

import ai.first.domain.agentfoundation.ReferenceAgentWorkTrace;
import ai.first.domain.agentfoundation.ReferenceAuthContext;
import ai.first.domain.agentfoundation.ReferenceBehaviorChangeRequest;
import ai.first.domain.agentfoundation.ReferenceBehaviorEditProposal;
import ai.first.domain.agentfoundation.ReferenceEvaluationFinding;
import ai.first.domain.agentfoundation.ReferenceEvaluationRun;
import ai.first.domain.agentfoundation.ReferenceImprovementProposal;
import ai.first.domain.agentfoundation.ReferenceImprovementTrace;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Deterministic reference helper for closed-loop improvement proposal creation.
 *
 * <p>The analyzer turns evaluator findings and source trace summaries into a governed
 * ImprovementProposal. Prompt/skill/manifest/tool-boundary changes are routed through the
 * behavior-edit helper; this reference never performs direct activation or active-record mutation.
 */
public final class ReferenceImprovementAnalyzer {
  private final ReferenceAgentBehaviorEditor behaviorEditor;
  private final Map<String, ReferenceAgentWorkTrace> sourceWorkTraces;
  private final ReferenceTraceSink traceSink;

  public ReferenceImprovementAnalyzer(
      ReferenceAgentBehaviorEditor behaviorEditor,
      Map<String, ReferenceAgentWorkTrace> sourceWorkTraces,
      ReferenceTraceSink traceSink) {
    this.behaviorEditor = behaviorEditor;
    this.sourceWorkTraces = Map.copyOf(sourceWorkTraces);
    this.traceSink = traceSink;
  }

  public ReferenceImprovementProposal proposeImprovement(
      ReferenceAuthContext authContext,
      ReferenceEvaluationRun evaluationRun,
      List<ReferenceEvaluationFinding> findings) {
    if (!authContext.tenantId().equals(evaluationRun.tenantId())) {
      throw new IllegalArgumentException("cross-tenant evaluation run denied");
    }
    if (evaluationRun.status() != ReferenceEvaluationRun.EvaluationStatus.COMPLETED) {
      throw new IllegalArgumentException("evaluation run must be completed before improvement analysis");
    }
    if (evaluationRun.passed()) {
      throw new IllegalArgumentException("passed evaluation runs do not create improvement proposals");
    }

    var normalizedFindings = normalizeFindings(authContext, evaluationRun, findings);
    if (normalizedFindings.isEmpty()) {
      throw new IllegalArgumentException("at least one tenant-scoped finding is required");
    }

    var primaryFinding = normalizedFindings.getFirst();
    var improvementProposalId = improvementProposalId(evaluationRun.evaluationRunId());
    normalizedFindings.forEach(finding -> recordFindingNormalized(improvementProposalId, evaluationRun, finding));

    var behaviorEditProposal = createBehaviorEditProposal(authContext, primaryFinding, evaluationRun);
    traceSink.recordImprovement(
        new ReferenceImprovementTrace(
            evaluationRun.tenantId(),
            "improvement-trace-behavior-edit-linked-" + improvementProposalId,
            improvementProposalId,
            evaluationRun.evaluationRunId(),
            evaluationRun.sourceWorkTraceId(),
            ReferenceImprovementTrace.TraceEvent.BEHAVIOR_EDIT_LINKED,
            "Linked improvement proposal to behavior edit proposal "
                + behaviorEditProposal.proposalId()
                + "; closed-loop improvement cannot bypass behavior-edit risk classification.",
            evaluationRun.correlationId()));

    var sourceTraceSummary = sourceTraceSummary(evaluationRun.sourceWorkTraceId());
    var proposal =
        new ReferenceImprovementProposal(
            evaluationRun.tenantId(),
            improvementProposalId,
            evaluationRun.targetAgentDefinitionId(),
            List.of(evaluationRun.evaluationRunId()),
            normalizedFindings.stream().map(ReferenceEvaluationFinding::findingId).toList(),
            behaviorEditProposal.proposalId(),
            primaryFinding.suggestedImprovementKind(),
            ReferenceImprovementProposal.ImprovementStatus.DRAFT,
            "Create governed behavior edit proposal from evaluation finding; source trace: "
                + sourceTraceSummary
                + "; no direct activation and no behavior-edit control bypass.",
            rollbackBaselineId(primaryFinding),
            List.of(),
            evaluationRun.correlationId());

    traceSink.recordImprovement(
        new ReferenceImprovementTrace(
            proposal.tenantId(),
            "improvement-trace-proposal-created-" + proposal.improvementProposalId(),
            proposal.improvementProposalId(),
            evaluationRun.evaluationRunId(),
            evaluationRun.sourceWorkTraceId(),
            ReferenceImprovementTrace.TraceEvent.PROPOSAL_CREATED,
            "Created draft ImprovementProposal with source trace linkage and no direct activation.",
            proposal.correlationId()));
    return proposal;
  }

  public List<ReferenceEvaluationFinding> normalizeFindings(
      ReferenceAuthContext authContext,
      ReferenceEvaluationRun evaluationRun,
      List<ReferenceEvaluationFinding> findings) {
    return findings.stream()
        .filter(finding -> finding.tenantId().equals(authContext.tenantId()))
        .filter(finding -> finding.evaluationRunId().equals(evaluationRun.evaluationRunId()))
        .filter(finding -> !finding.sourceTraceIds().isEmpty())
        .toList();
  }

  private ReferenceBehaviorEditProposal createBehaviorEditProposal(
      ReferenceAuthContext authContext,
      ReferenceEvaluationFinding finding,
      ReferenceEvaluationRun evaluationRun) {
    var request =
        new ReferenceBehaviorChangeRequest(
            finding.tenantId(),
            "behavior-request-from-" + finding.findingId(),
            authContext.accountId(),
            finding.affectedAgentDefinitionId(),
            finding.affectedArtifactType(),
            finding.affectedArtifactId(),
            "Address evaluator finding " + finding.findingId() + ": " + finding.safeSummary(),
            expansionTypesFor(finding),
            evaluationRun.correlationId());
    return behaviorEditor.propose(authContext, request);
  }

  private Set<String> expansionTypesFor(ReferenceEvaluationFinding finding) {
    if ("tool_boundary_change".equals(finding.suggestedImprovementKind())) {
      return Set.of("tool");
    }
    return Set.of();
  }

  private void recordFindingNormalized(
      String improvementProposalId,
      ReferenceEvaluationRun evaluationRun,
      ReferenceEvaluationFinding finding) {
    traceSink.recordImprovement(
        new ReferenceImprovementTrace(
            finding.tenantId(),
            "improvement-trace-finding-normalized-" + finding.findingId(),
            improvementProposalId,
            evaluationRun.evaluationRunId(),
            finding.sourceTraceIds().getFirst(),
            ReferenceImprovementTrace.TraceEvent.FINDING_NORMALIZED,
            "Normalized EvaluationFinding " + finding.findingId() + " for ImprovementProposal creation.",
            finding.correlationId()));
  }

  private String sourceTraceSummary(String sourceWorkTraceId) {
    var trace = sourceWorkTraces.get(sourceWorkTraceId);
    if (trace == null) {
      return "missing source trace " + sourceWorkTraceId;
    }
    return trace.summary();
  }

  private static String improvementProposalId(String evaluationRunId) {
    return "improvement-proposal-from-" + evaluationRunId;
  }

  private static String rollbackBaselineId(ReferenceEvaluationFinding finding) {
    return "rollback-baseline-" + finding.affectedArtifactType() + "-" + finding.affectedArtifactId();
  }
}
