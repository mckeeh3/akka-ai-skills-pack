# TASK-WSAGENT-04-001: Update docs and completion gates for Akka Agent-backed v0

## Objective

Update repository and starter guidance so future harness sessions cannot treat service-only provider calls, deterministic seams, or frontend-only behavior as a fully implemented workstream agent.

## Required reads

- AGENTS.md
- skills/README.md
- docs/agent-workstream-application-architecture.md
- docs/minimum-ai-first-saas-app.md
- docs/skills-pack-user-guide.md
- docs/agent-coverage-matrix.md
- README.md
- pack/AGENTS.md
- templates/ai-first-saas-starter/README.md
- specs/production-ready-five-core-v0/pending-tasks.md
- specs/workstream-akka-agent-runtime/README.md

## Expected outputs

- Update docs/guidance that define v0 workstream completion to require an Akka Agent component in the normal runtime path.
- Update starter README smoke checklist to include confirmation of Akka Agent-backed execution, real provider configuration, trace ids, and secret boundary.
- Update agent coverage/routing references if they imply the starter workstream runtime is complete without an Akka Agent component.
- Add a short retrospective note explaining this failure mode and the regression guard that now prevents it.
- If any older pending task wording still allows deterministic/demo/model-less normal runtime for workstream responses, supersede it or point to this queue.

## Required checks

- `git diff --check`
- `rg -n "Akka Agent|real model|model-backed|deterministic|mock|fixture|provider smoke|workstream agent" AGENTS.md pack/AGENTS.md skills/README.md docs README.md templates/ai-first-saas-starter specs/production-ready-five-core-v0 specs/workstream-akka-agent-runtime`

## Done criteria

- Documentation and completion gates align with the implemented Akka Agent-backed runtime path.
- The retrospective makes the root cause explicit enough for future agents.
- Task status is updated in `specs/workstream-akka-agent-runtime/pending-tasks.md`.
- A focused git commit exists with message `workstream-agent: update completion gates`.
