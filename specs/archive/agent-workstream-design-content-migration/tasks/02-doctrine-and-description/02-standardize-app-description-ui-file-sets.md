# Task Brief: Standardize App-Description UI File Sets

## Task

Standardize app-description architecture, bootstrap, and UI skill guidance around a single generated SaaS `55-ui/` structure and a clear `12-workstreams/` vs `55-ui/` ownership boundary.

## Expected outputs

- targeted edits to:
  - `docs/internal-app-description-architecture.md`
  - `skills/app-description-bootstrap/SKILL.md`
  - `skills/app-description-ui/SKILL.md`
  - related description-first guidance if directly affected
- queue status update and git commit

## Rules

- `12-workstreams/` owns functional agents, internal agents, durable workstreams, surfaces index, and surface contracts.
- `55-ui/` owns shell rendering, rail behavior, panel/composer behavior, structured-surface rendering, routes/deep links, frontend API contracts, state/realtime, accessibility/responsive behavior, and style.
- Managed-agent foundation UI files are mandatory for full core scope, but narrower explicit scopes may defer them.

## Completion

Mark `TASK-AWDD-02-002` done after commit.
