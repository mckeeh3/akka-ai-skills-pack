# Functional Agent to Capability Map

See `workstream-id-map.md` for canonical app-description ids, runtime/API/frontend functional-agent ids, workstream icon ids, and primary dashboard surface aliases.

| Functional agent | Expertise bundle | Primary surfaces | Capability links |
| --- | --- | --- | --- |
| My Account | `12-workstreams/workstream-expertise/my-account-agent.md` | `my-account-dashboard`, `markdown_response`, `system_message` | `secure-tenant-user-foundation`, `frontend-shell-integration-patterns`, personal notification/readiness slices |
| User Admin | `12-workstreams/workstream-expertise/user-admin-agent.md` | `user-admin-dashboard`, `user-admin-user-list`, `user-admin-user-account`, `decision-card`, `audit-trace-explorer`, `markdown_response` | `secure-tenant-user-foundation`, `governance-decisions-audit`, `managed-agent-foundation` |
| Agent Admin | `12-workstreams/workstream-expertise/agent-admin-agent.md` | `agent-governance-center`, `decision-card`, `audit-trace-explorer`, `markdown_response` | `managed-agent-foundation`, `governance-decisions-audit` |
| Audit/Trace | `12-workstreams/workstream-expertise/audit-trace-agent.md` | `audit-trace-explorer`, `markdown_response`, `system_message` | `governance-decisions-audit`, `secure-tenant-user-foundation`, `managed-agent-foundation` |
| Governance/Policy | `12-workstreams/workstream-expertise/governance-policy-agent.md` | `agent-governance-center`, `decision-card`, `audit-trace-explorer`, `markdown_response` | `governance-decisions-audit`, `managed-agent-foundation`, `ai-first-work-management` for governed task/impact follow-up |

## Extension rule

Add domain-specific functional agents here before generating implementation. Each row must link to surfaces, capabilities, and an expertise bundle.
