package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.CloudEvent;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import com.example.domain.ShoppingCart;
import java.net.URI;
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

    var itemAdded = new ShoppingCart.Event.ItemAdded(new ShoppingCart.LineItem("sku-1", "Akka T-Shirt", 1));
    var itemRemoved = new ShoppingCart.Event.ItemRemoved("sku-1");
    var checkedOut = new ShoppingCart.Event.CheckedOut();
    topicMessages.publish(builder.of(itemAdded, metadataFor("evt-add", itemAdded, "cart-topic-1")));
    topicMessages.publish(builder.of(itemRemoved, metadataFor("evt-remove", itemRemoved, "cart-topic-1")));
    topicMessages.publish(builder.of(checkedOut, metadataFor("evt-checkout", checkedOut, "cart-topic-1")));

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
            });
  }

  @Test
  void deletedTopicMessageRemovesTheRow() {
    var topicMessages = testKit.getTopicIncomingMessages("shopping-cart-events");
    var builder = testKit.getMessageBuilder();
    var itemAdded = new ShoppingCart.Event.ItemAdded(new ShoppingCart.LineItem("sku-1", "Akka Mug", 1));
    var deleted = new ShoppingCart.Event.Deleted();

    topicMessages.publish(builder.of(itemAdded, metadataFor("evt-delete-add", itemAdded, "cart-topic-delete")));
    topicMessages.publish(builder.of(deleted, metadataFor("evt-delete", deleted, "cart-topic-delete")));

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

  private akka.javasdk.Metadata metadataFor(String id, Object payload, String subject) {
    var typeName = payload.getClass().getAnnotation(akka.javasdk.annotations.TypeName.class).value();
    return CloudEvent.of(id, URI.create("ShoppingCartTopicViewIntegrationTest"), typeName)
        .withSubject(subject)
        .asMetadata()
        .add("Content-Type", "application/json");
  }
}
