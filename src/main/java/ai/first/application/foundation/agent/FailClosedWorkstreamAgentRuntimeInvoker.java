package ai.first.application.foundation.agent;

import ai.first.domain.foundation.agent.AgentRuntimeTrace;
import java.util.List;

/** Fail-closed placeholder used only when a service is constructed without an Akka ComponentClient. */
public final class FailClosedWorkstreamAgentRuntimeInvoker implements WorkstreamAgentRuntimeInvoker {
  @Override
  public AgentRuntimeService.RuntimeInvocationResult invokeWorkstreamAgent(AgentRuntimeService.RuntimeInvocationRequest request) {
    return new AgentRuntimeService.RuntimeInvocationResult(
        AgentRuntimeTrace.Decision.DENIED,
        null,
        List.of("trace-akka-agent-component-client-missing"),
        "AKKA_AGENT_COMPONENT_CLIENT_REQUIRED",
        "Workstream message submission requires the Akka Agent ComponentClient runtime path.");
  }

  @Override
  public AgentRuntimeService.PlanProposalInvocationResult proposeChatToolPlan(AgentRuntimeService.PlanProposalInvocationRequest request) {
    return AgentRuntimeService.planProposalUnavailable(
        request,
        "AKKA_AGENT_COMPONENT_CLIENT_REQUIRED",
        "Workstream chat tool plan proposal requires the Akka Agent ComponentClient runtime path.",
        List.of("trace-akka-agent-plan-component-client-missing"));
  }
}
