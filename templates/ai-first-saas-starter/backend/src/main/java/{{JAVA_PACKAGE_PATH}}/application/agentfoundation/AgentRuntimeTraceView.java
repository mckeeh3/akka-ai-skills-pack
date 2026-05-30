package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Consume;
import akka.javasdk.annotations.Query;
import akka.javasdk.view.TableUpdater;
import akka.javasdk.view.View;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentRuntimeTrace;
import java.time.Instant;
import java.util.List;

/**
 * Event-sourced trace search/detail views for Audit/Trace and Agent Admin investigation surfaces.
 *
 * <p>Rows remain tenant-scoped and safe for browser rendering. Protected endpoints must authorize
 * the caller before invoking these scoped queries and before exposing sensitive linked records.
 */
@Component(id = "agent-runtime-trace-view")
public class AgentRuntimeTraceView extends View {
  public record AgentRuntimeTraceRow(
      String traceId,
      Instant occurredAt,
      String tenantId,
      String agentDefinitionId,
      String correlationId,
      String workTraceId,
      String traceType,
      String decision,
      String actorId,
      String capabilityId,
      String targetId,
      String safeSummary,
      String checksum,
      boolean denied,
      boolean allowed,
      boolean approvalRequired) {
    static AgentRuntimeTraceRow from(AgentRuntimeTrace trace) {
      return new AgentRuntimeTraceRow(
          trace.traceId(),
          trace.occurredAt(),
          trace.tenantId(),
          trace.agentDefinitionId(),
          trace.correlationId(),
          trace.workTraceId(),
          trace.traceType(),
          trace.decision().name(),
          trace.actorId(),
          trace.capabilityId(),
          trace.targetId(),
          trace.safeSummary(),
          trace.checksum() == null ? "" : trace.checksum(),
          trace.decision() == AgentRuntimeTrace.Decision.DENIED,
          trace.decision() == AgentRuntimeTrace.Decision.ALLOWED,
          trace.decision() == AgentRuntimeTrace.Decision.APPROVAL_REQUIRED);
    }
  }

  public record TraceDetailQuery(String tenantId, String traceId) {}
  public record TenantTraceSearchQuery(String tenantId) {}
  public record AgentTraceSearchQuery(String tenantId, String agentDefinitionId) {}
  public record CorrelationTraceSearchQuery(String tenantId, String correlationId) {}
  public record WorkTraceSearchQuery(String tenantId, String workTraceId) {}
  public record TraceTypeSearchQuery(String tenantId, String traceType) {}
  public record DecisionTraceSearchQuery(String tenantId, String decision) {}
  public record TraceTimeRangeQuery(String tenantId, Instant occurredAtFrom, Instant occurredAtTo) {}
  public record TraceRows(List<AgentRuntimeTraceRow> traces) {}

  @Consume.FromEventSourcedEntity(AgentRuntimeTraceEntity.class)
  public static class AgentRuntimeTraceUpdater extends TableUpdater<AgentRuntimeTraceRow> {
    public Effect<AgentRuntimeTraceRow> onEvent(AgentRuntimeTraceEntity.Event event) {
      return effects().updateRow(AgentRuntimeTraceRow.from(event.trace()));
    }
  }

  @Query(
      """
      SELECT *
      FROM agent_runtime_trace_view
      WHERE tenantId = :tenantId AND traceId = :traceId
      """)
  public QueryEffect<AgentRuntimeTraceRow> getDetail(TraceDetailQuery query) {
    return queryResult();
  }

  @Query(
      """
      SELECT * AS traces
      FROM agent_runtime_trace_view
      WHERE tenantId = :tenantId
      """)
  public QueryEffect<TraceRows> byTenant(TenantTraceSearchQuery query) {
    return queryResult();
  }

  @Query(
      """
      SELECT * AS traces
      FROM agent_runtime_trace_view
      WHERE tenantId = :tenantId AND agentDefinitionId = :agentDefinitionId
      """)
  public QueryEffect<TraceRows> byAgent(AgentTraceSearchQuery query) {
    return queryResult();
  }

  @Query(
      """
      SELECT * AS traces
      FROM agent_runtime_trace_view
      WHERE tenantId = :tenantId AND correlationId = :correlationId
      """)
  public QueryEffect<TraceRows> byCorrelation(CorrelationTraceSearchQuery query) {
    return queryResult();
  }

  @Query(
      """
      SELECT * AS traces
      FROM agent_runtime_trace_view
      WHERE tenantId = :tenantId AND workTraceId = :workTraceId
      """)
  public QueryEffect<TraceRows> byWorkTrace(WorkTraceSearchQuery query) {
    return queryResult();
  }

  @Query(
      """
      SELECT * AS traces
      FROM agent_runtime_trace_view
      WHERE tenantId = :tenantId AND traceType = :traceType
      """)
  public QueryEffect<TraceRows> byTraceType(TraceTypeSearchQuery query) {
    return queryResult();
  }

  @Query(
      """
      SELECT * AS traces
      FROM agent_runtime_trace_view
      WHERE tenantId = :tenantId AND decision = :decision
      """)
  public QueryEffect<TraceRows> byDecision(DecisionTraceSearchQuery query) {
    return queryResult();
  }

  @Query(
      """
      SELECT * AS traces
      FROM agent_runtime_trace_view
      WHERE tenantId = :tenantId AND occurredAt >= :occurredAtFrom AND occurredAt <= :occurredAtTo
      """)
  public QueryEffect<TraceRows> byTimestampRange(TraceTimeRangeQuery query) {
    return queryResult();
  }
}
