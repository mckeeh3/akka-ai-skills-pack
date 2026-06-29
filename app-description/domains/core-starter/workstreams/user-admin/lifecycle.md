# User Admin lifecycle

Workstream id: `user-admin`
Owning domain: `core-starter`
Current readiness: `ready-to-build`
Ready-to-build assessment: 2026-06-26 — see `../ready-to-build-status.md`
Implementation alignment: `partially-aligned`
Source alignment: `realization/source-alignment.md`
Last description change: 2026-06-29 — TASK-ADR-02-002 refreshed the User Admin worker/adapter/tool/capability/surface/test/trace graph.
Last alignment review: 2026-06-29 — TASK-ADIA-02-002 mapped the refreshed User Admin graph to existing source/test/frontend evidence and the runtime-validation scaffold. The posture is source-evidence mapped and `partially-aligned`; no local runtime-ready, manual-ready, live provider, or WorkOS/AuthKit success claim is made.
Last compile: unknown
Last manual runtime test: unknown

## Current alignment posture

This workstream has been reconciled at source-evidence level after the current description refresh. Existing backend, frontend, and test evidence maps to meaningful slices of the User Admin graph: dashboard and scoped directory, SaaS Owner Organization/Admin branches, Tenant Customer/Customer Admin branches, invitation delivery/onboarding, membership role/status/support/identity flows, access-review advisory work, `human_chat_tool_plan`, frontend surfaces, and admin-audit/work traces.

Overall runtime readiness is still not claimed by this lifecycle record. TASK-ADIA-02-002 did not run Maven/npm tests, start Akka, authenticate through WorkOS/AuthKit, configure Resend/OpenAI, or execute browser/runtime-validation scenarios. The implementation alignment is therefore `partially-aligned` rather than stale, manual-ready, or runtime-ready.

## Slice status map

| Slice | Current status | Evidence level | Next validation owner |
| --- | --- | --- | --- |
| Dashboard / scoped user directory / admin read model | `source-evidence-mapped` | Protected endpoint/service, workstream dispatcher, frontend surface, endpoint integration, and vertical contract evidence exist. | Runtime-validation/API/UI smoke for dashboard/list/detail, no-enumeration denials, pagination/redaction, and trace refs. |
| SaaS Owner Organization and Organization Admin branch | `source-evidence-mapped` | Service/API/workstream/frontend/test evidence exists for Organization lifecycle and Organization Admin invite/manage. | Runtime-smoke SaaS Owner selected-context Organization lifecycle and Organization Admin invitation/denial flows. |
| Tenant Customer and Customer Admin branch | `source-evidence-mapped` | Service/API/workstream/frontend/test evidence exists for Customer lifecycle and Customer Admin invite/manage. | Runtime-smoke Tenant Admin Customer lifecycle, Customer Admin invitation, sibling-customer denial, terminal archive/no-reactivate, and branch rendering. |
| Invitation delivery, resend/revoke, and invitee onboarding | `source-evidence-mapped-provider-auth-blocked` | Invitation/email/identity services, acceptance endpoint, Resend boundary tests, admin endpoint tests, frontend surfaces, and `RV-USER-ADMIN-001` scaffold exist. | Execute authored runtime-validation with local auth setup; run configured Resend smoke or preserve fail-closed provider blocker. |
| Membership status, roles, support access, and identity exceptions | `source-evidence-mapped` | User Admin service/API, identity foundation, role/support/identity surfaces, and backend/frontend tests map to current intent. | Add/run runtime-validation for last-admin, approval, idempotency, disabled actor, support-access, identity, and trace slices. |
| Access-review agent | `source-evidence-mapped-provider-blocked` | Access-review service/worker/autonomous agent/repository/evidence tool and tests map advisory no-direct-mutation evidence. | Provider-configured access-review runtime-validation or explicit fail-closed provider/model/tool-boundary surface evidence. |
| `human_chat_tool_plan` and frontend surfaces | `source-evidence-mapped-provider-blocked` | Workstream dispatcher and frontend contract evidence map exact plan proposal/confirmation, catalog ids, result surfaces, and secret boundaries. | Runtime-smoke exact confirmation, stale/cross-context denial, partial failure, idempotent replay, no pre-confirm mutation, and no fake provider/model success. |
| Admin audit, traces, denials, and secret boundaries | `source-evidence-mapped` | Audit/workstream trace services, admin/invitation/access-review tests, trace UI components, and frontend contracts map browser-safe evidence obligations. | Runtime-validation capture for success, duplicate/no-op, denial, provider/model/outbox blocked, and Audit/Trace reauthorization evidence. |

## Blockers and assumptions

- File-level source alignment has been split by User Admin slice and TASK-ADIA-02-002 rechecked the refreshed current-intent graph against mapped source/test/frontend evidence.
- Runtime-ready/manual-ready is not claimed because protected local Akka/API/UI runtime execution, WorkOS/AuthKit invitation acceptance, browser validation, runtime-validation run records, and provider-backed success were not exercised.
- `RV-USER-ADMIN-001` is authored for invitation create/idempotency/provider-state/non-admin denial, but it has no run record. Role/status/support/identity/access-review/chat-plan runtime-validation scenarios still need authoring or execution follow-up.
- Resend delivery and model-backed access-review/User Admin guidance are provider-config dependent. Missing provider/model/outbox configuration must fail closed and cannot be counted as normal success.
- WorkOS/AuthKit invitee acceptance and selected-context refresh are auth-setup dependent until real local test users or an approved local equivalent exercise the protected acceptance path.
- Future workstream-specific validation must classify any new app-description or implementation changes as aligned, stale-description-changed, stale-code-changed, partially-aligned, blocked, or intentionally description-only.

## Next recommended action

Consolidate and schedule User Admin follow-up tasks for runtime-validation execution, WorkOS/AuthKit invitation acceptance setup, configured Resend/model provider smokes, and role/status/support/identity/access-review/chat-plan scenario coverage before claiming manual-ready or runtime-ready.
