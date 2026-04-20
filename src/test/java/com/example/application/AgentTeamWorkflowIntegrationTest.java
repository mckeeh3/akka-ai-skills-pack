package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.Done;
import akka.javasdk.JsonSupport;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class AgentTeamWorkflowIntegrationTest extends TestKitSupport {

  private final TestModelProvider weatherModel = new TestModelProvider();
  private final TestModelProvider activityModel = new TestModelProvider();

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withAdditionalConfig("akka.javasdk.agent.openai.api-key = n/a")
        .withModelProvider(WeatherAgent.class, weatherModel)
        .withModelProvider(ActivityAgent.class, activityModel);
  }

  @Test
  void workflowUsesSharedSessionAcrossAgents() {
    weatherModel.fixedResponse("Cool and sunny in Stockholm.");
    activityModel.fixedResponse(
        JsonSupport.encodeToString(
            new ActivityAgent.ActivitySuggestion(
                "Bike tour", "The weather is dry enough for exploring outdoors.", "outdoor")));

    var reply =
        componentClient
            .forWorkflow("agent-team-1")
            .method(AgentTeamWorkflow::start)
            .invoke("I am in Stockholm today. What should I do?");

    assertEquals(Done.getInstance(), reply);

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var answer =
                  componentClient
                      .forWorkflow("agent-team-1")
                      .method(AgentTeamWorkflow::getAnswer)
                      .invoke();

              assertTrue(answer.contains("Bike tour"));
              assertTrue(answer.contains("outdoor"));
            });
  }
}
