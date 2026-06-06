# Web UI docs index

Use this index to avoid loading every UI document. For generated AI-first SaaS, the canonical browser model is the authenticated agent workstream shell with functional-agent rail, continuous stream/composer, structured surfaces, backend-backed actions, realtime/stale state when needed, and accessible responsive rendering.

## Canonical docs

- `./workstream-ui-reference-architecture.md` — primary UI architecture and source layout reference.
- `./structured-surface-contracts.md` — surface payload/action/event/auth/trace contracts.
- `./web-ui-style-guide.md` — canonical AI-first SaaS visual/style system.
- `./web-ui-api-contract-patterns.md` — typed browser API and DTO/error patterns.
- `./web-ui-frontend-project-integration.md` — React/Vite frontend build and Akka static-resource hosting.
- `./web-ui-quality-checklist.md` — final review checklist.

## Thin routing docs

- `./web-ui-pattern-selection.md` — decide frontend project, API, streaming, WebSocket, and hosting pattern.
- `./web-ui-frontend-decomposition.md` — compact planning output for workstream shells and surfaces.
- `./frontend-with-akka-backend.md` — legacy combined reference; prefer the focused docs above for new work.

## Focused skills

- `../akka-web-ui-apps/SKILL.md` — overall browser app routing.
- `../akka-web-ui-frontend-project/SKILL.md` — frontend project setup/build integration.
- `../akka-http-endpoint-web-ui/SKILL.md` — Akka asset/API route hosting.
- `../akka-web-ui-api-client/SKILL.md` — typed frontend API client.
- `../akka-web-ui-ux-design/SKILL.md` — UX design.
- `../akka-web-ui-accessibility-responsive/SKILL.md` — a11y/responsive.
- `../akka-web-ui-state-rendering/SKILL.md` — state/rendering.
- `../akka-web-ui-forms-validation/SKILL.md` — forms/validation.
- `../akka-web-ui-realtime/SKILL.md` — SSE/WebSocket browser behavior.
- `../akka-web-ui-testing/SKILL.md` — UI checks and tests.

Do not use legacy `frontend/src/screens/**`, removed static UI fixtures, route-only tests, copied demo content, or pack examples as normal generated-app UI structure. Application code belongs in the target/root project, not under installed `.agents` assets.
