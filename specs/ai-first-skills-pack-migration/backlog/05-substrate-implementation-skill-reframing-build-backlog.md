# Sprint 5 Build Backlog: Substrate Implementation Skill Reframing

## Purpose

Align existing Akka implementation skill families with the new AI-first target architecture without making them bloated.

## Delivery goal

When a downstream task implements an AI-first substrate object, the focused component skill knows the AI-first role and which companion skills/docs to consult.

## Suggested harness task breakdown

### 1. Reframe agent and workflow implementation skills

- task ID: `TASK-05-001`
- output: concise updates to `akka-agents` and `akka-workflows` families.
- scope: agents as operational workers; workflows as execution plans, approval routing, exceptions, compensation, and long-running automation.

### 2. Reframe entity and view implementation skills

- task ID: `TASK-05-002`
- output: concise updates to ESE/KVE and view families.
- scope: event-sourced audit-grade objects, current-state objects, supervision queues, decision queues, audit/outcome views.

### 3. Reframe consumer, timer, and endpoint skills

- task ID: `TASK-05-003`
- output: concise updates to consumer, timed action, HTTP/gRPC/MCP endpoint families.
- scope: trace fanout, curation, scheduled digest/replay, API/control surfaces, MCP exposure.

### 4. Reframe web UI implementation skills

- task ID: `TASK-05-004`
- output: updates to `akka-web-ui-*` skills.
- scope: command center, decision card, policy/governance, digest, audit/work trace, accessibility and realtime behavior for AI-first surfaces.

### 5. Identify missing examples and tests

- task ID: `TASK-05-005`
- output: gap list under `docs/` or migration specs.
- scope: do not implement all examples; identify missing reference material for future sprints.

## Done criteria

- Component skills preserve low reading cost.
- AI-first context is consistent across implementation families.
- Missing coverage is explicitly captured instead of hidden.
