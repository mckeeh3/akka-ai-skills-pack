# Policies and Approval Gates

## Purpose

This file captures example policies, approval boundaries, and governed behavior-change rules for the AI-first DCA reference app.

Policies are runtime business logic. They must be versioned, addressable, cited by agents/workflows, and enforced mechanically where they grant or deny authority.

## Policy object pattern

| Object | Purpose | Akka substrate mapping |
|---|---|---|
| `PolicyDocument` | Versioned policy set for one tenant/dealer and domain area. | Event sourced entity for audit-grade history. |
| `PolicyClause` | Stable, citeable rule or prose guidance with scope and disposition. | Event sourced child record or event-backed model. |
| `Threshold` | Numeric or categorical autonomy/risk/cost/confidence limit. | Event sourced when consequential; key value when simple current config is enough. |
| `ReferenceExample` | Human-approved example used as precedent or guidance. | Event sourced entity linked to original decision/trace. |
| `PolicyProposal` | Drafted change from a human or agent, not active yet. | Workflow plus event sourced proposal history. |
| `SimulationResult` | Replay/impact evidence before activation. | Event sourced or view-backed result linked to proposal. |
| `PolicyCommit` | Human-authorized activation record. | Event sourced fact with actor, version, scope, and evidence. |

## Disposition tags

Each policy clause can carry a default disposition:

- `auto`: agent/workflow may complete the action when all evidence and permission checks pass.
- `review`: agent may prepare the action, but a human must approve before side effects.
- `escalate`: agent must create an exception or decision card for accountable human review.
- `fyi`: agent may act or summarize, but must surface the result in the digest or command center.

Disposition is not enough by itself. The action must also pass role, tenant, customer, tool, lifecycle, and data-quality checks.

## Example policy clauses

### Installation readiness

| Clause ID | Rule | Default disposition | Approval gate |
|---|---|---|---|
| `INST-1.0` | A new customer requires an onboarding plan before device shipment or installation scheduling. | auto | Review if contract, site, or contact data is incomplete. |
| `INST-2.0` | Required DCA collector must be installed and reporting before the customer becomes operational. | escalate | Operations supervisor may waive with reason and follow-up task. |
| `INST-3.0` | Imported or manual initial meter baselines require approval before billing activation. | review | Billing owner approves or requests additional evidence. |
| `INST-4.0` | Devices cannot become billable until mapped to active contract terms. | escalate | Contract/policy owner resolves mismatch. |

### Supplies fulfillment

| Clause ID | Rule | Default disposition | Approval gate |
|---|---|---|---|
| `SUP-1.0` | Auto-ship consumables only for active, monitored, contract-covered devices. | auto | Escalate if lifecycle, monitoring, or entitlement is unclear. |
| `SUP-2.0` | Do not ship consumables when customer is `Offboarding Planned` or later unless explicitly approved. | escalate | Operations supervisor and supplies owner approve exception. |
| `SUP-3.0` | Abnormal consumption above `2x` baseline creates a decision card before shipment. | review | Supplies/inventory owner reviews evidence and alternatives. |
| `SUP-4.0` | Shipment cost above configured threshold requires approval. | review | Supplies/inventory owner or dealer owner based on cost tier. |
| `SUP-5.0` | Customer-specific supply preferences override default supplier choice when active and contract-compatible. | auto | Escalate if preference conflicts with margin or inventory policy. |

### Service and fleet health

| Clause ID | Rule | Default disposition | Approval gate |
|---|---|---|---|
| `SVC-1.0` | SLA-risk tickets escalate when unresolved beyond configured window. | escalate | Service manager chooses dispatch, remote fix, or customer communication. |
| `SVC-2.0` | Repeated fault patterns create replacement review. | review | Service manager or dealer owner approves replacement recommendation. |
| `SVC-3.0` | Emergency dispatch outside contract terms requires approval. | review | Service manager and contract owner approve cost/coverage exception. |
| `SVC-4.0` | Remote fix should be attempted before dispatch when device capability and customer access allow it. | auto | Escalate if remote access is unavailable or customer impact is high. |

### Meter and billing

| Clause ID | Rule | Default disposition | Approval gate |
|---|---|---|---|
| `BILL-1.0` | Missing meter reads must be resolved before billing batch approval. | escalate | Billing owner may defer, estimate, or exclude with audit reason. |
| `BILL-2.0` | Usage spikes above configured threshold require review. | review | Billing owner approves, adjusts, or requests evidence. |
| `BILL-3.0` | Final meter reads are required before customer archive unless waived. | review | Billing owner approves waiver with rationale. |
| `BILL-4.0` | Device must be contract-mapped before billing event generation. | escalate | Contract owner resolves mapping. |

### Offboarding and retention

| Clause ID | Rule | Default disposition | Approval gate |
|---|---|---|---|
| `OFF-1.0` | No archive until leased/provider-owned device disposition is recorded. | escalate | Operations supervisor resolves ownership/disposition. |
| `OFF-2.0` | DCA collector must be removed or deauthorized before archive completion. | escalate | Data steward approves deauthorization when physical removal is not possible. |
| `OFF-3.0` | Pending supply shipments must be canceled or explicitly allowed during offboarding. | auto | Supplies owner approves exceptions. |
| `OFF-4.0` | Billing and audit records must be preserved according to retention policy. | auto | Data steward approves retention hold or deletion/anonymization plan. |
| `OFF-5.0` | Non-required customer data may be deleted or anonymized only through a retention decision. | review | Data steward approves final action. |

## Governed change flow

1. Human correction, repeated exception, agent finding, or outcome metric suggests policy improvement.
2. Contract and Policy Agent drafts a `PolicyProposal` with clause changes, affected actions, and evidence.
3. Workflow requests simulation or replay against representative historical decisions where impact is material.
4. Policy owner reviews proposal, simulation, risk, and alternatives through a decision card.
5. Approval creates a `PolicyCommit`; rejection or modification records a durable decision and rationale.
6. Active workflows cite the new policy version only after commit.

## Required tests for future implementation

Future generated code should test:

- allowed auto-ship path;
- denied shipment for offboarding customer;
- review path for abnormal consumption;
- escalation for missing contract mapping;
- billing hold for missing reads;
- policy proposal without activation;
- policy commit requiring authorized human role;
- replay/simulation result linked to the proposal and commit.
