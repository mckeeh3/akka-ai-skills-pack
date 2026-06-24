---
name: akka-solution-decomposition
description: Decompose high-level requirements, prompts, or specification files into the minimal Akka Java SDK component set, then route to the focused skills needed to implement the design.
---

# Akka Solution Decomposition

Context-budget rule: do not load every focused Akka component skill up front. Classify scope, derive capabilities, choose component candidates, then load only the focused downstream skills needed for the selected substrates.

Use this as the top-level starting skill when the task begins from accepted current intent or high-level intent rather than from a fixed Akka component type. For broad or changing product input, first treat the input as an intent compiler increment and preserve traceability to current app/global/domain/workstream nodes before selecting Akka components.

## Goal

Produce a compact implementation contract that compiles accepted intent through the canonical chain:

```text
worker
→ execution harness
→ actor adapter
→ governed tool
→ capability
→ Akka implementation
```

The plan must:

- interpret product intent through the secure AI-first SaaS operating model when generated-app scope is in play;
- derive governed backend capabilities before selecting Akka components or exposure channels;
- map capabilities and governed workstream tools to the smallest component set that preserves durability, authority, orchestration, query, timing, integration, AI, actor-adapter exposure, and edge semantics;
- label lifecycle/readiness scope before planning implementation;
- route to the smallest relevant skill set for code and tests;
- give vertical implementation order, not just a component list.

## Supported inputs

Use this skill for high-level prompts, PRDs, feature requests, UI/API briefs, change requests, or filenames containing requirements/specifications. If the user provides a path, read it completely and extract actors, capabilities, constraints, inputs, outputs, authority, side effects, state, queries, integrations, and tests before choosing components.

For broad generated-app input, use these by reference instead of restating their contents:

- `../docs/intent-compiler.md`
- `../docs/current-intent-model.md`
- `../docs/incremental-intent-processing.md`
- `../docs/intent-to-realization-flow.md`
- `../docs/requirements-to-workstream-development-process.md`
- `../docs/examples/requirements-to-workstream-mini-example.md`

Do not reintroduce removed historical domain-specific planning examples as generic guidance.

## Required reading

Read first when present/relevant:

- target project path: AGENTS.md for authoritative project rules and coding constraints;
- `../README.md` for current skill routing;
- `../docs/app-development-lifecycle.md` for interview/build/manual readiness vocabulary;
- `../docs/app-worker-tool-model.md` for the worker/harness/adapter/governed-tool/capability/Akka separation;
- `../docs/app-description-to-code-compile-contract.md` before treating any task as implementation-ready;
- `../core-saas-foundation/SKILL.md` for mandatory secure SaaS scope;
- `../docs/intent-compiler.md`, `../docs/current-intent-model.md`, and `../docs/intent-to-realization-flow.md` when input needs current-intent provenance or workstream binding before component selection;
- `../docs/full-core-foundation-readiness.md` for canonical SaaS Foundation App inventory; summarize it, do not paste it;
- `../docs/minimum-ai-first-saas-app.md` for minimum/core/basic/chatbot-like generated SaaS requests;
- `../docs/ai-first-saas-application-architecture.md`;
- `../agent-workstream-apps/SKILL.md` and `../docs/agent-workstream-application-architecture.md` when generated full-stack SaaS workstreams are in scope;
- `../docs/workforce-decomposition.md` and `../ai-first-saas-worker-decomposition/SKILL.md` when human/agent/system workers, authority, handoffs, or supervision are in scope;
- `../docs/structured-surface-contracts.md` when surfaces/actions/events are in scope;
- `../capability-first-backend/SKILL.md` and `../docs/capability-first-backend-architecture.md` before backend component selection;
- `../docs/agent-coverage-matrix.md` when agent runtime/governance coverage is in scope;
- `../references/akka-entity-comparison.md`, `../docs/workflow-endpoint-pattern.md`, and `../docs/timer-pattern-selection.md` as needed;
- `../references/generated-saas-runtime-completion.md` for generated-app completion standards;
- official Akka docs under `akka-context/sdk/**` for each selected component family before coding.

## Output: component plan sections

Before coding, produce a component plan with these sections. Keep each concise; link canonical references instead of duplicating checklists.

1. Inputs
2. Java base package: fixed `ai.first`
3. Scope label: `SaaS Foundation App maintenance/extension`, `business-domain extension`, `app-specific feature`, or another explicit narrower scope
4. AI-first interpretation
5. Core secure SaaS foundation obligations
6. Workstream decomposition decision
7. Agent workstream model and retained human authority
8. Workforce decomposition: human, functional-agent, internal/autonomous/evaluator agent, and system workers
9. Attention/dashboard model
10. Human surface graph and surface actions as human-worker harnesses, not authorization boundaries
11. Surface/action-to-capability and governed-tool mapping, including actor adapters/exposure channels (`surface_action`, `human_chat_tool_plan`, `agent_tool_call`, API/workflow/timer/consumer/MCP/internal), confirmation/approval behavior, idempotency/transaction boundary, result/partial-failure surface, and trace source
12. Internal agent/autonomous-task candidates
13. Workstream expertise plan
14. Capability and governed-tool inventory
15. Capability-to-component mapping
16. Chosen components and why each exists
17. Compile contract inherited by the implementation task: graph nodes, workers, harnesses, adapters, governed tools, capabilities, traces, selected substrates, checks, and manual scenario or non-runtime exemption
18. Skill routing for implementation and tests
19. Open questions/assumptions
20. Vertical implementation order
21. Required tests

A plan is incomplete if it names components without saying which current-intent graph nodes, functional agent, workstream, surface, capability, authority boundary, trace, and tests each increment belongs to.

## Decomposition workflow

### 1. Record fixed package and scope

Use `ai.first`; do not ask the user to choose a Java package. Classify the requested work with `../docs/minimum-ai-first-saas-app.md` and `../docs/full-core-foundation-readiness.md`:

- `SaaS Foundation App maintenance/extension`: modify the built-in foundation domain in place.
- `business-domain extension`: add domain-specific workstreams, surfaces, agents, capabilities, components, frontend/app-description/spec/doc/test assets while preserving foundation semantics.
- `app-specific feature`: a focused increment in an existing foundation or business domain.
- narrower scope: name it and list intentionally deferred areas.

### 2. Apply secure SaaS foundation

For generated SaaS work, load `core-saas-foundation` early unless the user explicitly asks for non-SaaS reference material. WorkOS/AuthKit and Resend are the supported foundation choices. Missing runtime values may become questions, but must not erase local authorization, tenancy, managed-agent governance, trace, or tool-boundary contracts.

Route complete invitation onboarding to `akka-saas-invitation-onboarding`; reusable app/agent email delivery to `akka-resend-email-service`; user/admin foundation to `akka-basic-user-admin` and `akka-workos-user-auth`.

### 3. Interpret AI-first operating model

Before CRUD or component decomposition, extract:

- human objective, owner, success criteria, constraints, definition of done;
- delegated work vs retained human authority;
- goals, plans, tasks, policies, decisions, approvals, exceptions, traces, outcomes;
- worker roster: human workers, functional-agent workers, internal/autonomous/evaluator agent workers, and system workers;
- agent/team responsibilities, tools, permissions, thresholds, escalation rules;
- supervision, decision, governance, digest, audit, and outcome UI needs.

If the product is clearly non-agentic, say so and continue with secure foundation-first Akka decomposition. Do not force optional AI-first patterns beyond the mandatory generated-app foundation.

### 4. Model workstreams and surfaces before backend components

For generated full-stack SaaS, apply `agent-workstream-apps` before backend mapping. Identify functional/context-area agents, workstreams, workforce roster, worker responsibility/authority/handoff map, attention categories, role dashboards, human surface graph nodes/actions, system-message/result surfaces, trace links, workstream expertise bundles, internal/background worker candidates, and notification/projection needs.

If this inventory is absent, stop and add it or record a blocking gap.

### 5. Derive capabilities and governed tools

For each operation/query/action/event/tool, capture:

- responsible worker and execution harness before naming an adapter;
- stable capability id/name and governed-tool id when executable;
- class: read/evidence, command, proposal, approval, workflow, policy/governance, trace/audit, scheduled, reactive, task lifecycle, projection read, integration;
- actors/callers, actor adapter/exposure channel (`surface_action`, `human_chat_tool_plan`, `agent_tool_call`, API/workflow/timer/consumer/MCP/internal), AuthContext, tenant/customer scope, roles/capabilities, denial shape;
- input/output schemas, validation, redaction, confirmation binding, approval policy, idempotency, transaction boundary, correlation;
- data reads/writes, side effects, external calls, topics, timers, emails, notifications;
- policy/approval/escalation, audit/work-trace obligations, exposure channels;
- browser UI, agent/tool, workflow/timer/consumer/MCP implications;
- required success/forbidden/tenant-isolation/idempotency/approval/audit/UI tests.

Only after this should the plan choose Akka components. If the task names only a page, route, endpoint, component method, or `@FunctionTool`, repair the compile inputs or block instead of inventing the worker/tool/capability contract.

### 6. Select Akka substrates by capability shape

Use this quick guide, then route to focused skills:

| Capability shape | Likely Akka substrate | Focused routing |
|---|---|---|
| Audit-grade facts/history, decisions, policies, approvals | EventSourcedEntity | `akka-event-sourced-entities`, ESE focused skills |
| Latest-state record, profile/settings/config, simple repository | KeyValueEntity | `akka-key-value-entities`, KVE focused skills |
| Long-running plan, approval, retry, compensation, waiting | Workflow | `akka-workflows`, focused workflow skills |
| Query/search/reporting/alternate lookup | View | `akka-views`, source/query/testing skills |
| React to entity/workflow/topic/service-stream changes | Consumer | `akka-consumers`, source/producing/testing skills |
| Deadline/reminder/expiry/recheck | Timer + TimedAction | `akka-timed-actions` |
| Browser/API exposure | HTTP endpoint | `akka-http-endpoints`, JWT/component/request/testing skills |
| Protobuf service boundary | gRPC endpoint | `akka-grpc-endpoints` |
| AI-client tool/resource/prompt boundary | MCP endpoint | `akka-mcp-endpoints` |
| Request/response model-backed turn | Akka Agent | `akka-agents`, focused agent skills |
| Durable internal/background model-driven task | AutonomousAgent | `akka-autonomous-agents`, task/coordination/testing skills |
| Browser workstream app | React/Vite + Akka HTTP hosting | `akka-web-ui-apps`, focused web UI skills |

Rules:

- Route to `akka-entity-type-selection` if stateful scope exists but ESE vs KVE is not clear.
- Do not add a workflow for a simple single-entity command.
- Do not add a view unless query shape justifies a projection.
- Keep one shared authority, validation, idempotency, approval, and audit contract across all exposure channels.

### 7. Add implementation order

Prefer vertical increments:

1. foundation/auth/tenancy/audit needed by the selected scope;
2. workstream/workforce/attention/dashboard/surface contract;
3. capability contracts and governed tools;
4. write model;
5. query/projection model;
6. orchestration/timers/consumers;
7. agent/runtime/tool governance;
8. endpoints/API clients;
9. frontend surface rendering;
10. tests and local smoke/manual validation.

Each increment should identify the next focused skills to load.

## Open questions policy

Ask only questions that materially block safe component choice or implementation. For generated AI-first SaaS, unresolved current-intent graph ownership, authority boundaries, approval gates, policy/risk thresholds, evidence, trace visibility, supervision UI, model binding, skill/reference governance, tool boundaries, or outcome metrics block only the affected slice.

## Anti-patterns

- Component-first planning before capabilities and authority.
- Page-first generated SaaS planning instead of workstream/surface/capability planning.
- Treating the skills install as app source.
- Copying curated examples wholesale as a baseline.
- Marking fixture/demo/model-less runtime behavior as implemented; see `../references/generated-saas-runtime-completion.md`.
- Adding all Akka component families just because they exist.
- Asking for Java package selection; use `ai.first`.

## Final review checklist

Before handoff, verify:

- scope label is explicit;
- fixed package `ai.first` is recorded;
- secure SaaS foundation obligations are included or explicitly out of scope;
- lifecycle/readiness target and compile contract are explicit;
- workstreams/surfaces/capabilities/governed-tool catalogs and actor adapters precede component choices;
- every chosen component has a reason and focused skill route;
- authority, confirmation/approval, idempotency, transaction boundary, side effects, partial-failure/result surfaces, traces, and tests are explicit;
- open questions are minimal and tied to blocked work;
- implementation order is vertical and runnable through the intended local Akka/API/UI path.

## Response style

Be decisive and concise. Prefer a concrete component table plus implementation order. If requirements are underspecified, state assumptions and ask the smallest blocking question set rather than producing a speculative design.
