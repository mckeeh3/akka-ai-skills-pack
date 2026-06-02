# Surface to Capability Map

| Surface | Owning workstream(s) | Capability/action mapping |
| --- | --- | --- |
| `my-account-dashboard` | My Account | read account/profile/settings/context/notifications → `secure-tenant-user-foundation`; shell launch/context state → `frontend-shell-integration-patterns`. |
| `user-admin-dashboard` | User Admin | dashboard/read attention/invitations/access review → `secure-tenant-user-foundation`; risky actions and audit evidence → `governance-decisions-audit`. |
| `user-admin-user-list` | User Admin | user search/list/invite draft/open detail → `secure-tenant-user-foundation`; audit excerpts → `governance-decisions-audit`. |
| `user-admin-user-account` | User Admin | membership/role/support-access/account actions → `secure-tenant-user-foundation`; last-admin/risky actions → `governance-decisions-audit`. |
| `agent-governance-center` | Agent Admin, Governance/Policy | AgentDefinition/prompt/skill/reference/manifest/tool-boundary read/propose/review → `managed-agent-foundation`; approval/impact/trace actions → `governance-decisions-audit`. |
| `decision-card` | User Admin, Agent Admin, Governance/Policy | approval/rejection/request changes/escalation → `governance-decisions-audit`; behavior/policy proposal conversion → `managed-agent-foundation` as needed. |
| `audit-trace-explorer` | Audit/Trace, User Admin, Agent Admin, Governance/Policy | search/inspect/export scoped traces → `governance-decisions-audit`; prompt assembly, skill/reference load, tool-boundary, work and denial trace details → `managed-agent-foundation`. |
| `markdown_response` | All five core workstreams | bounded answer/denial/next-step response → owning workstream capability plus governed runtime agent records and traces. |
| `system_message` | All five core workstreams | provider blocked, authorization denial, task/projection status → owning workstream capability plus audit/work trace. |

## Extension rule

Every new domain surface action must map to a governed capability/tool before implementation.
