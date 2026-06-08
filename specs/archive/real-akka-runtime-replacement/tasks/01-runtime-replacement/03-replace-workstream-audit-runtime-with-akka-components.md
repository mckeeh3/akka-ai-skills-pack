# Task: Replace workstream log and audit trace runtime with Akka components

## Objective

Replace normal-runtime local-demo/fail-closed workstream log and audit trace repositories with Akka component-backed persistence, projections, and query paths.

## Required reads

- AGENTS.md
- skills/README.md
- docs/ai-first-saas-application-architecture.md
- docs/capability-first-backend-architecture.md
- specs/real-akka-runtime-replacement/README.md
- specs/real-akka-runtime-replacement/non-akka-runtime-seam-map.md
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamLogRepository.java
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/AkkaWorkstreamLogRepository.java
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/DurableWorkstreamLogEntity.java
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/AuditTraceRepository.java
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/AuditTraceService.java
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamService.java

## Skills

- akka-event-sourced-entity or akka-key-value-entity as selected by trace/log history needs
- akka-view
- akka-http-endpoints

## In scope

- Ensure workstream request/result history writes and reads through Akka components in normal runtime.
- Replace `LocalDemoAuditTraceRepository` / `FailClosedAuditTraceRepository` with an Akka-backed audit trace repository/view.
- Query trace data from real trace/log/agent trace components rather than synthetic local rows.
- Update endpoints/services/tests for durable workstream item/surface/trace behavior.

## Out of scope

- Do not add external SIEM/export integrations.

## Expected outputs

- backend source updates for Akka-backed workstream log and audit trace repositories/views
- tests for message submission persistence, trace search/detail/timeline, cross-tenant denial, and provider fail-closed trace emission
- queue update

## Required checks

- `git diff --check`
- rendered backend tests covering WorkstreamService, workstream endpoints, audit trace entity/view/service, and agent runtime trace integration
- `rg -n "LocalDemoWorkstreamLog|FailClosedWorkstreamLog|LocalDemoAuditTrace|FailClosedAuditTrace|new LocalDemoWorkstreamLog|new LocalDemoAuditTrace" templates/ai-first-saas-starter/backend/src/main/java`

## Done criteria

- Workstream logs and audit traces use Akka components in normal runtime.
- No normal-runtime local-demo/fail-closed log/trace repository remains in main source.
- Local runtime validation proves persisted workstream/audit behavior.
- Changes and queue update are committed.

## Commit message

`runtime: replace workstream audit storage with Akka components`
