# My Account Starter Guidance

Use this skill to answer My Account questions in the five core workstream starter and SMB full-core baseline.

- Start from the selected AuthContext, authority basis, visible browser-safe capability list, personal attention, own trace refs, and safe navigation evidence returned by `myAccountEvidence.read`.
- Treat My Account as a role-specific dashboard plus human surface graph: explain which attention item, surface edge, governed-tool, browser-tool, agent-tool, or internal-tool is available for the selected user context, and deny unsupported edges safely.
- Be familiar with key surfaces: My Account dashboard for attention and navigation, My Profile and My Settings for editable self-service fields, Context and Authority for selected AuthContext explanation, Notification Center for personal notification triage, digest progress/result surfaces for personal attention work, and denied/open recovery system messages.
- Explain that profile/settings changes are committed only by deterministic `my_account.update_profile_settings`; the agent may guide, explain no-op/validation outcomes, and route to authorized surfaces, but has no direct mutation authority.
- Treat provider/runtime gaps as `blocked_provider_or_runtime` system-message guidance with trace refs; do not provide deterministic/model-less successful normal guidance when the model/provider path is unavailable.
- Direct administrative changes to User Admin, Agent Admin, Audit/Trace, or Governance/Policy as appropriate.
- Include trace or correlation ids when available.
- Do not expose raw JWTs, provider credentials, invitation tokens, hiddenPromptText, providerSecret, cross-tenant data, hidden workstreams, or hidden capabilities.

Confirmed chat tool plan note: explain that deterministic surface routing opens or prefills My Account surfaces first with no mutation. The `human_chat_tool_plan` catalog now covers multiple bounded paths, all requiring exact snapshot confirmation, selected AuthContext authorization (self-scope only), idempotency, and trace capture before any side effect occurs:
- `action-update-my-settings` / `my_account.update_profile_settings` / `schema.my-account.settings.update.v1` — change own theme, timezone, or display preferences;
- `action-update-my-profile` / `my_account.update_profile_settings` / `schema.my-account.profile.update.v1` — update own display name or profile fields;
- `action-notification-mark-read`, `action-notification-dismiss`, `action-notification-archive`, `action-notification-snooze` / notification governed tools / `notification.manage_own_state` — lifecycle changes for own visible notifications only, no source task mutation;
- `action-notification-update-preferences` / `notification.update_preferences` / `notification.update_own_preferences` — update own in-app notification category preferences.
All expanded catalog actions are self-scoped; role, membership, cross-account, or cross-tenant mutation is not permitted through any of these paths. Blocked or unsupported fields must be denied with a safe system message.
