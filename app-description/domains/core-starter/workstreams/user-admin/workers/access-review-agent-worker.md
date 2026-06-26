# Worker: Access Review Agent Worker

workerId: `user-admin.access-review-agent-worker`
workerType: `autonomous-agent`
reasoningEngine: `model`
scope: `local-workstream`
owningDomain: `core-starter`
owningWorkstream: `user-admin`
runtimeReadiness: `description-ready`

## Purpose

Bounded model-backed worker that performs durable access-review triage and recommendations for scoped users, memberships, invitations, roles, support access, and admin-risk evidence.

## Responsibility

Owns/does:

- Start/read/cancel access-review tasks only through authorized User Admin task surfaces or internal workflow calls.
- Collect scoped evidence from authorized views/tools, identify stale/dormant/risky access, score risk, and produce advisory recommendations.
- Produce progress, blocker, result, and review-needed outputs for `surface-user-admin-access-review-task` and attention queues.

Does not own/do:

- Directly mutate memberships, roles, invitations, support access, identity links, Organizations, Customers, policies, or agent behavior.
- Bypass selected `AuthContext`, evidence redaction, model/provider readiness, or tool-boundary checks.

## Behavior profile

- Instructions/prompt: governed access-review task prompt/profile assigned to `user-admin-agent` or a bounded specialist AgentDefinition.
- Skills: `ua.access-review-triage.v1`, `ua.admin-risk-scoring.v1`, `ua.role-recommendation.v1`, `ua.support-access-review.v1`, `ua.audit-summary.v1`.
- Tools: `run-access-review`, scoped evidence reads through `search-user-directory`, and governed `readSkill`/`readReferenceDoc` loaders when assigned.
- Evidence profile: scoped membership/invitation/support/audit/access-review evidence only; hidden tenants/customers/users, raw tokens, provider secrets, raw prompts, and denied loader contents forbidden.

## Authority and scope

- authorityLevel: `recommend` and `propose` only.
- AuthContext scope: selected human/admin scope or stored task authority basis captured at start and revalidated on reads/results.
- Requires human review for accepting/rejecting the advisory result; follow-up mutations route through deterministic User Admin surfaces.
- Provider/configuration precondition: no model-less normal success; missing model/provider/profile/tool-boundary/governed docs produces blocked task/system-message and trace.

## Harnesses and actor adapters

| Harness | Actor adapter | Exposure channel | Trace source | Notes |
|---|---|---|---|---|
| Access-review task surface | `surface_action` | browser | `surface_action` | Human starts/cancels/accepts/rejects advisory task. |
| Autonomous/agent runtime | `agent_tool_call` | runtime tool catalog | `agent_tool_call` | Evidence reads and advisory output only. |
| Workflow/task orchestration | `workflow_step` / `internal_call` | backend | `workflow_step` | Durable retries, progress, cancellation, provider-blocked states. |

## Governed tools and capabilities

| Governed tool id | Capability id | Allowed adapter(s) | Authority | Approval/confirmation |
|---|---|---|---|---|
| `run-access-review` | `user_admin.access_review.start/read/cancel/accept_result/reject_result` | `surface_action`, `agent_tool_call`, `workflow_step`, `internal_call` | recommend/propose/review | Human disposition required; no direct mutation. |
| `search-user-directory` | `user_admin.list_members`, `user_admin.read_user_account`, `admin.audit.read` | `agent_tool_call` read only | observe | Scoped and redacted evidence only. |

## Audit and work traces

Record task start/read/cancel/progress/result/accept/reject, prompt/model/tool-boundary decisions, evidence reads, skill/reference loads, blocked provider/model states, denials, and no-direct-mutation guarantee with worker id, task id, selected scope, governed tool, capability, and redaction state.

## Tests and manual runtime scenarios

- Automated tests: start/read/cancel/result flow, missing provider fail-closed, denied evidence, cross-scope evidence denial, advisory result cannot mutate access, accept/reject records review only, and trace completeness.
- Manual runtime scenario: Tenant Admin starts access review -> model-backed worker returns recommendation -> human accepts advisory result -> role/status follow-up opens deterministic role/status surface rather than mutating directly.

## Realization links

- Surfaces: `../surfaces/surfaces.md#user-admin-dashboard-surface` and access-review task section
- Tools: `../tools/governed-tools.md`
- Capability: `../../../capabilities/user-and-access-administration.md`
- Tests: `../tests/coverage.md`
- Traces: `../traces/work-traces.md`
