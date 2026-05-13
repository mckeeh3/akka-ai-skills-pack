# Seed App Design Validation

## Scope

Validated the seed app app-description against the mockup-derived design system.

Inputs:

- `specs/web-ui-design/ai-first-saas-web-ui-design-spec.md`
- `specs/web-ui-design/images/ai-first-saas-web-ui-01.png` through `ai-first-saas-web-ui-08.png`
- `docs/examples/ai-first-saas-seed-app-description/app-description/55-ui/**`
- `docs/examples/ai-first-saas-seed-app-description/app-description/15-operating-model/**`
- `docs/examples/ai-first-saas-seed-app-description/app-description/20-behavior/**`
- `docs/examples/ai-first-saas-seed-app-description/app-description/30-tests/**`
- `docs/examples/ai-first-saas-seed-app-description/app-description/40-auth-security/**`

## Validation result

- status: validated for seed-app UI design direction
- frontend planning readiness: ready for localized frontend implementation planning
- full app generation readiness: not validated for one-shot all-phases generation

The seed app description now supports the design system well enough to plan a bounded frontend implementation slice. Remaining open backend/cross-cutting decisions should not block frontend shell and UI-contract planning, as long as implementation uses typed client seams and fixture-backed states until backend routes are available.

## Validated design contract

### 1. AI-first operating model fit

Pass.

The UI description preserves the core AI-first model:

```text
human objective
→ durable goal and plan
→ bounded agent/team execution
→ policy, permission, evidence, and approval controls
→ human supervision and exception handling
→ traceable outcomes and learning loops
```

Evidence:

- `55-ui/ai-first-surfaces.md` defines Goal-to-Execution Workbench, Mission Control, Decision Review, Governance Center, Digest, and Audit Trace surfaces.
- `15-operating-model/` defines goals, retained human authority, decisions, policies, traces, and outcomes.
- `20-behavior/flows/` links goal execution and decision review to durable workflow semantics.

### 2. Mockup-derived visual system fit

Pass.

The seed app maps the mockups' layout and hierarchy without copying demo content:

- persistent left nav and authenticated SaaS shell;
- page title/subtitle and operational framing;
- AI command strip near the top of operational screens;
- KPI summary band;
- decision/exception cards with clear actions;
- agent activity timeline/cards;
- governance/trust controls;
- audit and evidence links.

The seed style guide explicitly forbids copying Atlas Ops names, mock users, mock customer/fleet data, logos, and metrics.

### 3. Light/dark/system mode

Pass.

The style guide and design spec require:

- light mode tokens;
- dark mode tokens;
- system mode preference;
- token-driven styling through CSS variables;
- visible focus and contrast in both modes.

Validation note: generated implementation must still run manual/automated contrast and focus checks after CSS exists.

### 4. Lightweight theme model

Pass.

The seed style guide and design spec restrict lightweight themes to:

- color tokens;
- font-family tokens.

They explicitly keep layout, spacing, component anatomy, radii, and interaction rules stable. This is suitable for later skills-pack integration because generation can rely on stable component structures.

### 5. Screen and navigation coverage

Pass for localized planning.

Covered screens:

- Briefing / Mission Control;
- Goal Workbench;
- Goal Detail / Execution Trace Summary;
- Decision Queue;
- Decision Card Detail;
- Governance Center / Policies;
- Audit Trace Explorer;
- Admin Users and Invitations;
- Profile / Preferences.

Validation note: route-level detail is sufficient for planning. Per-component visual copy and final content ordering should be refined during implementation task briefs.

### 6. Forms and action behavior

Pass for planning.

The seed app now defines fields, validation, idempotency, success behavior, and failure recovery for:

- create goal;
- request draft plan;
- approve launch;
- invite user;
- edit role assignment;
- review decision card;
- propose policy change;
- export trace/report.

Validation note: backend validation and command idempotency must be implemented authoritatively server-side; frontend validation is only early feedback.

### 7. Frontend API contract readiness

Pass for frontend seam planning.

The seed app now defines browser-facing DTO sketches for:

- session/user/tenant context;
- preferences and mode selection;
- admin users/invitations/roles;
- goals/plans;
- decisions and decision actions;
- governance policies/proposals/simulation;
- audit trace search/detail;
- realtime SSE event envelope.

Validation note: DTOs are intentionally sketches. Backend implementation may refine names, but frontend should keep a typed client boundary so changes remain localized.

### 8. Realtime behavior

Pass for planning.

Realtime expectations cover:

- SSE as default transport;
- mission-control activity;
- goal progress;
- decision queue updates;
- governance proposal updates;
- stale/reconnecting states;
- idempotent merge by id/version;
- tenant and permission scoped subscriptions.

Validation note: initial localized frontend can use fixture streams or an in-memory event simulator until backend SSE exists.

### 9. Accessibility and responsive behavior

Pass for planning.

The description includes:

- first-five-seconds comprehension targets;
- keyboard/focus expectations;
- modal/drawer focus return;
- color-not-alone status semantics;
- contrast requirements;
- mobile/tablet/desktop priority behavior;
- reduced motion.

Validation note: generated components must still be checked after implementation.

### 10. Tests and acceptance criteria

Pass for planning.

Design-specific acceptance checks now cover:

- selected style guide;
- shell and route state;
- light/dark/system mode;
- Mission Control regions;
- command strip constraints;
- decision card anatomy;
- Goal Workbench;
- Governance Center;
- Audit Trace Explorer;
- realtime stale/reconnect;
- responsive primary-action preservation;
- keyboard navigation and focus;
- color-not-alone status semantics.

## Validated implementation boundary

A localized frontend implementation plan may proceed under these constraints:

- implement frontend architecture and UI state seams without requiring final backend component implementation;
- use typed client interfaces matching `55-ui/frontend-api-contracts.md`;
- provide fixture-backed client adapters for seed validation where backend endpoints do not yet exist;
- do not choose final production auth provider during frontend planning;
- keep auth/session UI behind a `SessionClient` seam;
- do not implement MCP/gRPC UI-specific work in this slice;
- do not copy mockup content.

## Remaining risks

| Risk | Impact | Mitigation |
|---|---|---|
| Auth provider undecided | Login/session mechanics may change | Keep frontend behind session client seam and route guards. |
| Backend endpoints not implemented | UI cannot fully integrate yet | Use typed client interfaces plus fixture adapter until endpoints exist. |
| Realtime backend not implemented | Mission control cannot stream live data yet | Implement SSE client lifecycle and fixture event simulator. |
| Full phase boundary undecided | Scope creep into all app phases | Limit localized plan to shell + core AI-first UI scaffolding. |
| Contrast/focus untested in real CSS | Accessibility regressions possible | Add manual and automated checks after CSS implementation. |

## Validation conclusion

The seed app design is validated for the intended purpose: testing the mockup-derived AI-first SaaS UI design before integrating it into the skills pack. The next step is a localized frontend implementation plan that creates reusable frontend structure, tokens, route shells, typed clients, fixture-backed state, and acceptance checks without depending on complete backend generation.
