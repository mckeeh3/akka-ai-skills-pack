# Prune and Rewrite Criteria: Requirements Intake Alignment Cleanup

## Purpose

Use these criteria when rewriting, removing, or demoting active requirements-intake and planning guidance. The goal is to keep installed-pack guidance aligned with the current generated-application target:

```text
natural user requirement
→ secure AI-first SaaS foundation
→ functional/context-area agents and workstreams
→ attention model and default/briefing surfaces
→ structured surfaces, system-message surfaces, and actions
→ governed backend capabilities
→ Akka substrate and exposure channels
→ full-stack local runtime/API/UI validation
```

These criteria apply to active skills, docs, examples, and packaging references that influence PRDs, specs, feature requests, fixes, app-description updates, backlog generation, pending questions, pending tasks, readiness, generation, or web UI planning.

## Canonical alignment test

An active guidance artifact is aligned only when it preserves these rules:

1. **Secure SaaS is mandatory by default** for generated applications unless the request is explicitly repository-maintenance-only or non-SaaS reference material.
2. **Minimum/basic/starter/chatbot-like generated SaaS means the five core workstream v0 starter**: My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy with `markdown_response` surfaces and bootstrap authorization. It is not User-Admin-only, a public chatbot, or a generic page shell.
3. **Functional/context-area agents and durable workstreams are the primary app model** for authenticated consequential work, not pages, screens, navigation trees, CRUD modules, dashboards, resources, endpoints, or chat sessions.
4. **Structured surfaces and surface actions are first-class contracts** with typed payloads, states, AuthContext assumptions, traces, rendering tests, and capability-backed actions.
5. **Backend behavior is modeled as governed capabilities before Akka components or exposure surfaces**. Endpoints, browser actions, agent tools, workflow steps, timers, consumers, views, and component methods expose or realize capabilities; they are not the root design.
6. **Managed-agent runtime governance is mandatory from foundation planning** where generated SaaS agents are in scope: `AgentDefinition`, governed prompts/skills/references, compact manifests, loader tools, tool boundaries, prompt/skill/reference/work traces, provider fail-closed behavior, and real Akka `Agent` invocation for model-backed user-facing turns.
7. **Runtime completion claims require the intended local runtime/API/UI path** at the stated scope. Deterministic/demo/mock/model-less normal runtime behavior cannot satisfy generated-app feature readiness.
8. **Legacy examples are quarantined** as mechanics-only unless they are explicitly rewritten to the current workstream/surface/capability architecture.

## Classification criteria

### Keep

Classify an artifact as `keep` when all are true:

- It teaches or supports current secure AI-first SaaS, workstream/surface/capability, managed-agent, security, or runtime-completion doctrine.
- It does not route broad generated-app input through CRUD/page/resource/component-first decomposition.
- Any conventional UI, endpoint, or component terminology is clearly framed as implementation mechanics after workstreams, surfaces, and capabilities are defined.
- Any legacy example references are explicitly mechanics-only and are not the first or preferred generated-SaaS reference.
- It remains useful to future agents without needing substantial context repair.

### Focused rewrite

Classify as `focused rewrite` when the artifact is mostly aligned but contains bounded drift such as:

- a stale minimum-starter phrase such as User Admin-only instead of five core workstream v0;
- a preferred purchase-request/shopping-cart example where the AI-first seed should be preferred;
- isolated page/screen/list/detail/resource/API wording that can be reframed as realization mechanics;
- missing reminders to preserve AuthContext, capability ids, audit/work traces, structured surfaces, or runtime validation;
- stale links that can be corrected without changing the artifact's structure.

A focused rewrite should edit the smallest useful sections and avoid expanding the artifact with broad new doctrine already covered elsewhere.

### Heavy rewrite

Classify as `heavy rewrite` when the topic is still useful but the artifact's structure teaches the wrong path, for example:

- starts from pages, routes, resources, CRUD, dashboards, or component lists for broad generated-app input;
- treats AI-first interpretation as optional for default generated SaaS;
- centers a legacy purchase-request/shopping-cart flow as the main narrative for generated-SaaS intake;
- makes `55-ui/` or frontend screens the source of product meaning instead of a realization of `12-workstreams/`, capabilities, security, observability, and tests;
- omits the managed-agent runtime foundation where generated SaaS agents are in scope;
- creates planning or pending-task shapes that are component-only, page-only, module-only, or UI-only without vertical workstream/surface/capability context.

A heavy rewrite should prefer concise replacement over incremental patches. If only a small mechanics section remains valuable, move or preserve that section only when references justify it.

### Remove

Classify as `remove` when any are true:

- The artifact is an obsolete plan/backlog/status document that no longer guides active implementation.
- It duplicates newer canonical doctrine while preserving stale terminology or outdated decisions.
- Its useful content is already covered by canonical docs or skills and keeping it increases routing ambiguity.
- It teaches a deprecated intake model and would require near-total rewrite for little current value.
- It is referenced only by stale or internal migration material that should not be installed-pack guidance.

Prefer deletion over archiving inside active `docs/`, `skills/`, or installable resource paths. If provenance is genuinely needed, record it in a source-repo `specs/.../archive` area only in a task that explicitly scopes provenance preservation; do not leave obsolete active guidance in place.

### Demote to mechanics-only

Classify as `demote-to-mechanics-only` when an artifact or example remains useful for narrow mechanics but is not canonical generated-app architecture, such as:

- purchase-request app-description cross-linking mechanics;
- conventional PRD, solution-plan, sprint, backlog, or pending-task formatting examples;
- static asset hosting, endpoint wiring, route/deep-link, form, table, or frontend project mechanics;
- simple entity/component examples unrelated to the secure AI-first SaaS operating model.

Demoted material must be labeled at its entry point and wherever active skills point to it.

## Mechanics-only labeling standard

Use direct language. A demoted artifact or link should say all relevant points:

- `Mechanics-only reference` or `Conventional mechanics reference`.
- `Not the canonical generated AI-first SaaS target architecture`.
- `For secure AI-first SaaS foundation shape, prefer docs/examples/ai-first-saas-seed-app-description/README.md` when app-description structure is in scope.
- `For broad requirements-to-workstream planning, prefer docs/requirements-to-workstream-development-process.md and docs/examples/requirements-to-workstream-mini-example.md` when PRD/intake planning is in scope.
- `Do not copy its page/resource/CRUD decomposition as the primary generated-app model` when the artifact contains conventional UI/API shapes.

Demotion is insufficient if a skill still uses the legacy example as the first, preferred, or canonical example for generated SaaS work. Fix the skill reference or remove the artifact.

## Rewrite requirements for active intake guidance

When rewriting active intake, planning, or app-description guidance, preserve this minimum checklist:

- Natural language input may be broad; the harness infers the path without making users name internal skills.
- Broad generated-app input starts with secure AI-first SaaS and agent workstream interpretation.
- Minimum/basic/starter/chatbot-like requests route to the five core workstream v0 starter and record follow-up work to full-core readiness.
- Workstream modeling identifies functional agents, internal agents when needed, attention categories, default dashboards/briefings, structured surfaces, system-message surfaces, and surface actions.
- Capability modeling records actors/callers, AuthContext, tenant/customer scope, schemas, validation, side effects, idempotency, approval/policy, audit/work trace, exposure surfaces, and tests before component selection.
- Backlog and pending-task outputs carry vertical contracts: functional agent or explicit foundation/internal scope, surface/action or non-UI trigger, capability id/class, AuthContext, substrate, API/frontend/realtime work, audit/trace, and local validation path.
- App-description guidance keeps product meaning in `12-workstreams/` plus capability/security/test/observability layers; `55-ui/` owns browser realization and links back to those layers.
- Generated app readiness and generation block on missing security, workstream UI, managed-agent runtime, style selection where applicable, and local runtime/API/UI validation.

## Removal and reference-update requirements

Before removing or renaming an active file:

1. Run a repository reference search for the exact path basename and any common relative link text, excluding only the current RIAC specs when appropriate.
2. Update active references in skills, docs, examples, templates, package metadata, and installed-pack guidance.
3. If a reference points from a mechanics-only context, replace it with a mechanics-only label or a current canonical reference.
4. If a reference points from canonical routing, replace it with the current canonical doc/skill instead of another legacy artifact.
5. Check `pack/manifest.yaml`, `pack/AGENTS.md`, installer/resource manifests, and template READMEs when the removed path could be packaged or installed.
6. Record the reference search command and outcome in the task summary or verification artifact when the task removes/renames files.

Do not remove a file if active references cannot be updated safely in the same bounded task. Instead, append or block a follow-up task with exact references to repair.

## Stale-term search list

Use focused searches over edited files for ordinary rewrite tasks. Use broader searches during whole-pack review. Review hits manually; terms may be acceptable when quarantined as mechanics, implementation details, or explicit anti-patterns.

High-priority stale concepts:

```text
User Admin workstream v0
User Admin-only
minimum starter
basic chatbot
generic chatbot
chatbot shell
page tree
screen tree
screens first
page-first
screen-first
CRUD-first
CRUD screen
CRUD module
resource-first
/api/<resource>
nav bar
navigation bar
search page
help page
list/detail
list/detail/edit
user-list
user-edit
frontend/src/screens
shopping-cart
purchase-request
```

Suggested commands:

```bash
# Focused edited-file pass; replace paths with the files edited in the task.
rg -n "User Admin workstream v0|User Admin-only|generic chatbot|chatbot shell|page tree|screen tree|page-first|screen-first|CRUD-first|CRUD screen|CRUD module|resource-first|/api/<resource>|nav bar|navigation bar|search page|help page|list/detail|list/detail/edit|user-list|user-edit|frontend/src/screens|shopping-cart|purchase-request" <edited-files>

# Whole-pack active-guidance pass for Sprint 05; specs may be included for provenance review but should not drive installed-pack guidance.
rg -n "User Admin workstream v0|User Admin-only|generic chatbot|chatbot shell|page tree|screen tree|page-first|screen-first|CRUD-first|CRUD screen|CRUD module|resource-first|/api/<resource>|nav bar|navigation bar|search page|help page|list/detail|list/detail/edit|user-list|user-edit|frontend/src/screens|shopping-cart|purchase-request" skills docs pack templates --glob '!docs/examples/purchase-request-app-description/**'
```

Also search exact path references before deletion, for example:

```bash
rg -n "app-description-skills-plan-backlog|purchase-request-prd|purchase-request-app-description|purchase-request-solution-plan|purchase-request-pending-tasks" . --glob '!specs/requirements-intake-alignment-cleanup/**'
```

## Pass/fail rules for future cleanup tasks

A future cleanup task passes when:

- it edits only the task-scoped files plus the queue/status artifact;
- edited guidance starts from the canonical alignment chain or clearly references canonical docs for that chain;
- legacy mechanics are labeled or demoted instead of silently preferred;
- stale-term hits in edited files are reviewed and either removed, quarantined, or justified;
- deleted/renamed files have reference checks and replacement links;
- `git diff --check` passes;
- the task records completion in `pending-tasks.md` and commits only intended changes.

A task should block or append follow-up work instead of guessing when:

- a file's owner or canonical replacement is unclear;
- removing a file requires broad reference repair that exceeds the task;
- an active skill would still route broad generated-SaaS input to page/resource/component-only planning after the bounded edits;
- a generated-app task shape lacks workstream, surface/action, capability, AuthContext, audit/trace, or runtime-validation context.
