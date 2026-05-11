# PRD-to-Akka flow

Use this mini-example set when you want a concrete pattern for the repository's intended workflow:

1. read a small requirements artifact
2. classify whether AI-first SaaS interpretation applies before CRUD/component decomposition
3. produce an Akka solution plan before coding
4. route to the minimal focused skills
5. implement components in the recommended order

## Canonical example pair

Requirements input:
- `examples/purchase-request-prd.md`

Resulting solution plan:
- `examples/purchase-request-solution-plan.md`

Example pending task queue:
- `examples/purchase-request-pending-tasks.md`

Example module/sprint planning shape for larger PRDs:
- `examples/purchase-request-module-sprint-plan.md`

## How to use the pair

### As a learning example
Read the PRD first, then compare it with the solution plan.

### As a reusable prompt pattern
Use a prompt like:

```text
read docs/examples/purchase-request-prd.md,
then produce an Akka solution plan with:
- AI-first interpretation when delegated work, governance, supervision, audit, or outcomes are in scope
- capability summary
- chosen components
- why each component exists
- skill routing
- implementation order
- required tests
```

### As a coding handoff
Once the solution plan is accepted, treat it as the implementation contract.
If AI-first concerns are present, preserve delegated authority, policy/approval rules, trace obligations, supervision UI, and outcome semantics in every downstream backlog or task.
Load only the Stage 3 skills named in the plan and generate code component by component, with corresponding tests for each component family.
For reliable multi-session execution, materialize follow-on work as `specs/pending-tasks.md` and use `akka-do-next-pending-task` to execute one task per fresh context. For larger PRDs, prefer module-oriented sprint planning with `specs/modules/` and `specs/sprints/` so each module increment can be built and tested full stack before the next sprint.
For iterative changes after the initial plan, use `akka-change-request-to-spec-update`; for revised PRDs, use `akka-revised-prd-reconciliation`; for stale or large queues, use `akka-pending-task-queue-maintenance`.
For queue templates, see `module-sprint-planning.md`, `solution-plan-to-implementation-queue.md`, and `pending-task-queue.md`.

## What the example demonstrates

The example is not an AI-first SaaS example; it intentionally stays close to a conventional approval workflow while still demonstrating decomposition.
For AI-first reference structure, see `examples/ai-first-dca-app-description/README.md`.

The purchase-request example intentionally includes enough scope to justify multiple component families:
- one write model
- one durable orchestration flow
- one read model
- one timed behavior
- one async integration bridge
- one HTTP API surface

It is still small enough for future agents to load cheaply.

## Related docs

- `intent-driven-usage-flow.md`
- `../skills/README.md`
- `../skills/akka-solution-decomposition/SKILL.md`
