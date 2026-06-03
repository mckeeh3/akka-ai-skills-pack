package ai.first.application.agentfoundation;

import akka.javasdk.client.ComponentClient;
import ai.first.domain.agentfoundation.AgentRuntimeTrace;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Akka-backed trace sink that stores every runtime trace fact in AgentRuntimeTraceEntity.
 *
 * <p>The in-process list is a read-through convenience for same-process test/runtime surfaces after
 * the durable write succeeds; it is not the source of truth and can be rebuilt from
 * {@link AgentRuntimeTraceView} projections.
 */
public final class AkkaAgentRuntimeTraceSink implements AgentRuntimeTraceSink {
  private final ComponentClient componentClient;
  private final List<AgentRuntimeTrace> recordedInProcess = new CopyOnWriteArrayList<>();

  public AkkaAgentRuntimeTraceSink(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  @Override
  public AgentRuntimeTrace record(AgentRuntimeTrace trace) {
    var stored = componentClient
        .forEventSourcedEntity(AgentRuntimeTraceEntity.entityId(trace.tenantId(), trace.traceId()))
        .method(AgentRuntimeTraceEntity::record)
        .invoke(trace);
    recordedInProcess.add(stored);
    return stored;
  }

  @Override
  public List<AgentRuntimeTrace> traces() {
    return List.copyOf(recordedInProcess);
  }
}
