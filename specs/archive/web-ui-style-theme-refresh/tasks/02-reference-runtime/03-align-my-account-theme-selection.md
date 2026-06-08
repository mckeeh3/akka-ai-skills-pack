# Task Brief: Align My Account Theme Selection

## Objective

Implement or align the scoped reference/starter My Account theme selection path so users choose one available named theme and the UI changes to that theme.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/web-ui-style-theme-refresh/README.md`
- `specs/web-ui-style-theme-refresh/conversation-capture.md`
- `specs/web-ui-style-theme-refresh/sprints/02-reference-runtime-sprint.md`
- `specs/web-ui-style-theme-refresh/backlog/02-reference-runtime-build-backlog.md`
- `specs/web-ui-style-theme-refresh/tasks/02-reference-runtime/03-align-my-account-theme-selection.md`
- `docs/web-ui-style-guide.md`
- `templates/ai-first-saas-starter/app-description/app-description/55-ui/style-guide.md`
- `frontend/src/main.tsx`
- `templates/ai-first-saas-starter/frontend/src/main.tsx`
- My Account/settings frontend/API files identified by search

## In scope

- Replace mode-first frontend preference behavior with named-theme selection where this repository's reference/starter UI owns that behavior.
- Provide a simple available-theme selector in or through the My Account/settings workstream surface when existing structures support it.
- Apply selected theme through a document/root theme attribute and semantic CSS tokens.
- Preserve accessibility and keyboard behavior.
- If durable backend settings persistence is required but too large or absent, append a bounded follow-up task instead of overclaiming completion.

## Out of scope

- Large backend settings redesign unless already necessary and bounded in existing My Account settings code.
- New generic preferences UI outside the My Account workstream.
- Mocking durable runtime behavior as complete.

## Expected outputs

- Updated reference/starter frontend theme selection behavior or documented bounded limitation plus follow-up task
- Updated tests/checks where existing frontend test structure supports them
- Updated `specs/web-ui-style-theme-refresh/pending-tasks.md`

## Skills

- `akka-web-ui-state-rendering`
- `akka-web-ui-forms-validation` if settings form behavior is edited
- `akka-web-ui-accessibility-responsive`
- `akka-web-ui-testing`

## Required checks

- `git diff --check`
- frontend type/build checks documented by repository guidance for changed frontend assets, or a clear blocker if prerequisites are unavailable
- manual/source inspection confirming theme selection maps to named theme ids and document/root theme application

## Done criteria

- User-facing selector model is named-theme based, not mode-first.
- Choosing an available theme changes the UI theme at the scoped reference/starter level.
- Persistence/runtime scope is truthful and, if incomplete, follow-up work is appended before verification.
- Queue is updated and task changes are committed.

## Commit message convention

- `ui-theme: align my account theme selection`
