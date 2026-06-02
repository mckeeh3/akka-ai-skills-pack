package {{JAVA_BASE_PACKAGE}}.application.security;

import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.FailClosedMyAccountPersonalAttentionDigestAutonomousAgentRuntime;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.MyAccountPersonalAttentionDigestAutonomousAgentRuntime;
import {{JAVA_BASE_PACKAGE}}.domain.security.AttentionItem;
import {{JAVA_BASE_PACKAGE}}.domain.security.MyAccountPersonalAttentionDigestTask;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/** Governed lifecycle owner for My Account personal attention digest AutonomousAgent tasks and authorized attention evidence scope. */
public final class MyAccountPersonalAttentionDigestService {
  public static final String START_CAPABILITY = "my_account.personal_attention_digest.start";
  public static final String READ_CAPABILITY = "my_account.personal_attention_digest.read";
  public static final String CANCEL_CAPABILITY = "my_account.personal_attention_digest.cancel";
  public static final String ACCEPT_RESULT_CAPABILITY = "my_account.personal_attention_digest.accept_result";
  public static final String REJECT_RESULT_CAPABILITY = "my_account.personal_attention_digest.reject_result";
  public static final String OPEN_EVIDENCE_CAPABILITY = "my_account.personal_attention_digest.open_evidence";

  private final MyAccountPersonalAttentionDigestTaskRepository repository;
  private final AuthContextResolver authContextResolver;
  private final AttentionService attentionService;
  private final Clock clock;
  private final MyAccountPersonalAttentionDigestAutonomousAgentRuntime autonomousAgentRuntime;
  private final AttentionProducerService attentionProducerService;
  private final WorkstreamEventPublisher workstreamEventPublisher;

  public MyAccountPersonalAttentionDigestService(MyAccountPersonalAttentionDigestTaskRepository repository, AuthContextResolver authContextResolver, AttentionService attentionService, Clock clock) {
    this(repository, authContextResolver, attentionService, clock, new FailClosedMyAccountPersonalAttentionDigestAutonomousAgentRuntime());
  }

  public MyAccountPersonalAttentionDigestService(MyAccountPersonalAttentionDigestTaskRepository repository, AuthContextResolver authContextResolver, AttentionService attentionService, Clock clock, MyAccountPersonalAttentionDigestAutonomousAgentRuntime autonomousAgentRuntime) {
    this(repository, authContextResolver, attentionService, clock, autonomousAgentRuntime, null, null);
  }

  public MyAccountPersonalAttentionDigestService(MyAccountPersonalAttentionDigestTaskRepository repository, AuthContextResolver authContextResolver, AttentionService attentionService, Clock clock, MyAccountPersonalAttentionDigestAutonomousAgentRuntime autonomousAgentRuntime, AttentionProducerService attentionProducerService, WorkstreamEventPublisher workstreamEventPublisher) {
    this.repository = Objects.requireNonNull(repository);
    this.authContextResolver = Objects.requireNonNull(authContextResolver);
    this.attentionService = Objects.requireNonNull(attentionService);
    this.clock = Objects.requireNonNull(clock);
    this.autonomousAgentRuntime = Objects.requireNonNull(autonomousAgentRuntime);
    this.attentionProducerService = attentionProducerService;
    this.workstreamEventPublisher = workstreamEventPublisher;
  }

  public MyAccountPersonalAttentionDigestTask start(AuthContextResolver.ResolvedMe actor, StartPersonalAttentionDigestCommand command, String correlationId) {
    if (command == null) throw new AuthorizationException(400, "personal-attention-digest-command-required");
    if (blank(command.idempotencyKey())) throw new AuthorizationException(400, "idempotency-key-required");
    require(actor, START_CAPABILITY, correlationId);
    var duplicate = repository.findByIdempotencyKey(actor.selectedContext().tenantId(), actor.account().accountId(), command.idempotencyKey());
    if (duplicate.isPresent()) {
      authContextResolver.appendProtectedReadTrace(actor, START_CAPABILITY, "idempotent-personal-attention-digest-replay", correlationId);
      return duplicate.orElseThrow();
    }
    var now = Instant.now(clock);
    var evidence = authorizedEvidence(actor, correlationId);
    var digestTaskId = "my-account-digest-" + stableSuffix(actor.selectedContext().tenantId() + ":" + actor.account().accountId() + ":" + command.idempotencyKey());
    var traceId = "trace-my-account-personal-attention-digest-start-" + stableSuffix(correlationId + ":" + digestTaskId);
    var task = new MyAccountPersonalAttentionDigestTask(
        digestTaskId,
        null,
        actor.selectedContext().tenantId(),
        actor.selectedContext().customerId(),
        actor.selectedContext().membershipId(),
        actor.account().accountId(),
        actor.selectedContext().membershipId(),
        command.idempotencyKey(),
        evidence.size(),
        MyAccountPersonalAttentionDigestTask.Status.QUEUED,
        0,
        "My Account personal attention digest task record created; governed Akka AutonomousAgent task start is being attempted through the backend runtime path.",
        null,
        null,
        null,
        evidenceRefs(evidence),
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
    publishLifecycle(started, start.status() == MyAccountPersonalAttentionDigestTask.Status.BLOCKED_PROVIDER_OR_RUNTIME ? "blocked_provider_or_runtime" : "started", START_CAPABILITY, actor.account().accountId(), correlationId);
    authContextResolver.appendProtectedReadTrace(actor, START_CAPABILITY, start.status() == MyAccountPersonalAttentionDigestTask.Status.BLOCKED_PROVIDER_OR_RUNTIME ? "provider-blocked-fail-closed:no fake success" : "autonomous-agent-task-started", correlationId);
    return started;
  }

  public MyAccountPersonalAttentionDigestTask read(AuthContextResolver.ResolvedMe actor, String digestTaskId, String correlationId) {
    var task = task(actor, digestTaskId);
    require(actor, READ_CAPABILITY, correlationId);
    var projected = projectAutonomousAgentTask(task, correlationId);
    publishLifecycle(projected, semanticTransition(projected), READ_CAPABILITY, actor.account().accountId(), correlationId);
    authContextResolver.appendProtectedReadTrace(actor, READ_CAPABILITY, "browser-safe-personal-attention-digest-read:redacted", correlationId);
    return projected;
  }

  public MyAccountPersonalAttentionDigestTask cancel(AuthContextResolver.ResolvedMe actor, String digestTaskId, String reason, String correlationId) {
    var task = task(actor, digestTaskId);
    require(actor, CANCEL_CAPABILITY, correlationId);
    if (task.terminal()) return task;
    autonomousAgentRuntime.cancel(task, reason, correlationId);
    var traceId = "trace-my-account-personal-attention-digest-cancel-" + stableSuffix(correlationId + ":" + task.digestTaskId());
    var cancelled = task.withStatus(MyAccountPersonalAttentionDigestTask.Status.CANCELLED, task.progressPercent(), firstNonBlank(reason, "My Account personal attention digest cancelled by authorized user; source attention and protected state unchanged."), null, List.of(traceId), Instant.now(clock));
    repository.save(cancelled);
    publishLifecycle(cancelled, "cancelled", CANCEL_CAPABILITY, actor.account().accountId(), correlationId);
    authContextResolver.appendProtectedReadTrace(actor, CANCEL_CAPABILITY, "cancelled:no source attention mutation", correlationId);
    return cancelled;
  }

  public MyAccountPersonalAttentionDigestTask acceptResult(AuthContextResolver.ResolvedMe actor, String digestTaskId, String reason, String correlationId) {
    return decide(actor, digestTaskId, MyAccountPersonalAttentionDigestTask.Status.ACCEPTED, "accepted", reason, correlationId);
  }

  public MyAccountPersonalAttentionDigestTask rejectResult(AuthContextResolver.ResolvedMe actor, String digestTaskId, String reason, String correlationId) {
    if (blank(reason)) throw new AuthorizationException(400, "personal-attention-digest-rejection-reason-required");
    return decide(actor, digestTaskId, MyAccountPersonalAttentionDigestTask.Status.REJECTED, "rejected", reason, correlationId);
  }

  public AttentionService.OpenAttentionItemResult openEvidence(AuthContextResolver.ResolvedMe actor, String attentionItemId, String correlationId) {
    require(actor, OPEN_EVIDENCE_CAPABILITY, correlationId);
    authContextResolver.requireCapability(actor.selectedContext(), AttentionService.OPEN_ATTENTION_ITEM_TOOL);
    return attentionService.openAttentionItem(actor, attentionItemId, correlationId);
  }

  private MyAccountPersonalAttentionDigestTask decide(AuthContextResolver.ResolvedMe actor, String digestTaskId, MyAccountPersonalAttentionDigestTask.Status status, String decision, String reason, String correlationId) {
    var task = task(actor, digestTaskId);
    require(actor, status == MyAccountPersonalAttentionDigestTask.Status.ACCEPTED ? ACCEPT_RESULT_CAPABILITY : REJECT_RESULT_CAPABILITY, correlationId);
    if (!task.resultDecisionAllowed()) throw new AuthorizationException(409, "personal-attention-digest-result-not-completed");
    var traceId = "trace-my-account-personal-attention-digest-" + decision + "-" + stableSuffix(correlationId + ":" + task.digestTaskId());
    var decided = task.withDecision(status, decision, firstNonBlank(reason, "Human personal attention digest decision recorded; source attention, workstreams, authorization, and provider config unchanged."), List.of(traceId), Instant.now(clock));
    repository.save(decided);
    publishLifecycle(decided, status == MyAccountPersonalAttentionDigestTask.Status.ACCEPTED ? "result_accepted" : "result_rejected", status == MyAccountPersonalAttentionDigestTask.Status.ACCEPTED ? ACCEPT_RESULT_CAPABILITY : REJECT_RESULT_CAPABILITY, actor.account().accountId(), correlationId);
    authContextResolver.appendProtectedReadTrace(actor, status == MyAccountPersonalAttentionDigestTask.Status.ACCEPTED ? ACCEPT_RESULT_CAPABILITY : REJECT_RESULT_CAPABILITY, decision + ":advisory-only:no source attention mutation", correlationId);
    return decided;
  }

  private MyAccountPersonalAttentionDigestTask projectAutonomousAgentTask(MyAccountPersonalAttentionDigestTask task, String correlationId) {
    if (task.terminal() || task.status() == MyAccountPersonalAttentionDigestTask.Status.COMPLETED_REVIEW_REQUIRED || task.status() == MyAccountPersonalAttentionDigestTask.Status.COMPLETED_EMPTY || task.status() == MyAccountPersonalAttentionDigestTask.Status.BLOCKED_PROVIDER_OR_RUNTIME || task.status() == MyAccountPersonalAttentionDigestTask.Status.FAILED) return task;
    var projection = autonomousAgentRuntime.project(task, correlationId);
    if (!projection.changed()) return task;
    var updated = task.withWorkerUpdate(projection.status(), projection.progressPercent(), projection.summary(), projection.blockerCode(), projection.authorizedAttentionCount() == 0 ? task.authorizedAttentionCount() : projection.authorizedAttentionCount(), projection.evidenceRefs().isEmpty() ? task.evidenceRefs() : projection.evidenceRefs(), projection.sectionRefs().isEmpty() ? task.sectionRefs() : projection.sectionRefs(), projection.traceIds().isEmpty() ? task.traceIds() : projection.traceIds(), Instant.now(clock));
    repository.save(updated);
    return updated;
  }

  private void publishLifecycle(MyAccountPersonalAttentionDigestTask task, String semanticTransition, String capabilityId, String actorAccountId, String correlationId) {
    if (attentionProducerService != null) attentionProducerService.upsertWorkerTaskState(task, null, correlationId);
    if (workstreamEventPublisher != null) workstreamEventPublisher.publishMyAccountPersonalAttentionDigestLifecycle(task, semanticTransition, capabilityId, actorAccountId, correlationId);
  }

  private static String semanticTransition(MyAccountPersonalAttentionDigestTask task) {
    return switch (task.status()) {
      case QUEUED -> "queued";
      case RUNNING -> "running";
      case BLOCKED_PROVIDER_OR_RUNTIME -> "blocked_provider_or_runtime";
      case FAILED -> "failed";
      case CANCELLED -> "cancelled";
      case COMPLETED_EMPTY, COMPLETED_REVIEW_REQUIRED -> "completed_review_required";
      case ACCEPTED -> "result_accepted";
      case REJECTED -> "result_rejected";
    };
  }

  private MyAccountPersonalAttentionDigestTask task(AuthContextResolver.ResolvedMe actor, String digestTaskId) {
    var task = repository.find(digestTaskId).orElseThrow(() -> new AuthorizationException(404, "personal-attention-digest-not-found-or-forbidden"));
    if (!Objects.equals(actor.selectedContext().tenantId(), task.tenantId())) throw new AuthorizationException(404, "personal-attention-digest-not-found-or-forbidden");
    if (actor.selectedContext().customerId() != null && !Objects.equals(actor.selectedContext().customerId(), task.customerId())) throw new AuthorizationException(404, "personal-attention-digest-not-found-or-forbidden");
    if (!Objects.equals(actor.account().accountId(), task.startedByAccountId())) throw new AuthorizationException(404, "personal-attention-digest-not-found-or-forbidden");
    return task;
  }

  private void require(AuthContextResolver.ResolvedMe actor, String capabilityId, String correlationId) {
    authContextResolver.requireTenant(actor.selectedContext(), actor.selectedContext().tenantId());
    authContextResolver.requireCapability(actor.selectedContext(), capabilityId);
    authContextResolver.appendProtectedReadTrace(actor, capabilityId, "my_account.personalAttentionDigest.v1", correlationId);
  }

  private List<AttentionItem> authorizedEvidence(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), MyAccountService.LIST_PERSONAL_ATTENTION_CAPABILITY);
    var summary = attentionService.listMyAccountItems(actor, correlationId);
    authContextResolver.appendProtectedReadTrace(actor, AttentionService.LIST_MY_ACCOUNT_ITEMS_TOOL, "authorized personal attention evidence:redacted", correlationId);
    return summary.personalQueue();
  }

  private static List<String> evidenceRefs(List<AttentionItem> evidence) {
    var refs = new java.util.ArrayList<String>();
    refs.add("capability:attention.list_my_account_items");
    refs.add("capability:my_account.personal_attention_digest.read");
    refs.add("readSkill:my-account-personal-attention-digest");
    refs.add("readReferenceDoc:my-account-personal-attention-digest");
    refs.addAll(evidence.stream().map(item -> "attention_item:" + item.itemId()).toList());
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
    return Integer.toUnsignedString(Objects.requireNonNullElse(value, "my-account-personal-attention-digest").hashCode(), 36);
  }

  public record StartPersonalAttentionDigestCommand(String idempotencyKey) {}
}
