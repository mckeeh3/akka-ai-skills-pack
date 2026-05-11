# TASK-08-007: Add slice-level AI-first acceptance and trace/outcome tests

## Purpose

Verify the completed supplies autopilot reference slice as an AI-first vertical behavior, not just separate component mechanics.

## Required reads

- `AGENTS.md`
- `docs/ai-first-saas-application-architecture.md`
- `docs/ai-first-examples-and-tests-gap-list.md`
- `specs/ai-first-skills-pack-migration/sprints/08-executable-ai-first-reference-slice-sprint.md`
- `specs/ai-first-skills-pack-migration/backlog/08-executable-ai-first-reference-slice-build-backlog.md`
- all supplies slice files from `TASK-08-001` through `TASK-08-006`

## Scope

- Add slice-level tests that prove the full behavior:
  - normal forecast auto shipment prepared with complete trace;
  - abnormal/high-cost forecast creates approval-required decision card;
  - offboarding/unmapped contract suppresses or escalates safely;
  - approve/reject resumes or safely terminates workflow;
  - stale decision timer records escalation without duplicate side effects;
  - duplicate telemetry/action commands are idempotent;
  - every recommendation/action has policy, evidence, trace, and outcome refs.
- Add any small test fixtures needed to make the slice repeatable.

## Non-goals

- No new product scope.
- No broad refactor of earlier tasks unless required to make the acceptance tests pass.
- No real external services.

## Skills

- `ai-first-saas`
- `ai-first-saas-audit-trace`
- `ai-first-saas-outcomes-metrics`
- `akka-workflow-testing`
- `akka-ese-integration-testing`
- `akka-view-testing`
- `akka-http-endpoint-testing`
- `akka-web-ui-testing`

## Expected outputs

- Slice-level integration/acceptance tests and fixtures.
- Minimal fixes to prior slice files only when required by the tests.

## Required checks

- Run all supplies slice tests.
- Run full project test command if practical.

## Done criteria

- Tests prove authority boundaries, decision-card completeness, trace completeness, idempotency, and outcome linkage across the implemented slice.