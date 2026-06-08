# TASK-ADICM-04-002: Terminal migration verification

## Purpose

Verify that the current task group and overall mini-project done state are complete. If material gaps remain, append bounded follow-up tasks and a new terminal verification task.

## Required reads

- `AGENTS.md`
- `.agents/skills/docs/intent-compiler.md`
- `.agents/skills/docs/current-intent-model.md`
- `.agents/skills/docs/intent-to-realization-flow.md`
- `.agents/skills/docs/intent-compiler-skill-contracts.md`
- `.agents/skills/docs/pending-task-queue.md`
- `specs/app-description-intent-compiler-migration/README.md`
- `specs/app-description-intent-compiler-migration/conversation-capture.md`
- `specs/app-description-intent-compiler-migration/source-inventory.md`
- `specs/app-description-intent-compiler-migration/pending-tasks.md`
- all sprint/backlog/task files in this mini-project
- final `app-description/` graph
- active specs/readiness docs touched by the migration

## Expected outputs

- `specs/app-description-intent-compiler-migration/migration-verification.md`
- queue updates marking verification done only if done state is achieved
- if gaps remain: appended bounded tasks plus a new terminal verification task, with this task marked done or blocked according to findings

## Required checks

- `git diff --check`
- `find app-description -maxdepth 4 -type f | sort` graph-shape review
- focused `rg` proving all five core workstreams are present in the graph
- focused `rg` proving active content does not rely on archived legacy docs as authority
- focused `rg` or validation proof that specs/readiness references have been reconciled or explicitly deferred

## Done criteria

- Current task group completion is assessed.
- Overall mini-project done state from `README.md` is assessed.
- No material gaps remain, or new bounded follow-up tasks and a new terminal verification task are appended.
- Changes and queue update are committed.

## Vertical workstream contract

- Workstream / functional agent: docs-only terminal verification across core starter current-intent graph migration
- Attention category or non-attention reason: non-runtime verification
- Role-specific dashboard / surface: none directly
- Surface graph node/action edge: verifies described graph nodes only
- Governed-tool id and exposure: verifies described bindings only
- Capability id: migration done-state verification
- AuthContext / roles / tenant scope: verifies tenant/auth/trace commitments are captured or gap-recorded
- Akka substrate: docs/specs only
- API / frontend / realtime path: no runtime feature validation beyond mapping evidence
- Audit/work trace requirements: verification report and task notes
- Local validation path: `git diff --check` plus proof commands above
