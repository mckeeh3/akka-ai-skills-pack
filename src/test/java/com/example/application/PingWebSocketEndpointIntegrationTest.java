package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import akka.javasdk.testkit.TestKitSupport;
import org.junit.jupiter.api.Test;

class PingWebSocketEndpointIntegrationTest extends TestKitSupport {

  @Test
  void websocketRoundTripWorks() {
    var connection = testKit.getSelfWebSocketRouteTester().wsTextConnection("/websockets/ping");
    var publisher = connection.publisher();
    var subscriber = connection.subscriber();

    subscriber.request(2);

    publisher.sendNext("ping");
    assertEquals("pong", subscriber.expectNext());

    publisher.sendNext("akka");
    assertEquals("echo:akka", subscriber.expectNext());

    publisher.sendComplete();
    subscriber.expectComplete();
  }
}
