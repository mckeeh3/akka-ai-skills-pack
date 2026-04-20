package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.Metadata;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import com.example.domain.ShoppingCart;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class ShoppingCartTopicViewIntegrationTest extends TestKitSupport {

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT.withTopicIncomingMessages("shopping-cart-events");
  }

  @Test
  void topicMessagesPopulateTheViewUsingCeSubjectMetadata() {
    var topicMessages = testKit.getTopicIncomingMessages("shopping-cart-events");
    var builder = testKit.getMessageBuilder();

    Metadata metadata = Metadata.EMPTY.add("ce-subject", "cart-topic-1");
    topicMessages.publish(
        builder.of(
            new ShoppingCart.Event.ItemAdded(new ShoppingCart.LineItem("sku-1", "Akka T-Shirt", 1)),
            metadata));
    topicMessages.publish(
        builder.of(new ShoppingCart.Event.ItemRemoved("sku-1"), metadata));
    topicMessages.publish(builder.of(new ShoppingCart.Event.CheckedOut(), metadata));

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var result =
                  componentClient
                      .forView()
                      .method(ShoppingCartTopicView::getByStatus)
                      .invoke(new ShoppingCartTopicView.FindByStatus("CHECKED_OUT"));

              assertEquals(1, result.entries().size());
              var row = result.entries().getFirst();
              assertEquals("cart-topic-1", row.cartId());
              assertEquals(1, row.eventCount());
              assertTrue(row.localOrigin());
              assertEquals(testKit.getActorSystem().settings().config().getString("akka.javasdk.runtime.self.region"), row.originRegion());
            });
  }

  @Test
  void deletedTopicMessageRemovesTheRow() {
    var topicMessages = testKit.getTopicIncomingMessages("shopping-cart-events");
    var builder = testKit.getMessageBuilder();
    Metadata metadata = Metadata.EMPTY.add("ce-subject", "cart-topic-delete");

    topicMessages.publish(
        builder.of(
            new ShoppingCart.Event.ItemAdded(new ShoppingCart.LineItem("sku-1", "Akka Mug", 1)),
            metadata));
    topicMessages.publish(builder.of(new ShoppingCart.Event.Deleted(), metadata));

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var result =
                  componentClient
                      .forView()
                      .method(ShoppingCartTopicView::getByStatus)
                      .invoke(new ShoppingCartTopicView.FindByStatus("ACTIVE"));

              assertTrue(result.entries().isEmpty());
            });
  }
}
