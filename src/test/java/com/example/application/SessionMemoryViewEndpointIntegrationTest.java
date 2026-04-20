package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.agent.SessionMemoryEntity;
import akka.javasdk.testkit.EventingTestKit;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import com.example.api.SessionMemoryViewEndpoint;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class SessionMemoryViewEndpointIntegrationTest extends TestKitSupport {

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT.withEventSourcedEntityIncomingMessages(SessionMemoryEntity.class);
  }

  @Test
  void endpointReturnsSessionsIndexedByLastComponent() {
    EventingTestKit.IncomingMessages sessionMemoryEvents =
        testKit.getEventSourcedEntityIncomingMessages(SessionMemoryEntity.class);

    sessionMemoryEvents.publish(
        new SessionMemoryEntity.Event.UserMessageAdded(
            Instant.now(), "activity-worker-agent", "Need one idea", 20),
        "session-http-1");
    sessionMemoryEvents.publish(
        new SessionMemoryEntity.Event.AiMessageAdded(
            Instant.now(),
            "activity-worker-agent",
            "Try a museum visit.",
            30,
            50,
            java.util.List.of(),
            Optional.empty(),
            Optional.empty(),
            Map.of()),
        "session-http-1");

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var response =
                  httpClient
                      .GET("/agent-memory/components/activity-worker-agent")
                      .responseBodyAs(SessionMemoryViewEndpoint.SessionMemoryResponse.class)
                      .invoke();

              assertTrue(response.status().isSuccess());
              assertEquals(1, response.body().sessions().size());
              var row = response.body().sessions().getFirst();
              assertEquals("session-http-1", row.sessionId());
              assertEquals("activity-worker-agent", row.lastComponentId());
              assertEquals(2, row.messageCount());
              assertEquals(50L, row.historySizeInBytes());
            });
  }
}
