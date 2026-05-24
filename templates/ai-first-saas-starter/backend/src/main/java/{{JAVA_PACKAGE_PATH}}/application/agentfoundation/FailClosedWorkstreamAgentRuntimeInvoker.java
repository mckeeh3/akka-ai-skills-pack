package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentRuntimeTrace;
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
}
