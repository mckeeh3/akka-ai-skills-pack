package ai.first.application.foundation.agent;

/**
 * Production seam for workstream message execution.
 *
 * <p>Normal browser/API workstream responses must pass through this invoker so the implementation
 * can call the Akka Agent component instead of bypassing agent execution with direct provider or
 * canned markdown paths. Unit tests may provide explicitly named test adapters.
 */
@FunctionalInterface
public interface WorkstreamAgentRuntimeInvoker {
  AgentRuntimeService.RuntimeInvocationResult invokeWorkstreamAgent(AgentRuntimeService.RuntimeInvocationRequest request);

  default AgentRuntimeService.PlanProposalInvocationResult proposeChatToolPlan(AgentRuntimeService.PlanProposalInvocationRequest request) {
    return AgentRuntimeService.planProposalUnavailable(
        request,
        "CHAT_TOOL_PLAN_RUNTIME_NOT_IMPLEMENTED",
        "Workstream chat tool plan proposal requires the governed Akka Agent runtime path.",
        java.util.List.of("trace-chat-tool-plan-runtime-not-implemented"));
  }
}
