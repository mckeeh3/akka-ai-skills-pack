package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import akka.javasdk.testkit.EventingTestKit;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.agent.SessionMemoryEntity;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SessionMemoryAlertsConsumerIntegrationTest extends TestKitSupport {

  private EventingTestKit.IncomingMessages sessionMemoryEvents;
  private EventingTestKit.OutgoingMessages alertTopic;

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withEventSourcedEntityIncomingMessages(SessionMemoryEntity.class)
        .withTopicOutgoingMessages("session-memory-alerts");
  }

  @BeforeEach
  void setUpEventing() {
    sessionMemoryEvents = testKit.getEventSourcedEntityIncomingMessages(SessionMemoryEntity.class);
    alertTopic = testKit.getTopicOutgoingMessages("session-memory-alerts");
    alertTopic.clear();
  }

  @Test
  void aiMessagesOverThresholdProduceAlertEvents() {
    sessionMemoryEvents.publish(
        new SessionMemoryEntity.Event.AiMessageAdded(
            Instant.now(),
            "activity-worker-agent",
            "Long answer",
            100,
            900,
            java.util.List.of(),
            Optional.empty(),
            Optional.empty(),
            Map.of()),
        "session-memory-1");

    var alert =
        alertTopic.expectOneTyped(SessionMemoryAlertsConsumer.SessionMemoryAlert.class);

    assertEquals("session-memory-1", alert.getPayload().sessionId());
    assertEquals("activity-worker-agent", alert.getPayload().componentId());
    assertEquals(900L, alert.getPayload().historySizeInBytes());
    assertEquals("session-memory-1", alert.getMetadata().get("ce-subject").orElseThrow());
  }
}
