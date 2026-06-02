# Legacy and Reusable Asset Inventory

## Purpose

Classify existing executable and reference assets before creating the canonical AI-first SaaS starter app template. This inventory prevents stale DCA, conventional CRUD, static UI, or mechanics-only examples from becoming implicit full-core starter guidance.

Classification key:

- **reuse**: can be copied or adapted directly into the starter template after path/package normalization.
- **migrate**: contains starter-relevant semantics but must be reshaped before becoming template code.
- **mechanics-only**: useful to confirm Akka/frontend API mechanics, not canonical full-core structure.
- **quarantine**: keep as non-canonical reference until replacement exists; do not point new starter work at it as the target.
- **archive**: preserve as provenance/input only.
- **delete-later**: generated or superseded asset that can be removed after the starter template replaces it and tests/docs no longer depend on it.

## Canonical reuse set

| Asset | Classification | Starter use | Notes |
| --- | --- | --- | --- |
| `templates/ai-first-saas-starter/app-description/**` | reuse | Primary description/source contract for the starter app scope, foundation capability families, workstreams, UI split, and realization maps. | This is the canonical generated-SaaS app-description reference. Keep it aligned with the starter; do not replace it with DCA or purchase-request examples. |
| `frontend/src/workstream/**` | reuse | Primary React/Vite/TypeScript workstream shell module set: functional-agent rail, main workstream, composer, structured surfaces, actions, realtime, fixtures, and typed contracts. | Copy/adapt into the starter template source once the template path is chosen. Preserve backend-authoritative capability/action semantics. |
| `frontend/src/api/WorkstreamApiClient.ts`, `frontend/src/api/WorkstreamRealtimeClient.ts`, `frontend/src/api/FixtureWorkstreamApiClient.ts`, `frontend/src/api/FixtureWorkstreamRealtimeClient.ts` | reuse | Browser API/realtime seam for `/api/me`, workstream bootstrap, capability actions, and event streams. | Starter should replace fixtures with real backend clients while retaining fixture/test seams. |
| `frontend/src/workstream-*.contract.test.mjs`, especially `workstream-user-admin-vertical.contract.test.mjs` and `workstream-agent-admin-vertical.contract.test.mjs` | reuse | Contract coverage for shell, surfaces, actions, user-admin vertical, agent-admin vertical, deep links, stale/realtime states, and browser-safe DTOs. | Promote these patterns into starter frontend tests. |
| `frontend/src/styles/**` | reuse | Token/base/layout/component CSS foundation for the workstream shell. | Retain workstream naming. Avoid page-first route/screen taxonomy. |
| `frontend/src/design-system/Button.tsx`, `Card.tsx`, `CommandStrip.tsx`, `DataState.tsx`, `Drawer.tsx`, `FormField.tsx`, `IconChip.tsx`, `KpiCard.tsx`, `Modal.tsx`, `StatusPill.tsx` | reuse | Generic UI primitives for structured surfaces. | `PageHeader.tsx` is page-taxonomy legacy; migrate or avoid for starter canonical UI. |
| `docs/workstream-ui-reference-architecture.md` | reuse | Target architecture and frontend layout contract. | Treat as the canonical UI implementation reference with `frontend/src/workstream/**`. |

## Assets to migrate into starter code

| Asset | Classification | Starter-relevant content | Required migration |
| --- | --- | --- | --- |
| `src/main/java/com/example/domain/security/**` | migrate | Account status, local account/profile, role assignment, tenant directory, admin audit vocabulary, validation patterns. | Rename from `com.example`, expand to full-core `Account`, `UserProfile`, `UserSettings`, `Tenant`, `Customer`, `Membership`, `Role`, `Capability`, selected `AuthContext`, support access, and subscription/billing-safe owner boundary. |
| `src/main/java/com/example/application/security/**` | migrate | Akka entities/bootstrap for local account, tenant directory, admin audit. | Isolate into starter package; add missing views/workflows/authorization placements, invitation lifecycle, `/api/me` completeness, disabled/no-membership/forbidden paths, and tenant-isolation tests. |
| `src/main/java/com/example/api/security/**` | migrate | Existing `/api/me`, admin endpoints, tenant/customer admin seams, DCA starter frontend route. | Keep only security/admin API semantics that satisfy starter full-core scope. Remove DCA-specific naming and make browser DTOs safe by contract. |
| `src/test/java/com/example/application/security/**` | migrate | Acceptance and endpoint tests for seed security/admin slice. | Convert into starter foundation tests for `/api/me`, memberships, role/capability denial, tenant/customer isolation, disabled users, support access, admin audit, and frontend secret boundaries. |
| `src/main/java/com/example/security/**` | migrate | `AuthContext`, authorization service, WorkOS claim extraction, required environment, invitation email seams. | Generalize into starter security package; add Resend/local captured outbox boundary, no raw token exposure, and capability-aware server-side checks for every protected surface. |
| `src/main/java/com/example/domain/agentfoundation/**` | migrate | Governed runtime records: agent definitions, prompts, skills, manifests, tool boundaries, prompt/skill/work traces, behavior proposals, evaluation/improvement vocabulary. | Reshape from reference-prefixed domain objects into starter tenant-scoped managed-agent foundation with idempotent seed import and upgrade semantics. |
| `src/main/java/com/example/application/agentfoundation/**` | migrate | Runtime resolver, prompt assembler, skill read authorizer, seed loader, behavior editor/review/evaluation services, trace sink. | Convert into full-core starter backend components and tests with deterministic runtime/test-console behavior and strict tool-boundary enforcement. |
| `src/test/java/com/example/application/agentfoundation/**` | migrate | Unit and integration coverage for managed agent behavior governance. | Promote into starter governed-agent foundation tests, adding tenant/customer isolation, approval gates, trace redaction, and no-production-side-effect test-console paths. |
| `src/main/java/com/example/api/ManagedReferenceAgentEndpoint.java`, `ActivityAgentEndpoint.java`, `ActivityPromptEndpoint.java`, `PromptTemplateHistoryEndpoint.java` | migrate | Endpoint mechanics for managed/reference agents, prompt-backed agents, and prompt history. | Fold only the governed-agent/admin pieces into starter Agent Admin APIs; keep generic activity examples mechanics-only. |
| `src/test/java/com/example/application/Managed*`, `Activity*`, `PromptTemplateHistory*` test families | migrate | Agent endpoint/test-console and prompt-history patterns. | Split starter governed-agent tests from generic agent mechanics. |
| `frontend/src/main.tsx` | migrate | Integrated shell wiring, fixture bootstrap, deep links, mode preference, realtime subscription, capability action feedback. | Move into starter frontend entrypoint after replacing fixtures with starter API clients and production bootstrap/error states. |

## Mechanics-only Akka examples

| Asset family | Classification | Use | Not canonical because |
| --- | --- | --- | --- |
| Shopping cart family: `ShoppingCart*`, cart gRPC/MCP endpoints, cart views/consumers/tests | mechanics-only | Entity, view, consumer, gRPC, MCP, topic, and endpoint examples. | Conventional cart domain; not full-core SaaS foundation or agent workstream app. |
| Draft cart / expiring cart family | mechanics-only | KVE/entity lifecycle, view stream, checkout consumer, expiring session patterns. | Useful substrate mechanics only. |
| Order and purchase order families | mechanics-only | Event-sourced command validation and business logic patterns. | Conventional domain examples; do not use for full-core starter scope. |
| Approval, approval deadline, review, refund approval, supervised export, transfer workflows | mechanics-only | Workflow, pause/resume, deadlines, approval, consumer, and evidence-view mechanics. | Can inform starter invitation/review/approval workflows but must not define product model. |
| Reminder job and ticket reservation timed-action examples | mechanics-only | Timed action/entity mechanics. | Starter needs invitation/support-access expiry and governance schedules instead. |
| Agent examples outside `agentfoundation`: `Activity*`, `Weather*`, `PlannerAgent`, `SelectorAgent`, `DocumentAnalysisAgent`, `SessionMemory*`, `DynamicAgentTeam*`, `RemoteShoppingCartAgent`, guardrails/evaluators | mechanics-only | Agent call, tools, structured response, guardrail, memory, team orchestration, streaming, evaluation patterns. | Not governed runtime SaaS admin foundation by themselves. |
| HTTP endpoint examples: greeting, proxy, request headers, low-level HTTP, internal status, secure greeting, SSE/WebSocket, static web UI | mechanics-only | Endpoint, JWT/request-context, service ACL, SSE, WebSocket, static asset delivery mechanics. | Starter APIs must be capability/AuthContext-driven and workstream-oriented. |
| gRPC and MCP endpoint examples | mechanics-only | Protocol exposure mechanics. | Full-core starter is browser/API first; gRPC/MCP remain optional extension references. |
| `src/main/resources/static-resources/web-ui/**`, `web-ui-sse/**`, `web-ui-websocket/**` | mechanics-only | Minimal static hosting, SSE, and WebSocket browser delivery. | Not React/Vite workstream shell; do not use as generated SaaS UI structure. |
| `src/main/resources/static-resources/frontend-reference/**` | mechanics-only | Earlier frontend API/form/render/state mechanics. | Superseded by `frontend/src/workstream/**` for canonical generated UI. |

## Domain-rich references to quarantine from starter canon

| Asset | Classification | Keep for | Do not use as |
| --- | --- | --- | --- |
| `docs/examples/ai-first-dca-app-description/**` | quarantine | Rich vertical reference for delegated work, DCA lifecycle, policies, decision cards, traces, outcomes, and supplies slice decomposition. | Canonical starter app structure. Its DCA-specific consolidated UI and domain capabilities must not replace the starter core app-description split. |
| `src/main/java/com/example/domain/supplies/**`, `application/supplies/**`, `api/supplies/**` | quarantine | Runnable AI-first vertical slice showing decisions, policy/evidence, workflow gates, traces, views, endpoint, timed action, and UI surface. | Full-core starter foundation. It is DCA/supplies-specific and lacks the complete SaaS/user/admin/agent-governance foundation. |
| `src/test/java/com/example/application/supplies/**`, `src/test/java/com/example/domain/supplies/**` | quarantine | AI-first acceptance/evaluation test patterns: authority boundaries, decision cards, trace completeness, idempotency, pause/resume. | Starter acceptance matrix by itself; adapt the patterns to core foundation domains. |
| `src/main/resources/static-resources/supplies/**` | quarantine | Minimal packaged AI-first decision surface mechanics. | Canonical React/Vite workstream UI. Replace with workstream shell surfaces in starter. |
| `SupplyAutopilotUiEndpoint.java` and related supplies UI tests | quarantine | Akka static route mechanics for a vertical decision UI. | Starter UI route model. |
| DCA seed security/frontend route: `DcaSeedFrontendEndpoint.java`, `DcaSeedFrontendEndpointIntegrationTest.java`, `DcaSeedSecurityAcceptanceIntegrationTest.java` | quarantine | Security acceptance ideas from the seed/DCA transition. | Final starter naming or canonical frontend entrypoint. Rename/rework during migration. |

## App-description and planning examples

| Asset | Classification | Use | Notes |
| --- | --- | --- | --- |
| `docs/examples/core-ai-first-saas-input/**` | archive | Provenance and PRD/input sequence for the progressive core seed. | Input artifacts only; do not treat as maintained app-description tree or starter source code. |
| `docs/examples/purchase-request-app-description/**` | mechanics-only | Compact app-description mechanics, traceability maps, readiness/change summary examples. | Low-agentic approval workflow; not generated AI-first SaaS target architecture. |
| `docs/examples/purchase-request-prd.md`, `purchase-request-solution-plan.md`, `purchase-request-module-sprint-plan.md`, `purchase-request-pending-tasks.md` | mechanics-only | Planning/spec/backlog queue examples. | Conventional planning references only. |
| `docs/examples/ai-first-app-description-gaps.md` | archive | Historical gap notes. | Superseded by current seed/starter scope docs for template planning. |

## Frontend legacy and generated build assets

| Asset | Classification | Decision | Notes |
| --- | --- | --- | --- |
| `frontend/src/screens/**` if present or reintroduced | delete-later | Remove from canonical template once equivalent workstream surfaces cover the behavior. | Page/screen taxonomy is superseded by functional agents, workstreams, and structured surfaces. |
| `frontend/src/design-system/PageHeader.tsx` | delete-later | Replace with shell/surface headers or make it explicitly generic before copying into starter. | Page-first naming conflicts with canonical workstream decomposition. |
| `src/main/resources/static-resources/index.html`, `assets/index-*.js`, `assets/index-*.css`, `favicon.ico` | delete-later | Treat as generated frontend build output. Regenerate from starter frontend source instead of hand-editing. | Safe to remove/replace after starter template owns a clean build-output path. |
| `frontend/src/api/ApiClient.ts`, `FixtureApiClient.ts`, `HttpApiClient.ts`, `RealtimeClient.ts`, `FixtureRealtimeClient.ts`, `types.ts` | migrate | Generic API/realtime seams can remain support code if workstream-specific clients compose them. | Ensure starter public contracts are `/api/me`, governed capabilities, surfaces, and streams rather than old page routes. |
| Older frontend contract tests: `mission-control`, `goal-decision-flows`, `governance-audit-admin-profile` | migrate | Preserve useful assertions for governance, audit, admin/profile, decisions, and quality. | Rewrite around functional agents/surfaces if any route/page assumptions remain. |

## Starter quarantine application status

Status after starter scaffold packaging: **canonical routing cleanup applied**.

Use this rule for docs and skills:

- canonical full-core generated-app implementation guidance: `templates/ai-first-saas-starter/**` in this repository and `resources/templates/ai-first-saas-starter/**` in installed packs
- canonical UI architecture and reusable frontend patterns: `docs/workstream-ui-reference-architecture.md` plus `frontend/src/workstream/**` / installed `resources/examples/frontend/**`
- mechanics-only Akka examples: keep for focused component semantics and tests after the solution shape is known
- DCA/supplies assets: keep quarantined as domain-rich vertical AI-first references; cite only for delegated decision, policy, trace, workflow, outcome, and acceptance-test mechanics
- purchase-request assets: keep as description/planning mechanics references only
- static UI assets under `src/main/resources/static-resources/**`: treat as generated output, endpoint-delivery mechanics, or quarantined vertical UI; never as canonical generated SaaS frontend source

No source asset is removed by this cleanup pass. Deletion remains `delete-later` until replacement routes, build output, tests, and docs no longer depend on the asset.

## Must-not-reference-as-canonical list

After the starter template replaces current transitional references, do not cite these as canonical full-core guidance:

- `docs/examples/ai-first-dca-app-description/**` for starter structure; cite only as a domain-rich vertical extension.
- `docs/examples/purchase-request-app-description/**` or purchase-request planning files for AI-first SaaS architecture; cite only for description/planning mechanics.
- `src/main/java/com/example/**` broad root as starter code; cite only specific migrated starter packages once copied into the chosen template path.
- `src/main/resources/static-resources/frontend-reference/**`, `web-ui/**`, `web-ui-sse/**`, `web-ui-websocket/**`, and `supplies/**` as canonical UI; cite only as mechanics or quarantined vertical examples.
- `frontend/src/screens/**` and page-first route tests as generated SaaS UI taxonomy.
- Shopping cart/order/purchase-order/tutorial-style examples as starter domain or security model.

## Migration priorities for the starter template

1. Reuse `templates/ai-first-saas-starter/app-description/**` as the app-description source contract and `frontend/src/workstream/**` as the UI source reference.
2. Migrate security/admin backend assets into a clean starter package with complete `/api/me`, AuthContext, tenant/customer/membership/role/capability, invitation, audit, and authorization coverage.
3. Migrate `agentfoundation` assets into the governed runtime agent foundation with tenant-scoped records, seed import, prompt assembly, `readSkill`, tool-boundary checks, traces, and Agent Admin APIs.
4. Adapt frontend workstream fixtures/tests to real starter backend DTOs and keep fixture clients for deterministic local/dev/test behavior.
5. Quarantine DCA/supplies executable assets as vertical AI-first examples until full-core starter coverage exists, then update docs to point full-core readers to the starter instead.
6. Delete or archive generated/static/page-first assets only after replacement routes, build output, tests, and docs are in place.
