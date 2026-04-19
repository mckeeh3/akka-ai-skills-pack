package com.example.application;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Consume;
import akka.javasdk.annotations.DeleteHandler;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.consumer.Consumer;
import com.example.domain.DraftCart;
import com.example.domain.PurchaseOrder;

/**
 * Consumer that reacts to draft cart checkout state updates and creates a downstream purchase
 * order entity.
 *
 * <p>This example demonstrates an event-driven flow for key value entities:
 *
 * <ul>
 *   <li>an edge-facing {@code DraftCartEntity} is called from an endpoint</li>
 *   <li>checked out state is consumed here</li>
 *   <li>the consumer transforms draft cart state into a {@code PurchaseOrder.Command.CreateOrder}</li>
 *   <li>the downstream {@code PurchaseOrderEntity} is created with idempotent command handling</li>
 * </ul>
 */
@Component(id = "draft-cart-checkout-consumer")
@Consume.FromKeyValueEntity(DraftCartEntity.class)
public class DraftCartCheckoutConsumer extends Consumer {

  private final ComponentClient componentClient;

  public DraftCartCheckoutConsumer(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  public Effect onChange(DraftCart.State state) {
    if (!state.checkedOut()) {
      return effects().ignore();
    }

    var cartId = messageContext().eventSubject().orElse(state.cartId());
    componentClient
        .forKeyValueEntity(orderIdFromCartId(cartId))
        .method(PurchaseOrderEntity::createOrder)
        .invoke(new PurchaseOrder.Command.CreateOrder(cartId, toOrderLineItems(state)));

    return effects().done();
  }

  @DeleteHandler
  public Effect onDeleted() {
    return effects().ignore();
  }

  private static String orderIdFromCartId(String cartId) {
    return cartId;
  }

  private static java.util.List<PurchaseOrder.LineItem> toOrderLineItems(DraftCart.State cart) {
    return cart.items().stream()
        .map(item -> new PurchaseOrder.LineItem(item.productId(), item.name(), item.quantity(), false))
        .toList();
  }
}
