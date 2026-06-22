package ai.first.application.foundation.identity;

import ai.first.domain.foundation.identity.Tenant;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import ai.first.domain.foundation.identity.WorkosIdentity;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import java.util.List;
import org.junit.jupiter.api.Test;
import ai.first.application.foundation.workstream.InMemoryTestWorkstreamLogRepository;
import ai.first.application.coreapp.workstream.WorkstreamService;

/** Optional real-provider smoke. Skips unless backend-only provider env is present. */
class RealModelProviderSmokeTest extends TestKitSupport {
  @Override
  protected TestKit.Settings testKitSettings() {
    var apiKey = trimToNull(System.getenv("OPENAI_API_KEY"));
    var modelId = firstNonBlank(System.getenv("OPENAI_MODEL_ID"), "gpt-4o-mini");
    var baseUrl = firstNonBlank(System.getenv("OPENAI_API_BASE_URL"), "https://api.openai.com/v1");
    var config = new StringBuilder()
        .append("akka.javasdk.agent.openai-low-temperature.provider = openai\n")
        .append("akka.javasdk.agent.openai-low-temperature.model-name = \"").append(hoconString(modelId)).append("\"\n")
        .append("akka.javasdk.agent.openai-low-temperature.api-key = \"").append(hoconString(apiKey == null ? "n/a" : apiKey)).append("\"\n")
        .append("akka.javasdk.agent.openai-low-temperature.base-url = \"").append(hoconString(baseUrl)).append("\"\n")
        // Keep sampling parameters at OpenAI defaults. Some supported OpenAI
        // models reject non-default temperature values, while Akka provider
        // configuration still requires these keys.
        .append("akka.javasdk.agent.openai-low-temperature.temperature = 1.0\n")
        .append("akka.javasdk.agent.openai-low-temperature.top-p = 1.0\n")
        .append("akka.javasdk.agent.openai-low-temperature.max-tokens = -1\n")
        .append("akka.javasdk.agent.openai-low-temperature.max-completion-tokens = -1\n")
        .append("akka.javasdk.agent.openai-low-temperature.connection-timeout = 15s\n")
        .append("akka.javasdk.agent.openai-low-temperature.response-timeout = 1m\n")
        .append("akka.javasdk.agent.openai-low-temperature.max-retries = 2\n")
        .append("akka.javasdk.agent.openai-low-temperature.thinking = false\n")
        .append("akka.javasdk.agent.openai-low-temperature.additional-model-request-headers = []\n");
    return TestKit.Settings.DEFAULT.withAdditionalConfig(config.toString());
  }

  @Test
  void workstreamMessageSubmissionUsesRealProviderAndEmitsTraceShape() {
    assumeTrue(Boolean.getBoolean("realModelProviderSmoke"), "Skipping real model provider smoke because -DrealModelProviderSmoke=true was not provided.");
    var apiKey = trimToNull(System.getenv("OPENAI_API_KEY"));
    assumeTrue(apiKey != null, "Skipping real model provider smoke because OPENAI_API_KEY is not set.");

    StarterSecurityComponents.startup();
    StarterSecurityComponents.bindAkkaRuntime(componentClient);
    BootstrapAdminSeeder.seedFixtureAdmins(
        StarterSecurityComponents.identityRepository(),
        "admin@example.test:TENANT_ADMIN:" + BootstrapAdminSeeder.DEFAULT_TENANT_ID);
    StarterSecurityComponents.agentBehaviorSeedLoader()
        .importStarterDefaults(BootstrapAdminSeeder.DEFAULT_TENANT_ID, "real-provider-smoke", "corr-real-provider-seed");
    var meService = StarterSecurityComponents.meService();
    var service = StarterSecurityComponents.workstreamService(componentClient, new InMemoryTestWorkstreamLogRepository());
    var agentRuntimeService = StarterSecurityComponents.agentRuntimeService();
    var selectedContextId = "membership-admin@example.test";

    var identity = new WorkosIdentity("workos-admin", "admin@example.test", "Tenant Admin");
    var me = meService.me(identity, selectedContextId, "corr-real-provider-me");
    assertFalse(me.toString().contains(apiKey), "Provider secret leaked into /api/me response");

    for (var agentId : List.of("my-account-agent", "user-admin-agent", "agent-agent-admin", "agent-audit-trace", "governance-policy-agent")) {
      var response = service.submitMessage(
          identity,
          selectedContextId,
          new WorkstreamService.WorkstreamMessageRequest(
              selectedContextId,
              agentId,
              "Real provider smoke for " + agentId + ": reply with one short markdown sentence confirming the workstream runtime is reachable. Do not include secrets.",
              "corr-real-provider-smoke-" + agentId,
              "idem-real-provider-smoke-" + agentId),
          "corr-real-provider-header");

      var markdown = response.surface().data().get("markdown").toString();
      assertEquals("markdown_response", response.surface().surfaceType());
      assertEquals(agentId, response.surface().ownerFunctionalAgentId());
      assertEquals("ready", response.agentItem().status());
      assertNull(response.agentItem().body(), "Successful model text must render from the markdown_response surface, not from placeholder item copy");
      assertFalse(markdown.isBlank());
      assertFalse(markdown.contains(apiKey), "Provider secret leaked into markdown response for " + agentId);
      assertFalse(response.toString().contains(apiKey), "Provider secret leaked into workstream response DTO for " + agentId);
      assertTrue(response.surface().traceIds().size() >= 3, "Expected prompt/model/work trace ids for " + agentId);
      assertNotNull(response.surface().data().get("trace"));

      var persistedItems = service.items(identity, selectedContextId, agentId, "corr-real-provider-items-" + agentId);
      var persistedSurface = service.surface(identity, selectedContextId, response.surface().surfaceId(), "corr-real-provider-surface-" + agentId);
      assertFalse(persistedItems.toString().contains(apiKey), "Provider secret leaked into workstream items for " + agentId);
      assertFalse(persistedSurface.toString().contains(apiKey), "Provider secret leaked into persisted workstream surface for " + agentId);
    }

    assertTrue(agentRuntimeService.traces().stream().anyMatch(trace -> trace.traceType().equals("PROMPT_ASSEMBLY")));
    assertTrue(agentRuntimeService.traces().stream().anyMatch(trace -> trace.traceType().equals("MODEL_INVOCATION")));
    assertTrue(agentRuntimeService.traces().stream().anyMatch(trace -> trace.traceType().equals("AgentWorkTrace")));
    assertTrue(agentRuntimeService.traces().stream().noneMatch(trace -> trace.toString().contains(apiKey)), "Provider secret leaked into runtime traces");
  }

  private static String trimToNull(String value) {
    if (value == null || value.isBlank()) return null;
    return value.trim();
  }

  private static String firstNonBlank(String value, String fallback) {
    var trimmed = trimToNull(value);
    return trimmed == null ? fallback : trimmed;
  }

  private static String hoconString(String value) {
    return value.replace("\\", "\\\\").replace("\"", "\\\"");
  }
}
