package {{JAVA_BASE_PACKAGE}}.application.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import {{JAVA_BASE_PACKAGE}}.domain.security.AccessReviewTask;
import {{JAVA_BASE_PACKAGE}}.domain.security.Account;
import {{JAVA_BASE_PACKAGE}}.domain.security.AccountStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.FoundationRole;
import {{JAVA_BASE_PACKAGE}}.domain.security.Membership;
import {{JAVA_BASE_PACKAGE}}.domain.security.MembershipStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.ScopeType;
import {{JAVA_BASE_PACKAGE}}.domain.security.Tenant;
import {{JAVA_BASE_PACKAGE}}.domain.security.UserProfile;
import {{JAVA_BASE_PACKAGE}}.domain.security.UserSettings;
import {{JAVA_BASE_PACKAGE}}.domain.security.WorkosIdentity;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserAdminAccessReviewServiceTest {
  private final Clock clock = Clock.fixed(Instant.parse("2026-05-21T10:15:30Z"), ZoneOffset.UTC);
  private LocalDemoIdentityRepository identityRepository;
  private AuthContextResolver resolver;
  private UserAdminService userAdminService;
  private UserAdminAccessReviewService accessReviews;
  private AuthContextResolver.ResolvedMe tenantAdmin;

  @BeforeEach
  void setUp() {
    identityRepository = new LocalDemoIdentityRepository();
    resolver = new AuthContextResolver(identityRepository);
    userAdminService = new UserAdminService(identityRepository, clock);
    accessReviews = new UserAdminAccessReviewService(new LocalDemoAccessReviewTaskRepository(), userAdminService, clock);
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
    assertTrue(task.summary().contains("Access-review task record created"));

    var replay = accessReviews.start(tenantAdmin, "idem-access-review", "corr-access-review-replay");
    assertEquals(task.taskId(), replay.taskId());
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("user_admin.access_review.start") && event.result() == {{JAVA_BASE_PACKAGE}}.domain.security.AdminAuditEvent.Result.NO_OP));
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

  private void seed(String email, String membershipId, FoundationRole role) {
    identityRepository.saveAccount(new Account(email, null, email, email, AccountStatus.ACTIVE, "LINKED"));
    identityRepository.putProfile(new UserProfile(email, email, email, null, null, null));
    identityRepository.putSettings(new UserSettings(email, UserSettings.UiMode.LIGHT));
    identityRepository.putMembership(new Membership(membershipId, email, ScopeType.TENANT, "tenant-1", null, List.of(role), MembershipStatus.ACTIVE, false, null));
  }
}
