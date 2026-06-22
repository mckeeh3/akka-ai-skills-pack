# Global Policies: Foundation security and governance

Reusable policies referenced by core starter workstream bindings.

- `backend-authorization-default-deny`: every protected API, view, stream, workflow action, consumer side effect, timer action, and agent tool requires backend authorization.
- `tenant-customer-isolation`: records and queries are scoped by authorized tenant/customer context; cross-scope access is denied and traced.
- `organization-tenant-language-boundary`: browser surfaces, DTOs, and user-facing copy use Organization for customer-facing administration while backend state, audit partitions, and authorization enforce Tenant isolation. Organization administration alone never authorizes tenant/customer application-data access or support access.
- `provider-fail-closed`: WorkOS/AuthKit, Resend, and model provider dependencies fail closed with actionable errors and no secret exposure when configuration is missing; missing model provider configuration is a serious runtime issue for active default managed agents and model-backed work.
- `frontend-secret-boundary`: browser assets and API payloads never expose server/provider secrets or hidden authority state.
- `governed-agent-authority`: managed agents derive authority from active `AgentDefinition`, manifests, tool boundaries, roles/capabilities, and approval policies, not prompt text.
- `approval-for-high-impact-change`: risky role, support-access, identity, behavior, policy, model, or tool-boundary changes route to human approval/decision cards; high-risk policy changes require two approvers when multiple eligible approvers are available, while the same authorized human may draft and approve unless stricter scope policy applies.
- `redaction-and-export-governance`: audit, trace, digest, and export surfaces apply permissioned redaction and policy gates; audit/work trace retention defaults to one year and is configurable by app/SaaS-owner, Organization, and Customer scope; approved exports are redacted-only and include only retained/available evidence.
- `foundation-email-notification-boundary`: foundation email notifications are invitation-only; other foundation attention and approval notifications are in-app unless a later accepted intent change adds email contracts.
- `billing-boundary-non-authority`: billing records, billing-boundary capabilities, subscription state, payment-provider metadata, or SaaS Owner billing operations may inform platform operations but never grant application-data, support-access, or hidden tenant/customer authority without an explicit backend-authorized selected context.

These policies reference skills-pack foundation doctrine instead of duplicating it.
