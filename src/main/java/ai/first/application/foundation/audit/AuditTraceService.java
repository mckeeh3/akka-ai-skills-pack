package ai.first.application.foundation.audit;

import ai.first.domain.foundation.identity.AuthContext;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.AuthorizationException;
import ai.first.application.coreapp.audit.AuditTraceSummaryService;

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
  public static final String EXPORT_REQUEST_CAPABILITY = "audit.trace.export.request";
  private static final List<String> DEFAULT_OMITTED_FIELDS = List.of("rawJwt", "rawProviderCredential", "hiddenPromptText", "rawToolPayload", "invitationToken", "providerCredentialValue");

  private final AuthContextResolver authContextResolver;
  private final AuditTraceRepository repository;

  public AuditTraceService(AuthContextResolver authContextResolver, AuditTraceRepository repository) {
    this.authContextResolver = Objects.requireNonNull(authContextResolver);
    this.repository = Objects.requireNonNull(repository);
  }

  public SurfaceData dashboard(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), DASHBOARD_CAPABILITY);
    authContextResolver.appendProtectedReadTrace(actor, DASHBOARD_CAPABILITY, "dashboard scoped to selected AuthContext", correlationId);
    var events = sortedEvents(actor, correlationId);
    var warningCount = events.stream().filter(event -> "warning".equals(event.severity())).count();
    var providerCount = events.stream().filter(event -> containsIgnoreCase(event.summary(), "provider") || containsIgnoreCase(event.eventKind(), "provider")).count();
    var denialCount = events.stream().filter(event -> containsIgnoreCase(event.status(), "denied") || containsIgnoreCase(event.summary(), "denied")).count();
    return new SurfaceData("surface-audit-trace-dashboard", "dashboard", "Audit/Trace dashboard", List.of("trace-audit-dashboard-" + stableSuffix(correlationId)), mapOf(
        "surfaceContract", "audit.trace.dashboard.v1",
        "selectedScope", mapOf("tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId(), "scopeKind", actor.selectedContext().customerId() == null ? "tenant" : "customer", "supportAccess", "selected AuthContext"),
        "authContextSummary", mapOf("selectedContextId", actor.selectedContext().membershipId(), "visibleCapabilityCount", actor.selectedContext().capabilities().size(), "roleLabels", actor.selectedContext().roles().stream().map(Enum::name).toList()),
        "cards", List.of(
            mapOf("cardId", "card-runtime-traces", "label", "Runtime traces", "value", events.size(), "severity", events.isEmpty() ? "info" : "warning", "actionId", "action-audit-trace-search", "targetSurfaceId", "surface-audit-trace-search", "capabilityId", SEARCH_CAPABILITY, "emptyBehavior", "open authorized empty trace search"),
            mapOf("cardId", "card-provider-tool-model", "label", "Provider/tool/model failures", "value", providerCount, "severity", providerCount == 0 ? "info" : "warning", "actionId", "action-audit-trace-failure-evidence", "targetSurfaceId", "surface-audit-trace-failure-evidence", "capabilityId", FAILURE_EVIDENCE_CAPABILITY, "emptyBehavior", "open safe failure-evidence explanation"),
            mapOf("cardId", "card-denials", "label", "Authorization denials", "value", denialCount, "severity", denialCount == 0 ? "info" : "warning", "actionId", "action-audit-trace-timeline", "targetSurfaceId", "surface-audit-trace-timeline", "capabilityId", TIMELINE_CAPABILITY, "emptyBehavior", "open correlation timeline with hidden evidence omitted"),
            mapOf("cardId", "card-redaction", "label", "Redaction", "value", "browser-safe", "severity", "info", "actionId", "action-audit-trace-investigation-guide", "targetSurfaceId", "surface-audit-trace-investigation-guide", "capabilityId", INVESTIGATION_GUIDE_CAPABILITY, "emptyBehavior", "explain redaction and next safe steps")),
        "attentionItems", List.of(mapOf("itemId", "warnings", "label", "Warnings and denials", "status", warningCount == 0 ? "clear" : "needs_review", "severity", warningCount == 0 ? "info" : "warning", "actionId", "action-audit-trace-search", "resultSurfaceId", "surface-audit-trace-search", "capabilityId", SEARCH_CAPABILITY, "traceId", "trace-audit-dashboard-" + stableSuffix(correlationId))),
        "sections", List.of(
            mapOf("sectionId", "needs-attention", "label", "Needs attention", "summary", "Warnings, denials, provider/tool/model failures, and summary blockers open backend-authorized Audit/Trace surfaces."),
            mapOf("sectionId", "things-i-can-do", "label", "Things I can do", "summary", "Search, timeline, failure evidence, guidance, redacted export, and summary worker actions recheck capability and scope server-side."),
            mapOf("sectionId", "readiness-notices", "label", "Readiness notices", "summary", "Audit summary worker start fails closed until provider/runtime/tool-boundary configuration is present; no model-less success is shown.")),
        "readiness", "Trace search, details, timeline, failure evidence, guidance, redacted export, and summary worker entry points are backend-scoped and redacted for the selected AuthContext.",
        "capabilityIds", List.of(DASHBOARD_CAPABILITY, SEARCH_CAPABILITY, DETAIL_CAPABILITY, TIMELINE_CAPABILITY, FAILURE_EVIDENCE_CAPABILITY, INVESTIGATION_GUIDE_CAPABILITY, INVESTIGATION_NOTE_CAPABILITY, EXPORT_REQUEST_CAPABILITY, AuditTraceSummaryService.START_CAPABILITY, AuditTraceSummaryService.READ_CAPABILITY),
        "redaction", "redacted browser-safe evidence; provider credentials, raw tokens, hidden prompts, raw tool payloads, invitation tokens, and cross-scope evidence omitted"));
  }

  public SurfaceData search(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), SEARCH_CAPABILITY);
    var scope = validateScope(actor, input, correlationId);
    var pageSize = intInput(input, "pageSize", 10);
    if (pageSize < 1 || pageSize > 50) return validation(actor, correlationId, "pageSize", "Page size must be between 1 and 50.");
    var filter = stringInput(input, "filter", "recent");
    var normalizedFilter = filter == null || filter.isBlank() ? "recent" : filter.trim();
    authContextResolver.appendProtectedReadTrace(actor, SEARCH_CAPABILITY, "search filter:" + normalizedFilter + " page-size-band:" + (pageSize <= 10 ? "small" : "bounded"), correlationId);
    var matchingEvents = sortedEvents(actor, correlationId).stream()
        .filter(event -> matchesFilter(event, normalizedFilter))
        .toList();
    var rows = matchingEvents.stream()
        .map(this::row)
        .limit(pageSize)
        .toList();
    var omittedCategories = matchingEvents.stream()
        .filter(event -> event.omittedFieldKeys() != null && !event.omittedFieldKeys().isEmpty())
        .flatMap(event -> event.omittedFieldKeys().stream())
        .distinct()
        .toList();
    var traceId = "trace-audit-search-" + stableSuffix(correlationId);
    return new SurfaceData("surface-audit-trace-search", "list-search", "Trace search results", List.of(traceId), mapOf(
        "surfaceContract", "audit.trace.search.v1",
        "selectedScope", mapOf("tenantLabel", "Selected tenant", "customerLabel", actor.selectedContext().customerId() == null ? null : "Selected customer", "scopeKind", actor.selectedContext().customerId() == null ? "tenant" : "customer", "supportAccess", "selected AuthContext"),
        "authContextSummary", mapOf("selectedContextId", actor.selectedContext().membershipId(), "actor", actor.profile().displayName(), "roleLabels", actor.selectedContext().roles().stream().map(Enum::name).toList(), "capabilityCount", actor.selectedContext().capabilities().size()),
        "capabilityIds", List.of(SEARCH_CAPABILITY, DETAIL_CAPABILITY, TIMELINE_CAPABILITY, FAILURE_EVIDENCE_CAPABILITY, INVESTIGATION_GUIDE_CAPABILITY, EXPORT_REQUEST_CAPABILITY, DASHBOARD_CAPABILITY),
        "correlationId", correlationId,
        "traceRefs", List.of(traceId),
        "query", mapOf("displayValue", normalizedFilter, "pageSize", pageSize, "scope", scope, "helpText", "Search runs server-side over authorized, redacted Audit/Trace evidence for the selected AuthContext."),
        "filters", mapOf("text", normalizedFilter, "timeWindow", "recent", "category", "all-authorized", "sort", "newest-first"),
        "resultSummary", mapOf("visibleResultCount", rows.size(), "cappedAtPageSize", matchingEvents.size() > rows.size(), "appliedScope", actor.selectedContext().customerId() == null ? "tenant" : "customer", "omittedCategoryCounts", omittedCategories.isEmpty() ? Map.of() : Map.of("redactedFields", omittedCategories.size()), "nextStep", rows.isEmpty() ? "Clear filters or return to the Audit/Trace dashboard." : "Open a row detail, timeline, failure evidence, guidance, or redacted export through backend actions."),
        "rows", rows,
        "pageInfo", mapOf("pageSize", pageSize, "totalKnownCount", rows.size(), "hasMore", matchingEvents.size() > rows.size(), "nextCursor", null, "sortLabel", "Newest authorized evidence first", "staleHint", "Refresh reruns backend authorization and redaction."),
        "partial", false,
        "actions", List.of("action-audit-trace-search", "action-audit-trace-detail", "action-audit-trace-timeline", "action-audit-trace-failure-evidence", "action-audit-trace-investigation-guide", "action-audit-trace-request-redacted-export", "action-audit-trace-dashboard"),
        "emptyState", rows.isEmpty() ? mapOf("status", "empty", "message", "No authorized trace rows match this scoped search.", "recovery", "Clear filters or return to the Audit/Trace dashboard.") : null,
        "validationErrors", List.of(),
        "recovery", "Search, paging, row open, timeline, failure evidence, guidance, export, and dashboard return actions reauthorize server-side and preserve trace/correlation refs.",
        "redaction", mapOf("browserSafe", true, "omittedFieldKeys", DEFAULT_OMITTED_FIELDS, "hiddenCountPolicy", "non-enumerating", "safeExplanation", "Safe summaries only; raw payloads, tokens, provider secrets, invitation tokens, hidden prompts, storage cursors, and cross-scope evidence are omitted.", "traceRefs", List.of(traceId))));
  }

  public SurfaceData detail(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), DETAIL_CAPABILITY);
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
    authContextResolver.requireCapability(actor.selectedContext(), TIMELINE_CAPABILITY);
    validateScope(actor, input, correlationId);
    var requestedCorrelation = stringInput(input, "correlationId", correlationId);
    if (requestedCorrelation == null || requestedCorrelation.isBlank() || requestedCorrelation.length() > 128) return validation(actor, correlationId, "correlationId", "Correlation id is required and must be at most 128 characters.");
    var traceId = "trace-audit-timeline-" + stableSuffix(correlationId);
    authContextResolver.appendProtectedReadTrace(actor, TIMELINE_CAPABILITY, "timeline correlation:" + requestedCorrelation, correlationId);
    var selectedEvents = sortedEvents(actor, correlationId).stream()
        .filter(event -> requestedCorrelation.equals(event.correlationId()))
        .sorted(Comparator.comparing(TraceEvent::occurredAt))
        .toList();
    var events = new ArrayList<Map<String, Object>>();
    events.add(mapOf(
        "eventId", "auth-context",
        "nodeId", "auth-context",
        "sequence", 0,
        "occurredAt", Instant.now().toString(),
        "actor", "selected AuthContext",
        "action", "allowed: Selected AuthContext resolved and tenant/customer scope applied.",
        "traceId", "auth-context",
        "sourceType", "policy",
        "correlationId", requestedCorrelation,
        "status", "allowed",
        "severity", "info",
        "summary", "Selected AuthContext resolved and tenant/customer scope applied.",
        "redactedSummary", "Selected AuthContext resolved and tenant/customer scope applied.",
        "redactionBadges", List.of("tenant-scoped", "browser-safe"),
        "availableEventActionIds", List.of("action-audit-trace-detail", "action-audit-trace-investigation-guide"),
        "recoveryText", "Open governed detail, guidance, search, or dashboard actions to reauthorize more evidence."));
    for (var index = 0; index < selectedEvents.size(); index++) {
      var event = selectedEvents.get(index);
      events.add(timelineEvent(event, index + 1));
    }
    var omittedCategories = selectedEvents.stream()
        .filter(event -> event.omittedFieldKeys() != null && !event.omittedFieldKeys().isEmpty())
        .flatMap(event -> event.omittedFieldKeys().stream())
        .distinct()
        .toList();
    var nodes = events.stream()
        .map(event -> mapOf(
            "nodeId", event.get("eventId"),
            "sourceType", event.get("sourceType"),
            "summary", event.get("redactedSummary"),
            "correlationId", event.get("correlationId"),
            "status", event.get("status"),
            "traceId", event.get("traceId")))
        .toList();
    var firstOccurred = selectedEvents.stream().map(TraceEvent::occurredAt).min(Comparator.naturalOrder()).map(Instant::toString).orElse(null);
    var lastOccurred = selectedEvents.stream().map(TraceEvent::occurredAt).max(Comparator.naturalOrder()).map(Instant::toString).orElse(null);
    return new SurfaceData("surface-audit-trace-timeline", "audit-timeline", "Correlation timeline", List.of(traceId), mapOf(
        "surfaceContract", "audit.trace.timeline.v1",
        "surfaceId", "surface-audit-trace-timeline",
        "generatedAt", Instant.now().toString(),
        "selectedScope", mapOf("tenantLabel", "Selected tenant", "customerLabel", actor.selectedContext().customerId() == null ? null : "Selected customer", "scopeKind", actor.selectedContext().customerId() == null ? "tenant" : "customer", "supportAccess", "selected AuthContext"),
        "authContextSummary", mapOf("selectedContextId", actor.selectedContext().membershipId(), "actor", actor.profile().displayName(), "roleLabels", actor.selectedContext().roles().stream().map(Enum::name).toList(), "capabilityCount", actor.selectedContext().capabilities().size()),
        "capabilityIds", List.of(TIMELINE_CAPABILITY, DETAIL_CAPABILITY, FAILURE_EVIDENCE_CAPABILITY, INVESTIGATION_GUIDE_CAPABILITY, EXPORT_REQUEST_CAPABILITY, INVESTIGATION_NOTE_CAPABILITY, SEARCH_CAPABILITY, DASHBOARD_CAPABILITY),
        "correlationId", requestedCorrelation,
        "traceRefs", List.of(traceId),
        "readiness", "ready",
        "timelineKey", mapOf("displayHandle", requestedCorrelation, "retentionStatus", "retained-redacted", "staleOrPurgedStatus", selectedEvents.isEmpty() ? "empty-authorized" : "current"),
        "correlationSummary", mapOf("timeRange", mapOf("from", firstOccurred, "to", lastOccurred), "sourceLabels", selectedEvents.stream().map(TraceEvent::workstream).distinct().toList(), "initiatingActor", selectedEvents.stream().map(TraceEvent::actor).findFirst().orElse(actor.account().accountId()), "outcome", selectedEvents.stream().anyMatch(event -> containsIgnoreCase(event.status(), "denied")) ? "attention_needed" : "allowed", "severity", selectedEvents.stream().anyMatch(event -> "warning".equals(event.severity())) ? "warning" : "info", "visibleEventCount", events.size(), "selectedScope", actor.selectedContext().customerId() == null ? "tenant" : "customer", "nextStep", selectedEvents.isEmpty() ? "No retained events matched this authorized correlation; refine search or return to the dashboard." : "Open a governed detail, failure evidence, guidance, redacted export, note, search, or dashboard action."),
        "authorizationBasis", mapOf("selectedContextId", actor.selectedContext().membershipId(), "visibleCapabilityIds", List.of(TIMELINE_CAPABILITY), "customerScopeRestricted", actor.selectedContext().customerId() != null, "redactionExplanation", "Unauthorized categories and cross-scope evidence are omitted without enumeration."),
        "filters", mapOf("timeWindow", "recent", "eventCategories", "all-authorized", "correlationLabel", requestedCorrelation, "redaction", "browser-safe"),
        "events", events,
        "nodes", nodes,
        "links", selectedEvents.stream().limit(10).map(event -> mapOf("label", event.traceId(), "relationship", "timeline-event", "targetSurfaceId", "surface-audit-trace-detail", "actionId", "action-audit-trace-detail", "redactionBadges", List.of("browser-safe"))).toList(),
        "partial", false,
        "omittedCategories", omittedCategories,
        "availableActions", List.of("action-audit-trace-detail", "action-audit-trace-failure-evidence", "action-audit-trace-investigation-guide", "action-audit-trace-request-redacted-export", "action-audit-trace-append-investigation-note", "action-audit-trace-search", "action-audit-trace-dashboard"),
        "emptyState", selectedEvents.isEmpty() ? mapOf("status", "empty", "message", "No authorized timeline events match this correlation.", "recovery", "Refine search or return to the Audit/Trace dashboard; hidden evidence is not enumerated.") : null,
        "validationErrors", List.of(),
        "recovery", "Timeline reads, event opens, failure evidence, guidance, export, note, search, and dashboard return actions reauthorize server-side and preserve trace/correlation refs.",
        "redaction", mapOf("browserSafe", true, "omittedFieldKeys", DEFAULT_OMITTED_FIELDS, "hiddenCountPolicy", "non-enumerating", "safeExplanation", "Unauthorized tenant/customer evidence is omitted; raw prompts, provider/tool payloads, credentials, tokens, hidden ids, and cross-scope evidence are never returned.", "traceRefs", List.of(traceId)),
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

  public SurfaceData requestRedactedExport(AuthContextResolver.ResolvedMe actor, Object input, String idempotencyKey, String correlationId) {
    validateScope(actor, input, correlationId);
    if (idempotencyKey == null || idempotencyKey.isBlank()) return validation(actor, correlationId, "idempotencyKey", "A client-generated idempotency key is required for export requests.");
    var requestedFormat = stringInput(input, "format", "jsonl-redacted");
    var reason = stringInput(input, "reason", "Audit investigation export requested.");
    if (reason == null || reason.isBlank() || reason.length() > 300) return validation(actor, correlationId, "reason", "Export reason is required and must be at most 300 characters.");
    authContextResolver.appendProtectedReadTrace(actor, EXPORT_REQUEST_CAPABILITY, "redacted-export-request:policy-gated", correlationId);
    var exportId = "audit-export-" + stableSuffix(actor.selectedContext().tenantId() + ":" + idempotencyKey);
    return new SurfaceData("surface-audit-trace-export-request", "decision", "Redacted audit export request", List.of("trace-audit-export-" + stableSuffix(correlationId + ":" + idempotencyKey)), mapOf(
        "surfaceContract", "audit.trace.exportRequest.v1",
        "exportId", exportId,
        "status", "approval_required",
        "requestedFormat", requestedFormat,
        "reasonSummary", redacted(reason),
        "policyDecision", "redacted_export_requires_policy_gate",
        "recommendation", "Approve only scoped redacted export bundles; unredacted export remains forbidden by default.",
        "risk", "medium",
        "allowedActions", List.of(mapOf("actionId", "action-audit-trace-timeline", "label", "Review correlation timeline", "capabilityId", TIMELINE_CAPABILITY), mapOf("actionId", "action-audit-trace-search", "label", "Refine scoped evidence", "capabilityId", SEARCH_CAPABILITY)),
        "disabledActions", List.of(mapOf("actionId", "action-audit-trace-unredacted-export", "reason", "Unredacted export is not a default browser action and requires a separate policy exception.")),
        "bundleMetadata", mapOf("tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId(), "redactionProfile", "browser-safe", "omittedFieldKeys", DEFAULT_OMITTED_FIELDS),
        "traceLinks", List.of(correlationId),
        "redaction", "Export request stores scoped metadata only; raw evidence, tokens, provider secrets, hidden prompts, and cross-tenant facts are omitted."));
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
    return mapOf("scopeKind", actor.selectedContext().customerId() == null ? "tenant" : "customer", "tenantLabel", "Selected tenant", "customerLabel", actor.selectedContext().customerId() == null ? null : "Selected customer", "nonEnumeratingHiddenEvidence", true);
  }

  private List<TraceEvent> sortedEvents(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return repository.eventsFor(actor, correlationId).stream()
        .sorted(Comparator.comparing(TraceEvent::occurredAt).reversed())
        .toList();
  }

  private Map<String, Object> row(TraceEvent event) {
    return mapOf(
        "rowKey", "row-" + stableSuffix(event.tenantId() + ":" + event.traceId()),
        "traceId", event.traceId(),
        "safeTraceRefLabel", event.traceId(),
        "correlationId", event.correlationId(),
        "correlationLabel", event.correlationId(),
        "timestamp", event.occurredAt().toString(),
        "eventKind", event.eventKind(),
        "evidenceCategoryBadges", List.of(sourceType(event)),
        "actor", event.actor(),
        "workstream", event.workstream(),
        "severity", event.severity(),
        "status", event.status(),
        "availableRowActionIds", List.of("action-audit-trace-detail", "action-audit-trace-timeline", "action-audit-trace-failure-evidence", "action-audit-trace-investigation-guide"),
        "redactionBadges", List.of("browser-safe", "tenant-scoped"),
        "redactionSummary", "redacted safe summary only",
        "recoveryText", "Open governed detail or timeline to reauthorize this trace before viewing more evidence.",
        "summary", redacted(event.summary()));
  }

  private Map<String, Object> timelineEvent(TraceEvent event, int sequence) {
    return mapOf(
        "eventId", event.traceId(),
        "nodeId", event.traceId(),
        "sequence", sequence,
        "occurredAt", event.occurredAt().toString(),
        "actor", event.actor(),
        "action", event.status() + ": " + redacted(event.summary()),
        "traceId", event.traceId(),
        "sourceType", sourceType(event),
        "correlationId", event.correlationId(),
        "status", event.status(),
        "severity", event.severity(),
        "eventCategory", event.eventKind(),
        "sourceWorkstream", event.workstream(),
        "sourceSurfaceActionLabel", event.workstream(),
        "summary", redacted(event.summary()),
        "redactedSummary", redacted(event.summary()),
        "relationship", "selected-correlation-event",
        "redactionBadges", List.of("browser-safe", "tenant-scoped"),
        "availableEventActionIds", List.of("action-audit-trace-detail", "action-audit-trace-failure-evidence", "action-audit-trace-investigation-guide"),
        "recoveryText", "Open governed detail, failure evidence, or guidance to reauthorize this event before viewing more evidence.");
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
    if (input instanceof Map<?, ?> map && map.get(key) instanceof String value) {
      try {
        return Integer.parseInt(value.trim());
      } catch (NumberFormatException ignored) {
        return defaultValue;
      }
    }
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
