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

### Account, profile, and membership lifecycle

Canonical account states are `pending_link`, `active`, `disabled`, and `orphaned_no_membership`. Account creation happens only through a valid invitation acceptance, trusted SaaS Owner bootstrap, or an approved identity-recovery path; public self-registration remains out of scope. Profile/settings state is personal UX data and may be updated only by the authenticated account while active; profile/settings updates never create, remove, or expand memberships.

A membership is scoped to exactly one SaaS Owner, Organization/Tenant, or Customer context and has canonical states `invited`, `active`, `disabled`, `suspended`, and `removed`. `disabled` means the membership cannot authorize work but remains restorable by an authorized admin. `suspended` is used when the parent Organization/Customer or policy temporarily blocks the member. `removed` is a terminal membership state for authorization and directory visibility, but the audit record remains retained. Removing the last active membership from an account does not delete the account; it moves the account to `orphaned_no_membership` for sign-in recovery only, where `/api/me` returns a safe no-access/no-selected-context result and no workstream authority. Permanent account deletion, profile anonymization, and provider-side erasure are not foundation runtime features until a later accepted intent adds retention, deletion, and provider-reconciliation contracts.

WorkOS/AuthKit provider changes are input to, not authority over, Akka authorization state. Provider user deactivation, email changes, identity merge/split, or deleted-provider-user notifications must route through disabled-account, identity-exception, or support recovery flows and fail closed until Akka-owned account/membership state is reconciled. A provider-authenticated identity without an active Akka membership receives no application authority.

### First-admin and local bootstrap lifecycle

The first SaaS Owner Admin is created only through an explicit trusted bootstrap path controlled by deployment/local configuration or a migration/seed command, never through public signup, fixtures, frontend state, prompt text, or hidden demo users. Bootstrap input must identify the invited or linked WorkOS/AuthKit email identity, create or link an Akka account, create the first `SAAS_OWNER_ADMIN` membership, mark the bootstrap source/provenance, and emit an immutable admin audit/work trace. Bootstrap is idempotent for the same configured principal and refuses to create additional owner admins after an active SaaS Owner Admin exists; further owner admins use the normal invitation lifecycle. Local development may provide a documented captured-provider/bootstrap fixture only as test setup evidence, not as normal product authority.

### Organization and Customer boundary lifecycle

Organization/Tenant lifecycle states are `active`, `suspended`, and `archived`. Customer lifecycle states are `active`, `suspended`, and `archived`. Hard delete is not supported in the foundation app. `archived` is terminal for normal administration unless an explicit future recovery policy is added; `suspended` is reversible through the described reactivation confirmation surfaces. Rename/update changes only browser-safe labels and metadata, not tenant/customer ids or isolation boundaries.

Suspending an Organization/Tenant blocks new tenant/customer work, new invitations, Customer creation, Customer Admin bootstrap, support-access grant/extend, managed-agent behavior activation, policy activation, and provider-backed advisory tasks in that Organization. Existing active memberships remain recorded but are treated as suspended for authorization, `/api/me` selected-context eligibility, notifications, and workstream actions. Reactivation restores eligibility for previously active memberships and Customers except for invitations, support grants, or tasks that independently expired, were revoked, were cancelled, or require fresh policy review.

Suspending a Customer blocks Customer-scoped member authority, Customer Admin/user invitations, Customer Admin maintenance, support-access grant/extend for that Customer, customer-scoped notifications, and customer-scoped workstream openings. It does not suspend sibling Customers or Organization Admin authority. Reactivation restores eligibility for previously active Customer memberships except for independently expired/revoked invitations, expired support access, or tasks requiring fresh review. Archived Organizations or Customers remain visible only through authorized audit/detail views with no mutating lifecycle action except a future explicitly described recovery flow.

### Invitation delivery and acceptance lifecycle

Canonical invitation states are `draft_validation_failed`, `pending_delivery`, `delivery_blocked`, `sent`, `delivery_failed`, `bounced`, `open`, `expired`, `revoked`, `accepted`, and `superseded`. Admin create/resend moves an invitation to `pending_delivery` only after authorization, target-scope, role, duplicate/open-invite, idempotency, and policy validation pass. Resend provider/outbox failures move or keep the invitation in a browser-safe `delivery_blocked` or `delivery_failed` state and never report fake delivery success. Resend attempts are rate-limited and idempotent per idempotency key; exact limits are backend policy values surfaced as safe validation messages rather than client constants.

Resend/Resend-like provider delivery, bounce, complaint, and failure callbacks are accepted only through server-side trusted provider or captured-provider integration and update delivery status without granting authority. Revoked, expired, bounced, failed, and superseded invitations remain visible to authorized administrators as audit records under retention policy; acceptance is allowed only from `sent`/`open` states that are unexpired, unrevoked, target-scope-valid, and email/account eligible. Accepted invitation records are immutable authority evidence: correction requires a new membership/status/identity-recovery action rather than editing the accepted invitation.

## Scope and business-domain separation

Organization-level business domains may own customer-scoped records such as CRM accounts, customer health, renewals, sales opportunities, billing entitlements, or customer intelligence by referencing the foundation Customer boundary. Support/service may span organization-level objects such as queues, SLA policies, escalation rules, and incidents and customer-level objects such as cases, comments, attachments, SLA clocks, and escalations. Those domains must bind each capability to explicit organization, customer, affected-customer, assigned-case, or support-access scope and must not rely on browser state or prompt text as authority.

## Retention and traces

Security-sensitive transitions emit admin audit/work trace events with tenant/customer, actor, selected `AuthContext`, role/capability, target scope, idempotency key where applicable, correlation id, policy decision, and denial evidence. Customer lifecycle and Customer Admin membership/invitation traces must preserve enough evidence to reconstruct who acted, which tenant/customer boundary was affected, which capability was used, whether the action was a no-op/replay/conflict, and why a denial was safe. Browser payloads and logs never expose provider secrets, raw invitation tokens, raw provider ids, hidden counts, sibling-customer existence, or private cross-scope evidence.
