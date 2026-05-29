# Task: Implement backend UserAdminAgent guidance runtime and evidence tool

## Objective

Make UserAdminAgent useful through the governed Akka Agent runtime by adding read-only scoped User Admin evidence, tightening seed guidance, and preserving provider fail-closed behavior.

## Required reads

- AGENTS.md
- specs/full-core-smb-user-admin-agent-guidance/README.md
- specs/full-core-smb-user-admin-agent-guidance/agent-guidance-implementation-map.md
- specs/full-core-smb-user-admin/user-admin-vertical-contracts.md
- specs/full-core-smb-user-admin-access-management/access-management-implementation-map.md
- skills/akka-agents/SKILL.md
- skills/akka-agent-tools/SKILL.md
- skills/akka-agent-tool-boundaries/SKILL.md
- skills/akka-agent-seed-documents/SKILL.md

## In scope

- Register and implement a read-only `userAdminEvidence.read` governed runtime tool/facade.
- Use deterministic User Admin evidence sources only; enforce `AuthContext`, tenant/customer scope, role/capability, redaction, and trace fields before returning evidence to the model.
- Update `user-admin-system.md` and related User Admin seed skill/reference/expertise text only as needed to guide request/response summaries, explanations, safe next steps, and no direct mutation.
- Preserve `readSkill(skillId)` and `readReferenceDoc(referenceId)` as separate granted loader tools.
- Add or update backend tests for allowed evidence, denied cross-scope/unauthorized evidence, no direct mutation, prompt/skill/reference/tool traces, and provider fail-closed behavior.

## Out of scope

- No direct mutation tools for invite/resend/revoke/disable/reactivate/role changes.
- No access-review AutonomousAgent worker.
- No deterministic/model-less successful normal response in place of the Akka Agent model path.
- No broad frontend work except changing DTO/surface shape needed for backend tests.

## Expected outputs

- Updated backend agentfoundation/security source under `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/`.
- Updated User Admin seed resources under `templates/ai-first-saas-starter/backend/src/main/resources/agent-behavior-seeds/starter-v1/` when needed.
- Updated backend tests under `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/` and/or `.../application/security/`.

## Required checks

```bash
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=AgentBehaviorSeedLoaderTest,AgentRuntimeServiceTest,AgentRuntimeToolResolverTest,WorkstreamRuntimeAgentTest,WorkstreamServiceTest
rg -n "userAdminEvidence\.read|UserAdminAgent|user-admin-system|readSkill|readReferenceDoc|ToolPermissionBoundary|PromptAssemblyTrace|SkillLoadTrace|ReferenceLoadTrace|AgentWorkTrace|provider fail|system_message|no direct mutation" templates/ai-first-saas-starter/backend --glob '!**/target/**'
git diff --check
```

## Done criteria

- UserAdminAgent has a registered read-only evidence tool available through `effects().tools(runtimeTools)` when boundary-granted.
- Evidence tool returns scoped, redacted deterministic evidence and cannot mutate access state.
- Missing provider/configuration fails closed with safe traces/system-message-compatible response; no canned successful guidance is used.
- Backend tests prove allowed, denied, no-mutation, trace, and provider-fail-closed behavior.
- Queue status is updated and the task changes are committed with message `full-core-smb: implement user admin agent backend guidance`.
