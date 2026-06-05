package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.TestKitSupport;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class TicketReservationEndpointIntegrationTest extends TestKitSupport {

  record CreateReservationRequest(
      String customerEmail,
      String eventId,
      int seatCount,
      int expiresInSeconds) {}

  record ReservationResponse(
      String reservationId,
      String customerEmail,
      String eventId,
      int seatCount,
      String status) {}

  record StatusResponse(String reservationId, String status, String message) {}

  @Test
  void reservationExpiresAfterTimerFires() {
    var created =
        await(
            httpClient
                .POST("/ticket-reservations")
                .withRequestBody(new CreateReservationRequest("alice@example.com", "concert-1", 2, 1))
                .responseBodyAs(ReservationResponse.class)
                .invokeAsync());

    assertTrue(created.status().isSuccess());
    assertEquals("pending", created.body().status());

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var current =
                  await(
                      httpClient
                          .GET("/ticket-reservations/" + created.body().reservationId())
                          .responseBodyAs(ReservationResponse.class)
                          .invokeAsync());

              assertEquals("expired", current.body().status());
            });
  }

  @Test
  void confirmDeletesTimerAndKeepsReservationConfirmed() {
    var created =
        await(
            httpClient
                .POST("/ticket-reservations")
                .withRequestBody(new CreateReservationRequest("bob@example.com", "concert-2", 1, 1))
                .responseBodyAs(ReservationResponse.class)
                .invokeAsync());

    var confirmation =
        await(
            httpClient
                .PUT("/ticket-reservations/" + created.body().reservationId() + "/confirm")
                .responseBodyAs(StatusResponse.class)
                .invokeAsync());

    assertTrue(confirmation.status().isSuccess());
    assertEquals("confirmed", confirmation.body().status());

    Awaitility.await()
        .ignoreExceptions()
        .during(2, TimeUnit.SECONDS)
        .atMost(5, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var current =
                  await(
                      httpClient
                          .GET("/ticket-reservations/" + created.body().reservationId())
                          .responseBodyAs(ReservationResponse.class)
                          .invokeAsync());

              assertEquals("confirmed", current.body().status());
            });
  }

  @Test
  void invalidCreateReservationRequestReturnsBadRequest() {
    var error =
        org.junit.jupiter.api.Assertions.assertThrows(
            IllegalArgumentException.class,
            () ->
                await(
                    httpClient
                        .POST("/ticket-reservations")
                        .withRequestBody(new CreateReservationRequest("", "", 0, 0))
                        .responseBodyAs(String.class)
                        .invokeAsync()));

    assertTrue(error.getMessage().contains("HTTP status 400 Bad Request"));
    assertTrue(error.getMessage().contains("customerEmail must not be blank."));
  }
}
