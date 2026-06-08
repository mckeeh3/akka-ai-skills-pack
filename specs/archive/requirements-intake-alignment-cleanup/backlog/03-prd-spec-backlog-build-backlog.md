# Backlog 03: PRD, Spec, Backlog, and Queue Skills

## Goal

Make direct PRD/spec/backlog planning and queue flows preserve the current architecture and block stale task shapes.

## Suggested harness task breakdown

1. Rewrite solution decomposition and PRD-to-specs planning.
2. Rewrite revised-PRD/change-request/slice-to-backlog flows.
3. Rewrite backlog/task-brief/pending-question/pending-task/do-next flows.
4. Run a focused planning-queue consistency pass and append follow-ups if gaps remain.

## Required checks

- `git diff --check`
- `rg -n "User Admin workstream v0|purchase-request-prd|purchase-request-solution|CRUD screen|page|component-only|ReferenceDocument|readReferenceDoc|ReferenceLoadTrace" skills/akka-*-to-* skills/akka-solution-decomposition skills/akka-prd-to-specs-backlog skills/akka-pending-* skills/akka-do-next-*`
- Manual inspection of intentional remaining hits.

## Acceptance criteria

- No planning skill creates generated-SaaS tasks that are just pages, CRUD screens, components, dashboards, or endpoints without vertical context.
- Managed-agent reference governance is consistently represented where relevant.
