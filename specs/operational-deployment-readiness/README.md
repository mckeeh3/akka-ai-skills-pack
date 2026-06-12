# Operational Deployment Readiness Mini-Project

## Purpose

Prepare the hardened SaaS Foundation App for real deployment operation by documenting and validating environment configuration, secrets boundaries, health checks, smoke commands, observability expectations, and operational runbooks.

This mini-project follows completed User Admin surface conformance, browser smoke, and production runtime hardening work. It is root app realization work and should not edit `skills-pack/**` or installed `.agents/**` assets.

## Current intent

The app now has hardened User Admin runtime behavior, smoke commands, and fail-closed provider/model paths. The next readiness gap is operational clarity: an operator should know which environment variables are required, how to distinguish local/test from production, how to run confidence checks, how to diagnose provider/model/outbox/auth failures, and what health/observability signals prove the app is ready.

## Done state

This mini-project is complete when:

1. required and optional environment variables are documented with ownership, runtime boundary, safe defaults, failure behavior, and local/test/prod guidance;
2. secret handling and frontend/backend boundary rules are documented and validated by checks;
3. deployment/runbook docs explain startup, smoke validation, provider/model credential checks, fail-closed behavior, rollback, and troubleshooting;
4. health/readiness or diagnostic endpoints/signals are documented or implemented at the smallest necessary scope;
5. smoke command guidance includes User Admin, frontend build/static asset validation, and broad confidence checks;
6. tests or scripts validate the deployment readiness docs/config assumptions where practical;
7. terminal verification runs/reviews the required commands and appends follow-up tasks plus a new terminal verification task if material gaps remain.

## Non-goals

- Deploying to a specific cloud provider.
- Adding real provider credentials to the repository.
- Replacing WorkOS, Resend, or model providers.
- Reopening completed User Admin production runtime hardening except for deployment/runbook references.
- Building comprehensive SRE dashboards; this is deployment readiness baseline work.

## Primary source artifacts

- `AGENTS.md`
- `README.md`
- `pom.xml`
- `frontend/package.json`
- `src/main/resources/**`
- `src/main/java/ai/first/api/**`
- `src/main/java/ai/first/application/foundation/**`
- `src/main/java/ai/first/application/coreapp/**`
- `docs/**`
- `tools/**`
- `specs/archive/user-admin-production-runtime-hardening/**`
- `specs/user-admin-browser-workstream-smoke/**`

## Task execution rules

Use `specs/operational-deployment-readiness/pending-tasks.md`. Execute one task per fresh harness context, update task status before implementation edits, run the task's checks, and commit each completed task with the queue update.
