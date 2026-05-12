# TASK-SWPR-01-003 — Remove static-page executable examples and tests

## Objective

Remove executable source, resources, and tests whose primary purpose is demonstrating hand-authored static page serving.

## Required reads

- `AGENTS.md`
- `specs/static-web-page-content-removal/README.md`
- `specs/static-web-page-content-removal/sprints/01-remove-static-web-page-guidance-sprint.md`
- `specs/static-web-page-content-removal/backlog/01-remove-static-web-page-guidance-build-backlog.md`
- `specs/static-web-page-content-removal/pending-tasks.md`
- `src/main/java/com/example/api/StaticContentEndpoint.java`
- `src/test/java/com/example/application/StaticContentEndpointIntegrationTest.java`
- `src/main/resources/static-resources/http-endpoint/index.html`
- `src/main/resources/static-resources/http-endpoint/app.css`
- `src/main/resources/static-resources/http-endpoint/help.txt`
- `src/main/resources/static-resources/http-endpoint/guide/index.html`
- `src/main/resources/static-resources/http-endpoint/openapi.yaml`

## Steps

1. Update this task status to `in-progress` in `specs/static-web-page-content-removal/pending-tasks.md`.
2. Delete `src/main/java/com/example/api/StaticContentEndpoint.java`.
3. Delete `src/test/java/com/example/application/StaticContentEndpointIntegrationTest.java`.
4. Delete `src/main/resources/static-resources/http-endpoint/**` and remove empty directories.
5. Search for source/test references to the removed endpoint or resource path and remove/revise them.
6. Do not remove React/Vite/TypeScript frontend project guidance or generated build-output hosting examples.
7. Run required checks.
8. Update this task status to `done` with a completion note.
9. Commit only this task's changes and queue update.

## Required checks

```bash
rg -n "StaticContentEndpoint|http-endpoint/index.html|http-endpoint/app.css|http-endpoint/openapi.yaml|static-content" src test skills docs pack --glob '!specs/static-web-page-content-removal/**'
find src/main/resources/static-resources/http-endpoint -type f 2>/dev/null || true
mvn test -DskipITs=false
git status --short
```

Expected: no current implementation/example references to the removed static-page endpoint; Maven tests pass or any non-repo/environment failure is documented in the task notes.

## Done criteria

- Static-page endpoint source removed.
- Static-page integration test removed.
- Static-page packaged resources removed.
- Residual references repaired.
- Queue status updated.
- Git commit created.

## Commit message

```text
Remove static page executable examples
```
