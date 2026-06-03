package ai.first.application.security;

import java.util.List;

/** Test-only fail-closed audit trace port used to verify durable binding diagnostics. */
final class FailClosedAuditTraceRepository implements AuditTraceRepository {
  @Override
  public List<AuditTraceService.TraceEvent> eventsFor(AuthContextResolver.ResolvedMe actor, String correlationId) {
    throw FailClosedFoundationRuntime.unavailable("AuditTraceRepository");
  }
}
