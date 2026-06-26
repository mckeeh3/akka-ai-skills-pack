# Workstream: User Admin

## Purpose

Give authorized administrators an AI-first access operations command center for SaaS Owner Admin users, customer-facing Organizations, Organization Admin users, scoped users, memberships, invitations, roles/capabilities, support access, access-review work, identity exceptions, and admin audit evidence across three explicit admin levels:

1. **SaaS Owner / App Admin** — the app-owner administration level. SaaS Owner Admins manage other SaaS Owner/App Admin users, create and maintain customer-facing Organizations, and invite/manage Organization Admin users for those Organizations. App-owner authority is platform scoped and does not automatically grant tenant employee, customer admin, customer user, tenant application-data, support-access, or billing-derived authority unless a backend policy explicitly grants that selected context.
2. **Tenant / Organization Admin** — the SMB Organization administration level. Organization Admins create and maintain Customers inside their selected Organization/Tenant, invite/manage Customer Admin users for those Customers, and manage the tenant's employee access. Organization Admin authority is tenant scoped and cannot cross into other tenants, sibling Organizations, or SaaS Owner administration.
3. **Customer Admin** — the SMB customer's customer administration level. Customer Admins manage Customer Users within their assigned customer scope. Customer Admin authority cannot manage tenant employees, tenant-level roles/settings, other customers, or App Admin/Tenant Admin accounts.

The intended hierarchy is `SaaS Owner/App Admin -> Organization/Tenant Admin -> tenant employees and Customer Admins -> Customer Users`, with every step constrained by selected `AuthContext`, backend capability grants, resource ownership, redaction, and audit policy.

The workstream explicitly covers these access-administration areas:

- **Managing SaaS Owner Admins** — invite, inspect, activate/reactivate, disable/remove, and preserve last-owner-admin protection for app-owner administrators.
- **Managing Organizations** — create, inspect, rename, suspend, archive, and reactivate customer-facing Organizations backed by Tenant boundaries without granting tenant app-data/support/billing authority; archive is terminal normal-administration closure, not hard delete.
- **Managing Organization Admins** — bootstrap the first Organization Admin after Organization creation, invite additional `TENANT_ADMIN` users, and manage Organization Admin memberships/invitations under selected Organization/Tenant scope.
- **Managing Customers** — create, inspect, rename/update, suspend, archive, and reactivate Customer records inside the selected Organization/Tenant without leaking sibling-customer facts; archive is terminal normal-administration closure, not hard delete.
- **Managing Customer Admins** — bootstrap the first Customer Admin after Customer creation, invite additional `CUSTOMER_ADMIN` users, and manage Customer Admin memberships/invitations under selected Customer scope.
- **Managing users** — find, inspect, activate/reactivate, disable, and understand scoped user details at the App Admin, Tenant Admin, or Customer Admin level allowed by policy.
- **Managing memberships** — add, suspend, reactivate, or remove a user's membership in an app-owner, tenant, or customer context while preserving tenant/customer boundaries and last-admin protections.
- **Managing invitations** — create, resend, revoke, track, expire, and troubleshoot invitations for SaaS Owner Admins, Organization/Tenant Admins, tenant employees, Customer Admins, or Customer Users according to the caller's admin level.
- **Managing roles/capabilities** — preview and change role/capability assignments, show capability deltas and affected workstreams, enforce role-escalation and approval gates, and prevent authority expansion outside the caller's scope.
- **Managing support access** — grant, revoke, extend, view, and audit time-bounded support access when tenant policy permits it; support access does not replace App Admin, Tenant Admin, or Customer Admin authority.
- **Managing access reviews** — start, monitor, review, accept, reject, or resolve access-review tasks and recommendations; review output informs deterministic admin actions but does not directly mutate access.
- **Managing identity exceptions** — review identity-link/relink exceptions, provider-account mismatches, disabled or stale identity state, and approved recovery flows without exposing raw provider internals.
- **Managing admin audit evidence** — expose browser-safe audit excerpts, trace links, denial/no-op evidence, mutation history, and investigation context for authorized admins and auditors.

The workstream helps admins answer:

1. **Who needs access administration attention in this selected tenant/customer context?**
2. **Which invitations, memberships, roles, support grants, identity links, or review items can I safely act on?**
3. **What policy, last-admin, approval, support-access, and tenant/customer-scope boundaries govern each action?**
4. **What evidence and audit/work traces explain a recommendation, denial, no-op, or completed change?**

User Admin is not a generic CRUD console. It is a role-authorized functional-agent workstream whose structured surfaces are backed by capability `user-and-access-administration`, selected `AuthContext`, backend authorization, idempotency, approval policy, and durable traces.

## Functional agent

Owns `user-admin-agent` as its exactly-one user-facing functional-agent binding. The legacy `agent-user-admin` id is retired; it may appear only in compatibility/retirement notes and must not be emitted or accepted by runtime payloads, traceability, frontend routes, tests, generated clients, or surface graph edges. Runtime instances are selected-context workstream logs and surface graphs, not page sessions. Internal access-review worker/agent tasks may support this workstream, but they do not become left-rail functional agents.

## Worker roster and actor-adapter chain

User Admin uses the current skills-pack worker model. Every consequential operation must remain traceable through:

```text
worker -> execution harness -> actor adapter -> governed tool -> capability -> Akka implementation
```

Workstream worker bindings live under `workers/`:

- `user-admin.saas-owner-admin-human`: human SaaS Owner/App Admin worker for app-owner admins, Organizations, and Organization Admin bootstrap/maintenance through `surface_action`, protected `api_call`, and catalog-bound `human_chat_tool_plan` adapters.
- `user-admin.organization-admin-human`: human Organization/Tenant Admin worker for tenant employees, foundation Customers, Customer Admin bootstrap/maintenance, support access, and tenant-scoped access review.
- `user-admin.customer-admin-human`: human Customer Admin worker for Customer Users in one selected Customer scope.
- `user-admin.functional-agent-worker`: model-backed `user-admin-agent` workstream assistant that may explain, draft, recommend, propose decision cards, and prepare no-mutation chat plans but cannot autonomously mutate access.
- `user-admin.access-review-agent-worker`: bounded model-backed autonomous/advisory worker for access-review task progress and recommendations; outputs cannot directly change access.
- `user-admin.invitation-onboarding-system-worker`: deterministic system worker for invitation delivery/outbox, signed-token invitee acceptance, account/membership linking, expiry/revoke/replay handling, and onboarding recovery.
- `user-admin.admin-audit-projection-system-worker`: deterministic system worker for scoped projections, attention, row routing metadata, audit excerpts, trace refs, and redacted evidence.

Human surface actions, confirmed human chat plans, AI `agent_tool_call`s, workflow/internal calls, consumer reactions, timer invocations, and protected APIs reuse the same governed tool ids declared in this workstream. No worker inherits authority from another worker's harness: human surface visibility does not grant AI tool authority, agent guidance does not grant human permissions, and projection visibility does not authorize writes.

## Capability binding

Primary capability: `../../capabilities/user-and-access-administration.md`.

Capability families represented by this workstream include SaaS Owner Admin management, Organization lifecycle, Organization Admin bootstrap/maintenance, Customer lifecycle, Customer Admin bootstrap/maintenance, scoped directory reads, invitation lifecycle, membership status changes, role/capability preview and mutation, support-access lifecycle, access-review task lifecycle, identity relink review, admin audit/evidence reads, and User Admin agent guidance. Each family must preserve the three admin levels: SaaS Owner/App Admin manages app-owner admins plus Organization/Tenant Admin accounts through explicit target scopes; Tenant Admin manages tenant employees and Customer Admins for that tenant's customers; Customer Admin manages only Customer Users in their assigned customer scope.

## Attention model

Backend-owned attention includes pending/stale/expired invitations, invitation delivery failures, risky role or support-access changes, last-admin risks, dormant or disabled-user review items, identity link/relink exceptions, stale access-review findings, autonomous access-review task results needing human review, recent denied admin actions, provider/outbox/model fail-closed blockers, and approval-required decisions.

Attention counts feed the User Admin rail tile and User Admin dashboard. Personal User Admin attention that is assigned to or directly requires action from the signed-in human may also aggregate into My Account. Hidden tenants/customers, hidden users, hidden counts, raw provider state, and cross-scope evidence are never surfaced through attention.

## Surface graph

Default trunk surface: `surface-user-admin-dashboard`.

Primary graph branches:

- `surface-user-admin-saas-owner-admins` for SaaS Owner/Admin user discovery and invite/manage task entry points when the selected context is app-owner authorized.
- `surface-user-admin-organization-directory` for Organization discovery/lifecycle and Organization Admin bootstrap/maintenance task entry points when the selected context is app-owner authorized.
- `surface-user-admin-customer-directory` for Customer discovery/lifecycle and Customer Admin bootstrap/maintenance task entry points when the selected context is Organization/Tenant Admin authorized.
- `surface-user-admin-users` for scoped user/member discovery only; it exposes a create/invite action where allowed, and every row/card uses backend-authored state to open the correct lifecycle-aware user detail, invitation detail, role preview, access-review task, identity-exception review, or system-message surface.
- `surface-user-admin-user-detail` for scoped user/membership/invitation/support/access-review/identity/audit inspection and task entry points; it does not directly mutate access.
- `surface-user-admin-invitation-create`, `surface-user-admin-invitation-detail`, `surface-user-admin-invitation-resend-confirmation`, and `surface-user-admin-invitation-revoke-confirmation` for invitation create, inspect, resend, and destructive lifecycle flows.
- `surface-user-admin-membership-status-confirmation` for disable/suspend/reactivate/remove membership or account lifecycle changes with last-admin and self-action protections.
- `surface-user-admin-role-change-preview` for capability delta, affected workstreams, last-admin, policy, approval, and the only role-change commit path.
- `surface-user-admin-support-access-grant` and `surface-user-admin-support-access-revoke-confirmation` for support-access grant/extend/revoke flows with purpose, expiry, approval, idempotency, and audit.
- `surface-user-admin-access-review-task` for access-review progress/result/human decision; worker output cannot directly mutate access.
- `surface-user-admin-identity-exception-review` for identity-link/relink exception review and approved recovery routing.
- reusable `decision-card`, `audit-timeline`, `workflow-status`, `markdown-response`, and `system-message` patterns for risky actions, evidence, blocked provider/model/outbox states, no-ops, stale/conflict, and safe denials.

## Readiness posture

This node captures current intent only. Runtime readiness still requires local Akka/API/UI validation, scoped authorization tests, idempotency/no-op tests, frontend surface rendering checks, audit/work-trace proof, and provider/model/outbox fail-closed proof where applicable.


## Confirmed human chat tool-plan exposure

This workstream exposes a bounded `human_chat_tool_plan` adapter for execution-oriented chat prompts after deterministic no-mutation surface routing declines the prompt. The first-pass runtime path is implemented through backend-owned plan proposal, exact snapshot confirmation, catalog validation, dispatcher reauthorization, idempotency, and trace surfaces. It allows `user-admin-agent` to propose a plan for the representative prompt **create org "Org 1", and invite mckee.hugh@gmail.com as an org admin**, but it never permits prompt-only mutation, hidden target enumeration, or AI-autonomous authority.

Execution is allowed only when all of the following hold: the proposal was created with `noMutation=true`; the human explicitly confirms the exact plan snapshot; the backend reauthorizes the selected `AuthContext`, actor, capability, tool boundary, lifecycle state, approval policy, tenant/customer ownership, and idempotency on every step; and each step executes through its declared governed surface/action path as a separate transaction boundary.

Representative catalog binding: actions `action-submit-organization-create`; `action-submit-organization-admin-invitation`; governed tool ids `manage-organizations`; `manage-organization-admins`; capabilities `saas_owner.tenant.manage`; `saas_owner.organization_admin.invite`; input contract `schema.organization-admin.create.submit.v1` with `organizationName`/`reason`, then `schema.organization-admin.invitation-create.v1` with `organizationId` bound from step 1, `email`, `displayName`, `roles=[TENANT_ADMIN]`, and `reason`; expected result surfaces `surface-user-admin-organization-detail`; `surface-user-admin-invitation-detail`. The allowed effect is to create the Organization/Tenant boundary and then invite a `TENANT_ADMIN` for that created Organization; it cannot grant SaaS Owner roles, expose tenant app data, or send invitation/provider work before confirmation.
