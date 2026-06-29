---
id: RV-GOVPOL-001
title: Governance policy proposal reaches a decision card and records approval evidence
workstream: governance-policy
surface: governance-policy-decision-card
persona: saas-admin
environment: local-dev
dataSetup:
  - base-organization
authMode: workos-test-users
executionMode: human-manual
executionStatus: authored-not-run
readinessClaim: not-run
---

# Purpose

Validate the Governance/Policy proposal and decision-card path, including human approval behavior, policy scope, simulation/impact expectations where available, idempotent decision handling, and policy/audit trace evidence. If the runtime app cannot expose a decision-card path, the run should classify the gap explicitly rather than treating the scenario as passed.

# Prerequisites

- Start the app using `environments/local-dev.md`.
- Prepare `data-setups/base-organization.md`.
- Log in as a `personas/saas-admin.md` user with policy authority, or record the exact authority gap.
- Identify a safe non-production policy proposal for the base organization or platform scope.
- Record model provider state if impact analysis is expected; missing provider configuration may be a valid fail-closed blocker for model-backed impact analysis only.

# Runtime path

`policy operator -> Governance/Policy proposal/simulation/decision surface -> surface_action or protected workstream API -> governance-policy-lifecycle governed capability -> policy repository/entity/service plus optional impact-analysis agent -> decision-card/result surface and policy/audit/work trace evidence`

# Surface, adapter, and governed-tool contract

- Surface graph node: Governance/Policy draft/proposal/simulation/decision-card.
- Action edge: create or select proposal, review decision card, approve/reject/record decision.
- Actor adapter/source: browser `surface_action`; human chat plans for policy decisions require explicit confirmation and must not auto-approve.
- Governed tool scope: policy lifecycle tools within declared tenant/platform scope.
- Approval behavior: authority expansion, activation, rollback, or exception paths require human decision evidence.
- Transaction/idempotency: repeated approval/rejection of the same policy version should return already-decided or conflict-safe results.

# Setup

The base setup provides the administrator identity and tenant/platform context. Scenario setup may prepare a draft policy proposal, but the decision-card review and approval/rejection action must occur during validation.

# Human UI validation script

1. Open the local frontend URL and log in as `saas.admin@example.com` or the configured policy operator.
2. Navigate to Governance/Policy and locate or create the safe policy proposal.
3. Open the proposal decision card.
4. Review visible scope, impact, risk, rationale, affected capability, and approval requirements.
5. Approve or reject according to the run plan and record the result surface.
6. Repeat the same decision action and record idempotency/conflict behavior.
7. Attempt to perform the decision as `member@example.com` or an unprivileged persona and record denial.

# Expected results

- A policy proposal reaches a decision-card surface with scope, rationale, evidence, risk/impact, and permitted actions.
- The authorized decision records reviewer, decision, timestamp, scope, and result status.
- Model-backed impact analysis, if unavailable due to missing provider configuration, fails closed and does not block non-model policy denial evidence from being classified.
- Repeated decisions do not create duplicate activations or contradictory policy state.
- Unprivileged personas cannot approve, activate, roll back, or view protected policy data.
- Policy/audit/work traces link proposal, decision, reviewer, actor adapter, and result.

# Evidence to capture

- Proposal/policy id and version.
- Decision-card screenshot or DOM observations with sensitive content redacted.
- Network/API statuses for decision, repeated decision, and denied decision attempts.
- Audit/work trace ids for proposal review, decision, idempotency/conflict, and denial.
- Provider/fail-closed message if impact analysis is provider-backed and unconfigured.

# Failure classification hints

- `implementation gap` if no runtime decision-card path exists for accepted current intent.
- `app-description gap` if expected policy authority, scope, or decision semantics are ambiguous.
- `provider/config blocker` for model-backed impact analysis that requires unavailable provider config.
- `auth/setup gap` for missing policy-operator identity or authority mapping.
- `UX/state gap` for unclear decision-card evidence, risk, or result status.
