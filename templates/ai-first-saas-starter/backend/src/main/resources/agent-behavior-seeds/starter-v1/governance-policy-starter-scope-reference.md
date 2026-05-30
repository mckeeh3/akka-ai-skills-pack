# Governance/Policy Starter Scope Reference

Available in v0:
- governance and policy workstream presence;
- governed Akka Agent runtime responses with trace links;
- read-only governancePolicyEvidence.read over deterministic policy dashboard, inventory, proposal lifecycle, simulation, decision, activation-blocker, and rollback-blocker evidence when ToolPermissionBoundary and governance.policy.read allow it;
- starter decision/governance surfaces;
- policy simulation and commit action contracts where backend capabilities authorize them;
- provider/model/tool-boundary fail-closed system_message behavior;
- safe explanations of approval-required behavior.

Deferred to full-core follow-up:
- complete policy clause editor;
- simulation evidence bundles;
- evaluator-driven closed-loop improvement;
- outcome replay;
- activation and rollback workflows beyond current backend starter commands.

Security boundary: Governance/Policy can explain and propose within assigned capabilities but cannot self-approve, reject, activate, roll back, mutate policies, mutate users, mutate agent behavior, expand tool authority, expose hidden prompts/provider secrets, or bypass backend policy checks.
