package ai.first.application.foundation.agent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.first.domain.foundation.agent.AgentRuntimeTrace;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class AgentRuntimeTraceViewTest {
  @Test
  void traceViewProjectsSearchAndDetailFieldsForAuditTraceSurfaces() {
    var trace = AgentRuntimeTraceEntityTest.trace(
        "trace-1",
        "tenant-1",
        "agent-user-admin",
        "corr-1",
        "PromptAssemblyTrace",
        AgentRuntimeTrace.Decision.ALLOWED,
        "prompt assembled with compact manifest only");

    var row = AgentRuntimeTraceView.AgentRuntimeTraceRow.from(trace);

    assertEquals("trace-1", row.traceId());
    assertEquals("tenant-1", row.tenantId());
    assertEquals("agent-user-admin", row.agentDefinitionId());
    assertEquals("corr-1", row.correlationId());
    assertEquals("corr-1", row.workTraceId());
    assertEquals("PromptAssemblyTrace", row.traceType());
    assertEquals("ALLOWED", row.decision());
    assertTrue(row.allowed());
    assertFalse(row.denied());
    assertFalse(row.approvalRequired());
    assertFalse(row.toString().matches("(?is).*(api[_-]?key|secret|token)\\s*[:=].*"));
  }

  @Test
  void traceViewProjectsDeniedAndApprovalRequiredFilteringFlags() {
    var denied = AgentRuntimeTraceEntityTest.trace("trace-denied", "tenant-1", "agent-user-admin", "corr-denied", "ReferenceLoadTrace", AgentRuntimeTrace.Decision.DENIED, "reference-not-available");
    var approval = AgentRuntimeTraceEntityTest.trace("trace-approval", "tenant-1", "agent-user-admin", "corr-approval", "ToolInvocationTrace", AgentRuntimeTrace.Decision.APPROVAL_REQUIRED, "tool grant requires approval");

    var deniedRow = AgentRuntimeTraceView.AgentRuntimeTraceRow.from(denied);
    var approvalRow = AgentRuntimeTraceView.AgentRuntimeTraceRow.from(approval);

    assertTrue(deniedRow.denied());
    assertFalse(deniedRow.allowed());
    assertEquals("DENIED", deniedRow.decision());
    assertTrue(approvalRow.approvalRequired());
    assertEquals("ToolInvocationTrace", approvalRow.traceType());
  }

  @Test
  void traceTimeRangeQueryCarriesTenantAndTimestampBounds() {
    var from = Instant.parse("2026-05-20T00:00:00Z");
    var to = Instant.parse("2026-05-21T00:00:00Z");

    var query = new AgentRuntimeTraceView.TraceTimeRangeQuery("tenant-1", from, to);

    assertEquals("tenant-1", query.tenantId());
    assertEquals(from, query.occurredAtFrom());
    assertEquals(to, query.occurredAtTo());
  }
}
