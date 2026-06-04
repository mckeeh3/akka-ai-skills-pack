package ai.first.application.coreapp.agentadmin;

import ai.first.application.foundation.attention.AttentionProducerService;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.AuthorizationException;
import ai.first.application.foundation.workstream.WorkstreamEventPublisher;
import ai.first.domain.coreapp.agentadmin.PromptRiskReviewTask;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/** Deterministic lifecycle owner for Agent Admin prompt-risk review AutonomousAgent tasks. */
public final class AgentAdminPromptRiskReviewService {
  public static final String START_CAPABILITY = "agent_admin.prompt_risk_review.start";
  public static final String READ_CAPABILITY = "agent_admin.prompt_risk_review.read";
  public static final String CANCEL_CAPABILITY = "agent_admin.prompt_risk_review.cancel";
  public static final String ACCEPT_RESULT_CAPABILITY = "agent_admin.prompt_risk_review.accept_result";
  public static final String REJECT_RESULT_CAPABILITY = "agent_admin.prompt_risk_review.reject_result";

  private final PromptRiskReviewTaskRepository repository;
  private final AuthContextResolver authContextResolver;
  private final Clock clock;
  private final AttentionProducerService attentionProducerService;
  private final WorkstreamEventPublisher workstreamEventPublisher;
  private final PromptRiskAutonomousAgentRuntime autonomousAgentRuntime;

  public AgentAdminPromptRiskReviewService(PromptRiskReviewTaskRepository repository, AuthContextResolver authContextResolver, Clock clock) {
    this(repository, authContextResolver, clock, null, null, new FailClosedPromptRiskAutonomousAgentRuntime());
  }

  public AgentAdminPromptRiskReviewService(PromptRiskReviewTaskRepository repository, AuthContextResolver authContextResolver, Clock clock, PromptRiskAutonomousAgentRuntime autonomousAgentRuntime) {
    this(repository, authContextResolver, clock, null, null, autonomousAgentRuntime);
  }

  public AgentAdminPromptRiskReviewService(PromptRiskReviewTaskRepository repository, AuthContextResolver authContextResolver, Clock clock, AttentionProducerService attentionProducerService, WorkstreamEventPublisher workstreamEventPublisher) {
    this(repository, authContextResolver, clock, attentionProducerService, workstreamEventPublisher, new FailClosedPromptRiskAutonomousAgentRuntime());
  }

  public AgentAdminPromptRiskReviewService(PromptRiskReviewTaskRepository repository, AuthContextResolver authContextResolver, Clock clock, AttentionProducerService attentionProducerService, WorkstreamEventPublisher workstreamEventPublisher, PromptRiskAutonomousAgentRuntime autonomousAgentRuntime) {
    this.repository = Objects.requireNonNull(repository);
    this.authContextResolver = Objects.requireNonNull(authContextResolver);
    this.clock = Objects.requireNonNull(clock);
    this.attentionProducerService = attentionProducerService;
    this.workstreamEventPublisher = workstreamEventPublisher;
    this.autonomousAgentRuntime = Objects.requireNonNull(autonomousAgentRuntime);
  }

  public PromptRiskReviewTask start(AuthContextResolver.ResolvedMe actor, StartPromptRiskReviewCommand command, String correlationId) {
    if (command == null) throw new AuthorizationException(400, "prompt-risk-command-required");
    if (blank(command.idempotencyKey())) throw new AuthorizationException(400, "idempotency-key-required");
    if (blank(command.targetAgentDefinitionId())) throw new AuthorizationException(400, "target-agent-definition-required");
    if (blank(command.proposalId())) throw new AuthorizationException(400, "proposal-required");
    if (command.proposedDeltas().isEmpty()) throw new AuthorizationException(400, "proposal-deltas-required");
    require(actor, START_CAPABILITY, correlationId);
    var duplicate = repository.findByIdempotencyKey(actor.selectedContext().tenantId(), actor.account().accountId(), command.idempotencyKey());
    if (duplicate.isPresent()) {
      authContextResolver.appendProtectedReadTrace(actor, START_CAPABILITY, "idempotent-prompt-risk-replay", correlationId);
      return duplicate.orElseThrow();
    }
    var now = Instant.now(clock);
    var taskId = "prompt-risk-" + stableSuffix(actor.selectedContext().tenantId() + ":" + actor.account().accountId() + ":" + command.idempotencyKey());
    var traceId = "trace-agent-admin-prompt-risk-start-" + stableSuffix(correlationId + ":" + taskId);
    var task = new PromptRiskReviewTask(
        taskId,
        null,
        actor.selectedContext().tenantId(),
        actor.selectedContext().customerId(),
        command.targetAgentDefinitionId(),
        command.proposalId(),
        actor.account().accountId(),
        actor.selectedContext().membershipId(),
        command.idempotencyKey(),
        PromptRiskReviewTask.Status.QUEUED,
        0,
        "Prompt-risk task record created; governed Akka AutonomousAgent task start is being attempted through the backend runtime path.",
        null,
        null,
        null,
        command.proposedDeltas(),
        evidenceRefs(command),
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
    authContextResolver.appendProtectedReadTrace(actor, START_CAPABILITY, start.status() == PromptRiskReviewTask.Status.BLOCKED_PROVIDER_OR_RUNTIME ? "provider-blocked-fail-closed" : "autonomous-agent-task-started", correlationId);
    publishLifecycleOrAttention(started, start.status() == PromptRiskReviewTask.Status.BLOCKED_PROVIDER_OR_RUNTIME ? "blocked_provider_or_runtime" : "started", START_CAPABILITY, actor.account().accountId(), correlationId);
    return started;
  }

  public PromptRiskReviewTask read(AuthContextResolver.ResolvedMe actor, String taskId, String correlationId) {
    var task = task(actor, taskId);
    require(actor, READ_CAPABILITY, correlationId);
    var projected = projectAutonomousAgentTask(task, correlationId);
    authContextResolver.appendProtectedReadTrace(actor, READ_CAPABILITY, "browser-safe-prompt-risk-read", correlationId);
    return projected;
  }

  public PromptRiskReviewTask cancel(AuthContextResolver.ResolvedMe actor, String taskId, String reason, String correlationId) {
    var task = task(actor, taskId);
    require(actor, CANCEL_CAPABILITY, correlationId);
    if (task.terminal()) return task;
    autonomousAgentRuntime.cancel(task, reason, correlationId);
    var traceId = "trace-agent-admin-prompt-risk-cancel-" + stableSuffix(correlationId + ":" + task.taskId());
    var cancelled = task.withStatus(PromptRiskReviewTask.Status.CANCELLED, task.progressPercent(), firstNonBlank(reason, "Prompt-risk review cancelled by authorized Agent Admin; behavior artifacts unchanged."), null, List.of(traceId), Instant.now(clock));
    repository.save(cancelled);
    authContextResolver.appendProtectedReadTrace(actor, CANCEL_CAPABILITY, "cancelled:no direct mutation", correlationId);
    publishLifecycleOrAttention(cancelled, "cancelled", CANCEL_CAPABILITY, actor.account().accountId(), correlationId);
    return cancelled;
  }

  public PromptRiskReviewTask acceptResult(AuthContextResolver.ResolvedMe actor, String taskId, String reason, String correlationId) {
    return decide(actor, taskId, PromptRiskReviewTask.Status.ACCEPTED, "accepted", reason, correlationId);
  }

  public PromptRiskReviewTask rejectResult(AuthContextResolver.ResolvedMe actor, String taskId, String reason, String correlationId) {
    if (blank(reason)) throw new AuthorizationException(400, "prompt-risk-rejection-reason-required");
    return decide(actor, taskId, PromptRiskReviewTask.Status.REJECTED, "rejected", reason, correlationId);
  }

  private PromptRiskReviewTask decide(AuthContextResolver.ResolvedMe actor, String taskId, PromptRiskReviewTask.Status status, String decision, String reason, String correlationId) {
    var task = task(actor, taskId);
    require(actor, status == PromptRiskReviewTask.Status.ACCEPTED ? ACCEPT_RESULT_CAPABILITY : REJECT_RESULT_CAPABILITY, correlationId);
    if (!task.resultDecisionAllowed()) throw new AuthorizationException(409, "prompt-risk-result-not-completed");
    var traceId = "trace-agent-admin-prompt-risk-" + decision + "-" + stableSuffix(correlationId + ":" + task.taskId());
    var decided = task.withDecision(status, decision, firstNonBlank(reason, "Human Agent Admin prompt-risk result decision recorded; behavior artifacts unchanged."), List.of(traceId), Instant.now(clock));
    repository.save(decided);
    authContextResolver.appendProtectedReadTrace(actor, status == PromptRiskReviewTask.Status.ACCEPTED ? ACCEPT_RESULT_CAPABILITY : REJECT_RESULT_CAPABILITY, decision + ":advisory-only:no direct activation", correlationId);
    publishLifecycleOrAttention(decided, status == PromptRiskReviewTask.Status.ACCEPTED ? "result_accepted" : "result_rejected", status == PromptRiskReviewTask.Status.ACCEPTED ? ACCEPT_RESULT_CAPABILITY : REJECT_RESULT_CAPABILITY, actor.account().accountId(), correlationId);
    return decided;
  }

  private PromptRiskReviewTask projectAutonomousAgentTask(PromptRiskReviewTask task, String correlationId) {
    if (task.terminal() || task.status() == PromptRiskReviewTask.Status.COMPLETED_REVIEW_REQUIRED || task.status() == PromptRiskReviewTask.Status.BLOCKED_PROVIDER_OR_RUNTIME) return task;
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
      case ACCEPTED -> "result_accepted";
      case REJECTED -> "result_rejected";
    };
    publishLifecycleOrAttention(updated, transition, READ_CAPABILITY, null, correlationId);
    return updated;
  }

  private void publishLifecycleOrAttention(PromptRiskReviewTask task, String semanticTransition, String capabilityId, String actorAccountId, String correlationId) {
    if (workstreamEventPublisher != null) {
      workstreamEventPublisher.publishPromptRiskReviewLifecycle(task, semanticTransition, capabilityId, actorAccountId, correlationId);
    } else if (attentionProducerService != null) {
      if (task.status() == PromptRiskReviewTask.Status.CANCELLED || task.status() == PromptRiskReviewTask.Status.ACCEPTED) {
        attentionProducerService.resolveWorkerTaskState(task, semanticTransition, correlationId);
      } else {
        attentionProducerService.upsertWorkerTaskState(task, null, correlationId);
      }
    }
  }

  private PromptRiskReviewTask task(AuthContextResolver.ResolvedMe actor, String taskId) {
    var task = repository.find(taskId).orElseThrow(() -> new AuthorizationException(404, "prompt-risk-task-not-found-or-forbidden"));
    if (!Objects.equals(actor.selectedContext().tenantId(), task.tenantId())) throw new AuthorizationException(404, "prompt-risk-task-not-found-or-forbidden");
    if (actor.selectedContext().customerId() != null && !Objects.equals(actor.selectedContext().customerId(), task.customerId())) throw new AuthorizationException(404, "prompt-risk-task-not-found-or-forbidden");
    return task;
  }

  private void require(AuthContextResolver.ResolvedMe actor, String capabilityId, String correlationId) {
    authContextResolver.requireTenant(actor.selectedContext(), actor.selectedContext().tenantId());
    authContextResolver.requireCapability(actor.selectedContext(), capabilityId);
    authContextResolver.appendProtectedReadTrace(actor, capabilityId, "agent_admin.prompt_risk_review_task.v1", correlationId);
  }

  private static List<String> evidenceRefs(StartPromptRiskReviewCommand command) {
    var refs = new java.util.ArrayList<String>();
    refs.add("agentAdminEvidence.read");
    refs.add("readSkill:agent-admin-prompt-risk-review");
    refs.add("readReferenceDoc:agent-admin-prompt-risk-review");
    refs.add("proposal:" + command.proposalId());
    refs.add("targetAgentDefinition:" + command.targetAgentDefinitionId());
    refs.addAll(command.evidenceRefs());
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
    return Integer.toUnsignedString(Objects.requireNonNullElse(value, "prompt-risk").hashCode(), 36);
  }

  public record StartPromptRiskReviewCommand(
      String targetAgentDefinitionId,
      String proposalId,
      List<PromptRiskReviewTask.BehaviorArtifactDelta> proposedDeltas,
      List<String> evidenceRefs,
      String idempotencyKey) {
    public StartPromptRiskReviewCommand {
      proposedDeltas = List.copyOf(proposedDeltas == null ? List.of() : proposedDeltas);
      evidenceRefs = List.copyOf(evidenceRefs == null ? List.of() : evidenceRefs);
    }
  }
}
