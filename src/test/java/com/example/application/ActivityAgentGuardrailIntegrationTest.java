package com.example.application;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import akka.javasdk.JsonSupport;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import org.junit.jupiter.api.Test;

class ActivityAgentGuardrailIntegrationTest extends TestKitSupport {

  private final TestModelProvider activityModel = new TestModelProvider();

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withAdditionalConfig("""
            akka.javasdk.agent.openai.api-key = n/a
            akka.javasdk.agent.guardrails.\"competitor mention guard\".report-only = false
            """)
        .withModelProvider(ActivityAgent.class, activityModel);
  }

  @Test
  void blockingGuardrailRejectsConfiguredPhraseInModelResponse() {
    activityModel.fixedResponse(
        JsonSupport.encodeToString(
            new ActivityAgent.ActivitySuggestion(
                "CompetitorCo museum",
                "CompetitorCo has a strong indoor option for rainy days.",
                "indoor")));

    var error =
        assertThrows(
            RuntimeException.class,
            () ->
                componentClient
                    .forAgent()
                    .inSession("guardrail-blocking-session")
                    .method(ActivityAgent::suggest)
                    .invoke("Need one indoor activity"));

    assertTrue(error.getMessage().contains("CompetitorCo"));
  }
}
