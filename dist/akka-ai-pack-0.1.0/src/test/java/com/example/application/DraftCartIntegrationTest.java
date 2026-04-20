package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.TestKitSupport;
import java.util.List;
import org.junit.jupiter.api.Test;

class DraftCartIntegrationTest extends TestKitSupport {

  record AddItemRequest(String productId, String name, int quantity) {}

  record CartItemResponse(String productId, String name, int quantity) {}

  record CartResponse(String cartId, List<CartItemResponse> items, boolean checkedOut) {}

  record StatusResponse(String status) {}

  @Test
  void addItemAndGetCartViaEndpoint() {
    var cartId = "draft-cart-http-1";

    var addResponse =
        await(
            httpClient
                .POST("/draft-carts/" + cartId + "/items")
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
                .GET("/draft-carts/" + cartId)
                .responseBodyAs(CartResponse.class)
                .invokeAsync());

    assertTrue(getResponse.status().isSuccess());
    assertEquals(cartId, getResponse.body().cartId());
    assertEquals(1, getResponse.body().items().size());
    assertEquals("Akka T-Shirt", getResponse.body().items().getFirst().name());
  }

  @Test
  void entityValidationErrorBecomesBadRequest() {
    var error =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                await(
                    httpClient
                        .POST("/draft-carts/draft-cart-http-2/items")
                        .withRequestBody(new AddItemRequest("akka-tshirt", "Akka T-Shirt", 0))
                        .responseBodyAs(String.class)
                        .invokeAsync()));

    assertTrue(error.getMessage().contains("HTTP status 400 Bad Request"));
    assertTrue(error.getMessage().contains("quantity must be greater than zero."));
  }

  @Test
  void checkoutMarksCartAsCheckedOut() {
    var cartId = "draft-cart-http-3";

    await(
        httpClient
            .POST("/draft-carts/" + cartId + "/items")
            .withRequestBody(new AddItemRequest("akka-socks", "Akka Socks", 1))
            .responseBodyAs(CartResponse.class)
            .invokeAsync());

    var checkoutResponse =
        await(
            httpClient
                .POST("/draft-carts/" + cartId + "/checkout")
                .responseBodyAs(CartResponse.class)
                .invokeAsync());

    assertTrue(checkoutResponse.status().isSuccess());
    assertTrue(checkoutResponse.body().checkedOut());
  }

  @Test
  void deleteCartReturnsDeletedStatus() {
    var cartId = "draft-cart-http-4";

    await(
        httpClient
            .POST("/draft-carts/" + cartId + "/items")
            .withRequestBody(new AddItemRequest("akka-socks", "Akka Socks", 1))
            .responseBodyAs(CartResponse.class)
            .invokeAsync());

    var deleteResponse =
        await(
            httpClient
                .DELETE("/draft-carts/" + cartId)
                .responseBodyAs(StatusResponse.class)
                .invokeAsync());

    assertTrue(deleteResponse.status().isSuccess());
    assertEquals("deleted", deleteResponse.body().status());
  }
}
