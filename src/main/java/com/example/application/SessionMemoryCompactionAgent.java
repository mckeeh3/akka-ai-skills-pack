package com.example.application;

import akka.javasdk.agent.Agent;
import akka.javasdk.agent.MemoryProvider;
import akka.javasdk.agent.SessionHistory;
import akka.javasdk.agent.SessionMessage;
import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Description;
import java.util.stream.Collectors;

/** Agent example that summarizes session history into one user message and one AI message. */
@Component(id = "session-memory-compaction-agent")
public class SessionMemoryCompactionAgent extends Agent {

  public static final String COMPONENT_ID = "session-memory-compaction-agent";

  private static final String SYSTEM_MESSAGE =
      """
      You compact an interaction history into two messages.
      Return one userMessage that preserves the user's intent and one aiMessage that preserves the
      assistant's useful answer and relevant tool outcomes.
      Respond only with JSON matching the provided schema.
      """
          .stripIndent();

  public record Result(
      @Description("Condensed replacement user message") String userMessage,
      @Description("Condensed replacement AI message") String aiMessage) {}

  public Effect<Result> summarizeSessionHistory(SessionHistory history) {
    var userMessage =
        history.messages().stream()
            .map(SessionMemoryCompactionAgent::formatMessage)
            .collect(Collectors.joining("\n\n"));

    return effects()
        .memory(MemoryProvider.none())
        .systemMessage(SYSTEM_MESSAGE)
        .userMessage(userMessage)
        .responseConformsTo(Result.class)
        .thenReply();
  }

  private static String formatMessage(SessionMessage message) {
    return switch (message) {
      case SessionMessage.UserMessage user -> "USER: " + user.text();
      case SessionMessage.MultimodalUserMessage user ->
          "USER: " + user.text().orElse("[multimodal message]");
      case SessionMessage.AiMessage ai -> "AI: " + ai.text();
      case SessionMessage.ToolCallResponse tool ->
          "TOOL_CALL_RESPONSE[" + tool.name() + "]: " + tool.text();
    };
  }
}
