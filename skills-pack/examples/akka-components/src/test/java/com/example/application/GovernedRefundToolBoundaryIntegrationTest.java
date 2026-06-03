package com.example.application;

import static akka.javasdk.testkit.TestModelProvider.AutonomousAgentTools.completeTask;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.JsonSupport;
import akka.javasdk.agent.task.TaskStatus;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import akka.javasdk.testkit.TestModelProvider.AiResponse;
import akka.javasdk.testkit.TestModelProvider.ToolInvocationRequest;
import com.example.api.GovernedRefundMcpEndpoint;
import com.example.application.GovernedRefundToolBoundaryService.GovernedRefundContext;
import com.example.application.GovernedRefundToolBoundaryService.GovernedRefundToolGrant;
import com.example.application.GovernedRefundToolBoundaryService.GovernedRefundToolPermissionBoundary;
import com.example.application.GovernedRefundToolBoundaryService.GovernedRefundToolResult;
import com.example.application.GovernedRefundToolBoundaryService.RefundToolRequest;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GovernedRefundToolBoundaryIntegrationTest extends TestKitSupport {

  private final TestModelProvider refundModel = new TestModelProvider();
  private final GovernedRefundToolBoundaryService boundaryService = GovernedRefundToolBoundaryService.INSTANCE;

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withAdditionalConfig("akka.javasdk.agent.openai.api-key = n/a")
        .withModelProvider(GovernedRefundAutonomousAgent.class, refundModel);
  }

  @BeforeEach
  void setUp() {
    boundaryService.reset();
  }

  @AfterEach
  void tearDown() {
    refundModel.reset();
    boundaryService.reset();
  }

  @Test
  void autonomousAgentComponentToolReturnsApprovalRequiredAndDoesNotExecuteRefund() {
    registerContext("refund-component-approval");
    activateBoundary(componentApprovalGrant());
    refundModel
        .whenMessage(message -> message.contains("refund-component-approval"))
        .reply(
            new ToolInvocationRequest(
                "RefundApprovalWorkflow_requestFromGovernedTool",
                """
                {
                  "uniqueId": "refund-component-approval",
                  "request": {
                    "tenantId": "tenant-a",
                    "customerId": "customer-1",
                    "orderId": "order-7",
                    "amountCents": 1250,
                    "requestedBy": "agent-test",
                    "reason": "duplicate charge",
                    "idempotencyKey": "idem-component-approval",
                    "correlationId": "refund-component-approval"
                  }
                }
                """));
    refundModel
        .whenToolResult(
            result ->
                result.name().equals("RefundApprovalWorkflow_requestFromGovernedTool")
                    && result.content().contains("approval_required")
                    && result.content().contains("refund-proposal-refund-component-approval"))
        .thenReply(
            result ->
                new AiResponse(
                    completeTask(
                        new GovernedRefundTasks.GovernedRefundResult(
                            "tenant-a",
                            "customer-1",
                            "approval_required",
                            "refund-proposal-refund-component-approval",
                            false))));

    var taskId =
        componentClient
            .forAutonomousAgent(GovernedRefundAutonomousAgent.class, UUID.randomUUID().toString())
            .runSingleTask(
                GovernedRefundTasks.REQUEST_REFUND.instructions(
                    "tenantId=tenant-a, customerId=customer-1, refundId=refund-component-approval, orderId=order-7, amountCents=1250"));

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var snapshot = componentClient.forTask(taskId).get(GovernedRefundTasks.REQUEST_REFUND);
              assertEquals(TaskStatus.COMPLETED, snapshot.status());
              var result = snapshot.result().orElseThrow();
              assertEquals("approval_required", result.status());
              assertFalse(result.executed());
            });

    var proposal = boundaryService.proposal("refund-proposal-refund-component-approval").orElseThrow();
    assertFalse(proposal.executed());
    assertTrace("refund-component-approval", GovernedRefundToolBoundaryService.COMPONENT_TOOL_ID, "approval_required");
  }

  @Test
  void mcpToolDeniesUngrantedSideEffectAndDoesNotLeakOrExecute() throws Exception {
    registerContext("refund-mcp-denied");
    activateBoundary(componentApprovalGrant());
    var endpoint = new GovernedRefundMcpEndpoint();

    var json = endpoint.requestGovernedRefund("refund-mcp-denied", request("refund-mcp-denied", "idem-mcp-denied"));
    var result = JsonSupport.getObjectMapper().readValue(json, GovernedRefundToolResult.class);

    assertEquals("denied", result.status());
    assertFalse(result.executed());
    assertTrue(result.message().contains("ToolPermissionBoundary"));
    assertTrace("refund-mcp-denied", GovernedRefundToolBoundaryService.MCP_TOOL_ID, "denied");
  }

  @Test
  void mcpToolReturnsApprovalRequiredAndDuplicateIdempotencyDoesNotDuplicateProposal() throws Exception {
    registerContext("refund-mcp-approval");
    activateBoundary(mcpApprovalGrant());
    var endpoint = new GovernedRefundMcpEndpoint();

    var firstJson = endpoint.requestGovernedRefund("refund-mcp-approval", request("refund-mcp-approval", "idem-mcp-approval"));
    var secondJson = endpoint.requestGovernedRefund("refund-mcp-approval", request("refund-mcp-approval", "idem-mcp-approval"));
    var first = JsonSupport.getObjectMapper().readValue(firstJson, GovernedRefundToolResult.class);
    var second = JsonSupport.getObjectMapper().readValue(secondJson, GovernedRefundToolResult.class);

    assertEquals("approval_required", first.status());
    assertEquals(first, second);
    assertFalse(first.executed());
    assertTrue(boundaryService.proposal("refund-proposal-refund-mcp-approval").isPresent());
    assertEquals(
        1,
        boundaryService.traces().stream()
            .filter(
                trace ->
                    trace.toolId().equals(GovernedRefundToolBoundaryService.MCP_TOOL_ID)
                        && trace.decision().equals("approval_required"))
            .count());
    assertTrace("refund-mcp-approval", GovernedRefundToolBoundaryService.MCP_TOOL_ID, "duplicate");
  }

  @Test
  void componentToolDeniesCrossTenantRequestBeforeSideEffect() {
    registerContext("refund-cross-tenant");
    activateBoundary(componentApprovalGrant());

    var result =
        componentClient
            .forWorkflow("refund-cross-tenant")
            .method(RefundApprovalWorkflow::requestFromGovernedTool)
            .invoke(
                new RefundToolRequest(
                    "tenant-b",
                    "customer-9",
                    "order-7",
                    1250,
                    "agent-test",
                    "try cross tenant refund",
                    "idem-cross-tenant",
                    "refund-cross-tenant"));

    assertEquals("denied", result.status());
    assertFalse(result.executed());
    assertTrue(result.message().contains("scope mismatch"));
    assertTrace("refund-cross-tenant", GovernedRefundToolBoundaryService.COMPONENT_TOOL_ID, "denied");
  }

  private void registerContext(String refundId) {
    boundaryService.registerContext(
        new GovernedRefundContext(
            "tenant-a",
            "customer-1",
            refundId,
            "order-7",
            "agent-test",
            1250,
            GovernedRefundToolBoundaryService.AGENT_ID));
  }

  private RefundToolRequest request(String refundId, String idempotencyKey) {
    return new RefundToolRequest(
        "tenant-a",
        "customer-1",
        "order-7",
        1250,
        "agent-test",
        "duplicate charge",
        idempotencyKey,
        refundId);
  }

  private void activateBoundary(GovernedRefundToolGrant grant) {
    boundaryService.activateBoundary(
        new GovernedRefundToolPermissionBoundary(
            "tenant-a", GovernedRefundToolBoundaryService.AGENT_ID, Set.of(grant), true));
  }

  private GovernedRefundToolGrant componentApprovalGrant() {
    return new GovernedRefundToolGrant(
        GovernedRefundToolBoundaryService.COMPONENT_TOOL_ID,
        GovernedRefundToolBoundaryService.REFUND_REQUEST_CAPABILITY,
        "request_approval",
        "billing",
        "approval_required");
  }

  private GovernedRefundToolGrant mcpApprovalGrant() {
    return new GovernedRefundToolGrant(
        GovernedRefundToolBoundaryService.MCP_TOOL_ID,
        GovernedRefundToolBoundaryService.REFUND_REQUEST_CAPABILITY,
        "request_approval",
        "billing",
        "approval_required");
  }

  private void assertTrace(String correlationId, String toolId, String decision) {
    assertTrue(
        boundaryService.traces().stream()
            .anyMatch(
                trace ->
                    trace.correlationId().equals(correlationId)
                        && trace.toolId().equals(toolId)
                        && trace.capabilityId().equals(GovernedRefundToolBoundaryService.REFUND_REQUEST_CAPABILITY)
                        && trace.decision().equals(decision)));
  }
}
