package {{JAVA_BASE_PACKAGE}}.application.security;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Deterministic Audit/Trace read service for tenant-scoped, redacted investigation surfaces. */
public final class AuditTraceService {
  public static final String READ_CAPABILITY = "audit.trace.read";
  public static final String DASHBOARD_CAPABILITY = "audit.trace.dashboard.read";
  public static final String SEARCH_CAPABILITY = "audit.trace.search";
  public static final String DETAIL_CAPABILITY = "audit.trace.detail.read";
  public static final String TIMELINE_CAPABILITY = "audit.trace.timeline.read";
  public static final String FAILURE_EVIDENCE_CAPABILITY = "audit.trace.failureEvidence.read";
  public static final String INVESTIGATION_GUIDE_CAPABILITY = "audit.trace.investigationGuide.read";
  public static final String INVESTIGATION_NOTE_CAPABILITY = "audit.trace.investigation_note.append";
  private static final List<String> DEFAULT_OMITTED_FIELDS = List.of("rawJwt", "rawProviderCredential", "hiddenPromptText", "rawToolPayload", "invitationToken", "providerCredentialValue");

  private final AuthContextResolver authContextResolver;
  private final AuditTraceRepository repository;

  public AuditTraceService(AuthContextResolver authContextResolver, AuditTraceRepository repository) {
    this.authContextResolver = Objects.requireNonNull(authContextResolver);
    this.repository = Objects.requireNonNull(repository);
  }

  public SurfaceData dashboard(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.appendProtectedReadTrace(actor, DASHBOARD_CAPABILITY, "dashboard scoped to selected AuthContext", correlationId);
    var events = sortedEvents(actor, correlationId);
    var warningCount = events.stream().filter(event -> "warning".equals(event.severity())).count();
    var providerCount = events.stream().filter(event -> containsIgnoreCase(event.summary(), "provider") || containsIgnoreCase(event.eventKind(), "provider")).count();
    return new SurfaceData("surface-audit-trace-dashboard", "dashboard", "Audit/Trace dashboard", List.of("trace-audit-dashboard-" + stableSuffix(correlationId)), mapOf(
        "surfaceContract", "audit.trace.dashboard.v1",
        "cards", List.of(
            mapOf("cardId", "card-runtime-traces", "label", "Runtime traces", "value", events.size(), "severity", events.isEmpty() ? "info" : "warning"),
            mapOf("cardId", "card-provider-tool-model", "label", "Provider/tool/model failures", "value", providerCount, "severity", providerCount == 0 ? "info" : "warning"),
            mapOf("cardId", "card-selected-context", "label", "Selected context", "value", actor.selectedContext().membershipId(), "severity", "info"),
            mapOf("cardId", "card-redaction", "label", "Redaction", "value", "browser-safe", "severity", "info")),
        "attentionItems", List.of(mapOf("itemId", "warnings", "label", "Warnings and denials", "status", warningCount == 0 ? "clear" : "needs_review")),
        "readiness", "Trace search, details, timeline, failure evidence, and guidance are backend-scoped and redacted for the selected AuthContext.",
        "capabilityIds", List.of(DASHBOARD_CAPABILITY, SEARCH_CAPABILITY, DETAIL_CAPABILITY, TIMELINE_CAPABILITY, FAILURE_EVIDENCE_CAPABILITY, INVESTIGATION_GUIDE_CAPABILITY, INVESTIGATION_NOTE_CAPABILITY, AuditTraceSummaryService.START_CAPABILITY, AuditTraceSummaryService.READ_CAPABILITY),
        "redaction", "redacted browser-safe evidence; provider credentials, raw tokens, hidden prompts, and raw tool payloads omitted"));
  }

  public SurfaceData search(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    var scope = validateScope(actor, input, correlationId);
    var pageSize = intInput(input, "pageSize", 10);
    if (pageSize < 1 || pageSize > 50) return validation(actor, correlationId, "pageSize", "Page size must be between 1 and 50.");
    authContextResolver.appendProtectedReadTrace(actor, SEARCH_CAPABILITY, "search page-size-band:" + (pageSize <= 10 ? "small" : "bounded"), correlationId);
    var filter = stringInput(input, "filter", "recent");
    var rows = sortedEvents(actor, correlationId).stream()
        .filter(event -> matchesFilter(event, filter))
        .map(this::row)
        .limit(pageSize)
        .toList();
    return new SurfaceData("surface-audit-trace-search", "list-search", "Trace search results", List.of("trace-audit-search-" + stableSuffix(correlationId)), mapOf(
        "surfaceContract", "audit.trace.search.v1",
        "query", mapOf("tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId(), "pageSize", pageSize, "filter", filter, "scope", scope),
        "rows", rows,
        "pageInfo", mapOf("totalKnownCount", rows.size(), "nextCursor", null),
        "partial", false,
        "redaction", "safe summaries only; raw payloads, tokens, provider secrets, invitation tokens, and hidden prompts omitted"));
  }

  public SurfaceData detail(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    validateScope(actor, input, correlationId);
    var traceId = stringInput(input, "traceId", "trace-auth-context-" + stableSuffix(correlationId));
    if (traceId == null || traceId.isBlank() || traceId.length() > 160) return validation(actor, correlationId, "traceId", "Trace id is required and must be at most 160 characters.");
    authContextResolver.appendProtectedReadTrace(actor, DETAIL_CAPABILITY, "detail trace:" + traceId, correlationId);
    var matched = sortedEvents(actor, correlationId).stream().filter(event -> traceId.equals(event.traceId())).findFirst();
    var payload = matched.<Map<String, Object>>map(event -> mapOf(
        "surfaceContract", "audit.trace.detail.v1",
        "traceId", event.traceId(),
        "eventKind", event.eventKind(),
        "timestamp", event.occurredAt().toString(),
        "actor", event.actor(),
        "source", event.workstream(),
        "correlationIds", List.of(event.correlationId()),
        "authorizationBasis", event.capabilityId(),
        "decision", event.status(),
        "redactedEvidence", redacted(event.summary()),
        "redactionMetadata", mapOf("omittedFieldKeys", DEFAULT_OMITTED_FIELDS, "nonEnumerating", false)))
        .orElseGet(() -> notFound(traceId));
    return new SurfaceData("surface-audit-trace-detail", "detail-edit", "Trace detail/evidence", List.of("trace-audit-detail-" + stableSuffix(correlationId)), payload);
  }

  public SurfaceData timeline(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    validateScope(actor, input, correlationId);
    var requestedCorrelation = stringInput(input, "correlationId", correlationId);
    if (requestedCorrelation == null || requestedCorrelation.isBlank() || requestedCorrelation.length() > 128) return validation(actor, correlationId, "correlationId", "Correlation id is required and must be at most 128 characters.");
    authContextResolver.appendProtectedReadTrace(actor, TIMELINE_CAPABILITY, "timeline correlation:" + requestedCorrelation, correlationId);
    var nodes = new ArrayList<Map<String, Object>>();
    nodes.add(mapOf("nodeId", "auth-context", "sourceType", "policy", "summary", "Selected AuthContext resolved and tenant/customer scope applied.", "correlationId", requestedCorrelation, "status", "allowed", "redaction", "tenant-scoped"));
    sortedEvents(actor, correlationId).stream()
        .filter(event -> requestedCorrelation.equals(event.correlationId()))
        .forEach(event -> nodes.add(mapOf("nodeId", event.traceId(), "sourceType", sourceType(event), "summary", redacted(event.summary()), "correlationId", event.correlationId(), "status", event.status(), "traceId", event.traceId())));
    return new SurfaceData("surface-audit-trace-timeline", "audit-timeline", "Correlation timeline", List.of("trace-audit-timeline-" + stableSuffix(correlationId)), mapOf(
        "surfaceContract", "audit.trace.timeline.v1",
        "correlationId", requestedCorrelation,
        "nodes", nodes,
        "partial", false,
        "omittedCategories", List.of(),
        "redactionSummary", "Unauthorized tenant/customer evidence is omitted; not_found_or_redacted is used for hidden traces."));
  }

  public SurfaceData failureEvidence(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    validateScope(actor, input, correlationId);
    var category = stringInput(input, "failureCategory", "provider_blocked");
    authContextResolver.appendProtectedReadTrace(actor, FAILURE_EVIDENCE_CAPABILITY, "failure category:" + category, correlationId);
    var related = sortedEvents(actor, correlationId).stream()
        .filter(event -> containsIgnoreCase(event.summary(), category) || containsIgnoreCase(event.eventKind(), category) || containsIgnoreCase(event.status(), "denied") || containsIgnoreCase(event.summary(), "provider") || containsIgnoreCase(event.summary(), "tool") || containsIgnoreCase(event.summary(), "model"))
        .limit(5)
        .map(event -> mapOf("traceId", event.traceId(), "eventKind", event.eventKind(), "summary", redacted(event.summary()), "correlationId", event.correlationId(), "status", event.status()))
        .toList();
    return new SurfaceData("surface-audit-trace-failure-evidence", "detail-edit", "Denial/provider/tool evidence", List.of("trace-audit-failure-" + stableSuffix(correlationId)), mapOf(
        "surfaceContract", "audit.trace.failureEvidence.v1",
        "category", category,
        "safeReason", "Provider, tool, model, worker, policy, and authorization failures are shown as redacted browser-safe evidence only.",
        "userActionableNextSteps", List.of("Check selected AuthContext and required capability.", "Open correlation timeline.", "Ask Audit/Trace for an explanation after provider configuration is available."),
        "policyRefs", List.of(READ_CAPABILITY, FAILURE_EVIDENCE_CAPABILITY),
        "relatedEvents", related,
        "redactedDetails", mapOf("providerSecret", "[REDACTED]", "rawPrompt", "[OMITTED]", "rawToolPayload", "[OMITTED]"),
        "traceLinks", List.of(correlationId)));
  }

  public SurfaceData investigationGuide(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    validateScope(actor, input, correlationId);
    authContextResolver.appendProtectedReadTrace(actor, INVESTIGATION_GUIDE_CAPABILITY, "investigation guidance", correlationId);
    return new SurfaceData("surface-audit-trace-investigation-guide", "decision", "Investigation guidance", List.of("trace-audit-guide-" + stableSuffix(correlationId)), mapOf(
        "surfaceContract", "audit.trace.investigationGuide.v1",
        "recommendation", "Continue only with backend-authorized, tenant-scoped evidence.",
        "allowedActions", List.of(mapOf("actionId", "action-audit-trace-search", "label", "Refine search", "browserToolId", "action-audit-trace-search", "governedToolId", SEARCH_CAPABILITY, "capabilityId", SEARCH_CAPABILITY), mapOf("actionId", "action-audit-trace-timeline", "label", "Open timeline", "browserToolId", "action-audit-trace-timeline", "governedToolId", TIMELINE_CAPABILITY, "capabilityId", TIMELINE_CAPABILITY), mapOf("actionId", "action-audit-trace-summary-task-start", "label", "Start bounded audit summary task", "browserToolId", "action-audit-trace-summary-task-start", "governedToolId", "audit.trace.summaryTask.start", "capabilityId", AuditTraceSummaryService.START_CAPABILITY, "resultSurfaceId", "surface-audit-trace-summary-progress")),
        "disabledActions", List.of(mapOf("actionId", "action-audit-trace-summary-task-start-scheduled", "capabilityId", AuditTraceSummaryService.START_CAPABILITY, "reason", "Scheduled audit summary cadence remains future work; manual backend-governed start is wired.")),
        "risk", "low",
        "traceLinks", List.of(correlationId),
        "redaction", "no secrets, hidden prompts, raw payloads, or cross-tenant evidence"));
  }

  public SurfaceData appendInvestigationNote(AuthContextResolver.ResolvedMe actor, Object input, String idempotencyKey, String correlationId) {
    validateScope(actor, input, correlationId);
    var note = stringInput(input, "note", "Investigation note recorded without sensitive evidence.");
    if (note == null || note.isBlank() || note.length() > 500) return validation(actor, correlationId, "note", "Investigation note is required and must be at most 500 characters.");
    var traceId = stringInput(input, "traceId", "trace-auth-context-" + stableSuffix(correlationId));
    authContextResolver.appendProtectedReadTrace(actor, INVESTIGATION_NOTE_CAPABILITY, "append investigation note trace:" + traceId, correlationId);
    return new SurfaceData("surface-audit-trace-investigation-note", "system-message", "Investigation note recorded", List.of("trace-audit-note-" + stableSuffix(Objects.toString(idempotencyKey, correlationId))), mapOf(
        "surfaceContract", "audit.trace.investigationNote.v1",
        "status", "recorded",
        "traceId", traceId,
        "noteSummary", redacted(note),
        "idempotencyKey", idempotencyKey,
        "retainedAuthority", "Human-authored investigation notes annotate traces only; they do not mutate source traces, policy, authorization, or retained evidence.",
        "redactionMetadata", mapOf("omittedFieldKeys", DEFAULT_OMITTED_FIELDS, "nonEnumerating", false)));
  }

  private SurfaceData validation(AuthContextResolver.ResolvedMe actor, String correlationId, String field, String message) {
    authContextResolver.appendDeniedTrace(actor, "AUDIT_TRACE_VALIDATION", field + ":" + message, correlationId);
    return new SurfaceData("surface-audit-trace-validation-error", "validation-error", "Audit/Trace validation", List.of("trace-audit-validation-" + stableSuffix(correlationId)), mapOf("field", field, "message", message, "status", "validation-error", "safe", true, "redaction", "browser-safe validation only"));
  }

  private Map<String, Object> validateScope(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    if (input instanceof Map<?, ?> map) {
      var tenantId = map.get("tenantId") instanceof String value ? value : null;
      var customerId = map.get("customerId") instanceof String value ? value : null;
      if (tenantId != null && !tenantId.isBlank() && !tenantId.equals(actor.selectedContext().tenantId())) {
        authContextResolver.appendDeniedTrace(actor, "AUDIT_TRACE_SCOPE_DENIED", "tenant-mismatch", correlationId);
        throw new AuthorizationException(403, "AUDIT_TRACE_TENANT_FORBIDDEN");
      }
      if (customerId != null && actor.selectedContext().customerId() != null && !customerId.equals(actor.selectedContext().customerId())) {
        authContextResolver.appendDeniedTrace(actor, "AUDIT_TRACE_SCOPE_DENIED", "customer-mismatch", correlationId);
        throw new AuthorizationException(403, "AUDIT_TRACE_CUSTOMER_FORBIDDEN");
      }
    }
    return mapOf("tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId(), "nonEnumeratingHiddenEvidence", true);
  }

  private List<TraceEvent> sortedEvents(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return repository.eventsFor(actor, correlationId).stream()
        .sorted(Comparator.comparing(TraceEvent::occurredAt).reversed())
        .toList();
  }

  private Map<String, Object> row(TraceEvent event) {
    return mapOf("traceId", event.traceId(), "correlationId", event.correlationId(), "eventKind", event.eventKind(), "actor", event.actor(), "workstream", event.workstream(), "severity", event.severity(), "status", event.status(), "redactionSummary", "redacted safe summary only", "summary", redacted(event.summary()));
  }

  private Map<String, Object> notFound(String traceId) {
    return mapOf("surfaceContract", "audit.trace.detail.v1", "traceId", traceId, "eventKind", "synthetic-safe-not-found", "decision", "not_found_or_redacted", "redactedEvidence", "No authorized matching trace was found for the selected context.", "redactionMetadata", mapOf("nonEnumerating", true, "omittedFieldKeys", DEFAULT_OMITTED_FIELDS));
  }

  private boolean matchesFilter(TraceEvent event, String filter) {
    if (filter == null || filter.isBlank() || "recent".equalsIgnoreCase(filter)) return true;
    var normalized = filter.toLowerCase();
    return containsIgnoreCase(event.traceId(), normalized) || containsIgnoreCase(event.correlationId(), normalized) || containsIgnoreCase(event.eventKind(), normalized) || containsIgnoreCase(event.workstream(), normalized) || containsIgnoreCase(event.summary(), normalized) || containsIgnoreCase(event.status(), normalized);
  }

  private static String sourceType(TraceEvent event) {
    var kind = event.eventKind().toLowerCase();
    if (kind.contains("tool")) return "tool";
    if (kind.contains("model") || kind.contains("prompt") || event.workstream().contains("agent")) return "model";
    if (kind.contains("workstream")) return "workstream";
    return "policy";
  }

  private static String redacted(String value) {
    if (value == null) return "";
    return value.replaceAll("(?i)(sk-[A-Za-z0-9_-]+|api[_-]?key=[^\\s,}]+|bearer\\s+[^\\s,}]+)", "[REDACTED]");
  }

  private static boolean containsIgnoreCase(String value, String needle) {
    return value != null && needle != null && value.toLowerCase().contains(needle.toLowerCase());
  }

  private static int intInput(Object input, String key, int defaultValue) {
    if (input instanceof Map<?, ?> map && map.get(key) instanceof Number number) return number.intValue();
    return defaultValue;
  }

  private static String stringInput(Object input, String key, String defaultValue) {
    if (input instanceof Map<?, ?> map && map.get(key) instanceof String value) return value;
    return defaultValue;
  }

  public static String stableSuffix(String value) {
    return Integer.toHexString(Math.abs(Objects.toString(value, "missing").hashCode()));
  }

  @SafeVarargs
  private static Map<String, Object> mapOf(Object... entries) {
    var map = new LinkedHashMap<String, Object>();
    for (int index = 0; index < entries.length; index += 2) map.put((String) entries[index], entries[index + 1]);
    return map;
  }

  public record SurfaceData(String surfaceId, String surfaceType, String title, List<String> traceIds, Map<String, Object> data) {}

  public record TraceEvent(
      String traceId,
      Instant occurredAt,
      String tenantId,
      String customerId,
      String correlationId,
      String eventKind,
      String actor,
      String workstream,
      String severity,
      String status,
      String summary,
      String capabilityId,
      List<String> omittedFieldKeys) {}
}
