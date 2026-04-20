package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.JsonSupport;
import akka.javasdk.testkit.EventingTestKit.IncomingMessages;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import com.example.domain.DraftCart;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class DraftCartViewStreamEndpointIntegrationTest extends TestKitSupport {

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT.withKeyValueEntityIncomingMessages(DraftCartEntity.class);
  }

  @Test
  void viewBackedSseStreamsInitialRowsAndLaterUpdates() throws Exception {
    IncomingMessages draftCartUpdates = testKit.getKeyValueEntityIncomingMessages(DraftCartEntity.class);

    draftCartUpdates.publish(
        new DraftCart.State(
            "draft-stream-1",
            List.of(new DraftCart.LineItem("sku-1", "Akka T-Shirt", 1)),
            true),
        "draft-stream-1");
    draftCartUpdates.publish(
        new DraftCart.State(
            "draft-stream-2",
            List.of(new DraftCart.LineItem("sku-2", "Akka Socks", 1)),
            false),
        "draft-stream-2");

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var result =
                  componentClient
                      .forView()
                      .method(DraftCartsByCheckedOutView::getCarts)
                      .invoke(new DraftCartsByCheckedOutView.FindByCheckedOut(true));

              assertEquals(1, result.carts().size());
              assertEquals("draft-stream-1", result.carts().getFirst().cartId());
            });

    var publisher =
        new Thread(
            () -> {
              try {
                Thread.sleep(500);
              } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
              }
              draftCartUpdates.publish(
                  new DraftCart.State(
                      "draft-stream-2",
                      List.of(new DraftCart.LineItem("sku-2", "Akka Socks", 1)),
                      true),
                  "draft-stream-2");
            });
    publisher.start();

    var events =
        testKit
            .getSelfSseRouteTester()
            .receiveFirstN("/view-streams/draft-carts/checked-out/true", 2, Duration.ofSeconds(10));

    publisher.join();

    assertEquals(2, events.size());
    assertTrue(events.stream().allMatch(event -> event.getId().isPresent()));

    var cartIds =
        Set.of(
            JsonSupport.getObjectMapper().readTree(events.get(0).getData()).get("cartId").asText(),
            JsonSupport.getObjectMapper().readTree(events.get(1).getData()).get("cartId").asText());

    assertEquals(Set.of("draft-stream-1", "draft-stream-2"), cartIds);
  }
}
