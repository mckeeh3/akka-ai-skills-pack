# Runtime-validation Corpus Plan

This plan was updated by `TASK-ADIA-01-002` to turn the initial scaffold into authored scenario definitions. It remains an evidence-planning artifact only; no runtime-validation run has been executed and no workstream is claimed runtime-ready from this corpus.

## Authored structure

```text
specs/runtime-validation/
  README.md
  environments/local-dev.md
  personas/member.md
  personas/organization-admin.md
  personas/saas-admin.md
  personas/support-operator.md
  data-setups/base-organization.md
  scenarios/my-account/RV-MY-ACCOUNT-001-login-and-account-context.md
  scenarios/user-admin/RV-USER-ADMIN-001-invite-user.md
  scenarios/agent-admin/RV-AGENT-ADMIN-001-provider-fail-closed-test-console.md
  scenarios/governance-policy/RV-GOVPOL-001-policy-decision-card.md
  scenarios/audit-trace/RV-AUDIT-001-trace-search-denial-redaction.md
  runs/README.md
```

## Scenario authoring rule

Scenario files document the intended real runtime path, setup prerequisites, steps, expected results, evidence to capture, and failure classification hints. They must not claim a run passed until a later runtime-validation execution task records evidence under `specs/runtime-validation/runs/`.

## Minimum first scenario set

| Workstream | Scenario id | Validation focus | Current corpus state |
| --- | --- | --- | --- |
| My Account | `RV-MY-ACCOUNT-001` | login, `/api/me`, account context, denial/open-disabled behavior | authored, not executed |
| User Admin | `RV-USER-ADMIN-001` | invite user, invitation result/audit, forbidden non-admin denial | authored, not executed |
| Agent Admin | `RV-AGENT-ADMIN-001` | provider-missing fail-closed test console, loader/tool-boundary denial, frontend secret boundary | authored, not executed |
| Governance/Policy | `RV-GOVPOL-001` | policy proposal/decision card, approval evidence, idempotent decision behavior, explicit gap classification if blocked | authored, not executed |
| Audit/Trace | `RV-AUDIT-001` | scoped trace search/detail, denied trace read, redaction/support-scope behavior | authored, not executed |

## Reusable setup coverage

- `environments/local-dev.md` records the local-empty runtime start contract, provider state requirements, and evidence expected in future runs.
- Persona docs record AuthContext, roles, tenant/organization/support scope, allowed validation focus, and denial expectations.
- `data-setups/base-organization.md` records a local-seeded base organization setup contract, setup boundaries, idempotency expectations, evidence, and blocker categories.

## Next execution requirements

A later runtime-validation execution task must:

1. start the local app from empty/reset persistence;
2. record concrete backend/frontend start commands and local URLs;
3. prepare the base organization through the highest-fidelity available app path;
4. log in through WorkOS/AuthKit test users or classify an auth/setup blocker;
5. execute one or more scenario scripts through the real frontend/API/Akka path;
6. record setup evidence separately from validation evidence under `specs/runtime-validation/runs/`;
7. classify failures as implementation, app-description, provider/config, seed-data, auth/setup, seed-tooling, UX/state, test, or frontend secret-boundary gaps.

## Explicit non-claims

- This task did not start the app.
- This task did not execute browser, API, provider, or Akka runtime validation.
- This task did not add run records.
- This task did not mark any scenario passed or any workstream runtime-ready.
