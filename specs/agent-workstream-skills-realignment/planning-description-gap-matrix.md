# Planning / Description Gap Matrix: Agent Workstream Alignment

## Scope

Audit for `TASK-AWSR-02-001`. Reviewed description-first skills, PRD/spec/backlog planning guidance, app-description architecture/maintenance docs, and the Sprint 01 routing review.

Canonical model for generated full-stack AI-first SaaS planning remains:

```text
secure SaaS foundation
→ functional/context-area agents
→ durable workstreams
→ typed structured surfaces and surface actions
→ governed backend capabilities
→ horizontal Akka/frontend/test implementation
```

## Summary

The app-description guidance is now mostly aligned with the workstream model. The strongest sources are `app-descriptions`, `app-description-functional-agent-modeling`, `app-description-surface-modeling`, `app-description-ui`, `docs/internal-app-description-architecture.md`, and the review checklist. They make `12-workstreams/` authoritative for functional agents, internal agents, durable workstreams, structured surfaces, action-to-capability mappings, trace semantics, and tests, while `55-ui/` owns browser realization.

The remaining Sprint 02 risk is concentrated in two areas:

1. **Update-order ambiguity in description maintenance** — some flow text still leads with capabilities or generic UI maintenance before explicitly naming the functional-agent/surface layer. The individual companion skills are well aligned, but an agent following only the maintenance flow could still update `10-capabilities/` or `55-ui/` without first creating or checking the `12-workstreams/` contract.
2. **PRD/backlog verticality gap** — `akka-prd-to-specs-backlog` has strong foundation and capability guidance, but its master plan, sprint/slice, backlog, and pending-task contracts do not yet consistently require explicit functional-agent inventories, structured-surface payload/action contracts, and surface-action-to-capability maps before component/task breakdown.

No urgent rewrite of all app-description skills is needed. Sprint 02 should make targeted source edits that turn the existing aligned doctrine into unavoidable output contracts.

## Matrix

| File / skill | Current alignment status | Gap / drift risk | Recommended Sprint 02 update | Priority |
|---|---|---|---|---|
| `skills/app-descriptions/SKILL.md` | Strong. Default flow already routes generated SaaS through functional-agent modeling, surface modeling, then capability modeling. Core rules make `12-workstreams/` primary and page/screen hierarchy subordinate. | Minor ambiguity: the layer model lists `10-capabilities/` before `12-workstreams/`, and the flow has both capability-first and workstream-first language. A future agent could over-read the layer order as an instruction to define backend capabilities before checking functional-agent/surface ownership. | Add a compact invariant: for generated full-stack SaaS, before changing `10-capabilities/`, `55-ui/`, readiness, or generation scope, verify whether `12-workstreams/functional-agents.md`, `surfaces-index.md`, and relevant `surface-contracts/**` must change. Keep capability contracts authoritative for backend behavior, but require workstream/surface context for user-facing exposure. | P1 |
| `skills/app-description-intake-router/SKILL.md` | Mostly aligned. It defaults to description maintenance, detects AI-first semantics, extracts capability/UI/security/test deltas, and routes dashboards/work areas through functional-agent and surface skills via the top-level `app-descriptions` flow. | The `Routing rules` section can route capability changes directly to `app-description-capability-modeling` without an explicit pre-check for affected functional agents, surfaces, or surface actions. UI is listed as a delta category, but the output contract does not require workstream/surface candidates. | Add a routing pre-check for generated SaaS: when input mentions dashboards, portals, work queues, admin consoles, agent/chat areas, browser actions, approvals, decisions, audit timelines, or workflow status, identify candidate functional agents and surfaces before capability/UI routing. Extend the output contract with `functional agents`, `surfaces/actions`, and `surface-action capability candidates`. | P1 |
| `skills/app-description-functional-agent-modeling/SKILL.md` | Strong. It defines functional agents as vertical role-authorized work areas, not Akka components, chat sessions, pages, or generic assistants. It captures authority, surfaces, capabilities, traces, and tests. | No blocking gap. Potential refinement only: ensure it remains the first companion loaded for user-facing work areas even when the user's vocabulary is `dashboard`, `portal`, or `admin screen`. | In `TASK-AWSR-02-002`, consider adding one sentence under handoff/anti-patterns: UI nouns should be normalized into functional-agent ownership plus reusable surfaces before route/page details. | P2 |
| `skills/app-description-surface-modeling/SKILL.md` | Strong. It defines surfaces as typed renderable artifacts, requires payloads/actions/states/auth/traces/tests, and maps every action to governed capabilities. | No blocking gap. | Keep as canonical for surface contracts. If edited, only tighten cross-link wording to traceability maps and PRD task output requirements. | P2 |
| `skills/app-description-capability-modeling/SKILL.md` | Strong for backend capability contracts. It records actors/callers, AuthContext, schemas, side effects, idempotency, approval, audit, exposure surfaces, tests, and AI-first semantics. | It is intentionally capability-centered, so it can be selected without enough workstream/surface context. Handoff rules mention functional agents and UI, but the initial capture list does not explicitly require `source functional agent`, `source surface/action`, or `surface-to-capability-map` when exposure is user-facing. | Add a generated-SaaS rule: user-facing capability changes must record which functional agents, workstream actions, structured surfaces, and surface actions expose or consume the capability, or explicitly state internal-only/no human surface. Add these fields to the standard output shape. | P1 |
| `skills/app-description-ui/SKILL.md` | Strong. It explicitly keeps `55-ui/` as browser realization, treats routes/deep links as subordinate, points to the workstream UI reference architecture, and blocks page/screen-first realization. | Mostly aligned. The `Use this skill when` section directly handles UI vocabulary, but future agents may still jump to UI descriptions for dashboards/forms before updating `12-workstreams/` if they skip the change handling section. | Add or retain a high-visibility rule near the top: UI changes that create or alter user-facing work areas, surfaces, or actions must first update or verify `12-workstreams/` and `10-capabilities/`; `55-ui/` may only add rendering/route/API details. | P1 |
| `skills/akka-prd-to-specs-backlog/SKILL.md` | Partially aligned. It strongly enforces secure SaaS foundation, full-core scope labels, capability fields, pending questions/tasks, and bounded task queues. It mentions functional-agent/surface context in several places. | Main Sprint 02 gap. Required reads still omit `agent-workstream-apps`, `docs/agent-workstream-application-architecture.md`, and `docs/structured-surface-contracts.md`. The master plan section jumps from AI-first/core foundation to capability inventory without explicit `Agent workstream model`, `Structured surfaces`, and `Surface action-to-capability map` sections. Sprint/slice/backlog/task contracts can still be generated as capability/component slices rather than vertical workstream/surface/capability increments. | Add required reads for workstream/surface doctrine. Require solution plans, module specs, sprint/slice specs, build backlogs, task briefs, and pending tasks to include: functional-agent scope, structured surfaces, surface actions, action-to-capability ids, AuthContext, side effects, audit/trace, frontend realization, Akka substrate, and tests before component breakdown. Update anti-patterns/final checklist to reject vague module/page/component tasks. | P0 |
| `docs/internal-app-description-architecture.md` | Strong. It already declares functional agents/internal agents → workstreams/surfaces → capabilities → implementation maps, defines `12-workstreams/`, and states `55-ui/` links back rather than redefining meaning. | No blocking gap. Layer update guidance later says identify impacted capabilities before updating `12-workstreams/`; that is semantically reasonable for capability changes but can be read as capability-first for all changes. | Optional doc refinement: clarify that impact analysis may start from user intent, but generated SaaS user-facing work must reconcile `12-workstreams/` before `55-ui/` or horizontal implementation maps are considered ready. | P2 |
| `docs/app-description-maintenance-flow.md` | Mostly aligned, but weaker than the companion skills. It seeds workstreams and UI, but the default flow currently says update `10-capabilities/` before `15-operating-model/`, behavior, tests, and UI; it does not explicitly insert `12-workstreams/` in the layer update order. | This is the largest app-description doc drift. It can teach a future agent to maintain capabilities and UI without an explicit functional-agent/surface pass, even though the architecture doc and skills say otherwise. | In `TASK-AWSR-02-002`, update the flow to include a workstream/surface step before or alongside capability updates for generated SaaS: identify functional agents, internal agents, surfaces, surface actions, and action-to-capability mappings. Add `12-workstreams/` to the layer update order between `10-capabilities/` and `15-operating-model/`, with a note that capability and surface modeling may iterate together. | P0 |
| `docs/agent-workstream-design-review-checklist.md` | Strong. It is the clearest audit checklist for Sprint 02 acceptance. | No gap. | Use it as the final review checklist for Sprint 02 edits and sprint review. | P2 |

## Page/screen/generic-UI drift findings

- **Canonical guidance is no longer page-first.** The reviewed source consistently states that routes, pages, screens, and deep links are subordinate realization details.
- **Legacy vocabulary still appears intentionally.** `screens`, `pages`, `dashboards`, `portals`, and `admin consoles` appear as user vocabulary to normalize, not as the primary generated-SaaS architecture. This is acceptable when paired with functional-agent/surface routing.
- **Residual risk is placement, not terminology.** The main problem is not that page/screen words exist; it is whether output contracts force the agent to produce functional-agent and surface artifacts before UI realization or component tasks.
- **PRD/backlog output needs the strongest correction.** Planning artifacts are where vague `build dashboard`, `add admin module`, `implement all entities`, or `create UI pages` tasks can reappear unless the task contract explicitly requires functional agent, surface/action, capability id, Akka substrate, frontend work, and tests.

## Recommended Sprint 02 changes

### For `TASK-AWSR-02-002` — app-description ownership alignment

1. Update `docs/app-description-maintenance-flow.md` so generated SaaS change flow explicitly includes `12-workstreams/` before UI realization and before implementation planning is considered ready.
2. Add a high-visibility generated-SaaS invariant to `skills/app-descriptions/SKILL.md`: user-facing changes must verify functional-agent ownership, surface contracts, surface actions, and action-to-capability traceability.
3. Tighten `skills/app-description-intake-router/SKILL.md` to output candidate functional agents and structured surfaces/actions when user input uses UI/work-area vocabulary.
4. Tighten `skills/app-description-capability-modeling/SKILL.md` to require functional-agent/surface/action links for user-facing capability exposure or an explicit internal-only declaration.
5. Optionally add a near-top guardrail to `skills/app-description-ui/SKILL.md` that `55-ui/` never creates application meaning not already owned by `12-workstreams/` and `10-capabilities/`.

### For `TASK-AWSR-02-003` — PRD/spec/backlog vertical planning alignment

1. Add required reads to `skills/akka-prd-to-specs-backlog/SKILL.md`:
   - `../agent-workstream-apps/SKILL.md`
   - `../../docs/agent-workstream-application-architecture.md`
   - `../../docs/structured-surface-contracts.md`
   - `../../docs/agent-workstream-design-review-checklist.md`
2. Add master solution plan sections before capability/component mapping:
   - `Agent workstream model`
   - `Structured surfaces and surface actions`
   - `Surface action-to-capability map`
3. Require every sprint/slice/backlog to preserve vertical scope:
   - functional agent(s)
   - durable workstream item or surface(s)
   - surface action(s) with capability ids
   - AuthContext / tenant/customer scope
   - side effects / idempotency / approval / audit
   - Akka substrate and frontend/API realization
   - tests
4. Update queue/task rules so a pending task is not implementation-ready if it names only a component, page, module, or generic UI feature without the workstream/surface/capability contract.

## Sprint 02 acceptance checks

After `TASK-AWSR-02-002` and `TASK-AWSR-02-003`, run these text/semantic checks:

```bash
rg -n "12-workstreams|55-ui|functional agent|structured surface|surface action|capability" \
  skills/app-descriptions/SKILL.md \
  skills/app-description-intake-router/SKILL.md \
  skills/app-description-capability-modeling/SKILL.md \
  skills/app-description-ui/SKILL.md \
  docs/app-description-maintenance-flow.md \
  skills/akka-prd-to-specs-backlog/SKILL.md
```

Review the touched files against `docs/agent-workstream-design-review-checklist.md`, with special attention to:

- `12-workstreams/` owns functional agents, surfaces, action mappings, traces, and tests.
- `55-ui/` owns browser realization and routes/deep links only.
- PRD-generated specs/backlogs/tasks cannot be vague module/page/component slices.
- Every user-facing action maps to a governed capability with AuthContext, side effects, idempotency, approval/audit, and tests.
