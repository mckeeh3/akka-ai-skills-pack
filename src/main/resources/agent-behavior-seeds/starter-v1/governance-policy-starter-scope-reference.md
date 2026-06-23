# Governance/Policy Starter Scope Reference

Available in starter scope:
- governance and policy workstream presence;
- governed Akka Agent runtime responses with trace links;
- read-only governancePolicyEvidence.read over deterministic policy dashboard, inventory, proposal lifecycle, simulation, decision, activation-blocker, and rollback-blocker evidence when ToolPermissionBoundary and governance.policy.read allow it;
- starter decision/governance surfaces;
- dashboard, inventory, detail, proposal, simulation, decision, outcome, impact-analysis, activation/rollback blocked, and system-message recovery surfaces;
- policy simulation and commit action contracts where backend capabilities authorize them;
- provider/model/tool-boundary fail-closed system_message behavior;
- safe explanations of approval-required behavior.

Deferred to full-core follow-up:
- complete policy clause editor;
- simulation evidence bundles;
- evaluator-driven closed-loop improvement;
- outcome replay;
- activation and rollback workflows beyond current backend starter commands.

Surface routing note: routed Governance/Policy surfaces are navigation, review, and draft aids. A model response or prefilled surface cannot approve, reject, activate, roll back, commit, run simulations, record outcomes, or start impact analysis without the protected backend action and required approval state.

Security boundary: Governance/Policy can explain and propose within assigned capabilities but cannot self-approve, reject, activate, roll back, mutate policies, mutate users, mutate agent behavior, expand tool authority, expose hidden prompts/provider secrets, or bypass backend policy checks.

Confirmed chat tool plan reference: Governance/Policy now has a representative chat plan path for `action-governance-policy-draft-proposal` / `governance.policy.propose` / `schema.governance-policy.proposal.draft.v1`. Confirmed execution after exact plan snapshot confirmation creates only an inert draft proposal; approvals, activation, rollback, simulations, exports, and live authority changes remain separate governed actions.
