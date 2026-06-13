# Health and readiness diagnostics

Task: `TASK-ODR-03-001`

This document defines the smallest deployment-readiness diagnostic contract for the root AI-first SaaS core app. It is intentionally provider-neutral and avoids adding unauthenticated runtime status APIs that could leak tenant, customer, account, invitation, audit, prompt, model, or provider facts.

## Diagnostic posture

- Public unauthenticated routes prove only that the HTTP service can serve the packaged frontend shell and static assets.
- Protected APIs prove authentication/JWT wiring and local authorization only when called with a real or TestKit-issued bearer token and selected context.
- Workstream readiness must be validated through `/api/workstream/**` or the hosted UI, not by bypassing `AuthContextResolver` or durable repositories.
- External provider readiness is reported as configured/not-configured behavior at the relevant feature boundary. Missing provider secrets must be distinguishable from healthy configured readiness, but secrets themselves must never be printed.
- A future machine-readable health endpoint, if added, should expose only coarse platform status such as `ready`, `not_ready`, or `degraded` plus safe reason codes. It must not include tenant/customer ids, account emails, raw JWT claims, invitation tokens, prompt/reference text, model prompts, provider request payloads, provider keys, or audit event bodies.

## Baseline checks

Run these checks from the repository root unless noted otherwise.

| Area | What to run or inspect | Healthy configured signal | Missing or not-ready signal | Secret/data boundary |
|---|---|---|---|---|
| Startup/runtime wiring | `env -u ADMIN_USERS mvn -q -Dtest=WorkstreamServiceTest test` | Workstream service tests pass; runtime seams still bind concrete Akka-backed workstream agent invoker, workstream log, audit trace, and protected endpoint construction paths. | Test failure identifies broken startup, authorization, workstream, trace, or provider-fail-closed contract before deployment. | Use `env -u ADMIN_USERS` for broad checks unless intentionally validating first-admin bootstrap. Do not print configured admin allowlists. |
| Frontend static shell | `npm --prefix frontend run build` then `tools/scan-ai-first-saas-static-assets.sh src/main/resources/static-resources` | Vite assets and `index.html` are rebuilt under `src/main/resources/static-resources`; static scan finds no backend secret markers. | Build failure means the hosted UI shell is not ready. Static scan failure means deployment must stop until backend-only config is removed from browser assets. | Browser bundle may contain only public `VITE_WORKOS_CLIENT_ID` and `VITE_WORKOS_REDIRECT_URI`; never backend secrets. |
| Public asset serving | `GET /`, `/ui`, `/workstream`, `/accept`, `/favicon.ico`, and `/assets/**` against the running service | Routes return the packaged React shell/assets through `StarterFrontendEndpoint`. | Missing/404/500 responses indicate packaged static assets or service routing are not ready. | These routes are public by design and must not reveal local auth state or provider configuration. |
| Auth/JWT edge | Call `GET /api/me` without a bearer token and then with a valid WorkOS/TestKit bearer token. | Missing/invalid token is denied; valid token reaches local account/membership resolution. | Invalid token, wrong issuer/audience, disabled account, or missing membership is denied or returned as a safe recovery state. | Do not log raw bearer tokens or raw JWT claims. WorkOS identity proves identity only; local authorization still owns access. |
| Workstream API | With a valid bearer token and selected context, call `GET /api/workstream/bootstrap` and at least one scoped surface/action path used by the smoke fixture. | Response contains authorized functional agents, visible capabilities, selected context, trace ids, and browser-safe surface payloads. | Unknown user, forbidden selected context, missing role/capability, or suspended scope returns safe denial/recovery behavior. | Payloads must not include raw invitation tokens, hidden capabilities, prompts, provider secrets, or cross-scope facts. |
| Workstream SSE | With the same selected context, connect to `GET /api/workstream/events` or `GET /api/workstream/events?functionalAgentId=...`. | Authorized stream opens and is scoped to the selected tenant/customer context; reconnect can use `Last-Event-ID` or `lastEventId`. | Unauthorized callers are denied; malformed/obsolete event ids should be treated as a deployment bug to fix before relying on live refresh. | Stream rows are refresh hints/workstream events only for the selected scope. |
| Email/invitation | Validate `INVITATION_EMAIL_DELIVERY_MODE`/`EMAIL_DELIVERY_MODE`, `APP_ENV`/`AKKA_ENVIRONMENT`, `RESEND_API_KEY`, and `INVITE_EMAIL_FROM` or `RESEND_FROM_EMAIL` against `docs/deployment-env-secrets.md`. Exercise invitation create/resend in captured mode for local/test and, only with real credentials, production mode. | Local/test captures delivery without sending. Production mode sends through Resend and records provider message id/status through the invitation/email boundary. | Production mode without `RESEND_API_KEY` or sender config fails closed with safe `resend-config-missing` behavior. | Raw invitation tokens stay only in the email delivery/acceptance boundary; Resend keys and provider payloads never appear in browser APIs or traces. |
| Model provider | Validate `OPENAI_API_KEY`, `OPENAI_MODEL_ID`, `OPENAI_API_BASE_URL`, and `OPENAI_REQUEST_TIMEOUT_SECONDS`. Exercise model-backed workstream messages only when the deployment is intended to use real providers. | Configured invocation reaches the governed model/provider boundary and records safe provider/model alias summaries and traces. | Missing `OPENAI_API_KEY` or unusable model config returns actionable provider-configuration-required/fail-closed states; it must not produce model-less normal success. | Provider aliases and model ids are safe summaries; API keys, prompts with hidden data, and provider request/response payloads are not browser-safe. |
| Audit/work trace | In authorized workstream/admin flows, inspect surfaces or repository-backed tests that include trace ids/audit rows. | Allowed and denied consequential actions produce audit/work-trace facts with correlation ids and safe summaries. | Missing trace/audit evidence for protected actions is not deployment-ready for operational investigation. | Audit rows may expose only authorized scoped facts and redacted summaries; no secrets, raw tokens, hidden prompts, or cross-tenant data. |

## Readiness states

Use these deployment-facing states when summarizing diagnostics in runbooks, smoke output, or future status DTOs.

| State | Meaning | Example safe reason codes |
|---|---|---|
| `ready` | The checked area is configured and its runtime path completed successfully. | `frontend-assets-ready`, `auth-jwt-accepted`, `workstream-bootstrap-ready`, `resend-configured`, `model-provider-configured` |
| `not_ready` | Required local configuration, built assets, auth setup, provider config, or durable runtime binding is missing or invalid. | `frontend-assets-missing`, `jwt-config-missing`, `resend-config-missing`, `model-provider-config-missing`, `runtime-binding-unavailable` |
| `degraded` | Core service is running but an optional or credentialed external dependency is not available for a feature that can safely remain disabled. | `enterprise-identity-readiness-unconfigured`, `model-provider-smoke-skipped`, `email-production-smoke-skipped` |
| `denied` | The diagnostic intentionally exercised a forbidden or unauthenticated path and received a safe denial. | `missing-token-denied`, `context-forbidden`, `membership-disabled` |

Do not use `ready` for deterministic/demo/model-less behavior when the production feature requires a real provider, real authorization, durable traces, or production email delivery.

## Operator sequence

1. Confirm environment variables using `docs/deployment-env-secrets.md`; keep browser-public `VITE_` values separate from backend secrets.
2. Build and scan frontend assets:

   ```bash
   npm --prefix frontend run build
   tools/scan-ai-first-saas-static-assets.sh src/main/resources/static-resources
   ```

3. Run focused backend readiness regression:

   ```bash
   env -u ADMIN_USERS mvn -q -Dtest=WorkstreamServiceTest test
   ```

4. Start the service with intended local/test/prod environment values.
5. Verify public shell routes return static assets and protected `/api/me` denies missing/invalid bearer tokens.
6. Sign in through AuthKit or use the approved TestKit/smoke fixture, then validate `/api/me`, `/api/workstream/bootstrap`, one scoped surface/action, and `/api/workstream/events`.
7. Exercise email and model provider boundaries according to deployment intent:
   - local/test: captured email and safe provider-missing states are acceptable when documented;
   - production-like: Resend and model credentials must be configured before claiming email/model readiness.
8. Confirm audit/work-trace evidence is available for allowed and denied protected actions using correlation ids, without inspecting or exporting secrets.

## Endpoint implementation note

No new health endpoint is required for this task. Existing routes and focused tests provide the readiness contract without introducing a public diagnostic surface. If a later task adds `/api/health` or `/api/readiness`, prefer an internal-only or tightly scoped protected endpoint with coarse status codes and redacted reason codes only.
