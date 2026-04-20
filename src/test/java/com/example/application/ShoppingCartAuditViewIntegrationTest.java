package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.stream.javadsl.Sink;
import akka.javasdk.testkit.EventingTestKit.IncomingMessages;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import com.example.domain.ShoppingCart;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class ShoppingCartAuditViewIntegrationTest extends TestKitSupport {

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT.withEventSourcedEntityIncomingMessages(ShoppingCartEntity.class);
  }

  @Test
  void deleteHandlerCanMarkTheRowAsDeleted() {
    IncomingMessages shoppingCartEvents = testKit.getEventSourcedEntityIncomingMessages(ShoppingCartEntity.class);

    shoppingCartEvents.publish(
        new ShoppingCart.Event.ItemAdded(new ShoppingCart.LineItem("sku-1", "Akka T-Shirt", 1)),
        "audit-cart-1");
    shoppingCartEvents.publishDelete("audit-cart-1");

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var result =
                  componentClient
                      .forView()
                      .method(ShoppingCartAuditView::getByDeleted)
                      .invoke(new ShoppingCartAuditView.FindByDeleted(true));

              assertEquals(1, result.carts().size());
              assertEquals("audit-cart-1", result.carts().getFirst().cartId());
              assertTrue(result.carts().getFirst().deleted());
            });
  }

  @Test
  void queryStreamResultCanStreamCurrentRows() {
    IncomingMessages shoppingCartEvents = testKit.getEventSourcedEntityIncomingMessages(ShoppingCartEntity.class);

    shoppingCartEvents.publish(
        new ShoppingCart.Event.ItemAdded(new ShoppingCart.LineItem("sku-1", "Akka T-Shirt", 1)),
        "audit-stream-1");
    shoppingCartEvents.publish(
        new ShoppingCart.Event.CheckedOut(),
        "audit-stream-1");
    shoppingCartEvents.publish(
        new ShoppingCart.Event.ItemAdded(new ShoppingCart.LineItem("sku-2", "Akka Socks", 1)),
        "audit-stream-2");

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var result =
                  componentClient
                      .forView()
                      .method(ShoppingCartAuditView::getByDeleted)
                      .invoke(new ShoppingCartAuditView.FindByDeleted(false));
              assertEquals(2, result.carts().size());
            });

    var rows =
        await(
            componentClient
                .forView()
                .stream(ShoppingCartAuditView::streamByDeleted)
                .source(new ShoppingCartAuditView.FindByDeleted(false))
                .runWith(Sink.seq(), testKit.getMaterializer()));

    assertEquals(2, rows.size());
    assertTrue(rows.stream().anyMatch(row -> row.cartId().equals("audit-stream-1") && row.checkedOut()));
    assertTrue(rows.stream().anyMatch(row -> row.cartId().equals("audit-stream-2") && !row.deleted()));
  }
}
