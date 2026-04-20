package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.JsonSupport;
import akka.javasdk.testkit.TestKitSupport;
import java.time.Duration;
import org.junit.jupiter.api.Test;

class CounterStreamEndpointIntegrationTest extends TestKitSupport {

  @Test
  void sseRouteStreamsDeterministicEvents() throws Exception {
    var events = testKit.getSelfSseRouteTester().receiveFirstN("/counter-stream/numbers", 3, Duration.ofSeconds(5));

    assertEquals(3, events.size());
    assertEquals("1", events.get(0).getId().orElseThrow());
    assertEquals("counter", events.get(0).getEventType().orElseThrow());
    assertEquals(1, JsonSupport.getObjectMapper().readTree(events.get(0).getData()).get("value").asInt());
    assertEquals(3, JsonSupport.getObjectMapper().readTree(events.get(2).getData()).get("value").asInt());
  }

  @Test
  void sseRouteResumesFromLastSeenEventId() throws Exception {
    var events =
        testKit
            .getSelfSseRouteTester()
            .receiveNFromOffset("/counter-stream/numbers", 3, "2", Duration.ofSeconds(5));

    assertEquals(3, events.size());
    assertEquals("3", events.get(0).getId().orElseThrow());
    assertEquals(5, JsonSupport.getObjectMapper().readTree(events.get(2).getData()).get("value").asInt());
    assertTrue(events.stream().allMatch(event -> event.getEventType().orElseThrow().equals("counter")));
  }
}
