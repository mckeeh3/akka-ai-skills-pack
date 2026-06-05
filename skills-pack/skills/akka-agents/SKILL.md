---
name: akka-agents
description: Orchestrate Akka Java SDK Agent work across durable behavior profiles, governed behavior documents, prompt design, structured responses, tools, memory, streaming, workflow orchestration, guardrails, evaluation, and testing. Use when the task spans more than one agent concern.
---

# Akka Agents

Use this as the top-level skill for Akka Java SDK agent work when the agent responsibility, authority boundary, functional/internal placement, and selected capability surfaces are already clear enough to implement or review.

For broad product, PRD, feature, or automation requests in generated full-stack SaaS apps, route through `ai-first-saas`, `agent-workstream-apps`, `capability-first-backend`, and `akka-solution-decomposition` before implementing agents. Do not reduce delegated operational work to a standalone chatbot or tool list.

## Goal

Generate or review agent code that is:
- correct for Akka SDK 3.6.x
- explicit about the agent's single responsibility
- safe about session ids, memory, and failure handling
- easy for AI coding agents to extend with focused companion skills
- backed by tests or workflow-driven examples when reliability matters

## AI-first workstream substrate role

In generated AI-first SaaS implementations, distinguish two agent placements before writing Java agent code:

- **Functional/context-area agents** are user-facing, role-authorized workstream verticals such as User Admin, Agent Admin, Governance, Audit/Trace, Procurement, Finance, Support, or Sales Pipeline. They own a visible workstream context, default surfaces, callable capabilities, prompt/skill intent, tool boundaries, authority indicators, escalation behavior, traces, and UI tests.
- **Internal agents** are bounded backend workers invoked by workflows, functional agents, tools, consumers, timers, or services for classification, summarization, routing, proposal drafting, extraction, evaluation, replay, or governance review. They are not left-rail application navigation units, but still require governed `AgentDefinition`, authority, tool boundaries, traces, and tests.

Use request-based agents as bounded operational workers for planning, classification, recommendation, summarization, evaluation, explanation, or governed-tool use when one prompt turn or stream is the right unit. Use `akka-autonomous-agents` instead for durable task-oriented internal/background work with task ids, lifecycle, dependencies, failure/cancellation, snapshots, notifications, or model-driven coordination. Before coding, make responsibility, non-responsibility, functional/internal placement, internal workstream agent graph role when applicable, allowed governed-tools/data, tenant/customer scope, required permissions/capabilities, autonomous authority, policy gates, approval thresholds, escalation thresholds, session/memory behavior, and audit/work-trace obligations explicit. Use `akka-agent-behavior-profiles` first when agents are managed runtime actors with durable definitions, lifecycle, owner/steward, authority level, model references, tool permission boundaries, or admin UI. Use workflows for deterministic durable orchestration, approvals, retries, timeouts, and progress tracking instead of chaining request-based agents informally.

## Required reading before coding

Read these first if present:
- `../docs/agent-workstream-application-architecture.md`
- `../docs/capability-first-backend-architecture.md`
- `akka-context/sdk/agents.html.md`
- `akka-context/sdk/agents/prompt.html.md`
- `akka-context/sdk/agents/calling.html.md`
- `akka-context/sdk/agents/memory.html.md`
- `akka-context/sdk/agents/structured.html.md`
- `akka-context/sdk/agents/failures.html.md`
- `akka-context/sdk/agents/extending.html.md`
- `akka-context/sdk/agents/streaming.html.md`
- `akka-context/sdk/agents/orchestrating.html.md`
- `akka-context/sdk/agents/guardrails.html.md`
- `akka-context/sdk/agents/llm_eval.html.md`
- `akka-context/sdk/agents/testing.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`
- `../docs/agent-coverage-matrix.md`
- `../docs/agent-runtime-state-reference.md`
- `../docs/agent-runtime-invocation-pattern.md`

In this repository, prefer these examples:

## Agent governance routing matrix

Use this matrix before reading every governance companion. Load the first matching row, then add only the named companions required by the task.

| If the task is mainly about... | Load first | Add when needed |
|---|---|---|
| Agent identity, lifecycle, owner/steward, functional/internal placement, authority level, active references, or admin catalog | `akka-agent-behavior-profiles` | `akka-agent-model-governance`, `akka-agent-tool-boundaries`, `akka-agent-work-trace` |
| Tenant-scoped versioned artifacts such as prompts, skills, rubrics, policies, examples, snapshots, review, activation, diff/history, or rollback | `akka-agent-governed-documents` | focused prompt/skill/policy/rubric skills |
| First install, tenant bootstrap, or upgrade setup of implementation-developed default AgentDefinitions, prompts, skills, manifests, or tool boundaries | `akka-agent-governed-documents` | prompt/skill/tool-boundary skills |
| Prompt versions, prompt review/activation, effective prompt assembly, prompt test console, or `PromptAssemblyTrace` | `akka-agent-prompt-governance` | `akka-agent-governed-documents`, `akka-agent-behavior-editing` |
| Runtime skills, compact `AgentSkillManifest`, `readSkill(skillId)`, skill-load denials, or `SkillLoadTrace` | `akka-agent-skill-governance` | `akka-agent-tool-boundaries`, `akka-agent-governed-documents` |
| Governed-tool registry/catalog, agent-tool exposure, local/component/MCP/readSkill grants, data scope, side effects, approval-required tool expansion, or tool invocation denials | `akka-agent-tool-boundaries` | `akka-agent-tools`, `akka-agent-component-tools`, `akka-agent-mcp-tools`, `akka-agent-work-trace` |
| Model aliases, `ModelConfigRef`, fallback policy, provider secret boundaries, tenant/agent/task model selection, or model-use traces | `akka-agent-model-governance` | `akka-agent-behavior-profiles`, `akka-agent-work-trace` |
| Prompt/skill/manifest/tool-boundary/policy/rubric changes drafted by an editing agent with proposed diffs and review routing | `akka-agent-behavior-editing` | affected governance skill, `ai-first-saas-decision-cards` |
| Agent execution explainability, prompt/skill/model/tool/data references, correlation, redaction, investigation UI, or trace search | `akka-agent-work-trace` | `ai-first-saas-audit-trace`, capability/component skills producing traces |
| Evaluator findings becoming behavior-change proposals, replay/simulation, approval, activation, monitoring, or rollback | `akka-agent-closed-loop-improvement` | `akka-agent-evaluation`, affected governance skill |

Boundary rules:
- `AgentDefinition` decides whether an agent may run and which governed prompt, skill manifest, model ref, and tool boundary are active; it does not contain prompt/skill text or provider secrets.
- `PromptDocument` and `SkillDocument` are behavior guidance; they never grant role, data, tenant/customer, tool, model, approval, or autonomous authority.
- `AgentSkillManifest` advertises compact skill ids and when-to-use hints; full skill text is returned only through authorized `readSkill(skillId)`.
- `ToolPermissionBoundary`, AuthContext, permissions/capabilities, approval policy, and backend checks are the authority source for tool/data/side-effect access.
- Runtime agents resolve governed records after bootstrap; they must not depend on filesystem defaults for authority or content.
- Editing agents and evaluator agents may draft proposals and evidence, not directly activate consequential changes unless a narrow backend-enforced auto-approval policy is explicitly modeled and tested.

## Companion skills

Load the companion skill that matches the current task:

- `../agent-workstream-apps/SKILL.md`
  - functional/context-area agent versus internal-agent placement, workstream shell context, structured surfaces, and capability-first routing
- `akka-agent-behavior-profiles`
  - durable tenant-scoped AgentDefinition, lifecycle, owner/steward, functional/internal placement metadata, authority level, model references, tool permission boundaries, admin views, and runtime profile lookup
- `../docs/agent-runtime-invocation-pattern.md`
  - concrete runtime invocation sequence from AuthContext through active AgentDefinition, prompt assembly, compact AgentSkillManifest, ToolPermissionBoundary, Java Agent invocation, readSkill authorization, and PromptAssemblyTrace/SkillLoadTrace/AgentWorkTrace emission
- `akka-agent-governed-documents`
  - tenant-scoped governed prompts, skills, rubrics, policies, and examples with version history, immutable snapshots, review, activation, diff UI, and audit
- `akka-agent-governed-documents`
  - first-install/tenant-bootstrap governed setup of implementation-developed default AgentDefinition, prompt, skill, manifest, and tool-boundary records with provenance, upgrade behavior, and audit
- `akka-agent-prompt-governance`
  - runtime-managed agent system prompts with PromptDocument/PromptVersion, review, activation, diff/history UI, effective prompt assembly, PromptAssemblyTrace, and safe test consoles
- `akka-agent-skill-governance`
  - governed runtime skills with SkillDocument/SkillVersion, per-agent AgentSkillManifest, compact skill manifests, readSkill(skillId), SkillLoadTrace, and skill editor/test UI
- `akka-agent-behavior-editing`
  - AgentBehaviorEditorAgent flows for structured proposed diffs, draft versions, risk classification, review/approval routing, decision cards, and denial of unauthorized authority expansion
- `akka-agent-work-trace`
  - agent-specific audit/work traces for AgentDefinition, prompt/skill/model/tool/data/policy usage, authorization decisions, redaction, correlation, and trace timelines
- `akka-agent-closed-loop-improvement`
  - governed evaluation and self-improvement loops with EvaluationRubric, EvaluationRun, EvaluationFinding, ImprovementProposal, replay/simulation, approval, activation, monitoring, and rollback
- `akka-agent-component`
  - core agent class, single command handler, prompt shape, session strategy, and failure fallback
- `akka-agent-structured-responses`
  - `responseConformsTo(...)`, `responseAs(...)`, field descriptions, and fallback mapping
- `akka-agent-tools`
  - local `@FunctionTool` methods and external tool classes registered with `.tools(...)`
- `akka-agent-tool-boundaries`
  - backend-enforced `ToolPermissionBoundary` grants, tool registry/catalog, read-only vs side-effecting authority, component/MCP/readSkill tool permission, approval-required expansion, runtime denied-tool semantics, and tool invocation traces
- `akka-agent-model-governance`
  - governed `ModelConfigRef`, model policy, tenant/agent/task model selection, fallback model policy, provider secret boundaries, model config audit/work traces, and forbidden provider/secret-exposure tests
- `akka-agent-component-tools`
  - Views, entities, and workflows used as tools through `.tools(ComponentClass.class)`
- `akka-agent-mcp-tools`
  - remote MCP server tools added with `.mcpTools(...)`
- `akka-agent-harness-skills`
  - deploy-time packaged model-loadable internal guidance exposed through whitelisted `@FunctionTool` methods or MCP resources; use `akka-agent-skill-governance` for tenant-managed runtime skills
- `akka-agent-multimodal`
  - `UserMessage.from(...)`, image/PDF content, and `contentLoader(...)`
- `akka-agent-memory`
  - session ids, `MemoryProvider`, limited windows, `readOnly()`, and filtered memory reads
- `akka-agent-streaming`
  - `StreamEffect`, `tokenStream(...)`, streaming endpoints, and grouped token delivery
- `ai-first-saas-agent-team-design`
  - one governed skilled agent vs specialized agents vs workflow-supervised team vs evaluator decisions based on authority, tool boundary, model config, lifecycle, steward, memory, risk, audit, and approval needs
- `akka-agent-orchestration`
  - calling request-based agents from workflows, shared session ids, and multi-agent supervisor patterns
- `akka-autonomous-agents`
  - durable task-oriented internal/background model-driven work with typed tasks, task lifecycle, dependencies, notifications, and optional model-driven coordination
- `akka-autonomous-agent-tasks`
  - `Task`, `TaskTemplate`, `TaskAcceptance`, `TaskRule`, dependencies, attachments, and task client calls
- `akka-autonomous-agent-coordination`
  - delegation, handoff, TeamLeadership, Moderation, task dependencies, external input, and notification-aware coordination
- `akka-autonomous-agent-testing`
  - `TestModelProvider.AutonomousAgentTools`, Awaitility, typed task snapshots/results, notifications, and coordination scripts
- `akka-agent-guardrails`
  - runtime-enforced input/output validation and configuration-driven guardrail selection
- `akka-agent-evaluation`
  - evaluator agents and `EvaluationResult` patterns for LLM-as-judge flows; use `akka-agent-closed-loop-improvement` when evaluator results become governed proposals or activations
- `akka-agent-runtime-state`
  - built-in `PromptTemplate` and `SessionMemoryEntity` patterns, views, endpoints, and compaction flows
- `akka-agent-testing`
  - `TestModelProvider`, deterministic tests, and workflow or endpoint integration tests

## Default package layout

Use the fixed Java base package `ai.first` for this core-app-first repository and downstream generated code. Keep package declarations, imports, tests, and source paths under `ai.first`; do not infer package names from examples.

Typical layer paths are:
- `<base>.domain`
- `<base>.application`
- `<base>.api`

Rules:
- agent classes belong in `application`
- helper tool classes usually belong in `application`
- evaluator and guardrail classes may also live in `application`
- endpoints that call agents belong in `api`
- workflows orchestrating agents belong in `application`

## Core rules

1. An agent extends `Agent` and has `@Component(id = "...")`.
2. An agent has exactly one public command handler.
3. Command handlers accept 0 or 1 parameter and return `Effect<T>` or `StreamEffect`.
4. Prefer a stable system message constant or a small builder method.
5. Use explicit session ids with `componentClient.forAgent().inSession(...)`.
6. Prefer `responseConformsTo(...)` for structured replies.
7. Use `.onFailure(...)` for fallback handling instead of assuming model output is always valid.
8. For deploy-time harness-like skills, expose only whitelisted packaged resources through focused `@FunctionTool` methods or MCP; do not read `.agents/skills` from the Akka runtime.
9. For tenant-managed runtime skills, use governed SkillDocument/SkillVersion and AgentSkillManifest checks before `readSkill(skillId)` returns content.
10. Keep agents stateless; use memory or Akka components for context instead of mutable fields.
11. For managed runtime agents, create implementation-developed default prompts, skills, manifests, tool boundaries, and AgentDefinitions as governed records during install or tenant bootstrap; runtime agents must read governed records, not filesystem defaults.
12. Use workflows to orchestrate multiple agents or to add retries, timeouts, and durable progress.
13. Use `TestModelProvider` for deterministic tests.

## Decision guide

### 0. Functional/context-area agent or internal agent
Use `agent-workstream-apps` before implementation when the agent belongs to a generated full-stack SaaS app.

Choose a **functional/context-area agent** when the agent is a user-facing work area in the left rail or equivalent shell, has a durable workstream, renders structured surfaces, accepts composer input, and exposes business capabilities to authorized users.

Choose an **internal agent** when the agent performs bounded backend work behind a workflow, functional agent, timer, consumer, tool, or service call and should not be a primary navigation/workstream context.

For either placement, tools are selected exposure surfaces for named capabilities. Do not start from a tool list; start from capability contracts and then decide whether a tool exposure is appropriate.

### 1. AI-first operational worker
Use when the model performs a bounded responsibility within a durable goal, plan, approval, exception, policy, audit, or outcome loop.

Before implementation, identify:
- functional/context-area or internal-agent placement
- delegated work and retained human authority
- caller AuthContext, tenant/customer scope, and active membership requirements
- policies, permissions/capabilities, evidence, and risk thresholds that bound the agent
- tool/data access allowed per scope and forbidden-access behavior
- approval gates for consequential actions or high-risk recommendations
- trace records needed for prompts, tools, data access, recommendations, evaluations, denials, approvals, and outcomes
- whether a workflow must supervise retries, approvals, or multi-step execution

### 2. Durable behavior profile / managed runtime agent
Use when the app manages agents as tenant-scoped runtime actors with lifecycle, owner/steward, authority, model configuration references, prompt/skill references, tool permission boundaries, or admin UI.

Load `akka-agent-behavior-profiles` before prompt, skill, tool, orchestration, or Java agent implementation details. For managed runtime invocation handoff, read `../docs/agent-runtime-invocation-pattern.md` and use an `AgentRuntimeResolver`-style helper to resolve AuthContext, active AgentDefinition, prompt assembly, compact AgentSkillManifest, ToolPermissionBoundary, readSkill authorization, and PromptAssemblyTrace/SkillLoadTrace/AgentWorkTrace before model invocation.

### 3. Governed behavior documents
Use when prompts, skills, rubrics, policies, or examples need tenant-scoped version history, review, approval, activation, immutable snapshots, diff/history UI, or audit.

Load `akka-agent-governed-documents` before focused prompt governance, skill governance, policy governance, evaluation-rubric, default-document setup, or runtime document lookup implementation.

### 4. Governed runtime prompts
Use when agent system prompts need tenant-scoped review, approval, activation, version history, diff/history UI, effective prompt assembly, prompt assembly trace, or a safe prompt test console.

Load `akka-agent-prompt-governance`. Use `akka-agent-runtime-state` / built-in `PromptTemplate` instead for simple runtime-editable prompt text without governance workflow.

### 5. Governed runtime skills
Use when agents need tenant-scoped shared skills, skill versions, per-agent skill manifests, compact manifest prompt context, `readSkill(skillId)`, SkillLoadTrace, skill editor/review/diff UI, or a skill-loading test console.

Load `akka-agent-skill-governance`. Use `akka-agent-harness-skills` instead only for small deploy-time packaged skill resources.

### 6. Behavior editing agent
Use when admins or stewards ask an `AgentBehaviorEditorAgent` to draft changes to prompts, skills, manifests, tool boundaries, policies, rubrics, or examples.

Load `akka-agent-behavior-editing` with `akka-agent-structured-responses`, the affected governance skill, and `ai-first-saas-decision-cards` when risk, approval, or authority expansion is involved.

### 7. Agent work trace
Use when agent activity needs audit/work trace events, prompt/skill/model/tool/data references, authorization basis, redaction, correlation ids, trace search, or investigation timelines.

Load `akka-agent-work-trace` together with `ai-first-saas-audit-trace`.

### 8. Closed-loop improvement
Use when evaluator output or trace analysis should produce EvaluationRuns, findings, improvement proposals, replay/simulation evidence, human approvals, activation, monitoring, or rollback.

Load `akka-agent-closed-loop-improvement`. Load `akka-agent-evaluation` too when implementing evaluator agents that return `EvaluationResult`.

### 9. Durable autonomous task agent
Use `akka-autonomous-agents` instead of this request-based component path when the work is a durable internal/background task with a typed result, task id, lifecycle, dependencies, failure/cancellation, snapshots, notifications, or model-driven delegation/handoff/team/moderation. Do not use Autonomous Agents as the default for user-facing workstream turns or immediate streamed replies; use `Workflow + AutonomousAgent` when a deterministic business process launches or waits on a durable model-driven investigation.

### 10. Single-purpose request/reply agent
Use when one model interaction produces one reply.

Repository example:
- `WorkstreamRuntimeAgent`

### 11. Tool-using request-based agent
Use when the model must call functions to fetch data, trigger actions, or load approved internal guidance for named capabilities. Agent tools are capability exposure surfaces, not the root design objects.

Repository examples:
- `UserAdminAccessReviewAutonomousAgent`
- `UserAdminEvidenceTools`

For protected or managed-agent tools, load `akka-agent-tool-boundaries` before exposing local, component, MCP, or `readSkill` tools so the implementation resolves `ToolPermissionBoundary`, denies ungranted tools, distinguishes read-only from side-effecting authority, requires approval for expansion, and emits tool invocation traces.

For tenant-, agent-, task-, or mode-specific model selection, load `akka-agent-model-governance` so the implementation resolves `ModelConfigRef`, enforces model policy and fallback model policy, keeps provider secrets out of frontend/model-visible context, and emits model config/use traces before invocation.

For model-loadable guidance that approximates harness skills inside an Akka service, load `akka-agent-harness-skills` in addition to `akka-agent-tools`.

### 12. Streaming request-based agent
Use when tokens should be returned incrementally to an endpoint or notification flow.

Repository examples:
- `StreamingWorkstreamRuntimeAgent`
- `WorkstreamRuntimeAgentEndpoint#stream`

### 13. Agent responsibility shape
Use `ai-first-saas-agent-team-design` before creating multiple agent classes. Prefer a single governed skilled agent when responsibilities share authority, tool boundary, model config, lifecycle, steward, memory, risk, audit, and approval needs and differ only by governed skills in an `AgentSkillManifest`. Prefer specialized agents when those boundaries differ. Add an evaluator agent for independent quality, policy, completeness, or risk judgment. Use a workflow-supervised agent team when durable retries, handoffs, approvals, pauses, or progress visibility are required.

### 14. Workflow-supervised request-based agent team
Use when AI calls need durable retries, shared sessions, or multi-step orchestration.

Repository example:
- `AgentTeamWorkflow`

### 15. Evaluated or governed request-based agent
Use when output quality or runtime safety checks are a first-class concern.

Repository examples:
- `ActivityAnswerEvaluatorAgent`
- `CompetitorMentionGuard`

## Final review checklist

Before finishing, verify:
- the agent has exactly one public command handler
- session id strategy is explicit
- prompt and response type match each other
- memory behavior is intentional
- each agent-tool maps to a named governed-tool/capability contract before registration
- tools have rich `@FunctionTool` descriptions when used, but descriptions are not authorization controls
- harness-like skill tools are whitelisted and backed by packaged resources or MCP, not arbitrary filesystem reads
- structured response records are small and descriptive
- workflow orchestration is used instead of agent-to-agent tool chaining
- managed runtime agents have durable behavior profiles with tenant scope, lifecycle status, owner/steward, authority level, governed `ModelConfigRef`/model policy references, tool permission boundaries, and active prompt/skill references
- governed behavior documents use tenant-scoped version history, immutable snapshots, checksums, approval/activation rules, protected diff/history surfaces, and audit events
- implementation-developed default prompts, skills, manifests, tool boundaries, and AgentDefinitions are created in governed storage on first install or tenant bootstrap with provenance, idempotency, audit, and customization-preserving upgrade behavior
- AI-first agents are explicitly classified as functional/context-area agents or internal agents
- functional agents have workstream, structured surface, capability, authority, trace, and UI-test contracts before implementation
- internal agents are not presented as primary navigation/workstream units unless promoted to a functional agent by product intent
- AI-first agents have explicit authority boundaries, tenant/customer scope, required permissions, policy/approval gates, escalation criteria, and trace obligations
- agent-tools map to named governed-tools/capabilities and enforce backend authorization and audit before consequential data access or side effects
- internal agents participating in an internal workstream agent graph record delegation, result, escalation, and denial traces
- tests replace real models with `TestModelProvider` and cover forbidden/unauthorized governed-tool or action attempts when relevant

## Response style

When answering coding tasks:
- name the agent class and its single responsibility explicitly
- state whether the reply is plain text, structured, or streamed
- mention memory, tools, or workflow orchestration only when actually used
- list the concrete example files used as references
