# Task: Fix access-review frontend typecheck blocker

## Objective

Fix the integrated validation blocker where the starter frontend typecheck references a removed/renamed `userAdminSurfaceActions.approveRiskyAccess` action in User Admin access-review-capable surfaces.

## Required reads

Use the required reads listed on `TASK-FCSMB-UARW-01-006` in `pending-tasks.md`.

## In scope

- Inspect the User Admin access-review fixture/action definitions in `templates/ai-first-saas-starter/frontend/src/workstream/fixtures/surfaces.ts`.
- Replace or align stale `approveRiskyAccess` references with the implemented access-review result action names/capability ids.
- Run the focused frontend typecheck/build validation needed to prove the blocker is cleared.
- Update the queue with check results.

## Out of scope

- Do not change backend access-review lifecycle or worker behavior.
- Do not broaden the access-review worker beyond the SMB slice.
- Do not introduce direct access mutation from access-review result actions.

## Expected outputs

- Updated frontend fixture/action source under `templates/ai-first-saas-starter/frontend/src/workstream/fixtures/surfaces.ts`.
- Updated `specs/full-core-smb-user-admin-access-review-worker/pending-tasks.md` with completion notes.

## Required checks

```bash
cd templates/ai-first-saas-starter/frontend && npm run typecheck
cd templates/ai-first-saas-starter/frontend && npm test -- --runTestsByPath src/workstream-user-admin-vertical.contract.test.mjs src/workstream-actions.contract.test.mjs src/workstream-surfaces.contract.test.mjs src/api.contract.test.mjs
tools/validate-ai-first-saas-starter-fullstack.sh
git diff --check
```

## Done criteria

- Frontend typecheck passes.
- Targeted frontend contract tests pass.
- Broad starter validation passes or any remaining blocker is captured with another bounded follow-up before terminal verification.
- Queue has a runnable verification task after all required follow-ups.

## Commit message

- `full-core-smb: fix access review frontend typecheck`
