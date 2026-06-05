package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.Done;
import akka.javasdk.JsonSupport;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import com.example.domain.AgentPlan;
import com.example.domain.AgentSelection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class DynamicAgentTeamWorkflowIntegrationTest extends TestKitSupport {

  private final TestModelProvider selectorModel = new TestModelProvider();
  private final TestModelProvider plannerModel = new TestModelProvider();
  private final TestModelProvider weatherModel = new TestModelProvider();
  private final TestModelProvider activityWorkerModel = new TestModelProvider();
  private final TestModelProvider summarizerModel = new TestModelProvider();

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withAdditionalConfig("akka.javasdk.agent.openai.api-key = n/a")
        .withModelProvider(SelectorAgent.class, selectorModel)
        .withModelProvider(PlannerAgent.class, plannerModel)
        .withModelProvider(WeatherAgent.class, weatherModel)
        .withModelProvider(ActivityWorkerAgent.class, activityWorkerModel)
        .withModelProvider(SummarizerAgent.class, summarizerModel);
  }

  @Test
  void dynamicWorkflowSelectsPlansExecutesAndSummarizes() {
    selectorModel.fixedResponse(
        JsonSupport.encodeToString(
            new AgentSelection(List.of("weather-agent", "activity-worker-agent"))));

    plannerModel.fixedResponse(
        JsonSupport.encodeToString(
            new AgentPlan(
                List.of(
                    new AgentPlan.PlanStep(
                        "weather-agent", "What is the weather today in Stockholm?"),
                    new AgentPlan.PlanStep(
                        "activity-worker-agent",
                        "Suggest one activity in Stockholm using the weather forecast.")))));

    weatherModel.fixedResponse("The weather in Stockholm is sunny and cool.");
    activityWorkerModel.fixedResponse("Take a bike ride around Djurgården.");
    summarizerModel.fixedResponse(
        "It is sunny and cool in Stockholm, so a bike ride around Djurgården is a good choice.");

    var reply =
        componentClient
            .forWorkflow("dynamic-agent-team-1")
            .method(DynamicAgentTeamWorkflow::start)
            .invoke("I am in Stockholm today. What should I do?");

    assertEquals(Done.getInstance(), reply);

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var answer =
                  componentClient
                      .forWorkflow("dynamic-agent-team-1")
                      .method(DynamicAgentTeamWorkflow::getAnswer)
                      .invoke();

              assertTrue(answer.contains("Stockholm"));
              assertTrue(answer.contains("Djurgården"));
            });
  }
}
