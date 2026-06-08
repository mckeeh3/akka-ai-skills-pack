# Task Brief: Align Remaining Foundation Theme Guidance

## Objective

Update the remaining foundation and pending-question guidance found by verification so browser UI theme preference is named-theme based and obsolete style ids are no longer offered.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/web-ui-style-theme-refresh/README.md`
- `specs/web-ui-style-theme-refresh/conversation-capture.md`
- `specs/web-ui-style-theme-refresh/pending-tasks.md`
- `specs/web-ui-style-theme-refresh/sprints/03-remaining-guidance-sprint.md`
- `specs/web-ui-style-theme-refresh/backlog/03-remaining-guidance-build-backlog.md`
- `specs/web-ui-style-theme-refresh/tasks/03-remaining-guidance/01-align-remaining-foundation-theme-guidance.md`
- `docs/web-ui-style-guide.md`
- `docs/workstream-ui-reference-architecture.md`
- `docs/core-saas-identity-tenancy-admin.md`
- `skills/core-saas-foundation/SKILL.md`
- `skills/akka-pending-question-generation/SKILL.md`

## In scope

- Replace browser UI preference examples such as `preferredColorMode` and `uiMode` light/dark with named-theme preference semantics, using `preferredThemeId` and the canonical four initial theme ids where concrete examples are useful.
- Replace obsolete style option id `atlas-ops-supervisory-console` with `ai-first-workstream-enterprise` in pending-question guidance.
- Preserve theme choice as a presentation preference that never controls authorization, policy, audit, or capability visibility.
- Review remaining `mode` search hits and keep only unrelated governed agent runtime mode references or explicitly non-browser-theme uses.

## Out of scope

- Editing frontend or backend runtime code; implementation was handled by Sprint 02.
- Whole-repository identity/security doctrine rewrite beyond stale theme preference wording.
- Changing generated-app authorization or audit requirements.

## Expected outputs

- Updated `docs/workstream-ui-reference-architecture.md`
- Updated `docs/core-saas-identity-tenancy-admin.md`
- Updated `skills/core-saas-foundation/SKILL.md`
- Updated `skills/akka-pending-question-generation/SKILL.md`
- Updated `specs/web-ui-style-theme-refresh/pending-tasks.md`

## Skills

- none; repository docs/skill guidance alignment task

## Required checks

- `git diff --check`
- `rg -n "atlas-ops-supervisory-console|preferredColorMode|uiMode|mode preference|light/dark/system|system with light|Color mode|ModePreference|MY_ACCOUNT_INVALID_COLOR_MODE" docs skills frontend templates specs/web-ui-style-theme-refresh web-ui-high-level-style-guide.md` and review results for stale browser-theme contradictions

## Done criteria

- Foundation UI settings guidance uses named theme selection rather than mode-first UI settings.
- Pending-question generation offers the canonical style option id, not the obsolete Atlas style id.
- Remaining search hits are either removed or documented as unrelated/non-contradictory.
- Queue is updated and task changes are committed.

## Commit message convention

- `ui-theme: align remaining foundation theme guidance`
