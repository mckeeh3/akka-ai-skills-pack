# TASK-STARTER-04-001: Implement governed agent records and seed import

## Purpose

Implement governed agent behavior records and idempotent seed import for the starter app.

## Required reads

- `specs/core-app-full-stack-readiness/agent-admin-component-api-slice.md`
- `specs/governed-runtime-agent-foundation/minimal-governed-runtime-agent-reference-slice.md`
- `docs/agent-runtime-invocation-pattern.md`
- `skills/akka-agent-seed-documents/SKILL.md`
- `skills/akka-agent-behavior-profiles/SKILL.md`
- `skills/akka-agent-prompt-governance/SKILL.md`
- `skills/akka-agent-skill-governance/SKILL.md`
- `skills/akka-agent-tool-boundaries/SKILL.md`

## Expected outputs

- Governed agent record components/views.
- Seed bundle/import implementation and tests.

## Done criteria

- Seed import creates approved active defaults on first install/tenant bootstrap.
- Re-import is idempotent and does not overwrite tenant customizations incorrectly.
- Required checks pass, queue status is updated, and changes are committed.
