package ai.first.application.foundation.agent;

import ai.first.domain.foundation.agent.AgentRuntimeTrace;
import java.util.ArrayList;
import java.util.List;

/** Test/local sink for trace assertions; generated production wiring should use AkkaAgentRuntimeTraceSink. */
public final class LocalDemoAgentRuntimeTraceSink implements AgentRuntimeTraceSink {
  private final List<AgentRuntimeTrace> traces = new ArrayList<>();

  @Override
  public AgentRuntimeTrace record(AgentRuntimeTrace trace) {
    traces.add(trace);
    return trace;
  }

  @Override
  public List<AgentRuntimeTrace> traces() {
    return List.copyOf(traces);
  }
}
