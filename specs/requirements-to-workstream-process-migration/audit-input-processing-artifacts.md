# Audit: Input-Processing Artifacts

## Scope

Task: `TASK-REQWS-01-001`.

This audit reviewed the source artifacts that shape how broad app input, PRDs, app-description changes, solution plans, backlogs, pending questions, pending tasks, examples, and packaged guidance become generated-app work.

Target process used for classification:

```text
input/PRD/feature request
→ secure SaaS foundation
→ workstreams
→ per-workstream attention breakdown ("what needs my attention?")
→ dashboards
→ structured surfaces and surface actions
→ governed capabilities/APIs
→ Akka components
→ request-based workstream Agents
→ internal/background workers, usually AutonomousAgent tasks when durable lifecycle fits
→ events/messages/notifications
→ attention projections
→ audit/work traces
```

Classification values:
- **aligned** — already prescribes the target process strongly enough for downstream use.
- **partially aligned** — contains the right concepts but misses target-process fields, sequencing, or prescriptive defaults.
- **drift risk** — can still lead a future agent toward CRUD, page-first, component-first, event-only, chatbot-bolt-on, or capability-without-workstream planning.
- **not relevant** — inspected or considered but not an input-processing control point for this migration.

## Summary findings

- The pack is already strongly aligned on secure SaaS, functional-agent workstreams, structured surfaces, capability-first backend design, and runtime completion standards.
- The largest gaps are sequencing and artifact shape, not conceptual absence: broad input normalization, PRD/spec/backlog output contracts, and queue templates do not yet require the complete `attention → dashboard → surface/action → capability → AutonomousAgent-task/event/notification/trace` chain everywhere.
- `skills/app-description-input-normalization/SKILL.md` is the clearest immediate drift risk because its normalized envelope starts at capability/behavior/test/security/observability deltas and does not yet model workstream candidates, attention needs, dashboard candidates, autonomous task candidates, or notification/attention-projection implications.
- `docs/prd-to-akka-flow.md` remains a mechanics-only example and is intentionally conventional. It warns readers to start from secure AI-first SaaS, but later sprints should either crosslink the canonical requirements-to-workstream process or explicitly label the purchase-request path as insufficient for generated SaaS target architecture.
- `skills/akka-solution-decomposition/SKILL.md`, `skills/akka-prd-to-specs-backlog/SKILL.md`, `docs/module-sprint-planning.md`, `docs/solution-plan-to-implementation-queue.md`, and `docs/pending-task-queue.md` are mostly aligned for workstreams/surfaces/capabilities, but need explicit attention/dashboard/autonomous-task/notification fields before downstream tasks depend on them.
- Existing examples and packaged guidance should be audited in later sprints so generated installers do not preserve stale page-first or component-first routes.

## Artifact inventory

| Artifact | Classification | Current alignment | Drift risk / gap | Future edit point |
| --- | --- | --- | --- | --- |
| `AGENTS.md` | aligned | Requires high-level product input to begin with secure AI-first SaaS, agent workstream doctrine, capability-first backend, mandatory web UI, managed agents, authorization, and audit/work traces before CRUD or component decomposition. | Does not yet name the compact requirements-to-workstream process or attention/dashboard/autonomous-task chain explicitly as a canonical route. | After canonical doctrine exists, add a short crosslink in the high-level product input/default working model sections rather than duplicating prose. |
| `pack/AGENTS.md` | partially aligned | Installed-pack guidance requires secure foundation, agent workstream shell, managed-agent runtime, pending question/task queues, and anti-mock runtime completion. | Installed users may still see the broad process as secure foundation + capabilities rather than `workstream → attention → dashboard → surfaces → capabilities`; no explicit `what needs my attention?` default. | Add packaged crosslink to canonical process and selection reminder after source docs are updated. |
| `skills/README.md` | partially aligned | Strong top-level routing through AI-first SaaS, agent workstream apps, capability-first backend, minimum starter, queues, and runtime completion. Mentions extending starter with durable internal/background work through `AutonomousAgent`. | It has many correct pieces, but not a single prescriptive intake expansion chain. Broad input routing can still be read as workstreams/surfaces/capabilities without requiring attention breakdowns, dashboards, autonomous task candidates, event/notification mapping, and attention projections. | Add canonical process link and compact routing rule under AI-first SaaS entry routing; later update app-description and PRD/backlog sections to require attention/dashboard fields. |
| `docs/workstream-dashboard-attention-event-backbone-wip.md` | aligned | Source concept directly defines dashboards, workstream scoping, My Account aggregate attention, left-rail attention counts, first-class attention items, request-based Agent vs `AutonomousAgent`, events/messages/notifications, and trace implications. | WIP status means downstream skills should not cite it as canonical forever. Some implementation contract details may be too broad for a low-token routing reference. | Promote or distill into canonical `docs/requirements-to-workstream-development-process.md`; keep WIP as provenance or detailed companion. |
| `skills/ai-first-saas/SKILL.md` | aligned | Prescribes secure SaaS → `agent-workstream-apps` → core foundation → capability-first backend before decomposition; rejects CRUD-first and chatbot-bolt-on defaults; preserves request-based workstream Agents. | Output expectations do not yet require per-workstream attention breakdown, dashboard candidates, event/notification mapping, or `AutonomousAgent` task candidates. | Add a short handoff bullet to include attention/dashboard/autonomous-task/notification implications once canonical process exists. |
| `skills/agent-workstream-apps/SKILL.md` | partially aligned | Defines generated SaaS as role-authorized functional-agent workstreams with structured surfaces and capability mappings. Requires default dashboard, attention, or briefing surfaces. Cleans up page-first, CRUD-first, and chatbot-bolt-on defaults. | Attention is present but not mandatory enough: each workstream should start with "what needs my attention?", dashboard content, attention item categories, My Account/left-rail summary effects, and notifications/events. Internal agents are described, but durable background `AutonomousAgent` task selection is not first-class here. | Strengthen interpretation workflow: after functional agents, require attention breakdown and dashboard contract before generic surface modeling; add internal/background worker candidate section with `AutonomousAgent` default criteria. |
| `skills/app-description-input-normalization/SKILL.md` | drift risk | Good at preserving mixed intent and separating capability, behavior, tests, auth/security, observability, realization, and review deltas. | Primary envelope is capability-first and layer-first. It lacks workstream candidates, attention needs, dashboard candidates, surface/action expansion, autonomous task candidates, events/notifications, and attention/audit projection implications. This can let broad input bypass the target process. | Update goal, extraction list, normalized envelope, handoff rules, and final checklist to require workstream/attention/dashboard/surface/capability/autonomous-task fields for generated SaaS input. |
| `skills/app-description-intake-router/SKILL.md` | partially aligned | Already has a generated SaaS workstream pre-check before capability/UI routing and names dashboards, portals, work queues, admin consoles, surfaces, actions, and action-to-capability candidates. Rejects CRUD reduction. | Does not require `what needs my attention?` decomposition before dashboard/surface modeling; lacks autonomous task/event/notification/attention projection routing. | Add routing rule: broad generated SaaS input must pass through workstream attention/dashboard expansion, then surface/action/capability modeling, then internal worker/AutonomousAgent candidates where durable. |
| `docs/app-description-maintenance-flow.md` | partially aligned | Strongly sequences generated SaaS changes through `12-workstreams/` before capabilities/UI and prioritizes structured surfaces over CRUD/page navigation. | Dashboard/attention semantics appear indirectly through workstreams/surfaces, not as mandatory first-class per-workstream artifacts. Autonomous task and notification mapping are not prominent in the maintenance flow. | Update Step 3 and readiness/checklist sections to require attention breakdowns, dashboard contracts, autonomous task candidates, event/notification behavior, and attention projections. |
| `docs/internal-app-description-architecture.md` | partially aligned | `12-workstreams/` is authoritative for functional agents, workstreams, surface contracts, workstream expertise, traces, and tests; page trees are explicitly not primary. | App-description architecture has places for dashboards/surfaces but not a canonical attention item/workstream attention summary layer. It can represent the target process but does not force it yet. | Add/adjust `12-workstreams/` contracts for attention items, dashboard summaries, My Account/rail aggregate, autonomous task links, notification sources, and traces. |
| removed app-description skill-plan backlog | resolved drift risk | Historical planning map for app-description skills; source-level provenance only after removal from active docs. | Older layer-oriented wording could reinforce capability/behavior/test/security first. | Removed from active guidance by the RIAC cleanup. |
| `skills/akka-solution-decomposition/SKILL.md` | partially aligned | Requires secure foundation, agent workstream model, structured surfaces/actions, surface-action-to-capability mapping, component mapping, vertical implementation order, and no jump from product intent to Akka components. | Output sections do not yet explicitly require attention breakdowns, dashboard definitions, event/notification mapping, attention projections, or `AutonomousAgent` task candidates/results/notifications. It mentions internal agents but not the new backend-worker default candidate. | Add sections between workstream model and surfaces/actions: attention model/dashboard contract, autonomous task candidates, events/notifications/attention projections. Update required tests accordingly. |
| `skills/akka-prd-to-specs-backlog/SKILL.md` | partially aligned | Strongly preserves functional agents, internal agents, workstreams, surfaces, capabilities, workstream expertise, governed foundation, backlogs, pending questions, pending tasks, and vertical sprints. | Large PRD splitting still says modules/sprints and can be interpreted as business modules first. It lacks explicit rule that large PRDs split first by workstreams and attention/dashboard verticals unless cross-cutting foundation. Autonomous task definitions/results/notifications are not mandatory queue fields. | Update PRD extraction, master plan sections, module/sprint/slice contracts, backlog/task queue contracts to require workstream attention/dashboard/autonomous task/event notification fields. |
| `docs/prd-to-akka-flow.md` | drift risk | Warns that the example is not AI-first SaaS target architecture and says to start from mandatory foundation and AI-first seed. | The step list and canonical example are conventional, with capability summary and components but no workstream attention/dashboard/autonomous-task chain. Future agents may copy the mechanics without the new process. | Add a prominent reference to the canonical requirements-to-workstream process and state the purchase-request example is mechanics-only, not sufficient for generated SaaS planning. |
| `docs/module-sprint-planning.md` | partially aligned | Very strong vertical task contract: functional agent, surface/action or event, capability id/class, AuthContext, Akka substrate, frontend/API/realtime, tests, local run. Blocks page/component-only tasks. | Allows module terminology as durable boundary and does not require every generated SaaS sprint/backlog item to start from attention categories/dashboard contract. `AutonomousAgent` task lifecycle/results/notifications are not named in the backlog fields. | Add attention/dashboard/autonomous-task/notification fields to module, sprint, backlog, and anti-pattern sections; clarify modules are secondary to workstream verticals for generated SaaS PRDs. |
| `docs/solution-plan-to-implementation-queue.md` | partially aligned | Converts solution plans into vertical queues with functional agent/surface/trigger, capability id/class, expertise scope, AuthContext, substrate, frontend/API/realtime, local validation, and done means. | Missing explicit attention category/dashboard contract, `AutonomousAgent` task definition/result/notification, event/message source, attention projection update fields. | Extend minimal transformation and queue template with attention/dashboard/autonomous-task/event/notification/projection fields. |
| `docs/pending-task-queue.md` | partially aligned | Strong status/selection rules and implementation-ready vertical task rule; blocks tasks that are only module/page/dashboard/CRUD/component family; preserves capabilities, AuthContext, surfaces, checks, local smoke. | The required queue shape does not require attention category, dashboard/surface contract, autonomous task lifecycle/result/notification links, or event-to-attention projection details. A vague `build dashboard` is blocked, but a dashboard task could still omit "what needs my attention?" semantics. | Add required fields for workstream attention contract, dashboard/summary surfaces, My Account/left rail implications, autonomous task ids/results/notifications when applicable, source events/messages, attention projection, and audit/work trace linkage. |
| `docs/pending-question-queue.md` | partially aligned | Good durable clarification model for authority, approval, risk/evidence, audit, outcomes, UI, testing, and blockers. | Does not include categories for attention model, dashboard behavior, autonomous task lifecycle/result/notification, event/message source, or attention projection ambiguity. | Add category values/examples and rules for questions that block safe attention/dashboard/autonomous-task planning. |
| `docs/intent-driven-usage-flow.md` | drift risk | Basic process says read input, apply AI-first when delegated work exists, create questions/tasks. | It is too brief and still says capability contracts before components without workstream attention/dashboard sequencing. | Crosslink canonical process or update flow after doctrine sprint. |
| `docs/examples/ai-first-saas-seed-app-description/README.md` | partially aligned | Preferred seed reference for secure AI-first SaaS app descriptions; supports workstream-first architecture. | Needs later example-level verification for attention/dashboard/autonomous-task/event projection completeness. | Include in examples/packaging sprint audit and update if it lacks visible attention/dashboard contracts. |
| `docs/examples/purchase-request-*` references | drift risk | Useful mechanics references for plans, queues, and app-description examples. | Conventional approval workflow can reinforce component-first or CRUD-ish decomposition if copied as target architecture. | Later examples sprint should either update examples to include workstream attention/dashboard chain or add stronger warning/crosslink. |
| `.agents/` installed project skill copies | not relevant | Local project install mirrors source skills for this repo. | Do not edit installed copies directly in this source task unless packaging requires it. | Source skill updates and packaging/export tasks should regenerate/install as appropriate. |

## Stale pattern inventory

### CRUD-first

Current explicit anti-CRUD guidance is strong in `AGENTS.md`, `skills/README.md`, `skills/ai-first-saas/SKILL.md`, `skills/agent-workstream-apps/SKILL.md`, `skills/akka-solution-decomposition/SKILL.md`, `docs/module-sprint-planning.md`, and `docs/pending-task-queue.md`.

Residual CRUD drift risks:
- `docs/prd-to-akka-flow.md` uses a conventional purchase-request mechanics example.
- The removed app-description skill-plan backlog appeared older and layer-oriented.
- Any downstream examples that present record-management modules without workstream attention/dashboard expansion.

### Page-first

Current explicit anti-page guidance is strong in `skills/agent-workstream-apps/SKILL.md`, `docs/internal-app-description-architecture.md`, and `docs/app-description-maintenance-flow.md`.

Residual page-first drift risks:
- Input normalization has no workstream/surface-first envelope, so UI input could become generic UI or capability deltas.
- PRD/backlog examples may still present pages/routes as primary planning outputs.

### Component-first

Current explicit anti-component guidance is strong in `skills/akka-solution-decomposition/SKILL.md`, `docs/module-sprint-planning.md`, `docs/solution-plan-to-implementation-queue.md`, and `docs/pending-task-queue.md`.

Residual component-first drift risks:
- Solution plan and PRD-to-specs sections do not yet require attention/dashboard/autonomous-task fields before component selection.
- Conventional Akka example pairs are useful but can be copied without the new workstream process.

### Event-only

The WIP concept correctly places events/messages/notifications after workstream actions, autonomous tasks, and capabilities, feeding attention projections and traces.

Residual event-only drift risks:
- Existing planning docs mention consumers/events but do not require notification-to-surface or event-to-attention projection semantics.
- Pending queue contracts do not require source event/message ids, projection update behavior, or attention lifecycle linkage.

### Chatbot-bolt-on

Current anti-chatbot guidance is strong in `skills/ai-first-saas/SKILL.md`, `skills/agent-workstream-apps/SKILL.md`, `skills/README.md`, and `docs/app-description-maintenance-flow.md`.

Residual chatbot drift risks:
- Minimum/starter docs already constrain to five core workstreams and `markdown_response`, but examples and intake normalization should still preserve dashboard/attention/surface semantics so `markdown_response` is not mistaken for the root app model.

### Capability-without-workstream

Capability-first backend remains correct as the backend authority boundary, but target process says broad app input should discover capabilities through workstream attention/dashboard/surface actions.

Residual risks:
- `skills/app-description-input-normalization/SKILL.md` starts with capability deltas.
- `docs/intent-driven-usage-flow.md` and `docs/prd-to-akka-flow.md` are brief enough that capability-first may appear before workstream-attention discovery.

### Autonomous-task omission

Autonomous Agent guidance exists elsewhere, and the WIP source captures the backend-agent gap clearly.

Residual risks:
- `agent-workstream-apps`, `akka-solution-decomposition`, `akka-prd-to-specs-backlog`, and queue docs do not yet require durable internal/background worker candidates to be evaluated as `AutonomousAgent` tasks when lifecycle, snapshots/results, notifications, dependencies, failure/cancellation, delegation, handoff, teams, or moderation fit.
- Queue templates lack fields for autonomous task definition, result surface, notification stream, cancellation/failure behavior, dependencies, and linked attention items.

## Exact future edit targets by sprint

### Sprint 02: Doctrine consolidation

1. Create canonical process doc, likely `docs/requirements-to-workstream-development-process.md`, distilled from `docs/workstream-dashboard-attention-event-backbone-wip.md`.
2. Add crosslinks from:
   - `AGENTS.md`
   - `pack/AGENTS.md`
   - `skills/README.md`
   - `docs/ai-first-saas-application-architecture.md`
   - `docs/agent-workstream-application-architecture.md`
   - `docs/structured-surface-contracts.md`
   - `docs/capability-first-backend-architecture.md`
   - `docs/agent-component-selection-guide.md`
3. Keep WIP doc as detailed source/provenance unless it is renamed.

### Sprint 03: Intake and description-first realignment

1. `skills/app-description-input-normalization/SKILL.md`
   - Add fields: workstream candidates, attention needs/categories, dashboard candidates, structured surface/action candidates, action-to-capability candidates, autonomous task candidates, event/notification/attention projection implications, audit/work trace links.
   - Add final checklist items that broad generated SaaS input did not skip attention/dashboard modeling.
2. `skills/app-description-intake-router/SKILL.md`
   - Make workstream attention/dashboard expansion mandatory before direct capability/UI routing for broad generated SaaS input.
   - Route durable internal/background worker candidates toward autonomous-agent planning skills where appropriate.
3. `docs/app-description-maintenance-flow.md` and `docs/internal-app-description-architecture.md`
   - Add `12-workstreams/` attention/dashboard contracts and readiness implications.
4. Focused app-description skills to verify later:
   - bootstrap, functional-agent modeling, surface modeling, capability modeling, UI, readiness.

### Sprint 04: PRD/spec/backlog planning realignment

1. `skills/akka-solution-decomposition/SKILL.md`
   - Add required plan sections for workstream attention breakdown, dashboard contract, autonomous task candidates, events/notifications, attention projections.
2. `skills/akka-prd-to-specs-backlog/SKILL.md`
   - Require PRD split by workstream/attention verticals before module/component decomposition.
   - Preserve autonomous task lifecycle/result/notification fields in specs/backlogs/tasks.
3. `docs/prd-to-akka-flow.md`
   - Crosslink canonical process and strengthen mechanics-only warning.
4. `docs/module-sprint-planning.md`
   - Add attention/dashboard/autonomous-task/notification fields to sprint/backlog/task contracts.
5. Related change/reconciliation/backlog-generation skills should be audited in the same sprint because README scope names revised PRD/change request/spec backlog paths.

### Sprint 05: Queue and task contracts

1. `docs/solution-plan-to-implementation-queue.md`
   - Add attention category, dashboard contract, autonomous task, event/notification, projection, and trace fields to queue template.
2. `docs/pending-task-queue.md`
   - Add required queue fields for workstream attention/dashboard/action/capability/autonomous-task/event/notification/audit linkage.
3. `docs/pending-question-queue.md`
   - Add categories and examples for attention model, dashboard behavior, autonomous task lifecycle, event/notification mapping, and attention projection blockers.
4. Queue-generation and queue-maintenance skills should mirror those fields.

### Sprint 06: Examples, seed, and packaging

1. Verify the AI-first SaaS seed app-description shows attention/dashboard/surface/capability/autonomous-task chain for core workstreams.
2. Update purchase-request examples or add a new example showing PRD → workstreams → attention → dashboards → surfaces/actions → capabilities → components/tasks.
3. Ensure packaged docs/skills/manifests include canonical process doc and updated source skills.

## Recommended input set for target process contract task

`TASK-REQWS-01-002` should use this audit plus the WIP concept to draft a compact contract with these non-optional fields:

- workstream id/name/owner functional agent;
- user/role-specific attention categories and dashboard question: "what needs my attention?";
- dashboard summary and detailed attention item surface contracts;
- surface actions and governed surface-request actions;
- capability ids/classes and AuthContext/authority/audit contracts;
- selected Akka substrate and API/exposure path;
- request-based Akka `Agent` turns for immediate user-facing workstream messages;
- internal/background worker candidates with `AutonomousAgent` default criteria and task lifecycle/result/notification shape;
- event/message/notification sources;
- attention projections for My Account and left rail;
- audit/work trace linkage;
- required tests and local validation expectations.
