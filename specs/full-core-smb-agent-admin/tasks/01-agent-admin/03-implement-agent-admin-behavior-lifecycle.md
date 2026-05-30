# Task: Implement Agent Admin behavior-change lifecycle

## Objective

Implement deterministic draft/submit/review/activate/cancel/rollback lifecycle semantics for SMB Agent Admin behavior changes, with approval and activation separated.

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

## In scope

- Extend or wrap existing `AgentRuntimeService` proposal support so Agent Admin owns deterministic behavior-change lifecycle commands.
- Cover prompt, skill, reference, manifest, model-ref, and tool-boundary proposal targets where source model permits; otherwise explicitly block unsupported targets with safe `system_message`/decision surfaces.
- Add submit, approve, reject, activate, cancel, and rollback semantics with idempotency, state checks, version checks, and trace records.
- Keep approval and activation separate; no model-backed direct activation.
- Add backend tests for lifecycle success, invalid transitions, idempotent repeat, authority-expansion denial/approval-required, rollback-metadata missing, tenant isolation, and trace emission.

## Out of scope

- Do not implement frontend polish beyond necessary action/surface contract updates.
- Do not implement AgentAdminAgent evidence tool or worker behavior.

## Expected outputs

- updated lifecycle/domain/service code under `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/`, `.../domain/agentfoundation/`, and/or `.../application/security/WorkstreamService.java`
- updated backend tests
- updated `specs/full-core-smb-agent-admin/pending-tasks.md`

## Required checks

```bash
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=AgentRuntimeServiceTest,WorkstreamServiceTest,DurableAgentBehaviorRepositoryStateTest,ManifestBoundaryEntityTest
rg -n "agent_admin\.(draft_behavior_change|submit_behavior_change_for_review|approve_behavior_change|reject_behavior_change|activate_behavior_change|cancel_behavior_change|rollback_behavior_change|compare_versions|simulate_tool_boundary)|BehaviorChangeProposal|approval-required|activate|rollback|no direct mutation|ToolPermissionBoundary" templates/ai-first-saas-starter/backend/src --glob '!**/target/**'
git diff --check
```

## Done criteria

- Deterministic services own lifecycle, validation, authorization, activation, rollback, redaction, idempotency, and traces.
- Agent Admin behavior changes cannot activate through prompt/skill/model output or frontend-only state.
- Unsupported SMB targets fail closed with actionable surfaces/traces rather than fake success.
- Task changes and queue update are committed.

## Commit message

- `full-core-smb: implement agent admin lifecycle`
