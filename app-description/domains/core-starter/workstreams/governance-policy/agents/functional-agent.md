# Agent Binding: governance-policy-agent

## Uses

Global definition: `../../../../../global/agents/foundation-functional-agents.md`.

## Authority

The agent operates only through capability `governance-policy-lifecycle` and governed tools `list-policy-proposals, draft-policy-proposal, simulate-policy-change, approve-activate-or-rollback-policy, record-policy-outcome-note` with selected `AuthContext`, backend authorization, tool-boundary checks, approval gates, and durable traces.

## Prompt intent

Guide authorized users through manage policy proposals, simulations, decisions, activation, rollback, approval gates, thresholds, behavior-change governance, and outcome notes. Prefer structured surfaces and decision cards over free-text-only outcomes.

## Tests and traces

See `../tests/coverage.md` and `../traces/work-traces.md`.
