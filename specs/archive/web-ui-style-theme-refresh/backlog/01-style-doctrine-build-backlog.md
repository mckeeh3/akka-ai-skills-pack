# Backlog: Sprint 01 Style Doctrine and Named Theme Guidance

## Goal

Make the replacement style and named-theme model authoritative before implementation/reference updates.

## Implementation notes

- Treat `web-ui-high-level-style-guide.md` as the source visual reference.
- Preserve AI-first workstream UI anatomy and capability-backed actions.
- Use named themes as the user-facing preference model.
- Keep old orange/coral style assumptions out of the replacement default unless they are explicitly documented as non-default/custom examples.

## Suggested harness task breakdown

1. Update canonical style guide and theme contract.
2. Align web UI skills and quality checks with the named-theme contract.

## Dependencies

- Mini-project scaffold.

## Required checks

- `git diff --check`
- Targeted text search for stale old-default/mode-first language in touched docs and skills.

## Acceptance criteria

- The canonical docs define the replacement style, the named-theme contract, and four initial themes.
- Skills and checklists make the selected style/theme a required input for UI implementation.
- The first future implementation task can update seed/starter assets without inventing theme semantics.
