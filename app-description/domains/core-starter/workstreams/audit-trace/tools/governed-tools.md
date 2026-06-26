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
