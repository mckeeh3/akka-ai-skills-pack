# My Account lifecycle

Workstream id: `my-account`
Owning domain: `core-starter`
Current readiness: `compile-ready`
Ready-to-build assessment: 2026-06-26 — see `../ready-to-build-status.md`; normalized to current skills-pack lifecycle term `compile-ready` during My Account review.
Implementation alignment: `partially-aligned`
Source alignment: `realization/source-alignment.md`
Last description change: 2026-06-26 — current skills-pack review added explicit worker bindings and adapter/tool-chain clarification.
Last alignment review: 2026-06-27 — MAFA-02-001 added protected backend/API smoke evidence for dashboard/counter routing, profile/settings, context authority, selected `AuthContext`, safe denials, and no-access recovery; broader runtime readiness still not claimed.
Last compile: 2026-06-27 — command-center surface contract id, frontend-safe control-panel schema aliases, and accessible counter rendering aligned to current surface contract.
Last manual runtime test: unknown

## Current alignment posture

This workstream is partially aligned overall. Existing implementation evidence realizes the command-center/dashboard compile slice, and MAFA-02-001 now records protected backend/API smoke evidence for dashboard/counter routing, profile/settings, context authority, selected `AuthContext`, safe denials, and no-access recovery. The source-alignment split keeps notification center, digest/export, `human_chat_tool_plan`, trace/audit, frontend, and manual/runtime slices separate so later tasks can advance evidence without overstating readiness.

Overall runtime readiness is not claimed by this lifecycle record. Notification lifecycle, chat-plan runtime, digest provider-backed success path, durable audit/work trace persistence, frontend surface coverage, provider configuration, and manual API/UI evidence remain to be validated before claiming full alignment.

## Slice status map

| Slice | Current status | Evidence level | Next validation owner |
| --- | --- | --- | --- |
| Dashboard / personal command center | `backend-api-aligned` | Focused compile/static frontend contract evidence plus MAFA-02-001 protected API smoke for dashboard read, attention-counter open routing, selected `AuthContext`, and safe open-denied behavior. | Frontend/manual runtime and durable trace validation. |
| Profile and settings | `backend-api-aligned` | MAFA-02-001 protected API smoke for reads, saves, no-op/idempotent replay, validation, unsupported/provider-backed field denials, invalid theme/timezone, and safe payloads. | Frontend surface tests and durable trace validation. |
| Context authority | `backend-api-aligned` | MAFA-02-001 protected API smoke for context read, selected tenant/customer context, switch/no-op, hidden context denial, and safe payloads. | Frontend shell refresh and durable trace validation. |
| Notification center | `pending-validation` | Source-alignment map only. | Notification lifecycle backend/frontend tests. |
| Digest/export | `pending-validation` | Source-alignment map only; provider-backed success depends on concrete provider/runtime configuration. | Fail-closed/runtime classification and digest task tests. |
| `human_chat_tool_plan` | `pending-validation` | Source-alignment map only. | Proposal/confirmation/denial/idempotency/tool-boundary tests. |
| Trace/audit | `pending-validation` | Trace obligations mapped, durable evidence not yet proven. | Durable trace and audit verification. |
| No-access/open-denied recovery | `backend-api-aligned` | MAFA-02-001 protected API smoke for open-denied direct/action surfaces, hidden workstream denial, no-active-membership and disabled-account recovery, missing-bearer failure, and tenant/customer redaction. | Frontend recovery rendering, request-access follow-through, and durable denial traces. |

## Blockers and assumptions

- File-level source alignment has been split conservatively by My Account runtime slice.
- `compile-ready` means app-description scope is sufficient for focused build/compile tasks; it does not claim runtime behavior.
- Runtime readiness is not claimed by this lifecycle record.
- Provider-backed digest success is not claimed unless concrete provider/test runtime configuration is exercised; fail-closed behavior must be proven separately.
- Future workstream-specific validation must classify each split mapping as aligned, stale-description-changed, stale-code-changed, partially-aligned, blocked, or intentionally description-only.

## Next recommended action

Run the next focused My Account durable trace/audit verification (MAFA-03-001) before claiming additional runtime readiness.
