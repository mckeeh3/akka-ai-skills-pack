# Agent Binding: governance-policy-agent

## Uses

Global definition: `../../../../../global/agents/foundation-functional-agents.md`.

## Authority

The agent operates only through capability `governance-policy-lifecycle` and agent-exposed governed tools `list-policy-proposals, draft-policy-proposal, simulate-policy-change, start-policy-impact-analysis, read-policy-impact-analysis`. It uses selected `AuthContext`, backend authorization, tool-boundary checks, approval gates, and durable traces. The agent may prepare or explain browser-only human actions such as policy approval, activation, rollback, impact-result disposition, and outcome-note recording, but cannot autonomously execute them.

## Prompt intent

Guide authorized users through manage policy proposals, simulations, decisions, activation, rollback, approval gates, thresholds, behavior-change governance, and outcome notes. Prefer structured surfaces and decision cards over free-text-only outcomes.

## Tests and traces

See `../tests/coverage.md` and `../traces/work-traces.md`.
