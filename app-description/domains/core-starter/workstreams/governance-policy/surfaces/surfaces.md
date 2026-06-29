# Surfaces: Governance/Policy

## Canonical action/tool/capability matrix

All Governance/Policy browser actions use canonical `action-governance-policy-*` ids and map to capability `governance-policy-lifecycle`.

| Surface action | Governed tool | Actor adapter(s) | Result surface | Notes |
|---|---|---|---|---|
| `action-governance-policy-dashboard` | `governance.policy.search` | `surface_action`, `api_call` | `surface-governance-policy-dashboard` | Overview of pending decisions, simulation findings, exceptions, rollback items, recent changes, and safe shortcuts. |
| `action-governance-policy-catalog` | `governance.policy.search` | `surface_action`, `api_call`, bounded `agent_tool_call` read | `surface-governance-policy-catalog` | Search/list policy definitions and versions. |
| `action-governance-policy-read` | `governance.policy.read` | `surface_action`, `api_call`, bounded `agent_tool_call` read, `internal_call` runtime check | `surface-governance-policy-detail` | Shows active/draft versions, effective decision, exceptions, rollback targets, and trace links. |
| `action-governance-policy-draft` | `governance.policy.draft` | `surface_action`, `api_call`, bounded `agent_tool_call` assist, confirmed `human_chat_tool_plan` | `surface-governance-policy-draft` | Create/update a draft proposal; no active policy mutation. |
| `action-governance-policy-simulate` | `governance.policy.simulate` | `surface_action`, `api_call`, bounded `agent_tool_call` assist, confirmed `human_chat_tool_plan`, `workflow_step`, `internal_call` | `surface-governance-policy-simulation` or `surface-governance-policy-partial-failure` | Simulation/replay evidence only; no activation. |
| `action-governance-policy-submit-approval` | `governance.policy.submit_for_approval` | `surface_action`, `api_call`, confirmed `human_chat_tool_plan`, `workflow_step` | `surface-governance-policy-decision-card` | Creates review item/attention item. |
| `action-governance-policy-approve` | `governance.policy.approve` | `surface_action`, `api_call`, `workflow_step` | `surface-governance-policy-decision-card` or `surface-governance-policy-result` | Approve/reject/request evidence/modify/defer/escalate; activation remains separate. |
| `action-governance-policy-activate` | `governance.policy.activate` | `surface_action`, `api_call`, confirmed `human_chat_tool_plan`, `workflow_step`, `internal_call` | `surface-governance-policy-result` or `surface-governance-policy-partial-failure` | Requires approved decision card and confirmation; commits active version. |
| `action-governance-policy-rollback` | `governance.policy.rollback` | `surface_action`, `api_call`, confirmed `human_chat_tool_plan`, `workflow_step`, `internal_call` | `surface-governance-policy-result` or `surface-governance-policy-partial-failure` | Requires rollback decision card and confirmation; restores prior approved version or revokes exception. |
| `action-governance-policy-review-exception` | `governance.policy.review_exception` | `surface_action`, `api_call`, confirmed `human_chat_tool_plan`, `workflow_step` | `surface-governance-policy-exception` or `surface-governance-policy-result` | Grants/denies/revokes/expires policy exceptions when allowed. |
| `action-governance-policy-history` | `governance.policy.read_history` | `surface_action`, `api_call`, bounded `agent_tool_call` read | `surface-governance-policy-history` | Change, decision, simulation, exception, rollback, and runtime outcome history. |

## Structured-surface standard block

Applies to every surface below unless a surface states a narrower rule.

- ownerFunctionalAgentId: `governance-policy-agent`.
- reusableByFunctionalAgentIds: none by default; cross-workstream links open this workstream's surfaces through backend-authorized deep links.
- lifecycle status: compile-ready description; runtime-ready is not claimed.
- placement: Governance/Policy workstream shell route/panel; detail/draft/simulation/decision/exception/history surfaces may open inline, side-panel, modal, or deep-link according to frontend realization.
- data source: governed tools in `../tools/governed-tools.md`; browser payloads are frontend-safe DTOs, not raw entity state.
- actor adapters: human surface actions use `surface_action`; protected endpoints use `api_call`; confirmed command plans use separate `human_chat_tool_plan`; agent read/draft/simulation tools use `agent_tool_call` only when explicitly granted; lifecycle workflows use `workflow_step`; runtime checks use `internal_call`.
- action shape: every action includes `browserToolId` equal to its surface action id, `governedToolId`, `capabilityId: governance-policy-lifecycle`, input schema ref, idempotency requirements, result surface, and audit event type.
- decision-card shape: subject, goal/plan/task link, recommended action, decision authority, evidence considered, policy clauses/guardrails, simulation findings, confidence, risk/impact, alternatives, known gaps, available reviewer actions, action-to-governed-tool/capability mapping, deadline/SLA when present, trace links, and outcome follow-up.
- events/reconnect: stale or reconnect events mark affected surfaces stale and require refresh or server replay; malformed/duplicate/cross-context events are safe no-ops with diagnostics.
- style/catalog binding: use `../../../../../global/surfaces/ui-style-and-runtime-contracts.md` and the standard workstream shell/component catalog; avoid exposing backend ids as primary user copy.
- system feedback: denial, validation, stale, conflict, partial-failure, provider/configuration, and approval-required failures render typed `system_message` or result surfaces.

## Surface graph

`dashboard -> catalog -> detail -> draft -> simulation -> decision-card -> activation-result -> history` is the primary lifecycle path. `detail -> exception` and `detail/history -> rollback decision -> rollback result -> history` are guarded side paths. All paths can terminate in `system_message` or `partial-failure` surfaces without mutating active policy when authorization, approval, simulation, freshness, idempotency, or runtime-publication checks fail.

## Surface contracts

### `surface-governance-policy-dashboard` (`governance.policy.dashboard.v1`)

Pattern: `workstream-dashboard`.

Purpose: give authorized SaaS owners, tenant admins, policy operators, auditors, and support users a role-specific entry point into policy catalog, pending approvals, simulation findings, exception review, rollback decisions, runtime enforcement evidence, and recent changes.

Required payload schema:

- `summary`: selected context label, actor authority summary, visible active policies, pending drafts, pending decisions, open simulation findings, active/expiring exceptions, rollback candidates, recent changes, and safe empty-state copy.
- `attentionItems`: actionable approval, exception, simulation finding, rollback, denial, and partial-failure items with target surface/action and safe evidence summary.
- `cards`: actionable cards for policy catalog, pending approvals, simulation findings, exceptions, rollback candidates, policy history, and runtime decision evidence.
- `authorizedActions`: backend-authorized actions for catalog, read, draft, simulate, submit approval, approve, activate, rollback, review exception, and history.
- `redaction`: field-level indicators for hidden cross-tenant/customer/account details, raw secrets, raw provider/model data, raw prompts, raw tool payloads, JWTs, raw correlation ids, and idempotency keys.

Required states: loading, empty, ready, forbidden/`system_message`, stale/reconnect, partial-data, and failure.

### `surface-governance-policy-catalog` (`governance.policy.catalog.v1`)

Pattern: collection list/search.

Purpose: show all visible policy definitions and versions with simple search/filter.

Required payload schema:

- `catalogSummary`: selected scope label, total visible count, filtered count, active count, draft count, approval count, exception count, freshness state, and safe empty-state copy.
- `filters`: backend-allowed filters for policy name/search text, category, lifecycle state, workstream, agent, tool/action, role, scope, exception state, and approval state.
- `rows`: visible policy rows with display name, category, active version, draft/approval state, simulation status, exception marker, affected workstream/agent/tool/action/role/customer/account summaries, last changed summary, required reason marker, target detail/draft/simulation/decision/history surfaces, and backend-authorized row action ids.
- `authorizedActions`: only actions backend authorizes for the selected actor/context.
- `redaction`: field-level indicators for hidden scopes and protected data.

Required states: loading, empty, ready, filter-validation-error, forbidden/`system_message`, stale/reconnect, partial-data, and failure.

### `surface-governance-policy-detail` (`governance.policy.detail.v1`)

Pattern: collection-object show/inspection surface for a policy setting or policy document.

Purpose: explain one policy's active version, drafts, effective runtime decision, exceptions, scope precedence, decision evidence, and rollback targets.

Required payload schema:

- `detailSummary`: policy id/name, category, selected scope, active version, effective decision summary, exception state, approval state, rollback availability, and freshness state.
- `versionBreakdown`: active version, pending drafts, prior approved versions, superseded/rolled-back markers, winning scope, and last changed summary.
- `decisionExplanation`: browser-safe explanation of how this policy applies at runtime for selected context.
- `authorizedActions`: draft, simulate, submit approval, approve/reject/request evidence, activate, open exception, rollback, open history, or return to catalog only when backend-authorized.
- `traceLinks`: user-readable policy-decision/admin-audit/workstream trace summaries.
- `redaction`: field-level indicators for hidden scope details and protected data.

Required states: loading, ready, read-only, forbidden/`system_message`, not-found-or-redacted, conflict/stale, partial-data, approval-required, and failure.

### `surface-governance-policy-draft` (`governance.policy.draft.v1`)

Pattern: collection-object create/edit surface for a policy proposal.

Purpose: allow authorized actors to create or update a versioned proposal without changing active policy.

Required payload schema:

- `draftSummary`: policy id/name, category, target scope, base version, action mode, rationale, expected post-change behavior, affected workstreams/tools/roles, risk/impact/confidence notes, and approval requirements.
- `fields`: clause/value inputs, required reason, evidence refs, simulation selector, freshness token, and idempotency key.
- `validation`: field-level errors for unsupported category/value/scope, missing reason, stale version, hidden target, hard-platform-security denial, or missing capability.
- `result`: draft id, next action, simulation requirement, history reference, and trace reference after successful draft.
- `redaction`: indicators for hidden scopes and protected data.

Required states: loading, ready, editing, submitting, validation-error, forbidden/`system_message`, conflict/stale, success, and failure.

### `surface-governance-policy-simulation` (`governance.policy.simulation.v1`)

Pattern: evidence/replay result surface.

Purpose: show expected policy outcomes before activation, including findings, changed decisions, risk, impact, confidence, and evidence gaps.

Required payload schema:

- `simulationSummary`: draft/rollback/exception candidate, selected evidence scope, status, changed allow/deny/governed outcomes, risk/impact/confidence, and freshness.
- `findings`: policy clauses/values evaluated, affected workstreams/tools/roles, expected decisions, exception interactions, redacted example traces, and reviewer notes.
- `partialFailures`: missing/unavailable evidence, hidden evidence, replay gaps, or downstream projection gaps with safe recovery actions.
- `authorizedActions`: request approval, revise draft, request evidence, open decision card, or return to detail.
- `traceLinks`: simulation and source evidence trace summaries.

Required states: loading, queued, running, ready, partial-failure, forbidden/`system_message`, stale/reconnect, and failure.

### `surface-governance-policy-decision-card` (`governance.policy.decision-card.v1`)

Pattern: approval/exception/deviation decision card.

Purpose: support human review of policy activation, rejection, evidence request, exception, and rollback decisions.

Required payload schema:

- `decisionSubject`: draft/version/exception/rollback target, goal/plan/task link, requester, reviewer eligibility, deadline/SLA, and current workflow state.
- `recommendation`: recommended action, alternatives, uncertainty, risk, confidence, impact, and known gaps.
- `evidence`: simulation refs, policy clauses/guardrails triggered, affected workstreams/tools/roles, trace links, prior decisions, exception status, and redacted runtime outcomes.
- `availableActions`: approve, reject, request evidence, modify/counterpropose, defer, escalate, mark exception-required, activate approved version, or start rollback where backend-authorized.
- `decisionRecord`: reviewer, rationale, selected `AuthContext`, idempotency key, result state, and trace ref.

Required states: loading, ready, needs-evidence, approval-required, submitting, success, forbidden/`system_message`, stale/conflict, partial-data, and failure.

### `surface-governance-policy-exception` (`governance.policy.exception.v1`)

Pattern: exception review / lifecycle surface.

Purpose: grant, deny, revoke, expire, or request evidence for policy exceptions where policy permits exceptions.

Required payload schema:

- `exceptionSummary`: request id, policy/version/scope, requested deviation, owner, expiry, reason, reviewer, status, and runtime effect.
- `evidence`: decision-card refs, simulation refs, affected action/tool/workstream, prior exceptions, runtime trace summaries, and redaction markers.
- `fields`: expiry, allowed deviation, rationale, evidence refs, idempotency key, and freshness token.
- `authorizedActions`: grant, deny, revoke, expire, request evidence, open history, or return to decision card.
- `result`: exception state, runtime enforcement effect, trace ref, and next review/expiry action.

Required states: loading, ready, submitting, validation-error, expired, revoked, forbidden/`system_message`, conflict/stale, success, and failure.

### `surface-governance-policy-history` (`governance.policy.history.v1`)

Pattern: audit-timeline / lifecycle history surface.

Purpose: show direct policy changes, decisions, simulations, exceptions, rollback records, and practical runtime outcome links for authorized actors.

Required payload schema:

- `historySummary`: selected policy/scope filters, visible change count, decision count, simulation count, exception count, rollback count, runtime outcome count when available, and freshness state.
- `events`: chronological events with actor/reviewer display summary, policy id/name, action, scope summary, old/new/active version, reason/rationale, simulation/decision/exception/rollback refs, timestamp, redacted trace marker, and trace link.
- `runtimeOutcomes`: optional aggregated or linked policy-decision outcomes influenced by this policy without exposing hidden protected details.
- `filters`: policy name/search, category, workstream, agent, tool/action, role, actor, reviewer, changed window, decision state, exception state, rollback state.
- `redaction`: field-level indicators for hidden scope details and protected data.

Required states: loading, empty, ready, filter-validation-error, forbidden/`system_message`, stale/reconnect, partial-data, and failure.

### `surface-governance-policy-result` (`governance.policy.result.v1`)

Pattern: result/outcome surface.

Purpose: report successful draft, approval request, decision, activation, rollback, exception, or history-affecting action with trace links and next actions.

Required payload schema: result type, policy/version/scope, committed state, next actions, history ref, trace ref, actor/reviewer summary, visible changed outcomes, and redaction markers.

Required states: success, no-op/idempotent-replay, approval-required, forbidden/`system_message`, stale/conflict, and failure.

### `surface-governance-policy-partial-failure` (`governance.policy.partial-failure.v1`)

Pattern: partial-failure/result surface.

Purpose: distinguish simulation evidence gaps, activation publication gaps, rollback projection gaps, or exception-publication gaps from successful committed outcomes.

Required payload schema: attempted action, committed/not-committed/partial-publication state, failed scopes or redacted summary, safe recovery actions, idempotency status, trace refs, and support/audit drilldown availability.

Required states: ready, retry-available, support-needed, forbidden/`system_message`, and failure.

## Authorization and tenant scope

Backend resolves SaaS/defaults context, tenant/customer/account authority, reviewer authority, separation-of-duty state, and row/action visibility from selected `AuthContext`. Browser-provided tenant/customer/account ids, policy ids, filters, decision ids, and scopes are hints only. Direct/deep-link denials return safe `system_message` surfaces with no hidden target enumeration.

## Trace, audit, and work evidence

Every read/open, filter request, draft, simulation, approval request, decision, activation, rollback, exception grant/deny/revoke/expire, denial, stale/conflict result, partial failure, and runtime policy decision emits workstream-log/admin-audit/policy-decision evidence as appropriate. Default browser trace copy is human-readable; raw ids and protected details are role-gated.

## Accessibility, responsive, and UI realization

Use the selected web UI style guide and component catalog for dashboards, list/search, detail inspection, draft/edit forms, simulation evidence panels, decision cards, exception review, result/partial-failure cards, and timeline history. Controls are keyboard-operable, have accessible names, preserve focus after refresh/action results, and do not rely on color alone for lifecycle/approval/exception states.

## Required tests

- App-description/contract tests prove payload schemas, actions, auth/scope rules, states, trace links, redaction, policy lifecycle sufficiency, and result/partial-failure/system-message surfaces.
- Frontend tests prove catalog filters, detail, draft validation, simulation rendering, decision-card actions, exception review, rollback/result surfaces, history rendering, forbidden/`system_message` states, secret-boundary redaction, keyboard behavior, and responsive rendering.
- Backend/API tests prove selected AuthContext scoping, draft, simulation, approval decision, activation transaction, rollback transaction, exception lifecycle, idempotency, hard-platform-security denials, tenant isolation, and trace/history evidence.

Surface-description sufficiency review: these definitions are sufficiently unambiguous for a focused first implementation slice of Governance/Policy catalog/detail/draft/simulate/decision/activate flow with later rollback and exception slices, without inventing new policy categories or bypassing human governance.
