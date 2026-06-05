# AI-First Examples and Tests Gap List

## Purpose

This gap list records remaining reference implementation and test gaps after the AI-first migration and later cleanup. It distinguishes the canonical core app baseline from mechanics-only examples and executable examples that still need future planning and code.

Use it to avoid hiding coverage gaps behind broad doctrine or retrofitting domain-specific or static UI examples into canonical generated-app guidance without explicit intent.

## Current coverage baseline

The repository already has strong focused examples for individual Akka substrate families:

- canonical full-core core app baseline source under this repository's runnable core app root for new generated secure AI-first SaaS apps;
- reusable workstream UI reference modules under root `frontend/src/workstream/**` for shell/surface mechanics;
- agents, structured responses, tools, guardrails, evaluation, orchestration, memory/runtime state, and streaming;
- workflows with approval, pause/resume, compensation-style flow, notifications, deadlines, and endpoint integration;
- Event Sourced Entities, Key Value Entities, Views, Consumers, Timed Actions, HTTP/gRPC/MCP endpoints, and Akka-hosted web UI delivery patterns;
- a low-agentic purchase-request app-description reference under `docs/examples/purchase-request-app-description/` for description/planning mechanics only.

The core app baseline is the canonical end-to-end implementation baseline. Former DCA/supplies domain-specific assets were removed because the core app, root app-description, workstream UI reference, governed-agent examples, and focused Akka substrate examples now cover the reusable skills-pack value without carrying a domain-specific vertical app.

## Priority key

- **P0 required**: needed for future agents to confidently generate AI-first SaaS apps without guessing core operating-model semantics.
- **P1 important**: closes recurring implementation/test gaps once the starter path exists.
- **P2 breadth**: useful breadth examples, but not required before the first complete AI-first reference path.

## Completed canonical core app coverage

### Full-core secure AI-first SaaS core app baseline

Status: **canonical implementation baseline, extended by downstream forks and validated through the local runtime path**.

Reference:
- source repository and downstream forks: the runnable core app repository root
- harness skills-library install: skills plus referenced pack docs/examples/templates/tools are installed under `.agents/skills`; no duplicate full-app source is installed

Qualification: generated target projects are not complete until backend APIs, workstream endpoints, provider/security failure modes, tests, and the local Akka/API/UI smoke path are recorded for the selected scope.

Covered core app scope:

- fixed `ai.first` root package and Maven skeleton;
- secure SaaS foundation records and `/api/me` AuthContext bootstrap;
- invitation/user-admin backend services, views/seams, captured outbox/Resend boundary, and tests;
- governed agent records, seed import, prompt assembly, authorized `readSkill`, behavior-editing flow, and tests;
- workstream backend API foundation for Access/Profile, User Admin, Agent Admin, Audit/Trace, and Governance/Policy surfaces;
- React/Vite workstream frontend under root `frontend/**`; this remains application source in the repository workspace and is not exported into `.agents` by the harness install.

Canonical routing rule: for new full-core generated apps that need an implementation baseline, fork or clone this repository, then extend the target workspace. Use purchase-request, shopping-cart, and standalone static UI examples only for explicitly labeled mechanics after the core-app architecture is clear.

## Completed app-description coverage

### Secure AI-first SaaS core app-description

Status: **canonical app-description baseline in the target/core app workspace**.

Reference: target project `app-description/`.

Distinction: this tree is a description/reference contract for generated-app meaning, structure, UI style, readiness, and review. The canonical runnable implementation baseline is the root core app itself.

Covered coverage:

- secure SaaS foundation and generated-app structure;
- functional/context-area agents and workstream shell semantics;
- split `12-workstreams/` application-model layer;
- split `55-ui/` browser-realization layer;
- capability maps, Akka realization maps, readiness, and review posture.

## Required executable gaps

### P0: first domain-neutral executable AI-first decision/work trace slice

Status: **open after removal of the former domain-specific supplies slice**.

Need: a compact domain-neutral fixture that demonstrates durable evidence-backed decisions, retained human authority, policy gates, trace completeness, idempotency, and outcome linkage without depending on an app-specific vertical such as supplies, finance, procurement, or fleet operations.

Suggested shape:

- small Event Sourced Entity for `DecisionRecord` or `WorkDecision`;
- workflow for recommendation → approval-required/auto-allowed → outcome link;
- view for pending decisions and trace lookup;
- endpoint tests proving authorization, idempotency, trace completeness, and no unauthorized side effect;
- optional deterministic agent/tool stub for recommendation drafting.

### P0: explicit AI-first acceptance/evaluation test patterns

Status: **partially covered by starter foundation and governed-agent tests; domain-neutral decision acceptance tests remain open**.

Needed patterns:

- agent/workflow authority boundary tests;
- decision-card completeness tests for evidence, risk, confidence, impact, alternatives, policy clauses, trace links, and outcome links;
- workflow pause/resume tests for human approval/rejection;
- audit-trace tests proving policy, decision, approval/rejection/suppression, and outcome identifiers are persisted or projected;
- idempotency tests proving duplicate requests/timers do not duplicate side effects.

## Important gaps

### P1: audit-grade Event Sourced Entity example for AI-first decisions or policies

Status: **open for a compact domain-neutral example**.

Pattern notes:

- domain events that preserve temporal reasoning and accountability;
- command validation around authority, policy version, evidence references, and outcome links;
- replay tests that prove state reconstructs decision or policy history.

### P1: command-center and decision-queue View examples

Status: **open for a compact domain-neutral queue**.

Pattern notes:

- active goals/plans by owner/status/risk;
- pending approvals/exceptions sorted by urgency using valid Akka View query constraints;
- audit or decision search with explicit filters instead of optional-filter `OR` patterns;
- tests for projection updates and endpoint/UI read behavior.

### P1: AI-first web UI reference surface

Status: **covered by the workstream UI reference and starter routing; standalone static UI remains mechanics-only**.

References:
- canonical UI architecture: `docs/workstream-ui-reference-architecture.md`
- reusable frontend modules: `frontend/src/workstream/**`
- core app baseline: this repository's runnable core app root

Future breadth may add more vertical React/Vite workstream examples, but static resource examples are not canonical generated SaaS UI structure.

### P1: trace fanout and digest examples

Status: **partially covered by existing trace/audit examples; digest curation remains open**.

Pattern notes:

- consumer that enriches or republishes material trace events for supervision/audit;
- timed action that builds periodic digest inputs or triggers stale-goal rechecks;
- idempotency tests for duplicate/replayed trace events and timer replacement behavior.

## Optional gaps

### P2: MCP AI-client governance example

Status: **partially covered for side-effecting tool-boundary mechanics**.

Current reference coverage:

- `GovernedRefundMcpEndpoint` exposes the side-effecting `refund.request_consequential` capability as `request-governed-refund` with service ACL, stable MCP tool id, tenant/customer scope checks, idempotency, approval-required behavior, and trace emission.
- `GovernedRefundToolBoundaryIntegrationTest` covers ungranted MCP denial, approval-required behavior, duplicate idempotency handling, and no direct side-effect execution.

Remaining useful future breadth: MCP endpoints exposing approved resources/tools for goals, policies, traces, or broader bounded action requests.

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
