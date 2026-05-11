# Task Brief: Routing Map AI-First Entry Update

## Task ID

`TASK-01-003`

## Objective

Update the skill routing map to introduce AI-first SaaS as the high-level product interpretation path for future skill work.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `docs/ai-first-saas-application-architecture.md`
- `specs/ai-first-skills-pack-migration/sprints/01-architectural-pivot-and-doctrine-sprint.md`
- `specs/ai-first-skills-pack-migration/backlog/01-architectural-pivot-and-doctrine-build-backlog.md`

## Dependencies

- `TASK-01-001` complete.
- Prefer `TASK-01-002` complete first so routing wording matches repo guidance.

## Scope

Update `skills/README.md` to add an AI-first SaaS routing section that explains:

- when high-level inputs should be interpreted as AI-first SaaS
- that `ai-first-saas` is a planned/new top-level skill family to be created in Sprint 2
- how AI-first interpretation relates to existing `app-descriptions`, `akka-solution-decomposition`, and `akka-prd-to-specs-backlog`
- that existing Stage 3 component skills are implementation substrate skills

Because the actual `skills/ai-first-saas` skill may not exist yet, avoid links that falsely imply it exists unless it is clearly described as planned.

## Non-goals

- Do not create `skills/ai-first-saas` yet.
- Do not deeply edit every component-family section.
- Do not update app-description or decomposition skills yet.

## Expected outputs

- Updated `skills/README.md`

## Required checks

- Search for broken direct links to non-existent AI-first skill files.
- Ensure the routing map still supports the current installed pack until Sprint 2 creates new skills.

## Done criteria

- Future agents can infer that high-level product inputs now need AI-first interpretation before CRUD/component decomposition.
- The update is concise and does not bloat the routing map unnecessarily.
- `pending-tasks.md` is updated to mark `TASK-01-003` done after completion.
