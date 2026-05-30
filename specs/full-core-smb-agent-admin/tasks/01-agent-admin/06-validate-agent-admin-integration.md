# Task: Validate integrated Agent Admin full-core slice and worker readiness

## Objective

Run targeted and broad validation for the implemented Agent Admin full-core slices, then decide whether a prompt-risk/behavior-review worker is ready to implement or should remain blocked/deferred.

## Required reads

- AGENTS.md
- specs/full-core-smb-agent-admin/README.md
- specs/full-core-smb-agent-admin/conversation-capture.md
- specs/full-core-smb-agent-admin/sprints/01-agent-admin-full-core-sprint.md
- specs/full-core-smb-agent-admin/backlog/01-agent-admin-full-core-backlog.md
- specs/full-core-smb-agent-admin/agent-admin-implementation-map.md
- specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
- specs/agent-admin-workstream-v0/workstream-contract.md
- specs/full-core-smb-saas-hardening/agent-worker-opportunities.md
- docs/agent-component-selection-guide.md

## In scope

- Run backend, frontend, source-proof, and broad starter validation needed for Agent Admin source changes.
- Verify runtime/API/UI path for implemented reads, lifecycle actions, AgentAdminAgent provider-blocked behavior, redaction, trace links, and secret-boundary expectations.
- Decide whether deterministic lifecycle foundations justify a bounded behavior-review worker implementation task.
- If gaps remain, append bounded follow-up tasks before terminal verification.

## Out of scope

- Do not implement new runtime features except small validation fixes needed to make already-implemented scope pass.
- Do not mark worker behavior complete via model-less/demo/fixture-only success.

## Expected outputs

- validation notes in queue task notes or a small report if useful
- updated `specs/full-core-smb-agent-admin/pending-tasks.md` with either completion path or bounded follow-up tasks

## Required checks

```bash
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=AgentBehaviorSeedLoaderTest,AgentRuntimeServiceTest,AgentRuntimeToolResolverTest,WorkstreamRuntimeAgentTest,WorkstreamServiceTest,AgentDefinitionEntityTest,AgentDefinitionViewIntegrationTest,ManifestBoundaryEntityTest,ManifestBoundaryViewTest,DurableAgentBehaviorRepositoryStateTest
cd templates/ai-first-saas-starter/frontend && npm test -- --runTestsByPath src/workstream-agent-admin-vertical.contract.test.mjs src/workstream-actions.contract.test.mjs src/workstream-surfaces.contract.test.mjs src/workstream-composer-message-api.contract.test.mjs src/api.contract.test.mjs
rg -n "Agent Admin|AgentAdminAgent|AgentDefinition|ToolPermissionBoundary|AgentSkillManifest|AgentReferenceManifest|model ref|behavior change|proposal|activate|rollback|seed|provider|system_message|AgentWorkTrace|PromptAssemblyTrace|no direct mutation|agentAdminEvidence\.read" templates/ai-first-saas-starter specs/full-core-smb-agent-admin --glob '!**/node_modules/**'
tools/validate-ai-first-saas-starter-fullstack.sh
git diff --check
```

## Done criteria

- Implemented Agent Admin backend/frontend/runtime paths pass targeted validation or blockers are appended as bounded tasks.
- Broad starter validation passes or a concrete blocker/follow-up is recorded.
- Worker readiness is explicitly decided; no durable worker normal success is claimed without governed model-backed runtime.
- Task changes and queue update are committed.

## Commit message

- `full-core-smb: validate agent admin integration`
