# Capability-First Backend Architecture

## Status and scope

This is the canonical capability-first backend doctrine for this skills pack. It extends the secure AI-first SaaS doctrine in `ai-first-saas-application-architecture.md` without replacing it.

Default generated-application interpretation:

```text
product intent
→ mandatory secure SaaS foundation
→ agent workstream application model: role-authorized functional/context-area agents, workstreams, and structured surfaces
→ capability inventory
→ authority, scope, schemas, side effects, audit, approval, and supervision rules
→ selected capability exposure channels
→ Akka component realization
```

A capability is the backend design object. Agent workstream actions, Akka components, HTTP/gRPC/MCP endpoints, workflow steps, timer actions, consumers, browser UI actions, and agent tools are implementation or exposure choices for a capability.

## Non-negotiable foundation

Capability-first design does **not** weaken the mandatory secure SaaS foundation from `ai-first-saas-application-architecture.md`, `core-ai-first-saas-foundation.md`, `core-saas-identity-tenancy-admin.md`, and `core-saas-owner-tenant-billing.md`.

Every protected capability must mechanically enforce:

- authenticated account and selected `AuthContext`;
- active tenant/customer membership and status;
- role, permission, scope, or named capability authorization;
- tenant/customer isolation on all reads and writes;
- backend authorization independent of frontend navigation or prompt instructions;
- audit/work-trace records for denials, data access, approvals, side effects, policy decisions, and consequential AI/tool activity;
- security tests for cross-tenant access, disabled users, denied roles/scopes, audit creation, and frontend secret boundaries where applicable.

Prompt text, skill content, tool descriptions, UI copy, route names, and hidden form fields are never authorization controls. Generated AI-first SaaS foundations must use governed runtime agent artifacts (`AgentDefinition`, `PromptDocument`/`PromptVersion`, `SkillDocument`/`SkillVersion`, `AgentSkillManifest`, `ToolPermissionBoundary`) as behavior configuration only; authority still comes from AuthContext, permissions/capabilities, approval policy, and backend checks. Prompt assembly and skill loading must emit `PromptAssemblyTrace`, `SkillLoadTrace`, and `AgentWorkTrace`.

## Definition: backend capability

A backend capability is a named, intentional domain operation or query with explicit semantics. It may be read-only, side-effecting, long-running, scheduled, event-reactive, human-approved, agent-assisted, or internal-only.

A capability definition should include:

| Field | Required meaning |
|---|---|
| Capability id/name | Stable operation or query name, expressed in product language. |
| Purpose | Business outcome and why the operation exists. |
| Actors/callers | Human roles, agents, workflows, services, timers, consumers, or support roles allowed to request it. |
| Auth context | Required account, tenant/customer, membership, role/scope, and selected context. |
| Inputs | Typed command/query schema, validation, idempotency key, correlation id, and safe defaults. |
| Outputs | Typed response schema, redaction rules, user/agent-safe fields, errors, and denial shape. |
| Data access | Records/views/components read, tenant/customer filters, evidence boundaries, and PII/secret handling. |
| Side effects | State changes, external calls, topic publications, timers, emails, notifications, or workflow starts. |
| Idempotency | Duplicate command behavior, retry safety, dedupe keys, and no-op semantics. |
| Policy/approval | Autonomy level, approval gates, exception/escalation rules, risk/confidence thresholds, and human authority. |
| Audit/trace | Audit event type, work-trace fields, policy citations, tool/data references, and retention/redaction expectations. |
| Exposure channels | Selected workstream action, UI/API/tool/workflow/MCP/timer/consumer/internal channels, or explicit non-exposure. |
| Tests | Success, validation, forbidden, tenant-isolation, idempotency, audit, approval, and exposure-channel tests. |

## Capability is not agent tool

An agent tool is one possible exposure channel for a capability. It is not the root abstraction.

Official Akka agent tooling supports local `@FunctionTool` methods, external tool classes, Akka components as function tools, and remote MCP tools. See `../akka-context/sdk/agents/extending.html.md`, `../skills/akka-agent-tools/SKILL.md`, and `../skills/akka-agent-component-tools/SKILL.md`.

Capability-first interpretation of those tools:

- `@FunctionTool` exposes a capability operation to an agent for model-selected invocation.
- Local/external `@FunctionTool` classes may act as non-component capability facades backed by `ComponentClient`; use them when one model-facing tool should compose multiple component calls, hide component layout, enforce policy/scope/redaction, or return a computed agent-safe DTO.
- `.tools(ComponentClass.class)` exposes selected component command/query handlers as tools; it does not make the component itself the product boundary.
- MCP tools expose capabilities across an explicit remote boundary and must preserve service ACLs, allowed-tool filtering, tenant scope, and audit.
- Tool descriptions and loaded skill text should communicate impact and required inputs to the model, but capability enforcement must happen in backend code.
- Not every capability should be exposed as a tool. Many capabilities remain browser-only, workflow-only, timer-only, consumer-only, service-only, or internal-only.

Default stance: expose read-only evidence capabilities to agents more readily than side-effecting capabilities. Consequential side effects should default to recommendation, proposal, or approval-request capabilities unless an accepted policy grants bounded autonomous authority.

## Capability exposure channels

Select capability exposure after capability semantics are clear. Use `structured surface` for workstream renderable artifacts; use `exposure channel` for HTTP/gRPC/MCP/tool/workflow/timer/consumer/view/internal paths.

| Channel | Use when | Capability rules |
|---|---|---|
| Browser UI action | Humans directly initiate or supervise work. | Backend still enforces auth; UI shows allowed actions from `/api/me`/capabilities but never decides authorization alone. |
| HTTP/gRPC API | External clients, browser APIs, or services need a stable contract. | Validate tokens/context, scope every command/query, return safe denial/errors, audit protected access. |
| Agent tool | A bounded agent may choose to read evidence, draft recommendations, or request/perform allowed work. | Tool receives or resolves AuthContext, enforces permission/scope, limits side effects, records tool/data/work traces. |
| MCP tool/resource/prompt | Capabilities are shared with remote AI clients or other services. | Expose only selected tools/resources/prompts, use ACL/JWT/service identity, filter allowed tools, audit remote access. |
| Workflow step | Work is long-running, retryable, approval-gated, compensating, or multi-component. | Persist progress, approval state, retries, denials, and trace links. |
| View/query | Capability provides curated evidence, lists, dashboards, or search. | Return scoped and redacted read models, not raw state dumps by default. |
| Timer action | Deadlines, reminders, expiry, periodic checks, or scheduled governance work. | Store authority basis and audit scheduled actions; ensure idempotent retry behavior. |
| Consumer | Capability reacts to events/topics/service streams. | Preserve provenance/correlation, enforce allowed side effects, handle duplicate/retry semantics. |
| Internal component method | Operation is not directly exposed outside the backend. | Still validate invariants; apply auth at the caller boundary and audit where consequential. |

A capability may have multiple exposure channels, but the same authority, validation, idempotency, audit, and approval semantics must hold across all of them.

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
2. Interpret the agent workstream application model: functional/context-area agents (shortened to functional agents), internal agents, workstreams, structured surfaces, and retained human authority.
3. Interpret AI-first operating-model needs: delegated work, durable goals/plans, policies, decisions, traces, supervision, and outcomes.
4. Build a capability inventory before selecting Akka components.
5. For each capability, define schemas, auth/scope, side effects, idempotency, policy/approval, audit/trace, and tests.
6. Decide which exposure channels expose the capability, if any.
7. Select Akka components that realize the capability semantics. Use `agent-component-selection-guide.md` when a capability could be a request-based Agent, AutonomousAgent, Workflow, Workflow + Agent, or Workflow + AutonomousAgent.
8. Generate code/tests component by component while preserving the capability contract.

Do not jump from a product request directly to an entity, endpoint, or agent tool unless the capability contract is already clear enough.

## Capability classes

Use these classes to decompose a product safely:

- **Read/evidence capability:** scoped query, explanation context, decision evidence, dashboard/search/list data.
- **Command capability:** state-changing action with validation, auth, idempotency, audit, and denial semantics.
- **Proposal capability:** agent or human drafts a change or recommendation without committing the side effect.
- **Approval capability:** human or policy-governed decision commits, rejects, delegates, or asks for more evidence.
- **Workflow capability:** starts or advances deterministic long-running, retryable, approval-gated, or compensating work.
- **Autonomous task capability:** starts, assigns, reads, completes/fails, suspends/resumes, or observes a durable model-driven task owned by an Akka `AutonomousAgent`.
- **Policy/governance capability:** creates, reviews, simulates, activates, deprecates, or rolls back behavior-changing rules/prompts/skills/thresholds.
- **Trace/audit capability:** records, searches, explains, redacts, or exports what happened and why.
- **Scheduled capability:** timer-backed expiry, reminder, digest, replay, recheck, or retention work.
- **Reactive capability:** consumer-backed event reaction, integration, enrichment, or publication.

## Authority defaults

Use conservative defaults unless accepted product specs say otherwise:

- Read-only scoped evidence may be agent-accessible when it is redacted and audited appropriately.
- Side-effecting agent tools require explicit permission and should prefer proposal/approval flows.
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
- agent tool tests use deterministic model/tool invocation and do not rely on prompt-only security.

## Routing implications

Future skills and planning artifacts should use this doctrine as the backend substrate after secure AI-first SaaS and agent workstream interpretation:

- Description-first paths should maintain capability inventories alongside functional agents, internal agents, workstreams, surfaces, behavior, auth/security, UI, observability, readiness, and tests. In app-description trees, `12-workstreams/` owns functional agents, workstreams, surface contracts, action-to-capability mappings, trace semantics, and surface/action tests; `55-ui/` owns browser realization such as shell rendering, routes/deep links, interactions, frontend API contracts, state/realtime, accessibility/responsive behavior, and style guide.
- Direct Akka decomposition should derive capabilities before component selection.
- PRD/spec/backlog planning should preserve capability ids, auth/scope, side effects, approval, audit, exposure channels, and tests in generated tasks.
- Component skills should frame entities, workflows, views, endpoints, agents, MCP, consumers, and timers as capability carriers or capability exposure channels.

The top-level routing skill for this doctrine is `../skills/capability-first-backend/SKILL.md`. Use it with `../skills/README.md` and this document when modeling capability-first backend behavior before selecting Akka components or exposure channels.
