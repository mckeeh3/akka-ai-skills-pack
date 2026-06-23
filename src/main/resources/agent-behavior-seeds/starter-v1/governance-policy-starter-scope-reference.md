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

Confirmed chat tool plan reference: Governance/Policy now has an expanded chat plan catalog covering three classification categories:

- Chat-executable-now read (scoped, no hidden enumeration, no mutation): `action-governance-policy-list` / `list-policy-proposals` / `governance.policy.read`; `action-governance-policy-read` / `governance.policy.read`. Returns scoped policy inventory or detail only; cannot activate, roll back, or change any policy.

- Chat-proposal-only (inert proposals/simulations/advisory outputs, no authority, activation/rollback text denied): `action-governance-policy-draft-proposal` / `draft-policy-proposal` / `governance.policy.propose` / `schema.governance-policy.proposal.draft.v1`; `action-governance-policy-submit-proposal`; `action-governance-policy-simulate` / `simulate-policy-change` / `governance.policy.simulate`; `action-governance-policy-read-impact-analysis` / `governance.policy.impact_analysis.read`. Confirmed execution creates inert proposals/simulations only; approvals, activation, rollback, simulations commit, exports, and live authority changes remain separate governed actions.

- Approval-gated: `action-governance-policy-start-impact-analysis` / `start-policy-impact-analysis` / `governance.policy.impact_analysis.start`. Requires a separate approval-gated completion; snapshot confirmation alone is insufficient.

Routed Governance/Policy surfaces remain navigation, review, and draft aids. A protected backend action is required for any proposal, decision, activation, or rollback.
