# Deployment environment and secret configuration

This guide is the operator-facing environment contract for the root AI-first SaaS core app. It classifies public browser configuration, backend-only secrets, local/test defaults, production expectations, and fail-closed behavior. Do not commit real `.env` files or credentials.

## Boundary rules

- **Browser-public values** use `VITE_` names and are embedded into the Vite bundle at build time. Only `VITE_WORKOS_CLIENT_ID` and `VITE_WORKOS_REDIRECT_URI` belong in `frontend/.env.local`.
- **Backend-only secrets and operational config** stay in the service environment or deployment secret manager. Never copy them into `frontend/.env*`, browser DTOs, static resources, prompts, traces, fixtures, or logs.
- After changing `VITE_` values, rebuild the frontend so Akka serves the intended static assets from `src/main/resources/static-resources/`.
- Run the static asset scan after a production build when validating secret boundaries:

```bash
npm --prefix frontend run build
tools/scan-ai-first-saas-static-assets.sh src/main/resources/static-resources
```

## Environment variables

| Name | Boundary | Required for production? | Local/test behavior | Fail-closed or safety behavior |
|---|---|---:|---|---|
| `VITE_WORKOS_CLIENT_ID` | Browser-public build-time config | Yes, for real AuthKit sign-in | Missing or placeholder value shows the frontend WorkOS configuration gate | Browser runtime does not switch to fixture auth in normal mode. |
| `VITE_WORKOS_REDIRECT_URI` | Browser-public build-time config | Optional when origin default is correct | Defaults in code to `window.location.origin` when absent | Must match WorkOS/AuthKit redirect settings for sign-in to complete. |
| `WORKOS_API_KEY` | Backend secret | Required when backend must fetch WorkOS user profiles or enterprise IAM status | Missing key means provider profile lookup is unavailable; local authorization still owns access | Must not leak to browser; unknown/unauthorized users remain denied by local account and membership state. |
| `WORKOS_API_BASE_URL` | Backend non-secret endpoint override | No | Defaults to `https://api.workos.com` | Do not embed credentials in the URL. |
| `WORKOS_CLIENT_ID` | Backend operational config for enterprise identity readiness checks | Required only for enterprise identity readiness/status paths that expect it | May be absent when those readiness checks are not used | Keep distinct from browser `VITE_WORKOS_CLIENT_ID`; same WorkOS app value may be supplied under both names when both browser sign-in and backend readiness are used. |
| `WORKOS_REDIRECT_URI` | Backend operational config for enterprise identity readiness checks | Required only for enterprise identity readiness/status paths that expect it | May be absent when those readiness checks are not used | Keep distinct from browser `VITE_WORKOS_REDIRECT_URI`; do not treat backend config as a secret, but do not rely on browser env to configure backend readiness. |
| `WORKOS_JWT_ISSUER` | Backend JWT validation config | Yes, for protected API token validation | Tests can use Akka/TestKit JWT fixtures and do not require real WorkOS | Protected `@JWT` APIs deny missing/invalid bearer tokens. Use values from the same WorkOS environment as the AuthKit client. |
| `WORKOS_JWT_AUDIENCE` | Backend JWT validation config | Yes, for protected API token validation | Same as issuer | Protected `@JWT` APIs deny tokens with the wrong or missing audience. |
| `ADMIN_USERS` | Backend-only first-admin bootstrap allowlist | Required for normal production bootstrap of the first SaaS Owner on a clean app | Broad automated checks should unset ambient `ADMIN_USERS` unless explicitly testing bootstrap | Only `email:SAAS_OWNER_ADMIN:OWNER` entries are supported for production/default bootstrap. Tenant/customer admins must be created later through governed organization and invitation flows. |
| `APP_PUBLIC_BASE_URL` | Backend public URL config | Required for production invitation links | Local example: `http://localhost:9000/` | Raw invite tokens are appended only inside the email delivery/acceptance boundary and must not appear in admin lists, traces, or general browser APIs. |
| `RESEND_API_KEY` | Backend secret | Required for production Resend delivery | Local/dev/test should use captured delivery by default | Production delivery fails closed with a safe configuration error when required Resend settings are missing. |
| `RESEND_FROM_EMAIL` | Backend sender config | Required for production delivery unless `INVITE_EMAIL_FROM` is set | Optional in captured mode | Missing sender plus production mode blocks delivery; value must not be embedded in frontend assets. |
| `INVITE_EMAIL_FROM` | Backend invitation sender config | Required for production invitation email when `RESEND_FROM_EMAIL` is absent | Optional in captured mode | Missing sender plus production mode blocks delivery; static scan treats this as backend-only. |
| `INVITE_EMAIL_SUBJECT` | Backend invitation template config | No | Defaults to `You're invited` | Non-secret backend config. |
| `RESEND_API_BASE_URL` | Backend provider endpoint override | No | Defaults to `https://api.resend.com` | Do not embed credentials in the URL. |
| `INVITATION_EMAIL_DELIVERY_MODE` | Backend email mode selector | Optional but recommended for explicit production/captured behavior | Values such as `local`, `test`, `captured`, or `capture` force captured mode | Values `production` or `resend` force production mode and require Resend config. |
| `EMAIL_DELIVERY_MODE` | Backend fallback email mode selector | Optional | Used when `INVITATION_EMAIL_DELIVERY_MODE` is unset | Same values and fail-closed rules as invitation-specific mode. |
| `APP_ENV` | Backend environment classifier | Recommended in production | If unset, email mode falls through to other signals | `production` forces production email mode. |
| `AKKA_ENVIRONMENT` | Backend/Akka environment classifier | Recommended in production | If unset, email mode falls through to other signals | `production` forces production email mode. |
| `OPENAI_API_KEY` | Backend model-provider secret | Required for real model-backed agent invocation | Tests and non-provider smoke do not require it | Model invocation fails closed with actionable provider-configuration-required errors when missing; no model-less normal success is claimed. |
| `OPENAI_MODEL_ID` | Backend model-provider config | Optional unless overriding default | Defaults to `gpt-4o-mini` | Blank/unresolved values block model invocation when a real provider call is requested. |
| `OPENAI_API_BASE_URL` | Backend model-provider endpoint override | No | Defaults to `https://api.openai.com/v1` | Do not embed credentials in the URL. |
| `OPENAI_REQUEST_TIMEOUT_SECONDS` | Backend model-provider timeout | No | Defaults to 30 seconds | Invalid or out-of-range values fall back safely to 30 seconds. |

## WorkOS/AuthKit and protected APIs

WorkOS proves user identity; the Akka app owns authorization. Public static routes may be served without auth, but `/api/me` and `/api/workstream/**` require JWT bearer validation and local account/membership authorization. Missing, invalid, expired, or wrong-audience tokens must be denied. A valid WorkOS identity without an active local account, membership, or selected authorized scope is also denied or placed in a safe recovery state.

`WORKOS_CLIENT_ID`/`WORKOS_REDIRECT_URI` are backend readiness/status variables. `VITE_WORKOS_CLIENT_ID`/`VITE_WORKOS_REDIRECT_URI` are browser build-time variables. When a deployment uses both browser AuthKit and backend enterprise identity readiness, configure both pairs deliberately from the same WorkOS application/environment.

## First-admin bootstrap caveat

`ADMIN_USERS` is a bootstrap allowlist for a clean app, not a tenant-admin shortcut. Production/default startup supports SaaS Owner bootstrap entries only:

```text
email@example.com:SAAS_OWNER_ADMIN:OWNER
```

Tenant/customer admins are created later through governed organization, membership, and invitation flows. Unknown sign-ins must not self-register into privileged access. Because developer shells may contain an incompatible `ADMIN_USERS` value, broad Maven checks should usually run with it unset:

```bash
env -u ADMIN_USERS mvn test
```

## Email delivery modes

Invitation email delivery chooses mode in this order:

1. `INVITATION_EMAIL_DELIVERY_MODE` when set;
2. `EMAIL_DELIVERY_MODE` when set;
3. production mode when `APP_ENV=production` or `AKKA_ENVIRONMENT=production`;
4. production mode when `RESEND_API_KEY` and either `INVITE_EMAIL_FROM` or `RESEND_FROM_EMAIL` are present;
5. captured/local mode otherwise.

Use captured mode for local/dev/test. Use production mode only with Resend credentials and a verified sender. Missing production Resend config must produce safe not-ready or delivery-failed states without leaking secret values.

## Model provider configuration

There are two model-provider paths to understand:

- Akka Agent runtime configuration in `src/main/resources/application.conf` uses the safe alias `openai-low-temperature`, with `OPENAI_API_KEY`, `OPENAI_MODEL_ID`, and `OPENAI_API_BASE_URL` supplied from backend environment variables.
- Foundation runtime helpers such as `OpenAiModelProviderClient` also resolve backend-only OpenAI environment variables and report safe provider status/errors.

Provider aliases and model ids are safe to show as redacted operational summaries. API keys and credential-bearing URLs are never safe for browser APIs, traces, model-visible content, or static assets. Missing provider configuration blocks real model-backed invocation with actionable fail-closed states such as provider configuration required.

## Production-oriented validation checklist

Before a production-like smoke, verify:

```bash
# no backend secrets are embedded in the browser build
npm --prefix frontend run build
tools/scan-ai-first-saas-static-assets.sh src/main/resources/static-resources

# standard checks with accidental bootstrap config removed unless testing bootstrap itself
env -u ADMIN_USERS mvn test
npm --prefix frontend test -- --run
npm --prefix frontend run typecheck
```

Then validate through the real hosted UI/API path with deployment credentials: AuthKit sign-in, `/api/me`, selected workstreams, authorization denials, invitation email behavior, model-provider fail-closed or credentialed success behavior, and audit/work-trace visibility.
