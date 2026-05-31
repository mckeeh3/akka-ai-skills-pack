# TASK-WGGT-03-002: Update incremental change planning skills

## Objective

Update revised-PRD/change/backlog flows so incremental inputs reconcile against existing workstream graphs and governed-tools.

## Required reads

- AGENTS.md
- skills/README.md
- specs/workstream-graph-governed-tools-architecture/README.md
- skills/akka-revised-prd-reconciliation/SKILL.md
- skills/akka-change-request-to-spec-update/SKILL.md
- skills/akka-slice-spec-to-backlog/SKILL.md
- skills/akka-backlog-to-pending-tasks/SKILL.md
- skills/akka-backlog-item-to-task-brief/SKILL.md

## In scope

- Existing graph impact checks: workstream, role dashboard, attention item, surface node/edge, governed-tool, internal agent delegation, workstream expertise.
- Backlog/task repair rules when graph/tool context is missing.
- Preserve no-fresh-parallel-app behavior for scaffolded/existing apps.

## Checks

- `git diff --check`
- Focused term search over edited skills.

## Done criteria

- Incremental changes cannot bypass existing graph reconciliation.
- Queue updated and committed.
