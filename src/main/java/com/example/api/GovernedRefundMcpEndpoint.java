package com.example.api;

import akka.javasdk.JsonSupport;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.Description;
import akka.javasdk.annotations.mcp.McpEndpoint;
import akka.javasdk.annotations.mcp.McpTool;
import com.example.application.GovernedRefundToolBoundaryService;

/**
 * MCP endpoint reference for a consequential tool guarded by ToolPermissionBoundary.
 *
 * <p>The endpoint exposes the same {@code refund.request_consequential} capability as the component
 * tool example, but as a remote LLM-facing MCP surface. The service ACL is intentionally narrow and
 * the method still enforces backend tenant/customer scope, idempotency, approval policy, and traces;
 * tool descriptions are not treated as authorization.
 */
@Acl(allow = @Acl.Matcher(service = "refund-operations-agent-service"))
@McpEndpoint(
    path = "/mcp/governed-refunds",
    serverName = "governed-refund-tools",
    serverVersion = "1.0.0",
    instructions =
        "Expose only governed refund capabilities. Consequential refund requests must preserve ToolPermissionBoundary, tenant/customer scope, idempotency, approval, and audit semantics.")
public class GovernedRefundMcpEndpoint {

  private final GovernedRefundToolBoundaryService boundaryService;

  public GovernedRefundMcpEndpoint() {
    this(GovernedRefundToolBoundaryService.INSTANCE);
  }

  GovernedRefundMcpEndpoint(GovernedRefundToolBoundaryService boundaryService) {
    this.boundaryService = boundaryService;
  }

  @McpTool(
      name = "request-governed-refund",
      description =
          "Capability refund.request_consequential: request a consequential refund through a "
              + "remote MCP tool. The endpoint enforces service ACL, ToolPermissionBoundary, "
              + "tenant/customer scope, idempotency, approval policy, and trace emission. "
              + "Approval-required results do not execute the refund side effect.")
  public String requestGovernedRefund(
      @Description("Backend refund context id; this is not caller-controlled authority") String refundId,
      @Description("Tenant/customer/order scoped refund request")
          GovernedRefundToolBoundaryService.RefundToolRequest request) {
    return JsonSupport.encodeToString(boundaryService.requestRefundViaMcpTool(refundId, request));
  }
}
