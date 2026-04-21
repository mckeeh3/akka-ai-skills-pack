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
| Calling agents from workflows or other components | `akka-context/sdk/agents/calling.html.md` | `akka-agent-orchestration`, `akka-agents` | `AgentTeamWorkflow.java`, `DynamicAgentTeamWorkflow.java`, `src/main/java/com/example/api/DynamicAgentTeamWorkflowEndpoint.java` | `AgentTeamWorkflowIntegrationTest.java`, `DynamicAgentTeamWorkflowIntegrationTest.java`, `DynamicAgentTeamWorkflowEndpointIntegrationTest.java` | ✅ |
| Session ids, bounded memory, filtered reads, shared sessions | `akka-context/sdk/agents/memory.html.md` | `akka-agent-memory`, `akka-agent-orchestration` | `WorkerMemorySummaryAgent.java`, `AgentTeamWorkflow.java`, `DynamicAgentTeamWorkflow.java` | `WorkerMemorySummaryAgentTest.java`, workflow integration tests above | ✅ |
| Structured responses with `responseConformsTo(...)` | `akka-context/sdk/agents/structured.html.md` | `akka-agent-structured-responses` | `ActivityAgent.java`, `SelectorAgent.java`, `PlannerAgent.java` | `ActivityAgentTest.java`, `SelectorAgentTest.java`, `PlannerAgentTest.java` | ✅ |
| Failure handling and narrow fallback mapping | `akka-context/sdk/agents/failures.html.md` | `akka-agent-component`, `akka-agent-structured-responses` | `ActivityAgent.java` | `ActivityAgentTest.java` covers malformed JSON fallback and non-parsing failure propagation | ✅ |
| Local `@FunctionTool` methods and external tool classes | `akka-context/sdk/agents/extending.html.md` | `akka-agent-tools` | `WeatherAgent.java`, `WeatherForecastTools.java` | `WeatherAgentTest.java` | ✅ |
| Akka component tools used as agent tools | `akka-context/sdk/agents/extending.html.md` | `akka-agent-component-tools`, `akka-agent-tools` | `CartInspectorAgent.java`, `src/main/java/com/example/application/ShoppingCartEntity.java` | `CartInspectorAgentTest.java` | ✅ |
| Remote MCP tools | `akka-context/sdk/agents/extending.html.md` | `akka-agent-mcp-tools` | `RemoteShoppingCartAgent.java`, `src/main/java/com/example/api/ShoppingCartToolsMcpEndpoint.java` | `RemoteShoppingCartAgentTest.java` | ✅ |
| Multimodal inputs and custom content loading | `akka-context/sdk/agents/extending.html.md`, `akka-context/sdk/agents.html.md` | `akka-agent-multimodal` | `DocumentAnalysisAgent.java` | `DocumentAnalysisAgentTest.java` | ✅ |
| Streaming token responses | `akka-context/sdk/agents/streaming.html.md` | `akka-agent-streaming` | `StreamingActivityAgent.java`, `src/main/java/com/example/api/ActivityAgentEndpoint.java` | `ActivityAgentEndpointIntegrationTest.java` exercises `/agents/activity/stream` | ✅ |
| Static multi-agent orchestration | `akka-context/sdk/agents/orchestrating.html.md` | `akka-agent-orchestration` | `AgentTeamWorkflow.java`, `WeatherAgent.java`, `ActivityWorkerAgent.java` | `AgentTeamWorkflowIntegrationTest.java` | ✅ |
| Dynamic planning / selection / summarization orchestration | `akka-context/sdk/agents/orchestrating.html.md`, `akka-context/getting-started/planner-agent/dynamic-team.html.md` | `akka-agent-orchestration` | `DynamicAgentTeamWorkflow.java`, `SelectorAgent.java`, `PlannerAgent.java`, `SummarizerAgent.java` | `SelectorAgentTest.java`, `PlannerAgentTest.java`, `DynamicAgentTeamWorkflowIntegrationTest.java`, endpoint integration test | ✅ |
| Guardrails | `akka-context/sdk/agents/guardrails.html.md` | `akka-agent-guardrails` | `CompetitorMentionGuard.java`, `src/main/resources/application.conf`, `ActivityAgent.java` | `CompetitorMentionGuardTest.java`, `ActivityAgentGuardrailIntegrationTest.java` | ✅ |
| Evaluator agents / LLM-as-judge | `akka-context/sdk/agents/llm_eval.html.md` | `akka-agent-evaluation` | `ActivityAnswerEvaluatorAgent.java` | `ActivityAnswerEvaluatorAgentTest.java` | ✅ |
| Deterministic testing with `TestModelProvider` | `akka-context/sdk/agents/testing.html.md` | `akka-agent-testing` | all focused agent tests under `src/test/java/com/example/application/*Agent*Test.java` | `ActivityAgentTest.java`, `WeatherAgentTest.java`, `PlannerAgentTest.java`, `DocumentAnalysisAgentTest.java`, `WorkerMemorySummaryAgentTest.java` | ✅ |
| PromptTemplate runtime state views and endpoints | `akka-context/sdk/agents/prompt.html.md` | `akka-agent-runtime-state` | `PromptTemplateHistoryView.java`, `ActivityPromptEndpoint.java`, `PromptTemplateHistoryEndpoint.java` | `ActivityPromptEndpointIntegrationTest.java`, `PromptTemplateHistoryViewIntegrationTest.java`, `PromptTemplateHistoryEndpointIntegrationTest.java` | ✅ |
| SessionMemoryEntity analytics, views, alerts, and streaming endpoints | `akka-context/sdk/agents/memory.html.md` | `akka-agent-runtime-state` | `SessionMemoryByComponentView.java`, `SessionMemoryAlertView.java`, `SessionMemoryAlertsConsumer.java`, `SessionMemoryViewEndpoint.java`, `SessionMemoryAlertStreamEndpoint.java` | `SessionMemoryByComponentViewIntegrationTest.java`, `SessionMemoryAlertsConsumerIntegrationTest.java`, `SessionMemoryViewEndpointIntegrationTest.java`, `SessionMemoryAlertStreamEndpointIntegrationTest.java` | ✅ |
| Session-memory compaction flow and audit topic | `akka-context/sdk/agents/memory.html.md` | `akka-agent-runtime-state` | `SessionMemoryCompactionAgent.java`, `SessionMemoryCompactionConsumer.java`, `SessionMemoryCompactionAudit.java`, `SessionMemoryCompactionAuditConsumer.java`, `SessionMemoryCompactionAuditView.java`, `src/main/java/com/example/api/SessionMemoryCompactionStreamEndpoint.java` | `SessionMemoryCompactionAgentTest.java`, `SessionMemoryCompactionConsumerIntegrationTest.java`, `SessionMemoryCompactionAuditConsumerIntegrationTest.java` | ◑ |
| In-code model override and multiple configured model choices | `akka-context/sdk/agents.html.md` | `akka-agent-component` | `ConfiguredModelActivityAgent.java`, `src/main/resources/application.conf` (`openai-low-temperature`) | `ConfiguredModelActivityAgentTest.java` | ✅ |
| Custom model provider integration | `akka-context/sdk/agents.html.md` | none yet | no focused local example yet | no focused test yet | △ |

## Current cleanup backlog

Small, high-value follow-ups for the next sprint:

1. Add focused coverage for `SessionMemoryCompactionStreamEndpoint`; current compaction coverage stops at agent + consumer + audit-view path.
2. Add a minimal custom-model-provider reference only if the repository decides that hosted/local built-ins are no longer enough.
3. Consider a direct stream-agent test if endpoint-level streaming coverage becomes too indirect for future agents.

## Fast routing guide

- Need a plain agent class: start with `skills/akka-agent-component/SKILL.md`
- Need tools: read `skills/akka-agent-tools/SKILL.md`
- Need Akka component tools: read `skills/akka-agent-component-tools/SKILL.md`
- Need MCP tools: read `skills/akka-agent-mcp-tools/SKILL.md`
- Need multimodal: read `skills/akka-agent-multimodal/SKILL.md`
- Need memory/runtime state: read `skills/akka-agent-memory/SKILL.md` and `skills/akka-agent-runtime-state/SKILL.md`
- Need workflow orchestration: read `skills/akka-agent-orchestration/SKILL.md`
- Need guardrails or evaluators: read `skills/akka-agent-guardrails/SKILL.md` or `skills/akka-agent-evaluation/SKILL.md`
- Need tests: read `skills/akka-agent-testing/SKILL.md`
