# Runtime-validation Corpus Plan

This file starts as a planning scaffold for `TASK-ADIA-01-002`.

## Target structure

```text
specs/runtime-validation/
  README.md
  environments/local-dev.md
  personas/member.md
  personas/organization-admin.md
  personas/saas-admin.md
  personas/support-operator.md
  data-setups/base-organization.md
  scenarios/my-account/RV-MY-ACCOUNT-001-login-and-account-context.md
  scenarios/user-admin/RV-USER-ADMIN-001-invite-user.md
  scenarios/agent-admin/RV-AGENT-ADMIN-001-provider-fail-closed-test-console.md
  scenarios/governance-policy/RV-GOVPOL-001-policy-decision-card.md
  scenarios/audit-trace/RV-AUDIT-001-trace-search-denial-redaction.md
  runs/README.md
```

## Scenario authoring rule

Scenario files should document the intended real runtime path, setup prerequisites, steps, expected results, evidence to capture, and failure classification hints. They must not claim a run passed until a later runtime-validation execution task records evidence under `runs/`.

## Minimum first scenario set

- My Account: login, `/api/me`, account context, denial/open-disabled behavior.
- User Admin: invite user, invitation result/audit, forbidden non-admin denial.
- Agent Admin: safe test-console/provider-missing fail-closed, loader/tool-boundary denial evidence.
- Governance/Policy: policy proposal/decision-card approval or explicit current-blocking gap.
- Audit/Trace: trace search/detail, denied trace read, redaction/support-scope behavior.
