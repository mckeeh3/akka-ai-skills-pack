# TASK-STARTER-07-002: Add scaffolded fullstack smoke validation

## Goal

Add a repeatable validation path that proves the starter can scaffold, test backend code, install/typecheck/build the frontend, generate Akka static resources, and preserve scaffold hygiene.

## Required reads

- `tools/scaffold-ai-first-saas-starter.sh`
- `templates/ai-first-saas-starter/README.md`
- `templates/ai-first-saas-starter/frontend/package.json`
- `templates/ai-first-saas-starter/backend/pom.xml`
- `specs/ai-first-saas-starter-app-template/final-acceptance-review.md`
- `specs/ai-first-saas-starter-app-template/backlog/07-fullstack-gap-closure-build-backlog.md`

## Work

1. Add a script such as `tools/validate-ai-first-saas-starter-fullstack.sh`.
2. Script behavior should:
   - create a temp target;
   - scaffold from `templates/ai-first-saas-starter`;
   - verify rendered backend and frontend paths;
   - run `mvn test` in the scaffolded root;
   - run `npm install`, `npm test -- --run`, `npm run typecheck`, and `npm run build` in scaffolded `frontend/`;
   - verify static resources exist under `src/main/resources/static-resources/`;
   - run a simple no-secret scan over built static assets.
3. Document the script in `templates/ai-first-saas-starter/README.md` or the starter specs.
4. Update the pending queue entry for this task.

## Required checks

- `git diff --check`
- run the new validation script successfully, or document environmental failure precisely in task notes

## Done criteria

- A future harness can run one command to validate scaffolded backend + frontend build behavior.
- Script output is clear enough for release/acceptance use.
- Task status is marked `done` in `pending-tasks.md`.
- One git commit is created.

## Suggested commit message

`Add starter fullstack smoke validation`
