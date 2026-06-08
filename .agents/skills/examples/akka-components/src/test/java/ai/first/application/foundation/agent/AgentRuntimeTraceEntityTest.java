package ai.first.application.foundation.agent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.EventSourcedTestKit;
import ai.first.domain.foundation.agent.AgentRuntimeTrace;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class AgentRuntimeTraceEntityTest {
  private static final Instant NOW = Instant.parse("2026-05-20T00:00:00Z");

  @Test
  void recordsAllowedPromptAssemblyTraceAsImmutableDurableFact() {
    var trace = trace("trace-1", "tenant-1", "agent-user-admin", "corr-1", "PROMPT_ASSEMBLY", AgentRuntimeTrace.Decision.ALLOWED, "assembled PromptAssemblyTrace with compact manifests");
    var testKit = EventSourcedTestKit.of(
        AgentRuntimeTraceEntity.entityId(trace.tenantId(), trace.traceId()),
        AgentRuntimeTraceEntity::new);

    var saved = testKit.method(AgentRuntimeTraceEntity::record).invoke(trace);
    var duplicate = testKit.method(AgentRuntimeTraceEntity::record).invoke(trace);
    var detail = testKit.method(AgentRuntimeTraceEntity::detail)
        .invoke(new AgentRuntimeTraceEntity.TraceDetailQuery("tenant-1", "trace-1"));

    assertEquals(trace, saved.getReply());
    assertEquals(trace, saved.getNextEventOfType(AgentRuntimeTraceEntity.Event.TraceRecorded.class).trace());
    assertEquals(trace, duplicate.getReply());
    assertFalse(duplicate.didPersistEvents());
    assertEquals(trace, detail.getReply().orElseThrow());
  }

  @Test
  void recordsDeniedSkillAndReferenceLoadTracesWithSafeSummaries() {
    var skillTrace = trace("trace-skill", "tenant-1", "agent-user-admin", "corr-denied", "SKILL_LOAD", AgentRuntimeTrace.Decision.DENIED, "skill-not-available");
    var referenceTrace = trace("trace-reference", "tenant-1", "agent-user-admin", "corr-denied", "REFERENCE_LOAD", AgentRuntimeTrace.Decision.DENIED, "reference-not-available");
    var skillKit = EventSourcedTestKit.of(AgentRuntimeTraceEntity.entityId(skillTrace.tenantId(), skillTrace.traceId()), AgentRuntimeTraceEntity::new);
    var referenceKit = EventSourcedTestKit.of(AgentRuntimeTraceEntity.entityId(referenceTrace.tenantId(), referenceTrace.traceId()), AgentRuntimeTraceEntity::new);

    skillKit.method(AgentRuntimeTraceEntity::record).invoke(skillTrace);
    referenceKit.method(AgentRuntimeTraceEntity::record).invoke(referenceTrace);

    assertEquals(AgentRuntimeTrace.Decision.DENIED, skillKit.method(AgentRuntimeTraceEntity::detail)
        .invoke(new AgentRuntimeTraceEntity.TraceDetailQuery("tenant-1", "trace-skill")).getReply().orElseThrow().decision());
    assertEquals("REFERENCE_LOAD", referenceKit.method(AgentRuntimeTraceEntity::detail)
        .invoke(new AgentRuntimeTraceEntity.TraceDetailQuery("tenant-1", "trace-reference")).getReply().orElseThrow().traceType());
  }

  @Test
  void detailLookupIsTenantIsolatedAndTraceRecordIsImmutable() {
    var trace = trace("trace-1", "tenant-1", "agent-user-admin", "corr-1", "MODEL_INVOCATION", AgentRuntimeTrace.Decision.ALLOWED, "ModelInvocationTrace completed");
    var changed = trace("trace-1", "tenant-1", "agent-user-admin", "corr-1", "MODEL_INVOCATION", AgentRuntimeTrace.Decision.DENIED, "changed");
    var testKit = EventSourcedTestKit.of(AgentRuntimeTraceEntity.entityId(trace.tenantId(), trace.traceId()), AgentRuntimeTraceEntity::new);

    testKit.method(AgentRuntimeTraceEntity::record).invoke(trace);
    var wrongTenant = testKit.method(AgentRuntimeTraceEntity::detail)
        .invoke(new AgentRuntimeTraceEntity.TraceDetailQuery("tenant-2", "trace-1"));
    var changedResult = testKit.method(AgentRuntimeTraceEntity::record).invoke(changed);

    assertTrue(wrongTenant.getReply().isEmpty());
    assertTrue(changedResult.isError());
    assertEquals("trace-record-immutable", changedResult.getError());
    assertFalse(changedResult.didPersistEvents());
  }

  @Test
  void rejectsSecretLikeTraceSummariesBeforePersisting() {
    var unsafe = trace("trace-secret", "tenant-1", "agent-user-admin", "corr-secret", "AgentWorkTrace", AgentRuntimeTrace.Decision.DENIED, "api_key=hidden should not persist");
    var testKit = EventSourcedTestKit.of(AgentRuntimeTraceEntity.entityId(unsafe.tenantId(), unsafe.traceId()), AgentRuntimeTraceEntity::new);

    var result = testKit.method(AgentRuntimeTraceEntity::record).invoke(unsafe);

    assertTrue(result.isError());
    assertEquals("trace-secret-boundary-failed", result.getError());
    assertFalse(result.didPersistEvents());
  }

  static AgentRuntimeTrace trace(String traceId, String tenantId, String agentDefinitionId, String correlationId, String traceType, AgentRuntimeTrace.Decision decision, String summary) {
    return new AgentRuntimeTrace(
        traceId,
        NOW,
        tenantId,
        agentDefinitionId,
        correlationId,
        correlationId,
        traceType,
        decision,
        "acct-admin",
        AgentRuntimeService.INVOKE_CAPABILITY,
        agentDefinitionId,
        summary,
        "checksum");
  }
}
