package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import {{JAVA_BASE_PACKAGE}}.application.security.AuthContextResolver;
import {{JAVA_BASE_PACKAGE}}.application.security.LocalDemoIdentityRepository;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentRuntimeTrace;
import {{JAVA_BASE_PACKAGE}}.domain.security.AuthContext;
import {{JAVA_BASE_PACKAGE}}.domain.security.FoundationRole;
import {{JAVA_BASE_PACKAGE}}.domain.security.ScopeType;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class AgentRuntimeTraceSinkTest {
  @Test
  void runtimeServiceWritesPromptAssemblyTracesThroughInjectedDurableSinkBoundary() {
    var repository = new InMemoryAgentBehaviorRepository();
    new AgentBehaviorSeedLoader(repository, fixedClock()).importStarterDefaults("tenant-1", "bootstrap", "corr-seed");
    var sink = new CapturingTraceSink();
    var service = new AgentRuntimeService(
        repository,
        new AuthContextResolver(new LocalDemoIdentityRepository()),
        fixedClock(),
        new OpenAiModelProviderClient(),
        sink);

    var result = service.assemblePrompt(new AgentRuntimeService.PromptAssemblyRequest(
        "tenant-1",
        AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID,
        tenantAdmin(),
        "runtime",
        AgentRuntimeService.INVOKE_CAPABILITY,
        "corr-durable-trace",
        "Summarize user admin capabilities."));

    assertEquals(AgentRuntimeTrace.Decision.ALLOWED, result.decision());
    assertEquals(1, sink.persisted.size());
    assertEquals(sink.persisted, service.traces());
    assertEquals("PROMPT_ASSEMBLY", sink.persisted.get(0).traceType());
    assertEquals("corr-durable-trace", sink.persisted.get(0).correlationId());
    assertFalse(sink.persisted.get(0).safeSummary().matches("(?is).*(api[_-]?key|secret|token)\\s*[:=].*"));
  }

  @Test
  void runtimeServiceWritesDeniedLoaderTracesThroughInjectedDurableSinkBoundary() {
    var repository = new InMemoryAgentBehaviorRepository();
    new AgentBehaviorSeedLoader(repository, fixedClock()).importStarterDefaults("tenant-1", "bootstrap", "corr-seed");
    var sink = new CapturingTraceSink();
    var service = new AgentRuntimeService(
        repository,
        new AuthContextResolver(new LocalDemoIdentityRepository()),
        fixedClock(),
        new OpenAiModelProviderClient(),
        sink);

    var denied = service.readSkill(new AgentRuntimeService.SkillReadRequest(
        "tenant-1",
        AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID,
        tenantAdmin(),
        "runtime",
        AgentRuntimeService.INVOKE_CAPABILITY,
        "corr-denied-skill",
        "not-assigned"));

    assertEquals(AgentRuntimeTrace.Decision.DENIED, denied.decision());
    assertTrue(sink.persisted.stream().anyMatch(trace -> trace.traceType().equals("SKILL_LOAD")
        && trace.decision() == AgentRuntimeTrace.Decision.DENIED
        && trace.correlationId().equals("corr-denied-skill")
        && trace.safeSummary().contains("skill-not-available")));
  }

  private static Clock fixedClock() {
    return Clock.fixed(Instant.parse("2026-05-20T00:00:00Z"), ZoneOffset.UTC);
  }

  private static AuthContext tenantAdmin() {
    return new AuthContext(
        "admin-1",
        "workos-admin-1",
        "membership-1",
        ScopeType.TENANT,
        "tenant-1",
        null,
        List.of(FoundationRole.TENANT_ADMIN),
        List.of("agent.user_admin.use", "agent.behavior.manage", "tenant.user.read", "tenant.audit.read"));
  }

  private static final class CapturingTraceSink implements AgentRuntimeTraceSink {
    private final List<AgentRuntimeTrace> persisted = new ArrayList<>();

    @Override
    public AgentRuntimeTrace record(AgentRuntimeTrace trace) {
      persisted.add(trace);
      return trace;
    }

    @Override
    public List<AgentRuntimeTrace> traces() {
      return List.copyOf(persisted);
    }
  }
}
