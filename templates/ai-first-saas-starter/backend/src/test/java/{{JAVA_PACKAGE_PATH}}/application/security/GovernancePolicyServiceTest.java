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
  void simulationDecisionActivationRollbackAreScopedIdempotentAndFailClosed() {
    var actor = resolver.resolveMe(identity(), "membership-admin", "corr-gov-lifecycle");
    var draft = service.draftProposal(actor, Map.of("rationale", "tighten approval copy", "proposedContent", "activate authority only after human approval"), "idem-gov-lifecycle-draft", "corr-gov-lifecycle-draft");
    var proposalId = draft.surface().data().get("proposalId").toString();
    var submitted = service.submitProposal(actor, Map.of("proposalId", proposalId), "idem-gov-lifecycle-submit", "corr-gov-lifecycle-submit");
    assertEquals("in_review", submitted.surface().data().get("state"));

    var simulation = service.simulateProposal(actor, Map.of("proposalId", proposalId), "corr-gov-lifecycle-sim");
    assertEquals("accepted", simulation.status());
    assertEquals("surface-governance-policy-simulation", simulation.surface().surfaceId());
    assertTrue(simulation.surface().toString().contains("advisory deterministic simulation"));
    assertTrue(simulation.surface().toString().contains("model cannot self-approve"));

    var activationBeforeDecision = service.activateProposal(actor, Map.of("proposalId", proposalId, "rollbackReference", "rollback metadata v1"), "idem-gov-activate-early", "corr-gov-activate-early");
    assertEquals("approval-required", activationBeforeDecision.status());
    assertTrue(activationBeforeDecision.surface().toString().contains("rollback metadata"));

    var decision = service.decideProposal(actor, Map.of("proposalId", proposalId, "decision", "approve", "rationale", "bounded human approval"), "idem-gov-decision", "corr-gov-decision");
    assertEquals("accepted", decision.status());
    assertEquals("approved", decision.surface().data().get("status"));
    assertTrue(decision.surface().toString().contains("governance.policy.approve"));

    var duplicateDecision = service.decideProposal(actor, Map.of("proposalId", proposalId, "decision", "reject", "rationale", "ignored replay"), "idem-gov-decision-2", "corr-gov-decision-replay");
    assertEquals("no-op", duplicateDecision.status());
    assertTrue(duplicateDecision.message().contains("idempotency/no-op"));

    var activation = service.activateProposal(actor, Map.of("proposalId", proposalId, "rollbackReference", "rollback metadata v1"), "idem-gov-activate", "corr-gov-activate");
    assertEquals("accepted", activation.status());
    assertTrue(activation.surface().toString().contains("activated-with-rollback-metadata"));
    assertFalse(activation.surface().toString().contains("api_key="));

    var duplicateActivation = service.activateProposal(actor, Map.of("proposalId", proposalId, "rollbackReference", "ignored"), "idem-gov-activate-2", "corr-gov-activate-replay");
    assertEquals("no-op", duplicateActivation.status());

    var rollback = service.rollbackProposal(actor, Map.of("proposalId", proposalId), "idem-gov-rollback", "corr-gov-rollback");
    assertEquals("accepted", rollback.status());
    assertEquals("rolled_back", rollback.surface().data().get("status"));
  }

  @Test
  void rejectionBlocksActivationAndRollbackRequiresActivatedProposal() {
    var actor = resolver.resolveMe(identity(), "membership-admin", "corr-gov-reject");
    var draft = service.draftProposal(actor, Map.of("rationale", "unsafe expansion", "proposedContent", "grant broad tool authority"), "idem-gov-reject-draft", "corr-gov-reject-draft");
    var proposalId = draft.surface().data().get("proposalId").toString();
    service.submitProposal(actor, Map.of("proposalId", proposalId), "idem-gov-reject-submit", "corr-gov-reject-submit");

    var rejection = service.decideProposal(actor, Map.of("proposalId", proposalId, "decision", "reject", "rationale", "too broad"), "idem-gov-reject", "corr-gov-reject");
    assertEquals("accepted", rejection.status());
    assertEquals("rejected", rejection.surface().data().get("status"));

    var activation = service.activateProposal(actor, Map.of("proposalId", proposalId, "rollbackReference", "rollback metadata v1"), "idem-gov-reject-activate", "corr-gov-reject-activate");
    assertEquals("approval-required", activation.status());
    assertTrue(activation.surface().toString().contains("Approved proposal"));

    var rollback = service.rollbackProposal(actor, Map.of("proposalId", proposalId), "idem-gov-reject-rollback", "corr-gov-reject-rollback");
    assertEquals("blocked-runtime", rollback.status());
    assertTrue(rollback.surface().toString().contains("blocked-runtime"));
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
