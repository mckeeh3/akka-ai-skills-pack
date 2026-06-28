---
name: app-description-input-normalization
description: Normalize flexible user input into a current-intent delta envelope covering affected app/domain/workstream/global graph nodes, auth/security, traces/tests, realization implications, and blocking ambiguities before routing or maintenance.
---

# App Description Input Normalization

Use this skill when the harness needs to turn flexible user language into a consistent current-intent delta before routing, maintenance, impact analysis, planning, or generation decisions.

This skill is a **normalizer** in the intent compiler. It does not primarily decide the final next step; it converts messy input into a stable representation that downstream skills can safely compile.

## Lifecycle classification

- Phase: interview.
- Kind: normalizer.
- Family: app-description.
- Living-graph contract: normalization expresses flexible input as a current-intent graph delta with worker, execution-harness, actor-adapter, governed-tool, capability, trace, test, readiness, and realization implications visible before routing.
- Build/compile handoff: when normalized input includes planning, implementation, code, tests, or validation intent, identify whether the slice can proceed through `../docs/app-description-to-code-compile-contract.md` or must return to description work first.

## Goal

Produce a normalized result that:

- preserves the user's intended current state in structured form
- classifies intent kind and operation
- identifies affected app, global, domain, and workstream graph nodes, including worker, execution-harness, actor-adapter, governed-tool, capability, trace, test, and realization implications
- separates confirmed statements from inferred candidate deltas
- captures auth/security, policy, trace, test, and realization implications
- records ambiguity explicitly instead of hiding it
- avoids carrying historical clutter into current intent artifacts
- gives downstream skills a stable basis for routing and maintenance

## Required reading

Read only what the input requires:

- target project path: `AGENTS.md`, when present
- `../README.md`, when present
- `../docs/intent-compiler.md`
- `../docs/current-intent-model.md`
- `../docs/incremental-intent-processing.md`
- `../docs/intent-compiler-skill-contracts.md`
- `../docs/app-development-lifecycle.md` when the input mixes interview, build/compile, or runtime-validation concerns
- `../docs/runtime-validation.md` when the input describes runtime validation, manual/browser/API testing, setup prerequisites, or app-operation evidence
- `../docs/app-worker-tool-model.md` and `../docs/app-description-component-graph.md` when the input implies workers, harnesses, tools, capabilities, agents, surfaces, system triggers, or implementation readiness
- `../docs/app-description-to-code-compile-contract.md` when planning, generation, code, tests, or validation are requested
- `../docs/intent-to-realization-flow.md` when planning, generation, code, tests, or validation are requested
- current target-project `app-description/**`, `specs/**`, and pending question/task queues only when needed to identify affected graph nodes or blockers
- focused companion skills only after normalization identifies the route

For generated AI-first SaaS input, preserve secure SaaS foundation, runtime completion, tenant/customer scoping, backend authorization, governed agent/tool boundaries, audit/work traces, and frontend secret boundaries from the target project and focused skills.

## Use this skill when

The task sounds like:

- "interpret this request first"
- "normalize this input before updating the description"
- "extract the real deltas from this user prompt"
- "separate behavior, test, security, and generation intent"
- "turn this revision request into a structured maintenance input"

Use it especially when the input is broad, mixed across concerns, partly ambiguous, combines revision/review/realization intent, or likely affects more than one graph node.

## Core operating rule

Normalize the user's meaning without overcommitting beyond what they actually said.

A good normalized result:

- captures explicit requests faithfully
- restates the intended current behavior, not conversation chronology
- identifies probable implications carefully
- separates confirmed facts from assumptions
- leaves open questions visible

## What this skill must extract

From user input, derive as applicable:

- user increment summary
- primary and secondary intents
- intent kind: app, domain, workstream, worker, harness, actor_adapter, capability, surface, agent, tool, policy, trace, test, realization, code_change, validation, or repository maintenance
- operation: add, refine, replace, remove, reconcile, validate, or repair
- affected graph scope: app, global artifacts, domains, workers, capabilities, data/state objects, workstreams, and realization files
- worker candidates and responsibility boundaries, including human, functional-agent, internal-agent, autonomous-agent, evaluator-agent, and system workers
- functional-agent/workstream candidates, including count/boundary changes, owner roles, tenant/customer scope, and foundation vs domain-specific classification
- attention/dashboard needs, surface nodes/actions, reusable surface definitions, and workstream-specific surface bindings
- actor-adapter implications for each exposure path: `surface_action`, `human_chat_tool_plan`, `agent_tool_call`, `workflow_step`, `timer_invocation`, `consumer_reaction`, `mcp_tool_call`, `api_call`, or `internal_call`
- capability or governed-tool deltas, including candidate workers/callers, AuthContext, schemas, side effects, idempotency, approval/policy, audit/trace, and exposure surfaces
- agent/tool authority implications, including governed prompt/skill/reference, model, loader, tool-boundary concerns, and whether AI workers are explicitly allowed or denied each governed tool
- autonomous task candidates when durable internal/background model-driven work is implied
- system-worker implications for workflows, timers, consumers, projections, integrations, endpoint callers, and internal services when implied
- event/notification/projection/trace implications, including attention projection and audit/work trace candidates
- behavior, test, auth/security, UI, observability, readiness, generation, and review deltas
- explicit user constraints or preferences
- open questions created by ambiguity and whether each blocks safe compilation

## Normalized delta envelope

Return a compact envelope with these semantics when relevant:

```text
user_increment: <brief summary>
intent_kind: app | domain | workstream | worker | harness | actor_adapter | capability | surface | agent | tool | policy | trace | test | realization | code_change | validation | repository_maintenance
operation: add | refine | replace | remove | reconcile | validate | repair
scope:
  app: <name-or-current-app>
  domain: <domain-or-null>
  workstream: <workstream-or-null>
  global_artifacts: [<artifact refs>]
affected_artifacts: [<existing or intended paths>]
confirmed_delta: <current-state change explicitly requested>
inferred_candidate_delta: <careful implications, if any>
worker_tool_graph: <responsible workers, harnesses, actor adapters, governed tools, capabilities, and missing links>
auth_security: <roles, tenant/customer scope, trust boundaries, denials, approvals>
traces_tests: <audit/work traces, acceptance, regression, negative tests>
realization: <Akka/frontend/API/spec/task/runtime-validation implications>
review_or_generation_request: <none | review | readiness | planning | generate | implement>
next_transition: <done | partially-done-blocked | decomposed-to-tasks | blocked-before-work>
stop_reason: <completed | decomposed-to-tasks | blocked-by-product-question | blocked-by-security-question | blocked-by-runtime-config | blocked-by-provider-config | blocked-by-missing-reproduction | blocked-by-conflicting-intent | blocked-by-unsafe-assumption | blocked-by-failing-check | blocked-by-out-of-scope-dependency | runtime-validation-required>
ambiguities:
  - question: <question>
    blocks: <true/false and affected scope>
recommended_route: <next skill or action>
```

Keep file edits out of pure normalization unless the user explicitly requests maintenance and the next route is already safe.

## Normalization rules

- Prefer current-state phrasing over historical wording such as "previously" or "actually".
- Use global definitions plus workstream-specific bindings for reusable surfaces, agents, tools, policies, and traces.
- Preserve workstream centrality: access, workers, surfaces, agents, tools, capabilities, policies, traces, tests, and realization become meaningful at workstream scope.
- Preserve worker/tool separation: a page, route, prompt, endpoint, or Akka method is not a governed tool; a surface or agent runtime is a harness, not product authority.
- Ask only blocking questions; otherwise record assumptions and route to the next focused skill.
- Apply the app-developer input transaction contract: normalize input into a concrete next transition (`done`, `partially-done-blocked`, `decomposed-to-tasks`, or `blocked-before-work`) rather than stopping at analysis.
- For broad input, cascade from app/domain/cross-cutting impact to workstreams, workers, capabilities/tools, surfaces, Akka/frontend/API realization, and implementation/runtime-validation tasks until the next step would require guessing.
- Do not normalize directly into code-level tasks unless the user explicitly asks for realization and current intent is description-ready or the missing graph links are captured as blockers.

## Handoff rules

Route onward as needed:

- to `app-description-bootstrap` when the normalized intent creates a new current-intent graph
- to `app-description-intake-router` when routing is still needed after normalization
- to `app-description-functional-agent-modeling` and `app-description-surface-modeling` before direct capability/UI routing when broad generated-SaaS input names work areas, dashboards, queues, command centers, approvals, decisions, audit timelines, workflow status, forms, tables, actions, or agent/chat areas
- to `app-description-capability-modeling` when capability scope, actors, AuthContext, schemas, side effects, idempotency, approval, audit, exposure surfaces, or intended outcomes dominate
- to focused maintenance skills when normalization isolates a dominant graph-node delta clearly
- to `app-description-change-impact` when the input asks about affected areas, drift, or realization scope
- to `app-description-readiness-assessment` when the input asks whether generation is appropriate
- to planning/queue skills when the normalized delta is accepted current intent and the user asks to plan implementation

## Clarification policy

Ask only the smallest questions needed to reduce material ambiguity. Create or update pending questions when guessing would affect tenant/customer scope, workstream ownership, worker responsibility, actor/role authority, sensitive data, policy thresholds, approval gates, actor-adapter selection, agent/tool authority, acceptance criteria, package/app structure, or runtime validation.

## Anti-patterns

Avoid:

- collapsing mixed intent into a single simplistic label
- converting uncertain implications into confirmed requirements
- preserving obsolete alternatives in current intent artifacts
- losing user preference signals like phased scope or evaluation-only intent
- treating review questions as hidden generation requests
- bypassing workstream bindings for reusable global artifacts

## Final review checklist

Before finishing, verify:

- primary intent and operation are explicit
- affected app/global/domain/workstream graph nodes are identified when possible
- confirmed vs inferred deltas are separated
- workstream, worker, harness, actor-adapter, surface/action, governed-tool, capability, agent/tool, behavior, tests, security, UI, observability, and realization are separated
- ambiguities are recorded as open questions instead of guessed away
- recommended route is the smallest safe compiler step
- next transition and stop reason are explicit

## Response style

When answering:

- summarize the input briefly
- show the normalized intent and deltas clearly
- keep the structure compact but explicit
- make it usable as an immediate handoff to routing or maintenance skills
