# Frontend Integration with the Akka Backend

This project is a full-stack Akka application where the Java/Akka backend hosts both the secured backend APIs and the production React frontend assets. The frontend source code is developed independently in the `frontend/` directory, while the built/deployed frontend files are copied into Akka's static resource directory.

## Directory layout

```text
.
├── pom.xml
├── frontend/
│   ├── package.json
│   ├── vite.config.ts
│   ├── tsconfig.json
│   ├── index.html
│   ├── .env.local              # local Vite build/dev config, not deployed directly
│   └── src/
│       ├── main.tsx
│       └── styles.css
└── src/
    └── main/
        ├── java/
        │   └── com/example/...
        └── resources/
            └── static-resources/
                ├── index.html
                └── assets/
                    ├── *.js
                    └── *.css
```

The important separation is:

- `frontend/` contains the frontend development project.
- `src/main/resources/static-resources/` contains the frontend production build output served by Akka.

Do not copy `frontend/.env.local` into `src/main/resources/static-resources/`. Vite reads `.env.local` at build time and embeds `VITE_` variables into the generated JavaScript bundle.

## Build flow

The React app is built with Vite. The relevant script is in `frontend/package.json`:

```json
{
  "scripts": {
    "build": "vite build --outDir ../src/main/resources/static-resources --emptyOutDir"
  }
}
```

Running:

```bash
cd frontend
npm install
npm run build
```

emits the production frontend directly into:

```text
src/main/resources/static-resources/
```

A typical build result looks like:

```text
src/main/resources/static-resources/index.html
src/main/resources/static-resources/assets/index-xxxxx.js
src/main/resources/static-resources/assets/index-xxxxx.css
```

After building the frontend, compile or run Akka:

```bash
cd ..
mvn compile exec:java
```

During packaging/deployment, the files under `src/main/resources/static-resources/` are included on the Java classpath and can be served by Akka HTTP endpoints.

## Akka static resource hosting

The Akka endpoint responsible for serving the frontend is:

```text
src/main/java/com/example/api/StaticFrontendEndpoint.java
```

It exposes public static routes such as:

```text
/              -> index.html
/favicon.ico   -> favicon.ico
/assets/**     -> frontend JS/CSS/assets
```

The implementation uses Akka SDK static resource support:

```java
HttpResponses.staticResource("index.html")
HttpResponses.staticResource(request, "/")
```

Akka resolves those files relative to:

```text
src/main/resources/static-resources/
```

So this call:

```java
HttpResponses.staticResource("index.html")
```

serves:

```text
src/main/resources/static-resources/index.html
```

And this route:

```java
@Get("/assets/**")
public HttpResponse assets(HttpRequest request) {
  return HttpResponses.staticResource(request, "/");
}
```

serves built frontend assets such as:

```text
/assets/index-xxxxx.js
/assets/index-xxxxx.css
```

from:

```text
src/main/resources/static-resources/assets/
```

## Route separation

The app uses a clear separation between frontend static routes and backend API routes.

Frontend/static routes:

```text
/              React app entry point
/assets/**     React/Vite build assets
/favicon.ico   Browser favicon
```

Backend API routes:

```text
/api/me
/api/admin/users
/api/tenants
/api/tenants/{tenantId}/customers
```

This separation is important because Akka route discovery does not allow overlapping wildcard paths such as:

```java
@Get("/assets/**")
@Get("/**")
```

That combination causes a runtime startup error similar to:

```text
Overlapping wildcard paths: [/favicon.ico,/assets] and [/**]
```

For that reason, the current implementation does not use a broad `/**` SPA fallback route.

## SPA routing consideration

Because Akka cannot safely expose both `/assets/**` and a catch-all `/**` endpoint in this structure, direct deep-link browser refreshes such as:

```text
http://localhost:9000/admin/users
```

are not currently handled by the static endpoint.

Recommended options are:

1. use hash-based frontend routes, for example:

```text
http://localhost:9000/#/admin/users
```

2. add explicit non-wildcard frontend entry routes such as:

```text
/admin
/profile
/settings
```

where each route returns `index.html`, or

3. keep navigation state inside the SPA, as the current scaffold does.

The current frontend uses internal React state for navigation rather than browser path routing.

## Authentication integration

The frontend uses WorkOS AuthKit React:

```ts
import { AuthKitProvider, useAuth } from '@workos-inc/authkit-react';
```

The frontend is wrapped with:

```tsx
<AuthKitProvider clientId={clientId} redirectUri={redirectUri}>
  <SecureShell />
</AuthKitProvider>
```

The values come from Vite environment variables:

```ts
const clientId = import.meta.env.VITE_WORKOS_CLIENT_ID ?? '';
const redirectUri = import.meta.env.VITE_WORKOS_REDIRECT_URI ?? window.location.origin;
```

For local Akka-hosted testing, `frontend/.env.local` should contain:

```env
VITE_WORKOS_CLIENT_ID=client_your_workos_client_id
VITE_WORKOS_REDIRECT_URI=http://localhost:9000
```

These are public browser-side values. They are embedded into the frontend bundle at build time.

Do not put WorkOS API secrets or Resend API secrets in frontend `.env.local`. Backend secrets belong in backend environment variables only.

## WorkOS dashboard requirements

For local Akka-hosted frontend testing, WorkOS must be configured with:

Redirect URI:

```text
http://localhost:9000
```

Allowed origin / CORS origin:

```text
http://localhost:9000
```

If using Vite dev mode directly, also configure:

```text
http://localhost:5173
```

for the redirect and allowed origin as appropriate.

## Frontend calling backend APIs

After the user signs in, the frontend gets an access token from WorkOS AuthKit:

```ts
const token = await auth.getAccessToken();
```

It sends that token to Akka APIs using the standard bearer token header:

```ts
const response = await fetch('/api/me', {
  headers: {
    Authorization: `Bearer ${token}`
  }
});
```

Because the frontend is served from the same origin as the Akka backend, API calls can use relative URLs:

```text
/api/me
/api/admin/users
```

This avoids browser CORS issues between the frontend and backend in production/local Akka-hosted mode.

## Backend JWT validation

Akka HTTP API endpoints are annotated with JWT validation:

```java
@JWT(validate = JWT.JwtMethodMode.BEARER_TOKEN)
```

For example:

```java
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
@JWT(validate = JWT.JwtMethodMode.BEARER_TOKEN)
@HttpEndpoint("/api/me")
public class MeEndpoint extends AbstractHttpEndpoint {
  ...
}
```

This means:

- static frontend files are publicly reachable
- backend API endpoints require a valid WorkOS bearer token
- authenticated identity comes from the WorkOS JWT
- application authorization comes from Akka-stored user/account/role state

## Authorization model

The frontend should not be trusted to enforce authorization. It may hide or show navigation items based on the current user's roles, but the backend must enforce all permissions.

The backend stores roles in Akka entities, not in the frontend and not as the sole source of truth in JWT claims.

Current roles include:

```text
APP_ADMIN
TENANT_ADMIN
CUSTOMER_ADMIN
USER
```

The frontend uses `/api/me` to load the signed-in user's local Akka account:

```text
GET /api/me
```

The response includes profile, status, and roles. The frontend uses that to show role-aware navigation:

- APP_ADMIN sees all admin sections
- TENANT_ADMIN sees tenant/customer administration
- CUSTOMER_ADMIN sees customer/user administration
- USER sees basic profile/dashboard access

The backend still performs final authorization checks in Java before performing any protected operation.

## Startup admin bootstrap and first login

The backend can bootstrap initial admin users from the `ADMIN_USERS` environment variable on Akka startup.

Example:

```bash
export ADMIN_USERS="jane@gmail.com:ADMIN:ALL,joe@outlook.com:TENANT_ADMIN:tenant-123"
export RESEND_API_KEY="re_xxxxxxxxx"
export INVITE_EMAIL_FROM="Acme <onboarding@example.com>"
export INVITE_EMAIL_SUBJECT="Account access information"
export APP_BASE_URL="http://localhost:9000"
export WORKOS_API_KEY="sk_test_or_sk_live_xxxxxxxxx"
```

When the service starts, it creates invited local Akka users and sends standard invite emails.

When an invited user signs in through WorkOS, Akka links the WorkOS identity to the invited local account and activates it. The frontend then receives an active `/api/me` response and displays the authenticated app shell.

## Local development modes

### Akka-hosted production-like mode

This is the recommended mode for testing the deployed shape of the app:

```bash
cd frontend
npm run build
cd ..
mvn compile exec:java
```

Open:

```text
http://localhost:9000
```

In this mode:

- Akka serves the frontend
- Akka serves the backend APIs
- frontend calls APIs with relative `/api/...` URLs
- WorkOS redirect URI should be `http://localhost:9000`

### Vite dev server mode

Vite can also be used for frontend-only development:

```bash
cd frontend
npm run dev
```

The Vite config proxies backend API requests to Akka:

```ts
server: {
  port: 5173,
  proxy: {
    '/api': 'http://localhost:9000'
  }
}
```

In this mode:

- Vite serves the frontend at `http://localhost:5173`
- Akka still serves APIs at `http://localhost:9000`
- Vite proxies `/api` calls to Akka
- WorkOS must allow `http://localhost:5173` as redirect/origin

## Deployment model

For deployment, build the frontend before building or packaging the Akka service:

```bash
cd frontend
npm ci
npm run build
cd ..
mvn clean install -DskipTests
```

The resulting Akka service artifact/container includes the React build output as Java resources.

This means one deployed Akka service can provide:

- the SPA frontend
- authenticated REST/HTTP APIs
- Akka entities/workflows/views
- startup bootstrap logic
- audit logging

## Security notes

- Only `VITE_` variables are exposed to frontend code.
- WorkOS client ID and redirect URI are public values.
- WorkOS API key, Resend API key, and other secrets must only be backend environment variables.
- Static frontend routes are public, but API routes require JWT validation.
- Backend authorization must always be enforced server-side.
- Admin actions and impersonation should be audited by backend entities.
- Do not depend on hidden frontend navigation as a security control.
