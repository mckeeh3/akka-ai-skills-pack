# Task Brief: Harden Form and Theme Style Guidance

## Objective

Update canonical web UI style guidance and focused web UI skills/checklists so generated structured-surface forms cannot be accepted with default/native-looking controls and named theme selection must preview immediately.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/workstream-form-theme-live-remediation/README.md`
- `specs/workstream-form-theme-live-remediation/conversation-capture.md`
- `specs/workstream-form-theme-live-remediation/pending-tasks.md`
- `specs/workstream-form-theme-live-remediation/sprints/02-style-guide-hardening-sprint.md`
- `specs/workstream-form-theme-live-remediation/backlog/02-style-guide-hardening-build-backlog.md`
- `specs/workstream-form-theme-live-remediation/tasks/02-style-guide-hardening/01-harden-form-theme-style-guidance.md`
- `docs/web-ui-style-guide.md`
- relevant web UI skills found by search for form/theme guidance

## In scope

- Add explicit structured-surface form control style requirements.
- Add explicit rejection of browser-default/native-looking inputs/selects/textareas in generated workstream surfaces.
- Add named-theme behavior requirement: selection previews immediately; save/confirm persists through governed backend path.
- Update quality checklist/testing guidance if needed.

## Out of scope

- Runtime frontend code changes.

## Skills

- `akka-web-ui-ux-design`
- `akka-web-ui-forms-validation`
- `akka-web-ui-accessibility-responsive`
- `akka-web-ui-testing`

## Required checks

- `git diff --check`
- targeted search confirming touched docs/skills mention structured-surface form controls and immediate named-theme preview

## Done criteria

- Style guide/guidance would reject the screenshot UI as unacceptable.
- Guidance requires immediate named-theme preview plus governed save persistence.
- Changes and queue update are committed.

## Commit message convention

- `ui-theme: harden workstream form theme guidance`
