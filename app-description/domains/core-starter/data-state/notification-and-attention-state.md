# Data State: Notification and attention state

## Responsibility

Durable personal notification, workstream attention, dashboard-count, and source-opening state for the SaaS Foundation App. This state connects My Account personal attention surfaces to source workstream events without letting personal notification actions mutate source work, roles, memberships, policies, agents, traces, or provider state.

## Lifecycle and invariants

- Notification records are backend-owned and scoped to an account plus selected authorization context; browser state is never authoritative.
- Canonical notification states are `unread`, `read`, `dismissed`, `archived`, `snoozed`, `expired`, and `source_resolved`.
- Personal notification transitions (`mark_read`, `dismiss`, `archive`, `snooze`) are idempotent and affect only the signed-in user's personal notification lifecycle.
- Source workstream completion may move source-linked notifications to `source_resolved` or remove them from active attention counts, but personal dismissal never resolves the underlying source task, invitation, proposal, trace gap, access review, policy decision, or agent task.
- Attention items are derived from authorized source workstream state with a stable source key, severity, lifecycle status, and redaction summary. Aggregation deduplicates by source key and selected context so one source event cannot inflate dashboard or My Account counts through repeated projections.
- Hidden or unauthorized source items are omitted or summarized only as allowed by redaction policy; they never leak source object ids, hidden workstream names, hidden counts, or cross-tenant/customer facts.
- Snooze has an explicit backend expiry and reactivates only if the source item still needs attention and remains visible to the user.
- Expired notifications remain available only as audit/read-history evidence where policy permits; they are not active attention.
- Notification preferences are account-scoped UX preferences and cannot disable mandatory security, provider-failure, invitation, approval, or audit in-app notifications required by policy.
- Email notifications are foundation-scoped to invitations only. Approval-required, provider/outbox/model blocked, access-review ready, audit/export approval, policy activation/rollback, and other foundation attention events remain in-app unless a later accepted intent change adds email contracts, authorization, traces, and tests.

## Source ownership

Source workstreams own the canonical lifecycle of the source object: User Admin owns invitations/access reviews/identity exceptions, Agent Admin owns behavior proposals/risk reviews/seed imports, Governance/Policy owns policy proposals/impact analyses, Audit/Trace owns trace investigations/exports/summary tasks, and My Account owns personal digest/export tasks. Notification and attention state stores only browser-safe routing metadata, source refs, redaction summaries, lifecycle projection state, and personal read/dismiss/snooze/archive state.

## Retention and traces

Notification state changes emit work traces with actor account, selected `AuthContext`, source kind/ref when visible, personal lifecycle transition, idempotency/no-op status, redaction decision, and correlation id. Browser payloads never expose raw source payloads, JWT/session data, provider/model/tool internals, hidden object ids, raw trace ids beyond authorized trace refs, or fixture/demo notification data as normal runtime.
