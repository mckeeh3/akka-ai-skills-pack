---
id: saas-admin
title: SaaS administrator
email: saas.admin@example.com
authMode: workos-test-users
---

# AuthContext and scope

- Authenticated WorkOS/AuthKit user mapped to a SaaS/platform administrator account.
- Platform scope for managed-agent governance and policy administration.
- Tenant/customer context must still be explicit when an operation targets tenant data.

# Allowed validation focus

- Agent Admin managed-agent governance surfaces.
- Provider-missing fail-closed behavior without browser secret exposure.
- Governance/Policy proposal and decision-card flows when policy authority is assigned.

# Denial expectations

- Provider secrets never appear in browser-visible payloads.
- Authority expansion, tool-boundary changes, and policy activation require explicit approval/decision surfaces when the scenario includes them.
