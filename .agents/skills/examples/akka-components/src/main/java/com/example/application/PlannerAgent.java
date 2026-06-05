package com.example.application;

import akka.javasdk.JsonSupport;
import akka.javasdk.agent.Agent;
import akka.javasdk.agent.AgentRegistry;
import akka.javasdk.annotations.Component;
import com.example.domain.AgentPlan;
import com.example.domain.AgentSelection;
import java.util.List;

/** Agent that turns a selected agent set into an ordered execution plan. */
@Component(
    id = "planner-agent",
    name = "Planner Agent",
    description = "Creates an ordered execution plan for selected worker agents.")
public class PlannerAgent extends Agent {

  public record Request(String message, AgentSelection selection) {}

  private final AgentRegistry agentRegistry;

  public PlannerAgent(AgentRegistry agentRegistry) {
    this.agentRegistry = agentRegistry;
  }

  public Effect<AgentPlan> createPlan(Request request) {
    if (request.selection().agents().isEmpty()) {
      return effects().reply(new AgentPlan(List.of()));
    } else if (request.selection().agents().size() == 1) {
      return effects()
          .reply(
              new AgentPlan(
                  List.of(new AgentPlan.PlanStep(request.selection().agents().getFirst(), request.message()))));
    }

    var agentInfos = request.selection().agents().stream().map(agentRegistry::agentInfo).toList();
    var systemMessage =
        """
        You are creating an execution plan for worker agents.
        Return only JSON with one field named steps.
        Each step must contain agentId and query.
        The query should be tailored to that agent's expertise.

        Selected agents:
        %s
        """
            .stripIndent()
            .formatted(JsonSupport.encodeToString(agentInfos));

    return effects()
        .systemMessage(systemMessage)
        .userMessage(request.message())
        .responseConformsTo(AgentPlan.class)
        .thenReply();
  }
}
