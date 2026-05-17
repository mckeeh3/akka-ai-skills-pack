# Sprint 3: Capability-First Contracts

## Sprint goal

Refactor the DCA capability layer from a product capability list into current governed backend capability contracts.

## Scope

- Update `10-capabilities/capabilities-index.md` with capability ids, classes, actors/callers, protected scopes, and exposure surfaces.
- Add a detailed governed capability contract for Supplies Autopilot as the first vertical slice.
- Add lightweight/current contracts or index expansions for planned lifecycle, telemetry, service, billing, onboarding, offboarding, policy, command-center, and audit/outcome capabilities.
- Update traceability maps to include secure foundation and capability-first links.

## Expected outputs

- Current capability index.
- Detailed `03-supplies-autopilot.md` capability contract.
- Lightweight capability coverage for the remaining DCA domain.
- Traceability updates from capabilities to behavior, tests, auth/security, UI, observability, and generation slices.

## Acceptance behavior

A future agent should be able to start from a DCA capability and understand authority, scope, data access, side effects, idempotency, approvals, audit/trace, exposure surfaces, and tests before choosing Akka components.

## Defer list

- Do not make every planned capability generation-ready if external contracts and thresholds remain unknown.
- Do not expose capabilities as agent tools by default.
- Do not rewrite behavior flows unless needed to preserve capability consistency.
