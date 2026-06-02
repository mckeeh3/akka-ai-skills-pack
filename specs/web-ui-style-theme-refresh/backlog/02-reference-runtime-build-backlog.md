# Backlog: Sprint 02 Seed and Starter Reference Runtime Alignment

## Goal

Apply the replacement canonical style and named themes to the repository's seed app-description and starter/reference frontend assets.

## Implementation notes

- The seed app-description should describe selected style, available themes, default theme, and My Account theme-selection behavior.
- The starter/reference CSS should define named theme token bundles and keep component anatomy stable across themes.
- Theme changes should be applied by setting a theme identifier on the document/root, not by hard-coding style decisions in TypeScript.
- If My Account theme selection is durable at runtime, it must use the real settings/API path at the stated scope. If persistence is out of scope, narrow the task explicitly and append a follow-up.

## Suggested harness task breakdown

1. Update seed app-description style and My Account theme contracts.
2. Update starter/reference frontend theme tokens and visual styling.
3. Implement or align simple My Account theme selection behavior at the scoped runtime/reference level.

## Dependencies

- Sprint 01 canonical doctrine and skill alignment.

## Required checks

- `git diff --check`
- Frontend type/build checks where frontend source changes.
- Search checks for old default style assumptions in seed/starter assets.

## Acceptance criteria

- Seed app-description and starter/reference assets use the same named-theme model.
- The four initial themes are represented in docs and frontend tokens.
- My Account theme selection is simple and does not overclaim durability.
