# AI-First Examples and Tests Gap List

## Purpose

This gap list records the missing reference examples and tests after the AI-first substrate skill reframing. It is a planning artifact for future example/test work only; it does not implement the examples.

Use it to avoid hiding coverage gaps behind broad doctrine or retrofitting existing conventional examples into agentic ones without explicit intent.

## Current coverage baseline

The repository already has strong focused examples for individual Akka substrate families:

- agents, structured responses, tools, guardrails, evaluation, orchestration, memory/runtime state, and streaming;
- workflows with approval, pause/resume, compensation-style flow, notifications, deadlines, and endpoint integration;
- Event Sourced Entities, Key Value Entities, Views, Consumers, Timed Actions, HTTP/gRPC/MCP endpoints, and Akka-hosted web UI delivery patterns;
- a low-agentic purchase-request app-description reference under `docs/examples/purchase-request-app-description/`.

These examples are valuable implementation substrate references, but they are not yet a cohesive AI-first SaaS worked example.

## Priority key

- **P0 required**: needed for future agents to confidently generate AI-first SaaS apps without guessing core operating-model semantics.
- **P1 important**: closes recurring implementation/test gaps once the P0 worked example exists.
- **P2 optional**: useful breadth examples, but not required before the first complete AI-first reference path.

## Required gaps

### P0: cohesive AI-first worked app-description example

Missing reference: `docs/examples/agent-first-dca-app-description/`.

Required coverage:

- durable goals, constraints, success criteria, execution plans, and outcome links;
- bounded coordinator/specialist/evaluator agent roles with explicit authority limits;
- retained human authority, approvals, exceptions, and escalation rules;
- policy clauses, thresholds, permissions, simulations, proposals, and human-governed commits;
- decision cards with evidence, risk, confidence, impact, alternatives, and actions;
- audit/work/decision traces covering tool calls, data access, policy invocations, approvals, and outcomes;
- supervision-oriented UI surfaces: goal-to-execution workbench, command center, decision review, governance/learning, digest, and audit/trace;
- traceability from operating-model artifacts to behavior, tests, security, observability, UI, and realization slices.

Owner path: Sprint 6 DCA tasks (`TASK-06-001` through `TASK-06-004`).

### P0: AI-first implementation-slice examples derived from the worked app-description

Missing reference: slice-level docs or specs showing how the DCA app-description becomes bounded Akka implementation tasks.

Required coverage:

- one vertical slice for goal intake → execution plan → workflow start;
- one vertical slice for agent recommendation → decision card → human approval/rejection;
- one vertical slice for policy gate → exception/escalation → trace update;
- one vertical slice for audit/outcome projection → supervision UI/API surface.

Each slice should name the minimal component skills to load and should avoid implementing unrelated substrate breadth.

### P0: explicit AI-first acceptance/evaluation test patterns

Missing reference: test guidance or examples that verify AI-first semantics, not just component mechanics.

Required coverage:

- agent authority boundary tests: autonomous path allowed, approval-required path blocked, escalation path recorded;
- decision-card completeness tests: evidence, risk, confidence, impact, alternatives, policy trigger, and trace link present;
- workflow pause/resume tests for human approvals and exceptions;
- audit-trace tests proving policy, tool/data access, decision, approval, and outcome identifiers are persisted or projected;
- policy-change governance tests: simulation/proposal can be drafted, but commit requires authorized human action.

## Important gaps

### P1: audit-grade Event Sourced Entity example for AI-first decisions or policies

Current ESE examples demonstrate event-sourced state but not a purpose-built AI-first object such as `DecisionRecord`, `PolicyDocument`, or `WorkTrace`.

Needed when implementing P1:

- domain events that preserve temporal reasoning and accountability;
- command validation around authority, policy version, evidence references, and outcome links;
- replay tests that prove state reconstructs decision or policy history.

### P1: command-center and decision-queue View examples

Current View examples demonstrate projection mechanics, but not AI-first supervision queues.

Needed when implementing P1:

- active goals/plans by owner/status/risk;
- pending approvals/exceptions sorted by urgency using valid Akka View query constraints;
- audit or decision search with explicit filters instead of optional-filter `OR` patterns;
- tests for projection updates and endpoint/UI read behavior.

### P1: AI-first web UI reference surface

Current web UI guidance covers React/Vite integration and UI quality, but there is no concrete AI-first screen reference.

Needed when implementing P1:

- command center state model with active work, attention-needed items, and stale/reconnecting states;
- decision card component with evidence, policy trigger, risk/confidence, action controls, and trace link;
- governance/policy proposal screen with simulation result and commit authority state;
- accessibility and responsive checks for dense supervision surfaces.

### P1: trace fanout and digest examples

Current consumer/timer examples cover mechanics, but not AI-first trace curation.

Needed when implementing P1:

- consumer that enriches or republishes material trace events for supervision/audit;
- timed action that builds periodic digest inputs or triggers stale-goal rechecks;
- idempotency tests for duplicate/replayed trace events and timer replacement behavior.

## Optional gaps

### P2: MCP AI-client governance example

Useful future example: an MCP endpoint exposing approved resources/tools for goals, policies, traces, or bounded action requests.

Non-goal until P0/P1 are complete: broad autonomous external-agent control.

### P2: gRPC service-boundary example for AI-first status or approvals

Useful future example: protobuf-first approval, exception, or audit query API with explicit evidence/risk/trace fields.

### P2: outcome-loop analytics example

Useful future example: outcome metric entity/view flow linking decisions and automated actions to later business results, with replay or evaluation summary.

## Not gaps

Do not create duplicate examples just to restate existing focused mechanics for:

- basic agent prompt calls, structured responses, tool use, memory, guardrails, or evaluation;
- basic workflow start/step/pause/resume behavior;
- ordinary ESE/KVE command handling;
- ordinary View query mechanics;
- static HTTP hosting or generic frontend project setup.

Only add new examples when they clarify AI-first semantics: durable goals, bounded authority, governed policy, evidence-backed decisions, audit traces, supervision surfaces, or outcome loops.
