# AI-First Examples and Tests Gap List

## Purpose

This gap list records remaining reference implementation and test gaps after the AI-first migration and later cleanup. It distinguishes the canonical starter scaffold from mechanics-only examples and executable examples that still need future planning and code.

Use it to avoid hiding coverage gaps behind broad doctrine or retrofitting domain-specific or static UI examples into canonical generated-app guidance without explicit intent.

## Current coverage baseline

The repository already has strong focused examples for individual Akka substrate families:

- canonical full-core starter scaffold source under `templates/ai-first-saas-starter/**` for new generated secure AI-first SaaS apps;
- reusable workstream UI reference modules under `frontend/src/workstream/**` and installed frontend examples for shell/surface mechanics;
- agents, structured responses, tools, guardrails, evaluation, orchestration, memory/runtime state, and streaming;
- workflows with approval, pause/resume, compensation-style flow, notifications, deadlines, and endpoint integration;
- Event Sourced Entities, Key Value Entities, Views, Consumers, Timed Actions, HTTP/gRPC/MCP endpoints, and Akka-hosted web UI delivery patterns;
- a low-agentic purchase-request app-description reference under `docs/examples/purchase-request-app-description/` for description/planning mechanics only.

The starter scaffold is the canonical end-to-end implementation baseline. Former DCA/supplies domain-specific assets were removed because the starter, seed app-description, workstream UI reference, governed-agent examples, and focused Akka substrate examples now cover the reusable skills-pack value without carrying a domain-specific vertical app.

## Priority key

- **P0 required**: needed for future agents to confidently generate AI-first SaaS apps without guessing core operating-model semantics.
- **P1 important**: closes recurring implementation/test gaps once the starter path exists.
- **P2 breadth**: useful breadth examples, but not required before the first complete AI-first reference path.

## Completed canonical starter coverage

### Full-core secure AI-first SaaS starter scaffold

Status: **canonical starter implementation baseline, not full-core complete by itself until the target project renders/adapts the frontend and validates the local runtime path**.

Reference:
- source repository: `templates/ai-first-saas-starter/**`
- installed pack: `resources/templates/ai-first-saas-starter/**`
- scaffold command: `.agents/bin/scaffold-ai-first-saas-starter.sh`
- final acceptance: `specs/ai-first-saas-starter-app-template/final-acceptance-review.md`

Qualification: the scaffold currently renders the backend starter foundation plus `app-description/` and `specs/` seeds. The validated React/Vite workstream UI remains under `frontend/**` as the repository/installed-pack frontend reference and must be copied/adapted during extension until a rendered `templates/ai-first-saas-starter/frontend/**` template is added. A generated target project is not full-core complete until the adapted UI is wired to real backend APIs/workstream endpoints, provider/security gaps fail closed, tests pass, and the local Akka/API/UI smoke path is recorded.

Covered starter scope:

- selected Java base package rendering, Maven skeleton, and starter scaffold rules;
- secure SaaS foundation records and `/api/me` AuthContext bootstrap;
- invitation/user-admin backend services, views/seams, captured outbox/Resend boundary, and tests;
- governed agent records, seed import, prompt assembly, authorized `readSkill`, behavior-editing flow, and tests;
- workstream backend API foundation for Access/Profile, User Admin, Agent Admin, Audit/Trace, and Governance/Policy surfaces;
- validated frontend workstream reference tests/typecheck/build under `frontend/**`, not yet embedded directly in scaffold output.

Canonical routing rule: for new full-core generated apps, scaffold and extend this starter, then materialize/adapt the validated workstream frontend as part of the first frontend extension step. Use purchase-request, shopping-cart, and standalone static UI examples only for explicitly labeled mechanics after the starter/foundation architecture is clear.

## Completed app-description coverage

### Secure AI-first SaaS seed app-description

Status: **canonical app-description seed reference**.

Reference: `docs/examples/ai-first-saas-seed-app-description/`.

Distinction: this seed is a description/reference contract for generated-app meaning, structure, UI style, readiness, and review. The canonical runnable implementation baseline is the scaffold template under `templates/ai-first-saas-starter/` after placeholders are rendered.

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
- starter scaffold baseline: `templates/ai-first-saas-starter/**`

Future breadth may add more vertical React/Vite workstream examples, but static resource examples are not canonical generated SaaS UI structure.

### P1: trace fanout and digest examples

Status: **partially covered by existing trace/audit examples; digest curation remains open**.

Pattern notes:

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
