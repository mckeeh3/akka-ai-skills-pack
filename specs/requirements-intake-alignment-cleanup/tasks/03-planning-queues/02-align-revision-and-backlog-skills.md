# TASK-RIAC-03-002: Align revised PRD, change request, and backlog skills

## Objective

Update revised-PRD, change-request, slice-to-backlog, backlog-to-pending, and backlog-item task-brief skills so deltas and tasks preserve current architecture context and reject stale task shapes.

## Required reads

- AGENTS.md
- skills/README.md
- specs/requirements-intake-alignment-cleanup/content-inventory.md
- specs/requirements-intake-alignment-cleanup/sprints/03-prd-spec-backlog-sprint.md
- skills/akka-revised-prd-reconciliation/SKILL.md
- skills/akka-change-request-to-spec-update/SKILL.md
- skills/akka-slice-spec-to-backlog/SKILL.md
- skills/akka-backlog-to-pending-tasks/SKILL.md
- skills/akka-backlog-item-to-task-brief/SKILL.md

## In scope

- Add scope/readiness/five-core checks where missing.
- Ensure reference governance/readReferenceDoc/ReferenceLoadTrace coverage.
- Ensure CRUD/page/component-only backlog items are repaired or blocked.

## Checks

- `git diff --check`
- Focused stale-term search over edited skills.

## Done criteria

- Revision/change/backlog flows are compatible with current workstream architecture.
- Queue updated and committed.
