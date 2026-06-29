---
id: local-dev
title: Local development runtime
environmentType: local-empty
executionStatus: scaffold-not-run
---

# Purpose

Defines the intended local runtime environment for the first runtime-validation corpus. The environment starts from empty or reset local persistence, boots the app, prepares only scenario prerequisites, and validates through the real browser/API/Akka path.

# Start contract

A later execution task should record the concrete command used. The preferred command contract is:

```bash
./tools/runtime-validation/start-local.sh --empty
```

If that command is not available, the run record must name the project-specific backend/frontend start commands, local URLs, persistence reset steps, and any differences from this contract.

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
