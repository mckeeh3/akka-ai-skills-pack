# Capabilities Index

Capabilities are governed backend boundaries. Governed-tools live inside capability files and surface/action maps; do not create a separate top-level governed-tool directory by default.

| Capability id | File | Class | Primary callers | AuthContext / authority | Core workstream exposure |
| --- | --- | --- | --- | --- | --- |
| `secure-tenant-user-foundation` | `01-secure-tenant-user-foundation.md` | command, query, workflow, policy, audit | browser user, User Admin, My Account, support roles | account/member identity, tenant/customer scope, roles/capabilities, support grant where applicable | My Account, User Admin, `/api/me`, invitations, roles/memberships, audit |
| `ai-first-work-management` | `02-ai-first-work-management.md` | task, workflow, proposal, read/evidence | core functional agents and authorized internal workers | selected AuthContext, workstream role/capability, retained-human-authority gates | durable workstream requests, system messages, attention, internal/background worker follow-up |
| `governance-decisions-audit` | `03-governance-decisions-and-audit.md` | decision, approval, policy, trace, read/evidence | Governance/Policy, Audit/Trace, User Admin, Agent Admin | policy owner/reviewer/auditor/admin capability grants | approval gates, decision cards, denial traces, audit/trace surfaces |
| `frontend-shell-integration-patterns` | `04-frontend-shell-and-integration-patterns.md` | browser surface/query/action mapping | workstream shell, browser tools, frontend API clients | backend-issued browser-safe capability set only | left rail, composer, surfaces, deep links, realtime/projection refresh |
| `managed-agent-foundation` | `05-managed-agent-foundation.md` | command, query, proposal, trace, agent-tool | Agent Admin, governed runtime, authorized functional agents | agent steward/reviewer/admin/auditor grants, tool boundary, active manifests | AgentDefinition, prompts, skills, references, manifests, tool boundaries, loader tools, traces |

## Domain-specific expansion

Add CRM/SMB/domain capabilities with stable numeric prefixes after the core capabilities, for example:

```text
10-capabilities/
  20-crm-account-management.md
  21-crm-contact-management.md
  22-crm-opportunity-pipeline.md
  30-smb-invoicing.md
  31-smb-job-scheduling.md
```

Every new capability must record actors/callers, AuthContext, schemas, validation, safe denial shape, idempotency, side effects, policy/approval rules, audit/work traces, exposure surfaces, and tests.
