# Backlog: Core PRD to Workstream Reconciliation

## Goal

Turn the question "were the old core PRDs used?" into durable traceability and actionable follow-up.

## Suggested harness task breakdown

1. Create the mini-project planning scaffold.
2. Compare `docs/examples/core-ai-first-saas-input/*.md` against the five completed workstream contracts and capability inventories; write a traceability report and gap list.
3. Apply documentation or queue follow-up updates if the traceability report finds source-of-truth ambiguity or bounded gaps.
4. Verify reconciliation completion.

## Required checks

- `git diff --check`
- targeted `rg` checks for key PRD/module/workstream references
- docs/test/runtime checks only for follow-up tasks that modify docs or runtime assets

## Acceptance criteria

- Every module PRD is either mapped, deferred, superseded, or turned into follow-up work.
- No broad implementation work is started before traceability exists.
