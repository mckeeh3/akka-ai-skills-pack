# TASK-FCSR-05-001: Complete managed-agent foundation readiness

## Objective

Close the managed-agent foundation readiness gap for AgentDefinition, governed prompts, skills, references, manifests, tool boundaries, proposals, approvals, activation/rollback, and traces at the selected full-core scope.

## Required reads

- full-core readiness gap contract from `TASK-FCSR-01-001`
- `app-description/10-capabilities/05-managed-agent-foundation.md`
- `app-description/12-workstreams/workstream-expertise/*.md`
- `app-description/20-behavior/flows/04-managed-agent-foundation-flow.md`
- `src/main/java/ai/first/application/foundation/agent/**`
- `src/main/java/ai/first/application/coreapp/agentadmin/**`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- managed-agent backend/frontend tests

## Skills

- `akka-agent-behavior-profiles`
- `akka-agent-governed-documents`
- `akka-agent-prompt-governance`
- `akka-agent-skill-governance`
- `akka-agent-reference-governance`
- `akka-agent-tool-boundaries`
- `akka-agent-seed-documents`
- `akka-agent-work-trace`

## In scope

- Fill missing managed-agent lifecycle/governance coverage identified by the gap contract.
- Ensure seeded defaults, active managed config resolution, prompt assembly, manifests, readSkill/readReferenceDoc, ToolPermissionBoundary, and trace flows are runtime-backed and tested.
- Ensure authority expansion requires approval and prompt/skill/reference text cannot grant authority.

## Out of scope

- Implementing every future behavior editor or evaluator agent unless required by the gap contract.

## Expected outputs

- Backend/frontend/doc/test updates for managed-agent foundation readiness.

## Required checks

- `git diff --check`
- focused managed-agent backend tests
- frontend tests/typecheck/build if UI changes
- broader `mvn test` when shared agent foundation changes

## Done criteria

- Managed-agent foundation meets selected full-core scope through real backend/runtime paths or precise blockers are recorded.
- Changes and queue update are committed.

## Commit message

`full-core-ready: complete managed agents`
