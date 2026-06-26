# Tools: Audit/Trace

## Uses

Global tool inventory: `../../../../../global/tools/foundation-governed-tools.md`.

## Tenant-admin activity-log scope workstream exposure

Allowed governed tools for the tenant-admin activity-log scope:

- `search-audit-traces`
- `read-trace-detail`
- `read-trace-tool-call-detail`
- `read-audit-retention-setting`
- `update-audit-retention-setting`

Action-to-tool aliases are canonical:

- `action-audit-trace-search` -> `search-audit-traces`
- `action-audit-trace-detail` -> `read-trace-detail`
- `action-audit-trace-tool-call-detail` -> `read-trace-tool-call-detail`
- `action-audit-trace-retention-settings-open` -> `read-audit-retention-setting`
- `action-audit-trace-retention-settings-save` -> `update-audit-retention-setting`

## Tool authority boundaries

All tenant-admin activity-log scope tools are browser surface actions requiring backend-owned selected `AuthContext`, active membership, tenant-admin role/capability, tenant scope, and server-side authorization.

No tenant-admin activity-log scope `agent_tool_call`, `human_chat_tool_plan`, export, investigation-note, summary-task, support-operator, customer-admin, SaaS-owner, or auditor tool authority is granted by this workstream description.

`update-audit-retention-setting` is side-effecting and requires idempotency, correlation, validation of the 30–365 day range, and audit trace emission. Replaying the same value with the same idempotency context returns a no-op result without duplicate configuration changes.

Denied tool calls are traced and return safe feedback without hidden target enumeration or protected-data leakage.

## Adapter binding matrix

| Governed tool id | Capability id | Workers | Allowed actor adapters | Authority / side-effect boundary | Result surfaces / events |
|---|---|---|---|---|---|
| `search-audit-traces` | `audit-and-trace-investigation` | tenant-admin human, system worker | `surface_action`, `api_call`, `internal_call` | Read-only tenant-scoped metadata/summary query; no full-payload keyword search. | `surface-audit-trace-activity-log`, validation/forbidden system message, search trace. |
| `read-trace-detail` | `audit-and-trace-investigation` | tenant-admin human, system worker | `surface_action`, `api_call`, `internal_call` | Read-only authorized detail/full payload with sensitive warning and secret redaction. | `surface-audit-trace-detail`, `not_found_or_redacted`, detail-view trace. |
| `read-trace-tool-call-detail` | `audit-and-trace-investigation` | tenant-admin human, system worker | `surface_action`, `api_call`, `internal_call` | Read-only authorized linked tool-call/parent detail; no cross-tenant discovery. | `surface-audit-trace-detail`, `not_found_or_redacted`, linked-detail trace. |
| `read-audit-retention-setting` | `audit-and-trace-investigation` | tenant-admin human, system worker | `surface_action`, `api_call`, `internal_call` | Read-only tenant retention setting. | `surface-audit-trace-retention-settings`, retention-view trace. |
| `update-audit-retention-setting` | `audit-and-trace-investigation` | tenant-admin human, system worker | `surface_action`, `api_call`, `internal_call` | Mutates tenant retention setting only after 30–365 validation; idempotent same-value no-op. | Updated settings surface, system message, retention-update/no-op trace. |
| audit trace ingestion / retention expiry internals | `audit-and-trace-investigation` | system worker | `internal_call`, `consumer_reaction`, `timer_invocation` | Append immutable trace records or expire by retention policy with service provenance; not exposed to browser or agents. | Durable trace records, retention-expiry evidence. |

This matrix is the workstream binding; detailed worker authority and failure behavior lives in `../workers/`.
