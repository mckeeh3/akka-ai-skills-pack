# Capabilities Index

## Capability map

| ID | Capability | AI-first purpose | Initial status |
|---|---|---|---|
| CAP-01 | Lifecycle orchestration | Keep customers, devices, and DCA collectors moving through explicit lifecycle states and gates. | foundation |
| CAP-02 | Telemetry intelligence | Convert meter reads, consumable levels, faults, collector health, and availability signals into actionable work. | foundation |
| CAP-03 | Supplies autopilot | Forecast depletion, verify entitlement and stock, prepare shipments, and escalate high-cost or abnormal cases. | first implementation slice |
| CAP-04 | Service coordination | Detect service needs, prepare tickets, recommend remote fixes or dispatches, and escalate SLA risk. | planned |
| CAP-05 | Meter and billing review | Prepare billing-impacting records from telemetry while surfacing anomalies and missing reads for review. | planned |
| CAP-06 | Onboarding and installation | Plan new customer onboarding, coordinate installation, verify DCA reporting, map devices to contracts, and open operational service. | planned |
| CAP-07 | Offboarding and retention | Stop automation safely, remove or deauthorize devices and collectors, resolve final billing, and apply retention policy. | planned |
| CAP-08 | Policy governance | Maintain approval thresholds, contract/service/supply/billing rules, and governed policy improvements. | planned |
| CAP-09 | Owner command center | Present active objectives, agent work, blocked lifecycle gates, pending decisions, risk, and outcomes. | planned |
| CAP-10 | Audit and outcome review | Explain what happened, who/what authorized it, which evidence and policies applied, and whether outcomes improved. | planned |

## Lifecycle foundation

The app centers work around three lifecycle families instead of isolated records.

### Customer lifecycle

```text
Lead / Prospect
→ Acquired / Contract Pending
→ Onboarding Planned
→ Installation In Progress
→ DCA Validation
→ Operational / Active Service
→ At Risk / Exception State
→ Renewal / Expansion
→ Reduction / Device Change
→ Offboarding Planned
→ Removal In Progress
→ Decommissioned
→ Archived
```

### Device lifecycle

```text
Planned
→ Ordered / Allocated
→ Staged
→ Install Scheduled
→ Installed
→ DCA Discovered
→ Operational
→ Monitored
→ Needs Service
→ Supply Risk
→ Under Review
→ Replacement Candidate
→ Removal Scheduled
→ Removed
→ Decommissioned
→ Returned / Disposed / Reassigned
→ Archived
```

### DCA collector lifecycle

```text
Required
→ Install Scheduled
→ Installed
→ Connected
→ Discovering Devices
→ Healthy
→ Offline
→ Needs Update
→ Misconfigured
→ Removal Scheduled
→ Removed
→ Deauthorized
→ Archived
```

## First slice: supplies autopilot

The first implementation slice should prove the operating model with bounded automation:

1. receive or refresh device consumable telemetry;
2. forecast depletion and urgency;
3. verify contract entitlement and lifecycle state;
4. check inventory and shipment constraints;
5. auto-ship only when policy allows;
6. escalate abnormal, expensive, offboarding, or ambiguous cases with a decision card;
7. record trace and outcome data.

## Scope boundaries

The example intentionally preserves CRM, ERP, and DCA concepts as source/integration domains, but the product boundary is lifecycle operations. It may read from or write to existing systems, yet its own authoritative description focuses on goals, delegated work, policies, decisions, traces, and outcomes.
