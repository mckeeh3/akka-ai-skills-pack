# Surfaces: Governance/Policy

## Canonical action/tool/capability matrix

All Governance/Policy browser actions use the canonical `action-governance-policy-*` ids below. Legacy fixture aliases such as `action-govpol-*` may remain in tests only and must route to the same backend capability.

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

### `surface-governance-policy-proposal` (`governance.policy.proposal.v1`)

Pattern: `governance-diff`.

Required payload: proposal id, lifecycle state, source, risk classification, required approval, before/after summaries, change rows, affected capabilities/artifacts, simulation/evidence status, available next transitions, idempotency metadata as diagnostic-only, decision metadata, outcome notes, trace links, redaction, and `noDirectMutation=true`.

Allowed lifecycle states: `draft`, `submitted`, `simulation-required`, `in-review`, `changes-requested`, `approved`, `rejected`, `activated`, `rollback-candidate`, `rolled-back`, `superseded`. The surface must expose only backend-authorized actions for transitions valid from the current lifecycle state.

### `surface-governance-policy-simulation` (`governance.policy.simulation.v1`)

Pattern: `governance-diff`.

Simulation is advisory and cannot grant authority. Required payload includes simulation id, proposal id, scenario summary, expected allows, expected denials, warnings, confidence, evidence trace refs, activation gate summary, and `noDirectMutation=true`.

### `surface-governance-policy-decision` (`governance.policy.decision.v1`)

Pattern: `decision-card`.

Required payload: command mode (`decide`, `activate`, or `rollback`), recommendation, risk score/classification, confidence, impact, affected target, policy basis, evidence, alternatives, required approvals, allowed actions, disabled actions with safe reasons, trace links, outcome notes, redaction, and `noDirectMutation=true`.

Blocked approval, activation, or rollback may return `system-message` when prerequisites are missing, or a decision card with explicit blockers. It must not render as successful approval, activation, or rollback unless backend policy state changed. Activation and rollback are separate backend command modes behind `approve-activate-or-rollback-policy`; a decision recommendation alone is never a commit.

### `surface-governance-policy-outcome` (`governance.policy.outcome.v1`)

Pattern: `outcome-panel`.

Required payload: outcome id, proposal id, decision state, summary, metrics, recommendations, evidence refs, trace refs, redaction, actor, and `noDirectMutation=true`. Outcome notes link observations back to the policy decision and simulation evidence without changing authority.

### `surface-governance-policy-impact-analysis-task` (`governance.policy.impact_analysis.task.v1`)

Pattern: `workflow-status`.

Required payload: task id, optional autonomous-agent task id, proposal id, status, progress, blockers, evidence refs, trace ids, provider/runtime failures, authorized actions, activation-blocked flag, redaction, and `noDirectMutation=true`.

Allowed statuses include queued, running, blocked_provider_or_runtime, completed-review-required, cancelled, failed, accepted, rejected_result, and request_changes. Missing provider/runtime returns blocked_provider_or_runtime with no fake success.

### `surface-governance-policy-impact-analysis-result` (`governance.policy.impact_analysis.result.v1`)

Pattern: `decision-card`.

Required payload: impact task id, proposal id, overall risk, review state, summary, findings, evidence refs, trace ids, required human decisions, allowed result-disposition actions, disabled activation/rollback actions, activation-blocked flag, redaction, and `noDirectMutation=true`.

### `surface-governance-policy-system-message` (`governance.policy.system_message.v1`)

Pattern: `system-message`.

Used for authorization denials, missing selected context, missing proposal/task ids, validation errors, conflict/stale states, provider/runtime blockers, and unsupported authority expansion. Payload must include status, severity, safe reason code, recovery-oriented message, trace refs, redaction, `noFakeSuccess=true`, and `noDirectMutation=true`.

## State and action rules

Every consequential browser action carries a stable action id, maps to capability `governance-policy-lifecycle`, carries a correlation/idempotency key where needed, and returns a typed result, decision card, workflow status, outcome panel, markdown response, or safe system message. Side-effecting request payloads include action id, proposal/task id when applicable, command mode where applicable, reason/rationale when required, idempotency key, and correlation id; tenant/customer authority is always resolved from backend-selected `AuthContext`, not trusted browser fields.

Agents may draft, summarize, recommend, and prepare evidence. Agents and frontend state may not approve, activate, roll back, weaken security, expand authority, or fabricate provider/model-backed analysis.

## Surface-description sufficiency review

This pass makes the Governance/Policy surface set sufficiently unambiguous for scoped generation of proposal lifecycle, decision, activation/rollback, outcome, and impact-analysis task surfaces without inventing tool ids, lifecycle states, result authority, core payload categories, denial states, or trace obligations. Visual layout and component styling remain owned by the selected web UI style guide and frontend realization files.

## Required tests and traces

- Frontend contract tests prove the shell renders Governance/Policy dashboard, inventory, diff, decision, workflow-status, outcome, and system-message states.
- Backend tests prove scoped reads, proposal lifecycle idempotency, simulation advisory behavior, activation/rollback prerequisites, outcome panel return, impact-task fail-closed lifecycle, and structured denial surfaces.
- Required trace evidence: policy-decision-trace, admin-audit-event, workstream-log-trace, agent-work-trace, impact-analysis worker/task events, and denial/failure traces.
