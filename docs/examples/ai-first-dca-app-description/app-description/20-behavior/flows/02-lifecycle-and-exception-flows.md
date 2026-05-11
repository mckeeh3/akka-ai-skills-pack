# Lifecycle and Exception Flows

## Purpose

This file records workflow examples beyond the supplies autopilot slice. They are intentionally concise and serve as reference app-description material for future decomposition.

## New customer onboarding

```text
New customer acquired
-> contract and site details imported or entered
-> Onboarding Agent creates onboarding plan
-> devices, sites, contacts, and DCA requirements are validated
-> Install Coordinator creates installation tickets
-> DCA Monitoring Agent schedules or verifies collector deployment
-> devices are installed
-> collector discovers devices
-> initial meter baselines are captured
-> Contract and Policy Agent validates device-contract mapping
-> Supplies Agent enables consumable monitoring
-> customer becomes Operational / Active Service
```

Approval or exception triggers:

- unusual contract terms;
- missing site access details;
- device substitution;
- manual meter baseline acceptance;
- DCA collector not reporting;
- customer communication exception.

Akka substrate mapping:

- onboarding plan and gate progression -> workflow;
- lifecycle events and approvals -> event sourced entity;
- blocked onboarding queue -> view;
- access-window reminders and stale install tasks -> timed actions.

## Active fleet monitoring

```text
Device telemetry received
-> DCA Monitoring Agent validates signal freshness and identity
-> Fleet Health Agent interprets fault, SLA, and replacement risk
-> Supplies Agent checks consumables
-> Meter and Billing Agent checks meter data
-> Contract and Policy Agent applies entitlement and policy clauses
-> routine work is handled automatically
-> exceptions become decision cards or blocked workflow states
```

Approval or exception triggers:

- stale or contradictory telemetry;
- repeated fault pattern;
- SLA risk beyond threshold;
- replacement candidate;
- contract mismatch;
- billing or supply side effect requires review.

## Service ticket workflow

```text
Device fault detected
-> Fleet Health Agent classifies issue
-> Contract and Policy Agent checks service coverage and SLA
-> Service recommendation is prepared
-> Inventory Agent checks required parts
-> ticket is auto-created or escalated
-> technician work is tracked
-> device status is validated after resolution
-> outcome is linked to SLA and customer health
```

Approval or exception triggers:

- emergency dispatch outside contract terms;
- expensive part order;
- remote access unavailable;
- repeated issue suggests replacement;
- high-value customer or sensitive communication path.

Safe defaults:

- prepare ticket draft instead of dispatch when authority is unclear;
- keep device in `Needs Service` or `Under Review` until validation evidence arrives;
- escalate overdue SLA risk.

## Meter and billing review

```text
Meter read collected
-> Meter and Billing Agent validates monotonicity, freshness, and device mapping
-> Contract and Policy Agent applies contract terms
-> billing event draft is prepared
-> anomalies are escalated
-> approved billing data is exported or invoiced
-> audit trail is updated
```

Approval or exception triggers:

- missing read;
- usage spike;
- manual adjustment;
- device lacks contract mapping;
- final read waiver;
- invoice-impacting anomaly.

Safe defaults:

- hold affected billing line or batch;
- request evidence or recheck telemetry;
- record reason for any estimate, waiver, or adjustment.

## Customer offboarding

```text
Customer termination begins
-> Offboarding Agent creates offboarding plan
-> devices requiring removal are identified
-> DCA collectors requiring removal/deauthorization are identified
-> removal tickets are created
-> pending service and supply workflows are paused or canceled
-> final meter reads are captured
-> final billing is prepared
-> devices are removed and disposition is recorded
-> DCA is removed or deauthorized
-> integrations and access are revoked
-> retention policy is applied
-> customer is archived
```

Approval or exception triggers:

- ownership or lease status unknown;
- final meter reads missing;
- pending supply shipment needs exception;
- DCA collector still online;
- data retention, deletion, anonymization, or hold decision required;
- device removal dispute or customer-sensitive communication.

Safe defaults:

- stop new automation that could create shipments, dispatches, or billing side effects;
- preserve audit and billing records;
- keep customer in explicit deactivation/archive-pending state until gates pass.

## Exception lifecycle

```text
Agent detects blocked or risky condition
-> workflow records exception case
-> evidence snapshot and policy clauses are attached
-> decision card is created for accountable role
-> workflow pauses or selects safe no-op
-> reviewer approves, rejects, modifies, defers, escalates, or requests evidence
-> workflow resumes or remains blocked
-> decision and outcome are traced
```

Exception state rules:

- recommendations are not final decisions;
- missing evidence must be visible as a first-class reason;
- expired decision cards escalate or recheck rather than silently completing;
- reviewer actions must map to workflow transitions and durable facts;
- human corrections can create policy proposals or reference examples, not automatic policy commits.
