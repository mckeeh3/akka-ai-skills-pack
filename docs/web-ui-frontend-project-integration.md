# Web UI frontend project integration

Use this doc when an Akka service includes a real frontend application project, such as React with Vite, and the Akka backend hosts the production build output.

This guidance focuses only on web UI integration and delivery. Authentication, authorization, JWT, identity-provider, and secret-management implementation details belong in security-specific guidance.

## Canonical layout

Prefer this separation:

```text
.
├── pom.xml
├── frontend/
│   ├── package.json
│   ├── vite.config.ts
│   ├── tsconfig.json
│   ├── index.html
│   └── src/
│       ├── main.tsx
│       └── styles.css
└── src/main/
    ├── java/com/example/...
    └── resources/static-resources/
        ├── index.html
        └── assets/
            ├── *.js
            └── *.css
```

Rules:
- `frontend/` is the frontend development project.
- `src/main/resources/static-resources/` is the production asset directory served by Akka.
- Build-time frontend config files are not copied to `static-resources/` unless they are intentionally public runtime assets.
- Backend source, Akka components, and HTTP endpoints remain under the normal Java project tree.

## Build flow

A Vite app can emit directly into Akka static resources:

```json
{
  "scripts": {
    "build": "vite build --outDir ../src/main/resources/static-resources --emptyOutDir"
  }
}
```

Typical commands:

```bash
cd frontend
npm install
npm run build
cd ..
mvn compile
```

After build, Akka packages these classpath resources:

```text
src/main/resources/static-resources/index.html
src/main/resources/static-resources/assets/index-xxxxx.js
src/main/resources/static-resources/assets/index-xxxxx.css
```

Do not hand-edit generated files under `static-resources/` when they are owned by the frontend build. Edit `frontend/src/**` and rebuild.

## Akka hosting endpoint

Serve the frontend with explicit static routes. Example route plan:

```text
/              -> index.html
/favicon.ico   -> favicon.ico, if present
/assets/**     -> built JS/CSS/image/font assets
/api/...        -> backend JSON/SSE/WebSocket APIs, in separate endpoint classes or route families
```

Implementation pattern:

```java
@Get("/")
public HttpResponse index() {
  return HttpResponses.staticResource("index.html");
}

@Get("/assets/**")
public HttpResponse assets(HttpRequest request) {
  return HttpResponses.staticResource(request, "/");
}
```

Akka resolves static resources relative to:

```text
src/main/resources/static-resources/
```

## Route separation

Keep frontend routes and backend routes non-overlapping:

- frontend entry/assets: `/`, `/assets/**`, explicit UI entry paths
- backend APIs: `/api/...`
- streams: explicit `/streams/...` or feature-specific prefixes
- WebSockets: explicit `/websockets/...`

Avoid combining explicit asset wildcards with a broad catch-all route such as `/**`. Akka route discovery can reject overlapping wildcard paths.

## SPA routing choices

For browser-side routing, choose one of these intentionally:

1. hash routing, e.g. `/#/admin/users`, when one Akka `index.html` route is enough
2. explicit server entry routes, e.g. `/admin`, `/profile`, `/settings`, each returning `index.html`
3. in-app state navigation without deep-link refresh support

Do not add a broad `/**` SPA fallback when it overlaps static asset wildcard routes. If deep links are required, enumerate frontend entry routes.

## Browser API calls

When Akka hosts the frontend and APIs on the same origin, prefer relative URLs:

```ts
await fetch('/api/me');
await fetch('/api/admin/users');
```

This avoids production/local CORS complexity for the hosted app. Keep frontend DTOs browser-facing and stable; do not couple React components directly to internal Akka domain classes.

## Agent implementation checklist

When adding or revising a full frontend app:

- [ ] Confirm whether `frontend/` already exists and identify its framework/build tool.
- [ ] Confirm the build output path targets `src/main/resources/static-resources/`.
- [ ] Keep generated assets out of source edits except when verifying build output.
- [ ] Add or update an Akka static frontend endpoint for `/`, `/assets/**`, and any explicit SPA entry routes.
- [ ] Keep `/api/...` backend routes separate from frontend asset routes.
- [ ] Document SPA routing choice: hash, explicit entries, or in-app-only navigation.
- [ ] Add endpoint integration tests that fetch `index.html`, one built asset route, and verify API route references where applicable.
- [ ] Run frontend build and the relevant Akka tests.

## Non-goals

This doc does not define:
- authentication provider setup
- JWT validation
- role/permission UX
- backend secret handling
- production identity-provider dashboard configuration

Route to security/auth skills or docs for those topics when they are in scope.
