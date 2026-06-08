# Sprint 3: Component Skill Reframing

## Sprint goal

Reframe focused Akka implementation skills so components are selected and implemented as carriers of governed capabilities and selected exposure surfaces.

## Scope

- Update agent tool and component-tool skills around capability-first design.
- Update entity skills so command/query methods are capability surfaces, not raw CRUD operations by default.
- Update workflow skills so long-running/consequential capabilities use supervision, approval, retries, compensation, and audit.
- Update view skills so read capabilities expose curated evidence/read models rather than leaking internal state by default.
- Update endpoint and MCP skills so external exposure is selective, scoped, authorized, auditable, and aligned with capability definitions.
- Update consumer/timed-action guidance for event-driven and scheduled capability execution.

## Expected outputs

- Revised Stage 3 implementation skills.
- Cross-links to the canonical capability-first doctrine.
- Review checklist additions for capability authorization, scope, audit, approval, idempotency, and exposure decisions.

## Acceptance behavior

When implementing a component, a future agent should ask: which capability does this implement or expose, what authority does it require, what side effects does it have, and which surfaces should use it?

## Defer list

- Large new executable examples unless required to validate revised guidance.
