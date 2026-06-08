# Task: Harden agent, invitation, and trace Akka bindings

## Objective

Ensure invitation state/outbox, governed agent behavior records, and agent runtime traces are always bound to real Akka-backed implementations in normal runtime, with test doubles confined to tests.

## Required reads

- AGENTS.md
- skills/README.md
- docs/ai-first-saas-application-architecture.md
- specs/real-akka-runtime-replacement/README.md
- specs/real-akka-runtime-replacement/non-akka-runtime-seam-map.md
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/AkkaInvitationRepository.java
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/DurableInvitationRepositoryEntity.java
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AkkaAgentBehaviorRepository.java
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/DurableAgentBehaviorRepositoryEntity.java
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AkkaAgentRuntimeTraceSink.java
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeService.java
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/StarterSecurityComponents.java

## Skills

- akka-key-value-entity
- akka-event-sourced-entity when lifecycle history gaps are found
- akka-view
- akka-agent-work-trace
- akka-agent-seed-documents

## In scope

- Remove normal-runtime `LocalDemoInvitationRepository`, `LocalDemoAgentBehaviorRepository`, and `LocalDemoAgentRuntimeTraceSink` binding options.
- Ensure default constructors do not instantiate local-demo trace sinks.
- Ensure seed import, prompt assembly, readSkill/readReferenceDoc, model invocation traces, and invitation outbox use Akka-backed state/sinks.
- Move any required local substitute to test source only.

## Out of scope

- Do not weaken provider fail-closed behavior for missing OpenAI/WorkOS/Resend configuration.

## Expected outputs

- backend source updates for hard Akka bindings
- tests for seed import, invitation lifecycle/outbox, prompt/reference/skill loads, trace persistence/search, provider fail-closed behavior
- queue update

## Required checks

- `git diff --check`
- rendered backend tests for invitation, agent behavior seed loader, AgentRuntimeService, AgentRuntimeTraceSink, WorkstreamRuntimeAgent
- `rg -n "LocalDemo(Invitation|AgentBehavior|AgentRuntimeTrace)|FailClosedAgentRuntimeTrace|new LocalDemoAgentRuntimeTrace|new LocalDemoInvitation|new LocalDemoAgentBehavior" templates/ai-first-saas-starter/backend/src/main/java`

## Done criteria

- Normal runtime invitation, agent behavior, and agent trace paths are Akka-backed.
- Main-source default constructors cannot silently select local-demo/test storage.
- Tests use explicit test-only doubles or Akka test/runtime paths.
- Changes and queue update are committed.

## Commit message

`runtime: harden agent invitation trace Akka bindings`
