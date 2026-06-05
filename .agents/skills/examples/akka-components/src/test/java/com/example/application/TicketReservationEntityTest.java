package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.KeyValueEntityTestKit;
import com.example.domain.TicketReservation;
import org.junit.jupiter.api.Test;

class TicketReservationEntityTest {

  private KeyValueEntityTestKit<TicketReservation.State, TicketReservationEntity> newTestKit(
      String entityId) {
    return KeyValueEntityTestKit.of(entityId, TicketReservationEntity::new);
  }

  @Test
  void reserveCreatesPendingReservation() {
    var testKit = newTestKit("reservation-1");

    var result =
        testKit
            .method(TicketReservationEntity::reserve)
            .invoke(new TicketReservation.Reserve("alice@example.com", "concert-1", 2));

    assertTrue(result.stateWasUpdated());
    assertEquals("pending", result.getReply().status().name().toLowerCase());
    assertEquals("alice@example.com", testKit.getState().customerEmail());
    assertEquals("concert-1", testKit.getState().eventId());
  }

  @Test
  void confirmMovesPendingReservationToConfirmed() {
    var testKit = newTestKit("reservation-2");
    testKit
        .method(TicketReservationEntity::reserve)
        .invoke(new TicketReservation.Reserve("bob@example.com", "concert-2", 1));

    var result = testKit.method(TicketReservationEntity::confirm).invoke();

    assertEquals(TicketReservation.Outcome.OK, result.getReply().outcome());
    assertEquals("Reservation confirmed.", result.getReply().message());
    assertTrue(result.stateWasUpdated());
    assertTrue(testKit.getState().isConfirmed());
  }

  @Test
  void expireConfirmedReservationReturnsInvalidWithoutUpdatingState() {
    var testKit = newTestKit("reservation-3");
    testKit
        .method(TicketReservationEntity::reserve)
        .invoke(new TicketReservation.Reserve("cara@example.com", "concert-3", 3));
    testKit.method(TicketReservationEntity::confirm).invoke();

    var result = testKit.method(TicketReservationEntity::expire).invoke();

    assertEquals(TicketReservation.Outcome.INVALID, result.getReply().outcome());
    assertFalse(result.stateWasUpdated());
    assertTrue(testKit.getState().isConfirmed());
  }

  @Test
  void reserveRejectsMalformedInput() {
    var testKit = newTestKit("reservation-4");

    var result =
        testKit
            .method(TicketReservationEntity::reserve)
            .invoke(new TicketReservation.Reserve("", "", 0));

    assertTrue(result.isError());
    assertEquals(
        "customerEmail must not be blank.; eventId must not be blank.; seatCount must be greater than zero.",
        result.getError());
    assertFalse(result.stateWasUpdated());
  }
}
