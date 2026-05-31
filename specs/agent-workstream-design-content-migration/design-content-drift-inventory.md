# Agent Workstream Design-Content Drift Inventory

## Scope

Inventory task: `TASK-AWDD-01-001`.

This file records design-content drift to guide later migration tasks. It is intentionally an inventory only: no doctrine, skill, example, readiness, generation, or web UI routing content is rewritten in this task.

Follow-on target rules for applying this inventory are now captured in:
- `specs/agent-workstream-design-content-migration/canonical-content-targets.md`
- `docs/agent-workstream-design-review-checklist.md`

Canonical target model used for classification:

```text
secure SaaS foundation
→ functional/context-area agents
→ durable workstreams
→ typed structured surfaces
→ governed backend capabilities
→ horizontal Akka implementation
```

Ownership target:

- `12-workstreams/` owns the application model: functional agents, internal agents, durable workstreams, surface index, surface contracts, capability/action mappings, trace/test semantics.
- `55-ui/` owns browser realization: shell, rail, panel/composer rendering, routes/deep links, interactions/forms, frontend API contracts, state/realtime, accessibility/responsive behavior, style.
- `frontend/src/workstream/**` and the User Admin vertical contract test are the canonical generated-SaaS frontend reference.
- Legacy `frontend/src/screens/**`, page-route tests, standalone static resource examples, and consolidated historical app-description UI files may remain only as mechanics/legacy references unless migrated.

## Classification key

- **align now** — should be changed by an already queued near-term task.
- **label as legacy/mechanics-only** — can remain if clearly quarantined as not canonical generated-SaaS structure.
- **candidate for later task** — probably useful cleanup, but not required for the first doctrine/skill alignment tasks.
- **leave unchanged** — already aligned or acceptable supporting mechanics.

## High-priority findings

| Area | Files | Finding | Classification | Suggested follow-up |
|---|---|---|---|---|
| Canonical `55-ui/` file set | `docs/internal-app-description-architecture.md`, `skills/app-description-bootstrap/SKILL.md`, `skills/app-description-ui/SKILL.md`, `skills/app-description-functional-agent-modeling/SKILL.md`, `skills/app-description-surface-modeling/SKILL.md` | The file sets overlap but are not identical. Internal architecture includes `workstream-panel-and-composer.md`, `personas-and-journeys.md`, `interactions-and-forms.md`, `frontend-api-contracts.md`, `states-and-realtime.md`, `accessibility-and-responsive.md`; bootstrap omits several and adds `secure-shell-and-context-selection.md` / `admin-and-audit-surfaces.md`; UI skill includes `ai-first-surfaces.md`; focused modeling skills list only targeted subsets. | align now | `TASK-AWDD-02-002`: define one canonical full-core file set plus allowed minimal/deferred subsets. |
| `12-workstreams/` vs `55-ui/` ownership | `docs/internal-app-description-architecture.md`, `skills/app-description-ui/SKILL.md`, `skills/app-description-functional-agent-modeling/SKILL.md`, `skills/app-description-surface-modeling/SKILL.md` | Most guidance is aligned, but ownership is distributed across several files and can require readers to infer that `12-workstreams/` is authoritative for application meaning while `55-ui/` is browser realization. | align now | `TASK-AWDD-02-001` and `TASK-AWDD-02-002`: make first mention explicit and repeat compactly in skill targets. |
| Terminology drift | `docs/agent-workstream-application-architecture.md`, `docs/ai-first-saas-application-architecture.md`, `skills/agent-workstream-apps/SKILL.md`, `skills/app-description-functional-agent-modeling/SKILL.md`, `skills/app-description-ui/SKILL.md`, `docs/workstream-ui-reference-architecture.md` | Current terms include `functional agents`, `context-area agents`, `left-rail functional agents`, `functional-agent rail`, and `work areas`. The canonical doctrine defines `Functional agent / context-area agent`, but not every downstream first mention repeats the alias. | align now | `TASK-AWDD-02-001`: use `functional/context-area agent` on first mention where useful, then `functional agent`. |
| Surface-first guidance | `skills/ai-first-saas-ui-surfaces/SKILL.md` | The skill selects Goal-to-Execution, Mission Control, Decision Card, Governance, Digest, and Audit surfaces directly. It does not yet require each selected surface to name owning/reusable functional agents and workstream placement before route/API/component planning. | align now | `TASK-AWDD-03-001`: require functional-agent/workstream placement for every selected surface. |
| DCA example UI shape | `docs/examples/ai-first-dca-app-description/app-description/55-ui/README.md`, `docs/examples/ai-first-dca-app-description/app-description/55-ui/ui-surfaces.md`, many DCA capability files linking to `../55-ui/ui-surfaces.md` | DCA keeps a consolidated `ui-surfaces.md` and lacks the seed app's split `12-workstreams/` layer and split `55-ui/` file set. The README says future downstream realization may split it, but it still risks looking canonical. | align now / label as legacy or reference-specific | `TASK-AWDD-04-001`: either migrate DCA to the canonical split or label the consolidated UI as historical/reference-specific mechanics. |
| Legacy frontend taxonomy | `docs/workstream-ui-reference-architecture.md`, `docs/web-ui-pattern-selection.md`, `docs/web-ui-frontend-decomposition.md`, `docs/web-ui-quality-checklist.md`, `skills/akka-web-ui-apps/SKILL.md` | These mostly quarantine `frontend/src/screens/**` and static examples well, but the migration should confirm all web UI and generation paths point generated SaaS UI to `frontend/src/workstream/**` and User Admin vertical fixtures/tests. | leave unchanged now; verify later | `TASK-AWDD-03-002`: strengthen any weak remaining web UI/generation wording. |
| Readiness/generation gates | `skills/app-description-readiness-assessment/SKILL.md`, `skills/app-generate-app/SKILL.md` | Readiness and generation already block missing workstream UI, foundation agents, style guide, and security. They mention functional/context-area agents and full-core gates clearly. Main risk is not correctness but consistency with the forthcoming canonical file-set/checklist. | candidate for later task | `TASK-AWDD-03-002`: align references to canonical workstream UI and file-set target after checklist exists. |
| Seed example as canonical target | `docs/examples/ai-first-saas-seed-app-description/**` | Seed app contains `12-workstreams/` and a rich split `55-ui/` layer. It is the best current target model, but the exact file set differs from bootstrap/UI skill guidance (`ai-first-surfaces.md` exists in seed/UI skill but not internal architecture's default list). | leave unchanged now; use as input | `TASK-AWDD-02-002`: use seed plus doctrine to define the final canonical file set. |

## Detailed inventory by content family

### Core doctrine docs

| File | Status | Findings | Classification |
|---|---|---|---|
| `docs/agent-workstream-application-architecture.md` | Mostly aligned | Canonical workstream doctrine already rejects page-first, CRUD-first, and chatbot-bolt-on defaults; defines functional/context-area agent alias; positions surfaces and capabilities correctly. Could be made a little more explicit that routes are deep-link/implementation details and that `12-workstreams/` owns the app-description application model when app-description layers are mentioned elsewhere. | leave unchanged for this task; input to `TASK-AWDD-02-001` |
| `docs/structured-surface-contracts.md` | Aligned | Strong chain: functional agent/workstream placement → surface schema/actions → capabilities → auth/audit/trace → rendering/tests. Useful target language for checklist. | leave unchanged |
| `docs/capability-first-backend-architecture.md` | Aligned | Correctly treats workstream actions, browser actions, tools, APIs, timers, workflows, and consumers as exposure surfaces for governed capabilities. | leave unchanged |
| `docs/ai-first-saas-application-architecture.md` | Mostly aligned | Already states generated UI/application architecture is agent workstream model and not page hierarchy/chatbot. Later migration can standardize first-use terminology and link explicitly to the design review checklist. | candidate for `TASK-AWDD-02-001` |
| `docs/internal-app-description-architecture.md` | Needs alignment | Good ownership sections for `12-workstreams/` and `55-ui/`, but canonical `55-ui/` file list differs from bootstrap/UI skill and seed example. Also references `context-area agents` in a bullet after earlier `functional agents`; first mention can adopt canonical alias. | align now in `TASK-AWDD-02-001` / `TASK-AWDD-02-002` |

### App-description skills

| File | Status | Findings | Classification |
|---|---|---|---|
| `skills/app-descriptions/SKILL.md` | Mostly aligned | Routes description-first work through `12-workstreams/`, `15-operating-model/`, and `55-ui/`; states page/screen hierarchy is subordinate. Needs only checklist/file-set consistency review later. | candidate for later task |
| `skills/app-description-bootstrap/SKILL.md` | Needs alignment | Minimum outputs include `12-workstreams/functional-agents.md` and `surface-contracts/**` but omit `internal-agents.md` and `workstreams-and-retention.md` from the minimum list; `55-ui/` list differs from internal architecture and UI skill. Full-core gate is strong. | align now in `TASK-AWDD-02-002` |
| `skills/app-description-ui/SKILL.md` | Needs alignment | Strongly routes UI through agent workstream shell and canonical frontend reference. Its preferred `55-ui/` structure includes `ai-first-surfaces.md` and managed-agent files, but differs from bootstrap and internal architecture. It says create files justified by app; follow-up should clarify mandatory full-core vs optional/deferred narrower-scope files. | align now in `TASK-AWDD-02-002` |
| `skills/app-description-functional-agent-modeling/SKILL.md` | Mostly aligned | Correctly keeps `12-workstreams/functional-agents.md` authoritative and `55-ui/**` as realization. Uses `functional agents` wording; later terminology pass can standardize first alias. | candidate for later task |
| `skills/app-description-surface-modeling/SKILL.md` | Aligned | Clearly says surfaces are not pages/routes/CRUD/chat messages and keeps `12-workstreams/surface-contracts/**` authoritative with `55-ui/**` for rendering/API/state/accessibility. | leave unchanged; use in checklist |
| `skills/app-description-readiness-assessment/SKILL.md` | Mostly aligned | Strong readiness gates for functional/context-area agents, surfaces, full-core foundation, selected style guide, and workstream UI. Later task should check final checklist/file-set terminology. | candidate for `TASK-AWDD-03-002` |
| `skills/app-generate-app/SKILL.md` | Mostly aligned | Strong generation block on missing secure foundation, workstream UI, governed agents, style guide, and frontend secret-boundary tests. Later task should ensure realization starts from `frontend/src/workstream/**` explicitly enough. | candidate for `TASK-AWDD-03-002` |

### AI-first and routing skills

| File | Status | Findings | Classification |
|---|---|---|---|
| `skills/agent-workstream-apps/SKILL.md` | Aligned | Top-level routing skill expresses the current model and cleanup warnings. Could adopt checklist link once created. | leave unchanged now; candidate for `TASK-AWDD-03-003` |
| `skills/ai-first-saas-ui-surfaces/SKILL.md` | Needs alignment | Surface catalog is useful, but currently lets an agent plan selected surfaces before explicitly binding them to owning/reusable functional agents and workstream placement. | align now in `TASK-AWDD-03-001` |
| `skills/ai-first-saas/SKILL.md` | Mostly aligned | Anti-chatbot rule and AI-first interpretation are compatible. No immediate design drift found. | leave unchanged |
| `skills/akka-solution-decomposition/SKILL.md`, `skills/capability-first-backend/SKILL.md`, `skills/akka-prd-to-specs-backlog/SKILL.md` | Mostly aligned | These already warn against CRUD/component decomposition too early and preserve AI-first/workstream/capability semantics. Review after checklist to avoid duplicated long warnings. | candidate for `TASK-AWDD-03-003` |

### Web UI docs and skills

| File | Status | Findings | Classification |
|---|---|---|---|
| `docs/workstream-ui-reference-architecture.md` | Aligned and canonical | Clearly defines `frontend/src/workstream/**`, `/api/me`, AuthContext, functional agents, surfaces, capability actions, realtime, and quarantine boundaries for `screens/**` and static examples. | leave unchanged; source target for review checklist |
| `docs/web-ui-pattern-selection.md` | Mostly aligned | Points to workstream reference as canonical and endpoint/static examples as mechanics. Good route-shape guidance. | leave unchanged now |
| `docs/web-ui-frontend-decomposition.md` | Mostly aligned | Decomposes the browser app by shell regions and surfaces before routes. Strongly rejects legacy screens as primary model. | leave unchanged now |
| `docs/web-ui-quality-checklist.md` | Mostly aligned | Strong quality checklist; not a design-review checklist for app-description content. Future checklist should be separate and link here for implementation quality. | leave unchanged now |
| `skills/akka-web-ui-apps/SKILL.md` | Mostly aligned | Strong generated-SaaS workstream shell routing and canonical frontend references. Later task should verify generation/readiness wording and legacy reference quarantine remain consistent after file-set target is defined. | candidate for `TASK-AWDD-03-002` |
| `skills/akka-web-ui-frontend-project/SKILL.md`, `skills/akka-http-endpoint-web-ui/SKILL.md` | Mostly aligned mechanics | These are primarily integration/hosting mechanics; they warn against page-first generated SaaS shell where needed. | leave unchanged unless later review finds weak quarantine wording |
| `skills/akka-web-ui-realtime/SKILL.md` | Minor terminology drift | Contains generic phrase "navigating away from screens". Harmless mechanics wording, but can be normalized to workstream/surface regions later. | candidate for later task |

### Examples and reference app descriptions

| File/tree | Status | Findings | Classification |
|---|---|---|---|
| `docs/examples/ai-first-saas-seed-app-description/**` | Best current target | Has `12-workstreams/` and rich split `55-ui/`. It should be treated as the canonical secure AI-first SaaS seed reference. Minor drift: exact `55-ui/` files are richer than internal architecture and differ from bootstrap. | leave unchanged now; use in target definition |
| `docs/examples/ai-first-dca-app-description/**` | Needs label or migration | DCA app-description has no `12-workstreams/` layer in the current tree and uses consolidated `55-ui/ui-surfaces.md`. It is valuable but can obscure current canonical split if not clearly labeled or migrated. | align/label in `TASK-AWDD-04-001` |
| `docs/examples/ai-first-dca-app-description/app-description/80-review/structure-gap-summary.md` | Already flags gap | Explicitly says DCA uses compact UI surface guidance rather than seed's full UI layer file set. This helps, but the warning is buried in review output. | candidate for DCA task |
| `docs/examples/purchase-request-app-description/**` | Mechanics reference | README warns not to present page/screen hierarchy as generated SaaS target. It remains useful for description mechanics only. | leave unchanged; ensure future checklist preserves quarantine |
| `docs/examples/core-ai-first-saas-input/**` | Mostly aligned | Module PRDs repeatedly state legacy page/screen/navigation wording means workstream surfaces and deep-link details. Some phrases like "CRUD/governance UI" appear in module scope notes; context usually clarifies not page-first. | candidate for later cleanup only if noisy |
| `docs/examples/ai-first-app-description-gaps.md` | Stale-ish reference | Still shows a compact `55-ui/ui-surfaces.md` target from earlier DCA planning. It may confuse future agents if treated as current target. | candidate for later task / label as historical gap note |

### Readiness, generation, and planning guidance

| File | Status | Findings | Classification |
|---|---|---|---|
| `docs/app-description-maintenance-flow.md` | Mostly aligned | Maintains ordering through AI-first/capabilities/UI and includes mandatory UI/style-guide language. Later migration can link the design review checklist. | candidate for later task |
| `docs/description-first-application-doctrine.md` | Mostly aligned | Anti-patterns include CRUD screens plus chatbot; not central to file-set drift. | leave unchanged unless checklist references needed |
| `docs/app-description-end-to-end-workflow-example.md`; removed app-description skill-plan backlog | Not fully inspected for this inventory beyond targeted scans | No high-priority drift surfaced in targeted searches, but follow-up checklist task may decide whether the remaining workflow example needs links. | candidate for later verification |
| `skills/akka-prd-to-specs-backlog/SKILL.md`, `skills/akka-slice-spec-to-backlog/SKILL.md` | Mostly aligned | Preserve capability ids and style-guide questions; no immediate workstream design conflict found. | leave unchanged now |

## Files likely to edit in follow-on tasks

### Sprint 2: doctrine and app-description alignment

- `docs/ai-first-saas-application-architecture.md` — terminology/checklist link if needed.
- `docs/agent-workstream-application-architecture.md` — checklist link and app-description ownership sentence if needed.
- `docs/structured-surface-contracts.md` — likely leave unchanged; use as checklist input.
- `docs/capability-first-backend-architecture.md` — likely leave unchanged; use as checklist input.
- `docs/internal-app-description-architecture.md` — canonical `55-ui/` file set and explicit `12-workstreams/` vs `55-ui/` ownership.
- `skills/app-description-bootstrap/SKILL.md` — canonical minimum/full-core `12-workstreams/` and `55-ui/` file sets.
- `skills/app-description-ui/SKILL.md` — canonical `55-ui/` file set and full-core vs narrow-scope mandatory/optional wording.

### Sprint 3: routing and generation/web UI

- `skills/ai-first-saas-ui-surfaces/SKILL.md` — require owning/reusable functional agents and workstream placement for every surface.
- `docs/web-ui-pattern-selection.md`, `docs/web-ui-frontend-decomposition.md`, `docs/web-ui-quality-checklist.md` — verify/strengthen canonical `frontend/src/workstream/**` references if needed.
- `skills/akka-web-ui-apps/SKILL.md`, `skills/app-generate-app/SKILL.md`, `skills/app-description-readiness-assessment/SKILL.md` — verify realization/readiness cannot route to page-first/screens-first UI.
- `skills/README.md`, `skills/agent-workstream-apps/SKILL.md`, top-level routing docs — add compact checklist link after checklist exists.

### Sprint 4: examples

- `docs/examples/ai-first-dca-app-description/README.md`
- `docs/examples/ai-first-dca-app-description/app-description/55-ui/README.md`
- `docs/examples/ai-first-dca-app-description/app-description/55-ui/ui-surfaces.md`
- DCA capability/traceability/generation files currently linking directly to `../55-ui/ui-surfaces.md`
- `docs/examples/ai-first-app-description-gaps.md`

## Leave unchanged unless later evidence appears

- `docs/structured-surface-contracts.md`
- `docs/capability-first-backend-architecture.md`
- `docs/workstream-ui-reference-architecture.md`
- `docs/web-ui-pattern-selection.md` (likely already good)
- `docs/web-ui-frontend-decomposition.md` (likely already good)
- `docs/web-ui-quality-checklist.md` (implementation quality checklist, not design-review checklist)
- `skills/app-description-surface-modeling/SKILL.md`
- `skills/agent-workstream-apps/SKILL.md` (until checklist link exists)

## Inventory checks performed

Targeted scans covered:

- `55-ui`, `12-workstreams`, `frontend/src/workstream`, `frontend/src/screens`, `screens-and-navigation`, `page-first`, `CRUD`, `chatbot`, `left-rail`, `context-area`, `functional agent`, `ui-surfaces.md`, and DCA references across `docs/`, `skills/`, active specs, and packaging-related guidance.
- Required task reads: migration README, conversation capture, Sprint 1, backlog/task brief, canonical agent workstream doctrine, structured surface contracts, capability-first doctrine, AI-first doctrine, and `agent-workstream-apps` skill.
- Additional representative reads: internal app-description architecture, app-description bootstrap/UI/functional-agent/surface/readiness/generation skills, web UI pattern/decomposition/quality/reference docs, `akka-web-ui-apps`, and `ai-first-saas-ui-surfaces`.

No content files outside this inventory and the queue were changed by this task.
