package com.example.domain;

import java.util.List;

/** Dynamic execution plan for a multi-agent workflow. */
public record AgentPlan(List<PlanStep> steps) {

  public record PlanStep(String agentId, String query) {}
}
