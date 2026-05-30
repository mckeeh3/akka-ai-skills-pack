# Task: Implement AgentAdminAgent governed guidance evidence path

## Objective

Make AgentAdminAgent request/response guidance useful through governed Akka Agent runtime, seed guidance, loader tools, an optional read-only Agent Admin evidence tool, and provider fail-closed `system_message` behavior.

## Required reads

- AGENTS.md
- specs/full-core-smb-agent-admin/README.md
- specs/full-core-smb-agent-admin/conversation-capture.md
- specs/full-core-smb-agent-admin/sprints/01-agent-admin-full-core-sprint.md
- specs/full-core-smb-agent-admin/backlog/01-agent-admin-full-core-backlog.md
- specs/full-core-smb-agent-admin/agent-admin-implementation-map.md
- specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
- specs/agent-admin-workstream-v0/workstream-contract.md
- docs/agent-component-selection-guide.md
- skills/akka-agents/SKILL.md
- skills/akka-agent-tools/SKILL.md
- skills/akka-agent-tool-boundaries/SKILL.md
- skills/akka-agent-seed-documents/SKILL.md

## In scope

- Tighten Agent Admin seed prompt/skill/reference content around no direct mutation, proposal-only drafting, provider readiness, redaction, tool-boundary denials, and trace-linked explanations.
- Add `agentAdminEvidence.read` only if needed as a read-only capability facade over deterministic Agent Admin evidence; otherwise record why loader-only guidance is sufficient for this slice.
- Register/enforce any new tool through `ToolRegistry`, `AgentRuntimeToolResolver`, active `ToolPermissionBoundary`, and tests.
- Preserve `WorkstreamRuntimeAgent` as the concrete Akka Agent runtime path for normal user-facing turns.
- Prove missing provider/runtime configuration returns typed `system_message` and does not produce canned successful guidance.

## Out of scope

- Do not allow AgentAdminAgent to approve, activate, rollback, reseed, mutate model refs, or change tool boundaries directly.
- Do not implement durable prompt-risk worker.

## Expected outputs

- updated seed resources under `templates/ai-first-saas-starter/backend/src/main/resources/agent-behavior-seeds/starter-v1/`
- updated agent runtime/tool-boundary code and tests where needed
- updated frontend contract tests only if response fixture/runtime contracts change
- updated `specs/full-core-smb-agent-admin/pending-tasks.md`

## Required checks

```bash
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=AgentBehaviorSeedLoaderTest,AgentRuntimeServiceTest,AgentRuntimeToolResolverTest,WorkstreamRuntimeAgentTest,WorkstreamServiceTest
cd templates/ai-first-saas-starter/frontend && npm test -- --runTestsByPath src/workstream-agent-admin-vertical.contract.test.mjs src/workstream-composer-message-api.contract.test.mjs src/workstream-surfaces.contract.test.mjs
rg -n "AgentAdminAgent|agent-admin-system|agent-admin-starter-guidance|readSkill|readReferenceDoc|agentAdminEvidence\.read|ToolPermissionBoundary|PromptAssemblyTrace|SkillLoadTrace|ReferenceLoadTrace|AgentWorkTrace|provider|system_message|no direct mutation|proposal|activate|rollback" templates/ai-first-saas-starter --glob '!**/node_modules/**'
git diff --check
```

## Done criteria

- AgentAdminAgent normal runtime remains model-backed through concrete Akka Agent invocation.
- Provider/runtime missing path fails closed with actionable typed surface and traces.
- Guidance can explain/read scoped evidence but cannot mutate or activate behavior.
- Task changes and queue update are committed.

## Commit message

- `full-core-smb: implement agent admin guidance`
