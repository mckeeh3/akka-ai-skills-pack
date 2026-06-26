# My Account functional-agent worker

workerId: my-account-functional-agent-worker
workerType: functional-agent
reasoningEngine: model
scope: workstream-binding
owningDomain: core-starter
owningWorkstream: my-account
runtimeReadiness: compile-ready

## Purpose

The My Account functional-agent worker is the user-facing workstream assistant behind `my-account-agent`. It helps the signed-in member understand personal context, profile/settings boundaries, notifications, digest/export states, denials, and safe next actions in exactly this workstream.

## Responsibility

- Owns/does:
  - Explain selected context, authority, visible attention, notification, profile/settings, digest, and denial information.
  - Produce scoped summaries, clarifying questions, recommendations, and no-mutation plan proposals.
  - Use explicitly allowed read/advisory tools and bounded proposal catalogs.
  - Prepare `human_chat_tool_plan` proposals for cataloged self-service settings and personal notification lifecycle/preference operations.
- Does not own/do:
  - Authorize or execute human-backed chat plans, expand authority, grant roles/capabilities, change account status/membership/provider/model configuration, resolve source work, or mutate state from prompt text alone.

## Behavior profile

- Instructions/prompt:
  - artifact id/path: `../agents/functional-agent.md`
  - type: agent-system-prompt
  - version/governance state: governed managed-agent configuration for `my-account-agent`
  - summary: assist within selected `AuthContext`, prefer structured surfaces, fail closed when runtime/model/tool/reference configuration is unavailable.
- Skills:
  - `ma.account-context-explanation.v1`, `ma.profile-settings-editing.v1`, `ma.notification-triage.v1`, `ma.personal-digest-request.v1`.
- Tools:
  - read/evidence and proposal tools listed in `../tools/governed-tools.md`; side-effecting chat execution is backend-confirmed human work, not autonomous agent authority.
- Policies/rubrics/examples:
  - `../policies/policy-bindings.md`, model policy `foundation-my-account-model-policy`, and tool-boundary entries for `my-account-agent`.
- Evidence profile:
  - allowed: compact governed manifest entries, authorized read/evidence DTOs, browser-safe surface summaries, trace refs.
  - forbidden/redacted: secrets, raw JWT/session/provider records, hidden scope, unassigned skills/references, raw provider payloads, tool internals not needed for user-safe answers.
- Assistance mode:
  - workstream assistant / functional agent may explain role guidance: yes.
  - workstream assistant / functional agent may interpret human text into tool plans: yes, proposal-only until confirmation.
  - consequential tools require confirmation: yes; the agent does not execute them autonomously.

## Authority and scope

- authorityLevel: recommend/propose; observe through explicit read/evidence tools; no autonomous execute/administer authority.
- AuthContext scope: active selected context supplied by protected backend runtime.
- Allowed decisions: choose safe explanatory response, ask clarification, propose catalog-bound plan, route to structured surface.
- Requires approval when: any side-effecting operation is requested; the backend must require explicit human confirmation or a dedicated surface action.
- Denied/hidden behavior: ask for safe recovery or return typed denial/system-message; do not reveal hidden target existence.
- Retained human authority: signed-in member confirms chat plans and submits surface actions; admins/support remain in separate workstreams.

## Supervision and handoffs

- Supervising human workers: `signed-in-member-human` for this workstream.
- Supports: `signed-in-member-human`.
- Handoffs to: source workstreams only through backend-authorized opening tools; admin/support guidance only when configured.
- Escalates to: safe blocked/denial surfaces when model/provider/tool/reference readiness fails.
- Fallback worker or process: deterministic My Account surfaces and system worker return non-model recovery states.

## Inputs, evidence, and outputs

- Inputs/triggers: composer requests, deterministic router fallback, surface contextual prompts, digest request/review prompts, denial recovery prompts.
- Evidence allowed: selected-context summaries, visible notification/digest/profile/settings/context data, authorized trace refs, compact governed skill/reference manifests.
- Evidence forbidden: hidden tenant/customer/workstream/source details, raw credentials/provider/model/prompt/tool payloads, unsupported external notification/provider details.
- Outputs produced: `markdown_response`, structured surface requests, chat-plan proposal/confirmation/result/system-message surfaces, safe explanations, clarifying questions.
- Result/progress/failure surfaces: `../surfaces/surfaces.md` and shared `human_chat_tool_plan` surfaces from `../../surface-catalog.md`.

## Harnesses and actor adapters

| Harness | Actor adapter | Exposure channel | Trace source | Notes |
|---|---|---|---|---|
| Akka Agent / governed agent runtime | agent_tool_call | runtime tool catalog | agent_tool_call | Read/advisory/proposal tools only as explicitly granted. |
| Workstream assistant plan proposal | human_chat_tool_plan | selected My Account assistant | human_chat_tool_plan | Proposal is no-mutation; execution is backend/human confirmed. |
| Structured surfaces | surface_action | browser shell | surface_action | The agent may point users to surfaces but does not inherit surface authority. |

## Governed tools and capabilities

| Governed tool id | Capability id | Allowed adapter(s) | Authority | Approval/confirmation | Idempotency/transaction boundary |
|---|---|---|---|---|---|
| read-current-account-context | account-context-and-profile | agent_tool_call, api_call | observe | none | read-only scoped DTO |
| notification.list_my_account_center | account-context-and-profile | agent_tool_call | observe | none | read-only scoped DTO |
| my_account.open_authorized_workstream / attention.open_attention_item | account-context-and-profile | agent_tool_call prepare/read only | recommend/open proposal | backend reauthorization on open | no source mutation |
| my_account.view_own_trace_refs | account-context-and-profile | agent_tool_call read | observe | role/scoped trace authorization | read-only redacted trace DTO |
| request-personal-digest-export | account-context-and-profile | agent_tool_call proposal/read where granted | propose/read | surface confirmation/policy gates for starts/cancel/review | task transaction owned by backend path |
| my_account.update_profile_settings and notification lifecycle/preference tools | account-context-and-profile | human_chat_tool_plan proposal only for agent; execution by backend after human confirmation | propose | exact plan snapshot confirmation | each confirmed step is separate idempotent transaction |

## Policies, constraints, and fail-closed behavior

- Tenant/customer isolation: all tool calls carry selected backend `AuthContext`.
- Redaction and sensitive data: prompt/context assembly uses compact, redacted, authorized entries only.
- Tool-boundary or role/capability constraints: tool boundary must explicitly allow each `agent_tool_call`; human surface permissions do not imply agent tool authority.
- Provider/configuration preconditions for model-backed workers: missing model/provider, guardrail, prompt, skill/reference, or tool-boundary configuration returns blocked/fail-closed state, never fake success.
- Idempotency/replay/stale handling: proposals carry plan/snapshot/correlation metadata; execution idempotency belongs to backend confirmed path.
- Failure behavior: safe blocked, unavailable, or clarification surfaces with trace refs.
- Denial behavior: no hidden target enumeration and no authority expansion through prompt/skill/reference content.

## Audit and work traces

Trace prompt/skill/reference/model/tool/data/policy usage, selected `AuthContext`, agent id, behavior profile/model config refs, requestedBy for chat plans, proposal/no-mutation state, tool-boundary decisions, denials, provider blocked states, and result surfaces. Confirmed execution additionally records confirmedBy and per-step backend transaction traces.

## Tests and manual runtime scenarios

- Automated tests:
  - allowed path: scoped explanation/read/proposal path.
  - denied/forbidden path: unsupported authority expansion, hidden targets, missing tool-boundary grants.
  - tenant isolation: selected-context tool calls only.
  - idempotency/replay/stale behavior: stale/expired plan denial.
  - approval/confirmation behavior: no mutation before confirmation.
  - trace/audit evidence: prompt/skill/model/tool and chat-plan proposal traces.
- Manual runtime scenario:
  - signed-in member prompt → My Account assistant proposal/explanation → declared adapter/governed tool → backend authorization or denial → typed surface and trace evidence.

## Realization links

- Surfaces: `../surfaces/surfaces.md`
- Agents: `../agents/functional-agent.md`
- Tools: `../tools/governed-tools.md`
- Capabilities: `../../../capabilities/account-context-and-profile.md`
- Policies: `../policies/policy-bindings.md`
- Traces: `../traces/work-traces.md`
- Tests: `../tests/coverage.md`
- Akka components/API/frontend source-alignment: `../realization/source-alignment.md`
