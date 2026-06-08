# Retired content boundaries

Use this note to keep normal skills concise. Do not repeat long retired-content warnings in every skill; point here when a task risks reintroducing old structure.

## Do not restore as generated-app guidance

- standalone static UI fixture pages or static-resource UI examples as canonical SaaS UI structure;
- page-first or `frontend/src/screens/**` taxonomies as the primary workstream model;
- retired distribution output directories as template sources;
- historical domain-specific planning examples as generic placeholders;
- retired workstream-event gRPC/MCP fixture class names as current repository examples;
- copied pack examples as target application source or a second app baseline.

## Current replacements

- UI architecture: `./workstream-ui-reference-architecture.md` and target/root `frontend/src/workstream/**`.
- Surface contracts/templates: `templates/ai-first-saas-core-app/app-description/**`.
- Generated-app doctrine: `./generated-saas-canonical-doctrine.md`.
- Akka mechanics: focused Akka skills plus external top-level `akka-context/**` docs when present.
- Examples: curated `examples/akka-components/**` snippets for pattern lookup only; adapt behavior into the target project's root app workspace.

## Maintainer rule

If retired content is needed for archaeology, keep it under maintainer-only notes or source history. Installed runtime guidance should describe current structure first and mention retired content only to prevent a concrete regression.
