# Conversation Capture: Requirements-to-Workstream Process Migration

## Source discussion summary

The user and assistant discussed a major architectural clarification for secure AI-first SaaS app generation:

- Workstream dashboards are situational-awareness surfaces.
- Most workstream dashboards should focus only on their specific workstream.
- My Account is the main exception: it aggregates high-level "things needing your attention" counts across accessible workstreams.
- The left rail attention indicator should show the number of attention items per workstream.
- "Things needing your attention" should become a first-class concept.
- A governed event/message backbone should drive the app, with human users and app participants producing and consuming commands, events, tasks, notifications, attention signals, and traces.
- Dashboards expand to surfaces; surfaces expand to backend capabilities and internal agents/workers.
- Given this structure, the skills pack can infer much of an app's initial implementation from workstream names/responsibilities.
- Akka Autonomous Agents significantly fill the previous "backend agents" gap by giving durable internal/background agent work a first-class Akka component model.

The discussion was captured and revised in:
- `docs/workstream-dashboard-attention-event-backbone-wip.md`

## Accepted process model

The generated-app development process should become:

```text
any input from the user or PRD
→ process into app concepts
→ divide large PRDs into one or more workstreams
→ for each workstream, start with what needs attention
→ define dashboard contents
→ dashboards link to surfaces or directly invoke actions
→ surfaces are composed of UI components and may invoke actions
→ workstream actions invoke APIs/governed capabilities
→ APIs/capabilities invoke Akka components
→ internal agents/workers, often Autonomous Agents, perform durable backend work
→ events/messages/notifications update dashboards, attention counts, and traces
```

The process should be more prescriptive than the earlier CRUD/event-driven framing. It should guide agents from input to implementation without requiring users to know the internal skill taxonomy.

## Human and agent worker model

Human users and internal agents are both participants/workers in business workflows. Both may be driven by a version of:

> What do I need to do next?

Human workers use dashboards, surfaces, decision cards, forms, approvals, queues, and workstream actions.

Internal/background AI workers should often be Akka `AutonomousAgent` tasks when work is durable, task-oriented, observable, cancellable, dependency-aware, or coordinated. Deterministic non-AI workers may be workflows, consumers, timed actions, entities, or deterministic services/capability executors.

Request-based Akka `Agent` remains the default for user-facing functional workstream turns and immediate/streamed responses.

## Accepted constraints

- Preserve secure SaaS foundation as mandatory for generated SaaS apps.
- Preserve capability-first backend contracts; dashboards and surfaces discover capabilities, but capabilities remain the backend authority boundary.
- Preserve workstream functional agents as the root authenticated app abstraction.
- Preserve structured surfaces as typed renderable artifacts, not informal pages or chat blobs.
- Do not let frontend controls, prompt text, task instructions, or notification streams grant authority.
- Do not replace all agents with Autonomous Agents.
- Generated app runtime completion still requires real local runtime/API/UI paths; deterministic/demo/model-less normal runtime substitutes are not acceptable.

## Review findings from current pack snapshot

A quick targeted review found that many assets already point in the right direction:

- `skills/README.md` has strong routing through secure AI-first SaaS, agent workstreams, structured surfaces, capability-first backend, and Autonomous Agents.
- `skills/ai-first-saas/SKILL.md` already says not to decompose into CRUD first and routes through agent workstreams.
- `skills/agent-workstream-apps/SKILL.md` already identifies functional agents, workstreams, default dashboards, surfaces, and capability mappings.
- `skills/akka-solution-decomposition/SKILL.md` already requires sections for AI-first interpretation, secure foundation, agent workstream model, surfaces/actions, capability mapping, and implementation order.
- `skills/akka-prd-to-specs-backlog/SKILL.md` already requires PRD extraction of AI-first signals, workstreams, surfaces, capabilities, and workstream expert bundles.
- Autonomous Agent skills and `docs/agent-component-selection-guide.md` already establish request-based Agent vs AutonomousAgent vs Workflow routing.

The likely gaps are not absence of concepts, but consistency, sequencing, and prescriptiveness:

- Normalization/intake may still treat capability/behavior/test/auth as the primary envelope before workstream attention/dashboard decomposition.
- PRD/spec/backlog docs may still describe examples as conventional decomposition mechanics rather than requiring workstream-attention-dashboard-first processing.
- Pending task/queue docs may need stronger fields for attention categories, dashboard/surface contracts, autonomous task definitions/results/notifications, and notification-to-surface behavior.
- Examples and app-description seed artifacts may need updates to demonstrate the full input → workstream → attention → dashboard → surfaces → capabilities → autonomous tasks chain.
- Residual mentions of CRUD/page-first/component-first/examples need an audit pass.

## Suggestions already identified

1. Create a compact canonical process doc for the requirements-to-workstream pipeline.
2. Promote dashboard/attention/autonomous-task concepts from WIP into canonical doctrine once stable.
3. Update input normalization and routing to extract workstreams and attention model before capability or component details.
4. Update PRD/spec/backlog generation to split large PRDs into workstreams and vertical dashboard/surface/capability/autonomous-task increments.
5. Update pending task contracts so implementation tasks preserve the vertical chain and do not regress to component-family tasks.
6. Add or update examples showing a PRD decomposed through the new process.
7. Run a final audit for stale CRUD-first, page-first, chatbot-bolt-on, event-only, or component-first process guidance.

## Open concerns

- Whether `docs/workstream-dashboard-attention-event-backbone-wip.md` should become canonical as-is, be renamed, or be split into focused docs.
- How much of the new process belongs in `agent-workstream-application-architecture.md` versus a new process-specific doc.
- How detailed the canonical `AttentionItem`, `WorkstreamAttentionSummary`, autonomous task progress/result surface, and notification mapping contracts should be in the first pass.
- How to avoid overloading intake/planning skills with too much prose while still making the process hard to bypass.
