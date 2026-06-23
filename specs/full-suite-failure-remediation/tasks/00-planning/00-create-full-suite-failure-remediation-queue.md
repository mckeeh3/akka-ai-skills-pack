# TASK-FSFR-00-001: Create Full Suite Failure Remediation planning scaffold

## Purpose

Create a durable root-app mini-project and queue for pre-existing full-suite failures.

## Required reads

- `AGENTS.md`
- `specs/AGENTS.md`
- current conversation context
- `specs/workstream-chat-tool-catalog-expansion/verification-notes.md`

## Expected outputs

- `specs/full-suite-failure-remediation/README.md`
- `specs/full-suite-failure-remediation/conversation-capture.md`
- `specs/full-suite-failure-remediation/pending-tasks.md`
- `specs/full-suite-failure-remediation/sprints/*.md`
- `specs/full-suite-failure-remediation/backlog/*.md`
- `specs/full-suite-failure-remediation/tasks/**/*.md`

## Required checks

- `git diff --check -- specs/full-suite-failure-remediation`

## Done criteria

- Scope, done state, non-goals, implementation order, and verification loop are explicit.
- First non-done task is runnable without guessing.
- Changes and queue update are committed.
