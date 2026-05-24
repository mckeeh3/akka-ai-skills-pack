package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import akka.javasdk.agent.Agent;
import akka.javasdk.annotations.Component;

/**
 * Akka Agent component placeholder for governed workstream markdown responses.
 *
 * <p>The next runtime task wires {@link WorkstreamAgentRuntimeInvoker} to call this component from
 * the normal workstream message path. Keep this component model-backed; do not replace it with
 * deterministic production markdown.
 */
@Component(
    id = "workstream-runtime-agent",
    name = "Workstream Runtime Agent",
    description = "Produces governed markdown_response content for role-authorized workstreams.")
public final class WorkstreamRuntimeAgent extends Agent {

  public record GovernedWorkstreamRequest(
      String assembledSystemPrompt,
      String functionalAgentId,
      String correlationId,
      String redactedUserInput) {}

  public record MarkdownResponse(String markdown, String producingAgentId, String correlationId) {}

  public Effect<MarkdownResponse> respond(GovernedWorkstreamRequest request) {
    return effects()
        .systemMessage(request.assembledSystemPrompt())
        .userMessage(request.redactedUserInput())
        .responseConformsTo(MarkdownResponse.class)
        .thenReply();
  }
}
