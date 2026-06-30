---
id: local-dev
title: Local development runtime
environmentType: local-empty
executionStatus: scaffold-not-run
---

# Purpose

Defines the intended local runtime environment for the first runtime-validation corpus. The environment starts from empty or reset local persistence, boots the app, prepares only scenario prerequisites, and validates through the real browser/API/Akka path.

# Start contract

Use the checked-in local runtime-validation start script:

```bash
./tools/runtime-validation/start-local.sh --empty
```

The script loads `.env` when present, defaults `APP_AUTH_MODE=local-dev` and `VITE_APP_AUTH_MODE=local-dev`, enables the local-only runtime-validation seed endpoint, writes a private seed token to `.runtime-validation/local.env`, builds the frontend with `npm --prefix frontend run build`, starts the Akka dev runtime with `mvn clean compile exec:java -Dakka.runtime.http-interface=0.0.0.0` on `http://localhost:9000`, and waits for the HTTP endpoint to become reachable before returning. Use `--foreground` when a human wants to keep the server in the current terminal.

In local-dev auth mode, manual testers sign in through the local-only browser panel with a seeded test email such as `saas.admin@example.test`, `org1.admin1@example.test`, `org1.user3@example.test`, `cust1.admin@example.test`, or `cust1.user2@example.test`. The panel obtains a local bearer token from `/api/dev/auth/sign-in`, then the browser still calls `/api/me` and the normal protected workstream APIs. WorkOS/AuthKit configuration is only required when `APP_AUTH_MODE` is set to a WorkOS-backed mode.

Stop a background runtime started by the script with:

```bash
./tools/runtime-validation/stop-local.sh
```

Use `./tools/runtime-validation/stop-local.sh --force` only when graceful shutdown does not finish.

If the start command fails, the run record must capture the exit status, `.runtime-validation/logs/backend.log` path when present, the missing/placeholder auth keys reported by the script, and any differences from this contract.

# Expected local endpoints

- Frontend: local app URL printed by the start command.
- Protected API: backend URL printed by the start command.
- Workstream UI/API path: browser surface actions must call protected API or workstream endpoints rather than fixture-only data.

# Provider and secret state

- `APP_AUTH_MODE=local-dev` is the default for local runtime-validation runs and uses seeded local passwordless emails for browser auth. WorkOS/AuthKit configuration is required only for WorkOS-backed browser-auth scenarios; if such a scenario lacks config, the run records an `auth/setup gap`.
- Resend and model provider configuration may be intentionally absent for fail-closed scenarios.
- Browser-visible assets and API payloads must not expose WorkOS, Resend, OpenAI, or model provider secrets.
- Provider-missing behavior must fail closed with actionable operator-visible messages and trace evidence.

# Evidence to record in runs

- Git commit and branch.
- Start command, exit status, and local URLs.
- Persistence reset/empty-start proof.
- Provider configuration state as configured, missing, or intentionally withheld.
- Auth/test-user mapping state, including selected local-dev email or WorkOS test user subject mapping.
- Logs or trace ids for bootstrap/setup failures.
