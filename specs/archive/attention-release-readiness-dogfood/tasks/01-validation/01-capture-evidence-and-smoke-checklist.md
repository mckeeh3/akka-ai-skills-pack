# TASK-ARD-01-001: Capture dogfood evidence and smoke checklist

## Objective

Convert the user’s manual dogfood observation into a concrete release-readiness smoke checklist.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/attention-release-readiness-dogfood/README.md`
- `specs/attention-release-readiness-dogfood/dogfood-observations.md`
- `specs/attention-release-readiness-dogfood/conversation-capture.md`
- `specs/attention-release-readiness-dogfood/tasks/01-validation/01-capture-evidence-and-smoke-checklist.md`
- `specs/workstream-attention-backbone-v1/pending-tasks.md`
- `specs/workstream-attention-event-producers-v2/pending-tasks.md`

## In scope

- Create a focused smoke checklist artifact under this mini-project.
- Include checks for left rail, My Account, dashboards/surfaces, producer updates, lifecycle, denial/redaction, provider/fail-closed, and frontend-only authority guardrails.
- Map each checklist item to automated, manual, or not-applicable validation.

## Out of scope

- Running full validation.
- Implementing fixes.

## Required checks

- `git diff --check`

## Done criteria

- Checklist is specific enough for a fresh harness/browser validation session.
- Task changes and queue update are committed.

## Commit message

`attention-dogfood: capture smoke checklist`
