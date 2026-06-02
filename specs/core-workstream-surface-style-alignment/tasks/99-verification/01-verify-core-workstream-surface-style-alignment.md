# Task Brief: Verify Core Workstream Surface Style Alignment

## Objective

Verify this mini-project against its done state and append bounded follow-up tasks plus a new terminal verification task if material gaps remain.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/core-workstream-surface-style-alignment/README.md`
- `specs/core-workstream-surface-style-alignment/conversation-capture.md`
- `specs/core-workstream-surface-style-alignment/pending-tasks.md`
- `specs/core-workstream-surface-style-alignment/sprints/*.md`
- `specs/core-workstream-surface-style-alignment/backlog/*.md`
- `specs/core-workstream-surface-style-alignment/tasks/**/*.md`
- `docs/web-ui-style-guide.md`
- `docs/examples/ai-first-saas-core-app-domain/**/*.md`
- `docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/surface-contracts/*.md`
- `docs/examples/ai-first-saas-seed-app-description/app-description/55-ui/style-guide.md`

## In scope

- Compare completed work against README done state.
- Run stale contradiction searches.
- Review unresolved blockers/questions.
- Append follow-up tasks and a new terminal verification task if needed.

## Out of scope

- Implementing discovered gaps in the verification task, except queue/status/finding edits.

## Expected outputs

- Updated queue.
- Optional verification notes.
- Follow-up task briefs if needed.

## Skills

- none; repository verification task

## Required checks

- `git diff --check`
- `rg -n "preferredColorMode|uiMode|light/dark/system|system mode|atlas-ops-supervisory-console|orange|coral|warm near-black" docs/examples/ai-first-saas-core-app-domain docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams docs/examples/ai-first-saas-seed-app-description/app-description/55-ui/style-guide.md specs/core-workstream-surface-style-alignment || true`
- `rg -n "ai-first-workstream-enterprise|preferredThemeId|aurora-light|cobalt-light|obsidian-dark|midnight-dark" docs/examples/ai-first-saas-core-app-domain docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams docs/examples/ai-first-saas-seed-app-description/app-description/55-ui/style-guide.md`

## Done criteria

- Sprint goals and mini-project done state have been compared against completed work.
- If complete, completion is recorded with no new required work.
- If incomplete, new bounded tasks are appended before a new terminal verification task.
- Changes and queue update are committed.

## Commit message convention

- `ui-theme: verify core workstream surface style alignment`
