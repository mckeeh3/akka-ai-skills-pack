package ai.first.application.foundation.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.first.domain.coreapp.agentadmin.PromptRiskReviewTask;
import ai.first.domain.coreapp.useradmin.AccessReviewTask;
import ai.first.domain.foundation.identity.Account;
import ai.first.domain.foundation.identity.AccountStatus;
import ai.first.domain.foundation.attention.AttentionCategory;
import ai.first.domain.foundation.attention.AttentionItem;
import ai.first.domain.foundation.attention.AttentionItemStatus;
import ai.first.domain.foundation.attention.AttentionSeverity;
import ai.first.domain.foundation.attention.AttentionSourceRef;
import ai.first.domain.foundation.attention.AttentionSurfaceRef;
import ai.first.domain.coreapp.audit.AuditTraceSummaryTask;
import ai.first.domain.foundation.identity.FoundationRole;
import ai.first.domain.coreapp.governance.GovernancePolicyImpactTask;
import ai.first.domain.foundation.identity.Membership;
import ai.first.domain.foundation.identity.MembershipStatus;
import ai.first.domain.coreapp.myaccount.MyAccountPersonalAttentionDigestTask;
import ai.first.domain.foundation.notification.NotificationCategory;
import ai.first.domain.foundation.notification.NotificationChannel;
import ai.first.domain.foundation.notification.NotificationChannelStatus;
import ai.first.domain.foundation.notification.NotificationDeliveryAttemptStatus;
import ai.first.domain.foundation.notification.NotificationLifecycleStatus;
import ai.first.domain.foundation.notification.NotificationPriority;
import ai.first.domain.foundation.notification.NotificationRedactionLevel;
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
import ai.first.application.foundation.attention.AttentionService;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.AuthorizationException;
import ai.first.application.foundation.identity.InMemoryTestIdentityRepository;

class NotificationServiceTest {
  private InMemoryTestIdentityRepository identityRepository;
  private InMemoryTestNotificationRepository notificationRepository;
  private AuthContextResolver resolver;
  private NotificationService service;
  private Clock clock;

  @BeforeEach
  void setUp() {
    identityRepository = new InMemoryTestIdentityRepository();
    notificationRepository = new InMemoryTestNotificationRepository();
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

  @Test
  void projectsGeneralizedWorkerTaskStatesAcrossImplementedAutonomousAgentVerticals() {
    var actor = actor("admin@example.test", "membership-admin", "corr-workers");

    var access = service.projectFromAccessReviewTask(actor, accessReviewTask(AccessReviewTask.Status.COMPLETED, "Access review recommendations need human review", null), "corr-access");
    var promptRisk = service.projectFromPromptRiskReviewTask(actor, promptRiskTask(PromptRiskReviewTask.Status.BLOCKED_PROVIDER_OR_RUNTIME, "Prompt-risk review blocked", "provider-missing"), "corr-prompt-risk");
    var auditSummary = service.projectFromAuditTraceSummaryTask(actor, auditTraceSummaryTask(AuditTraceSummaryTask.Status.FAILED, "Audit summary failed safely", "evidence-unavailable"), "corr-audit-summary");
    var governanceImpact = service.projectFromGovernancePolicyImpactTask(actor, governancePolicyImpactTask(GovernancePolicyImpactTask.Status.ACCEPTED, "Impact analysis disposition accepted; policy unchanged", null), "corr-governance-impact");

    var center = service.listMyAccountCenter(actor, "corr-workers-center");

    assertEquals(NotificationCategory.WORKSTREAM_UPDATE, access.category());
    assertEquals(NotificationPriority.WARNING, access.priority());
    assertEquals(NotificationCategory.PROVIDER_READINESS, promptRisk.category());
    assertEquals(NotificationPriority.BLOCKED, promptRisk.priority());
    assertEquals(NotificationCategory.AUDIT_OR_SECURITY, auditSummary.category());
    assertEquals(NotificationCategory.POLICY_OR_GOVERNANCE, governanceImpact.category());
    assertEquals(4, center.visibleCount());
    assertTrue(center.items().stream().anyMatch(item -> item.origin().equals("access_review_task") && item.surfaceRef().surfaceId().equals("surface-user-admin-access-review-task")));
    assertTrue(center.items().stream().anyMatch(item -> item.origin().equals("prompt_risk_review_task") && item.summary().contains("Prompt-risk review blocked")));
    assertTrue(center.items().stream().flatMap(item -> item.sourceRefs().stream()).allMatch(ref -> ref.requiredCapabilityId() != null && !ref.requiredCapabilityId().isBlank()));
    assertFalse(center.toString().contains("token="));
  }

  @Test
  void generalizedWorkerTaskProjectionIsRecipientAndCapabilityScoped() {
    var admin = actor("admin@example.test", "membership-admin", "corr-workers");
    var member = actor("member@example.test", "membership-member", "corr-member");

    var projected = service.projectFromAccessReviewTask(admin, accessReviewTask(AccessReviewTask.Status.COMPLETED, "Review ready", null), "corr-access");
    var hidden = service.getNotification(member, projected.notificationId(), "corr-member-get");

    assertEquals(NotificationRedactionLevel.NOT_FOUND_OR_REDACTED, hidden.redactionLevel());
    assertTrue(service.listMyAccountCenter(member, "corr-member-center").items().isEmpty());
    assertThrows(AuthorizationException.class, () -> service.projectFromAccessReviewTask(member, accessReviewTask(AccessReviewTask.Status.COMPLETED, "Review ready", null), "corr-member-project"));
  }

  @Test
  void channelRegistryExposesProviderNeutralFailClosedExternalChannels() {
    var actor = actor("admin@example.test", "membership-admin", "corr-registry");

    var registry = service.listChannelRegistry(actor, "corr-registry");

    assertTrue(registry.stream().anyMatch(entry -> entry.channel() == NotificationChannel.IN_APP && entry.status() == NotificationChannelStatus.ACTIVE));
    assertTrue(registry.stream().anyMatch(entry -> entry.channel() == NotificationChannel.WEBHOOK && entry.status() == NotificationChannelStatus.PROVIDER_UNCONFIGURED && entry.localTestOutboxAvailable()));
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("NOTIFICATION_DELIVERY_LIST_PLATFORM")));
  }

  @Test
  void externalDeliveryAttemptFailsClosedAndCapturesLocalOutboxWithoutSuccess() {
    var actor = actor("admin@example.test", "membership-admin", "corr-external");
    var projected = service.projectFromAttention(actor, attention("attention-webhook", "tenant-1", "agent-agent-admin", "agent_admin.list_definitions", AttentionCategory.PROVIDER_READINESS, AttentionSeverity.BLOCKED, "Provider api_key=secret is blocked"), "corr-project");

    var attempt = service.evaluateExternalDelivery(actor, projected.notificationId(), NotificationChannel.WEBHOOK, "webhook https://hooks.example.test/redacted", "corr-webhook");
    var duplicate = service.evaluateExternalDelivery(actor, projected.notificationId(), NotificationChannel.WEBHOOK, "webhook duplicate", "corr-webhook-dup");

    assertEquals(NotificationDeliveryAttemptStatus.BLOCKED_PROVIDER_UNCONFIGURED, attempt.status());
    assertEquals(attempt.attemptId(), duplicate.attemptId());
    assertFalse(attempt.status().name().contains("SENT"));
    assertEquals(1, service.listDeliveryAttempts(actor, "corr-list").size());
    assertEquals(1, service.listExternalOutbox(actor, "corr-outbox").size());
    assertTrue(attempt.safeErrorSummary().contains("not configured"));
    assertFalse(attempt.toString().contains("api_key=secret"));
  }

  private AttentionItem attention(String itemId, String tenantId, String workstreamId, String capabilityId, AttentionCategory category, AttentionSeverity severity, String summary) {
    var now = Instant.parse("2026-05-26T09:00:00Z");
    return new AttentionItem(itemId, tenantId, null, workstreamId, "Attention " + itemId, summary, category, severity, AttentionItemStatus.OPEN, AttentionItem.AssigneeKind.CAPABILITY, capabilityId, capabilityId, new AttentionSurfaceRef(workstreamId, "surface-" + workstreamId, "dashboard", itemId, AttentionService.OPEN_ATTENTION_ITEM_TOOL, capabilityId), List.of(new AttentionSourceRef("audit_trace", "trace-" + itemId, "Trace " + itemId, capabilityId, "trace-" + itemId, "corr-source")), null, now, now, now, null, null, null, null, "corr-source");
  }

  private MyAccountPersonalAttentionDigestTask digest(String digestTaskId, MyAccountPersonalAttentionDigestTask.Status status, String summary, String blockerCode) {
    var now = Instant.parse("2026-05-26T09:00:00Z");
    return new MyAccountPersonalAttentionDigestTask(digestTaskId, "agent-task-" + digestTaskId, "tenant-1", null, "membership-admin", "admin@example.test", "membership-admin", "idem-" + digestTaskId, 3, status, 100, summary, blockerCode, null, null, List.of("evidence-" + digestTaskId), List.of("section-" + digestTaskId), List.of("trace-" + digestTaskId), now, now);
  }

  private AccessReviewTask accessReviewTask(AccessReviewTask.Status status, String summary, String blockerCode) {
    var now = Instant.parse("2026-05-26T09:00:00Z");
    return new AccessReviewTask("access-review-task-1", "agent-task-access-review-1", "tenant-1", null, ScopeType.TENANT, "admin@example.test", "membership-admin", "idem-access-review", status, status == AccessReviewTask.Status.COMPLETED ? 100 : 40, summary, blockerCode, null, null, List.of("access-evidence-1"), List.of("access-rec-1"), List.of("trace-access-review-1"), now, now);
  }

  private PromptRiskReviewTask promptRiskTask(PromptRiskReviewTask.Status status, String summary, String blockerCode) {
    var now = Instant.parse("2026-05-26T09:00:00Z");
    return new PromptRiskReviewTask("prompt-risk-task-1", "agent-task-prompt-risk-1", "tenant-1", null, "agent-user-admin", "proposal-1", "admin@example.test", "membership-admin", "idem-prompt-risk", status, 30, summary, blockerCode, null, null, List.of(), List.of("prompt-evidence-1"), List.of("prompt-finding-1"), List.of("trace-prompt-risk-1"), now, now);
  }

  private AuditTraceSummaryTask auditTraceSummaryTask(AuditTraceSummaryTask.Status status, String summary, String blockerCode) {
    var now = Instant.parse("2026-05-26T09:00:00Z");
    return new AuditTraceSummaryTask("audit-summary-task-1", "agent-task-audit-summary-1", "tenant-1", null, "membership-admin", "admin@example.test", "membership-admin", "idem-audit-summary", now.minusSeconds(3600), now, List.of("admin_audit"), status, 75, summary, blockerCode, null, null, List.of("audit-evidence-1"), List.of("audit-finding-1"), List.of("trace-audit-summary-1"), now, now);
  }

  private GovernancePolicyImpactTask governancePolicyImpactTask(GovernancePolicyImpactTask.Status status, String summary, String blockerCode) {
    var now = Instant.parse("2026-05-26T09:00:00Z");
    return new GovernancePolicyImpactTask("governance-impact-task-1", "agent-task-governance-impact-1", "proposal-1", "policy-1", "tenant-1", null, "admin@example.test", "membership-admin", "idem-governance-impact", status, 100, summary, blockerCode, null, null, List.of("agent_admin.prompt_risk_review.read"), List.of("tool-boundary:agent-admin"), List.of("governance-evidence-1"), List.of("governance-finding-1"), List.of("trace-governance-impact-1"), now, now);
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
