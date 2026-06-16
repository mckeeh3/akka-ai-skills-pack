# Tests: Governance/Policy

## Acceptance

- Given an authorized caller with selected `AuthContext`, when they open Governance/Policy, then the dashboard and allowed surfaces render only scoped data and expose only authorized actions.
- Given an authorized caller drafts a valid proposal with an idempotency key, when the draft action is submitted, then the proposal surface returns lifecycle state `draft`, inert before/after summaries, affected capabilities, trace refs, and no authority mutation.
- Given a draft or changes-requested proposal, when the caller submits it for review, then the proposal moves to `submitted` or `simulation-required` according to evidence prerequisites and emits policy/workstream traces.
- Given a proposal requiring evidence, when `simulate-policy-change` runs, then the simulation surface returns advisory expected allows/denials, warnings, confidence, evidence refs, and `noDirectMutation=true` without approval or activation.
- Given a proposal needing deeper review, when `start-policy-impact-analysis` is submitted, then a durable task surface returns `queued` or `running` and no policy authority changes.
- Given an impact-analysis task completes, when an authorized user accepts, rejects, or requests changes to the result, then the result surface records disposition and required reasons where applicable without approving or activating the proposal.
- Given a proposal with sufficient evidence and reviewer authority, when decision mode `decide` is approved, rejected, or request-changes, then only the allowed lifecycle transition occurs and a decision card plus policy-decision trace are emitted.
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
