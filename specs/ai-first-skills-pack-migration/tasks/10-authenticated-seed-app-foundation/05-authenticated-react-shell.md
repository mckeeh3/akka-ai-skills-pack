# TASK-10-005: Implement authenticated React/Vite shell and Akka hosting

## Purpose

Add the authenticated frontend shell for the DCA seed app using WorkOS AuthKit, same-origin API calls, role-aware navigation, and Akka-hosted React/Vite build output.

## Required reads

- `AGENTS.md`
- `docs/web-ui-style-guide.md`
- `docs/security-workos-auth-and-admin.md`
- `docs/examples/ai-first-dca-app-description/app-description/55-ui/style-guide.md`
- `specs/ai-first-skills-pack-migration/sprints/10-authenticated-seed-app-foundation-sprint.md`
- `specs/ai-first-skills-pack-migration/backlog/10-authenticated-seed-app-foundation-build-backlog.md`
- `/api/me` and admin APIs from `TASK-10-003` and `TASK-10-004`
- `examples/poc-user-auth-onboarding/docs/frontend-with-akka-backend.md`
- `examples/poc-user-auth-onboarding/frontend/package.json`
- `examples/poc-user-auth-onboarding/frontend/src/main.tsx`
- `examples/poc-user-auth-onboarding/src/main/java/com/example/api/StaticFrontendEndpoint.java`

## Scope

- Add or adapt a React/Vite frontend project.
- Wrap the app with WorkOS AuthKit using public `VITE_` variables.
- Implement bearer-token API client behavior for `/api/me` and protected APIs.
- Add role-aware navigation for DCA seed surfaces, including supplies autopilot when present.
- Add loading, signed-out, unauthorized, forbidden, disabled, and error states.
- Add Akka static frontend hosting for generated build assets.
- Add frontend build/smoke tests and static route tests.

## Non-goals

- No new AI-first domain behavior.
- No hidden frontend-only authorization.
- No broad SPA catch-all if it conflicts with Akka wildcard route constraints; use explicit routes or hash/internal navigation.

## Skills

- `akka-workos-user-auth`
- `akka-web-ui-apps`
- `akka-web-ui-frontend-project`
- `akka-web-ui-api-client`
- `akka-web-ui-state-rendering`
- `akka-web-ui-accessibility-responsive`
- `akka-web-ui-testing`
- `akka-http-endpoints`
- `akka-http-endpoint-web-ui`
- `akka-http-endpoint-testing`

## Expected outputs

- Frontend project/auth shell/API client files.
- Static frontend hosting endpoint updates.
- Frontend and route smoke tests.

## Required checks

- Run frontend build/test command.
- Run static route/asset tests.
- Verify backend secrets are absent from frontend env examples and build output.

## Done criteria

- The DCA seed app has a usable authenticated shell whose UX reflects roles/scopes but never replaces backend authorization.
