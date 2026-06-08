# Starter Core App Localized Frontend Implementation Plan

## Purpose

Plan a bounded frontend implementation for the AI-first SaaS starter core app that validates the mockup-derived design system without requiring complete backend generation.

This plan is a frontend implementation contract, not a full app build plan.

## Inputs

- `specs/web-ui-design/ai-first-saas-web-ui-design-spec.md`
- `specs/web-ui-design/starter-core-app-design-validation.md`
- `app-description/global/surfaces/foundation-surface-patterns.md` and `app-description/domains/core-starter/workstreams/*/surfaces/surfaces.md`
- `app-description/domains/core-starter/workstreams/*/tests/coverage.md`
- `app-description/global/policies/foundation-security-and-governance.md`

## Scope

### In scope

- React + Vite + TypeScript frontend structure.
- Tokenized CSS implementation of `atlas-ops-supervisory-console`.
- Light, dark, and system mode.
- Stable app shell and navigation.
- Route shells and state models for:
  - Briefing / Mission Control;
  - Goal Workbench;
  - Goal Detail;
  - Decision Queue;
  - Decision Card Detail;
  - Governance Center;
  - Audit Trace Explorer;
  - Admin Users and Invitations;
  - Profile / Preferences.
- Typed client interfaces matching starter frontend API contracts.
- Fixture-backed client adapter for design validation.
- SSE client lifecycle abstraction plus fixture event simulator.
- Reusable UI primitives needed by the starter core app surfaces.
- Accessibility/responsive implementation checks.

### Out of scope

- Final production identity-provider integration.
- Complete backend component implementation.
- Durable Akka state implementation.
- MCP/gRPC frontend affordances.
- Complete policy simulation engine.
- Real chart/map integrations beyond tokenized placeholder components.
- Copying mockup names, logos, people, accounts, fleet/customer data, or metrics.

## Assumptions

- Frontend source will live under `frontend/**` when realization begins.
- Built assets will eventually be packaged under `src/main/resources/static-resources/**` for Akka HTTP static hosting.
- UI can use fixture-backed data while backend APIs are incomplete.
- Frontend must be able to swap fixture clients for real API clients without rewriting screen components.
- The first implementation should optimize for reusable design-system validation, not product completeness.

## Architecture

### Proposed frontend structure

```text
frontend/
  index.html
  package.json
  tsconfig.json
  vite.config.ts
  src/
    main.tsx
    App.tsx
    routes.tsx
    styles/
      tokens.css
      base.css
      layout.css
      components.css
    design-system/
      Badge.tsx
      Button.tsx
      Card.tsx
      CommandStrip.tsx
      DataState.tsx
      Drawer.tsx
      FormField.tsx
      IconChip.tsx
      KpiCard.tsx
      Modal.tsx
      PageHeader.tsx
      StatusPill.tsx
      Tabs.tsx
    shell/
      AppShell.tsx
      SidebarNav.tsx
      TenantSwitcher.tsx
      UserMenu.tsx
      NotificationsButton.tsx
      ColorModeToggle.tsx
    api/
      types.ts
      ApiClient.ts
      FixtureApiClient.ts
      HttpApiClient.ts
      RealtimeClient.ts
      FixtureRealtimeClient.ts
    state/
      session.ts
      colorMode.ts
      realtime.ts
    screens/
      briefing/
        BriefingPage.tsx
        MissionKpiBand.tsx
        AgentActivityTimeline.tsx
        NeedsAttentionPanel.tsx
        AgentTeamsPanel.tsx
        TrustControlsPanel.tsx
        UpcomingActionsPanel.tsx
      goals/
        GoalWorkbenchPage.tsx
        GoalDetailPage.tsx
        GoalForm.tsx
        PlanReviewPanel.tsx
        ApprovalGatesPanel.tsx
      decisions/
        DecisionQueuePage.tsx
        DecisionCardDetailPage.tsx
        DecisionCard.tsx
        DecisionActionForm.tsx
      governance/
        GovernancePoliciesPage.tsx
        PolicyProposalPanel.tsx
      audit/
        AuditTraceExplorerPage.tsx
        TraceDetailPanel.tsx
      admin/
        AdminUsersPage.tsx
        InviteUserForm.tsx
        RoleAssignmentForm.tsx
      profile/
        ProfilePreferencesPage.tsx
    test-utils/
      renderWithProviders.tsx
      fixtures.ts
```

## Design-system implementation rules

- Define all colors and fonts in CSS variables.
- Keep spacing, radius, component anatomy, and layout stable across lightweight style overrides.
- Mode switch uses `data-mode="light|dark"` or equivalent documented root attribute.
- System mode observes `prefers-color-scheme` and user preference.
- Components consume semantic tokens, not raw hex values.
- Status components always render text labels and accessible names.
- Cards, badges, buttons, focus rings, and charts use tokenized colors.

## Screen implementation handoffs

### Briefing / Mission Control

- route: `/ui/briefing`
- first-five-seconds target: user sees active autonomous work, top risks, pending decisions, and policy/trust state.
- primary action: review highest-priority item in `NeedsAttentionPanel`.
- components:
  - `PageHeader`
  - `CommandStrip`
  - `MissionKpiBand`
  - `AgentActivityTimeline`
  - `NeedsAttentionPanel`
  - `AgentTeamsPanel`
  - `TrustControlsPanel`
  - `UpcomingActionsPanel`
- states:
  - loading skeletons per panel
  - empty: no active work or no pending attention
  - ready
  - stale/reconnecting
  - API failure with retry
- fixture data:
  - generic seed goals, agents, decisions, policies, and outcomes; no mockup names/data.
- acceptance focus:
  - command strip visible
  - KPI band visible
  - attention queue actions visible
  - stale/reconnect indicator works
  - narrow screen orders attention queue before secondary panels

### Goal Workbench

- route: `/ui/goals/new`
- first-five-seconds target: user knows how to define an objective and what authority gates will apply.
- primary action: create goal, then draft plan, then approve launch when ready.
- components:
  - `GoalForm`
  - `PlanReviewPanel`
  - `ApprovalGatesPanel`
- states:
  - draft form
  - validation error
  - submitting
  - draft-plan running
  - plan ready
  - launch blocked by gate
  - launched success
- acceptance focus:
  - labels/helper text present
  - validation preserves input
  - high-impact launch confirmation names consequence
  - focus moves to first invalid field

### Decision Queue

- route: `/ui/decisions`
- first-five-seconds target: user sees which decisions need action and why.
- primary action: open or act on highest-priority decision.
- components:
  - filters
  - `DecisionCard` list
  - queue count/status
- states:
  - loading
  - empty: no open decisions
  - ready
  - stale/reconnecting
  - conflict after action
- acceptance focus:
  - decision cards show recommendation, evidence/risk/policy, allowed actions, and trace link
  - status not color-only

### Decision Card Detail

- route: `/ui/decisions/:decisionId`
- first-five-seconds target: user can evaluate recommendation, evidence, risk, confidence, impact, policy trigger, and allowed actions.
- primary action: approve/reject/request changes/escalate/counter based on permission.
- components:
  - evidence panel
  - risk/impact summary
  - policy trigger summary
  - `DecisionActionForm`
  - trace links
- states:
  - loading
  - not found/forbidden
  - ready
  - submitting action
  - success
  - stale conflict
- acceptance focus:
  - acknowledgement required for high-impact approval
  - conflict message if version changed
  - focus returns to next actionable decision after completion

### Governance Center

- route: `/ui/governance/policies`
- first-five-seconds target: policy owner sees active policies, open proposals, and simulation/commit state.
- primary action: review policy proposal or run simulation.
- components:
  - policy list
  - proposal panel
  - simulation status
  - audit links
- states:
  - loading
  - empty proposals
  - simulation queued/running/completed
  - commit confirmation
- acceptance focus:
  - policy commit confirmation warns about authority change
  - proposal cannot activate policy without authorized commit

### Audit Trace Explorer

- route: `/ui/audit/traces`
- first-five-seconds target: auditor sees search filters and recent trace results with authorization/correlation context.
- primary action: search traces and inspect detail.
- components:
  - filter form
  - trace result list/table
  - `TraceDetailPanel`
- states:
  - initial empty search
  - loading
  - no results
  - ready
  - forbidden export
- acceptance focus:
  - filters cover goal, agent, decision, policy, tool, actor, time
  - trace rows show authorization basis and correlation id

### Admin Users and Invitations

- route: `/ui/admin/users`
- first-five-seconds target: tenant admin sees users, roles, invitation state, and primary invite action.
- primary action: invite user.
- components:
  - user list
  - `InviteUserForm`
  - `RoleAssignmentForm`
- states:
  - loading
  - empty users/invitations
  - validation error
  - duplicate invitation
  - forbidden role grant
  - success
- acceptance focus:
  - invite form validates email and role
  - elevated role change requires reason/confirmation

### Profile / Preferences

- route: `/ui/profile`
- first-five-seconds target: user can change local preferences, especially light/dark/system mode.
- primary action: save preferences.
- components:
  - profile summary
  - `ColorModeToggle`
  - notification preferences placeholder
- states:
  - ready
  - saving
  - success
  - API failure
- acceptance focus:
  - mode change updates root token attribute
  - preference persisted through client seam

## API/client planning

### Client interface groups

- `SessionClient`: `getMe`, `updatePreferences`
- `AdminClient`: `listUsers`, `inviteUser`, `updateRoles`
- `GoalsClient`: `listGoals`, `getGoal`, `createGoal`, `draftPlan`, `launchGoal`
- `DecisionsClient`: `listDecisions`, `getDecision`, `actOnDecision`
- `GovernanceClient`: `listPolicies`, `createPolicyProposal`, `simulatePolicyProposal`
- `AuditClient`: `searchTraces`, `getTrace`, `exportTraces`
- `RealtimeClient`: `connect(topics)`, `disconnect`, event subscription callbacks

### Fixture adapter rules

- Fixture data must use generic seed names and invented neutral metrics.
- Fixture adapter implements the same TypeScript interfaces as HTTP adapter.
- Fixture adapter simulates:
  - loading delay;
  - validation errors;
  - forbidden responses;
  - stale version conflicts;
  - SSE duplicate/replay events;
  - reconnect/stale states.

## Implementation slices

### Slice 1: Frontend foundation and design tokens

Status: complete. See `starter-core-app-frontend-slice-1-completion.md`.

Outputs:

- Vite/React/TypeScript project skeleton.
- CSS token files for light/dark/system mode.
- base layout and accessibility defaults.
- color-mode state and persistence seam.

Validation:

- app renders in light and dark modes;
- focus ring visible;
- no raw mockup names/data appear.

### Slice 2: App shell and routing

Status: complete. See `starter-core-app-frontend-slice-2-completion.md`.

Outputs:

- `AppShell`, sidebar nav, user region, notifications placeholder, tenant switcher placeholder.
- route configuration for planned screens.
- responsive nav behavior.

Validation:

- active route state visible;
- keyboard can reach nav and main content;
- mobile/drawer behavior preserves route access.

### Slice 3: Typed clients and fixtures

Status: complete. See `starter-core-app-frontend-slice-3-completion.md`.

Outputs:

- DTOs from `frontend-api-contracts.md`.
- API client interfaces.
- fixture-backed client adapter.
- realtime fixture client.

Validation:

- screens can render from fixture data only through clients;
- fixture errors drive UI error states.

### Slice 4: Reusable UI primitives

Status: complete. See `starter-core-app-frontend-slice-4-completion.md`.

Outputs:

- buttons, cards, badges/status pills, command strip, KPI cards, forms, data-state wrappers, modal/drawer.

Validation:

- components use tokens;
- status labels are accessible;
- loading/empty/error/success states have specific copy slots.

### Slice 5: Mission Control validation screen

Outputs:

- Briefing / Mission Control screen and panels.
- command strip interactions stubbed to safe fixture responses.
- realtime stale/reconnect indicator.

Validation:

- matches design spec hierarchy;
- no high-impact command executes directly;
- responsive order preserves attention queue.

### Slice 6: Goal and decision flows

Status: complete. See `starter-core-app-frontend-slice-6-completion.md`.

Outputs:

- Goal Workbench and Goal Detail.
- Decision Queue and Decision Detail.
- form validation, confirmations, success/failure states.

Validation:

- goal create/draft/launch fixture flow works;
- decision approve/reject/conflict fixture flow works;
- keyboard/focus behavior works.

### Slice 7: Governance, audit, admin, profile screens

Status: complete. See `starter-core-app-frontend-slice-7-completion.md`.

Outputs:

- Governance Center.
- Audit Trace Explorer.
- Admin Users and Invitations.
- Profile Preferences with mode selection.

Validation:

- policy proposal/simulation fixture states render;
- audit filters/results/detail render;
- invite/role forms validate;
- mode preference works through client seam.

### Slice 8: Quality checks and packaging handoff

Outputs:

- frontend checks/build command.
- smoke tests or component tests for design-specific acceptance checks.
- static build output path documented for Akka hosting.

Validation:

- production frontend build passes;
- route shell smoke checks pass;
- light/dark visual/accessibility manual checklist completed.

## Test plan

Minimum frontend test coverage for localized implementation:

- shell renders authenticated layout and active nav state;
- mode toggle applies light/dark/system token mode;
- Mission Control renders command strip, KPI band, attention queue, trust controls, and upcoming actions;
- decision card renders recommendation, evidence, risk, policy trigger, actions, and trace link;
- create goal validation preserves input and focuses first invalid field;
- decision action handles success and stale conflict;
- realtime fixture duplicate events do not duplicate visible rows;
- stale/reconnecting state appears and recovers;
- admin invite validates email/role and handles duplicate invite;
- audit search renders no-results and result-detail states;
- mobile layout keeps primary decision/action before secondary panels.

## Done criteria

Localized frontend implementation is complete when:

- all slices above are implemented or explicitly deferred;
- design tokens implement light/dark/system mode;
- all screens render with fixture data through typed client interfaces;
- core forms and action states are represented;
- realtime stale/reconnect behavior is demonstrable;
- acceptance checks from `30-tests/acceptance/01-starter-core-app-acceptance.md` are represented in tests or manual validation notes;
- generated frontend avoids copied mockup content;
- implementation can later swap fixture clients for real Akka HTTP APIs without screen rewrites.

## Recommended next execution task

Start with Slice 1 only:

> Create the starter frontend React/Vite/TypeScript foundation with tokenized `atlas-ops-supervisory-console` light/dark/system mode CSS, base app mount, and a minimal page proving mode switching and focus visibility. Do not implement app screens yet.
