# Task Brief: IAM/SCIM/SSO Administration Foundation

## Objective

Add a bounded IAM/SCIM/SSO administration foundation for the starter without overclaiming enterprise provider integration.

## Required reads

- `specs/core-app-feature-completion/README.md`
- `specs/core-app-feature-completion/pending-questions.md`
- `specs/core-app-feature-completion/sprints/04-enterprise-extensions-sprint.md`
- `docs/core-saas-identity-tenancy-admin.md`
- `docs/capability-first-backend-architecture.md`
- `skills/akka-workos-user-auth/SKILL.md` if present
- `skills/akka-http-endpoints/SKILL.md`

## In scope

- Capability contracts, admin surfaces, audit events, SCIM-style identity/membership operation DTOs, local validation, and fail-closed provider configuration seams.
- WorkOS/AuthKit boundary preservation and no frontend secrets.

## Out of scope

- Provider-specific SCIM/SSO production provisioning unless selected and validated in follow-up.

## Checks

- `git diff --check`
- focused backend identity/admin tests
- frontend tests/typecheck/build if surfaces change
- secret-boundary scans
- `tools/validate-ai-first-saas-starter-fullstack.sh`

## Done criteria

- IAM/SCIM/SSO foundation has safe local runtime behavior and docs accurately state provider-specific limits.
