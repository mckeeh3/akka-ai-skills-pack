package ai.first.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.CommandException;
import akka.javasdk.testkit.TestKitSupport;
import ai.first.domain.SupervisedExportState;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class SupervisedExportWorkflowIntegrationTest extends TestKitSupport {

  @Test
  void lowRiskExportCompletesWithTraceWithoutSupervision() {
    var workflowId = "export-low-risk-1";

    componentClient
        .forWorkflow(workflowId)
        .method(SupervisedExportWorkflow::start)
        .invoke(start("idem-export-start-1", 20));

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var state = getWorkflow(workflowId);
              assertEquals("customer.data-export.prepare", state.capabilityId());
              assertEquals("tenant-a", state.tenantId());
              assertEquals("customer-a", state.customerId());
              assertEquals("authctx-tenant-a-ops", state.authContextId());
              assertEquals(SupervisedExportState.Status.READY, state.status());
              assertTrue(state.resultUri().contains("export://tenant-a/customer-a/"));
              assertTrue(state.auditTrace().contains("risk-assessed:auto-allowed:20"));
              assertTrue(state.auditTrace().stream().anyMatch(event -> event.startsWith("export-ready:")));
              assertFalse(
                  state.auditTrace().stream().anyMatch(event -> event.startsWith("supervision-required:")));
            });
  }

  @Test
  void highRiskExportPausesForSupervisionThenResumesToCompletion() {
    var workflowId = "export-supervised-1";

    componentClient
        .forWorkflow(workflowId)
        .method(SupervisedExportWorkflow::start)
        .invoke(start("idem-export-start-2", 90));

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var waiting = getWorkflow(workflowId);
              assertEquals(SupervisedExportState.Status.SUPERVISION_REQUIRED, waiting.status());
              assertEquals("risk-score-90", waiting.supervisionReason());
              assertTrue(waiting.auditTrace().contains("risk-assessed:supervision-required:90"));
              assertTrue(waiting.auditTrace().contains("supervision-required:risk-score-90"));
            });

    componentClient
        .forWorkflow(workflowId)
        .method(SupervisedExportWorkflow::approve)
        .invoke(new SupervisedExportWorkflow.SupervisionDecision("idem-export-approval-1", "security-admin", "validated scoped customer request"));

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var ready = getWorkflow(workflowId);
              assertEquals(SupervisedExportState.Status.READY, ready.status());
              assertEquals("security-admin", ready.approvedBy());
              assertTrue(
                  ready.auditTrace().stream()
                      .anyMatch(event -> event.contains("supervision-approved:security-admin")));
              assertTrue(ready.auditTrace().stream().anyMatch(event -> event.startsWith("export-generation-started:")));
              assertTrue(ready.auditTrace().stream().anyMatch(event -> event.startsWith("export-ready:")));
            });
  }

  @Test
  void supervisorDenialEndsWithoutExportSideEffect() {
    var workflowId = "export-denied-1";

    componentClient
        .forWorkflow(workflowId)
        .method(SupervisedExportWorkflow::start)
        .invoke(start("idem-export-start-3", 95));

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> assertEquals(SupervisedExportState.Status.SUPERVISION_REQUIRED, getWorkflow(workflowId).status()));

    componentClient
        .forWorkflow(workflowId)
        .method(SupervisedExportWorkflow::deny)
        .invoke(new SupervisedExportWorkflow.SupervisionDecision("idem-export-deny-1", "security-admin", "missing legal basis"));

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var denied = getWorkflow(workflowId);
              assertEquals(SupervisedExportState.Status.DENIED, denied.status());
              assertEquals("", denied.resultUri());
              assertTrue(
                  denied.auditTrace().stream()
                      .anyMatch(event -> event.contains("supervision-denied:security-admin")));
              assertFalse(
                  denied.auditTrace().stream().anyMatch(event -> event.startsWith("export-ready:")));
            });
  }

  @Test
  void invalidStartIsRejectedBeforeWorkflowStateExists() {
    var error =
        assertThrows(
            CommandException.class,
            () ->
                componentClient
                    .forWorkflow("export-invalid-1")
                    .method(SupervisedExportWorkflow::start)
                    .invoke(start("idem-export-invalid", -1)));

    assertTrue(error.getMessage().contains("riskScore must be between 0 and 100"));
  }

  private SupervisedExportWorkflow.StartExport start(String idempotencyKey, int riskScore) {
    return new SupervisedExportWorkflow.StartExport(
        idempotencyKey,
        "tenant-a",
        "customer-a",
        "ops-user",
        "authctx-tenant-a-ops",
        "customer-records",
        riskScore);
  }

  private SupervisedExportState getWorkflow(String workflowId) {
    return componentClient.forWorkflow(workflowId).method(SupervisedExportWorkflow::get).invoke();
  }
}
