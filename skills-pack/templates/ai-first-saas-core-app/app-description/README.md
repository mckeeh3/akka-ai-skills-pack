# SaaS Foundation App description template

This template is source-controlled SaaS Foundation App description guidance for workstream and surface modeling in generated secure AI-first SaaS apps. Copy the smallest relevant files into a target project's `app-description/` tree when bootstrapping or repairing workstream contracts.

The template is intentionally app-description-only. It is not a runnable app baseline, does not replace the upstream SaaS Foundation App implementation, and does not claim that the listed foundation surfaces are app-level complete. Treat the surface contracts as process examples / `surface-ready` baselines. Specific workstream surface implementation cleanup belongs in separate app-development tasks.

Use with companion pack guidance. From an installed skills directory, resolve these paths under `.agents/skills/`; from a source checkout, resolve them under `skills-pack/`:

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

- Keep `12-workstreams/workstream-manifest.json` as the machine-readable index and `12-workstreams/**` markdown authoritative for functional agents, workstream definitions, instance/retention semantics, attention, dashboard purpose, surface meaning, payloads, graph role, actions, auth, traces, and tests.
- Keep `surface-graph.md` aligned with surface contracts and traceability maps; each graph node has exactly one owner functional agent and explicit reuse.
- Keep `deferred-typed-surfaces.md` honest. First-slice fallbacks are allowed in templates, but app-level cleanup must replace consequential deferred surfaces with full contracts before claiming capability readiness for that scope.
- Keep `55-ui/**` focused on browser realization of those contracts.
- Keep `70-traceability/**` current whenever a surface or action changes.
- Every protected surface action maps to a governed backend capability and qualified governed-tool exposure. If authority or stable ids are unclear during app implementation, ask or queue the blocking question instead of inventing them.
