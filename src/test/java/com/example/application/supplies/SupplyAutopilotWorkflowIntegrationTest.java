package com.example.application.supplies;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.CommandException;
import akka.javasdk.testkit.TestKitSupport;
import com.example.domain.supplies.Supply.DeviceLifecycleStatus;
import com.example.domain.supplies.Supply.DeviceTelemetry;
import com.example.domain.supplies.Supply.TraceRef;
import com.example.domain.supplies.SupplyDecision;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class SupplyAutopilotWorkflowIntegrationTest extends TestKitSupport {

  @Test
  void activeNormalCostTelemetryAutoShipsThroughWorkflowGate() {
    var telemetry = telemetry("auto", DeviceLifecycleStatus.ACTIVE, 18, 4_000);

    componentClient
        .forWorkflow("supply-auto-1")
        .method(SupplyAutopilotWorkflow::start)
        .invoke(telemetry);

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var state = getWorkflow("supply-auto-1");
              var decision = getDecision("supply-auto-1");

              assertEquals(SupplyAutopilotWorkflow.Status.AUTO_SHIPMENT_PREPARED, state.status());
              assertTrue(state.shipmentPrepared());
              assertEquals(SupplyDecision.Status.SHIPMENT_PREPARED, decision.status());
              assertTrue(
                  decision.traceEvents().stream()
                      .anyMatch(event -> event.type().name().equals("SHIPMENT_PREPARED")));
            });
  }

  @Test
  void highCostTelemetryPausesForApprovalThenPreparesShipment() {
    var telemetry = telemetry("approval", DeviceLifecycleStatus.ACTIVE, 12, 9_100);

    componentClient
        .forWorkflow("supply-approval-1")
        .method(SupplyAutopilotWorkflow::start)
        .invoke(telemetry);

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var state = getWorkflow("supply-approval-1");
              assertEquals(SupplyAutopilotWorkflow.Status.WAITING_FOR_APPROVAL, state.status());
              assertTrue(state.staleDecisionTimerRequested());
              assertFalse(state.shipmentPrepared());
              assertEquals(SupplyDecision.Status.APPROVAL_REQUIRED, getDecision("supply-approval-1").status());
            });

    componentClient
        .forWorkflow("supply-approval-1")
        .method(SupplyAutopilotWorkflow::approve)
        .invoke(
            new SupplyAutopilotWorkflow.ApproveDecision(
                "idem-approve-supply-approval-1", "ops-supervisor", "evidence is sufficient"));

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var state = getWorkflow("supply-approval-1");
              assertEquals(SupplyAutopilotWorkflow.Status.APPROVED_SHIPMENT_PREPARED, state.status());
              assertTrue(state.shipmentPrepared());
              assertEquals(SupplyDecision.Status.SHIPMENT_PREPARED, getDecision("supply-approval-1").status());
            });
  }

  @Test
  void duplicateStartAndDuplicateApprovalAreNoOps() {
    var telemetry = telemetry("idem", DeviceLifecycleStatus.ACTIVE, 12, 9_100);
    componentClient.forWorkflow("supply-idem-1").method(SupplyAutopilotWorkflow::start).invoke(telemetry);
    var duplicateStart =
        componentClient.forWorkflow("supply-idem-1").method(SupplyAutopilotWorkflow::start).invoke(telemetry);

    assertEquals("supply-idem-1", duplicateStart.workflowId());

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> assertEquals(SupplyAutopilotWorkflow.Status.WAITING_FOR_APPROVAL, getWorkflow("supply-idem-1").status()));

    var approval =
        new SupplyAutopilotWorkflow.ApproveDecision(
            "idem-approve-supply-idem-1", "ops-supervisor", "approved once");
    componentClient.forWorkflow("supply-idem-1").method(SupplyAutopilotWorkflow::approve).invoke(approval);

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> assertEquals(SupplyAutopilotWorkflow.Status.APPROVED_SHIPMENT_PREPARED, getWorkflow("supply-idem-1").status()));

    var duplicateApproval =
        componentClient.forWorkflow("supply-idem-1").method(SupplyAutopilotWorkflow::approve).invoke(approval);

    assertEquals(SupplyAutopilotWorkflow.Status.APPROVED_SHIPMENT_PREPARED, duplicateApproval.status());
    assertEquals(4, getDecision("supply-idem-1").traceEvents().size());
  }

  @Test
  void offboardingTelemetrySuppressesShipmentWithoutSideEffect() {
    componentClient
        .forWorkflow("supply-suppress-1")
        .method(SupplyAutopilotWorkflow::start)
        .invoke(telemetry("suppress", DeviceLifecycleStatus.OFFBOARDING, 15, 4_000));

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var state = getWorkflow("supply-suppress-1");
              assertEquals(SupplyAutopilotWorkflow.Status.SUPPRESSED, state.status());
              assertFalse(state.shipmentPrepared());
              assertEquals(SupplyDecision.Status.SUPPRESSED, getDecision("supply-suppress-1").status());
            });
  }

  @Test
  void unmappedContractEscalatesMissingEvidenceWithoutOpeningDecision() {
    componentClient
        .forWorkflow("supply-missing-1")
        .method(SupplyAutopilotWorkflow::start)
        .invoke(telemetry("missing", DeviceLifecycleStatus.UNMAPPED_CONTRACT, 14, 4_000));

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var state = getWorkflow("supply-missing-1");
              assertEquals(SupplyAutopilotWorkflow.Status.EVIDENCE_MISSING, state.status());
              assertFalse(state.shipmentPrepared());
              assertEquals(SupplyDecision.Status.EMPTY, getDecision("supply-missing-1").status());
            });
  }

  @Test
  void staleApprovalEscalationRecordsAuditFactWithoutShipment() {
    componentClient
        .forWorkflow("supply-stale-1")
        .method(SupplyAutopilotWorkflow::start)
        .invoke(telemetry("stale", DeviceLifecycleStatus.ACTIVE, 12, 9_100));

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> assertEquals(SupplyAutopilotWorkflow.Status.WAITING_FOR_APPROVAL, getWorkflow("supply-stale-1").status()));

    componentClient
        .forWorkflow("supply-stale-1")
        .method(SupplyAutopilotWorkflow::escalateStale)
        .invoke(
            new SupplyAutopilotWorkflow.EscalateStaleDecision(
                "idem-stale-supply-stale-1", "supply-decision-timer", "approval SLA elapsed"));

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var state = getWorkflow("supply-stale-1");
              assertEquals(SupplyAutopilotWorkflow.Status.STALE_ESCALATED, state.status());
              assertFalse(state.shipmentPrepared());
              assertEquals(SupplyDecision.Status.STALE_ESCALATED, getDecision("supply-stale-1").status());
            });
  }

  @Test
  void approvalBeforePendingDecisionIsRejected() {
    var error =
        org.junit.jupiter.api.Assertions.assertThrows(
            CommandException.class,
            () ->
                componentClient
                    .forWorkflow("supply-not-started-1")
                    .method(SupplyAutopilotWorkflow::approve)
                    .invoke(
                        new SupplyAutopilotWorkflow.ApproveDecision(
                            "idem-approve-missing", "ops-supervisor", "approve")));

    assertTrue(error.getMessage().contains("supply autopilot not started"));
  }

  @Test
  void deterministicAgentStubHasNoAutonomousSideEffectMethods() {
    var methodNames =
        Arrays.stream(SupplyForecastAgent.class.getDeclaredMethods()).map(Method::getName).toList();

    assertFalse(methodNames.stream().anyMatch(name -> name.toLowerCase().contains("ship")));
    assertFalse(methodNames.stream().anyMatch(name -> name.toLowerCase().contains("approve")));
    assertFalse(methodNames.stream().anyMatch(name -> name.toLowerCase().contains("suppress")));
  }

  private SupplyAutopilotWorkflow.State getWorkflow(String workflowId) {
    return componentClient.forWorkflow(workflowId).method(SupplyAutopilotWorkflow::get).invoke();
  }

  private SupplyDecision.State getDecision(String decisionId) {
    return componentClient
        .forEventSourcedEntity(decisionId)
        .method(SupplyDecisionEntity::getDecision)
        .invoke();
  }

  private DeviceTelemetry telemetry(
      String suffix, DeviceLifecycleStatus lifecycleStatus, int tonerPercent, int pagesSinceLastSupply) {
    return new DeviceTelemetry(
        "idem-telemetry-" + suffix,
        new TraceRef("trace-supply-" + suffix, "corr-supply-" + suffix),
        "customer-" + suffix,
        "device-" + suffix,
        Instant.parse("2026-05-11T10:00:00Z"),
        tonerPercent,
        pagesSinceLastSupply,
        lifecycleStatus);
  }
}
