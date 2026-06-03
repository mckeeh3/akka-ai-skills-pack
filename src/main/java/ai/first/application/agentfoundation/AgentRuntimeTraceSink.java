package ai.first.application.agentfoundation;

import ai.first.domain.agentfoundation.AgentRuntimeTrace;
import java.util.List;

/** Durable/runtime sink boundary for governed agent trace facts. */
public interface AgentRuntimeTraceSink {
  AgentRuntimeTrace record(AgentRuntimeTrace trace);
  List<AgentRuntimeTrace> traces();
}
