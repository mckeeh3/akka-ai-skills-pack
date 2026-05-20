# TASK-STARTER-07-001: Refresh starter acceptance and gap baseline

## Goal

Update the starter app migration/acceptance documentation so it reflects the current repository state: the React/Vite frontend is now embedded under `templates/ai-first-saas-starter/frontend/**` and is rendered by the scaffold command.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `templates/ai-first-saas-starter/README.md`
- `templates/ai-first-saas-starter/TEMPLATE-MANIFEST.md`
- `specs/ai-first-saas-starter-app-template/final-acceptance-review.md`
- `specs/ai-first-saas-starter-app-template/migration-completion-summary.md`
- `specs/ai-first-saas-starter-app-template/starter-app-scope-and-acceptance.md`
- `specs/ai-first-saas-starter-app-template/sprints/07-fullstack-gap-closure-sprint.md`

## Work

1. Re-run a lightweight direct scaffold into a temp dir and confirm frontend files are rendered.
2. Update `final-acceptance-review.md` to remove the stale claim that scaffold output lacks `frontend/`.
3. Update `migration-completion-summary.md` if it repeats the same qualification.
4. Add a concise current gap list that distinguishes:
   - already closed: embedded scaffold frontend;
   - still open: durable Akka components, local auth bootstrap, invitation E2E, Resend adapter, integration smoke, concrete admin/governance APIs.
5. Update the pending queue entry for this task.

## Required checks

- `git diff --check`
- direct scaffold command into a temp dir with `--template-dir templates/ai-first-saas-starter`
- verify scaffolded target contains `frontend/package.json`, `frontend/src/main.tsx`, and backend `pom.xml`

## Done criteria

- Acceptance docs no longer contradict the actual embedded frontend template.
- Remaining gaps are explicit and aligned with the fullstack objective.
- Task status is marked `done` in `pending-tasks.md`.
- One git commit is created.

## Suggested commit message

`Refresh starter acceptance gap baseline`
