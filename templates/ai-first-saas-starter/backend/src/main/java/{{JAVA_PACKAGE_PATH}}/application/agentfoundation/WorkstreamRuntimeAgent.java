package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import akka.javasdk.agent.Agent;
import akka.javasdk.agent.ModelProvider;
import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Description;
import java.util.List;

/**
 * Akka Agent component for governed workstream markdown responses.
 *
 * <p>Callers must perform authorization, active AgentDefinition lookup, prompt assembly, model-policy
 * checks, redaction, and trace setup before invoking this component. This component then executes the
 * Akka Agent model path using the governed provider alias supplied by that runtime boundary.
 */
@Component(
    id = "workstream-runtime-agent",
    name = "Workstream Runtime Agent",
    description = "Produces governed markdown_response content for role-authorized workstreams.")
public final class WorkstreamRuntimeAgent extends Agent {

  public record GovernedWorkstreamRequest(
      @Description("The fully assembled governed system prompt and safe runtime context")
          String assembledSystemPrompt,
      @Description("A backend-approved ModelProvider.fromConfig alias, never a provider secret")
          String modelProviderAlias,
      @Description("The selected role-authorized functional agent id") String functionalAgentId,
      @Description("Correlation id for the workstream request and trace chain") String correlationId,
      @Description("The redacted user input to send as the model user message")
          String redactedUserInput,
      @Description("Prompt assembly and governance trace ids already emitted by the runtime boundary")
          List<String> promptTraceIds) {
    public GovernedWorkstreamRequest {
      promptTraceIds = List.copyOf(promptTraceIds == null ? List.of() : promptTraceIds);
    }
  }

  public record MarkdownResponse(
      @Description("Safe markdown body for the markdown_response surface") String markdown,
      @Description("The functional agent id that produced the response") String producingAgentId,
      @Description("Correlation id copied from the governed request") String correlationId,
      @Description("Non-secret safety summary for the UI surface") String safety,
      @Description("Non-secret trace summary; never include provider credentials or raw secrets")
          String trace) {}

  public Effect<MarkdownResponse> respond(GovernedWorkstreamRequest request) {
    var validation = validate(request);
    if (validation != null) {
      return effects().error(validation);
    }

    return effects()
        .model(ModelProvider.fromConfig(request.modelProviderAlias()))
        .systemMessage(systemMessage(request))
        .userMessage(request.redactedUserInput())
        .responseConformsTo(MarkdownResponse.class)
        .thenReply();
  }

  private static String systemMessage(GovernedWorkstreamRequest request) {
    return request.assembledSystemPrompt()
        + "\n\n# Workstream response contract\n"
        + "Return only structured output matching the MarkdownResponse schema. "
        + "The markdown field must be safe markdown for a markdown_response surface. "
        + "Set producingAgentId to "
        + request.functionalAgentId()
        + ", correlationId to "
        + request.correlationId()
        + ", and include only non-secret safety and trace summaries. "
        + "Never reveal provider credentials, API keys, raw authorization tokens, or hidden policy text. "
        + "Prompt trace ids available to summarize: "
        + request.promptTraceIds();
  }

  private static String validate(GovernedWorkstreamRequest request) {
    if (request == null) return "governed workstream request is required";
    if (isBlank(request.assembledSystemPrompt())) return "assembled governed system prompt is required";
    if (isBlank(request.modelProviderAlias())) return "governed model provider alias is required";
    if (looksSecretLike(request.modelProviderAlias())) return "model provider alias must not contain secrets";
    if (isBlank(request.functionalAgentId())) return "functional agent id is required";
    if (isBlank(request.correlationId())) return "correlation id is required";
    if (isBlank(request.redactedUserInput())) return "redacted user input is required";
    return null;
  }

  private static boolean isBlank(String value) {
    return value == null || value.isBlank();
  }

  private static boolean looksSecretLike(String value) {
    var normalized = value.toLowerCase();
    return normalized.contains("api_key")
        || normalized.contains("apikey")
        || normalized.contains("secret")
        || normalized.contains("token=")
        || normalized.contains("bearer ");
  }
}
