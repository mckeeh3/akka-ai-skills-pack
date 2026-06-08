# TASK-STARTER-06-001: Apply legacy asset quarantine and canonical routing cleanup

## Purpose

Remove stale DCA/static seed assets from canonical guidance after the starter app supersedes their role.

## Required reads

- `specs/ai-first-saas-starter-app-template/legacy-and-reusable-asset-inventory.md`
- `docs/ai-first-examples-and-tests-gap-list.md`
- `docs/web-ui-pattern-selection.md`
- `docs/workstream-ui-reference-architecture.md`
- `skills/README.md`
- `skills/akka-web-ui-apps/SKILL.md`

## Expected outputs

- Docs/skills no longer promote stale DCA/static seed code as canonical full-core implementation guidance.
- Legacy assets are marked mechanics-only, archived, or removed according to inventory.

## Done criteria

- The starter app is the clear canonical end-to-end implementation reference.
- Cleanup does not remove still-needed mechanics without replacement coverage.
- Required checks pass, queue status is updated, and changes are committed.
