package {{JAVA_BASE_PACKAGE}}.application.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import {{JAVA_BASE_PACKAGE}}.domain.security.Account;
import {{JAVA_BASE_PACKAGE}}.domain.security.AccountStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.AttentionCategory;
import {{JAVA_BASE_PACKAGE}}.domain.security.AttentionItem;
import {{JAVA_BASE_PACKAGE}}.domain.security.AttentionItemStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.AttentionSeverity;
import {{JAVA_BASE_PACKAGE}}.domain.security.AttentionSourceRef;
import {{JAVA_BASE_PACKAGE}}.domain.security.AttentionSurfaceRef;
import {{JAVA_BASE_PACKAGE}}.domain.security.FoundationRole;
import {{JAVA_BASE_PACKAGE}}.domain.security.Membership;
import {{JAVA_BASE_PACKAGE}}.domain.security.MembershipStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.MyAccountPersonalAttentionDigestTask;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationCategory;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationLifecycleStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationPriority;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationRedactionLevel;
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

class NotificationServiceTest {
  private LocalDemoIdentityRepository identityRepository;
  private LocalDemoNotificationRepository notificationRepository;
  private AuthContextResolver resolver;
  private NotificationService service;
  private Clock clock;

  @BeforeEach
  void setUp() {
    identityRepository = new LocalDemoIdentityRepository();
    notificationRepository = new LocalDemoNotificationRepository();
    resolver = new AuthContextResolver(identityRepository);
    clock = Clock.fixed(Instant.parse("2026-05-26T10:00:00Z"), ZoneOffset.UTC);
    service = new NotificationService(notificationRepository, resolver, clock);

    identityRepository.putTenant(new Tenant("tenant-1", "Tenant One", true));
    identityRepository.putTenant(new Tenant("tenant-2", "Tenant Two", true));
    addAccount("admin@example.test", "membership-admin", "tenant-1", List.of(FoundationRole.TENANT_ADMIN));
    addAccount("member@example.test", "membership-member", "tenant-1", List.of(FoundationRole.TENANT_EMPLOYEE));
    addAccount("other@example.test", "membership-other", "tenant-2", List.of(FoundationRole.TENANT_ADMIN));
  }

  @Test
  void projectsAttentionIntoBackendOwnedInAppCenterWithRedactedSafeContent() {
    var actor = actor("admin@example.test", "membership-admin", "corr-project");
    var projected = service.projectFromAttention(actor, attention("attention-agent", "tenant-1", "agent-agent-admin", "agent_admin.list_definitions", AttentionCategory.PROVIDER_READINESS, AttentionSeverity.BLOCKED, "Provider token=secret is blocked"), "corr-project");

    var center = service.listMyAccountCenter(actor, "corr-center");

    assertEquals(NotificationCategory.PROVIDER_READINESS, projected.category());
    assertEquals(NotificationPriority.BLOCKED, projected.priority());
    assertEquals(1, center.unreadCount());
    assertEquals("my_account.notification_center.v1", center.surfaceContract());
    assertTrue(center.items().get(0).summary().contains("[redacted]"));
    assertTrue(center.items().get(0).sourceRefs().stream().allMatch(ref -> ref.requiredCapabilityId().equals("agent_admin.list_definitions")));
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("NOTIFICATION_PROJECT_FROM_SOURCE") && event.correlationId().equals("corr-project")));
  }

  @Test
  void duplicateProjectionUpdatesSameDedupeKeyWithoutCreatingDuplicateActiveNotifications() {
    var actor = actor("admin@example.test", "membership-admin", "corr-project");
    var first = service.projectFromAttention(actor, attention("attention-agent", "tenant-1", "agent-agent-admin", "agent_admin.list_definitions", AttentionCategory.PROVIDER_READINESS, AttentionSeverity.WARNING, "first summary"), "corr-first");
    var second = service.projectFromAttention(actor, attention("attention-agent", "tenant-1", "agent-agent-admin", "agent_admin.list_definitions", AttentionCategory.PROVIDER_READINESS, AttentionSeverity.BLOCKED, "second summary"), "corr-second");

    var center = service.listMyAccountCenter(actor, "corr-center");

    assertEquals(first.notificationId(), second.notificationId());
    assertEquals(1, center.visibleCount());
    assertEquals("second summary", center.items().get(0).summary());
    assertEquals(NotificationPriority.BLOCKED, center.items().get(0).priority());
  }

  @Test
  void lifecycleOperationsAreNotificationOnlyIdempotentAndAudited() {
    var actor = actor("admin@example.test", "membership-admin", "corr-lifecycle");
    var projected = service.projectFromAttention(actor, attention("attention-audit", "tenant-1", "agent-audit-trace", "audit.trace.read", AttentionCategory.AUDIT_FAILURE_EVIDENCE, AttentionSeverity.WARNING, "Audit evidence available"), "corr-project");

    var read = service.markRead(actor, projected.notificationId(), "corr-read");
    var readAgain = service.markRead(actor, projected.notificationId(), "corr-read-again");
    var snoozed = service.snooze(actor, projected.notificationId(), Instant.parse("2026-05-27T10:00:00Z"), "corr-snooze");
    var dismissed = service.dismiss(actor, projected.notificationId(), "corr-dismiss");
    var archived = service.archive(actor, projected.notificationId(), "corr-archive");

    assertEquals(NotificationLifecycleStatus.READ, read.status());
    assertEquals(read, readAgain);
    assertEquals(NotificationLifecycleStatus.SNOOZED, snoozed.status());
    assertEquals(NotificationLifecycleStatus.DISMISSED, dismissed.status());
    assertEquals(NotificationLifecycleStatus.ARCHIVED, archived.status());
    assertTrue(service.listMyAccountCenter(actor, "corr-after-archive").items().isEmpty());
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("NOTIFICATION_MARK_READ") && event.reasonCode().contains("no_op") && event.correlationId().equals("corr-read-again")));
  }

  @Test
  void preferencesFilterCenterWithoutEnumeratingHiddenNotifications() {
    var actor = actor("admin@example.test", "membership-admin", "corr-pref");
    service.projectFromAttention(actor, attention("attention-agent", "tenant-1", "agent-agent-admin", "agent_admin.list_definitions", AttentionCategory.PROVIDER_READINESS, AttentionSeverity.WARNING, "Provider warning"), "corr-project");
    service.updatePreference(actor, NotificationCategory.PROVIDER_READINESS, true, NotificationPriority.BLOCKED, null, false, "corr-pref-update");

    var center = service.listMyAccountCenter(actor, "corr-center");

    assertEquals(0, center.visibleCount());
    assertEquals(1, center.preferencesSummary().size());
    assertFalse(center.toString().contains("email"));
    assertFalse(center.toString().contains("push"));
  }

  @Test
  void tenantRecipientAndCapabilityIsolationReturnNotFoundOrRedactedWithoutLeaks() {
    var admin = actor("admin@example.test", "membership-admin", "corr-admin");
    var projected = service.projectFromAttention(admin, attention("attention-agent", "tenant-1", "agent-agent-admin", "agent_admin.list_definitions", AttentionCategory.PROVIDER_READINESS, AttentionSeverity.BLOCKED, "Provider blocked"), "corr-project");

    var member = actor("member@example.test", "membership-member", "corr-member");
    var direct = service.getNotification(member, projected.notificationId(), "corr-get-member");
    var otherTenant = actor("other@example.test", "membership-other", "corr-other");

    assertTrue(service.listMyAccountCenter(member, "corr-member-center").items().isEmpty());
    assertEquals(NotificationRedactionLevel.NOT_FOUND_OR_REDACTED, direct.redactionLevel());
    assertThrows(AuthorizationException.class, () -> service.projectFromAttention(otherTenant, attention("attention-cross", "tenant-1", "agent-agent-admin", "agent_admin.list_definitions", AttentionCategory.PROVIDER_READINESS, AttentionSeverity.BLOCKED, "cross tenant"), "corr-cross"));
    assertFalse(direct.toString().contains("agent-agent-admin"));
  }

  @Test
  void projectsPersonalDigestReadyAndBlockedStatesToRecipientOnly() {
    var actor = actor("admin@example.test", "membership-admin", "corr-digest");
    var ready = service.projectFromPersonalDigest(actor, digest("digest-ready", MyAccountPersonalAttentionDigestTask.Status.COMPLETED_REVIEW_REQUIRED, "Review digest", null), "corr-ready");
    var blocked = service.projectFromPersonalDigest(actor, digest("digest-blocked", MyAccountPersonalAttentionDigestTask.Status.BLOCKED_PROVIDER_OR_RUNTIME, "Blocked", "provider-missing"), "corr-blocked");

    assertEquals(NotificationCategory.DIGEST_READY, ready.category());
    assertEquals(NotificationCategory.DIGEST_BLOCKED, blocked.category());
    assertEquals(2, service.listMyAccountCenter(actor, "corr-center").visibleCount());
  }

  private AttentionItem attention(String itemId, String tenantId, String workstreamId, String capabilityId, AttentionCategory category, AttentionSeverity severity, String summary) {
    var now = Instant.parse("2026-05-26T09:00:00Z");
    return new AttentionItem(itemId, tenantId, null, workstreamId, "Attention " + itemId, summary, category, severity, AttentionItemStatus.OPEN, AttentionItem.AssigneeKind.CAPABILITY, capabilityId, capabilityId, new AttentionSurfaceRef(workstreamId, "surface-" + workstreamId, "dashboard", itemId, AttentionService.OPEN_ATTENTION_ITEM_TOOL, capabilityId), List.of(new AttentionSourceRef("audit_trace", "trace-" + itemId, "Trace " + itemId, capabilityId, "trace-" + itemId, "corr-source")), null, now, now, now, null, null, null, null, "corr-source");
  }

  private MyAccountPersonalAttentionDigestTask digest(String digestTaskId, MyAccountPersonalAttentionDigestTask.Status status, String summary, String blockerCode) {
    var now = Instant.parse("2026-05-26T09:00:00Z");
    return new MyAccountPersonalAttentionDigestTask(digestTaskId, "agent-task-" + digestTaskId, "tenant-1", null, "membership-admin", "admin@example.test", "membership-admin", "idem-" + digestTaskId, 3, status, 100, summary, blockerCode, null, null, List.of("evidence-" + digestTaskId), List.of("section-" + digestTaskId), List.of("trace-" + digestTaskId), now, now);
  }

  private AuthContextResolver.ResolvedMe actor(String email, String membershipId, String correlationId) {
    return resolver.resolveMe(new WorkosIdentity("workos-" + email, email, email), membershipId, correlationId);
  }

  private void addAccount(String email, String membershipId, String tenantId, List<FoundationRole> roles) {
    identityRepository.saveAccount(new Account(email, null, email, email, AccountStatus.ACTIVE, "LINKED"));
    identityRepository.putProfile(new UserProfile(email, email, email, null, null, null));
    identityRepository.putSettings(new UserSettings(email, UserSettings.ThemeId.AURORA_LIGHT));
    identityRepository.putMembership(new Membership(membershipId, email, ScopeType.TENANT, tenantId, null, roles, MembershipStatus.ACTIVE, false, null));
  }
}
