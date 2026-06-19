# TASK-FCSR-08-003: Run live model-provider workstream-agent smoke

## Objective

Validate live model-backed workstream agent execution through the governed Akka Agent runtime path.

## Blocker

Do not start until backend-only model-provider credentials/configuration, approved runtime tool-boundary grants, and a safe test tenant/runtime setup are supplied.

## Required reads

- `AGENTS.md`
- `specs/full-core-saas-readiness/full-core-readiness-verification.md`
- `specs/full-core-saas-readiness/managed-agent-foundation-validation.md`
- `specs/full-core-saas-readiness/audit-governance-validation.md`
- `src/main/java/ai/first/application/foundation/agent/**`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`

## Skills

- `akka-agent-component`
- `akka-agent-tools`
- `akka-agent-work-trace`
- `akka-agent-testing`

## Expected outputs

- Live model-provider workstream-agent/worker smoke evidence or precise blocker refresh.
- Queue/readiness updates.

## Required checks

- `git diff --check`
- Focused managed-agent/workstream tests plus the live-provider smoke command/runbook.

## Done criteria

- Governed Akka Agent runtime path invokes a real configured provider with active AgentDefinition, prompt, manifests, ToolPermissionBoundary, loader tools, runtime tools, and durable traces.
- Changes and queue update are committed.
