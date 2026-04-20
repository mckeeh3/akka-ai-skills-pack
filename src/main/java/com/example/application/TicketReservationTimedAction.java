package com.example.application;

import akka.javasdk.annotations.Component;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.timedaction.TimedAction;
import com.example.domain.TicketReservation;

/**
 * Timed action that converts a scheduled timer into a reservation-expiry command.
 *
 * <p>Business-terminal outcomes are treated as successful timer executions so obsolete timers are
 * removed instead of retried forever. Unexpected infrastructure or invocation failures are allowed
 * to bubble up so the timer can retry.
 */
@Component(id = "ticket-reservation-timed-action")
public class TicketReservationTimedAction extends TimedAction {

  private final ComponentClient componentClient;

  public TicketReservationTimedAction(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  public Effect expireReservation(String reservationId) {
    var result =
        componentClient
            .forKeyValueEntity(reservationId)
            .method(TicketReservationEntity::expire)
            .invoke();

    return switch (result.outcome()) {
      case OK -> effects().done();
      case NOT_FOUND -> effects().done();
      case INVALID -> effects().done();
    };
  }
}
