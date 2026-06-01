# AutonomousAgent Runtime Integration Handoff

## Current implemented verticals

The starter/reference template now has two bounded durable internal/background `AutonomousAgent` verticals:

1. **User Admin Access Review**.
2. **Agent Admin Prompt-Risk Review**.

### User Admin Access Review

Implemented runtime path:

- Akka component: `UserAdminAccessReviewAutonomousAgent`.
- Typed task contract: `UserAdminAccessReviewTasks.ACCESS_REVIEW`, `AccessReviewAutonomousAgentResult`, and result rule validation.
- Runtime adapter: `ComponentClientAccessReviewAutonomousAgentRuntime` starts the Akka task with `runSingleTask`, queries task snapshots with `forTask(...).get(...)`, and projects lifecycle/result state into the starter `AccessReviewTask` record.
- Fail-closed adapter: `FailClosedAccessReviewAutonomousAgentRuntime` blocks provider/runtime-missing operation with actionable status instead of model-less success.
- Projection key: starter tasks store `autonomousAgentTaskId` alongside the starter access-review task id.
- Capability family: `user_admin.access_review.start/read/cancel/accept_result/reject_result`.

This vertical is advisory only. Worker output may recommend follow-up, but membership, role, invitation, account-status, support-access, policy, or behavior changes must run through separate deterministic governed capabilities with human authorization.

### Agent Admin Prompt-Risk Review

Implemented runtime path:

- Akka component: `AgentAdminPromptRiskAutonomousAgent`.
- Typed task contract: `AgentAdminPromptRiskTasks.PROMPT_RISK_REVIEW`, `PromptRiskAutonomousAgentResult`, and result rule validation.
- Runtime adapter: `ComponentClientPromptRiskAutonomousAgentRuntime` starts the Akka task with `runSingleTask`, queries task snapshots with `forTask(...).get(...)`, and projects lifecycle/result state into the starter `PromptRiskReviewTask` record.
- Fail-closed adapter: `FailClosedPromptRiskAutonomousAgentRuntime` blocks provider/runtime-missing operation with actionable status instead of deterministic, fake, or model-less prompt-risk success.
- Projection key: starter tasks store `autonomousAgentTaskId` alongside the starter prompt-risk task id.
- Capability family: `agent_admin.prompt_risk_review.start/read/cancel/accept_result/reject_result`.

This vertical reviews proposed prompt, skill, reference, model, and tool-boundary behavior changes. It is advisory only: accepting or rejecting the prompt-risk result records a human Agent Admin review decision, and never approves, activates, edits, rolls back, reseeds, or expands authority for managed-agent behavior artifacts.

## Events, attention, and surfaces

Access-review lifecycle is backend-authoritative and visible through:

- v3 workflow events: `workflow.access_review.started`, `workflow.access_review.blocked_provider_or_runtime`, `workflow.access_review.completed_review_required`, `workflow.access_review.cancelled`, `workflow.access_review.result_accepted`, and `workflow.access_review.result_rejected`.
- v3 worker-task events: `worker.task.queued`, `worker.task.running`, `worker.task.blocked_provider_or_runtime`, `worker.task.failed`, `worker.task.completed_review_required`, `worker.task.cancelled`, `worker.task.accepted`, and `worker.task.rejected_result`.
- source refs: `autonomous_task`, starter workflow/task refs, capability refs, and trace/audit refs where available.
- attention id pattern: `attention:worker-task:<starter-task-id>:task-state`.
- structured surface: `surface-user-admin-access-review` with `user_admin.access_review_task.v1` data, backend-derived progress/result/review state, `noDirectMutation=true`, evidence refs, recommendations, trace ids, blockers/provider failures, and human accept/reject/cancel actions.

Prompt-risk lifecycle is backend-authoritative and visible through:

- v3 workflow events: `workflow.agent_admin.prompt_risk_review.started`, `workflow.agent_admin.prompt_risk_review.blocked_provider_or_runtime`, `workflow.agent_admin.prompt_risk_review.completed_review_required`, `workflow.agent_admin.prompt_risk_review.cancelled`, `workflow.agent_admin.prompt_risk_review.result_accepted`, and `workflow.agent_admin.prompt_risk_review.result_rejected`.
- v3 worker-task events: the same `worker.task.*` state family used by access review for queued/running/blocked/failed/completed-review/cancelled/accepted/rejected transitions.
- source refs: `autonomous_task`, starter prompt-risk task refs, target governed managed-agent definition refs, behavior proposal refs, prompt/skill/reference/model/tool-boundary artifact refs, capability refs, and trace/audit refs where available.
- attention id pattern: `attention:worker-task:<starter-task-id>:task-state`.
- structured surface: `surface-agent-admin-prompt-risk-review` with `agent_admin.prompt_risk_review_task.v1` data, backend-derived progress/result/review state, `noDirectMutation=true`, `activationBlockedUntilHumanDecision=true`, browser-safe risk findings/recommendations, evidence refs, trace ids, blockers/provider failures, and human accept/reject/cancel actions.

Frontend state is not authoritative. User Admin, Agent Admin, My Account, rail attention, and update delivery must reload/read backend projections.

## Provider fail-closed and no fake success guardrails

The normal successful access-review and prompt-risk result paths require the real Akka `AutonomousAgent` task lifecycle and a configured model/provider boundary. Missing or denied provider/runtime setup must fail closed with blocked/provider status, trace refs, v3 events, attention, and actionable surface copy.

Do not count any of the following as implemented normal runtime success:

- deterministic access-review recommendations or prompt-risk findings;
- canned/demo/fixture/model-less summaries;
- direct provider/service calls that bypass the Akka `AutonomousAgent` task path;
- frontend-only state that marks review complete;
- test-only `TestModelProvider.AutonomousAgentTools.completeTask` / `failTask` use outside tests.

## Validation evidence

Access-review validation: `runtime-validation-evidence.md` records the completed rendered-scaffold validation:

- backend `mvn -q test` passed;
- frontend `npm ci`, `npm test`, `npm run typecheck`, and `npm run build` passed;
- focused `rg` confirmed `AutonomousAgent` task APIs, provider fail-closed/no model-less success guardrails, v3 workflow/worker events, `autonomous_task` refs, worker-task attention ids, and `surface-user-admin-access-review` wiring;
- live provider smoke was not run because production provider secrets were absent, which is the intended fail-closed boundary and not a scoped release blocker.

Prompt-risk validation: `specs/agent-admin-prompt-risk-autonomous-agent/prompt-risk-validation.md` records the completed rendered-scaffold validation:

- no-provider fullstack validation passed with backend Maven tests (`207` tests, `0` failures, `0` errors, `1` skipped), frontend tests (`132` passed), frontend typecheck, frontend build, and static asset secret scan;
- Akka annotation processing detected `2 autonomous-agent` components;
- prompt-risk backend tests included `AgentAdminPromptRiskReviewServiceTest`, `AgentAdminPromptRiskAutonomousAgentTest`, and `DurablePromptRiskReviewTaskRepositoryEntityTest`;
- focused `rg` confirmed prompt-risk capabilities, `workflow.agent_admin.prompt_risk_review.*` events, `surface-agent-admin-prompt-risk-review`, `attention:worker-task` linkage, `blocked_provider_or_runtime`, and no fake/model-less/deterministic normal success guardrails;
- optional real-provider smoke was blocked by ambient provider/network/TLS behavior and is recorded as an environment/provider smoke blocker, not as a prompt-risk-specific runtime failure or a reason to add fake success.

## Future worker candidates

Use User Admin Access Review and Agent Admin Prompt-Risk Review as the first two reference implementation patterns for later workers. Candidate follow-ups remain future work until they have their own governed capability contract, deterministic support services, provider fail-closed behavior, v3 events, attention mappings, structured surfaces, tests, and rendered/local runtime validation:

1. Audit/Trace scheduled audit summary.
2. Governance/Policy policy-change impact analysis.
3. My Account personal attention digest after cross-workstream attention projections are reliable.
4. User Admin stale-invitation cleanup, duplicate-account review, and admin-risk summary.
5. Invitation-drafting queue or support-access review where the capability contract and approval boundary are explicit.

Broader AutonomousAgent team/delegation, handoff, moderation, dependency graphs, notification infrastructure, and multi-worker orchestration are intentionally outside these bounded starter verticals.

## Files to inspect first for future work

- Access-review contract: `specs/autonomous-agent-runtime-integration/user-admin-access-review-autonomous-agent-contract.md`.
- Access-review evidence: `specs/autonomous-agent-runtime-integration/runtime-validation-evidence.md`.
- Prompt-risk contract: `specs/agent-admin-prompt-risk-autonomous-agent/agent-admin-prompt-risk-autonomous-agent-contract.md`.
- Prompt-risk evidence: `specs/agent-admin-prompt-risk-autonomous-agent/prompt-risk-validation.md`.
- Starter docs: `templates/ai-first-saas-starter/README.md`.
- Worker candidates: `specs/full-core-smb-saas-hardening/agent-worker-opportunities.md`.
- User Admin contract: `specs/full-core-smb-user-admin/user-admin-vertical-contracts.md`.
- Release handoff: `specs/full-core-smb-polish-release-readiness/release-handoff.md`.
