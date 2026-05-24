package {{JAVA_BASE_PACKAGE}}.application.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AgentBehaviorSeedLoader;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AgentRuntimeService;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.InMemoryAgentBehaviorRepository;
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
import org.junit.jupiter.api.Test;

/** Optional real-provider smoke. Skips unless backend-only provider env is present. */
class RealModelProviderSmokeTest {
  @Test
  void workstreamMessageSubmissionUsesRealProviderAndEmitsTraceShape() {
    assumeTrue(Boolean.getBoolean("realModelProviderSmoke"), "Skipping real model provider smoke because -DrealModelProviderSmoke=true was not provided.");
    var apiKey = trimToNull(System.getenv("OPENAI_API_KEY"));
    assumeTrue(apiKey != null, "Skipping real model provider smoke because OPENAI_API_KEY is not set.");

    var identityRepository = new InMemoryIdentityRepository();
    var invitationRepository = new InMemoryInvitationRepository();
    var resolver = new AuthContextResolver(identityRepository);
    var meService = new MeService(resolver);
    var userAdminService = new UserAdminService(identityRepository, Clock.systemUTC());
    var invitationService = new InvitationService(identityRepository, invitationRepository, Clock.systemUTC());
    var agentRepository = new InMemoryAgentBehaviorRepository();
    new AgentBehaviorSeedLoader(agentRepository, Clock.systemUTC()).importStarterDefaults("tenant-1", "real-provider-smoke", "corr-real-provider-seed");
    var agentRuntimeService = new AgentRuntimeService(agentRepository, resolver, Clock.systemUTC());
    var service = new WorkstreamService(meService, resolver, new UserDirectoryView(userAdminService), new InvitationView(invitationService), userAdminService, invitationService, agentRepository, agentRuntimeService);

    identityRepository.putTenant(new Tenant("tenant-1", "Tenant One", true));
    identityRepository.saveAccount(new Account("admin@example.test", null, "admin@example.test", "admin@example.test", AccountStatus.ACTIVE, "LINKED"));
    identityRepository.putProfile(new UserProfile("admin@example.test", "admin@example.test", "Tenant Admin", "Tenant", "Admin", null));
    identityRepository.putSettings(new UserSettings("admin@example.test", UserSettings.UiMode.LIGHT));
    identityRepository.putMembership(new Membership("membership-admin", "admin@example.test", ScopeType.TENANT, "tenant-1", null, List.of(FoundationRole.TENANT_ADMIN, FoundationRole.AUDITOR), MembershipStatus.ACTIVE, false, null));

    var response = service.submitMessage(
        new WorkosIdentity("workos-admin", "admin@example.test", "Tenant Admin"),
        "membership-admin",
        new WorkstreamService.WorkstreamMessageRequest(
            "membership-admin",
            "agent-user-admin",
            "Real provider smoke: reply with one short markdown sentence confirming the workstream runtime is reachable. Do not include secrets.",
            "corr-real-provider-smoke",
            "idem-real-provider-smoke"),
        "corr-real-provider-header");

    var markdown = response.surface().data().get("markdown").toString();
    assertEquals("markdown_response", response.surface().surfaceType());
    assertEquals("agent-user-admin", response.surface().ownerFunctionalAgentId());
    assertEquals("ready", response.agentItem().status());
    assertNull(response.agentItem().body(), "Successful model text must render from the markdown_response surface, not from placeholder item copy");
    assertFalse(markdown.isBlank());
    assertFalse(markdown.contains(apiKey), "Provider secret leaked into markdown response");
    assertFalse(response.toString().contains(apiKey), "Provider secret leaked into workstream response DTO");
    assertTrue(response.surface().traceIds().size() >= 3, "Expected prompt/model/work trace ids");
    assertTrue(agentRuntimeService.traces().stream().anyMatch(trace -> trace.traceType().equals("PROMPT_ASSEMBLY")));
    assertTrue(agentRuntimeService.traces().stream().anyMatch(trace -> trace.traceType().equals("MODEL_INVOCATION")));
    assertTrue(agentRuntimeService.traces().stream().anyMatch(trace -> trace.traceType().equals("AgentWorkTrace")));
    assertTrue(agentRuntimeService.traces().stream().noneMatch(trace -> trace.toString().contains(apiKey)), "Provider secret leaked into runtime traces");
    assertNotNull(response.surface().data().get("trace"));
  }

  private static String trimToNull(String value) {
    if (value == null || value.isBlank()) return null;
    return value.trim();
  }
}
