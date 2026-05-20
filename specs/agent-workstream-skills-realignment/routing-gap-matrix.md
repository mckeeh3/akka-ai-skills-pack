# Routing Gap Matrix: Agent Workstream Alignment

## Scope

Audit for `TASK-AWSR-01-001`. Reviewed source routing docs/skills and spot-checked installed `.agents/skills/**` copies for the top-level entry path.

Canonical routing target:

```text
secure SaaS foundation
→ functional/context-area agents
→ durable workstreams
→ typed structured surfaces
→ governed backend capabilities
→ horizontal Akka implementation
```

## Summary

The top-level source routing is mostly aligned: `skills/README.md`, `agent-workstream-apps`, `capability-first-backend`, `akka-solution-decomposition`, and `akka-prd-to-specs-backlog` now name the workstream/surface/capability sequence. The remaining risk is not absence of doctrine; it is that several entry skills still emphasize their older primary concern first and only later mention the workstream handoff. A future agent can still plausibly select `ai-first-saas`, `capability-first-backend`, or `akka-solution-decomposition` and proceed with AI-first objects, capabilities, or Akka components without producing an explicit functional-agent and surface inventory.

## Matrix

| File / skill | Current alignment status | Gap | Recommended change | Priority |
|---|---|---|---|---|
| `skills/README.md` | Strongly aligned. It states generated SaaS apps are agent workstream applications, lists mandatory shell/surfaces, and orders secure foundation → workstreams → capabilities → implementation. Installed `.agents/skills/README.md` carries the same substantive routing with installed-pack path wording. | Long file with multiple entry sections; the most important sequence is present but could be diluted by later stage/component lists. | In follow-up, keep this as the canonical top-level source and add/retain short assertions in downstream skills rather than expanding this file further. If edited, prefer a compact “do not bypass workstreams” sentence near Stage 1 and PRD/backlog routing. | P1 |
| `skills/ai-first-saas/SKILL.md` | Partially aligned. It frames delegated work, durable goals/plans, governance, decisions, traces, and routes to downstream skills. | The `Goal` and interpretation workflow can still read as AI-first operating-model first, then direct substrate/component mapping. It does not explicitly require an `agent-workstream-apps` handoff before capability/component planning for generated full-stack SaaS. | Make `agent-workstream-apps` the explicit default downstream handoff after `core-saas-foundation` for generated SaaS apps. Require output to include functional agents, workstreams, structured surfaces, and surface-action capability candidates before Akka substrate routing. | P0 |
| `skills/agent-workstream-apps/SKILL.md` | Strongly aligned. It is the clearest statement of functional agents → workstreams → surfaces → capabilities → horizontals and warns against page-first/CRUD/chatbot defaults. | Minor: it describes itself as used “after secure AI-first SaaS foundation framing,” but some top-level users may enter through AI-first or decomposition and never load it. | Keep as the canonical workstream routing skill. Ensure `ai-first-saas`, `capability-first-backend`, `akka-solution-decomposition`, and PRD/backlog skills explicitly route through it for generated full-stack SaaS. | P0 |
| `skills/capability-first-backend/SKILL.md` | Mostly aligned. It states capability-first follows secure AI-first SaaS interpretation and the agent workstream model, and that capabilities are root backend design objects. | The skill title/core rule can be misread as allowing capabilities to replace workstream modeling. Required reading does not include `agent-workstream-apps`, `agent-workstream-application-architecture.md`, or `structured-surface-contracts.md`, so a harness could derive capabilities from product operations without first naming functional agents/surfaces. | Add required reading/routing for generated SaaS: load `agent-workstream-apps`, `agent-workstream-application-architecture.md`, and `structured-surface-contracts.md`; derive capabilities from functional-agent workstream actions, structured surface payload/query/action contracts, agent tools, workflows, APIs, timers, consumers, and internal calls. | P0 |
| `skills/akka-solution-decomposition/SKILL.md` | Mostly aligned. It requires secure AI-first SaaS interpretation, capability summary, component mapping, UI surfaces, and workstream UI handoff. | Required output sections do not separately require a functional-agent/workstream/surface inventory before `Capability summary`. Section ordering jumps from AI-first interpretation/core foundation to capabilities, so generated SaaS plans can still skip explicit functional agents/surfaces. | Add required output sections for `Agent workstream model` and `Structured surfaces and surface actions` before capability summary. Make the decomposition workflow explicitly load/use `agent-workstream-apps` for generated SaaS input before capability mapping. | P0 |
| `skills/akka-prd-to-specs-backlog/SKILL.md` | Mostly aligned. It preserves functional-agent/surface context in pending tasks and requires vertical full-stack work, but much of the concrete content remains solution-plan/backlog oriented. | PRD materialization guidance can still produce module/sprint artifacts around capabilities/components if the workstream model is only implicit. Required reads do not explicitly include `agent-workstream-apps` or the workstream/surface docs. | Add required reading/routing through `agent-workstream-apps`, `docs/agent-workstream-application-architecture.md`, and `docs/structured-surface-contracts.md`. Require solution plans, sprint specs, backlogs, and task queues to include functional agents, surfaces, surface actions, and action-to-capability mappings before component/task breakdown. | P1 |
| `.agents/skills/*` installed copies | Substantively aligned with source for reviewed skills. Differences are installed-pack path wording and source-vs-installed Akka context/example locations, not routing semantics. | Installed copies are generated/installed output and gitignored; source edits are required before reinstall/export refresh. | Do not edit `.agents/` directly. After source changes in later tasks, reinstall/export and spot-check installed copies for the same routing semantics. | P2 |
| `docs/agent-workstream-application-architecture.md` | Strongly aligned and should remain the canonical generated-app UI/application doctrine. | No blocking gap. It is not always named in required reads for the skills that can bypass it. | Use it as required reading in `capability-first-backend` and PRD/backlog routing for generated SaaS work; keep linked from decomposition and README. | P1 |
| `docs/structured-surface-contracts.md` | Strongly aligned as an implementation contract for surfaces/actions/events/capability mapping. | Not always required before planning/output generation that names UI or API tasks. | Require it where plans/backlogs/decompositions must produce surface payload/action contracts, especially `capability-first-backend`, `akka-solution-decomposition`, and `akka-prd-to-specs-backlog`. | P1 |
| `docs/agent-workstream-design-review-checklist.md` | Strong review guardrail with pass/fail checks for functional agents, `12-workstreams` ownership, surfaces, capabilities, and legacy quarantine. | Review checklist exists but is not yet embedded as a final checklist in all top-level routing/planning skills. | Reference it in follow-up routing and sprint review tasks; use as acceptance criteria for edited top-level skills. | P2 |

## First routing changes to make

1. **Align `ai-first-saas` and `agent-workstream-apps` relationship**: make generated SaaS routing explicitly pass from secure foundation + AI-first operating model into the agent workstream model before capabilities/components.
2. **Align `capability-first-backend` required reads and workflow**: require workstream/surface context before capability inventory for generated SaaS, so capability-first cannot become workstream-bypass.
3. **Align `akka-solution-decomposition` output shape**: add explicit functional-agent/workstream and structured-surface sections before capability summary and component mapping.
4. **Align `akka-prd-to-specs-backlog` materialization**: ensure specs/backlogs/tasks preserve functional-agent, surface, and surface-action-to-capability structure before Akka task breakdown.

## Source/installed-pack spot check

Installed `.agents/skills/**` copies differ from source only in expected installed-pack wording for `AGENTS.md`/`README.md` references and, for solution decomposition, Akka context/example paths. No installed-only routing gap was found in the reviewed top-level files. Future source edits should be propagated through the normal pack install/export process rather than direct `.agents/` edits.
