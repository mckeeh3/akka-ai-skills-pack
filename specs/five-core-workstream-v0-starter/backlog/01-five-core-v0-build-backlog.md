# Backlog: Five Core Workstream v0 Starter

## Build slices

1. Doctrine and routing alignment for five core v0 workstreams.
2. README/getting-started prompt updates.
3. Template app-description/spec guidance updates.
4. Frontend `markdown_response` structured surface renderer.
5. Backend workstream message endpoint and response contract.
6. Five core functional-agent v0 prompt/skill/model seed alignment.
7. Frontend composer-to-backend integration.
8. Fixture, contract test, scaffold validation, and final handoff update.

## Acceptance summary

A freshly scaffolded app can be configured, run locally, and tested manually as follows:

1. Bootstrap user signs in.
2. Left rail shows the five core workstreams as visible authorized workstreams for the bootstrap admin context.
3. Selecting any workstream opens a v0 text-first timeline.
4. The composer submits to the backend for the selected workstream.
5. The backend authorizes the selected context and functional agent, resolves that workstream's v0 prompt/skill/model configuration, and returns a `markdown_response` surface.
6. The frontend renders sanitized markdown as HTML and appends durable/trace-linked request-response items.
7. Tests cover allowed and denied access, surface rendering/sanitization, frontend secret boundary, and scaffold validation.
