# Foundation Surface Completion Tracker

## Purpose

This mini-project tracks every documented dashboard and structured surface in the five foundation workstreams and drives one fresh harness sub-task at a time until each surface is:

1. **fully-specified** — the app-description is sufficient for implementation without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics.
2. **fully-implemented** — the real local runtime path exists for the stated scope: browser surface/action or non-UI trigger -> API/endpoint/client -> Akka component/service/substrate -> view/audit/trace outcome. Fixture-only, frontend-only, mock-only, and contract-only behavior does not count.
3. **fully-tested** — automated checks and/or recorded manual/API/browser smoke evidence prove the intended runtime path, including role/AuthContext/tenant scope, authorization denials, audit/work traces, provider configured or fail-closed behavior, and frontend secret boundaries where applicable.

## Scope

Foundation workstreams covered:

- My Account
- User Admin
- Agent Admin
- Audit/Trace
- Governance/Policy

The source inventory is the active app-description under `app-description/domains/core-starter/workstreams/*/surfaces/surfaces.md`. The detailed status tracker is `surface-completion-tracker.md`; executable sub-tasks are in `pending-tasks.md`.

## Status values

Use these status values for each objective and pending sub-task:

- `pending` — not yet assessed or completed.
- `in-progress` — currently being assessed or implemented in this harness session.
- `blocked` — cannot complete because required intent, code, provider config, seed data, validation tooling, or another prerequisite is missing.
- `done` — objective completed with evidence recorded in `surface-completion-tracker.md` and relevant specs/tests/docs.
- `deferred` — intentionally postponed and not eligible for next-task selection.
- `superseded` — replaced by a later app-description/spec decision.

## Done state

The mini-project is complete when every dashboard/surface row in `surface-completion-tracker.md` has all three objectives marked `done` or has an explicit accepted `deferred`/`superseded` rationale, and the terminal verification task confirms no material runtime-readiness gaps remain for the tracked foundation surface scope.

## Non-goals

- Do not redesign the foundation workstreams while executing tracker tasks unless a task discovers a precise app-description gap.
- Do not mark implementation complete from fixture/demo/frontend-only paths.
- Do not mark testing complete from unit/contract/typecheck/build evidence alone for user-visible runtime behavior.
- Do not edit `skills-pack/**` from this mini-project.

## Counts

- tracked dashboards/surfaces: 77
- per-surface objective sub-tasks: 231
- scaffold and terminal verification tasks included in queue: 2
