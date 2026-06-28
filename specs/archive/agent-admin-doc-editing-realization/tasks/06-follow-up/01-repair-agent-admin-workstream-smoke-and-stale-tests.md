# Task AADE-06-001: Repair Agent Admin workstream smoke and stale backend test drift

## Scope

Repair the Agent Admin runtime/API validation gaps found by `AADE-05-001` without reintroducing stale governance-console intent.

## Required reads

- `specs/agent-admin-doc-editing-realization/verification-notes.md`
- `specs/agent-admin-doc-editing-realization/README.md`
- `specs/agent-admin-doc-editing-realization/conversation-capture.md`
- `specs/agent-admin-doc-editing-realization/pending-tasks.md`
- `app-description/domains/core-starter/workstreams/agent-admin/**`
- `app-description/domains/core-starter/capabilities/agent-doc-administration.md`
- affected backend/workstream tests and runtime paths named in the verification notes

## Skills

- `akka-runtime-feature-verification`
- `akka-agent-testing`

## Implementation requirements

- Make the current Agent Admin Akka workstream/API smoke path return non-empty current doc-editing agent list rows for a SaaS Owner/Admin context.
- Preserve server-side denial for non-SaaS-admin callers with the current `agent-admin-requires-saas-owner-admin` posture.
- Reconcile or remove stale `WorkstreamServiceTest` assertions that still treat Agent Admin as tenant-scoped governance, prompt-risk, seed-import, model-ref, tool-boundary, activation, or rollback administration.
- Keep old governance substrate tests only where they are explicitly non-current/internal and do not claim current Agent Admin UX readiness.
- Do not change app-description intent or reintroduce tenant/customer-scoped Agent Admin access.

## Required checks

```bash
mvn -Dtest='AgentAdminBrowserWorkstreamSmokeTest,WorkstreamServiceTest,AgentAdminDocAdministrationServiceTest,AgentAdminDocEditingAgentTest,AgentRuntimeServiceTest,AgentRuntimeToolResolverTest,AgentRuntimeTraceSinkTest' test
npm --prefix frontend test -- --run frontend/src/workstream-agent-admin-vertical.contract.test.mjs frontend/src/workstream-surfaces.contract.test.mjs frontend/src/workstream-surface-intent-routing.contract.test.mjs
git diff --check
```

Run broader checks if the repair touches shared workstream routing.

## Done criteria

- Current Agent Admin backend/API smoke proves SaaS-admin doc-editing list/detail/doc/edit/version/create-delete/trace surfaces at the implemented scope.
- Stale governance-console backend tests no longer block or masquerade as current Agent Admin validation.
- Targeted checks pass and changes are committed with the queue update.
