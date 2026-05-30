# TASK-RIAC-02-002: Align readiness, generation, and app-description orchestration

## Objective

Update readiness/generation/orchestration skills and any focused companion references identified by the previous task so generation readiness reflects the full workstream/surface/capability/UI/runtime doctrine.

## Required reads

- AGENTS.md
- skills/README.md
- specs/requirements-intake-alignment-cleanup/content-inventory.md
- specs/requirements-intake-alignment-cleanup/sprints/02-description-intake-skills-sprint.md
- skills/app-descriptions/SKILL.md
- skills/app-description-readiness-assessment/SKILL.md
- skills/app-generate-app/SKILL.md
- relevant companion app-description skills named by TASK-RIAC-02-001 notes

## In scope

- Remove or rewrite stale readiness/generation shortcuts.
- Ensure `12-workstreams/`, `55-ui/`, managed agents, workstream UI, and runtime validation are required when applicable.
- Ensure legacy `frontend/src/screens/**` or page-first examples are not allowed as generated SaaS realization paths.

## Out of scope

- Do not edit broader docs unless required to fix direct references.

## Checks

- `git diff --check`
- Focused stale-term search over app-description and generation skills.

## Done criteria

- App-description generation path is aligned end to end.
- Queue updated and committed.
