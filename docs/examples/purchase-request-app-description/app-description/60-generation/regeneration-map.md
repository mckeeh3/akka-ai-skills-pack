# Regeneration Map

- changed-capability-or-flow:
  - likely-affects: write-model code, approval flow code, acceptance and regression tests
- changed-auth-security:
  - likely-affects: endpoint or access surfaces, security tests, data-redaction behavior
- changed-observability:
  - likely-affects: audit output, metrics emission, trace propagation, operational tests
- localized-regeneration-preference:
  - use localized regeneration when only one of the above areas changes and affected outputs are clear
