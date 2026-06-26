# Realization: API contracts for Audit/Trace

Capability: `audit-and-trace-investigation`.

This file records the v1 build contract implied by current intent. It is not runtime proof.

## Browser/API contract obligations

| Tool / action | Exposure | Contract obligations |
|---|---|---|
| `search-audit-traces` / `action-audit-trace-search` | `browser-tool` | Tenant-admin-only scoped search over deterministic metadata/summary fields with filters for date/time range, worker type, actor/user/agent, action type, customer/account, and status. Must not search full payloads. |
| `read-trace-detail` / `action-audit-trace-detail` | `browser-tool` | Tenant-admin-only trace detail read with full request/response payloads, human/agent identity fields, denial reason/policy where applicable, and sensitive full-payload warning contract. |
| `read-trace-tool-call-detail` / `action-audit-trace-tool-call-detail` | `browser-tool` | Tenant-admin-only tool-call detail with tool name, purpose, input/output payload, authorization result, duration, status/error, and linked parent request/response. |
| `read-audit-retention-setting` / `action-audit-trace-retention-settings-open` | `browser-tool` | Tenant-admin-only read of current retention setting, default 90 days, min 30, max 365. |
| `update-audit-retention-setting` / `action-audit-trace-retention-settings-save` | `browser-tool` | Tenant-admin-only retention update with 30–365 day validation, idempotent same-value no-op, and audit trace emission for old/new values. |

## Validation evidence required before build completion

- Backend/API tests for tenant-admin success, non-admin denial, disabled/inactive denial, tenant isolation, invalid filters, hidden/expired trace references, and retention bounds.
- Backend/API tests that list/search output excludes full payloads and full-payload keyword search is unavailable.
- Backend/API tests that detail output includes full payloads only for authorized tenant admins.
- Backend/API tests that denied trace detail includes denial reason and policy reference when authorized.
- Backend/API tests that retention changes emit immutable audit trace evidence.

## Explicit v1 API exclusions

Do not implement export, investigation-note, acknowledgement, AI-summary, support-operator, customer-admin, auditor, SaaS-owner, or agent-tool APIs as part of this v1 build slice unless a later current-intent change adds them.
