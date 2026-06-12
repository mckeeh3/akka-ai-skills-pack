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

## Invitation delivery coverage

The smoke exercises Akka-served `/ui` assets and protected `/api/workstream` calls, then creates an invitation through `/api/workstream/actions`. The returned invitation detail surface must include browser-safe delivery state, retry recovery routing, trace references, and no raw invitation tokens, provider message ids, Resend payloads, provider secrets, bearer tokens, or WorkOS provider payloads.

## Real-provider behavior

External WorkOS, Resend, and model-provider credentials are not required for this deterministic smoke. Without real provider credentials, production Resend/model paths remain fail-closed and are not treated as successful runtime behavior. Real-provider validation should be run only in a credentialed environment with backend-only secrets; this smoke intentionally skips that path when credentials are absent.
