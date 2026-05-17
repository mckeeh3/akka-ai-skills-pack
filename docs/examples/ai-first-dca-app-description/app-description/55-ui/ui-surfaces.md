# AI-First UI Surfaces

## Purpose

This file defines the DCA vertical reference UI contract for delegated copier operations. It extends the canonical secure AI-first SaaS seed surfaces with DCA-specific supervision for supplies, lifecycle, service, billing, onboarding, offboarding, policy governance, and audit/outcome review.

The browser UI is not optional for a generated DCA app. It is organized around objectives, active delegated work, policy boundaries, human decisions, audit traces, and outcomes rather than CRUD tables as the primary frame.

Design reference:

- selected style: `atlas-ops-supervisory-console`
- authoritative style file: `style-guide.md`
- canonical style doctrine: `../../../../../docs/web-ui-style-guide.md`

## Cross-surface rules

- Backend authorization is authoritative for every protected screen, query, stream, and action; frontend navigation is never authorization.
- Every protected browser action or query links to a governed capability in `../10-capabilities/` and must preserve the same `AuthContext`, tenant/customer scope, validation, idempotency, approval, and audit semantics.
- Consequential agent work must resolve into durable goals, plans, decision cards, approvals, policy proposals, traces, outcomes, or audited side effects.
- Decision surfaces must expose recommendation, evidence, risk, confidence, impact, policy trigger, alternatives, allowed actions, stale-state status, and trace links before action submission.
- Routine activity may be compressed in briefings, but every summary item must drill down to trace facts.
- UI tests must cover loading, empty, error, success, authorization denial, stale/realtime, idempotent duplicate submit, frontend secret-boundary, and trace-link behavior for realized screens.

## Navigation model

Primary navigation should expose the way humans operate the dealer business:

1. `Owner Brief` — catch up on material work, pending decisions, outcome deltas, and admin/security attention items (`CAP-09`, `CAP-10`, `CAP-00`).
2. `Mission Control` — supervise active customer, device, DCA, supply, service, billing, onboarding, and offboarding objectives (`CAP-01` through `CAP-10`).
3. `Approvals & Exceptions` — decide policy-bound recommendations and blocked workflow gates (`CAP-03`, `CAP-04`, `CAP-05`, `CAP-06`, `CAP-07`, `CAP-08`).
4. `Supplies Autopilot` — review the first-slice supply automation queue, recommendations, shipments, suppressions, and outcomes (`CAP-03`).
5. `Lifecycle Workbench` — inspect or launch onboarding, service, billing, offboarding, and retention plans (`CAP-01`, `CAP-04`, `CAP-05`, `CAP-06`, `CAP-07`).
6. `Policy Center` — edit, simulate, and commit governed policies, thresholds, examples, and proposals (`CAP-08`).
7. `Audit & Outcomes` — investigate work traces, decision traces, policy invocations, data access, admin audit, and outcome links (`CAP-10`, `CAP-00`).
8. `Administration` — manage sign-in context, users, invitations, roles/memberships, access review, support access, tenant/customer settings, and admin audit (`CAP-00`).

Record detail pages may exist, but they should be reached from objectives, traces, decisions, queues, or admin searches rather than becoming the main product frame.

## Surface catalog

| Surface | Primary roles | Capability links | Required backing views/APIs | Test links |
|---|---|---|---|---|
| Owner Brief | Dealer owner, Outcome owner, Supervisor | `CAP-09`, `CAP-10`, `CAP-00` | digest summary view, pending decisions by stakes, material events, admin/security attention items, outcome deltas, `/api/realtime/stream` topics | acceptance owner-brief checks, operational outcome/trace checks, authorization denial |
| Mission Control | Supervisor, Exception handler | `CAP-01` through `CAP-10` | active objectives, agent activity stream, lifecycle gates, risk clusters, trace links, SSE/WebSocket updates | realtime/stale, scoped dashboard, trace-link, tenant-isolation |
| Approvals & Exceptions | Reviewer, Approver, Exception handler | `CAP-03` through `CAP-08` | risk-ranked decision queue, decision detail, approve/reject/modify/defer/escalate/request-evidence APIs | approval bypass, stale version conflict, idempotent action, audit trace |
| Supplies Autopilot | Supplies owner, Inventory owner, Supervisor | `CAP-03`, consumes `CAP-02`, links `CAP-10` | supply risk queue, recommendation/evidence views, auto-ship history, suppressed shipments, decision cards, shipment trace drawer | supplies acceptance, policy-gated automation, suppression, integration failure, outcome follow-up |
| Lifecycle Workbench | Intent author, Supervisor | `CAP-01`, `CAP-04`, `CAP-05`, `CAP-06`, `CAP-07` | plan creation/review, gate status, launch/pause/resume APIs, lifecycle views, policy version binding | lifecycle gate, invalid transition, approval, exception, trace, tenant-scope |
| Policy Center | Policy owner, Dealer owner, Auditor | `CAP-08` | versioned policy editor, proposals, simulation/replay results, commit/discard/rollback APIs | proposal-not-activation, simulation/replay, rollback, policy-version citation |
| Audit & Outcomes | Auditor, Policy owner, Outcome owner | `CAP-10`, `CAP-00` | trace search, decision provenance, policy invocation timeline, outcome reports, redacted export where permitted | audit search, redaction, support-access, outcome-link, restricted-audit |
| Administration | Tenant Admin, Customer Admin, SaaS Owner support, Auditor | `CAP-00` | `/api/me`, UserDirectoryView, InvitationView, MembershipView, AccessReviewQueueView, AdminAuditView, support-access and tenant/customer settings APIs | tenant isolation, disabled user, role/scope denial, invite lifecycle, support access, billing boundary, frontend secret-boundary |

## Mandatory foundation administration screens

These surfaces remain present alongside DCA operational surfaces because the DCA vertical reference extends the secure SaaS seed rather than replacing it.

| Screen | Purpose | Capability/API needs | Required checks |
|---|---|---|---|
| Sign-in and invitation acceptance | authenticate through WorkOS/AuthKit and complete invite-based first login | WorkOS browser auth, `POST /api/invitations/accept`, no privileged self-registration | invite token/context validation, expired/revoked invite denial, no frontend secrets |
| Context selector | select active tenant/customer context | `GET /api/me`, selected `AuthContext` persistence | disabled membership denial, tenant/customer isolation |
| Profile / Preferences | edit browser-safe profile and UI mode | `GET /api/me`, `PUT /api/me/preferences` | user-scoped updates, audit where sensitive |
| Admin Users | search/manage users and account status | user directory query, profile edits, disable/reactivate actions | role/scope denial, last-admin protections, AdminAuditEvent |
| Admin Invitations | create, resend, revoke, inspect delivery status | InvitationView and invite lifecycle APIs | idempotency, delivery failure visibility, audit, no raw-token exposure |
| Roles / Memberships | manage tenant/customer roles and membership lifecycle | MembershipView, role assignment APIs | privilege escalation denial, least-privilege evidence, decision card for risky changes |
| Access Review | resolve stale/risky/expiring access | AccessReviewQueueView, access-review agents, decision links | support-access expiry, dormant admin, risky role combo, audit |
| Support Access | grant/revoke/review time-limited SaaS Owner support access | support-access workflow/API, AdminAuditView | reason/expiry required, tenant/customer scope, revocation and denial tests |
| Admin Audit | search security/admin events | AdminAuditView, admin summary agent where permitted | redaction, support-access visibility, audit-summary trace links |
| Tenant/Customer Settings | inspect tenant/customer boundaries and safe settings | tenant/customer settings query/update APIs | billing-boundary separation, scope denial, audit |

## First-slice DCA screen set: Supplies Autopilot

The initial DCA implementation slice should make these screens concrete before expanding to service, billing, onboarding, and offboarding.

### Supplies Command Center

Purpose: show whether routine supply automation is healthy and what needs human attention.

Capability/API links:

- capability: `CAP-03` Supplies Autopilot, with evidence from `CAP-02` Telemetry Intelligence and trace/outcome links through `CAP-10`.
- browser APIs: scoped supply-risk queue, recommendation/evidence detail, auto-ship history, suppressed-shipment list, decision-card links, shipment trace lookup, and `/api/realtime/stream` topics for recommendations, decisions, shipments, suppressions, and agent activity.
- tests: CAP-03 acceptance, policy-gated auto-ship, suppression/no-op, integration-failure visibility, stale realtime, authorization denial, and outcome follow-up.

Required sections:

- objective banner for `GOAL-02` with active policy version and selected tenant/customer context;
- supply-risk metrics: devices below threshold, projected stockouts, auto-shipments prepared, suppressed shipments, pending approvals;
- agent activity stream with `Supplies Agent`, `DCA Monitoring Agent`, `Contract and Policy Agent`, and `Inventory Agent` events;
- pending decision cards ranked by depletion urgency, cost, customer impact, and policy risk;
- recent auto-shipments with trace links, idempotency keys, safe supplier/order references, and outcome status;
- suppressed shipments with explicit reason, policy clause, next recheck time, and safe-default status.

### Supply Decision Card

Purpose: allow a human to decide abnormal, high-cost, constrained, offboarding, or ambiguous shipment recommendations.

Capability/API links:

- capability: `CAP-03` approval/proposal surface with policy and evidence links to `CAP-08`, `CAP-02`, and `CAP-10`.
- browser APIs: decision detail, evidence item lookup, approve/reject/modify/defer/escalate/request-more-evidence actions with `expectedVersion`, `idempotencyKey`, and correlation id.
- tests: approval bypass denial, stale decision conflict, duplicate-submit idempotency, audit/work trace creation, and trace/outcome link visibility.

Required sections:

- recommendation summary and proposed side effect;
- device/customer lifecycle context;
- policy triggers such as `SUP-2.0`, `SUP-3.0`, `SUP-4.0`, or `SUP-5.0`;
- evidence: telemetry, depletion forecast, contract entitlement, inventory/cost, customer preference, recent service/offboarding context;
- risk, confidence, impact, alternatives considered, and known gaps;
- actions: approve, reject, modify, defer, escalate, request more evidence, create policy proposal/reference example;
- trace and outcome follow-up links.

### Shipment Trace Drawer

Purpose: explain why a shipment was prepared, approved, suppressed, blocked, sent, failed, or later judged useful/not useful.

Capability/API links:

- capability: `CAP-03` trace surface backed by `CAP-10` audit/outcome review.
- browser APIs: trace detail, related decision/work/policy/outcome links, redacted external-side-effect reference lookup.
- tests: trace completeness, redaction, support-access scoping, integration failure visibility, and outcome-link checks.

Required timeline events:

- telemetry refresh;
- depletion forecast;
- entitlement and lifecycle checks;
- inventory/supplier lookup;
- policy invocations;
- agent recommendation;
- human decision when applicable;
- supplier/order integration result;
- delivery/outcome follow-up.

## State and realtime behavior

- Decision queues should update through SSE or WebSocket when recommendations, approvals, evidence, workflow states, or admin/access-review items change.
- A decision card open in the browser must show stale-state warnings if new evidence, a newer version, or a conflicting human action arrives.
- Approve/reject/modify actions must use optimistic UI only after the backend accepts the command; otherwise show the workflow-safe error state and keep the card pending.
- Mutating forms must include idempotency keys and disable duplicate submit while preserving retry after safe failure.
- Protected-route errors must distinguish unauthenticated, forbidden, hidden/not-found, validation, conflict/stale, and safe server failure states using the common API error envelope expected by seed UI contracts.
- Routine activity can be compressed in summaries, but each summary item must drill down to trace facts.

## Accessibility and responsiveness

- Approval actions require clear labels, keyboard access, focus management, and confirmation for irreversible or high-impact actions.
- Evidence, policy clause, risk, confidence, impact, trace links, and authority boundary fields must remain visible on narrow screens before action controls.
- Color is not the only risk indicator; include text labels, icons, and ordering.
- Long timelines and evidence lists should preserve semantic headings and progressive disclosure.
- Administration screens must keep destructive or privilege-changing controls behind explicit labels, confirmations, focus management, and post-action audit feedback.

## Akka and web UI routing

- Read models for queues, mission control, admin lists, trace search, and outcomes -> `akka-views`.
- Decision, workflow, admin, invitation, support-access, policy, and supplies actions -> `akka-http-endpoints` with component clients and JWT/request-context authorization.
- Realtime queues and activity -> HTTP SSE or WebSocket plus `akka-web-ui-realtime`.
- Frontend project -> `akka-web-ui-apps`, `akka-web-ui-state-rendering`, `akka-web-ui-forms-validation`, `akka-web-ui-accessibility-responsive`, and `akka-web-ui-testing`.
- Future realization must read `style-guide.md`, `../10-capabilities/capabilities-index.md`, the relevant capability contract, `../30-tests/test-index.md`, and `../70-traceability/capability-to-layer-map.md` before generating UI code.
