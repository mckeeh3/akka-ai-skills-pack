# Task: Validate UserAdminAgent guidance runtime

## Objective

Run integrated backend/frontend/fullstack validation for the UserAdminAgent guidance slice and append blockers if runtime/API/UI readiness is not proven.

## Required reads

- AGENTS.md
- specs/full-core-smb-user-admin-agent-guidance/README.md
- specs/full-core-smb-user-admin-agent-guidance/agent-guidance-implementation-map.md
- specs/full-core-smb-user-admin-agent-guidance/pending-tasks.md
- specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md

## Skills

- none; repository validation task

## In scope

- Run targeted backend and frontend tests for the implemented guidance slice.
- Run broad starter validation when feasible.
- Verify provider-configured smoke if local provider environment exists; otherwise verify provider-missing fail-closed behavior and record the limitation.
- Inspect source/test coverage for no direct mutation, scoped evidence, governed loader tools, traces, and secret boundaries.
- Update queue notes or append bounded blocker tasks before verification when validation fails.

## Out of scope

- Do not implement unrelated source changes except minimal queue/blocker updates.
- Do not mark runtime behavior complete from fixture-only or deterministic/model-less paths.

## Expected outputs

- Updated `specs/full-core-smb-user-admin-agent-guidance/pending-tasks.md` with validation notes, blocked status, or appended follow-up tasks if needed.
- Optional validation notes in the mini-project if failures require explanation.

## Required checks

```bash
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=AgentBehaviorSeedLoaderTest,AgentRuntimeServiceTest,AgentRuntimeToolResolverTest,WorkstreamRuntimeAgentTest,WorkstreamServiceTest
cd templates/ai-first-saas-starter/frontend && npm test -- --runTestsByPath src/workstream-user-admin-expertise.contract.test.mjs src/workstream-composer-message-api.contract.test.mjs src/workstream-surfaces.contract.test.mjs
rg -n "UserAdminAgent|user-admin-system|readSkill|readReferenceDoc|ToolPermissionBoundary|PromptAssemblyTrace|SkillLoadTrace|ReferenceLoadTrace|AgentWorkTrace|provider fail|system_message|no direct mutation|userAdminEvidence\.read" templates/ai-first-saas-starter --glob '!**/node_modules/**' --glob '!**/target/**'
tools/validate-ai-first-saas-starter-fullstack.sh
git diff --check
```

If `tools/validate-ai-first-saas-starter-fullstack.sh` cannot run because of local environment limits, record the exact reason and rely only on targeted checks for task status; append a blocker if the implemented runtime path is not otherwise proven.

## Done criteria

- Targeted checks pass and broad validation passes or a justified blocker is appended.
- Provider-missing fail-closed behavior is validated; provider-configured smoke is run when environment exists.
- No direct mutation, unauthorized evidence, or secret leakage path is found.
- Queue status is updated and the task changes are committed with message `full-core-smb: validate user admin agent guidance runtime`.
