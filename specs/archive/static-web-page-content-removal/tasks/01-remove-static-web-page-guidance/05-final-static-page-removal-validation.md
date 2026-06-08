# TASK-SWPR-01-005 — Final static page removal validation

## Objective

Run broad validation after prior tasks, repair residual current-pack references to static web page implementation, and ensure packaging/routing remains React/Vite/TypeScript focused.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/static-web-page-content-removal/README.md`
- `specs/static-web-page-content-removal/sprints/01-remove-static-web-page-guidance-sprint.md`
- `specs/static-web-page-content-removal/backlog/01-remove-static-web-page-guidance-build-backlog.md`
- `specs/static-web-page-content-removal/pending-tasks.md`
- `pack/manifest.yaml`
- any file returned by required searches

## Steps

1. Update this task status to `in-progress` in `specs/static-web-page-content-removal/pending-tasks.md`.
2. Run all required searches.
3. For every match outside this planning package, classify it as:
   - allowed React/Vite/TypeScript generated build-output hosting,
   - archived/historical context,
   - or current static-page implementation guidance that must be removed/revised.
4. Remove/revise every current static-page implementation guidance match.
5. Verify `pack/manifest.yaml` has no removed static-content skill entry.
6. Verify `skills/README.md` routes browser UI work to React/Vite/TypeScript app skills.
7. Run required checks.
8. Update this task status to `done` with a completion note summarizing remaining allowed matches.
9. Commit only this task's changes and queue update.

## Required checks

```bash
rg -n "akka-http-endpoint-static-content|StaticContentEndpoint|static content only|simple file serving|packaged docs|static web page|static page|static pages|hand-authored static|non-interactive HTML|http-endpoint/index.html|http-endpoint/openapi.yaml" skills docs pack src test --glob '!**/target/**' --glob '!specs/static-web-page-content-removal/**'
rg -n "HttpResponses\.staticResource" skills docs src test --glob '!**/target/**' --glob '!specs/static-web-page-content-removal/**'
rg -n "React|Vite|TypeScript|frontend project" skills/README.md skills/akka-web-ui-apps/SKILL.md skills/akka-web-ui-frontend-project/SKILL.md docs/web-ui-frontend-project-integration.md
mvn test -DskipITs=false
git status --short
```

Allowed residual references:

- `HttpResponses.staticResource(...)` usage that serves generated React/Vite/TypeScript frontend build assets.
- Historical/archive material that is explicitly not current pack guidance.
- This planning package.

## Done criteria

- No current pack guidance promotes implementing static web pages.
- Manifest/routing are consistent.
- Tests pass or any environment-only failure is documented.
- Queue status updated.
- Git commit created.

## Commit message

```text
Validate static page guidance removal
```
