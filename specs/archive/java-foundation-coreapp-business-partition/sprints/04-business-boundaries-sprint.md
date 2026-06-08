# Sprint 04: Business Extension Seams and Boundary Checks

## Objective

Document and enforce the user-owned business package convention without implementing a real business domain.

## Scope

- Add package-info files or docs for `*.business` extension zones if useful.
- Add lightweight architecture/search checks for disallowed dependencies.
- Update domain-extension guidance to use `business.<area>` packages.

## Acceptance criteria

- Users can see where CRM/ERP/domain-specific Java code belongs.
- Boundary rules are documented and checked where practical.
- Foundation does not depend on coreapp or business.
