# TASK-SWPR-01-004 — Revise or remove static-web-page docs

## Objective

Remove obsolete docs that explain static web page implementation, or revise retained docs so they support only React/Vite/TypeScript app delivery and generated frontend build-output hosting.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/static-web-page-content-removal/README.md`
- `specs/static-web-page-content-removal/sprints/01-remove-static-web-page-guidance-sprint.md`
- `specs/static-web-page-content-removal/backlog/01-remove-static-web-page-guidance-build-backlog.md`
- `specs/static-web-page-content-removal/pending-tasks.md`
- `docs/web-ui-static-content.md`
- `docs/web-ui-static-content-checklist.md`
- `docs/web-ui-pattern-selection.md`
- `docs/next-steps.md`
- `docs/skills-pack-tech-stack.md`
- `docs/web-ui-frontend-decomposition.md`
- `docs/web-ui-frontend-project-integration.md`

## Steps

1. Update this task status to `in-progress` in `specs/static-web-page-content-removal/pending-tasks.md`.
2. Delete `docs/web-ui-static-content.md` if it is purely historical static-page implementation planning.
3. Delete `docs/web-ui-static-content-checklist.md` if it is purely historical static-page implementation checklist material.
4. Revise retained docs so they no longer refer to static web page implementation as a current pack path.
5. Preserve concise React/Vite/TypeScript frontend project decomposition and build-output hosting guidance.
6. Search docs for static-page terminology and repair current guidance.
7. Run required checks.
8. Update this task status to `done` with a completion note.
9. Commit only this task's changes and queue update.

## Required checks

```bash
rg -n "web-ui/static content|static-content foundation|static content example|packaged static assets|OpenAPI publication|simple file serving|static docs|static page|static pages" docs skills --glob '!specs/static-web-page-content-removal/**'
rg -n "React|Vite|TypeScript|frontend project|static-resources" docs/web-ui-frontend-decomposition.md docs/web-ui-frontend-project-integration.md docs/skills-pack-tech-stack.md
git status --short
```

Expected: no current docs present static-page implementation as supported guidance; retained `static-resources` mentions are for frontend build output.

## Done criteria

- Obsolete static web page docs removed or revised.
- Current docs emphasize React/Vite/TypeScript app delivery.
- Generated build-output hosting remains clear.
- Queue status updated.
- Git commit created.

## Commit message

```text
Remove static page documentation guidance
```
