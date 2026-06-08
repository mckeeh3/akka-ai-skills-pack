# Realization: Akka components for Agent Admin

Capability: `managed-agent-governance`.

This map is docs-only. It points to current implementation evidence and does not change runtime behavior.

## Component and service evidence

| Intent binding | Akka / Java evidence | Notes |
|---|---|---|
| Managed agent definitions and behavior repository | `src/main/java/ai/first/application/foundation/agent/AgentDefinitionEntity.java`, `DurableAgentBehaviorRepositoryEntity.java`, `AgentBehaviorRepository.java`, `AgentDefinitionView.java` | Tenant-scoped managed-agent state is durable and governed. |
| Governed prompt/skill/reference documents and manifests | `PromptDocumentEntity.java`, `SkillDocumentEntity.java`, `ReferenceDocumentEntity.java`, `AgentSkillManifestEntity.java`, `AgentReferenceManifestEntity.java`, matching views | Loader access is manifest/boundary-scoped; prompt text cannot expand authority. |
| Tool permission boundaries and registry | `ToolPermissionBoundaryEntity.java`, `ToolBoundaryGrantView.java`, `ToolRegistry.java`, `AgentRuntimeToolResolver.java` | Tool exposure must match governed-tool ids and be denied/traced when outside scope. |
| Workstream runtime agent and loader tools | `WorkstreamRuntimeAgent.java`, `AgentRuntimeLoaderTools.java`, `DefaultWorkstreamAgentRuntimeInvoker.java`, `FailClosedWorkstreamAgentRuntimeInvoker.java` | Model-backed workstream agents must use active configuration or fail closed. |
| Runtime trace capture | `AgentRuntimeTraceEntity.java`, `AgentRuntimeTraceView.java`, `AgentRuntimeTraceSink.java`, `AkkaAgentRuntimeTraceSink.java` | Prompt/skill/reference/model/tool/data/policy events require durable traces. |
| Agent Admin services and prompt risk worker | `AgentAdminService.java`, `AgentMarketplaceGovernanceService.java`, `AgentAdminPromptRiskReviewService.java`, `AgentAdminPromptRiskAutonomousAgent.java`, `DurablePromptRiskReviewTaskRepositoryEntity.java` | Behavior proposals and risk reviews support human-governed activation/rollback. |
| Seed content import | `src/main/resources/agent-behavior-seeds/starter-v1/**`, `AgentBehaviorSeedLoader.java` | Seed provenance should preserve customization and upgrade safety. |

## Validation evidence

- `src/test/java/ai/first/application/foundation/agent/AgentDefinitionEntityTest.java`
- `src/test/java/ai/first/application/foundation/agent/GovernedDocumentEntityTest.java`
- `src/test/java/ai/first/application/foundation/agent/ManifestBoundaryEntityTest.java`
- `src/test/java/ai/first/application/foundation/agent/AgentRuntimeServiceTest.java`
- `src/test/java/ai/first/application/foundation/agent/WorkstreamRuntimeAgentTest.java`
- `src/test/java/ai/first/application/coreapp/agentadmin/AgentAdminPromptRiskAutonomousAgentTest.java`
- `frontend/src/workstream-agent-admin-vertical.contract.test.mjs`

## Gaps / caveats

- Fake model providers and local demo repositories are test-only. Normal runtime must use governed provider configuration or fail closed.
