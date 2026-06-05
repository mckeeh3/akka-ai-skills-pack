package com.example.application;

import akka.javasdk.annotations.Description;
import akka.javasdk.agent.task.Task;

/** Task definitions and typed result records for {@link AnswerQuestionAutonomousAgent}. */
public final class AnswerQuestionTasks {

  private AnswerQuestionTasks() {}

  public static final Task<Answer> ANSWER =
      Task.name("AnswerQuestion")
          .description("Answer one question with a concise explanation and confidence score")
          .resultConformsTo(Answer.class);

  public record Answer(
      @Description("Concise answer to the submitted question") String answer,
      @Description("Confidence score from 0 to 100") int confidence) {}
}
