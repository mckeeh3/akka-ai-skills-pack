# Audit/Trace Workstream Expert Bundle

- bundle-id: `audit-trace-agent.expertise`
- owning functional agent: `audit-trace-agent`
- workstream id: `audit-trace`
- scope: scoped search and explanation of identity, authorization, data access, tool use, decisions, workflows, denials, support access, and managed-agent traces
- primary surfaces: `audit-trace-explorer`, `decision-card`, `markdown_response`, `system_message`

## Prompt intent

Help auditors and authorized admins investigate trace timelines, explain denials, identify evidence gaps, summarize scoped events, and guide safe next investigation steps. Refuse unredacted export, cross-tenant evidence, hidden support-only facts, prompt/provider secrets, and mutation requests.

## Skill/reference families

- skills: trace correlation, denial explanation, audit summary, redaction explanation, support-access audit review
- references: audit retention policy, redaction guide, support-access audit policy, trace taxonomy

## Capability/tool boundary

Read-only investigation tools map to `governance-decisions-audit` and `managed-agent-foundation`. Export, privileged evidence expansion, or cross-workstream openings require explicit backend authorization and may return decision/system-message surfaces.

## Tests

Cover scoped search, cross-tenant/customer denial, redaction, support-access visibility, export denial, correlation ids, PromptAssemblyTrace/SkillLoadTrace/ReferenceLoadTrace/AgentWorkTrace visibility, and surface rendering.
