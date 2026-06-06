# Functional Agents

This SaaS Foundation App description is modeled as a workstream application. Each user-facing workstream is backed by exactly one role-authorized functional/context-area agent. Routes, pages, endpoints, Akka components, and frontend components realize these contracts; they are not the primary application model.

## Five core functional agents

| Functional agent | Workstream id | Purpose | Authorized roles/capabilities | Default surface | Callable capability families | Readiness baseline |
|---|---|---|---|---|---|---|
| `my-account-agent` | `my-account` | Current account, selected context, profile, settings, sign out, personal queue, and safe self-service. Launched from the signed-in user tile/email, not the top rail. | Any active signed-in member for own account and permitted contexts. | `my-account-dashboard` | `secure-tenant-user-foundation`, `frontend-shell-integration-patterns`, `governance-decisions-audit` | surface-ready template; runtime readiness depends on target implementation. |
| `user-admin-agent` | `user-admin` | Invitations, users, memberships, roles/capabilities, access review, support-access visibility, and admin audit. | Tenant Admin, Customer Admin within scope, SaaS Owner support roles with explicit support grant, Auditor read-only where permitted. | `user-admin-dashboard` | `secure-tenant-user-foundation`, `managed-agent-foundation`, `governance-decisions-audit` | surface-ready template; expertise and runtime must be verified. |
| `agent-admin-agent` | `agent-admin` | Govern AgentDefinition records, prompts, skills, references, manifests, tool boundaries, lifecycle, behavior proposals, tests, and traces. | Agent Steward, Tenant Admin, Policy Owner, Reviewer, Auditor read-only where permitted. | `agent-governance-center` | `managed-agent-foundation`, `governance-decisions-audit` | surface-ready template; expertise and runtime must be verified. |
| `audit-trace-agent` | `audit-trace` | Search and explain identity, authorization, data access, tool use, decisions, workflows, denials, and outcomes. | Auditor, Tenant Admin, scoped Supervisor, SaaS Owner support roles with explicit support grant. | `audit-trace-explorer` | `governance-decisions-audit`, `secure-tenant-user-foundation`, `managed-agent-foundation` | surface-ready template; expertise and runtime must be verified. |
| `governance-policy-agent` | `governance-policy` | Manage policy concepts, approval gates, proposals, simulations/impact analysis, replay evidence, activation/rollback, and human review. | Policy Owner, Reviewer/Approver, Tenant Admin, Auditor read-only where permitted. | `agent-governance-center` | `governance-decisions-audit`, `managed-agent-foundation` | surface-ready template; expertise and runtime must be verified. |

## Workstream icon metadata

| Workstream id | Icon id | Visual hint | Accent token | Accessible label |
|---|---|---|---|---|
| `my-account` | `account-user` | user/profile | `workstream.account` | My Account workstream |
| `user-admin` | `users-shield` | users/shield | `workstream.user-admin` | User Admin workstream |
| `agent-admin` | `agent-gear` | agent/gear | `workstream.agent-admin` | Agent Admin workstream |
| `audit-trace` | `audit-search` | search/timeline | `workstream.audit` | Audit and Trace workstream |
| `governance-policy` | `policy-shield` | shield/checklist | `workstream.governance` | Governance and Policy workstream |

## Functional-agent rules

- The left rail lists only functional agents authorized by the selected `AuthContext`; My Account is opened from the signed-in user region.
- Selecting a functional agent opens its workstream instance and default dashboard/attention surface.
- Composer requests are interpreted inside the selected functional-agent context and resolve to capability-backed actions, evidence queries, proposals, approvals, surface requests, or safe denials.
- Prompt intent, skills, references, rail visibility, and frontend action visibility guide the user/model; backend capabilities and `ToolPermissionBoundary` enforce authority.
- Business-domain features add domain-specific functional agents with their own workstream contract, dashboard, attention model, surface graph, capability map, expertise bundle when LLM-backed, traces, and tests.
