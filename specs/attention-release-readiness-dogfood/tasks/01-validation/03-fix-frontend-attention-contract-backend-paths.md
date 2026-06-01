# TASK-ARD-01-003: Fix scaffold frontend attention contract test backend paths

## Objective

Fix the fresh scaffold blocker found by `TASK-ARD-01-002`: two frontend attention contract tests assume a `backend/` subdirectory even though the starter scaffold places backend source at the project root.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/attention-release-readiness-dogfood/README.md`
- `specs/attention-release-readiness-dogfood/fresh-scaffold-validation.md`
- `templates/ai-first-saas-starter/frontend/src/workstream-attention-backbone.contract.test.mjs`
- `templates/ai-first-saas-starter/frontend/src/workstream-attention-update-delivery.contract.test.mjs`
- this task brief

## In scope

- Update only the scaffold template/frontend contract-test path logic needed for the tests to read generated backend source from the actual scaffold layout.
- Scaffold a new fresh starter into a temp directory and rerun frontend checks.
- Update `pending-tasks.md` with the task result.

## Non-goals

- Do not broaden the attention release scope.
- Do not implement new attention behavior.
- Do not change backend source unless the path fix reveals a separate compile/test blocker that is explicitly recorded instead.

## Required checks

- `git diff --check`
- Fresh scaffold frontend `npm ci` and `npm test`
- Fresh scaffold frontend `npm run typecheck`
- Fresh scaffold frontend `npm run build`

## Done criteria

- Fresh scaffold frontend attention contract tests read the actual generated backend source layout.
- `npm test`, typecheck, and build pass in a new fresh scaffold, or any remaining unrelated blocker is recorded with a bounded follow-up.
- Task changes and queue update are committed.

## Commit message

`attention-dogfood: fix frontend attention contract paths`
