# TASK-FCSR-01-001: Define full-core readiness gap contract

## Objective

Create an evidence-based full-core readiness gap contract that maps current readiness claims, implemented evidence, blockers, and required follow-up tasks.

## Required reads

- `AGENTS.md`
- `app-description/app.md` and `app-description/domains/core-starter/realization/traceability.md`
- `specs/full-core-saas-readiness/full-core-readiness-verification.md`
- `app-description/domains/core-starter/capabilities/README.md`
- `app-description/global/agents/foundation-functional-agents.md` and `app-description/domains/core-starter/workstreams/*/agents/functional-agent.md`
- `app-description/domains/core-starter/realization/traceability.md`
- `specs/full-core-saas-readiness/README.md`
- `specs/full-core-saas-readiness/conversation-capture.md`
- selected sprint/backlog/queue entry and this task brief
- relevant existing tests/source files discovered by focused `rg`

## Skills

- `app-description-readiness-assessment`
- `app-description-change-impact`
- `core-saas-foundation`
- `agent-workstream-apps`

## In scope

- Add a readiness gap matrix under this mini-project, e.g. `full-core-readiness-gap-contract.md`.
- Map each gap to workstreams, capabilities, surfaces, backend/frontend components, tests, and validation command(s).
- Decide/document whether billing-boundary behavior is in scope or explicitly deferred.
- Update `pending-tasks.md` if evidence shows tasks need splitting, blocking, or reordering.

## Out of scope

- Implementing gap closure beyond small documentation/queue repairs.

## Expected outputs

- `specs/full-core-saas-readiness/full-core-readiness-gap-contract.md`
- updated `pending-tasks.md` if task ordering/scope changes

## Required checks

- `git diff --check`
- focused `rg` proving the contract covers WorkOS/AuthKit, invitations/Resend, User Admin, managed-agent foundation, Audit/Trace, Governance/Policy, support access, billing-boundary decision, tenant isolation, frontend secret boundary, and runtime smoke

## Done criteria

- Implementation tasks can proceed without guessing current evidence or scope.
- Blocked/deferred items are explicit.
- Changes and queue update are committed.

## Commit message

`full-core-ready: define gap contract`
