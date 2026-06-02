# Starter Core App Frontend Slice 8 Quality Handoff

## Scope

Slice 8 closes the localized starter frontend implementation with quality checks, route smoke coverage, build/package handoff notes, and manual light/dark/system accessibility-responsive review notes.

## Automated checks added

- `frontend/src/seed-frontend-quality.contract.test.mjs`
  - verifies every planned route shell is wired in `frontend/src/main.tsx`;
  - checks design-specific acceptance markers across Mission Control, Goal Workbench, Decision Queue, Governance Center, Audit Trace Explorer, Admin Users, and Profile Preferences;
  - checks source evidence for light/dark/system mode, focus visibility, main landmark, skip link, reduced motion, and responsive CSS;
  - checks source avoids unsafe dynamic HTML insertion;
  - checks frontend quality/build scripts and static output path documentation remain present.

Existing slice tests continue to cover typed clients, fixture adapters, design-system primitives, Mission Control behavior, goal/decision flows, and governance/audit/admin/profile flows.

## Build and Akka static hosting handoff

- Source of record: `frontend/src/**`.
- Frontend command: `cd frontend && npm run build`.
- Vite output path: `src/main/resources/static-resources/`.
- Akka endpoint serves:
  - `/` -> `index.html`;
  - `/favicon.ico` -> `favicon.ico`;
  - `/assets/**` -> hashed Vite assets referenced by `index.html`.
- Do not hand-edit generated Vite files under `src/main/resources/static-resources/assets/**`.
- The build command preserves other reference static examples under `src/main/resources/static-resources/**`; the active Vite bundle is the one referenced by generated `index.html`.

## Manual light/dark/system accessibility-responsive checklist

Status: completed for localized fixture frontend source and generated bundle.

- Light mode: token set exists under `[data-mode="light"]`; primary, AI, status, surface, border, and focus tokens are semantic.
- Dark mode: token set exists under `[data-mode="dark"]`; dark palette keeps the same layout/component anatomy.
- System mode: root mode resolution follows `prefers-color-scheme` while preserving `data-mode-preference="system"`.
- Focus: skip link and `:focus-visible` rules are present; route navigation moves focus to `#main-content`.
- Landmarks/headings: app shell has navigation and main landmarks; screens expose section/card headings for primary regions.
- Color not alone: status pills/cards render textual labels for priority, state, risk, policy, and connection status.
- Forms: visible labels and validation messages exist for goal, decision, governance, admin, audit, and preference forms.
- Dynamic content safety: React text binding is used; no `dangerouslySetInnerHTML` or direct `innerHTML` source writes are present.
- Reduced motion: global reduced-motion CSS is present.
- Narrow screens: shell, Mission Control, goal/decision two-column flows, trace rows, and admin/profile forms have responsive CSS; primary review/attention tasks remain before secondary diagnostics.

## Explicit defers preserved

- Real authenticated backend integration.
- Production identity-provider wiring.
- Backend admin authorization and tenant-scope enforcement.
- Real policy simulation or authorized policy commit endpoint.
- Real trace export.
- Durable Akka state and workflow/entity implementation for starter core app business objects.

## Final handoff note

The localized frontend is now suitable as a skills-pack reference asset for UI generation and design validation. Future backend realization should keep the existing client interfaces and replace fixture adapters with real Akka HTTP API implementations instead of rewriting screen components.
