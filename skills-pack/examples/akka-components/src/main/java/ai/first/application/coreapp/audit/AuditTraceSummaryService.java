package ai.first.application.coreapp.audit;

import ai.first.application.coreapp.audit.AuditTraceSummaryAutonomousAgentRuntime;
import ai.first.application.coreapp.audit.FailClosedAuditTraceSummaryAutonomousAgentRuntime;
import ai.first.domain.coreapp.audit.AuditTraceSummaryTask;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import ai.first.application.foundation.attention.AttentionProducerService;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.AuthorizationException;
import ai.first.application.foundation.workstream.WorkstreamEventPublisher;

/** Governed lifecycle owner for Audit/Trace summary AutonomousAgent tasks and redacted evidence scope. */
public final class AuditTraceSummaryService {
  public static final String START_CAPABILITY = "audit.trace.summary_task.start";
  public static final String READ_CAPABILITY = "audit.trace.summary_task.read";
  public static final String CANCEL_CAPABILITY = "audit.trace.summary_task.cancel";
  public static final String ACCEPT_RESULT_CAPABILITY = "audit.trace.summary_task.accept_result";
  public static final String REJECT_RESULT_CAPABILITY = "audit.trace.summary_task.reject_result";
  public static final String OPEN_EVIDENCE_CAPABILITY = "audit.trace.summary_task.open_evidence";
  public static final List<String> DEFAULT_EVIDENCE_CATEGORIES = List.of("admin_audit", "authorization_denial", "provider_readiness", "agent_work", "tool_invocation", "prompt_skill_reference", "attention", "workstream_event");
  private static final Duration MAX_LOOKBACK = Duration.ofDays(30);

  private final AuditTraceSummaryTaskRepository repository;
  private final AuthContextResolver authContextResolver;
  private final Clock clock;
  private final AttentionProducerService attentionProducerService;
  private final WorkstreamEventPublisher workstreamEventPublisher;
  private final AuditTraceSummaryAutonomousAgentRuntime autonomousAgentRuntime;

  public AuditTraceSummaryService(AuditTraceSummaryTaskRepository repository, AuthContextResolver authContextResolver, Clock clock) {
    this(repository, authContextResolver, clock, null, null, new FailClosedAuditTraceSummaryAutonomousAgentRuntime());
  }

  public AuditTraceSummaryService(AuditTraceSummaryTaskRepository repository, AuthContextResolver authContextResolver, Clock clock, AuditTraceSummaryAutonomousAgentRuntime autonomousAgentRuntime) {
    this(repository, authContextResolver, clock, null, null, autonomousAgentRuntime);
  }

  public AuditTraceSummaryService(AuditTraceSummaryTaskRepository repository, AuthContextResolver authContextResolver, Clock clock, AttentionProducerService attentionProducerService, WorkstreamEventPublisher workstreamEventPublisher, AuditTraceSummaryAutonomousAgentRuntime autonomousAgentRuntime) {
    this.repository = Objects.requireNonNull(repository);
    this.authContextResolver = Objects.requireNonNull(authContextResolver);
    this.clock = Objects.requireNonNull(clock);
    this.attentionProducerService = attentionProducerService;
    this.workstreamEventPublisher = workstreamEventPublisher;
    this.autonomousAgentRuntime = Objects.requireNonNull(autonomousAgentRuntime);
  }

  public AuditTraceSummaryTask start(AuthContextResolver.ResolvedMe actor, StartAuditTraceSummaryCommand command, String correlationId) {
    if (command == null) throw new AuthorizationException(400, "audit-summary-command-required");
    if (blank(command.idempotencyKey())) throw new AuthorizationException(400, "idempotency-key-required");
    require(actor, START_CAPABILITY, correlationId);
    var duplicate = repository.findByIdempotencyKey(actor.selectedContext().tenantId(), actor.account().accountId(), command.idempotencyKey());
    if (duplicate.isPresent()) {
      authContextResolver.appendProtectedReadTrace(actor, START_CAPABILITY, "idempotent-audit-trace-summary-replay", correlationId);
      return duplicate.orElseThrow();
    }
    var now = Instant.now(clock);
    var windowEnd = command.windowEnd() == null ? now : command.windowEnd();
    var windowStart = command.windowStart() == null ? windowEnd.minus(Duration.ofDays(7)) : command.windowStart();
    if (!windowStart.isBefore(windowEnd)) throw new AuthorizationException(400, "audit-summary-window-invalid");
    if (windowStart.isBefore(windowEnd.minus(MAX_LOOKBACK))) throw new AuthorizationException(400, "audit-summary-window-too-large");
    var categories = normalizedCategories(command.evidenceCategories());
    var taskId = "audit-summary-" + stableSuffix(actor.selectedContext().tenantId() + ":" + actor.account().accountId() + ":" + command.idempotencyKey());
    var traceId = "trace-audit-trace-summary-start-" + stableSuffix(correlationId + ":" + taskId);
    var task = new AuditTraceSummaryTask(
        taskId,
        null,
        actor.selectedContext().tenantId(),
        actor.selectedContext().customerId(),
        actor.selectedContext().membershipId(),
        actor.account().accountId(),
        actor.selectedContext().membershipId(),
        command.idempotencyKey(),
        windowStart,
        windowEnd,
        categories,
        AuditTraceSummaryTask.Status.QUEUED,
        0,
        "Audit/Trace summary task record created; governed Akka AutonomousAgent task start is being attempted through the backend runtime path.",
        null,
        null,
        null,
        evidenceRefs(categories),
        List.of(),
        List.of(traceId),
        now,
        now);
    repository.save(task);
    var start = autonomousAgentRuntime.start(actor, task, correlationId);
    var startTraceIds = new java.util.ArrayList<>(task.traceIds());
    startTraceIds.addAll(start.traceIds());
    var started = task.withAutonomousAgentTaskId(start.autonomousAgentTaskId(), start.status(), start.progressPercent(), start.summary(), start.blockerCode(), startTraceIds, Instant.now(clock));
    repository.save(started);
    authContextResolver.appendProtectedReadTrace(actor, START_CAPABILITY, start.status() == AuditTraceSummaryTask.Status.BLOCKED_PROVIDER_OR_RUNTIME ? "provider-blocked-fail-closed:no fake success" : "autonomous-agent-task-started", correlationId);
    publishLifecycleOrAttention(started, start.status() == AuditTraceSummaryTask.Status.BLOCKED_PROVIDER_OR_RUNTIME ? "blocked_provider_or_runtime" : "started", START_CAPABILITY, actor.account().accountId(), correlationId);
    return started;
  }

  public AuditTraceSummaryTask read(AuthContextResolver.ResolvedMe actor, String taskId, String correlationId) {
    var task = task(actor, taskId);
    require(actor, READ_CAPABILITY, correlationId);
    var projected = projectAutonomousAgentTask(task, correlationId);
    authContextResolver.appendProtectedReadTrace(actor, READ_CAPABILITY, "browser-safe-audit-trace-summary-read:redacted", correlationId);
    return projected;
  }

  public AuditTraceSummaryTask cancel(AuthContextResolver.ResolvedMe actor, String taskId, String reason, String correlationId) {
    var task = task(actor, taskId);
    require(actor, CANCEL_CAPABILITY, correlationId);
    if (task.terminal()) return task;
    autonomousAgentRuntime.cancel(task, reason, correlationId);
    var traceId = "trace-audit-trace-summary-cancel-" + stableSuffix(correlationId + ":" + task.taskId());
    var cancelled = task.withStatus(AuditTraceSummaryTask.Status.CANCELLED, task.progressPercent(), firstNonBlank(reason, "Audit/Trace summary cancelled by authorized reviewer; traces and protected state unchanged."), null, List.of(traceId), Instant.now(clock));
    repository.save(cancelled);
    authContextResolver.appendProtectedReadTrace(actor, CANCEL_CAPABILITY, "cancelled:no direct mutation", correlationId);
    publishLifecycleOrAttention(cancelled, "cancelled", CANCEL_CAPABILITY, actor.account().accountId(), correlationId);
    return cancelled;
  }

  public AuditTraceSummaryTask acceptResult(AuthContextResolver.ResolvedMe actor, String taskId, String reason, String correlationId) {
    return decide(actor, taskId, AuditTraceSummaryTask.Status.ACCEPTED, "accepted", reason, correlationId);
  }

  public AuditTraceSummaryTask rejectResult(AuthContextResolver.ResolvedMe actor, String taskId, String reason, String correlationId) {
    if (blank(reason)) throw new AuthorizationException(400, "audit-summary-rejection-reason-required");
    return decide(actor, taskId, AuditTraceSummaryTask.Status.REJECTED, "rejected", reason, correlationId);
  }

  private AuditTraceSummaryTask decide(AuthContextResolver.ResolvedMe actor, String taskId, AuditTraceSummaryTask.Status status, String decision, String reason, String correlationId) {
    var task = task(actor, taskId);
    require(actor, status == AuditTraceSummaryTask.Status.ACCEPTED ? ACCEPT_RESULT_CAPABILITY : REJECT_RESULT_CAPABILITY, correlationId);
    if (!task.resultDecisionAllowed()) throw new AuthorizationException(409, "audit-summary-result-not-completed");
    var traceId = "trace-audit-trace-summary-" + decision + "-" + stableSuffix(correlationId + ":" + task.taskId());
    var decided = task.withDecision(status, decision, firstNonBlank(reason, "Human Audit/Trace summary decision recorded; traces, policies, users, authorization, and provider config unchanged."), List.of(traceId), Instant.now(clock));
    repository.save(decided);
    authContextResolver.appendProtectedReadTrace(actor, status == AuditTraceSummaryTask.Status.ACCEPTED ? ACCEPT_RESULT_CAPABILITY : REJECT_RESULT_CAPABILITY, decision + ":advisory-only:no direct mutation", correlationId);
    publishLifecycleOrAttention(decided, status == AuditTraceSummaryTask.Status.ACCEPTED ? "result_accepted" : "result_rejected", status == AuditTraceSummaryTask.Status.ACCEPTED ? ACCEPT_RESULT_CAPABILITY : REJECT_RESULT_CAPABILITY, actor.account().accountId(), correlationId);
    return decided;
  }

  private AuditTraceSummaryTask projectAutonomousAgentTask(AuditTraceSummaryTask task, String correlationId) {
    if (task.terminal() || task.status() == AuditTraceSummaryTask.Status.COMPLETED_REVIEW_REQUIRED || task.status() == AuditTraceSummaryTask.Status.BLOCKED_PROVIDER_OR_RUNTIME || task.status() == AuditTraceSummaryTask.Status.FAILED) return task;
    var projection = autonomousAgentRuntime.project(task, correlationId);
    if (!projection.changed()) return task;
    var updated = task.withWorkerUpdate(projection.status(), projection.progressPercent(), projection.summary(), projection.blockerCode(), projection.evidenceRefs().isEmpty() ? task.evidenceRefs() : projection.evidenceRefs(), projection.findingRefs().isEmpty() ? task.findingRefs() : projection.findingRefs(), projection.traceIds().isEmpty() ? task.traceIds() : projection.traceIds(), Instant.now(clock));
    repository.save(updated);
    var transition = switch (updated.status()) {
      case QUEUED -> "queued";
      case RUNNING -> "running";
      case COMPLETED_REVIEW_REQUIRED -> "completed_review_required";
      case FAILED -> "failed";
      case CANCELLED -> "cancelled";
      case BLOCKED_PROVIDER_OR_RUNTIME -> "blocked_provider_or_runtime";
      case ACCEPTED -> "result_accepted";
      case REJECTED -> "result_rejected";
    };
    publishLifecycleOrAttention(updated, transition, READ_CAPABILITY, null, correlationId);
    return updated;
  }

  private void publishLifecycleOrAttention(AuditTraceSummaryTask task, String semanticTransition, String capabilityId, String actorAccountId, String correlationId) {
    if (workstreamEventPublisher != null) {
      workstreamEventPublisher.publishAuditTraceSummaryLifecycle(task, semanticTransition, capabilityId, actorAccountId, correlationId);
    } else if (attentionProducerService != null) {
      if (task.status() == AuditTraceSummaryTask.Status.CANCELLED || task.status() == AuditTraceSummaryTask.Status.ACCEPTED) attentionProducerService.resolveWorkerTaskState(task, semanticTransition, correlationId);
      else attentionProducerService.upsertWorkerTaskState(task, null, correlationId);
    }
  }

  private AuditTraceSummaryTask task(AuthContextResolver.ResolvedMe actor, String taskId) {
    var task = repository.find(taskId).orElseThrow(() -> new AuthorizationException(404, "audit-summary-task-not-found-or-forbidden"));
    if (!Objects.equals(actor.selectedContext().tenantId(), task.tenantId())) throw new AuthorizationException(404, "audit-summary-task-not-found-or-forbidden");
    if (actor.selectedContext().customerId() != null && !Objects.equals(actor.selectedContext().customerId(), task.customerId())) throw new AuthorizationException(404, "audit-summary-task-not-found-or-forbidden");
    return task;
  }

  private void require(AuthContextResolver.ResolvedMe actor, String capabilityId, String correlationId) {
    authContextResolver.requireTenant(actor.selectedContext(), actor.selectedContext().tenantId());
    authContextResolver.requireCapability(actor.selectedContext(), capabilityId);
    authContextResolver.appendProtectedReadTrace(actor, capabilityId, "audit.trace.summaryProgress.v1", correlationId);
  }

  private static List<String> normalizedCategories(List<String> requested) {
    if (requested == null || requested.isEmpty()) return DEFAULT_EVIDENCE_CATEGORIES;
    var copy = requested.stream().filter(DEFAULT_EVIDENCE_CATEGORIES::contains).distinct().toList();
    if (copy.isEmpty() || copy.size() != requested.size()) throw new AuthorizationException(400, "audit-summary-evidence-category-not-allowed");
    return copy;
  }

  private static List<String> evidenceRefs(List<String> categories) {
    var refs = new java.util.ArrayList<String>();
    refs.add("auditTraceSummaryEvidence.read");
    refs.add("auditTraceEvidence.read");
    refs.add("readSkill:audit-trace-summary-review");
    refs.add("readReferenceDoc:audit-trace-summary-review");
    refs.addAll(categories.stream().map(category -> "evidenceCategory:" + category).toList());
    return List.copyOf(refs);
  }

  private static boolean blank(String value) {
    return value == null || value.isBlank();
  }

  private static String firstNonBlank(String... values) {
    for (var value : values) if (value != null && !value.isBlank()) return value;
    return null;
  }

  private static String stableSuffix(String value) {
    return Integer.toUnsignedString(Objects.requireNonNullElse(value, "audit-trace-summary").hashCode(), 36);
  }

  public record StartAuditTraceSummaryCommand(Instant windowStart, Instant windowEnd, List<String> evidenceCategories, String idempotencyKey) {
    public StartAuditTraceSummaryCommand {
      evidenceCategories = List.copyOf(evidenceCategories == null ? List.of() : evidenceCategories);
    }
  }
}
