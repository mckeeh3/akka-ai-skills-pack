# Task Brief: Add Business Boundary Docs and Checks

## Objective

Document and lightly enforce the `business.<area>` package convention for user-owned business-specific domains.

## Required reads

- `specs/java-foundation-coreapp-business-partition/classification-and-package-map.md`
- `specs/java-foundation-coreapp-business-partition/sprints/04-business-boundaries-sprint.md`
- `AGENTS.md`
- root README/domain extension docs

## In scope

- Add or update package-info files documenting `api/application/domain.business.<area>` convention if useful.
- Add architecture docs or lightweight tests/search checks for dependency boundaries.
- Update root guidance for CRM/ERP/domain-specific package placement.

## Out of scope

- Implementing actual CRM/ERP/business-domain classes.
- Adding heavyweight architecture tooling unless already available and cheap.

## Expected outputs

- Boundary documentation and optional package-info/check files.

## Required checks

- `git diff --check`
- `mvn test`
- boundary search/check command if added

## Done criteria

- Users can identify where business-specific Java code belongs.
- Foundation dependency boundary is documented and checked where practical.
- Queue is updated/committed.
