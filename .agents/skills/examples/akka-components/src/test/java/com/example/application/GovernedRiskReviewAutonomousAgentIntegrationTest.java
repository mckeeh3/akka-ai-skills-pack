package com.example.application;

import static akka.javasdk.testkit.TestModelProvider.AutonomousAgentTools.completeTask;
import static akka.javasdk.testkit.TestModelProvider.AutonomousAgentTools.failTask;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.agent.task.TaskStatus;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import akka.javasdk.testkit.TestModelProvider.AiResponse;
import akka.javasdk.testkit.TestModelProvider.ToolInvocationRequest;
import com.example.application.GovernedRiskReviewTasks.GovernedRiskReviewRequest;
import com.example.application.GovernedRiskReviewTasks.GovernedRiskReviewResult;
import com.example.application.GovernedToolBoundaryService.EvidenceRecord;
import com.example.application.GovernedToolBoundaryService.GovernedToolGrant;
import com.example.application.GovernedToolBoundaryService.GovernedToolPermissionBoundary;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GovernedRiskReviewAutonomousAgentIntegrationTest extends TestKitSupport {

  private final TestModelProvider riskModel = new TestModelProvider();
  private final GovernedToolBoundaryService boundaryService = GovernedToolBoundaryService.INSTANCE;

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withAdditionalConfig("akka.javasdk.agent.openai.api-key = n/a")
        .withModelProvider(GovernedRiskReviewAutonomousAgent.class, riskModel);
  }

  @BeforeEach
  void setUp() {
    boundaryService.reset();
    boundaryService.addEvidence(
        new EvidenceRecord(
            "tenant-a", "customer-1", "evidence-1", "Recent failed payment and high usage spike.", "card-token-123"));
    boundaryService.addEvidence(
        new EvidenceRecord(
            "tenant-b", "customer-9", "evidence-secret", "Other tenant evidence.", "secret-other-tenant"));
  }

  @AfterEach
  void tearDown() {
    riskModel.reset();
    boundaryService.reset();
  }

  @Test
  void allowedReadOnlyToolInvocationRecordsTraceAndCompletesTypedTask() {
    var request = registerReview("review-allowed");
    activateBoundary(readGrant());
    riskModel
        .whenMessage(message -> message.contains("review-allowed"))
        .reply(
            new ToolInvocationRequest(
                "GovernedRiskReviewTools_readCustomerEvidence",
                "{\"reviewId\":\"review-allowed\",\"tenantId\":\"tenant-a\",\"customerId\":\"customer-1\",\"evidenceId\":\"evidence-1\",\"correlationId\":\"review-allowed\"}"));
    riskModel
        .whenToolResult(
            result ->
                result.name().equals("GovernedRiskReviewTools_readCustomerEvidence")
                    && result.content().contains("allowed")
                    && result.content().contains("Recent failed payment"))
        .thenReply(
            result ->
                new AiResponse(
                completeTask(
                new GovernedRiskReviewResult(
                    "tenant-a", "customer-1", "Review scoped evidence and contact account owner.", List.of("evidence-1"), null))));

    var taskId = runReview(request);

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var snapshot = componentClient.forTask(taskId).get(GovernedRiskReviewTasks.REVIEW);
              assertEquals(TaskStatus.COMPLETED, snapshot.status());
              assertEquals(List.of("evidence-1"), snapshot.result().orElseThrow().evidenceIds());
            });
    assertTrue(
        boundaryService.traces().stream()
            .anyMatch(
                trace ->
                    trace.toolId().equals(GovernedToolBoundaryService.READ_EVIDENCE_TOOL_ID)
                        && trace.capabilityId().equals(GovernedRiskReviewTasks.READ_EVIDENCE_CAPABILITY)
                        && trace.decision().equals("allowed")
                        && trace.tenantId().equals("tenant-a")));
  }

  @Test
  void ungrantedEvidenceToolReturnsSafeDenialAndDoesNotLeakEvidence() {
    var request = registerReview("review-ungranted");
    activateBoundary(proposalGrant());
    riskModel
        .whenMessage(message -> message.contains("review-ungranted"))
        .reply(
            new ToolInvocationRequest(
                "GovernedRiskReviewTools_readCustomerEvidence",
                "{\"reviewId\":\"review-ungranted\",\"tenantId\":\"tenant-a\",\"customerId\":\"customer-1\",\"evidenceId\":\"evidence-1\",\"correlationId\":\"review-ungranted\"}"));
    riskModel
        .whenToolResult(
            result ->
                result.name().equals("GovernedRiskReviewTools_readCustomerEvidence")
                    && result.content().contains("denied")
                    && !result.content().contains("card-token-123"))
        .thenReply(
            result ->
                new AiResponse(
                completeTask(
                new GovernedRiskReviewResult(
                    "tenant-a", "customer-1", "Evidence read denied by ToolPermissionBoundary.", List.of(), null))));

    var taskId = runReview(request);

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var result = componentClient.forTask(taskId).get(GovernedRiskReviewTasks.REVIEW).result().orElseThrow();
              assertTrue(result.evidenceIds().isEmpty());
              assertFalse(result.recommendation().contains("card-token-123"));
            });
    assertDeniedTrace("review-ungranted", GovernedToolBoundaryService.READ_EVIDENCE_TOOL_ID);
  }

  @Test
  void crossTenantEvidenceRequestIsDeniedAndTracedWithoutEvidenceLeakage() {
    var request = registerReview("review-cross-tenant");
    activateBoundary(readGrant());
    riskModel
        .whenMessage(message -> message.contains("review-cross-tenant"))
        .reply(
            new ToolInvocationRequest(
                "GovernedRiskReviewTools_readCustomerEvidence",
                "{\"reviewId\":\"review-cross-tenant\",\"tenantId\":\"tenant-b\",\"customerId\":\"customer-9\",\"evidenceId\":\"evidence-secret\",\"correlationId\":\"review-cross-tenant\"}"));
    riskModel
        .whenToolResult(
            result ->
                result.name().equals("GovernedRiskReviewTools_readCustomerEvidence")
                    && result.content().contains("denied")
                    && result.content().contains("scope mismatch")
                    && !result.content().contains("secret-other-tenant"))
        .thenReply(
            result ->
                new AiResponse(
                completeTask(
                new GovernedRiskReviewResult(
                    "tenant-a", "customer-1", "Cross-tenant evidence request denied.", List.of(), null))));

    var taskId = runReview(request);

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var result = componentClient.forTask(taskId).get(GovernedRiskReviewTasks.REVIEW).result().orElseThrow();
              assertTrue(result.evidenceIds().isEmpty());
              assertFalse(result.recommendation().contains("secret-other-tenant"));
            });
    assertDeniedTrace("review-cross-tenant", "unknown");
  }

  @Test
  void sideEffectingFollowupReturnsApprovalRequiredAndDoesNotExecuteSideEffect() {
    var request = registerReview("review-approval");
    activateBoundary(readGrant(), proposalGrant());
    riskModel
        .whenMessage(message -> message.contains("review-approval"))
        .reply(
            new ToolInvocationRequest(
                "GovernedRiskReviewTools_proposeCustomerFollowup",
                "{\"reviewId\":\"review-approval\",\"tenantId\":\"tenant-a\",\"customerId\":\"customer-1\",\"action\":\"Email customer owner\",\"correlationId\":\"review-approval\"}"));
    riskModel
        .whenToolResult(
            result ->
                result.name().equals("GovernedRiskReviewTools_proposeCustomerFollowup")
                    && result.content().contains("approval_required")
                    && result.content().contains("proposal-review-approval"))
        .thenReply(
            result ->
                new AiResponse(
                completeTask(
                new GovernedRiskReviewResult(
                    "tenant-a", "customer-1", "Follow-up proposal awaits approval.", List.of(), "proposal-review-approval"))));

    var taskId = runReview(request);

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var result = componentClient.forTask(taskId).get(GovernedRiskReviewTasks.REVIEW).result().orElseThrow();
              assertEquals("proposal-review-approval", result.proposedActionId());
            });
    var proposal = boundaryService.proposal("proposal-review-approval").orElseThrow();
    assertFalse(proposal.executed());
    assertTrue(
        boundaryService.traces().stream()
            .anyMatch(
                trace ->
                    trace.correlationId().equals("review-approval")
                        && trace.toolId().equals(GovernedToolBoundaryService.PROPOSE_FOLLOWUP_TOOL_ID)
                        && trace.decision().equals("approval_required")));
  }

  @Test
  void missingBoundaryConfigurationFailsClosedWithActionableFailedTask() {
    var request = registerReview("review-missing-boundary");
    riskModel
        .whenMessage(message -> message.contains("review-missing-boundary"))
        .reply(
            new ToolInvocationRequest(
                "GovernedRiskReviewTools_readCustomerEvidence",
                "{\"reviewId\":\"review-missing-boundary\",\"tenantId\":\"tenant-a\",\"customerId\":\"customer-1\",\"evidenceId\":\"evidence-1\",\"correlationId\":\"review-missing-boundary\"}"));
    riskModel
        .whenToolResult(
            result ->
                result.name().equals("GovernedRiskReviewTools_readCustomerEvidence")
                    && result.content().contains("fail-closed")
                    && result.content().contains("ToolPermissionBoundary"))
        .thenReply(
            result ->
                new AiResponse(
                failTask("fail-closed: missing active ToolPermissionBoundary for tenant and agent")));

    var taskId = runReview(request);

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var snapshot = componentClient.forTask(taskId).get(GovernedRiskReviewTasks.REVIEW);
              assertEquals(TaskStatus.FAILED, snapshot.status());
              assertTrue(snapshot.failureReason().orElseThrow().contains("ToolPermissionBoundary"));
            });
    assertDeniedTrace("review-missing-boundary", "unknown");
  }

  private GovernedRiskReviewRequest registerReview(String reviewId) {
    var request =
        new GovernedRiskReviewRequest(
            "tenant-a", "customer-1", reviewId, "Review customer risk with governed tools.");
    boundaryService.registerReview(request);
    return request;
  }

  private String runReview(GovernedRiskReviewRequest request) {
    return componentClient
        .forAutonomousAgent(GovernedRiskReviewAutonomousAgent.class, UUID.randomUUID().toString())
        .runSingleTask(
            GovernedRiskReviewTasks.REVIEW.instructions(
                "tenantId="
                    + request.tenantId()
                    + ", customerId="
                    + request.customerId()
                    + ", reviewId="
                    + request.reviewId()
                    + ", question="
                    + request.question()));
  }

  private void activateBoundary(GovernedToolGrant... grants) {
    boundaryService.activateBoundary(
        new GovernedToolPermissionBoundary(
            "tenant-a", GovernedToolBoundaryService.AGENT_ID, Set.of(grants), true));
  }

  private GovernedToolGrant readGrant() {
    return new GovernedToolGrant(
        GovernedToolBoundaryService.READ_EVIDENCE_TOOL_ID,
        GovernedRiskReviewTasks.READ_EVIDENCE_CAPABILITY,
        "read",
        "selected_customer",
        "none");
  }

  private GovernedToolGrant proposalGrant() {
    return new GovernedToolGrant(
        GovernedToolBoundaryService.PROPOSE_FOLLOWUP_TOOL_ID,
        GovernedRiskReviewTasks.PROPOSE_FOLLOWUP_CAPABILITY,
        "request_approval",
        "selected_customer",
        "approval_required");
  }

  private void assertDeniedTrace(String correlationId, String toolId) {
    assertTrue(
        boundaryService.traces().stream()
            .anyMatch(
                trace ->
                    trace.correlationId().equals(correlationId)
                        && trace.toolId().equals(toolId)
                        && trace.decision().equals("denied")));
  }
}
