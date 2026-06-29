# Implementation Alignment Sequence

## Phase 1: Evidence inventory

Inventory backend, frontend, tests, resources, active specs, and runtime-validation state against refreshed source-alignment expectations. Produce `source-evidence-inventory.md` with classification by workstream and gap type.

## Phase 2: Runtime-validation corpus scaffold

Create or update `specs/runtime-validation/**` with reusable local-dev environment, personas, data setup placeholders, and scenario skeletons matching refreshed workstream expectations. This is scenario authoring only; execution comes later.

## Phase 3: Workstream alignment passes

For each foundation workstream:

1. read refreshed app-description workstream files;
2. compare realization/source-alignment entries to actual source/frontend/tests/specs/runtime-validation artifacts;
3. update lifecycle/source-alignment evidence when alignment is proven;
4. mark partially aligned or blocked when evidence is incomplete;
5. append exact build/compile/remediation/runtime-validation tasks when implementation or validation work remains.

## Phase 4: Consolidated build/compile queue

Create or update a consolidated follow-up queue that can be executed one task per fresh context. Tasks must carry the vertical workstream contract and required checks.

## Phase 5: Terminal verification

Verify the mini-project done state. Do not claim `runtime-ready` unless real runtime-validation evidence exists. If material alignment gaps remain, append bounded follow-up tasks and a new terminal verification task.
