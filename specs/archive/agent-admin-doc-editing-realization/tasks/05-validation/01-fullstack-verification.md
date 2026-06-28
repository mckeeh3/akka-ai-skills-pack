# Task AADE-05-001: Full-stack validation and terminal verification

## Scope

Verify the mini-project done state across app-description, backend, frontend, and tests. This is the terminal verification task for the first task group.

## Required reads

- `specs/agent-admin-doc-editing-realization/README.md`
- `specs/agent-admin-doc-editing-realization/conversation-capture.md`
- `specs/agent-admin-doc-editing-realization/pending-tasks.md`
- all sprint files under `specs/agent-admin-doc-editing-realization/sprints/`
- all backlog files under `specs/agent-admin-doc-editing-realization/backlog/`
- `app-description/domains/core-starter/workstreams/agent-admin/**`
- `app-description/domains/core-starter/capabilities/agent-doc-administration.md`

## Skills

- `akka-runtime-feature-verification`
- `akka-web-ui-testing`
- `akka-agent-testing`

## Verification requirements

Assess whether completed work satisfies the mini-project done state:

- SaaS-admin-only Agent Admin access;
- backend agent/doc browsing;
- prompt/skill/reference doc versioning;
- current-version-only edits;
- Save/Cancel;
- adjacent-version diffs;
- restore-created versions;
- skill/reference create/delete and cascade semantics;
- editing-agent runtime/fail-closed behavior;
- runtime prompt + skill descriptor loading;
- `readSkill` / `readReferenceDoc` traces;
- frontend current Agent Admin surfaces;
- stale governance Agent Admin UI no longer treated as current;
- tests/checks pass or blockers are explicit.

## Required checks

Run the strongest practical validation for the completed scope, normally:

```bash
mvn test
npm --prefix frontend test -- --run
npm --prefix frontend run typecheck
npm --prefix frontend run build
git diff --check
```

If full checks are too slow or blocked, run targeted checks and record exact blockers/residual risk.

## Follow-up rule

If material gaps remain:

1. append bounded follow-up tasks to `pending-tasks.md` with task briefs;
2. append a new terminal verification task after those tasks;
3. do not mark this task done unless the current task's verification outputs and queue updates are committed.

If no material gaps remain, write `verification-notes.md` with evidence and mark the mini-project complete.

## Done criteria

- Verification notes are written.
- Required checks or documented blockers are recorded.
- Queue is updated to either complete the mini-project or include follow-up tasks plus a new terminal verification task.
- Changes are committed.
