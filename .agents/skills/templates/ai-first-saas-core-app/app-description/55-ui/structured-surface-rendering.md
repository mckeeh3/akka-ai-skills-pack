# Structured Surface Rendering

This UI layer realizes the surface contracts from legacy template `12-workstreams/**` or their mapped current-intent `app-description/domains/<domain>/workstreams/<workstream>/surfaces/**` artifacts; it does not redefine surface meaning, authority, or capability semantics.

## Required renderer behavior

- Render every workstream response as a typed surface envelope or typed workstream request item, with at most one primary result surface for the same prompt/action content.
- Do not render internal app activity records, generic agent-response detail cards, tool-call traces, or response metadata as additional sibling surfaces when a typed result surface such as `markdown_response` or `system_message` already represents the result. Keep those records collapsed behind trace/details affordances unless a typed progress/detail surface is explicitly requested.
- Route prompt-entered requests, rail/My Account selection, dashboard buttons, row/card clicks, action results, and deep links through the unified shell request pipeline.
- Use `SurfaceEnvelope` and `SurfaceAction` semantics from `docs/structured-surface-contracts.md` and preserve worker → execution harness → actor adapter → governed tool → capability links from the current-intent graph.
- Render `markdown_response` through an approved markdown parser and sanitizer; never render raw model HTML.
- Render `system_message` as a structured surface for denial, warning, validation, success, approval-required, stale/reconnect, background-work, deferred, no-op, and recovery feedback.
- Preserve loading, empty, error, forbidden, conflict, stale, reconnecting, partial-data, submitting, success, pending, and approval-needed states.
- Preserve keyboard navigation, labels, focus order, status announcements, and responsive table-to-card behavior.

## Action handling

Every action descriptor must include:

- `actionId`
- `browserToolId`
- `governedToolId`
- `capabilityId`
- idempotency requirements
- confirmation/approval requirements
- result surface or system-message behavior
- audit event type and trace requirement

Frontend action visibility is advisory only. Backend capabilities remain authoritative for authorization, selected `AuthContext`, tenant/customer scope, approval policy, idempotency, side effects, audit, and denial.

## Tests

UI tests should cover:

- envelope parsing and rendering for every surface type in `12-workstreams/surfaces-index.md`, including no duplicate generic activity/detail surface for the same `markdown_response` or `system_message`;
- shell request normalization for prompt, surface action, My Account panel, rail selection, and deep-link origins;
- action descriptors preserving browser-tool/governed-tool/capability ids and idempotency keys;
- authorized, denied, disabled-user, wrong-tenant/customer, support/auditor, and hidden-not-found variants;
- markdown sanitization and unsafe-link blocking;
- realtime duplicate/out-of-order/malformed event no-op handling and stale markers;
- frontend secret boundary: no provider secrets, raw tokens, prompt internals, hidden fields, or cross-tenant data in payloads, state, logs, or fixtures.
