# AutonomousAgent Worker Examples and Next-Worker Recommendations

## Purpose

This handoff makes the two completed starter-template `AutonomousAgent` worker verticals discoverable as reusable examples and records recommended next worker candidates without creating implementation queues.

Use this alongside:

- `docs/autonomous-agent-worker-runtime-pattern.md` — reusable worker contract and runtime guardrails.
- `specs/autonomous-agent-worker-pattern-extraction/worker-pattern-inventory.md` — extracted common/different pattern elements.
- `specs/autonomous-agent-runtime-integration/autonomous-agent-runtime-handoff.md` — source handoff for the completed verticals.

## Implemented example index

| Example | Owning workstream | Capability family | Primary surface | Contract/evidence |
|---|---|---|---|---|
| User Admin Access Review AutonomousAgent | `agent-user-admin` | `user_admin.access_review.start/read/cancel/accept_result/reject_result` | `surface-user-admin-access-review` / `user_admin.access_review_task.v1` | `specs/autonomous-agent-runtime-integration/user-admin-access-review-autonomous-agent-contract.md`; `specs/autonomous-agent-runtime-integration/runtime-validation-evidence.md` |
| Agent Admin Prompt-Risk Review AutonomousAgent | `agent-agent-admin` | `agent_admin.prompt_risk_review.start/read/cancel/accept_result/reject_result` | `surface-agent-admin-prompt-risk-review` / `agent_admin.prompt_risk_review_task.v1` | `specs/agent-admin-prompt-risk-autonomous-agent/agent-admin-prompt-risk-autonomous-agent-contract.md`; `specs/agent-admin-prompt-risk-autonomous-agent/prompt-risk-verification.md` |

Both examples are durable internal/background workers, not left-rail request/response workstream agents. Their normal runtime path uses concrete Akka `AutonomousAgent` tasks, backend projections, v3 `worker.task.*` events, attention, structured surfaces, and provider/runtime fail-closed behavior. Neither example treats deterministic/model-less output, fixture state, frontend-only state, or direct provider calls that bypass Akka `AutonomousAgent` as successful runtime completion.

## Example file landmarks

### User Admin Access Review

- Component/task/result: `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/UserAdminAccessReviewAutonomousAgent.java`, `UserAdminAccessReviewTasks.java`, `AccessReviewAutonomousAgentResult.java`, `AccessReviewAutonomousAgentResultRule.java`.
- Runtime adapter/fail-closed boundary: `ComponentClientAccessReviewAutonomousAgentRuntime.java`, `AccessReviewAutonomousAgentRuntime.java`, `FailClosedAccessReviewAutonomousAgentRuntime.java`.
- Capability/projection owner: `UserAdminAccessReviewService.java`, `AccessReviewTask.java`.
- Tests: `UserAdminAccessReviewAutonomousAgentTest.java`, `UserAdminAccessReviewServiceTest.java`, `WorkstreamEventBackboneServiceTest.java`.

### Agent Admin Prompt-Risk Review

- Component/task/result: `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentAdminPromptRiskAutonomousAgent.java`, `AgentAdminPromptRiskTasks.java`, `PromptRiskAutonomousAgentResult.java`, `PromptRiskAutonomousAgentResultRule.java`.
- Runtime adapter/fail-closed boundary: `ComponentClientPromptRiskAutonomousAgentRuntime.java`, `PromptRiskAutonomousAgentRuntime.java`, `FailClosedPromptRiskAutonomousAgentRuntime.java`.
- Capability/projection owner: `AgentAdminPromptRiskReviewService.java`, `PromptRiskReviewTask.java`, `DurablePromptRiskReviewTaskRepositoryEntity.java`.
- Tests: `AgentAdminPromptRiskAutonomousAgentTest.java`, `AgentAdminPromptRiskReviewServiceTest.java`, `DurablePromptRiskReviewTaskRepositoryEntityTest.java`, `WorkstreamEventBackboneServiceTest.java`, `workstream-agent-admin-vertical.contract.test.mjs`.

## Recommended next-worker candidates

These candidates are recommendations only. Do not start implementation from this file alone; each candidate first needs its own governed capability contract, task/result schema, evidence model, runtime adapter/fail-closed boundary, v3 event/attention mapping, structured surface, tests, and local rendered/runtime validation plan.

1. **Audit/Trace scheduled audit summary** — now tracked by `specs/audit-trace-summary-autonomous-agent/`. Current handoff: the bounded Audit/Trace summary contract and blocked/review surfaces are documented, but validation did not find backend `AuditTraceSummaryAutonomousAgent` runtime classes. Treat it as an implemented vertical only at the documented contract/surface-fixture scope until a concrete Akka `AutonomousAgent` runtime exists. It remains a narrow Audit/Trace worker, not a future digest platform; provider fail-closed behavior, scoped redaction, and no fake success remain mandatory completion gates.
2. **Governance/Policy policy-change impact analysis** — review proposed policy, threshold, approval-rule, or capability-boundary changes and produce advisory impact/risk findings before human governance approval.
3. **My Account personal attention digest** — create a user-scoped digest of cross-workstream attention only after attention projection semantics are reliable and tenant/customer visibility boundaries are proven.
4. **User Admin stale-invitation cleanup review** — investigate stale invitations, duplicate-account signals, and admin-risk indicators, then recommend deterministic follow-up actions for human User Admin approval.
5. **Support-access review** — review support-access grants, scope, expiry, and evidence for authorized admins; keep grant/revoke/extend as separate deterministic governed capabilities.
6. **Invitation-drafting queue** — draft invitation batches or onboarding follow-up text for human review, using Resend/outbox only through separate approved capabilities.

## Handoff rules for future workers

For any next worker, preserve these gates before marking the feature implemented:

- normal success invokes a concrete Akka `AutonomousAgent` task through `ComponentClient.forAutonomousAgent(...).runSingleTask(...)` and projects state with `ComponentClient.forTask(...).get(...)`;
- provider/model/governed runtime/tool/evidence gaps fail closed with actionable state, v3 events, attention, and no fake success;
- task output is typed and browser-safe, with domain-specific result rules;
- human accept/reject records advisory disposition only unless a separate governed capability performs a protected mutation;
- frontend surfaces reload backend projections and never invent completed worker state;
- `TestModelProvider.AutonomousAgentTools.completeTask` / `failTask` remain test infrastructure only.
