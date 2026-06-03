package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.JsonSupport;
import akka.javasdk.agent.SessionMemoryEntity;
import akka.javasdk.testkit.EventingTestKit;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class SessionMemoryAlertStreamEndpointIntegrationTest extends TestKitSupport {

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT.withEventSourcedEntityIncomingMessages(SessionMemoryEntity.class);
  }

  @Test
  void sseEndpointStreamsThresholdAlertsForOneComponent() throws Exception {
    EventingTestKit.IncomingMessages sessionMemoryEvents =
        testKit.getEventSourcedEntityIncomingMessages(SessionMemoryEntity.class);

    sessionMemoryEvents.publish(
        new SessionMemoryEntity.Event.AiMessageAdded(
            Instant.now(),
            "activity-worker-agent",
            "Large response",
            100,
            700,
            java.util.List.of(),
            Optional.empty(),
            Optional.empty(),
            Map.of()),
        "alert-stream-1");

    var publisher =
        new Thread(
            () -> {
              try {
                Thread.sleep(500);
              } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
              }
              sessionMemoryEvents.publish(
                  new SessionMemoryEntity.Event.AiMessageAdded(
                      Instant.now(),
                      "activity-worker-agent",
                      "Another large response",
                      120,
                      900,
                      java.util.List.of(),
                      Optional.empty(),
                      Optional.empty(),
                      Map.of()),
                  "alert-stream-2");
            });
    publisher.start();

    var events =
        testKit
            .getSelfSseRouteTester()
            .receiveFirstN("/agent-memory/alerts/activity-worker-agent", 2, Duration.ofSeconds(10));

    publisher.join();

    assertEquals(2, events.size());
    assertTrue(events.stream().allMatch(event -> event.getId().isPresent()));

    var first = JsonSupport.getObjectMapper().readTree(events.get(0).getData());
    var second = JsonSupport.getObjectMapper().readTree(events.get(1).getData());

    assertEquals("activity-worker-agent", first.get("componentId").asText());
    assertEquals(700L, first.get("historySizeInBytes").asLong());
    assertEquals(900L, second.get("historySizeInBytes").asLong());
  }
}
