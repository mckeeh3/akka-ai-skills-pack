---
id: RV-MY-ACCOUNT-001-2026-06-30-blocked-auth-login
scenario: RV-MY-ACCOUNT-001
scenarioFile: specs/runtime-validation/scenarios/my-account/RV-MY-ACCOUNT-001-login-and-account-context.md
workstream: my-account
date: 2026-06-30
branch: main
baseCommit: 881dba28
result: blocked
blockerClassification:
  - auth-setup-blocker
  - runtime-validation-gap
readinessConclusion: not-runtime-ready
---

# Runtime-validation run record: RV-MY-ACCOUNT-001

## Scope

Retried the My Account login/account-context runtime-validation scenario after the local runtime-validation start preflight stopped treating `WORKOS_JWT_AUDIENCE` as required. The local Akka runtime started on `http://localhost:9000`, frontend assets were served, and `base-organization` seeded the member, disabled-member, and inactive-member fixtures. The scenario remained blocked at the WorkOS/AuthKit login and bearer-token handoff: this non-browser execution did not have an interactive WorkOS test-user session, test-user credentials, or minted valid bearer token to exercise authenticated `/api/me`, workstream surfaces, account context, disabled/inactive denial, browser-safe authenticated payloads, or trace evidence.

## Environment and setup evidence

- Branch: `main`
- Base commit before this run record update: `881dba28`
- Intended environment: `specs/runtime-validation/environments/local-dev.md`
- Intended setup: `specs/runtime-validation/data-setups/base-organization.md`
- Persona: `specs/runtime-validation/personas/member.md` (`member@example.com`)
- Start contract: `tools/runtime-validation/start-local.sh --empty`
- Seed contract: `tools/runtime-validation/seed.sh base-organization`
- Local seed env generated/used: `.runtime-validation/local.env` (ignored local file; token not recorded)
- Provider secret state: not inspected or recorded; no WorkOS, Resend, OpenAI, model-provider, or seed token secret value is included in this record.

### Command evidence

Start command:

```bash
./tools/runtime-validation/start-local.sh --empty
```

Observed result: exit status `0`.

Observed output excerpt:

```text
Building frontend assets.
...
Starting Akka runtime in background: mvn clean compile exec:java -Dakka.runtime.http-interface=0.0.0.0
Runtime-validation backend pid: 3343191
Frontend/API URL: http://localhost:9000
Seed env: .runtime-validation/local.env
Log: .runtime-validation/logs/backend.log
```

Seed command:

```bash
./tools/runtime-validation/seed.sh base-organization
```

Observed result: exit status `0`.

Sanitized observed response excerpt:

```json
{
  "setupId": "base-organization",
  "result": "seeded",
  "tenant": {"tenantId": "tenant-starter", "present": true, "displayName": "Starter Tenant"},
  "personas": [
    {"accountId": "member@example.com", "scopeType": "TENANT", "status": "ACTIVE", "membershipId": "membership-member@example.com", "roles": ["TENANT_EMPLOYEE"], "tenantId": "tenant-starter", "supportAccess": false},
    {"accountId": "disabled.member@example.com", "scopeType": "TENANT", "status": "ACTIVE", "membershipId": "membership-disabled.member@example.com", "roles": ["TENANT_EMPLOYEE"], "tenantId": "tenant-starter", "supportAccess": false},
    {"accountId": "inactive.member@example.com", "scopeType": "TENANT", "status": "SUSPENDED", "membershipId": "membership-inactive.member@example.com", "roles": ["TENANT_EMPLOYEE"], "tenantId": "tenant-starter", "supportAccess": false}
  ],
  "disabledFixture": "disabled.member@example.com",
  "inactiveFixture": "inactive.member@example.com",
  "traceRefs": ["runtime-validation-base-organization-seed"],
  "authMapping": "Accounts are seeded by normalized email and link to WorkOS subject on first valid AuthKit login; no raw JWT or provider secret is returned.",
  "redaction": "Browser-safe setup metadata only; no WorkOS API key, JWT, Resend key, model key, or invitation token is exposed."
}
```

Runtime/API pre-auth probes:

```bash
set -euo pipefail
printf 'HEAD '; git rev-parse --short HEAD
printf 'branch '; git branch --show-current
printf 'root status '; curl -sS -o /tmp/rv-root.html -w '%{http_code}' http://localhost:9000/ --max-time 5; printf '\n'
printf 'api me no bearer status '; curl -sS -o /tmp/rv-me-no-bearer.out -w '%{http_code}' http://localhost:9000/api/me --max-time 5; printf '\n'
printf 'api me invalid bearer status '; curl -sS -o /tmp/rv-me-invalid.out -w '%{http_code}' -H 'Authorization: Bearer invalid.runtime.validation.token' -H 'X-Correlation-Id: rv-my-account-invalid-token-20260630' http://localhost:9000/api/me --max-time 5; printf '\n'
printf 'workstream bootstrap no bearer status '; curl -sS -o /tmp/rv-workstream-no-bearer.out -w '%{http_code}' http://localhost:9000/api/workstream/bootstrap --max-time 5; printf '\n'
```

Observed output:

```text
HEAD 881dba28
branch main
root status 200
api me no bearer status 400
api me invalid bearer status 403
workstream bootstrap no bearer status 400
root contains app marker yes
no bearer body excerpt Bearer token authorization header missing
invalid bearer body excerpt Failed to parse JWT token: Expected token [invalid.runtime.validation.token] to be composed of 2 or 3 parts separated by dots.
```

Cleanup command:

```bash
./tools/runtime-validation/stop-local.sh
```

Observed result: exit status `0`.

Observed output:

```text
Stopping runtime-validation backend pid 3343191
Runtime-validation backend stopped
```

## Scenario execution status

| Required evidence item | Status | Evidence / blocker |
| --- | --- | --- |
| Login as `member@example.com` through WorkOS/AuthKit | blocked | `auth-setup-blocker`: local frontend was reachable, but this execution had no interactive WorkOS/AuthKit browser session, test-user credentials, or valid bearer token handoff for `member@example.com`. |
| `/api/me` protected API response | blocked | `auth-setup-blocker`: unauthenticated `/api/me` returned `400` (`Bearer token authorization header missing`) and invalid bearer returned `403` parse failure, proving the protected boundary was present but not an authenticated member context. |
| Account context and tenant/organization scope | blocked | `auth-setup-blocker`: base seed created `member@example.com` in `tenant-starter`, but `/api/me` account-context payload could not be retrieved without a valid AuthKit bearer token. |
| Open-disabled / denial behavior | blocked | `auth-setup-blocker`: disabled and inactive fixtures were seeded, but their login or bearer-token mapping could not be exercised without WorkOS/AuthKit test-user authentication. |
| Browser-safe payloads | blocked | `runtime-validation-gap`: only frontend root and setup/pre-auth error payloads were observed. No authenticated browser-visible `/api/me`, workstream surface, or denied/open-disabled payload was captured. |
| Trace evidence | partially blocked | Seed emitted `runtime-validation-base-organization-seed`; no account-context read, workstream surface read, or denial/open-disabled trace ids were emitted because authenticated scenario actions did not execute. |

## Evidence matrix

| Claim | Intent source | Runtime path | Evidence | Level | Gap |
| --- | --- | --- | --- | --- | --- |
| Local runtime and setup are available for My Account runtime validation | `local-dev.md`, `base-organization.md`, `RV-MY-ACCOUNT-001` | `start-local -> Akka runtime/frontend -> seed endpoint -> base organization/persona fixtures` | Start command exit 0, root HTTP 200 with app marker, seed command exit 0 with member/disabled/inactive fixtures and setup trace ref. | `api-smoked` for setup only | None for setup; scenario auth remains blocked. |
| Member login, `/api/me`, account context, denied/open-disabled recovery, browser-safe authenticated payloads, and trace ids for My Account | `RV-MY-ACCOUNT-001`, My Account source-alignment, runtime-validation corpus plan | `member -> WorkOS/AuthKit -> frontend -> /api/me and workstream endpoints -> account-context-and-profile capability -> identity/MyAccount services -> trace/result surfaces` | Protected API pre-auth probes returned missing/invalid bearer errors; no valid WorkOS/AuthKit member session or bearer token was available. | `described` for authenticated scenario; not `manual-ready` or `runtime-ready` | `auth-setup-blocker`; `runtime-validation-gap` for missing authenticated API/UI/trace evidence. |

## Result

- Scenario result: `blocked`
- Primary blocker: `auth-setup-blocker`
- Additional gap: `runtime-validation-gap` for the unexecuted authenticated API/UI/trace portions.
- Readiness conclusion: My Account remains `partially-aligned` at source-evidence level and is not `manual-ready` or `runtime-ready` from this run.
- Follow-up needed: run `RV-MY-ACCOUNT-001` in a browser-capable/human session with WorkOS/AuthKit test-user credentials or a valid test-token handoff for `member@example.com`, `disabled.member@example.com`, and/or `inactive.member@example.com`; then record sanitized `/api/me`, account-context, denied/open-disabled, browser-safe payload, and trace evidence.
