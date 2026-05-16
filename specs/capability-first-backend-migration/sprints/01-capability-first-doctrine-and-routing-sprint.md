# Sprint 1: Capability-First Doctrine and Routing

## Sprint goal

Make capability-first backend architecture explicit and authoritative enough that subsequent skill refactors can proceed consistently.

## Scope

- Add canonical doctrine for capability-first backend architecture for agentic systems.
- Update the existing AI-first SaaS doctrine so capabilities become the backend substrate under secure AI-first SaaS.
- Add a top-level routing skill or equivalent entry guidance for capability-first backend design.
- Update the skill routing map so high-level implementation planning identifies governed capabilities before Akka components or agent tools.

## Non-goals

- Do not refactor every component skill in this sprint.
- Do not create large executable examples in this sprint.
- Do not weaken mandatory secure SaaS foundation requirements.
- Do not make agent tools the root abstraction; the root abstraction is capability.

## Key inputs

- `AGENTS.md`
- `skills/README.md`
- `docs/ai-first-saas-application-architecture.md`
- `docs/agent-coverage-matrix.md`
- `akka-context/sdk/agents/extending.html.md`
- `skills/akka-agent-tools/SKILL.md`
- `skills/akka-agent-component-tools/SKILL.md`

## Expected outputs

- Canonical capability-first doctrine under `docs/`.
- AI-first SaaS doctrine updated to reference capability-first backend substrate.
- Skill routing map updated with capability-first route language.
- Top-level capability-first skill created or clearly planned.
- Pending tasks updated and committed one task at a time.

## Acceptance behavior

A future agent reading `AGENTS.md`, `skills/README.md`, and the canonical doctrine should understand that backend functionality should be modeled as governed capabilities before deciding whether to expose it through agent tools, UI actions, APIs, workflows, MCP, consumers, or timers.

## Done criteria

- Sprint 1 pending tasks are completed and marked done.
- The doctrine clearly distinguishes capabilities from agent tools.
- Existing secure AI-first SaaS requirements remain mandatory.
- There are no broken links to new doctrine or skill files.

## Defer list

- App-description layer changes.
- PRD/spec/backlog intake changes.
- Component skill rewrites.
- Example/test implementation.
- Stale content deletion beyond directly affected routing/doc text.
