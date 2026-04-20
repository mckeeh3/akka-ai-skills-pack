package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.TestKitSupport;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class ReminderJobEndpointIntegrationTest extends TestKitSupport {

  record CreateReminderJobRequest(
      String recipient, String message, int intervalSeconds, int maxReminders) {}

  record ReminderJobResponse(
      String jobId,
      String recipient,
      String message,
      int intervalSeconds,
      int sentCount,
      int maxReminders,
      String status) {}

  record StatusResponse(String jobId, String status, String message, int sentCount) {}

  @Test
  void timedActionSelfSchedulesUntilMaxRemindersReached() {
    var created =
        await(
            httpClient
                .POST("/reminder-jobs/job-http-1")
                .withRequestBody(new CreateReminderJobRequest("alice@example.com", "Ping", 1, 2))
                .responseBodyAs(ReminderJobResponse.class)
                .invokeAsync());

    assertTrue(created.status().isSuccess());
    assertEquals("active", created.body().status());

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var current =
                  await(
                      httpClient
                          .GET("/reminder-jobs/job-http-1")
                          .responseBodyAs(ReminderJobResponse.class)
                          .invokeAsync());

              assertEquals("completed", current.body().status());
              assertEquals(2, current.body().sentCount());
            });
  }

  @Test
  void completeStopsFutureSelfRescheduling() {
    await(
        httpClient
            .POST("/reminder-jobs/job-http-2")
            .withRequestBody(new CreateReminderJobRequest("bob@example.com", "Ping", 2, 3))
            .responseBodyAs(ReminderJobResponse.class)
            .invokeAsync());

    var completed =
        await(
            httpClient
                .PUT("/reminder-jobs/job-http-2/complete")
                .responseBodyAs(StatusResponse.class)
                .invokeAsync());

    assertTrue(completed.status().isSuccess());
    assertEquals("completed", completed.body().status());
    assertEquals(0, completed.body().sentCount());

    Awaitility.await()
        .ignoreExceptions()
        .during(3, TimeUnit.SECONDS)
        .atMost(6, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var current =
                  await(
                      httpClient
                          .GET("/reminder-jobs/job-http-2")
                          .responseBodyAs(ReminderJobResponse.class)
                          .invokeAsync());

              assertEquals("completed", current.body().status());
              assertEquals(0, current.body().sentCount());
            });
  }

  @Test
  void invalidReminderJobRequestReturnsBadRequest() {
    var error =
        org.junit.jupiter.api.Assertions.assertThrows(
            IllegalArgumentException.class,
            () ->
                await(
                    httpClient
                        .POST("/reminder-jobs/job-http-3")
                        .withRequestBody(new CreateReminderJobRequest("", "", 0, 0))
                        .responseBodyAs(String.class)
                        .invokeAsync()));

    assertTrue(error.getMessage().contains("HTTP status 400 Bad Request"));
    assertTrue(error.getMessage().contains("recipient must not be blank."));
  }
}
