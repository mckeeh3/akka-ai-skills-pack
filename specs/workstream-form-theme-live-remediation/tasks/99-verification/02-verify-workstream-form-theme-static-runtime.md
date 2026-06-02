# Task Brief: Verify Workstream Form Theme Static Runtime Completion

## Objective

Verify the follow-up static-runtime synchronization and close the workstream form theme live remediation mini-project only if the source, committed Akka-hosted static assets, template assets, tests, and guidance all satisfy the original screenshot complaint.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/workstream-form-theme-live-remediation/README.md`
- `specs/workstream-form-theme-live-remediation/conversation-capture.md`
- `specs/workstream-form-theme-live-remediation/pending-tasks.md`
- `specs/workstream-form-theme-live-remediation/tasks/99-verification/02-verify-workstream-form-theme-static-runtime.md`
- `user-settings-surface.png`
- changed frontend/static/docs/skills files

## In scope

- Verify source frontend and committed Akka-hosted static resources both include styled structured-surface controls and live named-theme preview.
- Verify docs/skills guidance still rejects native/default controls and delayed theme preview.
- Append any further bounded follow-up only if a material gap remains.

## Out of scope

- Implementing discovered runtime gaps during verification, except queue/status/finding edits.

## Skills

- none; repository verification task

## Required checks

- `git diff --check`
- frontend checks if frontend or static assets changed since the previous task's checks
- targeted source/static/docs searches for detail-edit styling and immediate theme preview coverage

## Done criteria

- Screenshot class of default-looking structured-surface fields is addressed in source and committed served static assets.
- Theme selection previews immediately in source and committed served static assets.
- Guidance would reject recurrence.
- If complete, no follow-up tasks are appended.
- If incomplete, bounded follow-up tasks plus a new terminal verification task are appended.
- Verification changes and queue update are committed.

## Commit message convention

- `ui-theme: verify workstream form theme static runtime`
