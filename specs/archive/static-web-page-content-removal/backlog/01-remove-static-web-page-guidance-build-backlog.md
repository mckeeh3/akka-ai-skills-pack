# Build Backlog — Sprint 01 Static Web Page Guidance Removal

## Purpose

Provide implementation-ready work items that remove/revise static web page guidance while preserving React/Vite/TypeScript frontend app delivery and Akka hosting of generated frontend build output.

## Implementation order

1. Remove the dedicated static-content skill and routing/manifest entries.
2. Revise web UI routing docs/skills so static pages are not a current pattern.
3. Remove executable static-page examples, resources, and tests.
4. Revise remaining docs and examples to use React/Vite/TypeScript language.
5. Run packaging/search validation and repair residual references.

## Backlog items

### 01. Remove static-content skill exposure

Remove `skills/akka-http-endpoint-static-content/` and all current routing/packaging references to that skill.

Expected edits:

- Delete `skills/akka-http-endpoint-static-content/SKILL.md` and its containing directory if empty.
- Remove `akka-http-endpoint-static-content` from `pack/manifest.yaml`.
- Remove or rewrite references in `skills/README.md` that list static content as a Stage 3 route.
- Remove or rewrite references in `skills/akka-http-endpoints/SKILL.md` and any endpoint routing skill that points agents to the removed static-content skill.

Validation:

```bash
rg -n "akka-http-endpoint-static-content" skills pack docs specs --glob '!specs/static-web-page-content-removal/**'
```

Expected: no matches outside this planning package or explicit historical/archive context.

### 02. Revise web UI routing away from static page patterns

Update current web UI skills/docs so they no longer present static pages, packaged docs, or simple file serving as an implementation option.

Expected edits:

- `skills/akka-http-endpoint-web-ui/SKILL.md`
- `skills/akka-web-ui-apps/SKILL.md`
- `skills/akka-web-ui-frontend-project/SKILL.md`
- `docs/web-ui-pattern-selection.md`
- any directly related docs found by search

Required semantic change:

- Browser UI work should route to React/Vite/TypeScript frontend project guidance.
- Akka static resources should be described as generated build output hosting, not as a hand-authored page implementation path.
- If a single HTML entry point is mentioned, it must be the frontend build output or app shell produced by the frontend project, not a static-page example to hand-author.

Validation:

```bash
rg -n "static content only|simple file serving|packaged docs|static page|static pages|hand-authored static|non-interactive HTML" skills docs --glob '!specs/static-web-page-content-removal/**'
```

Expected: no current guidance that recommends static page implementation.

### 03. Remove static-page executable examples and tests

Remove source/resources/tests whose primary purpose is demonstrating hand-authored static page serving.

Expected removals:

- `src/main/java/com/example/api/StaticContentEndpoint.java`
- `src/test/java/com/example/application/StaticContentEndpointIntegrationTest.java`
- `src/main/resources/static-resources/http-endpoint/**`

Also inspect and remove or revise any remaining example that exists only to demonstrate static pages rather than React/Vite/TypeScript app hosting.

Do not remove examples that demonstrate JSON APIs, SSE, WebSocket, or React/Vite/TypeScript app hosting unless they are tightly coupled to the removed static-page pattern and cannot be cleanly revised.

Validation:

```bash
rg -n "StaticContentEndpoint|http-endpoint/index.html|http-endpoint/app.css|http-endpoint/openapi.yaml|static-content" src test skills docs pack --glob '!specs/static-web-page-content-removal/**'
```

Expected: no current implementation/example references to the removed static-page example.

### 04. Revise static-web-page docs into React/Vite-only guidance or remove them

Remove obsolete planning docs or rewrite current docs that still explain static web page implementation.

Expected edits/removals to evaluate:

- `docs/web-ui-static-content.md`
- `docs/web-ui-static-content-checklist.md`
- `docs/web-ui-pattern-selection.md`
- `docs/next-steps.md`
- `docs/skills-pack-tech-stack.md`
- any other doc found by search

Preferred outcome:

- Delete historical static-content implementation docs if they are no longer useful.
- If a doc is retained, revise it to say the supported browser UI path is React/Vite/TypeScript frontend project integration.
- Keep only the minimal generated-build-output hosting facts needed by React/Vite apps.

Validation:

```bash
rg -n "web-ui/static content|static-content foundation|static content example|packaged static assets|OpenAPI publication|simple file serving|static docs" docs skills --glob '!specs/static-web-page-content-removal/**'
```

Expected: no current docs presenting static-page implementation as supported guidance.

### 05. Final packaging/search validation and repair

Run broad validation after prior tasks and repair residual references.

Required checks:

```bash
rg -n "akka-http-endpoint-static-content|StaticContentEndpoint|static content only|simple file serving|packaged docs|static web page|static page|static pages|hand-authored static|non-interactive HTML|http-endpoint/index.html|http-endpoint/openapi.yaml" skills docs pack src test --glob '!**/target/**' --glob '!specs/static-web-page-content-removal/**'
rg -n "HttpResponses\.staticResource" skills docs src test --glob '!**/target/**' --glob '!specs/static-web-page-content-removal/**'
```

Allowed residual references:

- React/Vite/TypeScript build-output hosting from `src/main/resources/static-resources/`.
- Akka endpoint code/tests serving generated frontend assets for a real app.
- Archived/historical specs if explicitly outside current pack guidance.

Final validation should also inspect `pack/manifest.yaml` to ensure no removed skill ID remains.

## Commit rule for every task

Each backlog task must end with one git commit. The commit must include only:

- the intended edits/removals for that task, and
- that task's `pending-tasks.md` status update.

Recommended commit message format:

```text
Remove static page <scope>
```
