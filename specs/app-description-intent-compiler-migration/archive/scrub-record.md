# Legacy Archive Scrub Record

- task: `TASK-ADICM-04-001`
- result: removed the temporary copied legacy app-description tree at `specs/app-description-intent-compiler-migration/archive/legacy-app-description/`
- authority impact: none; the copied tree was migration provenance only and never product current intent

## Current authority after scrub

Use the reconstructed current-intent graph instead:

- `app-description/app.md`
- `app-description/global/**`
- `app-description/domains/core-starter/**`

Use active readiness/spec evidence under `specs/full-core-saas-readiness/**` and related active specs for validation status. Use git history for historical recovery of the removed migration copy if necessary.

## Scrub scope

The scrub is docs-only. It does not change runtime behavior, API contracts, frontend behavior, tenant/customer scoping, backend authorization, governed agent boundaries, provider fail-closed handling, or audit/work trace implementation.
