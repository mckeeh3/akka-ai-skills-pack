# Agent Admin Prompt-Risk AutonomousAgent Contract

## Status

Implementation handoff for `TASK-AAPR-01-001`. This contract defines the second starter-template durable internal/background `AutonomousAgent` vertical: **Agent Admin Prompt-Risk Review**.

The worker reviews proposed behavior changes for governed managed agents and produces advisory risk findings for human Agent Admin review. It must never approve, activate, rollback, reseed, edit, or directly mutate prompt, skill, reference, model, `AgentDefinition`, or `ToolPermissionBoundary` records.

## Runtime placement

- Owning functional workstream: `agent-agent-admin`.
- Runtime component: internal/background `AgentAdminPromptRiskAutonomousAgent`; it is not a left-rail request/response workstream agent.
- Initiating surfaces: Agent Admin catalog/detail, behavior proposal, prompt/skill/reference/manifest/model/tool-boundary review surfaces.
- Review surfaces: Agent Admin prompt-risk review surface and authorized My Account/rail attention paths.
- Authority: advisory only. Human Agent Admin approval remains required before any behavior artifact activation or authority expansion.

Terminology guardrail: the Akka autonomous `AgentDefinition` returned by `AutonomousAgent.definition()` is distinct from the starter governed managed-agent domain record `domain.agentfoundation.AgentDefinition`. Qualify both in code and docs when they appear together.

## SDK pattern

Reuse the confirmed User Admin Access Review pattern:

- Component extends `akka.javasdk.agent.AutonomousAgent` and is annotated with `@Component(id = "agent-admin-prompt-risk-autonomous-agent", description = "...")`.
- `definition()` returns Akka autonomous `AgentDefinition` from `define()`.
- Accepted work uses `TaskAcceptance.of(AgentAdminPromptRiskTasks.PROMPT_RISK_REVIEW).maxIterationsPerTask(n)`.
- Start through `componentClient.forAutonomousAgent(AgentAdminPromptRiskAutonomousAgent.class, agentInstanceId).runSingleTask(...)` for the first single-task slice.
- Query through `componentClient.forTask(taskId).get(AgentAdminPromptRiskTasks.PROMPT_RISK_REVIEW)`; use `.result(...)` only in blocking/test paths.
- Tests may use `TestModelProvider.AutonomousAgentTools.completeTask(...)` and `failTask(...)` as test infrastructure only.

No SDK blocker is expected for a single-task implementation. If the current dependency version lacks these APIs, block the runtime task with the exact missing class/method and add an SDK/dependency task.

## Governed capabilities and operations

| Operation | Capability | Caller | Required behavior |
|---|---|---|---|
| Start prompt-risk review | `agent_admin.prompt_risk_review.start` | Tenant Agent Admin with manage/review authority | Authorize selected `AuthContext`, enforce idempotency key, create a durable starter prompt-risk task/projection, start the Akka `AutonomousAgent` task when provider/runtime is ready, publish lifecycle events. |
| Read prompt-risk review | `agent_admin.prompt_risk_review.read` | Authorized Agent Admin / My Account attention reader with matching scope | Read browser-safe task state from backend projection; frontend state is never authoritative. |
| Cancel prompt-risk review | `agent_admin.prompt_risk_review.cancel` | Agent Admin manage/review authority | Cancel/terminate pending or running work where supported, update projection, publish v3 events, resolve/upsert attention. |
| Accept risk result | `agent_admin.prompt_risk_review.accept_result` | Agent Admin approval/review authority | Records human acceptance of the advisory review only; does not activate or mutate artifacts. |
| Reject risk result | `agent_admin.prompt_risk_review.reject_result` | Agent Admin approval/review authority | Requires reason; records human rejection/request for revised risk review; does not mutate artifacts. |
| Runtime evidence reads | `agent.agent_admin.use` plus tool-boundary grants | AutonomousAgent runtime only | Resolve active governed managed-agent profile, prompt, manifests, references, model policy, and read-only tools before model invocation; record traces. |

The implementation may add deterministic proposal capabilities later, but this worker contract does not include direct proposal creation, approval, activation, rollback, or tool-boundary expansion.

## Task input schema

Suggested Java records for `application.agentfoundation`:

```java
public final class AgentAdminPromptRiskTasks {
  public static final Task<PromptRiskAutonomousAgentResult> PROMPT_RISK_REVIEW = Task
      .name("AgentAdminPromptRiskReview")
      .description("Review proposed managed-agent behavior changes and produce advisory prompt-risk findings for human Agent Admin review")
      .resultConformsTo(PromptRiskAutonomousAgentResult.class)
      .rules(PromptRiskAutonomousAgentResultRule.class);
}

public record PromptRiskReviewRequest(
    String taskId,
    String tenantId,
    String customerId,
    String targetAgentDefinitionId,
    String proposalId,
    List<BehaviorArtifactDelta> proposedDeltas,
    List<String> evidenceRefs,
    String idempotencyKey,
    String correlationId) {}

public record BehaviorArtifactDelta(
    ArtifactKind artifactKind,
    String artifactId,
    Integer fromVersion,
    Integer toVersion,
    String changeSummary,
    String redactedDiffRef,
    String checksumBefore,
    String checksumAfter) {}

public enum ArtifactKind {
  AGENT_DEFINITION,
  PROMPT_DOCUMENT,
  AGENT_SKILL_MANIFEST,
  SKILL_DOCUMENT,
  AGENT_REFERENCE_MANIFEST,
  REFERENCE_DOCUMENT,
  MODEL_CONFIG_REF,
  MODEL_POLICY,
  TOOL_PERMISSION_BOUNDARY
}
```

Task instructions must include selected `AuthContext` summary, tenant/customer scope, starter task id, proposal id, correlation id, target `AgentDefinition`, compact governed prompt/skill/reference manifest context, model policy summary, tool-boundary summary, read-only Agent Admin evidence, explicit no-direct-mutation guardrail, and the requirement to complete with structured findings or fail with an actionable reason.

## Result schema

```java
public record PromptRiskAutonomousAgentResult(
    String taskId,
    String tenantId,
    String customerId,
    String targetAgentDefinitionId,
    String proposalId,
    String summary,
    RiskLevel overallRisk,
    List<PromptRiskFinding> findings,
    List<PromptRiskRecommendation> recommendations,
    List<String> requiredHumanReviewReasons,
    List<String> evidenceRefs,
    List<String> traceIds,
    String safety) {}

public enum RiskLevel { LOW, MEDIUM, HIGH, CRITICAL, BLOCKED }

public record PromptRiskFinding(
    String findingId,
    RiskLevel riskLevel,
    ArtifactKind artifactKind,
    String artifactId,
    String category,
    String browserSafeDescription,
    List<String> evidenceRefs,
    boolean requiresHumanReview) {}

public record PromptRiskRecommendation(
    String recommendationId,
    String action,
    String rationale,
    boolean blocksActivation,
    List<String> evidenceRefs) {}
```

Minimum result requirements:

- `taskId`, `tenantId`, nullable `customerId`, `targetAgentDefinitionId`, and `proposalId` match the starter projection/request.
- `summary`, findings, and recommendations are browser-safe: no raw prompt bodies, hidden prompt text, full skill/reference content unless separately authorized for display, provider credentials, API keys, JWTs, invitation tokens, or hidden tool payloads.
- Findings explicitly cover applicable prompt, skill, reference, model, and tool-boundary risks when those artifact types are present in the proposal.
- `overallRisk=BLOCKED` or a fail-closed task state is used when required evidence/provider/runtime/configuration is missing.
- `safety` states that the result is advisory and human Agent Admin review is required before activation or behavior changes.
- `traceIds` include prompt assembly, skill/reference loads, model invocation/work trace, task lifecycle trace refs, proposal/evidence reads, and human decision traces where available.

## Risk categories

At minimum, implementation prompts/result rules should recognize these categories:

- prompt instruction hierarchy conflict, hidden policy override, unsafe authority claim, prompt injection susceptibility, missing refusal/denial behavior;
- skill manifest overbreadth, irrelevant skill exposure, missing `whenToUse`, stale/disabled skill version, unsafe procedural guidance;
- reference manifest overexposure, cross-tenant/customer evidence risk, stale/disabled reference, unsupported factual/process claims;
- model policy mismatch, forbidden provider/mode, unsupported fallback, provider readiness/configuration gap;
- `ToolPermissionBoundary` expansion, side-effecting tool exposure, missing idempotency for side effects, missing approval requirement, readSkill/readReferenceDoc over-grant, tool category mismatch;
- behavior-profile authority expansion, lifecycle/status mismatch, disabled/archived target, missing owner/steward/audit requirement;
- trace/audit/redaction gap for prompt, skill, reference, model, tool, data access, or proposal lifecycle.

## Lifecycle mapping

| Akka task status / event | Starter status | v3 event | Attention behavior | Surface state |
|---|---|---|---|---|
| task start accepted before assignment | `QUEUED` | `worker.task.queued`, `workflow.agent_admin.prompt_risk_review.started` | optional progress item | `queued` |
| assigned/started/in progress | `RUNNING` | `worker.task.running` | optional progress attention when review-visible | `running` |
| missing provider/model/runtime/profile/tool grants/evidence before successful model call | `BLOCKED_PROVIDER_OR_RUNTIME` | `worker.task.blocked_provider_or_runtime`, `workflow.agent_admin.prompt_risk_review.blocked_provider_or_runtime`, optional `provider.readiness.fail_closed` | upsert blocked attention | `blocked_provider_or_runtime` |
| task rule rejects unsafe/incomplete result | `RUNNING` or `BLOCKED_PROVIDER_OR_RUNTIME` by reason | `worker.task.result_rejected` if supported, otherwise progress/failure evidence | upsert progress/review attention | `running` with rejected-result detail |
| Akka `COMPLETED` with valid result | `COMPLETED_REVIEW_REQUIRED` | `worker.task.completed_review_required`, `workflow.agent_admin.prompt_risk_review.completed_review_required` | upsert review-required attention | `completed_review_required` |
| Akka `FAILED` | `FAILED` or `BLOCKED_PROVIDER_OR_RUNTIME` | `worker.task.failed` | upsert blocked/failure attention | `failed`/blocked with actionable reason |
| Akka `CANCELLED` or service cancel | `CANCELLED` | `worker.task.cancelled`, `workflow.agent_admin.prompt_risk_review.cancelled` | resolve task-state attention | `cancelled` |
| human accepts advisory result | `ACCEPTED` | `worker.task.accepted`, `workflow.agent_admin.prompt_risk_review.result_accepted` | resolve review attention | `result_accepted` |
| human rejects advisory result | `REJECTED` | `worker.task.rejected_result`, `workflow.agent_admin.prompt_risk_review.result_rejected` | upsert rejected/review attention | `result_rejected` |

## v3 event requirements

Required workflow events:

- `workflow.agent_admin.prompt_risk_review.started`
- `workflow.agent_admin.prompt_risk_review.blocked_provider_or_runtime`
- `workflow.agent_admin.prompt_risk_review.completed_review_required`
- `workflow.agent_admin.prompt_risk_review.cancelled`
- `workflow.agent_admin.prompt_risk_review.result_accepted`
- `workflow.agent_admin.prompt_risk_review.result_rejected`

Required worker-task events:

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
- starter prompt-risk review task/projection id;
- target managed-agent `AgentDefinition` id;
- behavior proposal id and artifact refs for prompt/skill/reference/model/tool-boundary deltas;
- producing capability id;
- `work_trace` / `audit_trace` refs for prompt assembly, skill/reference loads, evidence reads, model call, lifecycle projection, and human decisions.

Idempotency key pattern:

```text
workstream-event:<family>:<eventType>:<tenantId>:<customerId-or-none>:<prompt-risk-task-id>:<semantic-transition>
```

Events are projection triggers only. They must not grant authority, activate behavior artifacts, or mutate managed-agent state.

## Attention mappings

Attention item id:

```text
attention:worker-task:<prompt-risk-task-id>:task-state
```

Mappings:

- blocked/fail-closed/failure/rejected/completed-review-required states upsert an item owned by `agent-agent-admin` and visible through Agent Admin plus authorized My Account/rail summaries.
- cancelled and accepted states resolve the item.
- duplicate event projection is a no-op with trace/audit evidence.
- stale events must not overwrite newer source evidence or a later human decision.
- source refs include workstream event id, idempotency key, prompt-risk task id, proposal id, capability id, target agent id, artifact refs, and trace refs.

## Surface contract

Primary surface id: `surface-agent-admin-prompt-risk-review`.

Surface contract: `agent_admin.prompt_risk_review_task.v1`.

Required data fields:

- `surfaceContract=agent_admin.prompt_risk_review_task.v1`
- `workflowId`, `taskId`, optional `autonomousAgentTaskId`
- `status` from backend projection
- `initiatingCapabilityId=agent_admin.prompt_risk_review.start`
- `scope` with scope type, tenant id, and customer id
- `targetAgentDefinitionId`, proposal id, proposal status, and redacted artifact delta summaries
- `riskSummary`, `overallRisk`, `findings`, `recommendations`, `requiredHumanReviewReasons`
- `blockers` and `providerFailures` for fail-closed states
- `evidenceRefs`, `traceIds`, source refs, and redaction hints
- `resultReviewState` (`pending_worker_result`, `completed_review_required`, `accepted`, `rejected`, `cancelled`, `blocked_provider_or_runtime`, `failed`)
- `noDirectMutation=true`
- `activationBlockedUntilHumanDecision=true`
- `safety` text stating advisory-only result and required human Agent Admin review

Required actions must be capability-backed:

- `action-agentadmin-start-prompt-risk-review`
- `action-agentadmin-read-prompt-risk-review`
- `action-agentadmin-cancel-prompt-risk-review`
- `action-agentadmin-accept-prompt-risk-review-result`
- `action-agentadmin-reject-prompt-risk-review-result`

Future deterministic proposal activation actions remain separate and must re-check current proposal/artifact state; accepting a prompt-risk result must not activate anything by itself.

## Provider/model/runtime fail-closed policy

Normal runtime must not return deterministic, demo, simulated, fixture, or model-less successful prompt-risk findings.

Fail closed when any of these are missing or denied:

- active governed managed-agent `AgentDefinition` for `agent-agent-admin`;
- active Agent Admin prompt, skill manifest, reference manifest, `ToolPermissionBoundary`, model config ref, and model policy;
- target managed-agent profile/artifacts or proposal evidence required by the task;
- read grants for `agentAdminEvidence.read`, `readSkill`, and `readReferenceDoc`;
- provider configuration/secret such as `OPENAI_API_KEY`;
- Akka `AutonomousAgent` setup/start/query path;
- tenant/customer/AuthContext capability checks.

Fail-closed state produces actionable status, browser-safe blocker details, trace refs, v3 blocked/fail-closed events, and attention. It must not silently fall back to canned findings, local heuristics marked as completed review, or direct provider/service calls that bypass the Akka `AutonomousAgent` path.

## Tool boundary

The first slice may expose only read-only evidence/context tools:

- `agentAdminEvidence.read`
- `readSkill(skillId)`
- `readReferenceDoc(referenceId)`

Forbidden tools/effects in this slice:

- approve, activate, rollback, reseed, archive, disable, or edit `AgentDefinition` records;
- edit or activate prompt, skill, reference, skill manifest, reference manifest, model config, model policy, or `ToolPermissionBoundary` records;
- grant roles/capabilities, tenant/customer scope, tool access, or approval bypass;
- create external side effects such as email/webhook/provider changes;
- expose provider credentials, raw prompt bodies, JWTs, hidden tool payloads, cross-tenant data, or support-only details.

## Idempotency and correlation

- Start idempotency is caller supplied and tenant/account scoped.
- The service stores both starter `promptRiskTaskId` and Akka `autonomousAgentTaskId`.
- Replaying start returns the existing starter task and must not create a second Akka task.
- Correlation id links capability call, audit event, Akka task id, v3 event ids, attention item, proposal id, artifact refs, and surface response.

## Required implementation tests

Backend tests should prove:

1. start requires `agent_admin.prompt_risk_review.start`, selected tenant/customer scope, a proposal/target agent, and idempotency key;
2. replay does not create duplicate starter or Akka tasks;
3. missing provider/model/runtime/profile/tool-boundary/evidence config fails closed with blocked state, v3 event, attention, and no risk result summary;
4. `TestModelProvider.AutonomousAgentTools.completeTask(...)` can complete a typed prompt-risk result in TestKit and map to `COMPLETED_REVIEW_REQUIRED` / `completed_review_required`;
5. `failTask(...)` maps to failure/blocked attention with actionable reason;
6. read/cancel/accept/reject enforce auth, tenant isolation, status preconditions, advisory-only semantics, and audit;
7. v3 events include `autonomous_task`, proposal, artifact, capability, and trace refs plus idempotency keys and redaction hints;
8. surfaces are backend-derived, browser-safe, and carry `noDirectMutation=true` / `activationBlockedUntilHumanDecision=true`;
9. no normal runtime path uses deterministic/model-less success; test doubles are named test infrastructure only.

Focused validation commands for later tasks:

```bash
rg "AutonomousAgent|TaskAcceptance|runSingleTask|forTask\(|completeTask|failTask" templates/ai-first-saas-starter/backend/src
rg "prompt_risk_review|workflow.agent_admin.prompt_risk_review|surface-agent-admin-prompt-risk-review|attention:worker-task" templates/ai-first-saas-starter
rg "blocked_provider_or_runtime|fail closed|model-less|fake|deterministic" templates/ai-first-saas-starter/backend/src templates/ai-first-saas-starter/frontend/src
rg "prompt|skill|reference|model|tool-boundary|ToolPermissionBoundary|AgentDefinition" specs/agent-admin-prompt-risk-autonomous-agent/agent-admin-prompt-risk-autonomous-agent-contract.md
```

## Next implementation handoff

`TASK-AAPR-02-001` should add the prompt-risk task/domain projection, concrete AutonomousAgent component, typed task/result/rule classes, runtime adapter/fail-closed adapter, lifecycle mapping, backend service capabilities, and backend tests. If SDK/runtime support is missing, record a precise blocker instead of implementing a deterministic/model-less substitute.
