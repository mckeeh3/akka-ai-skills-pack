# Task AABP-01-003: Align doc restore and skill/reference lifecycle

## Goal

Reconcile prompt/skill/reference document flows with proposal-first current intent: restore creates proposal, create skill/reference creates proposal, and delete defaults to deprecation with assignment/reference impact disclosure.

## Required reads

- `specs/agent-admin-behavior-profile-realization/implementation-map.md`
- `specs/agent-admin-behavior-profile-realization/tasks/01-backend/01-proposal-lifecycle-foundation.md`
- `app-description/domains/core-starter/workstreams/agent-admin/behavior.md`
- `app-description/domains/core-starter/workstreams/agent-admin/tests/coverage.md`
- Agent Admin doc administration service/domain tests.

## Skills

- `akka-agent-prompt-governance`
- `akka-agent-skill-governance`
- `akka-agent-reference-governance`
- `akka-agent-behavior-editing`

## Vertical contract

- Actor adapter: `surface_action` for human SaaS admin Save Draft/Restore/Create/Deprecate/Remove; editing-agent drafts content only.
- Governed tools: prompt/skill/reference read/edit/propose/activate/deprecate/remove tool ids from Agent Admin catalog.
- Transaction boundary: every proposal/save/activate/deprecate operation is separate and idempotent.
- Trace: includes affected doc id/version, lifecycle action, assignment/reference counts, and safe risk/approval state.

## Expected outputs

- Restore-version path creates restore proposal with edit request `Restored from version N`.
- Historical versions are read-only and cannot be edited directly.
- Create skill/reference creates non-active tenant-scoped proposal; activation creates first active version.
- Delete/remove skill/reference defaults to deprecation unless lifecycle policy permits hard delete.
- Confirmation/result payloads list affected assignments, references, and manifest entries/counts.
- Tests replacing stale permanent-delete/direct-restore assumptions.

## Done criteria

- Runtime/current active docs are unchanged by restore/create/delete draft actions until activation/deprecation operation is authorized.
- Deprecated skills cannot be newly assigned but remain readable for historical trace/version interpretation.
- No hidden loader access remains after skill/reference deprecation/removal.
- Queue status is updated and changes are committed.

## Required checks

```bash
mvn -Dtest='*AgentAdmin*Service*Test,*AgentAdmin*Doc*Test,*AgentRuntimeToolResolver*Test' test
git diff --check
```

## Commit message

`Align Agent Admin document lifecycle semantics`
