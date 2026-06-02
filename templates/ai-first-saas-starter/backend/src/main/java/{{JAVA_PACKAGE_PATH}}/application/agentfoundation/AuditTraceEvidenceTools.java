package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import akka.javasdk.annotations.Description;
import akka.javasdk.annotations.FunctionTool;
import {{JAVA_BASE_PACKAGE}}.application.security.AuditTraceService;
import {{JAVA_BASE_PACKAGE}}.application.security.AuthContextResolver;
import {{JAVA_BASE_PACKAGE}}.application.security.AuthorizationException;
import {{JAVA_BASE_PACKAGE}}.domain.security.Account;
import {{JAVA_BASE_PACKAGE}}.domain.security.AccountStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.AuthContext;
import {{JAVA_BASE_PACKAGE}}.domain.security.Membership;
import {{JAVA_BASE_PACKAGE}}.domain.security.MembershipStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.UserProfile;
import {{JAVA_BASE_PACKAGE}}.domain.security.UserSettings;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Request-scoped read-only Audit/Trace evidence facade for AuditTraceAgent. */
public final class AuditTraceEvidenceTools {
  public static final String TOOL_ID = "auditTraceEvidence.read";
  public static final String CAPABILITY_ID = AuditTraceService.SEARCH_CAPABILITY;

  private final AuditTraceService auditTraceService;
  private final AuthContext authContext;
  private final String correlationId;

  public AuditTraceEvidenceTools(AuditTraceService auditTraceService, AuthContext authContext, String correlationId) {
    this.auditTraceService = Objects.requireNonNull(auditTraceService);
    this.authContext = Objects.requireNonNull(authContext);
    this.correlationId = correlationId == null || correlationId.isBlank() ? "audit-trace-evidence" : correlationId;
  }

  @FunctionTool(description = """
      Read scoped, browser-safe Audit/Trace evidence for the selected AuthContext.
      This is a read-only DATA_LOOKUP tool over deterministic AuditTraceService search/detail/timeline/failure evidence.
      It cannot ingest traces, override authorization, bypass redaction, export evidence, mutate records, start workers, or reveal hidden prompts/secrets.
      It enforces selected tenant/customer scope and audit.trace.search before returning model-visible evidence summaries.
      """)
  public String read(@Description("Optional evidence focus; may include tenantId=<selected tenant>, traceId=<id>, correlationId=<id>, filter=<text>, or failureCategory=<category>") String evidenceRequest) {
    requireCapability(AuditTraceService.SEARCH_CAPABILITY);
    requireRequestedScope(evidenceRequest);
    var actor = syntheticActor();
    var query = new LinkedHashMap<String, Object>();
    query.put("tenantId", authContext.tenantId());
    if (authContext.customerId() != null) query.put("customerId", authContext.customerId());
    query.put("pageSize", 5);
    query.put("filter", valueAfter(evidenceRequest, "filter=").orElse("recent"));
    var search = auditTraceService.search(actor, query, correlationId);
    var timeline = auditTraceService.timeline(actor, Map.of("correlationId", valueAfter(evidenceRequest, "correlationId=").orElse(correlationId)), correlationId);
    var failure = auditTraceService.failureEvidence(actor, Map.of("failureCategory", valueAfter(evidenceRequest, "failureCategory=").orElse("provider_blocked")), correlationId);
    var detail = auditTraceService.detail(actor, Map.of("traceId", valueAfter(evidenceRequest, "traceId=").orElse("trace-auth-context-" + AuditTraceService.stableSuffix(correlationId))), correlationId);
    var traceId = "trace-audittrace-evidence-" + AuditTraceService.stableSuffix(correlationId + ":" + authContext.membershipId());
    return "tool_id=" + TOOL_ID
        + "\ncapability=" + CAPABILITY_ID
        + "\nmode=read_only_no_direct_mutation"
        + "\nselectedTenantId=" + safe(authContext.tenantId())
        + "\nselectedCustomerId=" + safe(authContext.customerId())
        + "\nsearchSurface=" + safe(search.surfaceId()) + ":" + safe(Objects.toString(search.data().get("surfaceContract"), "audit.trace.search.v1"))
        + "\ndetailSurface=" + safe(detail.surfaceId()) + ":" + safe(Objects.toString(detail.data().get("surfaceContract"), "audit.trace.detail.v1"))
        + "\ntimelineSurface=" + safe(timeline.surfaceId()) + ":" + safe(Objects.toString(timeline.data().get("surfaceContract"), "audit.trace.timeline.v1"))
        + "\nfailureEvidenceSurface=" + safe(failure.surfaceId()) + ":" + safe(Objects.toString(failure.data().get("surfaceContract"), "audit.trace.failureEvidence.v1"))
        + "\nevidenceSummaries=" + safe(search.data().get("rows").toString())
        + "\ncorrelationTimeline=" + safe(timeline.data().get("nodes").toString())
        + "\nfailureEvidence=" + safe(failure.data().get("relatedEvents").toString())
        + "\ntraceId=" + traceId
        + "\nredaction=raw prompt bodies, hidden prompt text, provider credentials, JWTs, invitation tokens, raw tool payloads, support-only data, and cross-tenant data omitted"
        + "\nauthority_note=Evidence is scoped deterministic data only; AuditTraceAgent has no direct mutation, ingestion, export, worker, authorization, redaction, or tenant-scope authority.";
  }

  private void requireCapability(String capabilityId) {
    if (!authContext.hasCapability(capabilityId)) {
      throw new AuthorizationException(403, "missing-capability:" + capabilityId);
    }
  }

  private void requireRequestedScope(String evidenceRequest) {
    if (evidenceRequest == null) return;
    var tenantId = valueAfter(evidenceRequest, "tenantId=").orElse(null);
    if (tenantId != null && !tenantId.isBlank() && !tenantId.equals(authContext.tenantId())) {
      throw new AuthorizationException(403, "audit-trace-evidence-tenant-mismatch");
    }
    var customerId = valueAfter(evidenceRequest, "customerId=").orElse(null);
    if (customerId != null && authContext.customerId() != null && !customerId.equals(authContext.customerId())) {
      throw new AuthorizationException(403, "audit-trace-evidence-customer-mismatch");
    }
  }

  private AuthContextResolver.ResolvedMe syntheticActor() {
    var account = new Account(authContext.accountId(), authContext.workosUserId(), authContext.accountId() + "@redacted.local", authContext.accountId() + "@redacted.local", AccountStatus.ACTIVE, "RUNTIME_TOOL_CONTEXT");
    var profile = new UserProfile(authContext.accountId(), account.displayEmail(), "Audit/Trace runtime actor", "Audit", "Trace", null);
    var settings = new UserSettings(authContext.accountId(), UserSettings.ThemeId.AURORA_LIGHT);
    var membership = new Membership(authContext.membershipId(), authContext.accountId(), authContext.scopeType(), authContext.tenantId(), authContext.customerId(), authContext.roles(), MembershipStatus.ACTIVE, false, null);
    return new AuthContextResolver.ResolvedMe(account, profile, settings, List.of(membership), authContext, correlationId);
  }

  private java.util.Optional<String> valueAfter(String text, String marker) {
    if (text == null) return java.util.Optional.empty();
    var index = text.indexOf(marker);
    if (index < 0) return java.util.Optional.empty();
    var value = text.substring(index + marker.length()).split("[\\s,;]")[0].trim();
    return value.isBlank() ? java.util.Optional.empty() : java.util.Optional.of(value);
  }

  private static String safe(String value) {
    if (value == null) return "null";
    return value.replaceAll("(?i)(api[_-]?key|secret|token|credential|bearer)\\s*[:=]?\\s*\\S+", "$1=[REDACTED]");
  }
}
