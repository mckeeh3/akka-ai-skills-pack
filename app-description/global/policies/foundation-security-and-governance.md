# Global Policies: Foundation security and governance

Reusable policies referenced by core starter workstream bindings.

- `backend-authorization-default-deny`: every protected API, view, stream, workflow action, consumer side effect, timer action, and agent tool requires backend authorization.
- `tenant-customer-isolation`: records and queries are scoped by authorized tenant/customer context; cross-scope access is denied and traced.
- `provider-fail-closed`: WorkOS/AuthKit, Resend, and model provider dependencies fail closed with actionable errors and no secret exposure when configuration is missing.
- `frontend-secret-boundary`: browser assets and API payloads never expose server/provider secrets or hidden authority state.
- `governed-agent-authority`: managed agents derive authority from active `AgentDefinition`, manifests, tool boundaries, roles/capabilities, and approval policies, not prompt text.
- `approval-for-high-impact-change`: risky role, support-access, identity, behavior, policy, model, or tool-boundary changes route to human approval/decision cards.
- `redaction-and-export-governance`: audit, trace, digest, and export surfaces apply permissioned redaction and policy gates.

These policies reference skills-pack foundation doctrine instead of duplicating it.
