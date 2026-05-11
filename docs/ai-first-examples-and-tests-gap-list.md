# AI-First Examples and Tests Gap List

## Purpose

This gap list records remaining reference implementation and test gaps after the AI-first migration. It distinguishes completed non-runnable app-description coverage from executable examples that still need future planning and code.

Use it to avoid hiding coverage gaps behind broad doctrine or retrofitting existing conventional examples into agentic ones without explicit intent.

## Current coverage baseline

The repository already has strong focused examples for individual Akka substrate families:

- agents, structured responses, tools, guardrails, evaluation, orchestration, memory/runtime state, and streaming;
- workflows with approval, pause/resume, compensation-style flow, notifications, deadlines, and endpoint integration;
- Event Sourced Entities, Key Value Entities, Views, Consumers, Timed Actions, HTTP/gRPC/MCP endpoints, and Akka-hosted web UI delivery patterns;
- a low-agentic purchase-request app-description reference under `docs/examples/purchase-request-app-description/`;
- a completed non-runnable AI-first app-description reference under `docs/examples/agent-first-dca-app-description/`.

The DCA app-description reference now covers durable goals, bounded agents, retained human authority, policies, approval gates, decision cards, audit/work/decision traces, supervision UI surfaces, outcome metrics, traceability, and realization slice sequencing. It is not runnable application code.

## Priority key

- **P0 required**: needed for future agents to confidently generate AI-first SaaS apps without guessing core operating-model semantics.
- **P1 important**: closes recurring implementation/test gaps once the first executable slice exists.
- **P2 optional**: useful breadth examples, but not required before the first complete AI-first reference path.

## Completed app-description coverage

### DCA AI-first worked app-description

Status: **complete as a non-runnable reference asset**.

Reference: `docs/examples/agent-first-dca-app-description/`.

Completed coverage:

- durable goals, constraints, success criteria, execution-plan intent, and outcome links;
- bounded coordinator/specialist/evaluator agent roles with explicit authority limits;
- retained human authority, approvals, exceptions, and escalation rules;
- policy clauses, thresholds, permissions, simulations, proposals, and human-governed commits;
- decision cards with evidence, risk, confidence, impact, alternatives, and actions;
- audit/work/decision traces covering tool calls, data access, policy invocations, approvals, and outcomes;
- supervision-oriented UI surfaces: goal-to-execution workbench, command center, decision review, governance/learning, digest, and audit/trace;
- traceability from operating-model artifacts to behavior, tests, security, observability, UI, and realization slices.

Placement note: audit/trace/outcome meaning is introduced by `15-operating-model/`, while concrete trace events, metrics, privacy/access rules, and tests implied by observability live in `50-observability/audit-trace-and-outcomes.md`.

## Required executable gaps

### P0: first executable AI-first implementation slice derived from the worked app-description

Missing reference: runnable or implementation-ready slice-level specs showing how the DCA app-description becomes bounded Akka implementation tasks.

Candidate first slice: `docs/examples/agent-first-dca-app-description/app-description/60-generation/implementation-slices.md` Slice 1, Supplies autopilot foundation.

Required coverage:

- durable goal/objective and trace vocabulary for supplies;
- supply recommendation and decision-card write model;
- workflow path for auto-ship, approval-required decision card, suppression, pause/resume, no-op, and idempotency behavior;
- bounded agent/tool stubs or deterministic test doubles for forecast, entitlement/policy, and inventory checks;
- views/endpoints/UI surfaces for supply risk, pending decisions, auto-ship history, suppressed shipments, and trace lookup;
- tests for success, approval, suppression, missing evidence, stale decision, retry/idempotency, trace completeness, and outcome linkage.

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

Current web UI guidance covers React/Vite integration and UI quality, but there is no concrete executable AI-first screen reference.

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

Only add new examples when they clarify executable AI-first semantics: durable goals, bounded authority, governed policy, evidence-backed decisions, audit traces, supervision surfaces, or outcome loops.
