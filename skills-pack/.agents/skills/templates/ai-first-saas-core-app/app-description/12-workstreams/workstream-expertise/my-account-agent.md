# My Account Workstream Expert Bundle

- bundle-id: `my-account-agent.expertise`
- owning functional agent: `my-account-agent`
- workstream id: `my-account`
- scope: current account, selected AuthContext, profile/settings, sign out guidance, personal queue, and authorized cross-workstream attention summaries
- primary surfaces: `my-account-dashboard`, `markdown_response`, `system_message`
- model binding: inherited governed default or explicit `ModelConfigRef`/`ModelPolicy`; missing provider/security configuration fails closed with an actionable `system_message` and AgentWorkTrace, and no provider secrets appear in prompt, skill, reference, trace, or browser payloads

## Prompt intent

Help the signed-in user understand their selected context, profile/settings options, personal queue, safe next steps, and denials. My Account can open authorized source workstreams and surfaces, but does not own source attention items.

## Skill/reference families

- skills: context selection guidance, profile/settings help, denial recovery, personal queue explanation
- references: organization/customer context policy, account settings policy, support-access visibility note

Full content loads only through authorized `readSkill(skillId)` / `readReferenceDoc(referenceId)` calls assigned in compact manifests.

## Capability/tool boundary

Own-account reads are scoped to the active account and membership. Cross-workstream actions are surface-request actions that must pass target workstream authorization. `ToolPermissionBoundary` denies unassigned loaders, cross-scope reads, support-only evidence without an active support grant, cross-tenant data, and authority expansion from text.

## Tests

Cover own-scope reads, disabled-user denial, context selection, no duplicate top-rail My Account, authorized aggregate counts, source workstream non-ownership, loader denials, provider/model fail-closed behavior, AgentWorkTrace, and surface rendering.
