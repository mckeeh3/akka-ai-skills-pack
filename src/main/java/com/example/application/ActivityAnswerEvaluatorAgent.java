package com.example.application;

import akka.javasdk.agent.Agent;
import akka.javasdk.agent.EvaluationResult;
import akka.javasdk.agent.MemoryProvider;
import akka.javasdk.annotations.AgentRole;
import akka.javasdk.annotations.Component;
import java.util.Locale;

/** Focused evaluator example for LLM-as-judge style agent evaluations. */
@Component(
    id = "activity-answer-evaluator",
    name = "Activity Answer Evaluator",
    description = "Evaluates whether an activity answer matches the user's request.")
@AgentRole("evaluator")
public class ActivityAnswerEvaluatorAgent extends Agent {

  private static final String SYSTEM_MESSAGE =
      """
      You are evaluating whether an AI-generated activity suggestion matches a user's request.
      Respond with JSON containing:
      - explanation: short reason for the decision
      - label: either correct or incorrect
      """
          .stripIndent();

  public record EvaluationRequest(String question, String aiAnswer) {}

  record ModelResult(String explanation, String label) {
    Result toEvaluationResult() {
      if (label == null) {
        throw new IllegalArgumentException("label must be present");
      }

      var passed =
          switch (label.toLowerCase(Locale.ROOT)) {
            case "correct" -> true;
            case "incorrect" -> false;
            default -> throw new IllegalArgumentException("unknown label " + label);
          };

      return new Result(explanation, passed);
    }
  }

  public record Result(String explanation, boolean passed) implements EvaluationResult {}

  public Effect<Result> evaluate(EvaluationRequest request) {
    var userMessage =
        """
        [Question]
        %s

        [AI Answer]
        %s
        """
            .stripIndent()
            .formatted(request.question(), request.aiAnswer());

    return effects()
        .memory(MemoryProvider.none())
        .systemMessage(SYSTEM_MESSAGE)
        .userMessage(userMessage)
        .responseConformsTo(ModelResult.class)
        .map(ModelResult::toEvaluationResult)
        .thenReply();
  }
}
