# Tests: Audit/Trace

## Acceptance

- Given a tenant admin with selected `AuthContext`, when they open the Audit/Trace activity log, then rows are scoped to their tenant and show time, worker type, actor/agent, action type, customer/account, status, deterministic summary, and correlation/session id.
- Given a tenant admin searches with date/time range, worker type, actor/user/agent, action type, customer/account, and status filters, when the search is submitted, then only authorized tenant-scoped metadata/summary matches are returned.
- Given a tenant admin opens a human request/response trace, then detail shows human email, role, org, full request payload, full response payload, status, summary, and the sensitive full-payload warning.
- Given a tenant admin opens an agent request/response trace, then detail shows agent name, role/workstream, model, prompt/skill/version, session/conversation id, requested-by user where applicable, full request payload, full response payload, status, summary, and the sensitive full-payload warning.
- Given a tenant admin opens a tool-call trace, then detail shows tool name, purpose, input payload, output payload, authorization result, duration, status/error, and linked parent request/response.
- Given a tenant admin opens a denied-action trace, then detail shows the denial reason and policy reference when authorized.
- Given a tenant admin opens retention settings, then the default is 90 days and the allowed range is shown as 30 to 365 days.
- Given a tenant admin saves a valid changed retention value, then the tenant setting changes and a retention configuration change trace records old value, new value, actor, tenant, timestamp, status, and correlation id.

## Regression and explicit tenant-admin activity-log scope exclusions

- Given a user searches by keyword, then the search matches deterministic metadata/summary fields only and never full request/response/tool payload text.
- Given the app renders activity rows, then full payloads do not appear in rows, filter chips, summaries, or browser-visible search indexes.
- Given a tenant admin uses Audit/Trace tenant-admin activity-log scope, then export/compliance bundles, investigation notes, suspicious-activity acknowledgements, and agent-generated summaries are not available as working tenant-admin activity-log scope features.

## Security and negative

- Given a non-tenant-admin authenticated user, when they open, search, view detail, or configure retention, then the action is denied server-side and no protected trace data is exposed.
- Given a disabled user or inactive membership, when they attempt any Audit/Trace tenant-admin activity-log scope action, then the action is denied and emits denial trace evidence.
- Given a tenant admin from tenant A, when they request tenant B traces or a cross-tenant customer/account filter, then the action is denied or returns no authorized rows without hidden target enumeration.
- Given a hidden, expired, or malformed trace reference, when detail is opened, then the response is `not_found_or_redacted` or validation-error without confirming protected record existence.
- Given a retention value below 30 or above 365 days, when it is submitted, then validation fails, no setting changes, and trace evidence records the failed attempt.
- Given browser payloads for list/detail/settings surfaces, then secrets, bearer/session tokens, provider credentials, hidden cross-tenant identifiers, and frontend-secret material are absent.

## Idempotency

- Given a tenant admin submits the same retention value that is already active, then the result is an idempotent no-op and records a diagnosable no-op trace without duplicating configuration changes.
- Given the same search request is repeated, then it returns authorized current results without creating side effects other than search trace evidence.
- Given the same detail read is repeated, then it returns authorized current detail or a safe retention/redaction outcome without mutating the audit record.

## Observability and trace verification

- Search, validation failure, denied search, empty result, detail read, denied detail, tool-call detail read, retention setting view, retention setting update, retention validation failure, and idempotent no-op each emit durable trace evidence with tenant, actor, action, status, and correlation id.
- Human request/response, agent request/response, and tool-call records include the minimum trace fields defined in `../traces/work-traces.md`.
- Denied-action traces include denial reason and policy reference for authorized tenant-admin detail viewing.
- Retention expiry removes records only according to configured retention and does not allow manual edit/delete of immutable audit records.

## UI and accessibility verification

- Activity log rows, filters, pagination, detail links, linked tool-call/parent links, and retention settings controls are keyboard-operable with visible focus.
- Status is not communicated by color alone.
- The sensitive full-payload warning is visible on every detail surface that renders full payloads.
- Error, empty, forbidden, validation-error, stale/reconnect, and not-found/redacted states provide safe recovery text without exposing hidden data.
