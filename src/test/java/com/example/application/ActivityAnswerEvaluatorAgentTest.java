package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.JsonSupport;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import org.junit.jupiter.api.Test;

class ActivityAnswerEvaluatorAgentTest extends TestKitSupport {

  private final TestModelProvider evaluatorModel = new TestModelProvider();

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withAdditionalConfig("akka.javasdk.agent.openai.api-key = n/a")
        .withModelProvider(ActivityAnswerEvaluatorAgent.class, evaluatorModel);
  }

  @Test
  void evaluatorMapsCorrectLabelToPassedResult() {
    evaluatorModel.fixedResponse(
        JsonSupport.encodeToString(
            new java.util.LinkedHashMap<String, Object>() {
              {
                put("explanation", "The answer matches the request.");
                put("label", "correct");
              }
            }));

    var result =
        componentClient
            .forAgent()
            .inSession("evaluator-session")
            .method(ActivityAnswerEvaluatorAgent::evaluate)
            .invoke(new ActivityAnswerEvaluatorAgent.EvaluationRequest("Need an indoor idea", "Visit a museum."));

    assertTrue(result.passed());
    assertEquals("The answer matches the request.", result.explanation());
  }

  @Test
  void evaluatorRejectsUnknownLabel() {
    evaluatorModel.fixedResponse(
        JsonSupport.encodeToString(
            new java.util.LinkedHashMap<String, Object>() {
              {
                put("explanation", "The label is unsupported.");
                put("label", "maybe");
              }
            }));

    assertThrows(
        RuntimeException.class,
        () ->
            componentClient
                .forAgent()
                .inSession("evaluator-invalid-session")
                .method(ActivityAnswerEvaluatorAgent::evaluate)
                .invoke(
                    new ActivityAnswerEvaluatorAgent.EvaluationRequest(
                        "Need an indoor idea", "Maybe take a walk.")));
  }
}
