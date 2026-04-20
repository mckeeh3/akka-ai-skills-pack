package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.JsonSupport;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import com.example.api.DynamicAgentTeamWorkflowEndpoint;
import com.example.domain.AgentPlan;
import com.example.domain.AgentSelection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class DynamicAgentTeamWorkflowEndpointIntegrationTest extends TestKitSupport {

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
  void startAndGetWorkflowViaEndpoint() {
    selectorModel.fixedResponse(
        JsonSupport.encodeToString(
            new AgentSelection(List.of("weather-agent", "activity-worker-agent"))));
    plannerModel.fixedResponse(
        JsonSupport.encodeToString(
            new AgentPlan(
                List.of(
                    new AgentPlan.PlanStep("weather-agent", "Weather in Stockholm?"),
                    new AgentPlan.PlanStep(
                        "activity-worker-agent", "Suggest one activity using the forecast.")))));
    weatherModel.fixedResponse("Sunny in Stockholm.");
    activityWorkerModel.fixedResponse("Go for a bike ride.");
    summarizerModel.fixedResponse("Sunny in Stockholm, so a bike ride is a good choice.");

    var startResponse =
        httpClient
            .POST("/dynamic-agent-team/dynamic-http-1")
            .withRequestBody(new DynamicAgentTeamWorkflowEndpoint.StartRequest("What should I do today in Stockholm?"))
            .responseBodyAs(DynamicAgentTeamWorkflowEndpoint.WorkflowResponse.class)
            .invoke();

    assertTrue(startResponse.status().isSuccess());
    assertEquals("dynamic-http-1", startResponse.body().workflowId());

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var getResponse =
                  httpClient
                      .GET("/dynamic-agent-team/dynamic-http-1")
                      .responseBodyAs(DynamicAgentTeamWorkflowEndpoint.WorkflowResponse.class)
                      .invoke();

              assertTrue(getResponse.status().isSuccess());
              assertTrue(getResponse.body().answer().contains("bike ride"));
            });
  }

  @Test
  void blankQueryReturnsBadRequest() {
    var error =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                httpClient
                    .POST("/dynamic-agent-team/dynamic-http-2")
                    .withRequestBody(new DynamicAgentTeamWorkflowEndpoint.StartRequest(" "))
                    .responseBodyAs(String.class)
                    .invoke());

    assertTrue(error.getMessage().contains("HTTP status 400 Bad Request"));
    assertTrue(error.getMessage().contains("query must not be blank"));
  }
}
