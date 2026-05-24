package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import java.util.Objects;

/** Default production invoker seam for the governed workstream Agent runtime path. */
public final class DefaultWorkstreamAgentRuntimeInvoker implements WorkstreamAgentRuntimeInvoker {
  private final AgentRuntimeService agentRuntimeService;

  public DefaultWorkstreamAgentRuntimeInvoker(AgentRuntimeService agentRuntimeService) {
    this.agentRuntimeService = Objects.requireNonNull(agentRuntimeService);
  }

  @Override
  public AgentRuntimeService.RuntimeInvocationResult invokeWorkstreamAgent(AgentRuntimeService.RuntimeInvocationRequest request) {
    return agentRuntimeService.invokeWorkstreamAgent(request);
  }
}
