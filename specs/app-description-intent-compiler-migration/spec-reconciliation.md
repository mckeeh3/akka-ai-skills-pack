# Spec Reconciliation: Current-intent Graph Migration

- task: TASK-ADICM-03-001
- scope: docs-only active spec/readiness/queue reconciliation
- result: active non-archive planning artifacts now point to the reconstructed current-intent graph rather than requiring the legacy numbered `app-description/` taxonomy as authority

## Reconciled active artifacts

Updated active planning/readiness references in:

- `specs/full-core-saas-readiness/README.md`
- `specs/full-core-saas-readiness/pending-tasks.md`
- `specs/full-core-saas-readiness/full-core-readiness-gap-contract.md`
- `specs/full-core-saas-readiness/full-core-readiness-verification.md`
- `specs/full-core-saas-readiness/tasks/**/*.md`
- `specs/web-ui-design/*.md`
- `specs/tasks/01-user-admin-workstream-v0/03-capability-denials-audit-traces.md`
- `specs/secure-ai-first-saas-core-starter-content-review.md`
- `specs/app-description-intent-compiler-migration/source-inventory.md`

## New authority mapping

| Former legacy reference family | Current graph authority |
|---|---|
| `app-description/00-system/readiness-status.md` | `app-description/app.md`, `app-description/domains/core-starter/realization/traceability.md`, plus readiness evidence under `specs/full-core-saas-readiness/**` |
| `app-description/10-capabilities/**` | `app-description/domains/core-starter/capabilities/**` |
| `app-description/12-workstreams/**` | `app-description/domains/core-starter/workstreams/**` and `app-description/global/agents/foundation-functional-agents.md` |
| `app-description/15-operating-model/**` | `app-description/app.md`, `app-description/global/**`, and workstream policy/trace/test bindings |
| `app-description/40-auth-security/**` | `app-description/global/policies/foundation-security-and-governance.md`, `app-description/global/roles/foundation-roles.md`, and workstream `access.md` files |
| `app-description/50-observability/**` | `app-description/global/traces/foundation-trace-patterns.md` and workstream `traces/work-traces.md` files |
| `app-description/55-ui/**` | `app-description/global/surfaces/foundation-surface-patterns.md` and workstream `surfaces/surfaces.md` plus `realization/frontend-routes.md` files |
| `app-description/70-traceability/**` | `app-description/domains/core-starter/realization/traceability.md` and workstream `realization/**` files |

## Queue maintenance

`specs/full-core-saas-readiness/pending-tasks.md` was updated to use current graph reads. Because this materially changed queue entries, the pending-task workstream contract validator was run and the pending terminal verification task was repaired with an explicit docs-only vertical contract.

## Drift and deferrals

No new runtime drift requiring implementation was discovered in this task. Existing provider/billing/timer/tooling status remains governed by `specs/full-core-saas-readiness/**` evidence:

- live WorkOS/AuthKit provider smoke remains blocked until a real AuthKit access token/session token is supplied;
- live Resend and model-provider smokes have passed where evidence says so;
- billing implementation and timer-backed invitation reminders remain deferred by accepted scope; and
- optional workstream icon validation tooling has been repaired.

Historical references under `specs/archive/**` and migration provenance under `specs/app-description-intent-compiler-migration/archive/**` were intentionally left unchanged. Sprint 04 owns archive-dependency scrub/removal.

## Proof commands

```bash
rg -n "app-description/(00-system|10-capabilities|12-workstreams|15-operating-model|20-behavior|40-auth-security|50-observability|55-ui|70-traceability|80-review)" specs docs app-description --glob '!specs/archive/**' --glob '!specs/app-description-intent-compiler-migration/**' || true
rg -n "app-description/(global|domains/core-starter|app\.md)|current-intent graph" specs/full-core-saas-readiness specs/web-ui-design specs/tasks/01-user-admin-workstream-v0 specs/secure-ai-first-saas-core-starter-content-review.md
bash .agents/skills/tools/validate-pending-task-workstream-contract.sh specs/full-core-saas-readiness/pending-tasks.md
git diff --check
```
