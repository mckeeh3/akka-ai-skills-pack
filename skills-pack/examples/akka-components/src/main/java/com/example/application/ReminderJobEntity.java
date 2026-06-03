package com.example.application;

import akka.javasdk.annotations.Component;
import akka.javasdk.keyvalueentity.KeyValueEntity;
import akka.javasdk.keyvalueentity.KeyValueEntityContext;
import com.example.domain.ReminderJob;
import java.util.ArrayList;
import java.util.List;

/**
 * Key value entity that stores progress for a self-rescheduling reminder job.
 *
 * <p>The timed action drives {@link #recordReminderSent()}, which replies with enough information
 * for the timed action to decide whether it should schedule the next timer.
 */
@Component(id = "reminder-job")
public class ReminderJobEntity extends KeyValueEntity<ReminderJob.State> {

  private final String jobId;

  public ReminderJobEntity(KeyValueEntityContext context) {
    this.jobId = context.entityId();
  }

  @Override
  public ReminderJob.State emptyState() {
    return ReminderJob.State.empty(jobId);
  }

  public ReadOnlyEffect<ReminderJob.State> getJob() {
    return effects().reply(currentState());
  }

  public Effect<ReminderJob.State> start(ReminderJob.Start command) {
    var errors = validate(command);
    if (!errors.isEmpty()) {
      return effects().error(String.join("; ", errors));
    }
    if (currentState().exists()) {
      return effects().error("Reminder job already exists.");
    }

    var newState =
        currentState()
            .start(
                command.recipient(),
                command.message(),
                command.intervalSeconds(),
                command.maxReminders());
    return effects().updateState(newState).thenReply(newState);
  }

  public Effect<ReminderJob.Result> recordReminderSent() {
    if (!currentState().exists()) {
      return effects()
          .reply(
              new ReminderJob.Result(ReminderJob.Outcome.NOT_FOUND, "Reminder job not found.", 0, false, 0));
    }
    if (currentState().isCompleted()) {
      return effects()
          .reply(
              new ReminderJob.Result(
                  ReminderJob.Outcome.INVALID,
                  "Reminder job is already completed.",
                  currentState().sentCount(),
                  false,
                  currentState().intervalSeconds()));
    }

    var updatedState = currentState().recordReminderSent();
    var shouldScheduleNext = updatedState.isActive();
    var message = shouldScheduleNext ? "Reminder recorded." : "Final reminder recorded.";

    return effects()
        .updateState(updatedState)
        .thenReply(
            new ReminderJob.Result(
                ReminderJob.Outcome.OK,
                message,
                updatedState.sentCount(),
                shouldScheduleNext,
                updatedState.intervalSeconds()));
  }

  public Effect<ReminderJob.Result> complete() {
    if (!currentState().exists()) {
      return effects()
          .reply(
              new ReminderJob.Result(ReminderJob.Outcome.NOT_FOUND, "Reminder job not found.", 0, false, 0));
    }
    if (currentState().isCompleted()) {
      return effects()
          .reply(
              new ReminderJob.Result(
                  ReminderJob.Outcome.OK,
                  "Reminder job already completed.",
                  currentState().sentCount(),
                  false,
                  currentState().intervalSeconds()));
    }

    var updatedState = currentState().complete();
    return effects()
        .updateState(updatedState)
        .thenReply(
            new ReminderJob.Result(
                ReminderJob.Outcome.OK,
                "Reminder job completed.",
                updatedState.sentCount(),
                false,
                updatedState.intervalSeconds()));
  }

  private static List<String> validate(ReminderJob.Start command) {
    var errors = new ArrayList<String>();
    if (command.recipient() == null || command.recipient().isBlank()) {
      errors.add("recipient must not be blank.");
    }
    if (command.message() == null || command.message().isBlank()) {
      errors.add("message must not be blank.");
    }
    if (command.intervalSeconds() <= 0) {
      errors.add("intervalSeconds must be greater than zero.");
    }
    if (command.maxReminders() <= 0) {
      errors.add("maxReminders must be greater than zero.");
    }
    return errors;
  }
}
