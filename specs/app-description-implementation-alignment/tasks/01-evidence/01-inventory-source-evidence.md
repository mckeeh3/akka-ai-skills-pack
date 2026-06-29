# TASK-ADIA-01-001: Inventory current source/runtime evidence

## Summary

Replace `source-evidence-inventory.md` placeholders with concrete evidence from backend, frontend, tests, resources, specs, and runtime-validation state.

## Required reads

- `AGENTS.md`
- `app-description/AGENTS.md`
- `specs/app-description-refresh/terminal-verification.md`
- `specs/app-description-implementation-alignment/README.md`
- `specs/app-description-implementation-alignment/source-evidence-inventory.md`
- `.agents/skills/docs/app-description-source-alignment.md`
- `.agents/skills/docs/intent-to-realization-flow.md`

## Skills

- `app-description-change-impact`
- `akka-solution-decomposition`

## Expected outputs

- Concrete `source-evidence-inventory.md` with evidence and gap classifications by workstream.
- Queue status update.

## Required checks

- `git diff --check`
- Evidence proof commands over `src/main/java/ai/first`, `src/test/java/ai/first`, `frontend`, `app-description`, and `specs/runtime-validation` if present.

## Done criteria

- Future workstream alignment tasks can proceed without guessing source/test/frontend evidence.
- Changes and queue update are committed.

## Vertical workstream contract

Cross-workstream evidence inventory; non-attention reason source-alignment audit; role-specific dashboard / surface all foundation surfaces inspected as evidence only; surface graph node/action edge inspected not implemented; governed-tool id/type/exposure inspected from app-description/source evidence; actor adapter/source inspected; confirmation/approval behavior and idempotency/transaction/result behavior inspected; capability or foundation scope all core-starter capabilities; AuthContext / roles / tenant scope inspected; API / frontend / realtime path inventoried; audit/work trace expectation inventoried; validation path `git diff --check` plus evidence proof.
