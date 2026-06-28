# Backlog 01: My Account Automated Alignment

## Current-intent provenance

- `app-description/domains/core-starter/workstreams/my-account/**`
- `app-description/domains/core-starter/capabilities/account-context-and-profile.md`
- `app-description/global/tools/foundation-governed-tools.md`
- `app-description/global/traces/foundation-trace-patterns.md`

## Backlog items

### B01: Source-alignment split and lifecycle map

Split the broad My Account source-alignment row into smaller entries for dashboard, profile/settings, context, notification center, digest, chat-plan, trace/audit, and no-access recovery. Record automated evidence and remaining validation gaps.

Suggested task: `MAFA-01-001`.

### B02: Backend protected API/action runtime tests

Add or repair backend tests for protected My Account runtime paths: dashboard default/open, counter routing, profile/settings save/no-op/validation/unsupported-field denial, context authority and safe context denial, no-access recovery, selected `AuthContext`, tenant/customer boundaries, and safe result surfaces.

Suggested task: `MAFA-02-001`.

### B03: Durable trace/audit evidence

Verify or repair durable audit/work trace recording for consequential My Account reads/actions and chat-plan lifecycle events. Static returned trace refs are not sufficient when the app-description requires durable trace facts.

Suggested task: `MAFA-03-001`.

### B04: Notification center lifecycle and rendering contracts

Verify or repair notification center backend and frontend behavior: refresh, empty center, mark-read, dismiss, archive, snooze, preference update for visible in-app categories, open-source reauthorization, no source-work mutation, hidden-source denial, and absence of external/provider controls.

Suggested task: `MAFA-04-001`.

### B05: Human chat tool-plan runtime proof

Verify or repair representative My Account chat-plan flows: deterministic no-mutation routing first, proposal without mutation, exact snapshot confirmation, catalog and tool-boundary enforcement, stale/cross-context/out-of-catalog denials, idempotent replay, partial-failure reporting, and safe provider/model unavailable state.

Suggested task: `MAFA-05-001`.

### B06: Digest/export fail-closed and provider-runtime classification

Verify or repair digest start/read/cancel/result/accept/reject behavior, provider/runtime fail-closed state, no fake/model-less success, advisory-only result review, lifecycle events, and task ownership checks. Classify provider-backed happy path as verified or blocked by config.

Suggested task: `MAFA-06-001`.

### B07: Frontend automated surface/action coverage

Strengthen frontend tests/contracts for dashboard counter-first rendering, profile/settings editable-only submissions and named-theme preview semantics, context/recovery surfaces, notification triage, digest surfaces, result/system-message display, and frontend secret-boundary checks.

Suggested task: `MAFA-07-001`.

### B08: Terminal automated verification

Run the verification loop, compare all completed tasks against the mini-project done state, update lifecycle/source-alignment/readiness evidence, and append follow-up tasks if gaps remain.

Suggested task: `MAFA-08-001`.
