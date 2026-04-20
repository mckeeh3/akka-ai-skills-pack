package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.CommandException;
import akka.javasdk.testkit.TestKitSupport;
import com.example.domain.ApprovalDeadlineState;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class ApprovalDeadlineWorkflowIntegrationTest extends TestKitSupport {

  @Test
  void workflowScheduledTimerTimesOutApproval() {
    var initial =
        componentClient
            .forWorkflow("approval-deadline-1")
            .method(ApprovalDeadlineWorkflow::start)
            .invoke(new ApprovalDeadlineWorkflow.StartApproval("document-1", "alice", 1));

    assertEquals(ApprovalDeadlineState.Status.WAITING_FOR_APPROVAL, initial.status());

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var state =
                  componentClient
                      .forWorkflow("approval-deadline-1")
                      .method(ApprovalDeadlineWorkflow::get)
                      .invoke();

              assertEquals(ApprovalDeadlineState.Status.TIMED_OUT, state.status());
            });
  }

  @Test
  void approveDeletesTimerAndWorkflowStaysApproved() {
    componentClient
        .forWorkflow("approval-deadline-2")
        .method(ApprovalDeadlineWorkflow::start)
        .invoke(new ApprovalDeadlineWorkflow.StartApproval("document-2", "bob", 1));

    var accepted =
        componentClient
            .forWorkflow("approval-deadline-2")
            .method(ApprovalDeadlineWorkflow::approve)
            .invoke(new ApprovalDeadlineWorkflow.Approve("manager-1", "looks good"));

    assertEquals("approval accepted", accepted);

    Awaitility.await()
        .ignoreExceptions()
        .during(2, TimeUnit.SECONDS)
        .atMost(5, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var state =
                  componentClient
                      .forWorkflow("approval-deadline-2")
                      .method(ApprovalDeadlineWorkflow::get)
                      .invoke();

              assertEquals(ApprovalDeadlineState.Status.APPROVED, state.status());
              assertEquals("manager-1", state.approvedBy());
            });
  }

  @Test
  void blankApproverIsRejected() {
    componentClient
        .forWorkflow("approval-deadline-3")
        .method(ApprovalDeadlineWorkflow::start)
        .invoke(new ApprovalDeadlineWorkflow.StartApproval("document-3", "alice", 5));

    var error =
        assertThrows(
            CommandException.class,
            () ->
                componentClient
                    .forWorkflow("approval-deadline-3")
                    .method(ApprovalDeadlineWorkflow::approve)
                    .invoke(new ApprovalDeadlineWorkflow.Approve("", "looks good")));

    assertTrue(error.getMessage().contains("approvedBy must not be blank"));
  }
}
