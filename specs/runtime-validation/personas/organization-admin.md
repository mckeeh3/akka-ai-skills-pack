---
id: organization-admin
title: Organization administrator
email: org.admin@example.com
authMode: workos-test-users
---

# AuthContext and scope

- Authenticated WorkOS/AuthKit user mapped to an active organization admin account.
- Tenant/organization scope limited to the base organization.
- May administer organization users and inspect tenant-scoped operational evidence allowed by policy.

# Allowed validation focus

- Invite users and observe invitation result surfaces.
- Review tenant-scoped user/admin audit evidence.
- Confirm non-admin and cross-tenant denials.

# Denial expectations

- SaaS-wide Agent Admin, provider secret, and platform policy operations are denied unless explicitly granted in a scenario.
- Support-only trace views remain unavailable without support authority.
