---
id: member
title: Signed-in organization member
email: member@example.com
authMode: workos-test-users
---

# AuthContext and scope

- Authenticated WorkOS/AuthKit user mapped to one account.
- Active membership in the base organization.
- Tenant/organization scope limited to the base organization.
- No admin, support, or SaaS-owner privileges.

# Allowed validation focus

- Read own `/api/me` account context.
- Use My Account surfaces and member-visible workstream actions.
- Confirm that disabled/inactive account or membership state is denied when the scenario provides that state.

# Denial expectations

- User Admin, Agent Admin, Governance/Policy admin actions, and privileged Audit/Trace reads are forbidden or hidden.
- Cross-tenant or support-only data must not appear in surfaces, API responses, logs, traces, or client payloads.
