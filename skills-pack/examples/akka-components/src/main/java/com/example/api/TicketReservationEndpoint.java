package com.example.api;

import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.CommandException;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.annotations.http.Put;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.http.HttpResponses;
import akka.javasdk.timer.TimerScheduler;
import com.example.application.TicketReservationEntity;
import com.example.application.TicketReservationTimedAction;
import com.example.domain.TicketReservation;
import java.time.Duration;
import java.util.Locale;
import java.util.UUID;

/**
 * HTTP endpoint that demonstrates timer registration before state creation and timer deletion after
 * successful confirmation.
 */
@HttpEndpoint("/ticket-reservations")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class TicketReservationEndpoint {

  public record CreateReservationRequest(
      String customerEmail,
      String eventId,
      int seatCount,
      int expiresInSeconds) {}

  public record ReservationResponse(
      String reservationId,
      String customerEmail,
      String eventId,
      int seatCount,
      String status) {}

  public record StatusResponse(String reservationId, String status, String message) {}

  private final TimerScheduler timerScheduler;
  private final ComponentClient componentClient;

  public TicketReservationEndpoint(TimerScheduler timerScheduler, ComponentClient componentClient) {
    this.timerScheduler = timerScheduler;
    this.componentClient = componentClient;
  }

  @Post
  public HttpResponse createReservation(CreateReservationRequest request) {
    var validationError = validate(request);
    if (validationError != null) {
      return HttpResponses.badRequest(validationError);
    }

    var reservationId = UUID.randomUUID().toString();
    timerScheduler.createSingleTimer(
        timerName(reservationId),
        Duration.ofSeconds(request.expiresInSeconds()),
        5,
        componentClient
            .forTimedAction()
            .method(TicketReservationTimedAction::expireReservation)
            .deferred(reservationId));

    try {
      var created =
          componentClient
              .forKeyValueEntity(reservationId)
              .method(TicketReservationEntity::reserve)
              .invoke(
                  new TicketReservation.Reserve(
                      request.customerEmail(), request.eventId(), request.seatCount()));
      return HttpResponses.created(toApi(created));
    } catch (CommandException error) {
      timerScheduler.delete(timerName(reservationId));
      return HttpResponses.badRequest(error.getMessage());
    }
  }

  @Get("/{reservationId}")
  public HttpResponse getReservation(String reservationId) {
    var state =
        componentClient
            .forKeyValueEntity(reservationId)
            .method(TicketReservationEntity::getReservation)
            .invoke();

    if (!state.exists()) {
      return HttpResponses.notFound("Reservation not found.");
    }

    return HttpResponses.ok(toApi(state));
  }

  @Put("/{reservationId}/confirm")
  public HttpResponse confirm(String reservationId) {
    var result =
        componentClient
            .forKeyValueEntity(reservationId)
            .method(TicketReservationEntity::confirm)
            .invoke();

    return switch (result.outcome()) {
      case OK -> {
        timerScheduler.delete(timerName(reservationId));
        yield HttpResponses.ok(
            new StatusResponse(reservationId, "confirmed", result.message()));
      }
      case NOT_FOUND -> HttpResponses.notFound(result.message());
      case INVALID -> HttpResponses.badRequest(result.message());
    };
  }

  private static String validate(CreateReservationRequest request) {
    if (request.customerEmail() == null || request.customerEmail().isBlank()) {
      return "customerEmail must not be blank.";
    }
    if (request.eventId() == null || request.eventId().isBlank()) {
      return "eventId must not be blank.";
    }
    if (request.seatCount() <= 0) {
      return "seatCount must be greater than zero.";
    }
    if (request.expiresInSeconds() <= 0) {
      return "expiresInSeconds must be greater than zero.";
    }
    return null;
  }

  private static ReservationResponse toApi(TicketReservation.State state) {
    return new ReservationResponse(
        state.reservationId(),
        state.customerEmail(),
        state.eventId(),
        state.seatCount(),
        state.status().name().toLowerCase(Locale.ROOT));
  }

  private static String timerName(String reservationId) {
    return "ticket-reservation-expiration-" + reservationId;
  }
}
