# Task: Repair Governance/Policy seed boundary validation

## Objective

Fix the rendered starter validation failure discovered by `TASK-GOVPOL-99-001`: `AgentBehaviorSeedLoaderTest.allFiveCoreAgentsResolveThroughSameManagedRuntimePathWithDistinctProfiles` expects `agent-governance-policy` runtime tool resolution to be `ALLOWED`, but the fullstack validation currently reports `DENIED`.

## Required reads

- AGENTS.md
- skills/README.md
- specs/five-core-workstreams-v0-plan/shared-five-core-v0-contract.md
- specs/governance-policy-workstream-v0/workstream-contract.md
- specs/governance-policy-workstream-v0/capability-inventory.md
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentBehaviorSeedLoader.java
- templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentBehaviorSeedLoaderTest.java
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeToolResolver.java

## Skills

- akka-agent-tool-boundaries
- akka-agent-seed-documents

## In scope

- Repair Governance/Policy managed-agent seed/boundary data or validation expectations so all five core agents resolve through the governed runtime path with distinct active profiles.
- Preserve deny-by-default `ToolPermissionBoundary`, selected `AuthContext`, tenant scope, and provider fail-closed semantics.
- Add or adjust focused tests only for the discovered Governance/Policy seed-boundary gap.

## Out of scope

- Do not implement new Governance/Policy proposal activation behavior.
- Do not weaken tool-boundary matching or broaden grants beyond the v0 read/evidence contract.
- Do not replace model-backed runtime with deterministic normal-runtime fallback.

## Required checks

- `tools/validate-ai-first-saas-starter-fullstack.sh`
- `git diff --check`

## Done criteria

- Fullstack starter validation passes.
- Governance/Policy managed-agent seed boundary resolves runtime `readSkill`/`readReferenceDoc` tools through the same governed path as the other four core agents.
- Task changes and queue update are committed.
