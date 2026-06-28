# MAFA-07-001: Frontend automated surface coverage

## Goal

Strengthen automated frontend coverage for My Account surfaces and browser secret boundaries so non-manual UI alignment is provable without claiming human browser acceptance.

## Required reads

- `specs/my-account-full-alignment/README.md`
- `app-description/domains/core-starter/workstreams/my-account/surfaces/surfaces.md`
- `app-description/domains/core-starter/workstreams/my-account/realization/frontend-routes.md`
- `frontend/src/workstream/surfaces/**`
- `frontend/src/workstream-my-account-vertical.contract.test.mjs`
- frontend API/type tests

## Skills

- `akka-web-ui-testing`
- `akka-web-ui-accessibility-responsive`
- `akka-web-ui-state-rendering`
- `akka-web-ui-api-client`

## Implementation requirements

- Test dashboard counter-first rendering and secondary/collapsed attention evidence behavior.
- Test profile/settings self-service forms submit only editable fields and expose named-theme preview as local-only until save.
- Test context/recovery surfaces render safe no-enumeration/no-direct-mutation messaging.
- Test notification triage sections/lifecycle controls and absence of external channel/provider controls.
- Test digest progress/result/blocked surfaces render provider/runtime blockers, advisory-only result, evidence refs, and trace refs.
- Test frontend does not render raw JWT/session/provider payloads, hidden workstream names/counts, hidden categories, external outbox/provider secrets, arbitrary CSS, or model/provider configuration.
- Repair frontend drift discovered by tests.

## Vertical workstream contract

- Lifecycle / readiness target: build-compile to frontend-rendered by automated tests/typecheck/build.
- Workstream / functional agent: My Account / `my-account-agent`.
- Surface graph/action edge: all My Account browser surfaces and their `surface_action` controls; no direct browser authorization.
- Governed-tool id and exposure: frontend renders backend-authored actions for My Account/notification/digest tools; does not grant authority.
- Confirmation / approval / transaction: frontend preserves idempotency/action input and does not auto-confirm chat plans or claim persistence for previews.
- Capability id: backend-authored visible capability ids only.
- AuthContext / tenant scope: selected `AuthContext` displayed from backend payload; hidden data omitted.
- Akka substrate: frontend tests only; backend remains authoritative.
- Audit/work trace requirements: trace links rendered safely; raw trace/provider internals not exposed.
- Local validation path: frontend tests, typecheck, build, `git diff --check`.

## Required checks

```bash
npm --prefix frontend test -- --run
npm --prefix frontend run typecheck
npm --prefix frontend run build
git diff --check
```

## Done criteria

- Automated frontend coverage proves non-manual My Account UI contract alignment.
- Source-alignment frontend entries are updated.
- Queue status is updated and changes are committed.
