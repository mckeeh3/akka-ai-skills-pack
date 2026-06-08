# TASK-RIAC-04-004: Demote or remove legacy examples from active intake guidance

## Objective

Ensure purchase-request, shopping-cart, standalone static UI, and other conventional examples cannot be mistaken for canonical generated AI-first SaaS architecture.

## Required reads

- AGENTS.md
- skills/README.md
- specs/requirements-intake-alignment-cleanup/content-inventory.md
- specs/requirements-intake-alignment-cleanup/prune-and-rewrite-criteria.md
- docs/examples/README.md
- docs/examples/purchase-request-prd.md
- docs/examples/purchase-request-solution-plan.md if present
- docs/examples/purchase-request-app-description/README.md
- relevant skills/docs that still reference these examples

## In scope

- Add mechanics-only warnings where examples remain useful.
- Remove active preferred-example references from intake/planning skills/docs.
- Delete or rewrite examples classified as non-contributing by the inventory, after updating references.

## Checks

- `git diff --check`
- `rg -n "purchase-request|ShoppingCart|shopping-cart|static UI|standalone" skills docs | head -200` with intentional remaining hits reviewed.

## Done criteria

- Legacy examples are demoted, rewritten, or removed according to criteria.
- Queue updated and committed.
