package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.agent.PromptTemplate;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import com.example.api.ActivityPromptEndpoint;
import org.junit.jupiter.api.Test;

class ActivityPromptEndpointIntegrationTest extends TestKitSupport {

  private final TestModelProvider templatedActivityModel = new TestModelProvider();

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withAdditionalConfig("akka.javasdk.agent.openai.api-key = n/a")
        .withModelProvider(TemplateBackedActivityAgent.class, templatedActivityModel);
  }

  @Test
  void promptEndpointUpdatesAndReadsPromptTemplate() {
    var updateResponse =
        httpClient
            .PUT("/agent-prompts/activity")
            .withRequestBody(
                new ActivityPromptEndpoint.PromptRequest(
                    "You are a concise activity assistant focused on indoor plans."))
            .responseBodyAs(ActivityPromptEndpoint.PromptResponse.class)
            .invoke();

    assertTrue(updateResponse.status().isSuccess());
    assertTrue(updateResponse.body().prompt().contains("indoor plans"));

    var getResponse =
        httpClient
            .GET("/agent-prompts/activity")
            .responseBodyAs(ActivityPromptEndpoint.PromptResponse.class)
            .invoke();

    assertTrue(getResponse.status().isSuccess());
    assertTrue(getResponse.body().prompt().contains("indoor plans"));

    var storedPrompt =
        componentClient
            .forEventSourcedEntity(TemplateBackedActivityAgent.PROMPT_TEMPLATE_ID)
            .method(PromptTemplate::get)
            .invoke();

    assertTrue(storedPrompt.contains("indoor plans"));
  }

  @Test
  void templateBackedAgentCanBeCalledAfterPromptInitialization() {
    componentClient
        .forEventSourcedEntity(TemplateBackedActivityAgent.PROMPT_TEMPLATE_ID)
        .method(PromptTemplate::init)
        .invoke("You are an activity assistant.");

    templatedActivityModel.fixedResponse("Try a museum visit.");

    var answer =
        componentClient
            .forAgent()
            .inSession("templated-agent-session")
            .method(TemplateBackedActivityAgent::suggest)
            .invoke("Need one rainy-day idea");

    assertEquals("Try a museum visit.", answer);
  }
}
