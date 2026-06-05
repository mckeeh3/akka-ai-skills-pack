package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.JsonSupport;
import akka.javasdk.agent.SessionHistory;
import akka.javasdk.agent.SessionMemoryEntity;
import akka.javasdk.agent.SessionMessage;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class SessionMemoryCompactionConsumerIntegrationTest extends TestKitSupport {

  private final TestModelProvider compactionModel = new TestModelProvider();

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withAdditionalConfig("akka.javasdk.agent.openai.api-key = n/a")
        .withModelProvider(SessionMemoryCompactionAgent.class, compactionModel);
  }

  @Test
  void oversizedHistoryCanBeCompactedToTwoSummaryMessages() {
    compactionModel.fixedResponse(
        JsonSupport.encodeToString(
            new SessionMemoryCompactionAgent.Result(
                "User wants one compact summary of the discussion.",
                "Assistant summarized the discussion into a shorter answer.")));

    var sessionId = "compact-session-1";
    var now = Instant.now();
    var longUserText = "U".repeat(320);
    var longAiText = "A".repeat(320);

    componentClient
        .forEventSourcedEntity(sessionId)
        .method(SessionMemoryEntity::addInteraction)
        .invoke(
            new SessionMemoryEntity.AddInteractionCmd(
                new SessionMessage.UserMessage(now, longUserText, "activity-agent"),
                List.of(new SessionMessage.AiMessage(now, longAiText, "activity-agent"))));

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              SessionHistory history =
                  componentClient
                      .forEventSourcedEntity(sessionId)
                      .method(SessionMemoryEntity::getHistory)
                      .invoke(new SessionMemoryEntity.GetHistoryCmd());

              assertEquals(2, history.messages().size());
              assertTrue(history.messages().get(0) instanceof SessionMessage.UserMessage);
              assertTrue(history.messages().get(1) instanceof SessionMessage.AiMessage);

              var userMessage = (SessionMessage.UserMessage) history.messages().get(0);
              var aiMessage = (SessionMessage.AiMessage) history.messages().get(1);

              assertEquals(SessionMemoryCompactionAgent.COMPONENT_ID, userMessage.componentId());
              assertEquals(SessionMemoryCompactionAgent.COMPONENT_ID, aiMessage.componentId());
              assertTrue(userMessage.text().contains("compact summary"));
              assertTrue(aiMessage.text().contains("summarized the discussion"));
            });
  }
}
