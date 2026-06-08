# Task: Bind invitation, agent behavior, and runtime trace durable seams

## Objective

Move normal generated runtime wiring away from Akka component-backed invitation, governed-agent behavior, and agent runtime trace defaults. Use existing durable Akka seams where present and require/implement durable trace persistence or explicit fail-closed construction where missing.

## Required reads

- AGENTS.md
- specs/full-core-smb-runtime-durability-remediation/README.md
- specs/full-core-smb-runtime-durability-remediation/runtime-durability-remediation-map.md
- templates/ai-first-saas-starter/README.md
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/StarterSecurityComponents.java
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/AkkaInvitationRepository.java
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/DurableInvitationRepositoryEntity.java
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AkkaAgentBehaviorRepository.java
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/DurableAgentBehaviorRepositoryEntity.java
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeService.java

## In scope

- Normal runtime binding for invitation current state/outbox.
- Normal runtime binding for AgentDefinition, prompt, skill, reference, manifest, model policy/config, and tool boundary records.
- Runtime trace sink default behavior in `AgentRuntimeService` constructors.
- Tests proving normal paths do not use non-Akka substitute defaults and provider fail-closed behavior remains intact.

## Out of scope

- Foundation identity/workstream/audit/governance/access-review state handled by the foundation task.
- Frontend fixture/static assets.

## Expected outputs

- Backend source and tests.
- README/doc updates for durability coverage.
- Updated queue status.

## Required checks

- `git diff --check`
- `rg -n "new Substitute(Invitation|AgentBehavior|AgentRuntimeTrace)|Substitute(Invitation|AgentBehavior)Repository|SubstituteAgentRuntimeTraceSink" templates/ai-first-saas-starter/backend/src/main/java`
- Targeted rendered backend tests for invitation, agent behavior, prompt assembly, runtime trace sink, and real-model fail-closed/smoke guards as applicable.

## Done criteria

- Normal generated runtime does not wire Akka component-backed invitation/agent behavior stores or Akka component-backed trace sinks as completed defaults.
- Unit-test fakes/adapters remain test-scoped.
- Governed Akka Agent runtime path and provider fail-closed behavior are preserved.
- Checks pass and changes are committed.

## Commit message

- `full-core-smb: bind durable agent and invitation seams`
