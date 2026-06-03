package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import akka.javasdk.testkit.EventingTestKit.IncomingMessages;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class ReviewRequestsByStatusViewIntegrationTest extends TestKitSupport {

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT.withWorkflowIncomingMessages(ReviewWorkflow.class);
  }

  @Test
  void workflowStateUpdatesPopulateTheView() {
    IncomingMessages workflowUpdates = testKit.getWorkflowIncomingMessages(ReviewWorkflow.class);

    workflowUpdates.publish(new ReviewWorkflow.State("request-1", "COMPLETED"), "review-1");
    workflowUpdates.publish(new ReviewWorkflow.State("request-2", "PENDING"), "review-2");

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var result =
                  componentClient
                      .forView()
                      .method(ReviewRequestsByStatusView::getByStatus)
                      .invoke(new ReviewRequestsByStatusView.FindByStatus("COMPLETED"));

              assertEquals(1, result.entries().size());
              assertEquals("review-1", result.entries().getFirst().workflowId());
              assertEquals("request-1", result.entries().getFirst().requestId());
            });
  }
}
