# TASK-STARTER-07-010: Rerun final fullstack acceptance and publish updated summary

## Goal

Run final acceptance against the improved starter and publish an accurate completion summary with any remaining qualified gaps.

## Required reads

- `specs/ai-first-saas-starter-app-template/README.md`
- `specs/ai-first-saas-starter-app-template/starter-app-scope-and-acceptance.md`
- `specs/ai-first-saas-starter-app-template/final-acceptance-review.md`
- `specs/ai-first-saas-starter-app-template/migration-completion-summary.md`
- `specs/ai-first-saas-starter-app-template/sprints/07-fullstack-gap-closure-sprint.md`
- `tools/validate-ai-first-saas-starter-fullstack.sh` if present
- `tools/build-pack.sh`
- `install.sh`

## Work

1. Run direct template scaffold validation.
2. Run installed-pack scaffold validation.
3. Run backend tests, frontend tests/typecheck/build, static asset checks, and build-pack validation.
4. Update `final-acceptance-review.md` with current evidence.
5. Update `migration-completion-summary.md` with what changed in Sprint 07.
6. Record any still-open gaps precisely and route them into a follow-up backlog if needed.
7. Update the pending queue entry.

## Required checks

- `git diff --check`
- fullstack starter validation script if present
- direct scaffold + `mvn test`
- scaffolded frontend `npm install && npm test -- --run && npm run typecheck && npm run build`
- installed pack scaffold validation
- `bash tools/build-pack.sh --output-dir "$TMP" --clean --no-archive --github-repo example/akka-ai-skills-pack`

## Done criteria

- Final acceptance reflects actual starter behavior and no stale frontend qualification remains.
- Evidence is sufficient for a downstream user to trust the scaffold as the current canonical fullstack starter baseline.
- Task status is marked `done` in `pending-tasks.md`.
- One git commit is created.

## Suggested commit message

`Publish starter fullstack acceptance rerun`
