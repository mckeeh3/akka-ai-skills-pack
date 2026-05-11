# TASK-08-003: Implement supplies workflow with deterministic agent/tool stubs

## Purpose

Create the durable supplies autopilot orchestration path while keeping agent/tool behavior bounded and deterministic for reference tests.

## Required reads

- `AGENTS.md`
- `docs/ai-first-saas-application-architecture.md`
- `docs/agent-coverage-matrix.md`
- `specs/ai-first-skills-pack-migration/sprints/08-executable-ai-first-reference-slice-sprint.md`
- `specs/ai-first-skills-pack-migration/backlog/08-executable-ai-first-reference-slice-build-backlog.md`
- supplies domain files from `TASK-08-001`
- `SupplyDecisionEntity` files from `TASK-08-002`

## Scope

- Add `SupplyAutopilotWorkflow` for telemetry-triggered recommendation flow.
- Add deterministic forecast/policy/inventory agent/tool stubs or fixtures that return structured recommendation inputs.
- Implement auto-ship, approval-required pause/resume, rejection, suppression, missing-evidence escalation, stale-decision scheduling hook, retry/idempotency, and no-op handling.
- Add workflow tests for each path.

## Non-goals

- No real LLM call or external service integration.
- No broad agent-team framework beyond the supplies specialist stub needed by this slice.
- No UI or endpoint code except minimal test harness support if required.

## Skills

- `ai-first-saas`
- `ai-first-saas-agent-team-design`
- `ai-first-saas-policy-governance`
- `akka-workflows`
- `akka-workflow-component`
- `akka-workflow-pausing`
- `akka-agent-structured-responses`
- `akka-agent-tools`
- `akka-workflow-testing`
- `akka-agent-testing`

## Expected outputs

- `SupplyAutopilotWorkflow` and deterministic stub classes.
- Workflow/agent boundary tests.

## Required checks

- Run focused workflow tests.
- Run compile for affected source/test packages.

## Done criteria

- The workflow owns side effects and authority gates; agents/tools only recommend or explain.
- Tests prove auto, approval, suppression, missing evidence, stale scheduling hook, and idempotent behavior.