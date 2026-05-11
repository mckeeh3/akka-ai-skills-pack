# Approval and Fail-Safe Rules

## Purpose

This file turns the DCA operating model into behavior rules that future workflow, agent, endpoint, and UI implementation tasks can test.

## Universal rules

1. A recommendation is not a decision unless an active policy grants bounded automation for the exact action.
2. Lifecycle-changing, billing-impacting, shipment, dispatch, removal, deauthorization, retention, deletion, or policy-changing actions must cite active policy and evidence.
3. Agents must pause rather than guess when customer, device, contract, ownership, telemetry, inventory, or authority facts are missing.
4. Human approval must record role, actor, decision, evidence snapshot, policy version, and rationale when consequential.
5. Policy proposals, prompt changes, skill changes, threshold changes, and permission changes do not become active until governed commit.
6. Failed or denied actions must preserve trace data and avoid duplicate side effects on retry.

## Approval boundary matrix

| Action | Auto allowed when | Human approval required when | Safe fail behavior |
|---|---|---|---|
| Supply shipment | Active monitored device; contract-covered; below cost threshold; normal consumption; inventory available; no customer block. | High cost, abnormal consumption, customer override, stock constraint, entitlement ambiguity, offboarding, or low confidence. | Do not ship; create or update decision card. |
| Service ticket creation | Routine covered issue; SLA/customer impact within threshold; no expensive parts or dispatch exception. | Emergency/out-of-contract dispatch, expensive part, repeated fault, replacement review, sensitive customer impact. | Draft ticket; keep device in service review. |
| Billing event generation | Fresh valid read; contract mapped; no anomaly; batch rules pass. | Missing read, spike, manual adjustment, final read waiver, unmapped contract. | Hold affected line or batch. |
| Onboarding operational transition | All installation, DCA, device mapping, meter baseline, supply monitoring, SLA, contact, and ticket gates pass or are approved deferred items. | Any gate missing, manually accepted, waived, or customer-sensitive. | Keep customer in installation/DCA validation state. |
| Offboarding archive transition | Device disposition, final reads, billing, collector removal/deauthorization, workflow closure, shipment handling, portal/integration access, retention, and audit gates pass. | Ownership unclear, final reads missing, collector online, retention/deletion decision, dispute, pending shipment exception. | Keep customer in deactivation/archive-pending state. |
| Policy activation | Never by agent alone in this example. | Always for authority, threshold, permission, prompt, skill, or clause activation. | Store proposal only. |

## Required evidence by decision type

| Decision type | Minimum evidence |
|---|---|
| Supply shipment | telemetry level, depletion forecast, contract entitlement, lifecycle state, inventory/cost, customer preference, policy clauses. |
| Service dispatch | fault details, service history, SLA/contract, customer impact, technician/parts availability, alternatives. |
| Billing exception | meter reads, anomaly details, contract terms, billing batch context, affected amount, prior adjustments. |
| Onboarding gate | install tickets, DCA heartbeat/discovery, device-contract mapping, baseline reads, contacts, deferred items. |
| Offboarding gate | termination context, ownership/disposition, removal tickets, final reads, pending services/supplies, access revocation, retention decision. |
| Policy change | source decision/feedback, affected clauses, proposed version, simulation/replay evidence when material, approver authority. |

## Idempotency rules

- Use stable business keys for recommendations: customer, device assignment, action type, supply/service/billing period, and policy version where relevant.
- A retry must update the existing workflow/decision card when the business action is the same.
- External side effects such as supplier orders, service tickets, billing exports, DCA deauthorization, or deletion requests require idempotency keys and recorded result status.
- A timed recheck can refresh evidence and risk, but must not create duplicate approval cards for the same unresolved issue.

## Trace rules

Every consequential action must link:

- goal or lifecycle workflow;
- actor type and actor id;
- agent/prompt/skill version when an agent produced the recommendation;
- policy document version and clause ids;
- evidence snapshot available at decision time;
- tool/data-access records;
- reviewer decision when applicable;
- outcome follow-up when available.

## Future test hooks

Future implementation tasks should include tests for:

- automatic allowed path;
- approval-required path;
- denied/suppressed path;
- missing-evidence path;
- retry/idempotent path;
- stale decision timeout path;
- policy proposal but not activation path;
- trace completeness for approval and automated action paths.
