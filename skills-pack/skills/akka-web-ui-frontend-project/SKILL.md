---
name: akka-web-ui-frontend-project
description: Integrate a standard frontend project, such as React/Vite/TypeScript, with an Akka Java SDK backend that hosts the production frontend build from static resources. Use for full agent workstream web apps under frontend/.
---

# Akka Web UI Frontend Project

Use this skill when the Akka service includes a React/Vite/TypeScript frontend application project and Akka should host its production build output.

For generated full-stack AI-first SaaS apps, the frontend project should implement the agent workstream shell: left-rail functional agents, main workstream panel, persistent bottom composer, context/authority indicators, and typed structured surfaces. Route/page files are implementation modules and deep-link entry points, not the primary application model.

This skill is about web UI project integration only. Defer authentication, authorization, JWT, identity-provider, and secret-handling details to security-specific skills/docs while preserving the shell's `/api/me`, AuthContext, capability-gated agent/action, and forbidden-state contracts.

## Generated SaaS input contract

For generated full-stack AI-first SaaS frontend-project work, implement only after the task, app-description, spec, or backlog supplies or explicitly defers:
- functional-agent shell, workstream regions, structured surface ids/types/versions, surface actions/events, and deep-link strategy;
- governed capability ids/classes behind browser API/realtime calls, selected Akka substrates, and frontend/API/realtime exposure;
- `/api/me`, `AuthContext`, tenant/customer scope, roles/capabilities, disabled/forbidden states, frontend secret boundaries, selected style guide, and named-theme contract;
- DTOs, redaction, idempotency/correlation ids, policy/approval/escalation states, audit/work trace links, rendering/API/realtime tests, and build checks.

If these are absent for generated SaaS implementation, route back to `akka-web-ui-apps`, `agent-workstream-apps`, and `capability-first-backend` or repair the task brief instead of creating a generic frontend project.

## Required reading

Read these first if present:
- `../../docs/web-ui-frontend-project-integration.md`
- `../../docs/web-ui-frontend-decomposition.md`
- `../../docs/web-ui-quality-checklist.md`
- `../akka-http-endpoint-web-ui/SKILL.md`
- project `frontend/package.json`
- project `frontend/vite.config.ts` or equivalent frontend build config
- project `frontend/src/**` for frontend source ownership
- project `src/main/resources/static-resources/**` for current built assets
- matching Akka frontend hosting endpoint and endpoint tests

Reference example document:
- `../../docs/frontend-with-akka-backend.md` — use only the frontend layout, build output, Akka static hosting, route separation, and SPA-routing guidance. Do not import its auth/security content unless a security task explicitly asks for it.

## Use this skill when

- the user asks for a full web app, frontend project, React app, Vite app, dashboard, admin console, portal, or agent workstream shell hosted by Akka
- `frontend/` exists or should be created as a separate frontend development project
- frontend source should build into `src/main/resources/static-resources/`
- Akka should serve `index.html` and built `/assets/**` files
- backend APIs should be called through same-origin `/api/...` routes

For interactive web app work, use this full frontend project path. Keep Akka static-resource guidance focused on generated React/Vite/TypeScript frontend build output.

For workstream apps, organize frontend source around the canonical reusable workstream reference, for example:
- `frontend/src/workstream/shell/` for the authenticated shell, workstream panel, context indicators, and deep-link helpers;
- `frontend/src/workstream/rail/` for role-authorized functional-agent rail behavior;
- `frontend/src/workstream/composer/` for the persistent selected-agent composer;
- `frontend/src/workstream/stream/` for workstream items, action feedback, trace links, and stream merge helpers;
- `frontend/src/workstream/surfaces/` for typed dashboard, list/search, detail/edit, decision, diff, approval, audit timeline, workflow status, and outcome surface components;
- `frontend/src/workstream/actions/` for capability-backed action controls and idempotency/result-surface helpers;
- `frontend/src/workstream/realtime/` for surface/workstream event parsing, dedupe, reconnect, and stale markers;
- `frontend/src/workstream/types/` and `frontend/src/workstream/fixtures/` for reusable contracts and fixture examples;
- `frontend/src/api/` for browser-safe DTO clients and error mapping, including `WorkstreamApiClient.ts`, `WorkstreamRealtimeClient.ts`, and fixture implementations.

Do not organize the primary frontend solely as a conventional route/page tree unless the task is public/static, non-SaaS, or explicitly outside the generated workstream default.

## Integration contract

Before implementation, identify:

1. Frontend framework/build tool and package manager
2. Frontend source root, usually `frontend/src/`
3. Production build output directory, usually `src/main/resources/static-resources/`
4. Akka static route plan for `/`, `/assets/**`, `favicon.ico`, and explicit SPA entry routes
5. Backend API route families, usually `/api/...`
6. Workstream deep-link strategy for selected functional agent, stream item, and structured surface URLs
7. SPA routing choice: hash routing, explicit server entry routes, or in-app-only navigation
8. Generated asset ownership rule: edit frontend source, rebuild, do not hand-edit generated assets
9. Endpoint integration tests for shell, explicit deep-link entries, and asset delivery

## Default implementation order

1. Create or inspect `frontend/package.json` and build config.
2. Set the frontend production build output to `../src/main/resources/static-resources` or equivalent.
3. Implement the frontend source in `frontend/src/**`, keeping workstream shell regions and structured surface components in source-owned modules.
4. Add or update Akka static frontend endpoint routes and any explicit workstream deep-link entries.
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

Avoid broad `/**` fallback routes when `/assets/**` or other wildcard routes exist. If browser deep links for selected functional agents, stream items, or structured surfaces must refresh correctly, add explicit non-wildcard frontend entry routes that return `index.html`.

## Testing rules

Test through Akka HTTP endpoints, not only the frontend dev server:
- `GET /` returns the built `index.html`
- the HTML references built JS/CSS assets
- at least one referenced asset route returns expected JS/CSS content
- explicit SPA/workstream deep-link entry routes return `index.html` when configured
- `/api/...` routes remain separate from frontend asset routes
- generated app shell references the workstream shell bundle without leaking protected data into static assets

## Anti-patterns

Avoid:
- treating a React/Vite/TypeScript app as unmanaged resource files instead of editing frontend source and rebuilding
- reducing the generated SaaS frontend to a page-first route tree when the required model is an agent workstream shell
- hand-editing generated files under `src/main/resources/static-resources/`
- copying local frontend env files into served resources
- mixing frontend assets and backend APIs under ambiguous route wildcards
- adding a `/**` SPA fallback that conflicts with asset wildcard routes
- importing auth/security implementation guidance into a pure web UI integration task
