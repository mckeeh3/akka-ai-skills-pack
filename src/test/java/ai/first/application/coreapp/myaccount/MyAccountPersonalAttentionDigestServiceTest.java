package ai.first.application.coreapp.myaccount;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.first.application.coreapp.myaccount.MyAccountPersonalAttentionDigestAutonomousAgentRuntime;
import ai.first.domain.foundation.identity.Account;
import ai.first.domain.foundation.identity.AccountStatus;
import ai.first.domain.foundation.attention.AttentionCategory;
import ai.first.domain.foundation.attention.AttentionItem;
import ai.first.domain.foundation.attention.AttentionItemStatus;
import ai.first.domain.foundation.attention.AttentionSeverity;
import ai.first.domain.foundation.attention.AttentionSourceRef;
import ai.first.domain.foundation.attention.AttentionSurfaceRef;
import ai.first.domain.foundation.identity.FoundationRole;
import ai.first.domain.foundation.identity.Membership;
import ai.first.domain.foundation.identity.MembershipStatus;
import ai.first.domain.coreapp.myaccount.MyAccountPersonalAttentionDigestTask;
import ai.first.domain.foundation.identity.ScopeType;
import ai.first.domain.foundation.identity.Tenant;
import ai.first.domain.foundation.identity.UserProfile;
import ai.first.domain.foundation.identity.UserSettings;
import ai.first.domain.foundation.identity.WorkosIdentity;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ai.first.application.foundation.attention.AttentionProducerService;
import ai.first.application.foundation.attention.AttentionService;
import ai.first.application.foundation.attention.LocalDemoAttentionRepository;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.AuthorizationException;
import ai.first.application.foundation.identity.LocalDemoIdentityRepository;
import ai.first.application.foundation.workstream.LocalDemoWorkstreamEventRepository;
import ai.first.application.foundation.workstream.WorkstreamEventAttentionConsumer;
import ai.first.application.foundation.workstream.WorkstreamEventPublisher;

class MyAccountPersonalAttentionDigestServiceTest {
  private final Clock clock = Clock.fixed(Instant.parse("2026-05-25T10:15:30Z"), ZoneOffset.UTC);
  private LocalDemoIdentityRepository identityRepository;
  private LocalDemoAttentionRepository attentionRepository;
  private AuthContextResolver resolver;
  private AuthContextResolver.ResolvedMe owner;
  private AuthContextResolver.ResolvedMe employee;
  private MyAccountPersonalAttentionDigestService service;

  @BeforeEach
  void setUp() {
    identityRepository = new LocalDemoIdentityRepository();
    attentionRepository = new LocalDemoAttentionRepository();
    resolver = new AuthContextResolver(identityRepository);
    var attentionService = new AttentionService(attentionRepository, resolver, clock);
    service = new MyAccountPersonalAttentionDigestService(new LocalDemoMyAccountPersonalAttentionDigestTaskRepository(), resolver, attentionService, clock);
    identityRepository.putTenant(new Tenant("tenant-1", "Tenant One", true));
    seed("owner@example.test", "membership-owner", FoundationRole.TENANT_ADMIN);
    seed("employee@example.test", "membership-employee", FoundationRole.TENANT_EMPLOYEE);
    owner = resolver.resolveMe(new WorkosIdentity("workos-owner", "owner@example.test", "Owner"), null, "corr-owner");
    employee = resolver.resolveMe(new WorkosIdentity("workos-employee", "employee@example.test", "Employee"), null, "corr-employee");
    seedAttention(owner, "attention-visible-audit", "agent-audit-trace", "audit.trace.read", AttentionSeverity.WARNING);
    seedAttention(owner, "attention-hidden-admin", "agent-agent-admin", "agent_admin.list_definitions", AttentionSeverity.BLOCKED);
  }

  @Test
  void startCollectsOnlyAuthorizedEvidenceAndFailsClosedWithoutFakeSuccess() {
    var task = service.start(owner, command("idem-digest"), "corr-digest");

    assertEquals(MyAccountPersonalAttentionDigestTask.Status.BLOCKED_PROVIDER_OR_RUNTIME, task.status());
    assertEquals("blocked_provider_or_runtime", task.blockerCode());
    assertEquals(2, task.authorizedAttentionCount());
    assertTrue(task.evidenceRefs().contains("attention_item:attention-visible-audit"));
    assertTrue(task.evidenceRefs().contains("attention_item:attention-hidden-admin"));
    assertTrue(task.summary().contains("fails closed"));
    assertTrue(task.summary().contains("model-less personal attention digest success"));
    assertFalse(task.summary().toLowerCase().contains("api_key"));

    var replay = service.start(owner, command("idem-digest"), "corr-digest-replay");
    assertEquals(task.digestTaskId(), replay.digestTaskId());
  }

  @Test
  void employeeDigestDoesNotLeakHiddenWorkstreamEvidence() {
    var task = service.start(employee, command("idem-employee-digest"), "corr-employee-digest");

    assertEquals(MyAccountPersonalAttentionDigestTask.Status.BLOCKED_PROVIDER_OR_RUNTIME, task.status());
    assertEquals(0, task.authorizedAttentionCount());
    assertFalse(task.evidenceRefs().stream().anyMatch(ref -> ref.contains("attention-visible-audit")));
    assertFalse(task.evidenceRefs().stream().anyMatch(ref -> ref.contains("attention-hidden-admin")));
    assertFalse(task.summary().contains("agent-audit-trace"));
    assertFalse(task.summary().contains("agent-agent-admin"));
  }

  @Test
  void lifecyclePublishesV3EventsAndDigestTaskAttentionWithoutMutatingSourceAttention() {
    var repository = new LocalDemoMyAccountPersonalAttentionDigestTaskRepository();
    var events = new LocalDemoWorkstreamEventRepository();
    var producer = new AttentionProducerService(attentionRepository, identityRepository, clock);
    var consumer = new WorkstreamEventAttentionConsumer(attentionRepository, identityRepository, producer, clock);
    var publisher = new WorkstreamEventPublisher(events, consumer, clock);
    var digests = new MyAccountPersonalAttentionDigestService(repository, resolver, new AttentionService(attentionRepository, resolver, clock), clock, new RecordingPersonalAttentionDigestRuntime(), producer, publisher);

    var task = digests.start(owner, command("idem-events"), "corr-events-start");

    var eventTypes = events.listTenant("tenant-1").stream().map(event -> event.eventType()).toList();
    assertTrue(eventTypes.contains("workflow.my_account.personal_attention_digest.started"));
    assertTrue(eventTypes.contains("worker.task.queued"));
    var digestAttention = attentionRepository.find("tenant-1", "attention:worker-task:" + task.digestTaskId() + ":task-state").orElseThrow();
    assertEquals("agent-my-account", digestAttention.owningWorkstreamId());
    assertEquals(MyAccountPersonalAttentionDigestService.READ_CAPABILITY, digestAttention.requiredCapabilityId());
    assertTrue(digestAttention.summary().contains("no source attention acknowledgement"));
    assertEquals(AttentionItemStatus.OPEN, attentionRepository.find("tenant-1", "attention-visible-audit").orElseThrow().status());

    var completed = task.withWorkerUpdate(MyAccountPersonalAttentionDigestTask.Status.COMPLETED_REVIEW_REQUIRED, 100, "Model-backed redacted digest ready for review; no direct mutation.", null, 2, task.evidenceRefs(), List.of("personal_attention_digest_section:agent-audit-trace"), List.of("trace-complete"), Instant.now(clock));
    repository.save(completed);
    var projected = digests.read(owner, completed.digestTaskId(), "corr-events-read");

    assertEquals(MyAccountPersonalAttentionDigestTask.Status.COMPLETED_REVIEW_REQUIRED, projected.status());
    assertTrue(events.listTenant("tenant-1").stream().anyMatch(event -> "workflow.my_account.personal_attention_digest.completed_review_required".equals(event.eventType())));
    assertTrue(events.listTenant("tenant-1").stream().anyMatch(event -> "worker.task.completed_review_required".equals(event.eventType())));
    assertTrue(events.listTenant("tenant-1").stream().anyMatch(event -> "surface-my-account-personal-attention-digest-result".equals(event.targetSurfaceId())));
  }

  @Test
  void componentClientBackedRuntimeStartIsQueuedAndProjectionCanCompleteReviewRequired() {
    var repository = new LocalDemoMyAccountPersonalAttentionDigestTaskRepository();
    var runtime = new RecordingPersonalAttentionDigestRuntime();
    var digests = new MyAccountPersonalAttentionDigestService(repository, resolver, new AttentionService(attentionRepository, resolver, clock), clock, runtime);

    var task = digests.start(owner, command("idem-autonomous-digest"), "corr-autonomous-digest-start");

    assertEquals(MyAccountPersonalAttentionDigestTask.Status.QUEUED, task.status());
    assertEquals("akka-task-" + task.digestTaskId(), task.autonomousAgentTaskId());
    assertEquals(1, runtime.startCount);

    var replay = digests.start(owner, command("idem-autonomous-digest"), "corr-autonomous-digest-replay");
    assertEquals(task.digestTaskId(), replay.digestTaskId());
    assertEquals(1, runtime.startCount, "idempotent replay must not start a second Akka AutonomousAgent task");

    runtime.nextProjection = new MyAccountPersonalAttentionDigestAutonomousAgentRuntime.Projection(
        MyAccountPersonalAttentionDigestTask.Status.COMPLETED_REVIEW_REQUIRED,
        100,
        "Akka AutonomousAgent completed model-backed redacted personal attention digest; source attention remains authoritative.",
        null,
        null,
        2,
        List.of("attention_item:attention-visible-audit", "attention_item:attention-hidden-admin"),
        List.of("personal_attention_digest_section:agent-audit-trace"),
        List.of("autonomous_task:" + task.autonomousAgentTaskId(), "trace-model-backed-personal-attention-digest-result"));

    var completed = digests.read(owner, task.digestTaskId(), "corr-autonomous-digest-read");

    assertEquals(MyAccountPersonalAttentionDigestTask.Status.COMPLETED_REVIEW_REQUIRED, completed.status());
    assertTrue(completed.summary().contains("model-backed redacted personal attention digest"));
    assertTrue(completed.sectionRefs().stream().anyMatch(ref -> ref.contains("personal_attention_digest_section")));
    assertFalse(completed.summary().toLowerCase().contains("api_key"));
  }

  @Test
  void cancelAcceptRejectAreAdvisoryOnlyAndRequireCompletedResult() {
    var task = service.start(owner, command("idem-decision"), "corr-decision-start");

    var accept = assertThrows(AuthorizationException.class, () -> service.acceptResult(owner, task.digestTaskId(), "looks good", "corr-accept"));
    assertEquals("personal-attention-digest-result-not-completed", accept.reasonCode());
    var reject = assertThrows(AuthorizationException.class, () -> service.rejectResult(owner, task.digestTaskId(), "not enough evidence", "corr-reject"));
    assertEquals("personal-attention-digest-result-not-completed", reject.reasonCode());

    var cancelled = service.cancel(owner, task.digestTaskId(), "not needed", "corr-cancel");
    assertEquals(MyAccountPersonalAttentionDigestTask.Status.CANCELLED, cancelled.status());
    assertTrue(cancelled.summary().contains("not needed"));
  }

  @Test
  void otherAccountCannotReadPersonalDigestTask() {
    var deniedActor = resolver.resolveMe(new WorkosIdentity("workos-employee", "employee@example.test", "Employee"), "membership-employee", "corr-denied");

    var task = service.start(owner, command("idem-owner"), "corr-owner-start");
    var deniedRead = assertThrows(AuthorizationException.class, () -> service.read(deniedActor, task.digestTaskId(), "corr-denied-read"));
    assertEquals("personal-attention-digest-not-found-or-forbidden", deniedRead.reasonCode());
  }

  private MyAccountPersonalAttentionDigestService.StartPersonalAttentionDigestCommand command(String idempotencyKey) {
    return new MyAccountPersonalAttentionDigestService.StartPersonalAttentionDigestCommand(idempotencyKey);
  }

  private static final class RecordingPersonalAttentionDigestRuntime implements MyAccountPersonalAttentionDigestAutonomousAgentRuntime {
    private int startCount;
    private Projection nextProjection = Projection.unchanged();

    @Override
    public StartOutcome start(AuthContextResolver.ResolvedMe actor, MyAccountPersonalAttentionDigestTask starterTask, String correlationId) {
      startCount++;
      return StartOutcome.queued(
          "akka-task-" + starterTask.digestTaskId(),
          "Akka AutonomousAgent task accepted; no deterministic/model-less personal attention digest success is returned by start.",
          List.of("autonomous_task:akka-task-" + starterTask.digestTaskId(), "trace-test-autonomous-personal-attention-digest-start"));
    }

    @Override
    public Projection project(MyAccountPersonalAttentionDigestTask starterTask, String correlationId) {
      return nextProjection;
    }
  }

  private void seedAttention(AuthContextResolver.ResolvedMe actor, String itemId, String workstreamId, String capabilityId, AttentionSeverity severity) {
    var now = Instant.now(clock);
    attentionRepository.upsert(new AttentionItem(
        itemId,
        actor.selectedContext().tenantId(),
        actor.selectedContext().customerId(),
        workstreamId,
        "Visible personal attention",
        "Authorized personal attention evidence for digest testing.",
        AttentionCategory.WORKFLOW_BLOCKED,
        severity,
        AttentionItemStatus.OPEN,
        AttentionItem.AssigneeKind.CAPABILITY,
        capabilityId,
        capabilityId,
        new AttentionSurfaceRef(workstreamId, "surface-test", "dashboard", itemId, AttentionService.OPEN_ATTENTION_ITEM_TOOL, capabilityId),
        List.of(new AttentionSourceRef("domain_event", itemId, "Attention event", capabilityId, "trace-" + itemId, "corr-seed")),
        null,
        now,
        now,
        now,
        null,
        null,
        null,
        null,
        "corr-seed"));
  }

  private void seed(String email, String membershipId, FoundationRole role) {
    identityRepository.saveAccount(new Account(email, null, email, email, AccountStatus.ACTIVE, "LINKED"));
    identityRepository.putProfile(new UserProfile(email, email, email, null, null, null));
    identityRepository.putSettings(new UserSettings(email, UserSettings.ThemeId.AURORA_LIGHT));
    identityRepository.putMembership(new Membership(membershipId, email, ScopeType.TENANT, "tenant-1", null, List.of(role), MembershipStatus.ACTIVE, false, null));
  }
}
