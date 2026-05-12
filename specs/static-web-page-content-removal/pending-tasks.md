# Pending Tasks

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Update task status before finishing the harness response.
- Each task must make one git commit before being marked `done`; the commit should include only that task's intended changes and its queue-status update.
- If the queue status update is included in the same commit, record the commit message in task notes instead of attempting to amend the commit hash.
- This queue is for static web page guidance removal, rooted at `specs/static-web-page-content-removal/`.

## Tasks

### TASK-SWPR-01-001: Remove static-content skill exposure

- status: done
- source: specs/static-web-page-content-removal/backlog/01-remove-static-web-page-guidance-build-backlog.md
- task brief: specs/static-web-page-content-removal/tasks/01-remove-static-web-page-guidance/01-remove-static-content-skill-exposure.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/static-web-page-content-removal/README.md
  - specs/static-web-page-content-removal/sprints/01-remove-static-web-page-guidance-sprint.md
  - specs/static-web-page-content-removal/backlog/01-remove-static-web-page-guidance-build-backlog.md
  - specs/static-web-page-content-removal/tasks/01-remove-static-web-page-guidance/01-remove-static-content-skill-exposure.md
  - skills/akka-http-endpoint-static-content/SKILL.md
  - pack/manifest.yaml
  - skills/akka-http-endpoints/SKILL.md
  - skills/akka-http-endpoint-web-ui/SKILL.md
- skills:
  - none; use repository guidance and local docs
- expected outputs:
  - remove skills/akka-http-endpoint-static-content/SKILL.md
  - update pack/manifest.yaml
  - update skills/README.md
  - update HTTP endpoint routing references that point to the removed skill
- required checks:
  - `rg -n "akka-http-endpoint-static-content" skills pack docs --glob '!specs/static-web-page-content-removal/**'`
  - `git status --short`
- done criteria:
  - static-content skill no longer exists or is exposed by manifest/routing
  - browser UI routing no longer points to the removed skill
  - task changes and queue update are committed
- notes:
  - commit message: `Remove static content skill exposure`
  - completed: removed the dedicated static-content skill exposure from manifest, routing, and current references.

### TASK-SWPR-01-002: Revise web UI routing away from static page patterns

- status: done
- source: specs/static-web-page-content-removal/backlog/01-remove-static-web-page-guidance-build-backlog.md
- task brief: specs/static-web-page-content-removal/tasks/01-remove-static-web-page-guidance/02-revise-web-ui-routing.md
- depends on: [TASK-SWPR-01-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/static-web-page-content-removal/README.md
  - specs/static-web-page-content-removal/sprints/01-remove-static-web-page-guidance-sprint.md
  - specs/static-web-page-content-removal/backlog/01-remove-static-web-page-guidance-build-backlog.md
  - specs/static-web-page-content-removal/tasks/01-remove-static-web-page-guidance/02-revise-web-ui-routing.md
  - skills/akka-http-endpoint-web-ui/SKILL.md
  - skills/akka-web-ui-apps/SKILL.md
  - skills/akka-web-ui-frontend-project/SKILL.md
  - docs/web-ui-pattern-selection.md
  - docs/web-ui-frontend-project-integration.md
- skills:
  - akka-web-ui-apps
  - akka-web-ui-frontend-project
- expected outputs:
  - revise skills/akka-http-endpoint-web-ui/SKILL.md
  - revise skills/akka-web-ui-apps/SKILL.md if needed
  - revise skills/akka-web-ui-frontend-project/SKILL.md if needed
  - revise docs/web-ui-pattern-selection.md
- required checks:
  - `rg -n "static content only|simple file serving|packaged docs|static page|static pages|hand-authored static|non-interactive HTML" skills docs --glob '!specs/static-web-page-content-removal/**'`
  - `rg -n "React|Vite|TypeScript|frontend project" skills/akka-http-endpoint-web-ui/SKILL.md skills/akka-web-ui-apps/SKILL.md skills/akka-web-ui-frontend-project/SKILL.md docs/web-ui-pattern-selection.md`
  - `git status --short`
- done criteria:
  - web UI routing points to React/Vite/TypeScript app delivery
  - generated frontend build-output hosting guidance remains intact
  - task changes and queue update are committed
- notes:
  - commit message: `Revise web UI routing away from static pages`
  - completed: routed web UI guidance to React/Vite/TypeScript frontend app delivery and limited static-resource discussion to generated build-output hosting.

### TASK-SWPR-01-003: Remove static-page executable examples and tests

- status: done
- source: specs/static-web-page-content-removal/backlog/01-remove-static-web-page-guidance-build-backlog.md
- task brief: specs/static-web-page-content-removal/tasks/01-remove-static-web-page-guidance/03-remove-static-page-examples.md
- depends on: [TASK-SWPR-01-002]
- required reads:
  - AGENTS.md
  - specs/static-web-page-content-removal/README.md
  - specs/static-web-page-content-removal/sprints/01-remove-static-web-page-guidance-sprint.md
  - specs/static-web-page-content-removal/backlog/01-remove-static-web-page-guidance-build-backlog.md
  - specs/static-web-page-content-removal/tasks/01-remove-static-web-page-guidance/03-remove-static-page-examples.md
  - src/main/java/com/example/api/StaticContentEndpoint.java
  - src/test/java/com/example/application/StaticContentEndpointIntegrationTest.java
  - src/main/resources/static-resources/http-endpoint/index.html
  - src/main/resources/static-resources/http-endpoint/app.css
  - src/main/resources/static-resources/http-endpoint/help.txt
  - src/main/resources/static-resources/http-endpoint/guide/index.html
  - src/main/resources/static-resources/http-endpoint/openapi.yaml
- skills:
  - none; source/example cleanup task
- expected outputs:
  - remove StaticContentEndpoint source
  - remove StaticContentEndpoint integration test
  - remove static-resources/http-endpoint resources
  - repair references to removed files
- required checks:
  - `rg -n "StaticContentEndpoint|http-endpoint/index.html|http-endpoint/app.css|http-endpoint/openapi.yaml|static-content" src test skills docs pack --glob '!specs/static-web-page-content-removal/**'`
  - `find src/main/resources/static-resources/http-endpoint -type f 2>/dev/null || true`
  - `mvn test -DskipITs=false`
  - `git status --short`
- done criteria:
  - static-page endpoint/resources/tests are removed
  - remaining examples are not static-page implementation examples
  - task changes and queue update are committed
- notes:
  - commit message: `Remove static page executable examples`
  - completed: removed the static page endpoint source, integration test, packaged resources, and current references to the removed example.

### TASK-SWPR-01-004: Revise or remove static-web-page docs

- status: done
- source: specs/static-web-page-content-removal/backlog/01-remove-static-web-page-guidance-build-backlog.md
- task brief: specs/static-web-page-content-removal/tasks/01-remove-static-web-page-guidance/04-revise-static-page-docs.md
- depends on: [TASK-SWPR-01-003]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/static-web-page-content-removal/README.md
  - specs/static-web-page-content-removal/sprints/01-remove-static-web-page-guidance-sprint.md
  - specs/static-web-page-content-removal/backlog/01-remove-static-web-page-guidance-build-backlog.md
  - specs/static-web-page-content-removal/tasks/01-remove-static-web-page-guidance/04-revise-static-page-docs.md
  - docs/web-ui-static-content.md
  - docs/web-ui-static-content-checklist.md
  - docs/web-ui-pattern-selection.md
  - docs/next-steps.md
  - docs/skills-pack-tech-stack.md
  - docs/web-ui-frontend-decomposition.md
  - docs/web-ui-frontend-project-integration.md
- skills:
  - none; documentation cleanup task
- expected outputs:
  - remove or revise docs/web-ui-static-content.md
  - remove or revise docs/web-ui-static-content-checklist.md
  - revise other docs with current static-page guidance
- required checks:
  - `rg -n "web-ui/static content|static-content foundation|static content example|packaged static assets|OpenAPI publication|simple file serving|static docs|static page|static pages" docs skills --glob '!specs/static-web-page-content-removal/**'`
  - `rg -n "React|Vite|TypeScript|frontend project|static-resources" docs/web-ui-frontend-decomposition.md docs/web-ui-frontend-project-integration.md docs/skills-pack-tech-stack.md`
  - `git status --short`
- done criteria:
  - obsolete static web page docs are removed or revised
  - current docs emphasize React/Vite/TypeScript app delivery
  - task changes and queue update are committed
- notes:
  - commit message: `Remove static page documentation guidance`
  - completed: removed obsolete static web page planning/checklist docs and revised retained docs/skill wording toward React/Vite/TypeScript frontend app delivery and generated build-output hosting.

### TASK-SWPR-01-005: Final static page removal validation

- status: done
- source: specs/static-web-page-content-removal/backlog/01-remove-static-web-page-guidance-build-backlog.md
- task brief: specs/static-web-page-content-removal/tasks/01-remove-static-web-page-guidance/05-final-static-page-removal-validation.md
- depends on: [TASK-SWPR-01-004]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/static-web-page-content-removal/README.md
  - specs/static-web-page-content-removal/sprints/01-remove-static-web-page-guidance-sprint.md
  - specs/static-web-page-content-removal/backlog/01-remove-static-web-page-guidance-build-backlog.md
  - specs/static-web-page-content-removal/tasks/01-remove-static-web-page-guidance/05-final-static-page-removal-validation.md
  - pack/manifest.yaml
  - any file returned by required searches
- skills:
  - none; validation and repair task
- expected outputs:
  - repair any remaining current static-page implementation references
  - update queue with final validation summary
- required checks:
  - `rg -n "akka-http-endpoint-static-content|StaticContentEndpoint|static content only|simple file serving|packaged docs|static web page|static page|static pages|hand-authored static|non-interactive HTML|http-endpoint/index.html|http-endpoint/openapi.yaml" skills docs pack src test --glob '!**/target/**' --glob '!specs/static-web-page-content-removal/**'`
  - `rg -n "HttpResponses\\.staticResource" skills docs src test --glob '!**/target/**' --glob '!specs/static-web-page-content-removal/**'`
  - `rg -n "React|Vite|TypeScript|frontend project" skills/README.md skills/akka-web-ui-apps/SKILL.md skills/akka-web-ui-frontend-project/SKILL.md docs/web-ui-frontend-project-integration.md`
  - `mvn test -DskipITs=false`
  - `git status --short`
- done criteria:
  - no current pack guidance promotes implementing static web pages
  - residual `HttpResponses.staticResource(...)` references are only for generated frontend app asset hosting or explicitly allowed historical context
  - task changes and queue update are committed
- notes:
  - commit message: `Validate static page guidance removal`
  - completed: final searches found no static-page guidance matches; remaining `HttpResponses.staticResource(...)` matches are generated frontend app shell/build-asset hosting references, with wording repaired away from packaged/static page patterns.
