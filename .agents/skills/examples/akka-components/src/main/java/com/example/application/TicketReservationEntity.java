package com.example.application;

import akka.javasdk.annotations.Component;
import akka.javasdk.keyvalueentity.KeyValueEntity;
import akka.javasdk.keyvalueentity.KeyValueEntityContext;
import com.example.domain.TicketReservation;
import java.util.ArrayList;
import java.util.List;

/**
 * Key value entity for a reservation that must be confirmed before a timer expires.
 *
 * <p>This example is intentionally small and timer-friendly:
 *
 * <ul>
 *   <li>the edge-facing create command still validates malformed input and can fail</li>
 *   <li>the timer-facing expire command never fails for terminal business outcomes</li>
 *   <li>obsolete timer calls are normalized into reply values instead of {@code effects().error(...)}</li>
 * </ul>
 */
@Component(id = "ticket-reservation")
public class TicketReservationEntity extends KeyValueEntity<TicketReservation.State> {

  private final String reservationId;

  public TicketReservationEntity(KeyValueEntityContext context) {
    this.reservationId = context.entityId();
  }

  @Override
  public TicketReservation.State emptyState() {
    return TicketReservation.State.empty(reservationId);
  }

  public ReadOnlyEffect<TicketReservation.State> getReservation() {
    return effects().reply(currentState());
  }

  public Effect<TicketReservation.State> reserve(TicketReservation.Reserve command) {
    var errors = validate(command);
    if (!errors.isEmpty()) {
      return effects().error(String.join("; ", errors));
    }
    if (currentState().exists()) {
      return effects().error("Reservation already exists.");
    }

    var newState =
        currentState().reserve(command.customerEmail(), command.eventId(), command.seatCount());
    return effects().updateState(newState).thenReply(newState);
  }

  public Effect<TicketReservation.Result> confirm() {
    if (!currentState().exists()) {
      return effects()
          .reply(
              new TicketReservation.Result(
                  TicketReservation.Outcome.NOT_FOUND, "Reservation not found."));
    }
    if (currentState().isExpired()) {
      return effects()
          .reply(
              new TicketReservation.Result(
                  TicketReservation.Outcome.INVALID, "Expired reservations cannot be confirmed."));
    }
    if (currentState().isConfirmed()) {
      return effects()
          .reply(
              new TicketReservation.Result(
                  TicketReservation.Outcome.OK, "Reservation already confirmed."));
    }

    return effects()
        .updateState(currentState().confirm())
        .thenReply(
            new TicketReservation.Result(
                TicketReservation.Outcome.OK, "Reservation confirmed."));
  }

  public Effect<TicketReservation.Result> expire() {
    if (!currentState().exists()) {
      return effects()
          .reply(
              new TicketReservation.Result(
                  TicketReservation.Outcome.NOT_FOUND, "Reservation not found."));
    }
    if (currentState().isConfirmed()) {
      return effects()
          .reply(
              new TicketReservation.Result(
                  TicketReservation.Outcome.INVALID,
                  "Confirmed reservations must not be expired."));
    }
    if (currentState().isExpired()) {
      return effects()
          .reply(
              new TicketReservation.Result(
                  TicketReservation.Outcome.OK, "Reservation already expired."));
    }

    return effects()
        .updateState(currentState().expire())
        .thenReply(
            new TicketReservation.Result(TicketReservation.Outcome.OK, "Reservation expired."));
  }

  private static List<String> validate(TicketReservation.Reserve command) {
    var errors = new ArrayList<String>();
    if (command.customerEmail() == null || command.customerEmail().isBlank()) {
      errors.add("customerEmail must not be blank.");
    }
    if (command.eventId() == null || command.eventId().isBlank()) {
      errors.add("eventId must not be blank.");
    }
    if (command.seatCount() <= 0) {
      errors.add("seatCount must be greater than zero.");
    }
    return errors;
  }
}
