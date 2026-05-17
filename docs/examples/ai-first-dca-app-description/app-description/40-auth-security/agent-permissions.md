# Agent Permissions

## Principle

Agents are managed workers and recommendation engines, not security authorities. Agent prompts, tool descriptions, and workflow labels may explain limits, but Akka backend authorization, policy state, workflow gates, and human approvals enforce those limits.

This file extends `../10-capabilities/01-secure-tenant-user-foundation.md` for DCA agent/tool activity.

## Effective principals

Material agent activity must record an effective principal that includes:

- delegating human account and selected `AuthContext` when work is launched by a human;
- agent/component identity for tool calls, recommendations, summaries, and drafts;
- workflow id, goal id, task id, and correlation/causation ids for durable execution context;
- tenant/customer/device scope for all DCA reads and actions;
- permission/capability checked, policy version, and policy clause ids used for authorization decisions;
- approval/decision-card link when a side effect or high-risk recommendation is gated.

Agents do not inherit all authority from a supervisor by default. Each tool call must resolve a bounded tool principal and allowed scope.

## Tool and data access rules

- Agent tools are exposure surfaces for backend capabilities, not the capability roots.
- Read/evidence tools must be scoped to the active goal, workflow, tenant, customer, and device context.
- Sensitive reads create data-access trace facts and return redacted evidence suitable for the tool's purpose.
- Draft/recommend tools may prepare invitations, role recommendations, policy proposals, support-access reviews, supply recommendations, audit summaries, and decision cards without committing high-impact changes.
- Write or external-impact tools require named permission/capability grants, policy evaluation, idempotency keys where relevant, audit/work trace, and workflow/decision-card approval when required.
- High-risk actions such as spending, supply shipment, changing permissions, deleting data, activating policies, expanding agent authority, granting support-access, billing overrides, or exposing tenant/customer data require human approval unless a future accepted policy defines a narrow autonomous boundary.
- Denied, blocked, revised, or escalated tool/action proposals emit work/decision trace facts with policy clause ids and reason categories.

## Admin-assistant agent boundaries

Foundation admin offload agents may:

- draft invitations and onboarding copy;
- recommend least-privilege roles;
- summarize access-review queues and admin audit results;
- identify stale access, dormant admins, expired support access, and risky memberships;
- prepare support-access reviews and admin decision cards;
- draft policy proposals when policy-governance drafting is enabled.

They must not autonomously:

- grant admin roles or widen membership scope;
- remove the last admin;
- grant, extend, or use support access without the required human/tenant authority;
- disable tenants or bulk-disable users;
- change permission, policy, prompt, skill, guardrail, evaluator, or threshold activation state;
- access tenant/customer data outside authorized tool scope.

## Action-boundary judge pattern

For non-trivial side effects, the DCA reference should model an action-boundary validation step with outcomes:

- `allow`;
- `block`;
- `revise`;
- `escalate`.

The proposal should include intended action, target resource, actor/tool principal, selected `AuthContext`, evidence, policy basis, risk/confidence/impact, reversibility, sensitive-data involvement, idempotency key where needed, and uncertainties.

## Supplies autopilot application

- Supply forecast agents may read scoped evidence, recommend, explain, and prepare decision cards.
- `SupplyAutopilotWorkflow`, policy gates, and `SupplyDecisionEntity` authority decide auto-ship, approval-required, suppression, or escalation paths.
- Human reviewers approve high-cost, abnormal, ambiguous, offboarding, unmapped-contract, policy-bound, or low-confidence shipments.
- Agent recommendations must not bypass workflow/entity authorization gates, tenant/customer/device scope, spend thresholds, supplier-integration boundaries, or audit requirements.
- Agent/tool permissions must be enforceable outside prompts and visible in audit/work/decision traces.

## Governed policy and prompt changes

Agents may draft policy, guardrail, prompt, skill, evaluator, or threshold proposals.

Activation rules:

- simulation or replay is required for impactful changes;
- `POLICY_OWNER` or equivalent human role must commit changes within tenant/customer scope;
- approval/commit traces must cite proposal, simulation result, policy clauses affected, expected outcome impact, approver, and rollback plan;
- rollbacks and rejected proposals must be recorded;
- expanded agent tool authority is a governed permission change, not a prompt edit.

## Audit requirements

Agent/tool activity must create durable trace facts for allowed calls, denied calls, sensitive data access, recommendations, decision-card creation, policy invocation, approval outcome, external side effects, and failures. Trace entries must include redaction markers and must not store raw JWTs, invite tokens, provider secrets, or unnecessary customer/device payloads.
