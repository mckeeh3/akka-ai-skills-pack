# Task: Verify SaaS Owner Organization Admin runtime completion

## Objective

Re-run terminal verification after follow-up runtime UI/API wiring tasks are complete and declare the mini-project complete only if the real local backend/API/UI path works at the selected scope.

## Required reads

- `AGENTS.md`
- `specs/saas-owner-organization-admin/README.md`
- `specs/saas-owner-organization-admin/verification.md`
- `specs/saas-owner-organization-admin/pending-tasks.md`
- `specs/saas-owner-organization-admin/tasks/99-verification/02-verify-saas-owner-organization-admin-runtime-completion.md`
- follow-up task briefs and files changed after TASK-SOOA-99-001
- backend/API/frontend tests and implementation touched by follow-up tasks

## Skills

- `app-description-readiness-assessment`
- `akka-web-ui-testing`
- `akka-http-endpoint-testing`

## Expected outputs

- Updated verification notes under `specs/saas-owner-organization-admin/`.
- Updated pending queue marking terminal verification done only if complete, or appending precise bounded follow-up tasks if material gaps remain.

## Required checks

- `git diff --check`
- Backend/API tests sufficient for any changed backend scope
- `npm --prefix frontend test -- --run`
- `npm --prefix frontend run typecheck`
- `npm --prefix frontend run build`

## Done criteria

- Real browser UI/workstream actions are verified against protected Admin API behavior, not fixture-only behavior.
- Mini-project done state has been compared against completed work.
- Required checks pass or blockers are precise and reflected in follow-up tasks.
- Remaining material gaps are appended as bounded tasks, or the mini-project is declared complete.
- Changes and queue update are committed.
