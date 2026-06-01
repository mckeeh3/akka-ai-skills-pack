# Agent Worker Opportunities

## Purpose

This document identifies concrete internal worker opportunities for full-core SMB hardening. These are not substitutes for request/response workstream agents or deterministic services. They are candidates for Akka `AutonomousAgent` or equivalent durable internal/background work only when task lifecycle semantics are justified and validated.

Current status: User Admin access-review investigation is the first implemented and validated starter/reference `AutonomousAgent` vertical. It remains bounded to advisory access-review task lifecycle and human result review; future workers below are candidates until they receive the same governed capability, provider fail-closed, no fake success, event/attention/surface, and runtime-validation evidence.

## Selection rule

Use a worker only when the work needs one or more of:

- durable task identity and result history;
- progress snapshots, notifications, dependencies, handoff, cancellation, retry, or failure handling;
- model-driven iteration beyond one request/response turn;
- human review, approval, rejection, or acceptance of a result;
- scheduled or triggered background analysis whose output becomes an attention item.

Do not use a worker for authorization, tenant filtering, idempotency, lifecycle state transitions, policy enforcement, trace normalization, redaction, outbox/email delivery, or provider readiness checks. Those remain deterministic services.

## Shared worker lifecycle surface contract

Every internal worker introduced by a child mini-project should expose typed surfaces for:

- task purpose, owner workstream, initiating capability, selected `AuthContext`, and tenant/customer scope;
- status: queued, running, blocked, provider_blocked, waiting_for_human, completed, failed, canceled, rejected, accepted;
- progress snapshots and last update time;
- blockers, dependencies, and next expected action;
- result summary, evidence references, confidence/risk where relevant, and recommended human action;
- authorized actions: start, read/progress, cancel, retry, suspend/resume where supported, accept/reject result, open trace, open related workstream;
- trace ids for task lifecycle, prompt assembly, skill/reference loads, model calls, tool calls, denials, provider failures, and result decisions.

Missing provider configuration must produce a blocked/provider system-message surface and trace. It must not create canned successful worker output.

## My Account worker candidates

### Personal attention digest

- Trigger: user opens My Account or scheduled lightweight digest generation.
- Purpose: summarize authorized cross-workstream attention items, blocked requests, pending decisions, failed provider/config states, and overdue personal actions.
- Justification: durable only if digest history, notifications, or asynchronous cross-workstream aggregation are needed; otherwise use deterministic attention aggregation plus request/response My Account Agent summary.
- Capabilities: `my_account.personal_digest.start`, `my_account.personal_digest.read`, `my_account.personal_digest.cancel`, `my_account.personal_digest.accept_result`.
- Required deterministic support: authority filtering, attention projection reads, trace redaction, idempotent scheduling.
- Human result: user can open items, dismiss digest, or request a refreshed digest.

### Context readiness review

- Trigger: selected context missing required profile/settings/notification preferences or has repeated context-selection denials.
- Purpose: investigate why the user cannot complete common workstream actions and propose self-service fixes.
- Justification: durable only when analysis spans multiple traces/settings and produces reviewable recommendations.
- Capabilities: `my_account.context_readiness_review.*`.
- Required deterministic support: context/membership checks, self-service field validation, denial trace reads.
- Human result: accept recommended settings changes only through governed self-service update capabilities.

## User Admin worker candidates

### Access review investigation

Status: **implemented first vertical at starter/reference scope** via Akka `AutonomousAgent`; see `specs/autonomous-agent-runtime-integration/user-admin-access-review-autonomous-agent-contract.md` and `specs/autonomous-agent-runtime-integration/runtime-validation-evidence.md`.

- Trigger: admin starts access review, scheduled SMB access review cadence, suspicious role/capability changes, or stale membership signals.
- Purpose: review scoped users, memberships, roles/capabilities, inactive accounts, disabled/reactivated users, risky grants, and last-admin constraints.
- Justification: durable task lifecycle, progress, evidence collection, result review, and potential human approvals are central.
- Capabilities: `user_admin.access_review.start`, `user_admin.access_review.read`, `user_admin.access_review.cancel`, `user_admin.access_review.accept_result`, `user_admin.access_review.reject_result`.
- Implemented runtime evidence: concrete `UserAdminAccessReviewAutonomousAgent`, typed task/result/rule definitions, ComponentClient-backed start/query/projection adapter, provider/runtime fail-closed adapter, starter `autonomousAgentTaskId`, `workflow.access_review.*` plus `worker.task.*` events with `autonomous_task` refs, `attention:worker-task:<taskId>:task-state`, and `surface-user-admin-access-review` progress/result/review states.
- Required deterministic support: member/role queries, tenant filtering, last-admin policy checks, audit trace reads, role-change preview capability.
- Human result: review recommendations; any membership or role change must execute through deterministic User Admin capabilities, not directly by worker output.
- Guardrail: no deterministic, fake, canned, or model-less successful recommendation may stand in for the normal Akka `AutonomousAgent` task path; missing provider/runtime setup fails closed with actionable blocked status, events, attention, and traces.

### Stale invitation cleanup review

- Trigger: expired invitations, repeated delivery failures, or dashboard stale-invite attention.
- Purpose: group stale/failed invitations and suggest resend, revoke, or contact updates.
- Justification: durable only when batching, notification, or human review is useful; simple expiry marking remains deterministic.
- Capabilities: `user_admin.stale_invite_review.*`.
- Required deterministic support: invitation/outbox state, Resend delivery status, expiry calculation, idempotent revoke/resend actions.
- Human result: admin approves resend/revoke actions per invitation or batch.

### Duplicate account detection

- Trigger: similar email/name patterns, repeated invite attempts, or support/admin concern.
- Purpose: identify possible duplicate accounts or memberships and recommend safe administrative follow-up.
- Justification: model-assisted comparison may be useful, but deterministic matching and tenant scoping must constrain evidence.
- Capabilities: `user_admin.duplicate_account_review.*`.
- Required deterministic support: scoped account/member query, redaction, no automatic merge.
- Human result: admin follows governed invite/member workflows; no worker-owned account merge.

### Admin-risk summary

- Trigger: recent high-risk admin changes or scheduled owner digest.
- Purpose: summarize disable/reactivate, role changes, invitation churn, failed authorization attempts, and unusual admin activity.
- Justification: cross-trace summary with durable result and attention item.
- Capabilities: `user_admin.admin_risk_summary.*`.
- Required deterministic support: audit/trace evidence, risk classification inputs, tenant filtering.
- Human result: open Audit/Trace or User Admin surfaces for action.

## Agent Admin worker candidates

### Behavior-change drafting worker

- Trigger: authorized admin requests help preparing a prompt, skill, reference, manifest, model-ref, or tool-boundary change.
- Purpose: draft a behavior-change proposal with diff, rationale, risk/authority summary, and required review path.
- Justification: useful when the drafting process spans multiple artifacts and requires reviewable output.
- Capabilities: `agent_admin.behavior_draft.start`, `agent_admin.behavior_draft.read`, `agent_admin.behavior_draft.cancel`, `agent_admin.behavior_draft.accept_result`.
- Required deterministic support: artifact reads, version validation, schema checks, ToolPermissionBoundary classification, proposal creation.
- Human result: admin may accept the draft into a proposal; activation still requires governed review/approval.

### Prompt-risk review worker

- Trigger: proposed prompt/skill/reference/tool-boundary change submitted for review or authority expansion detected.
- Purpose: analyze risks such as tool overreach, tenant leakage, hidden authority claims, unsafe fallback language, missing denials, or provider-secret exposure.
- Justification: durable review evidence, progress, and approver decision support.
- Capabilities: `agent_admin.prompt_risk_review.*`.
- Required deterministic support: diff extraction, authority-change detection, secret-boundary scans, trace/evidence linking.
- Human result: reviewer approves, rejects, or requests changes through proposal lifecycle capabilities.

### Replay/evaluation analysis worker

- Trigger: proposed behavior change requires simulation/replay evidence before activation.
- Purpose: run a bounded replay/evaluation batch against approved cases and summarize behavior deltas.
- Justification: long-running/batch work with progress, provider failures, result evidence, and human review.
- Capabilities: `agent_admin.behavior_eval.start`, `agent_admin.behavior_eval.read`, `agent_admin.behavior_eval.cancel`, `agent_admin.behavior_eval.accept_result`.
- Required deterministic support: approved replay case selection, provider boundary, result storage, redaction, scoring normalization.
- Human result: use results as proposal evidence; no automatic activation unless a later accepted policy explicitly allows it.

### Stale document review worker

- Trigger: old prompt/skill/reference versions, unreviewed drafts, broken references, or changed capability inventory.
- Purpose: identify stale governed behavior docs and propose maintenance tasks.
- Justification: scheduled background analysis with reviewable recommendations.
- Capabilities: `agent_admin.stale_doc_review.*`.
- Required deterministic support: artifact metadata query, version/provenance rules, link validation.
- Human result: open Agent Admin proposals or create backlog items.

## Audit/Trace worker candidates

### Scheduled audit summary worker

- Trigger: daily/weekly owner digest or explicit admin request.
- Purpose: summarize important denials, admin changes, provider failures, tool denials, policy decisions, worker failures, and unresolved attention.
- Justification: scheduled durable summary with evidence links and human review.
- Capabilities: `audit_trace.summary.start`, `audit_trace.summary.read`, `audit_trace.summary.cancel`, `audit_trace.summary.accept_result`.
- Required deterministic support: scoped trace search, severity/risk filters, redaction, correlation timeline assembly.
- Human result: open related traces or workstreams; dismiss/accept summary.

### Anomalous admin activity review

- Trigger: unusual cluster of role changes, disables/reactivations, failed admin actions, or cross-tenant denial attempts.
- Purpose: investigate patterns and produce a risk-ranked evidence summary.
- Justification: model-assisted pattern explanation over deterministic evidence with durable result review.
- Capabilities: `audit_trace.admin_anomaly_review.*`.
- Required deterministic support: trace query, redaction, actor/context filters, baseline thresholds from Governance/Policy where available.
- Human result: open User Admin, Governance/Policy, or Audit/Trace surfaces for decisions.

### Support-access investigation

- Trigger: support/SaaS-owner access is requested or used where that authority model exists.
- Purpose: review support-access rationale, approvals, scope, actions taken, and follow-up needs.
- Justification: consequential investigation requiring trace evidence and human acceptance.
- Capabilities: `audit_trace.support_access_review.*`.
- Required deterministic support: support authority separation, approval trace reads, redaction.
- Human result: approve/deny/close investigation through governance or support-access capabilities.

### Policy-drift trace summary

- Trigger: repeated policy denials, exceptions, or behavior-boundary changes.
- Purpose: summarize whether observed trace patterns suggest policy thresholds or guidance need review.
- Justification: cross-workstream evidence summary feeding Governance/Policy.
- Capabilities: `audit_trace.policy_drift_summary.*`.
- Required deterministic support: policy decision traces, exception records, redaction, capability mapping.
- Human result: open Governance/Policy proposal or exception review surfaces.

## Governance/Policy worker candidates

### Policy-change impact analysis

- Trigger: proposal submitted, authority expansion detected, threshold changed, or approver requests deeper evidence.
- Purpose: analyze affected capabilities, users, agents, tool boundaries, prior denials/allows, exceptions, and likely operational impact.
- Justification: durable task with evidence gathering, progress, provider/model work, and human review before decision.
- Capabilities: `governance_policy.impact_analysis.start`, `governance_policy.impact_analysis.read`, `governance_policy.impact_analysis.cancel`, `governance_policy.impact_analysis.accept_result`.
- Required deterministic support: proposal state, capability inventory, simulation inputs, trace evidence, tenant filtering, idempotent result attachment.
- Human result: approver uses analysis in approve/reject/activate/rollback decisions.

### Exception clustering worker

- Trigger: repeated exceptions or denials for similar capabilities, roles, agents, or contexts.
- Purpose: group exceptions and propose whether policy, training, access, or UX should be reviewed.
- Justification: batch analysis with reviewable recommendations.
- Capabilities: `governance_policy.exception_cluster_review.*`.
- Required deterministic support: exception records, denial traces, redaction, grouping inputs.
- Human result: draft policy proposal, close cluster, or assign human follow-up.

### Replay/evaluation batch worker

- Trigger: proposal needs evidence against historical trace cases or approved evaluation scenarios.
- Purpose: run bounded replay/evaluation cases and summarize expected behavior changes.
- Justification: long-running model/provider work with progress, failures, result evidence, and human review.
- Capabilities: `governance_policy.replay_eval.start`, `governance_policy.replay_eval.read`, `governance_policy.replay_eval.cancel`, `governance_policy.replay_eval.accept_result`.
- Required deterministic support: case selection, provider boundary, scoring normalization, redaction, result trace storage.
- Human result: attach findings to policy or behavior proposal.

### Approval-summary drafting worker

- Trigger: proposal ready for review with many evidence links.
- Purpose: draft a concise decision brief with risk, impact, alternatives, confidence, and recommended questions for the human approver.
- Justification: useful when evidence volume is high and brief becomes durable decision support.
- Capabilities: `governance_policy.approval_summary.*`.
- Required deterministic support: proposal/evidence reads, redaction, decision-card generation.
- Human result: approver may use, edit, or reject summary; final decision remains human-authorized.

### Stale exception review worker

- Trigger: exceptions near expiry, stale open exceptions, or recurring manual overrides.
- Purpose: recommend close, renew, escalate, or convert-to-policy-review actions.
- Justification: scheduled lifecycle task with human decision.
- Capabilities: `governance_policy.stale_exception_review.*`.
- Required deterministic support: exception lifecycle rules, notification scheduling, trace evidence.
- Human result: authorized user decides each exception action.

## First-wave prioritization guidance

Prioritize worker implementation only after the deterministic capability and structured surface path exists for the corresponding workstream. Suggested first candidates for SMB value:

1. User Admin access review investigation is the completed first vertical at starter/reference scope; use it as the example contract for later workers, not as permission to skip governed capabilities or runtime validation.
2. Agent Admin prompt-risk review, because behavior changes are central to AI-first SaaS safety and exercise tool-boundary/prompt/reference traces.
3. Audit/Trace scheduled audit summary, because it makes trace substrate visible and useful across all workstreams.
4. Governance/Policy policy-change impact analysis, because approval decisions need evidence and human authority boundaries.
5. My Account personal attention digest only after cross-workstream attention projections are reliable.

## Validation checklist for worker child tasks

Before marking any AutonomousAgent/internal worker slice complete, confirm:

- [ ] a governed start/read/cancel/result/accept-or-reject capability set exists;
- [ ] deterministic services enforce authorization, tenant/customer filtering, validation, lifecycle, redaction, idempotency, and policy gates;
- [ ] missing provider/model configuration fails closed with actionable system-message surfaces and traces;
- [ ] worker progress/result surfaces are typed, accessible, visually distinct from request/response chat, and linked from the relevant dashboard attention state;
- [ ] human decisions execute through governed capabilities, not raw worker output;
- [ ] durable task lifecycle, prompt/skill/reference/model/tool traces, denials, and result decisions are recorded;
- [ ] tests and local runtime/API/UI validation prove the normal path or explicitly record bounded provider-smoke skip behavior.
