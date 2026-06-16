# TASK-FCBAD-02-006: Normalize Customer branch action ids or document compatibility aliases

## Objective

Resolve app-description/runtime ambiguity around Customer branch action ids so future implementers have one canonical action vocabulary or explicit compatibility aliases.

## Source finding

- `runtime-audit/foundation-customer-boundary-runtime-drift-audit.md#fcb-rd-08-app-description-and-runtime-action-ids-are-not-fully-normalized`

## Required reads

- `AGENTS.md`
- `specs/foundation-customer-boundary-app-description/runtime-audit/foundation-customer-boundary-runtime-drift-audit.md`
- `app-description/domains/core-starter/workstreams/user-admin/agents/functional-agent.md`
- `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `frontend/src/workstream-user-admin-vertical.contract.test.mjs`
- this task brief

## Implementation scope

Choose the smallest safe normalization path:

1. Update app-description to use current runtime action ids as canonical, and explicitly remove/replace unmatched names; or
2. Add runtime compatibility aliases for app-description action ids while declaring one canonical set.

Do not change user-facing behavior except where action id compatibility requires it. Preserve existing tests and add focused assertions for the chosen canonical/alias mapping.

## Required checks

- `mvn -Dtest=ai.first.application.coreapp.workstream.WorkstreamServiceTest test`
- `npm --prefix frontend test -- --run frontend/src/workstream-user-admin-vertical.contract.test.mjs`
- `git diff --check`

## Done criteria

- Active app-description and runtime/tests agree on Customer branch action ids or explicitly document compatibility aliases.
- No orphan action ids remain in Customer branch current-intent text without runtime mapping or stated non-runtime meaning.
- Queue status and notes are updated and committed with changes.

## Vertical workstream contract

- Workstream / functional agent: User Admin / `user-admin-agent`.
- Attention category or non-attention reason: compatibility/drift repair; no new attention item.
- Surface graph: Customer branch action edges.
- Governed-tool id and exposure: `manage-customers`, `manage-customer-admins`.
- Capability id: `tenant.customer.*`, `tenant.customer_admin.*`.
- AuthContext / roles / tenant scope: unchanged; preserve existing backend authorization.
- Akka substrate: app-description/runtime action mapping and tests.
- Audit/work trace requirements: preserve action audit names or document aliases.
- Local validation path: focused Maven/frontend tests and `git diff --check`.
