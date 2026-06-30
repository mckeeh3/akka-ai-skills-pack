---
id: RV-MY-ACCOUNT-001-2026-06-30-blocked-auth-config
scenario: RV-MY-ACCOUNT-001
scenarioFile: specs/runtime-validation/scenarios/my-account/RV-MY-ACCOUNT-001-login-and-account-context.md
workstream: my-account
date: 2026-06-30
branch: main
baseCommit: 8eed38d6
result: blocked
blockerClassification:
  - auth-setup-blocker
  - runtime-validation-gap
  - seed-data-blocker
readinessConclusion: not-runtime-ready
---

# Runtime-validation run record: RV-MY-ACCOUNT-001

## Scope

Retried the My Account login/account-context runtime-validation scenario after local runtime-validation tooling fixes. The scenario remained blocked before authenticated runtime execution because the effective local WorkOS/AuthKit JWT audience is still a placeholder. The checked-in start/seed scripts were present and runnable, but the app did not start on `http://localhost:9000`, the base organization could not be seeded, and no browser login, `/api/me`, account-context, denial/open-disabled, browser-safe payload, or trace evidence was captured.

## Environment and setup evidence

- Branch: `main`
- Base commit before this run record update: `8eed38d6`
- Intended environment: `specs/runtime-validation/environments/local-dev.md`
- Intended setup: `specs/runtime-validation/data-setups/base-organization.md`
- Persona: `specs/runtime-validation/personas/member.md` (`member@example.com`)
- Start contract: `tools/runtime-validation/start-local.sh --empty`
- Seed contract: `tools/runtime-validation/seed.sh base-organization`
- Local seed env generated: `.runtime-validation/local.env` (ignored local file; token not recorded)

### Command evidence

Start attempt:

```bash
tools/runtime-validation/start-local.sh --empty
```

Observed output:

```text
Runtime-validation auth config is missing or placeholder: WORKOS_JWT_AUDIENCE
Set real local WorkOS/AuthKit values in the process environment or .env before running RV-MY-ACCOUNT-001.
```

Observed exit status: `78`.

Cleanup command:

```bash
tools/runtime-validation/stop-local.sh
```

Observed output:

```text
No runtime-validation backend pid file found at .runtime-validation/backend.pid
```

Follow-up readiness probe:

```bash
set -euo pipefail
printf 'runtime-validation files after start attempt:\n'
find .runtime-validation -maxdepth 2 -type f -printf '%p %m %s bytes\n' 2>/dev/null | sort || true
printf 'backend pid file: '
if [ -f .runtime-validation/backend.pid ]; then cat .runtime-validation/backend.pid; else echo absent; fi
printf 'localhost 9000 root status: '
curl -sS -o /tmp/rv-my-account-root.out -w '%{http_code}' http://localhost:9000/ --max-time 2 || true
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
tools/runtime-validation/seed.sh base-organization --wait-seconds 4
```

Observed output:

```text
runtime-validation backend is not reachable at http://localhost:9000/ after 4s
Start it with tools/runtime-validation/start-local.sh, or inspect .runtime-validation/logs/backend.log.
```

Observed exit status: `7`.

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
| Login as `member@example.com` through WorkOS/AuthKit | blocked | `auth-setup-blocker`: `tools/runtime-validation/start-local.sh --empty` exited 78 because `WORKOS_JWT_AUDIENCE` is placeholder in the effective runtime environment, so the browser/AuthKit login path was not opened. |
| `/api/me` protected API response | blocked | `runtime-validation-gap`: no Akka runtime was listening on `http://localhost:9000`, so `/api/me` was not called and protected bearer-token validation was not exercised. |
| Account context and tenant/organization scope | blocked | `seed-data-blocker`: `tools/runtime-validation/seed.sh base-organization --wait-seconds 4` could not connect because the app did not start, so no member account/membership handoff was available. |
| Open-disabled / denial behavior | blocked | `seed-data-blocker`: disabled/inactive fixtures were not seeded because local runtime startup failed before seed setup. |
| Browser-safe payloads | blocked | No browser-visible `/api/me` or workstream payload was captured. Existing source/tests are not counted as this runtime-validation run's browser-safe evidence. |
| Trace evidence | blocked | No audit/work trace ids were emitted because login, `/api/me`, workstream surface reads, and denial/open-disabled actions did not execute. |

## Evidence matrix

| Claim | Intent source | Runtime path | Evidence | Level | Gap |
| --- | --- | --- | --- | --- | --- |
| Member login, `/api/me`, account context, denied/open-disabled recovery, browser-safe payloads, and trace ids for My Account | `RV-MY-ACCOUNT-001`, My Account source-alignment, runtime-validation corpus plan | `member -> WorkOS/AuthKit -> frontend -> /api/me and workstream endpoints -> account-context-and-profile capability -> identity/MyAccount services -> trace/result surfaces` | Start script, readiness probe, cleanup, seed command, and redacted config evidence above. Runtime path was not started or exercised. | `described` | `auth-setup-blocker`; `runtime-validation-gap`; `seed-data-blocker`; no API/UI/trace evidence. |

## Result

- Scenario result: `blocked`
- Primary blocker: `auth-setup-blocker`
- Additional blockers: `runtime-validation-gap`, `seed-data-blocker`
- Readiness conclusion: My Account remains `partially-aligned` at source-evidence level and is not `manual-ready` or `runtime-ready` from this run.
- Follow-up needed: provide non-placeholder local WorkOS/AuthKit JWT audience and matching test-user/token setup, rerun `tools/runtime-validation/start-local.sh --empty`, seed `base-organization`, then execute `RV-MY-ACCOUNT-001` through browser/API/Akka runtime and record sanitized `/api/me`, denial/open-disabled, browser-safe payload, and trace evidence.
