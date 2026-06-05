package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import org.junit.jupiter.api.Test;

class ConfiguredModelActivityAgentTest extends TestKitSupport {

  private final TestModelProvider activityModel = new TestModelProvider();

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withAdditionalConfig("akka.javasdk.agent.openai.api-key = n/a")
        .withModelProvider(ConfiguredModelActivityAgent.class, activityModel);
  }

  @Test
  void agentCanSelectConfiguredModelAliasInCode() {
    activityModel.fixedResponse("Try a short gallery visit before dinner.");

    var result =
        componentClient
            .forAgent()
            .inSession("configured-model-session")
            .method(ConfiguredModelActivityAgent::suggest)
            .invoke("Need one concise indoor idea");

    assertEquals("Try a short gallery visit before dinner.", result);
  }
}
