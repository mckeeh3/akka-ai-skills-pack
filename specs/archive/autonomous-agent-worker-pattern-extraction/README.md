# AutonomousAgent Worker Pattern Extraction

## Purpose

Extract the reusable AutonomousAgent worker pattern from the two completed starter verticals:

1. User Admin Access Review AutonomousAgent.
2. Agent Admin Prompt-Risk Review AutonomousAgent.

The goal is to turn those concrete implementations into skills-pack guidance, docs, routing, and examples so future generated apps can add durable internal/background workers without rediscovering the pattern or weakening runtime completion standards.

## Source context

- `specs/autonomous-agent-runtime-integration/`
- `specs/agent-admin-prompt-risk-autonomous-agent/`
- `specs/workstream-event-backbone-v3/`
- `specs/workstream-attention-backbone-v1/`
- `specs/workstream-attention-event-producers-v2/`
- starter template backend/frontend AutonomousAgent worker implementation files
- existing AutonomousAgent skills/docs in `skills/` and `docs/`

## Scope

- Inventory common pattern elements across the two verticals.
- Define the standard worker contract: task schema, result schema, capabilities, v3 events, attention, surfaces, provider fail-closed behavior, tests, and validation.
- Update or add skills/docs so future worker implementation tasks route correctly.
- Add references to the two implemented starter examples.
- Identify next candidate workers without implementing them.

## Non-goals

- Do not implement another AutonomousAgent worker in this mini-project.
- Do not rewrite all existing agent skills.
- Do not relax provider/model fail-closed or no fake success guardrails.
- Do not make the pattern generic enough to obscure concrete capability/workstream/surface contracts.

## Execution model

Execute one task per fresh harness context. Each task must update `pending-tasks.md`, run checks or record blockers, and make one focused commit.

## Sprint sequence

1. **Inventory and pattern map** — compare the two completed workers and extract reusable decisions.
2. **Docs/pattern guidance** — create/update a focused doc for AutonomousAgent worker runtime pattern.
3. **Skill routing updates** — update relevant AutonomousAgent/workstream/capability skills to reference the pattern.
4. **Example index/handoff** — add references to starter examples and next worker candidates.
5. **Verification** — confirm guidance is discoverable, accurate, and not over-broad.

## Done state

Complete when the repository has:

- a reusable AutonomousAgent worker pattern doc;
- updated routing/skill guidance for when and how to use the pattern;
- explicit runtime completion guardrails for real Akka `AutonomousAgent`, provider fail-closed, no model-less success, v3 events, attention, and surfaces;
- references to User Admin Access Review and Agent Admin Prompt-Risk as examples;
- next-worker recommendations documented without creating implementation queues;
- checks proving no stale guidance contradicts the new pattern.
