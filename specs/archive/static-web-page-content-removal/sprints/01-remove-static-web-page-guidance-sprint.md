# Sprint 01 — Remove Static Web Page Guidance

## Goal

Remove or revise pack content that encourages implementing static web pages, static documentation pages, or simple packaged HTML/CSS pages, because that path distracts from the desired React/Vite/TypeScript web app implementation flow.

## Target state

- The skills pack routes browser UI work to `akka-web-ui-apps` and `akka-web-ui-frontend-project` for React/Vite/TypeScript apps.
- HTTP endpoint guidance covers serving generated frontend build assets, API routes, SSE, WebSocket, JWT, and endpoint tests without treating hand-authored static pages as an implementation option.
- Static page examples, docs, tests, resources, and manifest entries are removed or revised.
- References to `HttpResponses.staticResource(...)` remain only where needed for hosting frontend build output from `src/main/resources/static-resources/`.
- Search results for static-page concepts are limited to historical/archive context or explicit notes saying static page implementation is no longer a supported pack path.

## Scope

In scope:

- `skills/akka-http-endpoint-static-content/`
- static-content entries in `pack/manifest.yaml`
- static-content routing references in `skills/README.md`
- static-page sections in `skills/akka-http-endpoint-web-ui/SKILL.md`
- web UI docs that still present static page/file-serving patterns as current guidance
- executable examples whose primary purpose is hand-authored static page delivery
- tests that exist only for removed static page examples
- packaged static resources that exist only for removed examples

Out of scope:

- React/Vite/TypeScript app guidance under `skills/akka-web-ui-*`
- full frontend build-output hosting guidance under `docs/web-ui-frontend-project-integration.md`
- API/SSE/WebSocket endpoint examples that are not primarily static-page examples
- AI-first SaaS UI surface guidance unless it explicitly recommends static web pages

## Acceptance behavior

A future agent should be able to ask for a browser UI and be routed toward:

1. web UI UX/design planning when needed,
2. React/Vite/TypeScript frontend project integration,
3. typed browser API client/state/forms/realtime/accessibility/testing companion skills,
4. Akka endpoint hosting for generated frontend build output.

The agent should not be routed toward building a static HTML page as a product UI shortcut.

## Required final validation

At the end of the sprint, these searches should show no current guidance that still promotes static web page implementation:

```bash
rg -n "akka-http-endpoint-static-content|StaticContentEndpoint|static content only|simple file serving|packaged docs|static web page|static page|hand-authored static|http-endpoint/index.html|HttpResponses\.staticResource" skills docs pack src test --glob '!**/target/**'
```

Expected allowed matches:

- React/Vite build-output hosting references.
- Low-level Akka hosting examples for generated frontend assets.
- Historical planning specs under `specs/static-web-page-content-removal/` or clearly archived material.

## Done criteria

- All tasks in `../pending-tasks.md` for this sprint are `done` or intentionally `deferred`/`superseded`.
- The pack manifest no longer exposes a static-content skill.
- Static page endpoint examples/resources/tests are removed or replaced with React/Vite/TypeScript-oriented examples.
- Docs and routing no longer list static pages as an implementation path.
- Each completed task has a git commit.
