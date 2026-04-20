package com.example.domain;

/**
 * Pure domain model for a timer-backed ticket reservation flow.
 *
 * <p>A reservation starts in {@code PENDING}, can be moved to {@code CONFIRMED} by a user action,
 * or to {@code EXPIRED} by a timer-triggered timed action.
 */
public final class TicketReservation {

  private TicketReservation() {}

  public enum Status {
    EMPTY,
    PENDING,
    CONFIRMED,
    EXPIRED
  }

  public record State(
      String reservationId,
      String customerEmail,
      String eventId,
      int seatCount,
      Status status) {

    public static State empty(String reservationId) {
      return new State(reservationId, "", "", 0, Status.EMPTY);
    }

    public boolean exists() {
      return status != Status.EMPTY;
    }

    public boolean isPending() {
      return status == Status.PENDING;
    }

    public boolean isConfirmed() {
      return status == Status.CONFIRMED;
    }

    public boolean isExpired() {
      return status == Status.EXPIRED;
    }

    public State reserve(String customerEmail, String eventId, int seatCount) {
      return new State(reservationId, customerEmail, eventId, seatCount, Status.PENDING);
    }

    public State confirm() {
      return new State(reservationId, customerEmail, eventId, seatCount, Status.CONFIRMED);
    }

    public State expire() {
      return new State(reservationId, customerEmail, eventId, seatCount, Status.EXPIRED);
    }
  }

  public record Reserve(String customerEmail, String eventId, int seatCount) {}

  public enum Outcome {
    OK,
    NOT_FOUND,
    INVALID
  }

  public record Result(Outcome outcome, String message) {}
}
