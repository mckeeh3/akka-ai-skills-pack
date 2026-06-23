# Tests: Governance/Policy

## Acceptance

- Given an authorized caller with selected `AuthContext`, when they open Governance/Policy, then the dashboard and allowed surfaces render only scoped data and expose only authorized actions.
- Given an authorized caller drafts a valid proposal with an idempotency key, when the draft action is submitted, then the proposal surface returns lifecycle state `draft`, inert before/after summaries, affected capabilities, trace refs, and no authority mutation.
- Given a draft or changes-requested proposal, when the caller submits it for review, then the starter proposal moves to `in-review` while evidence-gated variants may use `submitted` or `simulation-required` according to prerequisites; policy/workstream traces and activation evidence blockers are emitted.
- Given a proposal requiring evidence, when `simulate-policy-change` runs, then the simulation surface returns advisory expected allows/denials, warnings, confidence, evidence refs, and `noDirectMutation=true` without approval or activation.
- Given a proposal needing deeper review, when `start-policy-impact-analysis` is submitted, then a durable task surface returns `queued` or `running` and no policy authority changes.
- Given an impact-analysis task completes, when an authorized user accepts, rejects, or requests changes to the result, then the result surface records disposition and required reasons where applicable without approving or activating the proposal.
- Given a proposal with sufficient evidence and reviewer authority, when decision mode `decide` is approved, rejected, or request-changes, then only the allowed lifecycle transition occurs and a decision card plus policy-decision trace are emitted; the same authorized drafter may approve unless a stricter scope policy applies.
- Given a high-risk proposal for role/capability expansion, support-access policy, agent tool-boundary, model/provider configuration, or audit export policy, when multiple eligible approvers exist, then two approval records are required before activation; when only one eligible approver exists, a single approval may proceed with trace evidence that no second approver was available.
- Given an approved proposal with current version, activation prerequisites, rollback metadata, and activation authority, when command mode `activate` is submitted, then the proposal moves to `activated`, emits admin audit and policy-decision traces, and returns a decision surface.
- Given an activated or rollback-candidate proposal with rollback authority and rollback metadata, when command mode `rollback` is submitted, then the proposal moves to `rolled-back`, emits traces, and returns a decision surface.
- Given an authorized caller records an outcome note for an existing decision, when the note is submitted, then an outcome surface links metrics/evidence to the decision without changing authority.

## Regression and lifecycle boundaries

- Impact-analysis acceptance must satisfy only evidence/disposition requirements; it must not count as policy approval, activation, rollback, or authority expansion.
- `approve-activate-or-rollback-policy` must require an explicit command mode and must not infer activation from an approval recommendation.
- Blocked activation/rollback must return a system message or decision card with blockers, never a successful state.
- Proposal surfaces must expose only actions valid from the current lifecycle state and authorized for the selected context.

## Security and negative

- Disabled users, inactive memberships, role/capability denials, and cross-tenant/customer requests are denied without protected-data leakage.
- Agent/tool calls cannot exceed the governed tool boundary or approval policy.
- The Governance/Policy agent may draft, summarize, recommend, simulate, and start/read advisory impact work, but cannot execute approval, activation, rollback, impact-result disposition, hidden threshold changes, or authority expansion.
- Browser payloads never expose provider secrets, raw prompts, hidden authority state, JWTs, raw tool payloads, or cross-tenant evidence.
- Missing selected context, missing proposal/task ids, invalid lifecycle transition, stale proposal version, missing evidence, missing rollback metadata, missing reason, and unsupported authority-expansion requests return safe validation/conflict/system-message outcomes and emit denial/failure traces.

## Idempotency and observability

- Repeating any side-effecting proposal, task, decision, activation, rollback, or outcome-note action with the same idempotency key returns the existing result and does not duplicate effects, traces, notifications, or attention items.
- Provider/runtime unavailable during impact analysis returns `blocked_provider_or_runtime`; no deterministic or fake successful model analysis is produced.
- Denials, approval-required outcomes, provider fail-closed states, stale/conflict outcomes, impact-task events, policy-decision traces, admin-audit events, workstream-log traces, and agent-work traces are verifiable through local Akka/API/UI tests or readiness evidence.


## `human_chat_tool_plan` coverage

- Given deterministic surface routing can safely open or prefill a surface, when a high-confidence no-mutation prompt is submitted, then the router returns that surface first and `human_chat_tool_plan` is not used.
- Given the representative prompt **draft a policy proposal to require approval before redacted exports** and an authorized selected `AuthContext`, when the chat request is classified as `human_chat_tool_plan`, then the response is a no-mutation plan proposal surface that lists actions `action-governance-policy-draft-proposal`, governed tools `governance.policy.propose`, capabilities `governance.policy.propose`, validated input schema `schema.governance-policy.proposal.draft.v1` with title, rationale, browser-safe proposed change summary, affected capabilities, and idempotency key, side effects, approval gates, idempotency, result surfaces `surface-governance-policy-proposal`, and trace refs.
- Given a proposed plan has not been explicitly confirmed, when the request completes, then no surface action, governed tool, external provider side effect, state mutation, invitation/email/outbox send, policy/agent lifecycle change, trace note append, or settings update has occurred.
- Given the human confirms the exact `planId` and `planSnapshotId`, when backend authorization, lifecycle, tool-boundary, validation, approval, tenant/customer ownership, and idempotency checks pass for every step, then each step executes as an independent transaction boundary and returns the declared result or recovery surface.
- Given a modified, stale, expired, cross-context, cross-tenant/customer, missing-confirmation, out-of-catalog, unsupported-field, hidden-target, provider/runtime/tool-boundary blocked, or unauthorized plan is confirmed, then execution is denied with `chat_tool_plan.system_message.v1`, `noDirectMutation=true`, safe recovery, no hidden-target enumeration, and trace refs.
- Given the same proposal or confirmed step is replayed with the same idempotency key, then the backend returns the existing proposal/result and does not duplicate side effects, traces, notifications, provider calls, or attention items.
- Given a later dependent step fails after an earlier step commits, then the plan result reports completed, failed, skipped, and recovery steps without rolling back committed work unless a cataloged compensating action exists.
- Given provider/model/runtime configuration is missing for model-backed proposal generation, then the workstream returns a typed plan-unavailable/system-message state and trace evidence instead of fake/model-less planning success.
- Given any agent, prompt, skill, reference, frontend state, route, or visible rail item suggests broader authority than the catalog grants, then the backend rejects the plan or step and records a denial trace.
