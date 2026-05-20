# Sprint 01: Routing and Intake Alignment

## Objective

Ensure the top-level intake path for generated SaaS apps consistently decomposes intent as:

```text
secure SaaS foundation
→ agent workstream application model
→ functional agents and internal agents
→ structured surfaces and surface actions
→ governed backend capabilities
→ Akka component realization
```

This sprint focuses on routing/readiness docs and broad entry skills. It should make it difficult for future agents to jump directly from a PRD or feature idea to CRUD pages, endpoints, agent tools, or Akka entities without first modeling workstreams and surfaces.

## Scope

Review and update only the smallest source files needed, likely including:

- `skills/README.md`
- `skills/ai-first-saas/SKILL.md`
- `skills/agent-workstream-apps/SKILL.md`
- `skills/capability-first-backend/SKILL.md`
- `skills/akka-solution-decomposition/SKILL.md`
- `docs/agent-workstream-application-architecture.md`
- `docs/capability-first-backend-architecture.md`
- `AGENTS.md` / `pack/AGENTS.md` only if routing behavior needs clarification

## Deliverables

- Top-level routing explicitly requires agent workstream modeling before capability/component decomposition.
- Capability-first guidance clearly states that capabilities are the backend substrate below workstreams/surfaces, not a replacement for workstream modeling.
- Solution decomposition output requires functional agents, workstreams, surfaces, surface action mappings, and then capability-to-component mappings for generated SaaS apps.
- Sprint review records remaining routing gaps and creates follow-up tasks if needed.

## Checks

- `git diff --check`
- Source/installed-pack consistency spot check if `.agents/` is present.
