package ai.first.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.EventingTestKit.IncomingMessages;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import ai.first.domain.SupervisedExportState;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class SupervisedExportEvidenceViewIntegrationTest extends TestKitSupport {

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT.withWorkflowIncomingMessages(SupervisedExportWorkflow.class);
  }

  @Test
  void viewReturnsCuratedScopedEvidenceForAgentAndUiConsumers() {
    IncomingMessages workflowUpdates = testKit.getWorkflowIncomingMessages(SupervisedExportWorkflow.class);

    workflowUpdates.publish(supervisionRequired("export-a", "tenant-a", "customer-a", 90), "export-a");
    workflowUpdates.publish(supervisionRequired("export-b", "tenant-b", "customer-a", 95), "export-b");
    workflowUpdates.publish(ready("export-c", "tenant-a", "customer-a", 25), "export-c");

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var result =
                  componentClient
                      .forView()
                      .method(SupervisedExportEvidenceView::findEvidence)
                      .invoke(
                          new SupervisedExportEvidenceView.FindExportEvidence(
                              "tenant-a", "customer-a", "SUPERVISION_REQUIRED", 75, ""));

              assertEquals(1, result.exports().size());
              var evidence = result.exports().getFirst();
              assertEquals("export-a", evidence.requestId());
              assertEquals("tenant-a", evidence.tenantId());
              assertEquals("customer-a", evidence.customerId());
              assertEquals("SUPERVISION_REQUIRED", evidence.status());
              assertEquals("customer-records", evidence.exportType());
              assertEquals(90, evidence.riskScore());
              assertTrue(evidence.supervisionRequired());
              assertFalse(evidence.resultReady());
              assertEquals("risk-score-90", evidence.supervisionReason());
              assertEquals("trace-export-a", evidence.traceId());
              assertEquals(3, evidence.auditEventCount());
              assertEquals("supervision-required:risk-score-90", evidence.latestAuditEvent());
            });
  }

  @Test
  void scopedQueryDoesNotLeakOtherCustomersOrRawWorkflowState() {
    IncomingMessages workflowUpdates = testKit.getWorkflowIncomingMessages(SupervisedExportWorkflow.class);

    workflowUpdates.publish(supervisionRequired("export-d", "tenant-x", "customer-x", 88), "export-d");
    workflowUpdates.publish(supervisionRequired("export-e", "tenant-x", "customer-y", 92), "export-e");

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var result =
                  componentClient
                      .forView()
                      .method(SupervisedExportEvidenceView::findEvidence)
                      .invoke(
                          new SupervisedExportEvidenceView.FindExportEvidence(
                              "tenant-x", "customer-x", "SUPERVISION_REQUIRED", 75, ""));

              assertEquals(1, result.exports().size());
              var evidence = result.exports().getFirst();
              assertEquals("export-d", evidence.requestId());

              var rendered = evidence.toString();
              assertFalse(rendered.contains("authctx-"));
              assertFalse(rendered.contains("idem-"));
              assertFalse(rendered.contains("export://"));
              assertFalse(rendered.contains("capability-started"));
            });
  }

  private SupervisedExportState supervisionRequired(
      String workflowId, String tenantId, String customerId, int riskScore) {
    return SupervisedExportState.started(
            workflowId,
            tenantId,
            customerId,
            "ops-user",
            "authctx-" + tenantId,
            "idem-" + workflowId,
            "customer-records",
            riskScore)
        .riskAssessed("supervision-required")
        .supervisionRequired("risk-score-" + riskScore);
  }

  private SupervisedExportState ready(String workflowId, String tenantId, String customerId, int riskScore) {
    return SupervisedExportState.started(
            workflowId,
            tenantId,
            customerId,
            "ops-user",
            "authctx-" + tenantId,
            "idem-" + workflowId,
            "customer-records",
            riskScore)
        .riskAssessed("auto-allowed")
        .generating()
        .ready("export://" + tenantId + "/" + customerId + "/" + workflowId);
  }
}
