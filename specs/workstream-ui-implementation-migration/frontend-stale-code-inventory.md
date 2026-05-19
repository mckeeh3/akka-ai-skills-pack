# Frontend Stale Code Inventory

## Scope

Inventory for `TASK-WUI-01-001`. This is an assessment only: no production/frontend code was rewritten.

Sources inspected:
- `frontend/**` tracked source, package metadata, tests, and local untracked/ignored directories
- `src/main/resources/static-resources/**` tracked static assets and reference examples
- workstream UI doctrine in `docs/agent-workstream-application-architecture.md` and `docs/structured-surface-contracts.md`
- migration plan and sprint 1 task context under `specs/workstream-ui-implementation-migration/`

## Classification legend

- **canonical / preserve**: useful as source-of-record or reusable implementation material.
- **revise**: keep concepts or seams, but reshape to workstream-first architecture.
- **retire**: remove after replacement because it promotes stale/page-first behavior or is generated buildup.
- **quarantine**: keep only as an explicitly legacy or narrow reference, not canonical generated-app guidance.
- **generated**: produced by tooling; do not hand-edit.

## High-level findings

1. `frontend/` is a real React/Vite/TypeScript source-of-record and should remain the implementation root.
2. The current authenticated app model is route/page-first: `RouteId`, `routes`, `SidebarNav`, `PageHeader`, `RouteShell`, and `frontend/src/screens/**` make pages/screens the primary decomposition.
3. The existing API and realtime client seams are valuable, but their DTOs are screen/domain-family oriented rather than workstream/surface envelope oriented.
4. `src/main/resources/static-resources/assets/` contains many stale Vite hash outputs. Only `index-B05vAgYp.js` and `index-Bw1dY5G_.css` are referenced by the current `index.html`; all other hashed assets are unreferenced build residue.
5. Several static-resource examples are useful as narrow endpoint/UI examples, but they should be quarantined from canonical full-stack SaaS UI guidance because they are page/demo-first.
6. `frontend/node_modules/` and `frontend/.env.local` exist locally but are untracked. They are not source assets and should remain ignored/outside migration commits.

## Inventory: `frontend/`

| Path | Classification | Recommendation | Rationale |
|---|---|---|---|
| `frontend/package.json`, `package-lock.json`, `tsconfig.json`, `vite.config.ts`, `index.html`, `public/favicon.ico` | canonical / preserve | Preserve and revise metadata names/copy when the workstream reference replaces the seed console. | Standard React/Vite project and Akka build output shape are correct. |
| `frontend/README.md` | revise | Rewrite in a later docs task to describe workstream shell checks, generated output, and fixtures. | Current README calls the app a "Seed frontend" and lists route smoke scope as `/ui/...` pages. |
| `frontend/src/main.tsx` | retire after replacement | Replace with a workstream shell entry that selects functional agents, embeds workstream stream + composer, and treats routes as deep links. | Current root uses `RouteId`, `routes`, `SidebarNav`, `PageHeader`, and `RouteShell`; it is page-first and brands the shell as a supervisory console. |
| `frontend/src/screens/**` | retire/rebuild as surfaces | Rebuild useful ideas into `frontend/src/workstream/surfaces/**` and reference vertical fixtures. | Directory and component names (`*Page.tsx`) encode page/screen-first decomposition. |
| `frontend/src/screens/admin/AdminUsersPage.tsx` | revise into reference vertical | Rebuild as User Admin dashboard/list/detail structured surfaces in later sprint tasks. | User Admin is the first migration reference vertical, but current code is a standalone route page. |
| `frontend/src/screens/briefing/BriefingPage.tsx` | retire/revise | Extract any useful attention/KPI/realtime ideas into stream items or dashboard surfaces. | "Mission Control" supervisory-console pattern is not the canonical functional-agent workstream shell. |
| `frontend/src/screens/decisions/DecisionQueuePage.tsx` | revise | Convert decision-card concepts into reusable decision/approval surface components. | Evidence/risk/policy/trace ideas match doctrine, but the implementation is a route page. |
| `frontend/src/screens/goals/GoalWorkbenchPage.tsx` | revise | Convert durable-goal/plan/approval ideas into workstream surfaces when a goal vertical is added. | Good AI-first concepts, stale page-first placement. |
| `frontend/src/screens/governance/GovernancePoliciesPage.tsx` | revise | Convert proposal/simulation/diff/commit-warning ideas into governance diff surfaces. | Useful governance semantics, but route/screen-first. |
| `frontend/src/screens/audit/AuditTraceExplorerPage.tsx` | revise | Convert trace filters/results into audit timeline/search surfaces. | Useful audit semantics, but route/screen-first. |
| `frontend/src/screens/profile/ProfilePreferencesPage.tsx` | revise | Convert to Access/Profile functional-agent surfaces and `/api/me` bootstrap state. | Current profile route lacks full AuthContext/authority indicator contract. |
| `frontend/src/design-system/**` | canonical / revise | Preserve generic primitives, then adapt naming/usage for shell, stream, surfaces, and actions. | Tokens, cards, data-state, forms, buttons, modal/drawer, KPI/status primitives are reusable. `PageHeader` should be renamed or deprecated once surfaces are primary. |
| `frontend/src/styles/**` | canonical / revise | Preserve tokenized style foundation; revise layout/component CSS around workstream shell regions. | Existing accessible focus, responsive, tokens, and semantic classes are useful, but layout names (`sidebar`, `content`, route shells) reflect stale shell. |
| `frontend/src/api/ApiClient.ts`, `HttpApiClient.ts`, `FixtureApiClient.ts` | canonical / revise | Preserve typed client pattern; add workstream/surface/capability-action client families in later tasks. | Client seam is valuable. Existing endpoint families (`goals`, `decisions`, `governance`, etc.) are not enough for `/api/me`, functional agents, surface envelopes, or capability actions. |
| `frontend/src/api/types.ts` | revise | Add/replace with `AuthContext`, functional-agent, workstream item, `SurfaceEnvelope`, `SurfaceAction`, and event contracts. | Current `MeResponse` is too small and there is no surface/action envelope. |
| `frontend/src/api/RealtimeClient.ts`, `FixtureRealtimeClient.ts` | canonical / revise | Preserve duplicate/stale/reconnect ideas; reshape events to `SurfaceEvent` / workstream event contracts. | Existing realtime seam demonstrates stale recovery and duplicate events, but topics are route/domain-family oriented. |
| `frontend/src/*.contract.test.mjs` | retire/rewrite progressively | Replace route/screen contract tests with workstream shell, functional-agent rail, composer, surface, action, deep-link, forbidden, and stale/realtime tests. | Current tests assert pages, screens, route shell strings, and seed console wording. |
| `frontend/.env.example` | canonical / preserve | Preserve as safe browser env template unless later WorkOS/frontend config needs expansion. | Tracked template only. |
| `frontend/.env.local` | local / ignore | Do not commit; verify ignored if future cleanup touches git settings. | Local environment file, untracked. |
| `frontend/node_modules/**` | local / ignore | Do not inventory as source or commit. | Installed dependencies, untracked/ignored; 66 MiB local artifact. |

## Inventory: generated Vite output in `src/main/resources/static-resources/`

| Path | Classification | Recommendation | Rationale |
|---|---|---|---|
| `src/main/resources/static-resources/index.html` | generated / revise via build | Replace through future `npm run build` after workstream shell migration; do not hand-edit. | Current title/description reference "AI-first DCA Seed Console" and points to current seed bundle. |
| `src/main/resources/static-resources/favicon.ico` | generated/static / preserve | Preserve unless frontend brand changes require replacement. | Shared favicon output. |
| `src/main/resources/static-resources/assets/index-B05vAgYp.js` | generated current | Replace via future frontend build. | Referenced by `index.html`; contains current route/page-first seed shell. |
| `src/main/resources/static-resources/assets/index-Bw1dY5G_.css` | generated current | Replace via future frontend build. | Referenced by `index.html`; contains current seed styles. |
| `src/main/resources/static-resources/assets/index-4AOLr12g.js` | retire | Delete in static cleanup after current build replacement. | Unreferenced Vite hash output. |
| `src/main/resources/static-resources/assets/index-B00riMG0.js` | retire | Delete in static cleanup after current build replacement. | Unreferenced Vite hash output. |
| `src/main/resources/static-resources/assets/index-B0E0K7o0.css` | retire | Delete in static cleanup after current build replacement. | Unreferenced Vite hash output. |
| `src/main/resources/static-resources/assets/index-B2fCJREw.js` | retire | Delete in static cleanup after current build replacement. | Unreferenced Vite hash output. |
| `src/main/resources/static-resources/assets/index-B8uHvJvA.css` | retire | Delete in static cleanup after current build replacement. | Unreferenced Vite hash output. |
| `src/main/resources/static-resources/assets/index-BDJwfDf5.js` | retire | Delete in static cleanup after current build replacement. | Unreferenced Vite hash output. |
| `src/main/resources/static-resources/assets/index-BIPWuAxb.css` | retire | Delete in static cleanup after current build replacement. | Unreferenced Vite hash output. |
| `src/main/resources/static-resources/assets/index-BNQpiHra.js` | retire | Delete in static cleanup after current build replacement. | Unreferenced Vite hash output. |
| `src/main/resources/static-resources/assets/index-Bbi7d-LX.js` | retire | Delete in static cleanup after current build replacement. | Unreferenced Vite hash output. |
| `src/main/resources/static-resources/assets/index-BkDcXXIH.css` | retire | Delete in static cleanup after current build replacement. | Unreferenced Vite hash output. |
| `src/main/resources/static-resources/assets/index-D9CUFPX1.js` | retire | Delete in static cleanup after current build replacement. | Unreferenced Vite hash output. |
| `src/main/resources/static-resources/assets/index-DQ-jGaQz.js` | retire | Delete in static cleanup after current build replacement. | Unreferenced Vite hash output. |
| `src/main/resources/static-resources/assets/index-Dvij60Uu.css` | retire | Delete in static cleanup after current build replacement. | Unreferenced Vite hash output. |
| `src/main/resources/static-resources/assets/index-JRxZ8ikc.css` | retire | Delete in static cleanup after current build replacement. | Unreferenced Vite hash output. |
| `src/main/resources/static-resources/assets/index-YYykwCqI.css` | retire | Delete in static cleanup after current build replacement. | Unreferenced Vite hash output. |

## Inventory: static reference examples

| Path | Classification | Recommendation | Rationale |
|---|---|---|---|
| `src/main/resources/static-resources/frontend-reference/**` | quarantine | Keep only as a lightweight endpoint/UI mechanics reference, not canonical generated SaaS UI. Consider relocating or labeling as legacy/narrow in later docs. | Purchase request dashboard is page/dashboard-first and not an agent workstream shell. |
| `src/main/resources/static-resources/supplies/**` | quarantine/revise | Preserve temporarily as an older supervision/decision-card example; do not promote as canonical. Mine decision/evidence/trace patterns if needed. | Demonstrates command center and decision card ideas, but uses page sections and sidebar navigation. |
| `src/main/resources/static-resources/web-ui/**` | quarantine | Keep as low-level Akka-hosted web UI endpoint example only. | Minimal static page shows route split mechanics, not secure AI-first workstream architecture. |
| `src/main/resources/static-resources/web-ui-sse/**` | quarantine | Keep as low-level SSE hosting example only. | Useful for browser SSE wiring, not canonical workstream surface events. |
| `src/main/resources/static-resources/web-ui-websocket/**` | quarantine | Keep as low-level WebSocket hosting example only. | Useful for WebSocket mechanics, not canonical workstream architecture. |

## Canonical material to carry forward

- React/Vite frontend project separation and `npm run build --outDir ../src/main/resources/static-resources --emptyOutDir false` integration.
- Typed API client seams and fixture/HTTP client split.
- Realtime client seam with duplicate-event and stale/reconnect simulation.
- Tokenized CSS, focus-visible handling, skip link, reduced-motion handling, responsive breakpoints.
- Reusable design primitives: buttons, cards, data state, form field, drawer/modal, KPI/status primitives.
- AI-first concepts embedded in current screens: evidence, risk, confidence, policy triggers, approval gates, trace links, role-change warnings, validation/conflict behavior.

## Stale patterns to remove from the canonical path

- `screens/**` and `*Page.tsx` as the root UI taxonomy.
- `RouteId`/`RouteShell` as the primary application model.
- Sidebar grouped by pages (`Work`, `Decisions`, `Governance`, `Audit`, `Admin`) instead of role-authorized functional agents.
- `aria-current="page"` and route labels as the main navigation semantics for authenticated work.
- Mission-control/supervisory-console copy as the canonical generated-app shell.
- Tests that assert route smoke scope rather than workstream shell and surface contracts.
- Static example pages that appear alongside the generated app without legacy/narrow-reference labeling.
- Accumulated unreferenced hashed build assets.

## Recommended migration sequence alignment

1. **TASK-WUI-01-002** should define `frontend/src/workstream/**` source layout and name routes strictly as deep-link implementation details.
2. **TASK-WUI-02-001** should add shared types/fixtures for `/api/me`, `AuthContext`, functional agents, workstream items, surface envelopes, capability actions, and surface events before UI components are rewritten.
3. **TASK-WUI-02-002 through TASK-WUI-02-004** should build reusable shell, rail, composer, stream, surface, and action components without depending on `frontend/src/screens/**`.
4. **TASK-WUI-03-001** should replace `main.tsx` route/page shell with the reusable workstream shell.
5. **TASK-WUI-03-002 and later** should rebuild deep links and User Admin reference vertical as surfaces.
6. After a successful workstream build, delete stale generated hash assets and either remove or explicitly quarantine static demo directories.

## Verification performed

- Listed tracked frontend and static-resource files with `git ls-files frontend src/main/resources/static-resources`.
- Listed current static output with `find src/main/resources/static-resources -maxdepth 4 -type f`.
- Checked Vite asset references; only `index-B05vAgYp.js` and `index-Bw1dY5G_.css` are referenced by `src/main/resources/static-resources/index.html`.
- Searched frontend/static source for stale page/route/screen/workstream/surface/auth/capability terminology.
- Confirmed current repository changes are limited to this inventory and the queue status update for the selected task.
