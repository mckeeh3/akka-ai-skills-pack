# Backlog: Sprint 03 Remaining Foundation Guidance Alignment

## Goal

Remove remaining stale style/theme guidance discovered during verification so downstream generated-app foundation and question-generation paths do not reintroduce mode-first UI settings or obsolete style ids.

## Implementation notes

- Keep the change bounded to text guidance; do not redesign foundation identity/settings behavior beyond naming the browser UI preference as selected named theme id.
- Preserve the rule that theme choice is a user preference and never authorization.
- Use `docs/web-ui-style-guide.md` as the source for theme ids and pending-question option names.

## Suggested harness task breakdown

1. Align remaining foundation and pending-question guidance references.

## Dependencies

- TASK-WUTR-99-001 verification finding.

## Required checks

- `git diff --check`
- Focused stale-language searches for obsolete style ids and browser mode-first settings terms.

## Acceptance criteria

- Stale `atlas-ops-supervisory-console` option text is replaced with `ai-first-workstream-enterprise` or equivalent canonical wording.
- Browser preference guidance uses `preferredThemeId` / named theme selection instead of `preferredColorMode` or `uiMode` light/dark.
- Remaining `mode` search hits are reviewed as unrelated governed agent runtime language or otherwise addressed.
