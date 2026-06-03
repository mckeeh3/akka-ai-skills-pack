package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import akka.javasdk.JsonSupport;
import akka.javasdk.agent.ModelTimeoutException;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import org.junit.jupiter.api.Test;

class ActivityAgentTest extends TestKitSupport {

  private final TestModelProvider activityModel = new TestModelProvider();

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withAdditionalConfig("akka.javasdk.agent.openai.api-key = n/a")
        .withModelProvider(ActivityAgent.class, activityModel);
  }

  @Test
  void structuredReplyIsMappedToRecord() {
    activityModel.fixedResponse(
        JsonSupport.encodeToString(
            new ActivityAgent.ActivitySuggestion(
                "Museum visit", "Fits a rainy afternoon indoors.", "indoor")));

    var result =
        componentClient
            .forAgent()
            .inSession("activity-test-session")
            .method(ActivityAgent::suggest)
            .invoke("I need something to do on a rainy afternoon");

    assertEquals("Museum visit", result.name());
    assertEquals("Fits a rainy afternoon indoors.", result.reason());
    assertEquals("indoor", result.setting());
  }

  @Test
  void invalidStructuredJsonFallsBackToDefaultSuggestion() {
    activityModel.fixedResponse("not valid json");

    var result =
        componentClient
            .forAgent()
            .inSession("activity-fallback-session")
            .method(ActivityAgent::suggest)
            .invoke("Give me one idea");

    assertEquals("Walk", result.name());
    assertEquals("outdoor", result.setting());
  }

  @Test
  void nonParsingFailuresAreNotSilentlySwallowed() {
    activityModel.whenMessage(message -> true).failWith(new ModelTimeoutException("timed out"));

    assertThrows(
        RuntimeException.class,
        () ->
            componentClient
                .forAgent()
                .inSession("activity-timeout-session")
                .method(ActivityAgent::suggest)
                .invoke("Give me one idea"));
  }
}
