# Task Brief: Align User Admin and Agent Admin Surface Style

## Objective

Update User Admin and Agent Admin core workstream README files so their required surfaces include concise `ai-first-workstream-enterprise` appearance expectations.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/core-workstream-surface-style-alignment/README.md`
- `specs/core-workstream-surface-style-alignment/conversation-capture.md`
- `specs/core-workstream-surface-style-alignment/pending-tasks.md`
- `specs/core-workstream-surface-style-alignment/sprints/01-core-domain-surfaces-sprint.md`
- `specs/core-workstream-surface-style-alignment/backlog/01-core-domain-surfaces-build-backlog.md`
- `specs/core-workstream-surface-style-alignment/tasks/01-core-domain-surfaces/02-align-admin-workstream-surfaces.md`
- `docs/web-ui-style-guide.md`
- `docs/examples/ai-first-saas-core-app-domain/user-admin-workstream/README.md`
- `docs/examples/ai-first-saas-core-app-domain/agent-admin-workstream/README.md`

## In scope

- Add surface style notes for User Admin dashboards, lists, details, invite forms, access-review cards, admin audit summaries, decision/system-message surfaces.
- Add surface style notes for Agent Admin dashboards, catalogs, details, prompt/skill/reference version surfaces, manifest/tool-boundary diffs, behavior proposals, prompt assembly traces, and system-message surfaces.
- Preserve capability/action mappings.

## Out of scope

- Editing My Account, Audit/Trace, or Governance/Policy docs.
- Frontend/backend implementation.

## Expected outputs

- Updated User Admin and Agent Admin workstream README files.
- Updated queue status.

## Skills

- `app-description-ui`
- `ai-first-saas-ui-surfaces`

## Required checks

- `git diff --check`
- focused stale style/theme search over touched files

## Done criteria

- User Admin and Agent Admin surfaces are no longer generic dashboard/table descriptions only; they include enterprise workstream style expectations.
- Touched docs contain no stale mode-first or old default style contradictions.
- Changes and queue update are committed.

## Commit message convention

- `ui-theme: align admin workstream surface style`
