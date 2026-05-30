# Task: Replace governance policy and access-review runtime with Akka components

## Objective

Replace normal-runtime local-demo/fail-closed Governance/Policy proposal state and User Admin access-review task state with real Akka components.

## Required reads

- AGENTS.md
- skills/README.md
- docs/ai-first-saas-application-architecture.md
- docs/capability-first-backend-architecture.md
- docs/agent-component-selection-guide.md
- specs/real-akka-runtime-replacement/README.md
- specs/real-akka-runtime-replacement/non-akka-runtime-seam-map.md
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/GovernancePolicyRepository.java
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/GovernancePolicyService.java
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/AccessReviewTaskRepository.java
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/UserAdminAccessReviewService.java
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/UserAdminAccessReviewWorker.java

## Skills

- akka-event-sourced-entity or akka-key-value-entity as selected by lifecycle/audit needs
- akka-view
- akka-workflows or akka-autonomous-agents if durable task lifecycle requires them

## In scope

- Add Akka-backed governance policy proposal/simulation/decision/activation/rollback state.
- Add Akka-backed access-review task state and query path.
- Bind `WorkstreamService` actions and surfaces to those Akka-backed services.
- Preserve approval, idempotency, cross-tenant denial, audit/trace, and provider fail-closed semantics.

## Out of scope

- Do not implement a full policy authoring suite beyond the starter's current claimed scope.
- Do not return model-less successful worker output for background analysis.

## Expected outputs

- backend Akka components/repositories/views for governance policy and access-review task state
- tests for proposal lifecycle, simulation, approval/rollback, access-review start/query/resolve, tenant isolation, idempotency, and trace behavior
- queue update

## Required checks

- `git diff --check`
- rendered backend tests covering GovernancePolicyService, WorkstreamService governance actions, UserAdminAccessReviewService/worker
- `rg -n "LocalDemoGovernancePolicy|FailClosedGovernancePolicy|LocalDemoAccessReview|FailClosedAccessReview|new LocalDemoGovernance|new LocalDemoAccessReview" templates/ai-first-saas-starter/backend/src/main/java`

## Done criteria

- Governance and access-review runtime state uses Akka components.
- Local-demo/fail-closed repositories are not wired in normal runtime.
- Tests prove capability-backed workstream actions read/write through Akka state.
- Changes and queue update are committed.

## Commit message

`runtime: replace governance and access review with Akka components`
