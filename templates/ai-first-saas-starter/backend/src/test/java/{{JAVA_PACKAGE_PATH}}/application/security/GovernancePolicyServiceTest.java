package {{JAVA_BASE_PACKAGE}}.application.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GovernancePolicyServiceTest {
  private InMemoryIdentityRepository identityRepository;
  private AuthContextResolver resolver;
  private GovernancePolicyService service;

  @BeforeEach
  void setUp() {
    identityRepository = new InMemoryIdentityRepository();
    resolver = new AuthContextResolver(identityRepository);
    service = new GovernancePolicyService(new InMemoryGovernancePolicyRepository(), resolver, Clock.systemUTC());

    identityRepository.putTenant(new Tenant("tenant-1", "Tenant One", true));
    identityRepository.saveAccount(new Account("admin@example.test", "workos-admin", "admin@example.test", "admin@example.test", AccountStatus.ACTIVE, "LINKED"));
    identityRepository.putProfile(new UserProfile("admin@example.test", "admin@example.test", "Tenant Admin", "Tenant", "Admin", null));
    identityRepository.putSettings(new UserSettings("admin@example.test", UserSettings.UiMode.LIGHT));
    identityRepository.putMembership(new Membership("membership-admin", "admin@example.test", ScopeType.TENANT, "tenant-1", null, List.of(FoundationRole.TENANT_ADMIN), MembershipStatus.ACTIVE, false, null));

    identityRepository.saveAccount(new Account("member@example.test", "workos-member", "member@example.test", "member@example.test", AccountStatus.ACTIVE, "LINKED"));
    identityRepository.putProfile(new UserProfile("member@example.test", "member@example.test", "Member User", "Member", "User", null));
    identityRepository.putSettings(new UserSettings("member@example.test", UserSettings.UiMode.LIGHT));
    identityRepository.putMembership(new Membership("membership-member", "member@example.test", ScopeType.TENANT, "tenant-1", null, List.of(FoundationRole.TENANT_EMPLOYEE), MembershipStatus.ACTIVE, false, null));
  }

  @Test
  void dashboardInventoryAndDetailAreTenantScopedRedactedAndTraced() {
    var actor = resolver.resolveMe(identity(), "membership-admin", "corr-gov-read");

    var dashboard = service.dashboard(actor, "corr-gov-dashboard");
    assertEquals("surface-governance-policy-dashboard", dashboard.surfaceId());
    assertEquals("governance.policy.dashboard.v1", dashboard.data().get("surfaceContract"));
    assertTrue(dashboard.toString().contains("GovernancePolicyService"));
    assertTrue(dashboard.toString().contains("governance.policy.proposal.draft"));
    assertFalse(dashboard.toString().contains("api_key="));

    var inventory = service.inventory(actor, "corr-gov-inventory");
    assertEquals("list-search", inventory.surfaceType());
    assertTrue(inventory.toString().contains("ToolPermissionBoundary"));
    assertTrue(inventory.toString().contains("tenant-1"));

    var detail = service.detail(actor, Map.of("policyId", "policy-human-approval"), "corr-gov-detail");
    assertEquals("detail-edit", detail.surfaceType());
    assertTrue(detail.toString().contains("backend AuthContext"));
    assertTrue(detail.traceIds().stream().anyMatch(trace -> trace.contains("trace-governance-policy")));
  }

  @Test
  void proposalDraftSubmitReadLifecycleIsIdempotentAndDoesNotMutateAuthority() {
    var actor = resolver.resolveMe(identity(), "membership-admin", "corr-gov-proposal");

    var draft = service.draftProposal(actor, Map.of("rationale", "tighten approval copy", "proposedContent", "no direct mutation; require human approval"), "idem-gov-draft", "corr-gov-draft");
    assertEquals("accepted", draft.status());
    assertEquals("surface-governance-policy-proposal", draft.surface().surfaceId());
    assertTrue(draft.surface().toString().contains("proposal lifecycle"));
    assertTrue(draft.surface().toString().contains("No authority changes before approval"));

    var duplicateDraft = service.draftProposal(actor, Map.of("rationale", "ignored duplicate"), "idem-gov-draft", "corr-gov-draft-replay");
    assertEquals("no-op", duplicateDraft.status());
    assertEquals(draft.surface().data().get("proposalId"), duplicateDraft.surface().data().get("proposalId"));

    var submit = service.submitProposal(actor, Map.of("proposalId", draft.surface().data().get("proposalId")), "idem-gov-submit", "corr-gov-submit");
    assertEquals("accepted", submit.status());
    assertEquals("in_review", submit.surface().data().get("state"));

    var duplicateSubmit = service.submitProposal(actor, Map.of("proposalId", draft.surface().data().get("proposalId")), "idem-gov-submit-2", "corr-gov-submit-replay");
    assertEquals("no-op", duplicateSubmit.status());
    assertEquals("in_review", duplicateSubmit.surface().data().get("state"));

    var read = service.readProposal(actor, Map.of("proposalId", draft.surface().data().get("proposalId")), "corr-gov-proposal-read");
    assertEquals("governance.policy.proposal.v1", read.data().get("surfaceContract"));
    assertFalse(read.toString().contains("api_key="));
    assertFalse(read.toString().contains("sk-secret"));
  }

  @Test
  void memberAndCrossTenantInputsAreDeniedBeforeEvidenceLeakage() {
    var member = resolver.resolveMe(memberIdentity(), "membership-member", "corr-member");
    var denied = assertThrows(AuthorizationException.class, () -> service.inventory(member, "corr-member-gov"));
    assertTrue(denied.reasonCode().contains("missing-capability:governance.policy.read"));

    var actor = resolver.resolveMe(identity(), "membership-admin", "corr-admin");
    var crossTenant = assertThrows(AuthorizationException.class, () -> service.detail(actor, Map.of("tenantId", "tenant-other"), "corr-cross"));
    assertEquals("GOVERNANCE_POLICY_TENANT_FORBIDDEN", crossTenant.reasonCode());
  }

  private WorkosIdentity identity() {
    return new WorkosIdentity("workos-admin", "admin@example.test", "Tenant Admin");
  }

  private WorkosIdentity memberIdentity() {
    return new WorkosIdentity("workos-member", "member@example.test", "Member User");
  }
}
