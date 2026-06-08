# Task: Implement governed access-review worker runtime

## Objective

Wire the model-backed governed access-review worker path for completed/running task behavior, preserving fail-closed provider/runtime states when configuration is absent.

## Required reads

Use the required reads listed on `TASK-FCSMB-UARW-01-003` in `pending-tasks.md`.

## In scope

- Inspect whether the current Akka SDK dependency supports direct `AutonomousAgent` implementation; otherwise add a minimal explicitly named worker seam that can be migrated to `AutonomousAgent` and does not fake successful model output.
- Use governed prompt/skill/reference/tool boundary semantics for access-review investigation.
- Use scoped read-only evidence only, including `userAdminEvidence.read`, `readSkill`, and `readReferenceDoc` where granted.
- Emit prompt/skill/reference/tool/model/work traces or reuse existing trace sinks where appropriate.
- Update task state through deterministic lifecycle methods only.
- Add provider-missing/provider-failure tests that produce blocked task surfaces/traces, not canned successful recommendations.

## Out of scope

- Do not add side-effecting access mutation tools.
- Do not use request/response `UserAdminAgent` as a substitute for durable task lifecycle.
- Do not change frontend rendering except where strictly required by backend DTO compatibility.

## Expected outputs

- Worker/invoker code under `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/` and/or `.../application/security/`.
- Updated seed/tool-boundary/runtime wiring only if needed for the worker.
- Backend worker tests covering allowed evidence use, provider fail-closed state, traces, and no direct mutation.

## Required checks

```bash
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=AgentRuntimeToolResolverTest,WorkstreamRuntimeAgentTest,UserAdminAccessReviewWorkerTest,WorkstreamServiceTest
rg -n "AutonomousAgent|UserAdminAccessReviewWorker|userAdminEvidence\.read|readSkill|readReferenceDoc|ToolPermissionBoundary|AgentWorkTrace|user_admin\.access_review_task\.v1|provider|blocked_provider_or_runtime|no direct mutation" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/backend/src/test/java templates/ai-first-saas-starter/backend/src/main/resources/agent-behavior-seeds --glob '!**/node_modules/**'
git diff --check
```

## Done criteria

- Normal successful worker behavior is model-backed through governed runtime when provider config is available.
- Missing/misconfigured provider fails closed with actionable blocked task/system-message state and trace links.
- Worker has no direct access mutation path.

## Commit message

- `full-core-smb: wire access review worker runtime`
