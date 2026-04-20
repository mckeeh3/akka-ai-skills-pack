package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.CommandException;
import akka.javasdk.testkit.TestKitSupport;
import com.example.domain.ApprovalState;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class ApprovalWorkflowIntegrationTest extends TestKitSupport {

  @Test
  void startPausesWorkflowUntilApproved() {
    var initial =
        componentClient
            .forWorkflow("approval-1")
            .method(ApprovalWorkflow::start)
            .invoke(new ApprovalWorkflow.StartApproval("document-1", "alice"));

    assertEquals(ApprovalState.Status.WAITING_FOR_APPROVAL, initial.status());

    var accepted =
        componentClient
            .forWorkflow("approval-1")
            .method(ApprovalWorkflow::approve)
            .invoke(new ApprovalWorkflow.Approve("manager-1", "looks good"));

    assertEquals("approval accepted", accepted);

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var completed =
                  componentClient.forWorkflow("approval-1").method(ApprovalWorkflow::get).invoke();

              assertEquals(ApprovalState.Status.APPROVED, completed.status());
              assertEquals("manager-1", completed.approvedBy());
              assertEquals("looks good", completed.comment());
            });
  }

  @Test
  void approveBeforeStartIsRejected() {
    var error =
        assertThrows(
            CommandException.class,
            () ->
                componentClient
                    .forWorkflow("approval-2")
                    .method(ApprovalWorkflow::approve)
                    .invoke(new ApprovalWorkflow.Approve("manager-1", "looks good")));

    assertTrue(error.getMessage().contains("approval not started"));
  }

  @Test
  void blankApproverIsRejected() {
    componentClient
        .forWorkflow("approval-3")
        .method(ApprovalWorkflow::start)
        .invoke(new ApprovalWorkflow.StartApproval("document-1", "alice"));

    var error =
        assertThrows(
            CommandException.class,
            () ->
                componentClient
                    .forWorkflow("approval-3")
                    .method(ApprovalWorkflow::approve)
                    .invoke(new ApprovalWorkflow.Approve("", "looks good")));

    assertTrue(error.getMessage().contains("approvedBy must not be blank"));
  }
}
