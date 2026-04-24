# Change Impact Map

- if capability scope changes:
  - update `10-capabilities/`
  - update linked behavior and acceptance tests
  - reassess readiness
- if behavior lifecycle changes:
  - update `20-behavior/`
  - update regression and negative tests
  - reassess auth/security and observability implications
- if auth/security changes:
  - update `40-auth-security/`
  - update negative and operational verification
  - reevaluate generation locality
- if observability changes:
  - update `50-observability/`
  - update operational verification
  - reevaluate generation locality
