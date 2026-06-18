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
    authContextResolver.requireCapability(actor.selectedContext(), FAILURE_EVIDENCE_CAPABILITY);
    validateScope(actor, input, correlationId);
    var category = stringInput(input, "failureCategory", "provider_blocked");
    var normalizedCategory = category == null || category.isBlank() ? "provider_blocked" : category.trim();
    if (normalizedCategory.length() > 120) return validation(actor, correlationId, "failureCategory", "Failure category must be at most 120 characters.");
    var traceId = "trace-audit-failure-" + stableSuffix(correlationId);
    authContextResolver.appendProtectedReadTrace(actor, FAILURE_EVIDENCE_CAPABILITY, "failure category:" + normalizedCategory, correlationId);
    var related = sortedEvents(actor, correlationId).stream()
        .filter(event -> containsIgnoreCase(event.summary(), normalizedCategory) || containsIgnoreCase(event.eventKind(), normalizedCategory) || containsIgnoreCase(event.status(), "denied") || containsIgnoreCase(event.summary(), "provider") || containsIgnoreCase(event.summary(), "tool") || containsIgnoreCase(event.summary(), "model") || containsIgnoreCase(event.summary(), "runtime"))
        .limit(5)
        .map(event -> mapOf(
            "traceId", event.traceId(),
            "safeTraceRefLabel", event.traceId(),
            "eventKind", event.eventKind(),
            "summary", redacted(event.summary()),
            "correlationId", event.correlationId(),
            "status", event.status(),
            "relationship", "related-redacted-failure-evidence",
            "redactionBadges", List.of("browser-safe", "tenant-scoped"),
            "availableActionIds", List.of("action-audit-trace-detail", "action-audit-trace-timeline", "action-audit-trace-investigation-guide", "action-audit-trace-request-redacted-export", "action-audit-trace-append-investigation-note")))
        .toList();
    var omittedFields = related.isEmpty() ? DEFAULT_OMITTED_FIELDS : sortedEvents(actor, correlationId).stream()
        .filter(event -> containsIgnoreCase(event.summary(), normalizedCategory) || containsIgnoreCase(event.eventKind(), normalizedCategory) || containsIgnoreCase(event.status(), "denied") || containsIgnoreCase(event.summary(), "provider") || containsIgnoreCase(event.summary(), "tool") || containsIgnoreCase(event.summary(), "model") || containsIgnoreCase(event.summary(), "runtime"))
        .filter(event -> event.omittedFieldKeys() != null)
        .flatMap(event -> event.omittedFieldKeys().stream())
        .distinct()
        .toList();
    var status = related.isEmpty() ? "empty-no-authorized-failure" : "ready";
    return new SurfaceData("surface-audit-trace-failure-evidence", "detail-edit", "Denial/provider/tool evidence", List.of(traceId), mapOf(
        "surfaceContract", "audit.trace.failureEvidence.v1",
        "surfaceId", "surface-audit-trace-failure-evidence",
        "generatedAt", Instant.now().toString(),
        "selectedScope", mapOf("tenantLabel", "Selected tenant", "customerLabel", actor.selectedContext().customerId() == null ? null : "Selected customer", "scopeKind", actor.selectedContext().customerId() == null ? "tenant" : "customer", "supportAccess", "selected AuthContext"),
        "authContextSummary", mapOf("selectedContextId", actor.selectedContext().membershipId(), "actor", actor.profile().displayName(), "roleLabels", actor.selectedContext().roles().stream().map(Enum::name).toList(), "capabilityCount", actor.selectedContext().capabilities().size()),
        "capabilityIds", List.of(FAILURE_EVIDENCE_CAPABILITY, DETAIL_CAPABILITY, TIMELINE_CAPABILITY, INVESTIGATION_GUIDE_CAPABILITY, EXPORT_REQUEST_CAPABILITY, INVESTIGATION_NOTE_CAPABILITY, SEARCH_CAPABILITY, DASHBOARD_CAPABILITY),
        "correlationId", correlationId,
        "traceRefs", List.of(traceId),
        "readiness", status,
        "failureKey", mapOf("displayHandle", normalizedCategory, "diagnosticCorrelationLabel", correlationId, "retentionStatus", "retained-redacted", "staleOrPurgedStatus", related.isEmpty() ? "empty-authorized" : "current"),
        "failureSummary", mapOf("severity", related.isEmpty() ? "info" : "warning", "status", status, "decision", related.isEmpty() ? "not_found_or_redacted" : "allowed", "sourceWorkstream", "Audit/Trace", "sourceSurface", "surface-audit-trace-failure-evidence", "failureCategory", normalizedCategory, "redactedNarrative", related.isEmpty() ? "No authorized matching failure evidence was found for the selected context." : "Authorized failure evidence is available as redacted browser-safe summaries.", "nextStep", related.isEmpty() ? "Refine search or return to the Audit/Trace dashboard; hidden failures are not enumerated." : "Open detail, timeline, guidance, export request, note, search, or dashboard actions through backend-governed links."),
        "authorizationBasis", mapOf("selectedContextId", actor.selectedContext().membershipId(), "visibleCapabilityIds", List.of(FAILURE_EVIDENCE_CAPABILITY), "customerScopeRestricted", actor.selectedContext().customerId() != null, "redactionExplanation", "Selected AuthContext, tenant/customer scope, and failure-evidence capability are rechecked server-side; hidden failures are not enumerated."),
        "failureClassification", mapOf("category", normalizedCategory, "safePublicErrorCode", normalizedCategory, "retryEligibility", "retry_after_provider_config", "approvalRequirement", "separate governed action required for export or policy review", "responsibleWorkstream", "Audit/Trace", "affectedCapability", FAILURE_EVIDENCE_CAPABILITY, "blocksCompletion", related.isEmpty() ? false : true),
        "category", normalizedCategory,
        "safeReason", "Provider, tool, model, worker, policy, runtime, and authorization failures are shown as redacted browser-safe evidence only.",
        "recovery", mapOf("steps", List.of("Confirm the selected AuthContext and capability basis.", "Open correlation timeline or source trace detail for reauthorized context.", "Use governed export or note actions when policy allows."), "failClosed", "Provider/model/tool/runtime blockers fail closed; the browser cannot bypass policy, hidden ids, or redaction."),
        "userActionableNextSteps", List.of("Check selected AuthContext and required capability.", "Open correlation timeline.", "Ask Audit/Trace for an explanation after provider configuration is available."),
        "evidence", mapOf("requestContext", "selected AuthContext and category only", "decisionOutcome", status, "failureTimelineExcerpt", related, "toolModelProviderRuntimeReadiness", "fail-closed when provider/runtime/tool-boundary configuration is unavailable", "retainedOutcome", "read-only redacted evidence; no source records are mutated"),
        "policyRefs", List.of(READ_CAPABILITY, FAILURE_EVIDENCE_CAPABILITY),
        "source", mapOf("workstream", "Audit/Trace", "surfaceId", "surface-audit-trace-failure-evidence", "actionId", "action-audit-trace-failure-evidence", "sourceResultStatus", status),
        "relatedEvents", related,
        "availableActions", List.of("action-audit-trace-detail", "action-audit-trace-timeline", "action-audit-trace-investigation-guide", "action-audit-trace-request-redacted-export", "action-audit-trace-append-investigation-note", "action-audit-trace-search", "action-audit-trace-dashboard"),
        "emptyState", related.isEmpty() ? mapOf("status", "empty/no-authorized-failure", "message", "No authorized failure evidence matched this category in the selected scope.", "recovery", "Refine search or return to the Audit/Trace dashboard; hidden failures are not enumerated.") : null,
        "validationErrors", List.of(),
        "redaction", mapOf("browserSafe", true, "omittedFieldKeys", omittedFields.isEmpty() ? DEFAULT_OMITTED_FIELDS : omittedFields, "hiddenCountPolicy", "non-enumerating", "safeExplanation", "Raw provider/model/tool payloads, prompts, credentials, tokens, hidden policy internals, raw storage keys, and cross-scope evidence are omitted.", "traceRefs", List.of(traceId)),
        "redactedDetails", mapOf("providerSecret", "[REDACTED]", "rawPrompt", "[OMITTED]", "rawToolPayload", "[OMITTED]", "rawModelOutput", "[OMITTED]"),
        "traceLinks", List.of(correlationId, traceId)));
  }

  public SurfaceData investigationGuide(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), INVESTIGATION_GUIDE_CAPABILITY);
    var scope = validateScope(actor, input, correlationId);
    var requestedCorrelation = stringInput(input, "correlationId", correlationId);
    var requestedTraceId = stringInput(input, "traceId", null);
    if (requestedCorrelation == null || requestedCorrelation.isBlank() || requestedCorrelation.length() > 128) return validation(actor, correlationId, "correlationId", "Correlation id is required and must be at most 128 characters.");
    if (requestedTraceId != null && requestedTraceId.length() > 160) return validation(actor, correlationId, "traceId", "Trace id must be at most 160 characters.");
    var traceId = "trace-audit-guide-" + stableSuffix(correlationId);
    authContextResolver.appendProtectedReadTrace(actor, INVESTIGATION_GUIDE_CAPABILITY, "investigation guidance context:" + (requestedTraceId == null ? requestedCorrelation : requestedTraceId), correlationId);
    var relatedEvents = sortedEvents(actor, correlationId).stream()
        .filter(event -> requestedTraceId == null ? requestedCorrelation.equals(event.correlationId()) : requestedTraceId.equals(event.traceId()) || requestedCorrelation.equals(event.correlationId()))
        .limit(5)
        .toList();
    var evidenceSummary = relatedEvents.stream()
        .map(event -> mapOf(
            "evidenceId", event.traceId(),
            "label", event.eventKind(),
            "summary", redacted(event.summary()),
            "status", event.status(),
            "redactionNote", "Browser-safe summary only; raw payloads, prompts, credentials, tokens, hidden ids, and cross-scope evidence are omitted.",
            "traceRefs", List.of(event.traceId(), event.correlationId())))
        .toList();
    var contextType = requestedTraceId == null ? "correlation_timeline" : "trace_detail";
    var readiness = evidenceSummary.isEmpty() ? "empty/no-authorized-guidance" : "ready";
    return new SurfaceData("surface-audit-trace-investigation-guide", "decision", "Investigation guidance", List.of(traceId), mapOf(
        "surfaceContract", "audit.trace.investigationGuide.v1",
        "surfaceId", "surface-audit-trace-investigation-guide",
        "generatedAt", Instant.now().toString(),
        "selectedScope", mapOf("tenantLabel", "Selected tenant", "customerLabel", actor.selectedContext().customerId() == null ? null : "Selected customer", "scopeKind", actor.selectedContext().customerId() == null ? "tenant" : "customer", "supportAccess", "selected AuthContext"),
        "authContextSummary", mapOf("selectedContextId", actor.selectedContext().membershipId(), "actor", actor.profile().displayName(), "roleLabels", actor.selectedContext().roles().stream().map(Enum::name).toList(), "capabilityCount", actor.selectedContext().capabilities().size()),
        "capabilityIds", List.of(INVESTIGATION_GUIDE_CAPABILITY, DETAIL_CAPABILITY, TIMELINE_CAPABILITY, FAILURE_EVIDENCE_CAPABILITY, EXPORT_REQUEST_CAPABILITY, INVESTIGATION_NOTE_CAPABILITY, SEARCH_CAPABILITY, DASHBOARD_CAPABILITY, AuditTraceSummaryService.START_CAPABILITY),
        "correlationId", requestedCorrelation,
        "traceRefs", List.of(traceId, requestedCorrelation),
        "readiness", readiness,
        "guideKey", mapOf("displayHandle", requestedTraceId == null ? requestedCorrelation : requestedTraceId, "diagnosticCorrelationLabel", requestedCorrelation, "sourceSurfaceActionLabel", stringInput(input, "sourceSurfaceId", "Audit/Trace investigation"), "retentionStatus", "retained-redacted", "staleOrPurgedStatus", evidenceSummary.isEmpty() ? "empty-authorized" : "current"),
        "investigationContext", mapOf("contextType", contextType, "sourceWorkstream", "Audit/Trace", "sourceSurface", stringInput(input, "sourceSurfaceId", "surface-audit-trace-investigation-guide"), "sourceAction", "action-audit-trace-investigation-guide", "selectedScope", scope, "visibleStatus", readiness, "severity", relatedEvents.stream().anyMatch(event -> "warning".equals(event.severity())) ? "warning" : "info", "question", "What should I do next, why is it safe, and what evidence can I open?", "redactionLimits", "Hidden traces, raw provider/model/tool payloads, prompts, credentials, policy internals, and cross-scope evidence are omitted without enumeration."),
        "authorizationBasis", mapOf("selectedContextId", actor.selectedContext().membershipId(), "visibleCapabilityIds", List.of(INVESTIGATION_GUIDE_CAPABILITY), "customerScopeRestricted", actor.selectedContext().customerId() != null, "redactionExplanation", "Selected AuthContext, tenant/customer scope, and investigation-guide capability are rechecked server-side; guidance is advisory and cannot expand authority."),
        "riskSummary", mapOf("severity", relatedEvents.stream().anyMatch(event -> "warning".equals(event.severity())) ? "warning" : "info", "confidence", evidenceSummary.isEmpty() ? "limited-authorized-evidence" : "authorized-redacted-evidence", "affectedEvidenceCategories", relatedEvents.stream().map(TraceEvent::eventKind).distinct().toList(), "retentionStatus", "retained-redacted", "providerModelToolRuntimeReadiness", "fail-closed when provider/runtime/tool-boundary configuration is unavailable", "approvalRequirement", "Redacted exports, notes, policy review, and summary worker paths remain separate governed actions."),
        "recommendedPath", List.of(
            mapOf("stepId", "step-review-authorized-evidence", "label", "Review authorized evidence context", "rationale", "Start from the selected AuthContext and redaction basis before opening more evidence.", "expectedUserOutcome", "Understand what is visible and what was omitted.", "requiredCapabilityLabel", INVESTIGATION_GUIDE_CAPABILITY, "targetActionId", "action-audit-trace-detail", "targetSurfaceId", "surface-audit-trace-detail", "approvalRequirement", "none", "recovery", "If the detail is hidden, use search or dashboard return without enumerating hidden ids."),
            mapOf("stepId", "step-open-timeline-or-failure", "label", "Open timeline or failure evidence", "rationale", "Reauthorize the related chronology or failure category before deciding on export, note, or provider follow-up.", "expectedUserOutcome", "See browser-safe chronology/failure evidence with trace refs.", "requiredCapabilityLabel", TIMELINE_CAPABILITY + " / " + FAILURE_EVIDENCE_CAPABILITY, "targetActionId", "action-audit-trace-timeline", "targetSurfaceId", "surface-audit-trace-timeline", "approvalRequirement", "none", "recovery", "If no authorized events remain, refine search or return to the command center."),
            mapOf("stepId", "step-record-or-export-only-when-governed", "label", "Record a note or request redacted export only through governed actions", "rationale", "Guidance is advisory and never mutates traces, policy, authorization, provider settings, or retained evidence.", "expectedUserOutcome", "A separate policy-gated export request or immutable note annotation is created when authorized.", "requiredCapabilityLabel", EXPORT_REQUEST_CAPABILITY + " / " + INVESTIGATION_NOTE_CAPABILITY, "targetActionId", "action-audit-trace-request-redacted-export", "targetSurfaceId", "surface-audit-trace-export-request", "approvalRequirement", "export requires policy gate; note requires idempotency", "recovery", "Use search/dashboard return when export or note is not appropriate.")),
        "allowedActions", List.of(
            mapOf("actionId", "action-audit-trace-detail", "label", "Open source trace detail", "browserToolId", "action-audit-trace-detail", "governedToolId", DETAIL_CAPABILITY, "capabilityId", DETAIL_CAPABILITY, "resultSurfaceId", "surface-audit-trace-detail", "reason", "Reauthorizes source evidence before showing detail."),
            mapOf("actionId", "action-audit-trace-timeline", "label", "Open correlation timeline", "browserToolId", "action-audit-trace-timeline", "governedToolId", TIMELINE_CAPABILITY, "capabilityId", TIMELINE_CAPABILITY, "resultSurfaceId", "surface-audit-trace-timeline", "reason", "Shows an authorized ordered timeline with omissions explained."),
            mapOf("actionId", "action-audit-trace-failure-evidence", "label", "Open failure evidence", "browserToolId", "action-audit-trace-failure-evidence", "governedToolId", FAILURE_EVIDENCE_CAPABILITY, "capabilityId", FAILURE_EVIDENCE_CAPABILITY, "resultSurfaceId", "surface-audit-trace-failure-evidence", "reason", "Shows redacted denial/provider/tool/model/runtime evidence."),
            mapOf("actionId", "action-audit-trace-search", "label", "Refine search", "browserToolId", "action-audit-trace-search", "governedToolId", SEARCH_CAPABILITY, "capabilityId", SEARCH_CAPABILITY, "resultSurfaceId", "surface-audit-trace-search", "reason", "Re-runs backend-scoped search instead of trusting browser state."),
            mapOf("actionId", "action-audit-trace-dashboard", "label", "Return to investigation command center", "browserToolId", "action-audit-trace-dashboard", "governedToolId", DASHBOARD_CAPABILITY, "capabilityId", DASHBOARD_CAPABILITY, "resultSurfaceId", "surface-audit-trace-dashboard", "reason", "Recomputes scoped dashboard counters server-side."),
            mapOf("actionId", "action-audit-trace-request-redacted-export", "label", "Request redacted export", "browserToolId", "action-audit-trace-request-redacted-export", "governedToolId", EXPORT_REQUEST_CAPABILITY, "capabilityId", EXPORT_REQUEST_CAPABILITY, "resultSurfaceId", "surface-audit-trace-export-request", "approvalRequirement", "policy-gated", "reason", "Creates a separate redacted export decision surface; unredacted browser export is forbidden."),
            mapOf("actionId", "action-audit-trace-append-investigation-note", "label", "Append investigation note", "browserToolId", "action-audit-trace-append-investigation-note", "governedToolId", INVESTIGATION_NOTE_CAPABILITY, "capabilityId", INVESTIGATION_NOTE_CAPABILITY, "resultSurfaceId", "surface-audit-trace-investigation-note", "idempotency", "client-generated", "reason", "Annotates a trace without mutating source evidence, policy, or authorization."),
            mapOf("actionId", "action-audit-trace-summary-task-start", "label", "Start bounded audit summary task", "browserToolId", "action-audit-trace-summary-task-start", "governedToolId", "audit.trace.summaryTask.start", "capabilityId", AuditTraceSummaryService.START_CAPABILITY, "resultSurfaceId", "surface-audit-trace-summary-progress", "reason", "Opens a fail-closed provider/runtime readiness surface unless a real model-backed worker is configured.")),
        "disabledActions", List.of(
            mapOf("actionId", "action-audit-trace-unredacted-export", "label", "Unredacted browser export", "reason", "export_forbidden: unredacted browser export is not available by default and requires a separate policy exception.", "recovery", "Use the governed redacted export request.", "auditOnlyDenialCategory", "export_forbidden"),
            mapOf("actionId", "action-audit-trace-guidance-authority-expansion", "label", "Use guidance as authorization", "reason", "not_applicable: investigation guidance is advisory and cannot grant roles, scopes, or evidence access.", "recovery", "Open each follow-up surface through its backend-governed action.", "auditOnlyDenialCategory", "authority_expansion_forbidden")),
        "evidenceSummary", evidenceSummary,
        "policyRefs", List.of(READ_CAPABILITY, INVESTIGATION_GUIDE_CAPABILITY, TIMELINE_CAPABILITY, FAILURE_EVIDENCE_CAPABILITY, EXPORT_REQUEST_CAPABILITY),
        "recovery", mapOf("steps", List.of("Reconfirm selected AuthContext and customer scope.", "Open detail, timeline, failure evidence, search, or dashboard through backend actions.", "Use idempotent note/export actions only when policy and context allow."), "failClosed", "Provider/model/tool/runtime blockers remain blocked-provider-or-runtime; this guide never fabricates model-backed success."),
        "emptyState", evidenceSummary.isEmpty() ? mapOf("status", "empty/no-authorized-guidance", "message", "No authorized retained evidence matched this guidance context.", "recovery", "Refine search or return to the Audit/Trace dashboard; hidden contexts are not enumerated.") : null,
        "validationErrors", List.of(),
        "recommendation", "Continue only with backend-authorized, tenant-scoped evidence; this guide is advisory and cannot expand authority or mutate retained traces.",
        "risk", relatedEvents.stream().anyMatch(event -> "warning".equals(event.severity())) ? "medium" : "low",
        "noDirectMutation", true,
        "traceLinks", List.of(requestedCorrelation, traceId),
        "redaction", mapOf("browserSafe", true, "omittedFieldKeys", DEFAULT_OMITTED_FIELDS, "hiddenCountPolicy", "non-enumerating", "safeExplanation", "No secrets, hidden prompts, raw provider/model/tool payloads, raw JWT/session data, policy internals, storage keys, or cross-tenant/customer evidence are returned.", "traceRefs", List.of(traceId))));
  }

  public SurfaceData requestRedactedExport(AuthContextResolver.ResolvedMe actor, Object input, String idempotencyKey, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), EXPORT_REQUEST_CAPABILITY);
    var scope = validateScope(actor, input, correlationId);
    if (idempotencyKey == null || idempotencyKey.isBlank()) return validation(actor, correlationId, "idempotencyKey", "A client-generated idempotency key is required for export requests.");
    var requestedFormat = stringInput(input, "format", "jsonl-redacted");
    if (requestedFormat == null || requestedFormat.isBlank() || requestedFormat.length() > 64) return validation(actor, correlationId, "format", "A redacted export format is required and must be at most 64 characters.");
    var normalizedFormat = requestedFormat.trim().toLowerCase();
    var reason = stringInput(input, "reason", "Audit investigation export requested.");
    if (reason == null || reason.isBlank() || reason.length() > 300) return validation(actor, correlationId, "reason", "Export reason is required and must be at most 300 characters.");
    var traceId = "trace-audit-export-" + stableSuffix(correlationId + ":" + idempotencyKey);
    var unredactedRequested = normalizedFormat.contains("unredacted") || !normalizedFormat.contains("redacted");
    authContextResolver.appendProtectedReadTrace(actor, EXPORT_REQUEST_CAPABILITY, unredactedRequested ? "redacted-export-request:unredacted-forbidden" : "redacted-export-request:policy-gated", correlationId);
    var exportId = "audit-export-" + stableSuffix(actor.selectedContext().tenantId() + ":" + idempotencyKey);
    var relatedEvents = sortedEvents(actor, correlationId).stream().limit(5).toList();
    var evidenceSummary = relatedEvents.stream()
        .map(event -> mapOf(
            "evidenceId", event.traceId(),
            "label", event.eventKind(),
            "summary", redacted(event.summary()),
            "status", event.status(),
            "redactionNote", "Browser-safe export scope only; raw evidence bodies, prompts, provider/tool payloads, tokens, hidden ids, and cross-scope evidence are omitted.",
            "traceRefs", List.of(event.traceId(), event.correlationId())))
        .toList();
    var status = unredactedRequested ? "denied" : "approval_required";
    var policyClassification = unredactedRequested ? "unredacted_export_forbidden" : "approval_required";
    var policyReason = unredactedRequested
        ? "Unredacted browser export is forbidden by default; request a redacted format and keep delivery backend-governed."
        : "Redacted export requests require a policy/approval gate before backend bundle assembly or delivery.";
    return new SurfaceData("surface-audit-trace-export-request", "decision", "Redacted audit export request", List.of(traceId), mapOf(
        "surfaceContract", "audit.trace.exportRequest.v1",
        "surfaceId", "surface-audit-trace-export-request",
        "generatedAt", Instant.now().toString(),
        "selectedScope", mapOf("tenantLabel", "Selected tenant", "customerLabel", actor.selectedContext().customerId() == null ? null : "Selected customer", "scopeKind", actor.selectedContext().customerId() == null ? "tenant" : "customer", "supportAccess", "selected AuthContext"),
        "authContextSummary", mapOf("selectedContextId", actor.selectedContext().membershipId(), "actor", actor.profile().displayName(), "roleLabels", actor.selectedContext().roles().stream().map(Enum::name).toList(), "capabilityCount", actor.selectedContext().capabilities().size()),
        "capabilityIds", List.of(EXPORT_REQUEST_CAPABILITY, DETAIL_CAPABILITY, TIMELINE_CAPABILITY, FAILURE_EVIDENCE_CAPABILITY, INVESTIGATION_GUIDE_CAPABILITY, INVESTIGATION_NOTE_CAPABILITY, SEARCH_CAPABILITY, DASHBOARD_CAPABILITY),
        "correlationId", correlationId,
        "traceRefs", List.of(traceId, correlationId),
        "readiness", status,
        "exportId", exportId,
        "status", status,
        "requestedFormat", normalizedFormat,
        "reasonSummary", redacted(reason),
        "exportRequest", mapOf("displayHandle", exportId, "status", status, "requestedFormatLabel", normalizedFormat, "requestedPurposeSummary", redacted(reason), "submitterLabel", actor.profile().displayName(), "submittedAt", Instant.now().toString(), "sourceWorkstream", "Audit/Trace", "sourceSurface", stringInput(input, "sourceSurfaceId", "surface-audit-trace-export-request"), "sourceAction", "action-audit-trace-request-redacted-export", "idempotencyReplay", false),
        "exportScope", mapOf("selectedScope", scope, "timeWindow", "recent authorized evidence", "evidenceCategories", relatedEvents.stream().map(TraceEvent::eventKind).distinct().toList(), "sourceWorkstreamLabels", relatedEvents.stream().map(TraceEvent::workstream).distinct().toList(), "visibleCount", relatedEvents.size(), "includedRedactedCategories", List.of("audit events", "work traces", "policy decisions", "provider/tool/model failure categories"), "omittedCategories", DEFAULT_OMITTED_FIELDS, "retentionStatus", relatedEvents.isEmpty() ? "empty-authorized" : "retained-redacted"),
        "authorizationBasis", mapOf("selectedContextId", actor.selectedContext().membershipId(), "visibleCapabilityIds", List.of(EXPORT_REQUEST_CAPABILITY), "customerScopeRestricted", actor.selectedContext().customerId() != null, "approvalGateLabels", List.of("redacted-export-policy-gate"), "redactionExplanation", "Selected AuthContext, tenant/customer scope, export-request capability, redaction, and approval requirements are rechecked server-side."),
        "policyDecision", mapOf("classification", policyClassification, "reason", policyReason, "approvalRequired", !unredactedRequested, "recoveryPath", unredactedRequested ? "Use format jsonl-redacted and submit through the governed export request action." : "Wait for backend policy approval; no raw browser bundle or download URL is returned.", "auditOnlyDenialCategory", unredactedRequested ? "unredacted_export_forbidden" : "approval_required"),
        "bundleMetadata", mapOf("bundleLabel", "Redacted Audit/Trace bundle", "requestedFormat", normalizedFormat, "estimatedAvailability", unredactedRequested ? "not_available" : "after_policy_approval", "retentionLabel", "backend-governed retained export decision", "redactionProfile", "browser-safe", "includedEvidenceCategoryLabels", relatedEvents.stream().map(TraceEvent::eventKind).distinct().toList(), "omittedFieldCategories", DEFAULT_OMITTED_FIELDS, "manifestAvailability", "available only after backend approval", "rawDownloadUrl", null),
        "approval", mapOf("status", unredactedRequested ? "denied" : "approval_required", "approverRoleLabel", "Authorized Audit/Trace reviewer", "requiredApprovalCapabilityLabel", EXPORT_REQUEST_CAPABILITY, "safeDueOrExpiry", "policy controlled", "guidance", unredactedRequested ? "Unredacted browser export is not retryable from this surface." : "Approval is required before bundle assembly or handoff."),
        "allowedActions", List.of(
            mapOf("actionId", "action-audit-trace-request-redacted-export", "label", unredactedRequested ? "Retry with redacted format" : "Submit or retry redacted export request", "browserToolId", "action-audit-trace-request-redacted-export", "governedToolId", EXPORT_REQUEST_CAPABILITY, "capabilityId", EXPORT_REQUEST_CAPABILITY, "resultSurfaceId", "surface-audit-trace-export-request", "approvalRequirement", "policy-gated", "idempotency", "client-generated", "reason", "Rechecks selected scope, redaction, policy, and idempotency server-side."),
            mapOf("actionId", "action-audit-trace-detail", "label", "Open source trace detail", "browserToolId", "action-audit-trace-detail", "governedToolId", DETAIL_CAPABILITY, "capabilityId", DETAIL_CAPABILITY, "resultSurfaceId", "surface-audit-trace-detail", "reason", "Reauthorizes source evidence before any detail is shown."),
            mapOf("actionId", "action-audit-trace-timeline", "label", "Review correlation timeline", "browserToolId", "action-audit-trace-timeline", "governedToolId", TIMELINE_CAPABILITY, "capabilityId", TIMELINE_CAPABILITY, "resultSurfaceId", "surface-audit-trace-timeline", "reason", "Reviews browser-safe chronology before export approval."),
            mapOf("actionId", "action-audit-trace-failure-evidence", "label", "Open failure evidence", "browserToolId", "action-audit-trace-failure-evidence", "governedToolId", FAILURE_EVIDENCE_CAPABILITY, "capabilityId", FAILURE_EVIDENCE_CAPABILITY, "resultSurfaceId", "surface-audit-trace-failure-evidence", "reason", "Inspects authorized failure categories without raw provider/tool payloads."),
            mapOf("actionId", "action-audit-trace-investigation-guide", "label", "Open investigation guidance", "browserToolId", "action-audit-trace-investigation-guide", "governedToolId", INVESTIGATION_GUIDE_CAPABILITY, "capabilityId", INVESTIGATION_GUIDE_CAPABILITY, "resultSurfaceId", "surface-audit-trace-investigation-guide", "reason", "Guidance is advisory and cannot approve or deliver exports."),
            mapOf("actionId", "action-audit-trace-append-investigation-note", "label", "Append investigation note", "browserToolId", "action-audit-trace-append-investigation-note", "governedToolId", INVESTIGATION_NOTE_CAPABILITY, "capabilityId", INVESTIGATION_NOTE_CAPABILITY, "resultSurfaceId", "surface-audit-trace-investigation-note", "idempotency", "client-generated", "reason", "Annotates traces only; retained evidence and policy are immutable."),
            mapOf("actionId", "action-audit-trace-search", "label", "Return to scoped search", "browserToolId", "action-audit-trace-search", "governedToolId", SEARCH_CAPABILITY, "capabilityId", SEARCH_CAPABILITY, "resultSurfaceId", "surface-audit-trace-search", "reason", "Reruns backend-scoped search instead of trusting browser state."),
            mapOf("actionId", "action-audit-trace-dashboard", "label", "Return to investigation command center", "browserToolId", "action-audit-trace-dashboard", "governedToolId", DASHBOARD_CAPABILITY, "capabilityId", DASHBOARD_CAPABILITY, "resultSurfaceId", "surface-audit-trace-dashboard", "reason", "Recomputes scoped dashboard counters server-side.")),
        "disabledActions", List.of(
            mapOf("actionId", "action-audit-trace-unredacted-export", "label", "Unredacted export", "reason", "Unredacted export is not a default browser action; no raw download URL is returned.", "recovery", "Use a redacted export format and backend-governed approval.", "auditOnlyDenialCategory", "unredacted_export_forbidden"),
            mapOf("actionId", "action-audit-trace-export-raw-download", "label", "Direct raw download", "reason", "not_authorized: delivery and handoff require a later backend-governed surface after approval.", "recovery", "Wait for approved redacted delivery status.", "auditOnlyDenialCategory", "raw_download_forbidden")),
        "evidenceSummary", evidenceSummary,
        "delivery", mapOf("status", unredactedRequested ? "not_available" : "not_started_pending_approval", "handoff", "No raw browser download URL is exposed from this decision surface.", "rawDownloadUrl", null),
        "recovery", mapOf("steps", List.of("Confirm selected AuthContext and customer scope.", "Use only redacted formats and a human-readable purpose.", "Open detail, timeline, failure evidence, guidance, note, search, or dashboard through backend-governed actions."), "failClosed", "Provider/model/tool/runtime or policy blockers fail closed; the browser cannot bypass approval, redaction, hidden ids, or delivery controls."),
        "emptyState", relatedEvents.isEmpty() ? mapOf("status", "empty/no-authorized-evidence", "message", "No authorized evidence was found for this export scope.", "recovery", "Refine search or return to the Audit/Trace dashboard; hidden evidence is not enumerated.") : null,
        "validationErrors", List.of(),
        "recommendation", policyReason,
        "risk", unredactedRequested ? "high" : "medium",
        "noDirectMutation", true,
        "traceLinks", List.of(correlationId, traceId),
        "redaction", mapOf("browserSafe", true, "omittedFieldKeys", DEFAULT_OMITTED_FIELDS, "hiddenCountPolicy", "non-enumerating", "safeExplanation", "Export request stores scoped metadata only; raw evidence bodies, prompts, model outputs, provider/tool payloads, credentials, tokens, hidden policy internals, storage keys, download URLs, and cross-tenant/customer facts are omitted.", "traceRefs", List.of(traceId))));
  }

  public SurfaceData appendInvestigationNote(AuthContextResolver.ResolvedMe actor, Object input, String idempotencyKey, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), INVESTIGATION_NOTE_CAPABILITY);
    var scope = validateScope(actor, input, correlationId);
    var traceId = stringInput(input, "traceId", "trace-auth-context-" + stableSuffix(correlationId));
    if (!(input instanceof Map<?, ?>)) {
      authContextResolver.appendProtectedReadTrace(actor, INVESTIGATION_NOTE_CAPABILITY, "investigation note result refresh without append target", correlationId);
      return investigationNoteResult(actor, scope, correlationId, idempotencyKey, traceId, "not_found_or_redacted", "Investigation note result", "No note append target was provided or persisted for this direct refresh; no annotation was recorded and hidden targets were not enumerated.", "", null, null, List.of(mapOf("actionCategory", "append-note", "reason", "validation_required", "recoveryPath", "Submit a note from an authorized trace, timeline, failure, guidance, export, or summary context.", "auditOnlyDenialCategory", "missing_note_target")));
    }
    var note = stringInput(input, "note", null);
    if (note == null || note.isBlank() || note.length() > 500) {
      authContextResolver.appendDeniedTrace(actor, "AUDIT_TRACE_NOTE_VALIDATION", "note:Investigation note is required and must be at most 500 characters.", correlationId);
      return investigationNoteResult(actor, scope, correlationId, idempotencyKey, traceId, "validation-error", "Investigation note validation", "Investigation note is required and must be at most 500 characters; no annotation was recorded.", "", "note", "Investigation note is required and must be at most 500 characters.", List.of(mapOf("actionCategory", "append-note", "reason", "note_body_rejected", "recoveryPath", "Provide a concise browser-safe note and retry with a client-generated idempotency key.", "auditOnlyDenialCategory", "note_validation")));
    }
    if (traceId == null || traceId.isBlank() || traceId.length() > 160) {
      authContextResolver.appendDeniedTrace(actor, "AUDIT_TRACE_NOTE_VALIDATION", "traceId:Trace id is required and must be at most 160 characters.", correlationId);
      return investigationNoteResult(actor, scope, correlationId, idempotencyKey, "trace-redacted", "validation-error", "Investigation note validation", "Trace context is required and must be at most 160 characters; no annotation was recorded.", "", "traceId", "Trace id is required and must be at most 160 characters.", List.of(mapOf("actionCategory", "append-note", "reason", "missing_scope", "recoveryPath", "Open an authorized trace or timeline before appending a note.", "auditOnlyDenialCategory", "trace_validation")));
    }
    authContextResolver.appendProtectedReadTrace(actor, INVESTIGATION_NOTE_CAPABILITY, "append investigation note trace:" + traceId, correlationId);
    return investigationNoteResult(actor, scope, correlationId, idempotencyKey, traceId, "recorded", "Investigation note recorded", "Investigation note appended as an auditable, tenant-scoped annotation; source traces remain immutable.", redacted(note), null, null, List.of());
  }

  private SurfaceData investigationNoteResult(AuthContextResolver.ResolvedMe actor, Map<String, Object> scope, String correlationId, String idempotencyKey, String traceId, String status, String title, String message, String noteSummary, String validationField, String validationMessage, List<Map<String, Object>> disabledActions) {
    var traceRef = "trace-audit-note-" + stableSuffix(Objects.toString(idempotencyKey, correlationId));
    var recorded = "recorded".equals(status);
    var safeNoteHandle = recorded ? "note-" + stableSuffix(actor.selectedContext().tenantId() + ":" + actor.selectedContext().membershipId() + ":" + Objects.toString(idempotencyKey, correlationId)) : null;
    var noteResultStatus = status.replace('-', '_');
    var allowedActions = List.of(
        mapOf("actionId", "action-audit-trace-detail", "label", "Open source trace detail", "capabilityId", DETAIL_CAPABILITY, "targetSurfaceId", "surface-audit-trace-detail", "safeReason", "Reauthorizes source evidence before showing detail."),
        mapOf("actionId", "action-audit-trace-timeline", "label", "Open related timeline", "capabilityId", TIMELINE_CAPABILITY, "targetSurfaceId", "surface-audit-trace-timeline", "safeReason", "Shows authorized correlation events only."),
        mapOf("actionId", "action-audit-trace-failure-evidence", "label", "Open failure evidence", "capabilityId", FAILURE_EVIDENCE_CAPABILITY, "targetSurfaceId", "surface-audit-trace-failure-evidence", "safeReason", "Inspects authorized failure categories without raw provider/tool payloads."),
        mapOf("actionId", "action-audit-trace-investigation-guide", "label", "Open investigation guidance", "capabilityId", INVESTIGATION_GUIDE_CAPABILITY, "targetSurfaceId", "surface-audit-trace-investigation-guide", "safeReason", "Guidance is advisory and cannot alter note or evidence records."),
        mapOf("actionId", "action-audit-trace-request-redacted-export", "label", "Request redacted export", "capabilityId", EXPORT_REQUEST_CAPABILITY, "targetSurfaceId", "surface-audit-trace-export-request", "approvalRequirement", "policy-gated", "safeReason", "Exports remain redacted and policy-gated."),
        mapOf("actionId", "action-audit-trace-search", "label", "Return to scoped search", "capabilityId", SEARCH_CAPABILITY, "targetSurfaceId", "surface-audit-trace-search", "safeReason", "Reruns backend-scoped search instead of trusting browser state."),
        mapOf("actionId", "action-audit-trace-dashboard", "label", "Return to investigation command center", "capabilityId", DASHBOARD_CAPABILITY, "targetSurfaceId", "surface-audit-trace-dashboard", "safeReason", "Recomputes scoped dashboard counters server-side."));
    return new SurfaceData("surface-audit-trace-investigation-note", "system-message", title, List.of(traceRef), mapOf(
        "surfaceContract", "audit.trace.investigationNote.v1",
        "surfaceId", "surface-audit-trace-investigation-note",
        "generatedAt", Instant.now().toString(),
        "selectedScope", scope,
        "authContextSummary", mapOf("selectedContextId", actor.selectedContext().membershipId(), "actor", actor.profile().displayName(), "roleLabels", actor.selectedContext().roles().stream().map(Enum::name).toList(), "capabilityCount", actor.selectedContext().capabilities().size()),
        "capabilityIds", List.of(INVESTIGATION_NOTE_CAPABILITY, DETAIL_CAPABILITY, TIMELINE_CAPABILITY, FAILURE_EVIDENCE_CAPABILITY, INVESTIGATION_GUIDE_CAPABILITY, EXPORT_REQUEST_CAPABILITY, SEARCH_CAPABILITY, DASHBOARD_CAPABILITY),
        "correlationId", correlationId,
        "traceRefs", List.of(traceRef),
        "readiness", recorded ? "ready" : status,
        "status", status,
        "message", message,
        "summary", message,
        "noteResult", mapOf("safeNoteHandle", safeNoteHandle, "status", noteResultStatus, "submitterLabel", actor.profile().displayName(), "recordedAt", recorded ? Instant.now().toString() : null, "sourceWorkstream", "Audit/Trace", "sourceSurfaceActionLabel", "action-audit-trace-append-investigation-note", "idempotencyReplayIndicator", idempotencyKey == null || idempotencyKey.isBlank() ? "none" : "client-generated-key-present-redacted", "resultSummary", message),
        "targetEvidence", mapOf("safeTraceRefLabel", traceId, "evidenceCategory", "audit-trace", "sourceWorkstream", "Audit/Trace", "sourceSurfaceLabel", "authorized trace context", "retentionStatus", "retained-redacted", "visibleStatus", recorded ? "annotated" : status, "immutableTargetStatement", "Source traces, policy, authorization, retained evidence, export decisions, summaries, and source workstream records are unchanged."),
        "authorizationBasis", mapOf("selectedContextId", actor.selectedContext().membershipId(), "visibleCapabilityIds", List.of(INVESTIGATION_NOTE_CAPABILITY), "customerScopeRestricted", actor.selectedContext().customerId() != null, "redactionExplanation", "Notes are scoped to the selected AuthContext; hidden or cross-scope targets are not enumerated."),
        "notePolicy", mapOf("maxLength", 500, "acceptedCategories", List.of("investigation-note"), "sanitization", "browser-safe preview redacts tokens, provider credentials, hidden prompts, and raw tool payloads", "retentionLabel", "audit-trace-annotation", "immutableAnnotation", true, "rejectionReason", validationMessage),
        "annotation", mapOf("sanitizedNotePreview", noteSummary, "authorDisplayLabel", actor.profile().displayName(), "noteCategory", "investigation-note", "retentionLabel", "audit-trace-annotation", "redactedFieldCategories", DEFAULT_OMITTED_FIELDS, "sourceUnchanged", true),
        "allowedActions", allowedActions,
        "disabledActions", disabledActions,
        "evidenceSummary", mapOf("redactedSupportingFacts", recorded ? "A browser-safe investigation note was recorded for the authorized trace context." : message, "visibleTraceLabel", traceId, "correlationLabel", correlationId, "relatedEventCategories", List.of("audit-trace", "investigation-note"), "omittedFieldCategories", DEFAULT_OMITTED_FIELDS, "nextUsefulEvidenceSurface", "surface-audit-trace-detail"),
        "recoverySteps", recorded ? List.of("Open the source trace detail to reauthorize evidence before reviewing more context.", "Open the related timeline or guidance for next steps.", "Request a redacted export only through the policy-gated export action when needed.") : List.of("Return to an authorized Audit/Trace surface.", "Open a visible trace, timeline, failure evidence, guidance, or export context.", "Submit a concise browser-safe note with a client-generated idempotency key."),
        "recovery", recorded ? "Continue through governed follow-up actions; this annotation did not mutate source evidence." : "Submit the note from an authorized target context; hidden targets are not enumerated.",
        "emptyState", recorded ? null : mapOf("status", status, "message", message, "recovery", "Open an authorized target before appending a note."),
        "validationErrors", validationField == null ? List.of() : List.of(mapOf("field", validationField, "message", validationMessage)),
        "noteSummary", noteSummary,
        "traceId", traceId,
        "retainedAuthority", "Human-authored investigation notes annotate traces only; they do not mutate source traces, policy, authorization, or retained evidence.",
        "redaction", mapOf("browserSafe", true, "omittedFieldKeys", DEFAULT_OMITTED_FIELDS, "hiddenCountPolicy", "non-enumerating", "safeExplanation", "Raw note bodies before sanitization, raw prompts, provider/model/tool payloads, credentials, bearer tokens, hidden ids, and cross-scope evidence are omitted.", "traceRefs", List.of(traceRef)),
        "redactionMetadata", mapOf("omittedFieldKeys", DEFAULT_OMITTED_FIELDS, "nonEnumerating", !recorded),
        "noDirectMutation", true,
        "safety", mapOf("redactionNote", "Provider secrets, raw JWTs, hidden prompts, invitation tokens, raw note input, and unauthorized tenant/customer evidence are not shown.")));
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
