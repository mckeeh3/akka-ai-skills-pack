# Functional Agents

This seed app is modeled as an agent workstream application. These user-facing functional agents are the primary authenticated application verticals; routes and pages are only deep-link or implementation details.

| Functional agent | Purpose | Authorized roles/capabilities | Default surface | Callable capabilities | Trace/test obligations |
|---|---|---|---|---|---|
| `my-account-agent` | Current account, selected context, profile, settings, sign out, and safe self-service. The signed-in user tile at the bottom of the left rail opens this workstream. | Any active signed-in member for own account and permitted contexts. | `my-account-dashboard` | `secure-tenant-user-foundation`, `frontend-shell-integration-patterns` | `/api/me`, context selection, disabled-user denial, profile/settings audit where consequential, request/response surface rendering. |
| `user-admin-agent` | Invitations, users, memberships, roles/capabilities, access review, support-access visibility, and admin audit. | Tenant Admin, Customer Admin within scope, SaaS Owner support roles with explicit support grant. | `user-admin-dashboard` with `user-admin-user-list` and `user-admin-user-account` as the canonical list/detail surfaces | `secure-tenant-user-foundation`, `governance-decisions-audit` | invite lifecycle, tenant isolation, last-admin protection, role denial, AdminAuditEvent coverage. |
| `agent-admin-agent` | Govern AgentDefinition records, prompts, skills, manifests, tool boundaries, lifecycle, behavior proposals, tests, and traces. | Agent Steward, Tenant Admin, Policy Owner, Reviewer, Auditor read-only where permitted. | `agent-governance-center` | `managed-agent-foundation`, `governance-decisions-audit` | active/draft lifecycle, `readSkill(skillId)` authorization, prompt/skill/tool-boundary trace coverage. |
| `mission-control-agent` | Supervise active goals, plans, delegated work, exceptions, approvals, and outcome signals. | Supervisor, Reviewer/Approver, Outcome Owner, Auditor read-only where permitted. | `mission-control-briefing` | `ai-first-work-management`, `governance-decisions-audit` | workstream timeline rendering, approval queue, policy-triggered exception, outcome link tests. |
| `governance-policy-agent` | Manage policies, approval gates, proposals, simulations, replay evidence, and activation/rollback. | Policy Owner, Reviewer/Approver, Tenant Admin, Auditor read-only where permitted. | `policy-governance-workbench` | `governance-decisions-audit`, `managed-agent-foundation` | proposal approval, unauthorized authority expansion denial, simulation/replay trace tests. |
| `audit-trace-agent` | Search and explain identity, authorization, data access, tool use, decisions, workflows, and outcomes. | Auditor, Tenant Admin, scoped Supervisor, SaaS Owner support roles with explicit support grant. | `audit-trace-explorer` | `governance-decisions-audit`, `secure-tenant-user-foundation`, `managed-agent-foundation` | redaction, tenant/customer filtering, trace correlation, export-denial tests. |

## Functional-agent rules

- The left rail lists only functional agents authorized by the selected `AuthContext` and backend capability grants.
- Selecting a functional agent opens its durable workstream and default dashboard/attention surface.
- Composer requests are interpreted in the selected functional-agent context and must resolve to capability-backed actions, evidence queries, proposals, or safe denials.
- Prompt intent and skill hints guide the agent; they do not grant authority. Backend capabilities and tool boundaries remain authoritative.
- Full core SaaS scope requires `user-admin-agent` and `agent-admin-agent`; removing either changes the realization scope to a narrower deferred foundation.
