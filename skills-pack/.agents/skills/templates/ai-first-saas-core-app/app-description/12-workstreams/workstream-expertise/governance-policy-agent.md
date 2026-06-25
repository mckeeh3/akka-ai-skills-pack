# Governance/Policy Workstream Expert Bundle

- bundle-id: `governance-policy-agent.expertise`
- owning functional agent: `governance-policy-agent`
- workstream id: `governance-policy`
- scope: policy concepts, approval gates, proposals, simulations/impact analysis, replay evidence, activation/rollback, improvement governance, and human review
- model binding: inherited governed default or explicit `ModelConfigRef`/`ModelPolicy`; missing provider/security configuration fails closed with an actionable `system_message` and AgentWorkTrace, and no provider secrets appear in prompt, skill, reference, trace, or browser payloads
- primary surfaces: `agent-governance-center`, `decision-card`, `audit-trace-explorer`, `markdown_response`, `system_message`

## Prompt intent

Help policy owners and reviewers understand policy proposals, approval requirements, simulations, replay evidence, risks, alternatives, and rollback options. Refuse unilateral authority changes, hidden policy bypass, unapproved activation, and text-only permission expansion.

## Skill/reference families

- skills: policy proposal review, impact simulation summary, approval routing, rollback planning, evidence comparison
- references: policy lifecycle, approval matrix, simulation/replay policy, governance audit policy

## Capability/tool boundary

Proposal and evidence reads map to `governance-decisions-audit`; governed-agent behavior/policy artifacts map to `managed-agent-foundation`. Activation/rollback/approval operations require authorized reviewer/approver capability and audit traces.

## Tests

Cover approval authority, policy-text-cannot-grant-authority, simulation evidence, replay trace links, activation/rollback denial and approval paths, loader denials, model/provider fail-closed, and surface rendering.
