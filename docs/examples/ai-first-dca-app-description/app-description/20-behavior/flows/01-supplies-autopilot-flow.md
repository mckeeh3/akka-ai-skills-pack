# Supplies Autopilot Flow

## Purpose

This is the first implementation-slice workflow example for the AI-first DCA app description. It proves the AI-first loop with a bounded, high-volume operational domain.

```text
telemetry -> depletion projection -> policy check -> shipment recommendation -> auto-ship or decision card -> trace -> outcome
```

## Trigger

A supplies flow starts when one of these events occurs:

- consumable telemetry is received or refreshed;
- DCA collector reports changed device supply level;
- usage baseline changes materially;
- inventory level or supplier availability changes;
- customer/device lifecycle state changes;
- contract entitlement or customer-specific supply policy changes;
- a timed recheck fires for a deferred or pending recommendation.

## Required inputs

- customer, site, device, and assignment identifiers;
- current customer and device lifecycle state;
- device telemetry freshness and consumable level;
- usage baseline and depletion forecast;
- active contract and supply entitlement;
- customer-specific preferences or overrides;
- inventory availability, supplier options, cost, and delivery estimate;
- active policy document/version and relevant clauses;
- recent service/offboarding/billing context that can affect shipment safety.

## Workflow steps

1. `DCA Monitoring Agent` validates telemetry freshness and device identity.
2. `Supplies Agent` forecasts depletion date, urgency, and candidate supply item.
3. `Contract and Policy Agent` checks entitlement, lifecycle, customer override, and policy clauses.
4. `Inventory Agent` checks local stock, supplier availability, cost, and substitution risk.
5. Workflow evaluates authority:
   - auto-ship if all policy, evidence, lifecycle, entitlement, stock, and threshold checks pass;
   - create decision card when review or escalation is required;
   - suppress shipment and record reason when shipment is not allowed.
6. Approved shipment creates or exports a fulfillment order.
7. Workflow emits trace and outcome follow-up events.

## Policy gates

| Gate | Auto-pass condition | Review/escalation condition | Safe default |
|---|---|---|---|
| Lifecycle | Customer is `Operational / Active Service`; device is monitored and not removal/offboarding flagged. | Customer is `Offboarding Planned` or later, device is under review, ownership unknown, or lifecycle stale. | Do not ship. |
| Entitlement | Device assignment maps to active contract with supplies included. | Contract missing, expired, ambiguous, or excludes requested supply. | Do not ship; create exception. |
| Telemetry freshness | Supply level and usage baseline are fresh enough for configured policy. | Stale telemetry, conflicting reads, or device identity mismatch. | Recheck or request evidence. |
| Depletion urgency | Forecast indicates shipment is needed within policy window. | Forecast confidence is low or usage spike suggests abnormal consumption. | Create decision card. |
| Cost and stock | Item is in stock and cost is below auto-approval threshold. | Cost exceeds threshold, supplier substitution needed, or inventory is constrained. | Create decision card. |
| Customer override | No blocking customer preference exists or preference is compatible with fulfillment. | Receiving, OEM/aftermarket, communication, or delivery override conflicts with recommendation. | Create decision card. |

## Auto-ship path

A shipment may be automatic only when all are true:

- active contract covers the consumable;
- customer and device lifecycle permit supply automation;
- telemetry is fresh and depletion forecast is confident;
- shipment cost is below threshold;
- inventory/supplier choice fits policy and customer preference;
- no abnormal consumption, offboarding, billing hold, or customer-specific block exists.

Auto-ship emits:

- `SupplyRecommendation` with evidence and policy citations;
- `FulfillmentOrderPrepared` or integration command;
- `PolicyInvocation` records for applied clauses;
- `ToolInvocation` or `DataAccessEvent` for inventory/supplier checks;
- `WorkTrace` linked to goal, device assignment, and policy version;
- `OutcomeLink` for later delivery success, stock impact, cost, and customer feedback.

## Decision-card path

Create a decision card when:

- abnormal consumption exceeds threshold;
- shipment is high cost;
- stock is constrained or substitution is proposed;
- customer-specific override affects the action;
- lifecycle or contract facts are incomplete;
- customer is offboarding or device is under removal/replacement review;
- confidence is below configured level.

The decision card must include:

- recommended action and alternatives;
- policy clauses and thresholds triggered;
- telemetry and forecast evidence;
- contract entitlement status;
- inventory/cost/delivery evidence;
- customer override or lifecycle context;
- allowed reviewer actions.

## Suppression path

Suppress shipment without human approval when policy clearly denies it and no useful decision exists, for example:

- customer is archived;
- device is removed or decommissioned;
- consumable is not contract-covered and no manual order path is in scope;
- telemetry maps to an unknown or unassigned device.

Suppression still records a durable reason and trace link.

## No-op and idempotency behavior

- Do not create duplicate shipments for the same device/supply/depletion window.
- If a recommendation is already pending review, update evidence rather than creating a second card.
- If a shipment was approved, later telemetry should not re-trigger until the policy-defined cool-down or delivery confirmation state changes.
- If offboarding starts while a shipment is pending, pause or cancel according to `OFF-3.0` and record the transition.

## Akka substrate mapping

- Supplies workflow -> Akka workflow with approval pause/resume.
- Supply recommendation and decision history -> event sourced entity.
- Current inventory snapshot or supplier availability cache -> key value entity when audit history is not required.
- Supplies queue and command-center lists -> views.
- Recheck and depletion reminders -> timed actions.
- Supplier order integration and notifications -> consumers or HTTP client endpoint patterns.
- Supplies decision card UI -> React/Vite app hosted through Akka HTTP endpoints.
