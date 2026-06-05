package com.example.application;

import akka.javasdk.annotations.Component;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.timedaction.TimedAction;
import com.example.domain.ReminderJob;
import java.time.Duration;

/**
 * Timed action that demonstrates self-rescheduling with {@link TimedAction#timers()}.
 *
 * <p>Each execution records one reminder send in the entity. If more reminders are still needed,
 * the timed action schedules the next run from inside the handler.
 */
@Component(id = "reminder-job-timed-action")
public class ReminderJobTimedAction extends TimedAction {

  private final ComponentClient componentClient;

  public ReminderJobTimedAction(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  public Effect sendReminder(String jobId) {
    var result =
        componentClient
            .forKeyValueEntity(jobId)
            .method(ReminderJobEntity::recordReminderSent)
            .invoke();

    return switch (result.outcome()) {
      case OK -> {
        if (result.shouldScheduleNext()) {
          timers()
              .createSingleTimer(
                  timerName(jobId),
                  Duration.ofSeconds(result.intervalSeconds()),
                  componentClient
                      .forTimedAction()
                      .method(ReminderJobTimedAction::sendReminder)
                      .deferred(jobId));
        }
        yield effects().done();
      }
      case NOT_FOUND -> effects().done();
      case INVALID -> effects().done();
    };
  }

  public static String timerName(String jobId) {
    return "reminder-job-" + jobId;
  }
}
