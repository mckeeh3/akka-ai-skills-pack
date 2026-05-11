# TASK-08-005: Implement supplies HTTP APIs and endpoint tests

## Purpose

Expose the supplies autopilot reference slice through small browser/API-friendly HTTP endpoints.

## Required reads

- `AGENTS.md`
- `docs/ai-first-saas-application-architecture.md`
- `specs/ai-first-skills-pack-migration/sprints/08-executable-ai-first-reference-slice-sprint.md`
- `specs/ai-first-skills-pack-migration/backlog/08-executable-ai-first-reference-slice-build-backlog.md`
- supplies domain/entity/workflow/view files from `TASK-08-001` through `TASK-08-004`

## Scope

- Add telemetry test hook API.
- Add pending decision list/detail/action APIs.
- Add trace lookup API.
- Map API errors for missing decision, stale action, validation failure, and authority/policy failure.
- Add endpoint integration tests for happy paths and failures.

## Non-goals

- No production auth provider integration.
- No browser UI implementation.
- No gRPC/MCP endpoints.

## Skills

- `ai-first-saas`
- `ai-first-saas-decision-cards`
- `ai-first-saas-audit-trace`
- `akka-http-endpoints`
- `akka-http-endpoint-component-client`
- `akka-http-endpoint-request-context`
- `akka-http-endpoint-testing`

## Expected outputs

- Supplies HTTP endpoint classes and API records.
- Endpoint integration tests.

## Required checks

- Run focused endpoint integration tests.
- Verify APIs include trace IDs and decision-card evidence fields needed by UI/tests.

## Done criteria

- API can drive telemetry intake, decision review actions, and trace lookup without bypassing workflow/entity authority gates.