# Task: Implement MyAccountAgent evidence tool and fail-closed guidance

## Objective

Implement governed MyAccountAgent evidence tooling, seed/tool-boundary updates, provider fail-closed tests, and no-secret checks.

## Required reads

- AGENTS.md
- specs/full-core-smb-my-account/README.md
- specs/full-core-smb-my-account/conversation-capture.md
- specs/full-core-smb-my-account/my-account-implementation-map.md
- specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
- specs/my-account-workstream-v0/workstream-contract.md
- docs/agent-component-selection-guide.md
- skills/akka-agents/SKILL.md
- skills/akka-agent-tools/SKILL.md
- skills/akka-agent-tool-boundaries/SKILL.md
- skills/akka-agent-seed-documents/SKILL.md
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/ToolRegistry.java
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeToolResolver.java
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeService.java
- templates/ai-first-saas-starter/backend/src/main/resources/agent-behavior-seeds/starter-v1/my-account-system.md
- templates/ai-first-saas-starter/backend/src/main/resources/agent-behavior-seeds/starter-v1/my-account-starter-guidance.md
- templates/ai-first-saas-starter/backend/src/main/resources/agent-behavior-seeds/starter-v1/my-account-starter-scope-reference.md

## In scope

- Add read-only `myAccountEvidence.read` or equivalent governed evidence facade over deterministic My Account service data.
- Register the tool with stable tool id/category/capability and `ToolPermissionBoundary` enforcement.
- Update seed prompt/skill/reference/tool-boundary defaults for full-core SMB My Account guidance, no-direct-mutation boundaries, and evidence use.
- Test allowed evidence tool reads, denied ungranted reads, cross-tenant/customer denials, provider fail-closed request/response guidance, and no deterministic/model-less successful normal guidance.
- Add no-secret/no-hidden-prompt checks for browser and model-visible payloads touched by this task.

## Out of scope

- Do not let MyAccountAgent update context, profile, settings, roles, memberships, policies, agent behavior, or trace redaction.
- Do not implement a personal digest worker.
- Do not bypass `AgentRuntimeService`, `WorkstreamRuntimeAgent`, `ToolPermissionBoundary`, or provider/model boundaries.

## Expected outputs

- Backend agentfoundation evidence tool/source updates.
- Seed/tool-boundary updates for MyAccountAgent.
- Targeted backend tests and frontend/provider-blocked contract coverage if needed.
- Updated queue status.

## Required checks

```bash
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=AgentBehaviorSeedLoaderTest,AgentRuntimeServiceTest,AgentRuntimeToolResolverTest,WorkstreamRuntimeAgentTest,WorkstreamServiceTest
cd templates/ai-first-saas-starter/frontend && npm test -- --runTestsByPath src/workstream-composer-message-api.contract.test.mjs src/workstream-my-account-vertical.contract.test.mjs src/api.contract.test.mjs
rg -n "MyAccountAgent|agent-my-account|myAccountEvidence\.read|my_account\.ask_agent|readSkill|readReferenceDoc|ToolPermissionBoundary|AgentWorkTrace|PromptAssemblyTrace|provider|system_message|blocked_provider_or_runtime|no direct mutation|rawJwt|providerSecret|hiddenPromptText" templates/ai-first-saas-starter --glob '!**/node_modules/**'
git diff --check
```

## Done criteria

- MyAccountAgent guidance can load scoped evidence only through governed tools and loader tools.
- Missing provider/model config fails closed with typed `system_message` and traces.
- No deterministic/model-less normal runtime guidance is used to claim completion.
- Task changes and queue update are committed with `full-core-smb: implement my account agent evidence`.
