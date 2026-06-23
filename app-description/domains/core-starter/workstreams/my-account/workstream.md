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

This workstream exposes a bounded `human_chat_tool_plan` adapter for execution-oriented chat prompts after deterministic no-mutation surface routing declines the prompt. The adapter is current-intent only until runtime tasks implement it. It allows `my-account-agent` to propose a plan for the representative prompt **change my theme to Obsidian Dark**, but it never permits prompt-only mutation, hidden target enumeration, or AI-autonomous authority.

Execution is allowed only when all of the following hold: the proposal was created with `noMutation=true`; the human explicitly confirms the exact plan snapshot; the backend reauthorizes the selected `AuthContext`, actor, capability, tool boundary, lifecycle state, approval policy, tenant/customer ownership, and idempotency on every step; and each step executes through its declared governed surface/action path as a separate transaction boundary.

Representative catalog binding: actions `action-update-my-settings`; governed tool ids `my_account.update_profile_settings`; capabilities `my_account.update_profile_settings`; input contract `schema.my-account.settings.update.v1` with `preferredThemeId=obsidian-dark` selected from backend-valid theme options; expected result surfaces `surface-my-settings`. The allowed effect is to update only the signed-in user's own profile/settings preferences; it cannot change tenant branding, roles, memberships, external delivery, or provider/model configuration.
