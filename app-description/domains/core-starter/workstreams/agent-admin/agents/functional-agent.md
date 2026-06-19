# Agent Binding: agent-admin-agent

## Uses

Global definition: `../../../../../global/agents/foundation-functional-agents.md`.

## Authority

The agent operates only for SaaS Owner/App Admin platform-governance contexts and tenant/organization administrator selected contexts through capability `managed-agent-governance` and governed tools `list-agent-catalog, read-agent-behavior-detail, draft-agent-behavior-proposal, approve-activate-or-rollback-agent-behavior, readSkill, readReferenceDoc`. SaaS Owner/App Admin contexts may govern platform-level managed agents, seed/default behavior, and app-owner managed agents; selected `TENANT_ADMIN` / `tenant-admin` contexts may govern tenant/organization-scoped managed agents. Backend authorization, tool-boundary checks, approval gates, selected governance scope, and durable traces are mandatory. Customer-scoped admins are denied before prompt, skill, reference, or tool-boundary evidence is loaded.

## Model and expertise binding

LLM-backed turns use inherited governed default model binding unless a tenant-approved override is activated for `agent-admin-agent`:

- `ModelConfigRef`: `foundation-agent-admin-default-model`.
- `ModelPolicy`: `foundation-agent-admin-model-policy`.
- No implicit fallback; approved fallback requires policy and trace.
- Provider secrets never appear in prompts, manifests, traces, browser payloads, or responses.

Prompt assembly includes only compact governed expertise manifest entries. Full skill/reference text loads only through authorized `readSkill(skillId)` or `readReferenceDoc(referenceId)` after active-agent assignment, governance scope, status/version, token/redaction, and `ToolPermissionBoundary` checks. Expertise can explain governance and draft proposals but cannot grant tools, data scope, model access, or approval authority.

Assigned procedural skill intents:

- `aa.agent-catalog-triage.v1` — catalog lifecycle, steward/owner, readiness, and attention triage.
- `aa.behavior-proposal-drafting.v1` — prompt/skill/reference/manifest/tool-boundary proposal drafting with diff and risk evidence.
- `aa.prompt-risk-review.v1` — prompt-risk review interpretation, provider/runtime blockers, and decision-card preparation.
- `aa.tool-boundary-review.v1` — least-authority tool-boundary analysis and authority-expansion denial rationale.
- `aa.activation-rollback-guidance.v1` — activation prerequisites, rollback readiness, freshness, and trace review.
- `aa.runtime-trace-explanation.v1` — explain model/tool/load traces without raw prompts, provider payloads, or hidden scope leakage.

Assigned reference intents:

- `aa.managed-agent-lifecycle-policy.v1`.
- `aa.prompt-governance-policy.v1`.
- `aa.skill-reference-manifest-guide.v1`.
- `aa.tool-permission-boundary-guide.v1`.
- `aa.model-policy-guide.v1`.
- `aa.seed-import-provenance-guide.v1`.

## Prompt intent

Guide authorized users through governing platform-level or tenant/organization-scoped managed-agent definitions, prompts, skills, references, manifests, tool boundaries, model refs, seed imports, behavior proposals, activation, rollback, and runtime traces. Prefer structured surfaces and decision cards over free-text-only outcomes.

## Required denials and recovery

The agent must safely recover from Customer-scoped actors, missing platform/tenant governance context, missing `agent_admin.*` capability, inactive or superseded behavior artifacts, prompt-only authority expansion, unapproved tool/model/data scope expansion, missing model/provider config, missing tool-boundary grant, denied skill/reference loads, raw secret/provider/prompt/model-response requests, stale proposal/version conflicts, and unauthorized activation or rollback. Safe recovery names the visible denial category, selected governance scope if safe, suggested non-sensitive next step, and trace/correlation id when available.

## Tests and traces

See `../tests/coverage.md` and `../traces/work-traces.md`.
