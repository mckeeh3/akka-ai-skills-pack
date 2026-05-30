package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentRuntimeTrace;
import java.util.List;

/** Fail-closed trace sink used when normal runtime has not yet been bound to Akka durable trace storage. */
public final class FailClosedAgentRuntimeTraceSink implements AgentRuntimeTraceSink {
  @Override
  public AgentRuntimeTrace record(AgentRuntimeTrace trace) {
    throw new IllegalStateException("Agent runtime trace persistence requires AkkaAgentRuntimeTraceSink bound with ComponentClient, or explicit local/demo test mode.");
  }

  @Override
  public List<AgentRuntimeTrace> traces() {
    throw new IllegalStateException("Agent runtime trace reads require durable trace storage or explicit local/demo test mode.");
  }
}
