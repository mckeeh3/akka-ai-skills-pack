# Surfaces Index

Structured surfaces are the browser/user-visible contract for workstream state. `markdown_response` v1 is the first common surface for the five core starter workstreams; richer typed surfaces are added as core readiness improves.

| Surface id | Type/version | Owning workstream(s) | Contract file | Primary actions / capability links | Required states/tests |
| --- | --- | --- | --- | --- | --- |
| `my-account-dashboard` | dashboard/card-set/v1 | My Account | `surface-contracts/01-my-account-dashboard.md` | read `/api/me`, select context, open profile/settings, open attention item → `secure-tenant-user-foundation` | own-scope, disabled user, context denial, cross-workstream surface request, no duplicate top-rail My Account. |
| `user-admin-dashboard` | dashboard/attention/v1 | User Admin | `surface-contracts/02-user-admin-dashboard.md` | read admin attention, invitations, access review, user queues → `secure-tenant-user-foundation` | tenant isolation, role denial, stale/failed invitations, audit links, left-rail/My Account count effects. |
| `user-admin-user-list` | searchable-table/v1 | User Admin | `surface-contracts/03-user-admin-user-list.md` | search/list users, open user, draft invite → `secure-tenant-user-foundation` | pagination, redaction, forbidden, empty, stale, idempotent invite draft. |
| `user-admin-user-account` | detail/action-panel/v1 | User Admin | `surface-contracts/04-user-admin-user-account.md` | membership/role/profile/support-access actions → `secure-tenant-user-foundation`, `governance-decisions-audit` | last-admin protection, support-access, cross-tenant denial, decision-card handoff. |
| `decision-card` | decision-card/v1 | User Admin, Agent Admin, Governance/Policy | `surface-contracts/05-decision-card.md` | approve/reject/request changes/defer/escalate → `governance-decisions-audit` | reviewer authority, conflict, evidence rendering, audit trace, denial. |
| `audit-trace-explorer` | search/timeline/v1 | Audit/Trace, User Admin, Agent Admin, Governance/Policy | `surface-contracts/06-audit-trace-explorer.md` | search/inspect/export scoped traces → `governance-decisions-audit`, `managed-agent-foundation` | redaction, tenant filtering, export denial, correlation ids, support/auditor visibility. |
| `agent-governance-center` | governance-workspace/v1 | Agent Admin, Governance/Policy | `surface-contracts/07-agent-governance-center.md` | inspect/edit/propose/review agent behavior records and policy changes → `managed-agent-foundation`, `governance-decisions-audit` | draft/active lifecycle, prompt-risk, skill/reference load traces, authority-expansion denial. |
| `markdown_response` | markdown_response/v1 | All five core workstreams | shared contract in `docs/structured-surface-contracts.md` | bounded agent answer, safe denial, blocked provider/system status → owning workstream capability | sanitization, redaction, forbidden/error/empty/stale states, trace links. |
| `system_message` | system_message/v1 | All five core workstreams | shared contract in `docs/structured-surface-contracts.md` | provider blocked, authorization denial, task status, projection refresh → owning workstream capability | safe copy, no secrets, actionable next step, trace link. |

## Domain-specific surfaces

Add domain-specific surfaces here with stable ids and explicit action-to-capability links before implementation. Examples should use the user's actual domain vocabulary, such as `<domain>-request-review`, `<domain>-case-workbench`, or `<domain>-dispatch-board`.
