# Core PRD Workstream Reconciliation

## Purpose

Verify and reconcile whether the older module-sequenced PRD inputs under `docs/examples/core-ai-first-saas-input/` are covered by the completed five-core workstream v0 mini-projects and starter template runtime.

The goal is not to reimplement the five workstreams. The goal is to compare source PRD intent against the completed workstream contracts, capability inventories, runtime/template assets, and current newer workstream-oriented guidance, then record any gaps or supersession decisions as bounded follow-up tasks.

## Background

The six five-core mini-projects were completed from the shared five-core v0 contract and workstream-specific contracts, not by explicitly ingesting `docs/examples/core-ai-first-saas-input/*.md`. The user asked whether those PRDs had been used as input. Repository search showed no direct references in the six mini-projects or starter implementation artifacts.

`docs/examples/core-ai-first-saas-input/README.md` still describes those files as canonical example input documents and names `10-canonical-core-app-prd.md` as the hard PRD target for full core generation. Separately, `docs/skills-pack-user-guide.md` now describes the newer workstream-oriented core-app domain PRD set as the preferred full-core rollout input, while treating `core-ai-first-saas-input/` as an older module-sequenced end-to-end sample.

## Scope

- Compare the older core PRD input set against the completed five-core v0 workstream artifacts.
- Produce a traceability matrix from PRD modules to workstreams/capabilities/runtime surfaces.
- Identify covered, partially covered, intentionally deferred, superseded, or conflicting requirements.
- Update local docs or append follow-up mini-project tasks only when the reconciliation finds actionable gaps.

## Non-goals

- Do not implement runtime code in the initial reconciliation task.
- Do not reopen completed five-core workstream queues unless a concrete gap is found.
- Do not treat module-sequenced PRDs as automatically more authoritative than the current workstream-oriented doctrine without documenting a decision.
- Do not perform a whole-repository review outside the PRD-to-workstream reconciliation scope.

## Affected repository areas

- `docs/examples/core-ai-first-saas-input/`
- `docs/examples/ai-first-saas-core-app-domain/`
- `docs/skills-pack-user-guide.md`
- `specs/five-core-workstreams-v0-plan/`
- the five completed workstream mini-projects under `specs/*-workstream-v0/`
- `templates/ai-first-saas-starter/` only if reconciliation produces implementation follow-up tasks

## Execution model

Execute one task per fresh harness session. The first task is a non-runtime reconciliation/report task. If it finds runtime, docs, or input-source gaps, append bounded follow-up tasks before the terminal verification task.

## Read order for future task sessions

1. `AGENTS.md`
2. `skills/README.md`
3. this mini-project's `README.md`
4. this mini-project's `conversation-capture.md`
5. this mini-project's `pending-tasks.md`
6. selected sprint/backlog/task brief
7. only the exact PRD/spec/source files listed by the task

## Done state

This mini-project is complete when:

- the older `core-ai-first-saas-input/` PRD set has been mapped against the completed five-core v0 workstream contracts/capability inventories;
- each meaningful requirement is classified as covered, partially covered, intentionally deferred, superseded by newer workstream-oriented input, or a gap;
- actionable gaps are appended as bounded tasks or explicitly routed to a separate mini-project;
- docs clearly state how the older module PRDs relate to the newer workstream-oriented core input path;
- terminal verification confirms no unresolved reconciliation work remains.
