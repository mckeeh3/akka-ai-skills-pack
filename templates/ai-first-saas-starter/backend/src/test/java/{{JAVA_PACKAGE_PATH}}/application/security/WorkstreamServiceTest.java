package {{JAVA_BASE_PACKAGE}}.application.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AgentBehaviorSeedLoader;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AgentRuntimeService;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.InMemoryAgentBehaviorRepository;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.ModelProviderClient;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.WorkstreamAgentRuntimeInvoker;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WorkstreamServiceTest {
  private WorkstreamService service;
  private TrackingWorkstreamAgentRuntimeTestAdapter trackingRuntimeInvoker;

  @BeforeEach
  void setUp() {
    var identityRepository = new InMemoryIdentityRepository();
    var invitationRepository = new InMemoryInvitationRepository();
    var resolver = new AuthContextResolver(identityRepository);
    var meService = new MeService(resolver);
    var userAdminService = new UserAdminService(identityRepository, Clock.systemUTC());
    var invitationService = new InvitationService(identityRepository, invitationRepository, Clock.systemUTC());
    var agentRepository = new InMemoryAgentBehaviorRepository();
    new AgentBehaviorSeedLoader(agentRepository, Clock.systemUTC()).importStarterDefaults("tenant-1", "bootstrap", "corr-agent-seed");
    var agentRuntimeService = new AgentRuntimeService(agentRepository, resolver, Clock.systemUTC(), request -> new ModelProviderClient.ModelProviderResponse("## " + request.functionalAgentId() + " model response\n\nProvider-backed test markdown.", "test-fake-provider", "test-fake-model", "fake-response-id", "stop", "unit-test fake model invocation"));
    trackingRuntimeInvoker = new TrackingWorkstreamAgentRuntimeTestAdapter(agentRuntimeService);
    service = new WorkstreamService(meService, resolver, new UserDirectoryView(userAdminService), new InvitationView(invitationService), userAdminService, invitationService, agentRepository, agentRuntimeService, trackingRuntimeInvoker, new InMemoryWorkstreamLogRepository());

    identityRepository.putTenant(new Tenant("tenant-1", "Tenant One", true));
    identityRepository.saveAccount(new Account("admin@example.test", null, "admin@example.test", "admin@example.test", AccountStatus.ACTIVE, "LINKED"));
    identityRepository.putProfile(new UserProfile("admin@example.test", "admin@example.test", "Tenant Admin", "Tenant", "Admin", null));
    identityRepository.putSettings(new UserSettings("admin@example.test", UserSettings.UiMode.LIGHT));
    identityRepository.putMembership(new Membership("membership-admin", "admin@example.test", ScopeType.TENANT, "tenant-1", null, List.of(FoundationRole.TENANT_ADMIN, FoundationRole.AUDITOR), MembershipStatus.ACTIVE, false, null));
    identityRepository.saveAccount(new Account("member@example.test", null, "member@example.test", "member@example.test", AccountStatus.ACTIVE, "UNLINKED"));
    identityRepository.putProfile(new UserProfile("member@example.test", "member@example.test", "Member User", "Member", "User", null));
    identityRepository.putSettings(new UserSettings("member@example.test", UserSettings.UiMode.LIGHT));
    identityRepository.putMembership(new Membership("membership-member", "member@example.test", ScopeType.TENANT, "tenant-1", null, List.of(FoundationRole.TENANT_EMPLOYEE), MembershipStatus.ACTIVE, false, null));
  }

  @Test
  void starterSourceContainsConcreteAkkaWorkstreamRuntimeAgentAndInvokerSeam() throws Exception {
    var agentSource = findSource("WorkstreamRuntimeAgent.java");
    var agentText = Files.readString(agentSource);
    assertTrue(agentText.contains("import akka.javasdk.agent.Agent;"), "Workstream runtime must import the Akka Agent base class");
    assertTrue(agentText.contains("extends Agent"), "Workstream runtime must be a concrete Akka Agent component");
    assertTrue(agentText.contains("@Component"), "Workstream runtime must be discoverable as an Akka component");
    assertFalse(agentText.matches("(?is).*class\\s+.*Fake.*"), "Production workstream agent must not be a fake runtime");

    var serviceText = Files.readString(findSource("WorkstreamService.java"));
    assertTrue(serviceText.contains("WorkstreamAgentRuntimeInvoker"), "WorkstreamService must depend on the Akka Agent runtime invoker seam");
    assertTrue(serviceText.contains("workstreamAgentRuntimeInvoker.invokeWorkstreamAgent"), "Successful message submission must go through the runtime invoker seam");
  }

  @Test
  void bootstrapReturnsFiveCoreV0MarkdownSurfacesWithoutSecrets() {
    var bootstrap = service.bootstrap(identity(), null, "corr-bootstrap");

    assertEquals("membership-admin", bootstrap.me().selectedAuthContext().selectedContextId());
    assertTrue(bootstrap.functionalAgents().stream().anyMatch(agent -> agent.functionalAgentId().equals("agent-user-admin") && agent.availability().equals("visible")));
    assertEquals(5, bootstrap.items().size());
    assertEquals(5, bootstrap.surfaces().size());
    for (var surfaceId : List.of("surface-v0-my-account-markdown", "surface-v0-user-admin-markdown", "surface-v0-agent-admin-markdown", "surface-v0-audit-trace-markdown", "surface-v0-governance-policy-markdown")) {
      assertTrue(bootstrap.items().stream().anyMatch(item -> surfaceId.equals(item.surfaceId()) && item.kind().equals("markdown_response")));
      assertTrue(bootstrap.surfaces().stream().anyMatch(surface -> surfaceId.equals(surface.surfaceId()) && surface.surfaceType().equals("markdown_response")));
    }
    assertFalse(bootstrap.surfaces().stream().anyMatch(surface -> surface.surfaceId().equals("surface-user-admin-dashboard")));
    assertFalse(bootstrap.surfaces().stream().anyMatch(surface -> surface.surfaceType().equals("dashboard") || surface.surfaceType().equals("list-search") || surface.surfaceType().equals("governance-diff") || surface.surfaceType().equals("workflow-status")));
    assertFalse(bootstrap.toString().contains("invite-token"));
    assertFalse(bootstrap.toString().contains("tokenHash"));
    assertFalse(bootstrap.toString().contains("providerSecret"));
  }

  @Test
  void actionDispatcherRequiresSelectedContextAndIdempotency() {
    var missingKey = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-invite-user", "secure-tenant-user-foundation", null, null, "membership-admin", "surface-user-admin-dashboard", "corr-invite"));

    assertEquals("validation-error", missingKey.status());

    var mismatch = assertThrows(AuthorizationException.class, () -> service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-display-user-list", "secure-tenant-user-foundation", null, null, "membership-other", "surface-user-admin-dashboard", "corr-forbidden")));
    assertEquals("CONTEXT_FORBIDDEN", mismatch.reasonCode());
  }

  @Test
  void realtimeEventsAreScopedAndResumeWithStaleFallback() {
    var events = service.events(identity(), "membership-admin", null, null, "corr-events");

    assertTrue(events.stream().anyMatch(event -> event.eventType().equals("surface.stale") && event.surfaceId().equals("surface-v0-user-admin-markdown") && event.surfaceType().equals("markdown_response")));
    assertTrue(events.stream().allMatch(event -> event.tenantId().equals("tenant-1")));

    var resumed = service.events(identity(), "membership-admin", null, "evt-audit-appended-002", "corr-events");
    assertTrue(resumed.stream().noneMatch(event -> event.eventId().equals("evt-audit-appended-002")));
    assertTrue(resumed.stream().anyMatch(event -> event.eventId().equals("evt-user-admin-stale-003")));

    var staleFallback = service.events(identity(), "membership-admin", null, "evt-missing", "corr-events");
    assertEquals("surface.stale", staleFallback.get(0).eventType());
  }

  @Test
  void agentAdminActionsCreateGovernedResultsAndTraces() {
    var promptProposal = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-propose-prompt-diff", "agent.prompts.govern", null, "idem-prompt", "membership-admin", "surface-agent-prompt-governance", "corr-prompt-ui"));
    assertEquals("accepted", promptProposal.status());
    assertEquals("surface-agent-prompt-governance", promptProposal.resultSurface().surfaceId());

    var testRun = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-test-agent-prompt", "agent.runtime.test", null, "idem-test", "membership-admin", "surface-agent-test-console", "corr-test-ui"));
    assertEquals("accepted", testRun.status());
    assertEquals("surface-agent-test-console", testRun.resultSurface().surfaceId());

    var approval = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-approve-skill-manifest", "agent.skills.govern", null, "idem-approval", "membership-admin", "surface-agent-skill-manifest-diff", "corr-approval-ui"));
    assertEquals("approval-required", approval.status());
    assertTrue(approval.message().contains("governed review gate"));
  }

  @Test
  void disabledSurfaceActionsReturnDenialResultSurface() {
    var result = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-replace-membership-role", "secure-tenant-user-foundation", null, "idem-1", "membership-admin", "surface-user-admin-detail-admin", "corr-role"));

    assertEquals("denied", result.status());
    assertTrue(result.message().contains("last tenant admin"));
    assertEquals("surface-user-admin-detail-admin", result.resultSurface().surfaceId());
  }

  @Test
  void submitMessageReturnsAuthorizedMarkdownResponseEnvelopeAndPersistsIt() {
    var response = service.submitMessage(identity(), "membership-admin", new WorkstreamService.WorkstreamMessageRequest(
        "membership-admin", "agent-user-admin", "What can I do next?", "corr-message", "idem-message-1"), "corr-header");

    assertEquals("corr-message", response.correlationId());
    assertEquals("idem-message-1", response.idempotencyKey());
    assertEquals("agent-user-admin", response.userItem().functionalAgentId());
    assertEquals("user-message", response.userItem().kind());
    assertEquals("agent-user-admin", response.agentItem().functionalAgentId());
    assertEquals("markdown_response", response.agentItem().kind());
    assertEquals(response.surface().surfaceId(), response.agentItem().surfaceId());
    assertNull(response.agentItem().body(), "Successful model response text belongs in the rendered markdown_response surface, not placeholder item copy");
    assertEquals("markdown_response", response.surface().surfaceType());
    assertEquals("agent-user-admin", response.surface().ownerFunctionalAgentId());
    assertEquals("membership-admin", response.surface().authContext().get("selectedContextId"));
    assertEquals("corr-message", response.surface().correlationId());
    assertFalse(response.surface().traceIds().isEmpty());
    assertEquals("agent-user-admin", response.surface().data().get("producingAgentId"));
    assertEquals(response.agentItem().itemId(), response.surface().data().get("workstreamEntryId"));
    assertTrue(response.surface().data().get("markdown").toString().contains("## agent-user-admin model response"));
    assertEquals(1, trackingRuntimeInvoker.invocationCount(), "Successful markdown_response must be produced through the workstream Akka Agent runtime invoker seam");
    assertEquals("agent-user-admin", trackingRuntimeInvoker.lastRequest().agentDefinitionId());
    assertNotNull(response.surface().data().get("safety"));
    assertNotNull(response.surface().data().get("trace"));

    var persistedItems = service.items(identity(), "membership-admin", "agent-user-admin", "corr-read");
    assertTrue(persistedItems.stream().anyMatch(item -> item.itemId().equals(response.userItem().itemId())));
    assertTrue(persistedItems.stream().anyMatch(item -> item.itemId().equals(response.agentItem().itemId())));
    var persistedSurface = service.surface(identity(), "membership-admin", response.surface().surfaceId(), "corr-read");
    assertEquals(response.surface().surfaceId(), persistedSurface.surfaceId());
    assertEquals("corr-message", persistedSurface.correlationId());
  }

  @Test
  void submitMessageSupportsEveryFiveCoreV0FunctionalAgent() {
    for (var agentId : List.of("agent-my-account", "agent-user-admin", "agent-agent-admin", "agent-audit-trace", "agent-governance-policy")) {
      var response = service.submitMessage(identity(), "membership-admin", new WorkstreamService.WorkstreamMessageRequest(
          "membership-admin", agentId, "Show five core v0 readiness", "corr-" + agentId, "idem-" + agentId), "corr-header");

      assertEquals(agentId, response.surface().ownerFunctionalAgentId());
      assertEquals("markdown_response", response.surface().surfaceType());
      assertEquals(agentId, response.surface().data().get("producingAgentId"));
      assertTrue(response.surface().data().get("markdown").toString().contains(agentId + " model response"));
    }
  }

  @Test
  void submitMessageIsIdempotentForDuplicateClientKeys() {
    var first = service.submitMessage(identity(), "membership-admin", new WorkstreamService.WorkstreamMessageRequest(
        "membership-admin", "agent-user-admin", "What can I do next?", "corr-idem-first", "idem-duplicate-message"), "corr-header");
    var duplicate = service.submitMessage(identity(), "membership-admin", new WorkstreamService.WorkstreamMessageRequest(
        "membership-admin", "agent-user-admin", "Changed prompt should not append", "corr-idem-second", "idem-duplicate-message"), "corr-header");

    assertEquals(first.userItem().itemId(), duplicate.userItem().itemId());
    assertEquals(first.agentItem().itemId(), duplicate.agentItem().itemId());
    assertEquals(first.surface().surfaceId(), duplicate.surface().surfaceId());
    var persistedItems = service.items(identity(), "membership-admin", "agent-user-admin", "corr-read-idem").stream()
        .filter(item -> item.itemId().equals(first.userItem().itemId()) || item.itemId().equals(first.agentItem().itemId()))
        .toList();
    assertEquals(2, persistedItems.size());
  }

  @Test
  void submitMessageRequiresSelectedContextMatch() {
    var mismatch = assertThrows(AuthorizationException.class, () -> service.submitMessage(identity(), "membership-admin", new WorkstreamService.WorkstreamMessageRequest(
        "membership-other", "agent-user-admin", "Hello", "corr-message", "idem-message-2"), "corr-header"));

    assertEquals("CONTEXT_FORBIDDEN", mismatch.reasonCode());
  }

  @Test
  void submitMessageRejectsDeniedFunctionalAgentBeforeModelResponseAndPersistsDenial() {
    var denied = assertThrows(AuthorizationException.class, () -> service.submitMessage(memberIdentity(), "membership-member", new WorkstreamService.WorkstreamMessageRequest(
        "membership-member", "agent-user-admin", "Invite someone", "corr-denied", "idem-message-3"), "corr-header"));

    assertEquals("FUNCTIONAL_AGENT_FORBIDDEN", denied.reasonCode());
    var deniedItems = service.items(memberIdentity(), "membership-member", "agent-user-admin", "corr-denied-read");
    assertTrue(deniedItems.stream().anyMatch(item -> item.kind().equals("system_message") && item.status().equals("blocked") && item.body().contains("FUNCTIONAL_AGENT_FORBIDDEN")));
  }

  @Test
  void submitMessagePropagatesFallbackCorrelationWhenBodyOmitsIt() {
    var response = service.submitMessage(identity(), "membership-admin", new WorkstreamService.WorkstreamMessageRequest(
        "membership-admin", "agent-audit-trace", "Show trace status", null, "idem-message-4"), "corr-header");

    assertEquals("corr-header", response.correlationId());
    assertEquals("corr-header", response.surface().correlationId());
    assertFalse(response.surface().traceIds().isEmpty());
  }

  private static Path findSource(String fileName) throws Exception {
    try (Stream<Path> paths = Files.walk(Path.of("src/main/java"))) {
      return paths
          .filter(path -> path.getFileName().toString().equals(fileName))
          .findFirst()
          .orElseThrow(() -> new AssertionError("Missing source file: " + fileName));
    }
  }

  private static final class TrackingWorkstreamAgentRuntimeTestAdapter implements WorkstreamAgentRuntimeInvoker {
    private final AgentRuntimeService delegate;
    private final AtomicInteger invocationCount = new AtomicInteger();
    private AgentRuntimeService.RuntimeInvocationRequest lastRequest;

    private TrackingWorkstreamAgentRuntimeTestAdapter(AgentRuntimeService delegate) {
      this.delegate = delegate;
    }

    @Override
    public AgentRuntimeService.RuntimeInvocationResult invokeWorkstreamAgent(AgentRuntimeService.RuntimeInvocationRequest request) {
      invocationCount.incrementAndGet();
      lastRequest = request;
      return delegate.invokeWorkstreamAgent(request);
    }

    private int invocationCount() {
      return invocationCount.get();
    }

    private AgentRuntimeService.RuntimeInvocationRequest lastRequest() {
      return lastRequest;
    }
  }

  private WorkosIdentity identity() {
    return new WorkosIdentity("workos-admin", "admin@example.test", "Tenant Admin");
  }

  private WorkosIdentity memberIdentity() {
    return new WorkosIdentity("workos-member", "member@example.test", "Member User");
  }
}
