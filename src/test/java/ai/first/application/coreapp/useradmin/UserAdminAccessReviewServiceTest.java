package ai.first.application.coreapp.useradmin;

import ai.first.domain.foundation.audit.AdminAuditEvent;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.first.domain.coreapp.useradmin.AccessReviewTask;
import ai.first.domain.foundation.identity.Account;
import ai.first.domain.foundation.identity.AccountStatus;
import ai.first.domain.foundation.identity.FoundationRole;
import ai.first.domain.foundation.identity.Membership;
import ai.first.domain.foundation.identity.MembershipStatus;
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
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.AuthorizationException;
import ai.first.application.foundation.identity.InMemoryTestIdentityRepository;

class UserAdminAccessReviewServiceTest {
  private final Clock clock = Clock.fixed(Instant.parse("2026-05-21T10:15:30Z"), ZoneOffset.UTC);
  private InMemoryTestIdentityRepository identityRepository;
  private AuthContextResolver resolver;
  private UserAdminService userAdminService;
  private UserAdminAccessReviewService accessReviews;
  private AuthContextResolver.ResolvedMe tenantAdmin;

  @BeforeEach
  void setUp() {
    identityRepository = new InMemoryTestIdentityRepository();
    resolver = new AuthContextResolver(identityRepository);
    userAdminService = new UserAdminService(identityRepository, clock);
    accessReviews = new UserAdminAccessReviewService(new InMemoryTestAccessReviewTaskRepository(), userAdminService, clock);
    identityRepository.putTenant(new Tenant("tenant-1", "Tenant One", true));
    seed("admin@example.test", "membership-admin", FoundationRole.TENANT_ADMIN);
    seed("member@example.test", "membership-member", FoundationRole.TENANT_EMPLOYEE);
    tenantAdmin = resolver.resolveMe(new WorkosIdentity("workos-admin", "admin@example.test", "Tenant Admin"), null, "corr-admin");
  }

  @Test
  void startCreatesDurableProviderBlockedAccessReviewTaskAndReplaysIdempotently() {
    var task = accessReviews.start(tenantAdmin, "idem-access-review", "corr-access-review");

    assertEquals(AccessReviewTask.Status.BLOCKED_PROVIDER_OR_RUNTIME, task.status());
    assertEquals("blocked_provider_or_runtime", task.blockerCode());
    assertTrue(task.evidenceRefs().contains("userAdminEvidence.read"));
    assertTrue(task.summary().contains("provider/runtime configuration"));
    assertTrue(task.traceIds().get(0).contains("trace-useradmin-access-review-start"));
    assertTrue(task.summary().contains("fails closed"));

    var replay = accessReviews.start(tenantAdmin, "idem-access-review", "corr-access-review-replay");
    assertEquals(task.taskId(), replay.taskId());
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("user_admin.access_review.start") && event.result() == ai.first.domain.foundation.audit.AdminAuditEvent.Result.NO_OP));
  }

  @Test
  void componentClientBackedAutonomousAgentRuntimeStartIsQueuedAndIdempotentUntilProjectionCompletes() {
    var repository = new InMemoryTestAccessReviewTaskRepository();
    var runtime = new RecordingAutonomousAgentRuntime();
    var service = new UserAdminAccessReviewService(repository, userAdminService, clock, null, null, runtime);

    var task = service.start(tenantAdmin, "idem-autonomous-agent", "corr-autonomous-start");

    assertEquals(AccessReviewTask.Status.QUEUED, task.status());
    assertEquals("akka-task-" + task.taskId(), task.autonomousAgentTaskId());
    assertTrue(task.traceIds().stream().anyMatch(trace -> trace.startsWith("autonomous_task:")));
    assertEquals(1, runtime.startCount);

    var replay = service.start(tenantAdmin, "idem-autonomous-agent", "corr-autonomous-replay");
    assertEquals(task.taskId(), replay.taskId());
    assertEquals(1, runtime.startCount, "idempotent replay must not start a second Akka AutonomousAgent task");

    runtime.nextProjection = new AccessReviewAutonomousAgentRuntime.Projection(
        AccessReviewTask.Status.COMPLETED,
        100,
        "Akka AutonomousAgent completed model-backed advisory access review; human review required.",
        null,
        null,
        List.of("userAdminEvidence.read", "readSkill:ua.access-review-triage.v1"),
        List.of("autonomous_agent_recommendation:review dormant admin"),
        List.of("autonomous_task:" + task.autonomousAgentTaskId(), "trace-model-backed-autonomous-result"));

    var completed = service.read(tenantAdmin, task.taskId(), "corr-autonomous-read");

    assertEquals(AccessReviewTask.Status.COMPLETED, completed.status());
    assertTrue(completed.summary().contains("model-backed advisory"));
    assertTrue(completed.recommendationRefs().stream().anyMatch(ref -> ref.contains("autonomous_agent_recommendation")));
    assertFalse(completed.summary().toLowerCase().contains("api_key"));
  }

  @Test
  void readAndCancelAreScopedAndDoNotMutateUserAdminAccessState() {
    var beforeStatus = identityRepository.findMembership("membership-member").orElseThrow().status();
    var task = accessReviews.start(tenantAdmin, "idem-cancel", "corr-cancel-start");

    var read = accessReviews.read(tenantAdmin, task.taskId(), "corr-read");
    assertEquals(task.taskId(), read.taskId());

    var cancelled = accessReviews.cancel(tenantAdmin, task.taskId(), "not needed", "corr-cancel");
    assertEquals(AccessReviewTask.Status.CANCELLED, cancelled.status());
    assertEquals(beforeStatus, identityRepository.findMembership("membership-member").orElseThrow().status(), "Access-review task lifecycle must not directly mutate membership status.");
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("user_admin.access_review.cancel") && event.reasonCode().equals("cancelled")));
  }

  @Test
  void acceptOrRejectRequiresCompletedWorkerResultAndLeavesAccessUnchanged() {
    var task = accessReviews.start(tenantAdmin, "idem-decision", "corr-decision-start");

    var accept = assertThrows(AuthorizationException.class, () -> accessReviews.acceptResult(tenantAdmin, task.taskId(), "looks good", "corr-accept"));
    assertEquals("access-review-result-not-completed", accept.reasonCode());
    var reject = assertThrows(AuthorizationException.class, () -> accessReviews.rejectResult(tenantAdmin, task.taskId(), "not enough evidence", "corr-reject"));
    assertEquals("access-review-result-not-completed", reject.reasonCode());
    assertEquals(List.of(FoundationRole.TENANT_EMPLOYEE), identityRepository.findMembership("membership-member").orElseThrow().roles());
  }

  @Test
  void memberWithoutUserAdminAuthorityCannotStartOrReadAccessReview() {
    var deniedActor = resolver.resolveMe(new WorkosIdentity("workos-member", "member@example.test", "Member"), "membership-member", "corr-member");

    var deniedStart = assertThrows(AuthorizationException.class, () -> accessReviews.start(deniedActor, "idem-denied", "corr-denied"));
    assertTrue(deniedStart.reasonCode().contains("missing-capability"));

    var task = accessReviews.start(tenantAdmin, "idem-admin", "corr-admin-start");
    var deniedRead = assertThrows(AuthorizationException.class, () -> accessReviews.read(deniedActor, task.taskId(), "corr-denied-read"));
    assertTrue(deniedRead.reasonCode().contains("missing-capability"));
    assertFalse(deniedRead.reasonCode().contains("tenant-1"));
  }

  private static final class RecordingAutonomousAgentRuntime implements AccessReviewAutonomousAgentRuntime {
    private int startCount;
    private Projection nextProjection = Projection.unchanged();

    @Override
    public StartOutcome start(AuthContextResolver.ResolvedMe actor, AccessReviewTask starterTask, String correlationId) {
      startCount++;
      return StartOutcome.queued(
          "akka-task-" + starterTask.taskId(),
          "Akka AutonomousAgent task accepted; no deterministic/model-less access-review success is returned by start.",
          List.of("autonomous_task:akka-task-" + starterTask.taskId(), "trace-test-autonomous-start"));
    }

    @Override
    public Projection project(AccessReviewTask starterTask, String correlationId) {
      return nextProjection;
    }
  }

  private void seed(String email, String membershipId, FoundationRole role) {
    identityRepository.saveAccount(new Account(email, null, email, email, AccountStatus.ACTIVE, "LINKED"));
    identityRepository.putProfile(new UserProfile(email, email, email, null, null, null));
    identityRepository.putSettings(new UserSettings(email, UserSettings.ThemeId.AURORA_LIGHT));
    identityRepository.putMembership(new Membership(membershipId, email, ScopeType.TENANT, "tenant-1", null, List.of(role), MembershipStatus.ACTIVE, false, null));
  }
}
