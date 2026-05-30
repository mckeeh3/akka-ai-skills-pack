# Full-Core SMB Runtime Durability Remediation

## Purpose

Remediate the gap between the full-core SMB release-readiness recommendation and the user's release bar that normal generated runtime behavior must not rely on in-memory mockups/default stores.

The previous release-readiness pass correctly validated governed model-backed agent paths and provider fail-closed behavior, but a follow-up source scan found normal starter runtime defaults such as `InMemoryIdentityRepository`, `InMemoryInvitationRepository`, `InMemoryAgentBehaviorRepository`, `InMemoryWorkstreamLogRepository`, `InMemoryAuditTraceRepository`, `InMemoryGovernancePolicyRepository`, `InMemoryAccessReviewTaskRepository`, and `InMemoryAgentRuntimeTraceSink`.

This mini-project should inspect and then remove, replace, or explicitly gate in-memory/fixture/demo runtime paths so they are not presented as completed normal generated-app runtime behavior.

## Background

The user asked whether there are no in-memory mockups. The answer was: not literally. The starter still contains in-memory runtime repositories/defaults and frontend fixture/demo inspection paths.

Accepted clarification:

- It is acceptable that tests use fakes/mocks/fixtures.
- It is acceptable to keep explicitly named local/demo/test adapters if they are gated and not claimed as the normal production-like generated-app runtime.
- It is not acceptable for completed normal runtime features to depend on in-memory stores or fixture/demo clients while being described as full-core release-ready.

## Scope

- Inventory all in-memory, fake, mock, fixture, demo, canned, and model-less paths in `templates/ai-first-saas-starter/` and root mirrored frontend where relevant.
- Classify each path as one of:
  - test-only and acceptable;
  - explicit local/dev demo adapter requiring gating/copy changes;
  - normal runtime default requiring replacement or fail-closed gating;
  - generated static artifact or stale build output requiring cleanup/exclusion;
  - documentation claim requiring correction.
- Define remediation tasks that replace normal in-memory stores with durable Akka components or fail-closed configuration requirements at the stated scope.
- Ensure model-backed agent behavior still invokes governed Akka Agent runtime or fails closed; do not introduce deterministic/model-less success substitutes.
- Update release-readiness docs/handoff if the prior ship recommendation must be superseded until remediation completes.

## Non-goals

- Do not remove tests or test-only fakes that are clearly scoped to tests.
- Do not implement a broad enterprise persistence layer beyond what the starter needs to avoid in-memory normal runtime claims.
- Do not mark features release-ready by renaming in-memory classes without changing runtime behavior or gating semantics.
- Do not keep frontend fixture/demo inspection as an unqualified normal path.

## Target source areas

- `templates/ai-first-saas-starter/backend/src/main/java/`
- `templates/ai-first-saas-starter/backend/src/test/java/`
- `templates/ai-first-saas-starter/frontend/src/`
- `templates/ai-first-saas-starter/src/main/resources/static-resources/` if stale generated assets are present
- root `frontend/` mirrors where repository convention requires synchronization
- `templates/ai-first-saas-starter/README.md`
- `specs/full-core-smb-polish-release-readiness/` release handoff/verification docs

## Execution model

Execute one task per fresh harness session. Start with a source-boundary inventory and remediation map. Append bounded source-edit tasks from that map.

## Done state

This mini-project is complete when:

- all in-memory/mock/fixture/demo paths in the starter are inventoried and classified;
- normal generated runtime no longer silently depends on in-memory stores for features claimed as complete, or those paths fail closed with clear configuration/actionable guidance;
- tests may still use test fakes and fixtures, but those are clearly test-only;
- frontend fixture/demo paths are explicitly gated and not confused with production-like runtime;
- stale/static generated assets are cleaned or regenerated as needed;
- release-readiness docs are corrected to supersede any prior ship recommendation until remediation passes;
- broad starter validation passes after remediation or blockers are bounded in the queue.
