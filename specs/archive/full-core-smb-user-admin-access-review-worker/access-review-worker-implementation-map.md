# Access-Review Worker Implementation Map

## Discovery commands used

```bash
find templates/ai-first-saas-starter -path '*/node_modules' -prune -o -type f -print | sort | rg -n "(AccessReview|access_review|access-review|UserAdminAgent|userAdminEvidence|AutonomousAgent|AgentWorkTrace|ToolPermissionBoundary|WorkstreamRuntimeAgent|WorkstreamService|agentfoundation|agent-behavior-seeds|surface|Surface|workstream|Workstream|UserAdmin|Invitation|Member|Role|Trace|test|frontend)"
rg -n "access_review|access-review|access review|AutonomousAgent|runSingleTask|assignTasks|TaskTemplate|TaskAcceptance|TaskRule|userAdminEvidence|user_admin\.access_review|access_review_task|ToolPermissionBoundary|AgentWorkTrace|system_message|provider|no direct mutation|USERADMIN_START_ACCESS_REVIEW|ACCESS_REVIEW|blocked_provider_or_runtime" templates/ai-first-saas-starter --glob '!**/node_modules/**'
rg -n "access_review_task|access review|access-review|Start access review|USERADMIN_START_ACCESS_REVIEW_TASK|blocked_provider_or_runtime|workflow-status|SystemMessageSurface|WorkflowStatusSurface|action-useradmin-start-access-review|AutonomousAgent|TaskTemplate|runSingleTask" templates/ai-first-saas-starter/frontend/src templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/backend/src/test/java --glob '!**/node_modules/**'
```

## Current source state

The starter has strong deterministic User Admin and governed request/response agent foundations, but no real durable access-review worker yet.

Implemented foundations:

- `WorkstreamService` exposes User Admin dashboard/list/detail/role-preview surfaces and handles invitation, member-status, role-change, UserAdminAgent message, and current blocked access-review actions.
- `UserAdminService` owns scoped member listing, role preview/change, member status transitions, self-disable and last-admin guardrails, tenant/customer scope checks, audit events, idempotency/no-op results, and deterministic membership/role mutation paths.
- `UserAdminEvidenceTools` exposes a read-only `userAdminEvidence.read` facade for `UserAdminAgent`, returning scoped member, invitation, and audit evidence with explicit no-direct-mutation text.
- Agent foundation files implement governed prompt/skill/reference/tool/model traces for request/response `WorkstreamRuntimeAgent`.
- Seed documents include User Admin access-review skill/reference content and User Admin tool-boundary grants for `readSkill`, `readReferenceDoc`, and `userAdminEvidence.read`.
- Frontend workstream fixtures and renderers already support `workflow-status`, `system_message`, blocked provider/runtime states, trace links, and a fixture-only blocked access-review surface.

Gaps for this slice:

- There is no `user_admin.access_review_task.v1` backend runtime surface; the backend currently returns `surface-user-admin-access-review` as a blocked `workflow-status` surface only.
- Capability ids are currently Java constants such as `USERADMIN_START_ACCESS_REVIEW_TASK`, not the contract ids `user_admin.access_review.start/read/cancel/accept_result/reject_result`.
- There is no durable access-review task state/repository/service, no task id, no start/read/cancel/result-decision lifecycle, and no idempotent task record creation.
- There is no internal worker or `AutonomousAgent` source in the starter; searches for `AutonomousAgent`, `TaskTemplate`, `runSingleTask`, and `assignTasks` find only contract/test text. The worker task must therefore add a minimal governed worker seam instead of assuming existing task runtime scaffolding.
- Provider-blocked access-review behavior exists only as a blocked action result/surface; future worker implementation must preserve fail-closed semantics when provider/model/runtime config is absent and must not produce model-less successful recommendations.
- Frontend fixtures include disabled access-review start/read/cancel actions, but runtime API alignment for completed/progress/cancel/accepted/rejected states is not implemented.

## Backend implementation boundary

Primary existing files:

- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamService.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/UserAdminService.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/UserDirectoryView.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/InvitationView.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamLogRepository.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/UserAdminEvidenceTools.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeService.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/WorkstreamRuntimeAgent.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/ToolRegistry.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeToolResolver.java`

New or likely files:

- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/security/AccessReviewTask.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/AccessReviewTaskRepository.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/SubstituteAccessReviewTaskRepository.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/UserAdminAccessReviewService.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/UserAdminAccessReviewWorker.java` or `UserAdminAccessReviewWorkerInvoker.java` depending on the runtime seam selected by the implementation task.

Primary backend tests:

- `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamServiceTest.java`
- `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/InvitationAndUserAdminServiceTest.java`
- `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeToolResolverTest.java`
- `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/WorkstreamRuntimeAgentTest.java`
- new `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/UserAdminAccessReviewServiceTest.java`
- new `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/UserAdminAccessReviewWorkerTest.java` if the worker seam lands in `agentfoundation`.

## Deterministic lifecycle responsibilities

Deterministic User Admin code must own:

- `user_admin.access_review.start`: authorize selected AuthContext, validate tenant/customer scope and requested scope, require idempotency key, create or return a durable task record, check provider/runtime readiness before model-backed work, emit start/queued/provider-blocked traces, and return `user_admin.access_review_task.v1`.
- `user_admin.access_review.read`: authorize scope, shape task progress/result/blocker/evidence references, redact browser-visible data, and return not-found-or-forbidden without enumeration.
- `user_admin.access_review.cancel`: authorize task management, transition only cancellable tasks, preserve no-op cancellation for already terminal tasks, and trace decision.
- `user_admin.access_review.accept_result`: authorize result decision, require completed result, record human acceptance trace, and expose deterministic follow-up actions without mutating memberships, invitations, roles, or authorization state.
- `user_admin.access_review.reject_result`: authorize result decision, validate rejection reason, record human rejection trace, and leave access state unchanged.
- tenant/customer isolation, disabled actor denial, last-admin/self-disable guardrail preservation, audit/work trace normalization, idempotent replay behavior, and browser-safe DTO shaping.

The deterministic lifecycle may update access-review task records and review decisions. It must not delegate authorization, tenant filtering, idempotency, trace normalization, or membership/role/invitation mutations to model output.

## Governed worker/model responsibilities

The governed worker path should be added only after deterministic task records and surfaces exist.

Worker/model scope:

- perform model-backed access-review investigation/summarization against scoped evidence;
- use only approved read-only evidence/tool/skill/reference paths, including existing `userAdminEvidence.read`, `readSkill`, and `readReferenceDoc` grants;
- load User Admin access-review skill/reference content through governed loader tools, with prompt/skill/reference/tool/model traces;
- write progress/result/blocker facts back through deterministic service methods or a narrow worker result adapter;
- fail closed with provider/runtime blocked task state and actionable `system_message`/`workflow-status` evidence when provider or worker runtime config is missing;
- return recommendations, risk/confidence, evidence refs, and suggested deterministic follow-up actions only.

Forbidden worker behavior:

- no direct disable/reactivate, resend/revoke, role/capability change, authorization, policy, outbox, provider config, or audit mutation;
- no request/response `UserAdminAgent` turn standing in for durable worker lifecycle;
- no model-less canned successful access-review result in normal runtime.

Because no `AutonomousAgent` source exists yet in the starter, the worker implementation task must either add a minimal runtime seam compatible with later Akka `AutonomousAgent` migration or wire the actual SDK component if the implementation session confirms available dependencies/API. In either case, user-visible normal completion must remain model-backed or provider-blocked.

## Frontend implementation boundary

Primary files:

- `templates/ai-first-saas-starter/frontend/src/workstream/types/surfaces.ts`
- `templates/ai-first-saas-starter/frontend/src/workstream/fixtures/surfaces.ts`
- `templates/ai-first-saas-starter/frontend/src/workstream/surfaces/WorkflowStatusSurface.tsx`
- `templates/ai-first-saas-starter/frontend/src/workstream/surfaces/SystemMessageSurface.tsx`
- `templates/ai-first-saas-starter/frontend/src/workstream/surfaces/SurfaceRenderer.tsx`
- `templates/ai-first-saas-starter/frontend/src/workstream/actions/capabilityActionState.ts`
- `templates/ai-first-saas-starter/frontend/src/workstream/actions/CapabilityActionButton.tsx`
- `templates/ai-first-saas-starter/frontend/src/api/HttpWorkstreamApiClient.ts`
- `templates/ai-first-saas-starter/frontend/src/workstream-user-admin-vertical.contract.test.mjs`
- `templates/ai-first-saas-starter/frontend/src/workstream-actions.contract.test.mjs`
- `templates/ai-first-saas-starter/frontend/src/workstream-surfaces.contract.test.mjs`

Frontend scope:

- add/align `user_admin.access_review_task.v1` runtime payload expectations for queued/running/provider-blocked/cancelled/completed/accepted/rejected states;
- render task purpose, status, progress, blockers, evidence refs, recommendations, risk/confidence, result review state, provider failures, and trace links;
- expose start/read/cancel/accept/reject actions with contract capability ids and backend-authoritative denials;
- keep blocked/provider missing states visually distinct from successful model-backed results;
- preserve existing `workflow-status`/`system_message` renderers unless a focused access-review task component is required by implementation.

Synchronize root `frontend/` only if the task changes mirrored source by repository convention; the discovered primary runtime target is `templates/ai-first-saas-starter/frontend/`.

## Validation boundary

Use targeted validation after each source-edit task:

```bash
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=UserAdminAccessReviewServiceTest,WorkstreamServiceTest,InvitationAndUserAdminServiceTest
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=AgentRuntimeToolResolverTest,WorkstreamRuntimeAgentTest,UserAdminAccessReviewWorkerTest
cd templates/ai-first-saas-starter/frontend && npm test -- --runTestsByPath src/workstream-user-admin-vertical.contract.test.mjs src/workstream-actions.contract.test.mjs src/workstream-surfaces.contract.test.mjs src/api.contract.test.mjs
rg -n "user_admin\.access_review\.(start|read|cancel|accept_result|reject_result)|user_admin\.access_review_task\.v1|AccessReviewTask|UserAdminAccessReview|AutonomousAgent|userAdminEvidence\.read|ToolPermissionBoundary|AgentWorkTrace|system_message|provider|no direct mutation|blocked_provider_or_runtime" templates/ai-first-saas-starter --glob '!**/node_modules/**'
git diff --check
```

Before the mini-project is complete, run broad validation or record a concrete blocker:

```bash
tools/validate-ai-first-saas-starter-fullstack.sh
```

## Appended implementation tasks

- `TASK-FCSMB-UARW-01-002`: implement deterministic backend access-review task lifecycle and surfaces.
- `TASK-FCSMB-UARW-01-003`: implement governed access-review worker/runtime integration and provider-blocked behavior.
- `TASK-FCSMB-UARW-01-004`: implement frontend access-review task surface/action rendering and contract tests.
- `TASK-FCSMB-UARW-01-005`: run integrated access-review validation and close or append blockers.
