# AutonomousAgent Runtime Integration Handoff

## Current implemented vertical

The starter/reference template now has one bounded durable internal/background `AutonomousAgent` vertical: **User Admin Access Review**.

Implemented runtime path:

- Akka component: `UserAdminAccessReviewAutonomousAgent`.
- Typed task contract: `UserAdminAccessReviewTasks.ACCESS_REVIEW`, `AccessReviewAutonomousAgentResult`, and result rule validation.
- Runtime adapter: `ComponentClientAccessReviewAutonomousAgentRuntime` starts the Akka task with `runSingleTask`, queries task snapshots with `forTask(...).get(...)`, and projects lifecycle/result state into the starter `AccessReviewTask` record.
- Fail-closed adapter: `FailClosedAccessReviewAutonomousAgentRuntime` blocks provider/runtime-missing operation with actionable status instead of model-less success.
- Projection key: starter tasks store `autonomousAgentTaskId` alongside the starter access-review task id.
- Capability family: `user_admin.access_review.start/read/cancel/accept_result/reject_result`.

This vertical is advisory only. Worker output may recommend follow-up, but membership, role, invitation, account-status, support-access, policy, or behavior changes must run through separate deterministic governed capabilities with human authorization.

## Events, attention, and surfaces

Access-review lifecycle is backend-authoritative and visible through:

- v3 workflow events: `workflow.access_review.started`, `workflow.access_review.blocked_provider_or_runtime`, `workflow.access_review.completed_review_required`, `workflow.access_review.cancelled`, `workflow.access_review.result_accepted`, and `workflow.access_review.result_rejected`.
- v3 worker-task events: `worker.task.queued`, `worker.task.running`, `worker.task.blocked_provider_or_runtime`, `worker.task.failed`, `worker.task.completed_review_required`, `worker.task.cancelled`, `worker.task.accepted`, and `worker.task.rejected_result`.
- source refs: `autonomous_task`, starter workflow/task refs, capability refs, and trace/audit refs where available.
- attention id pattern: `attention:worker-task:<starter-task-id>:task-state`.
- structured surface: `surface-user-admin-access-review` with `user_admin.access_review_task.v1` data, backend-derived progress/result/review state, `noDirectMutation=true`, evidence refs, recommendations, trace ids, blockers/provider failures, and human accept/reject/cancel actions.

Frontend state is not authoritative. User Admin, My Account, rail attention, and update delivery must reload/read backend projections.

## Provider fail-closed and no fake success guardrails

The normal successful access-review result path requires the real Akka `AutonomousAgent` task lifecycle and a configured model/provider boundary. Missing or denied provider/runtime setup must fail closed with blocked/provider status, trace refs, v3 events, attention, and actionable surface copy.

Do not count any of the following as implemented normal runtime success:

- deterministic access-review recommendations;
- canned/demo/fixture/model-less summaries;
- direct provider/service calls that bypass the Akka `AutonomousAgent` task path;
- frontend-only state that marks review complete;
- test-only `TestModelProvider.AutonomousAgentTools.completeTask` / `failTask` use outside tests.

## Validation evidence

`runtime-validation-evidence.md` records the completed rendered-scaffold validation:

- backend `mvn -q test` passed;
- frontend `npm ci`, `npm test`, `npm run typecheck`, and `npm run build` passed;
- focused `rg` confirmed `AutonomousAgent` task APIs, provider fail-closed/no model-less success guardrails, v3 workflow/worker events, `autonomous_task` refs, worker-task attention ids, and `surface-user-admin-access-review` wiring;
- live provider smoke was not run because production provider secrets were absent, which is the intended fail-closed boundary and not a scoped release blocker.

## Future worker candidates

Use User Admin Access Review as the reference implementation pattern for later workers. Candidate follow-ups remain future work until they have their own governed capability contract, deterministic support services, provider fail-closed behavior, v3 events, attention mappings, structured surfaces, tests, and rendered/local runtime validation:

1. Agent Admin prompt-risk review.
2. Audit/Trace scheduled audit summary.
3. Governance/Policy policy-change impact analysis.
4. My Account personal attention digest after cross-workstream attention projections are reliable.
5. User Admin stale-invitation cleanup, duplicate-account review, and admin-risk summary.

Broader AutonomousAgent team/delegation, handoff, moderation, dependency graphs, notification infrastructure, and multi-worker orchestration are intentionally outside this first vertical.

## Files to inspect first for future work

- Contract: `specs/autonomous-agent-runtime-integration/user-admin-access-review-autonomous-agent-contract.md`.
- Evidence: `specs/autonomous-agent-runtime-integration/runtime-validation-evidence.md`.
- Starter docs: `templates/ai-first-saas-starter/README.md`.
- Worker candidates: `specs/full-core-smb-saas-hardening/agent-worker-opportunities.md`.
- User Admin contract: `specs/full-core-smb-user-admin/user-admin-vertical-contracts.md`.
- Release handoff: `specs/full-core-smb-polish-release-readiness/release-handoff.md`.
