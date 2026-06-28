# Conversation Capture: Skills-Pack Comprehensive Review

## Accepted concern

The `skills-pack/` has gone through many significant revisions and refinements. The current risk is not merely individual bad files, but accumulated conflicts between skills, docs, templates, examples, installed mirror content, and process guidance.

## Accepted structure and process baseline

The review should start from a common understanding of the canonical app structure and process:

- root repository = runnable SaaS Foundation App baseline;
- `skills-pack/` = harness guidance and reusable reference assets;
- installed `.agents/skills/**` = support-library output, not target app source;
- `app-description/**` = living current-intent graph;
- workstreams are the operational unit for AI-first SaaS behavior;
- feature-bearing structure preserves:

```text
worker -> execution harness -> actor adapter -> governed tool -> capability -> Akka implementation
```

- app development uses the three-phase loop:

```text
Interview / intent reconciliation
  -> Build / compile / implement
  -> Manual runtime test / reconciliation
```

- runtime completion requires real local Akka/API/UI/agent evidence at the stated scope; mock/demo/model-less paths cannot close user-visible behavior.

## Accepted review approach

Create a mini-project under `specs/skills-pack-comprehensive-review/` with:

- a guide document capturing the doctrine spine and review rubric;
- a file inventory containing every tracked `skills-pack/**` file, its status, and review notes;
- a one-file-at-a-time subagent execution model;
- commits after each completed file review;
- terminal verification after all file entries reach terminal status.

## Important maintainer nuance

The comprehensive review should include every tracked `skills-pack/**` file in the inventory, but should distinguish source-authoritative assets from installed-output mirror assets. Direct doctrine changes belong in source assets (`skills-pack/skills/**`, `docs/**`, `references/**`, `examples/**`, `templates/**`, `tools/**`, `pack/**`, `install-skills.sh`) and should be propagated/validated through installer checks rather than hand-editing installed mirror copies as source truth.
