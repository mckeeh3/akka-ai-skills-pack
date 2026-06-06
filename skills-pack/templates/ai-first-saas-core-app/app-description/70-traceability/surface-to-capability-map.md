# Surface to Capability Map

| Surface | Owner functional agent | Reusable by | Capability/action mapping |
| --- | --- | --- | --- |
| `my-account-dashboard` | `my-account-agent` | opens authorized target workstreams/surfaces | read account/profile/settings/context/attention → `secure-tenant-user-foundation`; surface requests/open workstream → `frontend-shell-integration-patterns`; trace reads → `governance-decisions-audit`. |
| `user-admin-dashboard` | `user-admin-agent` | My Account attention summaries | dashboard/read attention/invitations/access review → `secure-tenant-user-foundation`; risky actions and audit evidence → `governance-decisions-audit`; internal investigation start → `managed-agent-foundation` plus selected worker capability. |
| `user-admin-user-list` | `user-admin-agent` | dashboard queues and prompt/deep-link requests | user search/list/invite draft/open detail → `secure-tenant-user-foundation`; audit excerpts → `governance-decisions-audit`. |
| `user-admin-user-account` | `user-admin-agent` | list rows, attention items, audit drill-ins | membership/role/support-access/account actions → `secure-tenant-user-foundation`; last-admin/risky actions → `governance-decisions-audit`. |
| `decision-card` | `governance-policy-agent` | User Admin, Agent Admin, Audit/Trace, domain-specific reviewer workstreams | approval/rejection/request changes/escalation → `governance-decisions-audit`; behavior/policy proposal conversion → `managed-agent-foundation` as needed. |
| `audit-trace-explorer` | `audit-trace-agent` | all foundation/domain workstreams with scoped trace access | search/inspect/export scoped traces → `governance-decisions-audit`; prompt assembly, skill/reference load, tool-boundary, work and denial trace details → `managed-agent-foundation`. |
| `agent-governance-center` | `agent-admin-agent` | Governance/Policy | AgentDefinition/prompt/skill/reference/manifest/tool-boundary read/propose/review/test → `managed-agent-foundation`; approval/impact/trace actions → `governance-decisions-audit`. |
| `markdown_response` | All five core workstreams | bounded answer/denial/next-step response → owning workstream capability plus governed runtime agent records and traces. |
| `system_message` | All five core workstreams | provider blocked, authorization denial, task/projection status → owning workstream capability plus audit/work trace. |

## Extension rule

Every new domain surface action, including read/query and surface-request actions, must map to a governed capability id and governed-tool id before implementation. Reusable surfaces retain one owner functional agent; reuse does not transfer authority.
