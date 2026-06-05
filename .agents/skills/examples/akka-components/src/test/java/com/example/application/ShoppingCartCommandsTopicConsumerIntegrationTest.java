package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.CloudEvent;
import akka.javasdk.testkit.EventingTestKit;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import com.example.domain.ShoppingCart;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ShoppingCartCommandsTopicConsumerIntegrationTest extends TestKitSupport {

  private EventingTestKit.IncomingMessages commandsTopic;
  private EventingTestKit.OutgoingMessages eventsTopic;

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withTopicIncomingMessages("shopping-cart-commands")
        .withTopicOutgoingMessages("shopping-cart-events");
  }

  @BeforeEach
  void setUpTopics() {
    commandsTopic = testKit.getTopicIncomingMessages("shopping-cart-commands");
    eventsTopic = testKit.getTopicOutgoingMessages("shopping-cart-events");
    eventsTopic.clear();
  }

  @Test
  void topicCommandsUpdateEntityAndPublishCartEvents() {
    var cartId = "cart-topic-consumer-1";
    var addItem = new ShoppingCartCommandsTopicConsumer.AddItem("sku-1", "Akka Hoodie", 2);
    var checkout = new ShoppingCartCommandsTopicConsumer.Checkout();
    var addItemMetadata = metadataFor("cmd-add", addItem, cartId);
    var checkoutMetadata = metadataFor("cmd-checkout", checkout, cartId);
    var builder = testKit.getMessageBuilder();

    commandsTopic.publish(builder.of(addItem, addItemMetadata));
    commandsTopic.publish(builder.of(checkout, checkoutMetadata));

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var cart =
                  componentClient
                      .forEventSourcedEntity(cartId)
                      .method(ShoppingCartEntity::getCart)
                      .invoke();

              assertEquals(cartId, cart.cartId());
              assertEquals(1, cart.items().size());
              assertTrue(cart.checkedOut());
            });

    var itemAdded = eventsTopic.expectOneTyped(ShoppingCart.Event.ItemAdded.class);
    var checkedOut = eventsTopic.expectOneTyped(ShoppingCart.Event.CheckedOut.class);

    assertEquals(cartId, itemAdded.getMetadata().get("ce-subject").orElseThrow());
    assertEquals("sku-1", itemAdded.getPayload().item().productId());
    assertEquals(2, itemAdded.getPayload().item().quantity());
    assertEquals(cartId, checkedOut.getMetadata().get("ce-subject").orElseThrow());
  }

  private akka.javasdk.Metadata metadataFor(String id, Object payload, String subject) {
    return CloudEvent.of(id, URI.create("ShoppingCartCommandsTopicConsumerIntegrationTest"), payload.getClass().getName())
        .withSubject(subject)
        .asMetadata()
        .add("Content-Type", "application/json");
  }
}
