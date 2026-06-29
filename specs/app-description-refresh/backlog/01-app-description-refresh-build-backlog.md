# App-description Refresh Build Backlog

## ADR-01: Audit shared foundation contracts

- task: `TASK-ADR-01-001`
- brief: `tasks/01-shared/01-audit-shared-foundation.md`
- goal: replace `shared-foundation-audit.md` checklist with concrete findings against current skills-pack graph doctrine.
- output: shared finding table, affected workstream map, questions/follow-ups.
- validation: `git diff --check` and coverage proof over shared/global/domain files.

## ADR-02: Refresh shared foundation app-description artifacts

- task: `TASK-ADR-01-002`
- brief: `tasks/01-shared/02-refresh-shared-foundation.md`
- goal: revise shared app/global/domain/capability/data-state artifacts before workstream-local refreshes.
- output: updated `app-description/app.md`, `global/**`, domain/capability/data-state files, and shared conventions.
- validation: `git diff --check` and graph-link proof for global/domain artifacts.

## ADR-03: Refresh My Account workstream

- task: `TASK-ADR-02-001`
- brief: `tasks/02-workstreams/01-refresh-my-account.md`
- plan: `workstreams/my-account-migration-plan.md`
- goal: revise My Account workstream graph, lifecycle, source-alignment, tests, traces, and runtime-validation expectations.

## ADR-04: Refresh User Admin workstream

- task: `TASK-ADR-02-002`
- brief: `tasks/02-workstreams/02-refresh-user-admin.md`
- plan: `workstreams/user-admin-migration-plan.md`
- goal: revise User Admin workstream graph, lifecycle, source-alignment, tests, traces, and runtime-validation expectations.

## ADR-05: Refresh Agent Admin workstream

- task: `TASK-ADR-02-003`
- brief: `tasks/02-workstreams/03-refresh-agent-admin.md`
- plan: `workstreams/agent-admin-migration-plan.md`
- goal: revise Agent Admin workstream graph, lifecycle, source-alignment, tests, traces, and runtime-validation expectations.

## ADR-06: Refresh Governance/Policy workstream

- task: `TASK-ADR-02-004`
- brief: `tasks/02-workstreams/04-refresh-governance-policy.md`
- plan: `workstreams/governance-policy-migration-plan.md`
- goal: revise Governance/Policy workstream graph, lifecycle, source-alignment, tests, traces, and runtime-validation expectations.

## ADR-07: Refresh Audit/Trace workstream

- task: `TASK-ADR-02-005`
- brief: `tasks/02-workstreams/05-refresh-audit-trace.md`
- plan: `workstreams/audit-trace-migration-plan.md`
- goal: revise Audit/Trace workstream graph, lifecycle, source-alignment, tests, traces, and runtime-validation expectations.

## ADR-08: Cross-workstream consistency and readiness pass

- task: `TASK-ADR-03-001`
- brief: `tasks/03-consistency/01-cross-workstream-consistency-readiness.md`
- goal: verify shared conventions, workstream bindings, source-alignment, lifecycle, and runtime-validation scenario coverage across all refreshed workstreams.

## ADR-99: Terminal verification

- task: `TASK-ADR-99-001`
- brief: `tasks/99-verification/01-terminal-verification.md`
- goal: verify mini-project done state or append follow-up bounded tasks plus a new terminal verification task.
