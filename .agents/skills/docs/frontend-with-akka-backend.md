# Frontend integration with the Akka backend

> Legacy combined reference. For new work, prefer the focused UI docs:
>
> - `./web-ui-docs-index.md`
> - `./web-ui-frontend-project-integration.md` for React/Vite build and Akka static-resource hosting
> - `./web-ui-pattern-selection.md` for pattern routing
> - `./web-ui-api-contract-patterns.md` for typed API contracts
> - `./security-workos-auth-and-admin.md` for auth/security
> - `../akka-web-ui-frontend-project/SKILL.md` and `../akka-http-endpoint-web-ui/SKILL.md` for implementation

This note is kept only to preserve the historical “frontend + backend + security” entry point. Do not expand it with new doctrine; update the focused docs instead.

## Standard layout

```text
frontend/                                  # React/Vite/TypeScript source
src/main/resources/static-resources/       # production build output served by Akka
src/main/java/ai/first/...                 # Akka backend/API/components
```

A typical frontend build script emits directly to the Akka static-resource directory:

```json
{
  "scripts": {
    "build": "vite build --outDir ../src/main/resources/static-resources --emptyOutDir"
  }
}
```

Run focused checks such as:

```bash
npm --prefix frontend run build
npm --prefix frontend run typecheck
npm --prefix frontend test -- --run
mvn test
```

## Hosting and API split

- Static frontend assets may be public.
- Protected app data, actions, workstream surface payloads, streams, and admin APIs use authenticated `/api/...` or explicit stream/WebSocket routes.
- Frontend source belongs in `frontend/**`; generated static assets belong in `src/main/resources/static-resources/**`.
- Do not commit frontend secrets or copy `.env.local` into static resources. Only intended `VITE_` public config may be embedded by the build.
- Browser code must call typed APIs and render frontend-safe DTOs/surface envelopes; it must not import backend domain state or secrets.

## Generated SaaS UI rule

For generated AI-first SaaS, use the workstream UI model in `./workstream-ui-reference-architecture.md`: functional-agent rail, continuous stream/composer, structured surfaces, backend-backed actions, realtime/stale state when needed, accessibility, and responsive behavior. Conventional routes are implementation/deep-link details.

Do not use legacy `frontend/src/screens/**`, removed static UI fixtures, static mockups, route-only tests, copied demo content, or pack examples as normal generated-app UI structure. Application code belongs in the target/root project, not installed `.agents` assets.
