# Managed-Agent Foundation Validation

## Scope

Validation artifact for `TASK-FCSR-05-001` at the selected full-core scope: Agent Admin plus runtime workstream agents use governed AgentDefinition, prompt, skill, reference, manifest, tool-boundary, model-binding, loader, proposal/review/activation/rollback, and trace paths.

## Evidence

- Seeded governed defaults are imported through `AgentBehaviorSeedLoader` into tenant-scoped application state for AgentDefinition, PromptDocument, SkillDocument, ReferenceDocument, AgentSkillManifest, AgentReferenceManifest, ToolPermissionBoundary, ModelConfigRef, and ModelPolicy.
- Runtime prompt assembly in `AgentRuntimeService` resolves active AgentDefinition, prompt, skill manifest, reference manifest, tool boundary, model config, and model policy before model invocation.
- Prompt assembly includes compact skill/reference manifests only; full skill/reference bodies load through governed `readSkill(skillId)` and `readReferenceDoc(referenceId)` loader tools.
- `readSkill` and `readReferenceDoc` require selected AuthContext capability, active manifests, active documents, active ToolPermissionBoundary grants, mode checks, token/secret-like-content checks, and emit allowed/denied load traces.
- Model-backed runtime paths invoke `WorkstreamRuntimeAgent`/provider through governed runtime preparation and fail closed when provider configuration or runtime tool context is invalid.
- Behavior-change proposals require backend capability, create reviewable proposals, deny prompt/skill/reference text authority-expansion attempts, route tool-boundary authority expansion to denial/review, and keep active behavior unchanged until approved activation.
- Prompt, skill, reference, and tool-boundary activation record rollback metadata; rollback restores the prior governed record.
- Agent Admin workstream surfaces expose browser-safe catalog/detail/prompt/skill/reference/manifest/tool-boundary/model/seed evidence and no direct mutation flags.

## Checks run

```bash
mvn test -Dtest=AgentRuntimeServiceTest,AgentBehaviorSeedLoaderTest,AgentRuntimeTraceSinkTest,AgentRuntimeTraceEntityTest,AgentRuntimeTraceViewTest,WorkstreamRuntimeAgentTest
```

Result: passed locally. The WorkstreamRuntimeAgent negative runtime-tool-tenant-mismatch path logs an expected fail-closed exception during the passing test suite.

## Remaining production prerequisites

- Live model-provider smoke remains dependent on backend-only provider configuration such as `OPENAI_API_KEY`; missing configuration fails closed and is covered by tests.
- Broader Audit/Trace investigation and Governance/Policy search/approval surfaces remain in `TASK-FCSR-06-001`.
