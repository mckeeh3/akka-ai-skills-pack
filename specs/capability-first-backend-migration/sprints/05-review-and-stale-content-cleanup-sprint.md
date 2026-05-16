# Sprint 5: Review and Stale Content Cleanup

## Sprint goal

Review prior sprint progress, identify stale content introduced or left behind by the AI-first and capability-first migrations, and remove or refine content that now misroutes future agents.

## Scope

- Review doctrine, routing, skills, examples, and docs for contradictions.
- Identify CRUD-first, endpoint-first, entity-first, or agent-tool-only assumptions that bypass capability modeling.
- Identify prompt-only security language that should become mechanical authorization/audit guidance.
- Identify duplicate or superseded docs/skills/examples.
- Convert findings into cleanup tasks and complete small safe cleanup edits.

## Expected outputs

- Review report under this migration directory.
- Cleanup backlog updates.
- Focused edits removing/refining stale content where safe.

## Acceptance behavior

The pack should not simultaneously tell agents to use capability-first decomposition and elsewhere route broad product requests straight to CRUD/component implementation without qualification.

## Defer list

- Final release validation; that belongs to Sprint 6.
