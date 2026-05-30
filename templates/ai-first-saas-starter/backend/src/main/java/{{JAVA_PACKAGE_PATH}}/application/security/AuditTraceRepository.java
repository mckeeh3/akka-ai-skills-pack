package {{JAVA_BASE_PACKAGE}}.application.security;

import java.util.List;

/** Read-only facade for deterministic, tenant-scoped Audit/Trace evidence sources. */
public interface AuditTraceRepository {
  List<AuditTraceService.TraceEvent> eventsFor(AuthContextResolver.ResolvedMe actor, String correlationId);
}
