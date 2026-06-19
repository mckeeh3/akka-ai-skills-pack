# TASK-FCSR-07-001: Run full-core runtime smoke and update readiness

## Objective

Run the final full-core local validation pass for the selected scope, update readiness docs, and record runtime/API/UI evidence or precise blockers.

## Required reads

- full-core readiness gap contract from `TASK-FCSR-01-001`
- completed task notes from all prior full-core readiness tasks
- `AGENTS.md`
- `app-description/app.md` and `app-description/domains/core-starter/realization/traceability.md`
- `specs/full-core-saas-readiness/full-core-readiness-verification.md`
- `frontend/package.json`
- `pom.xml`
- any existing validation tools under `tools/**`

## Skills

- `akka-web-ui-testing`
- `akka-http-endpoint-testing`
- `app-description-readiness-assessment`
- `app-description-readiness-summary`

## In scope

- Run backend tests, frontend tests/typecheck/build, static secret-boundary checks, and focused smoke/manual notes for the selected full-core scope.
- Verify auth fail-closed, invitation onboarding, User Admin, managed-agent foundation, Audit/Trace, Governance/Policy, tenant isolation/forbidden/disabled-user/audit paths at the level implemented.
- Update readiness docs to match evidence.
- Append follow-up tasks if material gaps remain before final verification.

## Out of scope

- Implementing large missing features discovered during smoke; append bounded follow-up tasks instead.

## Expected outputs

- `specs/full-core-saas-readiness/full-core-runtime-smoke.md` or equivalent validation artifact.
- Updated readiness docs and queue.

## Required checks

- `git diff --check`
- `mvn test`
- `npm --prefix frontend test -- --run`
- `npm --prefix frontend run typecheck`
- `npm --prefix frontend run build`
- any repository secret-boundary/static asset scan if available

## Done criteria

- Full-core smoke evidence or precise blockers are recorded.
- Readiness docs match evidence.
- Follow-up tasks are appended if needed.
- Changes and queue update are committed.

## Commit message

`full-core-ready: run runtime smoke`
