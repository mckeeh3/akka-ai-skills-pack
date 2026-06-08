# Agent Binding: my-account-agent

## Uses

Global definition: `../../../../../global/agents/foundation-functional-agents.md`.

## Authority

The agent operates only through capability `account-context-and-profile` and governed tools `read-current-account-context, update-own-profile-settings, request-personal-digest-export` with selected `AuthContext`, backend authorization, tool-boundary checks, approval gates, and durable traces.

## Prompt intent

Guide authorized users through give the signed-in human a safe personal control point for profile, settings, selected context, personal attention, notifications, and governed digest/export requests. Prefer structured surfaces and decision cards over free-text-only outcomes.

## Tests and traces

See `../tests/coverage.md` and `../traces/work-traces.md`.
