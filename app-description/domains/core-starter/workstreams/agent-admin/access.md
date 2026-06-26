# Access: Agent Admin

## Authorized roles

Only SaaS Owner/Admin users with explicit platform `agent_admin.*` or equivalent `agent-doc-administration` capability may access Agent Admin. SaaS app owners operate in a reserved `saas-app-owner` tenant scope for Agent Admin behavior overrides.

## Scope rules

Agent Admin applies to all generated agents in the app. Agents initially use globally scoped app-description-generated behavior profiles. When a tenant changes an agent's model config reference, prompt, skill assignments, or allowed generated tool list, the system creates a tenant-scoped behavior-profile clone/version for that tenant; SaaS app owners use the reserved `saas-app-owner` tenant. SaaS admins may view full prompt, skill, and reference document content for all agents in their authorized Agent Admin scope; inspect safe `AgentDefinition`, placement, lifecycle, steward, authority, `ModelConfigRef`, `AgentSkillManifest`, `AgentReferenceManifest`, generated tool list, and `ToolPermissionBoundary` summaries; update model config references; create/update/deprecate/remove tenant-scoped skills and references through governed proposal flows; assign/unassign skills and generated tools through behavior-profile versions; propose and activate permitted prompt/skill/reference/profile versions; restore historical versions through a new proposal/activation; and inspect Agent Admin runtime read traces.

## Denials

Disabled users, inactive users, missing SaaS admin authority, tenant/organization admins without explicit platform Agent Admin capability, customer admins, tenant employees, customer users, auditors without Agent Admin capability, and unauthenticated callers are denied server-side. Denials return safe system-message feedback and trace evidence without granting partial Agent Admin access or enumerating hidden agents/documents.

## Data visibility

Authorized SaaS admins see unredacted prompt, skill, and reference document text in Agent Admin for their authorized scope. Browser/API payloads never expose provider secrets, WorkOS/Resend secrets, raw model credentials, hidden platform instructions, generated tool implementation internals beyond safe summaries, or unapproved tool-boundary internals. Trace surfaces do not show the full skill/reference content that was read at runtime; they show read metadata and link back to the current doc where appropriate.
