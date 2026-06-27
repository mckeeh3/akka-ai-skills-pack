package ai.first.application.coreapp.agentadmin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.first.application.coreapp.agentadmin.AgentAdminDocAdministrationService.AgentDocKind;
import ai.first.application.coreapp.agentadmin.AgentAdminDocAdministrationService.ActivateProposalRequest;
import ai.first.application.coreapp.agentadmin.AgentAdminDocAdministrationService.AgentListRequest;
import ai.first.application.coreapp.agentadmin.AgentAdminDocAdministrationService.BehaviorProposalStatus;
import ai.first.application.coreapp.agentadmin.AgentAdminDocAdministrationService.CreateReferenceDocRequest;
import ai.first.application.coreapp.agentadmin.AgentAdminDocAdministrationService.CreateSkillRequest;
import ai.first.application.coreapp.agentadmin.AgentAdminDocAdministrationService.DeleteSkillRequest;
import ai.first.application.coreapp.agentadmin.AgentAdminDocAdministrationService.DocumentVersionRequest;
import ai.first.application.coreapp.agentadmin.AgentAdminDocAdministrationService.EditSessionCommandRequest;
import ai.first.application.coreapp.agentadmin.AgentAdminDocAdministrationService.EditSessionStatus;
import ai.first.application.coreapp.agentadmin.AgentAdminDocAdministrationService.RiskClassification;
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
import ai.first.domain.foundation.agent.AgentLifecycleStatus;
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
  void editSessionSaveDraftActivationCancelAndRestoreContractsAreExposedAtServiceBoundary() {
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
    assertEquals(BehaviorProposalStatus.DRAFT, saved.proposal().status());
    assertEquals(RiskClassification.LOW, saved.proposal().riskClassification());
    assertFalse(saved.proposal().authorityExpansion().detected());
    assertEquals(before.version(), saved.savedVersion());
    assertEquals(before.version() + 1, saved.proposal().proposalVersion());

    var stillCurrent = service.readDocumentVersion(owner, docRequest, "corr-after-save-draft");
    assertEquals(before.version(), stillCurrent.version());
    assertEquals(before.contentBody(), stillCurrent.contentBody());
    var historyAfterDraft = service.versionHistory(owner, docRequest, "corr-history-after-draft");
    assertEquals(List.of(1), historyAfterDraft.rows().stream().map(row -> row.version()).toList());

    var activated = service.activateProposal(owner, new ActivateProposalRequest(saved.proposal().proposalId(), "Activate low-risk copy edit"), "corr-activate");
    assertEquals(BehaviorProposalStatus.ACTIVATED, activated.proposal().status());
    assertEquals(2, activated.newCurrentVersion());

    var historical = service.readDocumentVersion(owner, new DocumentVersionRequest(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, AgentDocKind.PROMPT, AgentBehaviorSeedLoader.USER_ADMIN_PROMPT_ID, 1), "corr-historical");
    assertFalse(historical.currentVersion());
    assertFalse(historical.editable());

    var diff = service.adjacentDiff(owner, new DocumentVersionRequest(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, AgentDocKind.PROMPT, AgentBehaviorSeedLoader.USER_ADMIN_PROMPT_ID, 2), "corr-diff-v2");
    assertEquals(AgentAdminDocAdministrationService.DiffStatus.READY, diff.status());
    assertTrue(diff.unifiedDiff().contains("Concise behavior note."));

    var restored = service.restoreVersion(owner, new RestoreVersionRequest(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, AgentDocKind.PROMPT, AgentBehaviorSeedLoader.USER_ADMIN_PROMPT_ID, 1), "corr-restore");
    assertEquals(2, restored.newCurrentVersion());
    assertNotNull(restored.proposalId());
    assertEquals("Restored from version 1", restored.summary());
    var unchangedAfterRestoreProposal = service.readDocumentVersion(owner, docRequest, "corr-restored-current");
    assertEquals(2, unchangedAfterRestoreProposal.version());
    assertTrue(unchangedAfterRestoreProposal.contentBody().contains("Concise behavior note."));
    var restoreProposal = service.readProposal(owner, new AgentAdminDocAdministrationService.ProposalCommandRequest(restored.proposalId()), "corr-read-restore-proposal");
    assertEquals(AgentAdminDocAdministrationService.BehaviorProposalOperation.RESTORE_VERSION, restoreProposal.operation());
    assertEquals("Restored from version 1", restoreProposal.transcriptSummary());
    var activatedRestore = service.activateProposal(owner, new ActivateProposalRequest(restored.proposalId(), "Activate restore proposal"), "corr-activate-restore");
    assertEquals(3, activatedRestore.newCurrentVersion());
    var restoredCurrent = service.readDocumentVersion(owner, docRequest, "corr-restored-current-active");
    assertEquals(3, restoredCurrent.version());
    assertEquals("Restored from version 1", restoredCurrent.editSessionTranscriptSummary());

    var cancelSession = service.startEditSession(owner, new StartEditSessionRequest(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, AgentDocKind.SKILL, AgentBehaviorSeedLoader.ACCESS_REVIEW_SKILL_DOC_ID, "Try an alternate skill wording."), "corr-start-cancel");
    var cancelled = service.cancelEditSession(owner, new EditSessionCommandRequest(cancelSession.sessionId()), "corr-cancel");
    assertEquals(EditSessionStatus.CANCELLED, cancelled.status());
  }

  @Test
  void staleProposalActivationIsRejectedAfterCurrentVersionChanges() {
    var owner = actor("owner@example.test", "membership-owner");
    var request = new StartEditSessionRequest(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, AgentDocKind.PROMPT, AgentBehaviorSeedLoader.USER_ADMIN_PROMPT_ID, "First edit.");
    var stale = service.startEditSession(owner, request, "corr-stale-start");
    var winning = service.startEditSession(owner, request, "corr-winning-start");
    var docRequest = new DocumentVersionRequest(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, AgentDocKind.PROMPT, AgentBehaviorSeedLoader.USER_ADMIN_PROMPT_ID, null);
    var before = service.readDocumentVersion(owner, docRequest, "corr-stale-before");
    service.reviseEditSession(owner, new ReviseEditSessionRequest(winning.sessionId(), "Ship winning edit.", before.contentBody() + "\n\nWinning edit.", "Winning edit", List.of()), "corr-winning-revise");
    var winningDraft = service.saveEditSession(owner, new EditSessionCommandRequest(winning.sessionId()), "corr-winning-save-draft");
    service.reviseEditSession(owner, new ReviseEditSessionRequest(stale.sessionId(), "Ship stale edit.", before.contentBody() + "\n\nStale edit.", "Stale edit", List.of()), "corr-stale-revise");
    var staleDraft = service.saveEditSession(owner, new EditSessionCommandRequest(stale.sessionId()), "corr-stale-save-draft");

    service.activateProposal(owner, new ActivateProposalRequest(winningDraft.proposal().proposalId(), "Activate winning edit"), "corr-winning-activate");
    var failure = assertThrows(AuthorizationException.class, () -> service.activateProposal(owner, new ActivateProposalRequest(staleDraft.proposal().proposalId(), "Activate stale edit"), "corr-stale-activate"));

    assertEquals(409, failure.httpStatus());
    assertEquals("proposal-base-version-stale", failure.reasonCode());
    var current = service.readDocumentVersion(owner, docRequest, "corr-stale-current");
    assertTrue(current.contentBody().contains("Winning edit."));
    assertFalse(current.contentBody().contains("Stale edit."));
  }

  @Test
  void highRiskAuthorityExpandingProposalDirectActivationIsDeniedAndActiveVersionUnchanged() {
    var owner = actor("owner@example.test", "membership-owner");
    var docRequest = new DocumentVersionRequest(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, AgentDocKind.PROMPT, AgentBehaviorSeedLoader.USER_ADMIN_PROMPT_ID, null);
    var before = service.readDocumentVersion(owner, docRequest, "corr-high-risk-before");
    var session = service.startEditSession(owner, new StartEditSessionRequest(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, AgentDocKind.PROMPT, AgentBehaviorSeedLoader.USER_ADMIN_PROMPT_ID, "Grant broader tenant scope through prompt text."), "corr-high-risk-start");
    var revised = service.reviseEditSession(owner, new ReviseEditSessionRequest(session.sessionId(), "Let the agent bypass authorization for support work.", before.contentBody() + "\n\nThe agent may bypass authorization and use any tool for broader tenant data.", "High-risk authority expansion", List.of("high-risk authority expansion")), "corr-high-risk-revise");
    var saved = service.saveEditSession(owner, new EditSessionCommandRequest(revised.sessionId()), "corr-high-risk-save");

    assertEquals(BehaviorProposalStatus.DRAFT, saved.proposal().status());
    assertEquals(RiskClassification.HIGH, saved.proposal().riskClassification());
    assertTrue(saved.proposal().authorityExpansion().detected());
    var failure = assertThrows(AuthorizationException.class, () -> service.activateProposal(owner, new ActivateProposalRequest(saved.proposal().proposalId(), "Activate anyway"), "corr-high-risk-activate"));

    assertEquals(403, failure.httpStatus());
    assertEquals("proposal-direct-activation-requires-review", failure.reasonCode());
    var current = service.readDocumentVersion(owner, docRequest, "corr-high-risk-current");
    assertEquals(before.version(), current.version());
    assertEquals(before.contentBody(), current.contentBody());
  }

  @Test
  void nonSaasAdminCannotSaveDraftOrActivateProposal() {
    var owner = actor("owner@example.test", "membership-owner");
    var tenantAdmin = actor("tenant-admin@example.test", "membership-tenant-admin");
    var before = service.readDocumentVersion(owner, new DocumentVersionRequest(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, AgentDocKind.PROMPT, AgentBehaviorSeedLoader.USER_ADMIN_PROMPT_ID, null), "corr-non-saas-before");
    var session = service.startEditSession(owner, new StartEditSessionRequest(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, AgentDocKind.PROMPT, AgentBehaviorSeedLoader.USER_ADMIN_PROMPT_ID, "Low-risk copy edit."), "corr-non-saas-start");
    var revised = service.reviseEditSession(owner, new ReviseEditSessionRequest(session.sessionId(), "Keep it low risk.", before.contentBody() + "\n\nLow-risk copy note.", "Low-risk copy edit", List.of()), "corr-non-saas-revise");

    var saveDenied = assertThrows(AuthorizationException.class, () -> service.saveEditSession(tenantAdmin, new EditSessionCommandRequest(revised.sessionId()), "corr-non-saas-save"));
    assertEquals("agent-admin-requires-saas-owner-admin", saveDenied.reasonCode());

    var saved = service.saveEditSession(owner, new EditSessionCommandRequest(revised.sessionId()), "corr-non-saas-owner-save");
    var activateDenied = assertThrows(AuthorizationException.class, () -> service.activateProposal(tenantAdmin, new ActivateProposalRequest(saved.proposal().proposalId(), "Activate"), "corr-non-saas-activate"));
    assertEquals("agent-admin-requires-saas-owner-admin", activateDenied.reasonCode());
  }

  @Test
  void skillAndReferenceCreateUsesProposalAndDeprecationRemovesLoaderAccessWithoutHardDelete() {
    var owner = actor("owner@example.test", "membership-owner");
    var skill = service.createSkill(owner, new CreateSkillRequest(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, "skill-test-coaching", "test.coaching.v1", "Test Coaching", "Coach tests", "Use for test guidance", "# Test Coaching\nUse examples.", "Create test skill"), "corr-create-skill");
    assertEquals(0, skill.currentVersion());
    assertEquals("create_proposal", skill.lifecycleAction());
    assertNotNull(skill.proposalId());
    assertTrue(agentRepository.skillDocument(WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID, skill.skillDocumentId()).isEmpty());
    var createSkillProposal = service.readProposal(owner, new AgentAdminDocAdministrationService.ProposalCommandRequest(skill.proposalId()), "corr-read-skill-create-proposal");
    assertEquals(AgentAdminDocAdministrationService.BehaviorProposalOperation.CREATE_SKILL, createSkillProposal.operation());
    var activatedSkill = service.activateProposal(owner, new ActivateProposalRequest(skill.proposalId(), "Activate skill create"), "corr-activate-skill-create");
    assertEquals(1, activatedSkill.newCurrentVersion());
    var activeSkill = agentRepository.skillDocument(WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID, skill.skillDocumentId()).orElseThrow();
    assertEquals(AgentLifecycleStatus.ACTIVE, activeSkill.status());

    var reference = service.createReferenceDoc(owner, new CreateReferenceDocRequest(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, AgentBehaviorSeedLoader.ACCESS_REVIEW_SKILL_DOC_ID, "ref-test-coaching", "test.coaching.reference.v1", "Test Coaching Reference", "Reference for test coaching", "Consult for test coaching details", ReferenceDocument.ReferenceType.PROCESS, "# Test Reference\nDetails.", "Create test reference"), "corr-create-reference");
    assertEquals(0, reference.currentVersion());
    assertEquals("create_proposal", reference.lifecycleAction());
    assertNotNull(reference.proposalId());
    assertTrue(agentRepository.referenceDocument(WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID, reference.referenceDocumentId()).isEmpty());
    var activatedReference = service.activateProposal(owner, new ActivateProposalRequest(reference.proposalId(), "Activate reference create"), "corr-activate-reference-create");
    assertEquals(1, activatedReference.newCurrentVersion());
    assertEquals(AgentLifecycleStatus.ACTIVE, agentRepository.referenceDocument(WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID, reference.referenceDocumentId()).orElseThrow().status());

    var runtimeBeforeDeprecation = new AgentRuntimeService(agentRepository, resolver, CLOCK, new OpenAiModelProviderClient(), new InMemoryTestAgentRuntimeTraceSink());
    assertEquals(AgentRuntimeTrace.Decision.ALLOWED, runtimeBeforeDeprecation.readSkill(new AgentRuntimeService.SkillReadRequest(WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID, AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, owner.selectedContext(), "runtime", "saas_owner.admin.manage", "corr-before-deprecate-skill-read", "ua.access-review-triage.v1")).decision());
    assertEquals(AgentRuntimeTrace.Decision.ALLOWED, runtimeBeforeDeprecation.readReferenceDoc(new AgentRuntimeService.ReferenceReadRequest(WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID, AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, owner.selectedContext(), "runtime", "saas_owner.admin.manage", "corr-before-deprecate-reference-read", "ua.access-review-policy.v1", "consult")).decision());

    var referenceDocId = AgentBehaviorSeedLoader.ACCESS_REVIEW_POLICY_REFERENCE_DOC_ID;
    var deprecatedReferenceResult = service.deleteReferenceDoc(owner, new AgentAdminDocAdministrationService.DeleteReferenceDocRequest(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, referenceDocId, "Deprecate Access Review Policy and remove manifest access"), "corr-deprecate-reference");
    assertEquals("deprecated", deprecatedReferenceResult.lifecycleAction());
    assertTrue(deprecatedReferenceResult.affectedManifestEntryCount() >= 1);
    assertEquals(AgentLifecycleStatus.DEPRECATED, agentRepository.referenceDocument(WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID, referenceDocId).orElseThrow().status());
    var deprecatedReferenceDoc = service.readDocumentVersion(owner, new DocumentVersionRequest(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, AgentDocKind.REFERENCE, referenceDocId, null), "corr-read-deprecated-reference");
    assertEquals(1, deprecatedReferenceDoc.version());
    var deniedReferenceLoad = runtimeBeforeDeprecation.readReferenceDoc(new AgentRuntimeService.ReferenceReadRequest(WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID, AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, owner.selectedContext(), "runtime", "saas_owner.admin.manage", "corr-after-deprecate-reference-read", "ua.access-review-policy.v1", "consult"));
    assertEquals(AgentRuntimeTrace.Decision.DENIED, deniedReferenceLoad.decision());
    assertEquals("Reference is not available in this governed runtime context.", deniedReferenceLoad.safeDenialReason());

    var deprecated = service.deleteSkill(owner, new DeleteSkillRequest(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, AgentBehaviorSeedLoader.ACCESS_REVIEW_SKILL_DOC_ID, "Deprecate Access Review Triage and disclose affected assignments/references"), "corr-deprecate-skill");
    assertEquals("deprecated", deprecated.lifecycleAction());
    assertEquals(1, deprecated.affectedAssignmentCount());
    assertTrue(deprecated.affectedReferenceCount() >= 1);
    assertTrue(deprecated.affectedManifestEntryCount() >= deprecated.affectedAssignmentCount());
    assertEquals(AgentLifecycleStatus.DEPRECATED, agentRepository.skillDocument(WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID, AgentBehaviorSeedLoader.ACCESS_REVIEW_SKILL_DOC_ID).orElseThrow().status());
    var deprecatedSkillDoc = service.readDocumentVersion(owner, new DocumentVersionRequest(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, AgentDocKind.SKILL, AgentBehaviorSeedLoader.ACCESS_REVIEW_SKILL_DOC_ID, null), "corr-read-deprecated-skill");
    assertEquals(1, deprecatedSkillDoc.version());

    var runtimeAfterDeprecation = new AgentRuntimeService(agentRepository, resolver, CLOCK, new OpenAiModelProviderClient(), new InMemoryTestAgentRuntimeTraceSink());
    var deniedSkillLoad = runtimeAfterDeprecation.readSkill(new AgentRuntimeService.SkillReadRequest(WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID, AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, owner.selectedContext(), "runtime", "saas_owner.admin.manage", "corr-after-deprecate-skill-read", "ua.access-review-triage.v1"));
    assertEquals(AgentRuntimeTrace.Decision.DENIED, deniedSkillLoad.decision());
    assertEquals("Skill is not available in this governed runtime context.", deniedSkillLoad.safeDenialReason());

  }

  @Test
  void behaviorProfileAssignmentCreatesTenantScopedVersionWithoutMutatingSkillDocsOrGeneratedTools() {
    var owner = actor("owner@example.test", "membership-owner");
    var globalDetail = service.agentDetail(owner, AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, "corr-profile-detail");
    assertEquals("global", globalDetail.profile().scopeProvenance());
    assertEquals(1, globalDetail.profile().profileVersion());
    assertEquals("openai-low-temperature", globalDetail.profile().safeModelAlias());
    assertFalse(globalDetail.toString().toLowerCase().contains("api_key"));
    assertFalse(globalDetail.profileHistory().isEmpty());
    assertEquals("global", service.listAgents(owner, new AgentListRequest("User Admin", null), "corr-profile-list").rows().get(0).profile().scopeProvenance());

    var boundaryBefore = agentRepository.toolBoundary(WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID, AgentBehaviorSeedLoader.USER_ADMIN_BOUNDARY_ID).orElseThrow();
    var skillBefore = agentRepository.skillDocument(WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID, AgentBehaviorSeedLoader.ACCESS_REVIEW_SKILL_DOC_ID).orElseThrow();

    var updated = service.updateBehaviorProfileAssignments(owner, new AgentAdminDocAdministrationService.BehaviorProfileAssignmentRequest(
        "tenant-1",
        AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID,
        globalDetail.profile().profileVersion(),
        AgentBehaviorSeedLoader.STARTER_DEFAULT_MODEL_CONFIG_ID,
        List.of(AgentBehaviorSeedLoader.ACCESS_REVIEW_SKILL_DOC_ID, AgentBehaviorSeedLoader.ACCESS_REVIEW_SKILL_DOC_ID),
        List.of("userAdminEvidence.read"),
        "Tenant-specific access review profile"), "corr-profile-update");

    assertTrue(updated.changed());
    assertEquals("tenant:tenant-1", updated.profile().scopeProvenance());
    assertEquals(1, updated.profile().profileVersion());
    assertEquals(List.of(AgentBehaviorSeedLoader.ACCESS_REVIEW_SKILL_DOC_ID), updated.profile().assignedSkillDocumentIds());
    assertEquals(List.of("userAdminEvidence.read"), updated.profile().assignedGeneratedToolIds());
    assertEquals("openai-low-temperature", updated.profile().safeModelAlias());

    var tenantProfile = agentRepository.activeBehaviorProfile("tenant-1", AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID).orElseThrow();
    assertEquals(WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID, tenantProfile.clonedFromTenantId());
    assertEquals(1, tenantProfile.clonedFromProfileVersion());
    assertEquals(1, agentRepository.behaviorProfileVersions("tenant-1", AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID).size());
    assertEquals(globalDetail.profile().assignedSkillCount(), agentRepository.activeBehaviorProfile(WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID, AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID).orElseThrow().assignedSkillDocumentIds().size());

    var skillAfter = agentRepository.skillDocument(WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID, AgentBehaviorSeedLoader.ACCESS_REVIEW_SKILL_DOC_ID).orElseThrow();
    assertEquals(skillBefore.activeVersion(), skillAfter.activeVersion());
    assertEquals(skillBefore.contentChecksum(), skillAfter.contentChecksum());
    var boundaryAfter = agentRepository.toolBoundary(WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID, AgentBehaviorSeedLoader.USER_ADMIN_BOUNDARY_ID).orElseThrow();
    assertEquals(boundaryBefore.boundaryVersion(), boundaryAfter.boundaryVersion());
    assertEquals(boundaryBefore.allowedToolGrants(), boundaryAfter.allowedToolGrants());

    var idempotent = service.updateBehaviorProfileAssignments(owner, new AgentAdminDocAdministrationService.BehaviorProfileAssignmentRequest(
        "tenant-1",
        AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID,
        updated.profile().profileVersion(),
        AgentBehaviorSeedLoader.STARTER_DEFAULT_MODEL_CONFIG_ID,
        List.of(AgentBehaviorSeedLoader.ACCESS_REVIEW_SKILL_DOC_ID),
        List.of("userAdminEvidence.read"),
        "No-op repeat"), "corr-profile-idempotent");
    assertFalse(idempotent.changed());
    assertEquals(1, agentRepository.behaviorProfileVersions("tenant-1", AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID).size());
  }

  @Test
  void behaviorProfileAssignmentDeniesUnauthorizedOrUnknownRequests() {
    var owner = actor("owner@example.test", "membership-owner");
    var tenantAdmin = actor("tenant-admin@example.test", "membership-tenant-admin");
    var deniedActor = assertThrows(AuthorizationException.class, () -> service.updateBehaviorProfileAssignments(tenantAdmin, new AgentAdminDocAdministrationService.BehaviorProfileAssignmentRequest(
        "tenant-1", AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, 1, null, List.of(AgentBehaviorSeedLoader.ACCESS_REVIEW_SKILL_DOC_ID), List.of("userAdminEvidence.read"), "Denied"), "corr-profile-denied-actor"));
    assertEquals("agent-admin-requires-saas-owner-admin", deniedActor.reasonCode());

    var deniedSkill = assertThrows(AuthorizationException.class, () -> service.updateBehaviorProfileAssignments(owner, new AgentAdminDocAdministrationService.BehaviorProfileAssignmentRequest(
        "tenant-1", AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, 1, null, List.of("missing-skill"), null, "Denied"), "corr-profile-denied-skill"));
    assertEquals("skill-not-found-or-forbidden", deniedSkill.reasonCode());

    var deniedTool = assertThrows(AuthorizationException.class, () -> service.updateBehaviorProfileAssignments(owner, new AgentAdminDocAdministrationService.BehaviorProfileAssignmentRequest(
        "tenant-1", AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, 1, null, null, List.of("missing.generated.tool"), "Denied"), "corr-profile-denied-tool"));
    assertEquals("generated-tool-not-found-or-forbidden", deniedTool.reasonCode());
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
