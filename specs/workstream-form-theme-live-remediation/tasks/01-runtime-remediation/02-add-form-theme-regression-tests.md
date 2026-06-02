# Task Brief: Add Form Styling and Theme Preview Regression Tests

## Objective

Add or update frontend tests/source checks that prove the detail-edit settings surface has styled controls and live theme preview behavior.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/workstream-form-theme-live-remediation/README.md`
- `specs/workstream-form-theme-live-remediation/conversation-capture.md`
- `specs/workstream-form-theme-live-remediation/pending-tasks.md`
- `specs/workstream-form-theme-live-remediation/sprints/01-runtime-remediation-sprint.md`
- `specs/workstream-form-theme-live-remediation/backlog/01-runtime-remediation-build-backlog.md`
- `specs/workstream-form-theme-live-remediation/tasks/01-runtime-remediation/02-add-form-theme-regression-tests.md`
- frontend test files relevant to workstream shell/surfaces/theme behavior
- matching starter template frontend test files

## In scope

- Add tests for selecting `preferredThemeId` and observing immediate `document.documentElement.dataset.theme` change.
- Add tests or source assertions for detail-edit form classes/control styling hooks.
- Mirror tests in reference and starter template where test suites are duplicated.

## Out of scope

- Visual screenshot testing unless already available.

## Skills

- `akka-web-ui-testing`
- `akka-web-ui-accessibility-responsive`

## Required checks

- `git diff --check`
- `cd frontend && npm test && npm run typecheck && npm run build`
- `cd templates/ai-first-saas-starter/frontend && npm test && npm run typecheck && npm run build`

## Done criteria

- Tests fail if theme selection no longer previews immediately.
- Tests/source checks guard the structured-surface form styling path.
- Changes and queue update are committed.

## Commit message convention

- `ui-theme: add workstream form theme regression tests`
