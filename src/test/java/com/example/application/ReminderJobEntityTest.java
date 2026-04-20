package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.KeyValueEntityTestKit;
import com.example.domain.ReminderJob;
import org.junit.jupiter.api.Test;

class ReminderJobEntityTest {

  private KeyValueEntityTestKit<ReminderJob.State, ReminderJobEntity> newTestKit(String entityId) {
    return KeyValueEntityTestKit.of(entityId, ReminderJobEntity::new);
  }

  @Test
  void startCreatesActiveReminderJob() {
    var testKit = newTestKit("job-1");

    var result =
        testKit
            .method(ReminderJobEntity::start)
            .invoke(new ReminderJob.Start("alice@example.com", "Ping", 1, 3));

    assertTrue(result.stateWasUpdated());
    assertEquals("active", result.getReply().status().name().toLowerCase());
    assertEquals(0, testKit.getState().sentCount());
  }

  @Test
  void recordReminderSentRequestsAnotherScheduleWhileJobRemainsActive() {
    var testKit = newTestKit("job-2");
    testKit
        .method(ReminderJobEntity::start)
        .invoke(new ReminderJob.Start("bob@example.com", "Reminder", 2, 3));

    var result = testKit.method(ReminderJobEntity::recordReminderSent).invoke();

    assertEquals(ReminderJob.Outcome.OK, result.getReply().outcome());
    assertEquals(1, result.getReply().sentCount());
    assertTrue(result.getReply().shouldScheduleNext());
    assertTrue(result.stateWasUpdated());
    assertTrue(testKit.getState().isActive());
  }

  @Test
  void recordReminderSentStopsReschedulingAtMaxReminders() {
    var testKit = newTestKit("job-3");
    testKit
        .method(ReminderJobEntity::start)
        .invoke(new ReminderJob.Start("cara@example.com", "Reminder", 1, 2));

    testKit.method(ReminderJobEntity::recordReminderSent).invoke();
    var finalResult = testKit.method(ReminderJobEntity::recordReminderSent).invoke();

    assertEquals(ReminderJob.Outcome.OK, finalResult.getReply().outcome());
    assertEquals(2, finalResult.getReply().sentCount());
    assertFalse(finalResult.getReply().shouldScheduleNext());
    assertTrue(testKit.getState().isCompleted());
  }

  @Test
  void invalidStartReturnsError() {
    var testKit = newTestKit("job-4");

    var result =
        testKit
            .method(ReminderJobEntity::start)
            .invoke(new ReminderJob.Start("", "", 0, 0));

    assertTrue(result.isError());
    assertEquals(
        "recipient must not be blank.; message must not be blank.; intervalSeconds must be greater than zero.; maxReminders must be greater than zero.",
        result.getError());
    assertFalse(result.stateWasUpdated());
  }
}
