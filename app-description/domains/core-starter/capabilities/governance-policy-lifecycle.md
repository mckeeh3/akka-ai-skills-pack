# Capability: Governance policy lifecycle

## Purpose

Let authorized policy owners and approvers propose, simulate, review, decide, activate, roll back, and measure governance policies, approval gates, thresholds, and behavior-change rules for the core starter.

## Actors and scope

- Policy owner / approver: manages policy proposals and decisions.
- Auditor: reads policy, decision, and activation trace evidence.
- Governance/Policy functional agent: drafts proposals, summarizes impact, prepares decision cards, and explains outcomes under bounded authority.

## Governed tools and exposure

- `list-policy-proposals` (`browser-tool`, `agent-tool` read): scoped proposal queue and statuses.
- `draft-policy-proposal` (`browser-tool`, `agent-tool` proposal): creates proposal with rationale, impact, risk, affected capabilities, and suggested tests.
- `simulate-policy-change` (`browser-tool`, `agent-tool`, `internal-tool`): produces evidence and affected-scope summary without activation.
- `approve-activate-or-rollback-policy` (`browser-tool` approval): human-governed commit/rollback with audit.
- `record-policy-outcome-note` (`browser-tool`): links results and feedback to policy decisions.

## Authorization and denials

Policy commits, approval gates, thresholds, and authority expansions require backend-enforced roles/capabilities and approval records. Agents may recommend or draft but not autonomously weaken security, expand authority, or activate high-impact changes.

## Outcomes

In scope: proposal lifecycle, simulation/evidence bundles, decision cards, activation/rollback traceability, and outcome notes.

Out of scope: autonomous policy commits by prompt, app-specific business policy not tied to foundation behavior, and hidden threshold changes.

## Linked graph nodes

- Workstream: `../workstreams/governance-policy/workstream.md`
- Tests: `../workstreams/governance-policy/tests/coverage.md`
- Traces: `../workstreams/governance-policy/traces/work-traces.md`
