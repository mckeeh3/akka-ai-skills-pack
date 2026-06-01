# My Account Personal Attention Digest AutonomousAgent Contract

## Status

Implementation handoff for `TASK-MAPAD-01-001`. This contract defines a bounded starter-template durable internal/background Akka `AutonomousAgent` vertical: **My Account Personal Attention Digest**.

The worker summarizes the signed-in account's authorized, backend-derived cross-workstream attention into a personal digest for My Account. It reads only attention evidence already visible to the selected `AuthContext`, preserves tenant/customer/workstream scope, emits v3 workstream events, creates digest task-state attention when appropriate, and renders My Account digest progress/result surfaces. It is not a notification platform and must not send email, push, or scheduled enterprise digests in this slice.

## Runtime placement

- Owning functional workstream: `agent-my-account`.
- Runtime component: internal/background `MyAccountPersonalAttentionDigestAutonomousAgent`; it is not the default My Account request/response workstream agent.
- Initiating surfaces: My Account dashboard and personal attention panel, with optional open-attention deep links from authorized workstream surfaces.
- Review/result surfaces: My Account personal attention digest progress/result surfaces and authorized rail/My Account attention paths.
- Authority: advisory and read-only. The worker may summarize and recommend where to look next; it must not mutate source attention items, acknowledge/dismiss/resolve attention, open hidden workstreams, or perform protected workstream actions.

Terminology guardrail: the Akka autonomous `AgentDefinition` returned by `AutonomousAgent.definition()` is distinct from the starter governed managed-agent domain `AgentDefinition`. Qualify both when they appear together.

## SDK pattern

Reuse the confirmed worker pattern from access review, prompt-risk review, audit summary, and governance impact analysis:

- Component extends `akka.javasdk.agent.autonomous.AutonomousAgent` and is annotated with `@Component(id = "my-account-personal-attention-digest-autonomous-agent", description = "...")`.
- `definition()` returns the Akka autonomous `AgentDefinition` from `define()`.
- Accepted work uses `TaskAcceptance.of(MyAccountPersonalAttentionDigestTasks.PERSONAL_ATTENTION_DIGEST).maxIterationsPerTask(3)` unless a smaller bounded limit is justified.
- Start through `componentClient.forAutonomousAgent(MyAccountPersonalAttentionDigestAutonomousAgent.class, agentInstanceId).runSingleTask(...)`.
- Query through `componentClient.forTask(taskId).get(MyAccountPersonalAttentionDigestTasks.PERSONAL_ATTENTION_DIGEST)`; use `.result(...)` only for blocking/test paths.
- Tests may use `TestModelProvider.AutonomousAgentTools.completeTask(...)` and `failTask(...)` as test infrastructure only.

No SDK blocker is expected for a single-task implementation. If the current dependency version lacks these APIs, block the runtime task with the exact missing class/method and add an SDK/dependency task.

## Governed capabilities and operations

| Operation | Capability | Caller | Required behavior |
|---|---|---|---|
| Start personal attention digest | `my_account.personal_attention_digest.start` | Signed-in user with selected context and `my_account.list_personal_attention` | Authorize selected `AuthContext`, enforce idempotency key, collect only authorized personal attention evidence, create/reuse durable digest task projection, resolve governed runtime/tool context, start the Akka `AutonomousAgent` task when provider/runtime is ready, publish lifecycle events. |
| Read personal attention digest | `my_account.personal_attention_digest.read` | Same signed-in user or authorized My Account reader in matching scope | Return backend-projected browser-safe task state; frontend state is never authoritative. |
| Cancel personal attention digest | `my_account.personal_attention_digest.cancel` | Same signed-in user with digest start/read authority | Cancel or terminate pending/running work where supported, update projection, publish v3 events, resolve/upsert task-state attention. |
| Accept digest result | `my_account.personal_attention_digest.accept_result` | Same signed-in user | Record human acknowledgement/acceptance of the advisory digest only; does not acknowledge, dismiss, resolve, or mutate source attention. |
| Reject digest result | `my_account.personal_attention_digest.reject_result` | Same signed-in user | Requires reason; records rejection/request for refreshed digest; does not mutate source attention. |
| Open digest evidence | `my_account.personal_attention_digest.open_evidence` plus source item capability | Same signed-in user | Opens only source refs and attention items still authorized under current `AuthContext`; hidden or stale refs return `not_found_or_redacted`. |
| Runtime evidence reads | `attention.list_my_account_items`, `attention.open_attention_item`, optional `readSkill`/`readReferenceDoc` grants | AutonomousAgent runtime only | Read-only evidence tools with `ToolPermissionBoundary` enforcement; no write/acknowledge/resolve/dismiss grants. |

The existing `my_account.list_personal_attention`, `attention.list_my_account_items`, and `attention.open_attention_item` capabilities remain the source attention authorities. The digest capabilities govern only task lifecycle and digest review.

## Personal attention evidence and redaction model

Evidence collection starts from `AttentionService.listMyAccountItems(actor, correlationId)` and `MyAccountService.personalAttention(actor, correlationId)` semantics:

- include only `AttentionItem` records visible to the selected `AuthContext` by tenant, nullable customer, required capability, assignee rule, account/role/capability/workstream/tenant assignment, and actionable lifecycle state;
- preserve backend `AttentionRedactionLevel`; summary-only evidence may be summarized only from its already redacted title/summary/severity/category and must not expose source refs or surface refs omitted by the attention service;
- hidden workstreams/items must not leak by name, count, severity, category, trace id, inferred omission, or comparative wording;
- digest aggregate counts are counts of included authorized evidence only and must be labeled `authorizedAttentionCount`, never total tenant/customer attention;
- output may group by visible `owningWorkstreamId` only when each grouped item is authorized and the workstream itself is visible through source capabilities;
- stale source refs must be reauthorized when opened; if the user lost capability, return `not_found_or_redacted` without revealing prior existence;
- browser-safe payloads must omit raw prompt text, hidden prompt text, raw tool payloads, provider credentials, API keys, JWTs, invitation tokens, cross-tenant evidence, and unsupported hidden workstream identifiers.

Suggested evidence refs:

```text
attention_item:<attention-item-id>
attention_summary:<workstream-id>:<authorized-count-hash>
work_trace:<digest-task-id>:evidence-read
capability:attention.list_my_account_items
capability:my_account.personal_attention_digest.read
```

## Task input schema

Suggested Java records for `application.security` or `application.agentfoundation`:

```java
public final class MyAccountPersonalAttentionDigestTasks {
  public static final Task<PersonalAttentionDigestResult> PERSONAL_ATTENTION_DIGEST = Task
      .name("MyAccountPersonalAttentionDigest")
      .description("Summarize authorized personal attention for the signed-in account and selected AuthContext")
      .resultConformsTo(PersonalAttentionDigestResult.class)
      .rules(PersonalAttentionDigestResultRule.class);
}

public record PersonalAttentionDigestRequest(
    String digestTaskId,
    String tenantId,
    String customerId,
    String accountId,
    String selectedContextId,
    List<PersonalAttentionEvidenceItem> evidenceItems,
    List<String> evidenceRefs,
    List<String> visibleCapabilityIds,
    String idempotencyKey,
    String correlationId) {}

public record PersonalAttentionEvidenceItem(
    String evidenceId,
    String attentionItemId,
    String sourceWorkstreamId,
    String label,
    String redactedSummary,
    String status,
    String severity,
    String category,
    String requiredCapabilityId,
    String surfaceRefId,
    String redactionLevel,
    List<String> traceRefs) {}
```

Task instructions must include selected `AuthContext` summary, tenant/customer scope, signed-in account id, digest task id, correlation id, authorized evidence refs, redaction level for each evidence item, read-only authority, forbidden effects, and fail-closed/no fake success requirements.

## Result schema

```java
public record PersonalAttentionDigestResult(
    String digestTaskId,
    String tenantId,
    String customerId,
    String accountId,
    String selectedContextId,
    String summary,
    int authorizedAttentionCount,
    DigestUrgency highestUrgency,
    List<PersonalAttentionDigestSection> sections,
    List<PersonalAttentionDigestRecommendation> recommendations,
    List<String> evidenceRefs,
    List<String> traceIds,
    String safety) {}

public enum DigestUrgency { NONE, INFO, WARNING, URGENT, BLOCKED }

public record PersonalAttentionDigestSection(
    String sectionId,
    String sourceWorkstreamId,
    String heading,
    String redactedSummary,
    String highestSeverity,
    int authorizedItemCount,
    List<String> evidenceRefs) {}

public record PersonalAttentionDigestRecommendation(
    String recommendationId,
    String label,
    String rationale,
    String targetFunctionalAgentId,
    String targetSurfaceId,
    String requiredCapabilityId,
    List<String> evidenceRefs) {}
```

Minimum result requirements:

- `digestTaskId`, `tenantId`, nullable `customerId`, `accountId`, and `selectedContextId` match the durable projection/request.
- The result is browser-safe and preserves personal attention evidence/redaction constraints.
- `authorizedAttentionCount` equals only authorized evidence presented to the AutonomousAgent task; it must not claim global totals.
- Recommendations are navigation/review suggestions only and map to already authorized My Account/open-attention capabilities.
- Empty authorized attention is a valid typed result only after the real Akka `AutonomousAgent` runtime ran with provider/model/governed runtime configured; it must not be a deterministic/model-less normal success fallback.
- `safety` states that the digest is advisory, source attention remains authoritative, and source item lifecycle changes require separate governed capabilities.
- `traceIds` include attention evidence reads, prompt assembly/skill/reference loads when used, model invocation/work trace, task lifecycle projection, v3 event publication, and human decision traces where available.

## Lifecycle mapping

| Akka task status / event | Digest projection status | v3 event | Attention behavior | My Account surface state |
|---|---|---|---|---|
| start accepted before assignment | `QUEUED` | `worker.task.queued`, `workflow.my_account.personal_attention_digest.started` | optional progress-only task state | `queued` |
| assigned/started/in progress | `RUNNING` | `worker.task.running` | optional stale progress attention only if overdue | `running` |
| missing provider/model/runtime/profile/tool grants/evidence access before successful model call | `BLOCKED_PROVIDER_OR_RUNTIME` | `worker.task.blocked_provider_or_runtime`, `workflow.my_account.personal_attention_digest.blocked_provider_or_runtime`, optional `provider.readiness.fail_closed` | upsert blocked digest task attention | `blocked_provider_or_runtime` |
| authorized evidence is empty after real runtime invocation | `COMPLETED_REVIEW_REQUIRED` or `COMPLETED_EMPTY` | `worker.task.completed_review_required`, `workflow.my_account.personal_attention_digest.completed_review_required` | optional digest-ready attention; may avoid attention if surface can show empty digest | `completed_empty` / `completed_review_required` |
| Akka `COMPLETED` with valid result | `COMPLETED_REVIEW_REQUIRED` | `worker.task.completed_review_required`, `workflow.my_account.personal_attention_digest.completed_review_required` | upsert digest-ready attention owned by My Account | `completed_review_required` |
| Akka `FAILED` | `FAILED` | `worker.task.failed`, `workflow.my_account.personal_attention_digest.failed` | upsert blocked/failure attention | `failed` |
| service cancel | `CANCELLED` | `worker.task.cancelled`, `workflow.my_account.personal_attention_digest.cancelled` | resolve digest task-state attention | `cancelled` |
| human accepts advisory digest | `ACKNOWLEDGED` / `ACCEPTED` | `worker.task.accepted`, `workflow.my_account.personal_attention_digest.result_accepted` | resolve digest task-state attention only | `accepted` |
| human rejects advisory digest | `REJECTED` | `worker.task.rejected_result`, `workflow.my_account.personal_attention_digest.result_rejected` | upsert rejected digest attention | `rejected_result` |

## v3 event requirements

Required workflow events:

- `workflow.my_account.personal_attention_digest.started`
- `workflow.my_account.personal_attention_digest.blocked_provider_or_runtime`
- `workflow.my_account.personal_attention_digest.failed`
- `workflow.my_account.personal_attention_digest.completed_review_required`
- `workflow.my_account.personal_attention_digest.cancelled`
- `workflow.my_account.personal_attention_digest.result_accepted`
- `workflow.my_account.personal_attention_digest.result_rejected`

Required shared worker-task events:

- `worker.task.queued`
- `worker.task.running`
- `worker.task.blocked_provider_or_runtime`
- `worker.task.failed`
- `worker.task.completed_review_required`
- `worker.task.cancelled`
- `worker.task.accepted`
- `worker.task.rejected_result`

Required source refs:

- `autonomous_task` with Akka task id when available;
- digest task/projection id;
- `attention_item` refs for included authorized evidence only;
- `capability` refs for `my_account.personal_attention_digest.*`, `my_account.list_personal_attention`, and `attention.list_my_account_items`;
- `work_trace` / `audit_trace` refs for attention evidence reads, ToolPermissionBoundary decision, model call, task lifecycle projection, event publication, and human decisions.

Idempotency key pattern:

```text
workstream-event:<family>:<eventType>:<tenantId>:<customerId-or-none>:<digest-task-id>:<semantic-transition>
```

Events are projection triggers only. They must not grant authority, mutate source attention, or expose redacted/hidden workstreams.

## Attention mappings

Digest task-state attention item id:

```text
attention:worker-task:<digest-task-id>:task-state
```

Mappings:

- blocked/fail-closed/failure/rejected/completed-review-required states upsert an item owned by `agent-my-account` and visible only to the signed-in account or selected-context readers with `my_account.personal_attention_digest.read`.
- queued/running normally update progress surfaces without human attention unless stale/overdue.
- accepted/acknowledged and cancelled states resolve only the digest task-state attention item.
- digest acceptance must not acknowledge, dismiss, resolve, expire, or mutate source attention items.
- duplicate event projection is a no-op with trace/audit evidence.
- source refs include event id, idempotency key, digest task id, capability ids, authorized evidence refs, and trace refs.

## My Account structured surfaces

Add backend-projected surfaces; do not rely on frontend fixtures as authority:

- `surface-my-account-personal-attention-digest-progress` (`workflow-status`): status, progress summary, blocker/failure reason, `digestTaskId`, optional `autonomousAgentTaskId`, selected scope, initiating capability, trace refs, and safe recovery text.
- `surface-my-account-personal-attention-digest-result` (`dashboard` or `workflow-status`): typed summary, authorized attention count, highest urgency, sections, recommendations, evidence refs, trace refs, redaction metadata, and `noDirectMutation=true`.
- `surface-my-account-personal-attention-digest-blocked` (`system_message`): provider/runtime fail-closed copy, recovery steps, correlation id, trace refs, and no fake success wording.

Surface actions must map to governed capabilities:

| Action | Capability | Notes |
|---|---|---|
| Refresh digest state | `my_account.personal_attention_digest.read` | Reloads backend projection and latest task snapshot. |
| Start digest | `my_account.personal_attention_digest.start` | Requires idempotency key and current selected context. |
| Cancel digest | `my_account.personal_attention_digest.cancel` | No source attention mutation. |
| Accept/acknowledge digest result | `my_account.personal_attention_digest.accept_result` | Resolves digest task attention only. |
| Reject digest result | `my_account.personal_attention_digest.reject_result` | Requires safe reason; may keep digest attention open. |
| Open source attention item | `attention.open_attention_item` plus item capability | Reauthorizes item; hidden/stale evidence returns `not_found_or_redacted`. |
| Open authorized workstream | `my_account.open_authorized_workstream` | Only for visible workstream targets already present in backend-authorized evidence. |

## Provider fail-closed and no fake success

Normal runtime success requires the concrete Akka `AutonomousAgent` task lifecycle plus governed runtime resolution. Missing `ComponentClient`, provider/model configuration, governed profile, prompt/skill/reference grants, ToolPermissionBoundary grants, or evidence access must produce `BLOCKED_PROVIDER_OR_RUNTIME`, v3 blocked events, blocked attention, and browser-safe recovery copy.

Forbidden substitutes:

- deterministic, canned, demo, simulated, fake, fixture, or model-less successful personal attention digest output;
- direct service/provider calls bypassing Akka `AutonomousAgent` task lifecycle;
- frontend-only digest-ready state or rail badge authority;
- using `TestModelProvider.AutonomousAgentTools.completeTask(...)` outside tests;
- summarizing hidden workstream/item existence through counts, omissions, comparisons, or trace refs;
- accepting/rejecting the digest as a shortcut for source attention lifecycle mutation.

## Tests and validation expectations

Runtime and surface tasks should add tests for:

- start/read/cancel/accept/reject authorization, disabled-user denial, tenant/customer isolation, and selected-context capability denial;
- idempotent start and duplicate idempotency-key reuse;
- authorized attention evidence collection and redaction, including hidden workstream/item non-leakage by name/count/inference;
- ToolPermissionBoundary denial for ungranted `attention.list_my_account_items`, `attention.open_attention_item`, `readSkill`, or `readReferenceDoc` access;
- provider/model/runtime missing configuration maps to blocked provider fail-closed state with event and attention, never fake success;
- typed result validation and invalid-result rejection through `TaskRule`;
- Akka task snapshots/results as source of truth for completed digest findings;
- v3 `workflow.my_account.personal_attention_digest.*` and `worker.task.*` event payload/source refs/idempotency;
- attention upsert/resolve behavior for digest task-state attention without mutating source attention;
- My Account surfaces render blocked, failed, review-required, accepted, rejected, forbidden, stale, and reconnect states from backend projections;
- focused scans for `AutonomousAgent`, personal attention evidence/redaction, provider fail-closed, v3 events, attention, My Account surfaces, and no fake success.
