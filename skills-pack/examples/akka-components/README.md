# Focused Akka Component Examples

This directory contains `ai.first` reference examples for installable skills-pack guidance.

These examples are not the runnable core app and are not a standalone build module. They are focused source fixtures read by skills and tests in downstream/generated projects. The repository root app uses the fixed `ai.first` package; keep focused Akka component fixtures here so root `src/` remains the canonical core application source.

Key paths:

- `src/main/java/ai/first/**` — component, endpoint, agent, workflow, entity, view, consumer, timed-action, MCP, and gRPC reference implementations
- `src/test/java/ai/first/**` — focused tests and integration tests for the reference examples
- `src/main/proto/ai/first/**` — gRPC example proto definitions

Static browser UI fixtures were removed from this examples tree. In a source checkout, use the root app `frontend/src/workstream/**` reference and `skills-pack/docs/workstream-ui-reference-architecture.md` for generated SaaS UI structure. From an installed skills library, use `.agents/skills/docs/workstream-ui-reference-architecture.md`; root frontend application source is not installed. Use focused HTTP/SSE/WebSocket endpoint examples only for backend delivery mechanics.
