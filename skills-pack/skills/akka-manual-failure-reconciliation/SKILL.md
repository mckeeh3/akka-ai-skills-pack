---
name: akka-manual-failure-reconciliation
description: Convert manual tester failures in a generated SaaS or SaaS Foundation App into app-description gaps, implementation gaps, test gaps, provider/config blockers, and focused remediation tasks without adding unrelated features.
---

# Akka Manual Failure Reconciliation

Use this skill when a human reports that manual testing found many features or flows not working, especially in core SaaS workstreams such as User Admin, My Account, Agent Admin, Audit/Trace, or Governance/Policy.

Use `../docs/manual-test-reconciliation.md` as the lifecycle doctrine for manual runtime test sessions, worker-centric scenario evidence, finding categories, and reconciliation outputs.

This is a drift-repair skill. It should diagnose and update planning/spec/queue artifacts before implementation continues. It may append focused remediation tasks; it should not silently broaden an existing implementation task.

## Required reads

Read these first when present:

- `../README.md`
- `../references/generated-saas-runtime-completion.md`
- `../docs/intent-to-realization-flow.md`
- `../docs/manual-test-reconciliation.md`
- `../docs/pending-task-queue.md`
- `../akka-runtime-feature-verification/SKILL.md`
- `../akka-change-request-to-spec-update/SKILL.md`
- `../app-description-change-impact/SKILL.md`
- target project `AGENTS.md`
- affected target project `app-description/**` workstream/capability/surface/test files
- affected target project `specs/**` verification notes, pending tasks, task briefs, and smoke docs
- affected target project backend/frontend source and tests only after the failing flows are classified

## Manual failure intake table

Normalize every reported failure into this table before deciding what to edit:

| ID | Manual flow | Role/context | Expected behavior | Actual behavior | Intent present? | Runtime path | Classification | Blocking? | Remediation |
|---|---|---|---|---|---|---|---|---|---|
| MF-001 | `<click/action/API>` | `<role/AuthContext/tenant>` | `<expected>` | `<actual>` | yes/no/unclear | `<browser -> API -> Akka path>` | `<gap type>` | yes/no | `<task/update>` |

Classification values:

- `app-description gap` — expected behavior is not specified or is ambiguous.
- `implementation gap` — intent/spec exists but code does not realize it through the intended path.
- `test gap` — behavior may exist but lacks regression/runtime/manual coverage.
- `provider/config blocker` — behavior requires WorkOS, Resend, model provider, secrets, external service, or local seed data not available; must fail closed and be documented.
- `seed/demo-data gap` — local manual path lacks safe bootstrap data or fixture setup, but production behavior is otherwise specified.
- `UX/state gap` — behavior works technically but state, copy, accessibility, stale/error handling, or recovery is incomplete.
- `not a bug / expectation change` — manual expectation conflicts with accepted current intent; route to app-description change if desired.

## Reconciliation rules

1. First decide whether the expected behavior is already in current intent.
2. If intent is missing or ambiguous, update app-description/specs before implementation tasks.
3. If intent exists, do not expand the description; create implementation/test remediation tasks.
4. If a provider/config blocker exists, require fail-closed browser-safe behavior and trace evidence; do not mark provider-backed normal behavior done through a mock.
5. If local manual testing needs seed data, make seed/bootstrap requirements explicit rather than treating production auth/authorization as optional.
6. For each blocking failure in a feature group claimed done, update queue/verification notes so the group is not reported as `runtime-ready` until repaired.

## Remediation task requirements

Each remediation task must include:

- the manual failure IDs it fixes;
- canonical runtime path: browser surface/action, API/endpoint/client, Akka component/service/substrate, trace/audit path;
- role/AuthContext/tenant setup and denial scenario;
- success and failure state expectations;
- required automated checks and manual/browser/API smoke checklist;
- done criteria that forbid `done` until the manual failure is retested or explicitly blocked by a named external prerequisite.

Use `akka-runtime-feature-verification` after remediation to close the failure group.

## Output shape

```md
# Manual Failure Reconciliation

## Failure inventory
| ID | Manual flow | Role/context | Expected behavior | Actual behavior | Intent present? | Runtime path | Classification | Blocking? | Remediation |
|---|---|---|---|---|---|---|---|---|---|

## Summary by gap type
- app-description gaps:
- implementation gaps:
- test gaps:
- provider/config blockers:
- seed/demo-data gaps:
- UX/state gaps:

## Updated artifacts
- app-description:
- specs/verification:
- pending tasks:

## Remediation queue
- added:
- blocked:
- superseded:

## Stop/continue recommendation
- stop feature work until: ...
- next runnable remediation task: ...
```

## Anti-patterns

Avoid:

- assuming every manual failure means the app-description needs more detail;
- assuming every manual failure is a user error without checking the accepted intent;
- marking a feature group complete while blocking manual failures remain in its primary path;
- creating one broad “fix user admin” task instead of failure-linked remediation tasks;
- treating seed/demo/manual setup gaps as permission to bypass auth, tenant scope, audit, or provider boundaries.
