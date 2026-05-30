# Backlog 04: Docs, Examples, and UI Guidance

## Goal

Trim, rewrite, or demote docs/examples/UI guidance so active documentation teaches the workstream-shell architecture without stale traditional SaaS bias.

## Suggested harness task breakdown

1. Rewrite concise usage-flow and app-description workflow docs.
2. Rewrite/remove stale app-description skill-plan docs and update references.
3. Rewrite domain workstream and web UI/API/UX docs for structured-surface-first semantics.
4. Demote or remove purchase-request/shopping-cart conventional examples from active intake references.
5. Run docs link/reference cleanup for removed or renamed files.

## Required checks

- `git diff --check`
- `rg -n "app-description-skills-plan-backlog|purchase-request|/api/<resource>|navigation structure|primary nav|user-list|user-edit|search/detail navigation|form submission|CRUD|page-first" docs skills`
- Manual inspection of intentional remaining hits.

## Acceptance criteria

- Active docs and examples support current pack goals directly.
- Legacy mechanics examples cannot be mistaken for canonical generated-app architecture.
