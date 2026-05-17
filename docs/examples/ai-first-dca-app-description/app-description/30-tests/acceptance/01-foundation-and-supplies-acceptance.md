# Acceptance Tests: Foundation and Supplies Autopilot

## Behavior or capabilities under test

- linked capability ids/classes:
  - `CAP-00` secure tenant/user foundation: command, read/evidence, workflow, scheduled, trace/audit, policy/governance
  - `CAP-03` supplies autopilot: workflow, proposal, approval, command, read/evidence, reactive, scheduled, trace/audit
- exposure surfaces:
  - browser UI, JWT-protected HTTP APIs, `/api/me`, scoped views, workflows, timers, consumers, backend integration callers, and bounded agent tools

## Secure foundation acceptance cases

- Given a SaaS Owner Admin with platform authority, when they create a Tenant, assign billing-safe subscription state, and invite an initial Tenant Admin, then the Tenant, subscription metadata, Invitation, and AdminAuditEvent are created without exposing Tenant application data to SaaS Owner users.
- Given a Tenant Admin, when they create Customer organizations, invite employees and Customer Admins, and assign scoped roles/capabilities, then only valid scoped Memberships are activated after invitation acceptance.
- Given a Customer Admin, when they invite Customer users and manage customer-scoped memberships, then the resulting memberships are limited to the selected Tenant/Customer context.
- Given an authenticated user with active memberships, when the browser calls `/api/me`, then the response contains only browser-safe Account, UserProfile, UserSettings, active memberships, selected AuthContext, roles/capabilities, context-switch options, and no provider secrets or unauthorized scopes.
- Given a Tenant Admin, when they grant, review, revoke, or let support access expire, then support access remains Tenant-scoped, time-limited, visible to tenant admins, and audited.
- Given an authorized admin, when they search Users, Memberships, Invitations, Access Review, and Admin Audit views, then results are scoped, redacted, paginated, and searchable without requiring the caller to know internal ids.
- Given an admin-assistant agent, when it drafts invites, recommends least-privilege roles, scores admin risk, or summarizes audit evidence, then it creates only recommendations, summaries, or decision cards and not unapproved role, support-access, policy, or account side effects.

## Supplies autopilot acceptance cases

- Given an authorized tenant operations user or workflow with `supplies.recommendation.create`, fresh telemetry, active assignment, active customer lifecycle, contract entitlement, compatible customer preferences, and low-risk policy evidence, when `EvaluateSupplyNeed` runs, then the system creates or refreshes one `SupplyRecommendation` with cited policy clauses, evidence summaries, confidence/risk, allowed next actions, trace links, and no duplicate decision card.
- Given active policy permits bounded automation for the low-risk recommendation, when fulfillment preparation is reached, then the system prepares exactly one fulfillment command through the backend integration boundary and records the policy basis, idempotency key, safe external reference, and trace link.
- Given a recommendation that triggers abnormal-consumption, high-cost, substitution, stale-evidence, offboarding, missing-entitlement, preference-conflict, or lifecycle ambiguity gates, when evaluation completes, then a `SupplyDecisionCard` is created with evidence, risk, confidence, impact, policy triggers, alternatives, allowed reviewer actions, and no shipment side effect.
- Given an authorized reviewer with `supplies.shipment.approve`, when they approve, reject, modify, defer, escalate, or request more evidence on a supply decision card, then the workflow applies only that reviewed action, records the reviewer reason, and updates queue/status views.
- Given policy clearly denies shipment, when the supplies workflow evaluates the case, then the system records a durable suppression reason, emits trace evidence, and does not invoke fulfillment.
- Given a scoped supplies queue or recommendation detail request, when the caller has `supplies.evidence.read`, then the response contains only tenant/customer-scoped, redacted recommendation, decision, inventory, entitlement, fulfillment, and outcome evidence.
- Given the Supplies Coordinator Agent uses tools, when it drafts a recommendation or explanation, then the tool calls expose only scoped/redacted evidence and any consequential action still routes through backend policy and approval checks.

## UI acceptance cases

- Given the browser user has foundation admin capabilities, when they navigate the app shell, then sign-in, context selection, profile/settings, Users, Invitations, Roles/Memberships, Access Review, Support Access, Admin Audit, Tenant/Customer Settings, admin-agent recommendation queues, and decision cards are visible according to `/api/me` capabilities.
- Given the browser user has supplies capabilities, when they open Supplies Autopilot, then queues, evidence detail, recommendation review, decision cards, fulfillment status, suppression reason, trace links, loading/empty/error states, and stale/reconnecting states are available.
- Given an action is hidden in the UI because the current AuthContext lacks capability, when the same operation is attempted through an API replay or modified client request, then backend authorization still denies it.

## Outcome acceptance cases

- Given fulfillment, suppression, or approval completes, when delivery, stock, cost, customer feedback, depletion avoidance, or missed-depletion evidence arrives later, then the outcome link connects back to the original recommendation/card/order, policy clauses, actor/caller, and trace ids.
