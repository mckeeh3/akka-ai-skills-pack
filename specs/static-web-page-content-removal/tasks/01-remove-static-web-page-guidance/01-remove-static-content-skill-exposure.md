# TASK-SWPR-01-001 — Remove static-content skill exposure

## Objective

Remove the dedicated static-content skill and all current routing/packaging references that expose static web page/file-serving as a supported pack path.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/static-web-page-content-removal/README.md`
- `specs/static-web-page-content-removal/sprints/01-remove-static-web-page-guidance-sprint.md`
- `specs/static-web-page-content-removal/backlog/01-remove-static-web-page-guidance-build-backlog.md`
- `specs/static-web-page-content-removal/pending-tasks.md`
- `skills/akka-http-endpoint-static-content/SKILL.md`
- `pack/manifest.yaml`
- `skills/akka-http-endpoints/SKILL.md`
- `skills/akka-http-endpoint-web-ui/SKILL.md`

## Steps

1. Update this task status to `in-progress` in `specs/static-web-page-content-removal/pending-tasks.md`.
2. Delete `skills/akka-http-endpoint-static-content/SKILL.md` and remove the directory if empty.
3. Remove `akka-http-endpoint-static-content` entries from `pack/manifest.yaml`.
4. Revise `skills/README.md` so it no longer lists static content/static pages as a Stage 3 route.
5. Revise any direct endpoint-routing references that point to the removed skill, especially in `skills/akka-http-endpoints/SKILL.md` and `skills/akka-http-endpoint-web-ui/SKILL.md`.
6. Ensure wording points browser UI work toward React/Vite/TypeScript app guidance or generated frontend build-output hosting only.
7. Run required checks.
8. Update this task status to `done` with a completion note.
9. Commit only this task's changes and queue update.

## Required checks

```bash
rg -n "akka-http-endpoint-static-content" skills pack docs --glob '!specs/static-web-page-content-removal/**'
git status --short
```

Expected: no current references to the removed skill outside this planning package or explicit archived/historical context.

## Done criteria

- Removed static-content skill directory.
- Removed manifest references to the skill.
- Routing docs no longer point to the removed skill.
- Queue status updated.
- Git commit created.

## Commit message

```text
Remove static content skill exposure
```
