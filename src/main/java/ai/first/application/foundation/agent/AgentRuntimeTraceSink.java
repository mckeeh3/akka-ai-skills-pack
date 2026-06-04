package ai.first.application.foundation.agent;

import ai.first.domain.foundation.agent.AgentRuntimeTrace;
import java.util.List;

/** Durable/runtime sink boundary for governed agent trace facts. */
public interface AgentRuntimeTraceSink {
  AgentRuntimeTrace record(AgentRuntimeTrace trace);
  List<AgentRuntimeTrace> traces();
}
