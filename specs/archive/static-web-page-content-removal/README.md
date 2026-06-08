# Static Web Page Content Removal Plan

This planning package removes or revises skills-pack content that teaches agents to implement hand-authored static web pages, static documentation pages, or simple packaged HTML/CSS examples.

The retained target is **React/Vite/TypeScript web apps hosted by Akka**. Akka may still serve frontend production build output from `src/main/resources/static-resources/`, but the pack should no longer present static pages as a useful implementation path for browser UI work.

## User intent

- Remove distracting static web page guidance.
- Keep the skills pack focused on React/Vite/TypeScript application delivery.
- Preserve only the static-resource details needed to host generated frontend build assets.
- Queue the work as separate fresh-harness tasks.
- Each task must make a git commit containing only that task's intended changes and its queue-status update.

## Execution model

- Execute one task per fresh harness session.
- Start from `pending-tasks.md`.
- Mark the selected task `in-progress` before editing.
- Run the task's required checks.
- Commit the task changes before marking the task `done` when feasible; if the queue status update is included in the task commit, record the commit message rather than trying to amend a self-referential hash.
- Do not combine adjacent tasks.

## Sprint sequence

1. `sprints/01-remove-static-web-page-guidance-sprint.md` — remove static web page guidance, examples, routing references, docs, tests, and packaging entries while preserving React/Vite/TypeScript hosting guidance.

## Backlog alignment

- `backlog/01-remove-static-web-page-guidance-build-backlog.md`

## Task briefs

Physical task briefs live under:

- `tasks/01-remove-static-web-page-guidance/`

## Non-goals

- Do not remove React/Vite/TypeScript web UI app guidance.
- Do not remove Akka static-resource hosting semantics that are required to serve generated frontend build assets.
- Do not introduce a new static-page replacement pattern.
- Do not rewrite unrelated AI-first SaaS doctrine except where it explicitly mentions static web page implementation.
