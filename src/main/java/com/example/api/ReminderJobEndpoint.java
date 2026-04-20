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
import com.example.application.ReminderJobEntity;
import com.example.application.ReminderJobTimedAction;
import com.example.domain.ReminderJob;
import java.time.Duration;
import java.util.Locale;

/**
 * HTTP endpoint for the self-rescheduling timer example.
 *
 * <p>Unlike the reservation expiry example, this one starts a first timer and lets the timed
 * action schedule follow-up timers through {@code timers()} until the job completes.
 */
@HttpEndpoint("/reminder-jobs")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class ReminderJobEndpoint {

  public record CreateReminderJobRequest(
      String recipient, String message, int intervalSeconds, int maxReminders) {}

  public record ReminderJobResponse(
      String jobId,
      String recipient,
      String message,
      int intervalSeconds,
      int sentCount,
      int maxReminders,
      String status) {}

  public record StatusResponse(String jobId, String status, String message, int sentCount) {}

  private final TimerScheduler timerScheduler;
  private final ComponentClient componentClient;

  public ReminderJobEndpoint(TimerScheduler timerScheduler, ComponentClient componentClient) {
    this.timerScheduler = timerScheduler;
    this.componentClient = componentClient;
  }

  @Post("/{jobId}")
  public HttpResponse createJob(String jobId, CreateReminderJobRequest request) {
    try {
      var created =
          componentClient
              .forKeyValueEntity(jobId)
              .method(ReminderJobEntity::start)
              .invoke(
                  new ReminderJob.Start(
                      request.recipient(),
                      request.message(),
                      request.intervalSeconds(),
                      request.maxReminders()));

      timerScheduler.createSingleTimer(
          ReminderJobTimedAction.timerName(jobId),
          Duration.ofSeconds(request.intervalSeconds()),
          componentClient
              .forTimedAction()
              .method(ReminderJobTimedAction::sendReminder)
              .deferred(jobId));

      return HttpResponses.created(toApi(created));
    } catch (CommandException error) {
      return HttpResponses.badRequest(error.getMessage());
    }
  }

  @Get("/{jobId}")
  public HttpResponse getJob(String jobId) {
    var state = componentClient.forKeyValueEntity(jobId).method(ReminderJobEntity::getJob).invoke();
    if (!state.exists()) {
      return HttpResponses.notFound("Reminder job not found.");
    }
    return HttpResponses.ok(toApi(state));
  }

  @Put("/{jobId}/complete")
  public HttpResponse complete(String jobId) {
    var result =
        componentClient.forKeyValueEntity(jobId).method(ReminderJobEntity::complete).invoke();

    return switch (result.outcome()) {
      case OK -> {
        timerScheduler.delete(ReminderJobTimedAction.timerName(jobId));
        yield HttpResponses.ok(
            new StatusResponse(jobId, "completed", result.message(), result.sentCount()));
      }
      case NOT_FOUND -> HttpResponses.notFound(result.message());
      case INVALID -> HttpResponses.badRequest(result.message());
    };
  }

  private static ReminderJobResponse toApi(ReminderJob.State state) {
    return new ReminderJobResponse(
        state.jobId(),
        state.recipient(),
        state.message(),
        state.intervalSeconds(),
        state.sentCount(),
        state.maxReminders(),
        state.status().name().toLowerCase(Locale.ROOT));
  }
}
