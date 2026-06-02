# Task Brief: Digest and Export Platform Extensions

## Objective

Implement digest/export platform behavior beyond the bounded My Account personal digest and Audit/Trace summary workers.

## Required reads

- `specs/core-app-feature-completion/README.md`
- `specs/core-app-feature-completion/sprints/03-workers-governance-sprint.md`
- `templates/ai-first-saas-starter/README.md`
- `docs/ai-first-saas-application-architecture.md`
- `docs/capability-first-backend-architecture.md`
- `skills/akka-workflows/SKILL.md`
- `skills/akka-timed-actions/SKILL.md`
- `skills/akka-http-endpoints/SKILL.md`

## In scope

- Scheduled/manual digest jobs, export request lifecycle, redaction profiles, approval gates for sensitive export, idempotency, and audit.
- Digest/export status surfaces and failure/retry behavior.
- Tests for tenant isolation, sensitive/secret redaction, export audit, duplicate requests, and provider/runtime blocked states where model summarization is used.

## Out of scope

- SIEM/legal hold/e-discovery provider integrations; those are enterprise tasks.

## Checks

- `git diff --check`
- focused backend workflow/timer/export tests
- frontend tests/typecheck/build if surfaces change
- `tools/validate-ai-first-saas-starter-fullstack.sh`

## Done criteria

- Digest/export platform paths work locally through backend capabilities with redaction/audit and do not infer broader compliance-suite completion.
