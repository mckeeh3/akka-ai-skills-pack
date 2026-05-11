package com.example.application.supplies;

import akka.javasdk.annotations.Description;
import akka.javasdk.annotations.FunctionTool;
import com.example.domain.supplies.Supply.DeviceLifecycleStatus;

/** Deterministic tool stub for the supplies autopilot reference slice. */
public class SupplyForecastTools {

  @FunctionTool(description = "Forecast depletion risk from toner percent and recent print volume.")
  public double forecastDepletionRisk(
      @Description("Current toner percent from device telemetry.") int tonerPercent,
      @Description("Pages printed since the last replenishment.") int pagesSinceLastSupply) {
    var tonerRisk = Math.max(0.0, (35.0 - tonerPercent) / 35.0);
    var volumeRisk = Math.min(1.0, pagesSinceLastSupply / 10_000.0);
    return Math.min(1.0, Math.max(tonerRisk, volumeRisk));
  }

  @FunctionTool(description = "Return deterministic inventory evidence for a supplies SKU.")
  public ToolEvidence inventoryEvidence(@Description("Supply item SKU.") String sku) {
    return new ToolEvidence("inventory", "SKU " + sku + " is in stock at the regional depot.", 0.94);
  }

  @FunctionTool(description = "Return deterministic entitlement evidence for a customer/device lifecycle state.")
  public ToolEvidence entitlementEvidence(
      @Description("Customer identifier.") String customerId,
      @Description("Device lifecycle status.") DeviceLifecycleStatus lifecycleStatus) {
    if (lifecycleStatus == DeviceLifecycleStatus.UNMAPPED_CONTRACT) {
      return new ToolEvidence("entitlement", "No active supply contract mapping for " + customerId + ".", 0.35);
    }
    if (lifecycleStatus == DeviceLifecycleStatus.OFFBOARDING
        || lifecycleStatus == DeviceLifecycleStatus.SUSPENDED) {
      return new ToolEvidence("entitlement", "Supply entitlement is blocked by lifecycle status.", 0.88);
    }
    return new ToolEvidence("entitlement", "Customer has an active supply entitlement.", 0.96);
  }

  public record ToolEvidence(String source, String summary, double confidence) {}
}
