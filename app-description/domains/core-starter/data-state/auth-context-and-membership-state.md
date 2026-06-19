# Data State: Auth context and membership state

## Responsibility

Backend-owned account, profile, settings, tenant/customer organization, Customer boundary, membership, role/capability, selected `AuthContext`, invitation, support-access, and identity-linking state used by My Account and User Admin.

The foundation Customer state belongs to the secure core starter. It provides a tenant-owned boundary for authorization, Customer Admin membership scope, redaction, audit/work trace attribution, support-access checks, and safe cross-customer denials. It does not own CRM profile fields, sales/customer-success lifecycle, support case content, billing subscription state, customer intelligence, or industry-specific business records.

## Lifecycle and invariants

- WorkOS/AuthKit authenticates humans; Akka-owned state authorizes business actions.
- Tenant/Organization state anchors tenant isolation, Organization Admin authority, Customer lifecycle administration, and support-access boundaries.
- Customer state is owned by exactly one tenant, has a stable customer id, safe display label, and active/suspended lifecycle, and may be created, read, renamed, suspended, or reactivated only through `tenant.customer.*` capabilities from a selected Tenant/Organization Admin context.
- Customer records remain foundation boundary records only; downstream business domains may reference the customer id for scoped records but must not add business profile, CRM, support-case, billing, or industry-specific fields to this state.
- Customer Admin memberships and invitations are scoped to one selected Customer, require the Customer to exist in the selected tenant, and cannot grant sibling-customer, Organization Admin, or SaaS Owner authority.
- Selected `AuthContext` is authoritative: tenant-scoped Customer lifecycle actions require the selected Tenant/Organization context; Customer Admin user operations require explicit target Customer proof and preserve the Organization Admin actor context; Customer Admin actors can operate only inside their selected customer scope.
- Profile/settings changes cannot grant permissions.
- Membership and support-access changes remain scoped, status-aware, idempotent, and audited.
- Invitations track status, expiry, resend/revoke/acceptance, delivery attempts, target tenant/customer scope, requested role, invitee email identity, accepted account/membership linkage, and audit history.
- Standard invitation acceptance is a signed-token plus WorkOS/AuthKit-authenticated onboarding flow: the invitee opens the invitation link, authenticates or signs up through WorkOS/AuthKit with the invited email identity, backend validation confirms the invitation is unexpired, unrevoked, unaccepted, target-scope-valid, and email/account eligible, then Akka-owned state creates or links the account and membership for the invited tenant/customer/app-owner scope and marks the invitation accepted. Repeated acceptance by the same resolved account is idempotent; expired, revoked, already-accepted-by-another-account, email-mismatch, disabled-account, hidden-target, suspended-customer, or stale-token attempts fail closed with safe recovery and audit/work trace evidence.
- Disabled accounts, inactive memberships, suspended customers, hidden targets, sibling-customer targets, and cross-tenant/customer requests fail closed.

## Scope and business-domain separation

Organization-level business domains may own customer-scoped records such as CRM accounts, customer health, renewals, sales opportunities, billing entitlements, or customer intelligence by referencing the foundation Customer boundary. Support/service may span organization-level objects such as queues, SLA policies, escalation rules, and incidents and customer-level objects such as cases, comments, attachments, SLA clocks, and escalations. Those domains must bind each capability to explicit organization, customer, affected-customer, assigned-case, or support-access scope and must not rely on browser state or prompt text as authority.

## Retention and traces

Security-sensitive transitions emit admin audit/work trace events with tenant/customer, actor, selected `AuthContext`, role/capability, target scope, idempotency key where applicable, correlation id, policy decision, and denial evidence. Customer lifecycle and Customer Admin membership/invitation traces must preserve enough evidence to reconstruct who acted, which tenant/customer boundary was affected, which capability was used, whether the action was a no-op/replay/conflict, and why a denial was safe. Browser payloads and logs never expose provider secrets, raw invitation tokens, raw provider ids, hidden counts, sibling-customer existence, or private cross-scope evidence.
