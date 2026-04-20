package com.example.application;

import akka.javasdk.Metadata;
import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Consume;
import akka.javasdk.annotations.DeleteHandler;
import akka.javasdk.annotations.Produce;
import akka.javasdk.consumer.Consumer;

/**
 * Workflow consumer that publishes review completions to a topic.
 */
@Component(id = "review-workflow-topic-consumer")
@Consume.FromWorkflow(ReviewWorkflow.class)
@Produce.ToTopic("review-events")
public class ReviewWorkflowTopicConsumer extends Consumer {

  public record ReviewCompleted(String workflowId, String requestId, String status) {}

  public Effect onUpdate(ReviewWorkflow.State state) {
    if (!"COMPLETED".equals(state.status())) {
      return effects().ignore();
    }

    var workflowId = messageContext().eventSubject().orElseThrow();
    Metadata metadata = Metadata.EMPTY.add("ce-subject", workflowId);
    return effects().produce(new ReviewCompleted(workflowId, state.requestId(), state.status()), metadata);
  }

  @DeleteHandler
  public Effect onDelete() {
    return effects().ignore();
  }
}
