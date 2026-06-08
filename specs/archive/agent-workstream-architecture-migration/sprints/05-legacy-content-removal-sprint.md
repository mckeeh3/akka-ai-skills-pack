# Sprint 5: Legacy Content Removal

## Goal

Remove or revise content that conflicts with agent workstream architecture.

## Scope

- Search skills, docs, examples, manifests, and planning docs for page-first, CRUD-first, traditional admin-console, static dashboard, and chatbot-bolt-on defaults.
- Remove obsolete skills/docs where the concept should no longer exist.
- Revise retained content so it frames pages/routes as implementation details or deep links, not the product architecture.
- Update manifests and routing after removals.
- Add supersession notes only where deleting provenance would harm migration traceability.

## Out of scope

- Removing archived provenance under migration archive directories unless a task explicitly says to do so.
- Rewriting official `akka-context/` source material.

## Done criteria

- Current installed-pack guidance presents one opinionated generated-app model.
- Alternatives that weaken the model are gone or explicitly reframed as non-default implementation details.
