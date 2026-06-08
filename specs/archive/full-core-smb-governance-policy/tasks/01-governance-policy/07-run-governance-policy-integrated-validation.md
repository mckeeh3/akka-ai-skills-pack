# Task: Run integrated Governance/Policy validation

## Objective

Run targeted backend/frontend and broad starter validation for the completed Governance/Policy implementation group, then close readiness or append bounded blockers before terminal verification.

## Required reads

- AGENTS.md
- specs/full-core-smb-governance-policy/README.md
- specs/full-core-smb-governance-policy/governance-policy-implementation-map.md
- specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
- specs/governance-policy-workstream-v0/workstream-contract.md
- specs/full-core-smb-governance-policy/pending-tasks.md
- task outputs from `TASK-FCSMB-GP-01-002` through `TASK-FCSMB-GP-01-006`

## In scope

- Run targeted backend and frontend checks for Governance/Policy.
- Run broad starter fullstack validation or record exact blocker.
- Run secret-boundary/source evidence searches.
- If checks reveal material gaps, append bounded follow-up tasks before verification and do not mark this validation task complete unless the queue is repaired and the validation result is recorded.
- Update `pending-tasks.md` notes with validation summary.

## Out of scope

- Do not implement unrelated User Admin, Agent Admin, Audit/Trace, or enterprise governance features.
- Do not silently expand scope beyond the Governance/Policy SMB full-core slice.

## Expected outputs

- updated `specs/full-core-smb-governance-policy/pending-tasks.md` notes
- optional appended follow-up tasks if validation finds gaps

## Required checks

```bash
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=GovernancePolicyServiceTest,WorkstreamServiceTest,AdminEndpointIntegrationTest
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=AgentBehaviorSeedLoaderTest,AgentRuntimeServiceTest,AgentRuntimeToolResolverTest,WorkstreamRuntimeAgentTest
cd templates/ai-first-saas-starter/frontend && npm test -- --runTestsByPath src/workstream-governance-policy-vertical.contract.test.mjs src/workstream-actions.contract.test.mjs src/workstream-surfaces.contract.test.mjs src/workstream-composer-message-api.contract.test.mjs src/api.contract.test.mjs
rg -n "Governance/Policy|GovernancePolicyAgent|governance\.policy|governancePolicyEvidence\.read|policy dashboard|proposal|simulate|approve|reject|activate|rollback|exception|decision|AgentWorkTrace|PromptAssemblyTrace|ToolPermissionBoundary|provider|system_message|tenant|no direct mutation|blocked_provider_or_runtime" templates/ai-first-saas-starter --glob '!**/node_modules/**'
tools/validate-ai-first-saas-starter-fullstack.sh
git diff --check
```

## Done criteria

- Targeted checks and broad validation pass, or exact blockers are recorded and bounded follow-up tasks are appended.
- Governance/Policy implementation group is ready for terminal verification.
- No model-less successful normal GovernancePolicyAgent or impact-worker behavior is used to claim completion.

## Commit message

- `full-core-smb: validate governance policy full core`
