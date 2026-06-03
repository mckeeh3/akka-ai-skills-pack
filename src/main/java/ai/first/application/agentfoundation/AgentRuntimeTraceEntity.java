package ai.first.application.agentfoundation;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.TypeName;
import akka.javasdk.eventsourcedentity.EventSourcedEntity;
import ai.first.domain.agentfoundation.AgentRuntimeTrace;
import java.util.Optional;

/**
 * Append-only durable Event Sourced Entity for governed agent runtime trace facts.
 *
 * <p>Each entity id is one trace id so prompt assembly, skill/reference loads, tool invocation,
 * model invocation, and agent work trace facts remain immutable, tenant-scoped, and projectable for
 * Audit/Trace and Agent Admin surfaces. Producers store browser-safe summaries and checksums, not
 * raw prompts, provider secrets, JWTs, API keys, or hidden tenant data.
 */
@Component(id = "agent-runtime-trace")
public class AgentRuntimeTraceEntity extends EventSourcedEntity<AgentRuntimeTraceEntity.State, AgentRuntimeTraceEntity.Event> {
  public static final String PROMPT_ASSEMBLY_TRACE = "PromptAssemblyTrace";
  public static final String SKILL_LOAD_TRACE = "SkillLoadTrace";
  public static final String REFERENCE_LOAD_TRACE = "ReferenceLoadTrace";
  public static final String TOOL_INVOCATION_TRACE = "ToolInvocationTrace";
  public static final String MODEL_INVOCATION_TRACE = "ModelInvocationTrace";
  public static final String AGENT_WORK_TRACE = "AgentWorkTrace";

  public static String entityId(String tenantId, String traceId) {
    return tenantId + "__" + traceId;
  }

  @Override
  public State emptyState() {
    return State.empty();
  }

  public ReadOnlyEffect<Optional<AgentRuntimeTrace>> detail(TraceDetailQuery query) {
    return effects().reply(currentState().traceForTenant(query.tenantId()));
  }

  /** Compatibility write path for runtime trace sinks. Trace records are immutable/idempotent. */
  public Effect<AgentRuntimeTrace> record(AgentRuntimeTrace trace) {
    var validation = validate(trace);
    if (validation.isPresent()) {
      return effects().error(validation.get());
    }
    if (currentState().trace() != null) {
      if (currentState().trace().equals(trace)) {
        return effects().reply(trace);
      }
      return effects().error("trace-record-immutable");
    }
    return effects()
        .persist(new Event.TraceRecorded(trace))
        .thenReply(State::trace);
  }

  @Override
  public State applyEvent(Event event) {
    return switch (event) {
      case Event.TraceRecorded recorded -> new State(recorded.trace());
    };
  }

  private Optional<String> validate(AgentRuntimeTrace trace) {
    if (trace == null) {
      return Optional.of("trace-required");
    }
    if (blank(trace.traceId())) {
      return Optional.of("trace-id-required");
    }
    if (blank(trace.tenantId())) {
      return Optional.of("tenant-required");
    }
    if (blank(trace.agentDefinitionId())) {
      return Optional.of("agent-definition-id-required");
    }
    if (blank(trace.correlationId())) {
      return Optional.of("correlation-id-required");
    }
    if (blank(trace.traceType())) {
      return Optional.of("trace-type-required");
    }
    if (trace.decision() == null) {
      return Optional.of("trace-decision-required");
    }
    if (containsSecretLikeText(trace.safeSummary()) || containsSecretLikeText(trace.targetId())) {
      return Optional.of("trace-secret-boundary-failed");
    }
    return Optional.empty();
  }

  private static boolean blank(String value) {
    return value == null || value.isBlank();
  }

  private static boolean containsSecretLikeText(String text) {
    return text != null && text.matches("(?is).*(api[_-]?key|secret|token)\\s*[:=].*");
  }

  public record State(AgentRuntimeTrace trace) {
    static State empty() {
      return new State(null);
    }

    Optional<AgentRuntimeTrace> traceForTenant(String tenantId) {
      return Optional.ofNullable(trace).filter(candidate -> candidate.tenantId().equals(tenantId));
    }
  }

  public sealed interface Event {
    AgentRuntimeTrace trace();

    @TypeName("agent-runtime-trace-recorded")
    record TraceRecorded(AgentRuntimeTrace trace) implements Event {}
  }

  public record TraceDetailQuery(String tenantId, String traceId) {}
}
