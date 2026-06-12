# TASK-ODR-04-001: Create deployment runbook and smoke checklist

## Intent

Create operator-facing runbook documentation for startup, validation, smoke checks, troubleshooting, rollback, and external-provider readiness.

## Required reads

- `AGENTS.md`
- `specs/operational-deployment-readiness/README.md`
- env docs and readiness docs from prior tasks
- `specs/user-admin-browser-workstream-smoke/smoke-command.md`
- `specs/archive/user-admin-production-runtime-hardening/production-runtime-verification.md`

## Skills

- `akka-web-ui-testing`
- `akka-http-endpoint-testing`
- `akka-resend-email-service`
- `akka-agent-model-governance`

## Expected outputs

- Deployment runbook and smoke checklist under `docs/` or mini-project docs.
- Optional updates to root README linking the runbook.

## Required checks

```bash
git diff --check
npm --prefix frontend run smoke:user-admin-workstream
```

## Done criteria

- Runbook explains env setup, startup, smoke commands, expected pass/fail states, provider/model credential checks, static frontend build, rollback/troubleshooting.
- Commands are copy/pasteable and include `ADMIN_USERS` caveat.
- Docs distinguish default local validation from optional credentialed external-provider smoke.

## Vertical workstream contract

- Scope: deployment docs/runbook.
- Non-attention reason: operational documentation.
- Surfaces: smoke references User Admin and readiness surfaces.
- Substrate: docs plus smoke command validation.
- Validation: smoke command and diff check.
