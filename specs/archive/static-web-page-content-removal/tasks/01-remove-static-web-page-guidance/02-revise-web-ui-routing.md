# TASK-SWPR-01-002 — Revise web UI routing away from static page patterns

## Objective

Revise web UI skills and selection docs so the current supported browser UI implementation path is React/Vite/TypeScript frontend project delivery, not hand-authored static pages or simple packaged HTML/CSS.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/static-web-page-content-removal/README.md`
- `specs/static-web-page-content-removal/sprints/01-remove-static-web-page-guidance-sprint.md`
- `specs/static-web-page-content-removal/backlog/01-remove-static-web-page-guidance-build-backlog.md`
- `specs/static-web-page-content-removal/pending-tasks.md`
- `skills/akka-http-endpoint-web-ui/SKILL.md`
- `skills/akka-web-ui-apps/SKILL.md`
- `skills/akka-web-ui-frontend-project/SKILL.md`
- `docs/web-ui-pattern-selection.md`
- `docs/web-ui-frontend-project-integration.md`

## Steps

1. Update this task status to `in-progress` in `specs/static-web-page-content-removal/pending-tasks.md`.
2. In `skills/akka-http-endpoint-web-ui/SKILL.md`, remove static-content-only pattern selection and references to hand-authored packaged pages as an implementation path.
3. In `skills/akka-web-ui-apps/SKILL.md`, revise any wording that contrasts full apps with static pages so it does not route agents to static-page implementation.
4. In `skills/akka-web-ui-frontend-project/SKILL.md`, keep generated build-output hosting guidance, but remove references that tell agents to use static-content guidance for non-app pages.
5. In `docs/web-ui-pattern-selection.md`, remove the static-page/file-serving row and revise route guidance around React/Vite/TypeScript build output.
6. Search for related current guidance and revise it when it still recommends static page implementation.
7. Run required checks.
8. Update this task status to `done` with a completion note.
9. Commit only this task's changes and queue update.

## Required checks

```bash
rg -n "static content only|simple file serving|packaged docs|static page|static pages|hand-authored static|non-interactive HTML" skills docs --glob '!specs/static-web-page-content-removal/**'
rg -n "React|Vite|TypeScript|frontend project" skills/akka-http-endpoint-web-ui/SKILL.md skills/akka-web-ui-apps/SKILL.md skills/akka-web-ui-frontend-project/SKILL.md docs/web-ui-pattern-selection.md
git status --short
```

Expected: no current guidance that recommends implementing static pages; retained static-resource references must be about generated frontend build assets.

## Done criteria

- Web UI routing no longer presents static pages as a supported implementation path.
- React/Vite/TypeScript app delivery remains clear.
- Generated build-output hosting remains documented.
- Queue status updated.
- Git commit created.

## Commit message

```text
Revise web UI routing away from static pages
```
