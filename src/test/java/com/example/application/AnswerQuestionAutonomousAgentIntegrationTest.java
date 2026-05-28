package com.example.application;

import static akka.javasdk.testkit.TestModelProvider.AutonomousAgentTools.completeTask;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.agent.task.TaskStatus;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import com.example.api.AutonomousQuestionEndpoint;
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
}
