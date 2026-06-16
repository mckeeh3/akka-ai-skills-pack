# Foundation Customer Boundary App-description

## Purpose

Capture the foundation customer boundary as authoritative current intent in `app-description/` so future implementation and generated-app extension work can distinguish:

- the generic secure SaaS customer boundary used for tenant/customer isolation, Customer Admin authority, audit scope, and workstream authorization; from
- business-specific CRM, customer-success, sales, support/service, billing, or industry-specific customer domains that downstream apps may add later.

This mini-project is planning and app-description work only. It does not implement new runtime behavior unless a later appended task explicitly broadens scope.

## Target scope

App-facing root assets:

- `app-description/**`, especially `domains/core-starter/**` and reusable global actors/roles/policies/surfaces/agents/tools/traces where the foundation customer boundary is referenced.
- `specs/foundation-customer-boundary-app-description/**` for this planning queue and verification evidence.
- Runtime source and frontend files are evidence only unless a verification task appends a separate implementation/remediation queue.

## Current intent to capture

The foundation customer boundary is a reusable secure SaaS substrate inside the core starter. It is not a generic CRM module and must not absorb business-customer domains.

It should describe at least:

1. **Domain boundary**
   - Customer boundary belongs to the secure foundation/core starter domain.
   - It provides tenant-scoped customer records used for authorization, membership, Customer Admin bootstrap/maintenance, support access boundaries, audit scope, redaction, and cross-customer denial behavior.
   - It does not own CRM account profiles, contacts, opportunities, customer health, renewal plans, support cases, billing subscriptions, or industry-specific customer objects.

2. **Workstream placement**
   - Primary workstream: User Admin / Tenant Customer Admin branch.
   - Related workstreams: My Account context selection, Audit/Trace investigation, Governance/Policy when policies affect customer-scope administration, and Agent Admin only as managed-agent governance that must respect customer scope.
   - Customer Admin is a role-scoped operating mode inside User Admin, not an authority expansion from browser state or prompt text.

3. **Surfaces**
   - Customer directory, detail, create, rename, suspend/reactivate confirmation, Customer Admin list/invitation/detail, safe system-message/denial surfaces, and audit trace links.
   - Surfaces must be backend-authored, capability-backed, redacted, and explicit about tenant/customer scope.

4. **Functional agents**
   - `user-admin-agent` may summarize, draft, recommend, and prepare customer-boundary administration actions only through governed tools and approval/human confirmation paths.
   - It must not autonomously create/suspend customers, grant Customer Admin authority, cross tenant/customer scope, or mutate business CRM/customer data.
   - Other functional agents can read/explain customer-scope evidence only through their own authorized traces/tools.

5. **Backend Akka realization mapping**
   - Domain state: `Customer`, `Tenant`, `Membership`, `AuthContext`, invitation state, audit/work trace records.
   - Application services/components: `TenantCustomerAdminService`, identity repository/entity, invitation service/view, admin audit view, workstream service action routing, admin endpoint routes.
   - Required authorization, idempotency, redaction, audit, denial, and validation obligations.

## Done state

This mini-project is complete when:

- `app-description/` unambiguously describes the foundation customer boundary domain responsibility, non-goals, data/state responsibilities, capabilities, User Admin workstream bindings, surfaces, functional-agent authority limits, governed tools, policies, traces, tests, and Akka/frontend/API realization mappings.
- The description is clear enough that a future realization task can implement or adjust runtime behavior without guessing whether a requested feature belongs to foundation customer boundary or a business-specific customer domain.
- Customer boundary descriptions preserve tenant/customer scoping, backend authorization, Customer Admin limitations, audit/work trace obligations, redaction, idempotency, and frontend non-authority rules.
- The app-description distinguishes organization-level customer-boundary administration from downstream CRM/customer-success/sales/billing/support business domains, including the special case that support/service may span organization and customer layers while still using explicit scoped capabilities.
- A terminal verification task asks whether the description is sufficiently unambiguous; if not, it appends bounded follow-up tasks plus a new terminal verification task, repeating until the answer is yes.

## Non-goals

- Do not implement CRM, customer success, sales, billing, or support/service business domains in this mini-project.
- Do not add new backend/frontend runtime behavior during app-description capture tasks.
- Do not move the existing foundation `Customer` record into a business CRM package.
- Do not treat frontend-visible actions as authorization grants.
- Do not edit `skills-pack/**` or installed `.agents/**` assets.

## Execution model

Future task sessions must:

1. execute exactly one queued task in a fresh harness context;
2. mark it `in-progress` before app-description edits;
3. run required checks;
4. mark `done` only when criteria pass;
5. commit the task changes and queue update together; and
6. report the next runnable task.

Commit message format: `customer-boundary-desc: <short task title>`.
