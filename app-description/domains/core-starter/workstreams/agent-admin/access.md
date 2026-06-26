# Access: Agent Admin

## Authorized roles

Only SaaS Owner/Admin users with explicit platform `agent_admin.*` or equivalent `agent-doc-administration` capability may access Agent Admin.

## Scope rules

Agent Admin is platform-wide and applies to all agents in the app. Tenant/organization/customer-scoped Agent Admin is out of scope for this starter. SaaS admins may view full prompt, skill, and reference document content for all agents; inspect safe `AgentDefinition`, placement, lifecycle, steward, authority, `ModelConfigRef`, `AgentSkillManifest`, `AgentReferenceManifest`, and `ToolPermissionBoundary` summaries; edit agent names and purposes; create/update/deprecate/delete skills and references through governed proposal flows; propose and activate permitted prompt/skill/reference versions; restore historical versions through a new proposal/activation; and inspect Agent Admin runtime read traces.

## Denials

Disabled users, inactive users, missing SaaS admin authority, tenant/organization admins without explicit platform Agent Admin capability, customer admins, tenant employees, customer users, auditors without Agent Admin capability, and unauthenticated callers are denied server-side. Denials return safe system-message feedback and trace evidence without granting partial Agent Admin access or enumerating hidden agents/documents.

## Data visibility

Authorized SaaS admins see unredacted prompt, skill, and reference document text in Agent Admin. Browser/API payloads never expose provider secrets, WorkOS/Resend secrets, raw model credentials, hidden platform instructions, or unapproved tool-boundary internals. Trace surfaces do not show the full skill/reference content that was read at runtime; they show read metadata and link back to the current doc where appropriate.
