# PRD-to-Akka flow

Use this mini-example set when you want a concrete pattern for planning mechanics. For generated SaaS architecture, start from the mandatory secure foundation, AI-first SaaS seed reference, and `requirements-to-workstream-development-process.md` before using these conventional examples.

1. read a small requirements artifact
2. apply the secure SaaS foundation and classify AI-first SaaS concerns before CRUD/page/component decomposition
3. model workstreams, attention categories, dashboard contracts, structured surfaces, and surface actions
4. map surface actions and workstream events to governed capability/API contracts
5. select Akka substrate only after capability, AuthContext, approval/policy, audit/trace, notification/projection, and participant semantics are clear
6. preserve request-based Agent turns for immediate workstream interactions and evaluate durable internal/background work as AutonomousAgent task candidates
7. produce an Akka solution plan before coding
8. route to the minimal focused skills
9. implement bounded vertical increments in the recommended order

## Generated SaaS process example

For installed-pack users, start with:
- `examples/requirements-to-workstream-mini-example.md` — compact target architecture example showing input/PRD → workstreams → attention → dashboards → surfaces/actions → governed capabilities/APIs → selected Akka substrate → request-based Agents/AutonomousAgent candidates → events/notifications/projections → audit/work traces → task shape.

## Conventional mechanics reference pair

Requirements input:
- `examples/purchase-request-prd.md`

Resulting solution plan:
- `examples/purchase-request-solution-plan.md`

Example pending task queue:
- `examples/purchase-request-pending-tasks.md`

Example module/sprint planning shape for larger PRDs:
- `examples/purchase-request-module-sprint-plan.md`

These purchase-request files are a mechanics reference for planning shape only; they are not the generated SaaS target architecture.

## How to use the pair

### As a learning example
Read the PRD first, then compare it with the solution plan.

### As a reusable prompt pattern
Use a prompt like:

```text
read docs/examples/purchase-request-prd.md,
then produce an Akka solution plan with:
- full-stack secure AI-first SaaS interpretation, including mandatory foundation and UI surfaces
- requirements-to-workstream chain: workstreams, attention/dashboard, surface action, capability, AutonomousAgent task candidate, notification/projection, and trace context
- capability summary
- chosen components
- why each component exists
- skill routing
- implementation order
- required tests
```

### As a coding handoff
Once the solution plan is accepted, treat it as the implementation contract.
If AI-first concerns are present, preserve delegated authority, policy/approval rules, trace obligations, supervision UI, and outcome semantics in every downstream backlog or task. For generated SaaS, also preserve workstream id, attention category/dashboard or surface action, capability id/class, AuthContext/scope, selected substrate, AutonomousAgent task lifecycle when applicable, notifications/projections, and local validation path.
Load only the Stage 3 skills named in the plan and generate code component by component, with corresponding tests for each component family.
For reliable multi-session execution, materialize follow-on work as `specs/pending-tasks.md` and use `akka-do-next-pending-task` to execute one task per fresh context. For larger PRDs, prefer module-oriented sprint planning with `specs/modules/` and `specs/sprints/` so each module increment can be built and tested full stack before the next sprint.
For iterative changes after the initial plan, use `akka-change-request-to-spec-update`; for revised PRDs, use `akka-revised-prd-reconciliation`; for stale or large queues, use `akka-pending-task-queue-maintenance`.
For queue templates, see `module-sprint-planning.md`, `solution-plan-to-implementation-queue.md`, and `pending-task-queue.md`.

## What the example demonstrates

The example is not an AI-first SaaS target architecture; it intentionally stays close to a conventional approval workflow while demonstrating decomposition mechanics.
For generated SaaS foundation structure, start with `examples/ai-first-saas-seed-app-description/README.md`.

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
