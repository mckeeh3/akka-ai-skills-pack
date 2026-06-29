---
id: RV-MY-ACCOUNT-001-2026-06-29-blocked-auth-seed
scenario: RV-MY-ACCOUNT-001
scenarioFile: specs/runtime-validation/scenarios/my-account/RV-MY-ACCOUNT-001-login-and-account-context.md
workstream: my-account
date: 2026-06-29
branch: main
baseCommit: 4f8383f3
result: blocked
blockerClassification:
  - auth-setup-blocker
  - seed-data-blocker
readinessConclusion: not-runtime-ready
---

# Runtime-validation run record: RV-MY-ACCOUNT-001

## Scope

Attempted to execute the My Account login/account-context runtime-validation scenario through the local Akka/API/UI path. The scenario was precisely blocked before runtime execution because the local runtime-validation start/seed contracts are absent and the WorkOS JWT audience in `.env` is still placeholder, so the required WorkOS/AuthKit member login and browser bearer-token validation cannot be completed safely.

## Environment and setup evidence

- Branch: `main`
- Base commit before this run record: `4f8383f3`
- Intended environment: `specs/runtime-validation/environments/local-dev.md`
- Intended setup: `specs/runtime-validation/data-setups/base-organization.md`
- Persona: `specs/runtime-validation/personas/member.md` (`member@example.com`)
- Preferred start contract: `./tools/runtime-validation/start-local.sh --empty`
- Preferred seed contract: `./tools/runtime-validation/seed.sh base-organization`

### Command evidence

```bash
set +e
printf 'start-local contract: '
if [ -x ./tools/runtime-validation/start-local.sh ]; then echo present; ./tools/runtime-validation/start-local.sh --empty; else echo missing; fi
printf 'base-organization seed contract: '
if [ -x ./tools/runtime-validation/seed.sh ]; then echo present; ./tools/runtime-validation/seed.sh base-organization; else echo missing; fi
python3 - <<'PY'
from pathlib import Path
p=Path('.env')
print('env file:', 'present' if p.exists() else 'absent')
if p.exists():
    vals={}
    for line in p.read_text().splitlines():
        s=line.strip()
        if not s or s.startswith('#') or '=' not in s: continue
        k,v=s.split('=',1)
        vals[k]=v.strip().strip('"').strip("'")
    for k in ['VITE_WORKOS_CLIENT_ID','VITE_WORKOS_REDIRECT_URI','WORKOS_JWT_ISSUER','WORKOS_JWT_AUDIENCE','ADMIN_USERS']:
        v=vals.get(k)
        if v is None or not v:
            status='missing'
        elif any(token in v.lower() for token in ['your_', 'configured-workos', 'xxxxxxxx', 'sk_test_or', 'client_your']):
            status='placeholder'
        else:
            status='configured-redacted'
        print(f'{k}: {status}')
PY
```

Observed output:

```text
start-local contract: missing
base-organization seed contract: missing
env file: present
VITE_WORKOS_CLIENT_ID: configured-redacted
VITE_WORKOS_REDIRECT_URI: configured-redacted
WORKOS_JWT_ISSUER: configured-redacted
WORKOS_JWT_AUDIENCE: placeholder
ADMIN_USERS: configured-redacted
```

## Scenario execution status

| Required evidence item | Status | Evidence / blocker |
| --- | --- | --- |
| Login as `member@example.com` through WorkOS/AuthKit | blocked | `auth-setup-blocker`: backend JWT audience remains placeholder, so real AuthKit bearer-token validation is not configured for this local run. |
| `/api/me` protected API response | blocked | Not called. A browser/API call would not be valid runtime evidence without configured WorkOS JWT validation and seeded member account/membership state. |
| Account context and tenant/organization scope | blocked | `seed-data-blocker`: `./tools/runtime-validation/seed.sh base-organization` is absent and no approved equivalent seed path was available in this task. |
| Open-disabled / denial behavior | blocked | `seed-data-blocker`: no disabled/inactive member fixture could be created or recorded through the required seed setup. |
| Browser-safe payloads | blocked | No `/api/me` or workstream payload was captured because the scenario did not reach authenticated API/UI execution. Existing source/tests are not counted as this run's browser-safe runtime evidence. |
| Trace evidence | blocked | No runtime trace ids were emitted because the scenario did not execute through local auth/API/UI. |

## Evidence matrix

| Claim | Intent source | Runtime path | Evidence | Level | Gap |
| --- | --- | --- | --- | --- | --- |
| Member login, `/api/me`, account context, denied/open-disabled recovery, browser-safe payloads, and trace ids for My Account | `RV-MY-ACCOUNT-001`, My Account source-alignment, runtime-validation corpus plan | `member -> WorkOS/AuthKit -> frontend -> /api/me and workstream endpoints -> account-context-and-profile capability -> identity/MyAccount services -> trace/result surfaces` | Blocker command output above. Runtime path was not started or exercised. | `described` | `auth-setup-blocker`; `seed-data-blocker`; no run-time API/UI/trace evidence. |

## Result

- Scenario result: `blocked`
- Blocker classification: `auth-setup-blocker`, `seed-data-blocker`
- Readiness conclusion: My Account remains `partially-aligned` at source-evidence level and is not `manual-ready` or `runtime-ready` from this run.
- Follow-up needed: provide executable local runtime-validation start/seed tooling or an approved equivalent, configure real WorkOS/AuthKit JWT validation values for local test users, seed `member@example.com` plus an optional disabled/inactive member fixture, then rerun `RV-MY-ACCOUNT-001` through browser/API/Akka runtime and record payload/trace evidence.
