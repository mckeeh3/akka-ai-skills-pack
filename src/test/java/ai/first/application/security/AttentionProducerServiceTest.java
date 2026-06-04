package ai.first.application.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.first.domain.security.AccessReviewTask;
import ai.first.domain.foundation.identity.Account;
import ai.first.domain.foundation.identity.AccountStatus;
import ai.first.domain.foundation.attention.AttentionCategory;
import ai.first.domain.foundation.attention.AttentionItemStatus;
import ai.first.domain.foundation.attention.AttentionSeverity;
import ai.first.domain.foundation.email.EmailDeliveryStatus;
import ai.first.domain.foundation.identity.FoundationRole;
import ai.first.domain.foundation.invitation.InvitationStatus;
import ai.first.domain.foundation.identity.Membership;
import ai.first.domain.foundation.identity.MembershipStatus;
import ai.first.domain.foundation.identity.ScopeType;
import ai.first.domain.foundation.identity.Tenant;
import ai.first.domain.foundation.identity.UserProfile;
import ai.first.domain.foundation.identity.UserSettings;
import ai.first.domain.foundation.identity.WorkosIdentity;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AttentionProducerServiceTest {
  private final Clock clock = Clock.fixed(Instant.parse("2026-05-28T12:00:00Z"), ZoneOffset.UTC);
  private LocalDemoIdentityRepository identityRepository;
  private LocalDemoInvitationRepository invitationRepository;
  private LocalDemoAttentionRepository attentionRepository;
  private LocalDemoGovernancePolicyRepository governanceRepository;
  private AuthContextResolver resolver;
  private InvitationService invitations;
  private GovernancePolicyService governance;
  private AttentionService attention;
  private AuthContextResolver.ResolvedMe tenantAdmin;

  @BeforeEach
  void setUp() {
    identityRepository = new LocalDemoIdentityRepository();
    invitationRepository = new LocalDemoInvitationRepository();
    attentionRepository = new LocalDemoAttentionRepository();
    governanceRepository = new LocalDemoGovernancePolicyRepository();
    resolver = new AuthContextResolver(identityRepository);
    var producers = new AttentionProducerService(attentionRepository, identityRepository, clock);
    invitations = new InvitationService(identityRepository, invitationRepository, clock, producers);
    governance = new GovernancePolicyService(governanceRepository, resolver, clock, producers);
    attention = new AttentionService(attentionRepository, resolver, clock);

    identityRepository.putTenant(new Tenant("tenant-1", "Tenant One", true));
    identityRepository.putTenant(new Tenant("tenant-2", "Tenant Two", true));
    seedAccount("admin@example.test", "membership-admin", "tenant-1", List.of(FoundationRole.TENANT_ADMIN, FoundationRole.AUDITOR));
    seedAccount("member@example.test", "membership-member", "tenant-1", List.of(FoundationRole.TENANT_EMPLOYEE));
    seedAccount("other@example.test", "membership-other", "tenant-2", List.of(FoundationRole.TENANT_ADMIN));
    tenantAdmin = resolver.resolveMe(new WorkosIdentity("workos-admin@example.test", "admin@example.test", "Admin"), "membership-admin", "corr-admin");
  }

  @Test
  void invitationDeliveryFailureProducesIdempotentAttentionAndDeliverySuccessResolvesIt() {
    var invite = invitations.createInvitation(tenantAdmin, inviteRequest("invite-producer", "failed@example.test"));

    var failed = invitations.recordDeliveryResult(invite.invitationId(), "delivery-1", false, null, "resend-http-401", "corr-delivery-failed");
    var duplicate = invitations.recordDeliveryResult(invite.invitationId(), "delivery-1-retry", false, null, "resend-http-401", "corr-delivery-failed-replay");

    var items = attention.listWorkstreamItems(tenantAdmin, "agent-user-admin", "corr-user-admin-attention");
    assertEquals(1, items.size());
    var item = items.get(0);
    assertEquals("attention:user-admin:invitation-delivery:" + failed.invitationId(), item.itemId());
    assertEquals(AttentionCategory.INVITATION_DELIVERY, item.category());
    assertEquals(AttentionSeverity.URGENT, item.severity());
    assertTrue(item.sourceRefs().stream().anyMatch(ref -> ref.refId().equals(failed.invitationId())));
    assertFalse(item.toString().contains("api_key="));
    assertEquals(2, duplicate.deliveryAttempts());

    var delivered = invitations.recordDeliveryResult(invite.invitationId(), "delivery-2", true, "captured-2", null, "corr-delivery-ok");
    assertEquals(EmailDeliveryStatus.CAPTURED, delivered.deliveryStatus());
    assertEquals(0, attention.listMyAccountItems(tenantAdmin, "corr-after-delivery-ok").totalAttentionCount());
    assertEquals(AttentionItemStatus.RESOLVED, attentionRepository.find("tenant-1", item.itemId()).orElseThrow().status());
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("ATTENTION_PRODUCER_UPSERT") && event.reasonCode().contains(AttentionProducerService.INVITATION_DELIVERY_PRODUCER_ID)));
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("ATTENTION_PRODUCER_RESOLVE") && event.correlationId().equals("corr-delivery-ok")));
  }

  @Test
  void timedInvitationDeliveryCheckUpdatesNearExpiryAndExpiresExpiredAttention() {
    var invite = invitations.createInvitation(tenantAdmin, inviteRequest("invite-timed", "timed@example.test"));
    invitations.recordDeliveryResult(invite.invitationId(), "delivery-1", false, null, "provider blocked token=abc123", "corr-timed-failed");

    var producers = new AttentionProducerService(attentionRepository, identityRepository, clock);
    var timed = producers.runInvitationDeliveryTimedCheck(invitationRepository, Duration.ofHours(2), "timer.invitation-delivery-expiry", "corr-timed-check");

    assertEquals(1, timed.size());
    var item = attentionRepository.find("tenant-1", "attention:user-admin:invitation-delivery:" + invite.invitationId()).orElseThrow();
    assertEquals(AttentionItemStatus.OPEN, item.status());
    assertEquals(AttentionSeverity.URGENT, item.severity());
    assertTrue(item.sourceRefs().stream().anyMatch(ref -> ref.kind().equals("timer")));
    assertFalse(item.toString().contains("token=abc123"));

    invitations.expire(invite.invitationId(), "tenant-1", null, "corr-expire");
    assertEquals(AttentionItemStatus.RESOLVED, attentionRepository.find("tenant-1", item.itemId()).orElseThrow().status());
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("ATTENTION_PRODUCER_TIMED_CHECK") && event.reasonCode().contains("timer.invitation-delivery-expiry")));
  }

  @Test
  void workerTaskBlockedStateProducesAttentionAndCancelResolvesWithoutFakeSuccess() {
    var producers = new AttentionProducerService(attentionRepository, identityRepository, clock);
    var accessReviews = new UserAdminAccessReviewService(new LocalDemoAccessReviewTaskRepository(), new UserAdminService(identityRepository, clock), clock, producers);

    var task = accessReviews.start(tenantAdmin, "idem-worker-attention", "corr-worker-start");

    var item = attentionRepository.find("tenant-1", "attention:worker-task:" + task.taskId() + ":task-state").orElseThrow();
    assertEquals(AttentionCategory.WORKFLOW_BLOCKED, item.category());
    assertEquals(AttentionSeverity.BLOCKED, item.severity());
    assertTrue(item.summary().contains("blocked_provider_or_runtime"));
    assertTrue(item.summary().contains("no fake model-backed success"));
    assertTrue(item.sourceRefs().stream().anyMatch(ref -> ref.kind().equals("autonomous_task") && ref.refId().equals(task.taskId())));

    var replay = producers.upsertWorkerTaskState(task, "timer.worker-task-staleness", "corr-worker-timer");
    assertEquals(item.itemId(), replay.itemId());
    assertTrue(replay.sourceRefs().stream().anyMatch(ref -> ref.kind().equals("timer")));

    accessReviews.cancel(tenantAdmin, task.taskId(), "cancel blocked worker", "corr-worker-cancel");
    assertEquals(AttentionItemStatus.RESOLVED, attentionRepository.find("tenant-1", item.itemId()).orElseThrow().status());
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("ATTENTION_PRODUCER_UPSERT") && event.reasonCode().contains(AttentionProducerService.WORKER_TASK_STATE_PRODUCER_ID)));
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("ATTENTION_PRODUCER_RESOLVE") && event.reasonCode().contains("cancelled")));
  }

  @Test
  void governanceSubmitProducesApprovalAttentionAndDecisionResolvesWithoutLeakingToUnauthorizedOrOtherTenant() {
    var draft = governance.draftProposal(tenantAdmin, Map.of("rationale", "tighten approval", "proposedContent", "require human approval"), "idem-draft", "corr-draft");
    var proposalId = draft.surface().data().get("proposalId").toString();

    var submitted = governance.submitProposal(tenantAdmin, Map.of("proposalId", proposalId), "idem-submit", "corr-submit");
    assertEquals("accepted", submitted.status());
    var itemId = "attention:governance:policy-approval:" + proposalId;
    var item = attentionRepository.find("tenant-1", itemId).orElseThrow();
    assertEquals(AttentionItemStatus.OPEN, item.status());
    assertEquals(AttentionCategory.GOVERNANCE_APPROVAL, item.category());
    assertTrue(item.sourceRefs().stream().anyMatch(ref -> ref.refId().equals(proposalId)));

    var member = resolver.resolveMe(new WorkosIdentity("workos-member@example.test", "member@example.test", "Member"), "membership-member", "corr-member");
    assertTrue(attention.listWorkstreamItems(member, "agent-governance-policy", "corr-member-list").isEmpty());
    var other = resolver.resolveMe(new WorkosIdentity("workos-other@example.test", "other@example.test", "Other"), "membership-other", "corr-other");
    assertTrue(attention.listWorkstreamItems(other, "agent-governance-policy", "corr-other-list").isEmpty());

    var decision = governance.decideProposal(tenantAdmin, Map.of("proposalId", proposalId, "decision", "approve", "rationale", "bounded approval"), "idem-decision", "corr-decision");
    assertEquals("accepted", decision.status());
    assertEquals(AttentionItemStatus.RESOLVED, attentionRepository.find("tenant-1", itemId).orElseThrow().status());
    assertEquals(0, attention.listMyAccountItems(tenantAdmin, "corr-after-decision").totalAttentionCount());
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("ATTENTION_PRODUCER_UPSERT") && event.reasonCode().contains(AttentionProducerService.GOVERNANCE_POLICY_APPROVAL_PRODUCER_ID)));
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("ATTENTION_PRODUCER_RESOLVE") && event.reasonCode().contains("approved")));
  }

  private InvitationService.CreateInvitationRequest inviteRequest(String key, String email) {
    return new InvitationService.CreateInvitationRequest(
        key,
        ScopeType.TENANT,
        "tenant-1",
        null,
        email,
        "Invited User",
        List.of(FoundationRole.TENANT_EMPLOYEE),
        clock.instant().plusSeconds(3600),
        "onboarding",
        "corr-" + key);
  }

  private void seedAccount(String email, String membershipId, String tenantId, List<FoundationRole> roles) {
    identityRepository.saveAccount(new Account(email, "workos-" + email, email, email, AccountStatus.ACTIVE, "LINKED"));
    identityRepository.putProfile(new UserProfile(email, email, email, null, null, null));
    identityRepository.putSettings(new UserSettings(email, UserSettings.ThemeId.AURORA_LIGHT));
    identityRepository.putMembership(new Membership(membershipId, email, ScopeType.TENANT, tenantId, null, roles, MembershipStatus.ACTIVE, false, null));
  }
}
