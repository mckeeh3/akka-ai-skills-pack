package com.example.application;

import static akka.javasdk.testkit.TestModelProvider.AutonomousAgentTools.completeTask;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.agent.task.TaskStatus;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class EvidenceReviewAutonomousAgentIntegrationTest extends TestKitSupport {

  private final TestModelProvider reviewModel = new TestModelProvider();

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withAdditionalConfig("akka.javasdk.agent.openai.api-key = n/a")
        .withModelProvider(EvidenceReviewAutonomousAgent.class, reviewModel);
  }

  @Test
  void taskRuleRejectsUnsourcedResultAndRuntimeRetriesUntilTypedCompletion() {
    reviewModel
        .whenMessage(
            message ->
                message.contains("account takeover risk")
                    && !message.contains("evidenceSources must include at least one source"))
        .reply(
            completeTask(
                new EvidenceReviewTasks.EvidenceReview(
                    "Escalate the account for manual review.", List.of())));

    reviewModel
        .whenMessage(message -> message.contains("Reminder: complete or fail the current task"))
        .reply(
            completeTask(
                new EvidenceReviewTasks.EvidenceReview(
                    "Escalate the account for manual review.",
                    List.of("login-anomaly-report", "support-case-42"))));

    var agentInstanceId = UUID.randomUUID().toString();
    var taskId =
        componentClient
            .forAutonomousAgent(EvidenceReviewAutonomousAgent.class, agentInstanceId)
            .runSingleTask(
                EvidenceReviewTasks.REVIEW.instructions(
                    "Review account takeover risk for customer account acct-123."));

    assertFalse(taskId.isBlank());

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var snapshot = componentClient.forTask(taskId).get(EvidenceReviewTasks.REVIEW);
              // The first completion is rejected by EvidenceReviewRule; the autonomous
              // runtime then reminds the model to complete or fail the task.
              assertEquals(TaskStatus.COMPLETED, snapshot.status());
              var result = snapshot.result().orElseThrow();
              assertEquals("Escalate the account for manual review.", result.recommendation());
              assertEquals(List.of("login-anomaly-report", "support-case-42"), result.evidenceSources());
              assertTrue(snapshot.failureReason().isPresent());
            });
  }
}
