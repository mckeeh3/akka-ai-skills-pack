# Functional Agent Rail

## Rail entries

| Rail entry | Functional agent id | Default surface | Primary route/deep link |
|---|---|---|---|
| Access/Profile | `access-profile-agent` | `access-profile-dashboard` | `/ui/access` |
| User Admin | `user-admin-agent` | `user-admin-dashboard` with `user-admin-user-list` and `user-admin-user-account` deep links | `/ui/admin/users` |
| Agent Admin | `agent-admin-agent` | `agent-governance-center` | `/ui/agents` |
| Mission Control | `mission-control-agent` | `mission-control-briefing` | `/ui/mission-control` |
| Governance/Policy | `governance-policy-agent` | `policy-governance-workbench` / `agent-governance-center` | `/ui/governance/policies` |
| Audit/Trace | `audit-trace-agent` | `audit-trace-explorer` | `/ui/audit/traces` |

## Authorization

- The rail is role-authorized by backend capability grants in `/api/me`.
- A missing rail entry means the current AuthContext lacks permission; direct route access must still be rejected by backend APIs.
- Support access appears only with explicit support grant, expiry, reason, and audit visibility.

## Rendering expectations

- Use the reference implementation under `../../../../../frontend/src/workstream/rail/**` and `../../../../../frontend/src/workstream/shell/**` for generated rail behavior.
- Active functional agent is visually primary.
- Pending decision/exception badges are scoped to the selected tenant/customer.
- Narrow screens collapse the rail into a drawer while preserving current functional-agent context and composer access.
