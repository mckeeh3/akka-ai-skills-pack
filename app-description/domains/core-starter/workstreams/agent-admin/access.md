# Access: Agent Admin

## Authorized roles

Only SaaS Owner/Admin users with explicit platform `agent_admin.*` or equivalent managed-agent-governance / `agent-doc-administration` capability may access Agent Admin. SaaS app owners operate in a reserved `saas-app-owner` tenant scope for app-wide behavior overrides.

## Scope rules

Agent Admin applies to generated managed agents in the app. Agents initially use globally scoped app-description-generated behavior profiles. When an authorized tenant scope changes an agent's model config reference, prompt version, skill/reference manifest, generated-tool assignment, or tool-boundary policy reference, the system creates a tenant-scoped behavior-profile clone/version for that tenant; SaaS app owners use the reserved `saas-app-owner` tenant scope.

Authorized SaaS admins may:

- list/read `AgentDefinition` catalog and detail summaries;
- inspect safe placement, lifecycle, steward, authority, `ModelConfigRef`, model-policy, `AgentSkillManifest`, `AgentReferenceManifest`, generated-tool, and `ToolPermissionBoundary` summaries;
- view full `PromptDocument`, `SkillDocument`, and `ReferenceDocument` content for their authorized Agent Admin scope;
- create/update/deprecate governed skill and reference documents through proposal flows;
- assign/unassign skills, references, manifests, model config references, and generated-tool/tool-boundary choices through behavior-profile proposals;
- run safe test-console scenarios only for authorized tenant-scoped agents and modes;
- approve, reject, activate, route, or cancel proposals according to risk/authority policy;
- inspect runtime profile, prompt, skill, reference, tool-boundary, provider fail-closed, and test-console traces.

## Denials

Disabled users, inactive users, missing SaaS admin authority, tenant/organization admins without explicit platform Agent Admin capability, customer admins, tenant employees, customer users, auditors without Agent Admin capability, and unauthenticated callers are denied server-side. Disabled/archived agents, inactive model configs, missing provider configuration, stale proposals, deleted/deprecated docs, cross-scope documents, unassigned loader ids, unassigned generated tools, missing `ToolPermissionBoundary` grants, and unsupported authority expansion are denied before model-visible content or side effects.

Denials return safe system-message or partial-failure surfaces without granting partial Agent Admin access or enumerating hidden agents/documents. Protected trace/admin surfaces may show denial categories; model-visible and ordinary browser copy must not leak cross-tenant existence, provider secrets, hidden prompt text, or privileged policy internals.

## Data visibility and secret boundaries

Authorized SaaS admins can see unredacted prompt, skill, and reference document text in Agent Admin for their authorized scope. Browser/API payloads never expose provider secrets, WorkOS/Resend secrets, raw model credentials, hidden platform instructions, generated tool implementation internals beyond safe summaries, arbitrary backend class names, or unapproved tool-boundary internals.

Trace surfaces show runtime read metadata and link back to authorized current/historical docs where appropriate; they do not echo full loaded skill/reference bodies by default. Test-console outputs are labeled test mode, redact provider/model internals, and fail closed when provider/runtime configuration is unavailable or unauthorized.
