# Domain Workstream Contract Example

This example shows the expected shape for a non-foundation domain workstream. It is documentation guidance, not runtime source to copy wholesale.

## Workstream definition

| Field | Example |
|---|---|
| `workstreamId` | `sales-pipeline` |
| Display name | Sales Pipeline |
| Responsibility | Help authorized sales users understand pipeline health, follow-up risk, opportunity exceptions, approval needs, and next best governed actions. |
| Classification | `domain-specific` |
| Owning functional/context-area agent | `sales-pipeline-agent` |
| Icon metadata | `iconId: pipeline-chart`, `visualHint: rising-chart`, `accentColorToken: workstream.sales`, `ariaLabel: Sales Pipeline workstream` |
| Instance scope | `tenantId + selectedContextId + functionalAgentId`; customer scope only when selected customer context is active. |
| Authorized actors | Sales Rep, Sales Manager, Revenue Ops, Auditor read-only; disabled users and cross-customer actors receive `system_message` denials. |
| Default surface | `sales-pipeline-dashboard` |
| Readiness level | `described` until surfaces/capabilities/tests are completed. |

## Attention categories

| Category | Audience | Example source | Lifecycle | Aggregation |
|---|---|---|---|---|
| `overdue_item` | Sales Rep, Sales Manager | Opportunity follow-up due date passed. | open → acknowledged/resolved/escalated | Left rail and My Account for assigned actor. |
| `approval` | Sales Manager | Discount exception requires approval. | open → approved/rejected/expired | Left rail and dashboard approvals count. |
| `sla_risk` | Sales Rep, Manager | High-value opportunity idle for configured threshold. | open → resolved/dismissed/escalated | Dashboard risk card; My Account only for assigned owner. |
| `audit_anomaly` | Revenue Ops, Auditor | Unusual stage change or policy conflict. | open → investigated/resolved | Audit/Trace cross-workstream link where authorized. |

Producer contract example:

```text
producerId: sales-pipeline-attention-producer
sources: OpportunityEntity state changes, StageChangeWorkflow state, policy evaluation events, timers
idempotency: sales-pipeline:<tenantId>:<customerId|none>:<opportunityId>:<category>:<stable-state>
trace: WorkstreamEventEnvelope + DataAccessEvent + AgentWorkTrace when model summary is used
```

## Role-specific dashboard

`surfaceId: sales-pipeline-dashboard`

The dashboard answers:

1. What changed in my pipeline?
2. Which opportunities need follow-up, approval, or investigation?
3. What is blocked, stale, risky, or waiting on a human?
4. Which agents/workflows/tasks are participating?
5. What can I do next within my authority?

Role variants:

- Sales Rep: owned opportunities, follow-up queue, personal risks, allowed update/draft actions.
- Sales Manager: team exceptions, approvals, blocked deals, escalation actions.
- Auditor/Revenue Ops: scoped audit view, anomaly list, no mutation actions unless separately authorized.

## Human surface graph

```text
sales-pipeline-dashboard
├─ opportunity-list
│  └─ opportunity-detail
│     ├─ stage-change-form
│     ├─ discount-decision-card
│     └─ audit-trace-timeline
├─ follow-up-attention-card
│  └─ follow-up-draft-result
├─ pipeline-risk-card
│  └─ investigation-progress/result
└─ system_message surfaces for denial, validation, stale, approval-required, no-op, provider-blocked
```

Every edge is a governed surface request or action:

| Source → target | Action | Capability/governed-tool | Exposure | Result |
|---|---|---|---|---|
| dashboard → opportunity-list | `show_surface` | `sales.pipeline.read` / `pipeline.opportunities.search` | browser-tool/API | table surface or denial `system_message` |
| opportunity-detail → stage-change-form | `open_stage_change` | `sales.pipeline.manage` / `opportunity.stage_change.prepare` | browser-tool/API | form surface |
| stage-change-form → decision-card | `submit_stage_change` | `sales.pipeline.manage` / `opportunity.stage_change.submit` | browser-tool/workflow-tool | success, validation, approval-needed, or workflow status surface |
| risk-card → investigation-progress | `start_investigation` | `sales.pipeline.investigate` / `pipeline.risk.investigate` | browser-tool + internal-tool | AutonomousAgent task progress/result surface |
| decision-card → approve/reject | `approve_discount` / `reject_discount` | `sales.approvals` / `discount_exception.decide` | browser-tool/workflow-tool | updated decision card and audit trace |

## Capability inventory

- `sales.pipeline.read`: dashboard, opportunity search/detail, scoped evidence reads.
- `sales.pipeline.manage`: stage-change preparation/submission, follow-up update, note drafting; side effects are idempotent and audited.
- `sales.approvals`: approval/rejection/defer/escalate decisions for governed exceptions.
- `sales.pipeline.investigate`: start/read/cancel bounded internal investigation tasks.
- `governance-decisions-audit`: cross-cutting decision and audit trace reads.

Capability files own detailed governed-tool contracts: actors, AuthContext, schemas, side effects, idempotency, policy gates, audit/work traces, exposure channels, and tests.

## Workstream expertise bundle

`bundleId: sales-pipeline-agent.expertise`

Required bundle content:

- prompt intent for pipeline explanation, surface requests, denial recovery, and least-privilege recommendations;
- explicit `ModelConfigRef`/`ModelPolicy` or inherited governed default model binding;
- procedural skills such as `sales.follow-up-triage`, `sales.discount-risk-summary`, `sales.pipeline-help`;
- reference documents such as stage policy, discount policy, territory/customer rules, and handoff process;
- compact skill/reference manifests;
- `readSkill(skillId)` and `readReferenceDoc(referenceId)` loader grants;
- `ToolPermissionBoundary` allowing read-only evidence tools by default and requiring human/approval gates for side effects;
- PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, AgentWorkTrace, tool/data access traces;
- tests for assigned/unassigned loads, tool-boundary denial, model-provider fail-closed, no authority expansion from text, and surface rendering.

## Internal workstream agent graph

```text
virtual pipeline dashboard agent
→ risk triage worker
→ discount exception summarizer
→ follow-up draft worker
→ audit anomaly explainer
```

Example worker:

| Worker | Trigger | Substrate | Authority | Output |
|---|---|---|---|---|
| `pipeline-risk-triage-worker` | high-value idle opportunity or manager starts investigation | AutonomousAgent task | read scoped opportunity/evidence; no mutation; may propose follow-up or escalation | progress surface, risk summary result, attention update, trace refs |

Internal workers are not rail workstreams. Their results update the Sales Pipeline dashboard, attention items, decision cards, or Audit/Trace surfaces under governed capabilities.

## Tests and local validation

Minimum tests:

- authorized and denied `/api/me` functional-agent visibility;
- dashboard payload per role/AuthContext;
- attention producer idempotency, lifecycle, My Account/rail counts;
- surface graph traversal and deep-link authorization;
- every action maps to a capability/governed-tool and returns typed result/denial surfaces;
- tenant/customer isolation and redaction;
- workstream expertise loader allow/deny and tool-boundary denial;
- internal worker start/progress/result/failure surfaces;
- audit/work trace correlation for actions, model calls, and denials;
- frontend rendering for loading, empty, ready, submitting, forbidden, stale/reconnect, and error states;
- real local Akka/API/UI path for the stated runtime readiness level.
