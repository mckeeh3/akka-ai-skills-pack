package {{JAVA_BASE_PACKAGE}}.application.security;

import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.FailClosedGovernancePolicyImpactAutonomousAgentRuntime;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.GovernancePolicyImpactAutonomousAgentRuntime;
import {{JAVA_BASE_PACKAGE}}.domain.security.GovernancePolicyImpactTask;
import {{JAVA_BASE_PACKAGE}}.domain.security.GovernancePolicyProposal;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    return new SurfaceData("surface-governance-policy-impact-analysis-task", "worker-task", "Governance/Policy impact analysis task", List.of(trace("impact-task", correlationId)), Map.ofEntries(
        Map.entry("surfaceContract", "governance.policy.impact_analysis.task.v1"),
        Map.entry("impactTaskId", task.impactTaskId()),
        Map.entry("autonomousAgentTaskId", safe(task.autonomousAgentTaskId(), "")),
        Map.entry("proposalId", task.proposalId()),
        Map.entry("status", task.status().name().toLowerCase(java.util.Locale.ROOT)),
        Map.entry("progressPercent", task.progressPercent()),
        Map.entry("blockerCode", safe(task.blockerCode(), "")),
        Map.entry("summary", safe(task.summary(), "")),
        Map.entry("evidenceRefs", task.evidenceRefs()),
        Map.entry("traceIds", task.traceIds()),
        Map.entry("authorizedActions", List.of(READ_CAPABILITY, CANCEL_CAPABILITY, ACCEPT_RESULT_CAPABILITY, REJECT_RESULT_CAPABILITY, REQUEST_CHANGES_CAPABILITY)),
        Map.entry("noDirectMutation", true),
        Map.entry("activationBlockedUntilHumanDecision", true),
        Map.entry("redaction", "browser-safe; raw prompts, provider credentials, JWTs, raw tool payloads, and cross-tenant/customer data omitted")));
  }

  private GovernancePolicyImpactTask decide(AuthContextResolver.ResolvedMe actor, String impactTaskId, GovernancePolicyImpactTask.Status status, String decision, String capabilityId, String reason, String correlationId) {
    var task = task(actor, impactTaskId);
    require(actor, capabilityId, correlationId);
    if (!task.resultDecisionAllowed()) throw new AuthorizationException(409, "governance-impact-result-not-completed");
    var traceId = "trace-governance-policy-impact-" + decision + "-" + stableSuffix(correlationId + ":" + task.impactTaskId());
    var decided = task.withDecision(status, decision, firstNonBlank(reason, "Human Governance/Policy impact result disposition recorded; policy proposal unchanged."), List.of(traceId), Instant.now(clock));
    repository.save(decided);
    authContextResolver.appendProtectedReadTrace(actor, capabilityId, decision + ":advisory-only:no direct approval activation rollback or policy mutation", correlationId);
    publishLifecycleOrAttention(decided, decision.equals("accepted") ? "result_accepted" : decision.equals("rejected_result") ? "result_rejected" : "request_changes", capabilityId, actor.account().accountId(), correlationId);
    return decided;
  }

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
