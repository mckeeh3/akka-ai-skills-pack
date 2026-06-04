# Live WorkOS/AuthKit Provider Smoke

- task: TASK-FCSR-08-001
- date: 2026-06-04
- result: blocked after provider configuration check

## Scope

Attempted to unblock the live WorkOS/AuthKit provider smoke after backend/frontend provider environment variables were supplied.

No WorkOS API key, JWT, client id, issuer, audience, or other secret/config value is recorded in this artifact.

## Environment configuration status

The local shell contains the provider configuration required to configure the app boundary:

- `WORKOS_API_KEY` set, value hidden
- `WORKOS_API_BASE_URL` set, value hidden
- `WORKOS_JWT_ISSUER` set, value hidden
- `WORKOS_JWT_AUDIENCE` set, value hidden
- `VITE_WORKOS_CLIENT_ID` set, value hidden
- `VITE_WORKOS_REDIRECT_URI` set, value hidden
- `ADMIN_USERS` set, value hidden
- `APP_PUBLIC_BASE_URL` set, value hidden

## Remaining blocker

The task done criteria require a JWT-bearing `/api/me` and protected workstream API smoke against the real AuthKit/WorkOS provider boundary.

The local shell does not currently provide an actual AuthKit access token/session token that can be sent as the `Authorization: Bearer ...` value for the smoke. Checked token variable candidates:

- `WORKOS_ACCESS_TOKEN`: not set
- `AUTHKIT_ACCESS_TOKEN`: not set
- `WORKOS_TEST_ACCESS_TOKEN`: not set

Provider app/config environment is present, but it is not the same as a signed user access token. Without a real AuthKit access token for an admin user that is represented in local `ADMIN_USERS`/membership state, the smoke cannot prove the required JWT-bearing `/api/me` path.

## Required next input

Provide one backend/local-only AuthKit access token for a test/admin user, exported as one of:

```bash
export WORKOS_ACCESS_TOKEN='<redacted real AuthKit access token>'
# or
export AUTHKIT_ACCESS_TOKEN='<redacted real AuthKit access token>'
# or
export WORKOS_TEST_ACCESS_TOKEN='<redacted real AuthKit access token>'
```

The token subject/email must resolve to a local account/membership seeded by `ADMIN_USERS` or invitation/onboarding state, and the token must satisfy the configured WorkOS issuer/audience expected by the Akka JWT boundary.

## Intended smoke after token is supplied

Run a live provider smoke that starts the Akka local API, calls:

```text
GET /api/me
GET /api/workstream/bootstrap
```

with:

```text
Authorization: Bearer <real AuthKit access token>
X-Selected-Context-Id: <authorized membership/context id when required>
```

and verifies:

- `/api/me` returns browser-safe account, selected AuthContext, roles/capabilities, and functional-agent summaries;
- protected workstream bootstrap succeeds for the same token/context;
- wrong/omitted token behavior remains denied;
- WorkOS backend secrets and JWT values are not exposed in browser-safe responses, static frontend assets, workstream surfaces, or logs captured by the smoke artifact.

## Status

`TASK-FCSR-08-001` remains blocked until a real AuthKit access token/session token is supplied in addition to the already supplied provider configuration values.
