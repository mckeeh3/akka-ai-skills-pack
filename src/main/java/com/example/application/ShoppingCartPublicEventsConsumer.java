package com.example.application;

import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Consume;
import akka.javasdk.annotations.Produce;
import akka.javasdk.consumer.Consumer;
import com.example.domain.ShoppingCart;

/**
 * Consumer that exposes a filtered public event stream for other Akka services.
 *
 * <p>This example mirrors the official service-to-service eventing pattern: consume internal
 * entity events, map them to a public contract, and publish them through a service stream with an
 * explicit service ACL.
 */
@Component(id = "shopping-cart-public-events-consumer")
@Consume.FromEventSourcedEntity(ShoppingCartEntity.class)
@Produce.ServiceStream(id = "shopping_cart_public_events")
@Acl(allow = @Acl.Matcher(service = "*"))
public class ShoppingCartPublicEventsConsumer extends Consumer {

  public sealed interface PublicEvent {
    record ItemAdded(String productId, int quantity) implements PublicEvent {}

    record CheckedOut() implements PublicEvent {}
  }

  public Effect onEvent(ShoppingCart.Event event) {
    return switch (event) {
      case ShoppingCart.Event.ItemAdded added ->
          effects().produce(new PublicEvent.ItemAdded(added.item().productId(), added.item().quantity()));
      case ShoppingCart.Event.CheckedOut ignored -> effects().produce(new PublicEvent.CheckedOut());
      case ShoppingCart.Event.ItemRemoved ignored -> effects().ignore();
      case ShoppingCart.Event.Deleted ignored -> effects().ignore();
    };
  }
}
