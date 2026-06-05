package com.example.application;

import akka.javasdk.agent.autonomous.AgentDefinition;
import akka.javasdk.agent.autonomous.AutonomousAgent;
import akka.javasdk.agent.autonomous.capability.TaskAcceptance;
import akka.javasdk.annotations.Component;

/** Minimal Autonomous Agent example for one durable typed task. */
@Component(
    id = "answer-question-autonomous-agent",
    description =
        "Answers one focused question as a durable background task and returns a concise typed answer.")
public class AnswerQuestionAutonomousAgent extends AutonomousAgent {

  @Override
  public AgentDefinition definition() {
    return define()
        .instructions(
            "Answer the submitted question concisely. Include a confidence score from 0 to 100.")
        .capability(TaskAcceptance.of(AnswerQuestionTasks.ANSWER).maxIterationsPerTask(3));
  }
}
