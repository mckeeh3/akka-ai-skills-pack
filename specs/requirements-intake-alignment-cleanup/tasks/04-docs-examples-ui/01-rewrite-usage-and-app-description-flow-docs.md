# TASK-RIAC-04-001: Rewrite usage and app-description flow docs

## Objective

Rewrite concise usage and app-description workflow docs that still teach old sequencing or purchase-request-centered examples.

## Required reads

- AGENTS.md
- skills/README.md
- specs/requirements-intake-alignment-cleanup/content-inventory.md
- specs/requirements-intake-alignment-cleanup/prune-and-rewrite-criteria.md
- specs/requirements-intake-alignment-cleanup/sprints/04-docs-examples-ui-sprint.md
- docs/intent-driven-usage-flow.md
- docs/prd-to-akka-flow.md
- docs/app-description-end-to-end-workflow-example.md
- docs/requirements-to-workstream-development-process.md
- docs/examples/requirements-to-workstream-mini-example.md

## In scope

- Make usage flow start with secure AI-first SaaS default and requirements-to-workstream sequence.
- Replace or heavily rewrite purchase-request-centered workflow examples.
- Update references to current examples.

## Checks

- `git diff --check`
- Reference search for removed/renamed docs if applicable.

## Done criteria

- These docs no longer present purchase-request or component-first flow as canonical.
- Queue updated and committed.
