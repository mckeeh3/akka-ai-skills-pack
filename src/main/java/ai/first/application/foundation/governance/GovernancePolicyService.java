package ai.first.application.foundation.governance;

import ai.first.domain.foundation.agent.AgentDefinition;
import ai.first.domain.foundation.agent.ToolPermissionBoundary;
import ai.first.domain.foundation.identity.AuthContext;
import ai.first.domain.foundation.governance.GovernancePolicyProposal;
import ai.first.domain.foundation.governance.GovernancePolicySimulationResult;
import java.time.Clock;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import ai.first.application.foundation.attention.AttentionProducerService;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.AuthorizationException;

/** Deterministic Governance/Policy service for scoped reads and inert proposal lifecycle. */
public final class GovernancePolicyService {
  public static final String READ_CAPABILITY = "governance.policy.read";
  public static final String DASHBOARD_CAPABILITY = "governance.policy.dashboard.read";
  public static final String LIST_CAPABILITY = "governance.policy.list";
  public static final String PROPOSAL_DRAFT_CAPABILITY = "governance.policy.proposal.draft";
  public static final String PROPOSAL_SUBMIT_CAPABILITY = "governance.policy.proposal.submit";
  public static final String PROPOSAL_READ_CAPABILITY = "governance.policy.proposal.read";
  public static final String SIMULATE_CAPABILITY = "governance.policy.simulate";
  public static final String LEGACY_PROPOSE_CAPABILITY = "governance.policy.propose";
  public static final String REVIEW_CAPABILITY = "governance.proposals.review";
  public static final String APPROVE_CAPABILITY = "governance.policy.approve";
  public static final String REJECT_CAPABILITY = "governance.policy.reject";
  public static final String ACTIVATE_PROPOSAL_CAPABILITY = "governance.proposals.activate";
  public static final String ACTIVATE_CAPABILITY = "governance.policy.activate";
  public static final String ROLLBACK_CAPABILITY = "governance.policy.rollback";
  public static final String OUTCOMES_RECORD_CAPABILITY = "governance.outcomes.record";
  private static final List<String> OMITTED_FIELDS = List.of("rawPrompt", "hiddenPromptText", "rawProviderCredential", "rawToolPayload", "providerSecret", "api_key");

  private final GovernancePolicyRepository repository;
  private final AuthContextResolver authContextResolver;
  private final Clock clock;
  private final AttentionProducerService attentionProducerService;

  public GovernancePolicyService(GovernancePolicyRepository repository, AuthContextResolver authContextResolver, Clock clock) {
    this(repository, authContextResolver, clock, null);
  }

  public GovernancePolicyService(GovernancePolicyRepository repository, AuthContextResolver authContextResolver, Clock clock, AttentionProducerService attentionProducerService) {
    this.repository = Objects.requireNonNull(repository);
    this.authContextResolver = Objects.requireNonNull(authContextResolver);
    this.clock = Objects.requireNonNull(clock);
    this.attentionProducerService = attentionProducerService;
  }

  public SurfaceData dashboard(AuthContextResolver.ResolvedMe actor, String correlationId) {
    requireRead(actor, DASHBOARD_CAPABILITY, correlationId);
    var proposals = repository.listProposals(actor.selectedContext().tenantId(), actor.selectedContext().customerId());
    var draft = proposals.stream().filter(proposal -> proposal.status() == GovernancePolicyProposal.Status.DRAFT).count();
    var reviewRequired = proposals.stream().filter(proposal -> proposal.status() == GovernancePolicyProposal.Status.IN_REVIEW).count();
    var changesRequested = proposals.stream().filter(proposal -> proposal.status() == GovernancePolicyProposal.Status.CHANGES_REQUESTED).count();
    var approved = proposals.stream().filter(proposal -> proposal.status() == GovernancePolicyProposal.Status.APPROVED).count();
    var rejected = proposals.stream().filter(proposal -> proposal.status() == GovernancePolicyProposal.Status.REJECTED).count();
    var rollbackCandidates = proposals.stream().filter(proposal -> proposal.status() == GovernancePolicyProposal.Status.ACTIVATED).count();
    var rolledBack = proposals.stream().filter(proposal -> proposal.status() == GovernancePolicyProposal.Status.ROLLED_BACK).count();
    var blocked = proposals.stream().filter(proposal -> proposal.status() == GovernancePolicyProposal.Status.BLOCKED).count();
    var pending = draft + reviewRequired + changesRequested;
    var outcomeFollowUp = proposals.stream().filter(proposal -> (proposal.status() == GovernancePolicyProposal.Status.ACTIVATED || proposal.status() == GovernancePolicyProposal.Status.ROLLED_BACK) && proposal.outcomeNotes().isEmpty()).count();
    var visibleCapabilities = actor.selectedContext().capabilities();
    var authorizedActions = new java.util.ArrayList<Map<String, Object>>();
    authorizedActions.add(mapOf("actionId", "action-governance-policy-dashboard", "label", "Refresh governance dashboard", "capabilityId", READ_CAPABILITY, "governedToolId", "list-policy-proposals", "resultSurfaceId", "surface-governance-policy-dashboard"));
    authorizedActions.add(mapOf("actionId", "action-governance-policy-list", "label", "Review policy inventory", "capabilityId", READ_CAPABILITY, "governedToolId", "list-policy-proposals", "resultSurfaceId", "surface-governance-policy-inventory"));
    if (visibleCapabilities.contains(LEGACY_PROPOSE_CAPABILITY)) authorizedActions.add(mapOf("actionId", "action-governance-policy-draft-proposal", "label", "Draft policy proposal", "capabilityId", LEGACY_PROPOSE_CAPABILITY, "governedToolId", "draft-policy-proposal", "resultSurfaceId", "surface-governance-policy-proposal", "idempotencyRequired", true));
    if (visibleCapabilities.contains(SIMULATE_CAPABILITY)) authorizedActions.add(mapOf("actionId", "action-governance-policy-simulate", "label", "Open simulation evidence", "capabilityId", SIMULATE_CAPABILITY, "governedToolId", "simulate-policy-change", "resultSurfaceId", "surface-governance-policy-simulation", "denialHint", "Simulation remains advisory and cannot grant authority."));
    if (visibleCapabilities.contains(APPROVE_CAPABILITY)) authorizedActions.add(mapOf("actionId", "action-governance-policy-decide", "label", "Open policy decision review", "capabilityId", APPROVE_CAPABILITY, "governedToolId", "approve-activate-or-rollback-policy", "resultSurfaceId", "surface-governance-policy-decision", "approvalRequired", true, "denialHint", "Decision review requires a selected proposal and human authority; the dashboard cannot approve directly."));
    if (visibleCapabilities.contains("governance.policy.impact_analysis.start")) authorizedActions.add(mapOf("actionId", "action-governance-policy-start-impact-analysis", "label", "Start policy impact analysis", "capabilityId", "governance.policy.impact_analysis.start", "governedToolId", "start-policy-impact-analysis", "resultSurfaceId", "surface-governance-policy-impact-analysis-task", "approvalRequired", true, "denialHint", "Fails closed until model/provider/runtime and governed evidence grants are configured."));
    return surface("surface-governance-policy-dashboard", "dashboard", "Governance/Policy dashboard", correlationId, mapOf(
        "surfaceContract", "governance.policy.dashboard.v1",
        "ownerFunctionalAgentId", "governance-policy-agent",
        "accountContext", mapOf("tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId(), "selectedContextId", actor.selectedContext().membershipId()),
        "readiness", "ready_with_fail_closed_advisory_workers",
        "readinessSummary", mapOf("status", "ready_with_fail_closed_advisory_workers", "summary", "GovernancePolicyService owns scoped dashboard/read/proposal lifecycle data. Impact-analysis provider/runtime readiness is represented as blocked_provider_or_runtime until configured; no model-less success is exposed.", "providerRuntime", "blocked_provider_or_runtime_for_impact_analysis", "noFakeSuccess", true),
        "cards", List.of(
            mapOf("cardId", "card-active-policies", "label", "Active policy concepts", "count", policies().size(), "value", policies().size(), "severity", "info", "actionId", "action-governance-policy-list", "targetSurfaceId", "surface-governance-policy-inventory", "safeEmptyState", "Open inventory to inspect backend-owned policy concepts."),
            mapOf("cardId", "card-pending-proposals", "label", "Pending proposals", "count", pending, "value", pending, "severity", pending == 0 ? "info" : "warning", "actionId", "action-governance-policy-list", "targetSurfaceId", "surface-governance-policy-inventory", "safeEmptyState", "No drafts, submitted reviews, or changes-requested proposals are visible."),
            mapOf("cardId", "card-policy-traces", "label", "Policy traces", "count", proposals.size(), "value", proposals.size(), "severity", proposals.isEmpty() ? "info" : "warning", "actionId", "action-open-audit-trace", "targetSurfaceId", "surface-audit-trace-search", "safeEmptyState", "Trace drilldowns remain role-gated and redacted.")),
        "attentionQueues", List.of(
            mapOf("queueId", "policy-proposals-in-review", "label", "Submitted proposals awaiting review", "count", reviewRequired, "severity", reviewRequired == 0 ? "info" : "urgent", "statusText", reviewRequired == 0 ? "Clear" : "Human approval required", "reasonCode", "submitted_review_required", "sourceCapabilityId", REVIEW_CAPABILITY, "targetSurfaceId", "surface-governance-policy-inventory", "actionId", "action-governance-policy-list", "authorizedActionIds", List.of("action-governance-policy-list", "action-governance-policy-decide"), "redaction", "browser-safe proposal summaries"),
            mapOf("queueId", "policy-simulations-required-or-blocked", "label", "Simulation evidence and blockers", "count", reviewRequired + approved, "severity", reviewRequired + approved == 0 ? "info" : "warning", "statusText", reviewRequired + approved == 0 ? "No simulation queue" : "Review simulation evidence before activation", "reasonCode", "simulation_required_or_blocked", "sourceCapabilityId", SIMULATE_CAPABILITY, "targetSurfaceId", "surface-governance-policy-simulation", "actionId", "action-governance-policy-simulate", "authorizedActionIds", List.of("action-governance-policy-simulate"), "redaction", "advisory evidence only; no authority granted"),
            mapOf("queueId", "policy-activation-or-rollback-prerequisites", "label", "Activation or rollback prerequisites", "count", approved + rollbackCandidates, "severity", approved + rollbackCandidates == 0 ? "info" : "warning", "statusText", approved + rollbackCandidates == 0 ? "No activation or rollback work" : "Backend prerequisites required", "reasonCode", "activation_rollback_prerequisites", "sourceCapabilityId", ACTIVATE_CAPABILITY, "targetSurfaceId", "surface-governance-policy-decision", "actionId", "action-governance-policy-list", "authorizedActionIds", List.of("action-governance-policy-list", "action-governance-policy-activate", "action-governance-policy-rollback"), "redaction", "rollback metadata and hidden authority state omitted"),
            mapOf("queueId", "policy-outcome-follow-up", "label", "Outcome notes needing follow-up", "count", outcomeFollowUp, "severity", outcomeFollowUp == 0 ? "info" : "warning", "statusText", outcomeFollowUp == 0 ? "No follow-up" : "Record outcome evidence", "reasonCode", "outcome_follow_up", "sourceCapabilityId", OUTCOMES_RECORD_CAPABILITY, "targetSurfaceId", "surface-governance-policy-outcome", "actionId", "action-governance-policy-list", "authorizedActionIds", List.of("action-governance-policy-list", "action-governance-policy-outcome-note"), "redaction", "browser-safe outcome summary only"),
            mapOf("queueId", "policy-impact-analysis", "label", "Impact analysis tasks", "count", 0, "severity", "blocked_provider_or_runtime", "statusText", "Provider/runtime fail-closed until configured", "reasonCode", "provider_runtime_blocked_fail_closed", "sourceCapabilityId", "governance.policy.impact_analysis.start", "targetSurfaceId", "surface-governance-policy-impact-analysis-task", "actionId", "action-governance-policy-start-impact-analysis", "authorizedActionIds", List.of("action-governance-policy-start-impact-analysis", "action-governance-policy-read-impact-analysis"), "redaction", "no fake success; raw provider/model data omitted")),
        "authorizedActions", List.copyOf(authorizedActions),
        "recentActivity", proposals.stream().limit(5).map(proposal -> mapOf("activityId", proposal.proposalId(), "label", proposal.title(), "summary", proposal.status().name().toLowerCase().replace('_', '-') + " · " + proposal.riskClassification(), "actorSummary", "backend-scoped policy lifecycle", "targetSurfaceId", "surface-governance-policy-detail", "traceId", firstNonBlank(proposal.decisionCorrelationId(), proposal.submittedCorrelationId(), proposal.createdCorrelationId()), "redaction", "browser-safe activity title and trace summary")).toList(),
        "attentionItems", List.of(
            mapOf("itemId", "approval-gate", "label", "Authority-changing policy proposals", "status", pending == 0 ? "clear" : "approval_required", "reasonCode", "human_policy_review_required", "targetSurfaceId", "surface-governance-policy-inventory", "actionId", "action-governance-policy-list"),
            mapOf("itemId", "runtime-boundary", "label", "Provider/model boundary", "status", "blocked_provider_or_runtime", "reasonCode", "no_fake_impact_analysis_success", "targetSurfaceId", "surface-governance-policy-impact-analysis-task", "actionId", "action-governance-policy-start-impact-analysis")),
        "proposalLifecycle", List.of("draft", "submitted", "simulation-required", "in-review", "changes-requested", "approved", "rejected", "activated", "rollback-candidate", "rolled-back", "superseded"),
        "proposalLifecycleSegments", List.of(
            mapOf("state", "draft", "count", draft, "targetSurfaceId", "surface-governance-policy-inventory", "actionId", "action-governance-policy-list"),
            mapOf("state", "submitted", "count", reviewRequired, "targetSurfaceId", "surface-governance-policy-inventory", "actionId", "action-governance-policy-list"),
            mapOf("state", "simulation-required", "count", reviewRequired + approved, "targetSurfaceId", "surface-governance-policy-simulation", "actionId", "action-governance-policy-simulate"),
            mapOf("state", "in-review", "count", reviewRequired, "targetSurfaceId", "surface-governance-policy-decision", "actionId", "action-governance-policy-decide"),
            mapOf("state", "changes-requested", "count", changesRequested, "targetSurfaceId", "surface-governance-policy-inventory", "actionId", "action-governance-policy-list"),
            mapOf("state", "approved", "count", approved, "targetSurfaceId", "surface-governance-policy-decision", "actionId", "action-governance-policy-list"),
            mapOf("state", "rejected", "count", rejected, "targetSurfaceId", "surface-governance-policy-inventory", "actionId", "action-governance-policy-list"),
            mapOf("state", "activated", "count", rollbackCandidates, "targetSurfaceId", "surface-governance-policy-decision", "actionId", "action-governance-policy-list"),
            mapOf("state", "rollback-candidate", "count", rollbackCandidates, "targetSurfaceId", "surface-governance-policy-decision", "actionId", "action-governance-policy-list"),
            mapOf("state", "rolled-back", "count", rolledBack, "targetSurfaceId", "surface-governance-policy-outcome", "actionId", "action-governance-policy-list"),
            mapOf("state", "blocked", "count", blocked, "targetSurfaceId", "surface-governance-policy-system-message", "actionId", "action-governance-policy-dashboard")),
        "requiredStates", List.of("loading", "empty", "ready", "forbidden/system-message", "stale/reconnect", "partial-data", "blocked-provider-or-runtime"),
        "capabilityIds", List.of(READ_CAPABILITY, LEGACY_PROPOSE_CAPABILITY, SIMULATE_CAPABILITY, REVIEW_CAPABILITY, APPROVE_CAPABILITY, ACTIVATE_CAPABILITY, ROLLBACK_CAPABILITY, OUTCOMES_RECORD_CAPABILITY, "governance.policy.impact_analysis.start", "governance.policy.impact_analysis.read"),
        "traceLinks", List.of(mapOf("traceId", trace("dashboard", correlationId), "label", "Governance/Policy dashboard workstream read", "targetSurfaceId", "surface-audit-trace-detail", "correlationId", correlationId, "redaction", "raw ids and provider/tool payloads role-gated")),
        "traceRefs", List.of(trace("dashboard", correlationId)),
        "redaction", mapOf("browserSafe", true, "omittedFieldKeys", OMITTED_FIELDS, "hiddenCrossTenantEvidence", true, "rawProviderModelData", "omitted", "rawToolPayloads", "omitted", "correlationIds", "role-gated"),
        "visibilitySplit", mapOf("defaultUserVisible", List.of("queue/card labels", "counts", "lifecycle/status labels", "safe risk or blocker summaries", "available next actions"), "onDemandDrilldown", List.of("proposal/task ids", "evidence summaries", "redacted trace summaries", "lifecycle history"), "adminSupportAuditorOnly", List.of("capability ids", "trace ids", "policy-decision/admin-audit/workstream/agent-work refs"), "internalOnly", OMITTED_FIELDS),
        "noDirectMutation", true,
        "noFakeSuccess", true));
  }

  public SurfaceData inventory(AuthContextResolver.ResolvedMe actor, String correlationId) {
    return inventory(actor, Map.of(), correlationId);
  }

  public SurfaceData inventory(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    validateScope(actor, input, correlationId);
    requireRead(actor, LIST_CAPABILITY, correlationId);
    var allRows = new java.util.ArrayList<Map<String, Object>>();
    allRows.addAll(policies());
    allRows.addAll(repository.listProposals(actor.selectedContext().tenantId(), actor.selectedContext().customerId()).stream().map(proposal -> proposalRow(actor, proposal)).toList());
    var search = stringInput(input, "search", stringInput(input, "query", ""));
    var lifecycle = stringInput(input, "lifecycle", stringInput(input, "status", stringInput(input, "filter", "")));
    var rows = allRows.stream().filter(row -> matchesInventoryFilter(row, search, lifecycle)).toList();
    var visibleCapabilities = actor.selectedContext().capabilities();
    var authorizedActions = new java.util.ArrayList<Map<String, Object>>();
    authorizedActions.add(inventoryAction("action-governance-policy-list", "Refresh/open inventory", READ_CAPABILITY, "list-policy-proposals", "surface-governance-policy-inventory", false));
    authorizedActions.add(inventoryAction("action-governance-policy-read", "Open row detail/evidence", READ_CAPABILITY, "list-policy-proposals", "surface-governance-policy-detail", false));
    if (visibleCapabilities.contains(LEGACY_PROPOSE_CAPABILITY)) authorizedActions.add(inventoryAction("action-governance-policy-draft-proposal", "Draft new proposal", LEGACY_PROPOSE_CAPABILITY, "draft-policy-proposal", "surface-governance-policy-proposal", true));
    if (visibleCapabilities.contains(SIMULATE_CAPABILITY)) authorizedActions.add(inventoryAction("action-governance-policy-simulate", "Open simulation task", SIMULATE_CAPABILITY, "simulate-policy-change", "surface-governance-policy-simulation", false));
    if (visibleCapabilities.contains(APPROVE_CAPABILITY)) authorizedActions.add(inventoryAction("action-governance-policy-decide", "Open decision/activation/rollback work", APPROVE_CAPABILITY, "approve-activate-or-rollback-policy", "surface-governance-policy-decision", true));
    if (visibleCapabilities.contains("governance.policy.impact_analysis.start")) authorizedActions.add(inventoryAction("action-governance-policy-start-impact-analysis", "Start impact analysis", "governance.policy.impact_analysis.start", "start-policy-impact-analysis", "surface-governance-policy-impact-analysis-task", true));
    if (visibleCapabilities.contains("governance.policy.impact_analysis.read")) authorizedActions.add(inventoryAction("action-governance-policy-read-impact-analysis", "Read impact analysis", "governance.policy.impact_analysis.read", "read-policy-impact-analysis", "surface-governance-policy-impact-analysis-task", false));
    if (visibleCapabilities.contains(OUTCOMES_RECORD_CAPABILITY)) authorizedActions.add(inventoryAction("action-governance-policy-outcome-note", "Record/open outcome note", OUTCOMES_RECORD_CAPABILITY, "record-policy-outcome-note", "surface-governance-policy-outcome", true));
    var totalVisible = allRows.size();
    var filteredCount = rows.size();
    return surface("surface-governance-policy-inventory", "list-search", "Policy inventory", correlationId, mapOf(
        "surfaceContract", "governance.policy.inventory.v1",
        "ownerFunctionalAgentId", "governance-policy-agent",
        "inventorySummary", mapOf("selectedWorkstream", "Governance/Policy", "scopeLabel", contextLabel(actor), "totalVisibleCount", totalVisible, "filteredCount", filteredCount, "lifecycleBucketCounts", lifecycleCounts(allRows), "blockedProviderRuntimeCount", 1, "selectedFiltersSummary", selectedFiltersSummary(search, lifecycle), "freshnessState", "ready", "emptyStateCopy", "No active policy concepts or policy proposals are visible in this selected AuthContext."),
        "query", mapOf("selectedContextId", actor.selectedContext().membershipId(), "capabilityId", LIST_CAPABILITY, "search", search, "lifecycle", lifecycle, "includes", "active policy concepts and scoped policy proposals", "tenantCustomerScope", "backend-resolved selected AuthContext"),
        "filters", mapOf("searchText", search, "lifecycle", lifecycle, "allowedLifecycleValues", List.of("active", "draft", "submitted", "in-review", "changes-requested", "approved", "rejected", "activated", "rolled-back", "blocked"), "sourceArtifactOptions", List.of("GovernancePolicyService", "ToolPermissionBoundary", "AgentDefinition", "GovernancePolicyProposal"), "validationMessages", List.of()),
        "sortAndPage", mapOf("allowedSortKeys", List.of("lastActivity", "status", "risk", "title"), "currentSort", "lastActivity", "pageSize", rows.size(), "hasNextCursor", false, "hasPreviousCursor", false, "staleCursorRecoveryCopy", "Refresh inventory; raw database cursors are never exposed."),
        "rows", rows,
        "pageInfo", mapOf("totalKnownCount", totalVisible, "filteredCount", filteredCount),
        "emptyStates", mapOf("noVisibleProposals", "No active policy concepts or policy proposals are visible in this selected AuthContext.", "filterMatchedZero", "No authorized rows match the current filters; hidden rows are not enumerated.", "staleCursor", "Refresh inventory; stale cursor details are omitted.", "deniedDirectProposal", "The requested proposal is unavailable or redacted in this selected context.", "providerRuntimeBlocked", "Advisory simulation or impact-analysis status is blocked until provider/runtime configuration is available.", "partialDataOmission", "Some row evidence or trace details may be role-gated."),
        "emptyMessage", filteredCount == 0 ? "No authorized policy/proposal rows match this selected scope or filter." : "No active policy concepts or policy proposals are visible in this selected AuthContext.",
        "authorizedActions", List.copyOf(authorizedActions),
        "traceLinks", List.of(mapOf("traceId", trace("inventory", correlationId), "label", "Governance/Policy inventory protected read", "targetSurfaceId", "surface-audit-trace-detail", "correlationId", correlationId, "redaction", "raw ids and provider/tool payloads role-gated")),
        "traceRefs", List.of(trace("inventory", correlationId)),
        "redaction", mapOf("browserSafe", true, "omittedFieldKeys", OMITTED_FIELDS, "hiddenCrossTenantEvidence", true, "hiddenAuthorityState", true, "rawProviderModelData", "omitted", "rawToolPayloads", "omitted", "rawDatabaseCursors", "omitted", "correlationIds", "role-gated", "idempotencyKeys", "role-gated"),
        "readiness", mapOf("simulation", "ready_for_deterministic_advisory_evidence", "impactAnalysis", "blocked_provider_or_runtime", "noFakeSuccess", true),
        "systemStates", List.of("loading", "empty", "ready", "filter-validation-error", "forbidden/system-message", "stale/reconnect", "partial-data", "blocked-provider-or-runtime", "failure"),
        "noDirectMutation", true,
        "noFakeSuccess", true));
  }

  public SurfaceData detail(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    validateScope(actor, input, correlationId);
    requireRead(actor, READ_CAPABILITY, correlationId);
    var policyId = stringInput(input, "policyId", "policy-human-approval");
    var policy = policies().stream().filter(row -> policyId.equals(row.get("policyId"))).findFirst().orElse(policies().get(0));
    return surface("surface-governance-policy-detail", "detail-edit", "Policy evidence detail", correlationId, mapOf(
        "surfaceContract", "governance.policy.detail.v1",
        "recordId", policy.get("policyId"),
        "recordLabel", policy.get("name"),
        "recordKind", "policy",
        "summary", "Browser-safe policy evidence from GovernancePolicyService for selected backend AuthContext only.",
        "fields", List.of(
            mapOf("fieldId", "status", "label", "Status", "value", policy.get("status"), "editable", false),
            mapOf("fieldId", "authority", "label", "Authority source", "value", "backend AuthContext + deterministic GovernancePolicyService", "editable", false),
            mapOf("fieldId", "redaction", "label", "Redaction", "value", "raw prompts/provider secrets omitted", "editable", false)),
        "affectedCapabilityIds", policy.get("affectedCapabilityIds"),
        "traceReferences", List.of(policy.get("lastChangeTraceId")),
        "noDirectMutation", true));
  }

  public ActionResult draftProposal(AuthContextResolver.ResolvedMe actor, Object input, String idempotencyKey, String correlationId) {
    requireProposal(actor, PROPOSAL_DRAFT_CAPABILITY, correlationId);
    if (idempotencyKey == null || idempotencyKey.isBlank()) return validation("idempotencyKey", "Policy proposal drafts require a client-generated idempotency key.", correlationId);
    var existing = repository.findByIdempotencyKey(actor.selectedContext().tenantId(), actor.selectedContext().customerId(), actor.account().accountId(), idempotencyKey);
    if (existing.isPresent()) return action("no-op", "Idempotent policy proposal draft replay returned the existing proposal; no authority changed.", proposal(actor, existing.orElseThrow(), correlationId), List.of(trace("proposal-idempotency", correlationId)));
    var now = Instant.now(clock);
    var proposedContent = stringInput(input, "proposedContent", "Draft Governance/Policy policy clarification. Preserve backend authorization, human approval, idempotency, tenant isolation, and trace requirements.");
    var rationale = stringInput(input, "rationale", "Governance/Policy workstream draft proposal");
    var proposal = new GovernancePolicyProposal(
        "proposal-" + stableSuffix(actor.selectedContext().tenantId() + ":" + actor.account().accountId() + ":" + idempotencyKey),
        actor.selectedContext().tenantId(), actor.selectedContext().customerId(), actor.account().accountId(), GovernancePolicyProposal.Status.DRAFT,
        stringInput(input, "policyId", "policy-human-approval"),
        stringInput(input, "title", "Governance policy proposal"),
        safe(rationale),
        safe(proposedContent),
        classifyRisk(proposedContent),
        List.of(READ_CAPABILITY, SIMULATE_CAPABILITY, APPROVE_CAPABILITY, ACTIVATE_CAPABILITY),
        List.of("governance-policy-workstream", "ToolPermissionBoundary", "AgentDefinition"),
        APPROVE_CAPABILITY,
        "rollback metadata required before activation; no direct mutation in draft or submit",
        idempotencyKey,
        correlationId,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        List.of(),
        now,
        now);
    repository.saveProposal(proposal);
    authContextResolver.appendProtectedReadTrace(actor, "governance.policy.proposal lifecycle draft", proposal.proposalId(), correlationId);
    return action("accepted", "Draft policy proposal created by GovernancePolicyService without activating authority.", proposal(actor, proposal, correlationId), List.of(trace("proposal-draft", correlationId)));
  }

  public ActionResult submitProposal(AuthContextResolver.ResolvedMe actor, Object input, String idempotencyKey, String correlationId) {
    requireProposal(actor, PROPOSAL_SUBMIT_CAPABILITY, correlationId);
    var proposalId = stringInput(input, "proposalId", null);
    var proposal = proposalId == null ? repository.listProposals(actor.selectedContext().tenantId(), actor.selectedContext().customerId()).stream().findFirst().orElse(null) : repository.findProposal(actor.selectedContext().tenantId(), actor.selectedContext().customerId(), proposalId).orElse(null);
    if (proposal == null) return validation("proposalId", "A tenant-scoped proposal id is required before submit.", correlationId);
    if (proposal.status() == GovernancePolicyProposal.Status.IN_REVIEW) return action("no-op", "Policy proposal was already submitted; idempotent no-op returned current review record.", proposal(actor, proposal, correlationId), List.of(trace("proposal-submit-noop", correlationId)));
    var submitted = repository.saveProposal(proposal.submitted(correlationId, Instant.now(clock)));
    authContextResolver.appendProtectedReadTrace(actor, "governance.policy.proposal lifecycle submit", submitted.proposalId(), correlationId);
    if (attentionProducerService != null && submitted.status() == GovernancePolicyProposal.Status.IN_REVIEW) attentionProducerService.upsertGovernanceApproval(submitted, correlationId);
    return action(submitted.status() == GovernancePolicyProposal.Status.IN_REVIEW ? "accepted" : "validation-error", "Policy proposal submitted for human review; no active authority changed.", proposal(actor, submitted, correlationId), List.of(trace("proposal-submit", correlationId)));
  }

  public SurfaceData readProposal(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    validateScope(actor, input, correlationId);
    requireRead(actor, PROPOSAL_READ_CAPABILITY, correlationId);
    var proposal = findScopedProposal(actor, input);
    if (proposal != null) return proposal(actor, proposal, correlationId);
    if (stringInput(input, "proposalId", null) != null) return validation("proposalId", "No authorized proposal found for selected AuthContext.", correlationId).surface();
    if (actor.selectedContext().capabilities().contains(LEGACY_PROPOSE_CAPABILITY)) return newDraftProposalSurface(actor, input, correlationId);
    return systemMessage("surface-governance-policy-system-message", "not_found_or_redacted", "No authorized proposal is visible in the selected AuthContext, and draft authority is not available.", READ_CAPABILITY, correlationId);
  }

  public SurfaceData readSimulation(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    validateScope(actor, input, correlationId);
    requireRead(actor, READ_CAPABILITY, correlationId);
    var proposal = findScopedProposal(actor, input);
    if (proposal == null) {
      if (stringInput(input, "proposalId", null) != null || stringInput(input, "simulationId", null) != null) {
        return validation("proposalId", "No authorized proposal or simulation evidence was found for the selected AuthContext.", correlationId).surface();
      }
      return emptySimulation(actor, null, correlationId);
    }
    var simulationId = stringInput(input, "simulationId", null);
    var simulation = simulationId == null
        ? repository.listSimulations(actor.selectedContext().tenantId(), actor.selectedContext().customerId(), proposal.proposalId()).stream().findFirst()
        : repository.findSimulation(actor.selectedContext().tenantId(), actor.selectedContext().customerId(), simulationId).filter(candidate -> proposal.proposalId().equals(candidate.proposalId()));
    return simulation.map(result -> simulation(actor, proposal, result, correlationId)).orElseGet(() -> emptySimulation(actor, proposal, correlationId));
  }

  public ActionResult simulateProposal(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    return simulateProposal(actor, input, stringInput(input, "idempotencyKey", null), correlationId);
  }

  public ActionResult simulateProposal(AuthContextResolver.ResolvedMe actor, Object input, String idempotencyKey, String correlationId) {
    validateScope(actor, input, correlationId);
    requireVisible(actor, SIMULATE_CAPABILITY, correlationId);
    authContextResolver.appendProtectedReadTrace(actor, SIMULATE_CAPABILITY, "governance policy simulation", correlationId);
    var proposal = findScopedProposal(actor, input);
    if (proposal == null) return validation("proposalId", "Simulation requires a tenant-scoped proposal; no direct mutation or model-owned authority is allowed.", correlationId);
    var effectiveIdempotencyKey = firstNonBlank(idempotencyKey, "simulation:" + proposal.proposalId() + ":" + correlationId);
    var existing = repository.findSimulationByIdempotencyKey(actor.selectedContext().tenantId(), actor.selectedContext().customerId(), actor.account().accountId(), effectiveIdempotencyKey);
    if (existing.isPresent()) {
      return action("no-op", "Idempotent policy simulation replay returned existing review evidence; no authority changed.", simulation(actor, proposal, existing.orElseThrow(), correlationId), List.of(trace("simulation-noop", correlationId)));
    }
    var simulation = repository.saveSimulation(new GovernancePolicySimulationResult(
        "sim-" + stableSuffix(actor.selectedContext().tenantId() + ":" + proposal.proposalId() + ":" + effectiveIdempotencyKey),
        actor.selectedContext().tenantId(),
        actor.selectedContext().customerId(),
        proposal.proposalId(),
        actor.account().accountId(),
        GovernancePolicySimulationResult.Status.COMPLETED_REVIEW_REQUIRED,
        safe(stringInput(input, "scenario", "proposal change is replayed against approval, tool-boundary, tenant-isolation, redaction, and activation gates")),
        List.of("authorized read-only policy inventory", "authorized simulation evidence", "human approval by actor with " + APPROVE_CAPABILITY),
        List.of("model cannot self-approve", "prompt text cannot grant tool access", "cross-tenant evidence is omitted", "activation denied until simulation evidence, human approval, and rollback metadata exist"),
        List.of("Activation still requires approved proposal, idempotency key, current proposal status, simulation evidence, and rollback metadata.", "Simulation output is advisory and cannot grant authority."),
        List.of("risk=" + proposal.riskClassification(), "authority_expansion_requires_human_approval", "tool_boundary_changes_remain_backend_enforced"),
        List.of("proposal:" + proposal.proposalId(), "trace:" + correlationId, "policy:" + proposal.targetPolicyId()),
        List.of(APPROVE_CAPABILITY, ACTIVATE_CAPABILITY),
        effectiveIdempotencyKey,
        correlationId,
        Instant.now(clock)));
    return action("accepted", "Policy simulation evidence recorded for review; approval is still required before activation.", simulation(actor, proposal, simulation, correlationId), List.of(trace("simulation", correlationId)));
  }

  public ActionResult decideProposal(AuthContextResolver.ResolvedMe actor, Object input, String idempotencyKey, String correlationId) {
    validateScope(actor, input, correlationId);
    requireVisible(actor, APPROVE_CAPABILITY, correlationId);
    if (idempotencyKey == null || idempotencyKey.isBlank()) return validation("idempotencyKey", "Governance decisions require a client-generated idempotency key.", correlationId);
    var proposal = findScopedProposal(actor, input);
    if (proposal == null) return validation("proposalId", "Approval or rejection requires a tenant-scoped submitted proposal.", correlationId);
    var decision = stringInput(input, "decision", "approve").toLowerCase();
    var rationale = safe(stringInput(input, "rationale", "Human governance review recorded."));
    if (proposal.status() == GovernancePolicyProposal.Status.APPROVED || proposal.status() == GovernancePolicyProposal.Status.REJECTED) {
      return action("no-op", "Governance decision was already recorded; idempotency/no-op preserved and no direct mutation occurred.", decision(actor, proposal, correlationId), List.of(trace("decision-noop", correlationId)));
    }
    if (proposal.status() != GovernancePolicyProposal.Status.IN_REVIEW) return action("approval-required", "Proposal must be submitted for review before approval or rejection.", systemMessage("approval-required", "approval-required", "Submitted proposal is required before a governance decision.", APPROVE_CAPABILITY, correlationId), List.of(trace("decision-blocked", correlationId)));
    var decided = "request_changes".equals(decision) || "request-changes".equals(decision) ? proposal.changesRequested(rationale, correlationId, Instant.now(clock)) : "reject".equals(decision) ? proposal.rejected(rationale, correlationId, Instant.now(clock)) : proposal.approved(rationale, correlationId, Instant.now(clock));
    repository.saveProposal(decided);
    authContextResolver.appendProtectedReadTrace(actor, REVIEW_CAPABILITY, "governance policy decision", correlationId);
    if (attentionProducerService != null && (decided.status() == GovernancePolicyProposal.Status.APPROVED || decided.status() == GovernancePolicyProposal.Status.REJECTED)) attentionProducerService.resolveGovernanceApproval(decided, decided.status().name().toLowerCase(), correlationId);
    return action("approved".equals(decided.decision()) ? "accepted" : "accepted", "Governance decision recorded as human approval/rejection evidence; activation remains a separate backend command.", decision(actor, decided, correlationId), List.of(trace("decision", correlationId)));
  }

  public ActionResult activateProposal(AuthContextResolver.ResolvedMe actor, Object input, String idempotencyKey, String correlationId) {
    validateScope(actor, input, correlationId);
    requireVisible(actor, ACTIVATE_CAPABILITY, correlationId);
    if (idempotencyKey == null || idempotencyKey.isBlank()) return validation("idempotencyKey", "Activation requires a client-generated idempotency key.", correlationId);
    var proposal = findScopedProposal(actor, input);
    if (proposal == null) return action("approval-required", "Policy activation is blocked until an approved proposal, authority check, idempotency key, and rollback reference are present.", activationBlocked(correlationId, "No authorized proposal was found for activation."), List.of(trace("activation-blocked", correlationId)));
    if (proposal.status() == GovernancePolicyProposal.Status.ACTIVATED) return action("no-op", "Policy activation was already recorded; idempotency/no-op preserved.", decision(actor, proposal, correlationId), List.of(trace("activation-noop", correlationId)));
    var rollbackReference = stringInput(input, "rollbackReference", null);
    var simulations = repository.listSimulations(actor.selectedContext().tenantId(), actor.selectedContext().customerId(), proposal.proposalId());
    if (proposal.status() != GovernancePolicyProposal.Status.APPROVED || rollbackReference == null || rollbackReference.isBlank() || simulations.isEmpty()) {
      return action("approval-required", "Policy activation is blocked until an approved proposal, simulation evidence, authority check, idempotency key, and rollback reference are present.", activationBlocked(correlationId, "Approved proposal, simulation evidence, and rollback metadata are required before activation."), List.of(trace("activation-blocked", correlationId)));
    }
    var activated = repository.saveProposal(proposal.activated(safe(rollbackReference), correlationId, Instant.now(clock)));
    authContextResolver.appendProtectedReadTrace(actor, ACTIVATE_CAPABILITY, "governance policy activation", correlationId);
    if (attentionProducerService != null) attentionProducerService.resolveGovernanceApproval(activated, "activated", correlationId);
    return action("accepted", "Approved Governance/Policy proposal activated by backend-owned deterministic lifecycle with rollback metadata; no model or frontend state granted authority.", decision(actor, activated, correlationId), List.of(trace("activation", correlationId)));
  }

  public ActionResult rollbackProposal(AuthContextResolver.ResolvedMe actor, Object input, String idempotencyKey, String correlationId) {
    validateScope(actor, input, correlationId);
    requireVisible(actor, ROLLBACK_CAPABILITY, correlationId);
    if (idempotencyKey == null || idempotencyKey.isBlank()) return validation("idempotencyKey", "Rollback requires a client-generated idempotency key.", correlationId);
    var proposal = findScopedProposal(actor, input);
    if (proposal == null || proposal.status() != GovernancePolicyProposal.Status.ACTIVATED || proposal.rollbackReference() == null || proposal.rollbackReference().isBlank()) {
      return action("blocked-runtime", "Rollback requires an activated proposal with stored rollback metadata; this starter fails closed instead of fabricating rollback state.", rollbackBlocked(correlationId), List.of(trace("rollback-blocked", correlationId)));
    }
    var rolledBack = repository.saveProposal(proposal.rolledBack(correlationId, Instant.now(clock)));
    authContextResolver.appendProtectedReadTrace(actor, ROLLBACK_CAPABILITY, "governance policy rollback", correlationId);
    if (attentionProducerService != null) attentionProducerService.resolveGovernanceApproval(rolledBack, "rolled_back", correlationId);
    return action("accepted", "Governance/Policy rollback recorded through backend-owned deterministic lifecycle.", decision(actor, rolledBack, correlationId), List.of(trace("rollback", correlationId)));
  }

  public ActionResult recordOutcomeNote(AuthContextResolver.ResolvedMe actor, Object input, String idempotencyKey, String correlationId) {
    validateScope(actor, input, correlationId);
    requireVisible(actor, OUTCOMES_RECORD_CAPABILITY, correlationId);
    if (idempotencyKey == null || idempotencyKey.isBlank()) return validation("idempotencyKey", "Outcome notes require a client-generated idempotency key.", correlationId);
    var proposal = findScopedProposal(actor, input);
    if (proposal == null) return validation("proposalId", "Outcome note requires a tenant-scoped governance proposal.", correlationId);
    var noted = repository.saveProposal(proposal.withOutcomeNote(safe(stringInput(input, "note", "Manual outcome note recorded for governance review.")), correlationId, Instant.now(clock)));
    authContextResolver.appendProtectedReadTrace(actor, OUTCOMES_RECORD_CAPABILITY, "governance outcome note", correlationId);
    return action("accepted", "Governance/Policy outcome note recorded with retained human authority and no direct authority change.", outcome(actor, noted, correlationId), List.of(trace("outcome-note", correlationId)));
  }

  private SurfaceData newDraftProposalSurface(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    var proposedContent = stringInput(input, "proposedContent", "Draft Governance/Policy policy clarification. Preserve backend authorization, human approval, idempotency, tenant isolation, and trace requirements.");
    var rationale = stringInput(input, "rationale", "Governance/Policy workstream draft proposal");
    var draftPreview = new GovernancePolicyProposal(
        "new-draft-preview-" + stableSuffix(actor.selectedContext().tenantId() + ":" + actor.account().accountId() + ":" + correlationId),
        actor.selectedContext().tenantId(), actor.selectedContext().customerId(), actor.account().accountId(), GovernancePolicyProposal.Status.DRAFT,
        stringInput(input, "policyId", "policy-human-approval"),
        stringInput(input, "title", "Governance policy proposal"),
        safe(rationale),
        safe(proposedContent),
        classifyRisk(proposedContent),
        List.of(READ_CAPABILITY, SIMULATE_CAPABILITY, APPROVE_CAPABILITY, ACTIVATE_CAPABILITY),
        List.of("governance-policy-workstream", "ToolPermissionBoundary", "AgentDefinition"),
        APPROVE_CAPABILITY,
        "rollback metadata required before activation; no direct mutation in preview, draft, or submit",
        null,
        correlationId,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        List.of(),
        Instant.now(clock),
        Instant.now(clock));
    var surface = proposal(actor, draftPreview, correlationId);
    var data = new LinkedHashMap<>(surface.data());
    data.put("state", "empty/new-draft");
    data.put("lifecycleState", "empty/new-draft");
    data.put("proposalId", null);
    data.put("proposalSummary", mapOf("proposalRef", "new draft", "title", draftPreview.title(), "purpose", draftPreview.rationale(), "lifecycleState", "empty/new-draft", "sourceSummary", "backend-authored draft defaults; save action required before persistence", "ownerReviewerDisplaySummary", "Tenant governance actor", "riskClassification", draftPreview.riskClassification(), "affectedCapabilitySummary", draftPreview.affectedCapabilityIds(), "freshnessStatus", "new-draft", "safeEmptyNewDraftCopy", "Complete draft fields and use the governed draft action with an idempotency key to persist an inert proposal."));
    return new SurfaceData(surface.surfaceId(), surface.surfaceType(), surface.title(), surface.traceIds(), data);
  }

  private SurfaceData proposal(AuthContextResolver.ResolvedMe actor, GovernancePolicyProposal proposal, String correlationId) {
    var lifecycleState = proposal.status().name().toLowerCase();
    var visibleCapabilities = actor.selectedContext().capabilities();
    var availableTransitions = new java.util.ArrayList<Map<String, Object>>();
    if (visibleCapabilities.contains(LEGACY_PROPOSE_CAPABILITY) && (proposal.status() == GovernancePolicyProposal.Status.DRAFT || proposal.status() == GovernancePolicyProposal.Status.CHANGES_REQUESTED)) availableTransitions.add(proposalAction("action-governance-policy-draft-proposal", "Save inert draft", LEGACY_PROPOSE_CAPABILITY, "draft-policy-proposal", "surface-governance-policy-proposal", true));
    if (visibleCapabilities.contains(LEGACY_PROPOSE_CAPABILITY) && proposal.status() == GovernancePolicyProposal.Status.DRAFT) availableTransitions.add(proposalAction("action-governance-policy-submit-proposal", "Submit draft for review", LEGACY_PROPOSE_CAPABILITY, "draft-policy-proposal", "surface-governance-policy-proposal", true));
    if (visibleCapabilities.contains(SIMULATE_CAPABILITY)) availableTransitions.add(proposalAction("action-governance-policy-simulate", "Open/start simulation", SIMULATE_CAPABILITY, "simulate-policy-change", "surface-governance-policy-simulation", false));
    if (visibleCapabilities.contains(APPROVE_CAPABILITY) && proposal.status() == GovernancePolicyProposal.Status.IN_REVIEW) availableTransitions.add(proposalAction("action-governance-policy-decide", "Open decision review", APPROVE_CAPABILITY, "approve-activate-or-rollback-policy", "surface-governance-policy-decision", true));
    if (visibleCapabilities.contains("governance.policy.impact_analysis.start")) availableTransitions.add(proposalAction("action-governance-policy-start-impact-analysis", "Start/read impact analysis", "governance.policy.impact_analysis.start", "start-policy-impact-analysis", "surface-governance-policy-impact-analysis-task", true));
    if (visibleCapabilities.contains("governance.policy.impact_analysis.read")) availableTransitions.add(proposalAction("action-governance-policy-read-impact-analysis", "Read impact analysis", "governance.policy.impact_analysis.read", "read-policy-impact-analysis", "surface-governance-policy-impact-analysis-task", false));
    if (visibleCapabilities.contains(OUTCOMES_RECORD_CAPABILITY)) availableTransitions.add(proposalAction("action-governance-policy-outcome-note", "Open outcome note", OUTCOMES_RECORD_CAPABILITY, "record-policy-outcome-note", "surface-governance-policy-outcome", true));
    var changeRows = List.of(mapOf("path", proposal.targetPolicyId(), "before", "active policy unchanged", "after", proposal.proposedContent(), "impact", "No authority changes before approval.", "riskLabel", proposal.riskClassification(), "redaction", "raw policy clauses and hidden role rules omitted"));
    return surface("surface-governance-policy-proposal", "governance-diff", "Policy proposal", correlationId, mapOf(
        "surfaceContract", "governance.policy.proposal.v1",
        "canonicalSurfaceId", "surface.governance.proposal_queue.v1",
        "proposalId", proposal.proposalId(),
        "tenantId", proposal.tenantId(),
        "customerId", proposal.customerId(),
        "state", lifecycleState,
        "lifecycleState", lifecycleState,
        "proposal lifecycle", lifecycleState,
        "summary", proposal.rationale(),
        "source", "GovernancePolicyService deterministic proposal lifecycle",
        "risk", proposal.riskClassification(),
        "riskClassification", proposal.riskClassification(),
        "requiredApproval", proposal.requiredApprovalCapabilityId(),
        "capabilityId", REVIEW_CAPABILITY,
        "capabilityClass", "approval/governance",
        "affectedCapabilityIds", proposal.affectedCapabilityIds(),
        "affectedArtifacts", proposal.affectedArtifactRefs(),
        "proposalSummary", mapOf("proposalRef", proposal.proposalId(), "title", proposal.title(), "purpose", proposal.rationale(), "lifecycleState", lifecycleState, "sourceSummary", "GovernancePolicyService deterministic proposal lifecycle", "ownerReviewerDisplaySummary", "Tenant governance actor / backend-selected reviewer", "riskClassification", proposal.riskClassification(), "affectedCapabilitySummary", proposal.affectedCapabilityIds(), "freshnessStatus", "ready", "safeEmptyNewDraftCopy", "Use a governed draft action with an idempotency key for new proposals."),
        "changeSet", mapOf("beforeSummary", "Active policy remains unchanged.", "afterSummary", "Proposed governance change stays inert until simulation, human approval, activation, and rollback metadata checks.", "changedPolicyAreas", List.of(proposal.targetPolicyId()), "capabilityEffects", proposal.affectedCapabilityIds(), "diffRows", changeRows, "validationMessages", List.of(), "rowRedactionMarkers", List.of("privileged policy clauses omitted", "hidden authority state omitted")),
        "draftFields", mapOf("editable", visibleCapabilities.contains(LEGACY_PROPOSE_CAPABILITY) && (proposal.status() == GovernancePolicyProposal.Status.DRAFT || proposal.status() == GovernancePolicyProposal.Status.CHANGES_REQUESTED), "draftIntent", proposal.title(), "rationale", proposal.rationale(), "sourceArtifactReference", proposal.targetPolicyId(), "requestedEffectiveScopeSummary", "selected AuthContext only; browser tenant/customer hints cannot expand scope", "riskJustification", proposal.riskClassification(), "reviewerNote", proposal.decisionRationale(), "validationErrors", List.of()),
        "lifecycleGate", mapOf("requiredApprovals", List.of(proposal.requiredApprovalCapabilityId()), "simulationEvidenceStatus", "advisory evidence required before activation", "impactAnalysisStatus", "blocked_provider_or_runtime_until_provider_configured", "activationRollbackGateSummary", proposal.rollbackReference() == null ? "rollback metadata required before activation" : proposal.rollbackReference(), "disabledTransitionReasons", List.of("approval, activation, rollback, and evidence disposition stay on dedicated backend-governed surfaces"), "blockedProviderOrRuntimeStatus", "blocked_provider_or_runtime"),
        "availableTransitions", List.copyOf(availableTransitions),
        "decisionMetadata", mapOf("recommendation", proposal.decision(), "decisionState", lifecycleState, "reviewerDisplayLabel", "backend-selected governance reviewer", "decisionSummary", proposal.decisionRationale(), "outcomeNoteSummaries", proposal.outcomeNotes(), "activationRollbackPrerequisites", "simulation evidence, human approval, backend authority, idempotency, and rollback metadata"),
        "authorizedActions", List.copyOf(availableTransitions),
        "beforeSummary", "Active policy remains unchanged.",
        "afterSummary", "Proposed governance change stays inert until simulation, human approval, activation, and rollback metadata checks.",
        "changes", changeRows,
        "idempotency", mapOf("draftIdempotencyKey", proposal.idempotencyKey() == null ? "client-generated-required-on-save" : "role-gated", "submitIsNoOpWhenAlreadyInReview", true, "decisionActivationRollbackNoOp", true, "rawIdempotencyKey", "omitted"),
        "decision", mapOf("decision", proposal.decision(), "rationale", proposal.decisionRationale(), "decisionTraceId", proposal.decisionCorrelationId(), "activationTraceId", proposal.activationCorrelationId(), "rollbackReference", proposal.rollbackReference(), "rollbackTraceId", proposal.rollbackCorrelationId()),
        "outcomeNotes", proposal.outcomeNotes(),
        "traceLinks", List.of(trace("proposal", correlationId)),
        "traceRefs", List.of(trace("proposal", correlationId), trace("workstream-log", correlationId), trace("admin-audit", correlationId), trace("policy-decision", correlationId)),
        "readiness", mapOf("simulation", "ready_for_deterministic_advisory_evidence", "impactAnalysis", "blocked_provider_or_runtime", "providerRuntime", "blocked_provider_or_runtime", "noFakeSuccess", true),
        "systemStates", List.of("loading", "empty/new-draft", "ready", "editing-draft", "submitting", "validation-error", "forbidden/system-message", "conflict/stale", "partial-data", "blocked-provider-or-runtime", "read-only", "success/submitted", "failure"),
        "redaction", mapOf("omittedFieldKeys", OMITTED_FIELDS, "browserSafe", true, "hiddenCrossTenantEvidence", true, "privilegedPolicyClauses", "omitted", "hiddenAuthorityState", "omitted", "rawProviderModelData", "omitted", "rawToolPayloads", "omitted", "correlationIds", "role-gated", "idempotencyKeys", "role-gated"),
        "visibilitySplit", mapOf("defaultUserVisible", List.of("proposal title", "lifecycle/status label", "safe source/risk summaries", "before/after summary", "validation and blocker copy", "authorized next actions"), "onDemandDrilldown", List.of("proposal display ids", "detailed diff rows", "lifecycle history", "redacted source artifact details", "trace summaries"), "adminSupportAuditorOnly", List.of("capability ids", "policy-decision/admin-audit/workstream/agent-work refs", "denial/failure evidence"), "internalOnly", OMITTED_FIELDS),
        "noDirectMutation", true,
        "noFakeSuccess", true));
  }

  private SurfaceData emptySimulation(AuthContextResolver.ResolvedMe actor, GovernancePolicyProposal proposal, String correlationId) {
    var availableTransitions = simulationTransitions(actor, proposal);
    return surface("surface-governance-policy-simulation", "governance-diff", "Policy simulation", correlationId, mapOf(
        "surfaceContract", "governance.policy.simulation.v1",
        "ownerFunctionalAgentId", "governance-policy-agent",
        "simulationId", null,
        "proposalId", proposal == null ? null : proposal.proposalId(),
        "tenantId", actor.selectedContext().tenantId(),
        "customerId", actor.selectedContext().customerId(),
        "state", "empty/not-run",
        "lifecycleState", proposal == null ? "empty/not-run" : proposal.status().name().toLowerCase(),
        "simulationStatus", "not_run",
        "simulationSummary", proposal == null ? "No selected proposal is available for simulation in this selected AuthContext." : "No simulation evidence has been recorded for this visible proposal; running simulation is advisory only.",
        "simulationSummaryPayload", mapOf("simulationDisplayRef", "not-run", "proposalDisplayRef", proposal == null ? "none-selected" : proposal.proposalId(), "proposalTitle", proposal == null ? "No proposal selected" : proposal.title(), "scenarioName", "default advisory policy-change scenario", "selectedScenarioScopeSummary", "backend-resolved selected AuthContext only", "simulationStatus", "empty/not-run", "freshnessStatus", "not-run", "confidenceLabel", "not-available", "safeEmptyNotRunCopy", "Select a visible proposal and use the governed simulation action to record advisory evidence."),
        "scenarioInputSummary", "default advisory policy-change scenario; tenant/customer hints from the browser cannot expand scope",
        "riskClassification", proposal == null ? "none" : proposal.riskClassification(),
        "requiredApproval", proposal == null ? APPROVE_CAPABILITY : proposal.requiredApprovalCapabilityId(),
        "activationStatus", "blocked until simulation evidence, human approval, backend authority, idempotency, and rollback metadata are present",
        "beforeSummary", proposal == null ? "No selected proposal is loaded." : "Active policy remains unchanged; no simulation has run for this proposal in the selected context.",
        "afterSummary", proposal == null ? "Choose an authorized proposal before running advisory simulation." : "Use the governed simulation action to produce advisory evidence; no approval or activation occurs here.",
        "changes", proposal == null ? List.of() : List.of(mapOf("path", proposal.targetPolicyId(), "before", "current policy behavior", "after", proposal.proposedContent(), "impact", "Simulation has not run; no authority changed.")),
        "affectedCapabilities", proposal == null ? List.of() : proposal.affectedCapabilityIds(),
        "affectedArtifacts", proposal == null ? List.of() : proposal.affectedArtifactRefs(),
        "expectedAccessChanges", List.of(),
        "expectedAllows", List.of(),
        "expectedDenials", List.of(),
        "warnings", List.of(mapOf("severity", "info", "reasonCode", "simulation_not_run", "message", "Simulation evidence is not recorded yet; approval and activation remain disabled.", "disabledActionReason", "simulation_evidence_missing", "recovery", "Run the governed simulation action for a visible proposal.")),
        "confidenceAndLimits", mapOf("confidenceLabel", "not-available", "coverageSummary", "No simulation has run.", "knownBlindSpots", List.of("No expected access rows until simulation evidence is recorded."), "providerRuntimeReadiness", "ready_for_deterministic_advisory_evidence", "advisoryOnly", true),
        "readiness", mapOf("simulation", "ready_for_deterministic_advisory_evidence", "impactAnalysis", "blocked_provider_or_runtime", "status", "not_run", "noFakeSuccess", true),
        "availableTransitions", availableTransitions,
        "authorizedActions", availableTransitions,
        "simulation", mapOf("affectedCapabilities", proposal == null ? List.of() : proposal.affectedCapabilityIds(), "expectedAllows", List.of(), "expectedDenials", List.of(), "warnings", List.of("Simulation not run; no fake success is exposed."), "confidence", "not-available", "evidenceTraceIds", List.of()),
        "evidenceTraceLinks", List.of(),
        "activationGate", mapOf("simulationEvidenceRequired", true, "gateState", "blocked", "missingPrerequisites", List.of("simulation evidence", "human approval", "rollback metadata"), "relatedImpactAnalysisStatus", "blocked_provider_or_runtime", "disabledApprovalActivationReasons", List.of("No simulation evidence has been recorded."), "nextAuthorizedTargetSurfaces", availableTransitions.stream().map(action -> action.get("resultSurfaceId")).distinct().toList()),
        "traceLinks", List.of(trace("simulation-read", correlationId)),
        "traceRefs", List.of(trace("simulation-read", correlationId), trace("workstream-log", correlationId)),
        "redaction", mapOf("browserSafe", true, "omittedFieldKeys", OMITTED_FIELDS, "hiddenCrossTenantEvidence", true, "privilegedPolicyClauses", "omitted", "hiddenAuthorityState", "omitted", "rawProviderModelData", "omitted", "rawToolPayloads", "omitted", "correlationIds", "role-gated", "idempotencyKeys", "role-gated"),
        "visibilitySplit", mapOf("defaultUserVisible", List.of("proposal title", "scenario label", "status", "safe blocker copy", "authorized next actions"), "onDemandDrilldown", List.of("simulation display refs", "redacted evidence summaries", "trace summaries"), "adminSupportAuditorOnly", List.of("capability ids", "denial/failure evidence"), "internalOnly", OMITTED_FIELDS),
        "noDirectMutation", true,
        "noFakeSuccess", true));
  }

  private SurfaceData simulation(AuthContextResolver.ResolvedMe actor, GovernancePolicyProposal proposal, GovernancePolicySimulationResult simulation, String correlationId) {
    var availableTransitions = simulationTransitions(actor, proposal);
    var expectedAccessChanges = new java.util.ArrayList<Map<String, Object>>();
    simulation.expectedAllows().forEach(allow -> expectedAccessChanges.add(mapOf("expectedOutcome", "allow", "summary", allow, "riskLabel", proposal.riskClassification(), "evidenceStatus", "simulated-advisory", "redaction", "raw policy clauses and hidden role rules omitted")));
    simulation.expectedDenials().forEach(denial -> expectedAccessChanges.add(mapOf("expectedOutcome", "deny", "summary", denial, "riskLabel", proposal.riskClassification(), "evidenceStatus", "simulated-advisory", "redaction", "raw policy clauses and hidden role rules omitted")));
    return surface("surface-governance-policy-simulation", "governance-diff", "Policy simulation", correlationId, mapOf(
        "surfaceContract", "governance.policy.simulation.v1",
        "ownerFunctionalAgentId", "governance-policy-agent",
        "simulationId", simulation.simulationId(),
        "proposalId", proposal.proposalId(),
        "tenantId", actor.selectedContext().tenantId(),
        "customerId", actor.selectedContext().customerId(),
        "state", "ready",
        "lifecycleState", proposal.status().name().toLowerCase(),
        "simulationStatus", simulation.status().name().toLowerCase(),
        "simulationSummary", "advisory deterministic simulation evidence record; no direct mutation and no model-owned authority",
        "simulationSummaryPayload", mapOf("simulationDisplayRef", simulation.simulationId(), "proposalDisplayRef", proposal.proposalId(), "proposalTitle", proposal.title(), "lifecycleStatusLabel", proposal.status().name().toLowerCase(), "scenarioName", simulation.scenarioInputSummary(), "selectedScenarioScopeSummary", "backend-resolved selected AuthContext only", "simulationStatus", simulation.status().name().toLowerCase(), "freshnessStatus", "ready", "confidenceLabel", "bounded-starter", "safeEmptyNotRunCopy", "Simulation evidence is present and remains advisory."),
        "scenarioInputSummary", simulation.scenarioInputSummary(),
        "riskClassification", proposal.riskClassification(),
        "requiredApproval", proposal.requiredApprovalCapabilityId(),
        "activationStatus", "blocked until simulation evidence, human approval, backend authority, idempotency, and rollback metadata are present",
        "beforeSummary", "Active policy remains unchanged while simulation is reviewed.",
        "afterSummary", "Proposed policy would require the listed allows/denials and approval gates before activation.",
        "changes", List.of(
            mapOf("path", proposal.targetPolicyId(), "before", "current policy behavior", "after", proposal.proposedContent(), "impact", "Simulation is advisory and cannot grant authority."),
            mapOf("path", "activation.gate", "before", "activation unavailable without approval", "after", "activation still unavailable until approval, idempotency, simulation evidence, and rollback metadata exist", "impact", "No direct mutation from simulation.")),
        "affectedCapabilities", proposal.affectedCapabilityIds(),
        "affectedArtifacts", proposal.affectedArtifactRefs(),
        "riskFindings", simulation.riskFindings(),
        "requiredApprovalCapabilityIds", simulation.requiredApprovalCapabilityIds(),
        "expectedAccessChanges", List.copyOf(expectedAccessChanges),
        "expectedDenials", simulation.expectedDenials(),
        "expectedAllows", simulation.expectedAllows(),
        "warnings", simulation.warnings().stream().map(warning -> mapOf("severity", "warning", "reasonCode", stableSuffix(warning), "message", warning, "disabledActionReason", warning.toLowerCase().contains("activation") ? "activation_prerequisite_missing" : "advisory_limit", "recovery", "Review proposal, decision, impact-analysis, or outcome follow-up through backend-authorized transitions.")).toList(),
        "confidence", "bounded-starter",
        "confidenceAndLimits", mapOf("confidenceLabel", "bounded-starter", "coverageSummary", "Deterministic starter simulation covers selected AuthContext, approval gates, tenant isolation, redaction, and activation blockers.", "knownBlindSpots", List.of("No raw provider/model output is produced by this deterministic path.", "Impact analysis remains blocked_provider_or_runtime until model/runtime is configured."), "lastSimulatedAgeBucket", "recent", "providerRuntimeReadiness", "ready_for_deterministic_advisory_evidence", "advisoryOnly", true),
        "readiness", mapOf("simulation", "ready_for_deterministic_advisory_evidence", "impactAnalysis", "blocked_provider_or_runtime", "status", "ready", "noFakeSuccess", true),
        "availableTransitions", availableTransitions,
        "authorizedActions", availableTransitions,
        "simulation", mapOf("affectedCapabilities", proposal.affectedCapabilityIds(), "expectedAllows", simulation.expectedAllows(), "expectedDenials", simulation.expectedDenials(), "warnings", simulation.warnings(), "confidence", "bounded-starter", "evidenceTraceIds", simulation.evidenceRefs()),
        "evidenceTraceLinks", simulation.evidenceRefs(),
        "activationGate", mapOf("simulationEvidenceRequired", true, "gateState", "blocked_until_human_approval_and_rollback_metadata", "missingPrerequisites", List.of("human approval", "rollback metadata", "activation idempotency key"), "relatedImpactAnalysisStatus", "blocked_provider_or_runtime", "disabledApprovalActivationReasons", List.of("Simulation is advisory evidence only; approval and activation stay on dedicated backend-governed surfaces."), "nextAuthorizedTargetSurfaces", availableTransitions.stream().map(action -> action.get("resultSurfaceId")).distinct().toList()),
        "traceLinks", List.of(trace("simulation", correlationId), simulation.correlationId()),
        "traceRefs", List.of(trace("simulation", correlationId), trace("workstream-log", correlationId), trace("admin-audit", correlationId), trace("policy-decision", correlationId), simulation.correlationId()),
        "redaction", mapOf("browserSafe", true, "omittedFieldKeys", OMITTED_FIELDS, "hiddenCrossTenantEvidence", true, "privilegedPolicyClauses", "omitted", "hiddenAuthorityState", "omitted", "rawProviderModelData", "omitted", "rawToolPayloads", "omitted", "correlationIds", "role-gated", "idempotencyKeys", "role-gated"),
        "visibilitySplit", mapOf("defaultUserVisible", List.of("proposal title", "scenario label", "status", "safe expected allow/deny summaries", "warning and blocker copy", "authorized next actions"), "onDemandDrilldown", List.of("simulation display refs", "scenario inputs", "redacted evidence summaries", "trace summaries"), "adminSupportAuditorOnly", List.of("capability ids", "policy-decision/admin-audit/workstream/agent-work refs", "denial/failure evidence"), "internalOnly", OMITTED_FIELDS),
        "noDirectMutation", true,
        "noFakeSuccess", true));
  }

  private SurfaceData decision(AuthContextResolver.ResolvedMe actor, GovernancePolicyProposal proposal, String correlationId) {
    var status = proposal.status().name().toLowerCase();
    return surface("surface-governance-policy-decision", "decision", "Governance decision", correlationId, mapOf(
        "surfaceContract", "governance.policy.decision.v1",
        "canonicalSurfaceId", "surface.governance.decision_card.v1",
        "decisionId", "decision-" + stableSuffix(firstNonBlank(proposal.decisionCorrelationId(), correlationId)),
        "proposalId", proposal.proposalId(),
        "status", status,
        "decision", proposal.decision(),
        "actor", actor.account().accountId(),
        "recommendation", decisionRecommendation(proposal),
        "riskScore", riskScore(proposal.riskClassification()),
        "confidenceScore", 80,
        "risk", proposal.riskClassification(),
        "impact", "Affected capabilities: " + String.join(", ", proposal.affectedCapabilityIds()),
        "affectedTarget", proposal.targetPolicyId(),
        "policyBasis", REVIEW_CAPABILITY + " + " + proposal.requiredApprovalCapabilityId(),
        "idempotencyKeySource", "client-generated or surface-item per action contract",
        "activationBlocker", proposal.status() == GovernancePolicyProposal.Status.APPROVED ? "Activation still requires simulation evidence, backend authority, idempotency, current approved state, and rollback metadata." : null,
        "authorityBasis", REVIEW_CAPABILITY,
        "activationCapabilityId", ACTIVATE_PROPOSAL_CAPABILITY,
        "outcomeCapabilityId", OUTCOMES_RECORD_CAPABILITY,
        "rationale", proposal.decisionRationale(),
        "result", proposal.status() == GovernancePolicyProposal.Status.ACTIVATED ? "activated-with-rollback-metadata" : status,
        "rollback metadata", proposal.rollbackReference() == null ? "required before activation" : proposal.rollbackReference(),
        "evidence", List.of(
            mapOf("evidenceId", "proposal", "label", "Proposal", "summary", proposal.title() + " · " + status),
            mapOf("evidenceId", "authority", "label", "Authority basis", "summary", "Backend requires " + proposal.requiredApprovalCapabilityId() + "; frontend visibility is not authority."),
            mapOf("evidenceId", "redaction", "label", "Redaction", "summary", "Raw prompts, provider secrets, hidden authority, raw tool payloads, and cross-tenant evidence are omitted.")),
        "alternatives", List.of("Request changes for more evidence", "Reject the proposal", "Keep approved proposals inactive until rollback metadata and simulation evidence are attached"),
        "allowedActions", List.of(
            mapOf("actionId", "action-governance-policy-decide", "label", "Approve, reject, or request changes", "browserToolId", "action-governance-policy-decide", "governedToolId", REVIEW_CAPABILITY, "capabilityId", APPROVE_CAPABILITY),
            mapOf("actionId", "action-governance-policy-outcome-note", "label", "Add outcome note", "browserToolId", "action-governance-policy-outcome-note", "governedToolId", OUTCOMES_RECORD_CAPABILITY, "capabilityId", OUTCOMES_RECORD_CAPABILITY),
            mapOf("actionId", "action-open-audit-trace", "label", "Open audit trace", "browserToolId", "action-open-audit-trace", "governedToolId", "audit.trace.read", "capabilityId", "audit.trace.read")),
        "disabledActions", List.of(mapOf("actionId", "action-governance-policy-activate", "label", "Activate approved policy", "reason", "Activation is a separate backend-governed command and remains blocked until all activation prerequisites are present.")),
        "auditCorrelationId", correlationId,
        "traceLinks", List.of(trace("decision", correlationId)),
        "outcomeNotes", proposal.outcomeNotes(),
        "noDirectMutation", true));
  }

  private SurfaceData outcome(AuthContextResolver.ResolvedMe actor, GovernancePolicyProposal proposal, String correlationId) {
    return surface("surface-governance-policy-outcome", "outcome-panel", "Governance outcome", correlationId, mapOf(
        "surfaceContract", "governance.policy.outcome.v1",
        "outcomeId", "outcome-" + stableSuffix(proposal.proposalId() + ":" + correlationId),
        "proposalId", proposal.proposalId(),
        "status", proposal.status().name().toLowerCase(),
        "summary", "Outcome note recorded for Governance/Policy proposal " + proposal.proposalId() + "; source policy lifecycle remains backend-authoritative.",
        "decisionState", proposal.status().name().toLowerCase(),
        "metrics", List.of(
            mapOf("metricId", "outcome-notes", "label", "Outcome notes", "current", proposal.outcomeNotes().size(), "target", 1, "unit", "notes"),
            mapOf("metricId", "affected-capabilities", "label", "Affected capabilities", "current", proposal.affectedCapabilityIds().size(), "target", Math.max(1, proposal.affectedCapabilityIds().size()), "unit", "capabilities")),
        "recommendations", List.of(mapOf("recommendationId", "review-outcomes", "label", "Review observed outcomes", "summary", "Use outcome notes to decide whether follow-up simulation, rollback, or a new proposal is needed.")),
        "evidenceRefs", List.of(mapOf("refId", proposal.proposalId(), "label", proposal.title(), "summary", String.join("; ", proposal.outcomeNotes()), "traceId", trace("outcome-note", correlationId))),
        "traceRefs", List.of(trace("outcome-note", correlationId)),
        "noDirectMutation", true,
        "redaction", "browser-safe outcome note; secrets, hidden authority, raw prompts, and cross-tenant evidence omitted",
        "capabilityId", OUTCOMES_RECORD_CAPABILITY,
        "actor", actor.account().accountId()));
  }

  private List<Map<String, Object>> simulationTransitions(AuthContextResolver.ResolvedMe actor, GovernancePolicyProposal proposal) {
    var visibleCapabilities = actor.selectedContext().capabilities();
    var actions = new java.util.ArrayList<Map<String, Object>>();
    actions.add(proposalAction("action-governance-policy-read", "Open proposal lifecycle evidence", READ_CAPABILITY, "list-policy-proposals", proposal == null ? "surface-governance-policy-inventory" : "surface-governance-policy-proposal", false));
    if (visibleCapabilities.contains(SIMULATE_CAPABILITY) && proposal != null) actions.add(proposalAction("action-governance-policy-simulate", "Run or rerun advisory simulation", SIMULATE_CAPABILITY, "simulate-policy-change", "surface-governance-policy-simulation", true));
    if (visibleCapabilities.contains(APPROVE_CAPABILITY) && proposal != null) actions.add(proposalAction("action-governance-policy-decide", "Open decision review", APPROVE_CAPABILITY, "approve-activate-or-rollback-policy", "surface-governance-policy-decision", true));
    if (visibleCapabilities.contains("governance.policy.impact_analysis.start") && proposal != null) actions.add(proposalAction("action-governance-policy-start-impact-analysis", "Start impact analysis", "governance.policy.impact_analysis.start", "start-policy-impact-analysis", "surface-governance-policy-impact-analysis-task", true));
    if (visibleCapabilities.contains("governance.policy.impact_analysis.read") && proposal != null) actions.add(proposalAction("action-governance-policy-read-impact-analysis", "Read impact analysis", "governance.policy.impact_analysis.read", "read-policy-impact-analysis", "surface-governance-policy-impact-analysis-task", false));
    if (visibleCapabilities.contains(OUTCOMES_RECORD_CAPABILITY) && proposal != null) actions.add(proposalAction("action-governance-policy-outcome-note", "Open outcome note", OUTCOMES_RECORD_CAPABILITY, "record-policy-outcome-note", "surface-governance-policy-outcome", true));
    return List.copyOf(actions);
  }

  private Map<String, Object> proposalAction(String actionId, String label, String capabilityId, String governedToolId, String resultSurfaceId, boolean idempotencyRequired) {
    return mapOf("actionId", actionId, "label", label, "capabilityId", capabilityId, "governedToolId", governedToolId, "resultSurfaceId", resultSurfaceId, "idempotencyRequired", idempotencyRequired, "redaction", "browser-safe action metadata; raw idempotency/correlation values omitted");
  }

  private SurfaceData activationBlocked(String correlationId, String reason) {
    return systemMessage("surface-governance-policy-activation-blocked", "approval-required", reason + " Approved proposal, current version, activation target, rollback metadata, backend authority, and idempotency are required.", ACTIVATE_PROPOSAL_CAPABILITY, correlationId);
  }

  private SurfaceData rollbackBlocked(String correlationId) {
    return systemMessage("surface-governance-policy-rollback-blocked", "blocked-runtime", "Rollback requires an activated proposal with stored rollback metadata; no deterministic or model-less rollback success is fabricated.", ROLLBACK_CAPABILITY, correlationId);
  }

  private SurfaceData systemMessage(String surfaceId, String status, String message, String capability, String correlationId) {
    return new SurfaceData(surfaceId, "system_message", "Governance/Policy " + status, List.of(trace(status, correlationId)), mapOf("surfaceContract", "governance.policy.system_message.v1", "status", status, "message", message, "severity", "warning", "requiredCapabilityId", capability, "system_message", true, "sideEffect", "none", "traceLinks", List.of(trace(status, correlationId)), "redaction", "browser-safe", "noDirectMutation", true));
  }

  private GovernancePolicyProposal findScopedProposal(AuthContextResolver.ResolvedMe actor, Object input) {
    var proposalId = stringInput(input, "proposalId", null);
    if (proposalId == null) return repository.listProposals(actor.selectedContext().tenantId(), actor.selectedContext().customerId()).stream().findFirst().orElse(null);
    return repository.findProposal(actor.selectedContext().tenantId(), actor.selectedContext().customerId(), proposalId).orElse(null);
  }

  private void requireRead(AuthContextResolver.ResolvedMe actor, String capability, String correlationId) {
    requireVisible(actor, READ_CAPABILITY, correlationId);
    authContextResolver.appendProtectedReadTrace(actor, capability, "governance policy protected read", correlationId);
  }

  private void requireProposal(AuthContextResolver.ResolvedMe actor, String capability, String correlationId) {
    requireVisible(actor, LEGACY_PROPOSE_CAPABILITY, correlationId);
    authContextResolver.appendProtectedReadTrace(actor, capability, "governance policy proposal lifecycle", correlationId);
  }

  private void requireVisible(AuthContextResolver.ResolvedMe actor, String capability, String correlationId) {
    if (!actor.selectedContext().capabilities().contains(capability)) {
      authContextResolver.appendDeniedTrace(actor, "GOVERNANCE_POLICY_CAPABILITY_DENIED", capability, correlationId);
      throw new AuthorizationException(403, "missing-capability:" + capability);
    }
  }

  private void validateScope(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    if (input instanceof Map<?, ?> map) {
      var tenantId = map.get("tenantId") instanceof String value ? value : null;
      var customerId = map.get("customerId") instanceof String value ? value : null;
      if (tenantId != null && !tenantId.isBlank() && !tenantId.equals(actor.selectedContext().tenantId())) {
        authContextResolver.appendDeniedTrace(actor, "GOVERNANCE_POLICY_SCOPE_DENIED", "tenant-mismatch", correlationId);
        throw new AuthorizationException(403, "GOVERNANCE_POLICY_TENANT_FORBIDDEN");
      }
      if (customerId != null && actor.selectedContext().customerId() != null && !customerId.equals(actor.selectedContext().customerId())) {
        authContextResolver.appendDeniedTrace(actor, "GOVERNANCE_POLICY_SCOPE_DENIED", "customer-mismatch", correlationId);
        throw new AuthorizationException(403, "GOVERNANCE_POLICY_CUSTOMER_FORBIDDEN");
      }
    }
  }

  private ActionResult validation(String field, String message, String correlationId) {
    return action("validation-error", message, new SurfaceData("surface-governance-policy-validation-error", "system_message", "Governance/Policy validation", List.of(trace("validation", correlationId)), mapOf("surfaceContract", "governance.policy.system_message.v1", "status", "validation-error", "field", field, "message", message, "severity", "warning", "system_message", true, "sideEffect", "none", "traceLinks", List.of(trace("validation", correlationId)), "redaction", "browser-safe validation only", "noDirectMutation", true, "noFakeSuccess", true)), List.of(trace("validation", correlationId)));
  }

  private ActionResult action(String status, String message, SurfaceData surface, List<String> traceIds) {
    return new ActionResult(status, message, traceIds, surface);
  }

  private SurfaceData surface(String id, String type, String title, String correlationId, Map<String, Object> data) {
    return new SurfaceData(id, type, title, List.of(trace(id.replace("surface-governance-policy-", ""), correlationId)), data);
  }

  private Map<String, Object> proposalRow(AuthContextResolver.ResolvedMe actor, GovernancePolicyProposal proposal) {
    var status = proposal.status().name().toLowerCase().replace('_', '-');
    var authorizedRowActions = new java.util.ArrayList<String>();
    authorizedRowActions.add("action-governance-policy-read");
    if (actor.selectedContext().capabilities().contains(SIMULATE_CAPABILITY)) authorizedRowActions.add("action-governance-policy-simulate");
    if (actor.selectedContext().capabilities().contains(APPROVE_CAPABILITY) && proposal.status() == GovernancePolicyProposal.Status.IN_REVIEW) authorizedRowActions.add("action-governance-policy-decide");
    if (actor.selectedContext().capabilities().contains("governance.policy.impact_analysis.start")) authorizedRowActions.add("action-governance-policy-start-impact-analysis");
    if (actor.selectedContext().capabilities().contains(OUTCOMES_RECORD_CAPABILITY) && (proposal.status() == GovernancePolicyProposal.Status.ACTIVATED || proposal.status() == GovernancePolicyProposal.Status.ROLLED_BACK)) authorizedRowActions.add("action-governance-policy-outcome-note");
    return mapOf(
        "policyId", proposal.targetPolicyId(),
        "proposalRef", proposal.proposalId(),
        "proposalId", proposal.proposalId(),
        "title", proposal.title(),
        "name", proposal.title(),
        "type", "proposal",
        "lifecycle", status,
        "status", status,
        "riskClassification", proposal.riskClassification(),
        "affectedCapabilityIds", proposal.affectedCapabilityIds(),
        "affectedCapabilitySummaries", proposal.affectedCapabilityIds(),
        "sourceArtifact", "GovernancePolicyProposal",
        "sourceArtifactSummary", "Backend-owned policy proposal lifecycle record",
        "owner", proposal.createdByAccountId(),
        "ownerDisplay", "Tenant governance actor",
        "simulationEvidenceStatus", "advisory-required-before-activation",
        "approvalReadiness", proposal.status() == GovernancePolicyProposal.Status.IN_REVIEW ? "human-review-required" : status,
        "activationReadiness", proposal.status() == GovernancePolicyProposal.Status.APPROVED ? "blocked-until-simulation-and-rollback-metadata" : "not-ready",
        "outcomeImpactAnalysisSummary", "Impact analysis is fail-closed until provider/runtime is configured; no fake success is exposed.",
        "lastActivityAge", "recent backend lifecycle activity",
        "safeTraceSummary", "Trace refs are browser-safe summaries; raw ids are role-gated.",
        "lastChangeTraceId", firstNonBlank(proposal.activationCorrelationId(), proposal.decisionCorrelationId(), proposal.submittedCorrelationId(), proposal.createdCorrelationId()),
        "rowRedaction", "cross-tenant evidence, hidden authority state, raw prompts, raw tool payloads, JWTs, secrets, correlation ids, and idempotency keys omitted",
        "targetSurfaceId", "surface-governance-policy-detail",
        "openActionId", "action-governance-policy-read",
        "rowActionId", "action-governance-policy-read",
        "authorizedActionIds", List.copyOf(authorizedRowActions),
        "safeActionContext", mapOf("proposalId", proposal.proposalId(), "policyId", proposal.targetPolicyId()),
        "version", 1,
        "requiredApproval", proposal.requiredApprovalCapabilityId(),
        "redacted", true);
  }

  private static boolean matchesInventoryFilter(Map<String, Object> row, String search, String lifecycle) {
    var normalizedSearch = search == null ? "" : search.toLowerCase().trim();
    var normalizedLifecycle = lifecycle == null ? "" : lifecycle.toLowerCase().trim().replace('_', '-');
    if (!normalizedLifecycle.isBlank() && !"all".equals(normalizedLifecycle)) {
      var status = Objects.toString(row.get("status"), "").toLowerCase().replace('_', '-');
      var rowLifecycle = Objects.toString(row.get("lifecycle"), status).toLowerCase().replace('_', '-');
      if (!status.contains(normalizedLifecycle) && !rowLifecycle.contains(normalizedLifecycle)) return false;
    }
    if (normalizedSearch.isBlank()) return true;
    var haystack = String.join(" ", Objects.toString(row.get("policyId"), ""), Objects.toString(row.get("proposalId"), ""), Objects.toString(row.get("name"), ""), Objects.toString(row.get("title"), ""), Objects.toString(row.get("type"), ""), Objects.toString(row.get("sourceArtifact"), ""), Objects.toString(row.get("affectedCapabilityIds"), "")).toLowerCase();
    return haystack.contains(normalizedSearch);
  }

  private static Map<String, Object> inventoryAction(String actionId, String label, String capabilityId, String governedToolId, String resultSurfaceId, boolean idempotencyRequired) {
    return mapOf("actionId", actionId, "label", label, "capabilityId", capabilityId, "governedToolId", governedToolId, "resultSurfaceId", resultSurfaceId, "idempotencyRequired", idempotencyRequired);
  }

  private static Map<String, Object> lifecycleCounts(List<Map<String, Object>> rows) {
    return mapOf(
        "active", countRows(rows, "active"),
        "draft", countRows(rows, "draft"),
        "submitted", countRows(rows, "submitted"),
        "in-review", countRows(rows, "in-review"),
        "changes-requested", countRows(rows, "changes-requested"),
        "approved", countRows(rows, "approved"),
        "rejected", countRows(rows, "rejected"),
        "activated", countRows(rows, "activated"),
        "rolled-back", countRows(rows, "rolled-back"),
        "blocked", countRows(rows, "blocked"));
  }

  private static long countRows(List<Map<String, Object>> rows, String state) {
    return rows.stream().filter(row -> Objects.toString(row.get("status"), "").toLowerCase().replace('_', '-').equals(state)).count();
  }

  private static String selectedFiltersSummary(String search, String lifecycle) {
    var selected = new java.util.ArrayList<String>();
    if (search != null && !search.isBlank()) selected.add("search=" + safe(search));
    if (lifecycle != null && !lifecycle.isBlank() && !"all".equalsIgnoreCase(lifecycle)) selected.add("lifecycle=" + safe(lifecycle));
    return selected.isEmpty() ? "all authorized policy/proposal rows" : String.join(", ", selected);
  }

  private static String contextLabel(AuthContextResolver.ResolvedMe actor) {
    var selected = actor.selectedContext();
    return selected.scopeType().name().toLowerCase().replace('_', '-') + " " + selected.tenantId() + (selected.customerId() == null ? "" : " / " + selected.customerId());
  }

  private static String decisionRecommendation(GovernancePolicyProposal proposal) {
    return switch (proposal.status()) {
      case DRAFT -> "Submit for review only after rationale, affected capabilities, tests, and rollback expectations are documented.";
      case IN_REVIEW -> "Approve only if simulation evidence and authority basis are sufficient; otherwise request changes or reject.";
      case APPROVED -> "Keep activation separate until simulation evidence, idempotency, and rollback metadata are present.";
      case ACTIVATED -> "Monitor outcomes and retain rollback readiness.";
      case ROLLED_BACK -> "Review outcome notes and decide whether a safer replacement proposal is needed.";
      case REJECTED, CHANGES_REQUESTED, BLOCKED -> "Do not activate; record outcome or draft a revised proposal if needed.";
    };
  }

  private static int riskScore(String riskClassification) {
    return switch (riskClassification == null ? "" : riskClassification.toLowerCase()) {
      case "high" -> 85;
      case "medium" -> 65;
      default -> 35;
    };
  }

  private List<Map<String, Object>> policies() {
    return List.of(
        policy("policy-agent-tool-boundary", "ToolPermissionBoundary grants", "active", "agent-governance-policy", List.of("agent.skills.read", "agent.references.read"), "trace-tool-boundary-governance"),
        policy("policy-provider-fail-closed", "Provider fail-closed policy", "active", "starter-default-model-policy", List.of("agent.user_admin.use", READ_CAPABILITY), "trace-provider-fail-closed"),
        policy("policy-human-approval", "Human approval for authority changes", "active", "governance-policy-workstream", List.of(APPROVE_CAPABILITY, ACTIVATE_CAPABILITY, ROLLBACK_CAPABILITY), "trace-human-approval"));
  }

  private Map<String, Object> policy(String id, String name, String status, String source, List<String> capabilityIds, String traceId) {
    return mapOf(
        "policyId", id,
        "proposalRef", id,
        "title", name,
        "name", name,
        "type", "governance",
        "lifecycle", status,
        "status", status,
        "riskClassification", "baseline",
        "affectedCapabilityIds", capabilityIds,
        "affectedCapabilitySummaries", capabilityIds,
        "sourceArtifact", source,
        "sourceArtifactSummary", "Starter policy concept projected by GovernancePolicyService",
        "owner", "Governance/Policy",
        "ownerDisplay", "Governance/Policy",
        "simulationEvidenceStatus", "not-required-for-active-baseline-policy",
        "approvalReadiness", "active-read-only",
        "activationReadiness", "already-active-policy-concept",
        "outcomeImpactAnalysisSummary", "Read-only concept row; impact analysis only applies to proposal rows and fails closed until provider/runtime is configured.",
        "lastActivityAge", "starter baseline",
        "safeTraceSummary", "Trace refs are browser-safe summaries; raw ids are role-gated.",
        "lastChangeTraceId", traceId,
        "rowRedaction", "raw policy internals, prompts, tool payloads, JWTs, secrets, hidden scopes, and correlation/idempotency details omitted",
        "targetSurfaceId", "surface-governance-policy-detail",
        "openActionId", "action-governance-policy-read",
        "rowActionId", "action-governance-policy-read",
        "authorizedActionIds", List.of("action-governance-policy-read"),
        "safeActionContext", mapOf("policyId", id),
        "version", 1,
        "redacted", true);
  }

  private static String classifyRisk(String content) {
    var normalized = content == null ? "" : content.toLowerCase();
    return normalized.contains("tool") || normalized.contains("activate") || normalized.contains("authority") ? "medium" : "low";
  }

  private static String safe(String value) {
    if (value == null || value.isBlank()) return "unspecified";
    return value.replaceAll("(?i)(api[_-]?key|secret|token)=[^\\s,;]+", "$1=[REDACTED]");
  }

  private static String stringInput(Object input, String key, String fallback) {
    if (input instanceof Map<?, ?> map && map.get(key) instanceof String value && !value.isBlank()) return value;
    return fallback;
  }

  private static String stableSuffix(String value) {
    return Integer.toUnsignedString(Objects.requireNonNullElse(value, "governance-policy").hashCode(), 36);
  }

  private static String firstNonBlank(String... values) {
    for (var value : values) if (value != null && !value.isBlank()) return value;
    return "governance-policy";
  }

  private static String trace(String label, String correlationId) {
    return "trace-governance-policy-" + label + "-" + stableSuffix(correlationId);
  }

  private static Map<String, Object> mapOf(Object... values) {
    var map = new LinkedHashMap<String, Object>();
    for (int i = 0; i + 1 < values.length; i += 2) map.put(String.valueOf(values[i]), values[i + 1]);
    return map;
  }

  public record SurfaceData(String surfaceId, String surfaceType, String title, List<String> traceIds, Map<String, Object> data) {}
  public record ActionResult(String status, String message, List<String> traceIds, SurfaceData surface) {}
}
