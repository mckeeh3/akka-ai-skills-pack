# Sprint 02: Durable State and Views

Goal: move the starter from a single current-state repository seam toward first-class Akka components and views for managed-agent core records.

Tasks:

1. Add first-class AgentDefinition lifecycle component and views.
2. Add first-class prompt/skill/reference document components and version projections.
3. Add first-class manifest and tool-boundary components and views.
4. Add durable trace storage/search projections for prompt, skill, reference, tool, and work traces.

Acceptance:

- The core app has concrete Akka-owned state carriers for managed-agent runtime configuration.
- Runtime lookup uses stable tenant-scoped records.
- Agent Admin can later build management surfaces over these views without inventing a separate data model.
