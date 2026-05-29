# Audit Trace Workstream v0

## Purpose

Complete the Audit/Trace vertical for trace search, timelines, denial/model/tool evidence, correlation ids, investigation guidance, and audit-safe explanations.

This mini-project is one vertical in the five-core v0 secure AI-first SaaS starter/reference runtime series. It inherits the shared coordination contract from `specs/five-core-workstreams-v0-plan/shared-five-core-v0-contract.md` and the execution/dependency order from `specs/five-core-workstreams-v0-plan/workstream-dependency-map.md`.

## Background

The five-core v0 starter already exists as a production-ready baseline. This workstream project plans the next focused vertical increment for the `Audit/Trace Agent` without re-planning the other four workstreams.

## Agent-type intent

Audit/Trace should use deterministic projections and redaction as the default substrate, request/response explanation for bounded questions, and AutonomousAgent only for durable anomaly/risk review or audit-summary batches.

Common rules:

- keep normal user-facing composer turns request-based unless a task explicitly says otherwise;
- use Akka `AutonomousAgent` only for durable internal/background tasks with task lifecycle semantics;
- use deterministic non-AI services for mechanical policy, authorization, projection, lifecycle, redaction, validation, and trace work;
- model backend behavior as governed capabilities before exposing browser actions, APIs, agent tools, workflows, timers, consumers, or AutonomousAgent task operations.

## Scope

- Workstream-specific app-description/spec contract.
- Capability inventory and selected Akka substrate for the v0 vertical.
- Backend/API/runtime implementation tasks.
- Frontend structured surfaces and workstream shell integration tasks.
- Tests and local runtime/API/UI validation.

## Non-goals

- Do not implement other workstreams except where this workstream consumes their existing capabilities or traces.
- Do not add app-specific/domain-specific features.
- Do not claim full-core SaaS readiness unless the task explicitly extends scope and validation.
- Do not use deterministic/demo/model-less normal runtime behavior to satisfy model-backed workstream claims.

## Affected repository areas

- `templates/ai-first-saas-starter/`
- root `frontend/` mirror if frontend source changes
- `docs/` or `skills/` only when routing/doctrine must change for this workstream
- `specs/audit-trace-workstream-v0/`

## Execution model

Execute one task per fresh harness session. Prefer completing this workstream vertical before moving to the next workstream mini-project.

## Read order for future task sessions

1. `AGENTS.md`
2. `skills/README.md`
3. `specs/five-core-workstreams-v0-plan/shared-five-core-v0-contract.md`
4. `specs/five-core-workstreams-v0-plan/workstream-dependency-map.md`
5. this mini-project's `README.md`
6. this mini-project's `conversation-capture.md`
7. this mini-project's `pending-tasks.md`
8. selected sprint/backlog/task brief
9. only task-listed source files and skills

## Done state

This workstream mini-project is complete when the stated v0 vertical works through the intended local Akka runtime/API/UI path, with backend authorization, governed capabilities, traces, tests, frontend rendering where user-facing, and verification confirming no material queue gaps remain.
