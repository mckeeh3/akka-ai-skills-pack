# Functional Agents

This scaffolded core app is modeled as an agent workstream application. User-facing application verticals are functional agents with durable workstreams; routes and pages are only deep-link or implementation details.

## Five core functional agents

| Functional agent | Purpose | Authorized roles/capabilities | Default surface | Callable capabilities | Trace/test obligations |
| --- | --- | --- | --- | --- | --- |
| `my-account-agent` | Current account, selected context, profile, settings, sign out, personal notifications, and safe self-service. The signed-in user tile at the bottom of the left rail opens this workstream; it is not duplicated in the top workstream list. | Any active signed-in member for own account and permitted contexts. | `my-account-dashboard` plus `markdown_response` | `secure-tenant-user-foundation`, `frontend-shell-integration-patterns` | `/api/me`, context selection, disabled-user denial, self-scope enforcement, request/response rendering, skill/reference load denial. |
| `user-admin-agent` | Invitations, users, memberships, roles/capabilities, access review, support-access visibility, and admin audit. | Tenant Admin, Customer Admin within scope, SaaS Owner support roles with explicit support grant. | `user-admin-dashboard` plus User Admin structured surfaces and `markdown_response` | `secure-tenant-user-foundation`, `managed-agent-foundation`, `governance-decisions-audit` | invite lifecycle, tenant isolation, last-admin protection, role denial, support-access checks, AdminAuditEvent and AgentWorkTrace coverage. |
| `agent-admin-agent` | Govern AgentDefinition records, prompts, skills, references, manifests, tool boundaries, lifecycle, behavior proposals, tests, and traces. | Agent Steward, Tenant Admin, Policy Owner, Reviewer, Auditor read-only where permitted. | `agent-governance-center` plus `markdown_response` | `managed-agent-foundation`, `governance-decisions-audit` | active/draft lifecycle, `readSkill(skillId)`, `readReferenceDoc(referenceId)`, prompt/skill/reference/tool-boundary trace coverage, authority-expansion denial tests. |
| `audit-trace-agent` | Search and explain identity, authorization, data access, tool use, decisions, workflows, denials, and outcomes. | Auditor, Tenant Admin, scoped Supervisor, SaaS Owner support roles with explicit support grant. | `audit-trace-explorer` plus `markdown_response` | `governance-decisions-audit`, `secure-tenant-user-foundation`, `managed-agent-foundation` | redaction, tenant/customer filtering, trace correlation, support-access audit, skill/reference/tool denial traces, export-denial tests. |
| `governance-policy-agent` | Manage policy concepts, approval gates, proposals, simulations/impact analysis, replay evidence, activation/rollback, and human review. | Policy Owner, Reviewer/Approver, Tenant Admin, Auditor read-only where permitted. | `agent-governance-center` / governance dashboard plus `markdown_response` | `governance-decisions-audit`, `managed-agent-foundation` | proposal approval, unauthorized authority expansion denial, simulation/impact trace tests, policy-text-cannot-grant-authority tests, skill/reference load denial. |

## Functional-agent rules

- The left rail lists only functional agents authorized by the selected `AuthContext` and backend capability grants.
- My Account is launched from the signed-in user tile/email at the bottom of the left rail.
- Selecting a functional agent opens its durable workstream and default dashboard/attention surface.
- Composer requests are interpreted in the selected functional-agent context and must resolve to capability-backed actions, evidence queries, proposals, or safe denials.
- Prompt intent and skill hints guide the agent; they do not grant authority. Backend capabilities and tool boundaries remain authoritative.
- Full core SaaS scope requires all five core agents above. Domain-specific features add new functional agents or extend existing ones only after capability/security semantics are captured.

## Domain-specific extension pattern

For CRM/SMB/domain features, add or update functional agents here before generating code. Examples:

- CRM Sales Agent owns accounts, contacts, opportunities, follow-up, and pipeline attention.
- Billing Agent owns estimates, invoices, payments, billing exceptions, and customer billing evidence.
- Operations Agent owns scheduling, jobs, dispatch, service delivery, and completion evidence.

Each new functional agent needs workstream icon metadata, dashboard/attention model, surface graph, governed capability map, expertise bundle, traces, and tests.
