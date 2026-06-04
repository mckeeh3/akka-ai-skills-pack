# Auth Runtime Boundary Validation

## Scope

Validation for `TASK-FCSR-02-001` covers the local WorkOS/AuthKit boundary available in this repository:

- browser runtime uses WorkOS/AuthKit and real backend API clients;
- `/api/me` resolves JWT claims into local Akka-owned account, membership, selected `AuthContext`, capability, and audit data;
- disabled users, missing WorkOS claims, wrong selected membership, and tenant mismatch fail closed in backend authorization tests;
- frontend source keeps provider secrets backend-only and sends bearer tokens plus selected context headers to protected APIs.

## Evidence

- `frontend/src/main.tsx` renders a `Configure WorkOS/AuthKit` gate until `VITE_WORKOS_CLIENT_ID` is set to a real public WorkOS client id. It passes `redirectUri` to `AuthKitProvider` and never provides a normal runtime fixture mode.
- `frontend/src/api/HttpWorkstreamApiClient.ts` attaches `Authorization: Bearer <token>`, `X-Selected-Context-Id`, and `X-Correlation-Id` headers for protected `/api/me` and workstream APIs.
- `src/main/java/ai/first/api/foundation/security/MeEndpoint.java` is protected with `@JWT(validate = JWT.JwtMethodMode.BEARER_TOKEN)` and resolves identity from `requestContext().getJwtClaims()`.
- `src/main/java/ai/first/application/foundation/identity/AuthContextResolver.java` keeps local account/membership state authoritative and audits authorization denials.
- `src/test/java/ai/first/application/foundation/identity/MeServiceTest.java` covers linked active `/api/me`, configured admin bootstrap, disabled-user denial, missing WorkOS claims denial, forbidden selected context, and tenant mismatch.
- `src/test/java/ai/first/application/foundation/identity/WorkosIdentityResolverTest.java` covers token email claims, server-side WorkOS profile lookup when email is absent, safe failure when lookup is unavailable, unknown-user denial, and secret non-exposure in resolved identity.
- `frontend/src/auth-runtime-boundary.contract.test.mjs` covers AuthKit public-config gating, bearer-token API transport, selected context headers, backend-only secret names, and fixture-client exclusion from the normal frontend entrypoint.

## Production-like provider blocker

This task does not claim live WorkOS tenant validation against a real issuer/audience or provider account. Production-like validation remains blocked until deploy/runtime configuration supplies backend-only WorkOS values and a real WorkOS application:

- `WORKOS_API_KEY`
- `WORKOS_JWT_ISSUER`
- `WORKOS_JWT_AUDIENCE`
- configured WorkOS/AuthKit redirect/callback URI matching the hosted app URL
- public frontend `VITE_WORKOS_CLIENT_ID`
- optional public frontend `VITE_WORKOS_REDIRECT_URI` when the redirect differs from the browser origin

Without those values, the local runtime stays fail-closed: unknown or incomplete identities cannot create local authorization, and browser users see the WorkOS/AuthKit configuration gate instead of fixture-backed normal runtime.
