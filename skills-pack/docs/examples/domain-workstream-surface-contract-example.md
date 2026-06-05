# Domain workstream surface contract example

Use this as a compact pattern for adding a domain-specific workstream surface after the core SaaS foundation is preserved. It is deliberately not a page, CRUD screen, or endpoint list.

## Input fragment

```text
Domain operators need an AI-assisted workspace for governed work items. They should see risky items, ask for more evidence, approve low-risk items, and escalate policy deviations.
```

## Workstream and dashboard

| Workstream | Backing functional agent | Dashboard/default surface | Attention categories |
|---|---|---|---|
| `domain-operations` | Domain Operations Agent | `domain-operations-dashboard` | policy deviation, missing evidence, budget risk, counterpart risk, approval waiting, failed integration |

The Domain Operations dashboard is the surface graph trunk. It summarizes open domain-specific work attention, shows agent/workflow progress, and exposes only capability-backed next actions.

## Surface contract sketch

- surface-id: `domain-work-item-review`
- type/version: decision-detail/v1
- owner functional agent: `domain-operations-agent`
- reusable by: Governance/Policy for policy-deviation review, Audit/Trace for evidence drill-in, My Account for assigned approval items
- placement: attention item drill-in from `domain-operations-dashboard`; deep-linkable through shell request routing

### Payload summary

- work item id, requester, amount or value, counterpart, category, period, target date, and current lifecycle state;
- policy evaluation summary with clause ids, threshold values, deviation reason, and active policy version;
- AI recommendation with confidence, risk, impact, alternatives, and evidence refs;
- missing evidence list, counterpart/budget signals, trace ids, `correlationId`, stale marker, and redaction profile;
- action descriptors with `browserToolId`, `governedToolId`, `capabilityId`, confirmation/approval requirements, idempotency, and result-surface behavior.

### Actions and capability mapping

| Surface action | Capability id | Governed-tool / exposure | Result surface |
|---|---|---|---|
| Refresh review | `domain.work-items.review.read` | `domain.review.read` as browser-tool/agent-tool | update current surface |
| Approve item | `domain.work-items.approve` | `domain.approve` as browser-tool | success `system_message`, workflow status, dashboard refresh |
| Reject item | `domain.work-items.reject` | `domain.reject` as browser-tool | system message plus audit trace |
| Request evidence | `domain.work-items.evidence.request` | `domain.request_evidence` as browser-tool/internal-tool | evidence task progress surface |
| Escalate deviation | `domain.deviations.escalate` | `domain.escalate_deviation` as browser-tool | `decision-card` in Governance/Policy |
| Open policy clause | `governance.policy.read` | `policy.open_clause` as browser-tool surface-request | policy version card |
| Open audit trace | `audit.traces.view` | `audit.open_trace` as browser-tool | `audit-trace-explorer` |

### States and tests

- `loading`: skeleton with no counterpart or requester leakage until authorized payload arrives.
- `empty`: work item resolved or no longer visible in selected scope.
- `forbidden`: no work-item existence leakage across tenants/customers/business units.
- `stale`: approval/rejection disabled until the request review is refreshed.
- `conflict`: work item lifecycle changed after the surface was generated.
- tests: role/scope denial, policy deviation evidence rendering, approve/reject idempotency, escalation to Governance/Policy decision card, audit trace links, stale/conflict recovery, frontend secret boundary, and responsive decision-detail layout.

## Akka substrate after capability contract

| Need | Candidate substrate |
|---|---|
| work item lifecycle and immutable decision facts | Event Sourced Entity |
| deterministic approval/escalation/evidence workflow | Workflow |
| dashboard, review queues, and My Account attention | Views |
| counterpart/budget/policy signal enrichment | Consumers and request-based Agents as bounded helpers |
| long-running counterpart risk investigation | AutonomousAgent task if durable lifecycle/progress/cancellation is needed |
| protected browser API and SSE updates | HTTP endpoint plus SSE/realtime UI |

Every selected substrate remains downstream of the surface/action/capability contract.
