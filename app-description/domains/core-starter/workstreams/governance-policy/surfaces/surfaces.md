# Surfaces: Governance/Policy

## Canonical action/tool/capability matrix

All Governance/Policy browser actions use the canonical `action-governance-policy-*` ids below. Legacy fixture aliases such as `action-govpol-*` are retired and must not appear in current fixtures, runtime payloads, generated clients, or tests.

| Surface action | Governed tool | Capability | Result surface | Notes |
|---|---|---|---|---|
| `action-governance-policy-dashboard` | `list-policy-proposals` | `governance.policy.read` | `surface-governance-policy-dashboard` | Read-only scoped dashboard. |
| `action-governance-policy-list` | `list-policy-proposals` | `governance.policy.read` | `surface-governance-policy-inventory` | Scoped inventory and proposal queue. |
| `action-governance-policy-read` | `list-policy-proposals` | `governance.policy.read` | `surface-governance-policy-detail` | Browser-safe policy/proposal evidence. |
| `action-governance-policy-draft-proposal` | `draft-policy-proposal` | `governance.policy.propose` | `surface-governance-policy-proposal` | Inert draft; requires idempotency. |
| `action-governance-policy-submit-proposal` | `draft-policy-proposal` | `governance.policy.propose` | `surface-governance-policy-proposal` | Moves draft into review; no authority changes. |
| `action-governance-policy-simulate` | `simulate-policy-change` | `governance.policy.simulate` | `surface-governance-policy-simulation` | Advisory evidence only. |
| `action-governance-policy-decide` | `approve-activate-or-rollback-policy` | `governance.policy.approve` | `surface-governance-policy-decision` | Human approve/reject/request-changes. |
| `action-governance-policy-activate` | `approve-activate-or-rollback-policy` | `governance.policy.activate` | `surface-governance-policy-decision` or `system-message` | Separate command; requires approved proposal, simulation evidence, rollback metadata, idempotency, and backend authority. |
| `action-governance-policy-rollback` | `approve-activate-or-rollback-policy` | `governance.policy.rollback` | `surface-governance-policy-decision` or `system-message` | Separate command; requires activated proposal and rollback metadata. |
| `action-governance-policy-outcome-note` | `record-policy-outcome-note` | `governance.outcomes.record` | `surface-governance-policy-outcome` | Outcome panel with feedback/metrics/evidence. |
| `action-governance-policy-start-impact-analysis` | `start-policy-impact-analysis` | `governance.policy.impact_analysis.start` | `surface-governance-policy-impact-analysis-task` | Durable advisory autonomous-agent task path; requires proposal id, idempotency key, scope, and reason; fail closed when provider/runtime is unavailable. |
| `action-governance-policy-read-impact-analysis` | `read-policy-impact-analysis` | `governance.policy.impact_analysis.read` | `surface-governance-policy-impact-analysis-task` | Reads backend task projection for the selected context. |
| `action-governance-policy-cancel-impact-analysis` | `cancel-policy-impact-analysis` | `governance.policy.impact_analysis.cancel` | `surface-governance-policy-impact-analysis-task` | Cancels queued/running task; policy proposal state and authority remain unchanged. |
| `action-governance-policy-accept-impact-result` | `accept-policy-impact-result` | `governance.policy.impact_analysis.accept_result` | `surface-governance-policy-impact-analysis-result` | Records advisory evidence disposition only; does not approve or activate policy. |
| `action-governance-policy-reject-impact-result` | `reject-policy-impact-result` | `governance.policy.impact_analysis.reject_result` | `surface-governance-policy-impact-analysis-result` | Requires reason; no policy activation. |
| `action-governance-policy-request-impact-changes` | `request-policy-impact-changes` | `governance.policy.impact_analysis.request_changes` | `surface-governance-policy-impact-analysis-result` | Requires reason; no policy activation. |

## Surface contracts

### `surface-governance-policy-dashboard` (`governance.policy.dashboard.v1`)

Pattern: `workstream-dashboard`.

Owning workstream: Governance/Policy. Owning functional agent: `governance-policy-agent`. Reusable placements: foundation workstream shell and audit/trace drilldowns may deep-link to this dashboard only when the selected `AuthContext` grants Governance/Policy read authority. Purpose: action router for policy governance attention, proposal lifecycle work, simulation/review blockers, and impact-analysis tasks.

User goal: understand what governance policy work needs attention, open the appropriate scoped inventory/proposal/decision/task surface, and recover from missing provider/runtime or authorization states without exposing sensitive policy internals.

Dashboard ordering and surface graph: ready content is ordered top-to-bottom as **things that need my attention** followed by **things I can do**. Every visible card, counter, queue row, lifecycle segment, recent-activity item, or shortcut is keyboard-operable and declares the target surface/action it opens. Passive FYI-only metrics are omitted unless they open a scoped queue, explanation, setup, or history surface. Forbidden targets are omitted for ordinary users and returned as a safe `surface-governance-policy-system-message` for direct/deep-link attempts.

Required payload schema (frontend-safe):

- `cards`: actionable dashboard cards with `cardId`, label, count/status, severity, target surface id, target action id when protected, and safe empty-state explanation.
- `attentionQueues`: queues for submitted proposals awaiting review, simulations required/blocked, activation or rollback prerequisites, provider/runtime blockers, outcome notes needing follow-up, and impact-analysis tasks requiring human disposition; each queue item includes proposal/task display title, lifecycle/status, safe risk summary, target surface, authorized action ids, and redaction marker.
- `attentionItems`: high-priority flattened items derived from the queues with reason code, due/age bucket when available, target surface/action, and safe recovery copy.
- `authorizedActions`: only backend-authorized read/open/start actions available for the selected actor, including `action-governance-policy-dashboard`, `action-governance-policy-list`, `action-governance-policy-draft-proposal`, `action-governance-policy-simulate`, `action-governance-policy-decide`, and `action-governance-policy-start-impact-analysis` when the selected `AuthContext` permits the corresponding capability.
- `recentActivity`: redacted policy/proposal activity with user-safe titles, lifecycle labels, actor display summary, trace summary link, and target detail/proposal/outcome surface.
- `proposalLifecycle`: clickable lifecycle counts for `draft`, `submitted`, `simulation-required`, `in-review`, `changes-requested`, `approved`, `rejected`, `activated`, `rollback-candidate`, `rolled-back`, and `superseded`; each segment opens `surface-governance-policy-inventory` with a scoped filter.
- `capabilityIds`: diagnostic-only capability summaries visible to governance admins/auditors/support; ordinary users see human-readable authority labels instead of raw ids.
- `traceLinks`: user-readable trace summaries plus role-gated drilldowns to policy-decision, admin-audit, workstream-log, agent-work, impact-analysis task, and denial/failure traces.
- `redaction`: field-level indicators for hidden cross-tenant/customer details, privileged evidence, raw provider/model output, raw tool payloads, prompts, JWTs, secrets, correlation ids, and idempotency keys.
- `readiness`: provider/runtime/configuration status for dashboard-backed advisory features; blocked model/provider/autonomous-agent dependencies are represented as blocked states and never as successful analysis.

Visibility split:

- Default user-visible fields: queue/card labels, counts, lifecycle/status labels, safe risk or blocker summaries, recovery instructions, and available next actions.
- On-demand drilldown fields: proposal/task ids as diagnostic metadata, evidence summaries, redacted trace summaries, lifecycle history, and scoped activity details.
- Admin/support/auditor-only fields: capability ids, trace ids, policy-decision trace refs, admin-audit refs, workstream-log refs, agent-work refs, impact-analysis task refs, and denial/failure evidence.
- Internal-only metadata never rendered in ordinary browser payloads: backend component names, raw provider/model data, raw prompts, raw governed-tool payloads, hidden role policy state, cross-tenant identifiers, JWTs/secrets, and correlation/idempotency implementation details.

Required states: loading, empty, ready, forbidden/system-message, stale/reconnect, partial-data, blocked-provider-or-runtime.

State semantics:

- `loading`: shell has selected workstream and is fetching the protected dashboard projection.
- `empty`: actor is authorized, but no visible proposals, queues, tasks, or recent activity exist; render setup/start actions the backend authorizes.
- `ready`: all visible cards, queues, lifecycle segments, and actions are backed by the protected Governance/Policy read projection.
- `forbidden/system-message`: missing bearer token, missing selected context, missing `governance.policy.read`, or tenant/customer scope denial returns `surface-governance-policy-system-message` with `noFakeSuccess=true` and `noDirectMutation=true`.
- `stale/reconnect`: projection or trace link freshness cannot be confirmed; disable side-effecting shortcuts until refreshed.
- `partial-data`: some queues or trace summaries are unavailable; visible sections must identify omitted data and link to safe recovery.
- `blocked-provider-or-runtime`: provider/autonomous-agent runtime needed for advisory impact analysis or simulation summaries is unavailable; show blocked queues/recovery and do not fabricate analysis.

Action contract:

| Visible action / target | Browser action id | Governed tool | Capability | Request payload | Result surface | Notes |
|---|---|---|---|---|---|---|
| Refresh/open dashboard | `action-governance-policy-dashboard` | `list-policy-proposals` | `governance.policy.read` | selected workstream/context, optional queue filter, correlation key generated by client or backend | `surface-governance-policy-dashboard` | Read-only; never trusts tenant/customer ids from the browser. |
| Open proposal inventory or lifecycle segment | `action-governance-policy-list` | `list-policy-proposals` | `governance.policy.read` | lifecycle/status filter, search/sort/page hints, correlation key | `surface-governance-policy-inventory` | Segment `0` states open an empty filtered inventory with recovery copy. |
| Open selected proposal/detail evidence | `action-governance-policy-read` | `list-policy-proposals` | `governance.policy.read` | proposal id/display ref, correlation key | `surface-governance-policy-detail` | Detail content is redacted by backend scope. |
| Draft a proposal from dashboard shortcut | `action-governance-policy-draft-proposal` | `draft-policy-proposal` | `governance.policy.propose` | draft intent/source, idempotency key, reason/rationale when present, correlation key | `surface-governance-policy-proposal` | Inert draft only; no authority changes. |
| Open/start simulation attention item | `action-governance-policy-simulate` | `simulate-policy-change` | `governance.policy.simulate` | proposal id, scenario/scope, idempotency key for start, correlation key | `surface-governance-policy-simulation` | Advisory; cannot grant authority. |
| Open decision/activation/rollback attention item | `action-governance-policy-decide` | `approve-activate-or-rollback-policy` | `governance.policy.approve` | proposal id, command mode `decide`, reason, idempotency key when command submitted, correlation key | `surface-governance-policy-decision` | Dashboard may route to the decision card but cannot approve, activate, or roll back client-side. |
| Start or open impact analysis task | `action-governance-policy-start-impact-analysis` / `action-governance-policy-read-impact-analysis` | `start-policy-impact-analysis` / `read-policy-impact-analysis` | `governance.policy.impact_analysis.start` / `governance.policy.impact_analysis.read` | proposal id, task id when reading, scope, reason, idempotency key for start, correlation key | `surface-governance-policy-impact-analysis-task` | Fail closed with blocked_provider_or_runtime when provider/runtime is unavailable. |

Hidden or denied actions: proposal approval, activation, rollback, impact-result acceptance/rejection/request-changes, and outcome-note recording are not performed directly by dashboard cards. The dashboard may open their dedicated authorized surfaces only when backend projection exposes those actions. Direct attempts without authority return `surface-governance-policy-system-message` and are audit/trace recorded.

Authorization and tenant scope:

- Backend resolves tenant/customer/workspace authority from the selected `AuthContext`; browser-provided tenant/customer ids are treated only as display/filter hints and cannot expand scope.
- `governance.policy.read` is required for dashboard reads. Proposal, simulation, approval, activation, rollback, outcome, and impact-analysis capabilities are exposed only as authorized actions returned by the backend projection.
- Cross-tenant/customer proposal evidence, hidden authority state, raw policy internals, provider secrets, JWTs, prompts, raw tool payloads, and correlation/idempotency implementation details are redacted or omitted.
- Direct/deep-link denials must include a safe reason code, recovery-oriented message, trace refs, `noFakeSuccess=true`, and `noDirectMutation=true`.

Trace, audit, and work evidence:

- Required trace refs: `policy-decision-trace`, `admin-audit-event`, `workstream-log-trace`, `agent-work-trace`, `impact-analysis worker/task events`, plus denial/failure traces.
- Read/open actions produce workstream-log/correlation evidence; side-effecting shortcuts produce idempotency and admin-audit evidence where applicable.
- Trace links are summarized in user language by default and expose raw ids/details only through role-gated drilldowns.
- Provider/runtime failures are traceable and fail closed; no dashboard state may imply successful model-backed simulation or impact analysis when the provider/runtime path did not run.

Accessibility, responsive, and UI realization:

- Use the selected web UI style guide, named-theme contract, and component-catalog anatomy for workstream dashboards; this surface contract owns semantics and authority, while frontend realization owns layout/styling.
- Cards, lifecycle counters, queue rows, and shortcuts are keyboard-operable, have accessible names describing the target work, preserve focus after refresh/action results, and expose safe empty/forbidden/error copy.
- Responsive layouts preserve the attention-first ordering and do not hide required recovery or denial information.

Required tests:

- App-description/contract tests prove the dashboard contract includes payload, actions, auth/tenant rules, states, traces, redaction, and sufficiency review.
- Frontend tests prove attention-first rendering, empty/ready/partial/stale/blocked-provider-or-runtime/system-message states, authorized action visibility, forbidden target omission, secret-boundary redaction, and keyboard navigation to target surfaces.
- Backend/API tests prove scoped dashboard reads, selected AuthContext tenant/customer isolation, missing-bearer and missing-capability denials, authorized action filtering, lifecycle queue filters, trace/audit/work evidence, and provider/runtime fail-closed status for advisory queues.
- Negative/idempotency tests prove dashboard shortcuts do not approve, activate, roll back, mutate authority client-side, or duplicate side effects on repeated start/draft requests.

Surface-description sufficiency review: this definition is sufficiently unambiguous for a developer or generator to implement and review `surface-governance-policy-dashboard` without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics. No additional description pass is required before scoped implementation work for this dashboard.

### `surface-governance-policy-inventory` (`governance.policy.inventory.v1`)

Pattern: `list-search`.

Owning workstream: Governance/Policy. Owning functional agent: `governance-policy-agent`. Reusable placements: opened from `surface-governance-policy-dashboard` lifecycle counters, dashboard queue cards, proposal/detail breadcrumbs, audit/trace drilldowns, and direct deep links only when the selected `AuthContext` grants Governance/Policy read authority. Purpose: discover, filter, and open authorized policy/proposal work without exposing hidden policy internals or granting authority from the browser.

Collection-object progression role: this is the domain list/search surface for the policy/proposal collection. Row selection opens a lifecycle-aware inspection surface (`surface-governance-policy-detail`) or the specific task surface named by the backend row action (`surface-governance-policy-proposal`, `surface-governance-policy-simulation`, `surface-governance-policy-decision`, `surface-governance-policy-outcome`, `surface-governance-policy-impact-analysis-task`, or `surface-governance-policy-impact-analysis-result`). Draft/create, edit-like proposal changes, approval/activation/rollback, outcome notes, impact-analysis review, and rollback/destructive-lifecycle recovery are separate governed surfaces/actions and are not performed inline by the inventory.

User goal: find the policy/proposal items the actor is allowed to see, understand lifecycle and blocker status, open the next authorized work surface, and recover safely when filters, scope, provider/runtime readiness, or authority prevent showing or acting on a proposal.

Data source and backend authority: the payload is produced by the protected Governance/Policy policy proposal inventory projection for the backend-resolved tenant/customer/workspace scope. Browser-provided tenant/customer ids, lifecycle filters, search text, sort keys, page cursors, proposal ids, and correlation keys are hints only; the backend re-resolves selected `AuthContext`, capabilities, row visibility, trace visibility, redaction, and action availability before returning rows or target surfaces.

Required payload schema (frontend-safe):

- `inventorySummary`: selected workstream/context label, scope label, total visible count, filtered count, lifecycle bucket counts, blocked/provider-runtime count, selected filters summary, freshness state, and safe empty-state copy.
- `filters`: backend-allowed filter definitions for lifecycle/status, type, source artifact, affected capability family, risk class, reviewer/owner display label, blocked state, recent activity window, and search text; each filter includes stable field id, label, allowed values, selected values, and validation messages.
- `sortAndPage`: allowed sort keys, current sort, page size, next/previous cursor presence, stale cursor recovery copy, and no raw database cursor or implementation key in the default payload.
- `rows`: policy/proposal rows with `proposalRef` or policy display id, title/name, type, lifecycle/status label, risk classification, affected capability summaries, source artifact summary, owner/reviewer display summary, simulation/evidence status, approval/activation/rollback readiness, outcome/impact-analysis task summary, last activity age, safe trace summary, row redaction markers, row target surface, and backend-authorized row action ids.
- `emptyStates`: zero-result copy for no visible proposals, filter-matched zero results, stale cursor, denied direct proposal, provider/runtime-blocked advisory status, and partial-data omission.
- `authorizedActions`: only actions the backend authorizes for the selected actor, including `action-governance-policy-list`, `action-governance-policy-read`, `action-governance-policy-draft-proposal`, `action-governance-policy-simulate`, `action-governance-policy-decide`, `action-governance-policy-start-impact-analysis`, `action-governance-policy-read-impact-analysis`, and `action-governance-policy-outcome-note` when each corresponding capability is granted and the target row/lifecycle permits it.
- `traceLinks`: user-readable trace summaries plus role-gated references to policy-decision trace, admin-audit event, workstream-log trace, agent-work trace, impact-analysis task events, and denial/failure traces.
- `redaction`: field-level indicators for hidden cross-tenant/customer rows, hidden policy authority state, privileged evidence, raw provider/model output, raw prompts, raw governed-tool payloads, JWTs, secrets, raw correlation ids, and idempotency keys.
- `readiness`: provider/runtime/configuration state for advisory simulation and impact-analysis statuses represented as blocked/unknown/ready summaries; unavailable providers or autonomous-agent runtime are never rendered as successful analysis.

Visibility split:

- Default user-visible fields: proposal title/display ref, lifecycle/status labels, safe type/risk/source summaries, affected capability summaries, visible counts, readiness/blocker summaries, authorized next action labels, and recovery instructions.
- On-demand drilldown fields: proposal display ids, evidence summaries, lifecycle history, outcome note summaries, redacted trace summaries, reviewer/owner display labels, and scoped source artifact details.
- Admin/support/auditor-only fields: capability ids, policy-decision trace refs, admin-audit refs, workstream-log refs, agent-work refs, impact-analysis task refs, denial/failure evidence, and redaction reasons.
- Internal-only metadata never rendered in ordinary browser payloads: raw provider/model data, prompts, governed-tool payloads, backend component names, hidden role policy clauses, raw database cursors, cross-tenant identifiers, JWTs/secrets, correlation ids, and idempotency implementation details.

Required states: loading, empty, ready, filter-validation-error, forbidden/system-message, stale/reconnect, partial-data, blocked-provider-or-runtime, and failure.

State semantics:

- `loading`: workstream shell has selected Governance/Policy and is fetching the protected inventory projection.
- `empty`: actor is authorized, but no visible proposals match the current scope or filters; show safe setup/start actions the backend authorizes.
- `ready`: rows, filters, lifecycle counts, and action edges are backed by the protected projection for the selected `AuthContext`.
- `filter-validation-error`: unsupported sort/filter/page hints are rejected or normalized by the backend and returned with safe corrective copy.
- `forbidden/system-message`: missing bearer token, missing selected context, missing `governance.policy.read`, direct proposal access denial, or tenant/customer scope denial returns `surface-governance-policy-system-message` with `noFakeSuccess=true`, `noDirectMutation=true`, and no hidden proposal enumeration.
- `stale/reconnect`: cursor, projection freshness, or trace freshness cannot be confirmed; disable side-effecting shortcuts and offer refresh.
- `partial-data`: some row evidence, trace summaries, or provider readiness details are omitted; visible sections identify what was omitted and why.
- `blocked-provider-or-runtime`: advisory simulation or impact-analysis status cannot be produced because provider/autonomous-agent runtime is unavailable; display blocked status and recovery, not fabricated analysis.
- `failure`: unexpected read failure returns a safe system message with trace/audit reference and no raw exception, token, provider, or storage details.

Action contract:

| Visible action / target | Browser action id | Governed tool | Capability | Request payload | Result surface | Notes |
|---|---|---|---|---|---|---|
| Refresh/open inventory | `action-governance-policy-list` | `list-policy-proposals` | `governance.policy.read` | selected context/workstream, optional lifecycle/search/filter/sort/page hints, correlation key generated by client or backend | `surface-governance-policy-inventory` | Read-only; backend ignores tenant/customer expansion from the browser. |
| Open row detail/evidence | `action-governance-policy-read` | `list-policy-proposals` | `governance.policy.read` | proposal display ref, optional source row id, correlation key | `surface-governance-policy-detail` | Backend reauthorizes row visibility and redacts evidence by selected scope. |
| Draft new proposal from inventory | `action-governance-policy-draft-proposal` | `draft-policy-proposal` | `governance.policy.propose` | draft intent/source, optional template id, reason/rationale, idempotency key, correlation key | `surface-governance-policy-proposal` | Creates or opens an inert draft only; no authority changes. |
| Open simulation task for row | `action-governance-policy-simulate` | `simulate-policy-change` | `governance.policy.simulate` | proposal id/display ref, scenario/scope, idempotency key when starting, correlation key | `surface-governance-policy-simulation` | Advisory only; blocked provider/runtime returns safe blocked state. |
| Open decision/activation/rollback work | `action-governance-policy-decide` | `approve-activate-or-rollback-policy` | `governance.policy.approve` | proposal id/display ref, command mode `decide`, reason when supplied, idempotency key for command submission, correlation key | `surface-governance-policy-decision` | Inventory opens the decision surface; it does not approve, activate, or roll back inline. |
| Start/read impact analysis | `action-governance-policy-start-impact-analysis` / `action-governance-policy-read-impact-analysis` | `start-policy-impact-analysis` / `read-policy-impact-analysis` | `governance.policy.impact_analysis.start` / `governance.policy.impact_analysis.read` | proposal id/display ref, task id when reading, scope, reason, idempotency key for start, correlation key | `surface-governance-policy-impact-analysis-task` | Durable advisory task path; provider/runtime unavailable returns blocked_provider_or_runtime with no fake success. |
| Record/open outcome note | `action-governance-policy-outcome-note` | `record-policy-outcome-note` | `governance.outcomes.record` | proposal id/display ref, observation summary or open intent, idempotency key when recording, correlation key | `surface-governance-policy-outcome` | Outcome observations do not change authority or lifecycle state. |

Hidden or denied actions: approval, activation, rollback, impact-result acceptance/rejection/request-changes, and outcome-note mutation are exposed only when the backend returns a row-specific authorized action and valid lifecycle state. Direct/deep-link attempts without authority return `surface-governance-policy-system-message` and must not reveal whether hidden proposals, policies, capabilities, or cross-tenant/customer evidence exists.

Authorization and tenant scope:

- `governance.policy.read` is required for inventory reads and row opens.
- Proposal creation, simulation, approval/decision, activation, rollback, impact-analysis, and outcome capabilities are exposed only as backend-authorized actions for visible rows and selected lifecycle states.
- The backend resolves tenant/customer/workspace scope from selected `AuthContext`; browser filters cannot expand scope, enumerate hidden proposals, or request raw authority state.
- Rows and trace drilldowns redact hidden cross-tenant/customer details, privileged policy evidence, raw provider/model content, prompts, raw tool payloads, JWTs, secrets, and implementation correlation/idempotency details.
- Direct proposal id, stale cursor, or hidden row requests return safe no-enumeration denial or empty filtered inventory states with trace refs.

Trace, audit, and work evidence:

- Inventory read/refresh, filter changes that hit the backend, direct row opens, denials, stale cursor recovery, and every row action produce workstream-log/correlation evidence.
- Consequential row actions produce admin-audit and policy-decision traces where applicable; impact-analysis actions link agent-work/task events; denial/provider/runtime failures link failure traces.
- Default trace copy is human-readable; raw ids/details are visible only through role-gated audit/support drilldowns.
- Repeated draft/start/outcome commands require idempotency evidence; read-only inventory refresh is safe to repeat.

Accessibility, responsive, and UI realization:

- Use the selected web UI style guide, named-theme contract, and component-catalog list/search, filter bar, table/card, badge, empty-state, system-message, action-menu, and pagination anatomy.
- Rows, cards, filter chips, lifecycle counters, pagination controls, and row action menus are keyboard-operable, announce selected filters/status/action targets, preserve focus after refresh/action results, and provide accessible names for target surfaces.
- Responsive layouts may switch between table and cards but must preserve the same backend-authored row actions, redaction markers, filter semantics, recovery states, and attention ordering.

Required tests:

- App-description/contract tests prove the inventory contract includes payload schema, filters/sort/page, row action mapping, auth/tenant rules, states, traces, redaction, and sufficiency review.
- Frontend tests prove list/search rendering, empty/ready/filter-error/stale/partial/blocked-provider-or-runtime/system-message states, backend-authorized action visibility, row keyboard navigation, direct-denial recovery, and secret-boundary redaction.
- Backend/API tests prove selected AuthContext scoping, missing-bearer and missing-capability denials, no-enumeration direct proposal access, lifecycle filters, pagination validation/stale cursor behavior, row action authorization, trace/audit/work evidence, idempotency for side-effecting row actions, and provider/runtime fail-closed advisory statuses.

Surface-description sufficiency review: this definition is sufficiently unambiguous for a developer or generator to implement and review `surface-governance-policy-inventory` without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics. The default view avoids internal implementation details that do not help the target SaaS user, and no additional description pass is required before scoped implementation work for this inventory surface.

### `surface-governance-policy-detail` (`governance.policy.detail.v1`)

Pattern: `show-inspection`.

Owning workstream: Governance/Policy. Owning functional agent: `governance-policy-agent`. Reusable placements: opened from `surface-governance-policy-inventory` rows, dashboard attention queues, proposal breadcrumbs, decision/simulation/outcome/impact-analysis follow-ups, and Audit/Trace drilldowns only when the selected `AuthContext` grants Governance/Policy read authority. Purpose: inspect one visible policy or policy proposal as read-only browser-safe evidence and choose the next authorized governed surface without editing, approving, activating, rolling back, or expanding authority inline.

Collection-object progression role: this is the lifecycle-aware show/inspection surface for policy and proposal records. Create/edit-like work routes to `surface-governance-policy-proposal`; advisory evidence routes to `surface-governance-policy-simulation` or impact-analysis task/result surfaces; approve/activate/rollback routes to `surface-governance-policy-decision`; outcome feedback routes to `surface-governance-policy-outcome`. Detail never embeds inventory filtering or side-effecting lifecycle commands inline.

User goal: understand the visible policy/proposal purpose, lifecycle, affected capabilities, evidence, blockers, authorization basis, redaction, and next safe work item before opening a dedicated governed task surface.

Data source and backend authority: the payload is produced by the protected Governance/Policy read projection for the backend-resolved tenant/customer/workspace scope. Browser-provided policy ids, proposal ids, lifecycle filters, source refs, tenant/customer hints, and correlation keys are untrusted hints; the backend re-resolves selected `AuthContext`, row visibility, trace visibility, redaction, and authorized actions before returning detail or `surface-governance-policy-system-message`.

Required payload schema (frontend-safe):

- `detailSummary`: surface id/title/type/contract, policy or proposal display ref, title/name, lifecycle/status label, policy family, source artifact summary, owner/reviewer display summary, risk classification, affected capability families, freshness state, last activity age, and read-only/no-inline-mutation notice.
- `scopeSummary`: selected `AuthContext` label, tenant/customer/workspace scope label when authorized, actor role/capability labels, and safe explanation when the requested record is hidden, stale, outside scope, or redacted.
- `policyEvidence`: browser-safe policy/proposal narrative, current active state when visible, proposed state summary when applicable, before/after capability impact summary, simulation/impact-analysis/outcome evidence status, lifecycle gate state, and user-facing blockers. Raw policy clauses, hidden role rules, raw provider/model output, prompts, governed-tool payloads, JWTs, secrets, and cross-scope identifiers are excluded from ordinary payloads.
- `nextWork`: backend-authorized task entries for proposal editing/submission, simulation, decision/approval, activation, rollback, impact analysis, outcome note, inventory return, trace drilldown, and dashboard return. Each entry includes action id, target surface id, capability, lifecycle prerequisite, disabled/omitted reason when safe, idempotency requirement when side-effecting, and `noDirectMutation` semantics.
- `traceLinks`: human-readable trace summaries plus role-gated references to policy-decision, admin-audit, workstream-log, agent-work, impact-analysis task, and denial/failure traces.
- `redaction`: field-level indicators for hidden cross-tenant/customer evidence, privileged policy clauses, raw provider/model output, prompts, raw governed-tool payloads, JWTs, secrets, raw correlation ids, and idempotency keys.
- `authorizedActions`: only actions the backend authorizes for the selected actor and visible lifecycle state, commonly `action-governance-policy-read`, `action-governance-policy-list`, `action-governance-policy-draft-proposal`, `action-governance-policy-simulate`, `action-governance-policy-decide`, `action-governance-policy-activate`, `action-governance-policy-rollback`, `action-governance-policy-start-impact-analysis`, `action-governance-policy-read-impact-analysis`, and `action-governance-policy-outcome-note` when each capability and lifecycle rule permits it.
- `noDirectMutation`: always `true` for the detail surface; it renders state and routes to governed task surfaces only.

Required states: loading, ready, read-only, empty/no-authorized-detail, forbidden/system-message, not-found-or-redacted, conflict/stale, partial-data, blocked-provider-or-runtime for dependent advisory evidence, validation-error for malformed/stale refs, no-op refresh, and failure.

Action contract:

| Visible action / target | Browser action id | Governed tool | Capability | Result surface | Notes |
|---|---|---|---|---|---|
| Open or refresh detail | `action-governance-policy-read` | `list-policy-proposals` | `governance.policy.read` | `surface-governance-policy-detail` or `surface-governance-policy-system-message` | Read-only; backend reauthorizes row visibility and redaction. |
| Return to inventory/dashboard | `action-governance-policy-list` / `action-governance-policy-dashboard` | `list-policy-proposals` | `governance.policy.read` | Inventory or dashboard | Recomputes selected scope instead of trusting browser state. |
| Open proposal work | `action-governance-policy-draft-proposal` / `action-governance-policy-submit-proposal` | `draft-policy-proposal` | `governance.policy.propose` | `surface-governance-policy-proposal` | Inert draft/review work only; no authority change from detail. |
| Open advisory evidence | `action-governance-policy-simulate` / `action-governance-policy-start-impact-analysis` / `action-governance-policy-read-impact-analysis` | Simulation or impact-analysis tools | Simulation/impact capabilities | Simulation or impact-analysis surfaces | Advisory only; provider/runtime unavailable fails closed. |
| Open decision or lifecycle command | `action-governance-policy-decide` / `action-governance-policy-activate` / `action-governance-policy-rollback` | `approve-activate-or-rollback-policy` | Approve/activate/rollback capabilities | `surface-governance-policy-decision` or system-message | Detail may route to a dedicated decision surface but cannot commit inline. |
| Open outcome note | `action-governance-policy-outcome-note` | `record-policy-outcome-note` | `governance.outcomes.record` | `surface-governance-policy-outcome` | Observations do not change authority or lifecycle state. |

Authorization, traces, UI, and tests:

- `governance.policy.read` is required to open detail. Proposal, simulation, approval, activation, rollback, impact-analysis, and outcome actions are exposed only as backend-authorized task entries for the selected actor and lifecycle state.
- Hidden/cross-scope records, stale ids, missing capabilities, direct deep-link denials, and unsupported lifecycle transitions return safe no-enumeration system messages with trace refs and no hidden policy/capability facts.
- Every read, refresh, task-entry open, denial, redaction, stale/conflict result, and provider/runtime blocker emits workstream-log/correlation evidence; side-effecting target surfaces emit their own admin-audit and policy-decision traces.
- Frontend realization uses show/inspection, card-stack, action-bar, trace-summary, redaction, and system-message anatomy with keyboard-operable next-work actions, responsive field groups, and no color-only status communication.
- Required tests cover inventory/dashboard/proposal-to-detail traversal, protected direct detail load, hidden/cross-tenant/customer no-enumeration denial, read-only/no-inline-mutation rendering, authorized task routing, provider/runtime fail-closed evidence status, trace/redaction links, stale/conflict recovery, frontend secret boundaries, keyboard/focus behavior, and responsive rendering.

Surface-description sufficiency review: this definition is sufficiently unambiguous for a developer or generator to implement and review `surface-governance-policy-detail` without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics.

### `surface-governance-policy-proposal` (`governance.policy.proposal.v1`)

Pattern: `governance-diff`.

Owning workstream: Governance/Policy. Owning functional agent: `governance-policy-agent`. Reusable placements: opened from `surface-governance-policy-dashboard` draft shortcuts, `surface-governance-policy-inventory` row actions, lifecycle/detail breadcrumbs, simulation/decision/outcome follow-ups, and audit/trace drilldowns only when the selected `AuthContext` grants the required Governance/Policy authority. Purpose: draft, inspect, revise, and submit an inert policy proposal while clearly showing the before/after policy change, lifecycle blockers, evidence status, and next authorized transition without changing production authority from the browser.

Collection-object progression role: this is the lifecycle-aware create/edit/work surface for policy proposals. It may create or update an inert draft and may submit that draft for review through backend-governed proposal actions; simulation, approval, activation, rollback, outcome recording, and impact-analysis disposition remain separate governed surfaces/actions. Row selection and breadcrumbs return to the scoped proposal inventory or detail surfaces rather than embedding list/search behavior.

User goal: understand the proposed policy change, safely complete or submit a draft when authorized, see what evidence or approvals are still required, and recover from authorization, validation, provider/runtime, or stale/conflict states without exposing hidden policy internals.

Data source and backend authority: the payload is produced by the protected Governance/Policy proposal projection for the backend-resolved tenant/customer/workspace scope. Browser-provided proposal ids, source artifacts, requested lifecycle transitions, tenant/customer hints, correlation keys, and idempotency keys are treated as untrusted request metadata; the backend re-resolves the selected `AuthContext`, proposal visibility, lifecycle state, capabilities, row-level redaction, and allowed transitions before returning this surface or a safe system message.

Required payload schema (frontend-safe):

- `proposalSummary`: proposal display ref, title, purpose, lifecycle state, source summary, owner/reviewer display summary, risk classification, affected capability/artifact summary, freshness/conflict status, and safe empty/new-draft copy.
- `changeSet`: before/after summaries, changed policy areas, added/removed/changed capability effects, human-readable diff rows with risk labels, validation messages, and row redaction markers; raw policy clauses or hidden role rules are omitted unless exposed through a role-gated drilldown.
- `draftFields`: editable draft intent, rationale, source artifact reference, requested effective scope summary, risk justification, reviewer note, and validation errors for actors with `governance.policy.propose`; read-only actors receive display summaries only.
- `lifecycleGate`: required approvals, simulation/evidence status, impact-analysis status, activation/rollback gate summary, disabled transition reasons, and blocked-provider-or-runtime status where advisory evidence cannot be produced.
- `availableTransitions`: backend-authorized transition actions valid for the current lifecycle state, including draft/save, submit for review, open/start simulation, open decision, start/read impact analysis, and open outcome note only when the selected actor and proposal state permit them.
- `decisionMetadata`: current recommendation/decision summary, reviewer display label, decision state, outcome note summaries, and activation/rollback prerequisite summaries when available.
- `authorizedActions`: only actions the backend authorizes for the selected actor, including `action-governance-policy-draft-proposal`, `action-governance-policy-submit-proposal`, `action-governance-policy-simulate`, `action-governance-policy-decide`, `action-governance-policy-start-impact-analysis`, `action-governance-policy-read-impact-analysis`, and `action-governance-policy-outcome-note` when each corresponding capability and lifecycle rule is satisfied.
- `traceLinks`: user-readable trace summaries plus role-gated references to policy-decision trace, admin-audit event, workstream-log trace, agent-work trace, impact-analysis task events, source artifact trace, and denial/failure traces.
- `redaction`: field-level indicators for hidden cross-tenant/customer evidence, privileged policy clauses, hidden authority state, raw provider/model output, raw prompts, raw governed-tool payloads, JWTs, secrets, raw correlation ids, and idempotency keys.
- `readiness`: provider/runtime/configuration state for advisory simulation and impact-analysis prerequisites represented as ready, blocked, unknown, or not-required summaries; unavailable providers or autonomous-agent runtime are never rendered as successful analysis.
- `noDirectMutation`: always `true`; the browser renders backend-authored state and sends governed actions only. It cannot approve, activate, roll back, weaken security, expand authority, or fabricate evidence locally.

Visibility split:

- Default user-visible fields: proposal title/display ref, lifecycle/status label, purpose, safe source/risk summaries, before/after summary, validation and blocker copy, authorized next actions, required approval/evidence summary, and recovery instructions.
- On-demand drilldown fields: proposal display ids, detailed diff rows, lifecycle history, redacted source artifact details, outcome note summaries, simulation/impact evidence summaries, and trace summaries.
- Admin/support/auditor-only fields: capability ids, policy-decision trace refs, admin-audit refs, workstream-log refs, agent-work refs, impact-analysis task refs, denial/failure evidence, redaction reasons, and diagnostic idempotency/correlation status.
- Internal-only metadata never rendered in ordinary browser payloads: raw policy engine clauses, raw role policy state, raw provider/model data, prompts, governed-tool payloads, backend component names, database ids/cursors, cross-tenant identifiers, JWTs/secrets, correlation ids, and idempotency implementation details.

Allowed lifecycle states: `draft`, `submitted`, `simulation-required`, `in-review`, `changes-requested`, `approved`, `rejected`, `activated`, `rollback-candidate`, `rolled-back`, `superseded`. The surface exposes only backend-authorized actions for transitions valid from the current lifecycle state; invalid or stale transitions return a safe validation/conflict/system-message result and never imply a successful authority change.

Required states: loading, empty/new-draft, ready, editing-draft, submitting, validation-error, forbidden/system-message, conflict/stale, partial-data, blocked-provider-or-runtime, read-only, success/submitted, and failure.

State semantics:

- `loading`: workstream shell has selected Governance/Policy and is fetching the protected proposal projection.
- `empty/new-draft`: actor is authorized to propose and no existing proposal id was supplied; render backend-authored draft defaults and required field guidance.
- `ready`: proposal summary, diff, lifecycle gates, traces, and authorized transitions are backed by the protected proposal projection for the selected `AuthContext`.
- `editing-draft`: editable fields are available only for draft or changes-requested states and only when `governance.policy.propose` is granted; edits are unsaved until the governed draft action succeeds.
- `submitting`: a governed draft or submit request is in flight; repeat submissions use idempotency and must not duplicate proposals or lifecycle transitions.
- `validation-error`: missing rationale, unsupported source artifact, invalid policy diff, forbidden scope expansion, stale idempotency, or lifecycle-precondition failures are returned with field-level/user-safe copy.
- `forbidden/system-message`: missing bearer token, missing selected context, missing read/propose capability, hidden proposal, or tenant/customer scope denial returns `surface-governance-policy-system-message` with `noFakeSuccess=true`, `noDirectMutation=true`, and no hidden proposal enumeration.
- `conflict/stale`: proposal version, lifecycle state, source artifact, or trace freshness changed since the actor opened the surface; disable side-effecting actions until refresh.
- `partial-data`: some evidence, trace summaries, or source artifact details are omitted; visible sections identify what was omitted and why.
- `blocked-provider-or-runtime`: advisory simulation or impact-analysis evidence required for the next transition cannot be produced because provider/autonomous-agent runtime is unavailable; show blocked status and recovery, not fabricated analysis.
- `read-only`: actor can inspect the proposal but cannot draft, submit, or transition it; denied actions are omitted or shown as safe disabled explanations only when useful.
- `success/submitted`: backend accepted an inert draft save or submit transition and returns the refreshed proposal surface; approval, activation, rollback, or authority changes are not claimed.
- `failure`: unexpected read or command failure returns a safe system message with trace/audit reference and no raw exception, token, provider, storage, or policy-engine details.

Action contract:

| Visible action / target | Browser action id | Governed tool | Capability | Request payload | Result surface | Notes |
|---|---|---|---|---|---|---|
| Open existing proposal or refresh draft | `action-governance-policy-read` | `list-policy-proposals` | `governance.policy.read` | proposal display ref or draft source hint, optional refresh reason, correlation key generated by client or backend | `surface-governance-policy-proposal` or `surface-governance-policy-system-message` | Read-only projection; backend reauthorizes proposal visibility and selected scope. |
| Save or update inert draft | `action-governance-policy-draft-proposal` | `draft-policy-proposal` | `governance.policy.propose` | draft intent, source artifact/display ref, rationale, proposed change summary/diff input, requested scope summary, idempotency key, correlation key | `surface-governance-policy-proposal` | Creates or updates an inert draft only; no authority changes, approvals, activation, rollback, or hidden scope expansion. |
| Submit draft for review | `action-governance-policy-submit-proposal` | `draft-policy-proposal` | `governance.policy.propose` | proposal id/display ref, current version/freshness token, rationale, required approval acknowledgement, idempotency key, correlation key | `surface-governance-policy-proposal` | Moves an eligible draft into review/submitted state only when backend lifecycle rules pass. |
| Open/start simulation for proposal | `action-governance-policy-simulate` | `simulate-policy-change` | `governance.policy.simulate` | proposal id/display ref, scenario/scope, reason, idempotency key when starting, correlation key | `surface-governance-policy-simulation` or `surface-governance-policy-system-message` | Advisory only; provider/runtime unavailable returns blocked_provider_or_runtime with no fake success. |
| Open decision/review work | `action-governance-policy-decide` | `approve-activate-or-rollback-policy` | `governance.policy.approve` | proposal id/display ref, command mode `decide`, reason when supplied, idempotency key for command submission, correlation key | `surface-governance-policy-decision` | Proposal surface may route to decision review but cannot approve, activate, or roll back inline. |
| Start/read impact analysis | `action-governance-policy-start-impact-analysis` / `action-governance-policy-read-impact-analysis` | `start-policy-impact-analysis` / `read-policy-impact-analysis` | `governance.policy.impact_analysis.start` / `governance.policy.impact_analysis.read` | proposal id/display ref, task id when reading, scope, reason, idempotency key for start, correlation key | `surface-governance-policy-impact-analysis-task` | Durable advisory task path; activation remains blocked until valid human-reviewed evidence exists. |
| Open outcome note | `action-governance-policy-outcome-note` | `record-policy-outcome-note` | `governance.outcomes.record` | proposal id/display ref, observation/open intent, idempotency key when recording, correlation key | `surface-governance-policy-outcome` | Outcome observations do not change authority or lifecycle state. |

Hidden or denied actions: approval, activation, rollback, impact-result acceptance/rejection/request-changes, outcome-note mutation, raw policy-clause edits, tenant/customer scope expansion, and hidden proposal access are not performed directly by this proposal surface. Direct/deep-link attempts without authority return `surface-governance-policy-system-message`, are audit/trace recorded, and must not reveal whether hidden proposals, policies, capabilities, or cross-tenant/customer evidence exists.

Authorization and tenant scope:

- `governance.policy.read` is required to open an existing visible proposal; `governance.policy.propose` is required to create, update, or submit an inert draft.
- Simulation, decision, impact-analysis, and outcome actions are exposed only as backend-authorized actions for the selected actor and valid lifecycle state.
- The backend resolves tenant/customer/workspace authority from the selected `AuthContext`; browser fields cannot expand scope, enumerate hidden proposals, or request raw authority state.
- Draft/save/submit requests must be idempotent and version/freshness-aware so repeat or stale submissions cannot duplicate side effects or skip lifecycle gates.
- Proposal diffs, trace drilldowns, and source artifact details redact hidden cross-tenant/customer evidence, privileged policy clauses, raw provider/model content, prompts, raw tool payloads, JWTs, secrets, and implementation correlation/idempotency details.

Trace, audit, and work evidence:

- Proposal reads, draft saves, submit attempts, validation/conflict results, denials, stale refreshes, and every transition action produce workstream-log/correlation evidence.
- Draft and submit commands produce admin-audit and policy-decision trace evidence even when validation fails; simulation/impact-analysis actions link agent-work/task events; denials/provider/runtime failures link failure traces.
- Default trace copy is human-readable; raw ids/details are visible only through role-gated audit/support drilldowns.
- Repeated draft/submit/start/outcome commands require idempotency evidence and must return the original outcome or a safe conflict message.

Accessibility, responsive, and UI realization:

- Use the selected web UI style guide, named-theme contract, and component-catalog governance-diff, form, validation-summary, badge, system-message, action-bar, trace-summary, and confirmation anatomy.
- Diff rows, draft fields, validation summaries, lifecycle gates, action buttons, breadcrumbs, and trace links are keyboard-operable, announce lifecycle/validation/action targets, preserve focus after save/submit/action results, and provide accessible names for affected policy areas.
- Responsive layouts may stack diff, form, and lifecycle gate panels but must preserve backend-authored actions, redaction markers, validation states, trace summaries, and recovery guidance.

Required tests:

- App-description/contract tests prove the proposal contract includes payload schema, lifecycle states, draft/submit actions, auth/tenant rules, idempotency, states, traces, redaction, no-direct-mutation semantics, and sufficiency review.
- Frontend tests prove governance-diff rendering, new-draft/ready/editing/submitting/validation/conflict/blocked-provider-or-runtime/read-only/system-message states, backend-authorized action visibility, keyboard navigation, focus preservation, and secret-boundary redaction.
- Backend/API tests prove selected AuthContext scoping, missing-bearer and missing-capability denials, no-enumeration hidden proposal access, draft creation/update idempotency, submit lifecycle validation, stale conflict handling, row/action authorization, trace/audit/work evidence, and provider/runtime fail-closed advisory statuses.
- Negative tests prove the browser cannot approve, activate, roll back, mutate authority, expand tenant/customer scope, expose hidden policy internals, fabricate advisory evidence, or duplicate side effects on repeated draft/submit/start requests.

Surface-description sufficiency review: this definition is sufficiently unambiguous for a developer or generator to implement and review `surface-governance-policy-proposal` without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics. The default view avoids internal implementation details that do not help the target SaaS user, and no additional description pass is required before scoped implementation work for this proposal surface.

### `surface-governance-policy-simulation` (`governance.policy.simulation.v1`)

Pattern: `governance-diff`.

Owning workstream: Governance/Policy. Owning functional agent: `governance-policy-agent`. Reusable placements: opened from `surface-governance-policy-dashboard` simulation attention items, `surface-governance-policy-inventory` row actions, `surface-governance-policy-proposal` lifecycle gates, decision/activation prerequisites, impact-analysis evidence follow-ups, and audit/trace drilldowns only when the selected `AuthContext` grants Governance/Policy simulation authority. Purpose: produce and inspect advisory policy-change simulation evidence for a visible proposal without granting, approving, activating, rolling back, or otherwise changing production authority.

Collection-object progression role: this is the domain-specific single-action advisory evidence surface for a policy proposal. It is not the proposal editor, approval card, activation command, rollback command, impact-analysis task, or outcome note surface. Selection and breadcrumbs return to the scoped proposal, detail, or inventory surface; decision and activation work route to their dedicated surfaces after backend prerequisites are satisfied.

User goal: understand the expected access effects, denials, warnings, confidence limits, provider/runtime readiness, and activation blockers for a proposed policy change, then choose the next authorized follow-up without exposing hidden policy internals or raw model/tool output.

Data source and backend authority: the payload is produced by the protected Governance/Policy simulation projection for the backend-resolved tenant/customer/workspace scope. Browser-provided proposal ids, scenario/scope hints, tenant/customer hints, correlation keys, and idempotency keys are treated as untrusted request metadata; the backend re-resolves selected `AuthContext`, proposal visibility, simulation capability, lifecycle prerequisites, redaction, provider/runtime readiness, and trace visibility before returning this surface or a safe system message.

Required payload schema (frontend-safe):

- `simulationSummary`: simulation display ref, proposal display ref, proposal title, lifecycle/status label, scenario name, selected scenario scope summary, requested actor/customer/workspace summary when safe, simulation status, freshness/conflict status, confidence label, and safe empty/not-run copy.
- `expectedAccessChanges`: user-safe allow/deny/change summaries grouped by affected capability family, actor group, resource type, and customer/workspace scope; each row includes expected outcome, risk label, evidence status, and redaction markers without raw policy clauses or hidden role rules in the default view.
- `expectedAllows`: expected newly allowed or retained access outcomes with reason summary, scope label, risk classification, and evidence status.
- `expectedDenials`: expected denied, removed, or blocked access outcomes with safe reason summary, affected role/group label, scope label, and user-facing recovery copy where the denial is unexpected.
- `warnings`: validation, coverage, stale-data, incomplete-scenario, provider/runtime, hidden-evidence, and activation-prerequisite warnings with severity, disabled-action reason, and safe recovery guidance.
- `confidenceAndLimits`: confidence label, coverage summary, known blind spots, last simulated time/age bucket, provider/runtime readiness, and statement that advisory evidence is not an approval or activation.
- `activationGate`: whether simulation evidence is required, current gate state, missing prerequisites, related impact-analysis status, disabled approval/activation reasons, and next authorized target surfaces.
- `availableTransitions`: backend-authorized follow-up actions valid for the current proposal and simulation state, including rerun simulation, open proposal, open decision, start/read impact analysis, or open outcome note only when the selected actor and lifecycle state permit them.
- `authorizedActions`: only actions the backend authorizes for the selected actor, including `action-governance-policy-simulate`, `action-governance-policy-read`, `action-governance-policy-decide`, `action-governance-policy-start-impact-analysis`, `action-governance-policy-read-impact-analysis`, and `action-governance-policy-outcome-note` when each corresponding capability and lifecycle rule is satisfied.
- `traceLinks`: user-readable trace summaries plus role-gated references to policy-decision trace, admin-audit event, workstream-log trace, agent-work trace, simulation/provider failure traces, impact-analysis task events, source artifact trace, and denial traces.
- `redaction`: field-level indicators for hidden cross-tenant/customer evidence, privileged policy clauses, hidden authority state, raw provider/model output, raw prompts, raw governed-tool payloads, JWTs, secrets, raw correlation ids, and idempotency keys.
- `readiness`: provider/runtime/configuration state for simulation represented as ready, queued, running, blocked, unknown, or not-required summaries; unavailable providers or autonomous-agent runtime return blocked states and never fabricated successful analysis.
- `noDirectMutation`: always `true`; the browser renders backend-authored advisory state and sends governed actions only. It cannot approve, activate, roll back, weaken security, expand authority, or fabricate simulation evidence locally.

Visibility split:

- Default user-visible fields: proposal title/display ref, scenario label, lifecycle/status label, safe expected allow/deny/change summaries, warning and blocker copy, confidence/risk summary, authorized next actions, and recovery instructions.
- On-demand drilldown fields: simulation display refs, scenario inputs, detailed expected-access rows, redacted evidence summaries, lifecycle history, impact-analysis relationship, outcome note summaries, and trace summaries.
- Admin/support/auditor-only fields: capability ids, policy-decision trace refs, admin-audit refs, workstream-log refs, agent-work refs, simulation/provider failure evidence, impact-analysis task refs, denial evidence, redaction reasons, and diagnostic idempotency/correlation status.
- Internal-only metadata never rendered in ordinary browser payloads: raw policy engine clauses, raw role policy state, raw provider/model data, prompts, governed-tool payloads, backend component names, database ids/cursors, cross-tenant identifiers, JWTs/secrets, correlation ids, and idempotency implementation details.

Required states: loading, empty/not-run, queued/running, ready, validation-error, forbidden/system-message, conflict/stale, partial-data, blocked-provider-or-runtime, read-only, success/advisory-complete, and failure.

State semantics:

- `loading`: workstream shell has selected Governance/Policy and is fetching the protected simulation projection.
- `empty/not-run`: actor is authorized to simulate, but no current simulation evidence exists for the visible proposal/scenario; render backend-authored scenario guidance and the authorized run action.
- `queued/running`: a governed simulation request is accepted or in progress; repeat starts use idempotency and do not duplicate simulation jobs or imply completion.
- `ready`: expected access changes, warnings, confidence, activation gate, traces, and authorized transitions are backed by the protected simulation projection for the selected `AuthContext`.
- `validation-error`: missing proposal id, hidden proposal, unsupported scenario/scope, lifecycle-precondition failure, stale idempotency, or invalid request shape returns field-level/user-safe copy with no hidden policy enumeration.
- `forbidden/system-message`: missing bearer token, missing selected context, missing `governance.policy.simulate`, hidden proposal, or tenant/customer scope denial returns `surface-governance-policy-system-message` with `noFakeSuccess=true`, `noDirectMutation=true`, and no hidden proposal enumeration.
- `conflict/stale`: proposal version, source artifact, lifecycle state, scenario data, provider readiness, or trace freshness changed since the actor opened the surface; disable side-effecting follow-up actions until refresh or rerun.
- `partial-data`: some evidence, expected-access rows, trace summaries, or source artifact details are omitted; visible sections identify what was omitted and why.
- `blocked-provider-or-runtime`: required provider, model, policy simulator, or autonomous-agent runtime is unavailable; show blocked status and recovery, disable success-dependent transitions, and do not fabricate simulation results.
- `read-only`: actor can inspect existing simulation evidence but cannot start or rerun simulation; denied actions are omitted or shown as safe disabled explanations only when useful.
- `success/advisory-complete`: backend completed advisory simulation and returns refreshed evidence; approval, activation, rollback, or authority changes are not claimed.
- `failure`: unexpected read or command failure returns a safe system message with trace/audit reference and no raw exception, token, provider, storage, or policy-engine details.

Action contract:

| Visible action / target | Browser action id | Governed tool | Capability | Request payload | Result surface | Notes |
|---|---|---|---|---|---|---|
| Open existing simulation evidence | `action-governance-policy-read` | `list-policy-proposals` | `governance.policy.read` | proposal display ref, optional simulation display ref, refresh reason, correlation key generated by client or backend | `surface-governance-policy-simulation` or `surface-governance-policy-system-message` | Read-only projection; backend reauthorizes proposal visibility, trace visibility, and selected scope. |
| Run or rerun advisory simulation | `action-governance-policy-simulate` | `simulate-policy-change` | `governance.policy.simulate` | proposal display ref, scenario/scope summary, reason, idempotency key, correlation key | `surface-governance-policy-simulation` or `surface-governance-policy-system-message` | Advisory only; creates/refreshes simulation evidence when provider/runtime is available; no approval, activation, rollback, or authority change. |
| Open proposal lifecycle surface | `action-governance-policy-read` | `list-policy-proposals` | `governance.policy.read` | proposal display ref, correlation key | `surface-governance-policy-proposal` or `surface-governance-policy-detail` | Used for breadcrumbs and lifecycle review; no simulation mutation. |
| Open decision/review work | `action-governance-policy-decide` | `approve-activate-or-rollback-policy` | `governance.policy.approve` | proposal display ref, command mode `decide`, reason when supplied, idempotency key for command submission, correlation key | `surface-governance-policy-decision` | Simulation may satisfy one prerequisite, but this surface cannot approve, activate, or roll back inline. |
| Start/read impact analysis | `action-governance-policy-start-impact-analysis` / `action-governance-policy-read-impact-analysis` | `start-policy-impact-analysis` / `read-policy-impact-analysis` | `governance.policy.impact_analysis.start` / `governance.policy.impact_analysis.read` | proposal display ref, task id when reading, scope, reason, idempotency key for start, correlation key | `surface-governance-policy-impact-analysis-task` | Durable advisory task path; activation remains blocked until backend prerequisites are complete. |
| Open outcome note | `action-governance-policy-outcome-note` | `record-policy-outcome-note` | `governance.outcomes.record` | proposal display ref, observation/open intent, idempotency key when recording, correlation key | `surface-governance-policy-outcome` | Outcome observations do not change authority or lifecycle state. |

Hidden or denied actions: approval, activation, rollback, impact-result acceptance/rejection/request-changes, outcome-note mutation, raw policy-clause edits, tenant/customer scope expansion, hidden proposal access, and direct provider/model prompts are not performed by this simulation surface. Direct/deep-link attempts without authority return `surface-governance-policy-system-message`, are audit/trace recorded, and must not reveal whether hidden proposals, policies, capabilities, or cross-tenant/customer evidence exists.

Authorization and tenant scope:

- `governance.policy.simulate` is required to start or rerun simulation. `governance.policy.read` is required to open existing proposal/simulation evidence. Decision, impact-analysis, and outcome follow-ups require their own backend-authorized capabilities and lifecycle predicates.
- The backend resolves tenant/customer/workspace scope from selected `AuthContext`; browser scenario/scope hints cannot expand authority, enumerate hidden policies, or request raw authority state.
- Cross-tenant/customer proposal evidence, hidden role policy state, raw policy internals, provider secrets, JWTs, prompts, raw tool payloads, raw model responses, and correlation/idempotency implementation details are redacted or omitted.
- Direct proposal ids, stale simulation ids, unsupported scenario scope, or hidden evidence requests return safe no-enumeration denial/validation states with trace refs.

Trace, audit, and work evidence:

- Simulation open, run/rerun, denial, validation, stale/conflict recovery, provider/runtime failure, and every follow-up action produce workstream-log/correlation evidence.
- Simulation start/completion/failure produces admin-audit or policy-decision trace evidence as applicable, and links agent-work/provider/task traces when an autonomous or provider-backed path runs.
- Default trace copy is human-readable; raw ids/details are visible only through role-gated audit/support drilldowns.
- Repeated run/rerun requests require idempotency evidence; failed or blocked provider/runtime paths are traceable and fail closed without fake success.

Accessibility, responsive, and UI realization:

- Use the selected web UI style guide, named-theme contract, and component-catalog governance-diff, system-message, warning, evidence-summary, action-menu, and trace-link anatomy.
- Diff rows, expected allow/deny groups, warnings, activation-gate blockers, and follow-up actions are keyboard-operable, announce status/risk/action targets, preserve focus after refresh/action results, and provide accessible names for target surfaces.
- Responsive layouts may collapse expected access groups into cards, but must preserve the same backend-authored action availability, warning severity, redaction markers, recovery states, and advisory/no-mutation copy.

Required tests:

- App-description/contract tests prove the simulation contract includes payload schema, advisory/no-direct-mutation semantics, scenario/action mapping, auth/tenant rules, states, traces, redaction, provider/runtime fail-closed behavior, and sufficiency review.
- Frontend tests prove governance-diff rendering for empty/not-run, queued/running, ready, validation-error, stale/conflict, partial-data, blocked-provider-or-runtime, read-only, and system-message states; backend-authorized action visibility; secret-boundary redaction; keyboard navigation; and no client-side approval/activation/rollback claims.
- Backend/API tests prove selected AuthContext scoping, missing-bearer and missing-capability denials, no-enumeration hidden proposal access, simulation start/read idempotency, lifecycle prerequisite validation, row/action authorization, trace/audit/work evidence, provider/runtime fail-closed statuses, and no authority mutation from simulation.
- Negative tests prove the browser and agents cannot approve, activate, roll back, weaken policy, expand tenant/customer scope, expose hidden policy internals, fabricate advisory evidence, or duplicate side effects on repeated simulation requests.

Surface-description sufficiency review: this definition is sufficiently unambiguous for a developer or generator to implement and review `surface-governance-policy-simulation` without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics. The default view avoids internal implementation details that do not help the target SaaS user, and no additional description pass is required before scoped implementation work for this simulation surface.

### `surface-governance-policy-decision` (`governance.policy.decision.v1`)

Pattern: `decision-card`.

Owning workstream: Governance/Policy. Owning functional agent: `governance-policy-agent`. Reusable placements: opened from dashboard decision/activation/rollback attention items, inventory row actions, proposal lifecycle gates, simulation evidence, outcome follow-ups, impact-analysis result dispositions, and audit/trace drilldowns only when the selected `AuthContext` grants the required Governance/Policy authority. Purpose: review a submitted or approved policy proposal, record a human approval decision, activate an approved policy change, or roll back an activated policy through backend-governed commands without letting the browser or an agent commit authority changes locally.

Collection-object progression role: this is the lifecycle-aware decision and destructive-lifecycle recovery surface for policy proposals. It follows list/search (`surface-governance-policy-inventory`), inspection/proposal work (`surface-governance-policy-detail` and `surface-governance-policy-proposal`), and advisory evidence (`surface-governance-policy-simulation` and impact-analysis surfaces). It owns approve/reject/request-changes, activation, and rollback review commands; it does not embed proposal editing, simulation execution, impact-result disposition, outcome-note recording, or policy inventory filtering.

User goal: understand whether the selected proposal is safe and ready to approve, activate, or roll back; see required evidence and blockers in user language; take only the backend-authorized decision action; and recover safely from missing authority, stale state, missing evidence, provider/runtime blockers, or rollback prerequisites.

Data source and backend authority: the payload is produced by the protected Governance/Policy decision projection for the backend-resolved tenant/customer/workspace scope. Browser-provided proposal ids, command modes, reasons, tenant/customer hints, correlation keys, and idempotency keys are untrusted request metadata; the backend re-resolves selected `AuthContext`, proposal visibility, lifecycle state, approval/activation/rollback capability, evidence freshness, rollback metadata, redaction, and allowed transitions before returning a decision card or safe system message.

Required payload schema (frontend-safe):

- `decisionSummary`: proposal display ref, title, current lifecycle state, requested command mode (`decide`, `activate`, or `rollback`), recommended outcome, reviewer/activator display summary, affected capability/artifact summary, safe policy basis summary, freshness/conflict state, and user-facing success or recovery copy.
- `riskAndImpact`: risk score/classification, confidence label, expected access effect, affected target summaries, blast-radius summary, simulation/impact-analysis evidence status, rollback readiness, and safe uncertainty/provider-runtime blocker copy. Raw policy clauses, hidden role rules, provider/model output, prompts, and tool payloads are omitted from the ordinary payload.
- `decisionEvidence`: required approvals, approval quorum/status, advisory simulation evidence summary, impact-analysis result summary, prerequisite checklist, alternative outcomes, source proposal trace summary, outcome-note summaries, and evidence redaction markers.
- `commandForm`: backend-authored fields for the active command mode, including reason/rationale, acknowledgement text, optional rollback target summary, required approval acknowledgement, disabled-submit reasons, field validation messages, and idempotency requirement for side-effecting commands.
- `allowedActions`: only backend-authorized actions valid for the selected actor and proposal lifecycle, including `action-governance-policy-decide`, `action-governance-policy-activate`, `action-governance-policy-rollback`, `action-governance-policy-read`, `action-governance-policy-simulate`, `action-governance-policy-start-impact-analysis`, `action-governance-policy-read-impact-analysis`, and `action-governance-policy-outcome-note` when each corresponding capability and lifecycle rule is satisfied.
- `disabledActions`: useful but unavailable actions with safe reasons such as missing approval capability, missing activation capability, missing rollback capability, missing simulation evidence, provider/runtime blocked, stale proposal version, missing rollback metadata, already-decided proposal, tenant/customer scope denial, or read-only selected context.
- `traceLinks`: user-readable trace summaries plus role-gated references to policy-decision trace, admin-audit event, workstream-log trace, agent-work trace, simulation trace, impact-analysis task/result events, outcome note trace, and denial/failure traces.
- `redaction`: field-level indicators for hidden cross-tenant/customer evidence, privileged policy clauses, hidden authority state, raw provider/model output, raw prompts, raw governed-tool payloads, JWTs, secrets, raw correlation ids, and idempotency keys.
- `readiness`: provider/runtime/configuration state for advisory evidence and autonomous-agent impact-analysis prerequisites represented as ready, blocked, unknown, or not-required summaries; unavailable providers or autonomous-agent runtime are never rendered as successful evidence.
- `noDirectMutation`: always `true`; the browser renders backend-authored decision state and submits governed commands only. The frontend and agents cannot approve, activate, roll back, weaken security, expand authority, or fabricate evidence locally.

Visibility split:

- Default user-visible fields: proposal title/display ref, lifecycle/status label, command mode, recommendation, risk/impact summary, required approvals/evidence, safe blocker or success copy, authorized next actions, and recovery instructions.
- On-demand drilldown fields: proposal display ids, detailed policy diff summaries, lifecycle history, advisory evidence summaries, rollback plan summary, outcome note summaries, redacted trace summaries, and scoped source artifact details.
- Admin/support/auditor-only fields: capability ids, policy-decision trace refs, admin-audit refs, workstream-log refs, agent-work refs, simulation and impact-analysis refs, denial/failure evidence, redaction reasons, diagnostic idempotency/correlation status, and rollback metadata details.
- Internal-only metadata never rendered in ordinary browser payloads: raw policy engine clauses, hidden role policy state, raw provider/model data, prompts, governed-tool payloads, backend component names, database ids/cursors, cross-tenant identifiers, JWTs/secrets, correlation ids, and idempotency implementation details.

Allowed command modes and lifecycle rules: `decide` may approve, reject, or request changes for proposals in submitted, simulation-required, in-review, or changes-requested lifecycle states when approval prerequisites and `governance.policy.approve` are satisfied. `activate` may activate only an approved proposal with fresh required evidence, activation capability, rollback metadata, and satisfied activation prerequisites. `rollback` may roll back only an activated or rollback-candidate proposal with rollback capability, target metadata, and safe rollback plan evidence. Invalid, stale, duplicate, or prerequisite-missing transitions return validation/conflict/system-message results and never imply a successful authority change.

Required states: loading, ready, deciding, activating, rolling-back, success/approved, success/rejected, success/changes-requested, success/activated, success/rolled-back, validation-error, forbidden/system-message, conflict/stale, partial-data, blocked-provider-or-runtime, read-only, and failure.

State semantics:

- `loading`: workstream shell has selected Governance/Policy and is fetching the protected decision projection.
- `ready`: decision summary, evidence, traces, blockers, and authorized actions are backed by the protected decision projection for the selected `AuthContext`.
- `deciding`, `activating`, and `rolling-back`: a governed `approve-activate-or-rollback-policy` command is in flight with command mode, reason, current proposal version/freshness token, idempotency key, and correlation key; repeat submissions are idempotent and must not duplicate lifecycle transitions or audit events.
- `success/approved`, `success/rejected`, `success/changes-requested`, `success/activated`, and `success/rolled-back`: rendered only after the backend confirms the lifecycle or authority state changed; success copy includes safe next action and trace summary.
- `validation-error`: missing reason, missing approval acknowledgement, invalid command mode, stale idempotency key, missing simulation/impact evidence, missing rollback metadata, lifecycle-precondition failure, or unsupported scope returns field-level/user-safe copy without committing authority changes.
- `forbidden/system-message`: missing bearer token, missing selected context, missing read/approve/activate/rollback capability, hidden proposal, or tenant/customer scope denial returns `surface-governance-policy-system-message` with `noFakeSuccess=true`, `noDirectMutation=true`, and no hidden proposal enumeration.
- `conflict/stale`: proposal version, lifecycle state, required evidence, approval quorum, activation prerequisite, rollback plan, or trace freshness changed since the actor opened the card; disable side-effecting actions until refresh.
- `partial-data`: some evidence, trace summaries, outcome notes, or source artifact details are omitted; visible sections identify what was omitted and why.
- `blocked-provider-or-runtime`: required advisory simulation or impact-analysis evidence cannot be produced because provider/autonomous-agent runtime is unavailable; show blocked status and recovery, not fabricated approval support.
- `read-only`: actor can inspect the decision state but cannot decide, activate, or roll back; denied actions are omitted or shown as safe disabled explanations only when useful.
- `failure`: unexpected command/read failure returns a safe system message with trace/audit reference and no raw exception, token, provider, policy-engine, storage, or component details.

Action contract:

| Visible action / target | Browser action id | Governed tool | Capability | Request payload | Result surface | Notes |
|---|---|---|---|---|---|---|
| Open decision card or refresh evidence | `action-governance-policy-read` | `list-policy-proposals` | `governance.policy.read` | proposal display ref, requested command mode hint, refresh reason, correlation key generated by client or backend | `surface-governance-policy-decision` or `surface-governance-policy-system-message` | Read-only projection; backend reauthorizes proposal visibility, decision evidence, and selected scope. |
| Approve, reject, or request changes | `action-governance-policy-decide` | `approve-activate-or-rollback-policy` | `governance.policy.approve` | proposal display ref, command mode `decide`, decision outcome (`approve`, `reject`, or `request_changes`), reason/rationale, required approval acknowledgement, current version/freshness token, idempotency key, correlation key | `surface-governance-policy-decision` or `surface-governance-policy-system-message` | Records human decision only when lifecycle and evidence prerequisites pass; approval does not activate policy. |
| Activate approved policy change | `action-governance-policy-activate` | `approve-activate-or-rollback-policy` | `governance.policy.activate` | proposal display ref, command mode `activate`, reason/rationale, activation prerequisite acknowledgement, rollback metadata/plan summary, current version/freshness token, idempotency key, correlation key | `surface-governance-policy-decision` or `surface-governance-policy-system-message` | Commits authority change only after backend verifies approved lifecycle state, fresh required evidence, activation capability, and rollback metadata. |
| Roll back activated policy change | `action-governance-policy-rollback` | `approve-activate-or-rollback-policy` | `governance.policy.rollback` | proposal display ref, command mode `rollback`, rollback target/plan summary, reason/rationale, current version/freshness token, idempotency key, correlation key | `surface-governance-policy-decision` or `surface-governance-policy-system-message` | Reverts only the authorized activated proposal through backend policy state; requires rollback capability and safe rollback metadata. |
| Open advisory simulation evidence | `action-governance-policy-simulate` | `simulate-policy-change` | `governance.policy.simulate` | proposal display ref, scenario/scope summary, reason, idempotency key when starting, correlation key | `surface-governance-policy-simulation` or `surface-governance-policy-system-message` | Decision card may link to or start advisory evidence when authorized; it cannot fabricate simulation results. |
| Start/read impact analysis evidence | `action-governance-policy-start-impact-analysis` / `action-governance-policy-read-impact-analysis` | `start-policy-impact-analysis` / `read-policy-impact-analysis` | `governance.policy.impact_analysis.start` / `governance.policy.impact_analysis.read` | proposal display ref, task id when reading, scope, reason, idempotency key for start, correlation key | `surface-governance-policy-impact-analysis-task` | Activation remains blocked until required impact evidence is available or explicitly not required by backend policy; provider/runtime unavailable returns blocked_provider_or_runtime. |
| Open outcome note | `action-governance-policy-outcome-note` | `record-policy-outcome-note` | `governance.outcomes.record` | proposal display ref, observation/open intent, idempotency key when recording, correlation key | `surface-governance-policy-outcome` | Outcome observations do not approve, activate, roll back, or change authority. |

Hidden or denied actions: raw policy-clause edits, tenant/customer scope expansion, hidden proposal access, direct provider/model prompts, client-side approval, client-side activation, client-side rollback, and impact-result disposition are not performed by this decision surface. Direct/deep-link attempts without authority return `surface-governance-policy-system-message`, are audit/trace recorded, and must not reveal whether hidden proposals, policies, capabilities, or cross-tenant/customer evidence exists.

Authorization and tenant scope:

- `governance.policy.read` is required to open the decision projection; `governance.policy.approve`, `governance.policy.activate`, and `governance.policy.rollback` are separately required for the corresponding command modes.
- The backend resolves tenant/customer/workspace authority from selected `AuthContext`; browser tenant/customer, proposal, command-mode, correlation, and idempotency fields cannot expand scope or bypass lifecycle prerequisites.
- Approval, activation, and rollback actions are exposed only as backend-authorized actions for visible proposals and valid lifecycle states; denied or invalid actions are omitted or returned with safe disabled reasons/system messages.
- Cross-tenant/customer proposal evidence, privileged policy clauses, hidden authority state, raw provider/model content, prompts, raw tool payloads, JWTs, secrets, and implementation correlation/idempotency details are redacted or omitted.
- Direct proposal id, hidden row, missing capability, stale version, or unsupported command attempts return safe no-enumeration denial or validation states with trace refs, `noFakeSuccess=true`, and `noDirectMutation=true`.

Trace, audit, and work evidence:

- Decision-card reads, evidence refreshes, approval/rejection/request-changes, activation, rollback, denials, stale/conflict results, and prerequisite/provider/runtime failures produce workstream-log/correlation evidence.
- Consequential decision, activation, and rollback commands produce policy-decision trace and admin-audit evidence with idempotency outcome and selected `AuthContext`; advisory links include agent-work, simulation, and impact-analysis task/result traces when applicable.
- Default trace copy is human-readable; raw ids/details are visible only through role-gated audit/support drilldowns.
- Provider/runtime failures and missing evidence are traceable and fail closed; no decision card state may imply successful model-backed evidence, approval, activation, or rollback when the backend path did not run.

Accessibility, responsive, and UI realization:

- Use the selected web UI style guide, named-theme contract, and component-catalog decision-card, diff/evidence, alert, form, action-group, disabled-action, system-message, and trace-link anatomy.
- Recommendation, risk/impact, prerequisite checklist, disabled-action reasons, command form fields, evidence links, and submit controls are keyboard-operable, have accessible names that include the command mode and proposal summary, preserve focus after refresh/command results, and announce success, validation, forbidden, stale, and blocked-provider-or-runtime states.
- Responsive layouts preserve evidence-before-command ordering, keep destructive activation/rollback acknowledgements visible before submit, and do not hide required recovery, denial, redaction, or trace-summary information.

Required tests:

- App-description/contract tests prove the decision contract includes payload schema, command modes, lifecycle rules, action mapping, auth/tenant rules, states, traces, redaction, and sufficiency review.
- Frontend tests prove decision-card rendering for ready/read-only/partial/stale/blocked-provider-or-runtime/system-message states, authorized action visibility, disabled-action reasons, secret-boundary redaction, keyboard command flow, and no client-side authority mutation.
- Backend/API tests prove selected AuthContext scoping, missing-bearer and missing-capability denials, no-enumeration hidden proposal access, approve/reject/request-changes validation, activation prerequisites, rollback prerequisites, idempotency/replay behavior, trace/audit/work evidence, provider/runtime fail-closed evidence prerequisites, and no authority mutation from advisory links.
- Negative tests prove the browser and agents cannot approve, activate, roll back, weaken policy, expand tenant/customer scope, expose hidden policy internals, fabricate advisory evidence, or duplicate side effects on repeated decision/activation/rollback requests.

Surface-description sufficiency review: this definition is sufficiently unambiguous for a developer or generator to implement and review `surface-governance-policy-decision` without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics. The default view avoids internal implementation details that do not help the target SaaS user, and no additional description pass is required before scoped implementation work for this decision surface.

### `surface-governance-policy-outcome` (`governance.policy.outcome.v1`)

Pattern: `outcome-panel`.

Owning workstream: Governance/Policy. Owning functional agent: `governance-policy-agent`. Reusable placements: opened from dashboard outcome follow-up queues, inventory row actions, proposal/detail breadcrumbs, simulation and decision follow-ups, impact-analysis result disposition summaries, and audit/trace drilldowns only when the selected `AuthContext` grants Governance/Policy read or outcome authority. Purpose: inspect and record policy outcome observations, metrics, recommendations, and evidence after proposal decisions or activation without approving, activating, rolling back, or changing authority from the browser.

Collection-object progression role: this is the lifecycle-aware outcome/evidence surface for policy proposals. It follows inventory/detail/proposal discovery and decision/activation work, and it can append governed outcome notes for visible proposals. It does not embed list/search, policy editing, decision approval, activation, rollback, simulation execution, impact-result disposition, or destructive lifecycle behavior.

User goal: understand what happened after a policy decision or activation, see outcome metrics and evidence in safe product language, add an authorized observation or recommendation when appropriate, and recover safely from missing authority, stale lifecycle state, missing evidence, provider/runtime blockers, or tenant/customer scope denial.

Data source and backend authority: the payload is produced by the protected Governance/Policy outcome projection for the backend-resolved tenant/customer/workspace scope. Browser-provided proposal ids, outcome ids, observation text, tenant/customer hints, correlation keys, and idempotency keys are untrusted request metadata; the backend re-resolves selected `AuthContext`, proposal visibility, lifecycle state, outcome-note capability, evidence visibility, trace visibility, redaction, and allowed actions before returning the outcome panel or safe system message.

Required payload schema (frontend-safe):

- `outcomeSummary`: outcome display ref, proposal display ref, proposal title, decision state, activation or rollback state when applicable, outcome status, latest observation summary, actor display summary, freshness/conflict state, and user-facing success or recovery copy.
- `metrics`: safe outcome measures such as affected-user count summary, access-change summary, policy effectiveness signal, incident or exception counts, follow-up due/age bucket, confidence label, trend direction, and metric freshness; raw analytics queries, cross-tenant/customer identifiers, and implementation counters are omitted from ordinary payloads.
- `recommendations`: human-readable follow-up recommendations, risk warnings, required next review, suggested simulation or impact-analysis refresh, and disabled transition reasons; recommendations are advisory and cannot approve, activate, roll back, or weaken policy.
- `evidenceRefs`: decision summary, activation/rollback summary, simulation evidence summary, impact-analysis result summary, source proposal trace summary, supporting artifact summaries, outcome-note history, and evidence redaction markers.
- `noteForm`: backend-authored fields for actors with `governance.outcomes.record`, including observation summary, outcome category, recommendation, metric/evidence reference selector, required reason when configured, validation messages, current version/freshness token, and idempotency requirement for recording notes.
- `authorizedActions`: only backend-authorized actions valid for the selected actor and lifecycle, including `action-governance-policy-read`, `action-governance-policy-outcome-note`, `action-governance-policy-simulate`, `action-governance-policy-decide`, `action-governance-policy-start-impact-analysis`, and `action-governance-policy-read-impact-analysis` when each corresponding capability and proposal state permits it.
- `disabledActions`: useful but unavailable actions with safe reasons such as missing outcome capability, missing read capability, missing evidence, provider/runtime blocked, stale proposal version, already-superseded proposal, tenant/customer scope denial, or read-only selected context.
- `traceLinks`: user-readable trace summaries plus role-gated references to policy-decision trace, admin-audit event, workstream-log trace, agent-work trace, simulation trace, impact-analysis task/result events, outcome-note trace, and denial/failure traces.
- `redaction`: field-level indicators for hidden cross-tenant/customer evidence, privileged policy clauses, hidden authority state, raw provider/model output, raw prompts, raw governed-tool payloads, JWTs, secrets, raw correlation ids, and idempotency keys.
- `readiness`: provider/runtime/configuration state for advisory simulation or impact-analysis evidence represented as ready, blocked, unknown, or not-required summaries; unavailable providers or autonomous-agent runtime are never rendered as successful analysis or outcome proof.
- `noDirectMutation`: always `true`; the browser renders backend-authored outcome state and submits governed outcome-note actions only. The frontend and agents cannot approve, activate, roll back, weaken security, expand authority, or fabricate evidence locally.

Visibility split:

- Default user-visible fields: proposal title/display ref, decision or activation status label, outcome status, metric summaries, latest observation, safe recommendation copy, authorized next actions, and recovery instructions.
- On-demand drilldown fields: outcome display ids, detailed metric breakdowns, outcome-note history, lifecycle history, advisory evidence summaries, redacted trace summaries, and scoped source artifact details.
- Admin/support/auditor-only fields: capability ids, policy-decision trace refs, admin-audit refs, workstream-log refs, agent-work refs, simulation and impact-analysis refs, denial/failure evidence, redaction reasons, diagnostic idempotency/correlation status, and metric freshness diagnostics.
- Internal-only metadata never rendered in ordinary browser payloads: raw policy engine clauses, hidden role policy state, raw provider/model data, prompts, governed-tool payloads, backend component names, database ids/cursors, analytics implementation details, cross-tenant identifiers, JWTs/secrets, correlation ids, and idempotency implementation details.

Allowed lifecycle and note rules: outcome panels may be opened for visible proposals in submitted, in-review, changes-requested, approved, rejected, activated, rollback-candidate, rolled-back, or superseded states when the selected actor has read authority. Recording an outcome note requires `governance.outcomes.record`, a visible proposal, valid outcome category, non-empty observation or recommendation, fresh proposal/outcome version, and idempotency key. Outcome notes are evidence/feedback only; they never approve, activate, roll back, accept/reject impact-analysis results, or change authority. Invalid, stale, duplicate, or prerequisite-missing note submissions return validation/conflict/system-message results and never imply a successful authority change.

Required states: loading, empty/no-outcomes, ready, recording-note, success/note-recorded, validation-error, forbidden/system-message, conflict/stale, partial-data, blocked-provider-or-runtime, read-only, and failure.

State semantics:

- `loading`: workstream shell has selected Governance/Policy and is fetching the protected outcome projection.
- `empty/no-outcomes`: actor is authorized, but no outcome notes or metrics are available for the visible proposal; show safe explanation and only backend-authorized follow-up actions.
- `ready`: outcome summary, metrics, recommendations, evidence, traces, blockers, and authorized actions are backed by the protected outcome projection for the selected `AuthContext`.
- `recording-note`: a governed `record-policy-outcome-note` command is in flight with proposal display ref, observation fields, current version/freshness token, idempotency key, and correlation key; repeat submissions are idempotent and must not duplicate notes or audit events.
- `success/note-recorded`: rendered only after the backend confirms the outcome note was appended; success copy includes the refreshed outcome summary, safe next action, and trace summary.
- `validation-error`: missing observation, unsupported category, missing reason, stale idempotency key, hidden evidence reference, lifecycle-precondition failure, or invalid metric reference returns field-level/user-safe copy without changing authority.
- `forbidden/system-message`: missing bearer token, missing selected context, missing read or outcome-record capability, hidden proposal/outcome, or tenant/customer scope denial returns `surface-governance-policy-system-message` with `noFakeSuccess=true`, `noDirectMutation=true`, and no hidden proposal enumeration.
- `conflict/stale`: proposal version, outcome metric freshness, lifecycle state, evidence state, or trace freshness changed since the actor opened the panel; disable note submission and side-effecting follow-ups until refresh.
- `partial-data`: some metrics, evidence, trace summaries, or outcome-note history are omitted; visible sections identify what was omitted and why.
- `blocked-provider-or-runtime`: advisory simulation or impact-analysis evidence referenced by the outcome cannot be refreshed because provider/autonomous-agent runtime is unavailable; show blocked status and recovery, not fabricated outcome evidence.
- `read-only`: actor can inspect the outcome panel but cannot record notes; denied actions are omitted or shown as safe disabled explanations only when useful.
- `failure`: unexpected read or command failure returns a safe system message with trace/audit reference and no raw exception, token, provider, policy-engine, storage, analytics, or component details.

Action contract:

| Visible action / target | Browser action id | Governed tool | Capability | Request payload | Result surface | Notes |
|---|---|---|---|---|---|---|
| Open outcome panel or refresh evidence | `action-governance-policy-read` | `list-policy-proposals` | `governance.policy.read` | proposal display ref, optional outcome display ref, refresh reason, correlation key generated by client or backend | `surface-governance-policy-outcome` or `surface-governance-policy-system-message` | Read-only projection; backend reauthorizes proposal/outcome visibility, trace visibility, and selected scope. |
| Record outcome note | `action-governance-policy-outcome-note` | `record-policy-outcome-note` | `governance.outcomes.record` | proposal display ref, outcome display ref when present, observation summary, outcome category, recommendation or metric/evidence reference, reason when required, current version/freshness token, idempotency key, correlation key | `surface-governance-policy-outcome` or `surface-governance-policy-system-message` | Appends governed feedback/evidence only; does not approve, activate, roll back, accept impact results, or change authority. |
| Open advisory simulation evidence | `action-governance-policy-simulate` | `simulate-policy-change` | `governance.policy.simulate` | proposal display ref, scenario/scope summary, reason, idempotency key when starting, correlation key | `surface-governance-policy-simulation` or `surface-governance-policy-system-message` | Outcome panel may link to or start advisory evidence when authorized; provider/runtime unavailable returns blocked_provider_or_runtime. |
| Open decision/activation/rollback work | `action-governance-policy-decide` | `approve-activate-or-rollback-policy` | `governance.policy.approve` | proposal display ref, command mode `decide`, reason when supplied, idempotency key for command submission, correlation key | `surface-governance-policy-decision` | Outcome panel may route to decision work when lifecycle and authority permit; it cannot decide inline. |
| Start/read impact analysis evidence | `action-governance-policy-start-impact-analysis` / `action-governance-policy-read-impact-analysis` | `start-policy-impact-analysis` / `read-policy-impact-analysis` | `governance.policy.impact_analysis.start` / `governance.policy.impact_analysis.read` | proposal display ref, task id when reading, scope, reason, idempotency key for start, correlation key | `surface-governance-policy-impact-analysis-task` | Durable advisory task path; unavailable provider/runtime returns blocked_provider_or_runtime and no fake success. |

Hidden or denied actions: approval, activation, rollback, impact-result acceptance/rejection/request-changes, raw policy-clause edits, tenant/customer scope expansion, hidden proposal/outcome access, direct provider/model prompts, client-side outcome fabrication, and metric mutation outside the governed outcome-note tool are not performed by this outcome panel. Direct/deep-link attempts without authority return `surface-governance-policy-system-message`, are audit/trace recorded, and must not reveal whether hidden proposals, policies, capabilities, outcomes, or cross-tenant/customer evidence exists.

Authorization and tenant scope:

- `governance.policy.read` is required to open the outcome projection; `governance.outcomes.record` is required to record outcome notes.
- The backend resolves tenant/customer/workspace authority from selected `AuthContext`; browser tenant/customer, proposal, outcome, metric, evidence, correlation, and idempotency fields cannot expand scope or bypass lifecycle prerequisites.
- Outcome-note actions are exposed only as backend-authorized actions for visible proposals and valid lifecycle states; denied or invalid actions are omitted or returned with safe disabled reasons/system messages.
- Cross-tenant/customer proposal evidence, privileged policy clauses, hidden authority state, raw provider/model content, prompts, raw tool payloads, JWTs, secrets, raw metric implementation details, and implementation correlation/idempotency details are redacted or omitted.
- Direct proposal or outcome ids, hidden rows, missing capability, stale version, unsupported note category, or hidden evidence references return safe no-enumeration denial or validation states with trace refs, `noFakeSuccess=true`, and `noDirectMutation=true`.

Trace, audit, and work evidence:

- Outcome-panel reads, evidence refreshes, outcome-note submissions, denials, stale/conflict results, and prerequisite/provider/runtime failures produce workstream-log/correlation evidence.
- Consequential outcome-note commands produce admin-audit and outcome-note trace evidence with idempotency outcome and selected `AuthContext`; related decision, activation, simulation, and impact-analysis evidence links preserve their source traces without copying raw provider/model data.
- Default trace copy is human-readable; raw ids/details are visible only through role-gated audit/support drilldowns.
- Provider/runtime failures and missing evidence are traceable and fail closed; no outcome panel state may imply successful model-backed evidence, outcome proof, approval, activation, or rollback when the backend path did not run.

Accessibility, responsive, and UI realization:

- Use the selected web UI style guide, named-theme contract, and component-catalog outcome-panel, metric-summary, evidence-list, note-form, validation-summary, action-group, system-message, and trace-link anatomy.
- Metric summaries, recommendation cards, evidence links, outcome-note history, note fields, disabled-action reasons, and submit controls are keyboard-operable, have accessible names that include the proposal/outcome summary, preserve focus after refresh/note results, and announce empty, success, validation, forbidden, stale, and blocked-provider-or-runtime states.
- Responsive layouts preserve outcome-summary-before-note-entry ordering, keep evidence redaction and recovery information visible, and do not hide required denial, redaction, trace-summary, or provider/runtime blocker information.

Required tests:

- App-description/contract tests prove the outcome contract includes payload schema, note lifecycle rules, action mapping, auth/tenant rules, idempotency, states, traces, redaction, no-direct-mutation semantics, and sufficiency review.
- Frontend tests prove outcome-panel rendering for empty/ready/read-only/partial/stale/blocked-provider-or-runtime/system-message states, authorized action visibility, disabled-action reasons, secret-boundary redaction, keyboard note flow, and no client-side authority mutation.
- Backend/API tests prove selected AuthContext scoping, missing-bearer and missing-capability denials, no-enumeration hidden proposal/outcome access, outcome-note validation, lifecycle/freshness validation, idempotency/replay behavior, trace/audit/work evidence, provider/runtime fail-closed evidence references, and no authority mutation from outcome notes.
- Negative tests prove the browser and agents cannot approve, activate, roll back, weaken policy, expand tenant/customer scope, expose hidden policy internals, fabricate outcome evidence, mutate metrics directly, or duplicate notes on repeated submissions.

Surface-description sufficiency review: this definition is sufficiently unambiguous for a developer or generator to implement and review `surface-governance-policy-outcome` without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics. The default view avoids internal implementation details that do not help the target SaaS user, and no additional description pass is required before scoped implementation work for this outcome surface.

### `surface-governance-policy-impact-analysis-task` (`governance.policy.impact_analysis.task.v1`)

Pattern: `workflow-status`.

Owning workstream: Governance/Policy. Owning functional agent: `governance-policy-agent`. Reusable placements: opened from dashboard impact-analysis queues, inventory row actions, proposal/detail lifecycle gates, simulation/decision/outcome evidence follow-ups, result disposition breadcrumbs, and audit/trace drilldowns only when the selected `AuthContext` grants Governance/Policy read or impact-analysis authority. Purpose: start, monitor, cancel, and recover a durable advisory policy-impact analysis task without approving, activating, rolling back, weakening policy, or fabricating model-backed evidence in the browser.

Collection-object progression role: this is the domain-specific autonomous-task workflow/status surface for a visible policy proposal. It follows proposal discovery or proposal lifecycle review and precedes the human impact-analysis result review surface when the task completes with reviewable evidence. It does not embed list/search, proposal editing, policy decisions, activation, rollback, outcome notes, result disposition, or destructive lifecycle behavior.

User goal: understand whether advisory impact analysis for a policy proposal is queued, running, blocked, failed, cancelled, or ready for human review; see safe progress, blockers, evidence summaries, and authorized next actions; and recover from provider/runtime, authorization, stale task, or tenant/customer scope failures without exposing raw provider/model output or hidden policy internals.

Data source and backend authority: the payload is produced by the protected Governance/Policy impact-analysis task projection for the backend-resolved tenant/customer/workspace scope. Browser-provided proposal ids, task ids, scope hints, reason text, tenant/customer hints, correlation keys, and idempotency keys are untrusted request metadata; the backend re-resolves selected `AuthContext`, proposal visibility, impact-analysis capabilities, autonomous-agent/provider readiness, task lifecycle state, trace visibility, redaction, and allowed actions before returning this workflow-status surface or a safe system message.

Required payload schema (frontend-safe):

- `taskSummary`: impact task display ref, proposal display ref, proposal title, task status, lifecycle label, requested analysis scope summary, requested-by display summary, started/updated age bucket, freshness/conflict state, and safe success/recovery copy.
- `progress`: ordered workflow stages such as requested, queued, provider/runtime-check, running-analysis, awaiting-evidence, completed-review-required, cancelled, failed, accepted, rejected-result, and request-changes; each stage includes label, current/completed/error state, progress percentage or unknown marker, and user-facing blocker copy.
- `blockers`: provider unavailable, autonomous-agent runtime unavailable, missing model/tool configuration, missing proposal evidence, stale proposal version, missing capability, tenant/customer scope denial, task cancellation, task failure, duplicate idempotency replay, or partial-data conditions with safe recovery instructions.
- `evidenceRefs`: simulation summary, source proposal summary, policy decision prerequisite summary, task output summary when reviewable, evidence freshness, result-surface target, and redaction markers; raw provider/model output, prompts, raw governed-tool payloads, and hidden policy clauses are omitted from ordinary payloads.
- `authorizedActions`: only backend-authorized actions valid for the selected actor and lifecycle, including `action-governance-policy-start-impact-analysis`, `action-governance-policy-read-impact-analysis`, `action-governance-policy-cancel-impact-analysis`, `action-governance-policy-read`, and follow-up `action-governance-policy-simulate`, `action-governance-policy-decide`, or result-disposition actions only when the backend says the proposal state permits them.
- `disabledActions`: useful but unavailable actions with safe reasons such as missing impact-analysis capability, missing read capability, provider/runtime blocked, stale task/proposal version, already-cancelled task, already-dispositioned result, pending human review, tenant/customer scope denial, or read-only selected context.
- `traceLinks`: user-readable trace summaries plus role-gated references to policy-decision trace, admin-audit event, workstream-log trace, agent-work trace, autonomous-agent task events, provider/runtime failure events, result-review trace, and denial/failure traces.
- `redaction`: field-level indicators for hidden cross-tenant/customer evidence, privileged policy clauses, hidden authority state, raw provider/model output, raw prompts, raw governed-tool payloads, JWTs, secrets, raw correlation ids, and idempotency keys.
- `readiness`: provider/runtime/configuration state for the autonomous impact-analysis path represented as ready, queued, running, blocked, unknown, failed, or not-required summaries; unavailable providers or autonomous-agent runtime are never rendered as successful analysis.
- `activationBlocked`: always present as a user-facing boolean and reason summary while impact analysis is required or pending review; this task surface cannot approve, activate, roll back, or clear activation gates client-side.
- `noDirectMutation`: always `true`; the browser renders backend-authored task state and submits governed start/read/cancel actions only. The frontend and agents cannot approve, activate, roll back, weaken security, expand authority, accept/reject results, or fabricate evidence locally.

Visibility split:

- Default user-visible fields: proposal title/display ref, task status label, progress stages, safe blocker/recovery copy, evidence availability summary, activation-blocked reason, authorized next actions, and result-review availability.
- On-demand drilldown fields: task display ids, detailed lifecycle history, evidence summaries, redacted trace summaries, provider/runtime readiness explanations, idempotency replay status, and scoped source artifact details.
- Admin/support/auditor-only fields: capability ids, policy-decision trace refs, admin-audit refs, workstream-log refs, agent-work/autonomous-task refs, provider/runtime failure evidence, denial/failure evidence, redaction reasons, and diagnostic idempotency/correlation status.
- Internal-only metadata never rendered in ordinary browser payloads: raw provider/model data, prompts, governed-tool payloads, backend component names, raw policy clauses, hidden role policy state, database ids/cursors, cross-tenant identifiers, JWTs/secrets, correlation ids, and idempotency implementation details.

Allowed lifecycle and task rules: starting an impact-analysis task requires a visible proposal, `governance.policy.impact_analysis.start`, required reason/scope, valid proposal lifecycle state, provider/runtime readiness, current proposal version/freshness token, and idempotency key. Reading a task requires `governance.policy.impact_analysis.read` or Governance/Policy read authority for a visible task in the selected scope. Cancelling requires `governance.policy.impact_analysis.cancel`, a queued or running task, non-stale task version, and idempotency key. Completed analysis enters `completed-review-required` and routes to `surface-governance-policy-impact-analysis-result`; completion does not approve, activate, roll back, or change authority. Invalid, stale, duplicate, unauthorized, provider-blocked, or prerequisite-missing requests return validation/conflict/blocked/system-message results with `noFakeSuccess=true` and never imply successful analysis.

Allowed statuses include queued, running, blocked_provider_or_runtime, completed-review-required, cancelled, failed, accepted, rejected_result, and request_changes. Missing provider/runtime returns blocked_provider_or_runtime with no fake success.

Required states: loading, empty/no-task, ready, starting, running, cancelling, success/started, success/cancelled, completed-review-required, validation-error, forbidden/system-message, conflict/stale, partial-data, blocked-provider-or-runtime, read-only, failure, accepted, rejected-result, and request-changes.

State semantics:

- `loading`: workstream shell has selected Governance/Policy and is fetching the protected impact-analysis task projection.
- `empty/no-task`: actor is authorized, but no task exists for the visible proposal; show backend-authorized start action when permitted and safe explanation when not.
- `ready`: task summary, progress, blockers, evidence, traces, activation-blocked status, and authorized actions are backed by the protected task projection for the selected `AuthContext`.
- `starting`: a governed `start-policy-impact-analysis` command is in flight with proposal display ref, requested scope, reason, freshness token, idempotency key, and correlation key; repeat submissions are idempotent and must not duplicate tasks or audit events.
- `running`: backend task projection indicates queued or running autonomous-agent work; show progress and trace summary without raw provider/model payloads.
- `cancelling`: a governed cancel request is in flight for a queued/running task; repeat submissions are idempotent and cannot cancel hidden or completed tasks.
- `success/started`: rendered only after the backend confirms a task was created or an idempotent replay returned the original task; success copy includes refreshed progress, activation-blocked status, and trace summary.
- `success/cancelled`: rendered only after the backend confirms cancellation; success copy preserves proposal authority state and safe next actions.
- `completed-review-required`: task has reviewable advisory evidence and must route to `surface-governance-policy-impact-analysis-result` for human disposition before any downstream activation gate treats the evidence as accepted.
- `validation-error`: missing reason/scope, unsupported lifecycle state, missing proposal evidence, stale idempotency key, invalid task transition, or unsupported cancel request returns field-level/user-safe copy without changing authority.
- `forbidden/system-message`: missing bearer token, missing selected context, missing read/start/cancel capability, hidden proposal/task, or tenant/customer scope denial returns `surface-governance-policy-system-message` with `noFakeSuccess=true`, `noDirectMutation=true`, and no hidden proposal/task enumeration.
- `conflict/stale`: proposal version, task version, lifecycle state, evidence state, or trace freshness changed since the actor opened the surface; disable start/cancel and side-effecting follow-ups until refresh.
- `partial-data`: some progress details, evidence summaries, trace summaries, or readiness details are omitted; visible sections identify what was omitted and why.
- `blocked-provider-or-runtime`: provider/model configuration or autonomous-agent runtime required for advisory impact analysis is unavailable; show blocked status and recovery, not fabricated analysis.
- `read-only`: actor can inspect visible task status but cannot start or cancel tasks; denied actions are omitted or shown as safe disabled explanations only when useful.
- `failure`: unexpected read or command failure returns a safe system message with trace/audit reference and no raw exception, token, provider, policy-engine, storage, model, prompt, or component details.
- `accepted`, `rejected-result`, and `request-changes`: result disposition has already been recorded on the result surface; task status remains evidence history and cannot re-accept/reject inline.

Action contract:

| Visible action / target | Browser action id | Governed tool | Capability | Request payload | Result surface | Notes |
|---|---|---|---|---|---|---|
| Open/read impact-analysis task | `action-governance-policy-read-impact-analysis` | `read-policy-impact-analysis` | `governance.policy.impact_analysis.read` | proposal display ref, task display ref when present, refresh reason, correlation key generated by client or backend | `surface-governance-policy-impact-analysis-task` or `surface-governance-policy-system-message` | Read-only projection; backend reauthorizes task/proposal visibility, trace visibility, and selected scope. |
| Start impact-analysis task | `action-governance-policy-start-impact-analysis` | `start-policy-impact-analysis` | `governance.policy.impact_analysis.start` | proposal display ref, requested scope, reason, current proposal version/freshness token, idempotency key, correlation key | `surface-governance-policy-impact-analysis-task` or `surface-governance-policy-system-message` | Starts durable advisory autonomous-agent work only; provider/runtime unavailable returns blocked_provider_or_runtime with no fake success. |
| Cancel impact-analysis task | `action-governance-policy-cancel-impact-analysis` | `cancel-policy-impact-analysis` | `governance.policy.impact_analysis.cancel` | proposal display ref, task display ref, current task version/freshness token, reason when required, idempotency key, correlation key | `surface-governance-policy-impact-analysis-task` or `surface-governance-policy-system-message` | Cancels queued/running work only; proposal state and authority remain unchanged. |
| Open source proposal or detail | `action-governance-policy-read` | `list-policy-proposals` | `governance.policy.read` | proposal display ref, optional source task ref, correlation key | `surface-governance-policy-proposal`, `surface-governance-policy-detail`, or `surface-governance-policy-system-message` | Opens scoped proposal evidence; backend reauthorizes visibility and redacts hidden policy internals. |
| Open completed result review | `action-governance-policy-read-impact-analysis` | `read-policy-impact-analysis` | `governance.policy.impact_analysis.read` | proposal display ref, task display ref, open result intent, correlation key | `surface-governance-policy-impact-analysis-result` or `surface-governance-policy-system-message` | Available only when status is `completed-review-required`, accepted, rejected_result, or request_changes and backend exposes the result surface. |

Hidden or denied actions: approval, activation, rollback, result acceptance/rejection/request-changes, outcome-note mutation, raw policy-clause edits, tenant/customer scope expansion, hidden proposal/task access, direct provider/model prompts, client-side progress fabrication, and evidence fabrication are not performed by this task surface. Direct/deep-link attempts without authority return `surface-governance-policy-system-message`, are audit/trace recorded, and must not reveal whether hidden proposals, tasks, policies, capabilities, provider outputs, or cross-tenant/customer evidence exists.

Authorization and tenant scope:

- `governance.policy.impact_analysis.read` or Governance/Policy read authority is required to open a visible task projection; `governance.policy.impact_analysis.start` is required to start a task; `governance.policy.impact_analysis.cancel` is required to cancel queued/running work.
- The backend resolves tenant/customer/workspace authority from selected `AuthContext`; browser tenant/customer, proposal, task, scope, correlation, and idempotency fields cannot expand scope, enumerate hidden tasks, or bypass lifecycle prerequisites.
- Impact-analysis actions are exposed only as backend-authorized actions for visible proposals/tasks and valid lifecycle states; denied or invalid actions are omitted or returned with safe disabled reasons/system messages.
- Cross-tenant/customer proposal evidence, privileged policy clauses, hidden authority state, raw provider/model content, prompts, raw tool payloads, JWTs, secrets, raw task implementation details, and implementation correlation/idempotency details are redacted or omitted.
- Direct proposal/task ids, hidden rows, missing capability, stale version, unsupported lifecycle, provider/runtime failure, or hidden evidence references return safe no-enumeration denial, blocked, or validation states with trace refs, `noFakeSuccess=true`, and `noDirectMutation=true`.

Trace, audit, and work evidence:

- Task reads, starts, cancels, refreshes, denials, stale/conflict results, provider/runtime readiness checks, task failures, and result-ready transitions produce workstream-log/correlation evidence.
- Consequential start/cancel commands produce admin-audit and impact-analysis task trace evidence with idempotency outcome and selected `AuthContext`; autonomous-agent work links `agent-work-trace` and provider/runtime failure traces without copying raw provider/model data into the browser payload.
- Default trace copy is human-readable; raw ids/details are visible only through role-gated audit/support drilldowns.
- Provider/runtime failures are traceable and fail closed; no task surface state may imply successful model-backed analysis, result acceptance, approval, activation, or rollback when the backend path did not run.

Accessibility, responsive, and UI realization:

- Use the selected web UI style guide, named-theme contract, and component-catalog workflow-status, progress-stepper, blocker-callout, evidence-list, action-group, system-message, and trace-link anatomy.
- Progress steps, blocker callouts, evidence links, disabled-action reasons, start/cancel controls, result-review links, and trace summaries are keyboard-operable, have accessible names that include proposal/task status, preserve focus after refresh/start/cancel results, and announce queued, running, cancelled, completed-review-required, forbidden, stale, failed, and blocked-provider-or-runtime states.
- Responsive layouts preserve task-summary-before-actions ordering, keep activation-blocked and provider/runtime recovery information visible, and do not hide required denial, redaction, trace-summary, or evidence-readiness information.

Required tests:

- App-description/contract tests prove the impact-analysis task contract includes payload schema, lifecycle/status rules, action mapping, auth/tenant rules, idempotency, provider/runtime fail-closed states, traces, redaction, no-direct-mutation semantics, and sufficiency review.
- Frontend tests prove workflow-status rendering for empty/ready/running/read-only/partial/stale/completed-review-required/cancelled/failed/blocked-provider-or-runtime/system-message states, authorized action visibility, disabled-action reasons, secret-boundary redaction, keyboard start/cancel/result-review flow, and no client-side authority mutation or evidence fabrication.
- Backend/API tests prove selected AuthContext scoping, missing-bearer and missing-capability denials, no-enumeration hidden proposal/task access, start/cancel lifecycle validation, idempotency/replay behavior, provider/runtime fail-closed task state, trace/audit/work evidence, result-ready routing, and no authority mutation from task actions.
- Negative tests prove the browser and agents cannot approve, activate, roll back, weaken policy, expand tenant/customer scope, expose hidden policy internals, fabricate provider-backed analysis, accept/reject impact results inline, or duplicate/cancel tasks on repeated submissions.

Surface-description sufficiency review: this definition is sufficiently unambiguous for a developer or generator to implement and review `surface-governance-policy-impact-analysis-task` without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics. The default view avoids internal implementation details that do not help the target SaaS user, and no additional description pass is required before scoped implementation work for this workflow-status surface.

### `surface-governance-policy-impact-analysis-result` (`governance.policy.impact_analysis.result.v1`)

Pattern: `decision-card`.

Owning workstream: Governance/Policy. Owning functional agent: `governance-policy-agent`. Reusable placements: opened from `surface-governance-policy-impact-analysis-task` when an impact-analysis task reaches `completed-review-required`, from proposal/detail/decision/activation gates that need human disposition of advisory impact evidence, from outcome/evidence follow-ups, and from audit/trace drilldowns only when the selected `AuthContext` grants the required Governance/Policy impact-result authority. Purpose: review completed advisory impact-analysis evidence, record the human disposition, and keep policy approval/activation/rollback blocked until the result is explicitly accepted or sent back, without treating model/provider output as an automatic policy decision.

Collection-object progression role: this is a domain-specific single-action decision surface for the policy impact-analysis task lifecycle. It is not the autonomous task runner, proposal editor, policy approval card, activation/rollback command, or outcome-note surface. Selection and breadcrumbs return to the scoped impact-analysis task, proposal, detail, decision, or inventory surface; downstream approval/activation remains on dedicated decision surfaces after backend prerequisites are satisfied.

User goal: understand the completed advisory impact analysis, inspect findings, omissions, confidence limits, trace/evidence summaries, and activation blockers, then accept the result, reject it, or request changes when authorized without exposing hidden policy internals, raw provider/model output, prompts, tool payloads, secrets, or cross-tenant/customer evidence.

Data source and backend authority: the payload is produced by the protected Governance/Policy impact-analysis result projection for the backend-resolved tenant/customer/workspace scope. Browser-provided proposal ids, task ids, result ids, disposition commands, reasons, tenant/customer hints, correlation keys, and idempotency keys are untrusted request metadata; the backend re-resolves selected `AuthContext`, task/proposal visibility, lifecycle state, result-disposition capability, trace visibility, redaction, and idempotency before returning this surface or a safe system message.

Required payload schema (frontend-safe):

- `resultSummary`: impact task display ref, proposal display ref, proposal title, policy area, task lifecycle/status label, review state (`completed-review-required`, `accepted`, `rejected_result`, or `request_changes`), overall risk label, confidence label, freshness/conflict status, completed age bucket, and safe empty/no-result copy.
- `advisorySummary`: user-facing analysis narrative, decision-relevant recommendation summary, expected business/security impact, known limitations, confidence and coverage notes, omitted/hidden evidence summary, and explicit advisory-only copy stating that the result does not approve, activate, roll back, or change authority.
- `findings`: ordered findings with finding id/display label, severity, affected capability or policy area summary, affected actor/resource/customer/workspace summary when safe, expected allow/deny/change outcome, evidence status, reviewer attention reason, and row redaction markers.
- `evidenceRefs`: source proposal/task refs, redacted simulation refs, policy-decision evidence refs, agent-work/autonomous-task event summaries, supporting workstream-log/admin-audit summaries, provider/runtime readiness summary, and role-gated drilldown targets; raw provider/model output, prompts, raw tool payloads, hidden role clauses, and implementation ids are omitted from the default view.
- `humanDecision`: required reviewer decision, allowed disposition values, required reason rules for reject/request-changes, reviewer note draft state, prior disposition summary when present, disabled disposition reasons, and idempotency/replay-safe status copy.
- `activationGate`: whether accepted impact evidence is required before approval/activation, current gate state, missing prerequisites, disabled approval/activation/rollback reasons, related simulation/outcome/decision target surfaces, and confirmation that result disposition alone cannot activate or roll back policy.
- `authorizedActions`: only backend-authorized result actions for the selected actor and lifecycle state, including `action-governance-policy-read-impact-analysis`, `action-governance-policy-accept-impact-result`, `action-governance-policy-reject-impact-result`, `action-governance-policy-request-impact-changes`, `action-governance-policy-read`, `action-governance-policy-decide`, and `action-governance-policy-outcome-note` when each corresponding capability and lifecycle rule is satisfied.
- `traceLinks`: user-readable trace summaries plus role-gated references to policy-decision trace, admin-audit event, workstream-log trace, agent-work/autonomous-task events, impact-analysis worker/task events, provider/runtime failure traces, and denial/failure traces.
- `redaction`: field-level indicators for hidden cross-tenant/customer evidence, privileged policy clauses, hidden authority state, raw provider/model output, raw prompts, raw governed-tool payloads, JWTs, secrets, raw correlation ids, and idempotency keys.
- `readiness`: provider/runtime/configuration state that produced the result or blocked evidence sections, represented as completed, partial, stale, blocked, unknown, or not-required summaries; unavailable or failed provider/runtime paths are never rendered as successful analysis.
- `noDirectMutation`: always `true`; the browser renders backend-authored state and sends governed disposition actions only. It cannot approve, activate, roll back, weaken security, expand authority, mutate proposal scope, or fabricate evidence locally.

Visibility split:

- Default user-visible fields: proposal title/display ref, task/review status, overall risk/confidence, advisory summary, finding summaries, omitted-evidence copy, activation-blocked state, authorized next actions, required reason guidance, and recovery instructions.
- On-demand drilldown fields: task/proposal display ids, detailed finding evidence, redacted lifecycle history, reviewer disposition history, evidence/source summaries, trace summaries, and scoped source artifact details.
- Admin/support/auditor-only fields: capability ids, policy-decision trace refs, admin-audit refs, workstream-log refs, agent-work/autonomous-task refs, impact-analysis worker/task refs, provider/runtime failure evidence, denial/failure evidence, redaction reasons, and diagnostic idempotency/correlation status.
- Internal-only metadata never rendered in ordinary browser payloads: raw provider/model data, prompts, raw governed-tool payloads, backend component names, raw policy clauses, hidden role policy state, database ids/cursors, cross-tenant identifiers, JWTs/secrets, correlation ids, and idempotency implementation details.

Allowed lifecycle and disposition rules: reading a result requires a visible proposal/task and `governance.policy.impact_analysis.read` or a backend-authorized read edge for the selected scope. Accepting a result requires `governance.policy.impact_analysis.accept_result`, a completed-review-required result, current result/task freshness, and an idempotency key; it records advisory evidence disposition only and may unblock a downstream activation prerequisite, but does not approve or activate policy. Rejecting or requesting changes requires the corresponding `governance.policy.impact_analysis.reject_result` or `governance.policy.impact_analysis.request_changes` capability, a non-empty reason, current freshness, and an idempotency key; it records disposition and keeps approval/activation blocked until replacement evidence is produced. Repeating the same terminal disposition is idempotent or returns the original result; conflicting terminal dispositions, stale freshness, hidden tasks, and unsupported lifecycle states return safe validation/conflict/system-message results with no authority mutation.

Required states: loading, empty/no-result, ready, reviewing, submitting-disposition, accepted, rejected-result, request-changes, validation-error, forbidden/system-message, conflict/stale, partial-data, blocked-provider-or-runtime, read-only, and failure.

State semantics:

- `loading`: workstream shell has selected Governance/Policy and is fetching the protected result projection.
- `empty/no-result`: actor is authorized to read the result surface, but no task/result id or completed result is selected; render safe recovery actions back to task, proposal, dashboard, or inventory without enumerating hidden tasks.
- `ready`: result summary, findings, evidence refs, activation gate, traces, and authorized disposition actions are backed by the protected result projection for the selected `AuthContext`.
- `reviewing`: reviewer is reading completed advisory evidence; no side effect occurs until a governed disposition action succeeds.
- `submitting-disposition`: accept/reject/request-changes action is in flight with task display ref, proposal display ref, current freshness token, reason when required, idempotency key, and correlation key; repeated submissions cannot duplicate disposition/audit effects.
- `accepted`: backend recorded advisory result acceptance and returns the refreshed result surface with activation gate status; approval/activation actions still require dedicated policy decision/activation surfaces.
- `rejected-result`: backend recorded rejection with reviewer reason and keeps activation blocked; source proposal authority remains unchanged.
- `request-changes`: backend recorded requested changes with reviewer reason and keeps activation blocked until replacement analysis is started/completed.
- `validation-error`: missing reason, missing/stale idempotency key, unsupported lifecycle state, missing result evidence, invalid task/proposal freshness, or conflicting disposition returns field-level/user-safe copy without changing authority.
- `forbidden/system-message`: missing bearer token, missing selected context, missing impact-result capability, hidden proposal/task/result, or tenant/customer scope denial returns `surface-governance-policy-system-message` with `noFakeSuccess=true`, `noDirectMutation=true`, and no hidden proposal/task/result enumeration.
- `conflict/stale`: result version, task lifecycle, proposal version, evidence state, or trace freshness changed since the actor opened the surface; disable side-effecting disposition actions until refresh.
- `partial-data`: some findings, evidence, trace summaries, or readiness details are omitted; visible sections identify what was omitted and why.
- `blocked-provider-or-runtime`: provider/model configuration or autonomous-agent runtime prevented complete evidence generation; show blocked/partial status and recovery, not fabricated analysis.
- `read-only`: actor may inspect the result but cannot record disposition; denied disposition actions are omitted or shown as safe disabled explanations only when useful.
- `failure`: unexpected read or disposition failure returns a safe system message with trace/audit reference and no raw exception, token, provider, storage, or policy-engine details.

Action contract:

| Visible action / target | Browser action id | Governed tool | Capability | Request payload | Result surface | Notes |
|---|---|---|---|---|---|---|
| Open/read impact-analysis result | `action-governance-policy-read-impact-analysis` | `read-policy-impact-analysis` | `governance.policy.impact_analysis.read` | proposal display ref, task display ref, open result intent, refresh reason, correlation key generated by client or backend | `surface-governance-policy-impact-analysis-result` or `surface-governance-policy-system-message` | Read-only projection; backend reauthorizes task/proposal/result visibility, trace visibility, and selected scope. |
| Accept advisory result | `action-governance-policy-accept-impact-result` | `accept-policy-impact-result` | `governance.policy.impact_analysis.accept_result` | proposal display ref, task display ref, current result/task freshness token, reviewer acknowledgement, idempotency key, correlation key | `surface-governance-policy-impact-analysis-result` or `surface-governance-policy-system-message` | Records advisory evidence disposition only; no policy approval, activation, rollback, or authority change. |
| Reject advisory result | `action-governance-policy-reject-impact-result` | `reject-policy-impact-result` | `governance.policy.impact_analysis.reject_result` | proposal display ref, task display ref, required reason, current result/task freshness token, idempotency key, correlation key | `surface-governance-policy-impact-analysis-result` or `surface-governance-policy-system-message` | Requires user-safe reason; keeps activation blocked and leaves proposal authority unchanged. |
| Request impact-analysis changes | `action-governance-policy-request-impact-changes` | `request-policy-impact-changes` | `governance.policy.impact_analysis.request_changes` | proposal display ref, task display ref, required reason/change request summary, current result/task freshness token, idempotency key, correlation key | `surface-governance-policy-impact-analysis-result` or `surface-governance-policy-system-message` | Records requested changes and routes follow-up to task/proposal surfaces; no authority mutation. |
| Open source proposal/detail | `action-governance-policy-read` | `list-policy-proposals` | `governance.policy.read` | proposal display ref, optional source result/task ref, correlation key | `surface-governance-policy-proposal`, `surface-governance-policy-detail`, or `surface-governance-policy-system-message` | Opens scoped proposal evidence; backend reauthorizes visibility and redacts hidden policy internals. |
| Open downstream decision work | `action-governance-policy-decide` | `approve-activate-or-rollback-policy` | `governance.policy.approve` | proposal display ref, command mode `decide`, source result/task ref, reason when supplied, correlation key | `surface-governance-policy-decision` or `surface-governance-policy-system-message` | Result surface may route to decision review only when backend prerequisites and capabilities are satisfied; it cannot approve inline. |
| Open outcome note | `action-governance-policy-outcome-note` | `record-policy-outcome-note` | `governance.outcomes.record` | proposal display ref, source result/task ref, observation/open intent, idempotency key when recording, correlation key | `surface-governance-policy-outcome` or `surface-governance-policy-system-message` | Outcome observations do not change authority or result disposition. |

Hidden or denied actions: proposal approval, activation, rollback, task start/cancel, raw provider/model prompt access, raw policy-clause edits, outcome-note mutation without capability, tenant/customer scope expansion, hidden proposal/task/result access, client-side disposition, authority mutation, and evidence fabrication are not performed by this result surface. Direct/deep-link attempts without authority return `surface-governance-policy-system-message`, are audit/trace recorded, and must not reveal whether hidden proposals, policies, tasks, results, capabilities, or cross-tenant/customer evidence exists.

Authorization and tenant scope:

- `governance.policy.impact_analysis.read` is required to open/read a visible result unless the backend exposes an equivalent read edge for the selected Governance/Policy scope.
- `governance.policy.impact_analysis.accept_result`, `governance.policy.impact_analysis.reject_result`, and `governance.policy.impact_analysis.request_changes` are exposed only as backend-authorized actions for the selected actor, visible result, and valid lifecycle state.
- The backend resolves tenant/customer/workspace authority from selected `AuthContext`; browser fields cannot expand scope, enumerate hidden proposals/tasks/results, or request raw authority state.
- Disposition requests must be idempotent and freshness-aware so repeat or stale submissions cannot duplicate audit/disposition effects or skip lifecycle gates.
- Result findings, evidence refs, trace drilldowns, and source artifact details redact hidden cross-tenant/customer evidence, privileged policy clauses, raw provider/model content, prompts, raw tool payloads, JWTs, secrets, and implementation correlation/idempotency details.

Trace, audit, and work evidence:

- Result reads, disposition attempts, validation/conflict outcomes, denials, stale refreshes, and routed follow-up actions produce workstream-log/correlation evidence.
- Accept/reject/request-changes commands produce admin-audit and policy-decision trace evidence even when validation fails; impact-analysis evidence links agent-work/autonomous-task events; denials/provider/runtime failures link failure traces.
- Default trace copy is human-readable; raw ids/details are visible only through role-gated audit/support drilldowns.
- Repeated disposition commands require idempotency evidence and must return the original outcome or a safe conflict message.

Accessibility, responsive, and UI realization:

- Use the selected web UI style guide, named-theme contract, and component-catalog decision-card, evidence-list, badge, validation-summary, system-message, action-bar, trace-summary, and confirmation anatomy.
- Findings, evidence sections, required decisions, disposition controls, disabled activation blockers, breadcrumbs, and trace links are keyboard-operable, announce risk/review/action targets, preserve focus after disposition results, and provide accessible names for affected policy areas and consequence copy.
- Responsive layouts may stack summary, findings, evidence, and decision panels but must preserve backend-authored actions, redaction markers, validation states, trace summaries, and recovery guidance.

Required tests:

- App-description/contract tests prove the result contract includes payload schema, lifecycle/disposition states, action mappings, auth/tenant rules, idempotency, no-direct-mutation semantics, traces, redaction, provider/runtime fail-closed behavior, and sufficiency review.
- Frontend tests prove decision-card rendering, no-result/ready/submitting/accepted/rejected/request-changes/validation/conflict/partial/blocked-provider-or-runtime/read-only/system-message states, backend-authorized action visibility, required reason handling, keyboard navigation, focus preservation, and secret-boundary redaction.
- Backend/API tests prove selected AuthContext scoping, missing-bearer and missing-capability denials, no-enumeration hidden proposal/task/result access, result read routing from completed task, accept/reject/request-changes idempotency, stale/conflicting disposition handling, trace/audit/work evidence, activation-gate semantics, and provider/runtime fail-closed advisory statuses.
- Negative tests prove the browser and agents cannot approve, activate, roll back, weaken policy, expand tenant/customer scope, expose hidden policy internals, fabricate provider-backed analysis, mutate proposal authority, or duplicate/conflict disposition side effects.

Surface-description sufficiency review: this definition is sufficiently unambiguous for a developer or generator to implement and review `surface-governance-policy-impact-analysis-result` without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics. The default view avoids internal implementation details that do not help the target SaaS user, and no additional description pass is required before scoped implementation work for this decision-card surface.

### `surface-governance-policy-system-message` (`governance.policy.system_message.v1`)

Pattern: `system-message`.

Owning workstream: Governance/Policy. Owning functional agent: `governance-policy-agent`. Reusable placements: returned by the Governance/Policy dashboard, inventory, detail, proposal, simulation, decision, outcome, impact-analysis task, and impact-analysis result surfaces whenever a request cannot produce the requested governed surface safely. Purpose: communicate authorization denials, missing selected context, missing or hidden proposal/task/result references, validation errors, conflict/stale states, provider/runtime blockers, unsupported authority expansion, and unexpected failures without leaking hidden policy internals or implying a successful policy action.

Collection-object progression role: this is a safe terminal/recovery result surface, not a policy/proposal list, proposal editor, policy decision, activation/rollback command, outcome note, autonomous task runner, or impact-result disposition surface. It may offer backend-authorized recovery links back to the relevant Governance/Policy surface, but it never performs a mutation or changes authority itself.

User goal: understand why the requested Governance/Policy work is unavailable, forbidden, stale, blocked, or failed; see what can be tried next; and give an administrator, auditor, or support actor enough safe trace context to investigate without exposing raw policy clauses, provider/model output, prompts, tool payloads, secrets, cross-tenant/customer evidence, or hidden authority state.

Data source and backend authority: the payload is produced by the protected Governance/Policy workstream endpoint or service that rejected, blocked, or failed the original surface/action request after resolving the selected `AuthContext`. Browser-provided tenant/customer/workspace ids, proposal ids, task ids, result ids, action ids, correlation keys, and idempotency keys are untrusted request metadata; the backend re-resolves scope, capability, lifecycle, trace visibility, and redaction before returning this surface.

Required payload schema (frontend-safe):

- `messageSummary`: message id/display ref, originating surface id when safe, original browser action id when safe, status (`forbidden`, `missing-context`, `validation-error`, `conflict-stale`, `blocked-provider-or-runtime`, `not-found-or-hidden`, or `failure`), severity, safe title, safe reason code, and recovery-oriented user message.
- `contextSummary`: selected workstream label, selected `AuthContext` display label when available, requested proposal/task/result display ref only when visible, lifecycle/readiness summary when safe, and an omission reason for hidden or redacted context.
- `recoveryOptions`: backend-authorized recovery links such as select/switch context, retry read/refresh, return to `surface-governance-policy-dashboard`, open scoped inventory, open the relevant proposal/task/result surface after refresh, or ask an organization administrator; each option includes target surface, optional reused action id, disabled reason, and no side-effect guarantee.
- `validationMessages`: field-level or request-level safe validation copy for missing reason/rationale, stale freshness, unsupported lifecycle command, missing idempotency key, unsupported filter/page hints, hidden proposal/task/result references, or provider/runtime prerequisites.
- `authorizedActions`: only backend-authorized, safe recovery/read actions for the selected actor; system-message recovery actions reuse existing Governance/Policy action ids such as `action-governance-policy-dashboard`, `action-governance-policy-list`, `action-governance-policy-read`, `action-governance-policy-read-impact-analysis`, or the original retry action when the backend confirms it is still safe and authorized.
- `traceLinks`: user-readable trace summaries plus role-gated references to policy-decision trace, admin-audit event, workstream-log trace, agent-work/autonomous-task trace, impact-analysis worker/task events, provider/runtime failure trace, and denial/failure trace.
- `redaction`: field-level indicators for hidden cross-tenant/customer evidence, hidden proposal/task/result existence, privileged policy clauses, raw provider/model output, raw prompts, raw governed-tool payloads, JWTs, secrets, raw correlation ids, idempotency keys, and backend component details.
- `readiness`: provider/model/autonomous-agent/runtime/configuration state represented as ready, blocked, unavailable, stale, unknown, or not-applicable; unavailable runtime dependencies must be rendered as blocked recovery states and never as successful analysis, simulation, or evidence generation.
- `noFakeSuccess`: always `true`; this surface must never claim that a denied, blocked, failed, stale, or invalid action succeeded.
- `noDirectMutation`: always `true`; the browser renders backend-authored recovery state and can only invoke separate governed actions that the backend authorizes.

Visibility split:

- Default user-visible fields: safe title, reason code in user language, recovery guidance, selected-context label when available, visible target display ref when safe, disabled-action explanation, and safe next action labels.
- On-demand drilldown fields: redacted lifecycle context, validation detail, readiness detail, trace summaries, prior action/result summary, and scoped proposal/task/result display metadata.
- Admin/support/auditor-only fields: capability ids, policy-decision trace refs, admin-audit refs, workstream-log refs, agent-work/autonomous-task refs, impact-analysis worker/task refs, provider/runtime failure evidence, denial/failure evidence, redaction reasons, and diagnostic idempotency/correlation status.
- Internal-only metadata never rendered in ordinary browser payloads: raw policy clauses, hidden role policy state, raw provider/model data, prompts, raw governed-tool payloads, backend component names, database ids/cursors, hidden cross-tenant/customer identifiers, JWTs/secrets, raw correlation ids, idempotency keys, and stack traces.

Required states: loading, forbidden, missing-context, validation-error, conflict/stale, blocked-provider-or-runtime, not-found-or-hidden, partial-data, retryable-failure, terminal-failure, and ready/recovery.

State semantics:

- `loading`: the workstream shell is waiting for the protected endpoint to classify the denial, validation, conflict, blocked, or failure result.
- `forbidden`: missing bearer token, missing selected context authority, missing required Governance/Policy capability, tenant/customer scope denial, or hidden policy evidence returns safe denial copy with no hidden proposal/task/result enumeration.
- `missing-context`: no selected `AuthContext`, no visible Governance/Policy workstream binding, or missing required proposal/task/result display ref returns context-selection or safe navigation recovery.
- `validation-error`: malformed filter/page/action payloads, missing reason/rationale, missing idempotency key for side-effecting retry, unsupported command mode, or lifecycle-precondition failures return field-level/user-safe copy with no authority mutation.
- `conflict/stale`: proposal, task, result, lifecycle state, freshness token, provider readiness, or trace freshness changed since the actor opened the source surface; disable side-effecting retry until refresh.
- `blocked-provider-or-runtime`: model/provider configuration, policy simulator, autonomous-agent runtime, or impact-analysis worker is unavailable; show blocked recovery and do not fabricate simulation, impact analysis, evidence, approval, activation, or rollback success.
- `not-found-or-hidden`: hidden proposal/task/result, cross-tenant/customer evidence, or no-enumeration direct access returns safe recovery without confirming whether the object exists.
- `partial-data`: some trace, readiness, validation, or context detail is omitted; visible copy identifies what was omitted and why.
- `retryable-failure`: an unexpected read or action failure can be retried safely through a backend-authorized read/refresh option; raw exception, provider, token, storage, and policy-engine details remain hidden.
- `terminal-failure`: an unexpected failure requires administrator/support investigation; expose only safe trace summaries and role-gated diagnostic links.
- `ready/recovery`: the system message has enough backend-authored recovery options for the selected actor to continue safely.

Action contract:

| Visible action / target | Browser action id | Governed tool | Capability | Request payload | Result surface | Notes |
|---|---|---|---|---|---|---|
| Return to Governance/Policy dashboard | `action-governance-policy-dashboard` | `list-policy-proposals` | `governance.policy.read` | selected context/workstream, optional recovery reason, correlation key generated by client or backend | `surface-governance-policy-dashboard` or `surface-governance-policy-system-message` | Read-only recovery; backend reauthorizes selected `AuthContext` and never trusts tenant/customer ids from the browser. |
| Open scoped policy inventory | `action-governance-policy-list` | `list-policy-proposals` | `governance.policy.read` | selected context/workstream, safe filter hints, recovery reason, correlation key | `surface-governance-policy-inventory` or `surface-governance-policy-system-message` | Read-only recovery; no hidden proposal enumeration. |
| Retry/open visible proposal evidence | `action-governance-policy-read` | `list-policy-proposals` | `governance.policy.read` | visible proposal display ref or safe recovery source ref, refresh reason, correlation key | `surface-governance-policy-detail`, `surface-governance-policy-proposal`, or `surface-governance-policy-system-message` | Backend reauthorizes proposal visibility and redacts hidden policy internals. |
| Retry/open visible impact-analysis task/result | `action-governance-policy-read-impact-analysis` | `read-policy-impact-analysis` | `governance.policy.impact_analysis.read` | visible proposal/task/result display refs when safe, refresh reason, correlation key | `surface-governance-policy-impact-analysis-task`, `surface-governance-policy-impact-analysis-result`, or `surface-governance-policy-system-message` | Read-only recovery for advisory task/result state; provider/runtime failures remain blocked states with no fake success. |
| Retry original governed action after refresh | original `action-governance-policy-*` id | original governed tool | original backend capability | refreshed backend-authored payload, required reason/freshness/idempotency when side-effecting, correlation key | original result surface or `surface-governance-policy-system-message` | Offered only when the backend confirms the selected actor, lifecycle, idempotency, and provider/runtime state make the retry safe. The system message itself performs no mutation. |

Hidden or denied actions: approval, activation, rollback, proposal mutation, simulation run, outcome-note mutation, impact-analysis start/cancel, impact-result disposition, raw provider/model prompt access, raw policy-clause edits, tenant/customer scope expansion, hidden-object lookup, and diagnostic trace disclosure are not performed by this system-message surface. Direct attempts without authority return another safe system message, are audit/trace recorded, and must not reveal whether hidden proposals, policies, tasks, results, capabilities, or cross-tenant/customer evidence exists.

Authorization and tenant scope:

- Every system-message payload is scoped to the backend-resolved selected `AuthContext`; browser tenant/customer/workspace hints cannot expand scope, enumerate hidden objects, or change recovery options.
- Recovery actions are included only when the backend authorizes the underlying Governance/Policy capability for the selected actor and target lifecycle state.
- Missing bearer token, missing selected context, missing capability, tenant/customer denial, hidden proposal/task/result, stale freshness, unsupported lifecycle, and provider/runtime blockers return safe reason codes, recovery copy, trace refs, `noFakeSuccess=true`, and `noDirectMutation=true`.
- Redaction must hide cross-tenant/customer evidence, hidden policy authority state, privileged policy clauses, raw provider/model content, prompts, raw tool payloads, JWTs, secrets, stack traces, and implementation correlation/idempotency details from ordinary browser payloads.

Trace, audit, and work evidence:

- System-message creation records workstream-log/correlation evidence for the original request and its safe result classification.
- Denials, validation failures, stale/conflict outcomes, provider/runtime blockers, unexpected failures, and any offered retry/follow-up action link to denial/failure traces; consequential denied or failed actions also link admin-audit and policy-decision trace evidence where applicable.
- Autonomous-agent or impact-analysis blocker messages link agent-work/autonomous-task and impact-analysis worker/task events without copying raw provider/model output, prompts, or tool payloads into the browser payload.
- Default trace copy is human-readable; raw ids/details are exposed only through role-gated audit/support drilldowns.

Accessibility, responsive, and UI realization:

- Use the selected web UI style guide, named-theme contract, and component-catalog system-message, alert, validation-summary, recovery-action, trace-summary, and support-detail anatomy.
- Message title, reason, recovery actions, validation items, trace summaries, and disabled-action explanations are keyboard-operable or screen-reader reachable as appropriate; focus moves to the message heading after a failed/denied action and returns to the triggering control after a safe retry.
- Responsive layouts preserve reason-before-recovery ordering, keep `noFakeSuccess`/blocked-provider-or-runtime recovery copy visible, and do not hide denial, redaction, or trace-summary information.

Required tests:

- App-description/contract tests prove the system-message contract includes payload schema, state taxonomy, recovery action mapping, auth/tenant rules, redaction, trace/audit/work evidence, provider/runtime fail-closed semantics, `noFakeSuccess`, `noDirectMutation`, and sufficiency review.
- Frontend tests prove forbidden/missing-context/validation/conflict/blocked-provider-or-runtime/not-found-or-hidden/partial/retryable-failure/terminal-failure states, safe recovery rendering, backend-authorized action visibility, focus management, keyboard navigation, and secret-boundary redaction.
- Backend/API tests prove selected AuthContext scoping, missing-bearer and missing-capability denials, no-enumeration hidden proposal/task/result access, stale/freshness and validation errors, provider/runtime fail-closed blocker messages, trace/audit/work evidence, and safe retry/action reauthorization.
- Negative/idempotency tests prove system messages never approve, activate, roll back, weaken policy, expand tenant/customer scope, expose hidden policy internals, fabricate provider-backed analysis, mutate proposal/task/result state, or duplicate side effects on repeated recovery attempts.

Surface-description sufficiency review: this definition is sufficiently unambiguous for a developer or generator to implement and review `surface-governance-policy-system-message` without inventing payload fields, recovery actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics. The default view avoids internal implementation details that do not help the target SaaS user, and no additional description pass is required before scoped implementation work for this system-message surface.

## State and action rules

Every consequential browser action carries a stable action id, maps to capability `governance-policy-lifecycle`, carries a correlation/idempotency key where needed, and returns a typed result, decision card, workflow status, outcome panel, markdown response, or safe system message. Side-effecting request payloads include action id, proposal/task id when applicable, command mode where applicable, reason/rationale when required, idempotency key, and correlation id; tenant/customer authority is always resolved from backend-selected `AuthContext`, not trusted browser fields.

Agents may draft, summarize, recommend, and prepare evidence. Agents and frontend state may not approve, activate, roll back, weaken security, expand authority, or fabricate provider/model-backed analysis.

## Surface-description sufficiency review

This pass makes the Governance/Policy surface set sufficiently unambiguous for scoped generation of proposal lifecycle, decision, activation/rollback, outcome, and impact-analysis task surfaces without inventing tool ids, lifecycle states, result authority, core payload categories, denial states, or trace obligations. Visual layout and component styling remain owned by the selected web UI style guide and frontend realization files.

## Required tests and traces

- Frontend contract tests prove the shell renders Governance/Policy dashboard, inventory, diff, decision, workflow-status, outcome, and system-message states.
- Backend tests prove scoped reads, proposal lifecycle idempotency, simulation advisory behavior, activation/rollback prerequisites, outcome panel return, impact-task fail-closed lifecycle, and structured denial surfaces.
- Required trace evidence: policy-decision-trace, admin-audit-event, workstream-log-trace, agent-work-trace, impact-analysis worker/task events, and denial/failure traces.


## Shared chat tool-plan surfaces

Governance/Policy binds the shared `human_chat_tool_plan` surface contracts from `../../surface-catalog.md` to this workstream's functional agent `governance-policy-agent` and representative catalog entry.

| Plan surface | Contract | Workstream binding |
|---|---|---|
| Plan proposal | `chat_tool_plan.proposal.v1` | Shows the no-mutation proposal for **draft a policy proposal to require approval before redacted exports** with actions `action-governance-policy-draft-proposal`, governed tools `governance.policy.propose`, capabilities `governance.policy.propose`, validated inputs `schema.governance-policy.proposal.draft.v1` with title, rationale, browser-safe proposed change summary, affected capabilities, and idempotency key, side effects, approval requirements, idempotency root, output bindings, and trace refs. |
| Plan confirmation | `chat_tool_plan.confirmation.v1` | Requires explicit acknowledgement of the exact plan snapshot, selected `AuthContext`, requested/confirmed actor, step hashes, side effects, transaction boundaries, and idempotency before execution is requested. |
| Plan result | `chat_tool_plan.result.v1` | Reports completed, failed, skipped, no-op/idempotent replay, partial-failure, and recovery states; successful step result surfaces are `surface-governance-policy-proposal`. |
| Plan system message | `chat_tool_plan.system_message.v1` | Handles stale/expired/modified plan, missing confirmation, out-of-catalog step, validation error, forbidden/tenant/customer denial, approval-required blocker, provider/runtime/tool-boundary fail-closed state, and confirmation mismatch with no hidden-target enumeration. |

Surface-description sufficiency review: this binding is sufficient for current-intent implementation planning. It names the target prompt, surface contracts, action/tool/capability ids, input schema, confirmation requirements, result surfaces, denied states, trace obligations, and no-mutation/no-autonomous-authority constraints. Runtime/frontend tasks still must implement and verify the actual typed surfaces before claiming runtime readiness.
