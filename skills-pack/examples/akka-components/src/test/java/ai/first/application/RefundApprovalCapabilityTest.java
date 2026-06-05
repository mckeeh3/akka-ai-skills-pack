package ai.first.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.CommandException;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import akka.javasdk.testkit.TestModelProvider.AiResponse;
import akka.javasdk.testkit.TestModelProvider.ToolInvocationRequest;
import ai.first.domain.RefundApprovalState;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class RefundApprovalCapabilityTest extends TestKitSupport {

  private final TestModelProvider refundModel = new TestModelProvider();

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withAdditionalConfig("akka.javasdk.agent.openai.api-key = n/a")
        .withModelProvider(RefundApprovalAgent.class, refundModel);
  }

  @Test
  void agentToolReturnsApprovalProposalWithoutCommittingSideEffect() {
    refundModel
        .whenMessage(message -> message.contains("order-123"))
        .reply(
            new ToolInvocationRequest(
                "RefundProposalTools_proposeRefund",
                "{\"customerId\":\"customer-a\",\"orderId\":\"order-123\",\"amountCents\":9900,"
                    + "\"reason\":\"damaged item\",\"requestedBy\":\"support-agent\","
                    + "\"policyGrantAutonomy\":false}"));
    refundModel
        .whenToolResult(result -> result.name().equals("RefundProposalTools_proposeRefund"))
        .thenReply(
            result ->
                new AiResponse(
                    result.content().contains("\"approvalRequired\":true")
                            && result.content().contains("\"sideEffectCommitted\":false")
                        ? "Refund proposal created; approval is required in refund-approval-workflow and no refund was applied."
                        : "Refund proposal is unsafe."));

    var answer =
        componentClient
            .forAgent()
            .inSession("refund-proposal-session")
            .method(RefundApprovalAgent::reviewRefundRequest)
            .invoke("Can you refund 99.00 on order-123 for customer-a?");

    assertTrue(answer.contains("approval is required"));
    assertTrue(answer.contains("no refund was applied"));
    assertThrows(
        CommandException.class,
        () -> componentClient.forWorkflow("refund-agent-only").method(RefundApprovalWorkflow::get).invoke());
  }

  @Test
  void consequentialRefundWaitsForApprovalBeforeSideEffect() {
    var workflowId = "refund-approval-1";

    componentClient
        .forWorkflow(workflowId)
        .method(RefundApprovalWorkflow::request)
        .invoke(request("idem-refund-request-1", 9_900, false));

    var waiting = getWorkflow(workflowId);
    assertEquals(RefundApprovalState.Status.WAITING_FOR_APPROVAL, waiting.status());
    assertFalse(waiting.refundApplied());
    assertTrue(waiting.auditTrace().contains("proposal-created:idem-refund-request-1"));

    componentClient
        .forWorkflow(workflowId)
        .method(RefundApprovalWorkflow::approve)
        .invoke(new RefundApprovalWorkflow.ApproveRefund("idem-refund-approve-1", "ops-approver", "customer evidence accepted"));

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var approved = getWorkflow(workflowId);
              assertEquals(RefundApprovalState.Status.APPLIED, approved.status());
              assertTrue(approved.refundApplied());
              assertTrue(
                  approved.auditTrace().stream()
                      .anyMatch(event -> event.contains("refund-applied:ops-approver")));
            });
  }

  @Test
  void boundedAutonomousPolicyGrantCanApplyWithoutHumanApproval() {
    var workflowId = "refund-autonomous-1";

    componentClient
        .forWorkflow(workflowId)
        .method(RefundApprovalWorkflow::request)
        .invoke(request("idem-refund-auto-1", 2_000, true));

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var state = getWorkflow(workflowId);
              assertEquals(RefundApprovalState.Status.APPLIED, state.status());
              assertTrue(state.refundApplied());
              assertTrue(
                  state.auditTrace().stream()
                      .anyMatch(event -> event.contains("refund-applied:refund-policy")));
            });
  }

  @Test
  void duplicateApprovalIsIdempotentNoOp() {
    var workflowId = "refund-idempotent-1";
    componentClient
        .forWorkflow(workflowId)
        .method(RefundApprovalWorkflow::request)
        .invoke(request("idem-refund-request-idem", 9_900, false));

    var approval =
        new RefundApprovalWorkflow.ApproveRefund("idem-refund-approve-idem", "ops-approver", "approve once");
    componentClient.forWorkflow(workflowId).method(RefundApprovalWorkflow::approve).invoke(approval);

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(() -> assertEquals(RefundApprovalState.Status.APPLIED, getWorkflow(workflowId).status()));

    var duplicate = componentClient.forWorkflow(workflowId).method(RefundApprovalWorkflow::approve).invoke(approval);

    assertEquals(RefundApprovalState.Status.APPLIED, duplicate.status());
    assertEquals(2, getWorkflow(workflowId).auditTrace().size());
  }

  private RefundApprovalWorkflow.RequestRefund request(
      String idempotencyKey, long amountCents, boolean policyGrantAutonomy) {
    return new RefundApprovalWorkflow.RequestRefund(
        idempotencyKey,
        "customer-a",
        "order-123",
        amountCents,
        "support-agent",
        "damaged item",
        policyGrantAutonomy);
  }

  private RefundApprovalState getWorkflow(String workflowId) {
    return componentClient.forWorkflow(workflowId).method(RefundApprovalWorkflow::get).invoke();
  }
}
