# Global Workers: Foundation shared worker contracts

These shared worker contracts keep the core starter graph aligned to the current worker -> execution harness -> actor adapter -> governed tool -> capability chain. They define reusable worker classes only; each workstream later binds exact surfaces, tools, capability grants, traces, tests, and realization paths under its own directory.

## Shared actor adapter vocabulary

| Adapter | Caller / harness | Shared semantics |
| --- | --- | --- |
| `surface_action` | Authenticated human using the workstream shell, structured surfaces, forms, buttons, links, deep links, or dashboard cards. | Backend reauthorizes selected `AuthContext`, role/capability, tenant/customer scope, policy, idempotency, and stale/version state. Results return an updated surface, result surface, workflow/status surface, decision/approval surface, no-op, denial, or safe `system-message`. |
| `human_chat_tool_plan` | Authenticated human asking the selected workstream assistant to prepare a governed action. | The functional agent may draft a plan only. Execution requires explicit human confirmation bound to the plan, deterministic backend authorization, idempotency/transaction handling, `confirmedBy`, and partial-failure/result surfaces. |
| `agent_tool_call` | AI-backed functional/internal/autonomous/evaluator worker through the governed agent runtime. | Requires active managed-agent behavior profile, allowed generated tool list, `ToolPermissionBoundary`, model/provider readiness, selected `AuthContext` or service authority, safe DTOs, trace source `agent_tool_call`, and fail-closed denial when any prerequisite is missing. Human surface visibility never grants this adapter. |
| `api_call` | Protected browser API, service API, onboarding endpoint, or integration API. | Caller identity or service identity is validated, selected context is resolved server-side, errors are scoped/redacted, and audit/work traces use `api_call` source. |
| `workflow_step` | Durable workflow/process step. | Persisted step state, retry/compensation behavior, approval waits, provenance/correlation, idempotency, and trace source `workflow_step` are explicit. |
| `timer_invocation` | Timer, scheduled check, expiry, retention, reminder, or replay. | Stored authority basis, schedule provenance, idempotent retry/no-op behavior, and trace source `timer_invocation` are explicit. |
| `consumer_reaction` | Event/topic/stream consumer or projection reaction. | Event provenance, duplicate/retry handling, correlation propagation, allowed side effects, and trace source `consumer_reaction` are explicit. |
| `internal_call` | Backend service/component path not directly exposed to users or models. | Caller boundary, invariant checks, tenant/customer scope, idempotency where consequential, audit when required, and trace source `internal_call` are explicit. |
| `mcp_tool_call` | Remote model/client MCP boundary if later accepted. | Service ACL/JWT, allowed-tool filtering, tenant/context scoping, redaction, remote audit, and trace source `mcp_tool_call` are required before exposure. No current core-starter MCP exposure is implemented by this shared refresh. |

## Shared worker contracts

| Worker id | Type / engine | Primary harnesses | Allowed adapter classes | Authority and failure behavior |
| --- | --- | --- | --- | --- |
| `authenticated-member-human` | `human` / human judgment | Workstream shell, structured surfaces, forms, result surfaces, optional confirmed chat-plan UX. | `surface_action`; `human_chat_tool_plan` only when a workstream declares the plan and confirmation contract. | Authority comes from backend-selected `AuthContext`, membership, roles/capabilities, policy, and governed tool checks. Disabled/no-membership/no-selected-context states fail closed with safe recovery surfaces. |
| `foundation-admin-human` | `human` / human judgment | Admin workstream dashboards, decision cards, confirmations, audit/evidence surfaces. | `surface_action`; confirmed `human_chat_tool_plan` for declared low/medium-risk plans; approval/decision surfaces for high-impact actions. | May administer only within selected SaaS Owner, Organization/Tenant, Customer, support-access, or policy scope. Cannot expand authority by frontend visibility, prompt wording, or hidden fields. Last-admin, support-access, policy, provider/outbox, and role-escalation denials are traced. |
| `foundation-functional-agent-worker` | `functional-agent` / model through governed Akka Agent runtime | Selected workstream assistant, managed behavior profile, prompt/skill/reference manifests, generated tool boundary, markdown/result surfaces. | `agent_tool_call` only for explicitly allowed tool ids; may prepare `human_chat_tool_plan` proposals where local binding allows. | May explain, draft, recommend, or prepare bounded plans. It must not autonomously expand authority, bypass approvals, read hidden data, or mutate side-effecting tools unless a local tool binding explicitly grants that adapter. Missing provider/model/security/tool-boundary configuration fails closed. |
| `foundation-deterministic-system-worker` | `system` / deterministic logic | Workflows, timers, consumers, endpoints, projections/views, outbox adapters, retention jobs. | `workflow_step`, `timer_invocation`, `consumer_reaction`, `api_call`, `internal_call`. | Performs only capability-owned deterministic work with stored authority/provenance, idempotency, retries/compensation, safe no-ops, and audit/work traces. It cannot use hidden shortcuts to bypass backend authorization or tenant/customer scope. |
| `foundation-provider-boundary-worker` | `system` / external service boundary | WorkOS/AuthKit, Resend/captured outbox, model provider integrations. | `api_call`, `consumer_reaction`, `internal_call` as declared by local capability. | Server-side secrets remain server-only. Unconfigured, denied, failed, or ambiguous provider state returns actionable fail-closed surfaces/results and trace evidence; normal runtime must not report fake provider/model success. |
| `foundation-onboarding-worker` | `system` / deterministic onboarding flow | Invitation acceptance endpoint, WorkOS/AuthKit identity resolution, captured-provider/local setup when explicitly documented. | `api_call`, `internal_call`, provider callback `consumer_reaction` where applicable. | Validates signed invitation tokens, provider identity, target scope, role, status, expiry, idempotency, and account/membership linkage. It cannot accept browser-supplied tenant/customer/role as authority and cannot create public self-registration authority. |
| `foundation-audit-projection-worker` | `system` / deterministic projection and retention | Audit/work trace writers, projections/views, search/detail readers, retention timer. | `consumer_reaction`, `internal_call`, `timer_invocation`, protected `api_call`/`surface_action` reads through capability tools. | Records immutable evidence, redacts per scope, enforces retention, and exposes only backend-authorized trace views. It must preserve denial evidence without leaking hidden object existence or provider secrets. |

## Evidence, supervision, and tests expected in local bindings

Each workstream worker binding should name:

- the exact shared worker id or local worker;
- owning workstream and non-attention reason or attention category;
- harnesses and actor adapters used;
- governed tool ids and capability ids;
- selected `AuthContext`, tenant/customer scope, role/capability grants, and denial behavior;
- confirmation, approval, idempotency, transaction, no-op, and partial-failure semantics;
- result/system-message surfaces and attention/projection side effects;
- audit/work trace names using the source vocabulary above, including `requestedBy` for AI-mediated human requests and `confirmedBy` plus confirmation id for confirmed chat-plan execution;
- tests and runtime-validation references that exercise worker + adapter + governed tool + capability + result/trace behavior.

## Source-alignment convention for this refresh

This shared refresh is description-only. Per-workstream refresh tasks must update each affected workstream lifecycle and `realization/source-alignment.md` with `implementationAlignment: stale-description-changed` by default unless they record an explicit no-code-impact alignment review. No worker, adapter, governed tool, API, frontend, realtime, or Akka runtime exposure is implemented by this file.
