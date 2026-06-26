# Realization: Frontend routes and surfaces for Audit/Trace

Capability: `audit-and-trace-investigation`.

This file records the tenant-admin activity-log scope frontend realization contract implied by current intent. It is not runtime proof.

## Frontend surface obligations

| Surface / route concern | Contract obligations |
|---|---|
| Activity log | Render `surface-audit-trace-activity-log` with filters for date/time range, worker type, actor/user/agent, action type, customer/account, and status. Rows show time, worker type, actor/agent, action type, customer/account, status, deterministic summary, and correlation/session id. |
| Trace detail | Render `surface-audit-trace-detail` with full payload sections only after backend authorization, show **"Sensitive full payload — tenant admin access only"**, and show human/agent/tool/denial fields per surface contract. |
| Tool-call links | Let authorized users navigate between tool-call traces and linked parent request/response traces through backend-authorized detail actions. |
| Retention settings | Render `surface-audit-trace-retention-settings` with current value, default 90 days, min 30, max 365, validation errors, saved/no-op states, and last-change metadata when available. |
| Error and restricted states | Render safe loading, empty, validation-error, forbidden, not-found/redacted, stale/reconnect, partial-data/redacted, saved, no-op, and failure states without hidden data leakage. |

## Accessibility and frontend-security obligations

- Filters, rows, detail links, linked trace controls, and retention form controls are keyboard-operable with visible focus.
- Status is not color-only.
- Full payloads are never rendered in activity rows, filter chips, summaries, or client-side search indexes.
- Browser assets and API payloads never expose provider/server secrets, bearer/session tokens, hidden cross-tenant identifiers, or frontend-secret material.

## Explicit tenant-admin activity-log scope frontend exclusions

Do not render export/compliance bundle flows, investigation notes, suspicious-activity acknowledgement/review, AI-generated summary progress/review, or full-payload keyword search as working tenant-admin activity-log scope features.
