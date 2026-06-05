package ai.first.application;

import static akka.javasdk.testkit.TestModelProvider.AutonomousAgentTools.completeTask;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.JsonSupport;
import akka.javasdk.agent.task.TaskStatus;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import ai.first.api.AutonomousQuestionEndpoint;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class AnswerQuestionAutonomousAgentIntegrationTest extends TestKitSupport {

  private final TestModelProvider answerModel = new TestModelProvider();

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withAdditionalConfig("akka.javasdk.agent.openai.api-key = n/a")
        .withModelProvider(AnswerQuestionAutonomousAgent.class, answerModel);
  }

  @Test
  void endpointStartsAutonomousAgentTaskAndStoresTypedResult() {
    answerModel.fixedResponse(
        new TestModelProvider.AiResponse(
            completeTask(new AnswerQuestionTasks.Answer("2 plus 2 equals 4.", 100))));

    var response =
        httpClient
            .POST("/autonomous/questions")
            .withRequestBody(new AutonomousQuestionEndpoint.AskQuestion("What is 2 + 2?"))
            .responseBodyAs(AutonomousQuestionEndpoint.QuestionTaskResponse.class)
            .invoke();

    assertTrue(response.status().isSuccess());
    assertFalse(response.body().taskId().isBlank());
    assertFalse(response.body().agentInstanceId().isBlank());
    assertEquals("answer-question-autonomous-agent", response.body().agentComponentId());

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var snapshot =
                  componentClient.forTask(response.body().taskId()).get(AnswerQuestionTasks.ANSWER);
              assertEquals(TaskStatus.COMPLETED, snapshot.status());
              var result = snapshot.result().orElseThrow();
              assertEquals("2 plus 2 equals 4.", result.answer());
              assertEquals(100, result.confidence());
            });
  }

  @Test
  void taskNotificationEndpointStreamsTerminalNotificationAndSnapshotRemainsSourceOfTruth()
      throws Exception {
    answerModel.fixedResponse(
        new TestModelProvider.AiResponse(
            completeTask(new AnswerQuestionTasks.Answer("Notifications are observable progress.", 90))));

    var taskId = UUID.randomUUID().toString();
    var agentInstanceId = UUID.randomUUID().toString();

    componentClient
        .forTask(taskId)
        .create(AnswerQuestionTasks.ANSWER.instructions("Explain notification streams."));
    var starter =
        new Thread(
            () -> {
              try {
                Thread.sleep(200);
              } catch (InterruptedException error) {
                Thread.currentThread().interrupt();
                return;
              }
              componentClient
                  .forAutonomousAgent(AnswerQuestionAutonomousAgent.class, agentInstanceId)
                  .assignTasks(taskId);
            });
    starter.start();

    var events =
        testKit
            .getSelfSseRouteTester()
            .receiveFirstN(
                "/autonomous/questions/tasks/" + taskId + "/notifications",
                1,
                Duration.ofSeconds(10));
    starter.join();

    assertEquals(1, events.size());
    assertEquals(taskId, events.get(0).getId().orElseThrow());
    assertEquals("task-notification", events.get(0).getEventType().orElseThrow());
    var notification = JsonSupport.getObjectMapper().readTree(events.get(0).getData());
    assertEquals(taskId, notification.get("taskId").asText());
    assertEquals("AnswerQuestion", notification.get("taskName").asText());
    assertEquals("COMPLETED", notification.get("status").asText());

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var snapshot = componentClient.forTask(taskId).get(AnswerQuestionTasks.ANSWER);
              assertEquals(TaskStatus.COMPLETED, snapshot.status());
              var result = snapshot.result().orElseThrow();
              assertEquals("Notifications are observable progress.", result.answer());
              assertEquals(90, result.confidence());
            });
  }
}
