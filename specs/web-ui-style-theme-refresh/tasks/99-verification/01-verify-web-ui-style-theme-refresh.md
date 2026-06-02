# Task Brief: Verify Web UI Style Theme Refresh Completion

## Objective

Verify the completed task group against the mini-project done state, record the result, and append bounded follow-up tasks plus a new terminal verification task if material gaps remain.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/web-ui-style-theme-refresh/README.md`
- `specs/web-ui-style-theme-refresh/conversation-capture.md`
- `specs/web-ui-style-theme-refresh/pending-tasks.md`
- `specs/web-ui-style-theme-refresh/sprints/*.md`
- `specs/web-ui-style-theme-refresh/backlog/*.md`
- `specs/web-ui-style-theme-refresh/tasks/**/*.md`
- `web-ui-high-level-style-guide.md`
- `docs/web-ui-style-guide.md`
- affected docs/skills/examples/frontend/template files from completed tasks

## In scope

- Compare completed work against README done state and conversation decisions.
- Verify the current task group/sprints are complete.
- Search for stale contradictions in docs, skills, examples, frontend, and templates within mini-project scope.
- Run or review required checks for changed assets.
- Update queue with completion notes or append follow-up tasks and a new terminal verification task.

## Out of scope

- Whole-repository visual design review beyond this style/theme refresh.
- Implementing discovered gaps in the verification task itself, except small queue/status edits.

## Expected outputs

- Updated `specs/web-ui-style-theme-refresh/pending-tasks.md`
- Optional verification notes file if useful
- Follow-up task briefs if material gaps remain

## Skills

- none; repository verification task

## Required checks

- `git diff --check`
- `rg -n "orange|coral|warm near-black|system with light|light/dark/system|mode preference|atlas-ops-supervisory-console" docs skills frontend templates specs/web-ui-style-theme-refresh web-ui-high-level-style-guide.md` and review results for stale contradictions within scope
- frontend checks needed to validate changed frontend assets, as recorded by implementation tasks

## Done criteria

- Task group/sprint goals have been compared against completed work.
- Mini-project done state has been compared against completed work.
- Unresolved questions/blockers have been reviewed.
- If complete, completion is recorded with no new required work.
- If incomplete, new bounded tasks are appended before a new terminal verification task.
- Queue is updated and verification changes are committed.

## Commit message convention

- `ui-theme: verify style theme refresh`
