# TASK-ODR-01-001: Inventory environment, config, docs, and scripts

## Intent

Survey all runtime/deployment configuration and existing docs/scripts before adding readiness docs or validation.

## Required reads

- `AGENTS.md`
- `specs/operational-deployment-readiness/README.md`
- `specs/operational-deployment-readiness/conversation-capture.md`
- `pom.xml`
- `frontend/package.json`
- `README.md`
- `docs/**`
- `src/main/resources/**`
- `src/main/java/ai/first/**`
- `frontend/src/**`

## Skills

- `akka-web-ui-frontend-project`
- `akka-workos-user-auth`
- `akka-resend-email-service`
- `akka-agent-model-governance`

## Expected outputs

- `specs/operational-deployment-readiness/env-config-inventory.md`
- Queue updates if task blockers/order changes.

## Required checks

```bash
git diff --check
```

## Done criteria

- Inventory captures env vars, scripts, provider/model config, static asset behavior, and known gaps.
- Next task can document/validate config without rediscovery.

## Vertical workstream contract

- Scope: cross-cutting docs/survey.
- Non-attention reason: no runtime behavior change.
- Capabilities/AuthContext: inventory must preserve auth/provider boundaries.
- Substrate: docs-only survey.
- Validation: diff check.
