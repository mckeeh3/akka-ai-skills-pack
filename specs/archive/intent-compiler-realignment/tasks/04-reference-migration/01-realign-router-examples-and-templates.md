# Task: Realign Router Skills, Examples, and Templates

## Objective

Update high-level routing skills, examples, and templates that still teach or assume the old intent-processing model.

## Required reads

- `AGENTS.md`
- `skills-pack/AGENTS.md`
- `specs/intent-compiler-realignment/README.md`
- `specs/intent-compiler-realignment/intent-processing-inventory.md`
- `specs/intent-compiler-realignment/sprints/04-reference-migration-and-validation-sprint.md`
- canonical intent compiler docs

## In scope

- `ai-first-saas`, `agent-workstream-apps`, `capability-first-backend`, `core-saas-foundation`, `akka-solution-decomposition` if inventory marks them affected.
- Examples/templates that reference old app-description structure or flat global-only surfaces/agents/tools.

## Out of scope

- Deep implementation skill rewrites.

## Expected outputs

- updated router skills/examples/templates
- updated `pending-tasks.md`

## Required checks

- `git diff --check`
- `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run`

## Done criteria

- High-level skills route users to the intent compiler model.
- Examples/templates match the new directory/file structure or are marked legacy.
