# TASK-WGGT-05-001: Update installed-pack and starter-template graph guidance

## Objective

Propagate the canonical workstream graph and governed-tool vocabulary into installed-pack entry guidance and starter template artifacts so downstream harnesses see the model even before opening deeper docs or skills.

## Required reads

- AGENTS.md
- skills/README.md
- specs/workstream-graph-governed-tools-architecture/README.md
- specs/workstream-graph-governed-tools-architecture/final-verification.md
- pack/AGENTS.md
- pack/README.md
- pack/manifest.yaml
- templates/ai-first-saas-starter/README.md
- relevant starter app-description/spec/seed files discovered by focused search

## In scope

- Update `pack/AGENTS.md` to name the canonical sequence: affected workstreams, role-specific dashboard surfaces, attention items, human surface graph nodes/edges, internal workstream agent graph, workstream expertise, governed-tools, and qualified browser-tool/agent-tool/internal-tool exposures.
- Update `pack/README.md` and/or `pack/manifest.yaml` if packaged reference listings omit docs needed by the installed graph/governed-tool model.
- Update starter template README and the smallest relevant starter app-description/spec/seed files so the five-core starter surfaces and actions are described as role-specific dashboard/surface-graph/governed-tool structures, without overstating starter scope.
- Preserve the runtime-completion doctrine and governed managed-agent runtime requirements.

## Out of scope

- Do not rebuild the starter implementation.
- Do not create a new top-level governed-tools app-description directory.
- Do not rewrite broad active docs/skills already verified by `TASK-WGGT-99-001` unless a direct packaging/template reference requires it.

## Checks

- `git diff --check`
- Focused search over `pack/` and `templates/ai-first-saas-starter/` for `surface graph`, `role-specific dashboard`, `internal workstream agent graph`, `workstream expertise`, `governed-tool`, `browser-tool`, `agent-tool`, and `internal-tool`.
- Focused search for ambiguous bare architecture-level `tool` wording in edited files.

## Done criteria

- Installed-pack entry guidance and starter template artifacts directly expose the graph/governed-tool model.
- Pack/template searches no longer show zero coverage for the canonical terms where relevant.
- Task changes and queue update are committed.
