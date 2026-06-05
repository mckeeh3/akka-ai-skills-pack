# Focused Akka Component Examples

This directory contains `com.example` reference examples for installable skills-pack guidance.

These examples are not the runnable core app and are not a standalone build module. They are focused source fixtures read by skills and tests in downstream/generated projects. The repository root app uses the fixed `ai.first` package; keep focused Akka component fixtures here so root `src/` remains the canonical core application source.

Key paths:

- `src/main/java/com/example/**` — component, endpoint, agent, workflow, entity, view, consumer, timed-action, MCP, and gRPC reference implementations
- `src/test/java/com/example/**` — focused tests and integration tests for the reference examples
- `src/main/proto/com/example/**` — gRPC example proto definitions

Static browser UI fixtures were removed from this examples tree. Use the root app `frontend/src/workstream/**` reference and `skills-pack/docs/workstream-ui-reference-architecture.md` for generated SaaS UI structure; use focused HTTP/SSE/WebSocket endpoint examples only for backend delivery mechanics.
