package {{JAVA_BASE_PACKAGE}}.application.security;

import java.util.List;

/** Fail-closed audit trace port used when no durable evidence source is supplied. */
public final class FailClosedAuditTraceRepository implements AuditTraceRepository {
  @Override
  public List<AuditTraceService.TraceEvent> eventsFor(AuthContextResolver.ResolvedMe actor, String correlationId) {
    throw FailClosedFoundationRuntime.unavailable("AuditTraceRepository");
  }
}
