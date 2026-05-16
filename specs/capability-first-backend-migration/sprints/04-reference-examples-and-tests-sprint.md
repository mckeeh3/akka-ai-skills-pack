# Sprint 4: Reference Examples and Tests

## Sprint goal

Create or revise executable examples and tests that demonstrate capability-first backend architecture for agentic systems.

## Scope

Potential reference slices:

1. Read-only capability exposed as an agent component tool.
2. Consequential capability that returns a proposal or approval request instead of autonomously mutating state.
3. Workflow-backed capability for supervised long-running work.
4. View-backed capability for curated evidence retrieval.
5. MCP-exposed capability for remote agents or services.
6. Browser UI action and agent tool reusing the same capability semantics.

## Expected outputs

- Minimal examples under `src/main/java` and tests under `src/test/java`, only where they add agent-useful guidance.
- Updated docs/skills that point to canonical examples.
- Tests for success, denial, tenant/customer scope where applicable, audit/trace behavior, and model/tool invocation behavior.

## Acceptance behavior

A future agent should be able to copy a small pattern for capability-first implementation without accidentally exposing raw state, skipping authorization, or treating prompt instructions as controls.

## Defer list

- Full generated SaaS application implementation.
