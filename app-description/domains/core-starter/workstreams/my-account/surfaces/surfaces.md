# Surfaces: My Account

## Surface architecture

My Account is the signed-in human's personal AI-first control workstream. Its surfaces are not a conventional account-settings page set. They are structured, backend-backed work units owned by `my-account-agent` that help a user answer:

1. **What needs my personal attention in this selected context?**
2. **Which identity, preference, theme, and notification settings can I safely maintain myself?**
3. **Which tenant/customer context and authority basis is currently governing agent work and browser actions?**
4. **What personal digest/export work is running, blocked, or ready for review?**
5. **Why was a requested workstream, source item, or protected action unavailable?**

All My Account surfaces use the canonical AI-first workstream shell, structured surface envelope, governed browser-tool actions, selected `AuthContext`, redaction, trace links, and the skills-pack authoritative web UI style guide. Routes and links only reopen a functional agent, stream item, or structured surface; they do not define product meaning or bypass backend authorization.

## Surface inventory

| Surface id | Type | Contract | Primary purpose | Status |
|---|---|---|---|---|
| `surface-my-account-dashboard` | `dashboard` | `my_account.personal_command_center.v1` | Personal command center for attention, authority, settings, notifications, and digest/export work. | Rebuilt contract |
| `surface-my-profile` | `detail-edit` | `my_account.profile.self_service.v1` | Browser-safe identity/profile self-service with clear immutable/provider-backed fields. | Rebuilt contract |
| `surface-my-settings` | `detail-edit` | `my_account.preferences.self_service.v1` | Personal preferences, named theme selection, locale/timezone, and preference save state. | Rebuilt contract |
| `surface-my-context` | `detail-edit` / authority panel | `my_account.context_authority.v1` | Selected AuthContext, active membership, role/capability basis, and context-switch targets. | Rebuilt contract |
| `surface-my-account-notification-center` | `notification-center` | `my_account.notification_center.v1` | Personal in-app triage for authorized notifications. | Recently revised; preserve contract |
| `surface-my-account-personal-attention-digest-progress` | `workflow-status` | `my_account.personal_attention_digest.progress.v1` | Autonomous personal briefing/digest task progress. | Rebuilt contract |
| `surface-my-account-personal-attention-digest-result` | `outcome-panel` | `my_account.personal_attention_digest.result.v1` | Advisory digest/export result review with evidence, omissions, and accept/reject actions. | Rebuilt contract |
| `surface-my-account-personal-attention-digest-blocked` | `system-message` | `my_account.personal_attention_digest.blocked.v1` | Provider/runtime fail-closed explanation and recovery. | Rebuilt contract |
| `surface-my-account-open-denied` | `system-message` | `my_account.open_denied.v1` | Safe not-found/redacted/unavailable workstream recovery. | Rebuilt contract |

## My Account personal command center surface

### Intent

`surface-my-account-dashboard` is titled **My Account Dashboard** and is the default My Account surface and personal command center. It orients the signed-in human to what requires their attention in the currently selected tenant/customer context, highlights personal attention before lower-priority settings, and provides governed entry points into profile, settings, context authority, notifications, digest/export, and authorized sibling workstreams.

This surface must not become a generic navigation dashboard, CRM-style card grid, or client-side authorization shortcut. Each card, counter, list row, or action is a structured surface edge backed by server-side authorization and traceable capability execution.

### Contract

- Surface id: `surface-my-account-dashboard`.
- Surface type: `dashboard`.
- Surface contract: `my_account.personal_command_center.v1`.
- Owning workstream: My Account.
- Owning functional agent: `my-account-agent`.
- Required context: authenticated active account plus selected tenant/customer membership.
- Reusable placements: Audit/Trace may link to this surface for self-service trace investigation; other workstreams may link back for personal context recovery.

### User experience model

1. **Orient** — show selected context, account identity, role/capability basis, redaction profile, support/elevated-access state if visible, and correlation/trace affordances.
2. **Prioritize by available workstream** — render a top multi-workstream attention counter strip before profile/settings panels, using the web UI catalog `Attention counter card` anatomy. My Account is an explicit dashboard exception because it is the signed-in user's cross-workstream personal command center: it intentionally aggregates **total things that need my attention** counters for every currently available/authorized workstream, mirroring the left rail workstream counters while providing more context. There must be one counter card for each available authorized workstream, including zero-count states when the counter opens an empty queue, setup, history, or confirmation surface. Each card shows the workstream label, total attention count, status text, short purpose/context, severity, and an Open action to the workstream's backend-authorized default surface.
3. **Open workstreams, not raw items** — the dashboard's primary attention model is the per-workstream total counter view. Selecting a counter reauthorizes and opens the specific source workstream so the user can inspect and act on detailed items there. The dashboard may retain detailed `needsAttention[]` metadata in the backend payload for traceability/reuse, but it must not use that metadata to replace the multi-workstream counter view or become a raw item/task list. Raw capability ids, governed tool ids, target surface ids, and trace ids must not appear as primary dashboard text. Detailed attention/evidence belongs in the opened source workstream or collapsed trace/evidence affordances.
4. **Control** — expose compact secondary panels for Profile, Settings, Context & authority, Notifications, and Personal digest/export after the multi-workstream attention counters. Future My Account-specific surfaces may be added here without changing the cross-workstream attention-counter model.
5. **Explain** — every attention counter shows why it is visible: available source workstream, required opening capability summarized in user-safe language, redaction level, trace/correlation affordances when expanded, and safe target hints.
6. **Recover** — provider-fail-closed, stale/reconnect, unavailable-workstream, no-membership, and disabled-account states render explicit recovery without leaking hidden data.

### Frontend-safe payload

- `surfaceContract`, `accountContext`, `authorityBasis`, `redaction`, `capabilityIds`, `traceRefs`, `correlationId`.
- `attentionCounters[]`: required primary dashboard payload with one entry per available authorized workstream. Each entry has a stable counter id, workstream id, user-facing workstream label, **total things that need my attention** count value, severity, status text, short workstream purpose/context, source capability/tool, target surface id, and open action id. These counters are intentionally the dashboard equivalent of the left rail workstream counters and are the main multi-workstream attention view.
- `needsAttention[]`: backend payload may retain detailed attention item metadata for traceability/reuse, but the dashboard does not use it to replace the per-workstream counter model. Detailed item inspection happens after the user opens the source workstream through a governed counter/action edge.
- `controlPanels[]`: profile/settings/context/notifications/digest summaries with state, count/status, target surface id, action id, and denial hint if not available.

Forbidden payload/content:

- Hidden workstream names, hidden attention counts, cross-tenant/customer facts, raw roles/capability internals beyond browser-safe ids, provider secrets, raw model/provider configuration, local fixtures, or simulated normal-runtime data.

### Actions

| Action | Governed backend capability/tool | Result behavior |
|---|---|---|
| Refresh dashboard | `my_account.view_summary` / `read-current-account-context` | Reload backend-owned personal command center projection. |
| Open profile | `my_account.view_summary` | Render `surface-my-profile`. |
| Open settings | `my_account.view_summary` | Render `surface-my-settings`. |
| Open context authority | `my_account.view_context` / `read-current-account-context` | Render `surface-my-context`. |
| Open notifications | `notification.list_my_account_center` | Render `surface-my-account-notification-center`. |
| Open attention item | source capability such as `attention.open_attention_item` | Reauthorize target; render target surface or safe `not_found_or_redacted`. |
| Open authorized workstream | `my_account.open_authorized_workstream` | Deep-link into authorized workstream surface or render `surface-my-account-open-denied`. |
| Start/read personal digest/export | `my_account.personal_attention_digest.*` / `request-personal-digest-export` | Render progress, result, or blocked surface. |
| Sign out | auth/session boundary action | Clear local session through auth integration; never represented as a permission grant. |

### State and style expectations

The dashboard uses the current AI-first workstream style: authority/context panel, attention counter cards, trace/evidence blocks, system-message cards, and compact governed action bars. The dashboard's primary attention UI is the multi-workstream `attentionCounters[]` strip. Any `needsAttention[]` or queue row/card rendering must be secondary, collapsed, or trace/evidence-oriented and must not replace the counter-first flow or provide direct frontend-only item navigation. It must define loading, empty, ready, submitting, forbidden, stale/reconnect, partial-data, provider-fail-closed, no-op, and failure states.

### Specification completeness for implementation

- **Payload schema detail:** implement `attentionCounters[]` as the primary repeated dashboard section. Each counter requires `counterId`, `workstreamId`, `workstreamLabel`, `attentionCount`, `severity`, `statusText`, `purposeSummary`, `redactionLevel`, `sourceCapabilityId`, `sourceToolId`, `targetSurfaceId`, `openActionId`, `traceRefs[]`, and `disabledOrDeniedReason` when visible but not openable. Implement `controlPanels[]` with `panelId`, `panelLabel`, `summary`, `state`, `countOrStatus`, `targetSurfaceId`, `actionId`, `capabilityId`, `traceRefs[]`, and optional `denialHint`. `accountContext`, `authorityBasis`, `redaction`, `capabilityIds`, `traceRefs`, and `correlationId` are browser-safe summaries only.
- **Authorization and tenant rules:** every refresh/open action must be evaluated against the selected backend `AuthContext`; tenant/customer scope comes from `/api/me` and protected workstream APIs, not from client routing. Hidden or unauthorized workstreams, counts, traces, and targets are omitted or rendered through `surface-my-account-open-denied` without enumeration.
- **Trace and audit contract:** each backend dashboard read and consequential open/start action emits or links an audit/work trace containing actor account, selected context, capability decision, redaction level, source workstream when visible, target surface decision, result state, and correlation id. Browser surfaces show user-safe trace summaries and role-gated links only.
- **Accessibility and responsive expectations:** the attention counter strip is keyboard-operable as a set of labeled cards/buttons, preserves heading order, exposes count/severity/status in accessible names, supports empty zero-count targets, and wraps to a single column on narrow viewports without changing action order. Control panels remain secondary to attention counters and use the same focus and disabled-state semantics as governed action cards.
- **Acceptance and regression tests:** generated/runtime work must cover dashboard refresh success, zero-count authorized counter opening, forbidden/hidden workstream omission or safe denial, stale/reconnect recovery, provider-fail-closed digest entry, no-op refresh, trace/correlation visibility, tenant/customer isolation, and a frontend secret-boundary check proving no raw provider/JWT/hidden-workstream payload is rendered.
- **Surface-description sufficiency review:** yes — this surface definition is sufficiently unambiguous for a developer or generator to implement and review `surface-my-account-dashboard` without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics. The default view avoids exposing internal implementation details that do not help the signed-in SaaS user decide, act, recover, or understand their personal command center.

## My profile self-service surface

### Intent

`surface-my-profile` lets the signed-in human inspect browser-safe identity facts and update allowed profile fields. It is a self-service identity surface, not an admin user record, role editor, provider configuration page, or permission request workflow.

### Contract

- Surface id: `surface-my-profile`.
- Surface type: `detail-edit`.
- Surface contract: `my_account.profile.self_service.v1`.
- Owning workstream: My Account.
- Owning functional agent: `my-account-agent`.
- Required context: active authenticated account; selected `AuthContext` for trace and authorization basis.
- Reusable placements: My Account dashboard profile panel, `/api/me` account recovery flows, and role-gated Audit/Trace trace summaries may link here for self-service profile recovery; User Admin may link to the separate admin user-detail surface instead of reusing this surface for privileged account administration.

### User experience model

1. **Identity boundary** — display signed-in account id/email/display name with browser-safe provider/auth facts only.
2. **Editable profile** — show only self-service fields such as display name and user-experience profile fields approved by `update-own-profile-settings`.
3. **Immutable facts** — show read-only authentication, membership, and provider-backed facts with plain-language explanations.
4. **Save safely** — use designed form controls, validation text, disabled/submitting states, idempotency, no-op feedback, and trace-linked result messages.
5. **Deny safely** — unsupported fields, role/capability changes, account status changes, and provider-secret edits return validation/denial system messages without mutation.

### Frontend-safe payload

- `surfaceContract`, `recordId`, `recordLabel`, `profileSummary`, `providerBoundarySummary`, `fields[]`, `permissionState`, `audit`, `traceRefs`, `correlationId`.
- `fields[]`: field id, label, value, input type, editable flag, helper text, validation constraints, disabled reason, browser-safe options where applicable.

Forbidden payload/content:

- Passwords, raw JWT/session data, WorkOS/provider secrets, raw identity-provider payloads, hidden memberships, role assignment controls, support-access grants, or admin-only account status controls.

### Actions

| Action | Governed backend capability/tool | Result behavior |
|---|---|---|
| Refresh profile | `my_account.view_summary` / `read-current-account-context` | Reload browser-safe profile surface. |
| Save profile changes | `my_account.update_profile_settings` / `update-own-profile-settings` | Return updated profile, no-op result, validation-error, forbidden, conflict, or failure system message with trace refs. |
| Open related trace | `my_account.view_own_trace_refs` | Render authorized Audit/Trace surface or safe redacted message. |

### State and style expectations

Use tokenized structured-surface form controls from the current style guide, named-theme contract, and component-catalog form anatomy. The surface must not render browser-default controls. It includes loading, empty/limited-profile, ready, submitting, validation-error, forbidden, conflict, stale/reconnect, no-op, success, and failure states. The layout preserves a single visible form purpose, keeps immutable identity facts visually separate from editable self-service fields, exposes inline field errors with accessible descriptions, and collapses trace/audit diagnostics behind role-authorized evidence affordances.

### Specification completeness for implementation

- **Payload schema detail:** implement `profileSummary` with browser-safe `accountId`, `email`, `displayName`, `accountStatus`, optional avatar/color initials metadata, and selected-context label; implement `providerBoundarySummary` as plain-language read-only facts such as authentication provider family, verified email status, and immutable-provider-field explanations without raw provider payload; implement `fields[]` with `fieldId`, `label`, `value`, `inputType`, `editable`, `required`, `constraints`, `helperText`, `disabledReason`, `lastSavedValue`, and optional browser-safe `options[]`; implement `permissionState` with user-facing `canEdit`, `denialReason`, and `saveActionId`; implement `audit`, `traceRefs[]`, and `correlationId` as user-safe summaries only.
- **Authorization and tenant rules:** every refresh and save is evaluated against the signed-in account and selected backend `AuthContext`; the subject account must be the actor's own account, not a selected user from User Admin. Tenant/customer scope and membership visibility come from protected backend context resolution, not client-provided record ids. Unsupported fields, hidden memberships, role/capability changes, account status changes, provider-secret edits, and cross-account edits are denied or omitted without mutation.
- **Actions and result surfaces:** `refresh-profile` reloads the backend-owned surface through `my_account.view_summary`; `save-profile-changes` calls `my_account.update_profile_settings` / `update-own-profile-settings` with correlation and idempotency keys and returns updated surface, no-op, validation-error, forbidden, conflict, stale, or failure system message; `open-related-trace` calls `my_account.view_own_trace_refs` and renders an authorized Audit/Trace target or a safe redacted message. Save requests must submit only changed editable fields and must not include immutable/provider-backed fields.
- **Trace and audit contract:** each read and consequential save links or emits an audit/work trace containing actor account, selected context, self-subject decision, edited field ids only, validation/authorization result, redaction level, result state, and correlation id. Browser-visible trace summaries never expose raw JWTs, provider records, hidden memberships, raw event ids, or provider secrets.
- **Accessibility and responsive expectations:** the form has a stable heading, labeled controls, helper text and inline error associations, keyboard-operable Save/Cancel/Refresh/Open trace controls, clear submitting/disabled semantics, and focus recovery to the first validation error or result message. On narrow viewports, immutable facts, editable fields, and trace summaries stack in that order without changing action ordering.
- **Acceptance and regression tests:** generated/runtime work must cover refresh success, editable display-name/profile-field save, validation error, no-op save, forbidden unsupported-field mutation, conflict/stale recovery, provider-backed immutable facts rendered read-only, tenant/customer context isolation, safe denial for cross-account or admin-only changes, audit/trace/correlation visibility, and frontend secret-boundary checks proving no raw JWT/session/provider payload/hidden membership is rendered or submitted.
- **Surface-description sufficiency review:** yes — this surface definition is sufficiently unambiguous for a developer or generator to implement and review `surface-my-profile` without inventing payload fields, allowed/forbidden actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics. The default view avoids exposing internal implementation details that do not help the signed-in SaaS user inspect safe identity facts, edit allowed profile fields, recover from validation/authorization failures, or understand self-service profile outcomes.

## My settings and named-theme preferences surface

### Intent

`surface-my-settings` lets the signed-in human maintain personal user-experience preferences, especially canonical named-theme selection. It is not a platform configuration page and cannot change authorization, tenant settings, provider settings, model configuration, or hidden notification channels.

### Contract

- Surface id: `surface-my-settings`.
- Surface type: `detail-edit`.
- Surface contract: `my_account.preferences.self_service.v1`.
- Owning workstream: My Account.
- Owning functional agent: `my-account-agent`.
- Required context: active authenticated account; selected `AuthContext` for authorization and trace.
- Reusable placements: My Account dashboard settings panel, named-theme recovery prompts in the workstream shell, and role-gated Audit/Trace trace summaries may link here for self-service preference recovery; tenant branding, provider delivery, and admin policy settings use separate admin/governance surfaces.

### User experience model

1. **Preference boundary** — separate personal preferences from tenant/admin/platform policy.
2. **Named theme selection** — render the selected skills-pack theme ids/names as named choices, not `light`, `dark`, or `system` modes. Theme changes preview immediately in the current browser session by switching documented theme tokens only.
3. **Governed persistence** — Save/Confirm persists the selected theme and other preferences through `update-own-profile-settings`; preview is never proof of persistence.
4. **Recovery** — failed saves restore or clearly preserve unsaved local preview state with recovery instructions.
5. **Notification preference entry** — summarize in-app notification preferences and link to the notification center for category tuning; do not expose external delivery/provider controls here.

### Frontend-safe payload

- `surfaceContract`, `settingsSummary`, `preferredThemeId`, `availableThemes[]`, `locale`, `timeZone`, `notificationPreferenceSummary`, `digestPreferenceSummary`, `fields[]`, `permissionState`, `traceRefs`, `correlationId`.
- `availableThemes[]`: stable theme id, user-facing name, tone metadata for contrast testing only, selected flag.

Forbidden payload/content:

- `system`/`light`/`dark` as primary preference modes, arbitrary CSS injection, tenant-wide branding changes, provider secrets, email/SMS/push/webhook provider controls, hidden categories, model/provider configuration, or authorization changes.

### Actions

| Action | Governed backend capability/tool | Result behavior |
|---|---|---|
| Preview theme | browser-local preview only | Switch documented theme token id locally; no persistence claim and no authorization effect. |
| Save settings | `my_account.update_profile_settings` / `update-own-profile-settings` | Persist allowed self-service settings and return updated surface, no-op, validation-error, forbidden, conflict, or failure feedback. |
| Open notification preferences | `notification.list_my_account_center` | Render notification center with preference summary/tuning. |
| Open related trace | `my_account.view_own_trace_refs` | Render authorized Audit/Trace surface or safe redacted message. |

### State and style expectations

Use the named theme selector anatomy from the current skills-pack component catalog: label, select/radio choice list, helper text for immediate preview, Save/Confirm, and save failure recovery. Include loading, empty/default-preferences, ready, submitting, validation-error, forbidden, stale/reconnect, conflict, no-op, success, preview-unsaved, save-failed, and failure states. The layout keeps theme selection, locale/timezone preferences, and notification/digest summaries visually separate, preserves one primary Save/Confirm action, and collapses trace/audit diagnostics behind role-authorized evidence affordances.

### Specification completeness for implementation

- **Payload schema detail:** implement `settingsSummary` with browser-safe account display name/email, selected context label, current preference state, unsaved-preview indicator, and last-saved timestamp; implement `preferredThemeId` as one of the documented named theme ids only; implement `availableThemes[]` with `themeId`, `themeName`, `toneSummary`, `contrastStatus`, `selected`, and optional `previewTokenRef` without arbitrary CSS; implement `locale` and `timeZone` as user-safe values plus browser-safe option lists; implement `notificationPreferenceSummary` and `digestPreferenceSummary` as compact in-app/category summaries with counts/status only for visible categories; implement `fields[]` with `fieldId`, `label`, `value`, `inputType`, `editable`, `required`, `constraints`, `helperText`, `disabledReason`, `lastSavedValue`, and browser-safe `options[]`; implement `permissionState`, `traceRefs[]`, and `correlationId` as user-safe summaries only.
- **Authorization and tenant rules:** every refresh and save is evaluated for the signed-in account and selected backend `AuthContext`; the subject account is always the actor's own account. Tenant/customer scope, available categories, and preference visibility come from protected backend context resolution, not client-provided ids. Client preview has no authorization effect and cannot claim persistence. Tenant branding, role/capability changes, external delivery/provider settings, hidden categories, model/provider configuration, arbitrary CSS, and cross-account edits are denied or omitted without mutation.
- **Actions and result surfaces:** `preview-theme` is browser-local only and records no durable success claim; `save-settings` calls `my_account.update_profile_settings` / `update-own-profile-settings` with correlation and idempotency keys and returns updated settings, no-op, validation-error, forbidden, conflict, stale, or failure feedback; `open-notification-preferences` calls `notification.list_my_account_center` and renders `surface-my-account-notification-center`; `open-related-trace` calls `my_account.view_own_trace_refs` and renders an authorized Audit/Trace target or safe redacted message. Save requests submit only changed editable preference fields and must not include authorization, provider, hidden notification, or tenant-wide branding fields.
- **Trace and audit contract:** each backend read and consequential save links or emits an audit/work trace containing actor account, selected context, changed preference field ids only, preview-versus-persisted outcome, validation/authorization result, redaction level, result state, and correlation id. Browser-visible traces never expose raw JWTs, provider records, hidden categories, CSS internals, model/provider configuration, raw event ids, or provider secrets.
- **Accessibility and responsive expectations:** the form has a stable heading, labeled theme/locale/timezone controls, helper text and inline error associations, keyboard-operable Preview/Save/Refresh/Open notification/Open trace controls, clear submitting/disabled semantics, and focus recovery to the first validation error or result message. On narrow viewports, theme choices, locale/timezone fields, notification/digest summaries, and trace summaries stack in that order without changing action ordering; named-theme preview must preserve contrast requirements before it is presented as selectable.
- **Acceptance and regression tests:** generated/runtime work must cover refresh success, named-theme preview without persistence claim, save of theme/locale/timezone preferences, validation error for unknown theme or invalid timezone, no-op save, forbidden unsupported-field mutation, conflict/stale recovery, notification-center link routing, tenant/customer context isolation, safe denial for cross-account/admin/provider changes, audit/trace/correlation visibility, and frontend secret-boundary checks proving no raw JWT/session/provider payload/hidden category/arbitrary CSS/model config is rendered or submitted.
- **Surface-description sufficiency review:** yes — this surface definition is sufficiently unambiguous for a developer or generator to implement and review `surface-my-settings` without inventing payload fields, allowed/forbidden actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics. The default view avoids exposing internal implementation details that do not help the signed-in SaaS user preview and persist personal preferences, recover from failed saves, route to in-app notification preferences, or understand self-service settings outcomes.

## Context and authority surface

### Intent

`surface-my-context` explains which tenant/customer/account context is currently governing workstream visibility, agent behavior, browser actions, and trace access. It is an authority-center surface, not an editable profile form and not a client-side permission switch.

### Contract

- Surface id: `surface-my-context`.
- Surface type: `detail-edit` rendered as an authority/context panel.
- Surface contract: `my_account.context_authority.v1`.
- Owning functional agent: `my-account-agent`.
- Required context: backend-selected `AuthContext` from `/api/me` and protected workstream APIs.

### User experience model

1. **Selected authority** — show selected tenant/customer/membership, role basis, browser-safe visible capability ids/counts, support-access state, and redaction profile.
2. **Available contexts** — list only contexts the user is authorized to know about, with status and safe switch target.
3. **Switch safely** — context switching calls the backend `/api/me` or protected shell API with selected context id; it must reload affected workstream surfaces and mark stale surfaces as needed.
4. **Deny safely** — missing, inactive, hidden, or cross-tenant contexts return `not_found_or_redacted` or forbidden recovery without enumerating hidden contexts.
5. **Explain effects** — show that changing context can change visible agents, attention counts, capabilities, traces, and surface results.

### Frontend-safe payload

- `surfaceContract`, `selectedContext`, `authorityBasis`, `roleSummary`, `visibleCapabilitySummary`, `supportAccess`, `availableContexts[]`, `redaction`, `traceRefs`, `correlationId`.
- `availableContexts[]`: selectedContextId, tenant/customer display labels where authorized, membership status, role labels, selectable flag, denial hint if visible, selection action/api reference.

Forbidden payload/content:

- Hidden tenants/customers, inactive memberships not visible to the user, raw permission internals, cross-tenant counts, support-access grants, or editable role/capability controls.

### Actions

| Action | Governed backend capability/tool | Result behavior |
|---|---|---|
| Refresh context | `core.access.me` / `read-current-account-context` | Reload `/api/me`-safe context and memberships. |
| Select context | `core.access.context.select` | Backend-select context, refresh shell bootstrap, mark stale surfaces, and render updated My Account dashboard/context. |
| Open related trace | `my_account.view_own_trace_refs` | Render authorized Audit/Trace surface or safe redacted message. |

### State and style expectations

Render with authority/context panel anatomy: selected-context header, capability summary chips, available-context cards/rows, stale-impact warning, and trace/evidence block. Include loading, no-membership, disabled-account, ready, submitting, forbidden, not_found_or_redacted, stale/reconnect, conflict, success, and failure states.

### Specification completeness for implementation

- **Payload schema detail:** implement `selectedContext` with browser-safe context id, context type, tenant/customer display label where authorized, account display name/email, membership id, membership status, selected-at timestamp or freshness marker, and stale/refresh indicator; implement `authorityBasis` with user-facing role labels, capability count, redaction profile, support/elevated-access summary when visible, and explanation of which selected context governs workstream visibility; implement `roleSummary` as role names and plain-language scope only, never raw provider/group payloads; implement `visibleCapabilitySummary` with total visible capability count, grouped user-facing categories, and optional browser-safe diagnostic ids only in role-gated evidence affordances; implement `supportAccess` with active/expired/not-visible state and recovery hint without grant controls; implement `availableContexts[]` with `contextId`, `contextType`, tenant/customer labels where authorized, membership status, role labels, selectable flag, selected flag, stale indicator, denial hint when visible, selection action id, and last-used/recommended metadata if authorized; implement `redaction`, `traceRefs[]`, and `correlationId` as user-safe summaries only.
- **Authorization and tenant rules:** every refresh and select-context request is evaluated against the signed-in account plus backend-resolved memberships from `/api/me` or protected workstream APIs. The client may request a `selectedContextId`, but the backend decides whether it exists, is active, belongs to the account, and can be used in the current tenant/customer scope. Hidden, inactive, cross-tenant/customer, disabled-account, or support-only contexts are omitted or returned as safe `not_found_or_redacted`/forbidden recovery without enumeration. Context selection must not grant roles, edit membership status, expose hidden capabilities, or mutate provider/auth state.
- **Actions and result surfaces:** `refresh-context` calls `core.access.me` / `read-current-account-context` and returns the current backend-owned authority surface or no-membership/disabled-account recovery; `select-context` calls `core.access.context.select` with correlation/idempotency metadata and returns updated shell bootstrap plus refreshed `surface-my-account-dashboard` or `surface-my-context`, marking already-rendered surfaces stale until refetched; `open-related-trace` calls `my_account.view_own_trace_refs` and renders an authorized Audit/Trace target or safe redacted message. Selection no-ops on the already-selected context and must return a traceable no-op result rather than claiming a new authority grant.
- **Trace and audit contract:** each read and consequential select-context action links or emits an audit/work trace containing actor account, requested context id only when visible/authorized, prior selected context summary, resulting selected context summary, membership/role decision, visible capability category changes, redaction level, stale-surface impact, result state, and correlation id. Browser-visible traces never expose raw JWTs, provider records, hidden tenant/customer names, hidden role internals, raw event ids, or provider secrets.
- **Accessibility and responsive expectations:** the selected authority summary has a stable heading and plain-language explanation, capability/role chips have accessible labels, available-context cards/rows are keyboard-operable single-selection controls, selected/current state is not conveyed by color alone, stale-impact and denial messages are announced as status text, and focus returns to the selected-context header or first error after refresh/selection. On narrow viewports, selected authority, available contexts, stale-impact warning, and trace/evidence blocks stack in that order without changing action ordering.
- **Acceptance and regression tests:** generated/runtime work must cover refresh success for the selected context, listing only authorized available contexts, switching to another authorized context and refreshing shell/surface data, no-op selection of the current context, forbidden/hidden/cross-tenant context denial without enumeration, inactive/disabled/no-membership recovery, stale/reconnect recovery, support-access visibility boundaries, tenant/customer isolation, audit/trace/correlation visibility, visible capability summary changes, and frontend secret-boundary checks proving no raw JWT/session/provider payload/hidden context/hidden role internals are rendered or submitted.
- **Surface-description sufficiency review:** yes — this surface definition is sufficiently unambiguous for a developer or generator to implement and review `surface-my-context` without inventing payload fields, allowed/forbidden actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics. The default view avoids exposing internal implementation details that do not help the signed-in SaaS user understand the selected authority basis, safely switch among authorized contexts, recover from denied/hidden contexts, or understand why workstream visibility and trace access changed.

## My Account notification center surface

### Intent

`surface-my-account-notification-center` exists to help the signed-in human answer one question: **what do I personally need to notice, acknowledge, or come back to in this selected context?**

The surface is a personal in-app triage workspace owned by `my-account-agent`. It is not a notification-platform console, email preference page, provider-readiness dashboard, delivery-attempt audit, or cross-channel administration view. External delivery channels and provider operations must be represented by separate governed platform/admin surfaces.

### Contract

- Surface id: `surface-my-account-notification-center`.
- Surface type: `notification-center`.
- Surface contract: `my_account.notification_center.v1`.
- Owning workstream: My Account.
- Owning functional agent: `my-account-agent`.
- Required context: current authenticated account plus selected tenant/customer membership.
- Channel boundary: `channel: "in_app"`; all counts and items are derived from authorized backend notification state for the current `AuthContext`.

### User experience model

The surface is rebuilt around triage, not infrastructure:

1. **Orient** — show selected-context boundary, unread/visible counts, redaction state, and a plain-language intent statement.
2. **Triage** — group backend-authorized notifications into a primary full-width `Needs attention` area plus compact secondary `Awareness` and `Handled` areas:
   - `Needs attention`: unread, blocked, urgent, warning, or snoozed notifications that still require awareness. This area should use a responsive card grid so multiple active notifications do not become a long single column on desktop.
   - `Awareness`: visible informational notifications that can be read or archived.
   - `Handled`: read, dismissed, archived, expired, or otherwise completed notification records kept only as recent context.
3. **Act** — expose item-level lifecycle actions only: mark read, dismiss, archive, snooze. These actions mutate notification state only and never resolve source attention/tasks/events. On desktop, item actions should wrap horizontally as compact controls rather than stack into a tall button column.
4. **Explain** — each item shows why it is visible: source/workstream label, required capability, trace links, redaction level, and safe open-target hints when present.
5. **Tune** — show a compact in-app preference summary for visible categories only; hidden categories are never enumerated.
6. **Investigate** — expose trace/correlation links for audit, with redaction maintained in browser-safe fields.

### Frontend-safe payload

- `surfaceContract`, `channel`, `unreadCount`, `visibleCount`.
- `items[]`: notification id, title, summary, status, category, priority, origin, redaction level, required capability id, owning workstream id, source refs, target surface ref, lifecycle timestamps, trace refs.
- `preferencesSummary[]`: current user's in-app preference summary for visible categories only.
- `sourceSummary`: counts by visible source/category only.
- `redaction`, `traceRefs`, `correlationId`, `capabilityIds`.

Forbidden payload/content:

- Email preferences or email channel controls.
- Resend/provider configuration, secrets, delivery attempts, channel registry, local/test outbox records.
- SMS, mobile push, webhook, Slack, Teams, or other external-channel controls.
- Hidden workstream/category/count/source identifiers.
- Fixture/mock notifications in normal runtime.

### Actions

| Action | Governed backend capability/tool | Result behavior |
|---|---|---|
| Refresh center | `notification.list_my_account_center` | Reload authorized backend projection. |
| Mark read | `notification.mark_read` | Mark one authorized notification read; return updated center or no-op system message. |
| Dismiss | `notification.dismiss` | Dismiss one authorized notification from the active center; source state is unchanged. |
| Archive | `notification.archive` | Archive one authorized notification; return updated center/history target. |
| Snooze | `notification.snooze` | Snooze one authorized notification within bounded limits and show `snoozedUntil`. |
| Update in-app preferences | `notification.update_preferences` | Update current user's in-app preference summary with audit trace refs. |
| Open source | source capability such as `attention.open_attention_item` | Reauthorize and render the target surface, or return safe `not_found_or_redacted`. |

### State and security expectations

The surface explicitly represents empty, ready, submitting, no-op, forbidden, stale/reconnect, partial-data, provider-fail-closed, failure, and `not_found_or_redacted` states where applicable. All consequential actions carry correlation/idempotency behavior where needed, are authorized server-side, and preserve tenant/customer scoping and audit/work-trace links.

## Personal attention digest/export progress surface

### Intent

`surface-my-account-personal-attention-digest-progress` shows the status of a backend-governed personal attention digest/export request. It exists to make autonomous personal briefing work visible and recoverable; it is not a fake summary, deterministic demo, fixture, or source-attention mutator.

### Contract

- Surface id: `surface-my-account-personal-attention-digest-progress`.
- Surface type: `workflow-status`.
- Surface contract: `my_account.personal_attention_digest.progress.v1`.
- Owning functional agent: `my-account-agent`.
- Required context: current authenticated account plus selected tenant/customer membership.

### Payload and actions

Frontend-safe payload includes `digestTaskId`, `autonomousAgentTaskId`, `status`, `phase`, `summary`, `authorizedAttentionCount`, `evidenceWindow`, `progressEvents[]`, `blockedReason`, `traceRefs`, `correlationId`, `redaction`, and `noDirectMutation`. Hidden workstreams/items are never counted or named.

Actions include start, refresh/read, cancel, and open trace through `my_account.personal_attention_digest.start/read/cancel` and related trace-read capabilities. Results render progress, blocked, result, no-op, denied, or failure surfaces.

### States

Required states: not-started, accepted, queued, working, waiting-for-provider/runtime, waiting-for-human, completed-review-required, cancelled, stale/reconnect, provider-fail-closed, forbidden, conflict, no-op, and failure.

## Personal attention digest/export result surface

### Intent

`surface-my-account-personal-attention-digest-result` is an advisory outcome panel for reviewing a completed personal attention digest/export. It summarizes only authorized personal evidence and links back to source work without resolving or mutating that source work.

### Contract

- Surface id: `surface-my-account-personal-attention-digest-result`.
- Surface type: `outcome-panel`.
- Surface contract: `my_account.personal_attention_digest.result.v1`.
- Owning functional agent: `my-account-agent`.

### Payload and actions

Frontend-safe payload includes `digestTaskId`, `summary`, `recommendations[]`, `materialEvents[]`, `pendingDecisions[]`, `omissions/redactionSummary`, `authorizedSourceCounts`, `confidence/quality notes where available`, `traceRefs`, `sourceSurfaceRefs[]`, `decisionState`, and `correlationId`.

Actions include accept advisory digest, reject advisory digest with required reason, open source item with reauthorization, export/download if policy allows, and open trace. Accept/reject records the user's review decision and does not change source attention/tasks/events.

### States

Required states: loading, ready, partial-data, redacted, accepting/rejecting, validation-error, no-op, accepted, rejected, forbidden, stale/reconnect, conflict, and failure.

## Personal attention digest/export blocked surface

### Intent

`surface-my-account-personal-attention-digest-blocked` explains fail-closed provider/runtime or governed-tool readiness problems for digest/export work. It must be actionable and honest: no deterministic, model-less, fixture, or simulated success may be returned as normal runtime behavior.

### Contract

- Surface id: `surface-my-account-personal-attention-digest-blocked`.
- Surface type: `system-message`.
- Surface contract: `my_account.personal_attention_digest.blocked.v1`.
- Owning functional agent: `my-account-agent`.

### Payload and actions

Payload includes severity, blocker code, user-safe title/message, recovery steps, required capability/tool/provider readiness hints, trace refs, correlation id, redaction note, and `noFakeSuccess: true`. Actions are retry/read state, open trace, and return to dashboard when authorized.

## Open denied / unavailable workstream surface

### Intent

`surface-my-account-open-denied` is the safe recovery surface when a user attempts to open a workstream, source item, context, or surface that is hidden, unavailable, denied, stale, or not found in the selected context.

### Contract

- Surface id: `surface-my-account-open-denied`.
- Surface type: `system-message`.
- Surface contract: `my_account.open_denied.v1`.
- Owning functional agent: `my-account-agent`.

### Payload and actions

Payload includes `decision`, `safeReasonCode`, user-safe message, recovery steps, source action id where visible, target label only when authorized, trace refs, correlation id, and redaction note. Forbidden content includes hidden workstream names, hidden context names, missing role details, protected target ids, and cross-tenant/customer facts.

Actions include return to My Account dashboard, refresh selected context, open request-access guidance if available, and open trace if authorized.

## Common action rules

Every consequential browser action has a stable action id, maps to a governed backend capability/tool, carries correlation and idempotency behavior where needed, and returns a typed result surface, workflow status, outcome panel, decision/system message, markdown response, or safe `not_found_or_redacted`/denial surface. Frontend visibility and disabled state are UX hints only; backend authorization is authoritative.

## Common states

My Account surfaces define loading, empty, ready, submitting, success, validation-error, forbidden, not_found_or_redacted, conflict, stale/reconnect, partial-data, provider-fail-closed, no-op, approval-required where applicable, and failure states. All states preserve selected tenant/customer scoping, browser-safe redaction, trace/correlation links, and recovery guidance.
