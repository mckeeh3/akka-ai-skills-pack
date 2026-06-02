# Generation Policy

- generation allowed when:
  - the requested slice has defined capabilities, behavior, tests, security, UI contracts, and component mapping
  - auth and tenant-boundary behavior is explicit for all generated APIs
- default regeneration preference:
  - localized regeneration by module after the initial full seed generation
  - full regeneration acceptable for early seed bootstrap
- assumption policy:
  - assumptions must be recorded in readiness and review summaries
  - security assumptions must not weaken tenant isolation or authorization checks
- required validation after generation:
  - backend unit/component tests
  - endpoint authorization tests
  - frontend build/typecheck
  - frontend interaction tests for core shell flows where feasible
  - tenant isolation regression tests
