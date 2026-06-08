# Sprint 04: Docs, Examples, and UI Guidance Alignment

## Objective

Rewrite or remove active docs/examples that still teach traditional screens, pages, resource APIs, or purchase-request mechanics as the canonical generated-app interpretation.

## Scope

Primary files include:

- `docs/intent-driven-usage-flow.md`
- `docs/prd-to-akka-flow.md`
- `docs/app-description-skills-plan-backlog.md`
- `docs/app-description-end-to-end-workflow-example.md`
- `docs/domain-workstream-prd-structure.md`
- `docs/web-ui-api-contract-patterns.md`
- `docs/web-ui-style-guide.md`
- `docs/web-ui-ux-patterns.md`
- `docs/web-ui-frontend-decomposition.md`
- `docs/examples/purchase-request-*.md`
- `docs/examples/purchase-request-app-description/**`
- `docs/examples/README.md`
- other docs/examples discovered by the inventory

## Ordered work areas

1. Rewrite concise usage-flow docs around the current requirements-to-workstream sequence.
2. Rewrite or remove stale app-description planning docs.
3. Replace purchase-request-centered workflow examples with current workstream/surface/capability examples, or demote them to mechanics-only.
4. Rebalance UI/API docs to workstream/surface API envelopes and functional-agent rail/shell language.
5. Update references from skills/docs to removed or renamed docs.

## Acceptance criteria

- Active docs no longer present conventional page/resource/CRUD UX as the default generated app model.
- Purchase-request/shopping-cart examples are not preferred by intake or planning guidance except as mechanics-only references.
- Link/reference checks for touched files pass by search.
- `git diff --check` passes.
