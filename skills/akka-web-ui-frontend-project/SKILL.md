---
name: akka-web-ui-frontend-project
description: Integrate a standard frontend project, such as React/Vite, with an Akka Java SDK backend that hosts the production frontend build from static resources. Use for full web apps under frontend/ rather than hand-authored static pages.
---

# Akka Web UI Frontend Project

Use this skill when the Akka service includes a real frontend application project and Akka should host its production build output.

This skill is about web UI integration only. Defer authentication, authorization, JWT, identity-provider, and secret-handling details to security-specific skills/docs.

## Required reading

Read these first if present:
- `../../../docs/web-ui-frontend-project-integration.md`
- `../../../docs/web-ui-frontend-decomposition.md`
- `../../../docs/web-ui-quality-checklist.md`
- `../akka-http-endpoint-web-ui/SKILL.md`
- project `frontend/package.json`
- project `frontend/vite.config.ts` or equivalent frontend build config
- project `frontend/src/**` for frontend source ownership
- project `src/main/resources/static-resources/**` for current built assets
- matching Akka frontend hosting endpoint and endpoint tests

Reference example document:
- `../../../frontend-with-akka-backend.md` — use only the frontend layout, build output, Akka static hosting, route separation, and SPA-routing guidance. Do not import its auth/security content unless a security task explicitly asks for it.

## Use this skill when

- the user asks for a full web app, frontend project, React app, Vite app, dashboard, admin console, or portal hosted by Akka
- `frontend/` exists or should be created as a separate frontend development project
- frontend source should build into `src/main/resources/static-resources/`
- Akka should serve `index.html` and built `/assets/**` files
- backend APIs should be called through same-origin `/api/...` routes

Use `akka-web-ui-lightweight-typescript` instead only for small framework-free browser apps that deliberately do not need a frontend project.

## Integration contract

Before implementation, identify:

1. Frontend framework/build tool and package manager
2. Frontend source root, usually `frontend/src/`
3. Production build output directory, usually `src/main/resources/static-resources/`
4. Akka static route plan for `/`, `/assets/**`, `favicon.ico`, and explicit SPA entry routes
5. Backend API route families, usually `/api/...`
6. SPA routing choice: hash routing, explicit server entry routes, or in-app-only navigation
7. Generated asset ownership rule: edit frontend source, rebuild, do not hand-edit generated assets
8. Endpoint integration tests for page and asset delivery

## Default implementation order

1. Create or inspect `frontend/package.json` and build config.
2. Set the frontend production build output to `../src/main/resources/static-resources` or equivalent.
3. Implement the frontend source in `frontend/src/**`.
4. Add or update Akka static frontend endpoint routes.
5. Keep JSON/SSE/WebSocket APIs under separate route families such as `/api/...`, `/streams/...`, or `/websockets/...`.
6. Build the frontend.
7. Add/update endpoint integration tests for `index.html`, built assets, and route references.
8. Run frontend checks/build and relevant Akka tests.

## Akka hosting rules

Prefer explicit routes:

```text
/              -> index.html
/favicon.ico   -> favicon.ico when present
/assets/**     -> built frontend assets
/api/...        -> backend APIs, not static frontend files
```

Avoid broad `/**` fallback routes when `/assets/**` or other wildcard routes exist. If browser deep links must refresh correctly, add explicit non-wildcard frontend entry routes that return `index.html`.

## Testing rules

Test through Akka HTTP endpoints, not only the frontend dev server:
- `GET /` returns the built `index.html`
- the HTML references built JS/CSS assets
- at least one referenced asset route returns expected JS/CSS content
- explicit SPA entry routes return `index.html` when configured
- `/api/...` routes remain separate from frontend asset routes

## Anti-patterns

Avoid:
- treating a React/Vite app as hand-authored static HTML/CSS/JS
- hand-editing generated files under `src/main/resources/static-resources/`
- copying local frontend env files into served resources
- mixing frontend assets and backend APIs under ambiguous route wildcards
- adding a `/**` SPA fallback that conflicts with asset wildcard routes
- importing auth/security implementation guidance into a pure web UI integration task
