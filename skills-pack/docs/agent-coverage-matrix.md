# Agent coverage matrix

Purpose: map official Akka agent topics to local skills, current curated examples, and known coverage.

Maintenance note: rows that cite `../examples/akka-components/**` are installed skills-pack source-attention references curated from the runnable root app. Recheck this file whenever root app examples move or the curated subset changes. Do not list historical/demo class names unless they exist in the current curated examples set.

Legend:
- ✅ covered with local skill + current curated example/test
- ◑ partially covered or primarily documented guidance
- △ notable gap for a future cleanup sprint

| Official topic | Official source | Local skill(s) | Current curated examples | Test/reference coverage | Status |
|---|---|---|---|---|---|
| Request-based Agent structure, runtime tool loading, structured fallback, and governed prompt/reference context | `akka-context/sdk/agents.html.md`, `akka-context/sdk/agents/prompt.html.md`, `akka-context/sdk/agents/structured.html.md`, `akka-context/sdk/agents/testing.html.md` | `akka-agents`, `akka-agent-component`, `akka-agent-structured-responses`, `akka-agent-tools`, `akka-agent-testing` | `../examples/akka-components/src/main/java/ai/first/application/foundation/agent/WorkstreamRuntimeAgent.java`, `DefaultWorkstreamAgentRuntimeInvoker.java`, `FailClosedWorkstreamAgentRuntimeInvoker.java`, `AgentRuntimeLoaderTools.java`, `AgentRuntimeService.java` | `../examples/akka-components/src/test/java/ai/first/application/foundation/agent/WorkstreamRuntimeAgentTest.java`, `AgentRuntimeServiceTest.java` | ✅ |
| Autonomous Agents for durable internal/background typed tasks | `akka-context/sdk/autonomous-agents.html.md`, `akka-context/sdk/autonomous-agents/defining.html.md`, `akka-context/sdk/autonomous-agents/tasks.html.md`, `akka-context/sdk/autonomous-agents/testing.html.md` | `akka-autonomous-agents`, `akka-autonomous-agent-tasks`, `akka-autonomous-agent-testing`, `akka-autonomous-agent-governance` | `UserAdminAccessReviewAutonomousAgent.java`, `AgentAdminPromptRiskAutonomousAgent.java`, `AuditTraceSummaryAutonomousAgent.java`, `GovernancePolicyImpactAutonomousAgent.java`, `MyAccountPersonalAttentionDigestAutonomousAgent.java` | selected service tests and durable repository entity tests such as `DurableAccessReviewTaskRepositoryEntityTest.java` and `DurablePromptRiskReviewTaskRepositoryEntityTest.java` | ◑ |
| Governed AgentDefinition, prompt/skill/reference documents, manifests, tool boundaries, and seed loading | governed runtime agent foundation specs | `akka-agent-behavior-profiles`, `akka-agent-governed-documents`, `akka-agent-prompt-governance`, `akka-agent-skill-governance`, `akka-agent-reference-governance`, `akka-agent-tool-boundaries`, `akka-agent-seed-documents` | `AgentDefinitionEntity.java`, `PromptDocumentEntity.java`, `SkillDocumentEntity.java`, `ReferenceDocumentEntity.java`, `AgentSkillManifestEntity.java`, `AgentReferenceManifestEntity.java`, `ToolPermissionBoundaryEntity.java`, `AgentBehaviorSeedLoader.java` | `AgentDefinitionEntityTest.java`, `GovernedDocumentEntityTest.java`, `ManifestBoundaryEntityTest.java`, `AgentRuntimeServiceTest.java` | ✅ |
| Agent work traces, prompt/skill/reference/model/tool facts, and redaction-safe audit linkage | governed runtime agent foundation specs | `akka-agent-work-trace`, `ai-first-saas-audit-trace`, `akka-agent-model-governance`, `akka-agent-testing` | `AgentRuntimeTraceEntity.java`, `AgentRuntimeTraceView.java`, `AuditTraceSummaryAutonomousAgent.java` | `AgentRuntimeTraceEntityTest.java`, `AuditTraceSummaryAutonomousAgentTest.java`, `AgentRuntimeServiceTest.java` | ✅ |
| Governed model configuration and provider secret boundary | `akka-context/sdk/agents.html.md` | `akka-agent-model-governance`, `akka-agent-component`, `akka-agent-testing` | `ModelConfigRef.java`, `DefaultWorkstreamAgentRuntimeInvoker.java`, `FailClosedWorkstreamAgentRuntimeInvoker.java` | `FoundationRuntimeDurabilityBoundaryTest.java`, `AgentRuntimeServiceTest.java` | ◑ |
| Local function tools and governed evidence/tools boundaries | `akka-context/sdk/agents/extending.html.md` | `akka-agent-tools`, `akka-agent-tool-boundaries`, `akka-agent-component-tools` | `UserAdminEvidenceTools.java`, `AgentRuntimeLoaderTools.java`, `ToolPermissionBoundary.java`, `ToolPermissionBoundaryEntity.java` | entity/service tests plus governed boundary guidance | ◑ |
| Memory/runtime state, prompt-template built-ins, and session-memory analytics | `akka-context/sdk/agents/memory.html.md`, `akka-context/sdk/agents/prompt.html.md` | `akka-agent-memory`, `akka-agent-runtime-state` | Current core app primarily uses governed behavior documents and runtime traces; built-in `PromptTemplate`/`SessionMemoryEntity` guidance remains documented in `agent-runtime-state-reference.md` | No current curated PromptTemplate/session-memory view examples | ◑ |
| Workflow orchestration around agents, approvals, timeouts, and compensation | `akka-context/sdk/agents/orchestrating.html.md`, workflow docs | `akka-agent-orchestration`, `akka-workflows`, `akka-workflow-pausing`, `akka-workflow-compensation`, `akka-workflow-testing` | Current core app uses service/repository/autonomous-agent seams for the five-core workstreams; standalone workflow examples are documented patterns, not current curated classes | Workflow pattern docs and service tests; add a current minimal workflow example if future work needs executable workflow coverage | ◑ |
| Guardrails, evaluators, multimodal, remote MCP tools, and custom model providers | official Akka agent extension docs | `akka-agent-guardrails`, `akka-agent-evaluation`, `akka-agent-multimodal`, `akka-agent-mcp-tools`, `akka-mcp-endpoints` | Guidance skills exist; no current focused executable examples in the curated subset | Primarily documented guidance; add focused examples only when the root app needs those runtime paths | △ |
| Closed-loop behavior improvement and agent-mediated behavior editing | governed runtime agent foundation specs | `akka-agent-behavior-editing`, `akka-agent-closed-loop-improvement`, `akka-agent-governed-documents`, `akka-agent-work-trace` | `AgentAdminService.java`, `AgentAdminPromptRiskAutonomousAgent.java`, governed document/entity state | governed document/entity tests and selected autonomous/task review examples | ◑ |

## Capability-first exposure coverage addendum

| Capability-first pattern | Current curated examples | Test/reference coverage | Status |
|---|---|---|---|
| Browser/API workstream capability surface | `WorkstreamEndpoint.java`, `WorkstreamService.java` | `WorkstreamServiceTest.java`; endpoint examples exist for `AdminEndpoint` and `MeEndpoint` | ◑ |
| Authenticated foundation API and user/admin access | `MeEndpoint.java`, `AdminEndpoint.java`, `MeService.java`, `UserAdminService.java`, `UserDirectoryView.java` | `MeServiceTest.java`, `FoundationRuntimeDurabilityBoundaryTest.java`, `InvitationAndUserAdminServiceTest.java` | ✅ |
| Invitation, email, and Resend delivery patterns | `DurableInvitationRepositoryEntity.java`, `InvitationView.java`, `ResendEmailService.java`, `EmailNotificationService.java` | invitation entity tests and `RealResendProviderSmokeTest.java` | ◑ |
| Governed read/evidence tools for agents | `UserAdminEvidenceTools.java`, `AgentRuntimeLoaderTools.java` | service/entity tests plus bounded evidence guidance | ◑ |
| Durable task/repository-backed autonomous work | durable task repository entity tests under `application/coreapp/**` | selected durable repository entity tests | ◑ |
| Timer-, workflow-, MCP-, and custom-provider-backed capability examples | documented skills and pattern docs | no current minimal executable examples in curated examples | △ |

## Known coverage gaps

1. Add a current minimal workflow example if workflow-specific skills need executable class references.
2. Add current focused examples for SSE/WebSocket/MCP/timer/custom-provider paths only when the runnable root app needs those paths.
3. Add built-in `PromptTemplate`/`SessionMemoryEntity` examples if the root app adopts those runtime-state APIs in addition to governed behavior documents.
4. Keep this matrix aligned with `skills-pack/examples/akka-components/**`; do not mark rows ✅ for examples that are only conceptual.

## Fast routing guide

- Need user-facing functional/context-area agents in a generated SaaS workstream app: read `docs/agent-workstream-application-architecture.md`, `../agent-workstream-apps/SKILL.md`, then `../akka-agents/SKILL.md`.
- Need durable AgentDefinition, lifecycle, authority, workstream placement, or behavior profiles: read `../akka-agent-behavior-profiles/SKILL.md`.
- Need governed prompt/skill/reference/model/tool-boundary documents: read `../akka-agent-governed-documents/SKILL.md` plus the focused governance skills.
- Need agent work traces: read `../akka-agent-work-trace/SKILL.md`.
- Need to choose between request-based Agent, AutonomousAgent, Workflow, Workflow + Agent, or Workflow + AutonomousAgent: read `docs/agent-component-selection-guide.md`.
- Need request-based Agent tests: read `../akka-agent-testing/SKILL.md`.
- Need Autonomous Agent task, lifecycle, coordination, notification, or task-rule tests: read `../akka-autonomous-agent-testing/SKILL.md`.
