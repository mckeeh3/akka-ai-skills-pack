package com.example.application;

import akka.javasdk.JsonSupport;
import akka.javasdk.agent.Agent;
import akka.javasdk.agent.AgentRegistry;
import akka.javasdk.annotations.Component;
import com.example.domain.AgentSelection;

/** Planner-side agent that chooses which worker agents should participate. */
@Component(
    id = "selector-agent",
    name = "Selector Agent",
    description = "Selects useful worker agents for a user request based on agent descriptions.")
public class SelectorAgent extends Agent {

  private final String systemMessage;

  public SelectorAgent(AgentRegistry agentRegistry) {
    var workerAgents = agentRegistry.agentsWithRole("worker");
    this.systemMessage =
        """
        Your job is to analyze the user request and select which worker agents should be used.
        Return only JSON with one field named agents that contains the selected agent ids.
        If no listed agents are suitable, return an empty array.

        Available worker agents:
        %s
        """
            .stripIndent()
            .formatted(JsonSupport.encodeToString(workerAgents));
  }

  public Effect<AgentSelection> selectAgents(String message) {
    return effects()
        .systemMessage(systemMessage)
        .userMessage(message)
        .responseConformsTo(AgentSelection.class)
        .thenReply();
  }
}
