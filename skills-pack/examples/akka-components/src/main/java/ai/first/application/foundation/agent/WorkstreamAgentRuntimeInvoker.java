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
}
