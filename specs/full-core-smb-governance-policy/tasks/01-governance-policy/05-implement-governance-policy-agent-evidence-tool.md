# Task: Implement GovernancePolicyAgent evidence tool and seed boundary

## Objective

Add a governed read-only `governancePolicyEvidence.read` tool, seed/tool-boundary updates, and provider fail-closed tests so GovernancePolicyAgent can explain scoped deterministic evidence without gaining mutation authority.

## Required reads

- AGENTS.md
- specs/full-core-smb-governance-policy/README.md
- specs/full-core-smb-governance-policy/governance-policy-implementation-map.md
- specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
- specs/governance-policy-workstream-v0/workstream-contract.md
- skills/akka-agents/SKILL.md
- skills/akka-agent-tools/SKILL.md
- skills/akka-agent-tool-boundaries/SKILL.md
- skills/akka-agent-seed-documents/SKILL.md
- task outputs from `TASK-FCSMB-GP-01-002` and `TASK-FCSMB-GP-01-003`
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/ToolRegistry.java
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeToolResolver.java
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentBehaviorSeedLoader.java
- templates/ai-first-saas-starter/backend/src/main/resources/agent-behavior-seeds/starter-v1/governance-policy-system.md
- templates/ai-first-saas-starter/backend/src/main/resources/agent-behavior-seeds/starter-v1/governance-policy-starter-guidance.md
- templates/ai-first-saas-starter/backend/src/main/resources/agent-behavior-seeds/starter-v1/governance-policy-starter-scope-reference.md

## In scope

- Implement a read-only evidence tool facade over `GovernancePolicyService`.
- Register the tool in the backend tool registry and resolver with stable id `governancePolicyEvidence.read` or a documented equivalent.
- Seed a read-only `ToolPermissionBoundary` grant for GovernancePolicyAgent only.
- Update GovernancePolicyAgent seed prompt/skill/reference copy to instruct evidence use, no direct mutation, and fail-closed provider behavior.
- Add tests for allowed evidence read, denied ungranted tool, tenant isolation, no side effects, prompt/skill/reference/tool-boundary traces, provider-missing `system_message`, and secret-boundary/no hidden prompt/provider credential exposure.

## Out of scope

- Do not grant side-effecting proposal/approval/activation tools to the model.
- Do not bypass `WorkstreamRuntimeAgent`/governed runtime invocation.
- Do not implement AutonomousAgent policy-impact analysis.

## Expected outputs

- new likely `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/GovernancePolicyEvidenceTools.java`
- updated `ToolRegistry.java`, `AgentRuntimeToolResolver.java`, `AgentBehaviorSeedLoader.java`
- updated Governance/Policy seed resources
- updated backend agentfoundation tests

## Required checks

```bash
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=AgentBehaviorSeedLoaderTest,AgentRuntimeServiceTest,AgentRuntimeToolResolverTest,WorkstreamRuntimeAgentTest
rg -n "GovernancePolicyAgent|governancePolicyEvidence\.read|ToolPermissionBoundary|readSkill|readReferenceDoc|PromptAssemblyTrace|SkillLoadTrace|ReferenceLoadTrace|AgentWorkTrace|provider|system_message|no direct mutation|provider secret|hidden prompt" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/backend/src/main/resources templates/ai-first-saas-starter/backend/src/test/java --glob '!**/node_modules/**'
git diff --check
```

## Done criteria

- GovernancePolicyAgent normal guidance uses governed Akka Agent runtime plus authorized read-only evidence/loading tools.
- Missing provider/model/tool-boundary config fails closed with safe `system_message`/trace behavior.
- The tool cannot approve, activate, roll back, mutate policy, mutate users, mutate agent behavior, or expose secrets.

## Commit message

- `full-core-smb: add governance policy agent evidence tool`
