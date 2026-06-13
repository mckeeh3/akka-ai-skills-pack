# TASK-ODR-02-001: Document environment/secret configuration and add validation

## Intent

Create deployment-ready environment documentation and add minimal validation where practical.

## Required reads

- `AGENTS.md`
- `specs/operational-deployment-readiness/README.md`
- `specs/operational-deployment-readiness/env-config-inventory.md`
- files identified by inventory

## Skills

- `akka-workos-user-auth`
- `akka-resend-email-service`
- `akka-agent-model-governance`
- `akka-web-ui-frontend-project`

## Expected outputs

- Docs under `docs/` or `specs/operational-deployment-readiness/` for env/secrets.
- Optional validation script/test for env classification and frontend secret boundary.

## Required checks

```bash
git diff --check
npm --prefix frontend test -- --run
```

Add focused Maven/script check if validation code touches backend.

## Done criteria

- Required/optional env vars are documented with local/test/prod behavior.
- Frontend-public vs backend-secret boundary is explicit.
- `ADMIN_USERS` SaaS Owner bootstrap caveat is documented.
- Missing provider/model/email config fail-closed behavior is explicit.

## Vertical workstream contract

- Scope: cross-cutting operational docs/validation.
- Non-attention reason: deployment config, not product attention.
- Capabilities/AuthContext: documents auth/tenant bootstrap boundaries.
- Substrate: docs/scripts/tests.
- Validation: frontend tests and diff check, plus any focused backend check if touched.
