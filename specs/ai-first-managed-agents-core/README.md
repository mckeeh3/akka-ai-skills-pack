# AI-first Managed Agents Core Migration

## Purpose

Make configuration-driven Akka agents a first-class core application feature for generated AI-first SaaS apps, at the same architectural level as workstreams and structured surfaces.

The core rule is:

> If an agent answers a user in the workstream shell, it must already be configuration-driven. If an admin edits how agents behave, that belongs to Agent Admin.

This migration turns the existing governed-agent doctrine and partial starter implementation into the mandatory core runtime substrate:

```text
AuthContext + capability
→ active AgentDefinition
→ active prompt / compact skill manifest / compact reference manifest
→ active ToolPermissionBoundary + model policy
→ runtime tool list resolution
→ Akka Agent effects().tools(runtimeTools)
→ readSkill/readReferenceDoc and capability tools under backend enforcement
→ prompt/skill/reference/tool/work traces
```

## Scope

Primary implementation targets:

- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/agentfoundation/`
- `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/`
- starter seed resources under `templates/ai-first-saas-starter/backend/src/main/resources/agent-behavior-seeds/`
- related starter README, docs, skills, and validation scripts

## Non-goals

- Do not implement the full Agent Admin editing UI in the runtime-foundation tasks. Agent Admin manages this substrate later.
- Do not allow direct provider/service calls to count as normal runtime agent execution.
- Do not store arbitrary Java class names in tenant-managed agent configuration.
- Do not treat prompt or skill text as an authorization mechanism.

## Relationship to existing migrations

Builds on:

- `specs/governed-runtime-agent-foundation/`
- `specs/workstream-akka-agent-runtime/`
- `specs/workstream-expertise-foundation/`
- `specs/core-app-full-stack-readiness/`

This migration closes the remaining runtime gap: active managed configuration must determine the Akka tool list used by real workstream agents.
