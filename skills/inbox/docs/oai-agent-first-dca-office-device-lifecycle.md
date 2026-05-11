# Agent-First DCA and Office Device Lifecycle Application

## Purpose

This document captures the second major topic from the session: applying the ai-first operating system concept to a specific small-business application for office-device sales and service providers.

The target business provides sales, leasing, installation, monitoring, maintenance, supplies, and removal services for office devices such as printers, copiers, scanners, and plotters.

The business currently depends on three core SaaS systems:

- CRM: customer, sales, contacts, opportunities, renewals
- ERP: contracts, orders, inventory, service tickets, billing, invoices
- DCA: device monitoring, meter reads, consumables, alerts, telemetry

The product opportunity is not to create cheaper clones of these three systems. The opportunity is to create an ai-first operating platform that unifies the customer-device-contract-service-billing lifecycle.

---

## Core Thesis

Small office-device dealers do not need separate record-centric CRM, ERP, and DCA tools. They need an ai-first operations platform that monitors the installed device base, performs routine work, escalates judgment calls, and continuously improves business policy from owner decisions.

The DCA layer is a strong starting point because device telemetry provides continuous operational signals. These signals can drive agents that coordinate service, supplies, billing, customer success, renewals, and lifecycle management.

The application should not be understood as “DCA with AI.”

It should be understood as:

> An ai-first operating system for office-device dealers, starting with device telemetry.

---

## Business Context

The service provider manages customers with one or more sites. Each site may contain multiple office devices. Devices may be owned, leased, financed, serviced under contract, or monitored only. Devices generate telemetry, meter reads, consumable levels, faults, and availability signals.

The provider must:

- Acquire new customers
- Install devices
- Deploy onsite DCA collectors when needed
- Make devices operational
- Monitor device health
- Maintain uptime
- Dispatch service
- Supply consumables
- Capture meter reads
- Prepare billing events
- Manage contracts
- Identify renewal and upgrade opportunities
- Remove leased devices at relationship end
- Decommission onsite DCA collectors
- Deactivate, archive, or remove customer data

---

## Traditional System Split

Traditional tools divide this lifecycle across multiple systems.

```text
CRM
- Customers
- Contacts
- Sales activity
- Opportunities
- Renewals

ERP
- Contracts
- Service tickets
- Inventory
- Orders
- Invoices
- Billing
- Purchasing

DCA
- Device inventory
- Meter reads
- Alerts
- Consumables
- Device status
- Usage trends
```

This split creates operational fragmentation. A single device event can affect service, supplies, billing, contract compliance, customer health, and sales opportunity management.

AI-first design should unify these workflows around lifecycle state and business outcomes.

---

## Product Vision

The application should manage the entire customer-device lifecycle:

```text
Customer acquired
→ Contract activated
→ Devices planned
→ Installation tickets created
→ Devices installed
→ DCA deployed
→ Devices discovered and validated
→ Devices operational
→ Fleet monitored
→ Supplies replenished
→ Service incidents handled
→ Contract renewed, expanded, reduced, or terminated
→ Customer offboarded
→ Devices removed
→ DCA removed/deauthorized
→ Customer data deactivated, archived, or removed
```

The app should always know:

```text
What lifecycle state is this customer in?
What lifecycle state is each device in?
What must happen next?
Which agents can act automatically?
What requires human judgment?
What policies apply?
What risks block progression?
What records must be preserved?
```

---

## Lifecycle-Centered Design

Customer device management should not be modeled as static inventory management. It should be modeled as lifecycle orchestration.

### Customer Lifecycle States

```text
Lead / Prospect
Acquired / Contract Pending
Onboarding Planned
Installation In Progress
DCA Validation
Operational / Active Service
At Risk / Exception State
Renewal / Expansion
Reduction / Device Change
Offboarding Planned
Removal In Progress
Decommissioned
Archived
```

### Device Lifecycle States

```text
Planned
Ordered / Allocated
Staged
Install Scheduled
Installed
DCA Discovered
Operational
Monitored
Needs Service
Supply Risk
Under Review
Replacement Candidate
Removal Scheduled
Removed
Decommissioned
Returned / Disposed / Reassigned
Archived
```

### DCA Collector Lifecycle States

```text
Required
Install Scheduled
Installed
Connected
Discovering Devices
Healthy
Offline
Needs Update
Misconfigured
Removal Scheduled
Removed
Deauthorized
Archived
```

---

## Lifecycle Gates

Lifecycle transitions should have explicit completion gates.

### Installation to Operational Gate

A customer should not move from Installation In Progress to Operational until:

```text
- All planned devices are installed or explicitly deferred
- DCA collector is installed if required
- DCA collector is reporting
- Devices are discovered and mapped
- Devices are mapped to contract terms
- Initial meter baselines are captured
- Supply monitoring is enabled
- Service coverage and SLA are confirmed
- Customer contacts are confirmed
- Open installation tickets are closed or deferred
```

### Operational to Offboarding Gate

A customer can enter offboarding when:

```text
- Contract has ended, been terminated, or customer relationship is ending
- Device ownership and lease status are known
- Open service tickets are reviewed
- Pending supply orders are reviewed
- Billing status is reviewed
- Removal plan is created
```

### Removal to Archived Gate

A customer should not move from Removal In Progress to Archived until:

```text
- All provider-owned or leased devices have disposition recorded
- Final meter reads are captured or explicitly waived
- Final billing events are prepared or closed
- DCA collector is removed or deauthorized
- Integrations and access tokens are revoked
- Open service workflows are closed
- Pending supply shipments are canceled or resolved
- Customer portal access is disabled if applicable
- Data retention policy is applied
- Audit package is generated
```

---

## Agent Team

The application should include specialized agents with clear permissions and escalation rules.

### Owner Briefing Agent

Purpose: Summarize the business state and direct owner attention.

Responsibilities:

- Generate morning brief
- Summarize overnight agent activity
- Identify high-stakes items
- Rank waiting decisions
- Connect events to objectives
- Summarize policy impacts
- Highlight customer risk and opportunity

### Onboarding Agent

Purpose: Move newly acquired customers from contract to operational service.

Responsibilities:

- Create onboarding plan
- Verify contract and customer data
- Identify devices to install
- Identify DCA collector requirements
- Create installation checklist
- Coordinate installation tickets
- Verify readiness for operational state
- Escalate blocked onboarding steps

### Install Coordinator Agent

Purpose: Coordinate installation service tickets.

Responsibilities:

- Create installation tickets
- Assign technicians according to policy
- Verify device staging requirements
- Confirm customer access windows
- Track install completion
- Validate initial device status
- Trigger post-install verification

### DCA Monitoring Agent

Purpose: Manage device telemetry and DCA collector health.

Responsibilities:

- Monitor DCA collector connectivity
- Detect device discovery changes
- Validate reporting health
- Identify missing or stale telemetry
- Detect offline devices
- Flag DCA misconfiguration
- Escalate collector removal/deauthorization during offboarding

### Fleet Health Agent

Purpose: Monitor active devices and recommend service actions.

Responsibilities:

- Detect device faults
- Identify recurring issues
- Predict SLA risk
- Recommend preventive maintenance
- Recommend replacement when service cost or downtime exceeds threshold
- Prepare service tickets
- Escalate high-impact service issues

### Supplies Agent

Purpose: Manage consumables lifecycle.

Responsibilities:

- Monitor consumable levels
- Forecast depletion
- Prepare supply shipments
- Suppress unnecessary shipments
- Detect abnormal consumption
- Check contract entitlements
- Cancel or pause supplies during offboarding
- Escalate high-cost or unusual supply actions

### Meter and Billing Agent

Purpose: Convert device telemetry into reliable billing events.

Responsibilities:

- Collect meter reads
- Detect missing reads
- Detect anomalous usage
- Validate contract terms
- Prepare billing batches
- Flag billing exceptions
- Reconcile invoice-impacting data
- Capture final meter reads during offboarding

### Contract and Policy Agent

Purpose: Apply commercial and operational policies.

Responsibilities:

- Map devices to contracts
- Validate included services and consumables
- Apply SLA and billing terms
- Detect contract mismatches
- Enforce approval thresholds
- Recommend policy improvements
- Simulate policy changes against historical decisions

### Customer Success Agent

Purpose: Detect customer risk and opportunity.

Responsibilities:

- Monitor customer health signals
- Detect churn risk
- Detect expansion/upgrade opportunities
- Flag repeated service dissatisfaction
- Recommend proactive outreach
- Prepare renewal context

### Inventory Agent

Purpose: Maintain supply and part readiness.

Responsibilities:

- Forecast consumable and part demand
- Detect low inventory
- Recommend purchase orders
- Reserve parts for service tickets
- Prevent overstock
- Coordinate with Supplies Agent and Service Agent

### Offboarding Agent

Purpose: End customer relationships safely and completely.

Responsibilities:

- Create offboarding plan
- Identify devices requiring removal
- Identify DCA collectors requiring removal/deauthorization
- Create removal tickets
- Coordinate pickup and access windows
- Ensure final meter reads
- Stop monitoring and supply workflows
- Revoke integrations
- Trigger data archival/removal workflows
- Generate offboarding audit package

---

## Agent-First Workflows

### New Customer Onboarding Workflow

```text
New customer acquired
→ Contract details imported or entered
→ Onboarding Agent creates plan
→ Devices and sites are validated
→ Install Coordinator creates service tickets
→ DCA Monitoring Agent schedules collector deployment
→ Devices are installed
→ DCA discovers devices
→ Meter baselines are captured
→ Contract Agent validates device-contract mapping
→ Supplies Agent enables consumable monitoring
→ Customer becomes Operational / Active Service
```

Human approvals may be required for:

- Unusual contract terms
- Missing site access details
- Device substitutions
- Manual meter baseline acceptance
- Customer communication exceptions

### Active Fleet Monitoring Workflow

```text
Device telemetry received
→ DCA Monitoring Agent validates signal freshness
→ Fleet Health Agent interprets device status
→ Supplies Agent checks consumables
→ Meter Agent checks meter data
→ Contract Agent applies policy
→ Routine work is auto-handled
→ Exceptions are escalated to human
```

### Supplies Autopilot Workflow

```text
Consumable level drops
→ Supplies Agent forecasts depletion
→ Contract Agent checks entitlement
→ Inventory Agent checks stock
→ Supplies Agent prepares shipment
→ Policy Agent checks auto-ship criteria
→ Shipment is auto-approved or escalated
→ Customer communication is generated
→ Audit event is recorded
```

### Service Ticket Workflow

```text
Device fault detected
→ Fleet Health Agent classifies issue
→ Contract Agent checks SLA
→ Service Agent recommends remote fix or dispatch
→ Inventory Agent checks required parts
→ Ticket is auto-created or escalated
→ Technician work is tracked
→ Device status is validated after resolution
```

### Meter and Billing Workflow

```text
Meter read collected
→ Meter Agent validates read
→ Contract Agent applies contract terms
→ Billing event is prepared
→ Anomalies are escalated
→ Approved billing data is exported or invoiced
→ Audit trail is updated
```

### Customer Offboarding Workflow

```text
Customer termination begins
→ Offboarding Agent creates offboarding plan
→ Devices requiring removal are identified
→ DCA collectors requiring removal/deauthorization are identified
→ Removal tickets are created
→ Pending service and supply workflows are paused or canceled
→ Final meter reads are captured
→ Final billing is prepared
→ Devices are removed and disposition recorded
→ DCA is removed/deauthorized
→ Integrations and access are revoked
→ Data retention policy is applied
→ Customer is archived
```

---

## Primary UI Surfaces

### 1. Owner Morning Brief

Purpose: Give the owner a ranked summary of what happened and what needs attention.

Example content:

```text
Good morning
Last 16 hours
138 routine actions auto-handled
0 policy violations

Worth knowing
- Renewal closed
- Customer risk deepened
- Install blocked by missing access window
- Supplies auto-shipped for 12 devices
- Meter anomaly detected on high-volume account

Waiting for you
- Approve emergency toner shipment
- Review meter anomaly
- Confirm device removal date
- Approve replacement recommendation
```

### 2. Customer Lifecycle Command Center

Purpose: Show one customer’s lifecycle state, blockers, agent actions, and required human decisions.

Example for onboarding:

```text
Customer: Northwind Architecture
Lifecycle: Installation In Progress

Timeline:
Acquired → Install Planned → Install In Progress → DCA Validation → Operational

Current blockers:
- DCA collector not yet reporting
- 2 devices missing baseline meter reads
- Customer access window not confirmed

Agent actions:
- Install Agent created 4 service tickets
- DCA Agent detected 3 of 5 devices
- Contract Agent mapped 4 devices to agreement
- Supplies Agent paused auto-ship until operational

Waiting for you:
- Approve second technician visit
- Confirm legacy device removal
- Accept manual baseline for plotter
```

### 3. Device Fleet Intelligence

Purpose: Show device fleet status by business consequence rather than raw status only.

Group devices by:

- SLA risk
- Supply risk
- Billing anomaly
- Repeated service issue
- Replacement candidate
- Underused device
- Contract mismatch
- DCA reporting issue

### 4. Service Command Center

Purpose: Let agents triage, prepare, and manage service work.

Content:

- Active service tickets
- Agent recommendations
- SLA risk
- Required parts
- Technician assignment
- Customer communications
- Remote resolution options
- Approval-needed dispatches

### 5. Supplies Autopilot

Purpose: Manage consumable replenishment with agent automation.

Content:

- Upcoming depletion forecasts
- Prepared shipments
- Auto-approved shipments
- Approval-needed shipments
- Suppressed shipments
- Abnormal consumption alerts
- Contract entitlement exceptions

### 6. Meter and Billing Review

Purpose: Ensure reliable revenue capture.

Content:

- Meter read status
- Missing reads
- Usage anomalies
- Contract overages
- Billing batches
- Exceptions requiring approval
- Final reads for offboarding

### 7. Offboarding Control Room

Purpose: Manage safe customer termination, physical asset removal, DCA removal, and data retention.

Example content:

```text
Customer Offboarding: Initech

Physical removal:
- 4 leased copiers
- 1 plotter
- 1 DCA collector
- 2 pickup tickets scheduled
- 1 missing access window

Financial closure:
- Final meter reads pending
- Final invoice draft ready
- Contract termination fee waived by policy

System cleanup:
- DCA collector still online
- Customer portal disabled
- API integrations revoked
- Automated toner shipments stopped

Data handling:
- Operational records archived
- PII deletion pending retention approval
- Audit package generated
```

### 8. Policy and Automation Center

Purpose: Let the owner define and tune agent behavior.

Policy areas:

- Installation readiness
- Auto-dispatch
- Auto-ship supplies
- Meter anomaly handling
- Billing approvals
- Customer communication
- SLA escalation
- Replacement recommendations
- Offboarding completion gates
- Data retention

### 9. Approvals and Exceptions Queue

Purpose: Present only decisions requiring human judgment.

Examples:

- Emergency dispatch outside contract terms
- Expensive part order
- Abnormal meter spike
- Contract exception
- High-cost supply shipment
- Customer churn risk escalation
- Replacement quote recommendation
- Device removal dispute
- Data deletion approval

---

## Core Domain Model

The backend model should support lifecycle orchestration, not only static records.

Core entities:

```text
Customer
Site
Contact
Device
DeviceAssignment
Contract
ServiceTicket
InstallPlan
RemovalPlan
DcaCollector
MeterRead
ConsumableStatus
SupplyOrder
InventoryItem
BillingEvent
LifecycleEvent
LifecycleState
Policy
AgentAction
ApprovalDecision
DataRetentionRecord
AuditRecord
```

### DeviceAssignment

DeviceAssignment is especially important because devices can move, be replaced, be leased, be removed, or be reassigned.

Suggested fields:

```yaml
device_assignment:
  id: string
  customer_id: string
  site_id: string
  device_id: string
  contract_id: string
  assignment_status: planned | installed | operational | removal_scheduled | removed | archived
  start_date: date
  end_date: date | null
  install_ticket_id: string | null
  removal_ticket_id: string | null
  billing_status: active | paused | final_pending | closed
  monitoring_status: pending | active | stale | stopped
  supply_status: pending | active | paused | stopped
  ownership_type: provider_owned | customer_owned | leased | financed | unknown
  disposition: active | returned | disposed | reassigned | customer_retained | unknown
```

### LifecycleEvent

Lifecycle events should create a durable timeline.

```yaml
lifecycle_event:
  id: string
  entity_type: customer | device | dca_collector | contract | service_ticket
  entity_id: string
  from_state: string
  to_state: string
  reason: string
  actor_type: human | agent | system
  actor_id: string
  timestamp: datetime
  related_policy_id: string | null
  related_ticket_id: string | null
  audit_record_id: string
```

---

## Policy Examples

### Installation Policies

```text
- New customer requires an installation plan before device shipment.
- DCA collector must be online before account becomes operational unless explicitly waived.
- Initial meter baselines require approval if imported manually.
- Devices cannot become billable until contract mapping is complete.
```

### Supply Policies

```text
- Auto-ship consumables only for contract-covered devices.
- Do not ship consumables if customer is in Offboarding Planned or later state.
- Escalate abnormal consumption over 2x baseline.
- Require approval for shipments above configured cost threshold.
```

### Service Policies

```text
- SLA-risk tickets escalate if unresolved within configured window.
- Repeated fault patterns trigger replacement review.
- Emergency dispatch outside contract terms requires approval.
- Remote fix should be attempted before dispatch when device capability allows.
```

### Billing Policies

```text
- Missing meter reads must be resolved before billing batch approval.
- Usage spikes above threshold require review.
- Final meter reads are required before customer archive unless waived.
- Device must be contract-mapped before billing event generation.
```

### Offboarding Policies

```text
- No final archive until leased/provider-owned devices have disposition recorded.
- DCA collector must be removed or deauthorized before archival completion.
- Pending supply shipments must be canceled or explicitly allowed.
- Preserve billing and audit records according to retention policy.
- Delete, anonymize, or archive non-required customer data after retention window.
```

---

## Decision Card Pattern for This Domain

Example decision card fields:

```yaml
decision_card:
  title: string
  customer: string
  site: string | null
  device: string | null
  lifecycle_context: string
  recommendation: string
  agent: string
  confidence: high | medium | low | numeric
  risk: high | medium | low
  business_impact: string
  policy_trigger:
    policy_name: string
    rule: string
    reason: string
  evidence:
    - source: meter_read | telemetry | contract | service_history | inventory | customer_note
      summary: string
      strength: high | medium | low
  alternatives:
    - option: string
      expected_outcome: string
      reason_not_recommended: string
  allowed_actions:
    - approve
    - reject
    - counter
    - create_ticket
    - request_more_evidence
    - update_policy_from_decision
  audit:
    trace_id: string
    created_at: datetime
```

---

## Example Agent-First Scenarios

### Low Toner Scenario

Traditional DCA output:

```text
Device: HP LaserJet M607
Customer: Acme Legal
Toner: 8%
Average daily pages: 1,240
Alert: Replace black cartridge
```

AI-first output:

```text
Acme Legal will run out of black toner in 2.7 days.

Recommendation:
Ship one cartridge today from local inventory.

Reasoning:
- Current toner: 8%
- Usage increased 22% over baseline
- Contract includes auto-supply
- Local stock available
- Shipment cost within policy

Action:
Auto-ship unless blocked in next 2 hours.
```

### Installation Blocker Scenario

```text
Northwind Architecture installation is blocked.

Issue:
DCA collector is installed but not reporting.

Agent findings:
- 3 of 5 devices discovered
- Collector heartbeat stopped 38 minutes after install
- Network credentials may be incorrect
- Technician ticket can be reopened today

Recommendation:
Schedule follow-up visit and keep customer in Installation In Progress.
```

### Offboarding Scenario

```text
Initech offboarding cannot be completed.

Blockers:
- One onsite DCA collector is still online
- Final meter reads missing for two leased devices
- One pending toner shipment must be canceled

Recommendation:
Create removal ticket, cancel pending shipment, and keep customer data in deactivation-pending state.
```

---

## Data Retention and Archival

Offboarding must include backend data handling.

The app should support:

- Deactivation of customer account
- Stopping automated workflows
- Revoking DCA credentials and API tokens
- Disabling portal access
- Archiving operational data
- Preserving billing records required by policy/law
- Preserving audit records
- Removing or anonymizing non-required personal data
- Recording retention decisions
- Producing an offboarding audit package

Suggested data states:

```text
Active
Deactivation Pending
Deactivated
Archive Pending
Archived
Deletion Pending
Deleted
Anonymized
Retention Hold
```

---

## Agent-Readable Specification Implications

An agent-readable specification for this product should define:

- Customer lifecycle states
- Device lifecycle states
- DCA collector lifecycle states
- Lifecycle gates
- Agent responsibilities
- Agent tools
- Agent permissions
- Automatic actions
- Approval-required actions
- Policy rules
- Decision card schemas
- Domain entities
- Audit events
- UI surfaces
- End-to-end workflow scenarios
- Testable acceptance criteria

Each workflow should answer:

```text
What event starts the workflow?
What agent receives it?
What data is required?
What policies apply?
What actions can be automatic?
What conditions require human approval?
What records are created or updated?
What audit events are recorded?
What state transition occurs?
How does the workflow fail safely?
```

---

## Recommended Next Specification Documents

This document can be decomposed into more precise agent-oriented specifications:

1. Customer Lifecycle State Machine Specification
2. Device Lifecycle State Machine Specification
3. DCA Collector Lifecycle Specification
4. Agent Team Responsibilities Specification
5. Agent Permissions and Policy Specification
6. Decision Card Schema Specification
7. Owner Morning Brief UX Specification
8. Customer Lifecycle Command Center UX Specification
9. Supplies Autopilot Workflow Specification
10. Meter and Billing Review Workflow Specification
11. Offboarding Control Room Specification
12. Audit and Data Retention Specification

---

## Summary

The ai-first DCA application should manage the full operational lifecycle of office-device customers and fleets.

The product should unify CRM, ERP, and DCA concerns around customer-device lifecycle orchestration.

The core product loop is:

```text
Device and business signals arrive
→ Agents interpret signals
→ Routine actions are handled automatically
→ Exceptions are escalated
→ Human decisions update policy
→ Lifecycle state progresses
→ Audit records preserve accountability
```

This lifecycle-first, agent-oriented model should guide the application architecture, backend domain model, UX surfaces, agent specifications, and coding harness instructions.
