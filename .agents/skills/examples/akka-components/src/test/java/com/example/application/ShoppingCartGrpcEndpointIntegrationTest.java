package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.TestKitSupport;
import akka.stream.javadsl.Sink;
import com.example.api.grpc.AddItemRequest;
import com.example.api.grpc.CheckedOutCartsRequest;
import com.example.api.grpc.CheckoutCartRequest;
import com.example.api.grpc.DeleteCartRequest;
import com.example.api.grpc.ShoppingCartGrpcEndpointClient;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class ShoppingCartGrpcEndpointIntegrationTest extends TestKitSupport {

  @Test
  void unaryCallsMapToEntityCommandsAndReturnProtobufMessages() {
    var client = getGrpcEndpointClient(ShoppingCartGrpcEndpointClient.class);

    var cart =
        client.addItem(
            AddItemRequest.newBuilder()
                .setCartId("grpc-cart-1")
                .setProductId("sku-1")
                .setName("Akka T-Shirt")
                .setQuantity(2)
                .build());

    assertEquals("grpc-cart-1", cart.getCartId());
    assertEquals(1, cart.getItemsCount());
    assertEquals("open", cart.getStateLabel().getValue());
    assertEquals("sku-1", cart.getItems(0).getProductId());

    var checkedOut =
        client.checkoutCart(CheckoutCartRequest.newBuilder().setCartId("grpc-cart-1").build());

    assertTrue(checkedOut.getCheckedOut());
    assertEquals("checked-out", checkedOut.getStateLabel().getValue());

    var status = client.deleteCart(DeleteCartRequest.newBuilder().setCartId("grpc-cart-1").build());

    assertEquals("deleted", status.getStatus());
    assertTrue(status.hasDetail());
  }

  @Test
  void invalidEntityRejectionBecomesInvalidArgumentStatus() {
    var client = getGrpcEndpointClient(ShoppingCartGrpcEndpointClient.class);

    var error =
        assertThrows(
            StatusRuntimeException.class,
            () -> client.checkoutCart(CheckoutCartRequest.newBuilder().setCartId("grpc-empty-cart").build()));

    assertEquals(Status.Code.INVALID_ARGUMENT, error.getStatus().getCode());
    assertTrue(error.getStatus().getDescription().contains("Cannot checkout an empty cart"));
  }

  @Test
  void serverStreamingExposesViewRowsAsGrpcMessages() {
    var client = getGrpcEndpointClient(ShoppingCartGrpcEndpointClient.class);

    client.addItem(
        AddItemRequest.newBuilder()
            .setCartId("grpc-stream-1")
            .setProductId("sku-1")
            .setName("Akka Hoodie")
            .setQuantity(1)
            .build());
    client.checkoutCart(CheckoutCartRequest.newBuilder().setCartId("grpc-stream-1").build());

    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var result =
                  componentClient
                      .forView()
                      .method(ShoppingCartsByCheckedOutView::getCarts)
                      .invoke(new ShoppingCartsByCheckedOutView.FindByCheckedOut(true));
              assertTrue(result.carts().stream().anyMatch(cart -> cart.cartId().equals("grpc-stream-1")));
            });

    var rows =
        await(
            client
                .streamCheckedOutCarts(CheckedOutCartsRequest.newBuilder().setCheckedOut(true).build())
                .runWith(Sink.seq(), testKit.getMaterializer()));

    assertTrue(rows.stream().anyMatch(row -> row.getCartId().equals("grpc-stream-1") && row.getCheckedOut()));
  }
}
