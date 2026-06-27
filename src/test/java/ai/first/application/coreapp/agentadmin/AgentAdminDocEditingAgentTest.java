package ai.first.application.coreapp.agentadmin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.first.application.coreapp.agentadmin.AgentAdminDocAdministrationService.AgentDocKind;
import ai.first.application.coreapp.agentadmin.AgentAdminDocAdministrationService.AgentDraftEditSessionRequest;
import ai.first.application.coreapp.agentadmin.AgentAdminDocAdministrationService.AgentReviseEditSessionRequest;
import ai.first.application.coreapp.agentadmin.AgentAdminDocAdministrationService.DocumentVersionRequest;
import ai.first.application.coreapp.agentadmin.AgentAdminDocAdministrationService.EditSessionCommandRequest;
import ai.first.application.coreapp.agentadmin.AgentAdminDocAdministrationService.EditSessionStatus;
import ai.first.application.foundation.agent.AgentBehaviorSeedLoader;
import ai.first.application.foundation.agent.AgentRuntimeService;
import ai.first.application.foundation.agent.InMemoryTestAgentBehaviorRepository;
import ai.first.application.foundation.agent.InMemoryTestAgentRuntimeTraceSink;
import ai.first.application.foundation.agent.OpenAiModelProviderClient;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.AuthorizationException;
import ai.first.application.foundation.identity.InMemoryTestIdentityRepository;
import ai.first.application.foundation.workstream.WorkstreamEventPublisher;
import ai.first.domain.foundation.agent.AgentDefinition;
import ai.first.domain.foundation.agent.AgentLifecycleStatus;
import ai.first.domain.foundation.agent.AgentRuntimeTrace;
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
import akka.javasdk.JsonSupport;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AgentAdminDocEditingAgentTest extends TestKitSupport {
  private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-06-24T12:00:00Z"), ZoneOffset.UTC);

  private final TestModelProvider editingModelProvider = new TestModelProvider();
  private InMemoryTestIdentityRepository identityRepository;
  private InMemoryTestAgentBehaviorRepository agentRepository;
  private InMemoryTestAgentRuntimeTraceSink traceSink;
  private AuthContextResolver resolver;
  private AgentAdminDocAdministrationService service;

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withAdditionalConfig(
            """
            akka.javasdk.agent.openai-low-temperature.provider = openai
            akka.javasdk.agent.openai-low-temperature.model-name = gpt-4o-mini
            akka.javasdk.agent.openai-low-temperature.api-key = n/a
            """
                .stripIndent())
        .withModelProvider(AgentAdminDocEditingAgent.class, editingModelProvider);
  }

  @BeforeEach
  void setUp() {
    identityRepository = new InMemoryTestIdentityRepository();
    identityRepository.putTenant(new Tenant("tenant-1", "Tenant One", true));
    identityRepository.putCustomer(new Customer("tenant-1", "customer-1", "Customer One", true));
    seedActor("owner@example.test", "membership-owner", ScopeType.SAAS_OWNER, null, null, FoundationRole.SAAS_OWNER_ADMIN);
    resolver = new AuthContextResolver(identityRepository);
    agentRepository = new InMemoryTestAgentBehaviorRepository();
    new AgentBehaviorSeedLoader(agentRepository, CLOCK)
        .importStarterDefaults(WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID, "owner@example.test", "corr-seed");
    traceSink = new InMemoryTestAgentRuntimeTraceSink();
    var runtimeService = new AgentRuntimeService(agentRepository, resolver, CLOCK, new OpenAiModelProviderClient(), traceSink);
    var editingRuntime = new ComponentClientAgentAdminDocEditingRuntime(agentRepository, runtimeService, componentClient);
    service = new AgentAdminDocAdministrationService(agentRepository, resolver, CLOCK, editingRuntime, traceSink);
  }

  @Test
  void draftsRevisesSavesAndCancelsThroughModelBackedAgentPath() {
    var owner = actor("owner@example.test", "membership-owner");
    var before = service.readDocumentVersion(owner, promptRequest(null), "corr-before");

    editingModelProvider.fixedResponse(JsonSupport.encodeToString(new AgentAdminDocEditingAgent.EditProposal(
        "proposed",
        before.contentBody() + "\n\nConcise behavior note.",
        null,
        "Added concise behavior note.",
        List.of("Advisory warning only"),
        "safe proposal",
        "draft trace")));

    var draft = service.draftEditSessionWithAgent(owner, new AgentDraftEditSessionRequest(
        AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID,
        AgentDocKind.PROMPT,
        AgentBehaviorSeedLoader.USER_ADMIN_PROMPT_ID,
        "Add a concise behavior note."), "corr-draft");

    assertEquals(EditSessionStatus.PROPOSAL_READY, draft.status());
    assertTrue(draft.proposedContent().contains("Concise behavior note."));
    assertEquals(1, draft.instructions().size());
    assertFalse(draft.traceLinks().isEmpty());

    editingModelProvider.fixedResponse(JsonSupport.encodeToString(new AgentAdminDocEditingAgent.EditProposal(
        "proposed",
        before.contentBody() + "\n\nConcise behavior note.\nKeep the existing headings.",
        null,
        "Added note and retained headings.",
        List.of("Review heading consistency"),
        "safe revision",
        "revision trace")));

    var revised = service.reviseEditSessionWithAgent(owner, new AgentReviseEditSessionRequest(draft.sessionId(), "Also keep the existing headings."), "corr-revise");

    assertEquals(EditSessionStatus.PROPOSAL_READY, revised.status());
    assertEquals(2, revised.instructions().size());
    assertTrue(revised.proposedContent().contains("Keep the existing headings."));

    var saved = service.saveEditSession(owner, new EditSessionCommandRequest(revised.sessionId()), "corr-save");
    assertEquals(EditSessionStatus.SAVED, saved.session().status());
    assertEquals(1, saved.savedVersion());
    var afterDraft = service.readDocumentVersion(owner, promptRequest(null), "corr-after-draft");
    assertEquals(1, afterDraft.version());
    assertFalse(afterDraft.contentBody().contains("Keep the existing headings."));

    var activated = service.activateProposal(owner, new AgentAdminDocAdministrationService.ActivateProposalRequest(saved.proposal().proposalId(), "Activate low-risk draft"), "corr-activate");
    assertEquals(2, activated.newCurrentVersion());
    var afterSave = service.readDocumentVersion(owner, promptRequest(null), "corr-after-save");
    assertEquals(2, afterSave.version());
    assertTrue(afterSave.contentBody().contains("Keep the existing headings."));
    assertTrue(afterSave.editSessionTranscriptSummary().contains("Also keep the existing headings."));
    assertTrue(afterSave.editSessionTranscriptSummary().contains("proposedContentChecksum"));

    editingModelProvider.fixedResponse(JsonSupport.encodeToString(new AgentAdminDocEditingAgent.EditProposal(
        "proposed",
        afterSave.contentBody() + "\n\nTemporary cancelled edit.",
        null,
        "Temporary edit.",
        List.of(),
        "safe proposal",
        "cancel trace")));

    var cancelDraft = service.draftEditSessionWithAgent(owner, new AgentDraftEditSessionRequest(
        AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID,
        AgentDocKind.PROMPT,
        AgentBehaviorSeedLoader.USER_ADMIN_PROMPT_ID,
        "Try a temporary cancelled edit."), "corr-cancel-draft");
    var cancelled = service.cancelEditSession(owner, new EditSessionCommandRequest(cancelDraft.sessionId()), "corr-cancel");

    assertEquals(EditSessionStatus.CANCELLED, cancelled.status());
    var afterCancel = service.readDocumentVersion(owner, promptRequest(null), "corr-after-cancel");
    assertEquals(2, afterCancel.version());
    assertFalse(afterCancel.contentBody().contains("Temporary cancelled edit."));

    assertTrue(traceSink.traces().stream().anyMatch(trace -> trace.traceType().equals("PROMPT_ASSEMBLY") && trace.decision() == AgentRuntimeTrace.Decision.ALLOWED));
    assertTrue(traceSink.traces().stream().anyMatch(trace -> trace.traceType().equals("EDIT_AGENT_INVOCATION") && trace.decision() == AgentRuntimeTrace.Decision.ALLOWED));
    assertTrue(traceSink.traces().stream().anyMatch(trace -> trace.traceType().equals("EDIT_SESSION_SAVE_DRAFT") && trace.capabilityId().equals(AgentAdminDocAdministrationService.SAVE_EDIT_CAPABILITY)));
    assertTrue(traceSink.traces().stream().anyMatch(trace -> trace.traceType().equals("BEHAVIOR_PROPOSAL_ACTIVATED") && trace.capabilityId().equals(AgentAdminDocAdministrationService.ACTIVATE_PROPOSAL_CAPABILITY)));
    assertTrue(traceSink.traces().stream().anyMatch(trace -> trace.traceType().equals("EDIT_SESSION_CANCEL") && trace.capabilityId().equals(AgentAdminDocAdministrationService.CANCEL_EDIT_CAPABILITY)));
  }

  @Test
  void missingEditingAgentModelRuntimeFailsClosedBeforeModelSuccessIsFaked() {
    var owner = actor("owner@example.test", "membership-owner");
    var agent = agentRepository.agentDefinition(WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID, AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID).orElseThrow();
    agentRepository.saveAgentDefinition(new AgentDefinition(
        agent.tenantId(),
        agent.agentDefinitionId(),
        agent.displayName(),
        agent.description(),
        agent.placement(),
        agent.functionalAreaId(),
        agent.authorityLevel(),
        AgentLifecycleStatus.ACTIVE,
        agent.promptDocumentId(),
        agent.activePromptVersion(),
        agent.skillManifestId(),
        agent.activeSkillManifestVersion(),
        agent.referenceManifestId(),
        agent.activeReferenceManifestVersion(),
        agent.toolBoundaryId(),
        agent.activeToolBoundaryVersion(),
        "missing-editing-model",
        agent.modelPolicyRefId(),
        agent.runtimeClassRef(),
        agent.traceRequirements(),
        agent.seedProvenance(),
        agent.createdAt(),
        agent.updatedAt()));

    var failure = assertThrows(AuthorizationException.class, () -> service.draftEditSessionWithAgent(owner, new AgentDraftEditSessionRequest(
        AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID,
        AgentDocKind.PROMPT,
        AgentBehaviorSeedLoader.USER_ADMIN_PROMPT_ID,
        "Add a note."), "corr-missing-model"));

    assertEquals(503, failure.httpStatus());
    assertEquals("AGENT_ADMIN_DOC_EDITING_RUNTIME_DENIED", failure.reasonCode());
    assertTrue(traceSink.traces().stream().anyMatch(trace -> trace.traceType().equals("PROMPT_ASSEMBLY") && trace.decision() == AgentRuntimeTrace.Decision.DENIED && trace.safeSummary().contains("model-config-not-available")));
    assertTrue(traceSink.traces().stream().anyMatch(trace -> trace.traceType().equals("EDIT_AGENT_INVOCATION") && trace.decision() == AgentRuntimeTrace.Decision.DENIED));
  }

  private DocumentVersionRequest promptRequest(Integer version) {
    return new DocumentVersionRequest(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, AgentDocKind.PROMPT, AgentBehaviorSeedLoader.USER_ADMIN_PROMPT_ID, version);
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
