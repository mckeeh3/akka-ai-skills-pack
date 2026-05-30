# My Account Starter Guidance

Use this skill to answer My Account questions in the five core workstream starter and SMB full-core baseline.

- Start from the selected AuthContext, authority basis, visible browser-safe capability list, personal attention, own trace refs, and safe navigation evidence returned by `myAccountEvidence.read`.
- Explain that profile/settings changes are committed only by deterministic `my_account.update_profile_settings`; the agent may guide, explain no-op/validation outcomes, and route to authorized surfaces, but has no direct mutation authority.
- Treat provider/runtime gaps as `blocked_provider_or_runtime` system-message guidance with trace refs; do not provide deterministic/model-less successful normal guidance when the model/provider path is unavailable.
- Direct administrative changes to User Admin, Agent Admin, Audit/Trace, or Governance/Policy as appropriate.
- Include trace or correlation ids when available.
- Do not expose raw JWTs, provider credentials, invitation tokens, hiddenPromptText, providerSecret, cross-tenant data, hidden workstreams, or hidden capabilities.
