# UserAdminAgent Guidance Implementation Map

## Discovery commands used

```bash
find templates/ai-first-saas-starter -path '*/node_modules' -prune -o -type f -print | sort | rg -n "(UserAdminAgent|user-admin-system|agent-behavior-seeds|readSkill|readReferenceDoc|ToolPermissionBoundary|PromptAssemblyTrace|SkillLoadTrace|ReferenceLoadTrace|AgentWorkTrace|Provider|provider|system_message|WorkstreamRuntimeAgent|WorkstreamService|AgentDefinition|AgentBehavior|SkillManifest|ReferenceManifest|UserAdmin|user_admin|agentfoundation|test|frontend)"
rg -n "UserAdminAgent|user-admin-system|user-admin-agent-expertise|readSkill|readReferenceDoc|ToolPermissionBoundary|PromptAssemblyTrace|SkillLoadTrace|ReferenceLoadTrace|AgentWorkTrace|provider fail|provider.*fail|system_message|no direct mutation|mutation|ask_agent|askAgent|WorkstreamRuntimeAgent|AgentRuntimeService|AgentRuntimeToolResolver|AgentRuntimeLoaderTools|DefaultWorkstreamAgentRuntimeInvoker|FailClosedWorkstreamAgentRuntimeInvoker" templates/ai-first-saas-starter --glob '!**/node_modules/**'
```

## Current source state

The starter already has a governed request/response agent runtime path that User Admin can use:

- `WorkstreamService#submitMessage` authorizes the selected context, visible functional agent, idempotency key, and then invokes `WorkstreamAgentRuntimeInvoker`.
- `StarterSecurityComponents` wires production-like workstream messages through `DefaultWorkstreamAgentRuntimeInvoker`, `AgentRuntimeService`, `AgentRuntimeToolResolver`, and `WorkstreamRuntimeAgent`.
- `WorkstreamRuntimeAgent` is the concrete Akka `Agent` component. It receives an assembled governed prompt, uses `ModelProvider.fromConfig(...)`, registers `effects().tools(runtimeTools)`, and returns a typed `MarkdownResponse`.
- `AgentRuntimeService` performs active `AgentDefinition`, prompt, skill manifest, reference manifest, `ToolPermissionBoundary`, model binding, prompt assembly, and trace emission. It emits `PROMPT_ASSEMBLY`, `MODEL_INVOCATION`, `SkillLoadTrace`/`ReferenceLoadTrace`-equivalent `SKILL_LOAD`/`REFERENCE_LOAD`, and `AgentWorkTrace` records.
- `AgentBehaviorSeedLoader` seeds `agent-user-admin`, `prompt-user-admin-system`, `manifest-user-admin`, `reference-manifest-user-admin`, `tool-boundary-user-admin`, six User Admin skills, and six User Admin references.
- `WorkstreamRuntimeAgentTest`, `AgentRuntimeServiceTest`, `AgentRuntimeToolResolverTest`, and `workstream-user-admin-expertise.contract.test.mjs` already cover prompt assembly, loader tools, manifest-only prompt context, denied unassigned loads, model/provider fail-closed behavior, and frontend expertise fixture visibility.

## Boundary gaps for useful UserAdminAgent guidance

1. `tool-boundary-user-admin` already grants `userAdminEvidence.read`, but `ToolRegistry.starterDefault()` registers only `readSkill` and `readReferenceDoc`. `AgentRuntimeToolResolverTest#resolvesApprovedStableToolIdsIntoDeterministicRuntimeTools` proves `userAdminEvidence.read` is currently denied because no registry binding exists.
2. There is no model-facing evidence facade that returns scoped User Admin dashboard/member/invitation/role/status evidence. The agent can load procedural skills and references, but cannot fetch deterministic runtime evidence except what is included in the redacted user prompt.
3. `WorkstreamService#submitMessage` maps runtime/provider denial to a blocked `markdown_response`; the SMB contract prefers actionable typed `system_message` surfaces for provider/runtime blocked states.
4. The seed prompt and expertise resources describe safe User Admin behavior, but they should be tightened around request/response guidance now that deterministic access-management surfaces exist: no direct mutation, use evidence tool, cite trace ids, and guide users to deterministic actions.
5. There is no focused backend test proving UserAdminAgent evidence reads cannot mutate invitations, memberships, roles, or authorization state. Mutation denial is currently indirect through the lack of side-effecting tool registrations.
6. Frontend fixtures and renderers already understand `system_message`, `blocked_provider_or_runtime`, traces, and User Admin expertise, but they need runtime-aligned tests once backend blocked-agent responses switch from blocked markdown to typed `system_message`.

## Backend implementation boundary

Primary files:

- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/ToolRegistry.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeToolResolver.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/WorkstreamRuntimeAgent.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentBehaviorSeedLoader.java`
- new or extended read-only facade near `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/UserAdminEvidenceTools.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/StarterSecurityComponents.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamService.java`
- deterministic evidence sources: `UserAdminService`, `UserDirectoryView`, `InvitationView`, and existing User Admin surface builders in `WorkstreamService`
- seed resources under `templates/ai-first-saas-starter/backend/src/main/resources/agent-behavior-seeds/starter-v1/`
- backend tests under `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/` and `.../application/security/WorkstreamServiceTest.java`

Required backend scope:

1. Register `userAdminEvidence.read` as a read-only local function/facade tool with a stable tool catalog entry and capability id such as `user_admin.view_overview` or a narrow evidence capability inherited from the User Admin contracts.
2. Implement the facade as read-only and request-scoped: it must derive tenant/customer scope and caller authority from `AuthContext`, enforce tenant match and capability, call deterministic views/services only for evidence, redact browser/model-visible fields, and emit/access-link trace ids.
3. Keep `readSkill(skillId)` and `readReferenceDoc(referenceId)` as separate governed loader tools; `userAdminEvidence.read` must not grant skill/reference loading or mutation authority.
4. Tighten seed prompt/skill/reference wording so UserAdminAgent asks for scoped evidence, explains blocked invitations/member status/role changes, recommends deterministic surface actions, and refuses to claim it performed mutations.
5. Convert provider/runtime-blocked workstream submission output to a typed actionable `system_message` surface or explicitly named provider-blocked surface that carries safe reason, recovery, trace ids, and capability id without provider secrets.
6. Preserve the concrete Akka Agent runtime path. Do not implement a direct service/provider shortcut or deterministic canned normal response for successful User Admin guidance.

Backend non-goals:

- no direct invite/resend/revoke/disable/reactivate/role-change tool in this mini-project;
- no access-review `AutonomousAgent` worker;
- no Agent Admin behavior-editing expansion except seed/runtime changes necessary for User Admin guidance;
- no enterprise role-builder or custom IAM model.

## Frontend implementation boundary

Primary files:

- `templates/ai-first-saas-starter/frontend/src/workstream/stream/WorkstreamItem.tsx`
- `templates/ai-first-saas-starter/frontend/src/workstream/surfaces/MarkdownResponseSurface.tsx`
- `templates/ai-first-saas-starter/frontend/src/workstream/surfaces/SurfaceRenderer.tsx`
- `templates/ai-first-saas-starter/frontend/src/workstream/surfaces/SurfaceStateFrame.tsx`
- `templates/ai-first-saas-starter/frontend/src/workstream/fixtures/surfaces.ts`
- `templates/ai-first-saas-starter/frontend/src/workstream-user-admin-expertise.contract.test.mjs`
- `templates/ai-first-saas-starter/frontend/src/workstream-composer-message-api.contract.test.mjs`
- `templates/ai-first-saas-starter/frontend/src/workstream-surfaces.contract.test.mjs`
- root `frontend/` mirrors only if the repository convention requires synchronization for touched starter UI files.

Frontend scope:

1. Render provider/runtime-blocked agent responses as first-class `system_message`/blocked surfaces with recovery guidance and trace links.
2. Keep successful UserAdminAgent guidance as `markdown_response` only when it is bounded explanation/summary/guidance, not tables/forms/decision cards.
3. Add contract tests for no provider-secret exposure, trace link rendering, blocked state copy, and no UI implication that the agent directly changed access.

## Validation boundary

Targeted checks for implementation tasks:

```bash
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=AgentBehaviorSeedLoaderTest,AgentRuntimeServiceTest,AgentRuntimeToolResolverTest,WorkstreamRuntimeAgentTest,WorkstreamServiceTest
cd templates/ai-first-saas-starter/frontend && npm test -- --runTestsByPath src/workstream-user-admin-expertise.contract.test.mjs src/workstream-composer-message-api.contract.test.mjs src/workstream-surfaces.contract.test.mjs
rg -n "UserAdminAgent|user-admin-system|readSkill|readReferenceDoc|ToolPermissionBoundary|PromptAssemblyTrace|SkillLoadTrace|ReferenceLoadTrace|AgentWorkTrace|provider fail|system_message|no direct mutation|userAdminEvidence\.read" templates/ai-first-saas-starter --glob '!**/node_modules/**'
git diff --check
```

Run `tools/validate-ai-first-saas-starter-fullstack.sh` after backend and frontend changes both land, or when the task claims local fullstack runtime/API/UI readiness for UserAdminAgent guidance.

## Appended implementation tasks

- `TASK-FCSMB-UAG-01-002`: implement backend UserAdminAgent evidence tool, seed prompt/runtime updates, and tests.
- `TASK-FCSMB-UAG-01-003`: implement frontend blocked guidance and trace rendering updates if backend response DTOs/surfaces changed.
- `TASK-FCSMB-UAG-01-004`: run integrated UserAdminAgent guidance validation and either close readiness or append blockers.
