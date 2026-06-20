package ai.first.application.coreapp.governance;

import ai.first.application.coreapp.governance.FailClosedGovernancePolicyImpactAutonomousAgentRuntime;
import ai.first.application.coreapp.governance.GovernancePolicyImpactAutonomousAgentRuntime;
import ai.first.domain.coreapp.governance.GovernancePolicyImpactTask;
import ai.first.domain.foundation.governance.GovernancePolicyProposal;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import ai.first.application.foundation.attention.AttentionProducerService;
import ai.first.application.foundation.governance.GovernancePolicyRepository;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.AuthorizationException;
import ai.first.application.foundation.workstream.WorkstreamEventPublisher;

/** Deterministic lifecycle owner for Governance/Policy impact-analysis AutonomousAgent tasks. */
public final class GovernancePolicyImpactService {
  public static final String START_CAPABILITY = "governance.policy.impact_analysis.start";
  public static final String READ_CAPABILITY = "governance.policy.impact_analysis.read";
  public static final String CANCEL_CAPABILITY = "governance.policy.impact_analysis.cancel";
  public static final String ACCEPT_RESULT_CAPABILITY = "governance.policy.impact_analysis.accept_result";
  public static final String REJECT_RESULT_CAPABILITY = "governance.policy.impact_analysis.reject_result";
  public static final String REQUEST_CHANGES_CAPABILITY = "governance.policy.impact_analysis.request_changes";

  private final GovernancePolicyImpactTaskRepository repository;
  private final GovernancePolicyRepository governancePolicyRepository;
  private final AuthContextResolver authContextResolver;
  private final Clock clock;
  private final AttentionProducerService attentionProducerService;
  private final WorkstreamEventPublisher workstreamEventPublisher;
  private final GovernancePolicyImpactAutonomousAgentRuntime autonomousAgentRuntime;

  public GovernancePolicyImpactService(GovernancePolicyImpactTaskRepository repository, GovernancePolicyRepository governancePolicyRepository, AuthContextResolver authContextResolver, Clock clock) {
    this(repository, governancePolicyRepository, authContextResolver, clock, null, null, new FailClosedGovernancePolicyImpactAutonomousAgentRuntime());
  }

  public GovernancePolicyImpactService(GovernancePolicyImpactTaskRepository repository, GovernancePolicyRepository governancePolicyRepository, AuthContextResolver authContextResolver, Clock clock, GovernancePolicyImpactAutonomousAgentRuntime autonomousAgentRuntime) {
    this(repository, governancePolicyRepository, authContextResolver, clock, null, null, autonomousAgentRuntime);
  }

  public GovernancePolicyImpactService(GovernancePolicyImpactTaskRepository repository, GovernancePolicyRepository governancePolicyRepository, AuthContextResolver authContextResolver, Clock clock, AttentionProducerService attentionProducerService, WorkstreamEventPublisher workstreamEventPublisher, GovernancePolicyImpactAutonomousAgentRuntime autonomousAgentRuntime) {
    this.repository = Objects.requireNonNull(repository);
    this.governancePolicyRepository = Objects.requireNonNull(governancePolicyRepository);
    this.authContextResolver = Objects.requireNonNull(authContextResolver);
    this.clock = Objects.requireNonNull(clock);
    this.attentionProducerService = attentionProducerService;
    this.workstreamEventPublisher = workstreamEventPublisher;
    this.autonomousAgentRuntime = Objects.requireNonNull(autonomousAgentRuntime);
  }

  public GovernancePolicyImpactTask start(AuthContextResolver.ResolvedMe actor, StartGovernancePolicyImpactCommand command, String correlationId) {
    if (command == null) throw new AuthorizationException(400, "governance-impact-command-required");
    if (blank(command.idempotencyKey())) throw new AuthorizationException(400, "idempotency-key-required");
    if (blank(command.proposalId())) throw new AuthorizationException(400, "proposal-required");
    require(actor, START_CAPABILITY, correlationId);
    var proposal = governancePolicyRepository.findProposal(actor.selectedContext().tenantId(), actor.selectedContext().customerId(), command.proposalId())
        .orElseThrow(() -> new AuthorizationException(404, "governance-policy-proposal-not-found-or-forbidden"));
    var duplicate = repository.findByIdempotencyKey(actor.selectedContext().tenantId(), actor.account().accountId(), command.idempotencyKey());
    if (duplicate.isPresent()) {
      authContextResolver.appendProtectedReadTrace(actor, START_CAPABILITY, "idempotent-governance-policy-impact-replay", correlationId);
      return duplicate.orElseThrow();
    }
    var now = Instant.now(clock);
    var taskId = "governance-impact-" + stableSuffix(actor.selectedContext().tenantId() + ":" + actor.account().accountId() + ":" + command.idempotencyKey());
    var traceId = "trace-governance-policy-impact-start-" + stableSuffix(correlationId + ":" + taskId);
    var task = new GovernancePolicyImpactTask(
        taskId,
        null,
        proposal.proposalId(),
        firstNonBlank(command.targetPolicyId(), proposal.targetPolicyId()),
        actor.selectedContext().tenantId(),
        actor.selectedContext().customerId(),
        actor.account().accountId(),
        actor.selectedContext().membershipId(),
        command.idempotencyKey(),
        GovernancePolicyImpactTask.Status.QUEUED,
        0,
        "Governance/Policy impact task record created; governed Akka AutonomousAgent task start is being attempted through the backend runtime path.",
        null,
        null,
        null,
        merge(command.affectedCapabilityIds(), proposal.affectedCapabilityIds()),
        merge(command.affectedArtifactRefs(), proposal.affectedArtifactRefs()),
        evidenceRefs(command, proposal),
        List.of(),
        List.of(traceId),
        now,
        now);
    repository.save(task);
    var start = autonomousAgentRuntime.start(actor, task, command.evidenceRequest(), correlationId);
    var startTraceIds = new java.util.ArrayList<>(task.traceIds());
    startTraceIds.addAll(start.traceIds());
    var started = task.withAutonomousAgentTaskId(start.autonomousAgentTaskId(), start.status(), start.progressPercent(), start.summary(), start.blockerCode(), startTraceIds, Instant.now(clock));
    repository.save(started);
    authContextResolver.appendProtectedReadTrace(actor, START_CAPABILITY, start.status() == GovernancePolicyImpactTask.Status.BLOCKED_PROVIDER_OR_RUNTIME ? "provider-blocked-fail-closed:no fake success" : "autonomous-agent-task-started", correlationId);
    publishLifecycleOrAttention(started, start.status() == GovernancePolicyImpactTask.Status.BLOCKED_PROVIDER_OR_RUNTIME ? "blocked_provider_or_runtime" : "started", START_CAPABILITY, actor.account().accountId(), correlationId);
    return started;
  }

  public GovernancePolicyImpactTask read(AuthContextResolver.ResolvedMe actor, String impactTaskId, String correlationId) {
    var task = task(actor, impactTaskId);
    require(actor, READ_CAPABILITY, correlationId);
    var projected = projectAutonomousAgentTask(task, correlationId);
    authContextResolver.appendProtectedReadTrace(actor, READ_CAPABILITY, "browser-safe-governance-policy-impact-read", correlationId);
    return projected;
  }

  public GovernancePolicyImpactTask cancel(AuthContextResolver.ResolvedMe actor, String impactTaskId, String reason, String correlationId) {
    var task = task(actor, impactTaskId);
    require(actor, CANCEL_CAPABILITY, correlationId);
    if (task.terminal()) return task;
    autonomousAgentRuntime.cancel(task, reason, correlationId);
    var traceId = "trace-governance-policy-impact-cancel-" + stableSuffix(correlationId + ":" + task.impactTaskId());
    var cancelled = task.withStatus(GovernancePolicyImpactTask.Status.CANCELLED, task.progressPercent(), firstNonBlank(reason, "Governance/Policy impact analysis cancelled by authorized reviewer; policy proposal unchanged."), null, List.of(traceId), Instant.now(clock));
    repository.save(cancelled);
    authContextResolver.appendProtectedReadTrace(actor, CANCEL_CAPABILITY, "cancelled:no direct policy mutation", correlationId);
    publishLifecycleOrAttention(cancelled, "cancelled", CANCEL_CAPABILITY, actor.account().accountId(), correlationId);
    return cancelled;
  }

  public GovernancePolicyImpactTask acceptResult(AuthContextResolver.ResolvedMe actor, String impactTaskId, String reason, String correlationId) {
    return decide(actor, impactTaskId, GovernancePolicyImpactTask.Status.ACCEPTED, "accepted", ACCEPT_RESULT_CAPABILITY, reason, correlationId);
  }

  public GovernancePolicyImpactTask rejectResult(AuthContextResolver.ResolvedMe actor, String impactTaskId, String reason, String correlationId) {
    if (blank(reason)) throw new AuthorizationException(400, "governance-impact-rejection-reason-required");
    return decide(actor, impactTaskId, GovernancePolicyImpactTask.Status.REJECTED_RESULT, "rejected_result", REJECT_RESULT_CAPABILITY, reason, correlationId);
  }

  public GovernancePolicyImpactTask requestChanges(AuthContextResolver.ResolvedMe actor, String impactTaskId, String reason, String correlationId) {
    if (blank(reason)) throw new AuthorizationException(400, "governance-impact-request-changes-reason-required");
    return decide(actor, impactTaskId, GovernancePolicyImpactTask.Status.REQUEST_CHANGES, "request_changes", REQUEST_CHANGES_CAPABILITY, reason, correlationId);
  }

  public SurfaceData taskSurface(AuthContextResolver.ResolvedMe actor, String impactTaskId, String correlationId) {
    var task = read(actor, impactTaskId, correlationId);
    var status = task.status().name().toLowerCase(java.util.Locale.ROOT);
    var traceIds = task.traceIds().isEmpty() ? List.of(trace("impact-task", correlationId)) : task.traceIds();
    return new SurfaceData("surface-governance-policy-impact-analysis-task", "workflow-status", "Governance/Policy impact analysis task", traceIds, Map.ofEntries(
        Map.entry("surfaceContract", "governance.policy.impact_analysis.task.v1"),
        Map.entry("workflowId", "governance-policy-impact-analysis"),
        Map.entry("impactTaskId", task.impactTaskId()),
        Map.entry("taskId", task.impactTaskId()),
        Map.entry("autonomousAgentTaskId", safe(task.autonomousAgentTaskId(), "")),
        Map.entry("proposalId", task.proposalId()),
        Map.entry("targetPolicyId", safe(task.targetPolicyId(), "")),
        Map.entry("status", status),
        Map.entry("taskSummary", Map.of("impactTaskId", task.impactTaskId(), "proposalId", task.proposalId(), "status", status, "progressPercent", task.progressPercent(), "updatedAt", task.updatedAt().toString(), "safeCopy", safe(task.summary(), "Governance/Policy impact analysis task state is backend-owned."))),
        Map.entry("progress", Map.of("percent", task.progressPercent(), "summary", safe(task.summary(), "Governance/Policy impact analysis task state is backend-owned."))),
        Map.entry("steps", taskSteps(task)),
        Map.entry("progressPercent", task.progressPercent()),
        Map.entry("blockerCode", safe(task.blockerCode(), "")),
        Map.entry("blockers", taskBlockers(task)),
        Map.entry("providerFailures", providerFailures(task)),
        Map.entry("summary", safe(task.summary(), "")),
        Map.entry("evidenceRefs", task.evidenceRefs()),
        Map.entry("findingRefs", task.findingRefs()),
        Map.entry("traceIds", traceIds),
        Map.entry("traceLinks", traceLinks(traceIds, correlationId)),
        Map.entry("authorizedActions", List.of(READ_CAPABILITY, CANCEL_CAPABILITY)),
        Map.entry("disabledActions", disabledActions(task)),
        Map.entry("readiness", Map.of("state", readinessState(task), "providerRuntime", task.status() == GovernancePolicyImpactTask.Status.BLOCKED_PROVIDER_OR_RUNTIME ? "blocked_provider_or_runtime" : "backend lifecycle projection", "fakeSuccess", false)),
        Map.entry("noDirectMutation", true),
        Map.entry("activationBlocked", true),
        Map.entry("activationBlockedUntilHumanDecision", true),
        Map.entry("noFakeSuccess", true),
        Map.entry("redaction", "browser-safe; raw prompts, provider credentials, JWTs, raw tool payloads, and cross-tenant/customer data omitted")));
  }

  public SurfaceData resultSurface(AuthContextResolver.ResolvedMe actor, String impactTaskId, String correlationId) {
    if (blank(impactTaskId)) return emptyResultSurface(actor, correlationId);
    var task = read(actor, impactTaskId, correlationId);
    if (task.status() != GovernancePolicyImpactTask.Status.COMPLETED_REVIEW_REQUIRED
        && task.status() != GovernancePolicyImpactTask.Status.ACCEPTED
        && task.status() != GovernancePolicyImpactTask.Status.REJECTED_RESULT
        && task.status() != GovernancePolicyImpactTask.Status.REQUEST_CHANGES) {
      return taskSurface(actor, impactTaskId, correlationId);
    }
    var reviewState = surfaceStatus(task.status());
    var traceIds = resultTraceIds(task, correlationId);
    var risk = task.findingRefs().stream().anyMatch(ref -> ref.toLowerCase(java.util.Locale.ROOT).contains("critical")) ? "critical" : "high";
    return new SurfaceData("surface-governance-policy-impact-analysis-result", "decision", "Governance/Policy impact analysis result", traceIds, Map.ofEntries(
        Map.entry("surfaceContract", "governance.policy.impact_analysis.result.v1"),
        Map.entry("impactTaskId", task.impactTaskId()),
        Map.entry("taskId", task.impactTaskId()),
        Map.entry("proposalId", task.proposalId()),
        Map.entry("targetPolicyId", safe(task.targetPolicyId(), "")),
        Map.entry("decisionId", task.impactTaskId()),
        Map.entry("reviewState", reviewState),
        Map.entry("overallRisk", risk),
        Map.entry("risk", risk),
        Map.entry("confidenceScore", task.status() == GovernancePolicyImpactTask.Status.COMPLETED_REVIEW_REQUIRED || task.status() == GovernancePolicyImpactTask.Status.ACCEPTED ? "human-review-required" : "disposition-recorded"),
        Map.entry("resultSummary", Map.of("impactTaskId", task.impactTaskId(), "proposalId", task.proposalId(), "reviewState", reviewState, "overallRisk", risk, "completedAgeBucket", "current-projection", "safeEmptyCopy", "Select a completed impact-analysis task to review advisory evidence.")),
        Map.entry("recommendation", Map.of("outcome", "Review advisory impact evidence before any separate Governance/Policy approval or activation path.", "rationale", safe(task.summary(), "Governance/Policy impact analysis requires human review."), "providerRuntimeReadiness", readinessState(task), "noFakeSuccess", true)),
        Map.entry("summary", safe(task.summary(), "Governance/Policy impact analysis requires human review.")),
        Map.entry("advisorySummary", Map.of("narrative", safe(task.summary(), "Governance/Policy impact analysis requires human review."), "advisoryOnly", "This result does not approve, activate, roll back, weaken policy, or mutate authority.", "knownLimitations", List.of("Raw prompts, model output, tool payloads, hidden policy clauses, and cross-tenant/customer evidence are omitted from the browser payload."))),
        Map.entry("impact", "Advisory evidence disposition only; policy proposal state and authority remain unchanged until separate governed decision/activation commands run."),
        Map.entry("affectedTarget", firstNonBlank(task.targetPolicyId(), task.proposalId())),
        Map.entry("policyBasis", "governance.policy.impact_analysis.* capabilities with selected AuthContext tenant/customer scope"),
        Map.entry("idempotencyKeySource", "surface-item"),
        Map.entry("activationBlocker", task.status() == GovernancePolicyImpactTask.Status.ACCEPTED ? "Accepted impact evidence is advisory; activation still requires a separate approved Governance/Policy command." : "Activation remains blocked until an authorized human disposition and separate policy decision path complete."),
        Map.entry("findings", findingSummaries(task)),
        Map.entry("impactFindings", task.findingRefs()),
        Map.entry("evidenceRefs", task.evidenceRefs()),
        Map.entry("evidenceSummary", evidenceSummary(task, traceIds)),
        Map.entry("humanDecision", Map.of("reviewState", reviewState, "allowedDispositionValues", List.of("accept", "reject", "request_changes"), "reasonRequiredFor", List.of("reject", "request_changes"), "priorDisposition", safe(task.decision(), ""), "priorReason", safe(task.decisionReason(), ""), "idempotency", "surface-item replay returns original advisory disposition or safe conflict")),
        Map.entry("activationGate", Map.of("acceptedImpactEvidenceRequired", true, "currentGateState", task.status() == GovernancePolicyImpactTask.Status.ACCEPTED ? "impact-evidence-accepted-but-policy-decision-still-required" : "blocked-pending-human-impact-result-disposition", "separateDecisionSurface", "surface-governance-policy-decision", "noInlineActivation", true)),
        Map.entry("authorizedActions", allowedResultActions(actor, task)),
        Map.entry("allowedActions", allowedResultActions(actor, task)),
        Map.entry("disabledActions", disabledResultActions(task)),
        Map.entry("readiness", Map.of("state", readinessState(task), "providerRuntime", "backend AutonomousAgent projection produced or preserved advisory evidence", "fakeSuccess", false)),
        Map.entry("traceIds", traceIds),
        Map.entry("traceLinks", traceIds),
        Map.entry("requiredHumanDecisions", List.of("review advisory impact result", "record accept/reject/request-changes disposition when authorized", "separately approve/reject proposal", "separately activate approved policy change if still authorized")),
        Map.entry("noDirectMutation", true),
        Map.entry("noFakeSuccess", true),
        Map.entry("activationBlockedUntilHumanDecision", task.status() != GovernancePolicyImpactTask.Status.ACCEPTED),
        Map.entry("redaction", "browser-safe impact evidence only; raw prompts, hidden prompt text, provider credentials, JWTs, raw tool payloads, hidden role clauses, raw correlation/idempotency keys, and cross-tenant/customer data omitted")));
  }

  private GovernancePolicyImpactTask decide(AuthContextResolver.ResolvedMe actor, String impactTaskId, GovernancePolicyImpactTask.Status status, String decision, String capabilityId, String reason, String correlationId) {
    var task = task(actor, impactTaskId);
    require(actor, capabilityId, correlationId);
    if (task.status() == status && Objects.equals(task.decision(), decision)) {
      authContextResolver.appendProtectedReadTrace(actor, capabilityId, decision + ":idempotent-result-disposition-replay:no direct policy mutation", correlationId);
      return task;
    }
    if (task.terminal()) throw new AuthorizationException(409, "governance-impact-result-disposition-conflict");
    if (!task.resultDecisionAllowed()) throw new AuthorizationException(409, "governance-impact-result-not-completed");
    var traceId = "trace-governance-policy-impact-" + decision + "-" + stableSuffix(correlationId + ":" + task.impactTaskId());
    var decided = task.withDecision(status, decision, firstNonBlank(reason, "Human Governance/Policy impact result disposition recorded; policy proposal unchanged."), List.of(traceId), Instant.now(clock));
    repository.save(decided);
    authContextResolver.appendProtectedReadTrace(actor, capabilityId, decision + ":advisory-only:no direct approval activation rollback or policy mutation", correlationId);
    publishLifecycleOrAttention(decided, decision.equals("accepted") ? "result_accepted" : decision.equals("rejected_result") ? "result_rejected" : "request_changes", capabilityId, actor.account().accountId(), correlationId);
    return decided;
  }

  private SurfaceData emptyResultSurface(AuthContextResolver.ResolvedMe actor, String correlationId) {
    require(actor, READ_CAPABILITY, correlationId);
    var traceIds = List.of(trace("impact-result-empty", correlationId));
    return new SurfaceData("surface-governance-policy-impact-analysis-result", "decision", "Governance/Policy impact analysis result", traceIds, Map.ofEntries(
        Map.entry("surfaceContract", "governance.policy.impact_analysis.result.v1"),
        Map.entry("reviewState", "empty/no-result"),
        Map.entry("summary", "Select a completed Governance/Policy impact-analysis task before reviewing advisory evidence."),
        Map.entry("resultSummary", Map.of("reviewState", "empty/no-result", "safeEmptyCopy", "No completed impact-analysis task is selected; hidden tasks are not enumerated.")),
        Map.entry("recommendation", Map.of("outcome", "Open an authorized impact-analysis task or proposal first.", "rationale", "The direct result view is backend-derived and no-enumeration safe when no task id is selected.", "providerRuntimeReadiness", "not-required", "noFakeSuccess", true)),
        Map.entry("advisorySummary", Map.of("narrative", "No advisory result is selected.", "advisoryOnly", "No policy approval, activation, rollback, or authority mutation occurs from this empty result view.")),
        Map.entry("evidenceSummary", List.of()),
        Map.entry("findings", List.of()),
        Map.entry("humanDecision", Map.of("reviewState", "empty/no-result", "allowedDispositionValues", List.of(), "reasonRequiredFor", List.of("reject", "request_changes"))),
        Map.entry("activationGate", Map.of("currentGateState", "blocked-no-selected-impact-result", "noInlineActivation", true)),
        Map.entry("authorizedActions", List.of(Map.of("actionId", "action-governance-policy-read-impact-analysis", "label", "Read selected impact result", "capabilityId", READ_CAPABILITY, "browserToolId", "browser-tool:action-governance-policy-read-impact-analysis", "governedToolId", "governed-tool:" + READ_CAPABILITY, "resultSurfaceId", "surface-governance-policy-impact-analysis-result", "reason", "Requires a selected impactTaskId from a visible task."))),
        Map.entry("allowedActions", List.of(Map.of("actionId", "action-governance-policy-read-impact-analysis", "label", "Read selected impact result", "capabilityId", READ_CAPABILITY, "browserToolId", "browser-tool:action-governance-policy-read-impact-analysis", "governedToolId", "governed-tool:" + READ_CAPABILITY, "resultSurfaceId", "surface-governance-policy-impact-analysis-result", "reason", "Requires a selected impactTaskId from a visible task."))),
        Map.entry("disabledActions", List.of(Map.of("actionId", "action-governance-policy-accept-impact-result", "label", "Accept advisory impact result", "reason", "No completed impact-analysis result is selected.", "recovery", "Open a visible completed impact-analysis task first."), Map.of("actionId", "action-governance-policy-reject-impact-result", "label", "Reject advisory impact result", "reason", "No completed impact-analysis result is selected.", "recovery", "Open a visible completed impact-analysis task first."), Map.of("actionId", "action-governance-policy-request-impact-changes", "label", "Request impact-analysis changes", "reason", "No completed impact-analysis result is selected.", "recovery", "Open a visible completed impact-analysis task first."))),
        Map.entry("traceIds", traceIds),
        Map.entry("traceLinks", traceIds),
        Map.entry("readiness", Map.of("state", "not-required", "providerRuntime", "no selected task", "fakeSuccess", false)),
        Map.entry("noDirectMutation", true),
        Map.entry("noFakeSuccess", true),
        Map.entry("activationBlockedUntilHumanDecision", true),
        Map.entry("redaction", "browser-safe no-result recovery; hidden tasks, raw provider/model/tool data, JWTs, secrets, and cross-tenant/customer evidence omitted")));
  }

  private List<String> resultTraceIds(GovernancePolicyImpactTask task, String correlationId) {
    var traces = new java.util.ArrayList<String>();
    traces.addAll(task.traceIds());
    traces.add(trace("impact-result", correlationId));
    return List.copyOf(traces.stream().filter(value -> value != null && !value.isBlank()).distinct().toList());
  }

  private List<Map<String, Object>> evidenceSummary(GovernancePolicyImpactTask task, List<String> traceIds) {
    var evidence = new java.util.ArrayList<Map<String, Object>>();
    for (var ref : task.evidenceRefs()) {
      evidence.add(Map.<String, Object>of("evidenceId", ref, "label", safe(ref, "Evidence"), "summary", "Backend-authorized advisory evidence reference; raw provider/model/tool payload omitted.", "status", "available-redacted", "redactionNote", "browser-safe summary only", "noFakeSuccess", true, "traceRefs", traceIds));
    }
    if (evidence.isEmpty()) evidence.add(Map.<String, Object>of("evidenceId", "no-visible-evidence", "label", "No visible evidence", "summary", "No result evidence is visible for this selected context.", "status", "empty", "redactionNote", "hidden or unavailable evidence is not enumerated", "noFakeSuccess", true, "traceRefs", traceIds));
    return List.copyOf(evidence);
  }

  private List<Map<String, Object>> findingSummaries(GovernancePolicyImpactTask task) {
    return task.findingRefs().stream()
        .map(ref -> Map.<String, Object>of("findingId", ref, "label", safe(ref, "Impact finding"), "severity", ref.toLowerCase(java.util.Locale.ROOT).contains("critical") ? "critical" : "high", "summary", "Review this advisory finding before a separate policy approval or activation path.", "redaction", "raw model/provider/tool evidence omitted"))
        .toList();
  }

  private List<Map<String, Object>> allowedResultActions(AuthContextResolver.ResolvedMe actor, GovernancePolicyImpactTask task) {
    var capabilities = actor.selectedContext().capabilities();
    var actions = new java.util.ArrayList<Map<String, Object>>();
    if (capabilities.contains(READ_CAPABILITY)) actions.add(resultAction("action-governance-policy-read-impact-analysis", "Refresh impact result", READ_CAPABILITY, "surface-governance-policy-impact-analysis-result", "Read-only refresh reauthorizes selected AuthContext and task visibility.", "not-required"));
    if (task.status() == GovernancePolicyImpactTask.Status.COMPLETED_REVIEW_REQUIRED && capabilities.contains(ACCEPT_RESULT_CAPABILITY)) actions.add(resultAction("action-governance-policy-accept-impact-result", "Accept advisory impact result", ACCEPT_RESULT_CAPABILITY, "surface-governance-policy-impact-analysis-result", "Records advisory evidence disposition only; no activation.", "surface-item"));
    if (task.status() == GovernancePolicyImpactTask.Status.COMPLETED_REVIEW_REQUIRED && capabilities.contains(REJECT_RESULT_CAPABILITY)) actions.add(resultAction("action-governance-policy-reject-impact-result", "Reject advisory impact result", REJECT_RESULT_CAPABILITY, "surface-governance-policy-impact-analysis-result", "Requires reviewer reason; no policy mutation.", "surface-item"));
    if (task.status() == GovernancePolicyImpactTask.Status.COMPLETED_REVIEW_REQUIRED && capabilities.contains(REQUEST_CHANGES_CAPABILITY)) actions.add(resultAction("action-governance-policy-request-impact-changes", "Request impact-analysis changes", REQUEST_CHANGES_CAPABILITY, "surface-governance-policy-impact-analysis-result", "Requires reviewer reason; replacement evidence remains separate.", "surface-item"));
    return List.copyOf(actions);
  }

  private static Map<String, Object> resultAction(String actionId, String label, String capabilityId, String resultSurfaceId, String reason, String idempotency) {
    return Map.<String, Object>of("actionId", actionId, "label", label, "browserToolId", "browser-tool:" + actionId, "governedToolId", "governed-tool:" + capabilityId, "capabilityId", capabilityId, "resultSurfaceId", resultSurfaceId, "reason", reason, "idempotency", idempotency);
  }

  private static List<Map<String, Object>> disabledResultActions(GovernancePolicyImpactTask task) {
    var disabled = new java.util.ArrayList<Map<String, Object>>();
    disabled.add(Map.<String, Object>of("actionId", "action-governance-policy-activate", "label", "Activate policy", "reason", "governance.policy.activate is not executed by this worker result view; impact-result disposition never activates policy inline.", "recovery", "Use the separate Governance/Policy decision and activation tasks after prerequisites pass."));
    disabled.add(Map.<String, Object>of("actionId", "action-governance-policy-rollback", "label", "Roll back policy", "reason", "Rollback is a separate governed policy command and cannot run from impact-result review.", "recovery", "Open the dedicated policy decision review when authorized."));
    if (task.status() != GovernancePolicyImpactTask.Status.COMPLETED_REVIEW_REQUIRED) {
      disabled.add(Map.<String, Object>of("actionId", "action-governance-policy-accept-impact-result", "label", "Accept advisory impact result", "reason", "Result disposition is already recorded or no longer reviewable.", "recovery", "Review the prior disposition and start replacement analysis if needed."));
      disabled.add(Map.<String, Object>of("actionId", "action-governance-policy-reject-impact-result", "label", "Reject advisory impact result", "reason", "Result disposition is already recorded or no longer reviewable.", "recovery", "Review the prior disposition and start replacement analysis if needed."));
      disabled.add(Map.<String, Object>of("actionId", "action-governance-policy-request-impact-changes", "label", "Request impact-analysis changes", "reason", "Result disposition is already recorded or no longer reviewable.", "recovery", "Review the prior disposition and start replacement analysis if needed."));
    }
    return List.copyOf(disabled);
  }

  private static String surfaceStatus(GovernancePolicyImpactTask.Status status) { return status.name().toLowerCase(java.util.Locale.ROOT).replace('_', '-'); }

  private GovernancePolicyImpactTask projectAutonomousAgentTask(GovernancePolicyImpactTask task, String correlationId) {
    if (task.terminal() || task.status() == GovernancePolicyImpactTask.Status.COMPLETED_REVIEW_REQUIRED || task.status() == GovernancePolicyImpactTask.Status.BLOCKED_PROVIDER_OR_RUNTIME || task.status() == GovernancePolicyImpactTask.Status.FAILED) return task;
    var projection = autonomousAgentRuntime.project(task, correlationId);
    if (!projection.changed()) return task;
    var updated = task.withWorkerUpdate(projection.status(), projection.progressPercent(), projection.summary(), projection.blockerCode(), projection.evidenceRefs().isEmpty() ? task.evidenceRefs() : projection.evidenceRefs(), projection.findingRefs().isEmpty() ? task.findingRefs() : projection.findingRefs(), projection.traceIds().isEmpty() ? task.traceIds() : projection.traceIds(), Instant.now(clock));
    repository.save(updated);
    var transition = switch (updated.status()) {
      case QUEUED -> "queued";
      case RUNNING -> "running";
      case COMPLETED_REVIEW_REQUIRED -> "completed_review_required";
      case CANCELLED -> "cancelled";
      case BLOCKED_PROVIDER_OR_RUNTIME -> "blocked_provider_or_runtime";
      case FAILED -> "failed";
      case ACCEPTED -> "result_accepted";
      case REJECTED_RESULT -> "result_rejected";
      case REQUEST_CHANGES -> "request_changes";
    };
    publishLifecycleOrAttention(updated, transition, READ_CAPABILITY, null, correlationId);
    return updated;
  }

  private static List<Map<String, String>> taskSteps(GovernancePolicyImpactTask task) {
    var status = task.status().name().toLowerCase(java.util.Locale.ROOT);
    var steps = new java.util.ArrayList<Map<String, String>>();
    steps.add(Map.of("stepId", "requested", "label", "Requested", "status", "completed"));
    steps.add(Map.of("stepId", "provider-runtime-check", "label", "Provider/runtime check", "status", task.status() == GovernancePolicyImpactTask.Status.BLOCKED_PROVIDER_OR_RUNTIME ? "error" : "completed"));
    steps.add(Map.of("stepId", "analysis", "label", "Autonomous analysis", "status", task.status() == GovernancePolicyImpactTask.Status.QUEUED ? "queued" : task.status() == GovernancePolicyImpactTask.Status.RUNNING ? "running" : task.status() == GovernancePolicyImpactTask.Status.COMPLETED_REVIEW_REQUIRED ? "completed" : status));
    steps.add(Map.of("stepId", "human-review", "label", "Human impact result review", "status", task.resultDecisionAllowed() ? "waiting-for-human" : status));
    return List.copyOf(steps);
  }

  private static List<Map<String, String>> taskBlockers(GovernancePolicyImpactTask task) {
    if (blank(task.blockerCode())) return List.of();
    return List.of(Map.of("code", task.blockerCode(), "message", safe(task.summary(), "Governance/Policy impact analysis is blocked; review provider/runtime readiness and trace evidence.")));
  }

  private static List<String> providerFailures(GovernancePolicyImpactTask task) {
    if (task.status() != GovernancePolicyImpactTask.Status.BLOCKED_PROVIDER_OR_RUNTIME) return List.of();
    return List.of(firstNonBlank(task.blockerCode(), "blocked_provider_or_runtime"));
  }

  private static List<Map<String, String>> traceLinks(List<String> traceIds, String correlationId) {
    return traceIds.stream()
        .map(traceId -> Map.of("traceId", traceId, "label", "Impact-analysis trace", "summary", "Browser-safe Governance/Policy impact-analysis lifecycle evidence", "targetSurfaceId", "surface-audit-trace-detail", "correlationId", safe(correlationId, ""), "redaction", "raw provider/model/tool data omitted"))
        .toList();
  }

  private static List<String> disabledActions(GovernancePolicyImpactTask task) {
    if (task.status() == GovernancePolicyImpactTask.Status.CANCELLED) return List.of("cancel disabled: already cancelled");
    if (task.status() == GovernancePolicyImpactTask.Status.ACCEPTED || task.status() == GovernancePolicyImpactTask.Status.REJECTED_RESULT || task.status() == GovernancePolicyImpactTask.Status.REQUEST_CHANGES) return List.of("result disposition already recorded on the result surface");
    if (task.status() == GovernancePolicyImpactTask.Status.BLOCKED_PROVIDER_OR_RUNTIME) return List.of("successful impact analysis disabled until provider/runtime is configured");
    return List.of("approval, activation, rollback, and impact-result disposition are not performed by this task");
  }

  private static String readinessState(GovernancePolicyImpactTask task) {
    return switch (task.status()) {
      case QUEUED -> "queued";
      case RUNNING -> "running";
      case BLOCKED_PROVIDER_OR_RUNTIME -> "blocked";
      case FAILED -> "failed";
      case COMPLETED_REVIEW_REQUIRED -> "ready_for_human_review";
      default -> task.status().name().toLowerCase(java.util.Locale.ROOT);
    };
  }

  private void publishLifecycleOrAttention(GovernancePolicyImpactTask task, String semanticTransition, String capabilityId, String actorAccountId, String correlationId) {
    if (workstreamEventPublisher != null) {
      workstreamEventPublisher.publishGovernancePolicyImpactLifecycle(task, semanticTransition, capabilityId, actorAccountId, correlationId);
    } else if (attentionProducerService != null) {
      if (task.status() == GovernancePolicyImpactTask.Status.CANCELLED || task.status() == GovernancePolicyImpactTask.Status.ACCEPTED) attentionProducerService.resolveWorkerTaskState(task, semanticTransition, correlationId);
      else attentionProducerService.upsertWorkerTaskState(task, null, correlationId);
    }
  }

  private GovernancePolicyImpactTask task(AuthContextResolver.ResolvedMe actor, String impactTaskId) {
    var task = repository.find(impactTaskId).orElseThrow(() -> new AuthorizationException(404, "governance-impact-task-not-found-or-forbidden"));
    if (!Objects.equals(actor.selectedContext().tenantId(), task.tenantId())) throw new AuthorizationException(404, "governance-impact-task-not-found-or-forbidden");
    if (actor.selectedContext().customerId() != null && !Objects.equals(actor.selectedContext().customerId(), task.customerId())) throw new AuthorizationException(404, "governance-impact-task-not-found-or-forbidden");
    return task;
  }

  private void require(AuthContextResolver.ResolvedMe actor, String capabilityId, String correlationId) {
    authContextResolver.requireTenant(actor.selectedContext(), actor.selectedContext().tenantId());
    authContextResolver.requireCapability(actor.selectedContext(), capabilityId);
    authContextResolver.appendProtectedReadTrace(actor, capabilityId, "governance.policy.impact_analysis_task.v1", correlationId);
  }

  private static List<String> evidenceRefs(StartGovernancePolicyImpactCommand command, GovernancePolicyProposal proposal) {
    var refs = new java.util.ArrayList<String>();
    refs.add("governancePolicyEvidence.read");
    refs.add("readSkill:governance-policy-impact-analysis");
    refs.add("readReferenceDoc:governance-policy-impact-analysis");
    refs.add("proposal:" + proposal.proposalId());
    refs.add("targetPolicy:" + proposal.targetPolicyId());
    refs.addAll(proposal.affectedCapabilityIds());
    refs.addAll(command.evidenceRefs());
    return List.copyOf(refs);
  }

  private static List<String> merge(List<String> preferred, List<String> fallback) {
    var values = new java.util.ArrayList<String>();
    if (fallback != null) values.addAll(fallback);
    if (preferred != null) preferred.stream().filter(value -> !values.contains(value)).forEach(values::add);
    return List.copyOf(values);
  }

  private static boolean blank(String value) { return value == null || value.isBlank(); }
  private static String firstNonBlank(String... values) { for (var value : values) if (value != null && !value.isBlank()) return value; return null; }
  private static String safe(String value, String fallback) { return value == null || value.isBlank() ? fallback : value.replaceAll("(?i)(api[_-]?key|secret|token|jwt)=[^\\s,;]+", "$1=[REDACTED]"); }
  private static String stableSuffix(String value) { return Integer.toUnsignedString(Objects.requireNonNullElse(value, "governance-policy-impact").hashCode(), 36); }
  private static String trace(String label, String correlationId) { return "trace-governance-policy-" + label + "-" + stableSuffix(correlationId); }

  public record StartGovernancePolicyImpactCommand(String proposalId, String targetPolicyId, String evidenceRequest, List<String> affectedCapabilityIds, List<String> affectedArtifactRefs, List<String> evidenceRefs, String idempotencyKey) {
    public StartGovernancePolicyImpactCommand {
      affectedCapabilityIds = List.copyOf(affectedCapabilityIds == null ? List.of() : affectedCapabilityIds);
      affectedArtifactRefs = List.copyOf(affectedArtifactRefs == null ? List.of() : affectedArtifactRefs);
      evidenceRefs = List.copyOf(evidenceRefs == null ? List.of() : evidenceRefs);
    }
  }

  public record SurfaceData(String surfaceId, String surfaceType, String title, List<String> traceIds, Map<String, Object> data) {}
}
