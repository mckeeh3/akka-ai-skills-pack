package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.JsonSupport;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import com.example.api.ActivityAgentEndpoint;
import org.junit.jupiter.api.Test;

class ActivityAgentEndpointIntegrationTest extends TestKitSupport {

  private final TestModelProvider activityModel = new TestModelProvider();
  private final TestModelProvider streamingModel = new TestModelProvider();

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withAdditionalConfig("akka.javasdk.agent.openai.api-key = n/a")
        .withModelProvider(ActivityAgent.class, activityModel)
        .withModelProvider(StreamingActivityAgent.class, streamingModel);
  }

  @Test
  void askEndpointReturnsStructuredAgentReply() {
    activityModel.fixedResponse(
        JsonSupport.encodeToString(
            new ActivityAgent.ActivitySuggestion(
                "Board games", "Good for a small group indoors.", "indoor")));

    var response =
        httpClient
            .POST("/agents/activity/ask")
            .withRequestBody(new ActivityAgentEndpoint.AskRequest("session-1", "Need a team activity"))
            .responseBodyAs(ActivityAgentEndpoint.AskResponse.class)
            .invoke();

    assertTrue(response.status().isSuccess());
    assertEquals("session-1", response.body().sessionId());
    assertEquals("Board games", response.body().suggestion().name());
    assertEquals("indoor", response.body().suggestion().setting());
  }

  @Test
  void streamEndpointReturnsCollectedTokenText() {
    streamingModel.fixedResponse("Try a riverside walk while the weather stays clear.");

    var response =
        httpClient
            .POST("/agents/activity/stream")
            .withRequestBody(
                new ActivityAgentEndpoint.AskRequest(
                    "session-2", "Need one simple outdoor idea"))
            .responseBodyAs(String.class)
            .invoke();

    assertTrue(response.status().isSuccess());
    assertTrue(response.body().contains("riverside walk"));
  }

  @Test
  void blankSessionIdReturnsBadRequest() {
    var error =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                httpClient
                    .POST("/agents/activity/ask")
                    .withRequestBody(new ActivityAgentEndpoint.AskRequest(" ", "Need an idea"))
                    .responseBodyAs(String.class)
                    .invoke());

    assertTrue(error.getMessage().contains("HTTP status 400 Bad Request"));
    assertTrue(error.getMessage().contains("sessionId must not be blank"));
  }
}
