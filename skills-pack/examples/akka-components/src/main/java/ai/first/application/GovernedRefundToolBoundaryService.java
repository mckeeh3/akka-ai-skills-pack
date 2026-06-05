package ai.first.application;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory reference service for side-effecting component-tool and MCP-tool boundaries.
 *
 * <p>This example intentionally keeps persistence out of the helper so the important generated-app
 * contract is visible: stable tool ids map to capabilities, backend context wins over model input,
 * side effects require an active ToolPermissionBoundary grant, approval-required tools do not
 * execute the side effect, duplicate idempotency keys are safe, and every decision is traced.
 */
public final class GovernedRefundToolBoundaryService {

  public static final GovernedRefundToolBoundaryService INSTANCE = new GovernedRefundToolBoundaryService();

  public static final String AGENT_ID = "governed-refund-autonomous-agent";
  public static final String COMPONENT_TOOL_ID = "component.refund.request";
  public static final String MCP_TOOL_ID = "mcp.refund.request";
  public static final String REFUND_REQUEST_CAPABILITY = "refund.request_consequential";

  private final Map<String, GovernedRefundContext> contexts = new ConcurrentHashMap<>();
  private final Map<String, GovernedRefundToolPermissionBoundary> boundaries = new ConcurrentHashMap<>();
  private final Map<String, GovernedRefundToolResult> resultsByIdempotencyKey = new ConcurrentHashMap<>();
  private final Map<String, RefundProposal> proposals = new ConcurrentHashMap<>();
  private final List<GovernedRefundToolTrace> traces = java.util.Collections.synchronizedList(new ArrayList<>());

  private GovernedRefundToolBoundaryService() {}

  public void reset() {
    contexts.clear();
    boundaries.clear();
    resultsByIdempotencyKey.clear();
    proposals.clear();
    traces.clear();
  }

  public void registerContext(GovernedRefundContext context) {
    contexts.put(context.refundId(), context);
  }

  public void activateBoundary(GovernedRefundToolPermissionBoundary boundary) {
    boundaries.put(boundary.tenantId() + ":" + boundary.agentId(), boundary);
  }

  public List<GovernedRefundToolTrace> traces() {
    synchronized (traces) {
      return List.copyOf(traces);
    }
  }

  public Optional<RefundProposal> proposal(String proposalId) {
    return Optional.ofNullable(proposals.get(proposalId));
  }

  public GovernedRefundToolResult requestRefundViaComponentTool(
      String refundId, RefundToolRequest request) {
    return requestRefund(COMPONENT_TOOL_ID, refundId, request);
  }

  public GovernedRefundToolResult requestRefundViaMcpTool(String refundId, RefundToolRequest request) {
    return requestRefund(MCP_TOOL_ID, refundId, request);
  }

  private GovernedRefundToolResult requestRefund(
      String toolId, String refundId, RefundToolRequest request) {
    var correlationId = safe(request == null ? null : request.correlationId(), refundId);
    if (request == null) {
      return deny(unknown(refundId), toolId, "request is required", correlationId);
    }
    if (blank(request.idempotencyKey())) {
      return deny(unknown(refundId), toolId, "idempotencyKey is required", correlationId);
    }
    var previous = resultsByIdempotencyKey.get(request.idempotencyKey());
    if (previous != null) {
      trace(
          contextOrUnknown(refundId),
          toolId,
          "duplicate",
          "Duplicate idempotency key returned the original governed result",
          correlationId,
          request.idempotencyKey());
      return previous;
    }

    var context = contexts.get(refundId);
    if (context == null) {
      return remember(
          request.idempotencyKey(),
          deny(unknown(refundId), toolId, "fail-closed: no registered refund context", correlationId));
    }
    if (!context.tenantId().equals(request.tenantId()) || !context.customerId().equals(request.customerId())) {
      return remember(
          request.idempotencyKey(),
          deny(context, toolId, "tenant/customer scope mismatch for governed refund tool", correlationId));
    }
    if (!context.orderId().equals(request.orderId()) || context.amountCents() != request.amountCents()) {
      return remember(
          request.idempotencyKey(),
          deny(context, toolId, "refund order or amount does not match backend context", correlationId));
    }

    var boundary = boundaries.get(context.tenantId() + ":" + context.agentId());
    if (boundary == null || !boundary.active()) {
      return remember(
          request.idempotencyKey(),
          deny(context, toolId, "fail-closed: missing active ToolPermissionBoundary", correlationId));
    }
    var grant = boundary.grantFor(toolId, REFUND_REQUEST_CAPABILITY);
    if (grant.isEmpty()) {
      return remember(
          request.idempotencyKey(),
          deny(context, toolId, "ToolPermissionBoundary does not grant refund request tool", correlationId));
    }
    if (!grant.orElseThrow().operation().equals("request_approval")) {
      return remember(
          request.idempotencyKey(),
          deny(
              context,
              toolId,
              "reference example only grants request_approval for consequential refund tools",
              correlationId));
    }

    var proposalId = "refund-proposal-" + refundId;
    proposals.putIfAbsent(
        proposalId,
        new RefundProposal(
            proposalId,
            context.tenantId(),
            context.customerId(),
            context.orderId(),
            context.amountCents(),
            request.reason(),
            false,
            Instant.now()));
    var result =
        new GovernedRefundToolResult(
            "approval_required",
            "Refund request requires approval; no refund side effect was executed.",
            proposalId,
            false,
            request.idempotencyKey());
    trace(context, toolId, "approval_required", "Side-effecting refund converted to approval proposal", correlationId, request.idempotencyKey());
    return remember(request.idempotencyKey(), result);
  }

  private GovernedRefundToolResult remember(String idempotencyKey, GovernedRefundToolResult result) {
    if (idempotencyKey != null && !idempotencyKey.isBlank()) {
      resultsByIdempotencyKey.putIfAbsent(idempotencyKey, result);
      return resultsByIdempotencyKey.get(idempotencyKey);
    }
    return result;
  }

  private GovernedRefundToolResult deny(
      GovernedRefundContext context, String toolId, String reason, String correlationId) {
    trace(context, toolId, "denied", reason, correlationId, null);
    return new GovernedRefundToolResult("denied", reason, null, false, null);
  }

  private void trace(
      GovernedRefundContext context,
      String toolId,
      String decision,
      String reason,
      String correlationId,
      String idempotencyKey) {
    traces.add(
        new GovernedRefundToolTrace(
            context.tenantId(),
            context.customerId(),
            context.agentId(),
            toolId,
            REFUND_REQUEST_CAPABILITY,
            decision,
            reason,
            correlationId,
            idempotencyKey,
            Instant.now()));
  }

  private GovernedRefundContext contextOrUnknown(String refundId) {
    return contexts.getOrDefault(refundId, unknown(refundId));
  }

  private GovernedRefundContext unknown(String refundId) {
    return new GovernedRefundContext("unknown", "unknown", refundId, "unknown", "unknown", 0, AGENT_ID);
  }

  private static String safe(String value, String fallback) {
    return blank(value) ? fallback : value;
  }

  private static boolean blank(String value) {
    return value == null || value.isBlank();
  }

  public static Map<String, String> toolRegistry() {
    return Map.of(
        COMPONENT_TOOL_ID, REFUND_REQUEST_CAPABILITY,
        MCP_TOOL_ID, REFUND_REQUEST_CAPABILITY);
  }

  public record GovernedRefundContext(
      String tenantId,
      String customerId,
      String refundId,
      String orderId,
      String requestedBy,
      long amountCents,
      String agentId) {}

  public record RefundToolRequest(
      String tenantId,
      String customerId,
      String orderId,
      long amountCents,
      String requestedBy,
      String reason,
      String idempotencyKey,
      String correlationId) {}

  public record GovernedRefundToolPermissionBoundary(
      String tenantId, String agentId, Set<GovernedRefundToolGrant> grants, boolean active) {
    public GovernedRefundToolPermissionBoundary {
      grants = Set.copyOf(grants == null ? Set.of() : grants);
    }

    Optional<GovernedRefundToolGrant> grantFor(String toolId, String capabilityId) {
      return grants.stream()
          .filter(grant -> grant.toolId().equals(toolId) && grant.capabilityId().equals(capabilityId))
          .findFirst();
    }
  }

  public record GovernedRefundToolGrant(
      String toolId,
      String capabilityId,
      String operation,
      String sideEffectLevel,
      String approvalPolicy) {}

  public record GovernedRefundToolResult(
      String status, String message, String proposalId, boolean executed, String idempotencyKey) {}

  public record GovernedRefundToolTrace(
      String tenantId,
      String customerId,
      String agentId,
      String toolId,
      String capabilityId,
      String decision,
      String reason,
      String correlationId,
      String idempotencyKey,
      Instant timestamp) {}

  public record RefundProposal(
      String proposalId,
      String tenantId,
      String customerId,
      String orderId,
      long amountCents,
      String reason,
      boolean executed,
      Instant createdAt) {}
}
