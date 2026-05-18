# Agent coverage matrix

Purpose: map the official Akka agent topics to the local agent-optimized skills, executable examples, and tests in this repository.

Use this file when you need to answer:
- which local skill should I load first?
- what is the canonical example for this agent feature?
- do we already have test-backed coverage for this topic?
- what is still missing or only partially covered?

Legend:
- ✅ covered with local skill + example + test
- ◑ partially covered or missing one important test/reference
- △ notable gap for a future cleanup sprint

| Official topic | Official source | Local skill(s) | Canonical local examples | Test/reference coverage | Status |
|---|---|---|---|---|---|
| Basic agent structure, single command handler, explicit responsibility | `akka-context/sdk/agents.html.md`, `akka-context/sdk/agents/prompt.html.md` | `akka-agents`, `akka-agent-component` | `src/main/java/com/example/application/ActivityAgent.java`, `TemplateBackedActivityAgent.java` | `src/test/java/com/example/application/ActivityAgentTest.java` | ✅ |
| Prompt design and runtime-managed prompts | `akka-context/sdk/agents/prompt.html.md` | `akka-agent-component`, `akka-agent-runtime-state` | `TemplateBackedActivityAgent.java`, `src/main/java/com/example/api/ActivityPromptEndpoint.java`, `PromptTemplateHistoryView.java` | `ActivityPromptEndpointIntegrationTest.java`, `PromptTemplateHistoryViewIntegrationTest.java`, `PromptTemplateHistoryEndpointIntegrationTest.java` | ✅ |
| Governed runtime prompt documents, version snapshots, diff/review/activation, and prompt assembly traces | `akka-context/sdk/agents/prompt.html.md`, core seed Module 4 PRD | `akka-agent-prompt-governance`, `akka-agent-governed-documents`, `akka-agent-behavior-profiles` | `ReferencePromptDocument`, `ReferencePromptVersion`, `ReferenceAgentRuntimeResolver`, `ReferencePromptAssembler` | `ReferenceAgentRuntimeResolverTest` covers active prompt assembly, compact manifest-only prompt context, disabled-agent denial, cross-tenant prompt/manifest denial, and `PromptAssemblyTrace` creation; full lifecycle diff/review/activation remains documented guidance | ◑ |
| Calling agents from workflows or other components | `akka-context/sdk/agents/calling.html.md` | `akka-agent-orchestration`, `akka-agents` | `AgentTeamWorkflow.java`, `DynamicAgentTeamWorkflow.java`, `src/main/java/com/example/api/DynamicAgentTeamWorkflowEndpoint.java` | `AgentTeamWorkflowIntegrationTest.java`, `DynamicAgentTeamWorkflowIntegrationTest.java`, `DynamicAgentTeamWorkflowEndpointIntegrationTest.java` | ✅ |
| Session ids, bounded memory, filtered reads, shared sessions | `akka-context/sdk/agents/memory.html.md` | `akka-agent-memory`, `akka-agent-orchestration` | `WorkerMemorySummaryAgent.java`, `AgentTeamWorkflow.java`, `DynamicAgentTeamWorkflow.java` | `WorkerMemorySummaryAgentTest.java`, workflow integration tests above | ✅ |
| Structured responses with `responseConformsTo(...)` | `akka-context/sdk/agents/structured.html.md` | `akka-agent-structured-responses` | `ActivityAgent.java`, `SelectorAgent.java`, `PlannerAgent.java` | `ActivityAgentTest.java`, `SelectorAgentTest.java`, `PlannerAgentTest.java` | ✅ |
| Failure handling and narrow fallback mapping | `akka-context/sdk/agents/failures.html.md` | `akka-agent-component`, `akka-agent-structured-responses` | `ActivityAgent.java` | `ActivityAgentTest.java` covers malformed JSON fallback and non-parsing failure propagation | ✅ |
| Local `@FunctionTool` methods and external tool classes | `akka-context/sdk/agents/extending.html.md` | `akka-agent-tools` | `WeatherAgent.java`, `WeatherForecastTools.java` | `WeatherAgentTest.java` | ✅ |
| Harness-like skill loading through `@FunctionTool` guidance tools | `akka-context/sdk/agents/extending.html.md`, `akka-context/sdk/agents/prompt.html.md` | `akka-agent-harness-skills`, `akka-agent-tools` | pattern documented in `skills/akka-agent-harness-skills/SKILL.md` | no executable local example yet | ◑ |
| Governed runtime skills with SkillDocument, SkillVersion, AgentSkillManifest, readSkill, and SkillLoadTrace | `akka-context/sdk/agents/extending.html.md`, core seed Module 5 PRD | `akka-agent-skill-governance`, `akka-agent-governed-documents`, `akka-agent-tools`, `akka-agent-tool-boundaries` | `ReferenceSkillDocument`, `ReferenceSkillVersion`, `ReferenceAgentSkillManifest`, `ReferenceSkillReadAuthorizer`, `ReferenceAgentSkillTools` | `ReferenceSkillReadAuthorizerTest` covers assigned active skill success, unassigned skill denial, inactive/cross-tenant skill denial, missing `readSkill` tool grant denial, safe denial strings, and `SkillLoadTrace` creation | ✅ |
| Agent-specific work trace with prompt/skill/model/tool/data/policy references, authorization basis, redaction, and correlation | core seed Module 6 PRD | `akka-agent-work-trace`, `ai-first-saas-audit-trace` | `ReferencePromptAssemblyTrace`, `ReferenceSkillLoadTrace`, `ReferenceAgentWorkTrace`, `ReferenceTraceSink`, `ManagedReferenceActivityAgent` | `ReferenceAgentRuntimeResolverTest`, `ReferenceSkillReadAuthorizerTest`, and `ManagedReferenceActivityAgentTest` cover prompt assembly trace, skill load trace, work trace correlation, and denied-path trace facts; durable trace storage/search UI remains documented guidance | ◑ |
| ToolPermissionBoundary enforcement, tool registry/catalog, denied tool semantics, approval-required expansion, and tool invocation traces | core governed runtime agent foundation sprint | `akka-agent-tool-boundaries`, `akka-agent-tools`, `akka-agent-component-tools`, `akka-agent-mcp-tools`, `akka-agent-work-trace` | `ReferenceToolPermissionBoundary`, `ReferenceAgentRuntimeResolver`, `ReferenceSkillReadAuthorizer`, `ReferenceAgentSkillTools` | `ReferenceAgentRuntimeResolverTest` resolves active tool boundaries before invocation; `ReferenceSkillReadAuthorizerTest` covers missing `readSkill` tool grant denial and ToolPermissionBoundary-backed skill loading; broader side-effecting/component/MCP tool boundary examples remain documented guidance | ◑ |
| Durable AgentDefinition and behavior profiles | core seed Module 3 PRD | `akka-agent-behavior-profiles`, `akka-agent-governed-documents` | `ReferenceAgentDefinition`, `ReferenceResolvedAgentRuntime`, `ReferenceAgentRuntimeResolver`, `ReferenceAgentFoundationSeed` | `ReferenceAgentRuntimeResolverTest` covers active profile resolution, disabled-agent denial before model invocation, cross-tenant denial, and prompt assembly trace recording | ✅ |
| Governed behavior document/version pattern for prompts, skills, rubrics, policies, and examples | core seed Modules 4, 5, and 7 PRDs | `akka-agent-governed-documents` | prompt/skill records in `src/main/java/com/example/domain/agentfoundation/`; pattern documented in `skills/akka-agent-governed-documents/SKILL.md` | executable prompt/skill snapshots are covered by resolver/readSkill tests; rubric, policy, example documents and full review lifecycle remain gaps | ◑ |
| Agent-mediated behavior editing with proposals, proposed diffs, draft versions, decision cards, and authority-expansion denial | core governed runtime agent foundation sprint | `akka-agent-behavior-editing`, `akka-agent-governed-documents`, `akka-agent-prompt-governance`, `akka-agent-skill-governance` | pattern documented in `skills/akka-agent-behavior-editing/SKILL.md` | no executable local example yet | △ |
| Akka component tools used as agent tools | `akka-context/sdk/agents/extending.html.md` | `akka-agent-component-tools`, `akka-agent-tools` | `CartInspectorAgent.java`, `src/main/java/com/example/application/ShoppingCartEntity.java` | `CartInspectorAgentTest.java` | ✅ |
| Remote MCP tools | `akka-context/sdk/agents/extending.html.md` | `akka-agent-mcp-tools` | `RemoteShoppingCartAgent.java`, `src/main/java/com/example/api/ShoppingCartToolsMcpEndpoint.java` | `RemoteShoppingCartAgentTest.java` | ✅ |
| Multimodal inputs and custom content loading | `akka-context/sdk/agents/extending.html.md`, `akka-context/sdk/agents.html.md` | `akka-agent-multimodal` | `DocumentAnalysisAgent.java` | `DocumentAnalysisAgentTest.java` | ✅ |
| Streaming token responses | `akka-context/sdk/agents/streaming.html.md` | `akka-agent-streaming` | `StreamingActivityAgent.java`, `src/main/java/com/example/api/ActivityAgentEndpoint.java` | `ActivityAgentEndpointIntegrationTest.java` exercises `/agents/activity/stream` | ✅ |
| Static multi-agent orchestration | `akka-context/sdk/agents/orchestrating.html.md` | `akka-agent-orchestration` | `AgentTeamWorkflow.java`, `WeatherAgent.java`, `ActivityWorkerAgent.java` | `AgentTeamWorkflowIntegrationTest.java` | ✅ |
| Dynamic planning / selection / summarization orchestration | `akka-context/sdk/agents/orchestrating.html.md`, `akka-context/getting-started/planner-agent/dynamic-team.html.md` | `akka-agent-orchestration` | `DynamicAgentTeamWorkflow.java`, `SelectorAgent.java`, `PlannerAgent.java`, `SummarizerAgent.java` | `SelectorAgentTest.java`, `PlannerAgentTest.java`, `DynamicAgentTeamWorkflowIntegrationTest.java`, endpoint integration test | ✅ |
| Guardrails | `akka-context/sdk/agents/guardrails.html.md` | `akka-agent-guardrails` | `CompetitorMentionGuard.java`, `src/main/resources/application.conf`, `ActivityAgent.java` | `CompetitorMentionGuardTest.java`, `ActivityAgentGuardrailIntegrationTest.java` | ✅ |
| Evaluator agents / LLM-as-judge | `akka-context/sdk/agents/llm_eval.html.md` | `akka-agent-evaluation` | `ActivityAnswerEvaluatorAgent.java` | `ActivityAnswerEvaluatorAgentTest.java` | ✅ |
| Governed closed-loop improvement with EvaluationRun, findings, proposals, replay/simulation, approval, activation, monitoring, and rollback | `akka-context/sdk/agents/llm_eval.html.md`, core seed Module 7 PRD | `akka-agent-closed-loop-improvement`, `akka-agent-evaluation`, `akka-agent-work-trace`, `akka-agent-governed-documents` | pattern documented in `skills/akka-agent-closed-loop-improvement/SKILL.md` | no executable local example yet | △ |
| Deterministic testing with `TestModelProvider` | `akka-context/sdk/agents/testing.html.md` | `akka-agent-testing` | all focused agent tests under `src/test/java/com/example/application/*Agent*Test.java` | `ActivityAgentTest.java`, `WeatherAgentTest.java`, `PlannerAgentTest.java`, `DocumentAnalysisAgentTest.java`, `WorkerMemorySummaryAgentTest.java` | ✅ |
| Governed runtime agent testing for active profile resolution, draft/test prompt use, unassigned skill denial, ToolPermissionBoundary denial, AgentBehaviorEditorAgent proposal flow, PromptAssemblyTrace, SkillLoadTrace, and cross-tenant isolation | governed runtime agent foundation sprint | `akka-agent-testing`, `akka-agent-behavior-profiles`, `akka-agent-prompt-governance`, `akka-agent-skill-governance`, `akka-agent-tool-boundaries`, `akka-agent-work-trace` | `ReferenceAgentRuntimeResolverTest`, `ReferenceSkillReadAuthorizerTest`, `ManagedReferenceActivityAgentTest`, `ManagedReferenceAgentEndpointIntegrationTest` | executable tests cover active profile resolution, disabled-agent denial, cross-tenant denial, compact manifest-only prompt, unassigned skill denial, ToolPermissionBoundary denial for `readSkill`, `PromptAssemblyTrace`, `SkillLoadTrace`, and `AgentWorkTrace`; behavior-editing proposal flow remains a gap | ◑ |
| PromptTemplate runtime state views and endpoints | `akka-context/sdk/agents/prompt.html.md` | `akka-agent-runtime-state` | `PromptTemplateHistoryView.java`, `ActivityPromptEndpoint.java`, `PromptTemplateHistoryEndpoint.java` | `ActivityPromptEndpointIntegrationTest.java`, `PromptTemplateHistoryViewIntegrationTest.java`, `PromptTemplateHistoryEndpointIntegrationTest.java` | ✅ |
| SessionMemoryEntity analytics, views, alerts, and streaming endpoints | `akka-context/sdk/agents/memory.html.md` | `akka-agent-runtime-state` | `SessionMemoryByComponentView.java`, `SessionMemoryAlertView.java`, `SessionMemoryAlertsConsumer.java`, `SessionMemoryViewEndpoint.java`, `SessionMemoryAlertStreamEndpoint.java` | `SessionMemoryByComponentViewIntegrationTest.java`, `SessionMemoryAlertsConsumerIntegrationTest.java`, `SessionMemoryViewEndpointIntegrationTest.java`, `SessionMemoryAlertStreamEndpointIntegrationTest.java` | ✅ |
| Session-memory compaction flow and audit topic | `akka-context/sdk/agents/memory.html.md` | `akka-agent-runtime-state` | `SessionMemoryCompactionAgent.java`, `SessionMemoryCompactionConsumer.java`, `SessionMemoryCompactionAudit.java`, `SessionMemoryCompactionAuditConsumer.java`, `SessionMemoryCompactionAuditView.java`, `src/main/java/com/example/api/SessionMemoryCompactionStreamEndpoint.java` | `SessionMemoryCompactionAgentTest.java`, `SessionMemoryCompactionConsumerIntegrationTest.java`, `SessionMemoryCompactionAuditConsumerIntegrationTest.java` | ◑ |
| In-code model override and multiple configured model choices | `akka-context/sdk/agents.html.md` | `akka-agent-component`, `akka-agent-model-governance` | `ConfiguredModelActivityAgent.java`, `src/main/resources/application.conf` (`openai-low-temperature`) | `ConfiguredModelActivityAgentTest.java`; governance rules documented in `skills/akka-agent-model-governance/SKILL.md` | ✅ |
| Governed model configuration with ModelConfigRef, model policy, fallback model policy, provider secret boundary, model config audit/use traces, and denied provider/secret-exposure tests | governed runtime agent foundation sprint | `akka-agent-model-governance`, `akka-agent-behavior-profiles`, `akka-agent-work-trace` | pattern documented in `skills/akka-agent-model-governance/SKILL.md`; static alias example in `ConfiguredModelActivityAgent.java` | no executable governed ModelConfigRef resolver example yet | ◑ |
| Custom model provider integration | `akka-context/sdk/agents.html.md` | none yet | no focused local example yet | no focused test yet | △ |

## Capability-first exposure coverage addendum

Use this addendum when checking whether agent-facing examples preserve governed capability semantics across tools, APIs, MCP, workflows, views, timers, and consumers.

| Capability-first pattern | Canonical local examples | Test/reference coverage | Status |
|---|---|---|---|
| Read-only entity/component-tool capability with curated output | `ShoppingCartEntity.inspectCartSummary`, `CartInspectorAgent` | `CartInspectorAgentTest` | ✅ |
| Same read-only capability reused by browser/API | `ShoppingCartEndpoint` `GET /carts/{cartId}/summary` | `ShoppingCartIntegrationTest.browserApiReusesReadOnlyInspectSummaryCapability` | ✅ |
| Remote MCP boundary with selected read-only tool/resource exposure | `ShoppingCartToolsMcpEndpoint`, `ShoppingCartMcpEndpoint`, `RemoteShoppingCartAgent` | `RemoteShoppingCartAgentTest`, `ShoppingCartMcpEndpointTest` | ✅ |
| Consequential capability uses proposal/approval before side effect | `RefundProposalTools`, `RefundApprovalAgent`, `RefundApprovalWorkflow` | `RefundApprovalCapabilityTest` | ✅ |
| Workflow-backed supervised capability with trace, approval/denial, idempotency, and validation | `SupervisedExportWorkflow` | `SupervisedExportWorkflowIntegrationTest` | ✅ |
| View-backed scoped evidence capability with redaction | `SupervisedExportEvidenceView` | `SupervisedExportEvidenceViewIntegrationTest` | ✅ |
| Timer-backed capability execution | `SupplyDecisionTimedAction`, `SupplyAutopilotWorkflow` | `SupplyDecisionTimedActionTest`, `SupplyAutopilotWorkflowIntegrationTest`, `SupplySliceAcceptanceIntegrationTest` | ◑ covered by the broader AI-first supply slice, not a minimal standalone capability-first example |
| Event-reactive consumer capability execution | existing consumer examples plus supply slice event flow | consumer integration tests exist, but no minimal capability-first consumer reference was added in the migration | △ |

## Current cleanup backlog

Small, high-value follow-ups for the next sprint:

1. Add a minimal event-reactive capability example when consumer authority, provenance, idempotency, and audit semantics need a small standalone reference.
2. Add focused coverage for `SessionMemoryCompactionStreamEndpoint`; current compaction coverage stops at agent + consumer + audit-view path.
3. Add a minimal custom-model-provider reference only if the repository decides that hosted/local built-ins are no longer enough.
4. Add an executable behavior-editing proposal example if AgentBehaviorEditorAgent flows become the next hardening priority.
5. Add a small classpath-backed skill-tool example if harness-like runtime skill loading becomes a common application pattern.
6. Consider a direct stream-agent test if endpoint-level streaming coverage becomes too indirect for future agents.

## Fast routing guide

- Need durable AgentDefinition, lifecycle, authority, or behavior profiles: read `skills/akka-agent-behavior-profiles/SKILL.md`
- Need governed prompt/skill/rubric/policy/example documents: read `skills/akka-agent-governed-documents/SKILL.md`
- Need governed runtime prompts, prompt versions, diff/review/activation, or prompt assembly traces: read `skills/akka-agent-prompt-governance/SKILL.md`
- Need governed runtime skills, manifests, readSkill, or skill load traces: read `skills/akka-agent-skill-governance/SKILL.md`
- Need behavior-editing proposals, proposed diffs, draft versions, decision cards, or authority-expansion denial: read `skills/akka-agent-behavior-editing/SKILL.md`
- Need agent work traces, prompt/skill/model/tool/data references, redaction, or timelines: read `skills/akka-agent-work-trace/SKILL.md`
- Need a plain agent class: start with `skills/akka-agent-component/SKILL.md`
- Need tools: read `skills/akka-agent-tools/SKILL.md`
- Need backend-enforced ToolPermissionBoundary grants, tool registry/catalog, denied tool semantics, approval-required expansion, or tool invocation traces: read `skills/akka-agent-tool-boundaries/SKILL.md`
- Need governed ModelConfigRef records, model policy, tenant/agent/task model selection, fallback model policy, provider secret boundary, or model config/use traces: read `skills/akka-agent-model-governance/SKILL.md`
- Need harness-like model-loadable guidance through tools: read `skills/akka-agent-harness-skills/SKILL.md`
- Need Akka component tools: read `skills/akka-agent-component-tools/SKILL.md`
- Need MCP tools: read `skills/akka-agent-mcp-tools/SKILL.md`
- Need multimodal: read `skills/akka-agent-multimodal/SKILL.md`
- Need memory/runtime state: read `skills/akka-agent-memory/SKILL.md` and `skills/akka-agent-runtime-state/SKILL.md`
- Need workflow orchestration: read `skills/akka-agent-orchestration/SKILL.md`
- Need guardrails or evaluators: read `skills/akka-agent-guardrails/SKILL.md` or `skills/akka-agent-evaluation/SKILL.md`
- Need closed-loop improvement, proposals, replay/simulation, approval, activation, or rollback: read `skills/akka-agent-closed-loop-improvement/SKILL.md`
- Need tests, including governed runtime checks for active AgentDefinition resolution, unassigned skill denial, ToolPermissionBoundary denial, PromptAssemblyTrace, SkillLoadTrace, AgentBehaviorEditorAgent proposals, and cross-tenant isolation: read `skills/akka-agent-testing/SKILL.md`
