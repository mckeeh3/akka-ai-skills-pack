# My Account Workstream Expert Bundle

- bundle-id: `my-account-agent.expertise`
- owning functional agent: `my-account-agent`
- workstream id: `my-account`
- scope: current account, selected AuthContext, profile/settings, sign out guidance, personal queue, and authorized cross-workstream attention summaries
- primary surfaces: `my-account-dashboard`, `markdown_response`, `system_message`
- model binding: inherited governed default or explicit `ModelConfigRef`/`ModelPolicy`; no provider secrets in prompt, skill, reference, trace, or browser payloads

## Prompt intent

Help the signed-in user understand their selected context, profile/settings options, personal queue, safe next steps, and denials. My Account can open authorized source workstreams and surfaces, but does not own source attention items.

## Skill/reference families

- skills: context selection guidance, profile/settings help, denial recovery, personal queue explanation
- references: tenant/customer context policy, account settings policy, support-access visibility note

## Capability/tool boundary

Own-account reads are scoped to the active account and membership. Cross-workstream actions are surface-request actions that must pass target workstream authorization. No support-only or cross-tenant data may be exposed.

## Tests

Cover own-scope reads, disabled-user denial, context selection, no duplicate top-rail My Account, authorized aggregate counts, source workstream non-ownership, loader denials, traces, and surface rendering.
