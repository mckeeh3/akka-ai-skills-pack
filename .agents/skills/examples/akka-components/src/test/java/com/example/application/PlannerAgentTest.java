package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.JsonSupport;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import com.example.domain.AgentPlan;
import com.example.domain.AgentSelection;
import java.util.List;
import org.junit.jupiter.api.Test;

class PlannerAgentTest extends TestKitSupport {

  private final TestModelProvider plannerModel = new TestModelProvider();

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withAdditionalConfig("akka.javasdk.agent.openai.api-key = n/a")
        .withModelProvider(PlannerAgent.class, plannerModel);
  }

  @Test
  void plannerShortCircuitsSingleAgentSelection() {
    var result =
        componentClient
            .forAgent()
            .inSession("planner-single-session")
            .method(PlannerAgent::createPlan)
            .invoke(
                new PlannerAgent.Request(
                    "Suggest one thing to do indoors.",
                    new AgentSelection(List.of("activity-worker-agent"))));

    assertEquals(1, result.steps().size());
    assertEquals("activity-worker-agent", result.steps().getFirst().agentId());
    assertEquals("Suggest one thing to do indoors.", result.steps().getFirst().query());
  }

  @Test
  void plannerMapsStructuredResponseForMultiAgentPlan() {
    plannerModel.fixedResponse(
        JsonSupport.encodeToString(
            new AgentPlan(
                List.of(
                    new AgentPlan.PlanStep("weather-agent", "Get the Stockholm forecast."),
                    new AgentPlan.PlanStep(
                        "activity-worker-agent", "Suggest one activity using that forecast.")))));

    var result =
        componentClient
            .forAgent()
            .inSession("planner-multi-session")
            .method(PlannerAgent::createPlan)
            .invoke(
                new PlannerAgent.Request(
                    "I am in Stockholm today. What should I do?",
                    new AgentSelection(List.of("weather-agent", "activity-worker-agent"))));

    assertEquals(2, result.steps().size());
    assertEquals("weather-agent", result.steps().getFirst().agentId());
    assertTrue(result.steps().get(1).query().contains("activity"));
  }
}
