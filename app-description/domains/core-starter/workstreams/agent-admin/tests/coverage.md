# Tests: Agent Admin

## Acceptance

- Given an authorized caller with selected `AuthContext`, when they open Agent Admin, then the dashboard and allowed surfaces render only scoped data and expose only authorized actions.
- Given an allowed action, when it is submitted with valid input, then capability `managed-agent-governance` returns the expected structured result and emits required traces.
- Behavior proposal coverage includes draft, submit, approve, reject with reason, defer with reason/follow-up, cancel, ready-for-activation, activation confirmation, rollback confirmation, deactivation confirmation, stale version conflict, provider/runtime blocked, tool-boundary blocked, approval-required, and idempotent repeat/no-op paths.
- Prompt-risk review coverage includes start/read/cancel, provider/model/runtime/tool-boundary fail-closed, completed real model-backed advisory result, accept/reject into proposal evidence, and denial of blocked/deferred/fixture-only/model-less review acceptance.
- Seed material coverage includes search/filter/reset, provenance open, prepare import, start import, cancel import, conflict/customization-preservation blockers, provider/runtime blockers, open target agent detail, and redacted trace opens.

## Security and negative

- Disabled users, inactive memberships, Customer Admin selected contexts, role/capability denials, and cross-tenant/customer requests are denied without protected-data leakage.
- Customer Admins cannot see Agent Admin catalog/dashboard surfaces, submit AgentAdminAgent turns, read prompt/skill/reference/manifest/model/tool-boundary/seed material, start prompt-risk review tasks, or draft/approve/activate/roll back behavior changes.
- Agent/tool calls cannot exceed the governed tool boundary or approval policy.
- Browser payloads never expose provider secrets or hidden authority state.

## Idempotency and observability

- Repeated proposal decisions, lifecycle confirmations, prompt-risk review decisions, and seed-import starts/cancellations do not duplicate effects and retain correlation/trace evidence.
- Denials, approval-required outcomes, provider fail-closed states, tool-boundary denials, stale/conflict outcomes, hidden/not-found results, and trace emissions are verifiable through local Akka/API/UI tests or readiness evidence.


## `human_chat_tool_plan` coverage

- Given deterministic surface routing can safely open or prefill a surface, when a high-confidence no-mutation prompt is submitted, then the router returns that surface first and `human_chat_tool_plan` is not used.
- Given the representative prompt **start prompt risk review for the Agent Admin prompt proposal** and an authorized selected `AuthContext`, when the chat request is classified as `human_chat_tool_plan`, then the response is a no-mutation plan proposal surface that lists actions `action-agent-prompt-risk-review-start`, governed tools `agent_admin.start_behavior_review_task`, capabilities `agent_admin.start_behavior_review_task`, validated input schema `schema.agent-admin.prompt-risk-review.start.v1` with visible `agentDefinitionId`, `proposalId`, redacted `artifactDeltas`, reason, and idempotency key, side effects, approval gates, idempotency, result surfaces `surface-agent-admin-prompt-risk-review`, and trace refs.
- Given a proposed plan has not been explicitly confirmed, when the request completes, then no surface action, governed tool, external provider side effect, state mutation, invitation/email/outbox send, policy/agent lifecycle change, trace note append, or settings update has occurred.
- Given the human confirms the exact `planId` and `planSnapshotId`, when backend authorization, lifecycle, tool-boundary, validation, approval, tenant/customer ownership, and idempotency checks pass for every step, then each step executes as an independent transaction boundary and returns the declared result or recovery surface.
- Given a modified, stale, expired, cross-context, cross-tenant/customer, missing-confirmation, out-of-catalog, unsupported-field, hidden-target, provider/runtime/tool-boundary blocked, or unauthorized plan is confirmed, then execution is denied with `chat_tool_plan.system_message.v1`, `noDirectMutation=true`, safe recovery, no hidden-target enumeration, and trace refs.
- Given the same proposal or confirmed step is replayed with the same idempotency key, then the backend returns the existing proposal/result and does not duplicate side effects, traces, notifications, provider calls, or attention items.
- Given a later dependent step fails after an earlier step commits, then the plan result reports completed, failed, skipped, and recovery steps without rolling back committed work unless a cataloged compensating action exists.
- Given provider/model/runtime configuration is missing for model-backed proposal generation, then the workstream returns a typed plan-unavailable/system-message state and trace evidence instead of fake/model-less planning success.
- Given any agent, prompt, skill, reference, frontend state, route, or visible rail item suggests broader authority than the catalog grants, then the backend rejects the plan or step and records a denial trace.
