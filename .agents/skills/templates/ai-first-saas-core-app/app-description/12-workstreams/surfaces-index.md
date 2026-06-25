# Surfaces Index

Structured surfaces are the browser/user-visible contract for workstream state. This skills-pack template is a process-oriented legacy-numbered `surface-ready` baseline: it defines the shape, ownership, graph role, compact mappings, and user-visible/internal-metadata boundaries needed to guide app-development cleanup, but it does not claim that the foundation workstream surfaces are fully implemented, `backend-ready`, `manual-ready`, or `runtime-ready`.

Template contracts intentionally include internal ids for generation and traceability. Runtime surfaces must translate those ids into role-appropriate SaaS UX copy and expose raw diagnostic metadata only in authorized support, audit, admin, or developer drilldowns.

## Contracted foundation surface examples

| Surface id | Type/version | Owning workstream | Owner functional agent | Reusable by | Contract file | Primary actions / capability links | Required states/tests |
| --- | --- | --- | --- | --- | --- | --- | --- |
| `my-account-dashboard` | dashboard/card set/v1 | My Account | `my-account-agent` | opens authorized target workstreams/surfaces | `surface-contracts/01-access-profile-dashboard.md` | read `/api/me`, select context, open profile/settings/notifications → `secure-tenant-user-foundation` | own-scope, disabled user, context denial, no duplicate top-rail My Account. |
| `user-admin-dashboard` | dashboard/attention/v1 | User Admin | `user-admin-agent` | My Account attention summaries | `surface-contracts/02-user-admin-dashboard.md` | read admin attention, Organization bootstrap risks, invitations, access review, user queues → `secure-tenant-user-foundation` | tenant isolation, role denial, stale/failed invitations, Organization lifecycle links, audit links, left-rail/My Account count effects. |
| `saas-owner-organization-admin` | directory-detail-workspace/v1 | User Admin | `user-admin-agent` | SaaS Owner Admin Organization/Tenant lifecycle requests | `surface-contracts/08-saas-owner-organization-admin.md` | list/create/open/update/status Organization, bootstrap Organization Admin → `secure-tenant-user-foundation`; audit/status decisions → `governance-decisions-audit` | SaaS Owner-only mutation, platform-safe metadata, no organization app-data reads, bootstrap invite idempotency, status approval gates. |
| `user-admin-user-list` | searchable-table/v1 | User Admin | `user-admin-agent` | dashboard queues and prompt/deep-link requests | `surface-contracts/03-user-admin-user-list.md` | search/list users, open user, draft invite → `secure-tenant-user-foundation` | pagination, redaction, forbidden, empty, stale, idempotent invite draft. |
| `user-admin-user-account` | detail/action-panel/v1 | User Admin | `user-admin-agent` | list rows, attention items, audit drill-ins | `surface-contracts/04-user-admin-user-account.md` | membership/role/profile/support-access actions → `secure-tenant-user-foundation`, `governance-decisions-audit` | last-admin protection, support-access, cross-tenant denial, decision-card handoff. |
| `decision-card` | decision-card/v1 | Governance/Policy | `governance-policy-agent` | User Admin, Agent Admin, Audit/Trace, domain-specific reviewer workstreams | `surface-contracts/05-decision-card.md` | approve/reject/request changes/defer/escalate → `governance-decisions-audit` | reviewer authority, conflict, evidence rendering, audit trace, denial. |
| `audit-trace-explorer` | search/timeline/v1 | Audit/Trace | `audit-trace-agent` | User Admin, Agent Admin, Governance/Policy, My Account, domain-specific workstreams | `surface-contracts/06-audit-trace-explorer.md` | search/inspect/export scoped traces → `governance-decisions-audit`, `managed-agent-foundation` | redaction, tenant filtering, export denial, correlation ids, support/auditor visibility. |
| `agent-governance-center` | governance-workspace/v1 | Agent Admin | `agent-admin-agent` | Governance/Policy | `surface-contracts/07-agent-governance-center.md` | inspect/edit/propose/review agent behavior records and policy changes → `managed-agent-foundation`, `governance-decisions-audit` | draft/active lifecycle, prompt-risk, skill/reference load traces, authority-expansion denial. |

## Shared base surfaces

| Surface id | Type/version | Owning workstream | Contract file | Primary actions / capability links | Required states/tests |
| --- | --- | --- | --- | --- | --- |
| `markdown_response` | markdown_response/v1 | producing functional agent | shared contract in `docs/structured-surface-contracts.md` | bounded agent answer, safe denial, blocked provider/system status → owning workstream capabilities | sanitization, redaction, forbidden/error/empty states, trace links; no duplicate generic response/detail surface for the same markdown content. |
| `system_message` | system_message/v1 | producing functional agent | shared contract in `docs/structured-surface-contracts.md` | provider blocked, authorization denial, task status, bounded SSE replay/projection refresh → owning workstream capabilities | safe copy, no secrets, actionable next step, trace link; no duplicate activity-detail surface for the same status. |

## Deferred typed surfaces

Deferred typed result surfaces are listed in `deferred-typed-surfaces.md`. Template examples may reference them with a `markdown_response` or `system_message` first-slice fallback, but app-level implementation cleanup must replace consequential fallbacks with full contracts before claiming `backend-ready`, `manual-ready`, or `runtime-ready` for the relevant workstream/action scope. A fallback replaces the richer result surface for that slice; it must not be rendered in addition to a generic internal activity/detail surface for the same event.

## Surface graph

The explicit process-level graph is maintained in `surface-graph.md`. Keep graph nodes/edges aligned with this index, individual surface contracts, `70-traceability/surface-to-capability-map.md`, and future app-level capability mappings. When using this legacy numbered template in new current-intent work, map reusable surface definitions into `app-description/global/surfaces/**` and workstream-specific bindings into `app-description/domains/<domain>/workstreams/<workstream>/surfaces/**`.

## Domain-specific surfaces

Add domain-specific surfaces here with stable ids, a single owner functional agent, explicit reusable-by workstreams, graph edges in `surface-graph.md`, and action-to-capability/governed-tool links before implementation. Examples should use the user's actual domain vocabulary, such as `<domain>-request-review`, `<domain>-case-workbench`, or `<domain>-dispatch-board`.
