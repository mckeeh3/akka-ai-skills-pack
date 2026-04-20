package com.example.domain;

/**
 * Pure domain model for a self-rescheduling reminder flow.
 *
 * <p>The timed action records each reminder attempt in the entity and, while the job remains
 * active, schedules the next reminder by calling {@code timers().createSingleTimer(...)} from the
 * timed action handler itself.
 */
public final class ReminderJob {

  private ReminderJob() {}

  public enum Status {
    EMPTY,
    ACTIVE,
    COMPLETED
  }

  public record State(
      String jobId,
      String recipient,
      String message,
      int intervalSeconds,
      int sentCount,
      int maxReminders,
      Status status) {

    public static State empty(String jobId) {
      return new State(jobId, "", "", 0, 0, 0, Status.EMPTY);
    }

    public boolean exists() {
      return status != Status.EMPTY;
    }

    public boolean isActive() {
      return status == Status.ACTIVE;
    }

    public boolean isCompleted() {
      return status == Status.COMPLETED;
    }

    public State start(String recipient, String message, int intervalSeconds, int maxReminders) {
      return new State(jobId, recipient, message, intervalSeconds, 0, maxReminders, Status.ACTIVE);
    }

    public State recordReminderSent() {
      var nextSentCount = sentCount + 1;
      var nextStatus = nextSentCount >= maxReminders ? Status.COMPLETED : Status.ACTIVE;
      return new State(jobId, recipient, message, intervalSeconds, nextSentCount, maxReminders, nextStatus);
    }

    public State complete() {
      return new State(jobId, recipient, message, intervalSeconds, sentCount, maxReminders, Status.COMPLETED);
    }
  }

  public record Start(String recipient, String message, int intervalSeconds, int maxReminders) {}

  public enum Outcome {
    OK,
    NOT_FOUND,
    INVALID
  }

  public record Result(
      Outcome outcome,
      String message,
      int sentCount,
      boolean shouldScheduleNext,
      int intervalSeconds) {}
}
