# TASK-ODR-03-001: Document or implement health/readiness diagnostics

## Intent

Define and, if necessary, implement the smallest health/readiness diagnostics needed for deployment validation.

## Required reads

- `AGENTS.md`
- `specs/operational-deployment-readiness/README.md`
- `specs/operational-deployment-readiness/env-config-inventory.md`
- env/secret docs from `TASK-ODR-02-001`
- `src/main/java/ai/first/api/**`
- `src/main/java/ai/first/application/foundation/**`
- `src/test/java/ai/first/**`

## Skills

- `akka-http-endpoints`
- `akka-http-endpoint-testing`
- `akka-agent-model-governance`
- `akka-resend-email-service`

## Expected outputs

- Health/readiness diagnostic docs and optional endpoint/test changes.

## Required checks

```bash
git diff --check
env -u ADMIN_USERS mvn -q -Dtest=WorkstreamServiceTest test
```

Adjust focused test command if endpoint/tests are added.

## Done criteria

- Startup/frontend/workstream/auth/email/model/audit readiness checks are documented or implemented.
- Missing external config is distinguishable from healthy configured readiness.
- Diagnostics avoid leaking secrets or hidden tenant/customer data.

## Vertical workstream contract

- Scope: cross-cutting operational diagnostics.
- Attention: operational readiness only, no user-facing workstream item unless existing surfaces expose fail-closed state.
- Capabilities/AuthContext: diagnostics must not bypass auth or reveal hidden data.
- Substrate: docs and optional HTTP endpoint/tests.
- Validation: focused Maven and diff check.
