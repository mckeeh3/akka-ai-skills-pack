# Workstream: My Account

## Purpose

Give the signed-in human a safe AI-first personal control point for selected authority context, personal attention, profile, named-theme/settings preferences, in-app notifications, governed digest/export requests, and safe recovery from unavailable or denied workstream/source openings.

## Functional agent

Owns `my-account-agent` as its exactly-one user-facing functional-agent binding. Runtime instances are selected-context workstream logs, not page sessions.

## Capability binding

Primary capability: `../../capabilities/account-context-and-profile.md`.

## Attention model

Backend-owned attention includes personal action items, notification acknowledgements, context problems, digest/export status, unavailable-source recovery, and provider/configuration denials visible to the current user. Counts feed the signed-in user rail tile and My Account personal command-center aggregation.

## Readiness posture

This node captures current intent only. Runtime readiness still requires local Akka/API/UI validation and model/provider fail-closed proof where applicable.


## Confirmed human chat tool-plan exposure

This workstream exposes a bounded `human_chat_tool_plan` adapter for execution-oriented chat prompts after deterministic no-mutation surface routing declines the prompt. The My Account runtime path is implemented through backend-owned plan proposal, exact snapshot confirmation, catalog validation, dispatcher reauthorization, idempotency, and trace surfaces. It allows `my-account-agent` to propose plans for self-service profile/settings updates and personal in-app notification lifecycle/preference updates, but it never permits prompt-only mutation, hidden target enumeration, external-channel/provider controls, cross-account edits, or AI-autonomous authority.

Execution is allowed only when all of the following hold: the proposal was created with `noMutation=true`; the human explicitly confirms the exact plan snapshot; the backend reauthorizes the selected `AuthContext`, actor, capability, tool boundary, lifecycle state, approval policy, tenant/customer ownership, and idempotency on every step; and each step executes through its declared governed surface/action path as a separate transaction boundary.

Expanded catalog binding: actions `action-update-my-profile`, `action-update-my-settings`, `action-notification-mark-read`, `action-notification-dismiss`, `action-notification-archive`, `action-notification-snooze`, and `action-notification-update-preferences`; governed tool ids `my_account.update_profile_settings`, `notification.mark_read`, `notification.dismiss`, `notification.archive`, `notification.snooze`, and `notification.update_preferences`; capabilities of the same ids; input contracts `schema.my-account.profile.update.v1`, `schema.my-account.settings.update.v1`, and `schema.notification.*`. Representative prompts include **change my display name to Chat Catalog Admin**, **change my theme to Obsidian Dark**, **mark notification notification-123 read**, and **disable my notification preferences for security alerts**. The allowed effects are limited to the signed-in user's own profile/settings and in-app notification state; source work, tenant branding, roles, memberships, account status, external delivery, and provider/model configuration cannot change through this adapter.
