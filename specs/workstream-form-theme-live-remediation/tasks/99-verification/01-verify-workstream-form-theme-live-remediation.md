# Task Brief: Verify Workstream Form Theme Live Remediation

## Objective

Verify remediation against the screenshot complaint and mini-project done state; append follow-up tasks if gaps remain.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/workstream-form-theme-live-remediation/README.md`
- `specs/workstream-form-theme-live-remediation/conversation-capture.md`
- `specs/workstream-form-theme-live-remediation/pending-tasks.md`
- `specs/workstream-form-theme-live-remediation/sprints/*.md`
- `specs/workstream-form-theme-live-remediation/backlog/*.md`
- `specs/workstream-form-theme-live-remediation/tasks/**/*.md`
- `user-settings-surface.png`
- changed frontend/docs/skills files

## In scope

- Verify runtime behavior and docs/guidance hardening.
- Run checks or review recorded checks.
- Append follow-up tasks plus new terminal verification task if incomplete.

## Out of scope

- Implementing discovered runtime gaps during verification, except queue/status/finding edits.

## Skills

- none; repository verification task

## Required checks

- `git diff --check`
- frontend checks from runtime tasks if frontend changed since last test
- targeted source/docs searches for detail-edit styling and immediate theme preview coverage

## Done criteria

- Screenshot class of default-looking structured-surface fields is addressed.
- Theme selection previews immediately.
- Guidance would reject recurrence.
- If complete, no follow-up tasks are appended.
- If incomplete, bounded follow-up tasks plus a new terminal verification task are appended.
- Changes and queue update are committed.

## Commit message convention

- `ui-theme: verify workstream form theme remediation`
