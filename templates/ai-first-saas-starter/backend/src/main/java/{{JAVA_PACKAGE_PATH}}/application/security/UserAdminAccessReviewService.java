package {{JAVA_BASE_PACKAGE}}.application.security;

import {{JAVA_BASE_PACKAGE}}.domain.security.AccessReviewTask;
import {{JAVA_BASE_PACKAGE}}.domain.security.AdminAuditEvent;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/** Deterministic lifecycle owner for SMB User Admin access-review tasks. */
public final class UserAdminAccessReviewService {
  public static final String START_CAPABILITY = "user_admin.access_review.start";
  public static final String READ_CAPABILITY = "user_admin.access_review.read";
  public static final String CANCEL_CAPABILITY = "user_admin.access_review.cancel";
  public static final String ACCEPT_RESULT_CAPABILITY = "user_admin.access_review.accept_result";
  public static final String REJECT_RESULT_CAPABILITY = "user_admin.access_review.reject_result";
  private final AccessReviewTaskRepository repository;
  private final UserAdminService userAdminService;
  private final Clock clock;
  private final AttentionProducerService attentionProducerService;
  private final WorkstreamEventPublisher workstreamEventPublisher;

  public UserAdminAccessReviewService(AccessReviewTaskRepository repository, UserAdminService userAdminService, Clock clock) {
    this(repository, userAdminService, clock, null);
  }

  public UserAdminAccessReviewService(AccessReviewTaskRepository repository, UserAdminService userAdminService, Clock clock, AttentionProducerService attentionProducerService) {
    this(repository, userAdminService, clock, attentionProducerService, null);
  }

  public UserAdminAccessReviewService(AccessReviewTaskRepository repository, UserAdminService userAdminService, Clock clock, AttentionProducerService attentionProducerService, WorkstreamEventPublisher workstreamEventPublisher) {
    this.repository = Objects.requireNonNull(repository);
    this.userAdminService = Objects.requireNonNull(userAdminService);
    this.clock = Objects.requireNonNull(clock);
    this.attentionProducerService = attentionProducerService;
    this.workstreamEventPublisher = workstreamEventPublisher;
  }

  public AccessReviewTask start(AuthContextResolver.ResolvedMe actor, String idempotencyKey, String correlationId) {
    if (idempotencyKey == null || idempotencyKey.isBlank()) throw new AuthorizationException(400, "idempotency-key-required");
    userAdminService.requireAccessReviewManage(actor, actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId());
    var duplicate = repository.findByIdempotencyKey(actor.selectedContext().tenantId(), actor.account().accountId(), idempotencyKey);
    if (duplicate.isPresent()) {
      userAdminService.auditAccessReview(actor, duplicate.orElseThrow(), START_CAPABILITY, AdminAuditEvent.Result.NO_OP, "idempotent-replay", correlationId);
      return duplicate.orElseThrow();
    }
    var now = Instant.now(clock);
    var taskId = "access-review-" + stableSuffix(actor.selectedContext().tenantId() + ":" + actor.account().accountId() + ":" + idempotencyKey);
    var traceId = "trace-useradmin-access-review-start-" + stableSuffix(correlationId + ":" + taskId);
    var task = new AccessReviewTask(
        taskId,
        actor.selectedContext().tenantId(),
        actor.selectedContext().customerId(),
        actor.selectedContext().scopeType(),
        actor.account().accountId(),
        actor.selectedContext().membershipId(),
        idempotencyKey,
        AccessReviewTask.Status.BLOCKED_PROVIDER_OR_RUNTIME,
        0,
        "Access-review task record created, but worker execution is blocked until governed AutonomousAgent provider/runtime configuration is available.",
        "blocked_provider_or_runtime",
        null,
        null,
        List.of("userAdminEvidence.read", "readSkill:user-admin-access-review", "readReferenceDoc:user-admin-access-review"),
        List.of(),
        List.of(traceId),
        now,
        now);
    repository.save(task);
    userAdminService.auditAccessReview(actor, task, START_CAPABILITY, AdminAuditEvent.Result.ALLOWED, "provider-blocked-fail-closed", correlationId);
    publishLifecycleOrAttention(task, "blocked_provider_or_runtime", START_CAPABILITY, actor.account().accountId(), correlationId);
    return task;
  }

  public AccessReviewTask read(AuthContextResolver.ResolvedMe actor, String taskId, String correlationId) {
    var task = task(actor, taskId);
    userAdminService.requireAccessReviewRead(actor, task.scopeType(), task.tenantId(), task.customerId());
    userAdminService.auditAccessReview(actor, task, READ_CAPABILITY, AdminAuditEvent.Result.ALLOWED, "browser-safe-read", correlationId);
    return task;
  }

  public AccessReviewTask cancel(AuthContextResolver.ResolvedMe actor, String taskId, String reason, String correlationId) {
    var task = task(actor, taskId);
    userAdminService.requireAccessReviewManage(actor, task.scopeType(), task.tenantId(), task.customerId());
    if (task.terminal()) {
      userAdminService.auditAccessReview(actor, task, CANCEL_CAPABILITY, AdminAuditEvent.Result.NO_OP, "already-terminal", correlationId);
      return task;
    }
    var traceId = "trace-useradmin-access-review-cancel-" + stableSuffix(correlationId + ":" + task.taskId());
    var cancelled = task.withStatus(AccessReviewTask.Status.CANCELLED, task.progressPercent(), firstNonBlank(reason, "Access-review task cancelled by authorized User Admin."), null, List.of(traceId), Instant.now(clock));
    repository.save(cancelled);
    userAdminService.auditAccessReview(actor, cancelled, CANCEL_CAPABILITY, AdminAuditEvent.Result.ALLOWED, "cancelled", correlationId);
    publishLifecycleOrAttention(cancelled, "cancelled", CANCEL_CAPABILITY, actor.account().accountId(), correlationId);
    return cancelled;
  }

  public AccessReviewTask recordWorkerResult(AuthContextResolver.ResolvedMe actor, String taskId, AccessReviewWorker.WorkerResult result, String correlationId) {
    var task = task(actor, taskId);
    userAdminService.requireAccessReviewManage(actor, task.scopeType(), task.tenantId(), task.customerId());
    if (task.terminal()) {
      userAdminService.auditAccessReview(actor, task, READ_CAPABILITY, AdminAuditEvent.Result.NO_OP, "worker-result-ignored-terminal", correlationId);
      return task;
    }
    var status = result.status() == AccessReviewTask.Status.COMPLETED ? AccessReviewTask.Status.COMPLETED : AccessReviewTask.Status.BLOCKED_PROVIDER_OR_RUNTIME;
    var updated = task.withWorkerUpdate(status, result.progressPercent(), result.summary(), result.blockerCode(), result.evidenceRefs(), result.recommendationRefs(), result.traceIds(), Instant.now(clock));
    repository.save(updated);
    userAdminService.auditAccessReview(actor, updated, READ_CAPABILITY, status == AccessReviewTask.Status.COMPLETED ? AdminAuditEvent.Result.ALLOWED : AdminAuditEvent.Result.DENIED, status == AccessReviewTask.Status.COMPLETED ? "worker-completed:no direct mutation" : "worker-blocked-fail-closed", correlationId);
    publishLifecycleOrAttention(updated, status == AccessReviewTask.Status.COMPLETED ? "completed_review_required" : "blocked_provider_or_runtime", READ_CAPABILITY, actor.account().accountId(), correlationId);
    return updated;
  }

  public AccessReviewTask acceptResult(AuthContextResolver.ResolvedMe actor, String taskId, String reason, String correlationId) {
    return decide(actor, taskId, AccessReviewTask.Status.ACCEPTED, "accepted", reason, correlationId);
  }

  public AccessReviewTask rejectResult(AuthContextResolver.ResolvedMe actor, String taskId, String reason, String correlationId) {
    if (reason == null || reason.isBlank()) throw new AuthorizationException(400, "access-review-rejection-reason-required");
    return decide(actor, taskId, AccessReviewTask.Status.REJECTED, "rejected", reason, correlationId);
  }

  private AccessReviewTask decide(AuthContextResolver.ResolvedMe actor, String taskId, AccessReviewTask.Status status, String decision, String reason, String correlationId) {
    var task = task(actor, taskId);
    userAdminService.requireAccessReviewManage(actor, task.scopeType(), task.tenantId(), task.customerId());
    if (!task.resultDecisionAllowed()) {
      userAdminService.auditAccessReview(actor, task, status == AccessReviewTask.Status.ACCEPTED ? ACCEPT_RESULT_CAPABILITY : REJECT_RESULT_CAPABILITY, AdminAuditEvent.Result.DENIED, "result-not-completed", correlationId);
      throw new AuthorizationException(409, "access-review-result-not-completed");
    }
    var traceId = "trace-useradmin-access-review-" + decision + "-" + stableSuffix(correlationId + ":" + task.taskId());
    var decided = task.withDecision(status, decision, firstNonBlank(reason, "Human result decision recorded; access state unchanged."), List.of(traceId), Instant.now(clock));
    repository.save(decided);
    userAdminService.auditAccessReview(actor, decided, status == AccessReviewTask.Status.ACCEPTED ? ACCEPT_RESULT_CAPABILITY : REJECT_RESULT_CAPABILITY, AdminAuditEvent.Result.ALLOWED, decision + ":no direct mutation", correlationId);
    publishLifecycleOrAttention(decided, status == AccessReviewTask.Status.ACCEPTED ? "result_accepted" : "result_rejected", status == AccessReviewTask.Status.ACCEPTED ? ACCEPT_RESULT_CAPABILITY : REJECT_RESULT_CAPABILITY, actor.account().accountId(), correlationId);
    return decided;
  }

  private void publishLifecycleOrAttention(AccessReviewTask task, String semanticTransition, String capabilityId, String actorAccountId, String correlationId) {
    if (workstreamEventPublisher != null) {
      workstreamEventPublisher.publishAccessReviewLifecycle(task, semanticTransition, capabilityId, actorAccountId, correlationId);
    } else if (attentionProducerService != null) {
      if (task.status() == AccessReviewTask.Status.CANCELLED || task.status() == AccessReviewTask.Status.ACCEPTED) {
        attentionProducerService.resolveWorkerTaskState(task, semanticTransition, correlationId);
      } else {
        attentionProducerService.upsertWorkerTaskState(task, null, correlationId);
      }
    }
  }

  private AccessReviewTask task(AuthContextResolver.ResolvedMe actor, String taskId) {
    var task = repository.find(taskId).orElseThrow(() -> new AuthorizationException(404, "access-review-task-not-found-or-forbidden"));
    if (!Objects.equals(actor.selectedContext().tenantId(), task.tenantId())) throw new AuthorizationException(404, "access-review-task-not-found-or-forbidden");
    if (actor.selectedContext().customerId() != null && !Objects.equals(actor.selectedContext().customerId(), task.customerId())) throw new AuthorizationException(404, "access-review-task-not-found-or-forbidden");
    return task;
  }

  private static String firstNonBlank(String... values) {
    for (var value : values) if (value != null && !value.isBlank()) return value;
    return null;
  }

  private static String stableSuffix(String value) {
    return Integer.toUnsignedString(Objects.requireNonNullElse(value, "access-review").hashCode(), 36);
  }
}
