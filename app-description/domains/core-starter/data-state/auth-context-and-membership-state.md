# Data State: Auth context and membership state

## Responsibility

Backend-owned account, profile, settings, tenant/customer organization, membership, role/capability, selected `AuthContext`, invitation, support-access, and identity-linking state used by My Account and User Admin.

## Lifecycle and invariants

- WorkOS/AuthKit authenticates humans; Akka-owned state authorizes business actions.
- Profile/settings changes cannot grant permissions.
- Membership and support-access changes remain scoped, status-aware, idempotent, and audited.
- Invitations track status, expiry, resend/revoke/acceptance, delivery attempts, and audit history.
- Disabled accounts, inactive memberships, and cross-tenant/customer requests fail closed.

## Retention and traces

Security-sensitive transitions emit admin audit/work trace events with tenant/customer, actor, role/capability, correlation id, policy decision, and denial evidence. Browser payloads and logs never expose provider secrets.
