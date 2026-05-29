# Task: Fix Audit/Trace static secret-marker validation

## Objective

Repair the Audit/Trace v0 validation gap found by terminal verification: `tools/validate-ai-first-saas-starter-fullstack.sh` fails during the built-static secret scan because a frontend fixture/static asset includes an explicit backend secret environment-variable marker.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/audit-trace-workstream-v0/pending-tasks.md`
- `specs/audit-trace-workstream-v0/workstream-contract.md`
- `specs/audit-trace-workstream-v0/capability-inventory.md`
- `tools/validate-ai-first-saas-starter-fullstack.sh`
- `templates/ai-first-saas-starter/frontend/src/workstream/fixtures/surfaces.ts`
- `frontend/src/workstream/fixtures/surfaces.ts`
- `templates/ai-first-saas-starter/src/main/resources/static-resources/index.html`

## Skills

- none; focused validation repair task

## In scope

- Remove or generalize explicit backend secret marker strings from user-facing frontend fixture/static data while preserving redaction semantics.
- Ensure the template build/validation path does not leave stale built static assets that can fail the secret scan.
- Update the root frontend mirror when the template frontend source changes.
- Keep the change limited to Audit/Trace/front-end validation hygiene and static-resource validation.

## Out of scope

- Do not change backend provider configuration semantics.
- Do not weaken the secret scan in `tools/validate-ai-first-saas-starter-fullstack.sh` unless the scan is demonstrably incorrect and remains at least as strict for real backend secret markers.
- Do not implement new Audit/Trace capabilities or UI surfaces.

## Required checks

- `tools/validate-ai-first-saas-starter-fullstack.sh`
- `git diff --check`

## Done criteria

- Fullstack starter validation passes, including built static secret scan.
- Frontend redaction fixtures still demonstrate omitted/secret field redaction without exposing backend secret environment-variable names in built assets.
- Task changes and queue update are committed.
