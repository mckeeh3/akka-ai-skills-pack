---
id: support-operator
title: Support operator
email: support.operator@example.com
authMode: workos-test-users
---

# AuthContext and scope

- Authenticated WorkOS/AuthKit user mapped to support operator authority.
- Support access must be scoped, time-bounded, and traceable.
- Tenant/customer access requires an explicit support-access grant or scenario-defined equivalent.

# Allowed validation focus

- Audit/Trace search and detail inspection within granted scope.
- Redaction and no-enumeration behavior for out-of-scope tenants.
- Trace evidence for support reads and denied reads.

# Denial expectations

- No cross-tenant data appears without an active support grant.
- Redacted fields remain redacted in UI, API responses, logs, exported evidence, and traces.
