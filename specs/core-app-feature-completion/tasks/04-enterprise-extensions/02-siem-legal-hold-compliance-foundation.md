# Task Brief: SIEM, Legal Hold, E-Discovery, and Compliance Foundation

## Objective

Add bounded audit/export foundations for SIEM, legal hold, e-discovery, and compliance reporting without claiming specific compliance-suite certification.

## Required reads

- `specs/core-app-feature-completion/README.md`
- `specs/core-app-feature-completion/sprints/04-enterprise-extensions-sprint.md`
- `specs/full-core-smb-audit-trace/audit-trace-implementation-map.md`
- `docs/capability-first-backend-architecture.md`
- `skills/ai-first-saas-audit-trace/SKILL.md`
- `skills/akka-http-endpoints/SKILL.md`

## In scope

- Legal-hold markers, retention/export request capabilities, redacted compliance export DTOs, SIEM webhook/export seams, approval gates, and audit of exports/holds.
- Tests for tenant isolation, sensitive read/export authorization, no secret storage/display, and idempotency.

## Out of scope

- Specific SIEM vendor integrations and formal compliance certification.

## Checks

- `git diff --check`
- focused backend audit/export tests
- frontend tests/typecheck/build if surfaces change
- static asset and export secret scans
- `tools/validate-ai-first-saas-starter-fullstack.sh`

## Done criteria

- Enterprise audit/export foundations work locally and are documented without overclaiming vendor/compliance completion.
