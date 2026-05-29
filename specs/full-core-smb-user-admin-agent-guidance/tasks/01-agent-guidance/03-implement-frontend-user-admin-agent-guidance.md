# Task: Implement frontend UserAdminAgent guidance and blocked-state rendering

## Objective

Render UserAdminAgent guidance, provider/runtime denials, and trace-linked safe next steps clearly in the workstream UI after backend response/surface contracts are updated.

## Required reads

- AGENTS.md
- specs/full-core-smb-user-admin-agent-guidance/README.md
- specs/full-core-smb-user-admin-agent-guidance/agent-guidance-implementation-map.md
- specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
- specs/full-core-smb-user-admin/user-admin-vertical-contracts.md

## Skills

- none; focused frontend source task using the implementation map

## In scope

- Update starter frontend renderers/fixtures/tests for UserAdminAgent successful `markdown_response` guidance and provider/runtime-blocked `system_message` or blocked surfaces.
- Show recovery guidance, capability id where available, safe reason, trace/correlation links, and redaction/secret-boundary indicators.
- Ensure UI text does not imply the agent directly mutated invitations, memberships, roles, capabilities, or authorization state.
- Synchronize root `frontend/` only if the touched starter files have a repository mirror convention.

## Out of scope

- No backend tool/runtime implementation.
- No new access-review worker UI.
- No page-first CRUD redesign.

## Expected outputs

- Updated frontend workstream surface/stream rendering files under `templates/ai-first-saas-starter/frontend/src/`.
- Updated frontend contract tests for UserAdminAgent guidance, provider-blocked/system-message states, no secret leakage, and trace links.

## Required checks

```bash
cd templates/ai-first-saas-starter/frontend && npm test -- --runTestsByPath src/workstream-user-admin-expertise.contract.test.mjs src/workstream-composer-message-api.contract.test.mjs src/workstream-surfaces.contract.test.mjs
rg -n "UserAdminAgent|user-admin|markdown_response|system_message|blocked_provider_or_runtime|provider|trace|no direct mutation|readSkill|readReferenceDoc|userAdminEvidence" templates/ai-first-saas-starter/frontend/src
git diff --check
```

## Done criteria

- UserAdminAgent normal guidance and provider/runtime-blocked responses render with safe copy, trace links, and recovery steps.
- Frontend tests cover blocked, trace-linked, no-secret, and no-direct-mutation expectations.
- Queue status is updated and the task changes are committed with message `full-core-smb: implement user admin agent frontend guidance`.
