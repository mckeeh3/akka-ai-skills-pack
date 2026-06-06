# Core SaaS Owner to Tenant billing

Use this doc to model the product-agnostic billing foundation for AI-first SaaS applications.

Related docs:
- `./core-ai-first-saas-foundation.md`
- `./core-saas-identity-tenancy-admin.md`

## Scope

The core foundation covers only billing between the **SaaS Owner** and **Tenants**.

```text
SaaS Owner sells subscription -> Tenant
Tenant serves -> Customer organizations
```

Tenant to Customer billing is explicitly out of scope for the core foundation. It belongs to the specific business application built on top of the core and may differ by product domain.

## Billing boundaries

SaaS Owner billing capabilities may use Tenant metadata needed to operate the subscription relationship, such as:
- Tenant legal/display name;
- billing contact information;
- subscription plan;
- subscription status;
- payment status;
- invoice/payment provider identifiers;
- usage metrics explicitly defined as platform billing metrics.

SaaS Owner billing capabilities must not access Tenant application data, Customer service data, or user profile/settings data except for explicit billing contact fields stored in the billing account.

Examples of forbidden SaaS Owner billing access:
- Tenant customer records beyond billing-safe aggregate counts explicitly defined as billing metrics;
- Tenant case/ticket/order/document data;
- Customer user activity content;
- user profile attributes or settings such as UI mode, unless copied into billing-safe contact fields by an authorized billing flow;
- AI work traces from Tenant-owned business operations, unless the metric is explicitly redacted/aggregated for platform billing.

## Core billing objects

### Plan

Defines a sellable SaaS subscription package.

Recommended fields:
- `planId`
- `name`
- `status`: `DRAFT`, `ACTIVE`, `RETIRED`
- `billingPeriod`: monthly, annual, custom
- `priceReference` or external billing price id
- `includedLimits`, such as users, customers, storage, AI execution units, or app-specific seats when applicable
- `overagePolicy`, optional
- `effectiveFrom`, `retiredAt`

### Tenant Subscription

Represents the commercial relationship between SaaS Owner and Tenant.

Recommended fields:
- `subscriptionId`
- `tenantId`
- `planId`
- `status`: `TRIALING`, `ACTIVE`, `PAST_DUE`, `SUSPENDED`, `CANCELED`
- `billingAccountId` or external customer id
- `currentPeriodStart`, `currentPeriodEnd`
- `trialEndsAt`
- `cancelAt`, `canceledAt`
- `suspendedAt`, `suspensionReason`
- `paymentStatus`
- audit metadata and policy references

### Billing Account

Stores billing-safe contact and payment-provider linkage for a Tenant.

Recommended fields:
- `billingAccountId`
- `tenantId`
- `billingContactName`
- `billingContactEmail`
- `billingAddress`, if required
- `externalProviderCustomerId`
- `taxStatus`, optional
- `status`

### Invoice / Payment Reference

The SaaS Foundation App may store provider references and summary status, while the payment provider remains the financial system of record.

Recommended fields:
- `invoiceId`
- `tenantId`
- `subscriptionId`
- `externalInvoiceId`
- `amountDue`, `currency`
- `status`: `DRAFT`, `OPEN`, `PAID`, `VOID`, `UNCOLLECTIBLE`
- `dueDate`, `paidAt`
- `hostedInvoiceUrl`, optional

## Subscription lifecycle

```text
Create Tenant
  -> Create billing account
  -> Assign plan
  -> Start trial or active subscription
  -> Monitor payment/provider events
  -> Handle plan changes, past due, suspension, reactivation, cancellation
```

Key lifecycle transitions:
- `NO_SUBSCRIPTION -> TRIALING`
- `NO_SUBSCRIPTION -> ACTIVE`
- `TRIALING -> ACTIVE`
- `TRIALING -> CANCELED`
- `ACTIVE -> PAST_DUE`
- `PAST_DUE -> ACTIVE`
- `PAST_DUE -> SUSPENDED`
- `ACTIVE -> SUSPENDED`
- `SUSPENDED -> ACTIVE`
- `ACTIVE -> CANCELED`

Plan changes should be auditable and may require policy checks when they materially affect service limits, price, or access.

## Tenant service status vs subscription status

Keep billing status distinct from application service availability.

| Concept | Purpose |
|---|---|
| Subscription status | Commercial/payment lifecycle. |
| Tenant status | Tenant organization lifecycle, such as onboarding, active, suspended, closed. |
| Service entitlement | What the Tenant can currently use based on plan, subscription, limits, and policy. |

A past-due subscription should not automatically disable service unless the product policy says so. Prefer an explicit policy-controlled transition such as:

```text
subscription becomes PAST_DUE
  -> notification/reminder workflow
  -> grace period timer
  -> decision/policy gate
  -> tenant service suspension if approved or policy-authorized
```

## AI-first billing behavior

Billing operations are a good validation path for AI-first architecture because they involve policy, risk, communication, audit, exceptions, and human approval.

### Billing assistant

A bounded billing assistant may:
- summarize subscription state;
- explain why a Tenant is past due or suspended;
- draft billing notices;
- recommend next action based on policy and history;
- identify anomalous plan changes or payment patterns;
- prepare decision cards for suspension/reactivation/cancellation;
- answer SaaS Owner Admin questions using billing-safe data only.

It must not access Tenant application data.

### Decision cards

Use decision cards for high-impact billing actions:
- suspending a Tenant;
- reactivating a suspended Tenant;
- canceling a subscription;
- granting extended trial or grace period;
- changing a Tenant to a materially different plan;
- overriding payment or dunning policy.

Decision card fields:
- Tenant and subscription summary;
- requested action;
- policy triggers;
- payment/invoice evidence;
- service impact;
- customer-impact warning without exposing Tenant application data;
- alternatives, such as extend grace period or contact Tenant Admin;
- approver action and audit trace link.

### Billing audit trace

Record audit events for:
- plan creation/update/retirement;
- billing account creation/update;
- subscription creation/status change/plan change/cancellation;
- provider webhook receipt and interpretation;
- invoice/payment summary update;
- dunning notice generated/sent;
- grace period timer start/expiry;
- suspension/reactivation decision and execution;
- AI recommendation inputs/outputs and human approvals.

### Outcome metrics

Track core billing outcomes:
- Tenant onboarding time to active subscription;
- trial conversion rate;
- past-due resolution time;
- suspension rate;
- billing support workload;
- AI recommendation acceptance rate;
- incorrect or reversed billing-action rate;
- policy override frequency.

## Suggested Akka substrate

| Component | Responsibility |
|---|---|
| `PlanEntity` | Plan lifecycle and history. |
| `TenantSubscriptionEntity` | Tenant subscription lifecycle, plan changes, status transitions, audit-grade billing history. |
| `BillingAccountEntity` | Billing-safe Tenant contact/provider linkage. |
| `BillingWorkflow` | Trial activation, plan changes, provider event handling, grace periods, suspension/reactivation gates. |
| `BillingProviderConsumer` | Consume payment provider webhooks or integration events. |
| `BillingTimedAction` | Trial expiry, invoice due reminders, grace period expiry, periodic billing health checks. |
| `BillingDashboardView` | SaaS Owner billing dashboard and subscription queues. |
| `TenantEntitlementView` | Read model used by Tenant-scoped services to enforce plan/feature limits without exposing billing internals. |
| `BillingRiskAgent` | Read-only billing-safe analysis and recommendation. |
| `BillingDecisionWorkflow` | Human approval flow for high-impact billing actions. |

## API families

SaaS Owner Admin APIs:

```text
GET  /api/owner/plans
POST /api/owner/plans
POST /api/owner/plans/{planId}/retire

GET  /api/owner/tenants/{tenantId}/billing
POST /api/owner/tenants/{tenantId}/billing-account
POST /api/owner/tenants/{tenantId}/subscription
POST /api/owner/tenants/{tenantId}/subscription/plan-change
POST /api/owner/tenants/{tenantId}/subscription/cancel
POST /api/owner/tenants/{tenantId}/subscription/suspend
POST /api/owner/tenants/{tenantId}/subscription/reactivate

GET  /api/owner/billing/audit
GET  /api/owner/billing/decision-cards
POST /api/owner/billing/decision-cards/{decisionId}/approve
POST /api/owner/billing/decision-cards/{decisionId}/deny
```

Tenant Admin billing visibility APIs:

```text
GET /api/tenant/{tenantId}/subscription
GET /api/tenant/{tenantId}/billing-contact
```

Tenant APIs should expose only that Tenant's own subscription/billing-safe information. They must not expose SaaS Owner internal risk notes, other Tenant data, or payment-provider secrets.

## Authorization rules

- `SAAS_OWNER_ADMIN` may manage plans, billing accounts, subscriptions, invoices/payment summaries, and billing decisions.
- `TENANT_ADMIN` may view its own Tenant subscription and billing contact information, and may update billing contact details only if product policy allows.
- `TENANT_ADMIN` cannot change plan, subscription state, suspension state, or billing policy unless the generated app explicitly adds a self-service purchasing flow.
- Billing contact updates must not silently mutate user profile/settings records; synchronize only through explicit product rules and audit when needed.
- `CUSTOMER_ADMIN` and `CUSTOMER_USER` have no core billing access.
- Billing assistants operate with billing-safe data access and cannot read Tenant application data.

## Acceptance checklist

- [ ] SaaS Owner Admin can create and retire plans.
- [ ] SaaS Owner Admin can create a billing account and subscription for a Tenant.
- [ ] Subscription status transitions are validated and audited.
- [ ] Billing operations do not expose Tenant application data.
- [ ] Tenant Admin can view only its own billing/subscription summary when allowed.
- [ ] Past-due, grace period, suspension, and reactivation are policy-controlled.
- [ ] High-impact billing actions can produce decision cards.
- [ ] Billing assistant uses billing-safe data only.
- [ ] Tenant entitlement view gives app services the information needed to enforce plan limits without granting SaaS Owner data access.
- [ ] Provider webhook handling is idempotent and auditable.
