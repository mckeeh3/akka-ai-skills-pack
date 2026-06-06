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
- `../docs/governed-agent-substrate.md`
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

Use the routing matrix above plus `../README.md` for the full catalog. Load only the smallest matching companion:

- runtime component mechanics: `akka-agent-component`, `akka-agent-tools`, `akka-agent-component-tools`, `akka-agent-mcp-tools`, `akka-agent-memory`, `akka-agent-streaming`, `akka-agent-structured-responses`, `akka-agent-testing`
- governed runtime substrate: `akka-agent-behavior-profiles`, `akka-agent-governed-documents`, `akka-agent-prompt-governance`, `akka-agent-skill-governance`, `akka-agent-reference-governance`, `akka-agent-tool-boundaries`, `akka-agent-model-governance`, `akka-agent-work-trace`
- change/evaluation loops: `akka-agent-behavior-editing`, `akka-agent-evaluation`, `akka-agent-closed-loop-improvement`
- autonomous/background work: `akka-autonomous-agents`, `akka-autonomous-agent-tasks`, `akka-autonomous-agent-coordination`, `akka-autonomous-agent-testing`
- workstream/team placement: `../agent-workstream-apps/SKILL.md`, `ai-first-saas-agent-team-design`, `akka-agent-orchestration`

For the end-to-end governed invocation sequence, prefer `../docs/agent-runtime-invocation-pattern.md` instead of reloading multiple governance skills.

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

Use this routing matrix instead of loading broad historical agent guidance:

| Need | Load next |
|---|---|
| User-facing left-rail/workstream agent with composer input, structured surfaces, capabilities, and UI tests | `agent-workstream-apps`, then focused component/UI/capability skills |
| Bounded backend model call behind workflow, timer, consumer, tool, endpoint, or service | `akka-agent-component`; keep it internal unless product intent promotes it to a functional agent |
| Durable typed background task with task id, lifecycle, dependencies, failure/cancellation, snapshots, notifications, delegation/handoff/team/moderation | `akka-autonomous-agents` |
| Tenant-scoped managed agent lifecycle, owner/steward, authority, model config refs, prompt/skill refs, tool boundaries, or admin UI | `akka-agent-behavior-profiles`, then `../docs/agent-runtime-invocation-pattern.md` |
| Governed prompts, skills, references, rubrics, policies, examples, defaults, or runtime lookup | `akka-agent-governed-documents` plus the focused prompt/skill/reference/model/tool/trace skill |
| Admin/steward asks an agent to draft behavior changes | `akka-agent-behavior-editing`, affected governance skill, `akka-agent-structured-responses`, and decision-card routing when risk/approval is involved |
| Trace, redaction, model/tool/data references, authorization basis, investigation timeline | `akka-agent-work-trace` and `ai-first-saas-audit-trace` |
| Evaluator output should drive findings, proposals, approval, activation, monitoring, or rollback | `akka-agent-closed-loop-improvement` and `akka-agent-evaluation` |
| Tool use for data/action/guidance | `akka-agent-tools`, `akka-agent-component-tools`, `akka-agent-mcp-tools`, `akka-agent-tool-boundaries`, and capability docs |
| Tenant/agent/task/mode-specific model selection | `akka-agent-model-governance` |
| Token streaming | `akka-agent-streaming` |
| Multi-agent responsibility split | `ai-first-saas-agent-team-design`; prefer one governed skilled agent unless authority/tool/model/lifecycle/risk boundaries differ |
| Durable retries, handoffs, approvals, pauses, or progress visibility around model calls | `akka-agent-orchestration` and workflow skills |

Repository/pattern references: `WorkstreamRuntimeAgent`, `StreamingWorkstreamRuntimeAgent`, `WorkstreamRuntimeAgentEndpoint#stream`, `UserAdminAccessReviewAutonomousAgent`, `UserAdminEvidenceTools`, domain-specific evaluator/guardrail/workflow examples.

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
