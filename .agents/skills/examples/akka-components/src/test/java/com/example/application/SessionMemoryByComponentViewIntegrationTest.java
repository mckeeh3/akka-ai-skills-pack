package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.agent.SessionMemoryEntity;
import akka.javasdk.testkit.EventingTestKit;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class SessionMemoryByComponentViewIntegrationTest extends TestKitSupport {

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT.withEventSourcedEntityIncomingMessages(SessionMemoryEntity.class);
  }

  @Test
  void sessionMemoryEventsBuildQueryableRowsByLastComponent() {
    EventingTestKit.IncomingMessages sessionMemoryEvents =
        testKit.getEventSourcedEntityIncomingMessages(SessionMemoryEntity.class);

    sessionMemoryEvents.publish(
        new SessionMemoryEntity.Event.UserMessageAdded(
            Instant.now(), "activity-worker-agent", "Need one outdoor idea", 30),
        "session-view-1");
    sessionMemoryEvents.publish(
        new SessionMemoryEntity.Event.AiMessageAdded(
            Instant.now(),
            "activity-worker-agent",
            "Try a bike ride.",
            40,
            70,
            java.util.List.of(),
            Optional.empty(),
            Optional.empty(),
            Map.of()),
        "session-view-1");

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var result =
                  componentClient
                      .forView()
                      .method(SessionMemoryByComponentView::getByComponent)
                      .invoke(new SessionMemoryByComponentView.FindByComponent("activity-worker-agent"));

              assertEquals(1, result.sessions().size());
              var row = result.sessions().getFirst();
              assertEquals("session-view-1", row.sessionId());
              assertEquals(2, row.messageCount());
              assertEquals(70L, row.historySizeInBytes());
              assertTrue(row.lastComponentId().equals("activity-worker-agent"));
            });
  }
}
