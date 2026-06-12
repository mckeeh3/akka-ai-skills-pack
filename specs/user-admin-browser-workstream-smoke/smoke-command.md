# User Admin Browser Workstream Smoke Command

## Command

Run the deterministic User Admin hosted UI/workstream smoke from the repository root:

```bash
npm --prefix frontend run smoke:user-admin-workstream
```

The script delegates to:

```bash
env -u ADMIN_USERS mvn -q -Dtest=UserAdminBrowserWorkstreamSmokeTest test
```

## What it validates

The smoke is a Maven/TestKit hosted-app smoke, not a frontend fixture-only test. It exercises Akka-served `/ui` static assets and protected `/api/workstream` calls with deterministic test identity data, then traverses the User Admin surface graph:

1. hosted shell loads from Akka static resources;
2. authorized bootstrap selects the deterministic Tenant Admin context;
3. User Admin dashboard is visible for `agent-user-admin`;
4. dashboard action opens User Directory;
5. user row detail opens read-only inspection/task-router content;
6. invitation create task surface opens through the workstream action path;
7. hidden invitation access returns typed `system_message` denial instead of fake success;
8. browser-facing payloads are checked for raw token, provider-secret, bearer-token, and unsafe provider/model identifier leakage.

## Prerequisites and caveats

- Run from a clean checkout with backend test dependencies available.
- External WorkOS, Resend, and model-provider credentials are not required. The test seeds deterministic test-only accounts, tenant membership, and selected `AuthContext` data.
- The command unsets `ADMIN_USERS` intentionally. Inherited `ADMIN_USERS` values can perturb foundation bootstrap tests and must not be used to create Tenant/Customer admin bootstrap behavior for this smoke.
- The smoke does not claim production AuthKit sign-in, Resend delivery, or model-backed agent execution. Those provider-backed paths remain fail-closed unless explicitly configured and validated through their own runtime checks.
- No screenshots or browser reports are produced. Maven Surefire output remains under the standard `target/` test-report locations and is not committed.

## Troubleshooting

- If the script cannot find `mvn`, install/use the project's Maven-compatible Java build environment and retry from the repository root.
- If stale frontend assets are suspected, run `npm --prefix frontend run build` before the smoke. The smoke verifies hosted shell asset references but does not rebuild assets automatically.
- If a failure mentions `ADMIN_USERS`, rerun with the scripted command so the variable is unset for this deterministic smoke.
- If provider credentials are missing, that is expected; this smoke should not require them.
