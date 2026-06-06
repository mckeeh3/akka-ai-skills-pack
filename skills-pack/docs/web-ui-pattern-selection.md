# Web UI pattern selection

Use this thin routing doc when an Akka service needs browser-facing UI behavior. For generated full-stack AI-first SaaS, the default is a React/Vite/TypeScript agent workstream shell hosted by Akka, not a page-first console.

Read `./web-ui-docs-index.md` first when you need the full UI doc map.

## Pattern table

| Need | Use | Primary references |
|---|---|---|
| Full product browser app with functional-agent rail, stream/composer, structured surfaces, state, forms, API clients, accessibility, responsive behavior | Frontend project web app | `./workstream-ui-reference-architecture.md`, `./web-ui-frontend-decomposition.md`, `../akka-web-ui-apps/SKILL.md`, `../akka-web-ui-frontend-project/SKILL.md` |
| Co-hosted frontend app calling JSON APIs | UI + protected API pattern | `./web-ui-api-contract-patterns.md`, `../akka-http-endpoint-web-ui/SKILL.md`, `../akka-web-ui-api-client/SKILL.md` |
| Static asset hosting only | Akka static resources | `./web-ui-frontend-project-integration.md`, `../akka-http-endpoint-web-ui/SKILL.md` |
| SSE or live updates | Realtime browser pattern | `../akka-http-endpoint-sse/SKILL.md`, `../akka-web-ui-realtime/SKILL.md` |
| WebSocket interaction | Bidirectional browser/server pattern | `../akka-http-endpoint-websocket/SKILL.md`, `../akka-web-ui-realtime/SKILL.md` |
| Protected APIs, route ACLs, identity, tenant/customer filtering | Mandatory security foundation | `../core-saas-foundation/SKILL.md`, `../akka-workos-user-auth/SKILL.md`, `../akka-http-endpoint-jwt/SKILL.md`, `../akka-http-endpoint-acl-internal/SKILL.md` |

## Default conventions

- Frontend source lives in `frontend/**`.
- Production build output goes to `src/main/resources/static-resources/**`.
- Build with the frontend project's script, commonly `npm --prefix frontend run build`.
- The frontend build owns generated static assets; edit source then rebuild.
- Protected data, actions, and streams use `/api/...` or explicit stream/WebSocket route families.
- Public static assets are the narrow unauthenticated exception.

## Route shape

Use clear route families:

- frontend app shell/assets: `/`, `/assets/**`, `/ui...`, or explicit workstream entries
- workstream deep links: selected agent, stream item, or structured surface URLs that reload through protected APIs
- JSON APIs: `/api/...`
- SSE streams: `/streams/...`, `/view-streams/...`, or another explicit stream prefix
- WebSockets: `/websockets/...`
- internal-only routes: clearly separated and ACL-protected

Avoid broad `/**` SPA fallbacks that overlap asset/API routes. Use hash routing or explicit frontend entry routes if needed.

## Security note

Route shape, left-rail visibility, hidden controls, and client-side checks are never authorization. Backend JWT validation, local authorization, selected `AuthContext`, tenant/customer filtering, audit, and tenant-isolation tests inherit the mandatory secure SaaS foundation.

## Retired boundaries

Do not use legacy `frontend/src/screens/**`, removed static UI fixtures, route-only page tests, copied demo names/metrics, or pack examples as generated-app UI structure. See `./retired-content-boundaries.md`.
