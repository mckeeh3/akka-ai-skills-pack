package ai.first.application.foundation.audit;

import java.util.List;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.FailClosedFoundationRuntime;

/** Test-only fail-closed audit trace port used to verify durable binding diagnostics. */
public final class FailClosedAuditTraceRepository implements AuditTraceRepository {
  @Override
  public List<AuditTraceService.TraceEvent> eventsFor(AuthContextResolver.ResolvedMe actor, String correlationId) {
    throw FailClosedFoundationRuntime.unavailable("AuditTraceRepository");
  }
}
