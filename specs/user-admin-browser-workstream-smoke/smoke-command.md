# User Admin Browser Workstream Smoke Command

## Command

Run the deterministic hosted User Admin workstream smoke from the repository root:

```bash
npm --prefix frontend run smoke:user-admin-workstream
```

The script delegates to:

```bash
env -u ADMIN_USERS mvn -q -Dtest=UserAdminBrowserWorkstreamSmokeTest test
```

## Integrated production-runtime hardening coverage

The smoke exercises Akka-served `/ui` assets and protected `/api/workstream` calls, then validates representative User Admin hardening paths through `/api/workstream/actions`:

- invitation create returns a browser-safe invitation detail surface with backend-authored delivery state, retry recovery routing, trace references, and no raw invitation tokens, provider message ids, Resend payloads, provider secrets, bearer tokens, or WorkOS provider payloads;
- identity recovery opens the durable exception surface, requests review, approves recovery, and completes recovery while keeping provider identities/JWT details redacted;
- access review start returns the typed `blocked_provider_or_runtime` task surface when model/provider/tool-boundary runtime prerequisites are unavailable, including safe model/tool/data/policy usage summaries, trace links, and explicit no-direct-mutation copy.

## Optional real-provider/model behavior

External WorkOS, Resend, and model-provider credentials are not required for this deterministic smoke. Without real provider/model credentials, production Resend and access-review agent paths remain fail-closed and are not treated as successful runtime behavior. Credentialed validation should be run only in an environment with backend-only secrets and approved model/provider configuration; this smoke intentionally skips those external calls when credentials are absent.
