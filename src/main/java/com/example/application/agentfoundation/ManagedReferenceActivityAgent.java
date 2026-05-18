package com.example.application.agentfoundation;

import akka.javasdk.agent.Agent;
import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Description;

/**
 * Minimal managed-agent reference that receives an already assembled governed prompt from the
 * runtime resolver instead of owning static behavior text.
 */
@Component(id = "managed-reference-activity-agent")
public class ManagedReferenceActivityAgent extends Agent {

  public record ManagedActivityRequest(
      @Description("Governed system prompt assembled by ReferenceAgentRuntimeResolver")
          String assembledPrompt,
      @Description("User request for the activity recommendation") String userMessage,
      @Description("Correlation id linking prompt assembly, skill loads, and work trace")
          String correlationId) {}

  public record ManagedActivitySuggestion(
      @Description("Short name of the recommended activity") String name,
      @Description("Why this activity fits the user's request") String reason,
      @Description("Either indoor or outdoor") String setting) {}

  public Effect<ManagedActivitySuggestion> suggest(ManagedActivityRequest request) {
    return effects()
        .systemMessage(request.assembledPrompt())
        .userMessage(request.userMessage())
        .responseConformsTo(ManagedActivitySuggestion.class)
        .thenReply();
  }
}
