# Sprint 01: Workstream Contract Cleanup

## Objective

Tighten the skills-pack workstream contract so doctrine, manifest schema, validators, templates, and installed-layout reference checks agree with the accepted decisions.

## Scope

- Manifest required fields and validator behavior.
- Attention category/severity semantics.
- Surface action/governed-tool mappings.
- Runtime readiness evidence.
- Structured internal worker entries.
- Installed-layout link/reference validation.
- Focused consistency sweep and verification.

## Ordered work areas

1. Align existing manifest contract fields and enums.
2. Add implementation-readiness mapping/evidence structures.
3. Preserve and validate installed-layout references.
4. Verify all decisions and append follow-up tasks if needed.

## Acceptance criteria

- Workstream docs identify the semantic source of truth and machine-readable source of truth.
- JSON schema and validator enforce the required fields and conditional requirements decided in `conversation-capture.md`.
- Templates/examples affected by the schema pass validation or are intentionally marked at a lower readiness level.
- Installed-layout references remain unchanged where correct and are checked after install.
- Required pack checks pass or blockers are recorded in the queue.

## Handoff notes

This sprint is docs/tooling/template maintenance only. Do not implement root app runtime features. Use the installed-layout convention for skill references.
