---
id: RV-MY-ACCOUNT-001-2026-06-29-blocked-auth-config
scenario: RV-MY-ACCOUNT-001
scenarioFile: specs/runtime-validation/scenarios/my-account/RV-MY-ACCOUNT-001-login-and-account-context.md
workstream: my-account
date: 2026-06-29
branch: main
baseCommit: ace12c98
result: blocked
blockerClassification:
  - auth-setup-blocker
  - runtime-validation-gap
  - seed-data-blocker
readinessConclusion: not-runtime-ready
---

# Runtime-validation run record: RV-MY-ACCOUNT-001

## Scope

Attempted to execute the My Account login/account-context runtime-validation scenario through the checked-in local Akka/API/UI start and seed path. The scenario was blocked before authenticated runtime execution because the local WorkOS/AuthKit JWT audience remained a placeholder at process start. The runtime-validation start and seed scripts are now present, but the app did not start, the base organization could not be seeded, and no browser login, `/api/me`, workstream surface, denial/open-disabled, browser-safe payload, or trace evidence was emitted.

## Environment and setup evidence

- Branch: `main`
- Base commit before this run record update: `ace12c98`
- Intended environment: `specs/runtime-validation/environments/local-dev.md`
- Intended setup: `specs/runtime-validation/data-setups/base-organization.md`
- Persona: `specs/runtime-validation/personas/member.md` (`member@example.com`)
- Start contract: `./tools/runtime-validation/start-local.sh --empty`
- Seed contract: `./tools/runtime-validation/seed.sh base-organization`
- Local seed env generated: `.runtime-validation/local.env` (ignored local file; token not recorded)

### Command evidence

```bash
set -o pipefail
./tools/runtime-validation/start-local.sh --empty 2>&1 | tee /tmp/rv-my-account-start.out
status=${PIPESTATUS[0]}
echo "START_EXIT_STATUS=$status"
```

Observed output:

```text
Runtime-validation auth config is missing or placeholder: WORKOS_JWT_AUDIENCE
Set real local WorkOS/AuthKit values in the process environment or .env before running RV-MY-ACCOUNT-001.
START_EXIT_STATUS=78
```

Follow-up readiness probe:

```bash
set -euo pipefail
printf 'runtime-validation files after start attempt:\n'
find .runtime-validation -maxdepth 2 -type f -printf '%p %m %s bytes\n' 2>/dev/null | sort || true
printf 'backend pid file: '
if [ -f .runtime-validation/backend.pid ]; then cat .runtime-validation/backend.pid; else echo absent; fi
printf 'localhost 9000 root status: '
curl -sS -o /tmp/rv-root.out -w '%{http_code}' http://localhost:9000/ --max-time 2 || true
printf '\n'
```

Observed output:

```text
runtime-validation files after start attempt:
.runtime-validation/local.env 600 261 bytes
backend pid file: absent
localhost 9000 root status: curl: (7) Failed to connect to localhost port 9000 after 0 ms: Could not connect to server
000
```

Seed setup attempt after the failed start:

```bash
set -o pipefail
./tools/runtime-validation/seed.sh base-organization 2>&1 | tee /tmp/rv-my-account-seed.out
status=${PIPESTATUS[0]}
echo "SEED_EXIT_STATUS=$status"
```

Observed output:

```text
curl: (7) Failed to connect to localhost port 9000 after 0 ms: Could not connect to server
SEED_EXIT_STATUS=7
```

Redacted configuration status:

```bash
python3 - <<'PY'
from pathlib import Path
import os
keys=['VITE_WORKOS_CLIENT_ID','VITE_WORKOS_REDIRECT_URI','WORKOS_JWT_ISSUER','WORKOS_JWT_AUDIENCE','ADMIN_USERS','RUNTIME_VALIDATION_SEED_TOKEN']
vals={}
p=Path('.env')
if p.exists():
    for line in p.read_text().splitlines():
        s=line.strip()
        if not s or s.startswith('#') or '=' not in s:
            continue
        k,v=s.split('=',1)
        vals[k]=v.strip().strip('"').strip("'")
local=Path('.runtime-validation/local.env')
if local.exists():
    for line in local.read_text().splitlines():
        s=line.strip()
        if not s or s.startswith('#') or '=' not in s:
            continue
        k,v=s.split('=',1)
        vals.setdefault(k, v.strip().strip('"').strip("'"))
print('env file:', 'present' if p.exists() else 'absent')
print('local runtime env:', 'present' if local.exists() else 'absent')
for k in keys:
    v=os.environ.get(k, vals.get(k,''))
    if not v:
        status='missing'
    elif any(token in v.lower() for token in ['your_', 'configured-workos', 'client_your', 'replace_with', 'xxxxxxxx']):
        status='placeholder'
    else:
        status='configured-redacted'
    src='process-env' if os.environ.get(k) else ('local-env' if k in vals and k.startswith('RUNTIME_') else ('.env/local' if k in vals else 'none'))
    print(f'{k}: {status} ({src})')
PY
```

Observed output:

```text
env file: present
local runtime env: present
VITE_WORKOS_CLIENT_ID: configured-redacted (process-env)
VITE_WORKOS_REDIRECT_URI: configured-redacted (process-env)
WORKOS_JWT_ISSUER: configured-redacted (process-env)
WORKOS_JWT_AUDIENCE: placeholder (process-env)
ADMIN_USERS: configured-redacted (process-env)
RUNTIME_VALIDATION_SEED_TOKEN: configured-redacted (local-env)
```

## Scenario execution status

| Required evidence item | Status | Evidence / blocker |
| --- | --- | --- |
| Login as `member@example.com` through WorkOS/AuthKit | blocked | `auth-setup-blocker`: `tools/runtime-validation/start-local.sh --empty` exited 78 because `WORKOS_JWT_AUDIENCE` was placeholder, so the browser/AuthKit login path was not opened. |
| `/api/me` protected API response | blocked | `runtime-validation-gap`: no Akka runtime was listening on `http://localhost:9000`, so `/api/me` was not called and no protected bearer-token validation was exercised. |
| Account context and tenant/organization scope | blocked | `seed-data-blocker`: `tools/runtime-validation/seed.sh base-organization` could not connect because the app did not start, so no member account/membership handoff was available. |
| Open-disabled / denial behavior | blocked | `seed-data-blocker`: disabled/inactive fixtures were not seeded because local runtime startup failed before seed setup. |
| Browser-safe payloads | blocked | No browser-visible `/api/me` or workstream payload was captured. Existing source/tests are not counted as this runtime-validation run's browser-safe evidence. |
| Trace evidence | blocked | No audit/work trace ids were emitted because login, `/api/me`, workstream surface reads, and denial/open-disabled actions did not execute. |

## Evidence matrix

| Claim | Intent source | Runtime path | Evidence | Level | Gap |
| --- | --- | --- | --- | --- | --- |
| Member login, `/api/me`, account context, denied/open-disabled recovery, browser-safe payloads, and trace ids for My Account | `RV-MY-ACCOUNT-001`, My Account source-alignment, runtime-validation corpus plan | `member -> WorkOS/AuthKit -> frontend -> /api/me and workstream endpoints -> account-context-and-profile capability -> identity/MyAccount services -> trace/result surfaces` | Start script and seed command evidence above. Runtime path was not started or exercised. | `described` | `auth-setup-blocker`; `runtime-validation-gap`; `seed-data-blocker`; no API/UI/trace evidence. |

## Result

- Scenario result: `blocked`
- Primary blocker: `auth-setup-blocker`
- Additional blockers: `runtime-validation-gap`, `seed-data-blocker`
- Readiness conclusion: My Account remains `partially-aligned` at source-evidence level and is not `manual-ready` or `runtime-ready` from this run.
- Follow-up needed: provide non-placeholder local WorkOS/AuthKit JWT audience (and matching test-user login/token setup), rerun `./tools/runtime-validation/start-local.sh --empty`, seed `base-organization`, then execute `RV-MY-ACCOUNT-001` through browser/API/Akka runtime and record sanitized `/api/me`, denial/open-disabled, payload, and trace evidence.
