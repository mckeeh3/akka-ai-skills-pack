package com.example.application;

import static java.time.Duration.ofSeconds;

import akka.Done;
import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.StepName;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.client.DynamicMethodRef;
import akka.javasdk.workflow.Workflow;
import akka.javasdk.workflow.Workflow.RecoverStrategy;
import com.example.domain.AgentPlan;
import com.example.domain.AgentSelection;
import com.example.domain.DynamicAgentTeamState;

/** Workflow example for selector/planner-based dynamic multi-agent execution. */
@Component(id = "dynamic-agent-team-workflow")
public class DynamicAgentTeamWorkflow extends Workflow<DynamicAgentTeamState> {

  private final ComponentClient componentClient;

  public DynamicAgentTeamWorkflow(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  public Effect<Done> start(String query) {
    if (query == null || query.isBlank()) {
      return effects().error("query must not be blank");
    } else if (currentState() != null) {
      return effects().error("workflow already started");
    }

    return effects()
        .updateState(DynamicAgentTeamState.start(query))
        .transitionTo(DynamicAgentTeamWorkflow::selectAgentsStep)
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
        .defaultStepTimeout(ofSeconds(60))
        .defaultStepRecovery(
            RecoverStrategy.maxRetries(1).failoverTo(DynamicAgentTeamWorkflow::summarizeStep))
        .stepRecovery(
            DynamicAgentTeamWorkflow::selectAgentsStep,
            RecoverStrategy.maxRetries(1).failoverTo(DynamicAgentTeamWorkflow::errorStep))
        .stepRecovery(
            DynamicAgentTeamWorkflow::createPlanStep,
            RecoverStrategy.maxRetries(1).failoverTo(DynamicAgentTeamWorkflow::errorStep))
        .build();
  }

  @StepName("select-agents")
  private StepEffect selectAgentsStep() {
    var selection =
        componentClient
            .forAgent()
            .inSession(sessionId())
            .method(SelectorAgent::selectAgents)
            .invoke(currentState().userQuery());

    if (selection.agents().isEmpty()) {
      return stepEffects()
          .updateState(currentState().withAnswer("No suitable agents found"))
          .thenEnd();
    }

    return stepEffects().thenTransitionTo(DynamicAgentTeamWorkflow::createPlanStep).withInput(selection);
  }

  @StepName("create-plan")
  private StepEffect createPlanStep(AgentSelection selection) {
    var plan =
        componentClient
            .forAgent()
            .inSession(sessionId())
            .method(PlannerAgent::createPlan)
            .invoke(new PlannerAgent.Request(currentState().userQuery(), selection));

    if (plan.steps().isEmpty()) {
      return stepEffects()
          .updateState(currentState().withAnswer("No execution plan could be created"))
          .thenEnd();
    }

    return stepEffects()
        .updateState(currentState().withPlan(plan))
        .thenTransitionTo(DynamicAgentTeamWorkflow::executePlanStep);
  }

  @StepName("execute-plan")
  private StepEffect executePlanStep() {
    var step = currentState().nextStep();
    var response = callAgent(step);
    var newState = currentState().addResponse(step.agentId(), response);

    if (newState.hasMoreSteps()) {
      return stepEffects().updateState(newState).thenTransitionTo(DynamicAgentTeamWorkflow::executePlanStep);
    }

    return stepEffects().updateState(newState).thenTransitionTo(DynamicAgentTeamWorkflow::summarizeStep);
  }

  @StepName("summarize")
  private StepEffect summarizeStep() {
    if (currentState().agentResponses().isEmpty()) {
      return stepEffects().updateState(currentState().withAnswer("No agent responses available")).thenEnd();
    }

    var answer =
        componentClient
            .forAgent()
            .inSession(sessionId())
            .method(SummarizerAgent::summarize)
            .invoke(new SummarizerAgent.Request(currentState().userQuery(), currentState().agentResponses().values()));

    return stepEffects().updateState(currentState().withAnswer(answer)).thenEnd();
  }

  private StepEffect errorStep() {
    return stepEffects().updateState(currentState().withAnswer("Unable to create an agent plan")).thenEnd();
  }

  private String callAgent(AgentPlan.PlanStep step) {
    DynamicMethodRef<String, String> call =
        componentClient.forAgent().inSession(sessionId()).dynamicCall(step.agentId());
    return call.invoke(step.query());
  }

  private String sessionId() {
    return commandContext().workflowId();
  }
}
