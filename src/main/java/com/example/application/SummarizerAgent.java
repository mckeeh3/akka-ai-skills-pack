package com.example.application;

import akka.javasdk.agent.Agent;
import akka.javasdk.annotations.Component;
import java.util.Collection;

/** Final summarizer for dynamic multi-agent orchestration. */
@Component(
    id = "summarizer-agent",
    name = "Summarizer Agent",
    description = "Combines worker-agent outputs into one final answer for the user.")
public class SummarizerAgent extends Agent {

  public record Request(String question, Collection<String> agentResponses) {}

  private static final String SYSTEM_MESSAGE =
      """
      You are a summarizer.
      Combine the worker-agent responses into one concise final answer for the user.
      Preserve useful details and avoid repeating the same idea.
      """
          .stripIndent();

  public Effect<String> summarize(Request request) {
    var joinedResponses = String.join("\n\n", request.agentResponses());
    var userMessage =
        """
        Original question:
        %s

        Worker responses:
        %s
        """
            .stripIndent()
            .formatted(request.question(), joinedResponses);

    return effects().systemMessage(SYSTEM_MESSAGE).userMessage(userMessage).thenReply();
  }
}
