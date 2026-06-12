# Environment and Configuration Inventory

Task: `TASK-ODR-01-001`

## Scope and sources reviewed

Reviewed the deployment/configuration-relevant root app sources named by the task:

- `AGENTS.md`, `README.md`, `.env.example`, `frontend/.env.example`
- `pom.xml`, `frontend/package.json`, `frontend/vite.config.ts`, `frontend/scripts/*`
- `docs/**`, `tools/**`
- `src/main/resources/**`, including `application.conf` and built static assets under `static-resources/`
- `src/main/java/ai/first/**`, focusing on auth, identity, invitation/email, model-provider, frontend-hosting, and protected API paths
- `frontend/src/**`, focusing on AuthKit setup, API clients, static-build/runtime behavior, and test-only boundaries

This is a docs-only survey. It does not change runtime behavior.

## Environment variables and runtime configuration

| Variable/config | Boundary | Used by | Required when | Default / fallback | Current failure behavior / notes |
|---|---|---|---|---|---|
| `ADMIN_USERS` | Backend-only bootstrap config; do not expose to browser/static assets | `BootstrapAdminSeeder`, `StarterSecurityComponents.bindAkkaRuntime`; documented in `.env.example`, `application.conf`, `README.md` | Normal production/default first-admin bootstrap on a clean app | Empty/absent means no configured admin is seeded by production bootstrap | Production/default bootstrap supports only `email:SAAS_OWNER_ADMIN:OWNER`. Tenant/customer admins must be created through governed org/invitation flows. Broad Maven checks should unset ambient `ADMIN_USERS` unless intentionally testing bootstrap. |
| `WORKOS_API_KEY` | Backend secret | `WorkosIdentityResolver`, `EnterpriseIdentityAdminService` | WorkOS user-profile lookup when JWT lacks email; enterprise IAM/SSO/SCIM readiness | Missing API key makes `WorkosIdentityResolver` return no provider profile; local authorization still owns access | Must never appear in `frontend/.env*`, `/api/me`, workstream surfaces, traces, or static assets. |
| `WORKOS_API_BASE_URL` | Backend config, non-secret unless it embeds credentials (must not) | `WorkosIdentityResolver` | Optional WorkOS API override for tests/proxies | `https://api.workos.com` | Used with `WORKOS_API_KEY` to fetch `/user_management/users/{id}`. |
| `WORKOS_CLIENT_ID` | Backend config according to `EnterpriseIdentityAdminService`; public client id when mirrored as `VITE_WORKOS_CLIENT_ID` for browser | `EnterpriseIdentityAdminService.REQUIRED_SECRET_NAMES`; `.env.example` currently emphasizes `VITE_WORKOS_CLIENT_ID` for frontend | Enterprise identity provider status/readiness checks | None | Inventory gap: backend enterprise readiness checks require `WORKOS_CLIENT_ID`, but `.env.example` only documents the browser-public `VITE_WORKOS_CLIENT_ID` as the client id. Next docs/validation task should reconcile whether backend should read `VITE_WORKOS_CLIENT_ID`, document both names, or adjust readiness wording. |
| `WORKOS_REDIRECT_URI` | Backend config according to `EnterpriseIdentityAdminService`; public redirect URI when mirrored as `VITE_WORKOS_REDIRECT_URI` for browser | `EnterpriseIdentityAdminService.REQUIRED_SECRET_NAMES`; `.env.example` currently emphasizes `VITE_WORKOS_REDIRECT_URI` | Enterprise identity provider status/readiness checks | None | Inventory gap: backend enterprise readiness checks require `WORKOS_REDIRECT_URI`, while frontend uses `VITE_WORKOS_REDIRECT_URI`. Next docs/validation task should reconcile/document both names. |
| `WORKOS_JWT_ISSUER` | Backend JWT validation config | Documented in `.env.example` and `application.conf` comments; Akka `@JWT` validates bearer tokens on protected endpoints | Production JWT validation | None found in source code; consumed by Akka runtime/config if supported by deployment configuration | Protected endpoints use `@JWT(validate = JWT.JwtMethodMode.BEARER_TOKEN)`. Need explicit operator docs for where Akka reads issuer/audience values and how to validate configured denial behavior. |
| `WORKOS_JWT_AUDIENCE` | Backend JWT validation config | Same as above | Production JWT validation | None found in source code; consumed by Akka runtime/config if supported by deployment configuration | Same gap as issuer: document concrete Akka/JWT config path or add validation in follow-up. |
| `VITE_WORKOS_CLIENT_ID` | Browser-public build-time config; embedded in frontend bundle | `frontend/src/main.tsx`, `frontend/.env.example`, `.env.example` | Real AuthKit browser runtime | Missing/placeholder value displays the frontend configure gate; no fixture mode in normal runtime | Must start with `client_` and not contain `your_workos`. Public value only; not a secret. Rebuild frontend after changing it. |
| `VITE_WORKOS_REDIRECT_URI` | Browser-public build-time config; embedded in frontend bundle | `frontend/src/main.tsx`, `frontend/.env.example`, `.env.example` | Optional real AuthKit redirect override | `window.location.origin` | Must match WorkOS/AuthKit application redirect configuration. Public value only. |
| `APP_PUBLIC_BASE_URL` | Backend config, public URL value | `InvitationService` constructor from `StarterSecurityComponents`; `.env.example`, `application.conf` comments | Invitation emails/acceptance links | Constructor receives `System.getenv("APP_PUBLIC_BASE_URL")`; invitation service decides behavior if absent | Used to build `/accept?token=...` links. Raw invitation token remains inside email delivery/acceptance boundary. |
| `RESEND_API_KEY` | Backend secret | `ResendEmailService`, `StarterSecurityComponents.invitationEmailDeliveryMode`, `.env.example`, static-asset scanner | Production email delivery/readiness | Missing in local/test defaults keeps captured/local mode unless production mode is explicitly requested | Production delivery fails closed with `resend-config-missing` if missing. Must never appear in browser/static assets/traces. |
| `RESEND_FROM_EMAIL` | Backend email sender config; may contain operational sender identity | `ResendEmailService`, `.env.example` | Production email delivery when `INVITE_EMAIL_FROM` absent | None | `ResendEmailService` accepts either `INVITE_EMAIL_FROM` or `RESEND_FROM_EMAIL`; missing both blocks production delivery. Static scan currently treats `RESEND_API_KEY`, not `RESEND_FROM_EMAIL`, as secret marker. |
| `INVITE_EMAIL_FROM` | Backend invite sender config; do not expose to browser | `ResendEmailService`, `StarterSecurityComponents.invitationEmailDeliveryMode`, `.env.example`, static-asset scanner | Production invitation email delivery when `RESEND_FROM_EMAIL` absent | Falls back to `RESEND_FROM_EMAIL` | Static scan treats this marker as backend-only to prevent sender config leakage in frontend assets. |
| `INVITE_EMAIL_SUBJECT` | Backend email template config | `ResendEmailService`, `.env.example` | Optional invitation email customization | `You're invited` | Safe to document as non-secret backend config. |
| `RESEND_API_BASE_URL` | Backend provider endpoint override | `ResendEmailService`, `.env.example` | Optional tests/proxies/custom endpoint | `https://api.resend.com` | Must not embed credentials. |
| `INVITATION_EMAIL_DELIVERY_MODE` | Backend operational mode | `StarterSecurityComponents.invitationEmailDeliveryMode` | Optional explicit email delivery mode | If unset, falls through to `EMAIL_DELIVERY_MODE`, then environment/config detection | Values `production`/`resend` force production Resend mode; `local`/`test`/`captured`/`capture` force captured mode. |
| `EMAIL_DELIVERY_MODE` | Backend operational mode | `StarterSecurityComponents.invitationEmailDeliveryMode` | Optional fallback explicit email delivery mode | If unset, mode derives from `APP_ENV`/`AKKA_ENVIRONMENT` and Resend config | Same accepted values as `INVITATION_EMAIL_DELIVERY_MODE`. |
| `APP_ENV` | Backend environment classification | `StarterSecurityComponents.invitationEmailDeliveryMode` | Optional production detection for email delivery | If unset, falls through to `AKKA_ENVIRONMENT` | When equal to `production`, invitation email mode is production even before checking Resend config. |
| `AKKA_ENVIRONMENT` | Backend/Akka environment classification | `StarterSecurityComponents.invitationEmailDeliveryMode`, `application.conf` comments | Optional production detection | If unset, email mode derives from presence of Resend config | When equal to `production`, invitation email mode is production. |
| `OPENAI_API_KEY` | Backend secret | Akka `application.conf` model provider, `OpenAiModelProviderClient`, `ModelProviderClient`, `.env.example`, static-asset scanner | Real model-backed agent invocation/provider smoke | Empty in `application.conf`; `ModelProviderClient` fails closed when invocation requested without it | Missing key produces actionable `model-provider-config-missing`; no deterministic/model-less success should be claimed. Must never appear in frontend/assets/API/traces. |
| `OPENAI_MODEL_ID` | Backend provider config | Akka `application.conf`, `ModelProviderClient`, `.env.example` | Optional model id override; required nonblank for model invocation | `gpt-4o-mini` | If explicitly blank/unresolved for invocation, `ModelProviderClient` treats model id as missing. |
| `OPENAI_API_BASE_URL` | Backend provider endpoint override | Akka `application.conf`, `ModelProviderClient`, `.env.example` | Optional tests/proxies/provider endpoint override | `https://api.openai.com/v1` | Safe summary redacts secret and includes endpoint. Must not embed credentials. |
| `OPENAI_REQUEST_TIMEOUT_SECONDS` | Backend model-provider timeout | `ModelProviderClient`, `.env.example` | Optional model invocation timeout override | 30 seconds; invalid/out-of-range values fall back to 30 seconds | Accepted parsed range is 1..120 seconds. |
| `akka.javasdk.agent.model-provider` / `openai-low-temperature` | Backend Akka config in `src/main/resources/application.conf` | `WorkstreamRuntimeAgent` via `ModelProvider.fromConfig(request.modelProviderAlias())` and Akka agent runtime | Akka Agent model provider runtime | Alias `openai-low-temperature`, provider `openai`, default model `gpt-4o-mini`, env-backed key/base URL | Akka config keeps API key empty unless `OPENAI_API_KEY` is supplied. Sampling is default-compatible despite alias name. |

## Protected API and auth boundary inventory

- Static frontend routes are public by ACL: `/`, `/favicon.ico`, `/assets/**`, `/ui`, `/workstream`, and `/accept` in `StarterFrontendEndpoint`.
- Protected browser APIs use `@JWT(validate = JWT.JwtMethodMode.BEARER_TOKEN)` and internet ACL, then resolve WorkOS claims to local authorization state:
  - `GET /api/me` in `MeEndpoint`.
  - `/api/workstream/**` in `WorkstreamEndpoint`, including bootstrap, functional agents, items, surfaces, actions, messages, invitation acceptance, and SSE events.
- Frontend token acquisition is through AuthKit `getAccessToken()` in `frontend/src/main.tsx`.
- `HttpApiClient` and workstream clients attach `Authorization: Bearer <token>`, `X-Selected-Context-Id`, and `X-Correlation-Id` headers.
- Browser configuration is limited to `VITE_WORKOS_CLIENT_ID` and `VITE_WORKOS_REDIRECT_URI`. The frontend renders a configure gate when the client id is missing or still placeholder-like; normal runtime does not include a fixture mode.
- `/api/me` and workstream surfaces must remain browser-safe: no raw JWTs, provider payloads, provider credentials, raw invitation tokens, hidden prompts, hidden capabilities, or cross-scope facts.

## Provider/model configuration inventory

- The governed agent model boundary is split between durable governed metadata (`AgentDefinition`, `ModelConfigRef`, `ModelPolicy`, traces) and deployment/runtime provider secrets.
- `AgentRuntimeService` uses `ModelProviderClient`; `OpenAiModelProviderClient` reads backend-only environment values at construction.
- `WorkstreamRuntimeAgent` uses Akka `ModelProvider.fromConfig(request.modelProviderAlias())`, with the default alias configured in `application.conf` as `openai-low-temperature`.
- Model provider failures are intentionally surfaced as `blocked_provider_or_runtime`/provider-configuration-required states and trace summaries, not as model-less normal success.
- Known docs/validation gap: operator docs should explain both model-provider paths (`application.conf` Akka Agent provider alias and `OpenAiModelProviderClient` environment path), which variables are required for each, and how missing config is expected to fail closed.

## Email/invitation configuration inventory

- Production email delivery uses Resend only through `ResendEmailService`.
- Local/test behavior is captured/local unless explicitly configured otherwise.
- `StarterSecurityComponents.invitationEmailDeliveryMode()` determines invitation email mode in this order:
  1. explicit `INVITATION_EMAIL_DELIVERY_MODE`;
  2. fallback `EMAIL_DELIVERY_MODE`;
  3. `APP_ENV=production` or `AKKA_ENVIRONMENT=production`;
  4. production if `RESEND_API_KEY` and either `INVITE_EMAIL_FROM` or `RESEND_FROM_EMAIL` are present;
  5. otherwise local/test captured mode.
- Missing production Resend config returns a safe failure (`resend-config-missing`) and logs missing backend variable names without secret values.
- Invitation links use `APP_PUBLIC_BASE_URL`; raw invitation tokens are only in the email delivery/acceptance boundary and must stay out of admin lists, traces, browser APIs, and static assets.

## Frontend static asset and build inventory

- Frontend project: React/Vite/TypeScript in `frontend/`.
- `npm --prefix frontend run build` runs `prebuild` first, then builds with:
  - `vite build --outDir ../src/main/resources/static-resources --emptyOutDir false`.
- `frontend/scripts/clean-static-assets.mjs` removes previous Vite `assets/` and `index.html`, preserving non-Vite files such as `favicon.ico`.
- Current built assets live under `src/main/resources/static-resources/` and are served by `StarterFrontendEndpoint`.
- Do not hand-edit generated files under `src/main/resources/static-resources/`; rebuild from `frontend/` source.
- Vite dev server proxies `/api` and `/ui` to `http://localhost:9000`.
- Static secret scan exists at `tools/scan-ai-first-saas-static-assets.sh`; it checks built assets for backend secret markers including `WORKOS_API_KEY`, `WORKOS_CLIENT_SECRET`, `RESEND_API_KEY`, `OPENAI_API_KEY`, `ANTHROPIC_API_KEY`, `INVITE_EMAIL_FROM`, `ADMIN_USERS`, private keys, `sk-*`, and `whsec_*`.

## Package and script inventory

Backend:

- `pom.xml` uses Akka Java SDK parent `3.6.0` and currently declares no app-specific dependencies beyond the parent baseline.
- Common backend validation from `README.md`: `mvn test`.

Frontend/package scripts:

- `dev`: `vite --host 0.0.0.0`
- `typecheck`: `tsc --noEmit`
- `prebuild`: `node scripts/clean-static-assets.mjs`
- `build`: `vite build --outDir ../src/main/resources/static-resources --emptyOutDir false`
- `analyze:bundle`: `node scripts/report-bundle-size.mjs`
- `smoke:user-admin-workstream`: `cd .. && env -u ADMIN_USERS mvn -q -Dtest=UserAdminBrowserWorkstreamSmokeTest test`
- `test`: `node --test src/*.test.mjs`

Root tooling:

- `tools/scan-ai-first-saas-static-assets.sh <static-resources-dir>` validates generated static assets for browser-visible backend secret markers.
- `tools/prove-workstream-icons-v0.sh` is unrelated to env/secrets but proves frontend/backend source contracts for v0 workstream icons.

## Existing documentation coverage

- `README.md` documents root app purpose, standard backend/frontend checks, User Admin hosted UI/workstream smoke, static build output, and high-level production-like local runtime smoke requirements.
- `.env.example` documents backend secrets/config and repeats public `VITE_` variables.
- `frontend/.env.example` documents only public browser-side WorkOS/AuthKit variables and explicitly warns not to add backend secrets.
- `src/main/resources/application.conf` comments summarize common env vars and model-provider fail-closed behavior.
- `docs/**` currently covers root docs guidance, extension zones, Java package boundaries, and upstream merge practice. It does not yet provide a dedicated operator env/secret guide or deployment runbook.

## Known gaps for follow-up tasks

1. **Dedicated env/secret operator docs are missing.** Next task should create or update docs to classify required/optional variables, local/test/prod behavior, backend-secret vs browser-public boundaries, and fail-closed expectations.
2. **WorkOS backend variable names need reconciliation.** `EnterpriseIdentityAdminService` checks `WORKOS_CLIENT_ID` and `WORKOS_REDIRECT_URI`, while the public frontend uses `VITE_WORKOS_CLIENT_ID` and `VITE_WORKOS_REDIRECT_URI`. Decide whether to document both, alias them in code, or adjust readiness checks.
3. **JWT issuer/audience validation path needs explicit documentation.** `.env.example` names `WORKOS_JWT_ISSUER` and `WORKOS_JWT_AUDIENCE`, but the concrete Akka configuration wiring should be documented/validated so operators know how protected endpoints fail closed.
4. **Email mode selection needs operator-facing examples.** The implicit production/local selection in `StarterSecurityComponents.invitationEmailDeliveryMode()` should be documented to avoid accidentally sending emails or accidentally staying in captured mode.
5. **Model provider readiness needs one consolidated description.** Operators need a concise map from `application.conf`/Akka provider alias and `OpenAiModelProviderClient` env variables to expected provider-missing states, optional credentialed smoke, and secret redaction boundaries.
6. **Static asset secret scan is available but not yet integrated into the standard README/runbook checks.** Consider adding it to deployment validation guidance after frontend build.
7. **No cloud-provider-specific deployment config is present.** This matches the mini-project non-goal; deployment docs should stay provider-neutral unless future tasks add provider-specific files.

## Handoff to `TASK-ODR-02-001`

Use this inventory as the source list for env/secret documentation and validation. The next task should not need to rediscover variable names or script behavior; it should turn the table above into operator-facing docs and, if practical, a lightweight validation script/test for missing/unsafe configuration and frontend secret boundaries.
