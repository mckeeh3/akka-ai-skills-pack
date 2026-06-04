# TASK-WDA-01-002: Expand surface governed-tool mappings

## Objective

Update app-description capability and surface maps so core workstream surfaces/actions name exact governed-tool ids and exposure channels instead of only broad capability groups.

## Required reads

- mini-project README, conversation capture, sprint, backlog, queue entry, and this task brief
- `app-description/10-capabilities/capabilities-index.md`
- `app-description/10-capabilities/01-secure-tenant-user-foundation.md`
- `app-description/10-capabilities/03-governance-decisions-and-audit.md`
- `app-description/10-capabilities/04-frontend-shell-and-integration-patterns.md`
- `app-description/10-capabilities/05-managed-agent-foundation.md`
- `app-description/12-workstreams/surfaces-index.md`
- `app-description/70-traceability/surface-to-capability-map.md`
- `app-description/70-traceability/workstream-id-map.md`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `frontend/src/workstream/types/actions.ts`

## Skills

- `capability-first-backend`
- `app-description-capability-modeling`
- `app-description-surface-modeling`

## In scope

- Expand action mappings for My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy starter surfaces.
- Include qualified exposure labels such as browser-tool, agent-tool, internal-tool, workflow-tool, timer-tool, consumer-tool, or API where applicable.
- Preserve broad capability grouping while adding exact governed-tool ids.

## Out of scope

- Implementing missing governed-tools.
- Large capability-file rewrites unrelated to the mapped workstream actions.

## Expected outputs

- Updated `app-description/70-traceability/surface-to-capability-map.md` and/or linked capability files.
- Clear mapping from surface action ids to capability ids and governed-tool ids.

## Required checks

- `git diff --check`
- focused `rg` for representative exact ids such as `my_account.view_summary`, `attention.open_attention_item`, `agent_admin.list_definitions`, `audit.trace.search`, and `governance.policy.simulate`

## Done criteria

- Surface/action mapping is implementation-ready for runtime/UI tasks.
- Changes and queue update are committed.

## Commit message

`workstream-align: map surface governed tools`
