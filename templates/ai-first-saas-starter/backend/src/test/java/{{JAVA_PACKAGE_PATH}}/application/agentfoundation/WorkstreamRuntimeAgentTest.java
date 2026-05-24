package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.JsonSupport;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import java.util.List;
import org.junit.jupiter.api.Test;

class WorkstreamRuntimeAgentTest extends TestKitSupport {
  private static final String MODEL_ALIAS = "openai-low-temperature";
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
                    "agent-user-admin",
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
                            "agent-user-admin",
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
                            "agent-user-admin",
                            "corr-secret-model",
                            "Hello",
                            List.of("trace-prompt-3"))));

    assertTrue(failure.getMessage().contains("model provider alias must not contain secrets"));
  }
}
