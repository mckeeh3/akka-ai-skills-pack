# Core AI-first SaaS foundation

Use this document set when designing or implementing the common backend foundation for generated AI-first SaaS applications. These features are product-agnostic: they exist before the app-specific business domain is added.

Companion docs:
- `core-saas-identity-tenancy-admin.md` — actors, tenant/customer organization model, WorkOS identity, account administration, data isolation, support-access pattern.
- `core-saas-owner-tenant-billing.md` — SaaS Owner to Tenant subscription management and billing boundaries.

## Canonical vocabulary

| Term | Meaning |
|---|---|
| SaaS Owner | The platform operator that sells and operates the SaaS product. |
| Tenant | The SaaS user organization that subscribes to the platform and uses it to serve its own customers. This is the canonical internal/architectural term for “SaaS user.” |
| Customer | An organization served by a Tenant through app-specific online services. Customers always start as organizations in the seed model. |
| Account | A local Akka-owned authorization record linked to a WorkOS-authenticated human identity. |
| User Profile | Human-facing account information and scoped profile attributes shown in the application, separate from authentication and authorization facts. |
| User Settings | User-controlled preferences that affect application experience, such as UI appearance, without changing authorization. |
| Membership | A scoped relationship between an account and a SaaS Owner, Tenant, or Customer organization. |
| Role | A permission bundle within a scope, such as SaaS Owner Admin, Tenant Admin, Tenant Employee, Customer Admin, or Customer User. |

## Three-level operating model

```text
SaaS Owner
  sells subscriptions to Tenants
  manages Tenant admin accounts and Tenant billing
  has no access to Tenant application data

Tenant
  owns its application data
  provides services to Customer organizations
  manages Tenant employee accounts
  creates and manages Customer admin accounts

Customer
  consumes services provided by a Tenant
  manages its own Customer user accounts
```

## Administration hierarchy

```text
SaaS Owner Admin
  -> creates and maintains Tenant records
  -> creates and maintains initial Tenant Admin accounts
  -> manages Tenant subscription and billing state

Tenant Admin
  -> manages Tenant employee accounts
  -> creates and maintains Customer organizations
  -> creates and maintains Customer Admin accounts
  -> manages Tenant-owned application data and services

Customer Admin
  -> manages Customer user accounts within the Customer organization
  -> supervises Customer-side use of Tenant-provided services
```

The same human/email may have separate local accounts or memberships at multiple levels. For example, a person may be a SaaS Owner Admin for platform operations, a Tenant Admin in a demo tenant, and a Customer Admin in a test customer organization. Authorization must be based on the selected account/membership context, not email alone.

## Non-negotiable boundaries

- SaaS Owner users do not have platform-level access to Tenant application data.
- Tenant data belongs to the Tenant.
- Customer data belongs within a Tenant-owned Customer organization boundary.
- All backend commands and queries must enforce scope mechanically.
- Frontend navigation is only UX; backend authorization is authoritative.
- WorkOS authenticates humans; Akka-owned state authorizes business actions.
- Profiles and settings are user-experience state, not authorization state; they may be scoped, but they must not grant permissions.
- Billing in the core foundation covers only SaaS Owner → Tenant subscriptions.
- Tenant → Customer billing is app-specific and must be modeled by the generated business application when needed.

## AI-first expectations for the core foundation

The seed foundation should validate the broader AI-first architecture by using core administrative work as real AI-first product behavior, not only as CRUD.

Core AI-first features should include:

- durable audit traces for identity, role, scope, tenant, customer, and billing administration;
- policy-controlled administrative actions, such as role assignment, tenant suspension, and billing-plan changes;
- decision cards for risky or high-impact administration changes;
- anomaly and risk signals for suspicious account, access, or billing events;
- human-supervised recommendations rather than autonomous high-impact changes by default;
- outcome metrics for onboarding, tenant health, support-access usage, access-review completion, and billing workflow quality.

Example AI-first core scenarios:

| Scenario | AI-first behavior |
|---|---|
| Tenant onboarding | Assistant drafts setup checklist, missing configuration summary, and next-best action recommendations. |
| Access review | Agent flags stale admins, excessive roles, unusual cross-level memberships, and dormant customer admins. |
| Billing issue | Agent summarizes subscription state, payment issue, service impact, prior notices, and recommended action. |
| Tenant-created support account | Agent verifies purpose, expiry, permissions, and audit readiness before activation. |
| Role change | Decision card shows requested role, scope, risk, policy triggers, affected data boundary, and approver actions. |

## Suggested Akka substrate

| Concern | Preferred Akka substrate |
|---|---|
| Tenant, Customer, Account, Membership lifecycle | Event Sourced Entity when audit-grade history is required; Key Value Entity for low-risk current state only. |
| User profiles and settings | Key Value Entity for current editable profile/settings state; Event Sourced Entity if the app requires audit-grade profile or preference history. |
| Subscription lifecycle | Event Sourced Entity for plan changes, trial, activation, suspension, cancellation, and billing state history. |
| Invitations and onboarding | Workflow for invite/link/activate and multi-step onboarding. |
| Access review and support-access expiry | Timed Actions plus Views and Consumers. |
| Administrative dashboards | Views for scoped lists, queues, tenant health, subscription state, and audit search. |
| AI recommendations and summaries | Agents with bounded tools and read-only access unless explicitly approved. |
| Policy gates and approvals | Workflows plus decision-card records and audit events. |
| Browser APIs | JWT-protected HTTP endpoints using WorkOS authentication and Akka-owned authorization state. |

## Initial implementation slices

1. WorkOS-backed `/api/me` account bootstrap, membership selection, base profile, and base user settings.
2. SaaS Owner Admin tenant creation and initial Tenant Admin invitation.
3. Tenant Admin employee invitation and role management.
4. Tenant Admin customer organization creation and Customer Admin invitation.
5. Customer Admin customer user invitation and role management.
6. User profile and settings APIs, starting with editable display profile fields and UI light/dark appearance.
7. SaaS Owner to Tenant subscription creation, plan assignment, status changes, and billing audit.
8. Cross-scope audit trace and access review views.
9. AI-first admin decision cards for risky role, support-access, tenant suspension, and billing changes.
