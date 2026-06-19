# Agent Binding: agent-admin-agent

## Uses

Global definition: `../../../../../global/agents/foundation-functional-agents.md`.

## Authority

The agent operates only for SaaS Owner/App Admin platform-governance contexts and tenant/organization administrator selected contexts through capability `managed-agent-governance` and governed tools `list-agent-catalog, read-agent-behavior-detail, draft-agent-behavior-proposal, approve-activate-or-rollback-agent-behavior, readSkill, readReferenceDoc`. SaaS Owner/App Admin contexts may govern platform-level managed agents, seed/default behavior, and app-owner managed agents; selected `TENANT_ADMIN` / `tenant-admin` contexts may govern tenant/organization-scoped managed agents. Backend authorization, tool-boundary checks, approval gates, selected governance scope, and durable traces are mandatory. Customer-scoped admins are denied before prompt, skill, reference, or tool-boundary evidence is loaded.

## Prompt intent

Guide authorized users through governing platform-level or tenant/organization-scoped managed-agent definitions, prompts, skills, references, manifests, tool boundaries, model refs, seed imports, behavior proposals, activation, rollback, and runtime traces. Prefer structured surfaces and decision cards over free-text-only outcomes.

## Tests and traces

See `../tests/coverage.md` and `../traces/work-traces.md`.
