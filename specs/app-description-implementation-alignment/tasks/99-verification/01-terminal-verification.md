# TASK-ADIA-99-001: Terminal implementation alignment verification

## Summary

Verify whether the implementation alignment mini-project done state has been achieved. If material gaps remain, append bounded follow-up tasks and a new terminal verification task.

## Required reads

- `specs/app-description-implementation-alignment/README.md`
- `conversation-capture.md`
- `alignment-sequence.md`
- `pending-tasks.md`
- `source-evidence-inventory.md`
- `runtime-validation-corpus-plan.md`
- `implementation-follow-up-queue.md`
- refreshed `app-description/**`
- `.agents/skills/docs/app-description-source-alignment.md`
- `.agents/skills/docs/runtime-validation.md`
- `.agents/skills/docs/pending-task-queue.md`

## Skills

- `app-description-readiness-assessment`
- `akka-pending-task-queue-maintenance`
- `akka-runtime-feature-verification`

## Expected outputs

- `terminal-verification.md`
- Queue completion notes or appended follow-up tasks plus new terminal verification task.

## Required checks

- `git diff --check`
- graph/source-alignment/runtime-validation/queue proof commands.

## Done criteria

- Done state is verified or remaining gaps are appended as bounded tasks.
- Workstream alignment posture is recorded without overclaiming runtime readiness.
- Changes and queue update are committed.

## Vertical workstream contract

Terminal verification across all five foundation workstreams; non-attention reason verification; role-specific dashboard / surface all foundation surfaces verified for evidence posture; surface graph node/action edge verified; governed-tool id/type/exposure verified; actor adapter/source verified; confirmation/approval behavior and idempotency/transaction/result behavior verified; capability or foundation scope all core-starter capabilities; AuthContext / roles / tenant scope verified; API / frontend / realtime path verified; audit/work trace expectation verifies source-alignment, runtime-validation, trace, and queue evidence; validation path `git diff --check` plus proof commands.
