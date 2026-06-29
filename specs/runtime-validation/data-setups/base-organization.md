---
id: base-organization
title: Base organization with runtime-validation personas
setupMode: local-seeded
executionStatus: scaffold-not-run
---

# Purpose

Prepare the shared organization, tenant scope, and test identities needed by the first five runtime-validation scenarios. This setup is prerequisite evidence only; it must not perform the behavior being validated by an individual scenario.

# Preferred command contract

```bash
./tools/runtime-validation/seed.sh base-organization
```

If this command is unavailable, the run record must document the manual or API steps used and classify missing seed tooling as `seed tooling gap` when it blocks execution.

# State to create or reuse

- One base tenant/organization with stable identifiers recorded in the run.
- WorkOS/AuthKit test-user mappings for:
  - `member@example.com` as `member`;
  - `org.admin@example.com` as `organization-admin`;
  - `saas.admin@example.com` as `saas-admin`;
  - `support.operator@example.com` as `support-operator`.
- Active membership for member and organization admin in the base organization.
- SaaS/platform authority for the SaaS admin only.
- Optional disabled or inactive member state for denial checks, with the exact account/membership id recorded.
- Optional support-access grant data for the Audit/Trace scenario, with expiry and scope recorded.

# Setup boundaries

- The setup may use local-dev seed authority, but the run must record that authority and its trace ids.
- The setup must not invite the user in `RV-USER-ADMIN-001`; that invitation happens during validation.
- The setup must not approve the policy decision in `RV-GOVPOL-001`; that decision happens during validation.
- The setup must not mark any scenario as passed.

# Evidence emitted by setup

- Tenant/organization/account/membership ids.
- Persona-to-identity mapping and AuthContext handoff notes.
- Seed command, exit status, and setup trace/audit ids.
- Provider configuration state and any expected provider-missing fail-closed mode.

# Failure classifications

- `bootstrap gap` if the local app cannot start from empty persistence.
- `auth/setup gap` if WorkOS/AuthKit identities cannot be mapped to local accounts and memberships.
- `seed tooling gap` if setup tooling is missing or cannot call real app capabilities.
- `provider/config blocker` if required external provider configuration is missing for a scenario that needs it.
