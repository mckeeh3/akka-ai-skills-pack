# Implementation Slices

## Purpose

This file translates the app-description example into future realization slices. It is not a request to generate runnable code in this repository; it is a reference planning asset that shows how a downstream project could sequence Akka backend, React/Vite frontend, tests, and trace/outcome work.

## Slice sequencing principles

- Deliver vertical, testable AI-first behavior rather than layer-only increments.
- Prove durable goals, bounded agents, policies, decisions, traces, supervision UI, and outcomes in the first slice before broadening the domain.
- Keep agents narrow; workflows coordinate; policies decide authority; humans approve consequential boundaries.
- Add views and UI surfaces only when backed by durable facts and clear actions.
- Preserve fail-safe behavior and trace completeness in every slice.

## Seed foundation: authenticated full-stack app shell

Business goal: provide a secure, role-aware, Akka-hosted application foundation before broadening DCA operational automation.

Source reference: adapt the working `examples/poc-user-auth-onboarding/` proof-of-concept for WorkOS/AuthKit, JWT-protected APIs, local Akka user/account authorization, admin audit logging, and React/Vite static hosting. Treat that PoC as implementation guidance only; the DCA foundation must add the current mandatory invitation lifecycle, scoped administration, support-access, and billing-boundary contracts rather than relying on bootstrap-only onboarding.

Seed scope:

- public React/Vite frontend assets served by Akka;
- WorkOS/AuthKit browser authentication;
- frontend bearer-token calls to same-origin `/api/...` routes;
- JWT-protected Akka API endpoints;
- `/api/me` local account resolution with invitation/acceptance-context linking, pending-invite/not-invited handling, selected context, and active/disabled status;
- local Akka roles and scopes for app admin, dealer owner, operations supervisor, policy owner, auditor, customer admin, and baseline user access;
- tenant/customer authorization boundaries enforced server-side;
- bounded startup admin bootstrap from backend-only environment variables only for initial setup, not as the normal onboarding path;
- complete invitation lifecycle APIs and behavior: create account/membership intent, deliver or capture email, delivery-failure visibility, resend, revoke/cancel, expire, accept, and audit;
- admin user directory, membership, role assignment/replacement/removal, activation/disable, tenant, customer, support-access, access-review, admin-audit, and billing-boundary APIs;
- admin/security audit entries for privileged operations;
- role-aware frontend navigation as UX only, never as authorization;
- unauthorized, forbidden, disabled-account, loading, empty, and error frontend states.

Akka component map:

| Need | Suggested component family |
|---|---|
| Local user/account, tenant, customer, role assignment current state | Key Value Entity by default |
| Admin/security audit entries | Event Sourced Entity or append-only audit pattern |
| `/api/me`, admin, tenant, customer APIs | HTTP endpoints with JWT and request context |
| Central authorization checks | Plain application/security service used by endpoints and components |
| Account/admin list/search surfaces | Views when list/query needs exceed direct entity reads |
| Authenticated browser shell | React/Vite frontend project + Akka static hosting |

Implementation task groups:

1. DCA auth/security app-description files for identity/trust, authorization, agent permissions, data protection, and route boundaries;
2. user/account, tenant, customer, role, account-status, bootstrap, and admin-audit domain/components;
3. WorkOS/JWT `/api/me`, local account linking, disabled-user denial, and centralized authorization helper;
4. admin APIs for invitation lifecycle, scoped user directory, memberships, roles, status, tenants, customers, support access, access review, admin audit, billing-boundary metadata, and auditable privileged operations;
5. React/Vite AuthKit shell, same-origin bearer-token API client, role-aware navigation, invitation/admin/support-access screens, and Akka static hosting;
6. security acceptance tests for missing JWT, no privileged self-registration, first-login invitation link, delivery failure/resend/revoke/expiry, role/scope denial, support-access expiry/revocation, admin audit, frontend auth state, billing-safe redaction, and secret boundaries.

Done when a future app can demonstrate:

```text
unauthenticated browser -> public shell/login prompt; protected APIs reject
invited WorkOS user signs in -> /api/me links and activates local account only through valid invitation or membership policy
uninvited WorkOS user signs in -> no privileged self-registration; pending-invite/not-invited response
SAAS_OWNER_ADMIN -> canonical foundation role for platform setup/invites/billing-safe metadata without direct Tenant application-data access
APP_ADMIN -> DCA reference alias for SAAS_OWNER_ADMIN bootstrap capabilities only; prefer SAAS_OWNER_ADMIN in new generated apps
TENANT_ADMIN/CUSTOMER_ADMIN -> can manage only assigned scopes
DISABLED user with valid JWT -> backend rejects
frontend hidden navigation -> never substitutes for backend authorization
```

## Slice 1: Supplies autopilot foundation

Business goal: make supply fulfillment timely and policy-safe for active monitored devices.

AI-first scope:

- durable objective `GOAL-02`;
- supply depletion recommendation from bounded specialist agents;
- policy gates `SUP-1.0` through `SUP-5.0`;
- auto-ship, decision-card, and suppression paths;
- trace facts for telemetry, policy, evidence, recommendation, side effect, and outcome;
- supplies command center and supply decision card UI.

Akka component map:

| Need | Suggested component family |
|---|---|
| Supply recommendation, decision card, side-effect history | Event Sourced Entity |
| Current inventory/supplier snapshot cache | Key Value Entity, if not audit-grade |
| Supply recommendation/approval lifecycle | Workflow with pause/resume |
| Supplies queues and auto-ship history | Views |
| Depletion recheck and stale decision escalation | Timed Actions |
| Fulfillment/order integration events | Consumers or HTTP client endpoint pattern |
| Recommendation, forecast, policy explanation | Agents with structured responses |
| Browser APIs and UI hosting | HTTP endpoints + web UI app |

Implementation task groups:

1. common domain records and policy vocabulary for customer, device assignment, telemetry, supply item, recommendation, decision, trace, and outcome;
2. supply recommendation and decision-card event-sourced write models;
3. supplies workflow with auto-ship, decision-card, suppression, pause/resume, no-op, and idempotency behavior;
4. bounded agent/tool stubs or deterministic test doubles for forecast, entitlement/policy, and inventory checks;
5. views for supply risk, pending decisions, auto-ship history, and suppressed shipments;
6. endpoints for telemetry intake/test hook, recommendation review, decision actions, trace lookup, and UI APIs;
7. React/Vite supplies command center and supply decision card screens;
8. tests for success, approval, suppression, missing evidence, stale decision, retry/idempotency, trace completeness, and outcome linkage.

Done when a future app can demonstrate:

```text
fresh telemetry + active contract + normal forecast -> auto shipment prepared with trace
abnormal usage or high cost -> decision card with evidence and policy clauses
customer offboarding or unmapped contract -> shipment suppressed or escalated safely
approved/rejected decision -> workflow resumes or remains safe and outcome is measurable
```

## Slice 2: Owner mission control and digest

Business goal: let the dealer owner and operations supervisor understand active work and pending attention quickly.

AI-first scope:

- active goals and lifecycle workflows;
- agent activity stream with trace links;
- risk-ranked approval/exception queues;
- async digest that compresses routine work and surfaces material events;
- outcome deltas for supply automation and blocked lifecycle gates.

Akka component map:

- command-center read models -> Views;
- material event fanout and digest inputs -> Consumers;
- scheduled digest generation -> Timed Actions;
- briefing summarization -> Agent;
- owner brief and mission control screens -> web UI app and HTTP/SSE endpoints.

Implementation task groups:

1. common activity and material-event schema;
2. command-center and digest views from workflow/decision/trace/outcome facts;
3. scheduled digest generation and bounded summary agent when digest summarization is part of the slice;
4. HTTP/SSE APIs for active objectives, agent activity, decisions, and digest windows;
5. owner brief and mission-control UI screens;
6. tests for material event ranking, routine compression, trace links, authorization, and realtime/stale-state behavior.

## Slice 3: Policy governance and learning loop

Business goal: turn human corrections and recurring exceptions into governed policy improvements.

AI-first scope:

- versioned policy documents and stable clause ids;
- policy proposals from human decisions or agents;
- simulation/replay before material authority changes;
- human policy commit, rejection, or modification;
- policy center UI and policy impact outcomes.

Akka component map:

- policy documents, clauses, proposals, commits, and reference examples -> Event Sourced Entities;
- policy proposal/approval/replay lifecycle -> Workflow;
- replay/simulation scheduling -> Timed Actions;
- proposal drafting and ambiguity explanation -> Agents;
- governance lists and policy impact reports -> Views;
- policy center UI/API -> web UI and HTTP endpoints.

Implementation task groups:

1. policy domain and event history;
2. policy proposal workflow with simulation-required gates;
3. reference examples and precedent links from decisions;
4. governance views and policy center endpoints;
5. policy center UI with diff, examples, simulation result, and commit controls;
6. tests for proposal-not-activation, authorization, replay links, policy commit, and trace/outcome impact.

## Slice 4: Lifecycle expansion: onboarding, service, billing, offboarding

Business goal: expand from supplies into full office-device lifecycle operations without losing AI-first governance.

AI-first scope:

- onboarding plan and gate decisions;
- service dispatch recommendations and SLA risk;
- meter/billing anomaly review;
- offboarding retention/deauthorization/final-read gates;
- lifecycle command center and audit surfaces.

Akka component map:

- lifecycle histories and consequential decisions -> Event Sourced Entities;
- onboarding/service/billing/offboarding processes -> Workflows;
- DCA telemetry, service, billing, and retention integrations -> Consumers and endpoints;
- deadlines and rechecks -> Timed Actions;
- lifecycle queues, blocked gates, audit search, and outcomes -> Views;
- specialist recommendations and summaries -> Agents.

Implementation task groups:

1. lifecycle state models for customer, device, and DCA collector;
2. onboarding workflow and gate tests;
3. service workflow and SLA/replacement decision tests;
4. billing review workflow and anomaly tests;
5. offboarding workflow and retention/deauthorization tests;
6. lifecycle command-center and audit UI expansion;
7. cross-flow integration tests for lifecycle state changes that suppress or unblock supply automation.

## Cross-cutting realization requirements

Before or alongside slice work, downstream planning should create specs for:

- authenticated seed-app foundation: WorkOS/AuthKit, JWT-protected APIs, `/api/me`, local Akka accounts/roles/scopes, complete invitation onboarding, bounded startup admin bootstrap, support-access lifecycle, billing-safe SaaS Owner boundary, admin audit, and Akka-hosted React/Vite shell;
- tenancy, roles, and authority boundaries;
- shared IDs, event names, trace correlation, and redaction classes;
- frontend style guide and design tokens;
- integration contracts for DCA, ERP/fulfillment, billing, and service systems;
- evaluation/guardrail strategy for agent outputs;
- pending questions for thresholds, role names, retention policy, external system contracts, and UI style if not selected.

## Reference pending-task shape

A future `specs/pending-tasks.md` created from these slices should keep tasks bounded, for example:

- `TASK-SEED-001`: update DCA auth/security app-description files;
- `TASK-SEED-002`: implement local account, tenant, customer, role, and admin-audit domain/components;
- `TASK-SEED-003`: implement WorkOS/JWT `/api/me` and centralized backend authorization;
- `TASK-SEED-004`: implement admin APIs and startup bootstrap lifecycle;
- `TASK-SEED-005`: implement authenticated React/Vite shell and Akka hosting;
- `TASK-SEED-006`: add seed security acceptance tests and PoC alignment notes;
- `TASK-SUP-001`: implement supply domain records and trace vocabulary;
- `TASK-SUP-002`: implement supply recommendation/decision event-sourced model;
- `TASK-SUP-003`: implement supplies workflow with policy-gated paths;
- `TASK-SUP-004`: implement supplies views and timed rechecks;
- `TASK-SUP-005`: implement supplies endpoints and integration stubs;
- `TASK-SUP-006`: implement supplies command-center and decision-card frontend;
- `TASK-SUP-007`: run slice-level integration, trace, and outcome tests.

Each task should load AI-first companion skills only for its concern plus the concrete Akka implementation skills for the component family being built.
