# Task AABP-03-001: Wire current Agent Admin workstream/API surfaces

## Goal

Expose current Agent Admin behavior-profile proposal/review/activation semantics through protected workstream/API actions and remove stale product exposure of whole-agent lifecycle/profile mutation.

## Required reads

- `specs/agent-admin-behavior-profile-realization/implementation-map.md`
- `app-description/domains/core-starter/workstreams/agent-admin/surfaces/surfaces.md`
- `app-description/domains/core-starter/workstreams/agent-admin/tools/governed-tools.md`
- `app-description/domains/core-starter/workstreams/agent-admin/realization/api-contracts.md`
- `app-description/domains/core-starter/workstreams/agent-admin/tests/coverage.md`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java`
- `src/test/java/ai/first/application/coreapp/workstream/AgentAdminBrowserWorkstreamSmokeTest.java`

## Skills

- `akka-http-endpoints`
- `akka-http-endpoint-component-client`
- `akka-http-endpoint-testing`
- `akka-runtime-feature-verification`

## Vertical contract

- Worker: SaaS admin human.
- Actor adapter: `surface_action` through WorkstreamService/Endpoint.
- Governed tools/capabilities: Agent Admin list/detail/doc/proposal/profile/assignment/trace actions under `agent-doc-administration`.
- Confirmation/approval: activation/deprecation/removal require explicit protected action; direct high-risk activation denied/routed.
- Result surfaces: dashboard/catalog/detail/doc editor/proposal review/profile version/assignment/trace/system-message.
- Trace: every protected action emits safe trace/audit metadata.

## Expected outputs

- Workstream/API routes for dashboard, catalog, detail, doc version/history/diff, edit session/proposal review, activation/reject/cancel, profile version, skill assignment, generated tool assignment, model config ref, and runtime traces at implemented scope.
- Browser-safe denials for non-SaaS-admin, stale proposals, high-risk activation, and unauthorized documents.
- Stale action ids/tests reconciled or quarantined as compatibility-only.

## Done criteria

- Protected workstream smoke proves SaaS Owner/Admin current Agent Admin surfaces and key action paths.
- Non-SaaS-admin callers are denied without hidden document/workstream enumeration.
- Stale whole-agent create/delete/activate/deactivate/profile mutation actions are not presented as current product actions.
- Queue status is updated and changes are committed.

## Required checks

```bash
mvn -Dtest='AgentAdminBrowserWorkstreamSmokeTest,WorkstreamServiceTest,*AgentAdmin*Service*Test,*AgentRuntime*Test' test
git diff --check
```

## Commit message

`Wire Agent Admin current workstream surfaces`
