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

Confirmed chat tool plan note: explain that deterministic surface routing opens or prefills My Account surfaces first with no mutation. The representative `human_chat_tool_plan` path may only propose `action-update-my-settings` with governed tool/capability `my_account.update_profile_settings`, schema `schema.my-account.settings.update.v1`, and a backend-valid `preferredThemeId` such as `obsidian-dark`; execution requires exact snapshot confirmation, selected AuthContext authorization, idempotency, and trace capture.
