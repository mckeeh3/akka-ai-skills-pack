# Generation Policy

- default-description-mode: maintain-description-first
- generation-allowed-when:
  - readiness-state is `ready`
  - or readiness-state is `ready-with-assumptions` for a named narrowed scope and remaining assumptions are non-runtime or explicitly outside the completion claim
- default-regeneration-preference:
  - localized when change impact is clear
  - full when semantics changed broadly or impact is unclear
- required-post-generation-validation:
  - run generated tests
  - run the local Akka/API/UI path needed to prove the generated scope works before calling runtime features complete
  - do not accept mock, fixture, simulated, frontend-only, or provider-bypass behavior as normal runtime completion
  - surface any semantic gaps back into description maintenance
