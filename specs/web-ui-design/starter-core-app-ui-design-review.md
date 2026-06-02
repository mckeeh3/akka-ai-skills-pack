# Starter Core App UI Design Review

## Scope

Reviewed the starter core app UI description against `specs/web-ui-design/ai-first-saas-web-ui-design-spec.md`.

Reviewed files:

- `templates/ai-first-saas-starter/app-description/app-description/55-ui/ui-index.md`
- `templates/ai-first-saas-starter/app-description/app-description/55-ui/style-guide.md`
- `templates/ai-first-saas-starter/app-description/app-description/55-ui/screens-and-navigation.md`
- `templates/ai-first-saas-starter/app-description/app-description/55-ui/interactions-and-forms.md`
- `templates/ai-first-saas-starter/app-description/app-description/55-ui/frontend-api-contracts.md`
- `templates/ai-first-saas-starter/app-description/app-description/55-ui/states-and-realtime.md`
- `templates/ai-first-saas-starter/app-description/app-description/55-ui/accessibility-and-responsive.md`
- `templates/ai-first-saas-starter/app-description/30-tests/acceptance/01-core-app-acceptance.md`
- `templates/ai-first-saas-starter/app-description/app-description/30-tests/negative/01-forbidden-actions.md`
- `templates/ai-first-saas-starter/app-description/app-description/30-tests/operational/01-observability-and-audit.md`

## Result

- review status: passes for planning and seed validation
- generation readiness: improved after follow-up updates; still not a one-shot all-phases generation target because auth provider, first slice boundary, persistence assumptions, and optional MCP/gRPC scope remain open
- main reason: the starter core app now has a selected AI-first visual system, screen structure, AI-first surface contract, form/action requirements, frontend API DTO sketches, realtime expectations, and design-specific acceptance checks.

## What is now strong

### 1. Design selection is explicit

`style-guide.md` now records:

- selected style: `atlas-ops-supervisory-console`
- light/dark/system mode policy
- source reference to the design spec and mockups
- no-copy rule for mockup names, logos, users, and metrics
- token-driven implementation expectations

This resolves the prior style-selection gap for seed validation.

### 2. AI-first surfaces are represented

`screens-and-navigation.md` now covers the required AI-first surface families:

- Mission Control / Briefing
- Goal Workbench
- Goal Detail / Execution Trace Summary
- Decision Queue
- Decision Card Detail
- Governance Center / Policies
- Audit Trace Explorer
- Admin Users and Invitations
- Profile / Preferences

These align with the design spec and the canonical AI-first SaaS doctrine.

### 3. Light/dark mode and lightweight style overrides are constrained

The seed style guide correctly requires:

- light, dark, and system support
- token-driven CSS variables
- style overrides limited to colors and fonts
- stable layout/component anatomy across style variants

This is suitable for testing the style model before skills-pack integration.

### 4. Responsive and accessibility expectations are clearer

`accessibility-and-responsive.md` now covers:

- first-five-seconds comprehension
- keyboard/focus behavior
- mobile/tablet/desktop behavior
- WCAG AA contrast expectation
- color-not-alone status semantics
- reduced motion

This is enough to guide frontend implementation planning.

### 5. Realtime state expectations fit the AI-first UI

`states-and-realtime.md` now covers:

- stale/reconnecting behavior
- mission-control activity streams
- decision queue changes
- goal progress updates
- governance proposal updates
- idempotent event merge expectations
- tenant/permission scoped subscriptions

This aligns with the mockups' live operational-console feel.

## Gaps before full frontend generation

### Gap 1: Missing dedicated AI-first surfaces file

The UI skill recommends `55-ui/ai-first-surfaces.md` when delegated work, supervision, decisions, governance, digests, audit, or outcomes are in scope.

Current state:

- AI-first surfaces are embedded in `screens-and-navigation.md`.
- This is acceptable for seed validation.
- For generation and skills-pack integration, a dedicated file would make routing and regeneration safer.

Recommended next action:

- Add `templates/ai-first-saas-starter/app-description/app-description/55-ui/ai-first-surfaces.md` before frontend generation.

### Gap 2: Interactions and forms are too shallow

`interactions-and-forms.md` names forms but does not yet define:

- fields;
- labels and helper text;
- client validation;
- backend validation mapping;
- duplicate-submit/idempotency rules per action;
- success copy;
- confirmation copy for high-impact actions;
- focus behavior after validation failure per form.

Recommended next action:

- Expand the five named forms: create goal, invite user, edit role assignment, review decision card, propose policy change.

### Gap 3: Frontend API contracts are route-family level only

`frontend-api-contracts.md` defines endpoint families and common error shape, but not implementation-grade contracts.

Missing examples:

- `GET /api/me` response DTO;
- `POST /api/goals` request/response DTOs;
- `POST /api/goals/{goalId}/plan` or equivalent draft-plan route;
- decision action DTOs for approve/reject/counter/defer/escalate;
- governance proposal DTOs;
- audit search request/response DTOs;
- SSE event envelope shapes.

Recommended next action:

- Add browser-facing DTO sketches for phase-1 and phase-2 routes before generating TypeScript clients.

### Gap 4: Per-screen UX handoffs are not yet complete

`screens-and-navigation.md` defines the screen set and required regions, but downstream frontend generation will need per-screen handoff details:

- loading state;
- empty state;
- API failure recovery;
- success feedback;
- key UX copy;
- keyboard path;
- narrow-screen priority order;
- first actionable focus target.

Recommended next action:

- Add concise UX handoff blocks for Mission Control, Goal Workbench, Decision Queue, Decision Detail, Governance Center, and Audit Trace Explorer.

### Gap 5: Tests mention frontend states but do not yet verify the new design contract

Acceptance tests currently include a broad frontend shell assertion. They do not yet call out:

- light/dark mode rendering;
- selected style token use;
- command strip presence;
- decision-card evidence/risk/policy sections;
- responsive primary-action preservation;
- stale/reconnect indicator behavior.

Recommended next action:

- Add UI acceptance checks for the design spec before realization.

## Review checklist

| Area | Status | Notes |
|---|---:|---|
| Selected style guide | Pass | Style guide and source reference are explicit. |
| Light/dark/system mode | Pass | Required and token-driven. |
| Style override scope limited to colors/fonts | Pass | Explicitly constrained. |
| App shell/navigation | Pass | Desktop and mobile behavior described. |
| AI command strip | Pass | Required on operational screens. |
| KPI/summary band | Pass | Required on Mission Control. |
| Decision cards | Partial | Screen requirements exist; detail anatomy should be expanded in UX/API docs. |
| Agent activity visibility | Pass | Mission Control and goal detail include agent activity. |
| Governance controls | Pass | Governance center and trust controls represented. |
| Audit/trace paths | Pass | Audit explorer and trace links represented. |
| Forms/validation | Partial | High-level only. |
| Frontend API contracts | Partial | Endpoint families only. |
| Realtime/stale behavior | Pass | Good planning-level coverage. |
| Accessibility | Pass | Good planning-level coverage. |
| Responsive behavior | Pass | Good planning-level coverage. |
| UI tests | Partial | Acceptance intent exists; design-specific checks missing. |

## Completed follow-up updates

The recommended immediate follow-up has been applied:

1. Added `55-ui/ai-first-surfaces.md` for explicit AI-first UI surface semantics.
2. Expanded `55-ui/interactions-and-forms.md` with implementation-grade form/action specs.
3. Expanded `55-ui/frontend-api-contracts.md` with DTO sketches and SSE event envelopes.
4. Added design-specific UI checks to `30-tests/acceptance/01-starter-core-app-acceptance.md`.
5. Updated `00-system/readiness-status.md` to distinguish UI design validation readiness from full all-phases generation readiness.

## Remaining pre-generation decisions

Before full code generation, resolve or defer:

1. concrete local/prod authentication provider mode;
2. first implementation slice boundary;
3. local test persistence expectations;
4. whether phase-1 frontend includes only admin/auth shell or also phase-2 AI-first mission-control surfaces;
5. whether MCP/gRPC are included in seed v1 or deferred modules.

## Conclusion

The starter core app is now a strong validation target for the mockup-derived AI-first web UI design. It is ready for planning, design validation, and localized frontend implementation planning. It should not yet be treated as ready for complete one-shot generation of every backend/frontend phase until the remaining cross-cutting generation decisions are resolved or explicitly deferred.
