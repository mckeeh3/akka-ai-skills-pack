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

The app must first be started with `./tools/runtime-validation/start-local.sh --empty`, which enables the local-only seed endpoint and writes `.runtime-validation/local.env`. The start script waits for the local HTTP endpoint before returning, and the seed script also waits by default before posting so startup timing does not produce an immediate false `Could not connect to server` failure. The seed script calls the Akka runtime at `/internal/runtime-validation/seed/base-organization` with the generated seed token. If this command fails, the run record must document the HTTP status/body or readiness timeout and classify the blocker as auth/setup, seed tooling, or runtime-validation setup according to the failure.

# State to create or reuse

- One base tenant/organization with stable identifiers recorded in the run.
- WorkOS/AuthKit or local-dev passwordless test-user mappings for:
  - `member@example.com` as `member`;
  - `org.admin@example.com` as `organization-admin`;
  - `saas.admin@example.com` as `saas-admin`;
  - `support.operator@example.com` as `support-operator`;
  - `org1.admin1@example.test` as organization admin;
  - `org1.user3@example.test` as organization user;
  - `cust1.admin@example.test` as customer admin;
  - `cust1.user2@example.test` as customer user.
- Active membership for member and organization admin in the base organization.
- SaaS/platform authority for the SaaS admin only.
- Disabled and inactive member fixtures for denial checks:
  - `disabled.member@example.com` with a disabled account and active tenant membership.
  - `inactive.member@example.com` with an active account and suspended tenant membership.
- Optional support-access grant data for the Audit/Trace scenario, with expiry and scope recorded.

# Setup boundaries

- The setup may use local-dev seed authority, but the run must record that authority and its trace ids.
- In `APP_AUTH_MODE=local-dev`, manual testers use the local-only sign-in panel or `POST /api/dev/auth/sign-in` with a seeded email; roles and scopes still come from these seeded backend memberships.
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
