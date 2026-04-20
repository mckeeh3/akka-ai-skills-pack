package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import akka.javasdk.testkit.EventingTestKit;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReviewWorkflowTopicConsumerIntegrationTest extends TestKitSupport {

  private EventingTestKit.IncomingMessages workflowUpdates;
  private EventingTestKit.OutgoingMessages reviewEvents;

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withWorkflowIncomingMessages(ReviewWorkflow.class)
        .withTopicOutgoingMessages("review-events");
  }

  @BeforeEach
  void setUpEventing() {
    workflowUpdates = testKit.getWorkflowIncomingMessages(ReviewWorkflow.class);
    reviewEvents = testKit.getTopicOutgoingMessages("review-events");
    reviewEvents.clear();
  }

  @Test
  void completedWorkflowStatesArePublishedToTopic() {
    workflowUpdates.publish(new ReviewWorkflow.State("request-1", "PENDING"), "review-1");
    workflowUpdates.publish(new ReviewWorkflow.State("request-1", "COMPLETED"), "review-1");

    var message = reviewEvents.expectOneTyped(ReviewWorkflowTopicConsumer.ReviewCompleted.class);

    assertEquals("review-1", message.getPayload().workflowId());
    assertEquals("request-1", message.getPayload().requestId());
    assertEquals("COMPLETED", message.getPayload().status());
    assertEquals("review-1", message.getMetadata().get("ce-subject").orElseThrow());
  }
}
