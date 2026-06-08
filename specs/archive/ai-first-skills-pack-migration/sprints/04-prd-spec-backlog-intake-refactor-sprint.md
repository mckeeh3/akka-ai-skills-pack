# Sprint 4: PRD, Spec, Backlog, and Change-Intake Refactor

## Sprint goal

Refactor direct planning flows so PRDs, specs, feature requests, issues, and fixes are translated into AI-first application architecture before Akka component selection and backlog generation.

## Dependencies

- Sprint 1 doctrine complete.
- Sprint 2 AI-first routing skill family available.
- Sprint 3 app-description refactor mostly complete or intentionally coordinated.

## Scope

Update planning skills so they first derive the agentic operating model:

- operational work delegated to agents
- human governance boundaries
- durable goals/objectives
- policies, authority, permissions, and approval gates
- exception routing and decision cards
- evidence, confidence, risk, stakes, and alternatives
- work traces, audit events, and outcome links
- supervision, digest, governance, and audit UI surfaces

Then map those to Akka components and implementation tasks.

## Primary files likely affected

- `skills/akka-solution-decomposition/SKILL.md`
- `skills/akka-prd-to-specs-backlog/SKILL.md`
- `skills/akka-change-request-to-spec-update/SKILL.md`
- `skills/akka-revised-prd-reconciliation/SKILL.md`
- `skills/akka-pending-question-generation/SKILL.md`
- `skills/akka-backlog-to-pending-tasks/SKILL.md`
- `skills/akka-do-next-pending-task/SKILL.md` where task execution guidance needs AI-first context loading
- `docs/prd-to-akka-flow.md`
- `docs/intent-driven-usage-flow.md`
- `docs/module-sprint-planning.md`

## Acceptance behavior

- A conventional PRD is examined for opportunities and requirements to become AI-first, not blindly decomposed into CRUD modules.
- Planning outputs include agentic substrate tasks where relevant.
- Pending questions capture unresolved AI-first decisions rather than guessing.
- Backlogs remain one-session executable.

## Done criteria

- Stage 1 decomposition explicitly invokes AI-first interpretation when applicable.
- PRD-to-specs backlog outputs support AI-first app-description/spec/backlog trees.
- Change request reconciliation preserves agentic governance and audit implications.
