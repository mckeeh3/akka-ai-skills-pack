# Worker: Admin Audit Projection System Worker

workerId: `user-admin.admin-audit-projection-system-worker`
workerType: `system`
reasoningEngine: `deterministic`
scope: `local-workstream`
owningDomain: `core-starter`
owningWorkstream: `user-admin`
runtimeReadiness: `description-ready`

## Purpose

Deterministic worker that projects User Admin state, attention, audit excerpts, trace links, and redacted read models for dashboard, lists, detail surfaces, access-review inputs, and Audit/Trace handoffs.

## Responsibility

Owns/does:

- Maintain scoped User Admin dashboard/list/detail projections from account, membership, invitation, Organization, Customer, support-access, identity-exception, access-review, and audit events.
- Shape browser-safe attention counts, row/card routing metadata, stale/conflict states, redaction summaries, and trace references.
- Publish/maintain admin audit evidence and workstream trace links for protected reads, commands, denials, no-ops, provider/model/outbox blockers, and decision cards.

Does not own/do:

- Authorize by projection visibility, expose hidden targets/counts, perform access mutations, create model-backed recommendations, or store raw secrets/tokens/provider payloads in browser payloads.

## Behavior profile

- Instructions: deterministic projection/redaction policy in User Admin surfaces, traces, and tests.
- Tools: `search-user-directory`, `admin.audit.read`, projection consumers/views, attention aggregation, workstream log trace emission.
- Evidence profile: safe projection rows, counts, trace refs, correlation refs, and audit excerpts; hidden/cross-scope facts, raw tokens, provider secrets, raw model payloads, and unredacted trace detail forbidden.

## Authority and scope

- authorityLevel: `observe` and `internal_system`.
- AuthContext scope: read-time reauthorization by caller selected context; projection state is not authority.
- Failure behavior: stale, partial-data, projection-lag, hidden/not-found, and forbidden outcomes return typed system-message or stale/reconnect states with trace refs.

## Harnesses and actor adapters

| Harness | Actor adapter | Exposure channel | Trace source | Notes |
|---|---|---|---|---|
| Projection/view updates | `consumer_reaction` | backend | `consumer_reaction` | Idempotent event handling and safe redaction. |
| Protected dashboard/list/detail APIs | `api_call` | browser API | `api_call` | Reauthorize and redact every read. |
| Internal attention aggregation | `internal_call` | backend | `internal_call` | Feeds User Admin and My Account attention only when visible. |
| Audit/Trace handoff | `internal_call` / `api_call` | backend/browser | `data-access-trace` | Trace detail opening reauthorizes in Audit/Trace. |

## Governed tools and capabilities

| Governed tool id | Capability id | Allowed adapter(s) | Authority | Approval/confirmation |
|---|---|---|---|---|
| `search-user-directory` | `user_admin.view_overview`, `user_admin.list_members`, `user_admin.read_user_account`, `user_admin.acceptance_status.read`, `user_admin.support_access.read`, `user_admin.access_review.read` | `api_call`, `consumer_reaction`, `internal_call`, read-only `agent_tool_call` where allowed | observe | No mutation; scope/redaction enforced at read time. |
| `admin.audit.read` | `admin.audit.read` | `api_call`, `internal_call` | observe | Audit detail requires reauthorization through Audit/Trace. |

## Audit and work traces

Record projection updates, skipped/stale/out-of-order events, read authorization decisions, hidden/not-found redactions, dashboard/list/detail opens, attention aggregation, audit handoffs, and data-access traces with worker id, event/source ids where safe, selected scope, capability/tool, and redaction state.

## Tests and manual runtime scenarios

- Automated tests: scoped projections, cross-tenant/customer redaction, hidden count omission, row routing metadata, stale/reconnect state, projection idempotency, trace refs, and audit reauthorization.
- Manual runtime scenario: admin changes invitation status -> dashboard/list/detail projections update -> trace link opens only after Audit/Trace reauthorization.

## Realization links

- Surfaces: `../surfaces/surfaces.md`
- Tools: `../tools/governed-tools.md`
- Capability: `../../../capabilities/user-and-access-administration.md`
- Tests: `../tests/coverage.md`
- Traces: `../traces/work-traces.md`
