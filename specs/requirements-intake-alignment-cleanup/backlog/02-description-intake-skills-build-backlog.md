# Backlog 02: Description Intake Skills

## Goal

Make app-description intake and generation-adjacent skills enforce the current requirements-to-workstream architecture.

## Suggested harness task breakdown

1. Rewrite bootstrap/input/router/change-impact skills for five-core starter, AI-first starter core references, starter routing, and surface/workstream impact.
2. Rewrite readiness/generation/app-descriptions orchestration and focused companion references discovered by the first task.
3. Run a focused consistency pass across app-description skills and update the queue with any remaining bounded follow-ups.

## Required checks

- `git diff --check`
- `rg -n "User Admin workstream v0|purchase-request|generic chatbot|CRUD screens|page-first|frontend/src/screens" skills/app-description* skills/app-descriptions skills/app-generate-app`
- Manual inspection of intentional remaining hits.

## Acceptance criteria

- App-description flow starts from functional agents/workstreams, structured surfaces, capabilities, security, UI, and runtime validation.
- Minimum starter consistently means five core workstream v0.
