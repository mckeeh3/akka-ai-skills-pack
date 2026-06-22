# Capability: Governance policy lifecycle

## Purpose

Let authorized policy owners and approvers propose, simulate, review, decide, activate, roll back, and measure governance policies, approval gates, thresholds, and behavior-change rules for the core starter.

## Actors and scope

- Policy owner / approver: manages policy proposals and decisions.
- Auditor: reads policy, decision, and activation trace evidence.
- Governance/Policy functional agent: drafts proposals, summarizes impact, prepares decision cards, and explains outcomes under bounded authority.

## Governed tools and exposure

- `list-policy-proposals` (`browser-tool`, `agent-tool` read): scoped proposal queue, policy detail, proposal detail, lifecycle status, attention queues, and browser-safe trace links.
- `draft-policy-proposal` (`browser-tool`, `agent-tool` proposal): creates or updates an inert draft and can submit it for review with rationale, impact, risk, affected capabilities, rollback notes, and suggested tests.
- `simulate-policy-change` (`browser-tool`, `agent-tool`, `internal-tool` evidence): produces synchronous advisory evidence and affected-scope summary without activation.
- `approve-activate-or-rollback-policy` (`browser-tool` approval/commit): executes one explicit command mode, `decide`, `activate`, or `rollback`; every mode requires human/backend authorization, approval policy, idempotency, and audit.
- `record-policy-outcome-note` (`browser-tool` outcome): links observations, metrics, and feedback to an existing policy decision without changing authority.
- `start-policy-impact-analysis` (`browser-tool`, `agent-tool` proposal/workflow): starts a durable advisory impact-analysis task for a proposal; it never activates or mutates policy authority.
- `read-policy-impact-analysis` (`browser-tool`, `agent-tool` read): reads scoped task progress, provider/runtime blockers, advisory findings, and result disposition.
- `cancel-policy-impact-analysis` (`browser-tool` command): cancels a queued/running task when authorized; proposal state remains unchanged.
- `accept-policy-impact-result` (`browser-tool` decision): records human acceptance of advisory evidence for later decision use; activation remains separate.
- `reject-policy-impact-result` (`browser-tool` decision): records rejection of advisory evidence with a required reason; activation remains blocked until valid evidence exists.
- `request-policy-impact-changes` (`browser-tool` decision): records requested changes with a required reason and may make a new impact-analysis task eligible.

## Authorization and denials

Policy commits, approval gates, thresholds, and authority expansions require backend-enforced roles/capabilities and approval records. The same authorized human may draft and approve a policy change unless a specific policy adds stricter separation of duties. High-risk changes require two approvers when multiple eligible approvers are available in the selected scope; if only one eligible approver exists, the single-approver path is allowed but must record that no second approver was available. High-risk categories include role/capability expansion, support-access policy changes, agent tool-boundary changes, model/provider configuration changes, audit export policy changes, and other authority-expanding or security-impacting governance changes. Agents may recommend or draft but not autonomously weaken security, expand authority, or activate high-impact changes.

## Capability contract

Inputs for all side-effecting tools include selected `AuthContext`, actor id, proposal or task id when applicable, idempotency key, correlation id, reason/rationale where required, and command-specific payload. Browser payloads never carry tenant/customer authority as trusted input; backend-selected context is authoritative.

Outputs are typed surfaces or safe `system-message` responses with status, validation failures, allowed/disabled actions, redaction metadata, and trace refs. Raw prompts, provider secrets, hidden authority state, JWTs, raw tool payloads, and cross-tenant evidence are never returned.

Proposal lifecycle states are `draft`, `submitted`, `simulation-required`, `in-review`, `changes-requested`, `approved`, `rejected`, `activated`, `rollback-candidate`, `rolled-back`, and `superseded`. Legal transitions are:

- create/update draft: none or `draft` -> `draft`.
- submit proposal: `draft` or `changes-requested` -> `submitted` or `simulation-required` when evidence is missing.
- simulate/impact analysis: preserves proposal state and adds advisory evidence; enough evidence may move `simulation-required` -> `in-review`.
- decide approve: `submitted` or `in-review` -> `approved` when required evidence, reviewer authority, and approval policy pass.
- decide reject: `submitted`, `simulation-required`, `in-review`, or `changes-requested` -> `rejected` with reason.
- request changes: `submitted` or `in-review` -> `changes-requested` with reason.
- activate: `approved` -> `activated` only when activation prerequisites, rollback metadata, freshness checks, and backend authority pass.
- mark rollback candidate: `activated` -> `rollback-candidate` when outcome evidence or authorized human review indicates rollback should be considered.
- rollback: `activated` or `rollback-candidate` -> `rolled-back` only with rollback metadata and approval authority.
- supersede: any non-terminal proposal except `activated` -> `superseded` when a replacement proposal is linked.

Repeated side-effecting commands with the same idempotency key return the existing result and do not duplicate proposals, tasks, decisions, activations, rollback events, outcome notes, traces, notifications, or attention items. Stale version, missing prerequisite, validation, forbidden, provider/runtime, and conflict outcomes return structured blockers; they do not render as success.

Impact-analysis task states are `queued`, `running`, `blocked_provider_or_runtime`, `completed-review-required`, `cancelled`, `failed`, `accepted`, `rejected_result`, and `request_changes`. Task results are advisory evidence only. Accepting a result may satisfy an evidence prerequisite for a later approval decision but never approves, activates, rolls back, weakens security, or expands authority.

## Outcomes

In scope: proposal lifecycle, simulation/evidence bundles, durable advisory impact-analysis tasks, decision cards, activation/rollback traceability, and outcome notes.

Out of scope: autonomous policy commits by prompt, app-specific business policy not tied to foundation behavior, hidden threshold changes, and treating impact-analysis acceptance as policy approval or activation.

## Linked graph nodes

- Workstream: `../workstreams/governance-policy/workstream.md`
- Tests: `../workstreams/governance-policy/tests/coverage.md`
- Traces: `../workstreams/governance-policy/traces/work-traces.md`
