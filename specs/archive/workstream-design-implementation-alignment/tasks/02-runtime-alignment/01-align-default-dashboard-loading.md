# TASK-WDA-02-001: Align default dashboard loading

## Objective

Make default dashboard loading deterministic and backend-authoritative when a workstream is opened or selected, matching the workstream doctrine and updated id/action maps.

## Required reads

- mini-project README, conversation capture, sprint 02, backlog, queue entry, and this task brief
- `app-description/12-workstreams/surfaces-index.md`
- `app-description/70-traceability/workstream-id-map.md`
- `app-description/70-traceability/surface-to-capability-map.md`
- `src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java`
- `frontend/src/main.tsx`
- `frontend/src/workstream/shell/WorkstreamShell.tsx`
- relevant frontend workstream tests

## Skills

- `akka-http-endpoint-component-client`
- `akka-web-ui-state-rendering`
- `akka-web-ui-api-client`
- `akka-http-endpoint-testing`

## In scope

- Decide whether bootstrap returns default dashboard surface(s) or selection triggers an immediate backend shell request.
- Implement the smallest deterministic behavior consistent with backend authorization and trace/system-message rules.
- Add/update backend and frontend tests for initial default dashboard behavior and unauthorized/denied fallback.

## Out of scope

- Adding new rich dashboard types beyond current surfaces.
- Domain-specific workstream dashboards.

## Expected outputs

- Updated backend and/or frontend workstream loading path.
- Updated tests.
- Queue status update.

## Required checks

- `git diff --check`
- targeted backend tests, at minimum `mvn test -Dtest=WorkstreamServiceTest`
- targeted frontend tests for workstream shell/default dashboard behavior, plus `npm --prefix frontend run typecheck` if TypeScript changed

## Done criteria

- Opening/selecting a visible core workstream deterministically renders or requests its default dashboard through backend-authoritative behavior.
- Forbidden/missing targets produce safe denial/system-message behavior.
- Changes and queue update are committed.

## Commit message

`workstream-align: align dashboard loading`
