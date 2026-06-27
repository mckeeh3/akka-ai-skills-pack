# My Account lifecycle

Workstream id: `my-account`
Owning domain: `core-starter`
Current readiness: `compile-ready`
Ready-to-build assessment: 2026-06-26 — see `../ready-to-build-status.md`; normalized to current skills-pack lifecycle term `compile-ready` during My Account review.
Implementation alignment: `partially-aligned`
Source alignment: `realization/source-alignment.md`
Last description change: 2026-06-26 — current skills-pack review added explicit worker bindings and adapter/tool-chain clarification.
Last alignment review: 2026-06-27 — source-alignment split for My Account dashboard, profile/settings, context, notification, digest, chat-plan, trace/audit, and no-access recovery; runtime readiness still not claimed.
Last compile: 2026-06-27 — command-center surface contract id, frontend-safe control-panel schema aliases, and accessible counter rendering aligned to current surface contract.
Last manual runtime test: unknown

## Current alignment posture

This workstream is partially aligned for the command-center/dashboard compile slice only. Existing implementation evidence partially realizes the current intent, and the 2026-06-27 compile reconciled the backend dashboard surface contract id, required control-panel payload aliases, accessible counter rendering, and static frontend contract coverage. The 2026-06-27 source-alignment split records smaller follow-up slices for profile/settings, context authority, notification center, digest/export, `human_chat_tool_plan`, trace/audit, and no-access recovery so later tasks can advance evidence without overstating readiness.

Runtime readiness is not claimed by this lifecycle record. Broader worker bindings, protected API paths, selected `AuthContext` enforcement, tenant/customer denials, notification lifecycle, chat-plan runtime, digest provider-backed success path, durable audit/work trace persistence, frontend surface coverage, and manual API/UI evidence remain to be validated before claiming full alignment.

## Slice status map

| Slice | Current status | Evidence level | Next validation owner |
| --- | --- | --- | --- |
| Dashboard / personal command center | `partially-aligned` | Focused compile/static frontend contract evidence for contract id, payload aliases, and accessible counter rendering. | Backend/API and UI runtime validation. |
| Profile and settings | `pending-validation` | Source-alignment map only. | Protected API/action tests and frontend surface tests. |
| Context authority | `pending-validation` | Source-alignment map only. | Backend-selected `AuthContext`, context switch, and safe-denial tests. |
| Notification center | `pending-validation` | Source-alignment map only. | Notification lifecycle backend/frontend tests. |
| Digest/export | `pending-validation` | Source-alignment map only; provider-backed success depends on concrete provider/runtime configuration. | Fail-closed/runtime classification and digest task tests. |
| `human_chat_tool_plan` | `pending-validation` | Source-alignment map only. | Proposal/confirmation/denial/idempotency/tool-boundary tests. |
| Trace/audit | `pending-validation` | Trace obligations mapped, durable evidence not yet proven. | Durable trace and audit verification. |
| No-access/open-denied recovery | `pending-validation` | Source-alignment map only. | No-membership, hidden-target, stale, and retry-safe denial tests. |

## Blockers and assumptions

- File-level source alignment has been split conservatively by My Account runtime slice.
- `compile-ready` means app-description scope is sufficient for focused build/compile tasks; it does not claim runtime behavior.
- Runtime readiness is not claimed by this lifecycle record.
- Provider-backed digest success is not claimed unless concrete provider/test runtime configuration is exercised; fail-closed behavior must be proven separately.
- Future workstream-specific validation must classify each split mapping as aligned, stale-description-changed, stale-code-changed, partially-aligned, blocked, or intentionally description-only.

## Next recommended action

Run the next focused My Account backend protected API/action validation for dashboard counter opening, profile/settings validation denials, context authority, no-access recovery, selected `AuthContext`, and tenant/customer isolation before claiming additional runtime readiness.
