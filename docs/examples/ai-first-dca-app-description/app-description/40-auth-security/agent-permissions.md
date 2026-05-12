# Agent Permissions

## Principle

Agents are managed workers, not security authorities. The acting agent, workflow, or prompt may recommend work, but mechanical authorization must be enforced by code, policy state, workflow gates, and human approval where required.

## Effective principals

Implementation should record an effective principal for material agent activity:

- human delegator or supervisor when work is launched;
- agent/component identity for tool calls and recommendations;
- workflow id and goal id for durable execution context;
- policy version and clause ids used for authorization decisions;
- tenant/customer/device scope for all DCA actions.

## Tool/data access

- Read tools must be scoped to the tenant/customer/device context of the active goal or workflow.
- Sensitive reads must create data-access trace facts.
- Write/external-impact tools require a structured proposal, policy evaluation, and workflow gate before execution.
- High-risk actions such as spending, changing permissions, deleting data, activating policy changes, or expanding agent authority require human approval unless a future policy explicitly defines a narrow autonomous boundary.

## Action-boundary judge pattern

For non-trivial side effects, the seed app should plan for an action-boundary validation step with outcomes:

- `allow`;
- `block`;
- `revise`;
- `escalate`.

The proposal should include intended action, target resource, evidence, policy basis, risk/confidence/impact, reversibility, sensitive data involvement, and uncertainties.

## Supplies autopilot application

- Supply forecast agents may recommend and explain.
- Workflows and policy gates decide auto-ship, approval-required, suppression, or escalation paths.
- Human reviewers approve high-cost, abnormal, ambiguous, offboarding, unmapped-contract, or policy-bound shipments.
- Agent recommendations must not bypass `SupplyAutopilotWorkflow` or `SupplyDecisionEntity` authority gates.

## Governed policy and prompt changes

Agents may draft policy, guardrail, prompt, skill, evaluator, or threshold proposals.

Activation rules:

- simulation or replay is required for impactful changes;
- `POLICY_OWNER` or equivalent human role must commit changes;
- approval/commit traces must cite proposal, simulation result, policy clauses affected, and outcome expectations;
- rollbacks must be recorded.
