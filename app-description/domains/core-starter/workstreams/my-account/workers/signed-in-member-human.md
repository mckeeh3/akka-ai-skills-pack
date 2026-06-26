# Signed-in member human

workerId: signed-in-member-human
workerType: human
reasoningEngine: human
scope: local-workstream
owningDomain: core-starter
owningWorkstream: my-account
runtimeReadiness: compile-ready

## Purpose

The signed-in member uses My Account to inspect personal context, handle personal attention, maintain allowed profile/preferences, triage in-app notifications, review personal digest work, and recover from unavailable protected destinations.

## Responsibility

- Owns/does:
  - Choose the selected authorized context from backend-presented choices.
  - Review profile/settings fields and submit self-service changes.
  - Triage personal in-app notification lifecycle state.
  - Start, cancel, and review personal attention digest/export work when allowed.
  - Confirm exact chat tool plans before any human-backed chat execution.
- Does not own/do:
  - Grant roles/capabilities, alter memberships/account status, administer tenants/customers, edit provider/model configuration, resolve source work through notifications, or enumerate hidden workstreams/sources.

## Behavior profile

- Instructions/prompt:
  - artifact id/path: this worker binding plus `../surfaces/surfaces.md`
  - type: human-operating-brief
  - version/governance state: current app-description
  - summary: act only within the selected backend `AuthContext`; review visible evidence, confirmations, denials, and result surfaces before submitting consequential actions.
- Skills:
  - self-service profile/settings review, notification triage, context/authority interpretation, digest review, safe denial recovery.
- Tools:
  - `read-current-account-context` via `surface_action`/`api_call`.
  - `update-own-profile-settings` / `my_account.update_profile_settings` via `surface_action` and bounded `human_chat_tool_plan` confirmation.
  - notification lifecycle/preference tools via `surface_action` and bounded `human_chat_tool_plan` confirmation.
  - digest/export tools via `surface_action` only unless a later policy explicitly grants chat-plan support.
  - open-workstream/source/trace tools via backend-authorized surface requests.
- Policies/rubrics/examples:
  - `../policies/policy-bindings.md`, `../../../../../global/policies/foundation-security-and-governance.md`.
- Evidence profile:
  - allowed: browser-safe account/context/profile/settings/attention/notification/digest/trace summaries for the selected context.
  - forbidden/redacted: raw JWT/session/provider data, provider secrets, hidden memberships/workstreams/sources, raw prompts/model payloads, cross-tenant/customer facts.
- Assistance mode:
  - workstream assistant / functional agent may explain role guidance: yes.
  - workstream assistant / functional agent may interpret human text into tool plans: yes, only for cataloged `human_chat_tool_plan` entries.
  - consequential tools require confirmation: yes for chat-plan execution and dedicated confirmation surfaces where policy requires.

## Authority and scope

- authorityLevel: execute for self-service operations; approve only for the worker's own advisory digest review disposition; no administer authority.
- AuthContext scope: signed-in account plus backend-selected tenant/customer/member/role/capability context.
- Allowed decisions: self profile/settings changes, personal in-app notification lifecycle/preferences, allowed context selection from backend-owned choices, digest start/cancel/review actions.
- Requires approval when: action policy requires confirmation/approval, chat-plan execution is requested, digest/export/provider policy marks work as gated, or a surface declares a destructive/high-impact confirmation.
- Denied/hidden behavior: hidden or unauthorized objects are omitted or return `surface-my-account-open-denied` / safe system message without enumeration.
- Retained human authority: the human remains accountable for confirmed chat plans, self-service settings submissions, notification lifecycle changes, and digest disposition.

## Supervision and handoffs

- Supervising human workers: self; role-gated admin/support workers only through separate workstreams.
- Supports: none.
- Handoffs to: source workstreams through `my_account.open_authorized_workstream` / `attention.open_attention_item` when reauthorized.
- Escalates to: configured request-access/support/admin guidance when visible; otherwise safe recovery copy.
- Fallback worker or process: My Account system worker returns no-access, blocked, stale, or denial surfaces.

## Inputs, evidence, and outputs

- Inputs/triggers: dashboard/control actions, profile/settings forms, notification lifecycle controls, digest actions, context selection, deterministic surface router, confirmed chat-plan confirmation.
- Evidence allowed: selected-context browser-safe summaries, visible attention counters/items, visible trace refs, authorized notification/digest evidence.
- Evidence forbidden: hidden tenants/customers/workstreams/sources, secrets, raw provider/model/prompt/tool payloads.
- Outputs produced: form submissions, confirmations, review decisions, context-selection requests, notification lifecycle requests, digest task/review requests.
- Result/progress/failure surfaces: `../surfaces/surfaces.md` inventory and shared chat tool-plan surfaces in `../../surface-catalog.md`.

## Harnesses and actor adapters

| Harness | Actor adapter | Exposure channel | Trace source | Notes |
|---|---|---|---|---|
| My Account structured surfaces | surface_action | browser workstream shell | surface_action | Backend authorizes every action; UI visibility is advisory. |
| Workstream assistant chat plan proposal/confirmation | human_chat_tool_plan | selected My Account assistant | human_chat_tool_plan | No mutation until exact human confirmation and per-step backend reauthorization. |
| Protected HTTP/workstream APIs | api_call | browser API client | api_call | `/api/me` and workstream APIs resolve selected context server-side. |

## Governed tools and capabilities

| Governed tool id | Capability id | Allowed adapter(s) | Authority | Approval/confirmation | Idempotency/transaction boundary |
|---|---|---|---|---|---|
| read-current-account-context | account-context-and-profile | surface_action, api_call | observe | none | read-only scoped query |
| my_account.update_profile_settings / update-own-profile-settings | account-context-and-profile | surface_action, human_chat_tool_plan, api_call | execute self-service fields | form submit or exact chat-plan confirmation | one self-account settings transaction per idempotency key |
| notification.list_my_account_center | account-context-and-profile | surface_action, api_call | observe | none | read-only scoped query |
| notification.mark_read / notification.dismiss / notification.archive / notification.snooze / notification.update_preferences | account-context-and-profile | surface_action, human_chat_tool_plan, api_call | execute personal notification state | confirmation when surfaced by chat plan or policy | one visible notification/preference transaction per idempotency key |
| request-personal-digest-export | account-context-and-profile | surface_action, api_call | execute/review personal digest lifecycle | surface-specific confirmation/approval where required | one digest task/review transaction per idempotency key |
| my_account.open_authorized_workstream / attention.open_attention_item / my_account.view_own_trace_refs | account-context-and-profile | surface_action, api_call | observe/open authorized targets | none unless target surface requires it | reauthorize every open; no target mutation |

## Policies, constraints, and fail-closed behavior

- Tenant/customer isolation: backend-selected `AuthContext` scopes every query/action.
- Redaction and sensitive data: browser receives only frontend-safe fields; hidden facts are omitted or redacted.
- Tool-boundary or role/capability constraints: human surface/chat adapters do not inherit agent permissions and vice versa.
- Provider/configuration preconditions for model-backed workers: missing configuration prevents assistant plan/digest generation; human browser self-service still uses deterministic backend paths where available.
- Idempotency/replay/stale handling: side-effecting actions use idempotency keys and stale/conflict recovery surfaces.
- Failure behavior: return typed system/progress/outcome surfaces with safe recovery and trace refs.
- Denial behavior: safe `not_found_or_redacted`/forbidden/system-message output without protected-data leakage.

## Audit and work traces

Record worker id/type, actor adapter, selected `AuthContext`, account/member identity, requestedBy/confirmedBy for chat plans, governed tool/capability ids, idempotency/correlation keys, authorization decision, redaction level, result state, and linked surface/workstream item.

## Tests and manual runtime scenarios

- Automated tests:
  - allowed path: dashboard/profile/settings/context/notification/digest actions in `../tests/coverage.md`.
  - denied/forbidden path: hidden/cross-tenant/unsupported-field/provider-secret attempts.
  - tenant isolation: selected context comes from backend, not client ids.
  - idempotency/replay/stale behavior: repeated side-effecting actions and stale context surfaces.
  - approval/confirmation behavior: exact chat-plan confirmation and surface confirmations.
  - trace/audit evidence: surface, API, and chat-plan trace refs.
- Manual runtime scenario:
  - signed-in member → surface action or confirmed chat plan → governed tool → account-context-and-profile → protected Akka/API/UI path → trace/view evidence.

## Realization links

- Surfaces: `../surfaces/surfaces.md`
- Agents: `../agents/functional-agent.md`
- Tools: `../tools/governed-tools.md`
- Capabilities: `../../../capabilities/account-context-and-profile.md`
- Policies: `../policies/policy-bindings.md`
- Traces: `../traces/work-traces.md`
- Tests: `../tests/coverage.md`
- Akka components/API/frontend source-alignment: `../realization/source-alignment.md`
