# TASK-FC5-01-001: Define full-core workstream/surface/agent/capability contract matrix

## Objective

Create the implementation contract for all five full-core workstreams before coding rich behavior.

## Required reads

- `docs/requirements-to-workstream-development-process.md`
- `docs/agent-workstream-application-architecture.md`
- `docs/structured-surface-contracts.md`
- `docs/capability-first-backend-architecture.md`
- `docs/workstream-expertise-model.md`
- `templates/ai-first-saas-starter/README.md`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamService.java`

## Expected outputs

- A contract doc under `specs/full-core-five-workstreams/` that lists, for each of My Account, User Admin, Agent Admin, Audit/Trace, Governance/Policy:
  - functional agent and workstream id;
  - role-specific dashboard/attention model;
  - human surface graph nodes and actions;
  - internal workstream agent graph candidates;
  - governed capabilities and governed-tools;
  - Akka substrate choices;
  - expertise bundle changes;
  - audit/work traces and tests.
- Queue updates if contract work reveals required splits or blockers.

## Checks

- `git diff --check`
- `rg -n "workstream|surface|governed-tool|capability|AgentDefinition|ToolPermissionBoundary|trace" specs/full-core-five-workstreams`

## Done criteria

Future implementation tasks can execute without guessing the workstream/surface/capability chain.
