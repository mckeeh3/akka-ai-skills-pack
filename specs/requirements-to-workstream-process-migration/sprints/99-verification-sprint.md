# Sprint 99: Verification Loop

## Objective

Verify the migration has realigned the skills pack's app input processing path. If gaps remain, append new bounded tasks before a new terminal verification task.

## Scope

Review all source areas touched or identified by this mini-project:
- `AGENTS.md`
- `pack/AGENTS.md`
- `skills/README.md`
- relevant intake, app-description, PRD/spec/backlog, queue, and implementation-routing skills
- canonical docs and examples
- packaging manifest/export files

## Acceptance criteria

- Broad app input cannot reasonably be processed through a stale CRUD-first, page-first, chatbot-bolt-on, event-only, or component-first path.
- Requirements-to-workstream process is described in canonical docs and enforced by routing/planning skills.
- Description-first and direct PRD planning paths both preserve the vertical chain.
- Generated pending tasks preserve workstream, attention, dashboard, surface, capability, API/exposure, Akka substrate, autonomous task, notification, trace, auth, and test context.
- Any remaining gaps are captured as new pending tasks and a new verification task is appended.
