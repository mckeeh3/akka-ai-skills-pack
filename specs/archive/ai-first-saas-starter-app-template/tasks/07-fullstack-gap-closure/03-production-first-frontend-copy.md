# TASK-STARTER-07-003: Make starter frontend production-first while retaining fixture mode

## Goal

Ensure the canonical scaffolded frontend presents real API/AuthKit mode as the default production path and confines fixture language to explicit `?fixtureWorkstream=1`, tests, or developer docs.

## Required reads

- `templates/ai-first-saas-starter/frontend/README.md`
- `templates/ai-first-saas-starter/frontend/src/main.tsx`
- `templates/ai-first-saas-starter/frontend/src/api/**`
- `templates/ai-first-saas-starter/frontend/src/workstream/**`
- `templates/ai-first-saas-starter/frontend/src/screens/**`
- `skills/akka-web-ui-apps/SKILL.md`
- `skills/akka-web-ui-testing/SKILL.md`

## Work

1. Audit user-visible strings containing `fixture`, `placeholder`, or stale reference-language.
2. Keep fixture clients and fixtures for deterministic tests/dev inspection.
3. Change canonical app copy and README to emphasize:
   - real API/AuthKit mode by default;
   - fixture mode only when explicitly requested;
   - backend authorization remains authoritative.
4. If legacy screens remain imported nowhere, either quarantine copy more clearly or remove non-canonical references when safe.
5. Update tests that intentionally assert fixture markers so they check explicit fixture mode rather than production copy.
6. Update the pending queue entry for this task.

## Required checks

- `git diff --check`
- `cd templates/ai-first-saas-starter/frontend && npm test -- --run && npm run typecheck && npm run build`

## Done criteria

- Production/default frontend copy no longer looks fixture-backed.
- Fixture mode remains available and tested as explicit dev/test behavior.
- Task status is marked `done` in `pending-tasks.md`.
- One git commit is created.

## Suggested commit message

`Make starter frontend production first`
