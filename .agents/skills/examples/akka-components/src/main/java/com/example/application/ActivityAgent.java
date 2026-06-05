package com.example.application;

import akka.javasdk.agent.Agent;
import akka.javasdk.agent.JsonParsingException;
import akka.javasdk.agent.MemoryProvider;
import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Description;

/**
 * Focused agent example for prompt design, structured responses, bounded memory, and fallback
 * handling.
 */
@Component(
    id = "activity-agent",
    name = "Activity Agent",
    description =
        "Suggests one concrete real-world activity that fits the user's request and context.")
public class ActivityAgent extends Agent {

  private static final String SYSTEM_MESSAGE =
      """
      You are an activity recommendation agent.
      Suggest one concrete real-world activity that best fits the user's request.
      Keep the response practical and concise.
      Explain why the activity fits and whether it is best classified as indoor or outdoor.
      """
          .stripIndent();

  public record ActivitySuggestion(
      @Description("Short name of the recommended activity") String name,
      @Description("Why this activity fits the user's request") String reason,
      @Description("Either indoor or outdoor") String setting) {

    static ActivitySuggestion fallback() {
      return new ActivitySuggestion(
          "Walk",
          "A short walk is usually a safe fallback recommendation when structured generation fails.",
          "outdoor");
    }
  }

  public Effect<ActivitySuggestion> suggest(String message) {
    return effects()
        .memory(MemoryProvider.limitedWindow().readLast(6))
        .systemMessage(SYSTEM_MESSAGE)
        .userMessage(message)
        .responseConformsTo(ActivitySuggestion.class)
        .onFailure(
            error -> {
              if (error instanceof JsonParsingException) {
                return ActivitySuggestion.fallback();
              } else if (error instanceof RuntimeException runtimeException) {
                throw runtimeException;
              } else {
                throw new RuntimeException(error);
              }
            })
        .thenReply();
  }
}
