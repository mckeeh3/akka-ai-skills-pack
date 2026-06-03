package ai.first.application.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.first.domain.security.Account;
import ai.first.domain.security.AccountStatus;
import ai.first.domain.security.AttentionCategory;
import ai.first.domain.security.AttentionItem;
import ai.first.domain.security.AttentionItemStatus;
import ai.first.domain.security.AttentionSeverity;
import ai.first.domain.security.AttentionSourceRef;
import ai.first.domain.security.AttentionSurfaceRef;
import ai.first.domain.security.FoundationRole;
import ai.first.domain.security.Membership;
import ai.first.domain.security.MembershipStatus;
import ai.first.domain.security.ScopeType;
import ai.first.domain.security.Tenant;
import ai.first.domain.security.UserProfile;
import ai.first.domain.security.UserSettings;
import ai.first.domain.security.WorkosIdentity;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AttentionServiceTest {
  private LocalDemoIdentityRepository identityRepository;
  private LocalDemoAttentionRepository attentionRepository;
  private AuthContextResolver resolver;
  private AttentionService service;
  private Clock clock;

  @BeforeEach
  void setUp() {
    identityRepository = new LocalDemoIdentityRepository();
    attentionRepository = new LocalDemoAttentionRepository();
    resolver = new AuthContextResolver(identityRepository);
    clock = Clock.fixed(Instant.parse("2026-05-25T10:00:00Z"), ZoneOffset.UTC);
    service = new AttentionService(attentionRepository, resolver, clock);

    identityRepository.putTenant(new Tenant("tenant-1", "Tenant One", true));
    identityRepository.putTenant(new Tenant("tenant-2", "Tenant Two", true));
    addAccount("admin@example.test", "membership-admin", "tenant-1", List.of(FoundationRole.TENANT_ADMIN, FoundationRole.AUDITOR));
    addAccount("member@example.test", "membership-member", "tenant-1", List.of(FoundationRole.TENANT_EMPLOYEE));
    addAccount("other@example.test", "membership-other", "tenant-2", List.of(FoundationRole.TENANT_ADMIN));
  }

  @Test
  void authorizedReadsReturnWorkstreamMyAccountAndRailProjectionsFromSharedBackendState() {
    var actor = actor("admin@example.test", "membership-admin", "corr-read");
    service.upsertItem(actor, item("attention-agent", "tenant-1", "agent-agent-admin", "agent_admin.list_definitions", AttentionCategory.PROVIDER_READINESS, AttentionSeverity.BLOCKED), "corr-upsert-agent");
    service.upsertItem(actor, item("attention-audit", "tenant-1", "agent-audit-trace", "audit.trace.read", AttentionCategory.AUDIT_FAILURE_EVIDENCE, AttentionSeverity.WARNING), "corr-upsert-audit");

    var workstream = service.listWorkstreamItems(actor, "agent-agent-admin", "corr-workstream");
    var myAccount = service.listMyAccountItems(actor, "corr-my-account");
    var rail = service.listRailSummaries(actor, "corr-rail");

    assertEquals(List.of("attention-agent"), workstream.stream().map(AttentionItem::itemId).toList());
    assertEquals(2, myAccount.totalAttentionCount());
    assertEquals(AttentionSeverity.BLOCKED, myAccount.highestSeverity());
    assertTrue(myAccount.workstreams().stream().anyMatch(summary -> summary.workstreamId().equals("agent-agent-admin") && summary.attentionCount() == 1));
    assertTrue(rail.stream().anyMatch(summary -> summary.workstreamId().equals("agent-audit-trace") && summary.items().isEmpty()));
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("ATTENTION_LIST_MY_ACCOUNT_ITEMS") && event.correlationId().equals("corr-my-account")));
  }

  @Test
  void hiddenWorkstreamAndTenantIsolationDoNotLeakNamesCountsOrItems() {
    var admin = actor("admin@example.test", "membership-admin", "corr-admin");
    service.upsertItem(admin, item("attention-agent", "tenant-1", "agent-agent-admin", "agent_admin.list_definitions", AttentionCategory.PROVIDER_READINESS, AttentionSeverity.BLOCKED), "corr-upsert-agent");
    var otherTenant = actor("other@example.test", "membership-other", "corr-other");
    service.upsertItem(otherTenant, item("attention-other", "tenant-2", "agent-agent-admin", "agent_admin.list_definitions", AttentionCategory.PROVIDER_READINESS, AttentionSeverity.BLOCKED), "corr-upsert-other");

    var member = actor("member@example.test", "membership-member", "corr-member");
    assertTrue(service.listWorkstreamItems(member, "agent-agent-admin", "corr-member-list").isEmpty());
    assertEquals(0, service.listMyAccountItems(member, "corr-member-my-account").totalAttentionCount());
    assertTrue(service.listRailSummaries(member, "corr-member-rail").isEmpty());

    var opened = service.openAttentionItem(member, "attention-agent", "corr-member-open");
    assertEquals("not_found_or_redacted", opened.status());
    assertFalse(opened.toString().contains("agent-agent-admin"));
    assertTrue(service.listWorkstreamItems(admin, "agent-agent-admin", "corr-admin-list").stream().noneMatch(item -> item.tenantId().equals("tenant-2")));
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("ATTENTION_OPEN_ITEM") && event.result().name().equals("DENIED") && event.correlationId().equals("corr-member-open")));
  }

  @Test
  void lifecycleOperationsAreAuthorizedIdempotentAndAudited() {
    var actor = actor("admin@example.test", "membership-admin", "corr-lifecycle");
    service.upsertItem(actor, item("attention-governance", "tenant-1", "agent-governance-policy", "governance.policy.read", AttentionCategory.GOVERNANCE_APPROVAL, AttentionSeverity.URGENT), "corr-upsert-governance");

    var acknowledged = service.acknowledge(actor, "attention-governance", "corr-ack");
    var acknowledgedAgain = service.acknowledge(actor, "attention-governance", "corr-ack-duplicate");
    var resolved = service.resolve(actor, "attention-governance", "corr-resolve");
    var resolvedAgain = service.resolve(actor, "attention-governance", "corr-resolve-duplicate");

    assertEquals(AttentionItemStatus.ACKNOWLEDGED, acknowledged.status());
    assertEquals(acknowledged, acknowledgedAgain);
    assertEquals(AttentionItemStatus.RESOLVED, resolved.status());
    assertEquals(resolved, resolvedAgain);
    assertEquals(0, service.listMyAccountItems(actor, "corr-after-resolve").totalAttentionCount());
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("ATTENTION_ACKNOWLEDGE_ITEM") && event.correlationId().equals("corr-ack")));
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("ATTENTION_RESOLVE_ITEM") && event.reasonCode().contains("no_op") && event.correlationId().equals("corr-resolve-duplicate")));
  }

  @Test
  void producerUpsertRequiresTenantAndSourceCapability() {
    var member = actor("member@example.test", "membership-member", "corr-member");
    var missingCapability = assertThrows(AuthorizationException.class, () -> service.upsertItem(member, item("attention-agent", "tenant-1", "agent-agent-admin", "agent_admin.list_definitions", AttentionCategory.PROVIDER_READINESS, AttentionSeverity.BLOCKED), "corr-upsert-denied"));
    assertTrue(missingCapability.reasonCode().contains("missing-capability:agent_admin.list_definitions"));

    var admin = actor("admin@example.test", "membership-admin", "corr-admin");
    var tenantMismatch = assertThrows(AuthorizationException.class, () -> service.upsertItem(admin, item("attention-other", "tenant-2", "agent-agent-admin", "agent_admin.list_definitions", AttentionCategory.PROVIDER_READINESS, AttentionSeverity.BLOCKED), "corr-tenant-denied"));
    assertEquals("tenant-mismatch", tenantMismatch.reasonCode());
  }

  private AttentionItem item(String itemId, String tenantId, String workstreamId, String capabilityId, AttentionCategory category, AttentionSeverity severity) {
    var now = Instant.parse("2026-05-25T09:00:00Z");
    return new AttentionItem(
        itemId,
        tenantId,
        null,
        workstreamId,
        "Attention " + itemId,
        "Safe summary for " + itemId,
        category,
        severity,
        AttentionItemStatus.OPEN,
        AttentionItem.AssigneeKind.CAPABILITY,
        capabilityId,
        capabilityId,
        new AttentionSurfaceRef(workstreamId, "surface-" + workstreamId, "dashboard", itemId, "open_attention_item", capabilityId),
        List.of(new AttentionSourceRef("audit_trace", "trace-" + itemId, "Trace " + itemId, capabilityId, "trace-" + itemId, "corr-source")),
        null,
        now,
        now,
        now,
        null,
        null,
        null,
        null,
        "corr-source");
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
