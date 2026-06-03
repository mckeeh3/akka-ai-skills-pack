package com.example.application;

import akka.javasdk.annotations.Description;
import akka.javasdk.annotations.FunctionTool;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

/** External function tools that can be registered with an agent through effects().tools(...). */
public class WeatherForecastTools {

  @FunctionTool(description = "Returns a compact weather forecast for a city and optional date.")
  public String getWeather(
      @Description("A location or city name.") String location,
      @Description("Forecast date in yyyy-MM-dd format.") Optional<String> date) {
    var day = date.orElse(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
    var normalized = location == null ? "" : location.trim().toLowerCase(Locale.ROOT);

    return switch (normalized) {
      case "stockholm" -> "Forecast for Stockholm on " + day + ": cool and sunny.";
      case "london" -> "Forecast for London on " + day + ": mild with light rain.";
      case "miami" -> "Forecast for Miami on " + day + ": hot and humid.";
      default -> "Forecast for " + location + " on " + day + ": weather data unavailable.";
    };
  }
}
