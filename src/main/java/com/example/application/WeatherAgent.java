package com.example.application;

import akka.javasdk.agent.Agent;
import akka.javasdk.annotations.AgentRole;
import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.FunctionTool;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** Focused agent example for function tools. */
@Component(
    id = "weather-agent",
    name = "Weather Agent",
    description = "Answers weather questions and can call date and forecast tools when needed.")
@AgentRole("worker")
public class WeatherAgent extends Agent {

  private static final String SYSTEM_MESSAGE =
      """
      You are a weather assistant.
      Use the available tools when you need the current date or a weather forecast.
      Keep the final answer short and directly useful for planning an activity.
      """
          .stripIndent();

  private final WeatherForecastTools forecastTools = new WeatherForecastTools();

  public Effect<String> query(String message) {
    return effects()
        .systemMessage(SYSTEM_MESSAGE)
        .tools(forecastTools)
        .userMessage(message)
        .thenReply();
  }

  @FunctionTool(description = "Return the current date in yyyy-MM-dd format.")
  private String currentDate() {
    return LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
  }
}
