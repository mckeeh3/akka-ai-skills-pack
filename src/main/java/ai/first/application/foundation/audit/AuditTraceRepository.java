package ai.first.application.foundation.audit;

import java.util.List;
import ai.first.application.foundation.identity.AuthContextResolver;

/** Read-only facade for deterministic, tenant-scoped Audit/Trace evidence sources. */
public interface AuditTraceRepository {
  List<AuditTraceService.TraceEvent> eventsFor(AuthContextResolver.ResolvedMe actor, String correlationId);
}
