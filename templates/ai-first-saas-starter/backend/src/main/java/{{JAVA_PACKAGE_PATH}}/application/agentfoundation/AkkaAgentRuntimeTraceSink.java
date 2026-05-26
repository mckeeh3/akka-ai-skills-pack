package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import akka.javasdk.client.ComponentClient;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentRuntimeTrace;
import java.util.List;

/** Akka-backed trace sink that stores every runtime trace fact in AgentRuntimeTraceEntity. */
public final class AkkaAgentRuntimeTraceSink implements AgentRuntimeTraceSink {
  private final ComponentClient componentClient;

  public AkkaAgentRuntimeTraceSink(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  @Override
  public AgentRuntimeTrace record(AgentRuntimeTrace trace) {
    return componentClient
        .forEventSourcedEntity(AgentRuntimeTraceEntity.entityId(trace.tenantId(), trace.traceId()))
        .method(AgentRuntimeTraceEntity::record)
        .invoke(trace);
  }

  @Override
  public List<AgentRuntimeTrace> traces() {
    return List.of();
  }
}
