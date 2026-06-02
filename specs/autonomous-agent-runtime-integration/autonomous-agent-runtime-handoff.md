# AutonomousAgent Runtime Integration Handoff

## Current implemented verticals

The starter/reference template now has five bounded durable internal/background `AutonomousAgent` verticals:

1. **User Admin Access Review**.
2. **Agent Admin Prompt-Risk Review**.
3. **My Account Personal Attention Digest**.
4. **Audit/Trace Summary**.
5. **Governance/Policy Impact Analysis**.

This handoff started with the first two verticals and now includes status alignment for later completed starter workers. Use the worker-specific handoffs listed below as the detailed source for each later vertical.

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

### Later completed bounded workers

- **My Account Personal Attention Digest** is documented by `specs/my-account-personal-attention-digest-autonomous-agent/my-account-personal-attention-digest-handoff.md`. It summarizes authorized personal attention evidence for the signed-in selected `AuthContext`; it is not an email, push, scheduled, enterprise, or general notification platform.
- **Audit/Trace Summary** is documented by `specs/audit-trace-summary-autonomous-agent/audit-trace-summary-handoff.md`. It summarizes scoped/redacted audit and work-trace evidence; it is not a general scheduled digest platform or enterprise audit export capability.
- **Governance/Policy Impact Analysis** is documented by `specs/governance-policy-impact-autonomous-agent/handoff.md`. It analyzes proposed policy/governance changes; it is not a policy activation, rollback, policy-as-code, or full simulation platform.

## Events, attention, and surfaces

Each implemented worker emits shared `worker.task.*` v3 lifecycle events and a workflow-specific event family:

- User Admin Access Review: `workflow.access_review.*`, `attention:worker-task:<starter-task-id>:task-state`, `surface-user-admin-access-review`.
- Agent Admin Prompt-Risk Review: `workflow.agent_admin.prompt_risk_review.*`, `attention:worker-task:<starter-task-id>:task-state`, `surface-agent-admin-prompt-risk-review`.
- My Account Personal Attention Digest: `workflow.my_account.personal_attention_digest.*`, `attention:worker-task:<starter-task-id>:task-state`, My Account digest progress/result/blocked surfaces.
- Audit/Trace Summary: `workflow.audit_trace.summary_*`, `attention:worker-task:<starter-task-id>:task-state`, audit-summary progress/review surfaces.
- Governance/Policy Impact Analysis: `workflow.governance_policy.impact_analysis.*`, `attention:worker-task:<starter-task-id>:task-state`, `surface-governance-policy-impact-analysis-task` and `surface-governance-policy-impact-analysis-result`.

Frontend state is not authoritative. User Admin, Agent Admin, My Account, Audit/Trace, Governance/Policy, rail attention, notification center, and update delivery must reload/read backend projections.

## Provider fail-closed and no fake success guardrails

Normal successful worker output requires the real Akka `AutonomousAgent` task lifecycle and a configured model/provider boundary. Missing or denied provider/runtime setup, managed-agent profile, `ComponentClient` binding, tool grants, governed loader tools, evidence permissions, authorization, or tenant/customer scope must fail closed with blocked/provider status, trace refs, v3 events, attention, and actionable surface copy.

Do not count any of the following as implemented normal runtime success:

- deterministic recommendations, findings, summaries, impact analysis, or digest output;
- canned/demo/fixture/model-less summaries;
- direct provider/service calls that bypass the Akka `AutonomousAgent` task path;
- frontend-only state that marks review complete;
- test-only `TestModelProvider.AutonomousAgentTools.completeTask` / `failTask` use outside tests.

## Validation evidence

- Access-review validation: `specs/autonomous-agent-runtime-integration/runtime-validation-evidence.md` records rendered-scaffold backend/frontend validation and focused scans for task APIs, provider fail-closed/no model-less success guardrails, v3 events, attention ids, and `surface-user-admin-access-review` wiring.
- Prompt-risk validation: `specs/agent-admin-prompt-risk-autonomous-agent/prompt-risk-validation.md` records no-provider fullstack validation, prompt-risk backend tests, v3 event/surface/attention scans, and no fake/model-less/deterministic normal success guardrails.
- Personal digest validation: `specs/my-account-personal-attention-digest-autonomous-agent/validation/01-personal-attention-digest-validation.md` records provider-skip fullstack validation, digest backend/frontend tests, and later real-provider smoke readiness resolution.
- Audit/Trace summary validation: `specs/audit-trace-summary-autonomous-agent/validation/03-concrete-runtime-path-validation.md` records concrete runtime path, provider fail-closed, scoped redaction, events, attention, surfaces, and no fake success evidence.
- Governance/Policy impact validation: `specs/governance-policy-impact-autonomous-agent/validation.md` records targeted backend tests, frontend contract tests, typecheck/build, and focused guardrail scans.
- Integrated validation: `specs/autonomous-agent-fullstack-regression-readiness/integrated-readiness-handoff.md` records integrated regression-readiness for the access-review, prompt-risk, audit-summary, and governance-impact workers. The personal digest real-provider smoke setup was separately verified in `specs/autonomous-agent-real-provider-smoke-readiness/99-verify-real-provider-smoke-readiness.md`.

## Future worker/platform candidates

Use the implemented workers as reference patterns for later workers. Candidate follow-ups remain future work until they have their own governed capability contract, deterministic support services, provider fail-closed behavior, v3 events, attention mappings, structured surfaces, tests, and rendered/local runtime validation:

1. Invitation-drafting queue or support-access review where the capability contract and approval boundary are explicit.
2. User Admin stale-invitation cleanup, duplicate-account review, and admin-risk summary.
3. Scheduled/cross-account digest or notification orchestration beyond the bounded personal digest worker.
4. Audit export, legal hold/e-discovery, SIEM, or scheduled audit digest platforms beyond the bounded Audit/Trace summary worker.
5. Broader governance replay/evaluation, policy activation/rollback, policy-as-code, or simulation platforms beyond the bounded impact-analysis worker.

Broader AutonomousAgent team/delegation, handoff, moderation, dependency graphs, notification infrastructure, and multi-worker orchestration are intentionally outside these bounded starter verticals.

## Files to inspect first for future work

- Access-review contract: `specs/autonomous-agent-runtime-integration/user-admin-access-review-autonomous-agent-contract.md`.
- Access-review evidence: `specs/autonomous-agent-runtime-integration/runtime-validation-evidence.md`.
- Prompt-risk contract: `specs/agent-admin-prompt-risk-autonomous-agent/agent-admin-prompt-risk-autonomous-agent-contract.md`.
- Prompt-risk evidence: `specs/agent-admin-prompt-risk-autonomous-agent/prompt-risk-validation.md`.
- Personal digest handoff: `specs/my-account-personal-attention-digest-autonomous-agent/my-account-personal-attention-digest-handoff.md`.
- Audit/Trace summary handoff: `specs/audit-trace-summary-autonomous-agent/audit-trace-summary-handoff.md`.
- Governance/Policy impact handoff: `specs/governance-policy-impact-autonomous-agent/handoff.md`.
- Starter docs: `templates/ai-first-saas-starter/README.md`.
- Worker candidates: `specs/full-core-smb-saas-hardening/agent-worker-opportunities.md`.
- Release handoff: `specs/full-core-smb-polish-release-readiness/release-handoff.md`.
