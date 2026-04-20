package com.example.application;

import akka.javasdk.Metadata;
import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Consume;
import akka.javasdk.annotations.Produce;
import akka.javasdk.consumer.Consumer;
import com.example.domain.ShoppingCart;

/**
 * Consumer that republishes shopping-cart events to a broker topic.
 *
 * <p>AI-agent note: add {@code ce-subject} metadata when producing per-cart events so downstream
 * consumers and views can preserve entity ordering and row identity.
 */
@Component(id = "shopping-cart-events-to-topic-consumer")
@Consume.FromEventSourcedEntity(ShoppingCartEntity.class)
@Produce.ToTopic("shopping-cart-events")
public class ShoppingCartEventsToTopicConsumer extends Consumer {

  public Effect onEvent(ShoppingCart.Event event) {
    var cartId = messageContext().eventSubject().orElseThrow();
    Metadata metadata = Metadata.EMPTY.add("ce-subject", cartId);
    return effects().produce(event, metadata);
  }
}
