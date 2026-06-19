# Conversation Capture: Full-Core SaaS Readiness

## User goals

The user asked what should happen after all workstream design/implementation alignment pending tasks were completed. The assistant recommended moving to full-core SaaS readiness gap closure and proposed a new mini-project. The user replied:

> go ahead

## Accepted direction

Create a durable `specs/full-core-saas-readiness/` mini-project and pending-task queue focused on closing or explicitly blocking/defering the full-core readiness gaps recorded in the app-description readiness docs.

## Current state from prior work

The workstream alignment initiative completed at five-core starter scope. It aligned:

- canonical workstream/functional-agent/surface ids;
- surface action to governed-tool mappings;
- default dashboard loading;
- shell surface alias resolution;
- bounded realtime v1 semantics;
- readiness notes and legacy page artifact classification.

The app is still not full-core SaaS ready.

## Constraints

- Work must remain root app-facing unless a task explicitly targets skills-pack maintenance.
- Execute one task per fresh context.
- Do not mark runtime features complete without real local Akka/API/UI evidence at the stated scope.
- Model-backed workstream agents must invoke the governed Akka `Agent` runtime path with active managed configuration, loader tools, tool-boundary enforcement, runtime tools, provider fail-closed behavior, and traces.
- Frontend fixture clients/data are test-only and must not become normal runtime substitutes.
- Backend authorization, tenant/customer isolation, audit/work traces, provider fail-closed behavior, and frontend secret boundaries are mandatory.

## Target gap families

1. Readiness inventory and validation baseline.
2. WorkOS/AuthKit runtime validation and frontend secret boundary.
3. Invitation onboarding with Resend/local captured outbox.
4. User Admin structured surfaces and protected capability paths.
5. Managed-agent foundation lifecycle and governance depth.
6. Audit/Trace investigation surfaces and scoped redaction/export.
7. Governance/Policy workflows, approval gates, impact analysis, activation/rollback.
8. Full runtime smoke and readiness verification.

## Non-goals

- Do not add domain-specific workstreams.
- Do not redesign workstreams.
- Do not treat billing as complete unless specific billing-boundary behavior is implemented and tested or explicitly deferred.
- Do not silently expand one task into the entire full-core implementation.

## Unresolved questions

- Whether billing-boundary behavior is in the immediate full-core target or explicitly deferred. The readiness contract task should decide and document the current accepted scope or block billing-dependent tasks.
- Which production provider secrets are available locally. Tasks should validate fail-closed behavior when secrets are absent.
