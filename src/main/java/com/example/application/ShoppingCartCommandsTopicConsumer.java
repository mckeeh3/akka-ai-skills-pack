package com.example.application;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Consume;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.consumer.Consumer;
import com.example.domain.ShoppingCart;

/**
 * Topic consumer that turns external shopping-cart command messages into entity calls.
 *
 * <p>AI-agent note: this is the canonical local example for {@code @Consume.FromTopic}. The topic
 * message shape is intentionally separate from the entity command type, while the cart id is taken
 * from CloudEvent {@code ce-subject} metadata.
 */
@Component(id = "shopping-cart-commands-topic-consumer")
@Consume.FromTopic("shopping-cart-commands")
public class ShoppingCartCommandsTopicConsumer extends Consumer {

  public record AddItem(String productId, String name, int quantity) {}

  public record Checkout() {}

  private final ComponentClient componentClient;

  public ShoppingCartCommandsTopicConsumer(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  public Effect onMessage(AddItem message) {
    var cartId = cartIdFromSubject();
    componentClient
        .forEventSourcedEntity(cartId)
        .method(ShoppingCartEntity::addItem)
        .invoke(new ShoppingCart.Command.AddItem(message.productId(), message.name(), message.quantity()));
    return effects().done();
  }

  public Effect onMessage(Checkout ignored) {
    var cartId = cartIdFromSubject();
    componentClient
        .forEventSourcedEntity(cartId)
        .method(ShoppingCartEntity::checkout)
        .invoke(new ShoppingCart.Command.Checkout());
    return effects().done();
  }

  private String cartIdFromSubject() {
    return messageContext().eventSubject().orElseThrow();
  }
}
