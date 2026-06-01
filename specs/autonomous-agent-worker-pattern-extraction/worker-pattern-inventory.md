# AutonomousAgent Worker Pattern Inventory

## Source verticals

This inventory compares the two completed starter-template durable internal/background `AutonomousAgent` verticals:

1. **User Admin Access Review** — `UserAdminAccessReviewAutonomousAgent`.
2. **Agent Admin Prompt-Risk Review** — `AgentAdminPromptRiskAutonomousAgent`.

Primary sources:

- `specs/autonomous-agent-runtime-integration/autonomous-agent-runtime-handoff.md`
- `specs/autonomous-agent-runtime-integration/user-admin-access-review-autonomous-agent-contract.md`
- `specs/agent-admin-prompt-risk-autonomous-agent/agent-admin-prompt-risk-autonomous-agent-contract.md`
- `specs/agent-admin-prompt-risk-autonomous-agent/prompt-risk-verification.md`
- starter implementation files found by `rg -n "AutonomousAgent|AccessReviewAutonomousAgent|PromptRisk|worker\.task|autonomous_task" templates/ai-first-saas-starter`

## Common reusable worker shape

A bounded starter worker is a **governed internal/background task worker** owned by a functional workstream. It is not a left-rail request/response workstream agent and it is not an authority bypass for protected mutations.

Common elements:

- concrete Akka `AutonomousAgent` component extending `akka.javasdk.agent.autonomous.AutonomousAgent`;
- `definition()` returning Akka autonomous `AgentDefinition` from `define()`;
- `TaskAcceptance.of(<typed task>).maxIterationsPerTask(3)` for a single bounded task family;
- typed task definition using `Task.name(...).description(...).resultConformsTo(...).rules(...)`;
- instruction builder that embeds scope, governed capability, correlation id, governed runtime context, evidence/tool refs, advisory-only authority limits, and fail-closed guidance;
- typed result record plus result rule validation;
- deterministic lifecycle service owning start/read/cancel/accept/reject capabilities and idempotency;
- runtime adapter using `ComponentClient.forAutonomousAgent(...).runSingleTask(...)` to start and `ComponentClient.forTask(taskId).get(...)` to project task snapshots;
- fail-closed adapter for missing ComponentClient/provider/runtime binding;
- durable starter task/projection storing both starter task id and `autonomousAgentTaskId`;
- v3 workflow/process events plus shared `worker.task.*` events;
- attention item id `attention:worker-task:<starter-task-id>:task-state`;
- structured workstream surface with backend-derived state and human decision actions;
- validation through tests and focused `rg`, with `TestModelProvider.AutonomousAgentTools` allowed only in tests.

## Shared lifecycle pattern

| Phase | Common behavior |
|---|---|
| Start | Authorize selected `AuthContext`, require capability, require idempotency key, create starter task/projection, resolve governed runtime/tool context, start Akka `AutonomousAgent` task when ready, store `autonomousAgentTaskId`, publish workflow and worker-task events. |
| Queued/running projection | Read Akka task snapshot with `forTask(...).get(...)`; frontend state is non-authoritative; update starter projection and progress summary. |
| Provider/runtime blocked | Missing provider/model/runtime/profile/tool/evidence config maps to `BLOCKED_PROVIDER_OR_RUNTIME` with actionable safe summary, trace refs, v3 blocked events, and attention. |
| Completed | Only a valid typed Akka task result maps to completed-review-required state and browser-safe result projection. |
| Failed | Akka failure maps to blocked/failure state with safe failure reason and `autonomous_task` trace/source refs. |
| Cancelled | Authorized cancel records deterministic starter projection and attempts task termination/failure; cancelled/accepted states resolve attention. |
| Accepted/rejected | Human decision records review disposition only; it does not perform protected domain or behavior mutation. |

## Common capabilities

Both verticals use the same capability family shape:

- `*.start` — create/idempotently reuse starter task and start the Akka task through the governed runtime path;
- `*.read` — return backend-projected browser-safe state and project Akka task snapshots;
- `*.cancel` — authorized cancellation with no protected mutation side effects;
- `*.accept_result` — record human acceptance of advisory worker output only;
- `*.reject_result` — record human rejection/review reason only.

Capability families differ by workstream and domain:

| Vertical | Capability prefix | Owning workstream | Primary surface |
|---|---|---|---|
| User Admin Access Review | `user_admin.access_review` | `agent-user-admin` | `surface-user-admin-access-review` |
| Agent Admin Prompt-Risk Review | `agent_admin.prompt_risk_review` | `agent-agent-admin` | `surface-agent-admin-prompt-risk-review` |

## Common governed runtime boundary

Both runtime adapters do this before model-backed work can start:

1. resolve runtime tools for `autonomous_agent_task` and the start capability;
2. require the domain evidence tool plus `readSkill` and `readReferenceDoc` grants;
3. prepare governed managed-agent invocation context with `AgentRuntimeService`;
4. fail closed when the governed runtime decision is not allowed;
5. include model config/prompt trace context in task instructions without exposing secrets;
6. register only read-only evidence/context tools for the worker slice.

The Akka autonomous `AgentDefinition` in `definition()` is distinct from the app-governed managed-agent domain `AgentDefinition`. Future docs and skills must continue to qualify those terms when both are present.

## Shared v3 event and attention pattern

Common worker-task events:

- `worker.task.queued`
- `worker.task.running`
- `worker.task.blocked_provider_or_runtime`
- `worker.task.failed`
- `worker.task.completed_review_required`
- `worker.task.cancelled`
- `worker.task.accepted`
- `worker.task.rejected_result`

Common source refs:

- `autonomous_task` with Akka task id when available;
- starter workflow/task id;
- producing governed capability id;
- trace/work/audit refs for prompt assembly, skill/reference loads, model/work task lifecycle, evidence reads, and human decisions.

Common attention behavior:

- blocked, failed, rejected, and completed-review-required states upsert review/blocked attention;
- cancelled and accepted states resolve task-state attention;
- event projection is idempotent and must not grant authority or mutate protected domain state;
- source refs include event id, idempotency key, task id, capability id, and trace refs.

## Shared structured surface pattern

Both surfaces are backend-projected, browser-safe review surfaces with:

- stable surface id and task-specific surface contract;
- starter task id and optional `autonomousAgentTaskId`;
- status/progress/result review state from backend projection;
- initiating capability id;
- tenant/customer scope;
- blockers/provider failures for fail-closed states;
- evidence refs and trace ids;
- recommendations/findings only after a real typed Akka task result;
- `noDirectMutation=true`;
- capability-backed read/cancel/accept/reject actions;
- frontend and rail attention reload from backend state rather than trusting client state.

Prompt-risk adds `activationBlockedUntilHumanDecision=true` because behavior artifact activation is explicitly out of scope for the worker result.

## Runtime completion guardrails extracted

Future worker tasks must not mark a normal runtime path complete with any of these substitutes:

- deterministic, canned, demo, simulated, fixture, fake, or model-less successful worker findings;
- direct provider/service calls bypassing Akka `AutonomousAgent` task lifecycle;
- frontend-only state marking a worker complete;
- test-only `completeTask` / `failTask` helpers outside tests;
- human accept/reject actions that directly perform separate protected mutations.

Missing provider, model, governed profile, tool grants, evidence, ComponentClient binding, or authorization must produce fail-closed blocked state with actionable browser-safe status, trace refs, v3 events, and attention.

## Differences to preserve in future pattern guidance

| Concern | Access Review | Prompt-Risk Review | Pattern implication |
|---|---|---|---|
| Domain evidence | tenant/customer access, roles, memberships, invitations, account/support-access context | managed-agent behavior proposal, artifact deltas, prompt/skill/reference/model/tool-boundary evidence | each worker needs a task-specific evidence model and read-only evidence tool. |
| Result vocabulary | findings and access recommendations | risk findings, risk level, risk recommendations, required review reasons | keep result schemas domain-specific, not generic markdown. |
| Completion status | `COMPLETED` with review required by surface semantics | `COMPLETED_REVIEW_REQUIRED` explicit domain status | allow domain projections to name completion states differently while preserving worker-task events. |
| Forbidden effects | access, membership, role, invitation, tenant/customer, provider, audit-policy mutation | approve/activate/rollback/reseed/edit managed-agent artifacts or expand authority | worker guidance must enumerate forbidden side effects per capability contract. |
| Source refs | access-review workflow/task and capability refs | target managed-agent definition, behavior proposal, artifact refs, capability refs | source refs must include domain evidence needed for review/audit. |
| Surface safety | no direct access mutation; human User Admin review required | advisory-only; activation blocked until human Agent Admin decision | surface flags and copy are task-specific acceptance requirements. |
| Persistence | existing access-review repository/task record | added prompt-risk task repository/entity and domain projection | pattern can reuse existing durable task state or add a dedicated projection. |

## Implementation file map

Core component/task files:

- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/UserAdminAccessReviewAutonomousAgent.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/UserAdminAccessReviewTasks.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AccessReviewAutonomousAgentResult.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AccessReviewAutonomousAgentResultRule.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentAdminPromptRiskAutonomousAgent.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentAdminPromptRiskTasks.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/PromptRiskAutonomousAgentResult.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/PromptRiskAutonomousAgentResultRule.java`

Runtime/service/projection files:

- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/ComponentClientAccessReviewAutonomousAgentRuntime.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/AccessReviewAutonomousAgentRuntime.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/FailClosedAccessReviewAutonomousAgentRuntime.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/UserAdminAccessReviewService.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/security/AccessReviewTask.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/ComponentClientPromptRiskAutonomousAgentRuntime.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/PromptRiskAutonomousAgentRuntime.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/FailClosedPromptRiskAutonomousAgentRuntime.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentAdminPromptRiskReviewService.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/agentfoundation/PromptRiskReviewTask.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/DurablePromptRiskReviewTaskRepositoryEntity.java`

Events/attention/surface files:

- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamEventPublisher.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamEventAttentionConsumer.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/AttentionProducerService.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamService.java`
- frontend fixtures and tests under `templates/ai-first-saas-starter/frontend/src/**` for the two structured surfaces.

Key tests:

- `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/UserAdminAccessReviewAutonomousAgentTest.java`
- `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/UserAdminAccessReviewServiceTest.java`
- `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentAdminPromptRiskAutonomousAgentTest.java`
- `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentAdminPromptRiskReviewServiceTest.java`
- `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamEventBackboneServiceTest.java`
- `templates/ai-first-saas-starter/frontend/src/workstream-agent-admin-vertical.contract.test.mjs`

## Pattern extraction for next task

The reusable doc should define a standard worker contract with these required sections:

1. when to use Akka `AutonomousAgent` rather than request-based `Agent` or `Workflow`;
2. owning functional workstream and advisory authority boundary;
3. governed capability family and task-specific operation contracts;
4. typed task, instructions, result schema, and result rules;
5. runtime adapter and fail-closed adapter requirements;
6. durable starter task/projection and idempotency requirements;
7. v3 workflow and `worker.task.*` events;
8. attention id/projection rules;
9. structured surface contract and capability-backed actions;
10. provider/model/no-fake-success guardrails;
11. tests and validation scans.
