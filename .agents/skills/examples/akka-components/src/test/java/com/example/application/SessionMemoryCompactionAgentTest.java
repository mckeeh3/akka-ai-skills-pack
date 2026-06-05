package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import akka.javasdk.JsonSupport;
import akka.javasdk.agent.SessionHistory;
import akka.javasdk.agent.SessionMessage;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class SessionMemoryCompactionAgentTest extends TestKitSupport {

  private final TestModelProvider compactionModel = new TestModelProvider();

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withAdditionalConfig("akka.javasdk.agent.openai.api-key = n/a")
        .withModelProvider(SessionMemoryCompactionAgent.class, compactionModel);
  }

  @Test
  void compactionPromptIncludesToolCallRequests() {
    compactionModel
        .whenMessage(
            message ->
                message.contains("TOOL_CALL_REQUEST[id=req-1, name=get_weather, args={\"city\":\"Stockholm\"}]")
                    && message.contains("TOOL_CALL_RESPONSE[get_weather]: sunny"))
        .reply(
            JsonSupport.encodeToString(
                new SessionMemoryCompactionAgent.Result(
                    "Condensed user message", "Condensed ai message")));

    var now = Instant.now();
    var history =
        new SessionHistory(
            List.of(
                new SessionMessage.UserMessage(now, "What should I do today?", "activity-agent"),
                new SessionMessage.AiMessage(
                    now,
                    "Let me check the weather.",
                    "weather-agent",
                    List.of(
                        new SessionMessage.ToolCallRequest(
                            "req-1", "get_weather", "{\"city\":\"Stockholm\"}"))),
                new SessionMessage.ToolCallResponse(
                    now, "weather-agent", "req-1", "get_weather", "sunny")),
            3L);

    var result =
        componentClient
            .forAgent()
            .inSession("compaction-agent-test-session")
            .method(SessionMemoryCompactionAgent::summarizeSessionHistory)
            .invoke(history);

    assertEquals("Condensed user message", result.userMessage());
    assertEquals("Condensed ai message", result.aiMessage());
  }
}
