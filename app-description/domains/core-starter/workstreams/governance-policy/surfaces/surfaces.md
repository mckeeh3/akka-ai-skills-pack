# Surfaces: Governance/Policy

## Canonical action/tool/capability matrix

All Governance/Policy browser actions use canonical `action-governance-policy-*` ids. Legacy proposal-heavy action names remain historical only unless reintroduced by a future app-description change.

| Surface action | Governed tool | Capability | Result surface | Notes |
|---|---|---|---|---|
| `action-governance-policy-dashboard` | `governance.policy.list` | `governance-policy-lifecycle` | `surface-governance-policy-dashboard` | `surface_action` + `api_call`; simple overview of policies, overrides, recent changes, and safe shortcuts. |
| `action-governance-policy-list` | `governance.policy.list` | `governance-policy-lifecycle` | `surface-governance-policy-inventory` | `surface_action` + `api_call`; all-policy list with search/filter. |
| `action-governance-policy-read-effective` | `governance.policy.read_effective` | `governance-policy-lifecycle` | `surface-governance-policy-effective-detail` | `surface_action` + `api_call`; shows default, override, effective value, winning scope, and decision explanation. |
| `action-governance-policy-set-default` | `governance.policy.set_default` | `governance-policy-lifecycle` | `surface-governance-policy-edit` | `surface_action` + `api_call`; SaaS-owner/defaults context only; requires reason and idempotency. Confirmed `human_chat_tool_plan` may reuse the same governed tool. |
| `action-governance-policy-set-override` | `governance.policy.set_override` | `governance-policy-lifecycle` | `surface-governance-policy-edit` | `surface_action` + `api_call`; tenant-admin override; requires reason and idempotency; active immediately. Confirmed `human_chat_tool_plan` may reuse the same governed tool. |
| `action-governance-policy-reset-override` | `governance.policy.reset_override` | `governance-policy-lifecycle` | `surface-governance-policy-edit` | `surface_action` + `api_call`; tenant-admin reset to inherited/default behavior; requires reason and idempotency. Confirmed `human_chat_tool_plan` may reuse the same governed tool. |
| `action-governance-policy-history` | `governance.policy.read_history` | `governance-policy-lifecycle` | `surface-governance-policy-history` | `surface_action` + `api_call`; change history plus practical runtime outcome links when available. |

## Structured-surface standard block

Applies to every surface below unless a surface states a narrower rule.

- ownerFunctionalAgentId: `governance-policy-agent`.
- reusableByFunctionalAgentIds: none by default; cross-workstream links open this workstream's surfaces through backend-authorized deep links.
- lifecycle status: compile-ready description; runtime-ready is not claimed.
- placement: Governance/Policy workstream shell route/panel; detail/edit/history surfaces may open inline, side-panel, or deep-link according to frontend realization.
- data source: governed tools in `../tools/governed-tools.md`; browser payloads are frontend-safe DTOs, not raw entity state.
- actor adapters: human surface actions use `surface_action`; protected endpoints use `api_call`; confirmed command plans use separate `human_chat_tool_plan`; agent read tools use `agent_tool_call` only when explicitly granted.
- action shape: every action includes `browserToolId` equal to its surface action id, `governedToolId`, `capabilityId: governance-policy-lifecycle`, input schema ref, idempotency requirements, result surface, and audit event type.
- events/reconnect: stale or reconnect events mark affected surfaces stale and require refresh or server replay; malformed/duplicate/cross-context events are safe no-ops with diagnostics.
- style/catalog binding: use `../../../../../global/surfaces/ui-style-and-runtime-contracts.md` and the standard workstream shell/component catalog; avoid exposing backend ids as primary user copy.
- system feedback: denial, validation, stale, conflict, and provider/configuration failures render typed `system_message` surfaces.

## Surface contracts

### `surface-governance-policy-dashboard` (`governance.policy.dashboard.v1`)

Pattern: `workstream-dashboard`.

Purpose: give authorized SaaS owners, tenant admins, auditors, and support users a simple entry point into policy settings, overridden policies, recent changes, and effective-policy troubleshooting.

Required payload schema:

- `summary`: selected context label, actor authority summary, total visible policies, overridden count, recently changed count, and safe empty-state copy.
- `cards`: actionable cards for all policies, overridden policies, recent changes, and policy history.
- `authorizedActions`: backend-authorized actions for list, read-effective, default management, override management, reset, and history.
- `recentActivity`: redacted direct policy changes with actor display summary, policy name, scope summary, old/new/effective value summaries, reason, timestamp, and trace link.
- `redaction`: field-level indicators for hidden cross-tenant/customer/account details, raw secrets, raw provider/model data, raw prompts, raw tool payloads, JWTs, raw correlation ids, and idempotency keys.

Required states: loading, empty, ready, forbidden/`system_message`, stale/reconnect, partial-data, and failure.

### `surface-governance-policy-inventory` (`governance.policy.inventory.v1`)

Pattern: `list-search`.

Purpose: show all visible policy definitions and effective settings with simple search/filter.

Required payload schema:

- `inventorySummary`: selected scope label, total visible count, filtered count, overridden count, freshness state, and safe empty-state copy.
- `filters`: backend-allowed filters for policy name/search text, workstream, agent, tool/action, and role.
- `sortAndPage`: allowed sort keys, current sort, page size, and safe cursor state.
- `rows`: visible policy rows with policy id/display name, description, value type, SaaS default value, tenant override value when visible, effective value, overridden indicator, supported scopes, current winning scope, affected workstream/agent/tool/action/role/customer/account summaries, last changed summary, required reason marker for writes, target detail/edit/history surfaces, and backend-authorized row action ids.
- `authorizedActions`: only actions backend authorizes for the selected actor/context.
- `redaction`: field-level indicators for hidden scopes and protected data.

Required states: loading, empty, ready, filter-validation-error, forbidden/`system_message`, stale/reconnect, partial-data, and failure.

### `surface-governance-policy-effective-detail` (`governance.policy.effective-detail.v1`)

Pattern: collection-object show/inspection surface for a policy setting.

Purpose: explain one policy's current default, override, effective value, scope precedence, and runtime decision semantics.

Required payload schema:

- `detailSummary`: policy id/name, description, value type, selected scope, effective value, overridden indicator, and freshness state.
- `valueBreakdown`: SaaS default value, applicable tenant overrides by visible scope, winning scope, precedence explanation, reset availability, and last changed summary.
- `decisionExplanation`: browser-safe explanation of how this policy would be applied at runtime for the selected context.
- `authorizedActions`: edit default, set override, reset override, open history, or return to inventory only when backend-authorized.
- `traceLinks`: user-readable policy-decision/admin-audit/workstream trace summaries.
- `redaction`: field-level indicators for hidden scope details and protected data.

Required states: loading, ready, read-only, forbidden/`system_message`, not-found-or-redacted, conflict/stale, partial-data, and failure.

### `surface-governance-policy-edit` (`governance.policy.edit.v1`)

Pattern: collection-object edit surface for a policy setting.

Purpose: allow authorized SaaS owners to update defaults and tenant admins to set/reset tenant overrides for simple policy values.

Required payload schema:

- `editSummary`: policy id/name, value type, selected scope, current default, current override when visible, effective value, action mode (`set_default`, `set_override`, or `reset_override`), and expected post-change effective behavior.
- `fields`: value input for boolean or counter/limit when applicable, required reason, freshness token, and idempotency key.
- `validation`: field-level errors for unsupported value, unsupported scope, missing reason, stale version, hidden target, or hard-platform-security denial.
- `result`: refreshed effective-policy summary, history reference, and trace reference after successful write.
- `redaction`: indicators for hidden scopes and protected data.

Required states: loading, ready, editing, submitting, validation-error, forbidden/`system_message`, conflict/stale, success, and failure.

### `surface-governance-policy-history` (`governance.policy.history.v1`)

Pattern: `audit-timeline` / lifecycle history surface.

Purpose: show direct policy changes and practical runtime outcome links for tenant admins, auditors, and SaaS owner support.

Required payload schema:

- `historySummary`: selected policy/scope filters, visible change count, visible runtime outcome count when available, and freshness state.
- `changeEvents`: chronological events with actor display summary, policy id/name, action (`set_default`, `set_override`, `reset_override`), scope summary, old value, new value, effective value, reason, timestamp, idempotency/redacted trace marker, and trace link.
- `runtimeOutcomes`: optional aggregated or linked policy-decision outcomes influenced by this policy, such as allowed/denied/governed counts or trace summaries, without exposing hidden protected details.
- `filters`: policy name/search, workstream, agent, tool/action, role, actor, changed window, and overridden/default state where backend-supported.
- `redaction`: field-level indicators for hidden scope details and protected data.

Required states: loading, empty, ready, filter-validation-error, forbidden/`system_message`, stale/reconnect, partial-data, and failure.

## Authorization and tenant scope

Backend resolves SaaS-owner/defaults context, tenant/customer/account authority, and row/action visibility from selected `AuthContext`. Browser-provided tenant/customer/account ids, policy ids, filters, and scopes are hints only. Direct/deep-link denials return safe `system_message` surfaces with no hidden target enumeration.

## Trace, audit, and work evidence

Every read/open, filter request, write, reset, denial, stale/conflict result, and runtime policy decision emits workstream-log/admin-audit/policy-decision evidence as appropriate. Default browser trace copy is human-readable; raw ids and protected details are role-gated.

## Accessibility, responsive, and UI realization

Use the selected web UI style guide and component catalog for dashboards, list/search, detail inspection, settings edit, and timeline history surfaces. Controls are keyboard-operable, have accessible names, preserve focus after refresh/action results, and do not rely on color alone for overridden/default/effective states.

## Required tests

- App-description/contract tests prove payload schemas, actions, auth/scope rules, states, trace links, redaction, and simple-policy sufficiency.
- Frontend tests prove list/search filters, overridden indicators, effective detail, edit validation, history rendering, forbidden/`system_message` states, secret-boundary redaction, keyboard behavior, and responsive rendering.
- Backend/API tests prove selected AuthContext scoping, SaaS-owner default writes, tenant-admin overrides, reset-to-default, default update without tenant override overwrite, finer-grained precedence, required reason validation, idempotency, hard-platform-security denials, and trace/history evidence.

Surface-description sufficiency review: these definitions are sufficiently unambiguous for a focused first implementation slice of SMB-friendly Governance/Policy settings without inventing proposal/approval/simulation workflows.
