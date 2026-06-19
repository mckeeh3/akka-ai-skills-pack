# App Description: Current-Intent Graph

This directory is the authoritative file-backed current-intent graph for the secure multi-tenant AI-first SaaS core starter.

It follows the intent-compiler graph shape:

- `app.md` for app-level objective, operating model, tenant/customer assumptions, and non-goals.
- `global/**` for reusable actors, roles, policies, surfaces, UI style/runtime contracts, agents, tools, and trace patterns.
- `domains/core-starter/**` for core starter capabilities, data/state responsibilities, and five workstream bindings.
- `deferred-scope.md` for explicitly excluded behavior that must not be inferred from archived specs, fixtures, or compatibility names.

Active app-description content describes current intent only. Historical migration material belongs under `specs/archive/app-description-intent-compiler-migration/**` and is archived evidence only, not product authority.
