package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import {{JAVA_BASE_PACKAGE}}.domain.security.AuthContext;
import {{JAVA_BASE_PACKAGE}}.domain.security.FoundationRole;
import {{JAVA_BASE_PACKAGE}}.domain.security.ScopeType;
import akka.javasdk.JsonSupport;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import java.util.List;
import org.junit.jupiter.api.Test;

class WorkstreamRuntimeAgentTest extends TestKitSupport {
  private static final String MODEL_ALIAS = "openai-low-temperature";
  private static final String TENANT_ID = "tenant-starter";
  private final TestModelProvider workstreamRuntimeModelTestProvider = new TestModelProvider();

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
        .withModelProvider(WorkstreamRuntimeAgent.class, workstreamRuntimeModelTestProvider);
  }

  @Test
  void respondsWithStructuredMarkdownSurfacePayloadThroughAkkaAgentModelPath() {
    workstreamRuntimeModelTestProvider.fixedResponse(
        JsonSupport.encodeToString(
            new WorkstreamRuntimeAgent.MarkdownResponse(
                "## User Admin\n\nInvite, review, or adjust memberships from governed actions.",
                "agent-user-admin",
                "corr-agent-runtime",
                "safe markdown_response generated without provider secrets",
                "promptTraceIds=[trace-prompt-1]; modelConfigRef=starter-default-model")));

    var response =
        componentClient
            .forAgent()
            .inSession("workstream-runtime-agent-test-session")
            .method(WorkstreamRuntimeAgent::respond)
            .invoke(
                new WorkstreamRuntimeAgent.GovernedWorkstreamRequest(
                    "# Governed prompt\nUse backend capabilities only. OPENAI_API_KEY is never visible.",
                    MODEL_ALIAS,
                    TENANT_ID,
                    AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID,
                    tenantAdmin(),
                    "runtime",
                    AgentRuntimeService.INVOKE_CAPABILITY,
                    "corr-agent-runtime",
                    "What can I do next?",
                    List.of("trace-prompt-1")));

    assertEquals("agent-user-admin", response.producingAgentId());
    assertEquals("corr-agent-runtime", response.correlationId());
    assertTrue(response.markdown().contains("## User Admin"));
    assertTrue(response.safety().contains("without provider secrets"));
    assertTrue(response.trace().contains("trace-prompt-1"));
  }

  @Test
  void rejectsMissingGovernedModelAliasInsteadOfUsingImplicitFallback() {
    var failure =
        assertThrows(
            RuntimeException.class,
            () ->
                componentClient
                    .forAgent()
                    .inSession("workstream-runtime-missing-model-session")
                    .method(WorkstreamRuntimeAgent::respond)
                    .invoke(
                        new WorkstreamRuntimeAgent.GovernedWorkstreamRequest(
                            "# Governed prompt",
                            " ",
                            TENANT_ID,
                            AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID,
                            tenantAdmin(),
                            "runtime",
                            AgentRuntimeService.INVOKE_CAPABILITY,
                            "corr-missing-model",
                            "Hello",
                            List.of("trace-prompt-2"))));

    assertTrue(failure.getMessage().contains("governed model provider alias is required"));
  }

  @Test
  void rejectsSecretLikeModelAliasBeforeModelInvocation() {
    var failure =
        assertThrows(
            RuntimeException.class,
            () ->
                componentClient
                    .forAgent()
                    .inSession("workstream-runtime-secret-model-session")
                    .method(WorkstreamRuntimeAgent::respond)
                    .invoke(
                        new WorkstreamRuntimeAgent.GovernedWorkstreamRequest(
                            "# Governed prompt",
                            "openai-low-temperature api_key=hidden",
                            TENANT_ID,
                            AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID,
                            tenantAdmin(),
                            "runtime",
                            AgentRuntimeService.INVOKE_CAPABILITY,
                            "corr-secret-model",
                            "Hello",
                            List.of("trace-prompt-3"))));

    assertTrue(failure.getMessage().contains("model provider alias must not contain secrets"));
  }

  @Test
  void rejectsMissingRuntimeToolContextBeforeModelInvocation() {
    var failure =
        assertThrows(
            RuntimeException.class,
            () ->
                componentClient
                    .forAgent()
                    .inSession("workstream-runtime-missing-tools-session")
                    .method(WorkstreamRuntimeAgent::respond)
                    .invoke(
                        new WorkstreamRuntimeAgent.GovernedWorkstreamRequest(
                            "# Governed prompt",
                            MODEL_ALIAS,
                            " ",
                            AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID,
                            tenantAdmin(),
                            "runtime",
                            AgentRuntimeService.INVOKE_CAPABILITY,
                            "corr-missing-tools",
                            "Hello",
                            List.of("trace-prompt-4"))));

    assertTrue(failure.getMessage().contains("tenant id is required for governed runtime tools"));
  }

  @Test
  void rejectsInconsistentRuntimeToolContextBeforeModelInvocation() {
    var failure =
        assertThrows(
            RuntimeException.class,
            () ->
                componentClient
                    .forAgent()
                    .inSession("workstream-runtime-tool-mismatch-session")
                    .method(WorkstreamRuntimeAgent::respond)
                    .invoke(
                        new WorkstreamRuntimeAgent.GovernedWorkstreamRequest(
                            "# Governed prompt",
                            MODEL_ALIAS,
                            "tenant-other",
                            AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID,
                            tenantAdmin(),
                            "runtime",
                            AgentRuntimeService.INVOKE_CAPABILITY,
                            "corr-tool-mismatch",
                            "Hello",
                            List.of("trace-prompt-5"))));

    assertTrue(failure.getMessage().contains("governed runtime tool context denied before model invocation"));
    assertTrue(failure.getMessage().contains("runtime-tool-tenant-mismatch"));
  }

  private static AuthContext tenantAdmin() {
    return new AuthContext(
        "starter-admin",
        "workos-starter-admin",
        "membership-starter-admin",
        ScopeType.TENANT,
        TENANT_ID,
        null,
        List.of(FoundationRole.TENANT_ADMIN),
        List.of("agent.user_admin.use", "agent.behavior.manage", "tenant.user.read", "tenant.audit.read"));
  }
}
