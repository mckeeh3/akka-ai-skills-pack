# App-description Intent-Compiler Migration

## Purpose

Reconstruct the secure multi-tenant AI-first SaaS core starter's root `app-description/` and related spec traceability so they follow the current intent compiler model.

This mini-project resolves the gap between early rough workstream descriptions and the newer intent-compiler doctrine by using two temporary source inputs:

1. the existing legacy `app-description/` tree; and
2. the current root starter implementation under `src/`, `frontend/`, tests, docs, and active specs.

## Target scope

This mini-project targets app-facing root assets:

- `app-description/**`
- `specs/**`
- `src/main/java/ai/first/**` and `src/test/java/ai/first/**` for inventory/reference only unless a later task explicitly repairs traceability docs
- `frontend/**` for inventory/reference only unless a later task explicitly repairs traceability docs
- root `docs/**` only when needed to link the reconstructed current-intent graph

## Current-intent target shape

Use the installed/current intent compiler graph as the target shape:

```text
app-description/
  app.md
  global/
    actors/
    roles/
    policies/
    surfaces/
    agents/
    tools/
    traces/
  domains/<domain>/
    domain.md
    capabilities/
    data-state/
    workstreams/<workstream>/
      workstream.md
      access.md
      behavior.md
      surfaces/
      agents/
      tools/
      policies/
      traces/
      tests/
      realization/
```

The initial domain for this mini-project is the secure multi-tenant AI-first SaaS core starter. Use a stable domain id such as `core-starter` unless the selected task establishes a better id from current-intent docs.

## Foundation documentation rule

Do not duplicate reusable foundation implementation doctrine in root `app-description/`. Reference selected foundation concepts/capabilities from skills-pack/current docs and describe only this app's commitments, bindings, surfaces, behaviors, tests, and realization mappings.

## Temporary archive rule

The legacy `app-description/` archive is migration-only provenance. During migration, tasks may cite it in inventories and migration notes. Before this mini-project is complete:

- active `app-description/` content must not depend on archived legacy files;
- active specs/backlogs/tasks must not instruct future implementers to use archived legacy content as product authority;
- the temporary archive must be removed or reduced to non-authoritative migration notes outside active content; and
- verification must prove no active current-intent node relies on the archive.

## Non-goals

- Do not implement new runtime features in the same task that reconstructs descriptions.
- Do not change skills-pack source; this is root app-facing work.
- Do not create a separate downstream app-description root.
- Do not preserve obsolete/historical phrasing in active current-intent artifacts.
- Do not claim runtime readiness just because a description exists.
- Do not use this migration to add a domain-specific CRM/billing/procurement workstream.

## Execution model

Execute one queued task per fresh harness context. Each task must:

1. mark exactly one task `in-progress` before edits;
2. execute only that task;
3. run required checks or block with a precise reason;
4. mark `done` only when checks and done criteria pass;
5. create one focused git commit containing the task changes and queue update;
6. report the next runnable task.

## Read order for future task sessions

1. `AGENTS.md`
2. `.agents/skills/docs/intent-compiler.md`
3. `.agents/skills/docs/current-intent-model.md`
4. `.agents/skills/docs/intent-to-realization-flow.md`
5. `.agents/skills/docs/intent-compiler-skill-contracts.md`
6. `specs/app-description-intent-compiler-migration/README.md`
7. `specs/app-description-intent-compiler-migration/conversation-capture.md`
8. `specs/app-description-intent-compiler-migration/pending-tasks.md`
9. selected sprint/backlog/task brief
10. task-specific source/app-description/spec files

## Sprint sequence

1. Sprint 01: Temporary source baseline and implementation inventory.
2. Sprint 02: Current-intent graph reconstruction.
3. Sprint 03: Spec/backlog/readiness reconciliation.
4. Sprint 04: Archive scrub and migration verification.

## Done state

This mini-project is complete when:

- `app-description/` follows the current intent compiler graph shape for the secure multi-tenant AI-first SaaS core starter;
- the graph captures the core starter's app objective, selected foundation commitments, global actors/roles/policies/surfaces/agents/tools/traces, core domain capabilities, data/state responsibilities, workstream bindings, tests, and realization mappings;
- the five core workstreams are represented as current-state workstream bindings rather than legacy rough notes;
- root specs/readiness/backlog references point to the new current-intent graph where applicable;
- active content references skills-pack foundation docs for reusable doctrine instead of duplicating it;
- temporary traceability to archived legacy docs has been scrubbed from active content;
- the temporary legacy archive has been removed or clearly excluded from active authority; and
- a terminal verification task confirms there are no material migration gaps, or appends a follow-up bounded task group plus a new terminal verification task.

## Open concerns

- The first implementation task must choose the exact temporary archive location and avoid overwriting uncommitted work.
- Some current implementation details may conflict with legacy description claims. Future tasks must either update current intent, record a pending question, or queue runtime repair work rather than silently preserving drift.
- Existing full-core readiness tasks may already rely on old `app-description/` paths. Reconciliation must update those references or clearly stage follow-up work.
