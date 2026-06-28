# MAFA-08-001: Terminal automated verification

## Goal

Verify whether the current task group completes the My Account full automated alignment done state. If material automated gaps remain, append bounded follow-up tasks and a new terminal verification task.

## Required reads

- `specs/my-account-full-alignment/README.md`
- `specs/my-account-full-alignment/conversation-capture.md`
- `specs/my-account-full-alignment/pending-tasks.md`
- `specs/my-account-full-alignment/backlog/01-my-account-automated-alignment-build-backlog.md`
- all task briefs in `specs/my-account-full-alignment/tasks/**`
- `app-description/domains/core-starter/workstreams/my-account/lifecycle.md`
- `app-description/domains/core-starter/workstreams/my-account/realization/source-alignment.md`
- relevant changed tests/source files from completed tasks

## Skills

- `akka-runtime-feature-verification`
- `app-description-readiness-summary`
- `akka-pending-task-queue-maintenance`

## Verification requirements

- Compare completed work against the README done state, backlog items, task done criteria, source-alignment entries, and app-description tests/coverage.
- Run or justify the appropriate automated checks for backend, frontend, source alignment, and queue integrity.
- Confirm whether all non-manual automated alignment items are done.
- Record remaining manual-only or provider-config-only items separately from automated gaps.
- Update lifecycle/source-alignment/readiness to the highest supportable status without overstating runtime readiness.
- If material automated gaps remain, append new bounded tasks to `pending-tasks.md`, create task briefs as needed, and append a new terminal verification task.

## Vertical workstream contract

- Lifecycle / readiness target: verification; automated-aligned/manual-ready if evidence supports it.
- Workstream / functional agent: My Account / `my-account-agent`.
- Governed-tool id and exposure: verification covers all My Account governed tools/adapters in scope; no new runtime tool use unless checks exercise tests.
- Capability id: all My Account/notification/digest capability ids in source-alignment entries.
- AuthContext / tenant scope: verify evidence includes selected context and denial cases.
- Akka substrate: verification over backend/frontend/API/test/docs artifacts.
- Audit/work trace requirements: verify trace evidence or queue follow-up.
- Local validation path: aggregate automated checks plus `git diff --check`.

## Required checks

Run the smallest complete set that proves terminal state. Default target:

```bash
mvn -Dtest='WorkstreamServiceTest,MyAccountBrowserWorkstreamSmokeTest,MyAccountPersonalAttentionDigestServiceTest,MyAccountPersonalAttentionDigestAutonomousAgentTest,AgentBehaviorSeedLoaderTest' test
npm --prefix frontend test -- --run
npm --prefix frontend run typecheck
npm --prefix frontend run build
git diff --check
```

If commands are narrowed or unavailable, record why and whether that blocks completion.

## Done criteria

- Terminal verification report is recorded in the queue notes and/or a verification note.
- Lifecycle/source-alignment state is updated accurately.
- No material automated gaps remain, or follow-up tasks plus a new terminal verification task are appended.
- Queue status is updated and changes are committed.
