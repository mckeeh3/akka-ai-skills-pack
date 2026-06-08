# Sprint 01: Audit and Target Model

## Objective

Inventory current requirements/input processing guidance and define a concise target process contract for the new workstream-attention-dashboard-surface-capability-autonomous-task development flow.

## Scope

Review only content that affects how user input, PRDs, app descriptions, solution plans, backlogs, pending questions, pending tasks, or examples are processed into generated-app work.

Primary areas:
- `skills/README.md`
- `skills/ai-first-saas/SKILL.md`
- `skills/agent-workstream-apps/SKILL.md`
- `skills/app-description-input-normalization/SKILL.md`
- `skills/app-description-intake-router/SKILL.md`
- `skills/akka-solution-decomposition/SKILL.md`
- `skills/akka-prd-to-specs-backlog/SKILL.md`
- related planning docs and queue docs
- existing migration specs that may overlap

## Work areas

1. Create an audit inventory of current intake/planning artifacts.
2. Classify each artifact as aligned, partially aligned, stale/drift risk, or not relevant.
3. Identify exact stale patterns: CRUD-first, page-first, component-first, event-only, chatbot-bolt-on, capability-without-workstream, autonomous-task omission.
4. Define the target process contract in a compact doc or notes file.
5. Recommend the precise follow-up edits for later sprints.

## Acceptance criteria

- Audit findings are written under this mini-project.
- The target process contract is explicit enough to guide downstream doc/skill edits.
- Follow-up sprints have enough detail to avoid a broad unfocused rewrite.
- No installable skill behavior is changed in this sprint except possibly adding source planning docs under `specs/`.
