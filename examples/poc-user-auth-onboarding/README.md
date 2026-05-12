# Akka Full-Stack Security Sample Reference

This directory is a portable reference copy of the sample application for use in another repository, especially for AI review and implementation guidance.

## What is included

- `src/` — Java Akka SDK backend source, domain model, security/authorization helpers, endpoints, resources, and tests.
- `frontend/` — React + Vite frontend source and package manifests.
- `pom.xml` — Maven project definition and backend dependencies.
- `docs/` — curated app, product, design, security, and frontend/backend integration notes.

## What is intentionally excluded

- `frontend/node_modules/` — generated dependency install output.
- `target/` — Maven build output.
- `frontend/.env.local` — local secrets/config; use `frontend/.env.example` as the template.
- `src/main/resources/static-resources/` — generated Vite production build output. Rebuild it from `frontend/` when needed.
- Large external Akka documentation snapshots. Add those separately only if the destination repo needs offline vendor docs.

## Key reference areas

- Authentication: WorkOS JWT validation on Akka HTTP endpoints.
- Authorization: `src/main/java/com/example/security/AuthorizationService.java`.
- Roles and scopes: `src/main/java/com/example/domain/Role.java` and `RoleAssignment.java`.
- User lifecycle: `src/main/java/com/example/application/UserAccountEntity.java`.
- Admin APIs: `src/main/java/com/example/api/AdminUsersEndpoint.java`, `TenantAdminEndpoint.java`, and `CustomerAdminEndpoint.java`.
- Current-user API: `src/main/java/com/example/api/MeEndpoint.java`.
- Static frontend hosting: `src/main/java/com/example/api/StaticFrontendEndpoint.java` and `docs/frontend-with-akka-backend.md`.
- UI/product guidance: `docs/PRODUCT.md` and `docs/DESIGN.md`.

## Build notes

Backend:

```bash
mvn compile
```

Frontend:

```bash
cd frontend
npm install
cp .env.example .env.local
npm run build
```

The frontend build emits production assets into `src/main/resources/static-resources/`.
