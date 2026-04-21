package com.example.application;

import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import akka.javasdk.testkit.TestModelProvider.AiResponse;
import akka.javasdk.testkit.TestModelProvider.ToolInvocationRequest;
import org.junit.jupiter.api.Test;

class WeatherAgentTest extends TestKitSupport {

  private final TestModelProvider weatherModel = new TestModelProvider();

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withAdditionalConfig("akka.javasdk.agent.openai.api-key = n/a")
        .withModelProvider(WeatherAgent.class, weatherModel);
  }

  @Test
  void weatherAgentCanUseForecastTool() {
    weatherModel
        .whenMessage(message -> message.contains("Stockholm"))
        .reply(
            new ToolInvocationRequest(
                "WeatherForecastTools_getWeather",
                "{\"location\":\"Stockholm\",\"date\":\"2026-04-20\"}"));
    weatherModel
        .whenToolResult(result -> result.name().equals("WeatherForecastTools_getWeather"))
        .thenReply(
            result ->
                new AiResponse(
                    result.content().contains("sunny")
                        ? "Forecast: cool and sunny in Stockholm."
                        : "Forecast unavailable."));

    var answer =
        componentClient
            .forAgent()
            .inSession("weather-test-session")
            .method(WeatherAgent::query)
            .invoke("What is the weather in Stockholm on 2026-04-20?");

    assertTrue(answer.contains("Stockholm"));
    assertTrue(answer.contains("sunny"));
  }
}
