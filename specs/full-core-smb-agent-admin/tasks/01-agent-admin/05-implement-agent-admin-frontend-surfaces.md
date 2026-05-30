# Task: Implement Agent Admin frontend runtime-aligned surfaces

## Objective

Align frontend Agent Admin fixtures, types, action handling, and surface rendering with backend full-core Agent Admin DTOs and lifecycle states.

## Required reads

- AGENTS.md
- specs/full-core-smb-agent-admin/README.md
- specs/full-core-smb-agent-admin/conversation-capture.md
- specs/full-core-smb-agent-admin/sprints/01-agent-admin-full-core-sprint.md
- specs/full-core-smb-agent-admin/backlog/01-agent-admin-full-core-backlog.md
- specs/full-core-smb-agent-admin/agent-admin-implementation-map.md
- specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
- specs/agent-admin-workstream-v0/workstream-contract.md

## In scope

- Update Agent Admin runtime-aligned frontend surface contracts for catalog/dashboard, detail, artifact previews, manifests, tool boundary simulation, model/provider readiness, seed status, behavior proposals, blocked system messages, and traces.
- Ensure action descriptors use backend capability ids and idempotency requirements.
- Distinguish deterministic lifecycle actions from AgentAdminAgent guidance.
- Preserve visual quality, accessibility, responsive behavior, trace links, and redaction indicators.
- Keep fixture client behavior synchronized with backend result surface ids/statuses.
- Synchronize root `frontend/` only if touched starter UI files have repository-mirrored root counterparts.

## Out of scope

- Do not implement backend lifecycle or agent runtime logic.
- Do not create page-first CRUD prompt editors or enterprise marketplace/model procurement UI.

## Expected outputs

- updated frontend files under `templates/ai-first-saas-starter/frontend/src/`
- updated frontend contract tests
- optional synchronized root `frontend/` changes if required by repository convention
- updated `specs/full-core-smb-agent-admin/pending-tasks.md`

## Required checks

```bash
cd templates/ai-first-saas-starter/frontend && npm test -- --runTestsByPath src/workstream-agent-admin-vertical.contract.test.mjs src/workstream-actions.contract.test.mjs src/workstream-surfaces.contract.test.mjs src/workstream-composer-message-api.contract.test.mjs src/api.contract.test.mjs
rg -n "surface-agent-admin|surface-agent-prompt|surface-agent-skill|surface-agent-tool|surface-agent-model|surface-agent-test|surface-agent-behavior|action-(display-agent|open-agent|propose-prompt|test-agent|approve-skill|simulate-tool|manage-model)|agent_admin\.|system_message|blocked_provider_or_runtime|provider|redacted|trace|no direct mutation" templates/ai-first-saas-starter/frontend/src --glob '!**/node_modules/**'
git diff --check
```

## Done criteria

- Frontend Agent Admin surfaces are workstream-first, typed, trace-linked, redacted, visually polished, and backend capability-aligned.
- Blocked provider/runtime and unauthorized states are explicit `system_message`/blocked surfaces, not hidden UI assumptions.
- Fixtures and contract tests match runtime DTOs closely enough for later fullstack validation.
- Task changes and queue update are committed.

## Commit message

- `full-core-smb: implement agent admin frontend`
