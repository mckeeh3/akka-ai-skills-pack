# MAFA-01-001: Split My Account source-alignment entries

## Goal

Replace the broad My Account source-alignment row with smaller evidence-bearing entries so later tasks can mark specific slices aligned, partially aligned, blocked, or manual-only without overstating readiness.

## Required reads

- `specs/my-account-full-alignment/README.md`
- `specs/my-account-full-alignment/conversation-capture.md`
- `specs/my-account-full-alignment/backlog/01-my-account-automated-alignment-build-backlog.md`
- `app-description/domains/core-starter/workstreams/my-account/lifecycle.md`
- `app-description/domains/core-starter/workstreams/my-account/realization/source-alignment.md`
- `app-description/domains/core-starter/workstreams/my-account/**`

## Implementation requirements

- Split alignment entries for at least: dashboard, profile/settings, context authority, notification center, digest/export, `human_chat_tool_plan`, trace/audit, no-access recovery.
- Preserve the current dashboard partial-alignment evidence from the recent compile.
- Mark unverified slices conservatively; do not claim runtime readiness.
- Update lifecycle/readiness language only for automated alignment planning evidence.

## Vertical workstream contract

- Lifecycle / readiness target: build-compile planning/alignment; non-runtime documentation task.
- Workstream / functional agent: My Account / `my-account-agent`.
- Governed-tool id and exposure: documentation maps all current My Account surface actions, `human_chat_tool_plan`, API/internal paths; no tool execution.
- Capability id: `account-context-and-profile` and linked My Account/notification/digest ids.
- AuthContext / tenant scope: document selected `AuthContext`, tenant/customer safe-denial requirements.
- Akka substrate: docs-only alignment map.
- Audit/work trace requirements: map trace obligations; no runtime trace emission in this task.
- Local validation path: `git diff --check`.

## Required checks

```bash
git diff --check -- specs/my-account-full-alignment app-description/domains/core-starter/workstreams/my-account
```

## Done criteria

- Source-alignment file contains split entries with status/evidence notes.
- Lifecycle file accurately says which slices are automated-aligned, partially aligned, blocked, or pending validation.
- Queue status is updated and changes are committed.
