---
name: akka-agents
description: Orchestrate Akka Java SDK Agent work across durable behavior profiles, governed behavior documents, prompt design, structured responses, tools, memory, streaming, workflow orchestration, guardrails, evaluation, and testing. Use when the task spans more than one agent concern.
---

# Akka Agents

Use this as the top-level skill for Akka Java SDK agent work.

## Goal

Generate or review agent code that is:
- correct for Akka SDK 3.4+
- explicit about the agent's single responsibility
- safe about session ids, memory, and failure handling
- easy for AI coding agents to extend with focused companion skills
- backed by tests or workflow-driven examples when reliability matters

## AI-first substrate role

In AI-first SaaS implementations, use agents as bounded operational workers for planning, classification, recommendation, summarization, evaluation, explanation, or tool use. Before coding, make responsibility, non-responsibility, allowed tools/data, tenant/customer scope, required permissions/capabilities, autonomous authority, policy gates, approval thresholds, escalation thresholds, session/memory behavior, and audit/work-trace obligations explicit. Use `akka-agent-behavior-profiles` first when agents are managed runtime actors with durable definitions, lifecycle, owner/steward, authority level, model references, tool permission boundaries, or admin UI. Use workflows for durable multi-agent orchestration, approvals, retries, timeouts, and progress tracking instead of chaining agents informally.

## Required reading before coding

Read these first if present:
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
- `../../../docs/agent-coverage-matrix.md`
- `../../../docs/agent-runtime-state-reference.md`

In this repository, prefer these examples:
- `../../../src/main/java/com/example/application/ActivityAgent.java`
- `../../../src/main/java/com/example/application/ConfiguredModelActivityAgent.java`
- `../../../src/main/java/com/example/application/TemplateBackedActivityAgent.java`
- `../../../src/main/java/com/example/application/WeatherAgent.java`
- `../../../src/main/java/com/example/application/WeatherForecastTools.java`
- `../../../src/main/java/com/example/application/CartInspectorAgent.java`
- `../../../src/main/java/com/example/application/RemoteShoppingCartAgent.java`
- `../../../src/main/java/com/example/application/DocumentAnalysisAgent.java`
- `../../../src/main/java/com/example/application/WorkerMemorySummaryAgent.java`
- `../../../src/main/java/com/example/application/StreamingActivityAgent.java`
- `../../../src/main/java/com/example/application/AgentTeamWorkflow.java`
- `../../../src/main/java/com/example/application/DynamicAgentTeamWorkflow.java`
- `../../../src/main/java/com/example/application/SelectorAgent.java`
- `../../../src/main/java/com/example/application/PlannerAgent.java`
- `../../../src/main/java/com/example/application/SummarizerAgent.java`
- `../../../src/main/java/com/example/application/SessionMemoryAlertsConsumer.java`
- `../../../src/main/java/com/example/application/SessionMemoryByComponentView.java`
- `../../../src/main/java/com/example/application/SessionMemoryAlertView.java`
- `../../../src/main/java/com/example/application/SessionMemoryCompactionAgent.java`
- `../../../src/main/java/com/example/application/SessionMemoryCompactionConsumer.java`
- `../../../src/main/java/com/example/application/SessionMemoryCompactionAuditConsumer.java`
- `../../../src/main/java/com/example/application/PromptTemplateHistoryView.java`
- `../../../src/main/java/com/example/application/ActivityAnswerEvaluatorAgent.java`
- `../../../src/main/java/com/example/application/CompetitorMentionGuard.java`
- `../../../src/main/java/com/example/api/ActivityAgentEndpoint.java`
- `../../../src/main/java/com/example/api/ShoppingCartToolsMcpEndpoint.java`
- `../../../src/main/java/com/example/api/ActivityPromptEndpoint.java`
- `../../../src/main/java/com/example/api/PromptTemplateHistoryEndpoint.java`
- `../../../src/main/java/com/example/api/SessionMemoryViewEndpoint.java`
- `../../../src/main/java/com/example/api/SessionMemoryAlertStreamEndpoint.java`
- `../../../src/main/java/com/example/api/DynamicAgentTeamWorkflowEndpoint.java`
- `../../../src/test/java/com/example/application/ActivityAgentTest.java`
- `../../../src/test/java/com/example/application/ActivityAgentGuardrailIntegrationTest.java`
- `../../../src/test/java/com/example/application/ConfiguredModelActivityAgentTest.java`
- `../../../src/test/java/com/example/application/CartInspectorAgentTest.java`
- `../../../src/test/java/com/example/application/RemoteShoppingCartAgentTest.java`
- `../../../src/test/java/com/example/application/DocumentAnalysisAgentTest.java`
- `../../../src/test/java/com/example/application/WorkerMemorySummaryAgentTest.java`
- `../../../src/test/java/com/example/application/AgentTeamWorkflowIntegrationTest.java`
- `../../../src/test/java/com/example/application/DynamicAgentTeamWorkflowIntegrationTest.java`
- `../../../src/test/java/com/example/application/ActivityAgentEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/ActivityPromptEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/DynamicAgentTeamWorkflowEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/PromptTemplateHistoryViewIntegrationTest.java`
- `../../../src/test/java/com/example/application/PromptTemplateHistoryEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/SessionMemoryViewEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/SessionMemoryAlertStreamEndpointIntegrationTest.java`
- `../../../src/test/java/com/example/application/SessionMemoryAlertsConsumerIntegrationTest.java`
- `../../../src/test/java/com/example/application/SessionMemoryByComponentViewIntegrationTest.java`
- `../../../src/test/java/com/example/application/SessionMemoryCompactionConsumerIntegrationTest.java`
- `../../../src/test/java/com/example/application/SessionMemoryCompactionAuditConsumerIntegrationTest.java`

## Companion skills

Load the companion skill that matches the current task:

- `akka-agent-behavior-profiles`
  - durable tenant-scoped AgentDefinition, lifecycle, owner/steward, authority level, model references, tool permission boundaries, admin views, and runtime profile lookup
- `akka-agent-governed-documents`
  - tenant-scoped governed prompts, skills, rubrics, policies, and examples with version history, immutable snapshots, review, activation, diff UI, and audit
- `akka-agent-prompt-governance`
  - runtime-managed agent system prompts with PromptDocument/PromptVersion, review, activation, diff/history UI, effective prompt assembly, PromptAssemblyTrace, and safe test consoles
- `akka-agent-skill-governance`
  - governed runtime skills with SkillDocument/SkillVersion, per-agent AgentSkillManifest, compact skill manifests, readSkill(skillId), SkillLoadTrace, and skill editor/test UI
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
- `akka-agent-orchestration`
  - calling agents from workflows, shared session ids, and multi-agent supervisor patterns
- `akka-agent-guardrails`
  - runtime-enforced input/output validation and configuration-driven guardrail selection
- `akka-agent-evaluation`
  - evaluator agents and `EvaluationResult` patterns for LLM-as-judge flows; use `akka-agent-closed-loop-improvement` when evaluator results become governed proposals or activations
- `akka-agent-runtime-state`
  - built-in `PromptTemplate` and `SessionMemoryEntity` patterns, views, endpoints, and compaction flows
- `akka-agent-testing`
  - `TestModelProvider`, deterministic tests, and workflow or endpoint integration tests

## Default package layout

Use:
- `com.<org>.<app>.domain`
- `com.<org>.<app>.application`
- `com.<org>.<app>.api`

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
11. Use workflows to orchestrate multiple agents or to add retries, timeouts, and durable progress.
12. Use `TestModelProvider` for deterministic tests.

## Decision guide

### 0. AI-first operational worker
Use when the model performs a bounded responsibility within a durable goal, plan, approval, exception, policy, audit, or outcome loop.

Before implementation, identify:
- delegated work and retained human authority
- caller AuthContext, tenant/customer scope, and active membership requirements
- policies, permissions/capabilities, evidence, and risk thresholds that bound the agent
- tool/data access allowed per scope and forbidden-access behavior
- approval gates for consequential actions or high-risk recommendations
- trace records needed for prompts, tools, data access, recommendations, evaluations, denials, approvals, and outcomes
- whether a workflow must supervise retries, approvals, or multi-step execution

### 1. Durable behavior profile / managed runtime agent
Use when the app manages agents as tenant-scoped runtime actors with lifecycle, owner/steward, authority, model configuration references, prompt/skill references, tool permission boundaries, or admin UI.

Load `akka-agent-behavior-profiles` before prompt, skill, tool, orchestration, or Java agent implementation details.

### 2. Governed behavior documents
Use when prompts, skills, rubrics, policies, or examples need tenant-scoped version history, review, approval, activation, immutable snapshots, diff/history UI, or audit.

Load `akka-agent-governed-documents` before focused prompt governance, skill governance, policy governance, evaluation-rubric, or runtime document lookup implementation.

### 3. Governed runtime prompts
Use when agent system prompts need tenant-scoped review, approval, activation, version history, diff/history UI, effective prompt assembly, prompt assembly trace, or a safe prompt test console.

Load `akka-agent-prompt-governance`. Use `akka-agent-runtime-state` / built-in `PromptTemplate` instead for simple runtime-editable prompt text without governance workflow.

### 4. Governed runtime skills
Use when agents need tenant-scoped shared skills, skill versions, per-agent skill manifests, compact manifest prompt context, `readSkill(skillId)`, SkillLoadTrace, skill editor/review/diff UI, or a skill-loading test console.

Load `akka-agent-skill-governance`. Use `akka-agent-harness-skills` instead only for small deploy-time packaged skill resources.

### 5. Agent work trace
Use when agent activity needs audit/work trace events, prompt/skill/model/tool/data references, authorization basis, redaction, correlation ids, trace search, or investigation timelines.

Load `akka-agent-work-trace` together with `ai-first-saas-audit-trace`.

### 6. Closed-loop improvement
Use when evaluator output or trace analysis should produce EvaluationRuns, findings, improvement proposals, replay/simulation evidence, human approvals, activation, monitoring, or rollback.

Load `akka-agent-closed-loop-improvement`. Load `akka-agent-evaluation` too when implementing evaluator agents that return `EvaluationResult`.

### 7. Single-purpose request/reply agent
Use when one model interaction produces one reply.

Repository example:
- `ActivityAgent`

### 8. Tool-using agent
Use when the model must call functions to fetch data, trigger actions, or load approved internal guidance.

Repository examples:
- `WeatherAgent`
- `WeatherForecastTools`

For model-loadable guidance that approximates harness skills inside an Akka service, load `akka-agent-harness-skills` in addition to `akka-agent-tools`.

### 9. Streaming agent
Use when tokens should be returned incrementally to an endpoint or notification flow.

Repository examples:
- `StreamingActivityAgent`
- `ActivityAgentEndpoint#stream`

### 6. Workflow-supervised agent team
Use when AI calls need durable retries, shared sessions, or multi-step orchestration.

Repository example:
- `AgentTeamWorkflow`

### 7. Evaluated or governed agent
Use when output quality or runtime safety checks are a first-class concern.

Repository examples:
- `ActivityAnswerEvaluatorAgent`
- `CompetitorMentionGuard`
- `../../../src/main/resources/application.conf`

## Final review checklist

Before finishing, verify:
- the agent has exactly one public command handler
- session id strategy is explicit
- prompt and response type match each other
- memory behavior is intentional
- tools have rich `@FunctionTool` descriptions when used
- harness-like skill tools are whitelisted and backed by packaged resources or MCP, not arbitrary filesystem reads
- structured response records are small and descriptive
- workflow orchestration is used instead of agent-to-agent tool chaining
- managed runtime agents have durable behavior profiles with tenant scope, lifecycle status, owner/steward, authority level, model references, tool permission boundaries, and active prompt/skill references
- governed behavior documents use tenant-scoped version history, immutable snapshots, checksums, approval/activation rules, protected diff/history surfaces, and audit events
- AI-first agents have explicit authority boundaries, tenant/customer scope, required permissions, policy/approval gates, escalation criteria, and trace obligations
- agent tools enforce backend authorization and audit before consequential data access or side effects
- tests replace real models with `TestModelProvider` and cover forbidden/unauthorized tool or action attempts when relevant

## Response style

When answering coding tasks:
- name the agent class and its single responsibility explicitly
- state whether the reply is plain text, structured, or streamed
- mention memory, tools, or workflow orchestration only when actually used
- list the concrete example files used as references
