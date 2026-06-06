# SaaS Foundation App description template

This template is source-controlled SaaS Foundation App description guidance for workstream surface modeling in generated secure AI-first SaaS apps. Copy the smallest relevant files into a target project's `app-description/` tree when bootstrapping or repairing surface contracts.

The template is intentionally app-description-only. It is not a runnable app baseline and does not replace the upstream SaaS Foundation App implementation.

Use with companion pack guidance. From an installed skills directory, resolve these paths under `.agents/skills/`; from a source checkout, resolve them under `skills-pack/`:

- `docs/agent-workstream-application-architecture.md`
- `docs/requirements-to-workstream-development-process.md`
- `docs/structured-surface-contracts.md`
- `app-description-surface-modeling/SKILL.md`

## Included SaaS Foundation App layers

```text
12-workstreams/
  surfaces-index.md
  surface-contracts/*.md
55-ui/
  structured-surface-rendering.md
70-traceability/
  surface-to-capability-map.md
```

## Rules

- Keep `12-workstreams/**` authoritative for surface meaning, payloads, graph role, actions, auth, traces, and tests.
- Keep `55-ui/**` focused on browser realization of those contracts.
- Keep `70-traceability/**` current whenever a surface or action changes.
- Every protected surface action maps to a governed backend capability and qualified governed-tool exposure.
