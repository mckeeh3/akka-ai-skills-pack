# Specs and Task Queue Guidance

This directory is part of the default app-realization mode. Edit here for PRDs, solution specs, backlogs, pending questions, pending tasks, slice specs, and implementation task briefs for the runnable SaaS app.

## Scope

- Planning and execution artifacts under `specs/**`.
- Domain-specific specs should live under `specs/extensions/<domain>/` when possible.

## Rules

- When using `specs/**/pending-tasks.md`, execute only one queued task per fresh harness session.
- Read the selected pending-task entry and task brief before editing implementation files.
- Mark the selected task `in-progress` before implementation edits and `done` only after checks pass.
- Keep task queue updates and implementation changes consistent.
- Preserve explicit blockers, answered-question reconciliation, acceptance criteria, validation commands, and residual risks.
- Do not edit `skills-pack/**` from this mode unless the user explicitly requests skills-pack maintenance.

## Checks

Use the smallest checks that prove consistency for the touched artifacts, commonly:

```bash
git diff --check
```
