# Internal Workstream Agents

Internal agents are backend/supporting workers. They are not left-rail workstreams unless they represent a user-facing responsibility boundary. They support functional-agent workstreams through governed internal workstream agent graphs.

## Foundation internal graph examples

Each row is a compact worker contract. Expand a row into a dedicated section before implementation when the worker becomes runtime scope. Internal workers use actor adapter/source `internal`; each governed-tool invocation still needs idempotency, a transaction boundary, a result/partial-failure surface, and an audit/work trace source.

| workerId | Owning workstream | Virtual dashboard agent view | Trigger/source | Substrate | Capability/governed-tool | Authority boundary | Progress/result/failure surfaces |
|---|---|---|---|---|---|---|---|
| `personal-attention-digest-worker` | `my-account` | Identify cross-workstream attention that can be summarized for the current user. | My Account digest action or scheduled personal digest. | `AutonomousAgent` candidate when model-backed durable lifecycle is needed. | `secure-tenant-user-foundation` / `my_account.personal_attention_digest.*` | Read only authorized personal attention; advisory result only; source attention lifecycle unchanged. | `my-account-dashboard`, digest result card, `system_message`. |
| `access-review-triage-worker` | `user-admin` | Identify invitation, access-review, support-access, and risky-role items that need admin attention. | Stale access review item, admin starts investigation, timer detects overdue review. | `AutonomousAgent` candidate. | `secure-tenant-user-foundation` / `useradmin.access_review.*` | Read scoped membership/audit evidence; proposal only; no autonomous role/user mutations. | `decision-card`, `audit-trace-explorer`, user-admin dashboard attention update, `system_message`. |
| `invitation-delivery-investigation-worker` | `user-admin` | Identify failed/expired onboarding work. | Failed/expired invitation event or admin request. | Workflow + Agent or `AutonomousAgent` depending on duration. | `secure-tenant-user-foundation` / `useradmin.invitation_delivery.investigate` | Read invitation metadata and delivery status; no raw token exposure; resend/revoke requires human-confirmed capability. | dashboard card, invitation detail/result surface, `system_message`. |
| `behavior-change-risk-worker` | `agent-admin` | Identify governed behavior changes needing review. | Prompt/skill/reference/tool-boundary proposal. | `AutonomousAgent` candidate. | `managed-agent-foundation` / `agentadmin.behavior_change.risk_review` | Read active/draft governed records; classify risk; no activation authority. | `agent-governance-center`, `decision-card`, trace links. |
| `trace-correlation-summarizer` | `audit-trace` | Determine investigation paths from trace/correlation gaps. | Auditor search, anomaly event, denied access investigation. | Request-based `Agent` for bounded summary or `AutonomousAgent` for durable investigation. | `governance-decisions-audit` / `audit_trace.correlation.summarize` | Read scoped trace evidence with redaction; no export authority. | `audit-trace-explorer`, markdown summary, `system_message`. |
| `policy-impact-simulation-worker` | `governance-policy` | Evaluate policy proposal impact. | Policy proposal or approval request. | `AutonomousAgent` candidate when simulation is durable/model-backed. | `governance-decisions-audit` / `governance_policy.impact.simulate` | Run bounded simulation/read evidence; proposal only; activation requires approver. | `decision-card`, governance dashboard update, audit/work trace. |

## Required fields for each internal worker

- worker id and owning workstream;
- trigger/event/source surface;
- selected Akka substrate, usually `AutonomousAgent` when durable lifecycle, progress, cancellation, delegation, or result snapshots matter;
- input/output schema;
- capability ids and governed-tool ids;
- actor adapter/source, normally `internal`, plus any originating surface/chat/API/workflow/timer/consumer/MCP trigger context;
- service or AuthContext authority basis;
- idempotency key and transaction boundary for every governed-tool invocation;
- result/partial-failure surface behavior;
- model config/policy, prompt/skill/reference manifests, and tool boundary when model-backed;
- progress/result/failed/cancelled/stale surfaces;
- attention creation/resolution behavior;
- audit/work trace fields, including trace source and requestedBy/confirmedBy when a human request or confirmed chat tool plan initiated the work;
- tests for authorization, tenant isolation, tool-boundary denial, failure/cancellation, replay/idempotency, and surface rendering.

Internal workers may recommend, summarize, classify, draft, or propose. They do not grant authority. Every tool call and side effect still maps to governed capabilities, policy, idempotency, and audit.
