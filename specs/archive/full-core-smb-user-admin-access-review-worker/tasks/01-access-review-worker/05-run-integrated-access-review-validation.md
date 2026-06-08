# Task: Run integrated access-review validation

## Objective

Run targeted and broad validation for the implemented access-review worker slice, then update the queue with blockers or readiness notes.

## Required reads

Use the required reads listed on `TASK-FCSMB-UARW-01-005` in `pending-tasks.md`.

## In scope

- Run backend lifecycle/worker tests.
- Run frontend access-review/workstream/action/surface tests.
- Run targeted evidence searches proving capability ids, typed surface ids, worker/runtime, provider blocked state, trace, and no-direct-mutation coverage.
- Run broad starter validation or record a concrete environment/source blocker and append follow-up tasks.
- Update this mini-project queue with newly discovered bounded blockers before verification when needed.

## Out of scope

- Do not silently fix unrelated repository issues.
- Do not expand scope beyond SMB access-review worker readiness.

## Expected outputs

- Updated `specs/full-core-smb-user-admin-access-review-worker/pending-tasks.md` with validation notes, blockers, or appended follow-up tasks.
- Optional validation notes in `access-review-worker-implementation-map.md` if useful.

## Required checks

```bash
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=UserAdminAccessReviewServiceTest,UserAdminAccessReviewWorkerTest,WorkstreamServiceTest,InvitationAndUserAdminServiceTest,AgentRuntimeToolResolverTest,WorkstreamRuntimeAgentTest
cd templates/ai-first-saas-starter/frontend && npm test -- --runTestsByPath src/workstream-user-admin-vertical.contract.test.mjs src/workstream-actions.contract.test.mjs src/workstream-surfaces.contract.test.mjs src/api.contract.test.mjs
rg -n "user_admin\.access_review\.(start|read|cancel|accept_result|reject_result)|user_admin\.access_review_task\.v1|AccessReviewTask|UserAdminAccessReview|AutonomousAgent|userAdminEvidence\.read|ToolPermissionBoundary|AgentWorkTrace|system_message|provider|no direct mutation|blocked_provider_or_runtime" templates/ai-first-saas-starter --glob '!**/node_modules/**'
tools/validate-ai-first-saas-starter-fullstack.sh
git diff --check
```

## Done criteria

- Targeted backend/frontend checks pass.
- Broad validation passes, or any blocker is captured with a bounded follow-up task before terminal verification.
- The queue has a runnable verification task after all required follow-ups.

## Commit message

- `full-core-smb: validate access review worker`
