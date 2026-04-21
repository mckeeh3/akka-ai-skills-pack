package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.JsonSupport;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import com.example.domain.AgentSelection;
import java.util.List;
import org.junit.jupiter.api.Test;

class SelectorAgentTest extends TestKitSupport {

  private final TestModelProvider selectorModel = new TestModelProvider();

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withAdditionalConfig("akka.javasdk.agent.openai.api-key = n/a")
        .withModelProvider(SelectorAgent.class, selectorModel);
  }

  @Test
  void selectorMapsStructuredResponseToAgentSelection() {
    selectorModel.fixedResponse(
        JsonSupport.encodeToString(
            new AgentSelection(List.of("weather-agent", "activity-worker-agent"))));

    var result =
        componentClient
            .forAgent()
            .inSession("selector-test-session")
            .method(SelectorAgent::selectAgents)
            .invoke("I am visiting Stockholm today and need ideas based on the weather.");

    assertEquals(2, result.agents().size());
    assertTrue(result.agents().contains("weather-agent"));
    assertTrue(result.agents().contains("activity-worker-agent"));
  }
}
