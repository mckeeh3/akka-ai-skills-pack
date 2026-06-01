# User Admin Access Review AutonomousAgent Contract

## Status

Implementation handoff for `TASK-AAI-01-001`. This contract is for the first starter-template durable internal/background `AutonomousAgent` vertical: **User Admin Access Review**.

The current starter has a durable `AccessReviewTask` record, governed User Admin capabilities, fail-closed blocked state, v3 workstream events, attention projection, and structured workflow surfaces. The next implementation task should replace the current direct worker/provider seam with a concrete Akka `AutonomousAgent` task lifecycle where the local SDK supports it.

## Official SDK pattern confirmed

Use the Akka Java SDK Autonomous Agent APIs documented under `akka-context/sdk/autonomous-agents/`:

- Component: class extends `akka.javasdk.agent.AutonomousAgent` and is annotated with `@Component(id = "user-admin-access-review-autonomous-agent", description = "...")`.
- Definition: implement `definition()` and return Akka autonomous `AgentDefinition` from `define()`.
- Accepted work: declare `TaskAcceptance.of(UserAdminAccessReviewTasks.ACCESS_REVIEW).maxIterationsPerTask(n)`.
- Task schema: declare a typed `Task<AccessReviewAutonomousAgentResult>` with `.resultConformsTo(...)`; add `TaskRule` if result invariants need model retry before completion.
- Start: use `componentClient.forAutonomousAgent(UserAdminAccessReviewAutonomousAgent.class, agentInstanceId).runSingleTask(UserAdminAccessReviewTasks.ACCESS_REVIEW.instructions(...))` for the first slice so the agent auto-stops when the queue drains.
- Query: use `componentClient.forTask(taskId).get(UserAdminAccessReviewTasks.ACCESS_REVIEW)` for lifecycle snapshots and `.result(...)` only in blocking/test paths.
- Notifications: `forAutonomousAgent(...).notificationStream()` exposes non-authoritative progress; `forTask(taskId).notificationStream()` exposes terminal task notifications. Durable source of truth remains task snapshot plus starter `AccessReviewTask` projection.
- Tests: register `TestModelProvider` with `.withModelProvider(UserAdminAccessReviewAutonomousAgent.class, model)` and use `TestModelProvider.AutonomousAgentTools.completeTask(...)` / `failTask(...)` as test infrastructure only.

No SDK blocker was found for a single-task `AutonomousAgent` implementation. The only design gap is starter integration work: mapping Akka task ids/statuses/notifications into existing `AccessReviewTask`, v3 events, attention, and surfaces without making notifications the source of truth.

Terminology guardrail: the Akka autonomous `AgentDefinition` returned by `definition()` is not the starter governed managed-agent domain record `domain.agentfoundation.AgentDefinition`. When both appear in code/docs, qualify them.

## Existing starter inputs to preserve

- Domain task state: `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/security/AccessReviewTask.java`.
- Capability owner: `UserAdminAccessReviewService` with capability ids:
  - `user_admin.access_review.start`
  - `user_admin.access_review.read`
  - `user_admin.access_review.cancel`
  - `user_admin.access_review.accept_result`
  - `user_admin.access_review.reject_result`
- Current worker seam: `AccessReviewWorker` / `application.agentfoundation.UserAdminAccessReviewWorker`.
- Governed runtime foundation: `AgentRuntimeService`, `AgentRuntimeToolResolver`, `ToolPermissionBoundary`, `readSkill`, `readReferenceDoc`, `AgentRuntimeTrace`.
- Event path: `WorkstreamEventPublisher.publishAccessReviewLifecycle(...)` and `WorkstreamEventAttentionConsumer.project(...)`.
- Surface id: `surface-user-admin-access-review` with contract `user_admin.access_review_task.v1`.

## Runtime placement

- Functional workstream owner: `agent-user-admin`.
- Runtime component: internal/background `UserAdminAccessReviewAutonomousAgent`; it is not a left-rail request/response workstream agent.
- Initiating surface: User Admin dashboard / access-review surface.
- Review surfaces: User Admin access-review surface and My Account/rail attention paths via backend-derived attention items.
- Authority: worker output is advisory and cannot directly mutate accounts, invitations, memberships, roles, capabilities, tenant/customer records, provider config, audit policy, or tool boundaries.

## Governed capabilities and operations

| Operation | Capability | Caller | Required behavior |
|---|---|---|---|
| Start task | `user_admin.access_review.start` | Tenant/customer User Admin with manage authority | Authorize selected `AuthContext`, enforce idempotency key, create starter `AccessReviewTask`, start Akka `AutonomousAgent` task when provider/runtime is ready, publish lifecycle event. |
| Read task | `user_admin.access_review.read` | Authorized User Admin / My Account attention reader with matching scope | Read browser-safe task state from backend projection; never trust frontend task state. |
| Cancel task | `user_admin.access_review.cancel` | Authorized User Admin manage authority | Cancel/terminate pending or running work where supported, update starter task, publish v3 event, resolve/upsert attention. |
| Accept result | `user_admin.access_review.accept_result` | Authorized User Admin with approval authority | Allowed only after completed-review-required state; records human acceptance only; no direct access mutation. |
| Reject result | `user_admin.access_review.reject_result` | Authorized User Admin with approval authority | Requires reason; reopens attention/review state as rejected; no direct access mutation. |
| Runtime tool reads | `agent.user_admin.use` plus tool-boundary grants | AutonomousAgent runtime only | Resolve governed tools before exposing to model; record prompt/skill/reference/work traces. |

## Task schema

Suggested Java records for `application.agentfoundation`:

```java
public final class UserAdminAccessReviewTasks {
  public static final Task<AccessReviewAutonomousAgentResult> ACCESS_REVIEW = Task
      .name("UserAdminAccessReview")
      .description("Investigate tenant/customer user access and produce advisory recommendations for human review")
      .resultConformsTo(AccessReviewAutonomousAgentResult.class)
      .rules(AccessReviewAutonomousAgentResultRule.class);
}

public record AccessReviewAutonomousAgentResult(
    String taskId,
    String tenantId,
    String customerId,
    String summary,
    List<AccessReviewFinding> findings,
    List<AccessReviewRecommendation> recommendations,
    List<String> evidenceRefs,
    List<String> traceIds,
    String safety)
{}
```

Minimum result requirements:

- `taskId`, `tenantId`, and nullable `customerId` match the starter `AccessReviewTask` scope.
- `summary` is browser-safe and contains no raw prompt, JWT, invitation token, provider secret, API key, hidden tool payload, or secret config.
- `findings` and `recommendations` are advisory and include evidence refs; no result field may request direct mutation as completed work.
- `safety` must state that User Admin human review is required before any follow-up access changes.
- `traceIds` must include prompt assembly, skill/reference loads, model invocation/work trace, and task lifecycle trace refs where available.

Task instructions must include:

- selected `AuthContext` summary, tenant/customer scope, task id, and correlation id;
- compact governed prompt/skill/reference manifest context from `AgentRuntimeService`;
- scoped evidence from read-only User Admin evidence tooling;
- explicit no-direct-mutation guardrail;
- requirement to complete with the structured result or fail with an actionable reason.

## Lifecycle mapping

Akka task statuses are mapped into the starter `AccessReviewTask.Status` projection:

| Akka task status / event | Starter status | v3 event | Attention behavior | Surface state |
|---|---|---|---|---|
| task start accepted before assignment | `QUEUED` | `worker.task.queued` and/or `workflow.access_review.started` | upsert informational/progress item only if surfaced | `queued` |
| assigned/started/in progress | `RUNNING` | `worker.task.running` | upsert progress attention only when review-visible | `running` with progress snapshots |
| provider/model/runtime missing config before successful model call | `BLOCKED_PROVIDER_OR_RUNTIME` | `worker.task.blocked_provider_or_runtime`, `workflow.access_review.blocked_provider_or_runtime`, optional `provider.readiness.fail_closed` | upsert blocked attention | `blocked_provider_or_runtime` |
| task rule rejected result | `RUNNING` or `BLOCKED_PROVIDER_OR_RUNTIME` depending reason | `worker.task.result_rejected` if added, otherwise progress event | upsert progress/review attention | `running` with rejected-result detail |
| Akka `COMPLETED` with valid result | `COMPLETED` | `worker.task.completed_review_required`, `workflow.access_review.completed_review_required` | upsert review-required attention | `completed_review_required` |
| Akka `FAILED` | `BLOCKED_PROVIDER_OR_RUNTIME` or future `FAILED` extension | `worker.task.failed` | upsert blocked/failure attention | `failed`/blocked with actionable reason |
| Akka `CANCELLED` or service cancel | `CANCELLED` | `worker.task.cancelled`, `workflow.access_review.cancelled` | resolve task-state attention | `cancelled` |
| human accepts result | `ACCEPTED` | `worker.task.accepted`, `workflow.access_review.result_accepted` | resolve review attention | `result_accepted` |
| human rejects result | `REJECTED` | `worker.task.rejected_result`, `workflow.access_review.result_rejected` | upsert rejected/review attention | `result_rejected` |

Implementation note: starter `AccessReviewTask.Status` currently lacks `FAILED`. For the next task, either map Akka `FAILED` to `BLOCKED_PROVIDER_OR_RUNTIME` with `blockerCode=autonomous_agent_task_failed`, or add a `FAILED` status with corresponding tests and surface copy.

## v3 event requirements

Access-review `AutonomousAgent` integration must publish browser-safe `WorkstreamEventEnvelope` records. Required event coverage for the first implementation slice:

- `workflow.access_review.started`
- `workflow.access_review.blocked_provider_or_runtime`
- `workflow.access_review.completed_review_required`
- `workflow.access_review.cancelled`
- `workflow.access_review.result_accepted`
- `workflow.access_review.result_rejected`
- `worker.task.queued`
- `worker.task.running`
- `worker.task.blocked_provider_or_runtime`
- `worker.task.failed`
- `worker.task.completed_review_required`
- `worker.task.cancelled`
- `worker.task.accepted`
- `worker.task.rejected_result`

Required source refs:

- `autonomous_task` with Akka task id;
- `workflow` or starter task ref with `AccessReviewTask.taskId`;
- `capability` for the producing governed capability;
- `work_trace` / `audit_trace` refs for prompt assembly, tool loads, model call, lifecycle projection, and human decisions.

Idempotency key pattern:

```text
workstream-event:<family>:<eventType>:<tenantId>:<customerId-or-none>:<taskId>:<semantic-transition>
```

Events are projection triggers only. They must not grant authority or mutate access state.

## Attention mappings

Attention item id remains:

```text
attention:worker-task:<starter-task-id>:task-state
```

Mappings:

- blocked/fail-closed/failure/rejected/completed-review-required states upsert an item owned by `agent-user-admin` and visible through User Admin plus authorized My Account/rail summaries.
- cancelled and accepted states resolve the item.
- duplicate event projection is a no-op with audit evidence.
- stale events must not overwrite newer source evidence.
- source refs must include the workstream event id, idempotency key, task id, capability id, and trace refs.

## Surface contract

The existing `surface-user-admin-access-review` / `workflow-status` surface remains the first surface.

Required data fields:

- `surfaceContract=user_admin.access_review_task.v1`
- `workflowId`, `taskId`, and optional `autonomousAgentTaskId`
- `status` from backend projection
- `initiatingCapabilityId=user_admin.access_review.start`
- `scope` with scope type, tenant id, customer id
- `progress` snapshots derived from backend task state/notifications/projections
- `resultSummary` only after real Akka task completion
- `blockers` and `providerFailures` for fail-closed states
- `evidenceRefs`, `recommendations`, and `traceIds`
- `resultReviewState` (`pending_worker_result`, `completed_review_required`, `accepted`, `rejected`, `cancelled`, `blocked_provider_or_runtime`)
- `noDirectMutation=true`
- `safety` text stating no direct mutation and human review required

Required actions remain capability-backed:

- `action-useradmin-start-access-review`
- `action-useradmin-read-access-review`
- `action-useradmin-cancel-access-review`
- `action-useradmin-accept-access-review-result`
- `action-useradmin-reject-access-review-result`

Frontend state is never authoritative; User Admin, My Account, rail, and update delivery must read backend projections.

## Provider/model fail-closed policy

Normal runtime must not return deterministic, demo, simulated, fixture, or model-less successful access-review recommendations.

Fail closed when any of these are missing or denied:

- active governed managed-agent `AgentDefinition` for `agent-user-admin`;
- active prompt, skill manifest, reference manifest, `ToolPermissionBoundary`, model config ref, and model policy;
- read grants for `userAdminEvidence.read`, `readSkill`, and `readReferenceDoc`;
- provider configuration/secret such as `OPENAI_API_KEY`;
- Akka `AutonomousAgent` setup/start/query path;
- tenant/customer/auth capability checks.

Fail-closed state must produce actionable status, trace refs, v3 blocked/fail-closed events, and attention. It must not silently fall back to canned success.

## Tool boundary

The AutonomousAgent may receive only read-only evidence/context tools in the first slice:

- `userAdminEvidence.read`
- `readSkill(skillId)`
- `readReferenceDoc(referenceId)`

Forbidden tools/effects in this slice:

- create/revoke invitation;
- add/remove/update membership;
- role/capability mutation;
- account disable/reactivate;
- support-access grant mutation;
- agent behavior/tool-boundary/model-provider mutation;
- direct audit-policy or authorization-state mutation.

## Idempotency and correlation

- Start idempotency remains caller supplied and tenant/account scoped.
- The service stores both starter `taskId` and Akka `autonomousAgentTaskId` when implementation adds the SDK task.
- Replaying start returns the existing starter task and must not create a second Akka task.
- Correlation id links capability call, audit event, Akka task id, v3 event ids, attention item, and surface response.

## Tests required by implementation task

Backend tests should prove:

1. start requires `user_admin.access_review.start`, selected tenant/customer scope, and idempotency key;
2. replay does not create duplicate starter or Akka tasks;
3. missing provider/model/runtime config fails closed with blocked state, v3 event, attention, and no result summary;
4. `TestModelProvider.AutonomousAgentTools.completeTask(...)` can complete a typed result in TestKit and map to `COMPLETED` / `completed_review_required`;
5. `failTask(...)` maps to failure/blocked attention with actionable reason;
6. read/cancel/accept/reject enforce auth, tenant isolation, status preconditions, and audit;
7. v3 events include `autonomous_task` source refs, capability refs, trace refs, redaction hints, and idempotency keys;
8. no normal runtime path uses deterministic/model-less success; test doubles are named test infrastructure only.

Focused `rg` checks for later tasks should include:

```bash
rg "AutonomousAgent|TaskAcceptance|runSingleTask|forTask\(|completeTask|failTask" templates/ai-first-saas-starter/backend/src
rg "blocked_provider_or_runtime|fail closed|no deterministic|model-less|fake" templates/ai-first-saas-starter/backend/src templates/ai-first-saas-starter/frontend/src
rg "workflow.access_review|worker.task|autonomous_task|attention:worker-task|surface-user-admin-access-review" templates/ai-first-saas-starter
```

## Next implementation handoff

`TASK-AAI-02-001` can proceed without an SDK blocker. It should add the concrete AutonomousAgent component, task definitions, starter task-to-Akka-task id storage/projection as needed, lifecycle mapping, and backend tests. If implementation discovers that the current project dependency version lacks the documented APIs, update the queue by blocking `TASK-AAI-02-001` with the exact missing class/method and add a dependency/SDK-upgrade task.
