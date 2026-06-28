# MAFA-02-001: Backend protected API and action path tests

## Goal

Prove and repair non-manual backend My Account runtime paths for protected workstream surfaces and `surface_action` adapters.

## Required reads

- `specs/my-account-full-alignment/README.md`
- `specs/my-account-full-alignment/backlog/01-my-account-automated-alignment-build-backlog.md`
- `specs/my-account-full-alignment/tasks/02-backend/01-protected-api-action-tests.md`
- `app-description/domains/core-starter/workstreams/my-account/surfaces/surfaces.md`
- `app-description/domains/core-starter/workstreams/my-account/tools/governed-tools.md`
- `app-description/domains/core-starter/workstreams/my-account/tests/coverage.md`
- `src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `src/main/java/ai/first/application/coreapp/myaccount/**`
- existing My Account backend tests

## Skills

- `akka-http-endpoints`
- `akka-http-endpoint-testing`
- `capability-first-backend`
- `akka-runtime-feature-verification`

## Implementation requirements

- Add or repair tests for dashboard default/open and counter routing.
- Cover profile/settings update success, no-op, validation errors, unsupported-field denial, invalid theme/timezone, and immutable/provider-backed field protection.
- Cover context authority refresh/select/no-op and hidden/cross-tenant/invalid context denial without enumeration.
- Cover no-active-membership/disabled-account My Account recovery through protected workstream API behavior where feasible in automated tests.
- Ensure all consequential actions use backend authorization, selected `AuthContext`, correlation/idempotency where applicable, and typed result/system-message surfaces.
- Repair runtime code only where tests reveal drift from current intent.

## Vertical workstream contract

- Lifecycle / readiness target: build-compile to backend-ready/api-smoked by automated backend tests.
- Workstream / functional agent: My Account / `my-account-agent`.
- Surface graph/action edge: `surface-my-account-dashboard`, `surface-my-profile`, `surface-my-settings`, `surface-my-context`, `surface-my-account-open-denied`; `surface_action` edges.
- Governed-tool id and exposure: `read-current-account-context`, `my_account.update_profile_settings`, `my_account.open_authorized_workstream`, `core.access.context.select`; actor adapter `surface_action` and API/internal calls.
- Confirmation / approval / transaction: profile/settings use idempotency; context selection no-op/refresh semantics; no approval gate.
- Capability id: `account-context-and-profile`, `my_account.view_summary`, `my_account.view_context`, `my_account.update_profile_settings`, `my_account.open_authorized_workstream`.
- AuthContext / tenant scope: signed-in account, selected tenant/customer membership, hidden/cross-tenant denials.
- Akka substrate: HTTP endpoint/service tests; no model provider required.
- Audit/work trace requirements: returned and persisted/recorded trace evidence checked where existing trace sink supports it; gaps feed `MAFA-03-001`.
- Local validation path: targeted Maven tests plus `git diff --check`.

## Required checks

```bash
mvn -Dtest='WorkstreamServiceTest,MyAccountBrowserWorkstreamSmokeTest' test
git diff --check
```

## Done criteria

- Tests prove the protected backend paths above or record precise blockers.
- Runtime repairs preserve tenant/customer scope and frontend secret boundaries.
- Source-alignment notes are updated for backend/API slices.
- Queue status is updated and changes are committed.
