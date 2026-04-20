package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.TestKitSupport;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class ApprovalDeadlineWorkflowEndpointIntegrationTest extends TestKitSupport {

  record StartApprovalRequest(String documentId, String requestedBy, int timeoutSeconds) {}

  record ApproveRequest(String approvedBy, String comment) {}

  record ApprovalDeadlineResponse(
      String approvalId,
      String documentId,
      String requestedBy,
      int timeoutSeconds,
      String status,
      String approvedBy,
      String comment) {}

  record StatusResponse(String status) {}

  @Test
  void deadlineWorkflowEndpointShowsTimedOutStateAfterTimerFires() {
    var created =
        await(
            httpClient
                .POST("/approval-deadlines/approval-http-1")
                .withRequestBody(new StartApprovalRequest("document-1", "alice", 1))
                .responseBodyAs(ApprovalDeadlineResponse.class)
                .invokeAsync());

    assertTrue(created.status().isSuccess());
    assertEquals("WAITING_FOR_APPROVAL", created.body().status());

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var current =
                  await(
                      httpClient
                          .GET("/approval-deadlines/approval-http-1")
                          .responseBodyAs(ApprovalDeadlineResponse.class)
                          .invokeAsync());

              assertEquals("TIMED_OUT", current.body().status());
            });
  }

  @Test
  void approvingThroughEndpointKeepsWorkflowApproved() {
    await(
        httpClient
            .POST("/approval-deadlines/approval-http-2")
            .withRequestBody(new StartApprovalRequest("document-2", "bob", 1))
            .responseBodyAs(ApprovalDeadlineResponse.class)
            .invokeAsync());

    var approved =
        await(
            httpClient
                .POST("/approval-deadlines/approval-http-2/approve")
                .withRequestBody(new ApproveRequest("manager-1", "looks good"))
                .responseBodyAs(StatusResponse.class)
                .invokeAsync());

    assertTrue(approved.status().isSuccess());
    assertEquals("approval accepted", approved.body().status());

    Awaitility.await()
        .ignoreExceptions()
        .during(2, TimeUnit.SECONDS)
        .atMost(5, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var current =
                  await(
                      httpClient
                          .GET("/approval-deadlines/approval-http-2")
                          .responseBodyAs(ApprovalDeadlineResponse.class)
                          .invokeAsync());

              assertEquals("APPROVED", current.body().status());
              assertEquals("manager-1", current.body().approvedBy());
            });
  }

  @Test
  void invalidStartRequestReturnsBadRequest() {
    var error =
        org.junit.jupiter.api.Assertions.assertThrows(
            IllegalArgumentException.class,
            () ->
                await(
                    httpClient
                        .POST("/approval-deadlines/approval-http-3")
                        .withRequestBody(new StartApprovalRequest("", "", 0))
                        .responseBodyAs(String.class)
                        .invokeAsync()));

    assertTrue(error.getMessage().contains("HTTP status 400 Bad Request"));
    assertTrue(error.getMessage().contains("documentId must not be blank"));
  }
}
