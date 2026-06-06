# Internal Workstream Agents

Internal agents are backend/supporting workers. They are not left-rail workstreams unless they represent a user-facing responsibility boundary. They support functional-agent workstreams through governed internal workstream agent graphs.

## Foundation internal graph examples

| Owning workstream | Virtual dashboard agent view | Worker candidate | Trigger | Authority boundary | Result surfaces |
|---|---|---|---|---|---|
| User Admin | Identify invitation, access-review, support-access, and risky-role items that need admin attention. | `access-review-triage-worker` | stale access review item, admin starts investigation, timer detects overdue review | Read scoped membership/audit evidence; proposal only; no autonomous role/user mutations. | `decision-card`, `audit-trace-explorer`, user-admin dashboard attention update, `system_message`. |
| User Admin | Identify failed/expired onboarding work. | `invitation-delivery-investigation-worker` | failed/expired invitation event or admin request | Read invitation metadata and delivery status; no raw token exposure; resend/revoke requires human-confirmed capability. | dashboard card, invitation detail/result surface, `system_message`. |
| Agent Admin | Identify governed behavior changes needing review. | `behavior-change-risk-worker` | prompt/skill/reference/tool-boundary proposal | Read active/draft governed records; classify risk; no activation authority. | `agent-governance-center`, `decision-card`, trace links. |
| Audit/Trace | Determine investigation paths from trace/correlation gaps. | `trace-correlation-summarizer` | auditor search, anomaly event, denied access investigation | Read scoped trace evidence with redaction; no export authority. | `audit-trace-explorer`, markdown summary, `system_message`. |
| Governance/Policy | Evaluate policy proposal impact. | `policy-impact-simulation-worker` | policy proposal or approval request | Run bounded simulation/read evidence; proposal only; activation requires approver. | `decision-card`, governance dashboard update, audit/work trace. |

## Required fields for each internal worker

- worker id and owning workstream;
- trigger/event/source surface;
- selected Akka substrate, usually `AutonomousAgent` when durable lifecycle, progress, cancellation, delegation, or result snapshots matter;
- input/output schema;
- capability ids and governed-tool ids;
- service or AuthContext authority basis;
- model config/policy, prompt/skill/reference manifests, and tool boundary when model-backed;
- progress/result/failed/cancelled/stale surfaces;
- attention creation/resolution behavior;
- audit/work trace fields;
- tests for authorization, tenant isolation, tool-boundary denial, failure/cancellation, replay/idempotency, and surface rendering.

Internal workers may recommend, summarize, classify, draft, or propose. They do not grant authority. Every tool call and side effect still maps to governed capabilities, policy, idempotency, and audit.
