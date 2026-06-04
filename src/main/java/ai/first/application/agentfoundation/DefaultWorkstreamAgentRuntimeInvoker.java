package ai.first.application.agentfoundation;

import akka.javasdk.client.ComponentClient;
import ai.first.domain.foundation.agent.AgentRuntimeTrace;
import java.util.Objects;

/** Default production invoker that calls the Akka Agent component after governed runtime preparation. */
public final class DefaultWorkstreamAgentRuntimeInvoker implements WorkstreamAgentRuntimeInvoker {
  private final AgentRuntimeService agentRuntimeService;
  private final ComponentClient componentClient;

  public DefaultWorkstreamAgentRuntimeInvoker(AgentRuntimeService agentRuntimeService, ComponentClient componentClient) {
    this.agentRuntimeService = Objects.requireNonNull(agentRuntimeService);
    this.componentClient = Objects.requireNonNull(componentClient);
  }

  @Override
  public AgentRuntimeService.RuntimeInvocationResult invokeWorkstreamAgent(AgentRuntimeService.RuntimeInvocationRequest request) {
    var preparation = agentRuntimeService.prepareWorkstreamAgentInvocation(request);
    if (preparation.decision() != AgentRuntimeTrace.Decision.ALLOWED) {
      return new AgentRuntimeService.RuntimeInvocationResult(preparation.decision(), null, preparation.traceIds(), preparation.safeErrorCode(), preparation.safeErrorSummary());
    }
    try {
      var response = componentClient
          .forAgent()
          .inSession("workstream-" + request.authContext().membershipId() + "-" + request.agentDefinitionId())
          .method(WorkstreamRuntimeAgent::respond)
          .invoke(preparation.governedRequest());
      return agentRuntimeService.completeWorkstreamAgentInvocation(request, preparation, response);
    } catch (RuntimeException failure) {
      return agentRuntimeService.failWorkstreamAgentInvocation(request, preparation, failure);
    }
  }
}
