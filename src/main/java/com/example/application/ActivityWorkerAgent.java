package com.example.application;

import akka.javasdk.agent.Agent;
import akka.javasdk.annotations.AgentRole;
import akka.javasdk.annotations.Component;

/** Worker agent used by the dynamic multi-agent workflow. */
@Component(
    id = "activity-worker-agent",
    name = "Activity Worker Agent",
    description = "Suggests activities that fit a user request and any weather context provided.")
@AgentRole("worker")
public class ActivityWorkerAgent extends Agent {

  private static final String SYSTEM_MESSAGE =
      """
      You are an activity planning worker.
      Suggest one or two concise activity ideas based only on the request and provided context.
      """
          .stripIndent();

  public Effect<String> query(String message) {
    return effects().systemMessage(SYSTEM_MESSAGE).userMessage(message).thenReply();
  }
}
