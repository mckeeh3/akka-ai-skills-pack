# Task: Run integrated My Account validation

## Objective

Run targeted backend/frontend validation for the implemented My Account full-core slice, execute broad starter validation when appropriate, and append blockers or mark readiness for terminal verification.

## Required reads

- AGENTS.md
- specs/full-core-smb-my-account/README.md
- specs/full-core-smb-my-account/conversation-capture.md
- specs/full-core-smb-my-account/pending-tasks.md
- specs/full-core-smb-my-account/my-account-implementation-map.md
- specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
- specs/my-account-workstream-v0/workstream-contract.md
- predecessor task changes from TASK-FCSMB-MA-01-002 through TASK-FCSMB-MA-01-005

## In scope

- Run targeted backend tests for `/api/me`, context, settings, attention, navigation, authorization, tenant isolation, idempotency, provider blocked states, and traces.
- Run targeted frontend tests/typecheck/build for user tile launch, My Account surfaces, denials, no top-rail duplicate, trace links, provider-blocked states, and API contracts.
- Run broad `tools/validate-ai-first-saas-starter-fullstack.sh` if source changes are broad enough or record a concrete blocker.
- Append bounded follow-up tasks before terminal verification if validation finds gaps.

## Out of scope

- Do not implement broad fixes in this validation task unless they are trivial validation-script/doc updates required to record results.
- Do not mark completion if real runtime/API/UI paths are not validated or blockers are not queued.

## Expected outputs

- Updated queue with validation result notes and any newly appended bounded tasks.
- Optional validation notes in `my-account-implementation-map.md` or a small verification note if helpful.

## Required checks

```bash
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=MeServiceTest,WorkstreamServiceTest,AdminEndpointIntegrationTest
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=AgentBehaviorSeedLoaderTest,AgentRuntimeServiceTest,AgentRuntimeToolResolverTest,WorkstreamRuntimeAgentTest
cd templates/ai-first-saas-starter/frontend && npm test -- --runTestsByPath src/workstream-my-account-vertical.contract.test.mjs src/workstream-shell.contract.test.mjs src/workstream-actions.contract.test.mjs src/workstream-surfaces.contract.test.mjs src/workstream-composer-message-api.contract.test.mjs src/api.contract.test.mjs
rg -n "My Account|MyAccountAgent|my_account|/api/me|selected context|authority|profile|settings|personal attention|user tile|trace refs|open_authorized_workstream|myAccountEvidence\.read|ToolPermissionBoundary|provider|system_message|tenant|no duplicate top-rail|blocked_provider_or_runtime" templates/ai-first-saas-starter specs/full-core-smb-my-account --glob '!**/node_modules/**'
git diff --check
```

Run or explicitly record blocker for:

```bash
tools/validate-ai-first-saas-starter-fullstack.sh
```

## Done criteria

- Targeted validation passes or blockers are captured as bounded follow-up tasks before terminal verification.
- Broad validation is run or a concrete blocker is recorded.
- Terminal verification task can determine readiness without guessing.
- Task changes and queue update are committed with `full-core-smb: validate my account full core`.
