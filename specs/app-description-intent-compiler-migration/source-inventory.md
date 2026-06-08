# Source Inventory: App-description Intent-Compiler Migration

## Purpose

This inventory gives later current-intent graph reconstruction tasks enough evidence to rebuild `app-description/` without guessing. It separates reusable foundation doctrine from this repository's secure multi-tenant AI-first SaaS core starter commitments, stale exclusions, and drift candidates.

## Source classes

| Source class | Evidence paths | Use during migration |
|---|---|---|
| Intent compiler doctrine | `.agents/skills/docs/intent-compiler.md`, `.agents/skills/docs/current-intent-model.md`, `.agents/skills/docs/intent-to-realization-flow.md` | Target graph shape, workstream centrality, traceability spine, and realization-validation rules. |
| Foundation architecture doctrine | `.agents/skills/docs/ai-first-saas-application-architecture.md`, `.agents/skills/docs/capability-first-backend-architecture.md` | Reusable doctrine to reference, not duplicate, for secure SaaS, managed agents, governed tools, workstream shell, capabilities, and runtime completion. |
| Temporary legacy app-description archive | `specs/app-description-intent-compiler-migration/archive/legacy-app-description/**`, manifest at `specs/app-description-intent-compiler-migration/archive/source-manifest.md` | Migration provenance only. Mine for current-starter candidates, then scrub dependencies before completion. |
| Current root app-description | `app-description/**` | Active legacy-shape content until graph replacement. Treat as evidence, not final current-intent shape. |
| Backend implementation | `src/main/java/ai/first/**` | Concrete package layout, protected API/component names, durable state, service, agent, trace, and workstream evidence. |
| Backend tests | `src/test/java/ai/first/**` | Validation evidence and local/test-scope readiness boundaries. Test-only fixtures/demos are not normal runtime evidence. |
| Frontend implementation | `frontend/src/**` | Canonical workstream shell, API clients, workstream surfaces, contract tests, and legacy page-fixture exclusions. |
| Active specs/docs | `specs/full-core-saas-readiness/**`, `specs/web-ui-design/**`, `specs/workstream-visual-sessions/**`, `docs/**`, selected root specs | Readiness evidence, explicit blockers/deferred scope, UI design evidence, extension/merge rules. Archived specs under `specs/archive/**` are historical unless explicitly cited by active specs. |

## Reusable foundation references to cite, not duplicate

Later `app-description/` nodes should reference these doctrine areas as reusable foundation commitments and capture only this starter's selected bindings:

- Secure SaaS foundation: WorkOS/AuthKit auth boundary, local authorization, tenant/customer scoping, memberships/roles/capabilities, invitation onboarding, `/api/me`, backend authorization, audit/traces, tenant isolation, security tests.
- AI-first managed-agent foundation: `AgentDefinition`, governed prompts/skills/references, manifests, tool boundaries, loader tools, model policy, prompt/skill/reference/work traces, default seed import, behavior-editing governance.
- Agent workstream application model: role-authorized functional agents, continuous workstream panel, persistent composer, structured surfaces, shell request resolution, context/authority indicators, safe denial/recovery states.
- Capability-first backend model: capability → governed-tool → exposure channel → Akka substrate. Browser tools, agent tools, workflow/timer/consumer/internal tools, APIs, views, and component methods are exposure choices, not the root app model.
- Runtime completion doctrine: real local Akka/API/UI paths and provider fail-closed behavior are required for feature readiness; deterministic/demo/model-less normal runtime substitutes are not acceptable.

## Core starter current intent candidates

### App and domain boundary

- Root app is the canonical runnable secure AI-first SMB SaaS core starter under package `ai.first`.
- Recommended current-intent domain id remains `core-starter` unless the graph skeleton task chooses a better stable id.
- Built-in foundation/core app packages are distinct from user-owned extension zones:
  - foundation packages: `src/main/java/ai/first/domain/foundation/**`, `src/main/java/ai/first/application/foundation/**`, `src/main/java/ai/first/api/foundation/**`;
  - built-in core starter packages: `src/main/java/ai/first/domain/coreapp/**`, `src/main/java/ai/first/application/coreapp/**`, `src/main/java/ai/first/api/coreapp/**`;
  - extension seams: `src/main/java/ai/first/**/business/**`, `frontend/src/extensions/<domain>/`, `app-description/extensions/<domain>/`, `specs/extensions/<domain>/`, `docs/extensions/<domain>/`.

### Five core workstreams

| Workstream | Current evidence | Current-intent capture candidates |
|---|---|---|
| My Account | `src/main/java/ai/first/application/coreapp/myaccount/**`, `src/main/java/ai/first/domain/coreapp/myaccount/**`, `frontend/src/workstream-my-account-vertical.contract.test.mjs`, `frontend/src/workstream/**`, `specs/full-core-saas-readiness/full-core-runtime-smoke.md` | Signed-in user tile entry rather than top rail duplication; personal attention digest; notification center; profile/settings/access context panels; digest/export requests with approval/redaction boundaries. |
| User Admin | `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java`, `src/main/java/ai/first/application/coreapp/useradmin/**`, `src/main/java/ai/first/application/foundation/identity/**`, `src/main/java/ai/first/application/foundation/invitation/**`, `src/main/java/ai/first/application/foundation/email/**`, `frontend/src/workstream-user-admin-vertical.contract.test.mjs`, `specs/full-core-saas-readiness/user-admin-surfaces-validation.md` | Users, memberships, roles, invitations, support access, access review, identity relink, enterprise identity validation, admin audit, Resend/captured-outbox boundary, idempotency, tenant/customer scope, denial/audit behavior. |
| Agent Admin | `src/main/java/ai/first/application/foundation/agent/**`, `src/main/java/ai/first/domain/foundation/agent/**`, `src/main/resources/agent-behavior-seeds/starter-v1/**`, `frontend/src/workstream-agent-admin-vertical.contract.test.mjs`, `specs/full-core-saas-readiness/managed-agent-foundation-validation.md` | Managed agent catalog/detail, prompt/skill/reference documents and versions, manifests, tool boundaries, model config, behavior proposals, seed imports, activation/rollback, loader and tool-boundary denials, prompt/skill/reference/model/work traces. |
| Audit/Trace | `src/main/java/ai/first/application/foundation/audit/**`, `src/main/java/ai/first/application/coreapp/audit/**`, `frontend/src/workstream-audit-trace-vertical.contract.test.mjs`, `specs/full-core-saas-readiness/audit-governance-validation.md` | Scoped audit search/detail/timeline, investigation notes, redaction/export denial evidence, audit summary worker, trace links across workstream items and agent/tool/model activity. |
| Governance/Policy | `src/main/java/ai/first/application/foundation/governance/**`, `src/main/java/ai/first/application/coreapp/governance/**`, `src/main/java/ai/first/domain/foundation/governance/**`, `frontend/src/workstream-governance-policy-vertical.contract.test.mjs`, `specs/full-core-saas-readiness/audit-governance-validation.md` | Policy proposal/simulation/decision/activation/rollback/outcome notes, impact worker, approval gates, policy evidence and traceability to capabilities and governed agent behavior. |

### Cross-cutting implementation evidence

- Protected HTTP/API surface:
  - `/api/me`: `src/main/java/ai/first/api/foundation/security/MeEndpoint.java` with `@JWT` and browser-safe current account/context response.
  - Workstream shell APIs: `src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java` for bootstrap, functional agents, stream items, surfaces, actions, shell requests, messages, invitation acceptance, and events.
  - Admin APIs: `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java` for user/admin, invitation, membership, support access, access review, enterprise identity, digest/export, and audit events.
  - Frontend hosting: `src/main/java/ai/first/api/coreapp/workstream/StarterFrontendEndpoint.java` serves the React/Vite build.
- Durable foundation components and repositories:
  - Identity and invitation: `DurableIdentityRepositoryEntity`, `DurableInvitationRepositoryEntity`, `InvitationLifecycleHistoryEntity`, `InvitationView`.
  - Agent behavior/runtime: `AgentDefinitionEntity`, `PromptDocumentEntity`, `SkillDocumentEntity`, `ReferenceDocumentEntity`, manifest/boundary entities and views, `AgentRuntimeTraceEntity`, `AgentRuntimeTraceView`, `WorkstreamRuntimeAgent`.
  - Attention/workstream/notifications: `DurableAttentionRepositoryEntity`, `DurableWorkstreamLogEntity`, `DurableWorkstreamEventRepositoryEntity`, `WorkstreamEventAttentionConsumer`, notification repository/service.
  - Audit/governance: `AdminAuditView`, `AuditTraceService`, `DurableGovernancePolicyRepositoryEntity`, `GovernancePolicyService`.
- Frontend canonical runtime area:
  - `frontend/src/main.tsx` renders the WorkOS/AuthKit-gated workstream shell.
  - `frontend/src/api/**` owns browser-safe API clients and DTOs.
  - `frontend/src/workstream/**` is the canonical shell/rail/composer/stream/surface/action/realtime/type implementation.
  - `frontend/src/workstream/surfaces/**` includes dashboard, decision, audit timeline, governance diff, list/search, markdown response, notification center, outcome, system message, workflow status, and detail/edit surfaces.
- Test evidence spans backend service/entity/view/agent/runtime tests, frontend contract tests for workstream shell/surfaces/actions/auth boundaries, and active readiness evidence under `specs/full-core-saas-readiness/**`.

## Stale, legacy, or exclusion candidates

- `specs/app-description-intent-compiler-migration/archive/legacy-app-description/**` is temporary migration provenance only and must not be cited as final authority.
- Current root `app-description/**` uses the legacy numbered taxonomy (`00-system`, `10-capabilities`, `12-workstreams`, etc.). Its content may inform reconstruction, but the final target is the current-intent graph shape from `current-intent-model.md`.
- `frontend/src/screens/**` files are legacy/page-style fixture/reference artifacts according to current app-description and tests. They are not canonical runtime-completion evidence unless a future task explicitly migrates them into the workstream shell.
- `frontend/src/__tests__/fixtures/**` and test-only `LocalDemo*` repositories/providers support tests only; do not use them as normal runtime readiness evidence.
- Archived specs under `specs/archive/**` are historical unless active specs explicitly reference them. Prefer `specs/full-core-saas-readiness/**`, current source, and current frontend tests for current-state evidence.
- Billing implementation and timer-backed invitation reminders are deferred by active readiness docs; do not add them as current completed starter behavior without a new accepted task.

## Drift and pending-question candidates

| Candidate | Evidence | Migration handling |
|---|---|---|
| Live WorkOS/AuthKit provider smoke remains blocked | `specs/full-core-saas-readiness/live-workos-authkit-provider-smoke.md`, `full-core-runtime-smoke.md` | Current intent can require WorkOS/AuthKit as supported auth boundary, but readiness mappings must distinguish local fail-closed validation from live provider proof. |
| Live Resend and model provider smokes have passed but secrets are external | `specs/full-core-saas-readiness/live-resend-provider-smoke.md`, `live-model-provider-smoke.md` | Capture provider-backed readiness as evidence without recording secrets; keep fail-closed behavior in current intent. |
| Billing and timer-reminder scope deferred | `specs/full-core-saas-readiness/billing-timer-reminder-scope-decision.md`, `full-core-readiness-verification.md` | Exclude from core completed behavior; allow future app-specific/domain-specific extension tasks. |
| Legacy page components remain in source | `frontend/src/screens/**`, `app-description/55-ui/routes-and-deep-links.md`, `frontend/src/mission-control.contract.test.mjs` | Classify as test/reference fixtures. Do not describe as primary UI architecture. Queue cleanup only if future reconciliation requires it. |
| Admin route integration fixture assumptions | `specs/full-core-saas-readiness/user-admin-surfaces-validation.md` notes direct `AdminEndpointIntegrationTest` seeded-data assumptions | Current graph should map User Admin completion primarily to service/workstream/action path and note route-level fixture assumptions as validation nuance. |
| Active specs still cite legacy app-description paths | `specs/full-core-saas-readiness/pending-tasks.md` cites paths like `app-description/12-workstreams/surface-contracts/...` | Reconcile in `TASK-ADICM-03-001` after new graph nodes exist. |
| Root app-description duplicates reusable doctrine | legacy/current `app-description/40-auth-security/**`, `15-operating-model/**`, `10-capabilities/**` | Rebuild to reference skills-pack foundation docs and capture only starter-specific commitments/bindings. |

## Suggested current-intent graph seeds

Later graph tasks can populate these nodes using this evidence:

- `app-description/app.md`: secure multi-tenant AI-first SaaS core starter objective, extension seams, non-goals, local/runtime validation posture.
- `app-description/global/actors/`: authenticated member, tenant admin, customer admin, SaaS owner/support operator, managed agent/runtime worker, auditor, policy owner/approver.
- `app-description/global/roles/`: owner/admin/member/auditor/policy owner/support-access roles aligned to `FoundationRole` and active readiness docs.
- `app-description/global/policies/`: tenant isolation, backend authorization, provider fail-closed, secret boundary, governed agent behavior, approval/decision, audit/redaction.
- `app-description/global/surfaces/`: workstream shell, markdown response, system message, dashboard, list/search, detail/edit, decision card, audit timeline, governance diff, notification center, outcome, workflow status.
- `app-description/global/agents/`: My Account, User Admin, Agent Admin, Audit/Trace, Governance/Policy functional agents plus bounded internal workers for access review, digest, prompt risk, audit summary, and governance impact where implementation exists.
- `app-description/global/tools/`: governed browser/agent/internal tools for `/api/me`, workstream actions, invitation lifecycle, membership/role/status changes, support access, access review, agent behavior reads/loads/proposals, audit search/export requests, policy proposal/simulation/activation.
- `app-description/global/traces/`: admin audit, workstream event/log trace, prompt/skill/reference/model/tool/agent work traces, denial/data-access/provider traces.
- `app-description/domains/core-starter/`: core starter domain, capabilities, data-state, five workstreams with access/behavior/surfaces/agents/tools/policies/traces/tests/realization.

## Coverage proof commands used by this task

The following focused inventory commands were used to prove coverage across the requested source classes:

```bash
find src/main/java/ai/first -type f | sort
find src/test/java/ai/first -type f | sort
find frontend/src -type f | sort
find docs -maxdepth 3 -type f | sort
find specs -path 'specs/archive' -prune -o -path 'specs/app-description-intent-compiler-migration/archive/legacy-app-description' -prune -o -type f | sort | head -300
find specs/app-description-intent-compiler-migration/archive/legacy-app-description -maxdepth 2 -type f | sort
find src/main/java/ai/first -type f | sed 's#src/main/java/ai/first/##; s#/[^/]*$##' | sort | uniq -c
find src/test/java/ai/first -type f | sed 's#src/test/java/ai/first/##; s#/[^/]*$##' | sort | uniq -c
find frontend/src/workstream -type f | sed 's#frontend/src/workstream/##; s#/[^/]*$##' | sort | uniq -c
rg -n "my-account|user-admin|agent-admin|audit-trace|governance-policy|My Account|User Admin|Agent Admin|Audit/Trace|Governance/Policy" src/main/java src/test/java frontend/src specs/full-core-saas-readiness docs app-description | head -200
rg -n "TODO|FIXME|demo|mock|fixture|legacy|not implemented|fail closed|fail-closed|provider" src/main/java src/test/java frontend/src specs/full-core-saas-readiness docs app-description | head -160
```
