package com.example.application;

import static java.time.Duration.ofSeconds;

import akka.Done;
import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.StepName;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.workflow.Workflow;
import akka.javasdk.workflow.Workflow.RecoverStrategy;

/**
 * Focused workflow example that orchestrates multiple agents while reusing the workflow id as the
 * shared agent session id.
 */
@Component(id = "agent-team-workflow")
public class AgentTeamWorkflow extends Workflow<AgentTeamWorkflow.State> {

  public record State(String userQuery, String weatherForecast, String answer) {
    State withWeatherForecast(String forecast) {
      return new State(userQuery, forecast, answer);
    }

    State withAnswer(String newAnswer) {
      return new State(userQuery, weatherForecast, newAnswer);
    }
  }

  private final ComponentClient componentClient;

  public AgentTeamWorkflow(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  public Effect<Done> start(String query) {
    if (query == null || query.isBlank()) {
      return effects().error("query must not be blank");
    } else if (currentState() != null) {
      return effects().error("workflow already started");
    }

    return effects()
        .updateState(new State(query, "", ""))
        .transitionTo(AgentTeamWorkflow::askWeather)
        .thenReply(Done.getInstance());
  }

  public ReadOnlyEffect<String> getAnswer() {
    if (currentState() == null || currentState().answer().isBlank()) {
      return effects().error("workflow not completed");
    }
    return effects().reply(currentState().answer());
  }

  @Override
  public WorkflowSettings settings() {
    return WorkflowSettings.builder()
        .stepTimeout(AgentTeamWorkflow::askWeather, ofSeconds(60))
        .stepTimeout(AgentTeamWorkflow::suggestActivity, ofSeconds(60))
        .defaultStepRecovery(
            RecoverStrategy.maxRetries(2).failoverTo(AgentTeamWorkflow::errorStep))
        .build();
  }

  @StepName("ask-weather")
  private StepEffect askWeather() {
    var forecast =
        componentClient
            .forAgent()
            .inSession(sessionId())
            .method(WeatherAgent::query)
            .invoke(currentState().userQuery());

    return stepEffects()
        .updateState(currentState().withWeatherForecast(forecast))
        .thenTransitionTo(AgentTeamWorkflow::suggestActivity);
  }

  @StepName("suggest-activity")
  private StepEffect suggestActivity() {
    var request =
        currentState().userQuery() + "\nWeather forecast: " + currentState().weatherForecast();

    var suggestion =
        componentClient
            .forAgent()
            .inSession(sessionId())
            .method(ActivityAgent::suggest)
            .invoke(request);

    var answer = suggestion.name() + ": " + suggestion.reason() + " (" + suggestion.setting() + ")";

    return stepEffects().updateState(currentState().withAnswer(answer)).thenEnd();
  }

  private StepEffect errorStep() {
    return stepEffects().updateState(currentState().withAnswer("Unable to complete request")).thenEnd();
  }

  private String sessionId() {
    return commandContext().workflowId();
  }
}
