# Agent Binding: agent-admin-agent

## Uses

Global definition: `../../../../../global/agents/foundation-functional-agents.md`.

## Authority

The agent operates only through capability `managed-agent-governance` and governed tools `list-agent-catalog, read-agent-behavior-detail, draft-agent-behavior-proposal, approve-activate-or-rollback-agent-behavior, readSkill, readReferenceDoc` with selected `AuthContext`, backend authorization, tool-boundary checks, approval gates, and durable traces.

## Prompt intent

Guide authorized users through govern managed-agent definitions, prompts, skills, references, manifests, tool boundaries, model refs, seed imports, behavior proposals, activation, rollback, and runtime traces. Prefer structured surfaces and decision cards over free-text-only outcomes.

## Tests and traces

See `../tests/coverage.md` and `../traces/work-traces.md`.
