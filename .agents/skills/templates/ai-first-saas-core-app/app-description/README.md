# Legacy SaaS Foundation App description template

This template preserves the pre-intent-compiler SaaS Foundation App workstream/surface template layout. It remains available as legacy reference material for projects that already use the numbered `12-workstreams`, `55-ui`, and `70-traceability` layout. For new current-intent app-description work, prefer the intent compiler graph from `docs/current-intent-model.md` (`app.md`, `global/**`, and `domains/<domain>/workstreams/<workstream>/**`) plus source-alignment guidance from `docs/app-description-source-alignment.md`; copy only the smallest relevant content after mapping it into that structure.

The template is intentionally app-description-only. It is not a runnable app baseline, does not replace the upstream SaaS Foundation App implementation, and does not claim that the listed foundation surfaces are app-level complete. Treat the surface contracts as process examples / `surface-ready` baselines. Specific workstream surface implementation cleanup belongs in separate app-development tasks.

Use with companion pack guidance. From an installed skills directory, resolve these paths under `.agents/skills/`; from a source checkout, resolve them under `skills-pack/`:

- `docs/intent-compiler.md`
- `docs/current-intent-model.md`
- `docs/intent-to-realization-flow.md`
- `docs/workstream-contract.md`
- `docs/workstream-manifest-schema.md`
- `docs/minimum-implementable-workstream-slice.md`
- `docs/workstream-attention-contracts.md`
- `docs/agent-workstream-application-architecture.md`
- `docs/requirements-to-workstream-development-process.md`
- `docs/structured-surface-contracts.md`
- `app-description-functional-agent-modeling/SKILL.md`
- `app-description-surface-modeling/SKILL.md`

## Included SaaS Foundation App layers

```text
12-workstreams/
  workstream-manifest.json
  functional-agents.md
  workstreams-and-retention.md
  attention-and-dashboards.md
  internal-agents.md
  surfaces-index.md
  surface-graph.md
  deferred-typed-surfaces.md
  foundation-workstream-completeness.md
  surface-contracts/*.md
  workstream-expertise/*.md
55-ui/
  structured-surface-rendering.md
70-traceability/
  functional-agent-to-capability-map.md
  surface-to-capability-map.md
```

## Rules

- Treat the numbered directories as legacy template folders, not the canonical structure for new current-intent descriptions. When using this content in new work, map global reusable definitions into `app-description/global/**` and workstream-specific bindings into `app-description/domains/<domain>/workstreams/<workstream>/**`.
- Keep `12-workstreams/workstream-manifest.json` as the machine-readable index and `12-workstreams/**` markdown authoritative only within this legacy template for functional agents, workstream definitions, instance/retention semantics, attention, dashboard purpose, surface meaning, payloads, graph role, actions, auth, traces, and tests.
- Keep `surface-graph.md` aligned with surface contracts and traceability maps; each graph node has exactly one owner functional agent and explicit reuse.
- Keep `deferred-typed-surfaces.md` honest. First-slice fallbacks are allowed in templates, but app-level cleanup must replace consequential deferred surfaces with full contracts before claiming capability readiness for that scope.
- Keep `55-ui/**` focused on browser realization of those contracts.
- Keep `70-traceability/**` current whenever a surface or action changes.
- For new current-intent app-descriptions, also create or update each feature-bearing workstream's `realization/source-alignment.md` so graph files map to implementation/source/test evidence.
- Every protected surface action maps to a governed backend capability and qualified governed-tool exposure. If authority or stable ids are unclear during app implementation, ask or queue the blocking question instead of inventing them.
