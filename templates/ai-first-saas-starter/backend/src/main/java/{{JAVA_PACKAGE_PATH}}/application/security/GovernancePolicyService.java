package {{JAVA_BASE_PACKAGE}}.application.security;

import {{JAVA_BASE_PACKAGE}}.domain.security.GovernancePolicyProposal;
import java.time.Clock;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Deterministic Governance/Policy service for scoped reads and inert proposal lifecycle. */
public final class GovernancePolicyService {
  public static final String READ_CAPABILITY = "governance.policy.read";
  public static final String DASHBOARD_CAPABILITY = "governance.policy.dashboard.read";
  public static final String LIST_CAPABILITY = "governance.policy.list";
  public static final String PROPOSAL_DRAFT_CAPABILITY = "governance.policy.proposal.draft";
  public static final String PROPOSAL_SUBMIT_CAPABILITY = "governance.policy.proposal.submit";
  public static final String PROPOSAL_READ_CAPABILITY = "governance.policy.proposal.read";
  public static final String LEGACY_PROPOSE_CAPABILITY = "governance.policy.propose";
  public static final String APPROVE_CAPABILITY = "governance.policy.approve";
  public static final String ACTIVATE_CAPABILITY = "governance.policy.activate";
  public static final String ROLLBACK_CAPABILITY = "governance.policy.rollback";
  private static final List<String> OMITTED_FIELDS = List.of("rawPrompt", "hiddenPromptText", "rawProviderCredential", "rawToolPayload", "providerSecret", "api_key");

  private final GovernancePolicyRepository repository;
  private final AuthContextResolver authContextResolver;
  private final Clock clock;

  public GovernancePolicyService(GovernancePolicyRepository repository, AuthContextResolver authContextResolver, Clock clock) {
    this.repository = Objects.requireNonNull(repository);
    this.authContextResolver = Objects.requireNonNull(authContextResolver);
    this.clock = Objects.requireNonNull(clock);
  }

  public SurfaceData dashboard(AuthContextResolver.ResolvedMe actor, String correlationId) {
    requireRead(actor, DASHBOARD_CAPABILITY, correlationId);
    var proposals = repository.listProposals(actor.selectedContext().tenantId(), actor.selectedContext().customerId());
    var pending = proposals.stream().filter(proposal -> proposal.status() == GovernancePolicyProposal.Status.DRAFT || proposal.status() == GovernancePolicyProposal.Status.IN_REVIEW).count();
    return surface("surface-governance-policy-dashboard", "dashboard", "Governance/Policy dashboard", correlationId, mapOf(
        "surfaceContract", "governance.policy.dashboard.v1",
        "readiness", "GovernancePolicyService deterministic dashboard/read/proposal lifecycle foundation; no direct mutation by model or frontend.",
        "cards", List.of(
            mapOf("cardId", "card-active-policies", "label", "Active policy concepts", "value", policies().size(), "severity", "info"),
            mapOf("cardId", "card-pending-proposals", "label", "Pending proposals", "value", pending, "severity", pending == 0 ? "info" : "warning"),
            mapOf("cardId", "card-policy-traces", "label", "Policy traces", "value", proposals.size(), "severity", proposals.isEmpty() ? "info" : "warning")),
        "attentionItems", List.of(
            mapOf("itemId", "approval-gate", "label", "Authority-changing policy proposals", "status", pending == 0 ? "clear" : "approval_required"),
            mapOf("itemId", "runtime-boundary", "label", "Provider/model boundary", "status", "fail-closed; deterministic services own lifecycle")),
        "proposalLifecycle", List.of("draft", "in_review", "approved", "rejected", "activated", "rolled_back", "blocked"),
        "capabilityIds", List.of(DASHBOARD_CAPABILITY, LIST_CAPABILITY, READ_CAPABILITY, PROPOSAL_DRAFT_CAPABILITY, PROPOSAL_SUBMIT_CAPABILITY, PROPOSAL_READ_CAPABILITY, "governance.policy.simulate", APPROVE_CAPABILITY, ACTIVATE_CAPABILITY, ROLLBACK_CAPABILITY),
        "traceLinks", List.of(trace("dashboard", correlationId)),
        "redaction", "browser-safe; raw prompts, provider secrets, hidden prompt text, and raw tool payloads omitted"));
  }

  public SurfaceData inventory(AuthContextResolver.ResolvedMe actor, String correlationId) {
    requireRead(actor, LIST_CAPABILITY, correlationId);
    return surface("surface-governance-policy-inventory", "list-search", "Policy inventory", correlationId, mapOf(
        "surfaceContract", "governance.policy.inventory.v1",
        "query", mapOf("tenantId", actor.selectedContext().tenantId(), "customerId", actor.selectedContext().customerId(), "capabilityId", LIST_CAPABILITY),
        "rows", policies(),
        "pageInfo", mapOf("totalKnownCount", policies().size()),
        "systemStates", List.of("ready", "empty", "forbidden", "validation-error", "no-op", "system_message"),
        "redaction", "policy rows are browser-safe summaries; secrets and hidden prompt text omitted"));
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
        List.of(READ_CAPABILITY, "governance.policy.simulate", APPROVE_CAPABILITY, ACTIVATE_CAPABILITY),
        List.of("governance-policy-workstream", "ToolPermissionBoundary", "AgentDefinition"),
        APPROVE_CAPABILITY,
        "rollback metadata required before activation; no direct mutation in draft or submit",
        idempotencyKey,
        correlationId,
        null,
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
    return action(submitted.status() == GovernancePolicyProposal.Status.IN_REVIEW ? "accepted" : "validation-error", "Policy proposal submitted for human review; no active authority changed.", proposal(actor, submitted, correlationId), List.of(trace("proposal-submit", correlationId)));
  }

  public SurfaceData readProposal(AuthContextResolver.ResolvedMe actor, Object input, String correlationId) {
    validateScope(actor, input, correlationId);
    requireRead(actor, PROPOSAL_READ_CAPABILITY, correlationId);
    var proposalId = stringInput(input, "proposalId", null);
    var proposal = proposalId == null ? repository.listProposals(actor.selectedContext().tenantId(), actor.selectedContext().customerId()).stream().findFirst() : repository.findProposal(actor.selectedContext().tenantId(), actor.selectedContext().customerId(), proposalId);
    return proposal.map(value -> proposal(actor, value, correlationId)).orElseGet(() -> validation("proposalId", "No authorized proposal found for selected AuthContext.", correlationId).surface());
  }

  private SurfaceData proposal(AuthContextResolver.ResolvedMe actor, GovernancePolicyProposal proposal, String correlationId) {
    return surface("surface-governance-policy-proposal", "governance-diff", "Policy proposal", correlationId, mapOf(
        "surfaceContract", "governance.policy.proposal.v1",
        "proposalId", proposal.proposalId(),
        "tenantId", proposal.tenantId(),
        "customerId", proposal.customerId(),
        "state", proposal.status().name().toLowerCase(),
        "proposal lifecycle", proposal.status().name().toLowerCase(),
        "summary", proposal.rationale(),
        "source", "GovernancePolicyService deterministic proposal lifecycle",
        "risk", proposal.riskClassification(),
        "requiredApproval", proposal.requiredApprovalCapabilityId(),
        "affectedCapabilityIds", proposal.affectedCapabilityIds(),
        "affectedArtifacts", proposal.affectedArtifactRefs(),
        "beforeSummary", "Active policy remains unchanged.",
        "afterSummary", "Proposed governance change stays inert until simulation, human approval, activation, and rollback metadata checks.",
        "changes", List.of(mapOf("path", proposal.targetPolicyId(), "before", "active policy unchanged", "after", proposal.proposedContent(), "impact", "No authority changes before approval.")),
        "idempotency", mapOf("draftIdempotencyKey", proposal.idempotencyKey(), "submitIsNoOpWhenAlreadyInReview", true),
        "traceLinks", List.of(trace("proposal", correlationId)),
        "redaction", mapOf("omittedFieldKeys", OMITTED_FIELDS, "browserSafe", true),
        "noDirectMutation", true));
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
    return action("validation-error", message, new SurfaceData("surface-governance-policy-validation-error", "system_message", "Governance/Policy validation", List.of(trace("validation", correlationId)), mapOf("surfaceContract", "governance.policy.system_message.v1", "status", "validation-error", "field", field, "message", message, "severity", "warning", "system_message", true, "traceLinks", List.of(trace("validation", correlationId)), "redaction", "browser-safe validation only")), List.of(trace("validation", correlationId)));
  }

  private ActionResult action(String status, String message, SurfaceData surface, List<String> traceIds) {
    return new ActionResult(status, message, traceIds, surface);
  }

  private SurfaceData surface(String id, String type, String title, String correlationId, Map<String, Object> data) {
    return new SurfaceData(id, type, title, List.of(trace(id.replace("surface-governance-policy-", ""), correlationId)), data);
  }

  private List<Map<String, Object>> policies() {
    return List.of(
        policy("policy-agent-tool-boundary", "ToolPermissionBoundary grants", "active", "agent-governance-policy", List.of("agent.skills.read", "agent.references.read"), "trace-tool-boundary-governance"),
        policy("policy-provider-fail-closed", "Provider fail-closed policy", "active", "starter-default-model-policy", List.of("agent.user_admin.use", READ_CAPABILITY), "trace-provider-fail-closed"),
        policy("policy-human-approval", "Human approval for authority changes", "active", "governance-policy-workstream", List.of(APPROVE_CAPABILITY, ACTIVATE_CAPABILITY, ROLLBACK_CAPABILITY), "trace-human-approval"));
  }

  private Map<String, Object> policy(String id, String name, String status, String source, List<String> capabilityIds, String traceId) {
    return mapOf("policyId", id, "name", name, "type", "governance", "status", status, "affectedCapabilityIds", capabilityIds, "sourceArtifact", source, "lastChangeTraceId", traceId, "version", 1, "owner", "Governance/Policy", "redacted", true);
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
