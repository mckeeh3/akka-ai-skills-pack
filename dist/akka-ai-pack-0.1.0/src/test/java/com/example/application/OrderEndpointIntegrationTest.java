package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.TestKitSupport;
import java.util.List;
import org.junit.jupiter.api.Test;

class OrderEndpointIntegrationTest extends TestKitSupport {

  record CreateOrderRequest(String cartId, List<LineItemRequest> lineItems) {}

  record LineItemRequest(String productId, String name, int quantity, boolean readyToShip) {}

  record OrderLineItemResponse(String productId, String name, int quantity, boolean readyToShip) {}

  record OrderResponse(
      String orderId,
      String cartId,
      List<OrderLineItemResponse> lineItems,
      boolean readyToShip,
      boolean created) {}

  record RegionRequest(String region) {}

  record StatusResponse(String orderId, String status) {}

  @Test
  void createOrderReturnsCreatedResponse() {
    var orderId = "order-http-1";

    var response =
        await(
            httpClient
                .POST("/orders/" + orderId)
                .withRequestBody(
                    new CreateOrderRequest(
                        "cart-1",
                        List.of(new LineItemRequest("sku-1", "Akka T-Shirt", 2, false))))
                .responseBodyAs(OrderResponse.class)
                .invokeAsync());

    assertTrue(response.status().isSuccess());
    assertEquals(orderId, response.body().orderId());
    assertEquals("cart-1", response.body().cartId());
    assertTrue(response.body().created());
  }

  @Test
  void invalidCreateOrderRequestReturnsBadRequest() {
    var error =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                await(
                    httpClient
                        .POST("/orders/order-http-2")
                        .withRequestBody(
                            new CreateOrderRequest(
                                "",
                                List.of(new LineItemRequest("", "", 0, false))))
                        .responseBodyAs(String.class)
                        .invokeAsync()));

    assertTrue(error.getMessage().contains("HTTP status 400 Bad Request"));
    assertTrue(error.getMessage().contains("cartId must not be blank"));
  }

  @Test
  void getOrderConsistentReturnsCurrentState() {
    var orderId = "order-http-3";

    await(
        httpClient
            .POST("/orders/" + orderId)
            .withRequestBody(
                new CreateOrderRequest(
                    "cart-3",
                    List.of(new LineItemRequest("sku-1", "Akka T-Shirt", 2, false))))
            .responseBodyAs(OrderResponse.class)
            .invokeAsync());

    var response =
        await(
            httpClient
                .GET("/orders/" + orderId + "/consistent")
                .responseBodyAs(OrderResponse.class)
                .invokeAsync());

    assertTrue(response.status().isSuccess());
    assertEquals(orderId, response.body().orderId());
    assertEquals("cart-3", response.body().cartId());
  }

  @Test
  void includeAndExcludeRegionEndpointsReturnStatus() {
    var orderId = "order-http-4";

    await(
        httpClient
            .POST("/orders/" + orderId)
            .withRequestBody(
                new CreateOrderRequest(
                    "cart-4",
                    List.of(new LineItemRequest("sku-1", "Akka T-Shirt", 2, false))))
            .responseBodyAs(OrderResponse.class)
            .invokeAsync());

    var includeResponse =
        await(
            httpClient
                .PUT("/orders/" + orderId + "/replication/include")
                .withRequestBody(new RegionRequest("aws-us-east-2"))
                .responseBodyAs(StatusResponse.class)
                .invokeAsync());

    assertTrue(includeResponse.status().isSuccess());
    assertEquals("included-region:aws-us-east-2", includeResponse.body().status());

    var excludeResponse =
        await(
            httpClient
                .PUT("/orders/" + orderId + "/replication/exclude")
                .withRequestBody(new RegionRequest("aws-us-east-2"))
                .responseBodyAs(StatusResponse.class)
                .invokeAsync());

    assertTrue(excludeResponse.status().isSuccess());
    assertEquals("excluded-region:aws-us-east-2", excludeResponse.body().status());
  }

  @Test
  void lineItemReadyToShipEndpointUpdatesOrderState() {
    var orderId = "order-http-5";

    await(
        httpClient
            .POST("/orders/" + orderId)
            .withRequestBody(
                new CreateOrderRequest(
                    "cart-5",
                    List.of(
                        new LineItemRequest("sku-1", "Akka T-Shirt", 2, false),
                        new LineItemRequest("sku-2", "Akka Socks", 1, false))))
            .responseBodyAs(OrderResponse.class)
            .invokeAsync());

    var firstResponse =
        await(
            httpClient
                .PUT("/orders/" + orderId + "/line-items/sku-1/ready-to-ship")
                .responseBodyAs(OrderResponse.class)
                .invokeAsync());

    assertTrue(firstResponse.status().isSuccess());
    assertTrue(firstResponse.body().lineItems().stream().anyMatch(OrderLineItemResponse::readyToShip));
    assertTrue(!firstResponse.body().readyToShip());

    var secondResponse =
        await(
            httpClient
                .PUT("/orders/" + orderId + "/line-items/sku-2/ready-to-ship")
                .responseBodyAs(OrderResponse.class)
                .invokeAsync());

    assertTrue(secondResponse.status().isSuccess());
    assertTrue(secondResponse.body().readyToShip());
    assertEquals(2, secondResponse.body().lineItems().stream().filter(OrderLineItemResponse::readyToShip).count());
  }
}
