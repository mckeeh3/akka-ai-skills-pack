# Whole-Pack Capability-First Consistency Review

## Scope

Task: `TASK-06-001`

Reviewed the whole-pack routing spine for coherence from natural-language input through implementation routing:

- `AGENTS.md`
- `skills/README.md`
- `docs/ai-first-saas-application-architecture.md`
- `docs/capability-first-backend-architecture.md`
- `skills/capability-first-backend/SKILL.md`
- Sprint 5 review reports for doctrine/routing, description/decomposition, component skills, examples/tests, and duplicate content cleanup

## Summary

The pack now presents one coherent design path:

```text
natural language product input
→ secure AI-first SaaS interpretation
→ mandatory core SaaS foundation
→ governed backend capability inventory
→ authority/scope/schema/side-effect/audit/approval/test semantics
→ description-first, decomposition, PRD/backlog, or focused implementation route
→ selected Akka components and exposure surfaces preserving the capability contract
```

The reviewed guidance consistently treats backend capabilities as the root abstraction and treats entities, workflows, views, consumers, timers, endpoints, browser actions, MCP surfaces, and agent tools as realization or exposure choices.

## Fixes made in this task

1. Aligned the short sequence in `docs/capability-first-backend-architecture.md` with the rest of the doctrine by placing selected exposure-surface decisions before Akka component realization.
2. Tightened the `skills/README.md` component-tool routing entry so it describes component tools as selected View/entity/workflow capability surfaces rather than a generic invitation to call components as tools.

## Findings

### Coherent paths verified

- Top-level repository instructions route high-level product input through secure AI-first SaaS interpretation and governed backend capabilities before description/decomposition/component implementation.
- `skills/README.md` positions `capability-first-backend` as the substrate below secure AI-first SaaS and above app-description, direct decomposition, PRD/backlog, and Stage 3 skills.
- `docs/ai-first-saas-application-architecture.md` preserves the mandatory secure SaaS and web UI foundation while adding capability-first backend modeling below the AI-first operating model.
- `docs/capability-first-backend-architecture.md` defines the capability contract fields and rejects CRUD-first, endpoint-first, entity-first, and agent-tool-root design for broad product input.
- `skills/capability-first-backend/SKILL.md` provides a concise routing handoff without duplicating downstream skills.
- Sprint 5 reports show app-description/decomposition paths, component skills, examples/tests, and duplicate migration content were already reviewed and either fixed or intentionally retained.

### No new residual issues queued

No additional whole-pack consistency task is required from this review. Security/governance details and example/test coverage are intentionally covered by the next queued Sprint 6 tasks.

## Checks performed

- `rg` review of broad product/PRD/feature routing language across `skills/**/*.md`.
- `rg` review of capability-first references across skill files.
- `rg` review for prompt-only/frontend-only authorization language; remaining matches are prohibitions or unrelated UI navigation text.
- `rg` review for all-tools/all-component exposure language; remaining matches prohibit broad tool exposure or are descriptive headings.
- `rg` review for CRUD/entity/tool/component-first routing language; remaining matches are anti-pattern guidance or qualified Stage 3 routing.
- `git diff --check` after cleanup edits.
