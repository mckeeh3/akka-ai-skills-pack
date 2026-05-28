package com.example.application;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Small in-memory governed tool-boundary reference service for the Autonomous Agent example.
 *
 * <p>The service is intentionally minimal, but preserves generated-app contracts: a backend-owned
 * boundary is resolved before protected tool work, model-supplied tenant/customer arguments never
 * grant authority, denials are safe and traced, and side effects become approval-required
 * proposals instead of direct execution.
 */
public final class GovernedToolBoundaryService {

  public static final GovernedToolBoundaryService INSTANCE = new GovernedToolBoundaryService();

  public static final String AGENT_ID = "governed-risk-review-autonomous-agent";
  public static final String READ_EVIDENCE_TOOL_ID = "risk.read_customer_evidence";
  public static final String PROPOSE_FOLLOWUP_TOOL_ID = "risk.propose_customer_followup";

  private final Map<String, GovernedRiskReviewTasks.GovernedRiskReviewRequest> reviews =
      new ConcurrentHashMap<>();
  private final Map<String, GovernedToolPermissionBoundary> boundaries = new ConcurrentHashMap<>();
  private final Map<String, EvidenceRecord> evidence = new ConcurrentHashMap<>();
  private final Map<String, FollowupProposal> proposals = new ConcurrentHashMap<>();
  private final List<GovernedToolTrace> traces = java.util.Collections.synchronizedList(new ArrayList<>());

  private GovernedToolBoundaryService() {}

  public void reset() {
    reviews.clear();
    boundaries.clear();
    evidence.clear();
    proposals.clear();
    traces.clear();
  }

  public void registerReview(GovernedRiskReviewTasks.GovernedRiskReviewRequest request) {
    reviews.put(request.reviewId(), request);
  }

  public void activateBoundary(GovernedToolPermissionBoundary boundary) {
    boundaries.put(boundary.tenantId() + ":" + boundary.agentId(), boundary);
  }

  public void addEvidence(EvidenceRecord record) {
    evidence.put(record.evidenceId(), record);
  }

  public List<GovernedToolTrace> traces() {
    synchronized (traces) {
      return List.copyOf(traces);
    }
  }

  public List<FollowupProposal> proposals() {
    return List.copyOf(proposals.values());
  }

  public Optional<FollowupProposal> proposal(String proposalId) {
    return Optional.ofNullable(proposals.get(proposalId));
  }

  GovernedToolInvocationResult readCustomerEvidence(
      String reviewId,
      String requestedTenantId,
      String requestedCustomerId,
      String evidenceId,
      String correlationId) {
    var context = contextFor(reviewId, requestedTenantId, requestedCustomerId, correlationId);
    if (context.isDenied()) {
      return context.deniedResult();
    }
    var allowed = authorize(context, READ_EVIDENCE_TOOL_ID, GovernedRiskReviewTasks.READ_EVIDENCE_CAPABILITY, "read");
    if (allowed.isDenied()) {
      return allowed.deniedResult();
    }

    var record = evidence.get(evidenceId);
    if (record == null
        || !record.tenantId().equals(context.review().tenantId())
        || !record.customerId().equals(context.review().customerId())) {
      return deny(
          context.review(),
          READ_EVIDENCE_TOOL_ID,
          GovernedRiskReviewTasks.READ_EVIDENCE_CAPABILITY,
          "tenant/customer scope mismatch for requested evidence",
          context.correlationId());
    }

    trace(
        context.review(),
        READ_EVIDENCE_TOOL_ID,
        GovernedRiskReviewTasks.READ_EVIDENCE_CAPABILITY,
        "allowed",
        "ToolPermissionBoundary allowed scoped redacted evidence read",
        context.correlationId());
    return new GovernedToolInvocationResult(
        "allowed",
        "Scoped redacted evidence returned for tenant/customer review.",
        List.of(new EvidenceSummary(record.evidenceId(), record.redactedSummary())),
        null);
  }

  GovernedToolInvocationResult proposeCustomerFollowup(
      String reviewId,
      String requestedTenantId,
      String requestedCustomerId,
      String action,
      String correlationId) {
    var context = contextFor(reviewId, requestedTenantId, requestedCustomerId, correlationId);
    if (context.isDenied()) {
      return context.deniedResult();
    }
    var allowed =
        authorize(
            context,
            PROPOSE_FOLLOWUP_TOOL_ID,
            GovernedRiskReviewTasks.PROPOSE_FOLLOWUP_CAPABILITY,
            "request_approval");
    if (allowed.isDenied()) {
      return allowed.deniedResult();
    }

    var proposalId = "proposal-" + context.review().reviewId();
    proposals.put(
        proposalId,
        new FollowupProposal(
            proposalId,
            context.review().tenantId(),
            context.review().customerId(),
            action,
            false,
            Instant.now()));
    trace(
        context.review(),
        PROPOSE_FOLLOWUP_TOOL_ID,
        GovernedRiskReviewTasks.PROPOSE_FOLLOWUP_CAPABILITY,
        "approval_required",
        "Side-effecting follow-up converted to approval proposal; no side effect executed",
        context.correlationId());
    return new GovernedToolInvocationResult(
        "approval_required",
        "Follow-up action requires approval before execution; no side effect was performed.",
        List.of(),
        proposalId);
  }

  private AuthorizationContext contextFor(
      String reviewId, String requestedTenantId, String requestedCustomerId, String correlationId) {
    var review = reviews.get(reviewId);
    var safeCorrelation = correlationId == null || correlationId.isBlank() ? reviewId : correlationId;
    if (review == null) {
      var unknown = new GovernedRiskReviewTasks.GovernedRiskReviewRequest("unknown", "unknown", reviewId, "unknown");
      return AuthorizationContext.denied(
          deny(
              unknown,
              "unknown",
              "unknown",
              "fail-closed: no registered governed risk review context",
              safeCorrelation));
    }
    if (!review.tenantId().equals(requestedTenantId) || !review.customerId().equals(requestedCustomerId)) {
      return AuthorizationContext.denied(
          deny(
              review,
              "unknown",
              "unknown",
              "tenant/customer scope mismatch for governed tool call",
              safeCorrelation));
    }
    var boundary = boundaries.get(review.tenantId() + ":" + AGENT_ID);
    if (boundary == null || !boundary.active()) {
      return AuthorizationContext.denied(
          deny(
              review,
              "unknown",
              "unknown",
              "fail-closed: missing active ToolPermissionBoundary for tenant and agent",
              safeCorrelation));
    }
    return AuthorizationContext.allowed(review, boundary, safeCorrelation);
  }

  private AuthorizationContext authorize(
      AuthorizationContext context, String toolId, String capabilityId, String operation) {
    var grant = context.boundary().grantFor(toolId, capabilityId, operation);
    if (grant.isEmpty()) {
      return AuthorizationContext.denied(
          deny(
              context.review(),
              toolId,
              capabilityId,
              "ToolPermissionBoundary does not grant " + operation + " for " + toolId,
              context.correlationId()));
    }
    return context;
  }

  private GovernedToolInvocationResult deny(
      GovernedRiskReviewTasks.GovernedRiskReviewRequest review,
      String toolId,
      String capabilityId,
      String reason,
      String correlationId) {
    trace(review, toolId, capabilityId, "denied", reason, correlationId);
    return new GovernedToolInvocationResult("denied", reason, List.of(), null);
  }

  private void trace(
      GovernedRiskReviewTasks.GovernedRiskReviewRequest review,
      String toolId,
      String capabilityId,
      String decision,
      String reason,
      String correlationId) {
    traces.add(
        new GovernedToolTrace(
            review.tenantId(),
            review.customerId(),
            toolId,
            capabilityId,
            decision,
            reason,
            correlationId,
            Instant.now()));
  }

  private record AuthorizationContext(
      GovernedRiskReviewTasks.GovernedRiskReviewRequest review,
      GovernedToolPermissionBoundary boundary,
      String correlationId,
      GovernedToolInvocationResult deniedResult) {
    static AuthorizationContext allowed(
        GovernedRiskReviewTasks.GovernedRiskReviewRequest review,
        GovernedToolPermissionBoundary boundary,
        String correlationId) {
      return new AuthorizationContext(review, boundary, correlationId, null);
    }

    static AuthorizationContext denied(GovernedToolInvocationResult result) {
      return new AuthorizationContext(null, null, null, result);
    }

    boolean isDenied() {
      return deniedResult != null;
    }
  }

  public record GovernedToolPermissionBoundary(
      String tenantId, String agentId, Set<GovernedToolGrant> grants, boolean active) {
    public GovernedToolPermissionBoundary {
      grants = Set.copyOf(grants);
    }

    Optional<GovernedToolGrant> grantFor(String toolId, String capabilityId, String operation) {
      return grants.stream()
          .filter(
              grant ->
                  grant.toolId().equals(toolId)
                      && grant.capabilityId().equals(capabilityId)
                      && grant.operation().equals(operation))
          .findFirst();
    }
  }

  public record GovernedToolGrant(
      String toolId, String capabilityId, String operation, String tenantScope, String approvalPolicy) {}

  public record EvidenceRecord(
      String tenantId, String customerId, String evidenceId, String redactedSummary, String rawSecret) {}

  public record EvidenceSummary(String evidenceId, String redactedSummary) {}

  public record GovernedToolInvocationResult(
      String status, String message, List<EvidenceSummary> evidence, String proposalId) {
    public GovernedToolInvocationResult {
      evidence = List.copyOf(evidence == null ? List.of() : evidence);
    }
  }

  public record GovernedToolTrace(
      String tenantId,
      String customerId,
      String toolId,
      String capabilityId,
      String decision,
      String reason,
      String correlationId,
      Instant timestamp) {}

  public record FollowupProposal(
      String proposalId,
      String tenantId,
      String customerId,
      String action,
      boolean executed,
      Instant createdAt) {}

  public static Map<String, String> toolRegistry() {
    var registry = new LinkedHashMap<String, String>();
    registry.put(READ_EVIDENCE_TOOL_ID, GovernedRiskReviewTasks.READ_EVIDENCE_CAPABILITY);
    registry.put(PROPOSE_FOLLOWUP_TOOL_ID, GovernedRiskReviewTasks.PROPOSE_FOLLOWUP_CAPABILITY);
    return Map.copyOf(registry);
  }
}
