package ai.first.application.coreapp.agentadmin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.first.application.coreapp.agentadmin.AgentAdminDocAdministrationService.AgentDocKind;
import ai.first.application.coreapp.agentadmin.AgentAdminDocAdministrationService.AgentListRequest;
import ai.first.application.coreapp.agentadmin.AgentAdminDocAdministrationService.CreateReferenceDocRequest;
import ai.first.application.coreapp.agentadmin.AgentAdminDocAdministrationService.CreateSkillRequest;
import ai.first.application.coreapp.agentadmin.AgentAdminDocAdministrationService.DeleteSkillRequest;
import ai.first.application.coreapp.agentadmin.AgentAdminDocAdministrationService.DocumentVersionRequest;
import ai.first.application.coreapp.agentadmin.AgentAdminDocAdministrationService.EditSessionCommandRequest;
import ai.first.application.coreapp.agentadmin.AgentAdminDocAdministrationService.EditSessionStatus;
import ai.first.application.coreapp.agentadmin.AgentAdminDocAdministrationService.RestoreVersionRequest;
import ai.first.application.coreapp.agentadmin.AgentAdminDocAdministrationService.ReviseEditSessionRequest;
import ai.first.application.coreapp.agentadmin.AgentAdminDocAdministrationService.StartEditSessionRequest;
import ai.first.application.foundation.agent.AgentBehaviorSeedLoader;
import ai.first.application.foundation.agent.AgentRuntimeService;
import ai.first.application.foundation.agent.InMemoryTestAgentBehaviorRepository;
import ai.first.application.foundation.agent.InMemoryTestAgentRuntimeTraceSink;
import ai.first.application.foundation.agent.OpenAiModelProviderClient;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.AuthorizationException;
import ai.first.application.foundation.identity.InMemoryTestIdentityRepository;
import ai.first.application.foundation.workstream.WorkstreamEventPublisher;
import ai.first.domain.foundation.agent.AgentRuntimeTrace;
import ai.first.domain.foundation.agent.ReferenceDocument;
import ai.first.domain.foundation.identity.Account;
import ai.first.domain.foundation.identity.AccountStatus;
import ai.first.domain.foundation.identity.Customer;
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

class AgentAdminDocAdministrationServiceTest {
  private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-06-24T12:00:00Z"), ZoneOffset.UTC);

  private InMemoryTestIdentityRepository identityRepository;
  private InMemoryTestAgentBehaviorRepository agentRepository;
  private AuthContextResolver resolver;
  private AgentAdminDocAdministrationService service;

  @BeforeEach
  void setUp() {
    identityRepository = new InMemoryTestIdentityRepository();
    identityRepository.putTenant(new Tenant("tenant-1", "Tenant One", true));
    identityRepository.putCustomer(new Customer("tenant-1", "customer-1", "Customer One", true));
    seedActor("owner@example.test", "membership-owner", ScopeType.SAAS_OWNER, null, null, FoundationRole.SAAS_OWNER_ADMIN);
    seedActor("tenant-admin@example.test", "membership-tenant-admin", ScopeType.TENANT, "tenant-1", null, FoundationRole.TENANT_ADMIN);
    seedActor("customer-admin@example.test", "membership-customer-admin", ScopeType.CUSTOMER, "tenant-1", "customer-1", FoundationRole.CUSTOMER_ADMIN);
    seedActor("member@example.test", "membership-member", ScopeType.TENANT, "tenant-1", null, FoundationRole.TENANT_EMPLOYEE);
    resolver = new AuthContextResolver(identityRepository);
    agentRepository = new InMemoryTestAgentBehaviorRepository();
    new AgentBehaviorSeedLoader(agentRepository, CLOCK)
        .importStarterDefaults(WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID, "owner@example.test", "corr-seed");
    service = new AgentAdminDocAdministrationService(agentRepository, resolver, CLOCK);
  }

  @Test
  void saasOwnerListsFiltersAndReadsAgentDocContracts() {
    var owner = actor("owner@example.test", "membership-owner");

    var allAgents = service.listAgents(owner, new AgentListRequest(null, null), "corr-list");
    assertFalse(allAgents.rows().isEmpty());
    assertEquals(allAgents.rows().size(), allAgents.filteredCount());

    var filtered = service.listAgents(owner, new AgentListRequest("User Admin", "user-admin"), "corr-filter");
    assertEquals(1, filtered.rows().size());
    assertEquals(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, filtered.rows().get(0).agentDefinitionId());

    var detail = service.agentDetail(owner, AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, "corr-detail");
    assertEquals("User Admin Agent", detail.agentName());
    assertNotNull(detail.prompt());
    assertFalse(detail.skills().isEmpty());
    assertFalse(detail.referenceDocs().isEmpty());
    assertFalse(detail.traceLinks().isEmpty());

    var prompt = service.readDocumentVersion(owner, new DocumentVersionRequest(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, AgentDocKind.PROMPT, detail.prompt().documentId(), null), "corr-prompt");
    assertEquals(1, prompt.version());
    assertTrue(prompt.currentVersion());
    assertTrue(prompt.editable());
    assertTrue(prompt.contentBody().contains("User Admin"));

    var skill = service.readDocumentVersion(owner, new DocumentVersionRequest(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, AgentDocKind.SKILL, AgentBehaviorSeedLoader.ACCESS_REVIEW_SKILL_DOC_ID, 1), "corr-skill");
    assertEquals(AgentDocKind.SKILL, skill.kind());
    assertFalse(skill.contentBody().isBlank());

    var history = service.versionHistory(owner, new DocumentVersionRequest(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, AgentDocKind.PROMPT, detail.prompt().documentId(), null), "corr-history");
    assertEquals(List.of(1), history.rows().stream().map(row -> row.version()).toList());

    var diff = service.adjacentDiff(owner, new DocumentVersionRequest(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, AgentDocKind.PROMPT, detail.prompt().documentId(), 1), "corr-diff");
    assertEquals(AgentAdminDocAdministrationService.DiffStatus.NO_PRIOR_VERSION, diff.status());
  }

  @Test
  void runtimeDocReadTraceRowsExposeMetadataWithoutFullContent() {
    var owner = actor("owner@example.test", "membership-owner");
    var traceSink = new InMemoryTestAgentRuntimeTraceSink();
    var runtimeService = new AgentRuntimeService(agentRepository, resolver, CLOCK, new OpenAiModelProviderClient(), traceSink);
    var traceService = new AgentAdminDocAdministrationService(agentRepository, resolver, CLOCK, new FailClosedAgentAdminDocEditingRuntime(), traceSink);

    var skillRead = runtimeService.readSkill(new AgentRuntimeService.SkillReadRequest(
        WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID,
        AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID,
        owner.selectedContext(),
        "runtime",
        "saas_owner.admin.manage",
        "corr-runtime-skill-read",
        "ua.access-review-triage.v1"));
    var referenceRead = runtimeService.readReferenceDoc(new AgentRuntimeService.ReferenceReadRequest(
        WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID,
        AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID,
        owner.selectedContext(),
        "runtime",
        "saas_owner.admin.manage",
        "corr-runtime-reference-read",
        "ua.access-review-policy.v1",
        "consult"));

    assertEquals(AgentRuntimeTrace.Decision.ALLOWED, skillRead.decision());
    assertEquals(AgentRuntimeTrace.Decision.ALLOWED, referenceRead.decision());
    var allRows = traceService.runtimeDocReadTraces(owner, new AgentAdminDocAdministrationService.RuntimeDocReadTraceQuery(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, null, null, null), "corr-trace-query");
    assertEquals(2, allRows.rows().size());
    assertTrue(allRows.rows().stream().anyMatch(row -> row.agentName().equals("User Admin Agent") && row.documentType().equals("skill") && row.documentName().equals("Access Review Triage") && row.requestSessionId().equals("corr-runtime-skill-read") && row.userCustomerContext().contains("accountId=owner@example.test")));
    assertTrue(allRows.rows().stream().anyMatch(row -> row.documentType().equals("reference") && row.documentName().equals("Access Review Policy") && row.requestSessionId().equals("corr-runtime-reference-read")));
    assertFalse(allRows.rows().toString().contains(skillRead.content()));
    assertFalse(allRows.rows().toString().contains(referenceRead.content()));

    var filtered = traceService.runtimeDocReadTraces(owner, new AgentAdminDocAdministrationService.RuntimeDocReadTraceQuery(null, "Access Review Policy", null, null), "corr-trace-filter");
    assertEquals(1, filtered.rows().size());
    assertEquals("reference", filtered.rows().get(0).documentType());
  }

  @Test
  void editSessionSaveCancelAndRestoreContractsAreExposedAtServiceBoundary() {
    var owner = actor("owner@example.test", "membership-owner");
    var docRequest = new DocumentVersionRequest(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, AgentDocKind.PROMPT, AgentBehaviorSeedLoader.USER_ADMIN_PROMPT_ID, null);
    var before = service.readDocumentVersion(owner, docRequest, "corr-before");

    var session = service.startEditSession(owner, new StartEditSessionRequest(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, AgentDocKind.PROMPT, AgentBehaviorSeedLoader.USER_ADMIN_PROMPT_ID, "Make the tone more concise."), "corr-start");
    assertEquals(EditSessionStatus.DRAFTING, session.status());
    assertEquals(before.version(), session.baseVersion());

    var revised = service.reviseEditSession(owner, new ReviseEditSessionRequest(session.sessionId(), "Keep the same headings.", before.contentBody() + "\n\nConcise behavior note.", "Added concise behavior note.", List.of("Advisory warning only")), "corr-revise");
    assertEquals(EditSessionStatus.PROPOSAL_READY, revised.status());
    assertEquals(2, revised.instructions().size());

    var saved = service.saveEditSession(owner, new EditSessionCommandRequest(revised.sessionId()), "corr-save");
    assertEquals(EditSessionStatus.SAVED, saved.session().status());
    assertEquals(2, saved.savedVersion());

    var historical = service.readDocumentVersion(owner, new DocumentVersionRequest(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, AgentDocKind.PROMPT, AgentBehaviorSeedLoader.USER_ADMIN_PROMPT_ID, 1), "corr-historical");
    assertFalse(historical.currentVersion());
    assertFalse(historical.editable());

    var diff = service.adjacentDiff(owner, new DocumentVersionRequest(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, AgentDocKind.PROMPT, AgentBehaviorSeedLoader.USER_ADMIN_PROMPT_ID, 2), "corr-diff-v2");
    assertEquals(AgentAdminDocAdministrationService.DiffStatus.READY, diff.status());
    assertTrue(diff.unifiedDiff().contains("Concise behavior note."));

    var restored = service.restoreVersion(owner, new RestoreVersionRequest(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, AgentDocKind.PROMPT, AgentBehaviorSeedLoader.USER_ADMIN_PROMPT_ID, 1), "corr-restore");
    assertEquals(3, restored.newCurrentVersion());
    assertEquals("Restored from version 1", restored.summary());
    var restoredCurrent = service.readDocumentVersion(owner, docRequest, "corr-restored-current");
    assertEquals(3, restoredCurrent.version());
    assertEquals("Restored from version 1", restoredCurrent.editSessionTranscriptSummary());

    var cancelSession = service.startEditSession(owner, new StartEditSessionRequest(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, AgentDocKind.SKILL, AgentBehaviorSeedLoader.ACCESS_REVIEW_SKILL_DOC_ID, "Try an alternate skill wording."), "corr-start-cancel");
    var cancelled = service.cancelEditSession(owner, new EditSessionCommandRequest(cancelSession.sessionId()), "corr-cancel");
    assertEquals(EditSessionStatus.CANCELLED, cancelled.status());
  }

  @Test
  void staleEditSessionSaveIsRejectedAfterCurrentVersionChanges() {
    var owner = actor("owner@example.test", "membership-owner");
    var request = new StartEditSessionRequest(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, AgentDocKind.PROMPT, AgentBehaviorSeedLoader.USER_ADMIN_PROMPT_ID, "First edit.");
    var stale = service.startEditSession(owner, request, "corr-stale-start");
    var winning = service.startEditSession(owner, request, "corr-winning-start");
    var before = service.readDocumentVersion(owner, new DocumentVersionRequest(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, AgentDocKind.PROMPT, AgentBehaviorSeedLoader.USER_ADMIN_PROMPT_ID, null), "corr-stale-before");
    service.reviseEditSession(owner, new ReviseEditSessionRequest(winning.sessionId(), "Ship winning edit.", before.contentBody() + "\n\nWinning edit.", "Winning edit", List.of()), "corr-winning-revise");
    service.saveEditSession(owner, new EditSessionCommandRequest(winning.sessionId()), "corr-winning-save");
    service.reviseEditSession(owner, new ReviseEditSessionRequest(stale.sessionId(), "Ship stale edit.", before.contentBody() + "\n\nStale edit.", "Stale edit", List.of()), "corr-stale-revise");

    var failure = assertThrows(AuthorizationException.class, () -> service.saveEditSession(owner, new EditSessionCommandRequest(stale.sessionId()), "corr-stale-save"));

    assertEquals(409, failure.httpStatus());
    assertEquals("edit-session-base-version-stale", failure.reasonCode());
  }

  @Test
  void skillAndReferenceLifecycleSupportsPermanentDeletionCascade() {
    var owner = actor("owner@example.test", "membership-owner");
    var skill = service.createSkill(owner, new CreateSkillRequest(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, "skill-test-coaching", "test.coaching.v1", "Test Coaching", "Coach tests", "Use for test guidance", "# Test Coaching\nUse examples.", "Create test skill"), "corr-create-skill");
    var reference = service.createReferenceDoc(owner, new CreateReferenceDocRequest(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, skill.skillDocumentId(), "ref-test-coaching", "test.coaching.reference.v1", "Test Coaching Reference", "Reference for test coaching", "Consult for test coaching details", ReferenceDocument.ReferenceType.PROCESS, "# Test Reference\nDetails.", "Create test reference"), "corr-create-reference");

    var skillDoc = service.readDocumentVersion(owner, new DocumentVersionRequest(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, AgentDocKind.SKILL, skill.skillDocumentId(), null), "corr-read-created-skill");
    var referenceDoc = service.readDocumentVersion(owner, new DocumentVersionRequest(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, AgentDocKind.REFERENCE, reference.referenceDocumentId(), null), "corr-read-created-reference");
    assertEquals(1, skillDoc.version());
    assertEquals(1, referenceDoc.version());

    service.deleteSkill(owner, new DeleteSkillRequest(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, skill.skillDocumentId(), "Permanently delete Test Coaching and its reference docs"), "corr-delete-skill");

    assertThrows(AuthorizationException.class, () -> service.readDocumentVersion(owner, new DocumentVersionRequest(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, AgentDocKind.SKILL, skill.skillDocumentId(), null), "corr-read-deleted-skill"));
    assertThrows(AuthorizationException.class, () -> service.readDocumentVersion(owner, new DocumentVersionRequest(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, AgentDocKind.REFERENCE, reference.referenceDocumentId(), null), "corr-read-deleted-reference"));
    assertThrows(AuthorizationException.class, () -> service.restoreVersion(owner, new RestoreVersionRequest(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, AgentDocKind.REFERENCE, reference.referenceDocumentId(), 1), "corr-restore-deleted-reference"));
  }

  @Test
  void saasOwnerCanUpdateAgentNameAndPurposeButTenantAndCustomerAdminsAreDenied() {
    var owner = actor("owner@example.test", "membership-owner");
    var updated = service.updateAgentProfile(owner, new AgentAdminDocAdministrationService.AgentProfileUpdateRequest(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, "User Admin Agent Updated", "Updated purpose"), "corr-update");
    assertEquals("User Admin Agent Updated", updated.row().agentName());
    assertEquals("Updated purpose", service.agentDetail(owner, AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, "corr-detail-updated").purpose());

    assertAccessDenied(actor("tenant-admin@example.test", "membership-tenant-admin"));
    assertAccessDenied(actor("customer-admin@example.test", "membership-customer-admin"));
    assertAccessDenied(actor("member@example.test", "membership-member"));
  }

  private void assertAccessDenied(AuthContextResolver.ResolvedMe deniedActor) {
    var denied = assertThrows(AuthorizationException.class, () ->
        service.listAgents(deniedActor, new AgentListRequest(null, null), "corr-denied-" + deniedActor.account().accountId()));
    assertEquals("agent-admin-requires-saas-owner-admin", denied.reasonCode());
  }

  private AuthContextResolver.ResolvedMe actor(String email, String membershipId) {
    return resolver.resolveMe(new WorkosIdentity("workos-" + email, email, email), membershipId, "corr-resolve-" + email);
  }

  private void seedActor(String email, String membershipId, ScopeType scopeType, String tenantId, String customerId, FoundationRole role) {
    identityRepository.saveAccount(new Account(email, null, email, email, AccountStatus.ACTIVE, "LINKED"));
    identityRepository.putProfile(new UserProfile(email, email, email, email, email, null));
    identityRepository.putSettings(new UserSettings(email, UserSettings.ThemeId.AURORA_LIGHT));
    identityRepository.putMembership(new Membership(membershipId, email, scopeType, tenantId, customerId, List.of(role), MembershipStatus.ACTIVE, false, null));
  }
}
