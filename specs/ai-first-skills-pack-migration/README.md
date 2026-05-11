# AI-First Skills Pack Migration Plan

This planning package coordinates the migration of this repository from a general Akka application skills pack into an **AI-first SaaS Akka skills pack**.

The migration is planning-only until individual pending tasks are executed in fresh Pi sessions. The temporary concept-development material under `specs/ai-first-skills-pack-migration/archive/inbox/` is reference input, not authoritative pack content.

## Execution model

- Execute one task per fresh Pi session.
- Use `pending-tasks.md` as the durable queue.
- Start with Sprint 1 tasks. Later sprint task briefs should be materialized or adjusted after earlier discoveries.
- Do not edit implementation/source guidance outside the selected task scope.
- Each task must end by making a git commit containing only that task's intended changes and the corresponding `pending-tasks.md` status update.
- Record the commit hash in the task's `notes` before marking it `done` when possible.
- Preserve task IDs; supersede obsolete tasks instead of renumbering.

## Read order

1. `AGENTS.md`
2. `skills/README.md`
3. `specs/ai-first-skills-pack-migration/archive/inbox/docs/ai-first-saas-coding-agent-framework.md`
4. This file
5. The relevant sprint spec under `sprints/`
6. The matching backlog under `backlog/`
7. The task brief under `tasks/` when present
8. `pending-tasks.md`

## Sprint sequence

1. `sprints/01-architectural-pivot-and-doctrine-sprint.md` — declare the AI-first target architecture and promote canonical doctrine.
2. `sprints/02-ai-first-routing-skill-family-sprint.md` — add top-level AI-first routing and focused companion skill skeletons.
3. `sprints/03-app-description-ai-first-refactor-sprint.md` — refactor app-description intake and maintained description layers around the agentic operating model.
4. `sprints/04-prd-spec-backlog-intake-refactor-sprint.md` — refactor PRD/spec/change-request planning around AI-first interpretation before Akka component selection.
5. `sprints/05-substrate-implementation-skill-reframing-sprint.md` — reframe existing Akka component implementation skills as the AI-first runtime substrate.
6. `sprints/06-worked-example-and-inbox-cleanup-sprint.md` — convert DCA concept material into a worked example and retire temporary inbox material.

## Backlog alignment

Each sprint has a matching backlog:

- `backlog/01-architectural-pivot-and-doctrine-build-backlog.md`
- `backlog/02-ai-first-routing-skill-family-build-backlog.md`
- `backlog/03-app-description-ai-first-refactor-build-backlog.md`
- `backlog/04-prd-spec-backlog-intake-refactor-build-backlog.md`
- `backlog/05-substrate-implementation-skill-reframing-build-backlog.md`
- `backlog/06-worked-example-and-inbox-cleanup-build-backlog.md`

Only Sprint 1 has detailed physical task briefs initially. Later task briefs should be created or refined after Sprint 1 discoveries.

## Target architecture summary

The skills pack should default to AI-first SaaS applications where agents perform operational work and humans supervise, approve, correct, teach, audit, and own outcomes. Akka components remain the implementation substrate: agents, workflows, entities, views, consumers, timed actions, endpoints, and React/Vite/TypeScript web UI delivery.
