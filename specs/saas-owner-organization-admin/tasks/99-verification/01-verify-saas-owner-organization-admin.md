# Task: Verify SaaS Owner Organization Admin completion

## Objective

Verify the implemented task group against the mini-project done state and append follow-up tasks if material gaps remain.

## Required reads

- `AGENTS.md`
- `specs/saas-owner-organization-admin/README.md`
- `specs/saas-owner-organization-admin/conversation-capture.md`
- `specs/saas-owner-organization-admin/pending-tasks.md`
- `specs/saas-owner-organization-admin/sprints/*.md`
- `specs/saas-owner-organization-admin/backlog/*.md`
- `specs/saas-owner-organization-admin/tasks/**/*.md`
- app-description files touched by this mini-project
- backend/API/frontend tests and implementation touched by this mini-project

## Skills

- `app-description-readiness-assessment`
- `akka-web-ui-testing`
- `akka-http-endpoint-testing`

## Expected outputs

- Completion/verification notes under `specs/saas-owner-organization-admin/` if needed.
- Updated `pending-tasks.md` marking verification done only if complete, or appending follow-up tasks plus a new terminal verification task if gaps remain.

## Required checks

- `git diff --check`
- `mvn test` or targeted backend/API tests sufficient for touched backend scope
- `npm --prefix frontend test -- --run` if frontend was changed
- `npm --prefix frontend run typecheck` if frontend was changed
- `npm --prefix frontend run build` if production frontend output changed

## Done criteria

- Current task group goals have been compared against completed work.
- Overall mini-project done state has been compared against completed work.
- Required checks passed or blockers are precise and reflected in follow-up tasks.
- Remaining material gaps are appended as bounded tasks and followed by a new terminal verification task, or the mini-project is declared complete.
- Changes and queue update are committed.
