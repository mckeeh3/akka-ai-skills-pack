# Task: Implement access-review frontend surfaces and actions

## Objective

Render runtime-aligned User Admin access-review task states, actions, provider-blocked states, evidence references, recommendations, review decisions, and trace links.

## Required reads

Use the required reads listed on `TASK-FCSMB-UARW-01-004` in `pending-tasks.md`.

## In scope

- Align frontend surface/action types and fixtures with `user_admin.access_review_task.v1`.
- Render queued/running/provider-blocked/cancelled/completed/accepted/rejected access-review states.
- Show progress, blockers, evidence refs, recommendation/risk/confidence, provider failure, result-review state, and trace links.
- Add start/read/cancel/accept/reject action handling as backend-authoritative actions.
- Keep denied/blocked provider states visually distinct from completed model-backed results.
- Update contract tests for accessibility-safe labels, trace links, no secret leakage, and no UI implication that the worker directly changed access.

## Out of scope

- Do not implement backend lifecycle or worker logic here.
- Do not introduce page-first CRUD flows or enterprise certification campaign UI.
- Do not make frontend state grant authority.

## Expected outputs

- Updated starter frontend source under `templates/ai-first-saas-starter/frontend/src/workstream/` and `.../src/api/` if needed.
- Updated frontend contract tests.
- Root `frontend/` synchronized only if touched source is mirrored by repository convention.

## Required checks

```bash
cd templates/ai-first-saas-starter/frontend && npm test -- --runTestsByPath src/workstream-user-admin-vertical.contract.test.mjs src/workstream-actions.contract.test.mjs src/workstream-surfaces.contract.test.mjs src/api.contract.test.mjs
rg -n "user_admin\.access_review_task\.v1|user_admin\.access_review\.(start|read|cancel|accept_result|reject_result)|access-review|blocked_provider_or_runtime|provider|trace|no direct mutation" templates/ai-first-saas-starter/frontend/src --glob '!**/node_modules/**'
git diff --check
```

## Done criteria

- Frontend contract tests pass.
- Runtime and fixture surfaces cover all required access-review states safely.
- UI copy preserves backend authority and no-direct-mutation boundaries.

## Commit message

- `full-core-smb: render access review task surfaces`
