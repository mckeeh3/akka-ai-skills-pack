package ai.first.application.foundation.audit;

import ai.first.domain.foundation.identity.AuthContext;
import ai.first.application.foundation.agent.AgentRuntimeService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.workstream.WorkstreamLogRepository;

/**
 * Starter repository facade that normalizes currently available Akka runtime and workstream evidence.
 * Production templates can replace this with view-backed adapters without changing AuditTraceService DTOs.
 */
public final class InMemoryTestAuditTraceRepository implements AuditTraceRepository {
  private final AgentRuntimeService agentRuntimeService;
  private final WorkstreamLogRepository workstreamLogRepository;

  public InMemoryTestAuditTraceRepository(AgentRuntimeService agentRuntimeService, WorkstreamLogRepository workstreamLogRepository) {
    this.agentRuntimeService = Objects.requireNonNull(agentRuntimeService);
    this.workstreamLogRepository = Objects.requireNonNull(workstreamLogRepository);
  }

  @Override
  public List<AuditTraceService.TraceEvent> eventsFor(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var events = new ArrayList<AuditTraceService.TraceEvent>();
    events.add(new AuditTraceService.TraceEvent(
        "trace-auth-context-" + AuditTraceService.stableSuffix(correlationId),
        Instant.now(),
        actor.selectedContext().tenantId(),
        actor.selectedContext().customerId(),
        correlationId,
        "AUTH_CONTEXT_RESOLVE",
        actor.account().accountId(),
        "security",
        "info",
        "allowed",
        "Selected AuthContext resolved; tenant/customer scope applied.",
        "audit.trace.read",
        List.of("rawJwt", "rawProviderCredential", "hiddenPromptText", "rawToolPayload")));

    for (var trace : agentRuntimeService.traces()) {
      if (!actor.selectedContext().tenantId().equals(trace.tenantId())) continue;
      events.add(new AuditTraceService.TraceEvent(
          trace.traceId(),
          trace.occurredAt(),
          trace.tenantId(),
          actor.selectedContext().customerId(),
          trace.correlationId(),
          trace.traceType(),
          trace.actorId(),
          trace.agentDefinitionId(),
          trace.decision().name().equals("ALLOWED") ? "info" : "warning",
          trace.decision().name().toLowerCase(),
          trace.safeSummary(),
          trace.capabilityId(),
          List.of("rawJwt", "rawProviderCredential", "hiddenPromptText", "rawToolPayload")));
    }

    for (var item : workstreamLogRepository.items(actor.selectedContext().tenantId(), actor.selectedContext().membershipId(), null)) {
      events.add(new AuditTraceService.TraceEvent(
          item.traceIds().isEmpty() ? "trace-workstream-item-" + AuditTraceService.stableSuffix(item.itemId()) : item.traceIds().get(0),
          Instant.parse(item.createdAt()),
          actor.selectedContext().tenantId(),
          actor.selectedContext().customerId(),
          item.correlationId(),
          "WORKSTREAM_LOG_ITEM",
          item.functionalAgentId(),
          item.functionalAgentId(),
          item.status().equals("blocked") ? "warning" : "info",
          item.status(),
          item.title() + " · " + item.body(),
          "audit.trace.read",
          List.of("rawJwt", "rawProviderCredential", "hiddenPromptText", "rawToolPayload")));
    }
    return events;
  }
}
