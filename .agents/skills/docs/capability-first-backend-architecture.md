# Capability-First Backend Architecture

## Status and scope

This is the canonical capability-first backend doctrine for this skills pack. It extends the secure AI-first SaaS doctrine in `./ai-first-saas-application-architecture.md` without replacing it.

Default generated-application interpretation:

```text
product intent
→ mandatory secure SaaS foundation
→ requirements-to-workstream process: workstreams, workforce, attention, dashboards, surfaces/actions
→ agent workstream application model: role-authorized functional/context-area agents, worker rosters, workstreams, and structured surfaces
→ capability inventory
→ authority, scope, schemas, side effects, audit, approval, and supervision rules
→ selected capability exposure channels
→ Akka component realization
```

Use `./requirements-to-workstream-development-process.md` for broad input, PRD, app-description, planning, backlog, and implementation-readiness work. It discovers capabilities through workstream workforce, attention, dashboard, surface, and action semantics before selecting APIs or Akka components. Use `./app-worker-tool-model.md` for the canonical separation of workers, execution harnesses, actor adapters, governed tools, capabilities, and Akka implementation.

A capability is the product-level backend ability or grouping. A capability owns one or more governed-tools: semantic executable operations or queries with actors, AuthContext, schemas, side effects, idempotency, policy/approval, audit/work trace, and implementation mapping. Agent workstream actions, structured surface/browser actions, confirmed human chat tool plans, Akka components, HTTP/gRPC/MCP endpoints, workflow steps, timer actions, consumers, and agent-tools are implementation or exposure choices for those governed-tools.

## Non-negotiable foundation

Capability-first design does **not** weaken the mandatory secure SaaS foundation from `./ai-first-saas-application-architecture.md`, `./core-ai-first-saas-foundation.md`, `./core-saas-identity-tenancy-admin.md`, and `./core-saas-owner-tenant-billing.md`.

Every protected capability must mechanically enforce:

- authenticated account and selected `AuthContext`;
- active tenant/customer membership and status;
- role, permission, scope, or named capability authorization;
- tenant/customer isolation on all reads and writes;
- backend authorization independent of frontend navigation or prompt instructions;
- audit/work-trace records for denials, data access, approvals, side effects, policy decisions, and consequential AI/governed-tool activity;
- security tests for cross-tenant access, disabled users, denied roles/scopes, audit creation, and frontend secret boundaries where applicable.

Prompt text, skill content, agent-tool descriptions, UI copy, route names, and hidden form fields are never authorization controls. Generated AI-first SaaS foundations must use governed runtime agent artifacts (`AgentDefinition`, `PromptDocument`/`PromptVersion`, `SkillDocument`/`SkillVersion`, `AgentSkillManifest`, `ToolPermissionBoundary`) as behavior configuration only; authority still comes from AuthContext, permissions/capabilities, approval policy, and backend checks. Prompt assembly and skill loading must emit `PromptAssemblyTrace`, `SkillLoadTrace`, and `AgentWorkTrace`.

## Definition: backend capability and governed-tool

A backend capability is a named product ability or grouping with explicit semantics. It may contain one governed-tool for a simple operation, or multiple governed-tools for related reads, commands, proposals, approvals, scheduled checks, agent work, or internal work.

A capability definition should include:

| Field | Required meaning |
|---|---|
| Capability id/name | Stable product ability or grouping name, expressed in product language. |
| Purpose | Business outcome and why the operation exists. |
| Actors/callers | Human roles, agents, workflows, services, timers, consumers, or support roles allowed to request it. |
| Auth context | Required account, tenant/customer, membership, role/scope, and selected context. |
| Governed-tools | One or more executable operations/queries within the capability, each with stable governed-tool id, class, authority, schemas, side effects, idempotency, audit, and tests. |
| Inputs | Typed command/query schemas for each governed-tool, validation, idempotency key, correlation id, and safe defaults. |
| Outputs | Typed response schemas for each governed-tool, redaction rules, user/agent-safe fields, errors, and denial shape. |
| Data access | Records/views/components read by each governed-tool, tenant/customer filters, evidence boundaries, and PII/secret handling. |
| Side effects | State changes, external calls, topic publications, timers, emails, notifications, or workflow starts caused by each governed-tool. |
| Idempotency | Duplicate command behavior, retry safety, dedupe keys, and no-op semantics. |
| Policy/approval | Autonomy level, approval gates, exception/escalation rules, risk/confidence thresholds, and human authority. |
| Audit/trace | Audit event type, work-trace fields, policy citations, tool/data references, and retention/redaction expectations. |
| Exposure channels | Selected workstream action, browser-tool, confirmed human chat tool-plan adapter, agent-tool, API, workflow-tool, MCP-tool, timer-tool, consumer-tool, internal-tool, view/query, or explicit non-exposure for each governed-tool. |
| Tests | Success, validation, forbidden, tenant-isolation, idempotency, audit, approval, and exposure-channel tests. |

## Capability, governed-tool, and exposure terms

Use this hierarchy consistently:

```text
capability = product ability or grouping
→ governed-tool = semantic executable operation/query inside the capability
→ exposure channel = browser-tool, human_chat_tool_plan, agent-tool, workflow-tool, timer-tool, consumer-tool, MCP-tool, internal-tool, API, view/query, or component method
→ Akka substrate = entity, view, workflow, Agent, AutonomousAgent, consumer, timer, endpoint, or service code
```

A capability can contain one governed-tool when the operation is simple, or multiple governed-tools when a product ability includes related reads, commands, proposals, approvals, trace searches, scheduled checks, or internal worker operations. Keep the capability as the product grouping and make each governed-tool precise enough to implement, authorize, audit, and test independently.

Use qualified exposure terms in architecture guidance:

- **browser-tool:** governed-tool exposed to humans through structured surface actions and browser APIs.
- **human_chat_tool_plan:** governed-tool exposed to a human-backed actor through a selected workstream agent's natural-language plan/confirmation adapter. The model may interpret and propose, but execution waits for explicit confirmation and deterministic backend authorization.
- **agent-tool:** governed-tool exposed to request-based or internal agents through Akka `@FunctionTool`, component-tool exposure, MCP, or an equivalent model-facing facade.
- **internal-tool:** governed-tool used by workflows, timers, consumers, internal services, or internal worker agents without direct browser exposure.
- **workflow-tool**, **timer-tool**, **consumer-tool**, and **MCP-tool:** qualified forms when the exposure boundary matters.

Do not introduce a separate top-level governed-tool inventory when an app-description capability file and surface/action maps already own the contract. Instead, list governed-tools inside capability definitions and link each surface/action edge to the relevant governed-tool id.

## Capability is not agent-tool

An agent-tool is one possible exposure channel for a capability's governed-tool. It is not the root abstraction.

Official Akka agent tooling supports local `@FunctionTool` methods, external agent-tool classes, Akka component-tool exposure, and remote MCP-tools. See `akka-context/sdk/agents/extending.html.md`, `../akka-agent-tools/SKILL.md`, and `../akka-agent-component-tools/SKILL.md`.

Capability-first interpretation of those tools:

- `@FunctionTool` exposes a governed-tool to an agent for model-selected invocation.
- Local/external `@FunctionTool` classes may act as non-component governed-tool facades backed by `ComponentClient`; use them when one model-facing agent-tool should compose multiple component calls, hide component layout, enforce policy/scope/redaction, or return a computed agent-safe DTO.
- `.tools(ComponentClass.class)` exposes selected component command/query handlers as agent-tools; it does not make the component itself the product boundary.
- MCP-tools expose governed-tools across an explicit remote boundary and must preserve service ACLs, allowed-tool filtering, tenant scope, and audit.
- Agent-tool descriptions and loaded skill text should communicate impact and required inputs to the model, but governed-tool enforcement must happen in backend code.
- Not every governed-tool should be exposed as an agent-tool. Many governed-tools remain browser-only, workflow-only, timer-only, consumer-only, service-only, or internal-only.

Default stance: expose read-only evidence governed-tools to agents more readily than side-effecting governed-tools. Consequential side effects should default to recommendation, proposal, or approval-request governed-tools unless an accepted policy grants bounded autonomous authority.

## Capability exposure channels

Select governed-tool exposure after capability semantics are clear. Use `structured surface` for workstream renderable artifacts; use `exposure channel` for HTTP/gRPC/MCP-tool/workflow-tool/timer-tool/consumer-tool/view/internal-tool paths.

| Channel | Use when | Capability rules |
|---|---|---|
| Browser UI action | Humans directly initiate or supervise work, including prompt/action/deep-link shell requests such as `show_surface` and `open_workstream`. | Backend still enforces auth; UI shows allowed actions from `/api/me`/capabilities but never decides authorization alone. Shell request resolution must scope targets, audit origin metadata, and return safe denial/system-message surfaces for unauthorized workstreams or surfaces. |
| Human chat tool plan | A signed-in human asks the selected workstream agent to perform consequential work in natural language. | The selected workstream agent may propose a detailed tool plan, but execution requires explicit confirmation bound to that plan. Each governed-tool invocation is separately authorized, idempotent, traced with `source=human_chat_tool_plan` and `confirmedBy`, and returns result or partial-failure surfaces. The AI model is not the security boundary. |
| HTTP/gRPC API | External clients, browser APIs, or services need a stable contract. | Validate tokens/context, scope every command/query, return safe denial/errors, audit protected access. |
| Agent-tool | A bounded agent may choose to read evidence, draft recommendations, or request/perform allowed work. | Agent-tool receives or resolves AuthContext, enforces permission/scope, limits side effects, records governed-tool/data/work traces. |
| MCP-tool/resource/prompt | Capabilities are shared with remote AI clients or other services. | Expose only selected MCP-tools/resources/prompts, use ACL/JWT/service identity, filter allowed MCP-tools, audit remote access. |
| Workflow step | Work is long-running, retryable, approval-gated, compensating, or multi-component. | Persist progress, approval state, retries, denials, and trace links. |
| View/query | Capability provides curated evidence, lists, dashboards, attention summaries, My Account aggregate panels, or search. | Return scoped and redacted read models, not raw state dumps by default. |
| Timer action | Deadlines, reminders, expiry, periodic checks, or scheduled governance work. | Store authority basis and audit scheduled actions; ensure idempotent retry behavior. |
| Consumer | Capability reacts to events/topics/service streams. | Preserve provenance/correlation, enforce allowed side effects, handle duplicate/retry semantics. |
| Internal component method | Operation is not directly exposed outside the backend. | Still validate invariants; apply auth at the caller boundary and audit where consequential. |

A capability may have multiple exposure channels, but the same authority, validation, idempotency, audit, and approval semantics must hold across all of them. Multi-step human chat plans execute as a sequence of independently authorized governed-tool invocations; failure of one step must report partial results safely and must not rely on the model to repair consistency without backend transaction, retry, or compensation rules.

## Akka realization rules

Choose Akka components from the capability shape, not from CRUD intuition.

| Capability shape | Likely Akka substrate |
|---|---|
| Audit-grade decisions, policies, approvals, goals, traces, or records where event history matters | Event Sourced Entity |
| Current-state profile, preference, configuration, or cache-like state without audit-grade event history | Key Value Entity |
| Deterministic multi-step execution, approval waits, retries, compensation, or process orchestration | Workflow |
| Durable model-driven internal/background task with task lifecycle, dependencies, snapshots, notifications, delegation, handoff, teams, moderation, or independent cancellation/failure | AutonomousAgent |
| Curated read/evidence/search/reporting capability | View |
| Bounded request/response classification, planning, summarization, recommendation, evaluation, explanation, or user-facing workstream turn | request-based Agent |
| Event reaction, trace enrichment, publication, integration, or downstream side effect | Consumer |
| Deadlines, reminders, expiry, periodic digest/replay/recheck | Timed Action / Timer |
| Browser or service request boundary | HTTP or gRPC endpoint |
| LLM-facing remote tool/resource/prompt boundary | MCP endpoint |
| Full-stack supervision, decision, governance, audit, and outcome surfaces | React/Vite/TypeScript web UI hosted behind Akka endpoints |

## Design sequence for agents

For broad product input or implementation planning:

1. Preserve the mandatory secure SaaS foundation.
2. Apply the requirements-to-workstream process: identify workstreams, workforce rosters, per-worker responsibility/authority/handoff maps, per-workstream attention categories ("what needs my attention?"), dashboard scope, `WorkstreamAttentionSummary` needs, structured surfaces/actions, My Account aggregate behavior, left rail projections, AutonomousAgent task progress/result surfaces, events/notifications, traces, and tests.
3. Interpret the agent workstream application model: functional/context-area agents (shortened to functional agents), human workers, internal/autonomous/evaluator agent workers, system workers, workstreams, structured surfaces, and retained human authority.
4. Interpret AI-first operating-model needs: delegated work, durable goals/plans, policies, decisions, traces, supervision, and outcomes.
5. Build a capability inventory from worker needs and surface/action semantics before selecting Akka components.
6. For each capability, define schemas, auth/scope, side effects, idempotency, policy/approval, audit/trace, and tests.
7. Decide which exposure channels expose the capability, if any.
8. Select Akka components that realize the capability semantics. Use `./agent-component-selection-guide.md` when a capability could be a request-based Agent, AutonomousAgent, Workflow, Workflow + Agent, or Workflow + AutonomousAgent.
9. Generate code/tests component by component while preserving the capability contract.

Do not jump from a product request directly to an entity, endpoint, or agent-tool unless the capability contract is already clear enough.

## Capability classes

Use these classes to decompose a product safely:

- **Read/evidence capability:** scoped query, explanation context, decision evidence, dashboard/search/list data, shell request target resolution, attention summaries, My Account aggregate panels, and task progress/result evidence.
- **Command capability:** state-changing action with validation, auth, idempotency, audit, and denial semantics.
- **Proposal capability:** agent or human drafts a change or recommendation without committing the side effect.
- **Approval capability:** human or policy-governed decision commits, rejects, delegates, or asks for more evidence.
- **Workflow capability:** starts or advances deterministic long-running, retryable, approval-gated, or compensating work.
- **Autonomous task capability:** starts, assigns, reads, completes/fails, suspends/resumes, or observes a durable model-driven task owned by an Akka `AutonomousAgent`; includes task progress snapshots/results/notifications when exposed to dashboards, attention items, or system-message surfaces.
- **Policy/governance capability:** creates, reviews, simulates, activates, deprecates, or rolls back behavior-changing rules/prompts/skills/thresholds.
- **Trace/audit capability:** records, searches, explains, redacts, or exports what happened and why.
- **Scheduled capability:** timer-backed expiry, reminder, digest, replay, recheck, or retention work.
- **Reactive capability:** consumer-backed event reaction, integration, enrichment, or publication.

## Authority defaults

Use conservative defaults unless accepted product specs say otherwise:

- Read-only scoped evidence may be agent-accessible when it is redacted and audited appropriately.
- Side-effecting human chat tool plans require explicit confirmation bound to the proposed plan, plus any separate approval gates required by policy.
- Side-effecting agent-tools require explicit permission and should prefer proposal/approval flows.
- `readSkill(skillId)` is a governed guidance-loading capability, not an authorization grant; it must check tenant, active governed managed-agent `AgentDefinition`, AgentSkillManifest assignment, skill version/status, mode, AuthContext, and trace allowed or denied loads. Do not confuse that managed-agent domain record with Akka autonomous `AgentDefinition`, the SDK definition returned by `AutonomousAgent.definition()` or dynamic `AgentSetup`.
- High-impact, irreversible, cross-tenant, billing, security, policy, governance, data-export, email-send, or external-side-effect capabilities require human approval or a documented autonomous policy boundary. Generated app email capabilities use Resend through the shared email service; agent access must be a governed `@FunctionTool` or equivalent capability exposure channel with tool-boundary enforcement and traces.
- Agents may recommend governance changes; humans approve activation unless a narrow safe boundary is explicitly defined.
- Support access and SaaS owner operations require separate authority, audit, and tenant/customer context rules.

## Testing expectations

Capability tests should verify behavior, not just component mechanics:

- success path with authorized AuthContext;
- validation failures and safe error shape;
- forbidden access for wrong tenant/customer, missing membership, disabled account, or missing role/scope;
- idempotent duplicate command/retry behavior;
- no-op behavior where relevant;
- audit/work-trace creation for denials, data access, approvals, and side effects;
- approval/escalation behavior for consequential actions;
- exposure-specific behavior for UI/API/tool/MCP/workflow/timer/consumer paths;
- confirmed human chat tool-plan tests cover proposed-plan detail, explicit confirmation binding, denial before confirmation, per-tool idempotency/transaction behavior, partial-failure reporting, and trace source;
- agent tool tests use deterministic model/tool invocation and do not rely on prompt-only security.

## Routing implications

Future skills and planning artifacts should use this doctrine as the backend substrate after secure AI-first SaaS and agent workstream interpretation:

- Description-first paths should maintain capability inventories alongside functional agents, internal agents, workstreams, surfaces, behavior, auth/security, UI, observability, readiness, and tests in the current-intent graph. Prefer `app-description/domains/<domain>/workstreams/<workstream>/**` for workstream-owned agents, surfaces, tool bindings, trace semantics, tests, and `realization/**` maps; keep legacy numbered folders such as `12-workstreams/` or `55-ui/` only as compatibility paths when they are clearly mapped back to the current app/global/domain/workstream nodes.
- Direct Akka decomposition should derive capabilities before component selection.
- PRD/spec/backlog planning should preserve the requirements-to-workstream chain: workstream id, attention category, dashboard/surface/action, capability ids, auth/scope, side effects, approval, audit, exposure channels, AutonomousAgent task semantics where applicable, notifications/projections, and tests in generated tasks.
- Component skills should frame entities, workflows, views, endpoints, agents, MCP, consumers, and timers as capability carriers or capability exposure channels.

The top-level routing skill for this doctrine is `../capability-first-backend/SKILL.md`. Use it with `../README.md` and this document when modeling capability-first backend behavior before selecting Akka components or exposure channels.
