# Task: Implement Agent Admin catalog and governed artifact reads

## Objective

Implement backend-authoritative Agent Admin catalog/detail/artifact read surfaces for managed-agent configuration, seed/provider readiness, redacted previews, trace links, and Agent Admin capability checks.

## Required reads

- AGENTS.md
- specs/full-core-smb-agent-admin/README.md
- specs/full-core-smb-agent-admin/conversation-capture.md
- specs/full-core-smb-agent-admin/sprints/01-agent-admin-full-core-sprint.md
- specs/full-core-smb-agent-admin/backlog/01-agent-admin-full-core-backlog.md
- specs/full-core-smb-agent-admin/agent-admin-implementation-map.md
- specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
- specs/agent-admin-workstream-v0/workstream-contract.md
- skills/akka-agent-tool-boundaries/SKILL.md
- skills/akka-agent-seed-documents/SKILL.md

## In scope

- Add or extract a deterministic Agent Admin service/facade for `agent_admin.list_definitions`, `agent_admin.get_definition`, prompt/skill/reference/manifest/model/tool-boundary reads, and seed/provider readiness.
- Ensure `WorkstreamService` Agent Admin surfaces are dynamically retrievable and backend-authoritative.
- Add explicit `agent_admin.*` capability visibility handling.
- Redact browser-visible previews and model/provider details.
- Add backend tests for allowed reads, forbidden reads, tenant isolation, disabled/missing capability denial, redaction, trace/correlation ids, and seed/provider readiness.

## Out of scope

- Do not implement behavior-change activation/rollback lifecycle.
- Do not implement AgentAdminAgent evidence tool.
- Do not implement frontend visual changes except when needed to keep contracts compiling.

## Expected outputs

- updated backend Agent Admin service/surface code under `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/` and/or `.../application/security/`
- updated backend tests under `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/` and/or `.../application/security/`
- updated `specs/full-core-smb-agent-admin/pending-tasks.md`

## Required checks

```bash
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=AgentBehaviorSeedLoaderTest,AgentRuntimeServiceTest,WorkstreamServiceTest,AgentDefinitionEntityTest,AgentDefinitionViewIntegrationTest,ManifestBoundaryEntityTest,ManifestBoundaryViewTest
rg -n "agent_admin\.(list_definitions|get_definition|get_prompt_version|get_skill_version|get_reference_version|get_manifest|get_model_ref|get_tool_boundary|list_seed_material)|Agent Admin|AgentDefinition|ToolPermissionBoundary|AgentSkillManifest|AgentReferenceManifest|provider|system_message|PromptAssemblyTrace|AgentWorkTrace" templates/ai-first-saas-starter/backend/src --glob '!**/target/**'
git diff --check
```

## Done criteria

- Backend Agent Admin catalog/detail/artifact reads are tenant-scoped, capability-checked, redacted, trace-linked, and source-path obvious.
- Dynamic Agent Admin surface retrieval works for implemented surface ids.
- Tests prove allowed and denied read behavior without leaking provider secrets or hidden prompts.
- Task changes and queue update are committed.

## Commit message

- `full-core-smb: implement agent admin reads`
