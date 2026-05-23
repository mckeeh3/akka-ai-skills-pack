# My Account Workstream PRD

## PRD identity

- **Workstream id:** `my_account`
- **Backing functional agent:** `functional_agent.my_account`
- **Domain:** `ai_first_saas_core_app`
- **Entry point:** signed-in user tile at bottom of left rail, showing user icon plus email/name
- **Purpose:** self-service signed-in user account, selected context, profile, settings, sign out, and browser-safe capability visibility
- **Primary users:** every authenticated user with at least one valid or recoverable account state

## Invariants

```text
This workstream is backed by exactly one functional/context-area agent.
Surfaces are the only renderable workstream artifacts.
System messages are typed surfaces.
Every surface action, including read/query and surface-request actions, maps to a governed backend capability.
The workstream agent may request surfaces and guide users, but backend capabilities enforce authority.
```

The signed-in user tile opens this workstream. Profile/settings/sign-out are not a separate shell menu.

## User intents

The workstream agent must handle:

- `dashboard`, `my account`, `show my account`
- `show my profile`, `edit my profile`
- `show settings`, `change timezone`, `change theme`, `change notification settings`
- `what can I do`, `why can't I access X`, `show my permissions`
- `switch tenant`, `switch customer`, `change context`
- `sign out`
- help/how-to questions about account, context, settings, capabilities, denials, and recovery

Side effects require explicit surface actions; the agent may guide but not silently change account state.

## Required surfaces

| Surface id | File candidate | Type | Purpose | Producing capability | Primary actions |
|---|---|---|---|---|---|
| `surface.my_account.dashboard.v1` | `surfaces/dashboard.md` | dashboard/detail | account overview, active AuthContext, profile/settings shortcuts, capabilities summary | `my_account.dashboard.view` | open profile, open settings, open context selector, sign out |
| `surface.my_account.profile.v1` | `surfaces/profile.md` | detail/form | view/edit allowed profile fields | `my_account.profile.view` | save profile, cancel, open audit trace |
| `surface.my_account.settings.v1` | `surfaces/settings.md` | form | preferences, locale/timezone/theme/notifications | `my_account.settings.view` | save settings, reset defaults |
| `surface.my_account.context_selector.v1` | `surfaces/context-selector.md` | data_table/form | choose tenant/customer AuthContext from memberships | `my_account.contexts.list` | select context, refresh contexts |
| `surface.my_account.capabilities.v1` | `surfaces/capabilities.md` | detail/table | browser-safe roles/capabilities and denied-workstream explanations | `my_account.capabilities.view` | open related workstream, request access info |
| `surface.my_account.system_message.v1` | `surfaces/system-messages.md` | system_message | success/denial/recovery/system feedback | capability-specific | retry, open trace, request help |

## Capability inventory and exposure channels

A capability is the governed backend contract. It may be exposed through one or more channels: surface action, browser API, workstream-agent tool, internal-agent tool, workflow step, timer, consumer, MCP tool, view, or internal method. Browser APIs and agent tools are exposure forms over the same capability; they do not redefine authorization, validation, idempotency, side effects, audit, or denial behavior.

For this workstream, read/evidence capabilities may be exposed to the My Account workstream agent as tools for conversational requests such as “what can I do?” or “why can’t I access User Admin?”. Command capabilities require explicit user confirmation or surface action; the agent may guide or prepare inputs but must not silently mutate account state.

| Capability id | Class | Purpose | Actors | Side effects | Result surface |
|---|---|---|---|---|---|
| `my_account.dashboard.view` | read/evidence | show current account/context summary | signed-in user, My Account agent | sensitive-read audit optional | dashboard |
| `my_account.profile.view` | read/evidence | fetch profile | signed-in user, My Account agent | sensitive-read audit optional | profile |
| `my_account.profile.update` | command | update allowed profile fields | signed-in user | profile update, audit | profile or system_message |
| `my_account.settings.view` | read/evidence | fetch settings | signed-in user, My Account agent | none or read trace | settings |
| `my_account.settings.update` | command | update preferences | signed-in user | settings update, audit | settings/system_message |
| `my_account.contexts.list` | read/evidence | list eligible AuthContexts | signed-in user, My Account agent | read trace | context_selector |
| `my_account.context.select` | command | set selected/default AuthContext | signed-in user | selected context update, audit | dashboard/system_message |
| `my_account.capabilities.view` | read/evidence | show browser-safe capabilities and denied reasons | signed-in user, My Account agent | read trace | capabilities |
| `my_account.session.sign_out` | command | end browser session | signed-in user | auth/session sign-out, audit | system_message or redirect |

## Authorization and scope

- Caller must be authenticated unless rendering safe signed-out recovery.
- Profile/settings update limited to own account fields.
- Email/provider subject and roles/capabilities are not editable here.
- Context selection limited to active memberships and allowed support-access context.
- Disabled users receive safe recovery/denial system-message surfaces and cannot perform protected actions except allowed recovery flows.
- `/api/me` remains authoritative for visible workstreams and capabilities.

## Workstream-agent prompt requirements

`workstream-agent/prompt.md` must define the agent as the user's own account assistant. It must:

- explain available account/context/settings actions;
- interpret shorthand surface requests;
- explain capability visibility and denials without leaking protected data;
- guide users to User Admin only when they have admin capabilities;
- never claim it can grant roles or bypass authorization;
- emit system-message surfaces for denied, disabled, stale, or recovery states.

Runtime skills should cover profile/settings guidance, context selection, capability explanations, and sign-out/recovery behavior.

## Akka realization candidates

- KVE: `AccountEntity`, `UserProfileEntity`, `UserSettingsEntity`, selected-context state if not derived.
- Views: `CurrentAccountView`, `MembershipContextView`, `CapabilitySummaryView`.
- HTTP: `/api/me`, `/api/my-account/dashboard`, `/api/my-account/profile`, `/api/my-account/settings`, `/api/my-account/contexts`, `/api/session/sign-out`.
- Agent: `MyAccountAgent` with read-only account/context/capability tools and no privileged side effects.
- Audit: `AccountProfileUpdated`, `UserSettingsUpdated`, `AuthContextSelected`, `SignOutRequested`, `CapabilityExplanationRequested`, denials.

## Tests

Required:

- `/api/me` returns browser-safe account/profile/settings/memberships/capabilities.
- User tile opens My Account workstream.
- Profile/settings read and update success.
- Invalid profile/settings validation produces `system_message`.
- Context selector excludes unauthorized tenant/customer contexts.
- Selecting context changes visible workstreams/capabilities.
- Disabled user receives safe denial/recovery.
- User cannot edit roles, email identity claims, or memberships here.
- Surface actions invoke backend capabilities.
- Audit/work traces emitted for updates, context changes, sign out, and denials.

## Not ready if

- profile/settings are implemented as a separate shell menu instead of workstream surfaces;
- visible capabilities are hardcoded in frontend;
- context selection is frontend-only;
- system messages are ad hoc toasts;
- user can mutate roles/memberships from this workstream;
- tests do not cover disabled, no-membership, and denied-context states.
