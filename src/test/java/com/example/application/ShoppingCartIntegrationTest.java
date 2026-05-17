package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.TestKitSupport;
import java.util.List;
import org.junit.jupiter.api.Test;

class ShoppingCartIntegrationTest extends TestKitSupport {

  record AddItemRequest(String productId, String name, int quantity) {}

  record CartItemResponse(String productId, String name, int quantity) {}

  record CartResponse(String cartId, List<CartItemResponse> items, boolean checkedOut) {}

  record CartSummaryItemResponse(String name, int quantity) {}

  record CartSummaryResponse(
      String capabilityId,
      String cartId,
      List<CartSummaryItemResponse> items,
      int totalQuantity,
      boolean checkedOut) {}

  record StatusResponse(String status) {}

  @Test
  void addItemAndGetCartViaEndpoint() {
    var cartId = "cart-http-1";

    var addResponse =
        await(
            httpClient
                .POST("/carts/" + cartId + "/items")
                .withRequestBody(new AddItemRequest("akka-tshirt", "Akka T-Shirt", 2))
                .responseBodyAs(CartResponse.class)
                .invokeAsync());

    assertTrue(addResponse.status().isSuccess());
    assertEquals(cartId, addResponse.body().cartId());
    assertEquals(1, addResponse.body().items().size());
    assertEquals("akka-tshirt", addResponse.body().items().getFirst().productId());

    var getResponse =
        await(
            httpClient
                .GET("/carts/" + cartId)
                .responseBodyAs(CartResponse.class)
                .invokeAsync());

    assertTrue(getResponse.status().isSuccess());
    assertEquals(cartId, getResponse.body().cartId());
    assertEquals(1, getResponse.body().items().size());
    assertEquals("Akka T-Shirt", getResponse.body().items().getFirst().name());
  }

  @Test
  void browserApiReusesReadOnlyInspectSummaryCapability() {
    var cartId = "cart-http-summary-1";

    await(
        httpClient
            .POST("/carts/" + cartId + "/items")
            .withRequestBody(new AddItemRequest("secret-sku", "Akka Mug", 3))
            .responseBodyAs(CartResponse.class)
            .invokeAsync());

    var response =
        await(
            httpClient
                .GET("/carts/" + cartId + "/summary")
                .responseBodyAs(CartSummaryResponse.class)
                .invokeAsync());

    assertTrue(response.status().isSuccess());
    assertEquals("cart.inspect-summary", response.body().capabilityId());
    assertEquals(cartId, response.body().cartId());
    assertEquals(1, response.body().items().size());
    assertEquals("Akka Mug", response.body().items().getFirst().name());
    assertEquals(3, response.body().items().getFirst().quantity());
    assertEquals(3, response.body().totalQuantity());

    var componentToolSummary =
        componentClient
            .forEventSourcedEntity(cartId)
            .method(ShoppingCartEntity::inspectCartSummary)
            .invoke();
    assertEquals(componentToolSummary.totalQuantity(), response.body().totalQuantity());
    assertEquals(componentToolSummary.items().getFirst().name(), response.body().items().getFirst().name());
  }

  @Test
  void entityValidationErrorBecomesBadRequest() {
    var error =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                await(
                    httpClient
                        .POST("/carts/cart-http-2/items")
                        .withRequestBody(new AddItemRequest("akka-tshirt", "Akka T-Shirt", 0))
                        .responseBodyAs(String.class)
                        .invokeAsync()));

    assertTrue(error.getMessage().contains("HTTP status 400 Bad Request"));
    assertTrue(error.getMessage().contains("quantity must be greater than zero."));
  }

  @Test
  void checkoutMarksCartAsCheckedOut() {
    var cartId = "cart-http-3";

    await(
        httpClient
            .POST("/carts/" + cartId + "/items")
            .withRequestBody(new AddItemRequest("akka-socks", "Akka Socks", 1))
            .responseBodyAs(CartResponse.class)
            .invokeAsync());

    var checkoutResponse =
        await(
            httpClient
                .POST("/carts/" + cartId + "/checkout")
                .responseBodyAs(CartResponse.class)
                .invokeAsync());

    assertTrue(checkoutResponse.status().isSuccess());
    assertTrue(checkoutResponse.body().checkedOut());
  }

  @Test
  void deleteCartReturnsDeletedStatus() {
    var cartId = "cart-http-4";

    await(
        httpClient
            .POST("/carts/" + cartId + "/items")
            .withRequestBody(new AddItemRequest("akka-socks", "Akka Socks", 1))
            .responseBodyAs(CartResponse.class)
            .invokeAsync());

    var deleteResponse =
        await(
            httpClient
                .DELETE("/carts/" + cartId)
                .responseBodyAs(StatusResponse.class)
                .invokeAsync());

    assertTrue(deleteResponse.status().isSuccess());
    assertEquals("deleted", deleteResponse.body().status());
  }
}
