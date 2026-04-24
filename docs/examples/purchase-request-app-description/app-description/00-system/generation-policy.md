# Generation Policy

- default-description-mode: maintain-description-first
- generation-allowed-when:
  - readiness-state is `ready`
  - or readiness-state is `ready-with-assumptions` and assumptions are acceptable for requested evaluation
- default-regeneration-preference:
  - localized when change impact is clear
  - full when semantics changed broadly or impact is unclear
- required-post-generation-validation:
  - run generated tests
  - run the app for manual evaluation when requested
  - surface any semantic gaps back into description maintenance
