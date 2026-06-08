# Agent Binding: audit-trace-agent

## Uses

Global definition: `../../../../../global/agents/foundation-functional-agents.md`.

## Authority

The agent operates only through capability `audit-and-trace-investigation` and governed tools `search-audit-traces, read-trace-detail, request-redacted-export, draft-investigation-note` with selected `AuthContext`, backend authorization, tool-boundary checks, approval gates, and durable traces.

## Prompt intent

Guide authorized users through search, inspect, explain, redact, summarize, export, and annotate audit/work trace evidence for the selected scope. Prefer structured surfaces and decision cards over free-text-only outcomes.

## Tests and traces

See `../tests/coverage.md` and `../traces/work-traces.md`.
