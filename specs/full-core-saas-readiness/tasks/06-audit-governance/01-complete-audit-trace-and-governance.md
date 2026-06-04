# TASK-FCSR-06-001: Complete Audit/Trace and Governance/Policy readiness

## Objective

Close Audit/Trace and Governance/Policy full-core readiness gaps for searchable investigations, scoped redaction/export, investigation notes, policy proposals, impact/simulation, approval, activation/rollback, and outcome evidence.

## Required reads

- full-core readiness gap contract from `TASK-FCSR-01-001`
- `app-description/10-capabilities/03-governance-decisions-and-audit.md`
- `app-description/12-workstreams/surface-contracts/05-decision-card.md`
- `app-description/12-workstreams/surface-contracts/06-audit-trace-explorer.md`
- `app-description/12-workstreams/surface-contracts/07-agent-governance-center.md`
- `src/main/java/ai/first/application/foundation/audit/**`
- `src/main/java/ai/first/application/foundation/governance/**`
- `src/main/java/ai/first/application/coreapp/audit/**`
- `src/main/java/ai/first/application/coreapp/governance/**`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- relevant frontend surfaces/tests

## Skills

- `ai-first-saas-audit-trace`
- `ai-first-saas-policy-governance`
- `ai-first-saas-decision-cards`
- `akka-views`
- `akka-workflows`
- `akka-web-ui-state-rendering`

## In scope

- Fill missing Audit/Trace and Governance/Policy behavior identified by the gap contract.
- Preserve scoped redaction, export denial/approval, investigation notes, decision cards, and trace links.
- Ensure policy activation/rollback requires proper authority and audit/work traces.
- Add/update backend/frontend tests.

## Out of scope

- Domain-specific policy packs.
- Cross-tenant SaaS-owner support access beyond selected full-core scope.

## Expected outputs

- Backend/frontend/doc/test updates for Audit/Trace and Governance/Policy readiness.

## Required checks

- `git diff --check`
- focused audit/governance/backend workstream tests
- frontend tests/typecheck/build if UI changes

## Done criteria

- Audit/Trace and Governance/Policy meet selected full-core scope through real backend/API/UI paths or precise blockers are recorded.
- Changes and queue update are committed.

## Commit message

`full-core-ready: complete audit governance`
