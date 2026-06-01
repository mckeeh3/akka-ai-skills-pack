# Attention Release-Readiness Handoff

## Scope assessed

This handoff covers the starter template attention behavior delivered by:

- `specs/workstream-attention-backbone-v1/` — shared backend-owned attention backbone, scoped projections, lifecycle operations, My Account aggregate, and left-rail/workstream summary integration.
- `specs/workstream-attention-event-producers-v2/` — bounded producer/update-delivery paths for starter service state, including invitations, governance proposals/decisions, timed checks, worker/task blocked states, and frontend refresh delivery.
- `specs/attention-release-readiness-dogfood/` — dogfood evidence, fresh scaffold validation, runtime-edge review, and this release handoff.

The claim is intentionally bounded to v1/v2 starter scope. It does not claim a generic Workstream Event Backbone v3, full notification infrastructure, enterprise digesting, or broad AutonomousAgent task notification integration.

## Release-readiness assessment

Status: **release-ready at the implemented v1/v2 starter attention scope**.

Evidence supports the following release-ready claims:

- left-rail “things needing my attention” are backend-derived and visibly working in user dogfood testing;
- My Account personal/open attention and workstream navigation are covered by backend and frontend tests;
- five-core dashboards/surfaces render attention metadata, empty/denied states, trace refs, and provider-blocked surfaces at current starter scope;
- invitation, governance, timed, and user-admin access-review paths upsert, update, and resolve attention idempotently through the shared backbone;
- lifecycle/no-op behavior is covered by backend tests;
- hidden/denied workstreams and tenant/customer mismatches are redacted or denied safely;
- missing provider/configuration states fail closed with blocked/actionable status rather than deterministic normal-runtime success;
- frontend `railAttentionState` remains transient/unseen-response UI state and is not authoritative for actionable attention;
- a fresh scaffold now passes the targeted backend attention test set and the full frontend test/typecheck/build sequence.

## Validation evidence

### Manual/dogfood evidence

Recorded user observation:

> i tested the app and things have significantly improved. the left rail workstream things needing my attention are working. also, seeing improvements in the dashboards and surfaces.

This is the direct browser/manual evidence for the visible left rail and dashboard/surface attention improvements.

### Fresh scaffold automated evidence

From `fresh-scaffold-validation.md` and the follow-up path-fix task:

- scaffold generation passed from `templates/ai-first-saas-starter` using `ai.first` base package;
- targeted backend Maven attention tests passed: 75 tests, 0 failures/errors/skips;
- the initial frontend blocker was isolated to stale contract-test backend paths;
- after the fix, a new fresh scaffold frontend passed `npm ci`, `npm test` (132 passed), `npm run typecheck`, and `npm run build`.

### Runtime-edge/security evidence

From `manual-runtime-edge-review.md` fresh scaffold `/tmp/attention-dogfood-runtime-review-q2RB7j`:

- backend targeted attention tests passed: 75 tests, 0 failures/errors/skips;
- frontend `npm ci`, `npm test` (132 passed), `npm run typecheck`, and `npm run build` passed;
- source checks confirmed `attention.list_rail_summaries`, `attention.list_workstream_items`, `functionalAgentsWithBackendAttention`, `blocked_provider_or_runtime`, and backend-derived attention rendering markers;
- source checks confirmed `railAttentionState` is frontend-only transient state;
- provider-secret checks found Resend configuration keys in backend service code, with frontend references limited to safe test/assertion text.

## Known limitations / non-blocking future work

These are outside the current release-ready claim and should remain future work unless promoted into a new queue:

- generic Workstream Event Backbone v3;
- broader event consumers for every possible domain state change;
- SSE/push delivery beyond the current backend refresh/update path;
- enterprise notification center and digest infrastructure;
- richer audit/provider-readiness producers beyond current bounded starter flows;
- broader model-backed AutonomousAgent worker notification integration beyond the current access-review/task blocked-state coverage.

## Handoff guidance for future agents

- Do not report the starter attention backbone as missing after the v1/v2 work; it exists as a shared backend attention backbone with scoped projections and lifecycle operations.
- Do not overclaim realtime/event-notification infrastructure; v1/v2 readiness is based on backend-derived summaries/items, bounded producers, lifecycle behavior, frontend refresh/update delivery, and tested fail-closed/security behavior.
- Treat future event backbone, SSE/push, notification center, digests, and broader AutonomousAgent task notification work as new bounded initiatives, not as blockers for this v1/v2 release-ready state.
- Preserve the runtime completion doctrine: provider/model-backed paths must fail closed or invoke the governed runtime path; deterministic/demo/model-less normal runtime behavior must not be used to mark future generated-app features complete.

## Documentation scan

A focused scan was run over attention docs/handoff areas for stale missing-backbone or overclaim language. Findings were compatible with the bounded handoff: v1/v2 documents describe implemented attention backbone and bounded producer/update delivery, while v3 event backbone, SSE/push, notification, digest, and broader AutonomousAgent integration remain explicitly future/non-goal work.
