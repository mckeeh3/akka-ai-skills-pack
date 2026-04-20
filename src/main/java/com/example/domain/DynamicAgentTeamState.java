package com.example.domain;

import java.util.LinkedHashMap;
import java.util.Map;

/** Durable workflow state for dynamic agent planning and execution. */
public record DynamicAgentTeamState(
    String userQuery,
    AgentPlan plan,
    Map<String, String> agentResponses,
    String answer) {

  public static DynamicAgentTeamState start(String userQuery) {
    return new DynamicAgentTeamState(userQuery, null, Map.of(), "");
  }

  public DynamicAgentTeamState withPlan(AgentPlan newPlan) {
    return new DynamicAgentTeamState(userQuery, newPlan, agentResponses, answer);
  }

  public DynamicAgentTeamState addResponse(String agentId, String response) {
    var updated = new LinkedHashMap<>(agentResponses);
    updated.put(agentId, response);
    return new DynamicAgentTeamState(userQuery, plan, updated, answer);
  }

  public DynamicAgentTeamState withAnswer(String newAnswer) {
    return new DynamicAgentTeamState(userQuery, plan, agentResponses, newAnswer);
  }

  public AgentPlan.PlanStep nextStep() {
    return plan.steps().get(agentResponses.size());
  }

  public boolean hasMoreSteps() {
    return plan != null && agentResponses.size() < plan.steps().size();
  }
}
