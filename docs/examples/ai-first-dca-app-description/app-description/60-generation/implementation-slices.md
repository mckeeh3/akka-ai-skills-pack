# Implementation Slices

## Purpose

This file translates the app-description example into future realization slices. It is not a request to generate runnable code in this repository; it is a reference planning asset that shows how a downstream project could sequence Akka backend, React/Vite frontend, tests, and trace/outcome work from the refreshed DCA app-description contracts.

## Slice sequencing principles

- Deliver vertical, testable AI-first behavior rather than layer-only increments.
- Prove durable goals, bounded agents, policies, decisions, traces, supervision UI, and outcomes in the first slice before broadening the domain.
- Keep agents narrow; workflows coordinate; policies decide authority; humans approve consequential boundaries.
- Add views and UI surfaces only when backed by durable facts and clear actions.
- Preserve fail-safe behavior and trace completeness in every slice.
- Treat each slice as a projection from the refreshed app description. If a future implementation finds missing authority, schema, policy, trace, fixture, or integration detail, update the app-description/source spec first rather than inventing semantics in generated code.

## Current handoff source map

Future realization tasks should start from these refreshed description files before selecting Akka components or generating code:

| Concern | Current source files | Handoff use |
|---|---|---|
| secure SaaS foundation | `../10-capabilities/01-secure-tenant-user-foundation.md`, `../40-auth-security/identity-and-trust.md`, `../40-auth-security/authorization-rules.md`, `../40-auth-security/foundation-onboarding-admin-boundaries.md`, `../40-auth-security/boundary-and-surface-rules.md` | define WorkOS/AuthKit authentication, local Akka authorization, Account/Profile/Settings, Tenant/Customer scope, Membership/Role/Permission, Invitation, `/api/me`, support access, billing boundary, and admin audit before DCA automation |
| capability inventory | `../10-capabilities/capabilities-index.md` plus `../10-capabilities/02-lifecycle-orchestration.md` through `../10-capabilities/11-audit-outcome-review.md` | preserve capability ids/classes, actors/callers, protected scope, selected exposure surfaces, and known future-detail gaps |
| first executable DCA slice | `../10-capabilities/03-supplies-autopilot.md`, `../20-behavior/flows/01-supplies-autopilot-flow.md`, `../15-operating-model/policies-and-approval-gates.md`, `../15-operating-model/decisions-exceptions-and-evidence.md` | derive Supplies Autopilot workflow, policy gates, decision cards, authority, evidence, side effects, and no-op/idempotency behavior |
| tests and fixtures | `../30-tests/test-index.md`, `../30-tests/acceptance/01-foundation-and-supplies-acceptance.md`, `../30-tests/negative/01-security-and-approval-bypass.md`, `../30-tests/regression/01-idempotency-and-policy-regression.md`, `../30-tests/operational/01-audit-trace-and-outcomes.md` | turn description-level acceptance, negative, regression, operational, tenant-isolation, approval-bypass, idempotency, audit/trace, and outcome cases into executable tests; add concrete fixtures without weakening scenarios |
| UI and style | `../55-ui/ui-surfaces.md`, `../55-ui/style-guide.md` | realize mandatory foundation administration surfaces plus Supplies Command Center, Supply Decision Card, and Shipment Trace Drawer using the selected Atlas Ops supervisory console style |
| observability and traces | `../50-observability/logs-and-audit.md`, `../50-observability/traces-and-correlation.md`, `../50-observability/metrics.md`, `../50-observability/health-and-alerts.md`, `../50-observability/audit-trace-and-outcomes.md` | implement structured logs, AdminAuditEvent/work traces, correlation ids, metrics, health/alert surfaces, redaction, retention, and outcome links |
| traceability and readiness | `../70-traceability/capability-to-layer-map.md`, `../70-traceability/ai-first-coverage-map.md`, `../80-review/latest-readiness-summary.md`, `../00-system/readiness-status.md`, `../00-system/generation-policy.md` | verify affected layers, current readiness blockers, and non-runnable reference status before future executable planning |
| future implementation sprint | `../../../../../specs/ai-first-skills-pack-migration/sprints/08-executable-ai-first-reference-slice-sprint.md` | use as historical executable-slice plan for Supplies Autopilot, but refresh any task against the current source map above before coding |

## Seed foundation: authenticated full-stack app shell

Business goal: provide a secure, role-aware, Akka-hosted application foundation before broadening DCA operational automation.

Primary source references: `../10-capabilities/01-secure-tenant-user-foundation.md`, the `../40-auth-security/` layer, `../30-tests/*`, `../55-ui/ui-surfaces.md`, and `../50-observability/*`. Use `templates/ai-first-saas-starter/` for the canonical generated starter foundation and the current root security reference packages under `src/main/java/com/example/**/security` for executable WorkOS/AuthKit, JWT-protected APIs, local Akka user/account authorization, admin audit logging, and React/Vite static hosting patterns. The DCA foundation must include the current mandatory invitation lifecycle, scoped administration, support-access, billing-boundary, admin-agent recommendation, tenant-isolation, frontend secret-boundary, and audit/trace contracts rather than relying on bootstrap-only onboarding.

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
5. React/Vite AuthKit shell, same-origin bearer-token API client, role-aware navigation, invitation/admin/support-access surfaces, and Akka static hosting;
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

Primary source references: `../10-capabilities/03-supplies-autopilot.md`, `../20-behavior/flows/01-supplies-autopilot-flow.md`, `../15-operating-model/policies-and-approval-gates.md`, `../15-operating-model/decisions-exceptions-and-evidence.md`, `../30-tests/*`, `../55-ui/ui-surfaces.md`, and `../50-observability/*`.

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
7. React/Vite supplies command center and supply decision card surfaces;
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
- owner brief and mission control surfaces -> web UI app and HTTP/SSE endpoints.

Implementation task groups:

1. common activity and material-event schema;
2. command-center and digest views from workflow/decision/trace/outcome facts;
3. scheduled digest generation and bounded summary agent when digest summarization is part of the slice;
4. HTTP/SSE APIs for active objectives, agent activity, decisions, and digest windows;
5. owner brief and mission-control UI surfaces;
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

- authenticated seed-app foundation from `CAP-00`: WorkOS/AuthKit, JWT-protected APIs, `/api/me`, local Akka accounts/roles/scopes, complete invitation onboarding, bounded startup admin bootstrap, support-access lifecycle, billing-safe SaaS Owner boundary, admin audit, admin-agent recommendation surfaces, and Akka-hosted React/Vite shell;
- tenancy, roles, capability grants, selected `AuthContext`, backend authorization helper, support-access enforcement, and tenant/customer isolation boundaries shared by all endpoints, components, timers, consumers, workflows, views, tools, and UI actions;
- shared IDs, natural dedupe keys, event names, expected versions, idempotency keys, correlation ids, trace ids, outcome ids, and redaction classes;
- frontend style guide and design tokens from `../55-ui/style-guide.md`, including loading/empty/error/forbidden/stale/reconnecting states and frontend secret-boundary checks;
- integration contracts and deterministic fake adapters for DCA telemetry, ERP/fulfillment, inventory, billing, service, WorkOS/JWT, email outbox, and agent tools before runnable tests depend on provider behavior;
- evaluation/guardrail strategy for agent outputs, including deterministic structured-response fixtures, backend policy enforcement, approval-bypass tests, and trace requirements for every tool/data access;
- fixture datasets for policy thresholds (`SUP-1.0` through `SUP-5.0`, `OFF-3.0`), WorkOS test principals, tenant/customer/device assignments, contract entitlement summaries, inventory/fulfillment responses, support-access state, and outcome evidence;
- pending questions for provider payloads, concrete metric/alert thresholds, retention periods, external system contracts, and any role/capability names not already fixed in the capability contracts.

## Reference pending-task shape

A future executable-slice `specs/pending-tasks.md` created from these slices should keep tasks bounded and planning-first. Recommended task sequence:

- `TASK-DCA-SEED-001`: create executable-slice scaffold and copy the current handoff source map into the implementation spec; no code generation yet.
- `TASK-DCA-SEED-002`: define foundation domain model and authorization contracts for Account, UserProfile, UserSettings, Tenant, Customer, Membership, Role, Permission/Capability, Invitation, SupportAccessGrant, subscription/billing metadata, and AdminAuditEvent from `CAP-00`.
- `TASK-DCA-SEED-003`: implement WorkOS/JWT request context, `/api/me`, invitation acceptance/linking, centralized backend authorization, selected `AuthContext`, disabled-user denial, support-access enforcement, and billing-boundary checks.
- `TASK-DCA-SEED-004`: implement invitation, user, membership, role, access-review, support-access, admin-audit, tenant/customer settings, and billing-boundary APIs/views/workflows/timers/consumers with audit and idempotency tests.
- `TASK-DCA-SEED-005`: implement authenticated React/Vite shell, context selection, administration surfaces, admin-agent recommendation surfaces, and Akka static hosting using `../55-ui/style-guide.md`.
- `TASK-DCA-SEED-006`: add foundation security, tenant-isolation, invitation lifecycle, support-access, admin-agent boundary, frontend secret-boundary, and audit/trace executable tests from `../30-tests/*`.
- `TASK-DCA-SUP-001`: create concrete fixtures for telemetry, lifecycle, entitlement, inventory, fulfillment, policy thresholds, WorkOS/JWT principals, support-access state, agent stubs, and outcome evidence; keep provider integrations fake/deterministic unless separately specified.
- `TASK-DCA-SUP-002`: implement supply domain records, trace vocabulary, natural dedupe keys, policy clause references, and validation helpers from `CAP-03`.
- `TASK-DCA-SUP-003`: implement supply recommendation/decision event-sourced model for recommendation, decision-card state, approval/rejection/modification/deferral/escalation, suppression, shipment-prepared, stale/expired decision, no-op, and outcome linkage.
- `TASK-DCA-SUP-004`: implement `SupplyAutopilotWorkflow` with deterministic forecast/contract/policy/inventory agent/tool stubs, policy-gated auto-ship, pause/resume approval, suppression, stale-decision handling, idempotent retries, and no-op paths.
- `TASK-DCA-SUP-005`: implement supplies views, timed rechecks, telemetry/inventory/fulfillment/outcome consumers, and trace fanout for risk queues, pending decisions, auto-ship history, suppressed shipments, trace lookup, and outcome review.
- `TASK-DCA-SUP-006`: implement supplies HTTP APIs and minimal Supplies Command Center, Supply Decision Card, and Shipment Trace Drawer UI surfaces with backend authorization, idempotency keys, expected versions, stale/realtime behavior, and trace links.
- `TASK-DCA-SUP-007`: run slice-level integration, endpoint, workflow, view, agent/tool-boundary, frontend smoke, trace-completeness, idempotency, approval-bypass, tenant-isolation, and outcome-linkage tests.

Each task should load AI-first companion skills only for its concern plus the concrete Akka implementation skills for the component family being built. Do not start code generation from this handoff alone; first create or update an executable-slice spec, verify readiness against `../00-system/readiness-status.md`, and keep the generated app as a reference projection rather than the source of truth.
