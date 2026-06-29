# TASK-ADIA-01-002: Author runtime-validation corpus scaffold

## Summary

Create or update `specs/runtime-validation/**` scenario scaffolding for the five refreshed foundation workstreams. This task authors scenarios only; it does not execute them or claim pass results.

## Required reads

- `AGENTS.md`
- `.agents/skills/docs/runtime-validation.md`
- `.agents/skills/docs/runtime-validation-task-authoring.md`
- `specs/app-description-implementation-alignment/runtime-validation-corpus-plan.md`
- `specs/app-description-implementation-alignment/source-evidence-inventory.md`
- refreshed workstream `tests/coverage.md` and `realization/source-alignment.md` files

## Skills

- `akka-runtime-feature-verification`
- `app-description-test-specification`

## Expected outputs

- `specs/runtime-validation/README.md`
- reusable environment/persona/data-setup docs
- at least one scenario skeleton per foundation workstream
- updated `runtime-validation-corpus-plan.md`

## Required checks

- `git diff --check`
- `find specs/runtime-validation -type f | sort`

## Done criteria

- Runtime-validation scenario skeletons identify real runtime path, setup prerequisites, steps, expected evidence, and failure classifications.
- No scenario claims execution success without a run record.
- Changes and queue update are committed.

## Vertical workstream contract

All five foundation workstreams; attention category scenario-dependent; role-specific dashboard / surface scenario files name target surfaces; surface graph node/action edge documented per scenario; governed-tool id/type/exposure documented per scenario; actor adapter/source documented; confirmation/approval behavior and idempotency/transaction/result behavior documented as expected results; capability or foundation scope all core-starter capabilities; AuthContext / roles / tenant scope documented per persona; API / frontend / realtime path documented; audit/work trace expectation documented; validation path `git diff --check` and scenario file proof.
