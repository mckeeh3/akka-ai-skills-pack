# My Account Starter Scope Reference

Available in the SMB full-core My Account baseline:
- selected AuthContext summary, authority basis, and browser-safe capability groups;
- signed-in account/profile/settings summaries plus deterministic self-service profile/settings update surfaces;
- dashboard, profile, settings, context/authority, notification center, personal digest progress/result, and blocked/open-denied recovery surfaces that keep fields reviewable and backend-authorized;
- personal attention aggregated only from authorized sibling workstreams;
- own trace refs and safe workstream navigation through backend authorization;
- provider/runtime blocked states rendered as typed `system_message` surfaces;
- governed `markdown_response` answers with PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, ToolPermissionBoundary, AgentWorkTrace, and model/provider trace metadata.

Deferred or out of scope:
- multi-device session administration;
- notification delivery preferences and personal digest workers until durable task semantics are selected;
- identity-provider administration, role/membership/support-access changes, policy commits, prompt/tool-boundary edits, provider configuration changes, and trace-redaction changes.

Security boundary: My Account can explain and guide from scoped evidence, but cannot edit roles, memberships, support access, tenant/customer boundaries, policies, agent behavior, provider settings, or audit/trace redaction.

Confirmed chat tool plan reference: My Account now has an expanded confirmed chat path catalog. All entries are self-scoped chat-executable-now paths requiring exact plan snapshot confirmation and backend reauthorization before any side effect:
- Profile and settings: `action-update-my-profile` / `schema.my-account.profile.update.v1`; `action-update-my-settings` / `schema.my-account.settings.update.v1`; both via `my_account.update_profile_settings`;
- Notification lifecycle: `action-notification-mark-read`, `action-notification-dismiss`, `action-notification-archive`, `action-notification-snooze` / `notification.manage_own_state` — own visible notifications only, idempotent, no-op repeated lifecycle;
- Notification preferences: `action-notification-update-preferences` / `notification.update_own_preferences` — in-app category preferences only.
Deterministic routing still opens settings/profile/notification surfaces first when safe; the chat plan proposal is no-mutation until exact snapshot confirmation and backend reauthorization. Backend-authorized and self-scoped at all steps; blocked/open-denied recovery surfaces handle unsupported requests.
