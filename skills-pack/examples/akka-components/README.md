# Focused Akka Component Examples

This directory contains `com.example` reference examples for installable skills-pack guidance.

These examples are not the runnable core app and are not a standalone build module. They are focused source fixtures read by skills and tests in downstream/generated projects. The repository root app uses the fixed `ai.first` package; keep focused Akka component fixtures here so root `src/` remains the canonical core application source.

Key paths:

- `src/main/java/com/example/**` — component, endpoint, agent, workflow, entity, view, consumer, timed-action, MCP, and gRPC reference implementations
- `src/test/java/com/example/**` — focused tests and integration tests for the reference examples
- `src/main/proto/com/example/**` — gRPC example proto definitions
- `src/main/web-ui/**` and `src/main/resources/static-resources/{frontend-reference,web-ui,web-ui-sse,web-ui-websocket}/**` — focused HTTP/static UI delivery references
