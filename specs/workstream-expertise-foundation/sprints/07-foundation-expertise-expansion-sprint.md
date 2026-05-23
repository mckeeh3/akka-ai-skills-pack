# Sprint 07: Foundation Workstream Expertise Expansion

## Objective

Move beyond the single User Admin example by ensuring every foundation functional agent in the seed app-description has either a workstream expert bundle or an explicit readiness-impacting deferral. Prioritize Agent Admin and Audit/Trace because they govern and investigate the workstream expertise system itself.

## Scope

Likely source files:

- `docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/functional-agents.md`
- `docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/workstream-expertise/**`
- `docs/examples/ai-first-saas-seed-app-description/app-description/10-capabilities/05-managed-agent-foundation.md`
- `docs/examples/ai-first-saas-seed-app-description/app-description/10-capabilities/03-governance-decisions-and-audit.md`
- `docs/examples/ai-first-saas-seed-app-description/app-description/30-tests/test-index.md`
- `docs/examples/ai-first-saas-seed-app-description/app-description/70-traceability/**`
- `docs/agent-coverage-matrix.md` only if coverage status changes

## Deliverables

- Coverage audit of all foundation functional agents and whether they have bundles or explicit deferrals.
- Detailed `agent-admin-agent` expert bundle covering prompt/skill/reference/tool-boundary governance, behavior proposals, seed upgrades, approval, and tests.
- Detailed `audit-trace-agent` expert bundle covering trace search/explanation, redaction, evidence citation, export limits, denials, and tests.
- Initial bundles or explicit deferrals for `governance-policy-agent`, `mission-control-agent`, and `my-account-agent`.
- Traceability and readiness references updated so full-core readiness cannot silently rely on User Admin expertise alone.

## Checks

- `git diff --check`
- Text search proving every functional agent in the seed app-description has a matching expertise artifact or explicit deferral.
