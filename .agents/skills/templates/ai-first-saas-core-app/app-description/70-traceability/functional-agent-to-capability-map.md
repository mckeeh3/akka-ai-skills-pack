# Functional Agent to Capability Map

| Functional agent | Workstream id | Primary surfaces | Capability families | Required trace/test focus |
|---|---|---|---|---|
| `my-account-agent` | `my-account` | `my-account-dashboard`, `markdown_response`, `system_message` | `secure-tenant-user-foundation`, `frontend-shell-integration-patterns`, `governance-decisions-audit` | own-scope, context selection, disabled-user denial, aggregate attention, trace links. |
| `user-admin-agent` | `user-admin` | `user-admin-dashboard`, `user-admin-user-list`, `user-admin-user-account`, `decision-card`, `audit-trace-explorer` | `secure-tenant-user-foundation`, `managed-agent-foundation`, `governance-decisions-audit` | tenant isolation, invitation lifecycle, last-admin protection, support access, AdminAuditEvent, AgentWorkTrace. |
| `agent-admin-agent` | `agent-admin` | `agent-governance-center`, `decision-card`, `audit-trace-explorer` | `managed-agent-foundation`, `governance-decisions-audit` | AgentDefinition lifecycle, PromptDocument/SkillDocument/ReferenceDocument governance, manifests, tool boundaries, model refs, authority-expansion denial. |
| `audit-trace-agent` | `audit-trace` | `audit-trace-explorer`, `decision-card`, `markdown_response`, `system_message` | `governance-decisions-audit`, `secure-tenant-user-foundation`, `managed-agent-foundation` | redaction, scoped search, support-access audit, export denial, correlation ids, trace explanations. |
| `governance-policy-agent` | `governance-policy` | `agent-governance-center`, `decision-card`, `audit-trace-explorer` | `governance-decisions-audit`, `managed-agent-foundation` | policy proposal, simulation/replay, approval, activation/rollback, authority expansion denial. |

## Extension rule

Every domain-specific functional agent must map to at least one workstream id, one default dashboard/surface, one capability family, auth/security expectations, trace expectations, and tests before runtime implementation.
