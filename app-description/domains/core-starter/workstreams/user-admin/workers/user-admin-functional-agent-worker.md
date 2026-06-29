# Worker: User Admin Functional Agent Worker

workerId: `user-admin.functional-agent-worker`
workerType: `functional-agent`
reasoningEngine: `model`
scope: `workstream-binding`
owningDomain: `core-starter`
owningWorkstream: `user-admin`
runtimeReadiness: `description-ready`

## Purpose

User-facing workstream assistant backed by `user-admin-agent`. It helps authorized admins understand access state, draft safe inputs, recommend next steps, prepare decision cards, and propose catalog-bound human-confirmed plans without becoming an autonomous administrator.

## Responsibility

Owns/does:

- Explain scoped dashboard/list/detail/audit evidence and safe denials.
- Draft invitation rationale, resend/revoke explanations, role recommendations, support-access summaries, access-review findings, and lifecycle reason text.
- Prepare no-mutation `human_chat_tool_plan` proposals and decision-card facts for catalog-bound or approval-gated work.
- Use assigned User Admin skills/references only through governed loader tools and tool-boundary checks.

Does not own/do:

- Autonomously mutate access, send invitations, grant roles, create/rename/suspend/archive/reactivate Organizations or Customers, grant/revoke support access, resolve identity exceptions, or expand authority.
- Treat prompt text, skill text, rail visibility, or frontend state as authorization.

## Behavior profile

- Instructions/prompt: `../agents/functional-agent.md` and global `user-admin-agent` definition.
- Skills: `ua.access-review-triage.v1`, `ua.admin-risk-scoring.v1`, `ua.invitation-drafting.v1`, `ua.role-recommendation.v1`, `ua.support-access-review.v1`, `ua.audit-summary.v1`.
- References: User Admin role catalog, invitation policy, access-review policy, support-access procedure, last-admin protection, admin-audit redaction guide.
- Tools: scoped read/proposal tools, `readSkill`, `readReferenceDoc`, and explicitly allowed `agent_tool_call`/proposal adapters only.
- Evidence profile: scoped/redacted User Admin evidence visible to the selected human/admin context; hidden target evidence, raw tokens, provider secrets, raw prompts/model payloads, and cross-scope data forbidden.

## Authority and scope

- authorityLevel: `recommend`, `draft`, and `propose` by default.
- AuthContext scope: inherited selected human context or backend-approved service context for advisory tasks, always revalidated by tool boundary.
- Retained human authority: humans approve/confirm every consequential mutation; backend authorization remains authoritative.
- Provider/configuration precondition: missing model/provider/profile/tool-boundary/governed-doc configuration returns model-fail-closed `surface-user-admin-system-message` and trace, not model-less success.

## Harnesses and actor adapters

| Harness | Actor adapter | Exposure channel | Trace source | Notes |
|---|---|---|---|---|
| Akka Agent runtime | `agent_tool_call` | runtime tool catalog | `agent_tool_call` | Only read/proposal tools in active `ToolPermissionBoundary`. |
| Workstream composer | `human_chat_tool_plan` | browser/chat | `human_chat_tool_plan` | Agent proposes; backend confirms/executes after human exact confirmation. |
| Governed document loaders | `agent_tool_call` | runtime loader tool | `skill-load-trace` / `reference-load-trace` | `readSkill`/`readReferenceDoc` only when assigned and redaction passes. |

## Governed tools and capabilities

| Governed tool id | Capability id | Allowed adapter(s) | Authority | Approval/confirmation |
|---|---|---|---|---|
| `search-user-directory` | `user_admin.*`, `admin.audit.read` | `agent_tool_call` read only | observe/recommend | No mutation. |
| `run-access-review` | `user_admin.access_review.*` | `agent_tool_call`, `human_chat_tool_plan` proposal | recommend/propose | Advisory result only; human disposition required. |
| `admin.audit.read` | `admin.audit.read` | read-only bounded `agent_tool_call`, `api_call`/surface handoff | observe/explain | Browser-safe summaries only; Audit/Trace detail reauthorizes and redacts. |
| `manage-organizations`, `manage-organization-admins`, `manage-customers`, `manage-customer-admins`, `create-or-resend-invitation`, `change-membership-role-or-status`, `grant-or-revoke-support-access` | `user-and-access-administration` families | `human_chat_tool_plan` proposal/preparation only unless separately allowed | draft/propose | Human confirmation and backend capability path required; no autonomous agent execution. |
| `readSkill`, `readReferenceDoc` | governed document loading | `agent_tool_call` | observe | Active assignment, version/status, token/redaction, and boundary checks. |

## Audit and work traces

Record agent work traces for prompt assembly, model config, skill/reference loads, tool-boundary decisions, evidence reads, plan proposals, denials, provider/model blocked states, decision-card preparation, and user-visible guidance. Include worker id, functional-agent id `user-admin-agent`, selected scope, requestedBy, governed tool/capability, and redaction state.

## Tests and manual runtime scenarios

- Automated tests: deterministic model-backed role recommendation, invitation drafting, risk scoring, access-review summary, denied tool/loader access, missing provider fail-closed, no autonomous mutation, chat-plan proposal/confirmation split, and trace fields.
- Manual runtime scenario: admin asks the assistant to explain a risky role change -> assistant summarizes evidence and opens role preview/decision card -> no mutation occurs until human confirmation and backend authorization.

## Realization links

- Agent binding: `../agents/functional-agent.md`
- Surfaces: `../surfaces/surfaces.md`
- Tools: `../tools/governed-tools.md`
- Capability: `../../../capabilities/user-and-access-administration.md`
- Tests: `../tests/coverage.md`
- Traces: `../traces/work-traces.md`
