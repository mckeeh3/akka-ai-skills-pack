package {{JAVA_BASE_PACKAGE}}.application.security;

import akka.javasdk.client.ComponentClient;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AgentRuntimeTraceView;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Akka-backed Audit/Trace repository that composes durable trace views and workstream log state. */
public final class AkkaAuditTraceRepository implements AuditTraceRepository {
  private static final List<String> OMITTED_FIELDS = List.of("rawJwt", "rawProviderCredential", "hiddenPromptText", "rawToolPayload");

  private final ComponentClient componentClient;
  private final WorkstreamLogRepository workstreamLogRepository;

  public AkkaAuditTraceRepository(ComponentClient componentClient, WorkstreamLogRepository workstreamLogRepository) {
    this.componentClient = Objects.requireNonNull(componentClient);
    this.workstreamLogRepository = Objects.requireNonNull(workstreamLogRepository);
  }

  @Override
  public List<AuditTraceService.TraceEvent> eventsFor(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var events = new ArrayList<AuditTraceService.TraceEvent>();
    events.add(authContextEvent(actor, correlationId));
    events.addAll(agentRuntimeTraceEvents(actor));
    events.addAll(workstreamLogEvents(actor));
    return events;
  }

  private AuditTraceService.TraceEvent authContextEvent(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return new AuditTraceService.TraceEvent(
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
        AuditTraceService.READ_CAPABILITY,
        OMITTED_FIELDS);
  }

  private List<AuditTraceService.TraceEvent> agentRuntimeTraceEvents(AuthContextResolver.ResolvedMe actor) {
    return componentClient
        .forView()
        .method(AgentRuntimeTraceView::byTenant)
        .invoke(new AgentRuntimeTraceView.TenantTraceSearchQuery(actor.selectedContext().tenantId()))
        .traces()
        .stream()
        .map(row -> new AuditTraceService.TraceEvent(
            row.traceId(),
            row.occurredAt(),
            row.tenantId(),
            actor.selectedContext().customerId(),
            row.correlationId(),
            row.traceType(),
            row.actorId(),
            row.agentDefinitionId(),
            row.denied() ? "warning" : "info",
            row.decision().toLowerCase(),
            row.safeSummary(),
            row.capabilityId(),
            OMITTED_FIELDS))
        .toList();
  }

  private List<AuditTraceService.TraceEvent> workstreamLogEvents(AuthContextResolver.ResolvedMe actor) {
    return workstreamLogRepository
        .items(actor.selectedContext().tenantId(), actor.selectedContext().membershipId(), null)
        .stream()
        .map(item -> new AuditTraceService.TraceEvent(
            item.traceIds().isEmpty() ? "trace-workstream-item-" + AuditTraceService.stableSuffix(item.itemId()) : item.traceIds().get(0),
            Instant.parse(item.createdAt()),
            actor.selectedContext().tenantId(),
            actor.selectedContext().customerId(),
            item.correlationId(),
            "WORKSTREAM_LOG_ITEM",
            item.functionalAgentId(),
            item.functionalAgentId(),
            "blocked".equals(item.status()) ? "warning" : "info",
            item.status(),
            item.title() + " · " + item.body(),
            AuditTraceService.READ_CAPABILITY,
            OMITTED_FIELDS))
        .toList();
  }
}
