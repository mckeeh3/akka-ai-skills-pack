# Build Backlog 02: Secure Foundation Backend

## Items

1. Establish the starter backend project/package layout and base package policy.
2. Implement local secure SaaS foundation records and components: Account, UserProfile, UserSettings, Tenant, Customer, Membership, Role, Permission/Capability, AuthContext, AdminAuditEvent.
3. Implement `/api/me`, context selection, disabled/no-membership/forbidden states, and browser-safe capability exposure.
4. Implement complete Invitation onboarding with Resend seam, captured local/test outbox, expiry/reminder timers, resend/revoke/acceptance, views, and audit.
5. Implement User Admin capabilities, views, endpoints, and security tests.
6. Add backend acceptance tests for tenant isolation, forbidden access, disabled users, role/scope denial, audit, idempotency, and invite lifecycle.

## Completion signal

The starter backend can run and prove the full secure foundation without frontend fixtures or doc-only assumptions.
