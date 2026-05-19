# Sprint 3: UI and Agent Skill Realignment

## Goal

Update web UI and agent skills so they implement the agent workstream shell and structured surface model.

## Scope

- Revise web UI skills to target left-rail functional agents, stream panel, bottom composer, structured response surfaces, and deep links into stream artifacts.
- Revise agent skills to distinguish functional/context-area agents from internal agents.
- Add guidance for renderable surface contracts and typed frontend/backend payloads.
- Update endpoint guidance to expose workstream APIs and surface/action APIs while preserving capability-first auth/audit.
- Add or revise tests guidance for surface rendering, action authorization, trace creation, and stream continuity.

## Out of scope

- Rewriting all component implementation skills unless they explicitly contradict the model.
- Creating a production design system.

## Done criteria

- UI skills no longer center conventional pages/routes as the default authenticated app structure.
- Agent skills describe user-facing functional agents and internal agents clearly.
- Structured surfaces are first-class outputs of agent workstreams.
