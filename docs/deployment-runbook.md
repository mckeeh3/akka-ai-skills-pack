# Deployment runbook and smoke checklist

This runbook is the operator-facing deployment baseline for the secure AI-first SaaS core app. It complements [`docs/deployment-env-secrets.md`](deployment-env-secrets.md) and [`specs/operational-deployment-readiness/health-readiness-diagnostics.md`](../specs/operational-deployment-readiness/health-readiness-diagnostics.md).

Do not commit real credentials, `.env` files, bearer tokens, WorkOS payloads, Resend payloads, raw invitation tokens, model prompts, or provider responses.

## 1. Pre-deployment environment setup

1. Classify values by boundary:
   - Browser-public build-time values: `VITE_WORKOS_CLIENT_ID`, `VITE_WORKOS_REDIRECT_URI`.
   - Backend-only secrets/config: WorkOS backend/JWT values, `ADMIN_USERS`, `APP_PUBLIC_BASE_URL`, Resend settings, email mode settings, and OpenAI/model-provider settings.
2. Configure first-admin bootstrap only for a clean production bootstrap:

   ```text
   ADMIN_USERS=email@example.com:SAAS_OWNER_ADMIN:OWNER
   ```

   `ADMIN_USERS` is not a tenant-admin shortcut. Broad local checks should remove accidental shell values unless intentionally testing bootstrap:

   ```bash
   env -u ADMIN_USERS mvn test
   ```

3. Keep production email in Resend mode only when `RESEND_API_KEY` and `INVITE_EMAIL_FROM` or `RESEND_FROM_EMAIL` are configured. Use captured/local mode for default local and automated smoke runs.
4. Configure model-provider credentials only in backend runtime configuration. Missing `OPENAI_API_KEY` must produce provider-configuration-required/fail-closed behavior, not a model-less success path.

## 2. Build and static-asset validation

Run from the repository root:

```bash
npm --prefix frontend run build
tools/scan-ai-first-saas-static-assets.sh src/main/resources/static-resources
```

Expected pass state:

- Vite emits static assets under `src/main/resources/static-resources/`.
- Static scan finds no backend-only secrets or provider markers in packaged browser assets.

Stop deployment if the build fails or the scan reports backend secrets in static assets.

## 3. Default local confidence smoke

Run the deterministic hosted User Admin workstream smoke:

```bash
npm --prefix frontend run smoke:user-admin-workstream
```

The command delegates to:

```bash
env -u ADMIN_USERS mvn -q -Dtest=UserAdminBrowserWorkstreamSmokeTest test
```

Expected pass state:

- Akka-served `/ui` assets load through the hosted app path.
- Protected `/api/workstream` calls use the intended workstream/API path.
- Invitation create surfaces show safe delivery state and trace references without raw invitation tokens, provider message ids, Resend payloads, provider secrets, bearer tokens, or WorkOS provider payloads.
- Identity recovery surfaces and actions exercise the durable exception workflow with redacted provider/JWT details.
- Access review surfaces show `blocked_provider_or_runtime` when model/provider/tool-boundary prerequisites are unavailable.

Expected local skip/fail-closed states:

- Real WorkOS, Resend, and model-provider calls are not required for this deterministic smoke.
- Missing provider/model credentials are acceptable only when the UI/API reports skipped or fail-closed readiness; do not treat absent credentials as successful production behavior.

## 4. Startup and route checks

After starting the service with intended environment values, verify:

1. Public shell/static routes return the packaged frontend and assets:
   - `GET /`
   - `GET /ui`
   - `GET /workstream`
   - `GET /accept`
   - `GET /favicon.ico`
   - `GET /assets/**`
2. `GET /api/me` without a bearer token is denied.
3. With a valid WorkOS/TestKit bearer token, `/api/me` reaches local account and membership resolution.
4. With a valid selected context, `/api/workstream/bootstrap` returns authorized functional agents, visible capabilities, trace ids, and browser-safe surface payloads.
5. `/api/workstream/events` opens only for an authorized selected tenant/customer context.

Never log raw bearer tokens, JWT claims, provider payloads, invitation tokens, prompts, or backend secrets while performing these checks.

## 5. Optional credentialed external-provider smoke

Run this only in an environment with approved backend-only credentials.

### WorkOS/AuthKit

- Sign in through the real AuthKit client configured by `VITE_WORKOS_CLIENT_ID` and `VITE_WORKOS_REDIRECT_URI`.
- Confirm backend JWT settings (`WORKOS_JWT_ISSUER`, `WORKOS_JWT_AUDIENCE`) match the same WorkOS environment.
- Verify unknown, disabled, wrong-context, or missing-membership users are denied or placed in a safe recovery state.

### Resend invitation email

- Use production/resend email mode only with `RESEND_API_KEY` and a verified sender.
- Create or resend an invitation through the User Admin path.
- Confirm provider delivery status is visible only as redacted message/status summaries through authorized admin/operator surfaces.
- Confirm raw invitation tokens remain only inside the delivery/acceptance boundary.

Expected not-ready state: production email mode without required Resend config must fail closed with a safe configuration error such as `resend-config-missing`.

### Model-backed workstream behavior

- Configure `OPENAI_API_KEY` and optional safe model settings in backend runtime configuration.
- Exercise model-backed workstream messages only when the deployment is intended to use real providers.
- Confirm traces show safe model/provider aliases, policy decisions, and fail/fallback summaries without API keys, hidden prompts, provider request payloads, or provider responses.

Expected not-ready state: missing or unusable model config returns provider-configuration-required/fail-closed behavior. It must not produce a normal model-less success.

## 6. Rollback checklist

If validation fails after a deployment:

1. Preserve correlation ids, safe status codes, and redacted logs for investigation.
2. Roll back to the last known-good service build and static asset bundle.
3. Restore the last known-good backend environment/secret set from the deployment secret manager.
4. Re-run:

   ```bash
   npm --prefix frontend run smoke:user-admin-workstream
   ```

5. Re-run static asset scan if browser build inputs changed:

   ```bash
   npm --prefix frontend run build
   tools/scan-ai-first-saas-static-assets.sh src/main/resources/static-resources
   ```

6. Do not mask provider or model configuration failures by enabling fixture/demo behavior in normal runtime.

## 7. Troubleshooting quick reference

| Symptom | Likely cause | Operator action |
|---|---|---|
| Static UI missing or stale | Frontend build not regenerated or assets not packaged | Run `npm --prefix frontend run build`; verify `src/main/resources/static-resources/`; scan assets before deploy. |
| Static scan finds secret markers | Backend-only config leaked into browser build | Stop deploy; remove secret from `VITE_`/frontend paths; rebuild and rescan. |
| Broad Maven checks fail locally with bootstrap errors | Ambient `ADMIN_USERS` contains an unsupported Tenant Admin entry | Re-run broad checks with `env -u ADMIN_USERS ...`; test production SaaS Owner bootstrap separately. |
| `/api/me` accepts missing/invalid auth | JWT edge regression | Stop deploy; protected APIs must deny missing/invalid bearer tokens. |
| Valid WorkOS user cannot access workstream | Missing/disabled local account, membership, role, or selected context | Inspect authorized admin surfaces and audit traces; do not bypass local authorization. |
| Invitation production delivery fails | Resend mode missing API key or sender; sender/domain unverified | Configure `RESEND_API_KEY` and verified `INVITE_EMAIL_FROM`/`RESEND_FROM_EMAIL`; keep captured mode for local/test. |
| Access review shows `blocked_provider_or_runtime` | Model/provider/tool-boundary prerequisites unavailable | Configure approved model/provider and governed tool boundaries before claiming model-backed readiness. |
| Audit/work trace missing for protected action | Trace/audit regression | Treat as not deployment-ready for operational investigation; fix before release. |

## 8. Release smoke summary template

```text
Deployment build:
Environment: local | staging | production
ADMIN_USERS caveat handled: yes/no; command used:
Static build and scan: pass/fail; notes:
User Admin workstream smoke: pass/fail; notes:
Public route checks: pass/fail; notes:
/api/me auth denial and authorized path: pass/fail; notes:
Workstream bootstrap/action/events: pass/fail; notes:
Resend credentialed smoke: skipped/captured/pass/fail; safe reason:
Model-provider smoke: skipped/pass/fail; safe reason:
Audit/work-trace evidence: pass/fail; correlation ids:
Rollback required: yes/no; action taken:
```
