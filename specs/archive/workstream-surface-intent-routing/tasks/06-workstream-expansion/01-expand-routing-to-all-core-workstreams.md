# Task: Expand routing to all core workstreams

## Objective

Use the surface catalog to add high-confidence deterministic open/prefill routing for My Account, Agent Admin, Audit/Trace, and Governance/Policy, while preserving User Admin routing.

## Required reads

- `AGENTS.md`
- `specs/workstream-surface-intent-routing/README.md`
- `specs/workstream-surface-intent-routing/sprints/03-catalog-and-expansion.md`
- surface catalog from previous task
- router implementation files
- relevant frontend surface components and tests

## Skills

- capability-first-backend
- akka-web-ui-apps
- ai-first-saas-ui-surfaces

## Expected outputs

- Representative deterministic routes for each core workstream.
- Safe fallback for ambiguous/high-risk prompts.
- Contract tests covering at least one matched route per workstream.
- Queue update.

## Required checks

- `git diff --check`
- targeted backend router tests
- `npm --prefix frontend test -- --run`
- `npm --prefix frontend run typecheck`

## Done criteria

- All five core workstreams have at least one useful deterministic route.
- Destructive or approval-gated asks open relevant surfaces or fall back; they do not execute commands.
- Existing User Admin Organization Create routing remains covered.
- Changes and queue update are committed.
