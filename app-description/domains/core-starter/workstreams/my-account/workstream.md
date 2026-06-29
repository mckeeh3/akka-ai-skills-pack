# Workstream: My Account

## Purpose

Give the signed-in human a safe AI-first personal control point for selected authority context, personal attention, profile, named-theme/settings preferences, in-app notifications, governed digest/export requests, and safe recovery from unavailable or denied workstream/source openings.

## Worker roster

My Account binds explicit workers under `workers/` so implementation tasks preserve the current skills-pack chain:

```text
worker → execution harness → actor adapter → governed tool → capability → Akka/API/frontend realization
```

- `workers/signed-in-member-human.md` — authenticated human worker using structured surfaces and exact-confirmed `human_chat_tool_plan` where cataloged.
- `workers/my-account-functional-agent-worker.md` — the user-facing workstream assistant / functional-agent worker behind `my-account-agent`; it explains, summarizes, and proposes within the selected context but does not execute side effects autonomously.
- `workers/my-account-system-worker.md` — deterministic backend/API/projection/workflow participants that resolve context, assemble surfaces, reauthorize openings, emit traces, and enforce fail-closed behavior.

## Functional agent

Owns `my-account-agent` as its exactly-one user-facing functional-agent binding and product-facing workstream assistant. Runtime instances are selected-context workstream logs, not page sessions, and the agent's authority is defined by the functional-agent worker binding plus explicit governed-tool adapters.

## Capability binding

Primary capability: `../../capabilities/account-context-and-profile.md`.

## Attention model

Backend-owned attention includes personal action items, notification acknowledgements, context problems, digest/export status, unavailable-source recovery, and provider/configuration denials visible to the current user. Counts feed the signed-in user rail tile and My Account personal command-center aggregation.

Non-attention member self-service status includes profile completeness, selected context, membership/Organization scope, personal preference save state, no-access recovery, and disabled/inactive membership guidance. These states belong in My Account dashboard/profile/context/settings surfaces even when they do not create attention items.

## Current-intent graph contract (TASK-ADR-02-001)

My Account is the signed-in member functional-agent workstream for account/profile context. The graph is authoritative in this order:

```text
signed-in-member-human or my-account-agent → structured My Account surface or governed assistant turn → actor adapter (`surface_action`, `api_call`, bounded `human_chat_tool_plan`, or described read/advisory `agent_tool_call`) → shared governed account/profile/context tool → capability `account-context-and-profile` plus selected membership/Organization context → Akka/API/frontend realization mapping → test/runtime-validation expectation → durable audit/work trace
```

Required surface graph nodes are `surface-my-account-dashboard`, `surface-my-profile`, `surface-my-settings`, `surface-my-context`, and result/system-message surfaces such as `surface-my-account-open-denied` and shared chat-plan result/system-message surfaces. Account/context reads require no confirmation, profile/settings updates require a form submission or exact chat-plan confirmation when chat execution is used, and context opening/selection is backend-reauthorized without granting new authority. The functional agent may explain, summarize, route, and propose catalog-bound plans; it cannot autonomously mutate account/profile/context state or broaden membership, role, tenant, or Organization scope.

## Readiness posture

This node captures current intent only and is description-ready for focused build/alignment tasks, not runtime-ready. Runtime readiness still requires local Akka/API/UI validation and model/provider fail-closed proof where applicable. Because TASK-ADR-02-001 refreshed the current-intent graph after the last automated alignment evidence, mapped implementation should be treated as `stale-description-changed` until a source-alignment review or runtime-validation pass updates evidence.


## Confirmed human chat tool-plan exposure

This workstream exposes a bounded `human_chat_tool_plan` adapter for execution-oriented chat prompts after deterministic no-mutation surface routing declines the prompt. The My Account runtime path is implemented through backend-owned plan proposal, exact snapshot confirmation, catalog validation, dispatcher reauthorization, idempotency, and trace surfaces. It allows `my-account-agent` to propose plans for self-service profile/settings updates and personal in-app notification lifecycle/preference updates, but it never permits prompt-only mutation, hidden target enumeration, external-channel/provider controls, cross-account edits, or AI-autonomous authority.

Execution is allowed only when all of the following hold: the proposal was created with `noMutation=true`; the human explicitly confirms the exact plan snapshot; the backend reauthorizes the selected `AuthContext`, actor, capability, tool boundary, lifecycle state, approval policy, tenant/customer ownership, and idempotency on every step; and each step executes through its declared governed surface/action path as a separate transaction boundary.

Expanded catalog binding: actions `action-update-my-profile`, `action-update-my-settings`, `action-notification-mark-read`, `action-notification-dismiss`, `action-notification-archive`, `action-notification-snooze`, and `action-notification-update-preferences`; governed tool ids `my_account.update_profile_settings`, `notification.mark_read`, `notification.dismiss`, `notification.archive`, `notification.snooze`, and `notification.update_preferences`; capabilities of the same ids; input contracts `schema.my-account.profile.update.v1`, `schema.my-account.settings.update.v1`, and `schema.notification.*`. Representative prompts include **change my display name to Chat Catalog Admin**, **change my theme to Obsidian Dark**, **mark notification notification-123 read**, and **disable my notification preferences for security alerts**. The allowed effects are limited to the signed-in user's own profile/settings and in-app notification state; source work, tenant branding, roles, memberships, account status, external delivery, and provider/model configuration cannot change through this adapter.
