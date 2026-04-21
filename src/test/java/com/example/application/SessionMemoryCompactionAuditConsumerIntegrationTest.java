package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import akka.javasdk.JsonSupport;
import akka.javasdk.agent.SessionMemoryEntity;
import akka.javasdk.agent.SessionMessage;
import akka.javasdk.testkit.EventingTestKit;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SessionMemoryCompactionAuditConsumerIntegrationTest extends TestKitSupport {

  private final TestModelProvider compactionModel = new TestModelProvider();
  private EventingTestKit.OutgoingMessages compactionTopic;

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withAdditionalConfig("akka.javasdk.agent.openai.api-key = n/a")
        .withModelProvider(SessionMemoryCompactionAgent.class, compactionModel)
        .withTopicOutgoingMessages("session-memory-compactions");
  }

  @BeforeEach
  void setUpTopic() {
    compactionTopic = testKit.getTopicOutgoingMessages("session-memory-compactions");
    compactionTopic.clear();
  }

  @Test
  void compactionFlowPublishesAuditEventToTopic() {
    compactionModel.fixedResponse(
        JsonSupport.encodeToString(
            new SessionMemoryCompactionAgent.Result(
                "User summary after compaction.", "Assistant summary after compaction.")));

    var sessionId = "compact-audit-session-1";
    var now = Instant.now();

    componentClient
        .forEventSourcedEntity(sessionId)
        .method(SessionMemoryEntity::addInteraction)
        .invoke(
            new SessionMemoryEntity.AddInteractionCmd(
                new SessionMessage.UserMessage(now, "U".repeat(320), "activity-agent"),
                List.of(new SessionMessage.AiMessage(now, "A".repeat(320), "activity-agent"))));

    var message =
        compactionTopic.expectOneTyped(
            SessionMemoryCompactionAudit.class);

    assertEquals(sessionId, message.getPayload().sessionId());
    assertEquals(SessionMemoryCompactionAgent.COMPONENT_ID, message.getPayload().compactedBy());
    assertEquals("session memory compacted", message.getPayload().reason());
    assertEquals(sessionId, message.getMetadata().get("ce-subject").orElseThrow());
  }
}
