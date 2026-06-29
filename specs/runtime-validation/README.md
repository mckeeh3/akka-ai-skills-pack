# Runtime-validation corpus

This corpus defines durable runtime-validation scenarios for the secure AI-first SaaS core app. It is a scenario scaffold only: no scenario in this directory has passed until a later execution task records evidence under `runs/`.

## Scope

The first scenario set covers the five refreshed foundation workstreams:

| Workstream | Scenario | Status |
| --- | --- | --- |
| My Account | `scenarios/my-account/RV-MY-ACCOUNT-001-login-and-account-context.md` | authored, not executed |
| User Admin | `scenarios/user-admin/RV-USER-ADMIN-001-invite-user.md` | authored, not executed |
| Agent Admin | `scenarios/agent-admin/RV-AGENT-ADMIN-001-provider-fail-closed-test-console.md` | authored, not executed |
| Governance/Policy | `scenarios/governance-policy/RV-GOVPOL-001-policy-decision-card.md` | authored, not executed |
| Audit/Trace | `scenarios/audit-trace/RV-AUDIT-001-trace-search-denial-redaction.md` | authored, not executed |

## Reusable setup documents

- Environment: `environments/local-dev.md`
- Personas: `personas/member.md`, `personas/organization-admin.md`, `personas/saas-admin.md`, `personas/support-operator.md`
- Data setup: `data-setups/base-organization.md`
- Run records: `runs/README.md`

## Execution rule

A runtime-validation run must record:

1. app start/reset command, commit, branch, and provider configuration state;
2. bootstrap and data setup evidence;
3. persona login/auth evidence through the real auth path;
4. UI/API/runtime observations for the scenario steps;
5. audit/work trace, result surface, denial, and fail-closed evidence;
6. pass/fail/blocked conclusion and remediation tasks when needed.

Do not mark a workstream `runtime-ready` from these scenario definitions alone.
