package ai.first.application.coreapp.agentadmin;

import ai.first.domain.foundation.agent.ModelConfigRef;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.AuthorizationException;
import ai.first.domain.foundation.agent.AgentDefinition;
import ai.first.domain.foundation.agent.AgentLifecycleStatus;
import ai.first.domain.foundation.agent.AgentReferenceManifest;
import ai.first.domain.foundation.agent.AgentSkillManifest;
import ai.first.domain.foundation.agent.PromptDocument;
import ai.first.domain.foundation.agent.ReferenceDocument;
import ai.first.domain.foundation.agent.SeedProvenance;
import ai.first.domain.foundation.agent.SkillDocument;
import ai.first.domain.foundation.agent.ToolPermissionBoundary;
import ai.first.domain.foundation.identity.FoundationRole;
import ai.first.domain.foundation.identity.ScopeType;
import ai.first.application.foundation.workstream.WorkstreamEventPublisher;
import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import ai.first.application.foundation.agent.AgentBehaviorRepository;
import ai.first.application.foundation.agent.AgentBehaviorSeedLoader;

/** Deterministic Agent Admin read facade for browser-safe, tenant-scoped managed-agent artifacts. */
public final class AgentAdminService {
  public static final String LIST_DEFINITIONS = "agent_admin.list_definitions";
  public static final String GET_DEFINITION = "agent_admin.get_definition";
  public static final String GET_PROMPT_VERSION = "agent_admin.get_prompt_version";
  public static final String GET_SKILL_VERSION = "agent_admin.get_skill_version";
  public static final String GET_REFERENCE_VERSION = "agent_admin.get_reference_version";
  public static final String GET_MANIFEST = "agent_admin.get_manifest";
  public static final String GET_MODEL_REF = "agent_admin.get_model_ref";
  public static final String GET_TOOL_BOUNDARY = "agent_admin.get_tool_boundary";
  public static final String LIST_SEED_MATERIAL = "agent_admin.list_seed_material";

  private static final int PREVIEW_CHARS = 220;

  private final AgentBehaviorRepository repository;
  private final AuthContextResolver authContextResolver;

  public AgentAdminService(AgentBehaviorRepository repository, AuthContextResolver authContextResolver) {
    this.repository = Objects.requireNonNull(repository);
    this.authContextResolver = Objects.requireNonNull(authContextResolver);
  }

  public Map<String, Object> catalog(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return catalog(actor, Map.of(), correlationId);
  }

  public Map<String, Object> catalog(AuthContextResolver.ResolvedMe actor, Map<String, ?> input, String correlationId) {
    require(actor, LIST_DEFINITIONS, correlationId, "agent_admin.catalog.v1");
    var searchText = safeFilter(input, "query", safeFilter(input, "searchText", "")).trim();
    var lifecycleFilter = safeFilter(input, "lifecycle", "").trim().toLowerCase(Locale.ROOT);
    var readinessFilter = safeFilter(input, "readiness", "").trim().toLowerCase(Locale.ROOT);
    var providerFilter = safeFilter(input, "providerReadiness", "").trim().toLowerCase(Locale.ROOT);
    var authorityFilter = safeFilter(input, "authorityTier", "").trim().toLowerCase(Locale.ROOT);
    var allRows = repository.agentDefinitions(governanceScopeId(actor)).stream()
        .sorted(Comparator.comparing(AgentDefinition::displayName))
        .map(agent -> catalogRow(actor, agent, correlationId))
        .toList();
    var filteredRows = allRows.stream()
        .filter(row -> matches(row, searchText))
        .filter(row -> lifecycleFilter.isBlank() || lifecycleFilter.equals(String.valueOf(row.get("lifecycleState"))))
        .filter(row -> readinessFilter.isBlank() || readinessFilter.equals(String.valueOf(row.get("readinessState"))))
        .filter(row -> providerFilter.isBlank() || providerFilter.equals(String.valueOf(row.get("providerModelReadinessCategory"))))
        .filter(row -> authorityFilter.isBlank() || authorityFilter.equals(String.valueOf(row.get("authorityTier")).toLowerCase(Locale.ROOT)))
        .toList();
    var traceId = traceId("catalog", governanceScopeId(actor), correlationId);
    var providerSummary = providerReadinessSummary(actor);
    var readinessCounts = counts(allRows, "readinessState");
    var lifecycleCounts = counts(allRows, "lifecycleState");
    var providerCounts = counts(allRows, "providerModelReadinessCategory");
    var filters = mapOf(
        "searchText", searchText,
        "lifecycle", lifecycleFilter,
        "readiness", readinessFilter,
        "authorityTier", authorityFilter,
        "providerReadiness", providerFilter,
        "seedCustomization", safeFilter(input, "seedCustomization", ""),
        "sortKey", "displayName",
        "sortDirection", "asc",
        "pageCursor", safeFilter(input, "pageCursor", ""),
        "pageSize", 25,
        "backendAuthoritative", true);
    var emptyReason = allRows.isEmpty() ? "empty-no-agents" : filteredRows.isEmpty() ? "empty-no-filter-matches" : null;
    return mapOf(
        "surfaceContract", "agent_admin.catalog.v1",
        "surfaceContractAliases", List.of("surface.agent_admin.catalog.v1"),
        "catalogSummary", mapOf("surfaceId", "surface-agent-admin-catalog", "title", "Managed agent catalog", "type", "list-search", "contract", "agent_admin.catalog.v1", "selectedScopeLabel", scopeLabel(actor), "resultCount", filteredRows.size(), "filteredCount", filteredRows.size(), "totalVisibleCount", allRows.size(), "readinessCounts", readinessCounts, "lifecycleCounts", lifecycleCounts, "providerReadinessCounts", providerCounts, "seedCustomizationSummary", seedCustomizationSummary(allRows), "lastRefreshedAt", Instant.now().toString(), "emptyStateReason", emptyReason),
        "scopeSummary", mapOf("selectedAuthContextId", actor.selectedContext().membershipId(), "scopeType", actor.selectedContext().scopeType().name().toLowerCase(Locale.ROOT), "tenantDisplayName", governanceScopeId(actor), "organizationDisplayName", governanceScopeId(actor), "actorRoleSummary", actor.selectedContext().roles().stream().map(Enum::name).toList(), "governanceAuthorized", true),
        "filters", filters,
        "query", searchText.isBlank() ? "tenant:" + governanceScopeId(actor) : searchText,
        "rows", filteredRows,
        "agents", filteredRows,
        "emptyState", mapOf("state", emptyReason == null ? "ready" : emptyReason, "reason", emptyReason == null ? "Authorized managed agents are visible." : (allRows.isEmpty() ? "No governed AgentDefinition records are seeded for this selected scope." : "No visible managed agents match the backend-validated filters."), "recoveryActions", emptyReason == null ? List.of() : List.of("action-agent-admin-reset-catalog-filters", "action-agent-admin-refresh-catalog")),
        "pageInfo", mapOf("totalKnownCount", allRows.size(), "visibleCount", filteredRows.size(), "pageSize", 25, "nextCursor", null),
        "capabilityIds", List.of(LIST_DEFINITIONS, GET_DEFINITION, GET_PROMPT_VERSION, GET_SKILL_VERSION, GET_REFERENCE_VERSION, GET_MANIFEST, GET_MODEL_REF, GET_TOOL_BOUNDARY, LIST_SEED_MATERIAL),
        "providerReadiness", providerSummary,
        "seedMaterial", seedMaterial(actor, correlationId),
        "safeRedactionSummary", mapOf("prompts", "omitted", "skills", "omitted", "references", "omitted", "providerCredentials", "omitted", "modelInternals", "omitted", "hiddenScopes", "omitted", "rawTraceEvidence", "role-gated", "privilegedPolicyDiagnostics", "role-gated"),
        "redaction", redactionMetadata(),
        "traceLinks", List.of(traceId),
        "diagnostics", mapOf("capabilityIds", List.of(LIST_DEFINITIONS, GET_DEFINITION), "correlationId", correlationId, "traceIds", List.of(traceId), "filterRequest", filters, "redactionProfile", "agent-admin-catalog-browser-safe"),
        "systemStates", List.of("loading", "ready", "empty-no-agents", "empty-no-filter-matches", "submitting/searching", "forbidden", "not-found-or-redacted", "stale/reconnect", "partial-data", "provider-fail-closed", "validation-error", "no-op", "failure"),
        "emptyCopy", "Empty when no governed AgentDefinition records are seeded or match the backend-validated filters.");
  }

  public Map<String, Object> definitionDetail(AuthContextResolver.ResolvedMe actor, String agentDefinitionId, String correlationId) {
    require(actor, GET_DEFINITION, correlationId, "agent_admin.detail.v1");
    var requestedAgentId = firstNonBlank(agentDefinitionId, AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID);
    var maybeAgent = repository.agentDefinition(governanceScopeId(actor), requestedAgentId);
    if (maybeAgent.isEmpty()) return hiddenOrStaleDetail(actor, requestedAgentId, correlationId);

    var agent = maybeAgent.orElseThrow();
    var prompt = repository.promptDocument(governanceScopeId(actor), agent.promptDocumentId()).orElse(null);
    var skillManifest = repository.skillManifest(governanceScopeId(actor), agent.skillManifestId()).orElse(null);
    var referenceManifest = repository.referenceManifest(governanceScopeId(actor), agent.referenceManifestId()).orElse(null);
    var boundary = repository.toolBoundary(governanceScopeId(actor), agent.toolBoundaryId()).orElse(null);
    var model = repository.modelConfigRef(governanceScopeId(actor), agent.modelConfigRefId()).orElse(null);
    var provider = providerReadiness(actor, agent);
    var providerStatus = String.valueOf(provider.get("status"));
    var readinessState = "ready".equals(providerStatus) && agent.status() == AgentLifecycleStatus.ACTIVE ? "ready" : "blocked_provider_or_runtime";
    var traceId = traceId("definition", agent.agentDefinitionId(), correlationId);
    var fields = List.of(
        field("status", "Status", agent.status().name().toLowerCase(Locale.ROOT), false, null),
        field("authorityLevel", "Authority tier", agent.authorityLevel().name(), false, null),
        field("promptDocumentId", "Prompt", agent.promptDocumentId() + "@" + agent.activePromptVersion(), false, null),
        field("skillManifestId", "Skill manifest", agent.skillManifestId() + "@" + agent.activeSkillManifestVersion(), false, null),
        field("referenceManifestId", "Reference manifest", agent.referenceManifestId() + "@" + agent.activeReferenceManifestVersion(), false, null),
        field("toolBoundaryId", "Tool boundary", agent.toolBoundaryId() + "@" + agent.activeToolBoundaryVersion(), false, null),
        field("modelConfigRef", "Model ref", agent.modelConfigRefId(), false, "Provider credential values are never browser-visible"));
    var relatedArtifacts = List.of(
        artifactRef("prompt", agent.promptDocumentId(), prompt == null ? null : prompt.status().name(), GET_PROMPT_VERSION),
        artifactRef("skill_manifest", agent.skillManifestId(), skillManifest == null ? null : skillManifest.status().name(), GET_MANIFEST),
        artifactRef("reference_manifest", agent.referenceManifestId(), referenceManifest == null ? null : referenceManifest.status().name(), GET_MANIFEST),
        artifactRef("tool_boundary", agent.toolBoundaryId(), boundary == null ? null : boundary.status().name(), GET_TOOL_BOUNDARY),
        artifactRef("model_ref", agent.modelConfigRefId(), model == null ? null : model.status().name(), GET_MODEL_REF));
    var artifactCards = List.of(
        behaviorArtifactCard("prompt", "Prompt", agent.promptDocumentId(), agent.activePromptVersion(), prompt == null ? "missing" : prompt.status().name().toLowerCase(Locale.ROOT), "Redacted prompt/version diff; raw prompt text omitted.", "action-agent-detail-open-prompt-governance", "surface-agent-prompt-governance", GET_PROMPT_VERSION),
        behaviorArtifactCard("skill_manifest", "Skill manifest", agent.skillManifestId(), agent.activeSkillManifestVersion(), skillManifest == null ? "missing" : skillManifest.status().name().toLowerCase(Locale.ROOT), "Compact skill manifest review; full skill bodies omitted.", "action-agent-detail-open-skill-manifest", "surface-agent-skill-manifest-diff", GET_MANIFEST),
        behaviorArtifactCard("reference_manifest", "Reference bundle", agent.referenceManifestId(), agent.activeReferenceManifestVersion(), referenceManifest == null ? "missing" : referenceManifest.status().name().toLowerCase(Locale.ROOT), "Reference manifest health with raw evidence bodies omitted.", "action-agent-detail-open-skill-manifest", "surface-agent-skill-manifest-diff", GET_MANIFEST),
        behaviorArtifactCard("tool_boundary", "Governed tool boundary", agent.toolBoundaryId(), agent.activeToolBoundaryVersion(), boundary == null ? "missing" : boundary.status().name().toLowerCase(Locale.ROOT), "ToolPermissionBoundary grants and denial semantics remain backend-authoritative.", "action-agent-detail-open-tool-boundary", "surface-agent-tool-boundary-diff", GET_TOOL_BOUNDARY),
        behaviorArtifactCard("model_ref", "Model reference", agent.modelConfigRefId(), null, model == null ? "missing" : model.status().name().toLowerCase(Locale.ROOT), "Provider/model readiness is shown without credentials or raw provider errors.", "action-agent-detail-open-model-refs", "surface-agent-model-refs", GET_MODEL_REF));
    var taskEntryPoints = List.of(
        taskEntryPoint("action-agent-detail-refresh", "Refresh read-only detail", "surface-agent-admin-detail", GET_DEFINITION, true, "Read-only refresh; no mutation."),
        taskEntryPoint("action-agent-detail-open-prompt-governance", "Review prompt governance", "surface-agent-prompt-governance", GET_PROMPT_VERSION, true, "Raw prompt text stays omitted."),
        taskEntryPoint("action-agent-detail-open-skill-manifest", "Review skill/reference manifest", "surface-agent-skill-manifest-diff", GET_MANIFEST, true, "Compact manifests only."),
        taskEntryPoint("action-agent-detail-open-tool-boundary", "Simulate tool boundary", "surface-agent-tool-boundary-diff", GET_TOOL_BOUNDARY, true, "ToolPermissionBoundary denials preserved."),
        taskEntryPoint("action-agent-detail-open-model-refs", "Inspect model refs", "surface-agent-model-refs", GET_MODEL_REF, true, "Provider credentials redacted."),
        taskEntryPoint("action-agent-detail-run-test", "Run no-side-effect runtime test", "surface-agent-test-console", "agent_admin.draft_behavior_change", true, "Advisory test cannot activate behavior."),
        taskEntryPoint("action-agent-detail-open-prompt-risk-review", "Open prompt-risk review", "surface-agent-admin-prompt-risk-review", "agent_admin.prompt_risk_review.read", true, "Model-backed review fails closed when provider/runtime is unavailable."),
        taskEntryPoint("action-agent-detail-open-activation", "Open activation confirmation", "surface-agent-activation-confirmation", "activate-agent-behavior-version", "ready".equals(readinessState), "Separate approval/provider prerequisites required."),
        taskEntryPoint("action-agent-detail-open-deactivation", "Open deactivation confirmation", "surface-agent-deactivation-confirmation", "deactivate-agent-behavior-version", true, "Separate consequential confirmation required."),
        taskEntryPoint("action-agent-detail-open-rollback", "Open rollback confirmation", "surface-agent-rollback-confirmation", "rollback-agent-behavior-version", false, "Requires backend-visible activated proposal metadata."),
        taskEntryPoint("action-agent-detail-open-trace", "Open redacted trace", "surface-agent-admin-trace", "audit.trace.read", true, "Raw trace evidence remains role-gated."),
        taskEntryPoint("action-agent-detail-back-to-catalog", "Back to catalog", "surface-agent-admin-catalog", LIST_DEFINITIONS, true, "Catalog filters remain backend-validated."));
    return mapOf(
        "surfaceContract", "agent_admin.detail.v1",
        "surfaceContractAliases", List.of("surface.agent_admin.detail.v1", "agent_admin.definition.v1", "surface.agent_admin.definition_detail.v1"),
        "recordId", agent.agentDefinitionId(),
        "recordLabel", agent.displayName(),
        "recordKind", "AgentDefinition",
        "summary", "Backend-authoritative managed-agent readiness inspection; behavior and lifecycle changes must use separate governed tasks.",
        "detailSummary", mapOf("surfaceId", "surface-agent-admin-detail", "title", "Agent readiness/behavior inspection", "type", "show-inspection", "contract", "agent_admin.detail.v1", "selectedManagedAgentDisplayName", agent.displayName(), "shortPurpose", safe(agent.description()), "lifecycleState", agent.status().name().toLowerCase(Locale.ROOT), "readinessState", readinessState, "authorityTier", agent.authorityLevel().name(), "owningScopeLabel", scopeLabel(actor), "lastChangedAt", agent.updatedAt() == null ? null : agent.updatedAt().toString(), "lastReviewedAt", agent.updatedAt() == null ? null : agent.updatedAt().toString(), "lastRefreshedAt", Instant.now().toString(), "readOnlyNotice", "No inline mutation; use dedicated governed task surfaces."),
        "scopeSummary", mapOf("selectedAuthContextId", actor.selectedContext().membershipId(), "scopeType", actor.selectedContext().scopeType().name().toLowerCase(Locale.ROOT), "tenantDisplayName", governanceScopeId(actor), "organizationDisplayName", governanceScopeId(actor), "actorRoleSummary", actor.selectedContext().roles().stream().map(Enum::name).toList(), "governanceAuthorized", true, "visibilityDecision", "visible"),
        "readinessNarrative", mapOf("outcome", readinessState, "providerModelReadinessCategory", providerStatus, "promptRiskStatus", "ready".equals(providerStatus) ? "review-ready" : "deferred_until_provider_runtime_configured", "manifestToolBoundaryReferenceHealth", "active compact manifests and tool boundary are available when their artifact cards are active.", "seedCustomizationState", Boolean.TRUE.equals(seedStatus(agent.seedProvenance()).get("tenantCustomized")) ? "tenant-customized" : "starter-default", "blockedReasons", "ready".equals(readinessState) ? List.of() : List.of("Provider/model runtime is not fully ready; no fake success is shown."), "recoveryRouteLabels", List.of("Model references", "Prompt-risk review", "Trace"), "noFakeSuccess", !"ready".equals(providerStatus)),
        "fields", fields,
        "relatedArtifacts", relatedArtifacts,
        "behaviorArtifactCards", artifactCards,
        "taskEntryPoints", taskEntryPoints,
        "providerReadiness", provider,
        "seedStatus", seedStatus(agent.seedProvenance()),
        "permissionState", mapOf("canEdit", false, "reason", "Read-only inspection. Use inert behavior-change proposals, lifecycle confirmations, no-side-effect tests, and trace drill-ins; no direct mutation from this view.", "authoritativeCapabilityId", GET_DEFINITION),
        "safeRedactionSummary", mapOf("rawPromptText", "omitted", "rawSkillReferenceBodies", "omitted", "providerCredentials", "omitted", "hiddenTenantCustomerIdentifiers", "omitted", "rawLoaderToolInputs", "omitted", "jwtSessionMaterial", "omitted", "internalStackTraces", "omitted", "fullEvidenceDocuments", "role-gated"),
        "redaction", redactionMetadata(),
        "traceLinks", List.of(traceId),
        "audit", mapOf("lastEventType", "AgentDefinitionDetailDisplayed", "selectedManagedAgentVisibilityDecision", "visible", "traceIds", List.of(traceId), "correlationId", correlationId, "redactionProfile", "agent-admin-detail-browser-safe"),
        "diagnostics", mapOf("agentDefinitionId", agent.agentDefinitionId(), "promptVersionId", agent.promptDocumentId() + "@" + agent.activePromptVersion(), "manifestVersionIds", List.of(agent.skillManifestId() + "@" + agent.activeSkillManifestVersion(), agent.referenceManifestId() + "@" + agent.activeReferenceManifestVersion()), "toolBoundaryVersionId", agent.toolBoundaryId() + "@" + agent.activeToolBoundaryVersion(), "modelConfigRefId", agent.modelConfigRefId(), "capabilityIds", List.of(GET_DEFINITION, GET_PROMPT_VERSION, GET_MANIFEST, GET_MODEL_REF, GET_TOOL_BOUNDARY), "traceIds", List.of(traceId), "correlationId", correlationId, "rowContextHash", traceId("row-context", agent.agentDefinitionId(), correlationId), "redactionProfile", "agent-admin-detail-browser-safe"),
        "actionContext", mapOf("agentDefinitionId", agent.agentDefinitionId(), "rowContextHash", traceId("row-context", agent.agentDefinitionId(), correlationId)),
        "systemStates", List.of("loading", "ready", "empty-hidden-or-stale-selection", "submitting/refreshing", "forbidden", "not-found-or-redacted", "stale/reconnect", "partial-data", "provider-fail-closed", "approval-required", "conflict", "validation-error", "no-op", "failure"),
        "noDirectMutation", true);
  }

  private Map<String, Object> hiddenOrStaleDetail(AuthContextResolver.ResolvedMe actor, String requestedAgentId, String correlationId) {
    var traceId = traceId("definition-denied", requestedAgentId, correlationId);
    return mapOf(
        "surfaceContract", "agent_admin.detail.v1",
        "surfaceContractAliases", List.of("surface.agent_admin.detail.v1"),
        "recordKind", "AgentDefinition",
        "summary", "The selected managed agent is unavailable, hidden, stale, inactive, or outside the selected governance scope.",
        "detailSummary", mapOf("surfaceId", "surface-agent-admin-detail", "title", "Agent readiness/behavior inspection", "type", "show-inspection", "contract", "agent_admin.detail.v1", "readinessState", "empty-hidden-or-stale-selection", "owningScopeLabel", scopeLabel(actor), "lastRefreshedAt", Instant.now().toString(), "readOnlyNotice", "No inline mutation; choose a backend-authorized catalog row."),
        "scopeSummary", mapOf("selectedAuthContextId", actor.selectedContext().membershipId(), "scopeType", actor.selectedContext().scopeType().name().toLowerCase(Locale.ROOT), "tenantDisplayName", governanceScopeId(actor), "organizationDisplayName", governanceScopeId(actor), "actorRoleSummary", actor.selectedContext().roles().stream().map(Enum::name).toList(), "governanceAuthorized", true, "visibilityDecision", "not_found_or_redacted", "safeReason", "Selected managed-agent row is not visible in this AuthContext."),
        "readinessNarrative", mapOf("outcome", "not_found_or_redacted", "blockedReasons", List.of("Open a visible managed agent from the backend-authorized catalog."), "noFakeSuccess", true),
        "fields", List.of(),
        "relatedArtifacts", List.of(),
        "behaviorArtifactCards", List.of(),
        "taskEntryPoints", List.of(taskEntryPoint("action-agent-detail-back-to-catalog", "Back to catalog", "surface-agent-admin-catalog", LIST_DEFINITIONS, true, "Catalog filters remain backend-validated."), taskEntryPoint("action-agent-detail-open-trace", "Open redacted trace", "surface-agent-admin-trace", "audit.trace.read", true, "Raw trace evidence remains role-gated.")),
        "safeRedactionSummary", mapOf("hiddenTenantCustomerIdentifiers", "omitted", "rawPromptText", "omitted", "rawSkillReferenceBodies", "omitted", "providerCredentials", "omitted", "rawTraceEvidence", "role-gated"),
        "redaction", redactionMetadata(),
        "traceLinks", List.of(traceId),
        "audit", mapOf("lastEventType", "AgentDefinitionDetailDenied", "selectedManagedAgentVisibilityDecision", "not_found_or_redacted", "traceIds", List.of(traceId), "correlationId", correlationId, "redactionProfile", "agent-admin-detail-browser-safe"),
        "diagnostics", mapOf("rowContextHash", traceId("row-context", requestedAgentId, correlationId), "traceIds", List.of(traceId), "correlationId", correlationId, "redactionProfile", "agent-admin-detail-browser-safe"),
        "systemStates", List.of("empty-hidden-or-stale-selection", "not-found-or-redacted", "forbidden", "stale/reconnect", "failure"),
        "noDirectMutation", true);
  }

  private Map<String, Object> behaviorArtifactCard(String category, String label, String artifactId, Integer version, String status, String summary, String actionId, String targetSurfaceId, String capabilityId) {
    return mapOf(
        "artifactCategory", category,
        "displayLabel", label,
        "artifactId", artifactId,
        "currentVersionLabel", version == null ? "current" : "v" + version,
        "riskReadinessSummary", summary,
        "lastReviewStatus", status,
        "redactionNote", "Browser payload omits raw prompt, skill, reference, provider, loader-tool, hidden-scope, and full trace data.",
        "actionId", actionId,
        "targetSurfaceId", targetSurfaceId,
        "governedCapability", capabilityId,
        "disabledOrOmittedReason", null);
  }

  private Map<String, Object> taskEntryPoint(String actionId, String label, String targetSurfaceId, String capabilityId, boolean prerequisitesSatisfied, String reason) {
    return mapOf(
        "actionId", actionId,
        "label", label,
        "targetSurfaceId", targetSurfaceId,
        "governedCapability", capabilityId,
        "approvalProviderRuntimePrerequisitesSatisfied", prerequisitesSatisfied,
        "disabledOrOmittedReason", prerequisitesSatisfied ? null : reason,
        "safeReason", reason);
  }

  public Map<String, Object> promptDetail(AuthContextResolver.ResolvedMe actor, String agentDefinitionId, String correlationId) {
    require(actor, GET_PROMPT_VERSION, correlationId, "agent_admin.prompt_version.v1");
    var agent = agent(actor, firstNonBlank(agentDefinitionId, AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID));
    var prompt = repository.promptDocument(governanceScopeId(actor), agent.promptDocumentId()).orElseThrow(() -> new AuthorizationException(404, "TARGET_NOT_FOUND_OR_FORBIDDEN"));
    return mapOf(
        "surfaceContract", "agent_admin.prompt_version.v1",
        "surfaceContractAliases", List.of("surface.agent_admin.prompt_versions.v1"),
        "capabilityAliases", List.of("agent.prompts.govern"),
        "recordId", prompt.promptDocumentId(),
        "recordLabel", prompt.title() + "@" + prompt.activeVersion(),
        "recordKind", "PromptDocument",
        "status", prompt.status().name().toLowerCase(),
        "promptType", prompt.promptType(),
        "redactedPreview", preview(prompt.contentBody()),
        "fullContentAvailableInBrowser", false,
        "checksum", prompt.contentChecksum(),
        "changeSummary", prompt.changeSummary(),
        "seedStatus", seedStatus(prompt.seedProvenance()),
        "traceLinks", List.of(traceId("prompt", prompt.promptDocumentId(), correlationId)),
        "redaction", redactionMetadata(),
        "noDirectMutation", true);
  }

  public Map<String, Object> skillDetail(AuthContextResolver.ResolvedMe actor, String agentDefinitionId, String stableSkillId, String correlationId) {
    require(actor, GET_SKILL_VERSION, correlationId, "agent_admin.skill_version.v1");
    var agent = agent(actor, firstNonBlank(agentDefinitionId, AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID));
    var manifest = repository.skillManifest(governanceScopeId(actor), agent.skillManifestId()).orElseThrow(() -> new AuthorizationException(404, "TARGET_NOT_FOUND_OR_FORBIDDEN"));
    var entry = manifest.entries().stream()
        .filter(candidate -> stableSkillId == null || stableSkillId.isBlank() || candidate.stableSkillId().equals(stableSkillId))
        .findFirst()
        .orElseThrow(() -> new AuthorizationException(404, "TARGET_NOT_FOUND_OR_FORBIDDEN"));
    var skill = repository.skillDocument(governanceScopeId(actor), entry.skillDocumentId()).orElseThrow(() -> new AuthorizationException(404, "TARGET_NOT_FOUND_OR_FORBIDDEN"));
    return mapOf(
        "surfaceContract", "agent_admin.skill_version.v1",
        "surfaceContractAliases", List.of("surface.agent_admin.skill_versions.v1"),
        "capabilityAliases", List.of("agent.skills.govern", "agent.read_skill"),
        "recordId", skill.skillDocumentId(),
        "stableSkillId", skill.stableSkillId(),
        "recordLabel", skill.title() + "@" + skill.activeVersion(),
        "recordKind", "SkillDocument",
        "status", skill.status().name().toLowerCase(),
        "pinnedManifestVersion", entry.pinnedVersion(),
        "purpose", skill.purpose(),
        "whenToUse", skill.whenToUse(),
        "redactedPreview", preview(skill.contentBody()),
        "fullContentAvailableInBrowser", false,
        "checksum", skill.contentChecksum(),
        "seedStatus", seedStatus(skill.seedProvenance()),
        "readSkillRuntime", mapOf("toolId", "readSkill", "requiredManifestId", manifest.manifestId(), "assignedOnly", true, "traceType", "SkillLoadTrace"),
        "traceLinks", List.of(traceId("skill-version", skill.stableSkillId(), correlationId)),
        "redaction", redactionMetadata(),
        "noDirectMutation", true);
  }

  public Map<String, Object> manifestDetail(AuthContextResolver.ResolvedMe actor, String agentDefinitionId, String correlationId) {
    require(actor, GET_MANIFEST, correlationId, "agent_admin.manifest.v1");
    var agent = agent(actor, firstNonBlank(agentDefinitionId, AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID));
    var skills = repository.skillManifest(governanceScopeId(actor), agent.skillManifestId()).orElseThrow(() -> new AuthorizationException(404, "TARGET_NOT_FOUND_OR_FORBIDDEN"));
    var references = repository.referenceManifest(governanceScopeId(actor), agent.referenceManifestId()).orElseThrow(() -> new AuthorizationException(404, "TARGET_NOT_FOUND_OR_FORBIDDEN"));
    return mapOf(
        "surfaceContract", "agent_admin.manifest.v1",
        "surfaceContractAliases", List.of("surface.agent_admin.manifest_detail.v1"),
        "capabilityAliases", List.of("agent.manifests.manage", "agent.skills.govern"),
        "recordId", skills.manifestId() + ":" + references.manifestId(),
        "recordKind", "AgentSkillManifest+AgentReferenceManifest",
        "skillManifest", mapOf("manifestId", skills.manifestId(), "status", skills.status().name().toLowerCase(), "version", skills.manifestVersion(), "checksum", skills.compactManifestChecksum(), "entries", skillEntries(actor, skills), "seedStatus", seedStatus(skills.seedProvenance())),
        "referenceManifest", mapOf("manifestId", references.manifestId(), "status", references.status().name().toLowerCase(), "version", references.manifestVersion(), "checksum", references.compactManifestChecksum(), "entries", referenceEntries(actor, references), "seedStatus", seedStatus(references.seedProvenance())),
        "traceLinks", List.of(traceId("manifest", skills.manifestId(), correlationId), traceId("manifest", references.manifestId(), correlationId)),
        "redaction", redactionMetadata(),
        "noDirectMutation", true);
  }

  public Map<String, Object> toolBoundaryDetail(AuthContextResolver.ResolvedMe actor, String agentDefinitionId, String correlationId) {
    require(actor, GET_TOOL_BOUNDARY, correlationId, "agent_admin.tool_boundary.v1");
    var agent = agent(actor, firstNonBlank(agentDefinitionId, AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID));
    var boundary = repository.toolBoundary(governanceScopeId(actor), agent.toolBoundaryId()).orElseThrow(() -> new AuthorizationException(404, "TARGET_NOT_FOUND_OR_FORBIDDEN"));
    return mapOf(
        "surfaceContract", "agent_admin.tool_boundary.v1",
        "surfaceContractAliases", List.of("surface.agent_admin.tool_boundary.v1"),
        "capabilityAliases", List.of("agent.tool_boundaries.manage"),
        "recordId", boundary.boundaryId(),
        "recordKind", "ToolPermissionBoundary",
        "status", boundary.status().name().toLowerCase(),
        "version", boundary.boundaryVersion(),
        "grants", boundary.allowedToolGrants().stream().map(this::toolGrant).toList(),
        "checksum", boundary.checksum(),
        "seedStatus", seedStatus(boundary.seedProvenance()),
        "traceLinks", List.of(traceId("tool-boundary", boundary.boundaryId(), correlationId)),
        "redaction", redactionMetadata(),
        "noDirectMutation", true);
  }

  public Map<String, Object> modelRefDetail(AuthContextResolver.ResolvedMe actor, String agentDefinitionId, String correlationId) {
    require(actor, GET_MODEL_REF, correlationId, "agent_admin.model_refs.v1");
    var agent = agent(actor, firstNonBlank(agentDefinitionId, AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID));
    var model = repository.modelConfigRef(governanceScopeId(actor), agent.modelConfigRefId()).orElseThrow(() -> new AuthorizationException(404, "TARGET_NOT_FOUND_OR_FORBIDDEN"));
    return mapOf(
        "surfaceContract", "agent_admin.model_refs.v1",
        "surfaceContractAliases", List.of("agent_admin.model_ref.v1", "surface.agent_admin.model_ref.v1", "surface.agent_admin.model_refs.v1"),
        "recordId", model.modelConfigRefId(),
        "recordKind", "ModelConfigRef",
        "displayName", model.displayName(),
        "providerAlias", safe(model.providerAlias()),
        "providerCredential", "[REDACTED]",
        "status", model.status().name().toLowerCase(),
        "allowedModes", model.allowedModes(),
        "allowedCapabilityIds", model.allowedCapabilityIds(),
        "allowedAuthorityLevels", model.allowedAuthorityLevels().stream().map(Enum::name).toList(),
        "seedStatus", seedStatus(model.seedProvenance()),
        "readiness", providerReadiness(actor, agent),
        "traceLinks", List.of(traceId("model-ref", model.modelConfigRefId(), correlationId)),
        "redaction", redactionMetadata(),
        "noDirectMutation", true);
  }

  public Map<String, Object> seedMaterialDetail(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return seedMaterialDetail(actor, null, correlationId);
  }

  public Map<String, Object> seedMaterialDetail(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    require(actor, LIST_SEED_MATERIAL, correlationId, "agent_admin.seed_material.v1");
    var searchText = safeInput(input, "query", safeInput(input, "searchText", "")).trim();
    var sourceCategory = safeInput(input, "sourceCategory", "").trim().toLowerCase(Locale.ROOT);
    var readinessState = safeInput(input, "readinessState", safeInput(input, "readiness", "")).trim().toLowerCase(Locale.ROOT);
    var importStatus = safeInput(input, "importStatus", "").trim().toLowerCase(Locale.ROOT);
    var selectedSeedId = safeInput(input, "seedMaterialId", safeInput(input, "artifactId", "")).trim();
    var targetAgentId = safeInput(input, "targetAgentDefinitionId", safeInput(input, "agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID)).trim();
    var workflowStatus = safeInput(input, "workflowStatus", "").trim().toLowerCase(Locale.ROOT);
    var allRows = seedMaterial(actor, correlationId);
    var filteredRows = allRows.stream()
        .filter(row -> matches(row, searchText))
        .filter(row -> sourceCategory.isBlank() || sourceCategory.equals(String.valueOf(row.get("sourceCategory")).toLowerCase(Locale.ROOT)))
        .filter(row -> readinessState.isBlank() || readinessState.equals(String.valueOf(row.get("readinessState")).toLowerCase(Locale.ROOT)))
        .filter(row -> importStatus.isBlank() || importStatus.equals(String.valueOf(row.get("importStatus")).toLowerCase(Locale.ROOT)))
        .toList();
    var selectedRow = filteredRows.stream()
        .filter(row -> selectedSeedId.isBlank() || selectedSeedId.equals(row.get("id")) || selectedSeedId.equals(row.get("artifactId")))
        .findFirst()
        .orElse(filteredRows.isEmpty() ? null : filteredRows.get(0));
    var traceId = traceId("seed-material", governanceScopeId(actor), correlationId);
    var filters = mapOf(
        "searchText", searchText,
        "sourceCategory", sourceCategory,
        "agentFamily", safeInput(input, "agentFamily", ""),
        "readinessState", readinessState,
        "compatibilityState", safeInput(input, "compatibilityState", ""),
        "customizationPreservationState", safeInput(input, "customizationPreservationState", ""),
        "importStatus", importStatus,
        "sortKey", "artifactKind",
        "sortDirection", "asc",
        "pageCursor", safeInput(input, "pageCursor", ""),
        "pageSize", 25,
        "backendAuthoritative", true);
    var customizedCount = allRows.stream().filter(row -> Boolean.TRUE.equals(row.get("tenantCustomized"))).count();
    var emptyReason = allRows.isEmpty() ? "empty-no-seed-material" : filteredRows.isEmpty() ? "empty-no-filter-matches" : null;
    return mapOf(
        "surfaceContract", "agent_admin.seed_material.v1",
        "surfaceContractAliases", List.of("surface.agent_admin.seed_material.v1", "surface.agent_admin.seed_import.v1"),
        "seedMaterialSummary", mapOf("surfaceId", "surface-agent-seed-material", "title", "Seed material discovery and import workflow", "type", "list-search / workflow-status", "contract", "agent_admin.seed_material.v1", "selectedScopeLabel", scopeLabel(actor), "resultCount", filteredRows.size(), "filteredCount", filteredRows.size(), "totalVisibleCount", allRows.size(), "importReadinessCategory", "ready", "customizationPreservationStatus", customizedCount == 0 ? "starter-defaults-visible" : "tenant-customizations-preserved", "providerRuntimeReadinessCategory", "not-required-for-seed-discovery", "lastRefreshedAt", Instant.now().toString(), "emptyStateReason", emptyReason),
        "scopeSummary", mapOf("selectedAuthContextId", actor.selectedContext().membershipId(), "scopeType", actor.selectedContext().scopeType().name().toLowerCase(Locale.ROOT), "tenantDisplayName", governanceScopeId(actor), "organizationDisplayName", governanceScopeId(actor), "actorRoleSummary", actor.selectedContext().roles().stream().map(Enum::name).toList(), "governanceAuthorized", true, "visibilityDecision", "visible"),
        "filters", filters,
        "query", searchText.isBlank() ? mapOf("tenantScoped", true, "scope", governanceScopeId(actor)) : searchText,
        "rows", filteredRows,
        "seedRows", filteredRows,
        "emptyState", mapOf("state", emptyReason == null ? "ready" : emptyReason, "reason", emptyReason == null ? "Authorized seed material is visible." : (allRows.isEmpty() ? "No governed seed material is visible for this selected scope." : "No visible seed material matches the backend-validated filters."), "recoveryActions", emptyReason == null ? List.of() : List.of("action-agent-seed-material-reset-filters", "action-agent-seed-material-refresh")),
        "pageInfo", mapOf("totalKnownCount", allRows.size(), "visibleCount", filteredRows.size(), "pageSize", 25, "nextCursor", null),
        "provenanceInspection", provenanceInspection(selectedRow, correlationId),
        "importWorkflow", seedImportWorkflow(workflowStatus.isBlank() ? "not-started" : workflowStatus, selectedRow, targetAgentId, correlationId),
        "customizationPreservation", mapOf("tenantCustomizationsPreservedCount", customizedCount, "starterDefaultCount", allRows.size() - customizedCount, "preservedCategories", List.of("approvals", "deactivations", "prompt_overrides", "skill_manifests", "tool_boundaries", "model_refs", "trace_links"), "skippedOrRequiresHumanConfirmation", List.of("raw tenant override bodies", "hidden scope identifiers"), "rawOverrideContentVisible", false, "noDestructiveDelete", true),
        "authorizedActions", List.of(
            seedAction("action-agent-seed-material-refresh", "Refresh seed material", LIST_SEED_MATERIAL, "surface-agent-seed-material", true, null),
            seedAction("action-agent-seed-material-search", "Search seed material", LIST_SEED_MATERIAL, "surface-agent-seed-material", true, null),
            seedAction("action-agent-seed-material-reset-filters", "Reset seed filters", LIST_SEED_MATERIAL, "surface-agent-seed-material", true, null),
            seedAction("action-agent-seed-material-open-provenance", "Open seed provenance", LIST_SEED_MATERIAL, "surface-agent-seed-material", selectedRow != null, selectedRow == null ? "Select a visible seed row." : null),
            seedAction("action-agent-seed-material-prepare-import", "Prepare customization-preserving import", LIST_SEED_MATERIAL, "surface-agent-seed-material", selectedRow != null, selectedRow == null ? "Select a visible seed row." : null),
            seedAction("action-agent-seed-material-start-import", "Start import after acknowledgement", "agent_admin.reseed_missing_defaults", "surface-agent-seed-material", selectedRow != null, selectedRow == null ? "Select a visible seed row." : null),
            seedAction("action-agent-seed-material-cancel-import", "Cancel visible import task", "agent_admin.reseed_missing_defaults", "surface-agent-seed-material", true, null),
            seedAction("action-agent-seed-material-open-agent-detail", "Open target managed agent", GET_DEFINITION, "surface-agent-admin-detail", true, null),
            seedAction("action-agent-seed-material-open-trace", "Open seed trace", "audit.trace.read", "surface-agent-admin-trace", true, null),
            seedAction("action-agent-seed-material-back-to-source", "Back to source surface", LIST_DEFINITIONS, "surface-agent-admin-catalog", true, null)),
        "safeRedactionSummary", mapOf("rawSeedPackageContents", "omitted", "prompts", "omitted", "skills", "omitted", "referenceDocuments", "omitted", "tenantOverrideBodies", "omitted", "providerCredentials", "omitted", "modelInternals", "omitted", "hiddenScopes", "omitted", "rawTraceEvidence", "role-gated", "loaderToolInputs", "omitted", "bearerTokens", "omitted", "internalStackTraces", "omitted"),
        "redaction", redactionMetadata(),
        "traceLinks", List.of(traceId),
        "audit", mapOf("lastEventType", "AgentSeedMaterialListed", "visibilityDecision", "visible", "traceIds", List.of(traceId), "correlationId", correlationId, "redactionProfile", "agent-seed-material-browser-safe"),
        "diagnostics", mapOf("selectedSeedMaterialId", selectedRow == null ? null : selectedRow.get("id"), "targetManagedAgentId", targetAgentId, "capabilityIds", List.of(LIST_SEED_MATERIAL, "agent_admin.reseed_missing_defaults", GET_DEFINITION, "audit.trace.read"), "traceIds", List.of(traceId), "correlationId", correlationId, "redactionProfile", "agent-seed-material-browser-safe"),
        "systemStates", List.of("loading", "ready", "empty-no-seed-material", "empty-no-filter-matches", "ready-provenance", "prepared-import", "queued", "running", "completed", "cancelled", "blocked-provider-or-runtime", "blocked-customization-conflict", "submitting/searching/preparing/importing/cancelling", "validation-error", "forbidden", "not-found-or-redacted", "stale/reconnect", "partial-data", "conflict", "no-op", "failure"),
        "noDirectMutation", true,
        "noDirectActivation", true,
        "noDestructiveDelete", true);
  }

  private void require(AuthContextResolver.ResolvedMe actor, String capabilityId, String correlationId, String surfaceContract) {
    requireAgentAdminOperator(actor);
    authContextResolver.requireCapability(actor.selectedContext(), capabilityId);
    authContextResolver.appendProtectedReadTrace(actor, capabilityId, surfaceContract, correlationId);
  }

  private void requireAgentAdminOperator(AuthContextResolver.ResolvedMe actor) {
    var context = actor.selectedContext();
    if (context.scopeType() == ScopeType.SAAS_OWNER && context.roles().contains(FoundationRole.SAAS_OWNER_ADMIN)) return;
    if (context.scopeType() == ScopeType.TENANT && context.roles().contains(FoundationRole.TENANT_ADMIN)) return;
    throw new AuthorizationException(403, "agent-admin-requires-tenant-admin");
  }

  private static String governanceScopeId(AuthContextResolver.ResolvedMe actor) {
    var context = actor.selectedContext();
    return context.scopeType() == ScopeType.SAAS_OWNER
        ? WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID
        : context.tenantId();
  }

  private AgentDefinition agent(AuthContextResolver.ResolvedMe actor, String agentDefinitionId) {
    return repository.agentDefinition(governanceScopeId(actor), agentDefinitionId).orElseThrow(() -> new AuthorizationException(404, "TARGET_NOT_FOUND_OR_FORBIDDEN"));
  }

  private List<Map<String, Object>> skillEntries(AuthContextResolver.ResolvedMe actor, AgentSkillManifest manifest) {
    return manifest.entries().stream().map(entry -> {
      var skill = repository.skillDocument(governanceScopeId(actor), entry.skillDocumentId()).orElse(null);
      return mapOf("stableSkillId", entry.stableSkillId(), "skillDocumentId", entry.skillDocumentId(), "pinnedVersion", entry.pinnedVersion(), "title", entry.title(), "purpose", entry.purpose(), "whenToUse", entry.whenToUse(), "status", skill == null ? "missing" : skill.status().name().toLowerCase(), "redactedPreview", skill == null ? null : preview(skill.contentBody()), "fullContentAvailableInBrowser", false, "seedStatus", skill == null ? null : seedStatus(skill.seedProvenance()), "traceId", traceId("skill", entry.stableSkillId(), manifest.manifestId()));
    }).toList();
  }

  private List<Map<String, Object>> referenceEntries(AuthContextResolver.ResolvedMe actor, AgentReferenceManifest manifest) {
    return manifest.entries().stream().map(entry -> {
      var reference = repository.referenceDocument(governanceScopeId(actor), entry.referenceDocumentId()).orElse(null);
      return mapOf("stableReferenceId", entry.stableReferenceId(), "referenceDocumentId", entry.referenceDocumentId(), "pinnedVersion", entry.pinnedVersion(), "title", entry.title(), "summary", entry.summary(), "whenToConsult", entry.whenToConsult(), "allowedUse", entry.allowedUse(), "accessLevel", entry.accessLevel(), "status", reference == null ? "missing" : reference.status().name().toLowerCase(), "redactedPreview", reference == null ? null : preview(reference.contentBody()), "fullContentAvailableInBrowser", false, "seedStatus", reference == null ? null : seedStatus(reference.seedProvenance()), "traceId", traceId("reference", entry.stableReferenceId(), manifest.manifestId()));
    }).toList();
  }

  private List<Map<String, Object>> seedMaterial(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var tenantId = governanceScopeId(actor);
    var rows = new java.util.ArrayList<Map<String, Object>>();
    repository.agentDefinitions(tenantId).forEach(agent -> rows.add(seedRow(
        "AgentDefinition",
        agent.agentDefinitionId(),
        agent.displayName(),
        safe(agent.description()),
        "starter-agent-definition",
        agent.agentDefinitionId(),
        agent.status(),
        agent.seedProvenance(),
        agent.updatedAt(),
        correlationId)));
    repository.skillDocuments(tenantId).forEach(skill -> rows.add(seedRow(
        "SkillDocument",
        skill.skillDocumentId(),
        skill.title(),
        safe(skill.purpose()),
        "starter-skill",
        AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID,
        skill.status(),
        skill.seedProvenance(),
        skill.updatedAt(),
        correlationId)));
    repository.referenceDocuments(tenantId).forEach(reference -> rows.add(seedRow(
        "ReferenceDocument",
        reference.referenceDocumentId(),
        reference.title(),
        safe(reference.summary()),
        "starter-reference",
        AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID,
        reference.status(),
        reference.seedProvenance(),
        reference.updatedAt(),
        correlationId)));
    return List.copyOf(rows.stream().sorted(Comparator.comparing(row -> String.valueOf(row.get("artifactKind")) + ":" + String.valueOf(row.get("displayName")))).toList());
  }

  private Map<String, Object> catalogRow(AuthContextResolver.ResolvedMe actor, AgentDefinition agent, String correlationId) {
    var provider = providerReadiness(actor, agent);
    var lifecycle = agent.status().name().toLowerCase(Locale.ROOT);
    var providerStatus = String.valueOf(provider.get("status"));
    var readiness = "ready".equals(providerStatus) && agent.status() == AgentLifecycleStatus.ACTIVE ? "ready" : "blocked_provider_or_runtime";
    var seed = seedStatus(agent.seedProvenance());
    var customized = Boolean.TRUE.equals(seed.get("tenantCustomized"));
    return mapOf(
        "id", agent.agentDefinitionId(),
        "rowType", "AgentDefinition",
        "displayName", agent.displayName(),
        "shortPurpose", safe(agent.description()),
        "lifecycleState", lifecycle,
        "status", lifecycle,
        "readinessState", readiness,
        "readinessSummary", readiness.equals("ready") ? "Provider/model references are active; credentials remain backend-only." : "Provider/model readiness is blocked or partial; no fake success is shown.",
        "authorityTier", agent.authorityLevel().name(),
        "authorityLevel", agent.authorityLevel().name(),
        "providerModelReadinessCategory", providerStatus,
        "providerStatus", providerStatus,
        "promptRiskStatus", providerStatus.equals("ready") ? "review-ready" : "deferred_until_provider_runtime_configured",
        "seedCustomizationState", customized ? "tenant-customized" : "starter-default",
        "seedStatus", seed,
        "attentionSummary", providerStatus.equals("ready") ? "No provider blocker surfaced for this catalog row." : "Provider/model readiness requires governed configuration before runtime claims.",
        "lastChangedAt", agent.updatedAt() == null ? null : agent.updatedAt().toString(),
        "lastReviewedAt", agent.updatedAt() == null ? null : agent.updatedAt().toString(),
        "redactionNote", "Raw prompts, skills, references, provider credentials, loader-tool inputs, hidden scopes, and full traces are omitted.",
        "openActionId", "action-open-agent-detail",
        "targetSurfaceId", "surface-agent-admin-detail",
        "safeActionContext", mapOf("agentDefinitionId", agent.agentDefinitionId(), "rowTraceId", traceId("definition", agent.agentDefinitionId(), correlationId)),
        "placement", agent.placement().name(),
        "functionalAreaId", agent.functionalAreaId(),
        "modelConfigRefId", agent.modelConfigRefId(),
        "providerReadiness", provider,
        "tracePolicy", agent.traceRequirements(),
        "traceId", traceId("definition", agent.agentDefinitionId(), correlationId));
  }

  private Map<String, Object> seedRow(String kind, String id, String displayName, String shortPurpose, String sourceCategory, String targetAgentId, AgentLifecycleStatus status, SeedProvenance provenance, Instant updatedAt, String correlationId) {
    var seed = seedStatus(provenance);
    var tenantCustomized = Boolean.TRUE.equals(seed.get("tenantCustomized"));
    var traceId = traceId("seed", id, correlationId);
    return mapOf(
        "id", "seed-" + id,
        "artifactKind", kind,
        "artifactId", id,
        "displayName", displayName,
        "shortPurpose", shortPurpose,
        "sourceCategory", sourceCategory,
        "recommendedManagedAgentTarget", targetAgentId,
        "seedVersionLabel", String.valueOf(seed.getOrDefault("contentVersion", "current")),
        "seedBundleId", seed.get("seedBundleId"),
        "checksum", seed.get("checksum"),
        "compatibilitySummary", "Compatible with the selected tenant/organization Agent Admin starter runtime; backend checks remain authoritative.",
        "compatibilityState", "compatible",
        "readinessState", status == AgentLifecycleStatus.ACTIVE ? "ready" : "blocked-customization-conflict",
        "importStatus", tenantCustomized ? "customized-preserved" : "not-started",
        "status", status.name().toLowerCase(Locale.ROOT),
        "customizationPreservationSummary", tenantCustomized ? "Tenant customization is preserved; raw override content is omitted." : "Starter default is visible; future tenant overrides will be preserved.",
        "lastReviewedAt", updatedAt == null ? null : updatedAt.toString(),
        "lastImportedAt", seed.get("importedAt"),
        "safeProvenanceLabel", String.valueOf(seed.getOrDefault("seedBundleId", "unknown-seed")) + " / " + String.valueOf(seed.getOrDefault("resourceId", id)),
        "redactionNote", "Raw seed package contents, prompts, skills, references, tenant overrides, provider secrets, JWT/session material, and full traces are omitted.",
        "openActionId", "action-agent-seed-material-open-provenance",
        "prepareImportActionId", "action-agent-seed-material-prepare-import",
        "targetSurfaceId", "surface-agent-seed-material",
        "governedCapability", LIST_SEED_MATERIAL,
        "disabledOrOmittedReason", null,
        "seedStatus", seed,
        "tenantCustomized", tenantCustomized,
        "traceId", traceId,
        "traceRefs", List.of(traceId));
  }

  private Map<String, Object> provenanceInspection(Map<String, Object> selectedRow, String correlationId) {
    if (selectedRow == null) return mapOf("state", "empty-no-seed-material", "safeReason", "No visible seed material row is selected.", "rawContentVisible", false);
    return mapOf(
        "state", "ready-provenance",
        "selectedSeedMaterialId", selectedRow.get("id"),
        "artifactKind", selectedRow.get("artifactKind"),
        "displayName", selectedRow.get("displayName"),
        "sourceLineage", selectedRow.get("safeProvenanceLabel"),
        "publisherCategory", selectedRow.get("sourceCategory"),
        "versionCompatibilityClaims", selectedRow.get("compatibilitySummary"),
        "reviewHistorySummary", "Seed provenance was imported through the backend seed loader and is trace-linked for Agent Admin review.",
        "knownConstraints", List.of("No raw seed package content in browser payloads", "Tenant overrides are summarized and preserved", "Provider credentials are never included"),
        "omittedCategoryCounts", mapOf("rawSeedPackageContents", 1, "tenantOverrideBodies", 0, "providerCredentials", 0),
        "safeEvidenceRefs", List.of(selectedRow.get("traceId"), "trace-agent-seed-provenance-" + Math.abs((String.valueOf(selectedRow.get("id")) + correlationId).hashCode())),
        "rawContentVisible", false);
  }

  private Map<String, Object> seedImportWorkflow(String status, Map<String, Object> selectedRow, String targetAgentId, String correlationId) {
    var normalizedStatus = status == null || status.isBlank() ? "not-started" : status;
    return mapOf(
        "selectedSeedMaterialId", selectedRow == null ? null : selectedRow.get("id"),
        "importTaskId", "seed-import-" + Math.abs((String.valueOf(selectedRow == null ? "none" : selectedRow.get("id")) + correlationId).hashCode()),
        "currentStatus", normalizedStatus,
        "phaseLabel", switch (normalizedStatus) {
          case "prepared", "prepared-import" -> "Customization-preserving import plan prepared";
          case "queued", "running" -> "Import lifecycle is backend-governed";
          case "completed" -> "Missing starter defaults imported without deleting tenant customizations";
          case "cancelled" -> "Import task cancelled with no source artifact deletion";
          case "failed", "blocked", "blocked-provider-or-runtime" -> "Import is blocked; no fake success is shown";
          default -> "Select a visible seed row and prepare an import plan";
        },
        "targetManagedAgentDisplayLabel", targetAgentId,
        "customizationPreservationPlan", "Preserve tenant/organization prompt overrides, skill manifests, tool boundaries, model refs, lifecycle choices, approvals, and trace links; never delete raw tenant overrides.",
        "conflictSummary", selectedRow == null ? "No selected visible seed row." : "No customization conflict detected for the browser-safe starter seed plan.",
        "providerRuntimeToolReadinessSummary", "Seed discovery and missing-default import do not claim provider/model success; provider-backed behavior remains fail-closed until configured.",
        "progressEvents", List.of(mapOf("eventId", "seed-plan-visible", "status", selectedRow == null ? "blocked" : normalizedStatus, "summary", "Backend-authored seed workflow status with browser-safe provenance only.")),
        "resultPreview", "Import may create missing starter defaults only; active behavior/lifecycle activation remains delegated to separate governed tasks.",
        "noDestructiveDelete", true,
        "noDirectActivation", true);
  }

  private Map<String, Object> seedAction(String actionId, String label, String capabilityId, String targetSurfaceId, boolean available, String reason) {
    return mapOf("actionId", actionId, "label", label, "governedToolId", capabilityId, "capabilityId", capabilityId, "resultSurfaceId", targetSurfaceId, "available", available, "disabledOrOmittedReason", available ? null : reason);
  }

  private Map<String, Object> providerReadinessSummary(AuthContextResolver.ResolvedMe actor) {
    var total = repository.agentDefinitions(governanceScopeId(actor)).size();
    var ready = repository.agentDefinitions(governanceScopeId(actor)).stream().filter(agent -> "ready".equals(providerReadiness(actor, agent).get("status"))).count();
    return mapOf("status", ready == total ? "ready" : "warning", "readyAgents", ready, "totalAgents", total, "secretVisibility", "redacted");
  }

  private Map<String, Object> providerReadiness(AuthContextResolver.ResolvedMe actor, AgentDefinition agent) {
    var model = repository.modelConfigRef(governanceScopeId(actor), agent.modelConfigRefId());
    if (model.isEmpty()) return mapOf("status", "blocked_provider_or_runtime", "safeReason", "ModelConfigRef is unavailable.", "providerAlias", null, "secretVisibility", "redacted");
    var value = model.orElseThrow();
    if (value.status() != AgentLifecycleStatus.ACTIVE) return mapOf("status", "blocked_provider_or_runtime", "safeReason", "ModelConfigRef is not active.", "providerAlias", safe(value.providerAlias()), "secretVisibility", "redacted");
    return mapOf("status", "ready", "safeReason", "Provider alias is configured; credentials remain backend-only.", "providerAlias", safe(value.providerAlias()), "secretVisibility", "redacted");
  }

  private Map<String, Object> seedStatus(SeedProvenance provenance) {
    if (provenance == null) return mapOf("source", "unknown", "tenantCustomized", false, "checksum", null);
    return mapOf("seedBundleId", provenance.seedBundleId(), "contentVersion", provenance.contentVersion(), "resourceId", provenance.resourceId(), "checksum", provenance.checksum(), "importedAt", provenance.importedAt() == null ? null : provenance.importedAt().toString(), "importerActor", provenance.importerActor(), "tenantCustomized", provenance.tenantCustomized());
  }

  private Map<String, Object> redactionMetadata() {
    return mapOf("browserSafe", true, "omittedFieldKeys", List.of("rawPromptBody", "rawSkillBody", "rawReferenceBody", "rawProviderCredential", "providerCredentialValue", "rawProviderError", "rawJwt", "hiddenTenantId", "hiddenCustomerId", "loaderToolInput", "fullTraceDocument"), "previewLimitChars", PREVIEW_CHARS);
  }

  private Map<String, Long> counts(List<Map<String, Object>> rows, String key) {
    var counts = new LinkedHashMap<String, Long>();
    for (var row : rows) {
      var value = String.valueOf(row.get(key));
      counts.put(value, counts.getOrDefault(value, 0L) + 1L);
    }
    return counts;
  }

  private Map<String, Object> seedCustomizationSummary(List<Map<String, Object>> rows) {
    var customized = rows.stream().filter(row -> "tenant-customized".equals(row.get("seedCustomizationState"))).count();
    return mapOf("tenantCustomizedCount", customized, "starterDefaultCount", rows.size() - customized, "rawSeedContentVisible", false);
  }

  private boolean matches(Map<String, Object> row, String searchText) {
    if (searchText == null || searchText.isBlank()) return true;
    var needle = searchText.toLowerCase(Locale.ROOT);
    return String.valueOf(row.get("displayName")).toLowerCase(Locale.ROOT).contains(needle)
        || String.valueOf(row.get("shortPurpose")).toLowerCase(Locale.ROOT).contains(needle)
        || String.valueOf(row.get("functionalAreaId")).toLowerCase(Locale.ROOT).contains(needle);
  }

  private String safeFilter(Map<String, ?> input, String key, String fallback) {
    if (input == null || !input.containsKey(key) || input.get(key) == null) return fallback;
    return safe(String.valueOf(input.get(key)));
  }

  private String safeInput(Object input, String key, String fallback) {
    if (!(input instanceof Map<?, ?> map) || !map.containsKey(key) || map.get(key) == null) return fallback;
    return safe(String.valueOf(map.get(key)));
  }

  private String scopeLabel(AuthContextResolver.ResolvedMe actor) {
    return actor.selectedContext().scopeType().name().toLowerCase(Locale.ROOT).replace('_', '-') + ":" + governanceScopeId(actor);
  }

  private Map<String, Object> toolGrant(ToolPermissionBoundary.ToolGrant grant) {
    return mapOf("toolId", grant.toolId(), "category", grant.category().name(), "capabilityId", grant.capabilityId(), "allowedOperations", grant.allowedOperations(), "allowedModes", grant.allowedModes(), "sideEffectLevel", grant.sideEffectLevel(), "autonomy", grant.autonomy(), "idempotencyRequired", grant.idempotencyRequired(), "traceLevel", grant.traceLevel());
  }

  private Map<String, Object> field(String id, String label, Object value, boolean editable, String disabledReason) {
    return mapOf("fieldId", id, "label", label, "value", value, "editable", editable, "disabledReason", disabledReason);
  }

  private Map<String, Object> artifactRef(String kind, String id, String status, String capabilityId) {
    return mapOf("artifactKind", kind, "artifactId", id, "status", status == null ? "missing" : status.toLowerCase(), "capabilityId", capabilityId);
  }

  private String preview(String content) {
    var redacted = safe(content == null ? "" : content).replaceAll("\\s+", " ").trim();
    return redacted.length() <= PREVIEW_CHARS ? redacted : redacted.substring(0, PREVIEW_CHARS) + "…";
  }

  private String safe(String value) {
    return value == null ? null : value.replaceAll("(?i)(api[_-]?key|secret|token)\\s*[:=]\\s*\\S+", "$1=[REDACTED]");
  }

  private String traceId(String kind, String id, String correlationId) {
    return "trace-agent-admin-" + kind + "-" + Math.abs((String.valueOf(id) + ":" + String.valueOf(correlationId)).hashCode());
  }

  private String firstNonBlank(String value, String fallback) {
    return value == null || value.isBlank() ? fallback : value;
  }

  private Map<String, Object> mapOf(Object... entries) {
    var map = new LinkedHashMap<String, Object>();
    for (var index = 0; index < entries.length; index += 2) map.put(String.valueOf(entries[index]), entries[index + 1]);
    return map;
  }
}
