package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.JsonSupport;
import akka.javasdk.testkit.TestKitSupport;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class ApprovalWorkflowEndpointIntegrationTest extends TestKitSupport {

  record StartApprovalRequest(String documentId, String requestedBy) {}

  record ApproveRequest(String approvedBy, String comment) {}

  record ApprovalResponse(
      String approvalId,
      String documentId,
      String requestedBy,
      String status,
      String approvedBy,
      String comment) {}

  record StatusResponse(String status) {}

  record ApprovalUpdateResponse(String step, String status, String message) {}

  @Test
  void startApproveAndGetApprovalViaEndpoint() {
    var startResponse =
        await(
            httpClient
                .POST("/approvals/approval-http-1")
                .withRequestBody(new StartApprovalRequest("document-1", "alice"))
                .responseBodyAs(ApprovalResponse.class)
                .invokeAsync());

    assertTrue(startResponse.status().isSuccess());
    assertEquals("approval-http-1", startResponse.body().approvalId());
    assertEquals("WAITING_FOR_APPROVAL", startResponse.body().status());

    var approveResponse =
        await(
            httpClient
                .POST("/approvals/approval-http-1/approve")
                .withRequestBody(new ApproveRequest("manager-1", "approved"))
                .responseBodyAs(StatusResponse.class)
                .invokeAsync());

    assertTrue(approveResponse.status().isSuccess());
    assertEquals("approval accepted", approveResponse.body().status());

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var getResponse =
                  await(
                      httpClient
                          .GET("/approvals/approval-http-1")
                          .responseBodyAs(ApprovalResponse.class)
                          .invokeAsync());

              assertTrue(getResponse.status().isSuccess());
              assertEquals("APPROVED", getResponse.body().status());
              assertEquals("manager-1", getResponse.body().approvedBy());
              assertEquals("approved", getResponse.body().comment());
            });
  }

  @Test
  void startValidationErrorBecomesBadRequest() {
    var error =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                await(
                    httpClient
                        .POST("/approvals/approval-http-2")
                        .withRequestBody(new StartApprovalRequest("", "alice"))
                        .responseBodyAs(String.class)
                        .invokeAsync()));

    assertTrue(error.getMessage().contains("HTTP status 400 Bad Request"));
    assertTrue(error.getMessage().contains("documentId must not be blank"));
  }

  @Test
  void approveBeforeStartBecomesBadRequest() {
    var error =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                await(
                    httpClient
                        .POST("/approvals/approval-http-3/approve")
                        .withRequestBody(new ApproveRequest("manager-1", "approved"))
                        .responseBodyAs(String.class)
                        .invokeAsync()));

    assertTrue(error.getMessage().contains("HTTP status 400 Bad Request"));
    assertTrue(error.getMessage().contains("approval not started"));
  }

  @Test
  void getUnknownApprovalBecomesNotFound() {
    var error =
        assertThrows(
            RuntimeException.class,
            () ->
                await(
                    httpClient
                        .GET("/approvals/missing-approval")
                        .responseBodyAs(String.class)
                        .invokeAsync()));

    assertTrue(error.getMessage().contains("HTTP status 404 Not Found"));
    assertTrue(error.getMessage().contains("approval not started"));
  }

  @Test
  void updatesEndpointStreamsApprovalNotifications() throws Exception {
    var starter =
        new Thread(
            () -> {
              try {
                Thread.sleep(500);
              } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
              }

              await(
                  httpClient
                      .POST("/approvals/approval-http-4")
                      .withRequestBody(new StartApprovalRequest("document-4", "alice"))
                      .responseBodyAs(ApprovalResponse.class)
                      .invokeAsync());

              try {
                Thread.sleep(500);
              } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
              }

              await(
                  httpClient
                      .POST("/approvals/approval-http-4/approve")
                      .withRequestBody(new ApproveRequest("manager-4", "approved"))
                      .responseBodyAs(StatusResponse.class)
                      .invokeAsync());
            });
    starter.start();

    var events =
        testKit
            .getSelfSseRouteTester()
            .receiveFirstN("/approvals/approval-http-4/updates", 2, Duration.ofSeconds(10));

    starter.join();

    assertEquals(2, events.size());

    var statuses =
        List.of(
            JsonSupport.getObjectMapper().readTree(events.get(0).getData()).get("status").asText(),
            JsonSupport.getObjectMapper().readTree(events.get(1).getData()).get("status").asText());
    var steps =
        List.of(
            JsonSupport.getObjectMapper().readTree(events.get(0).getData()).get("step").asText(),
            JsonSupport.getObjectMapper().readTree(events.get(1).getData()).get("step").asText());

    assertEquals(List.of("WAITING_FOR_APPROVAL", "APPROVED"), statuses);
    assertEquals(List.of("wait-for-approval", "apply-approval"), steps);
  }
}
