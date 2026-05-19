# Surface to Capability Map

| Surface | Functional agents | Capability-backed actions |
|---|---|---|
| `access-profile-dashboard` | Access/Profile | context selection and profile/settings updates → `secure-tenant-user-foundation`; shell state → `frontend-shell-integration-patterns` |
| `user-admin-command-center` | User Admin | invite/resend/revoke, membership/role changes, support-access review → `secure-tenant-user-foundation`; risky decision actions → `governance-decisions-audit` |
| `agent-governance-center` | Agent Admin, Governance/Policy | draft/review/activate/rollback agent behavior, `readSkill` test → `managed-agent-foundation`; approval decisions → `governance-decisions-audit` |
| `mission-control-briefing` | Mission Control | create/launch/monitor goals and plans → `ai-first-work-management`; review decisions/exceptions → `governance-decisions-audit` |
| `decision-card` | Mission Control, Governance/Policy, User Admin | approve/reject/counter/defer/escalate → `governance-decisions-audit`; behavior/policy proposal conversion → `managed-agent-foundation` as needed |
| `audit-trace-explorer` | Audit/Trace, User Admin, Agent Admin | search/inspect/export scoped traces → `governance-decisions-audit`; prompt/skill/work trace details → `managed-agent-foundation` |
