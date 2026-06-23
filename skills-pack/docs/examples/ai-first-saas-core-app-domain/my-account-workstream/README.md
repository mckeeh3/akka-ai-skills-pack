# My Account Workstream PRD

## PRD identity

- **Workstream id:** `my_account`
- **Backing functional agent:** `functional_agent.my_account`
- **Domain:** `ai_first_saas_core_app`
- **Entry point:** signed-in user tile at bottom of left rail, showing user icon plus email/name
- **Purpose:** personal operating hub plus self-service signed-in user account, selected context, profile, settings, sign out, cross-workstream attention, and browser-safe capability visibility
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

The default My Account landing surface is a dashboard that answers: **what do I need to do next?** It should remain a lightweight personal operating hub, not a duplicate of every workstream's own dashboard.

## User intents

The workstream agent must handle:

- `dashboard`, `my account`, `show my account`
- `show my profile`, `edit my profile`
- `show settings`, `change timezone`, `change theme`, `change notification settings`
- `what can I do`, `why can't I access X`, `show my permissions`
- `switch tenant`, `switch customer`, `change context`
- `sign out`
- help/how-to questions about account, context, settings, capabilities, denials, and recovery

Side effects require explicit backend-authorized surface actions or modeled `human_chat_tool_plan` confirmation; the agent may guide or prepare inputs but must not silently change account state.

## Required surfaces

| Surface id | File candidate | Type | Purpose | Producing capability | Primary actions |
|---|---|---|---|---|---|
| `surface.my_account.dashboard.v1` | `surfaces/dashboard.md` | dashboard/detail | personal next-action hub with account overview, active AuthContext, profile/settings shortcuts, personal queue, accessible workstream attention counts, and capabilities summary | `my_account.dashboard.view` | open profile, open settings, open context selector, open personal queue item, open workstream, sign out |
| `surface.my_account.profile.v1` | `surfaces/profile.md` | detail/form | view/edit allowed profile fields | `my_account.profile.view` | save profile, cancel, open audit trace |
| `surface.my_account.settings.v1` | `surfaces/settings.md` | form | preferences, locale/timezone/named theme/notifications, including `preferredThemeId` selection from available theme ids | `my_account.settings.view` | save settings, reset defaults |
| `surface.my_account.context_selector.v1` | `surfaces/context-selector.md` | data_table/form | choose tenant/customer AuthContext from memberships | `my_account.contexts.list` | select context, refresh contexts |
| `surface.my_account.capabilities.v1` | `surfaces/capabilities.md` | detail/table | browser-safe roles/capabilities and denied-workstream explanations | `my_account.capabilities.view` | open related workstream, request access info |
| `surface.my_account.system_message.v1` | `surfaces/system-messages.md` | system_message | success/denial/recovery/system feedback | capability-specific | retry, open trace, request help |

## Capability inventory and exposure channels

A capability is the governed backend contract. It may be exposed through one or more channels: surface action/browser-tool, confirmed `human_chat_tool_plan`, AI-backed `agent_tool_call`/workstream-agent tool, browser API, internal-agent tool, workflow step, timer, consumer, MCP tool, view, or internal method. Browser APIs, confirmed chat plans, and agent tools are exposure forms over the same capability; they do not redefine authorization, validation, idempotency, side effects, audit, or denial behavior.

For this workstream, read/evidence capabilities may be exposed to the My Account workstream agent through confirmed `human_chat_tool_plan` or AI-backed `agent_tool_call` adapters for conversational requests such as “what can I do?” or “why can’t I access User Admin?”. Command capabilities require explicit surface action or plan-bound human confirmation; the agent may guide or prepare inputs but must not silently mutate account state.

| Capability id | Class | Purpose | Actors | Side effects | Result surface |
|---|---|---|---|---|---|
| `my_account.dashboard.view` | read/evidence | show current account/context summary, personal queue, and accessible workstream attention counts | signed-in user, My Account agent | sensitive-read audit optional | dashboard |
| `my_account.personal_queue.view` | read/evidence | show consolidated user-specific items needing action across accessible workstreams | signed-in user, My Account agent | read trace | dashboard or queue section |
| `my_account.workstream_attention.list` | read/evidence | show one attention count per accessible workstream with icon metadata and open-workstream action affordance | signed-in user, My Account agent | read trace | dashboard |
| `my_account.workstream.open` | read/surface-request | open an allowed workstream from the dashboard while preserving selected AuthContext and recording navigation feedback | signed-in user | workstream navigation trace optional | target workstream default surface |
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

## Surface style expectations

My Account surfaces inherit `ai-first-workstream-enterprise` from `../../../web-ui-style-guide.md` and the core domain overview. Keep the dashboard as a personal operating hub rendered with enterprise workstream surface patterns: profile/context summary cards, a concise personal attention queue, compact workstream status panels, capability/evidence affordances, and typed system-message cards for denial or recovery feedback.

The Settings surface should expose named theme selection. Users choose one available named theme by label; the saved value is the stable theme id in `preferredThemeId`. Initial available ids are `aurora-light`, `cobalt-light`, `obsidian-dark`, `midnight-dark`, and `dark-night`. The UI may use each theme's light/dark tone for contrast testing, but it must not present `system`, `light`, or `dark` as the primary user preference. Changing `preferredThemeId` only changes visual tokens and must not alter workstream visibility, capability grants, authorization, audit behavior, routes, or surface contracts.

## Dashboard surface details

The dashboard is the default surface for `dashboard`, `my account`, `show my account`, and signed-in user tile selection. It is a personal action router, not an account report. Aside from labels and minimal microcopy, visible content should be clickable indicators or shortcuts. It should include:

- **Attention counter strip first**: render cross-workstream attention counters above profile/settings/details using the same shared dashboard attention-card style as role-specific workstream dashboards: semibold/bold label, large `itemsNeedingAttention` number, and a concise badge/status or open affordance below. The whole counter opens the filtered workstream attention queue; `0` remains useful when it opens the empty queue, explanation, or history.
- **Personal queue**: a concise cross-workstream list below the counter strip of the signed-in user's actionable items, such as approvals, decisions, exceptions, reviews, assigned tasks, or overdue items. Each item includes enough context to choose the next action: title, source workstream, item type, priority/due state, and an action that opens the relevant workstream surface or detail.
- **Profile and Settings actions** as secondary shortcuts below the attention counters and personal queue. These request the Profile or Settings surface in the My Account workstream rather than navigating to a separate shell menu.
- **Workstream status panels**: one compact clickable panel for each workstream visible to the current `/api/me` AuthContext. Each panel uses the shared attention-card counter style and shows only a large `itemsNeedingAttention` number, a short label, and an icon button/open affordance or status badge for the workstream. The panel does not list detailed items; detailed investigation belongs below the counter strip or in that workstream's own dashboard.
- **Workstream icons**: use the universal shell workstream icon metadata. The icon is generated or selected from the workstream name/domain, appears on the panel button, and exposes the full workstream name through tooltip/accessible label text.
- **Context and authority indicator**: selected organization/customer and role/capability basis appear as concise microcopy attached to the context selector/capability shortcut, not as a passive summary block.

Dashboard payload guidance:

```ts
type MyAccountDashboardData = {
  profileSummary: {
    displayName: string;
    email: string;
    roleLabel?: string;
  };
  selectedAuthContext: {
    tenantLabel: string;
    customerLabel?: string;
    selectedContextId: string;
  };
  personalQueue: Array<{
    itemId: string;
    title: string;
    sourceWorkstreamId: string;
    sourceWorkstreamName: string;
    itemType: "decision" | "approval" | "exception" | "review" | "task";
    priorityLabel?: string;
    dueLabel?: string;
    openActionId: string;
  }>;
  workstreamStatus: Array<{
    workstreamId: string;
    displayName: string;
    icon: WorkstreamIconDescriptor;
    itemsNeedingAttention: number;
    openActionId: string;
  }>;
};
```

The dashboard should answer **where should I go next?** Workstream-specific dashboards answer **what exactly is happening here and what should I do inside this workstream?**

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

- `/api/me` returns browser-safe account/profile/settings/memberships/capabilities and visible workstream metadata.
- User tile opens My Account workstream dashboard.
- Dashboard renders profile/settings shortcuts, personal queue, and one attention count per accessible workstream.
- Workstream status panels do not expose inaccessible workstreams or hidden item details.
- Workstream icon buttons expose accessible labels/tooltips with full workstream names.
- Opening a queue item or workstream panel selects the target workstream/surface through a governed surface-request action.
- Profile/settings read and update success, including saving an available named theme id as `preferredThemeId`.
- Invalid profile/settings validation produces `system_message`, including unknown or unavailable theme ids.
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
