# My Account lifecycle

Workstream id: `my-account`
Owning domain: `core-starter`
Current readiness: `description-ready`
Ready-to-build assessment: 2026-06-29 — TASK-ADR-02-001 refreshed the My Account current-intent graph for worker/adapter/governed-tool/capability/realization/test/runtime-validation/trace coverage.
Automated alignment readiness: `historical-automated-aligned` for non-manual My Account slices verified by MAFA-08-001; manual browser/API acceptance and concrete provider-backed digest success remain separate residual checks.
Implementation alignment: `partially-aligned`
Source alignment: `realization/source-alignment.md`
Last description change: 2026-06-29 — TASK-ADR-02-001 clarified the My Account functional-agent workstream graph, account/profile/context surfaces, governed tool adapter boundaries, AuthContext scope, runtime-validation references, and trace obligations.
Last alignment review: 2026-06-29 — TASK-ADIA-02-001 rechecked the refreshed My Account graph against the source evidence inventory, mapped implementation/test/frontend files, and runtime-validation scaffold. The posture is now source-evidence mapped and `partially-aligned`; no local runtime-ready or provider-backed success claim is made.
Last compile: 2026-06-27 — command-center surface contract id, frontend-safe control-panel schema aliases, and accessible counter rendering aligned to the prior surface contract.
Last manual runtime test: 2026-06-30 — `RV-MY-ACCOUNT-001` was retried with the corrected start/seed tooling; local Akka/frontend startup and `base-organization` seeding succeeded, but authenticated API/UI execution remained blocked by missing WorkOS/AuthKit test-user session or valid bearer-token handoff; see `specs/runtime-validation/runs/2026-06-30-RV-MY-ACCOUNT-001-blocked-auth-login.md`.

## Current alignment posture

This workstream is description-ready after TASK-ADR-02-001. The refreshed description explicitly links signed-in member and My Account functional-agent workers to `surface_action`, `api_call`, bounded `human_chat_tool_plan`, and described `agent_tool_call` adapters; governed account/profile/context tools; capability `account-context-and-profile`; realization mappings; test/runtime-validation references; and durable trace expectations. TASK-ADIA-02-001 reconciled that refreshed graph to existing source/test/frontend evidence and the runtime-validation scaffold, so the implementation alignment posture is `partially-aligned` rather than stale. This is a source-evidence posture only: prior MAFA automated evidence remains historical, and this review did not exercise the local protected Akka/API/UI runtime path.

Overall runtime readiness is still not claimed by this lifecycle record. Human manual browser/API acceptance, real WorkOS/AuthKit production login smoke, concrete provider-backed digest success, production export/vendor delivery, passed runtime-validation run records, and broader stale/reconnect manual review remain residual checks outside this evidence-alignment task. TASK-ADIA-FU-001 has blocked runtime-validation run records for `RV-MY-ACCOUNT-001`; the local start/seed contracts and local-only seed endpoint now start and seed successfully, but the latest retry remained blocked at WorkOS/AuthKit login or valid bearer-token handoff. The scenario still has not exercised authenticated `/api/me`, account context, My Account surface reads, disabled/inactive denial/open-disabled behavior, browser-safe authenticated payload capture, or account-context/denial trace capture.

## Slice status map

| Slice | Current status | Evidence level | Next validation owner |
| --- | --- | --- | --- |
| Dashboard / personal command center | `automated-aligned` | MAFA-02/07/08 protected API smoke, frontend contract/build evidence, selected `AuthContext`, safe open-denied behavior, counter-first rendering, and terminal aggregate checks. | Manual browser runtime and stale/reconnect manual review only. |
| Profile and settings | `automated-aligned` | MAFA-02/03/05/07/08 protected API smoke for reads/saves/no-op/idempotency/validation/denial, durable trace evidence, chat-plan confirmation execution, frontend editable-field checks, and terminal aggregate checks. | Manual browser runtime and conflict/stale visual review only. |
| Context authority | `automated-aligned` | MAFA-02/07/08 selected tenant/customer context, authorized switch/no-op, hidden context denial, safe no-enumeration frontend recovery, and terminal aggregate checks. | Manual shell refresh review only. |
| Notification center | `automated-aligned` | MAFA-04/07/08 backend lifecycle/preferences/source-open tests, frontend triage/lifecycle rendering, secret-boundary checks, and terminal aggregate checks. | Manual browser/responsive review only. |
| Digest/export | `automated-aligned-provider-success-config-blocked` | MAFA-06/07/08 lifecycle, ownership, fail-closed provider/runtime/tool-boundary behavior, no-fake-success semantics, advisory review, autonomous-agent typed result/failure mapping, frontend digest surfaces, and terminal aggregate checks. | Concrete provider-backed happy path and production export/vendor delivery remain provider-config/manual residuals. |
| `human_chat_tool_plan` | `automated-aligned-provider-success-config-blocked` | MAFA-05/08 proposal, no pre-confirm mutation, exact confirmation, denial/system-message, idempotency/replay, partial failure, tool-boundary, durable trace, frontend confirmation contract, and terminal aggregate checks. | Provider/model-backed happy path beyond test-double/fail-closed and manual browser smoke only. |
| Trace/audit | `automated-aligned` | MAFA-03/08 durable admin-audit/workstream-log facts for My Account surface actions, digest fail-closed start, notification lifecycle/preference, and chat-plan proposal/confirmation/step execution. | Manual trace-link browser review only. |
| No-access/open-denied recovery | `automated-aligned` | MAFA-02/07/08 protected open-denied recovery, no-active-membership/disabled-account recovery, missing-bearer failure, tenant/customer redaction, no-enumeration frontend copy, and terminal aggregate checks. | Request-access follow-through and manual browser runtime only. |

## Blockers and assumptions

- File-level source alignment has been split by My Account runtime slice and TASK-ADIA-02-001 rechecked the refreshed current-intent graph against mapped source/test/frontend evidence.
- `description-ready` is the refreshed app-description lifecycle term for TASK-ADR-02-001; historical automated-aligned entries record prior implementation/test evidence for the non-manual mini-project scope, not full manual/runtime acceptance.
- Runtime-ready/manual-ready is not claimed by this lifecycle record because manual browser/API acceptance and concrete provider-backed digest success were not exercised.
- `RV-MY-ACCOUNT-001` is ready to rerun in a browser-capable/human authenticated session: `tools/runtime-validation/start-local.sh --empty` and `tools/runtime-validation/seed.sh base-organization` now provide the preferred start/seed contracts and member/disabled/inactive fixtures. Valid member login and `/api/me` evidence still require working local WorkOS/AuthKit issuer, public client id, redirect URI, test-user mapping, and either interactive login credentials or a valid bearer-token handoff; `WORKOS_JWT_AUDIENCE` is no longer treated as a startup prerequisite.
- Provider-backed digest success is not claimed unless concrete provider/test runtime configuration is exercised; the current automated evidence proves fail-closed/no-fake-success behavior.
- Future workstream-specific validation must classify any new app-description or implementation changes as aligned, stale-description-changed, stale-code-changed, partially-aligned, blocked, or intentionally description-only.

## Next recommended action

Run separate manual/browser/API acceptance and provider-configured digest verification if runtime-ready/manual-ready evidence is required beyond this automated mini-project.
