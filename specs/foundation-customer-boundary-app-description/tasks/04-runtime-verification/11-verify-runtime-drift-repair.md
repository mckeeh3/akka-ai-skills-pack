# TASK-FCBAD-02-007: Verify foundation Customer boundary runtime drift repair

## Objective

Verify that the runtime now matches the active foundation Customer boundary app-description for the bounded drift findings. If material drift remains, append follow-up tasks and a new terminal verification task.

## Required reads

- `AGENTS.md`
- `specs/foundation-customer-boundary-app-description/runtime-audit/foundation-customer-boundary-runtime-drift-audit.md`
- `specs/foundation-customer-boundary-app-description/pending-tasks.md`
- `app-description/domains/core-starter/capabilities/user-and-access-administration.md`
- `app-description/domains/core-starter/data-state/auth-context-and-membership-state.md`
- `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md`
- `app-description/domains/core-starter/workstreams/user-admin/agents/functional-agent.md`
- `app-description/domains/core-starter/workstreams/user-admin/realization/akka-components.md`
- `app-description/domains/core-starter/workstreams/user-admin/realization/frontend-routes.md`
- `app-description/domains/core-starter/workstreams/user-admin/tests/coverage.md`
- this task brief

## Verification question

Ask and answer explicitly:

> Does the implemented foundation Customer boundary runtime now match the active app-description for Customer lifecycle and Customer Admin branch behavior?

## Verification checklist

Answer yes only if runtime/tests prove:

- Customer lifecycle API and workstream paths preserve selected tenant scope, idempotency, redaction, safe denial, and audit/trace evidence.
- Customer Admin APIs enforce target Customer proof and Customer Admin-safe roles.
- Customer Admin workstream surfaces preserve selected `customerId` and use customer-scoped invitation/actions.
- Suspended Customer boundaries fail closed for Customer Admin operations while detail/reactivate remains available.
- Customer list/search filters work through API and workstream where declared.
- Frontend scoped admin surfaces submit backend-authored target context and do not infer authority.
- Action ids are normalized or explicit aliases are documented/tested.

## Expected outputs

- `specs/foundation-customer-boundary-app-description/runtime-audit/foundation-customer-boundary-runtime-repair-verification.md`
- Updated `pending-tasks.md` status/notes.
- If gaps remain: append bounded follow-up tasks and a new terminal verification task.

## Required checks

- `mvn -Dtest=ai.first.application.coreapp.workstream.WorkstreamServiceTest,ai.first.application.coreapp.useradmin.AdminEndpointIntegrationTest test`
- `npm --prefix frontend test -- --run frontend/src/workstream-user-admin-vertical.contract.test.mjs`
- `git diff --check`

## Done criteria

- Verification records a clear yes/no answer with evidence.
- If yes: no follow-up runtime repair tasks are appended for this bounded drift scope.
- If no: queue contains specific follow-up tasks and a new terminal verification task.
- Queue status and notes are updated and committed with verification output.

## Vertical workstream contract

- Workstream / functional agent: User Admin / `user-admin-agent`.
- Attention category or non-attention reason: terminal runtime verification; no runtime attention item.
- Surface graph: Customer lifecycle and Customer Admin branch surfaces.
- Governed-tool id and exposure: `manage-customers`, `manage-customer-admins`.
- Capability id: `tenant.customer.*`, `tenant.customer_admin.*`.
- AuthContext / roles / tenant scope: verify selected tenant/customer scoping and denials.
- Akka substrate: endpoint/workstream/service/frontend tests.
- Audit/work trace requirements: verify safe audit/trace/redaction evidence in tests or runtime proof.
- Local validation path: focused Maven and npm tests plus `git diff --check`.
