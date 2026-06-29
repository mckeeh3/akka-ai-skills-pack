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

The script loads `.env` when present, validates that WorkOS/AuthKit public/JWT values are not placeholders, enables the local-only runtime-validation seed endpoint, writes a private seed token to `.runtime-validation/local.env`, and starts the Akka dev runtime with `mvn compile exec:java` on `http://localhost:9000`. Use `--foreground` when a human wants to keep the server in the current terminal.

If this command fails, the run record must capture the exit status, `.runtime-validation/logs/backend.log` path when present, the missing/placeholder auth keys reported by the script, and any differences from this contract.

# Expected local endpoints

- Frontend: local app URL printed by the start command.
- Protected API: backend URL printed by the start command.
- Workstream UI/API path: browser surface actions must call protected API or workstream endpoints rather than fixture-only data.

# Provider and secret state

- WorkOS/AuthKit configuration is required for browser-auth scenarios unless the run records an `auth/setup gap`.
- Resend and model provider configuration may be intentionally absent for fail-closed scenarios.
- Browser-visible assets and API payloads must not expose WorkOS, Resend, OpenAI, or model provider secrets.
- Provider-missing behavior must fail closed with actionable operator-visible messages and trace evidence.

# Evidence to record in runs

- Git commit and branch.
- Start command, exit status, and local URLs.
- Persistence reset/empty-start proof.
- Provider configuration state as configured, missing, or intentionally withheld.
- Auth/test-user mapping state.
- Logs or trace ids for bootstrap/setup failures.
