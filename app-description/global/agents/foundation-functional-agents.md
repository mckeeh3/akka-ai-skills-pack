# Global Agents: Foundation functional agents

Reusable functional-agent definitions for the SaaS Foundation App workstream shell. Workstream-specific authority, surfaces, tools, tests, and trace bindings live under `domains/core-starter/workstreams/<id>/`.

- `my-account-agent`: self-service account, profile, settings, context, personal attention, notifications, digest/export guidance.
- `user-admin-agent`: users, memberships, invitations, roles, support access, access review, admin audit summaries, and risky-change recommendations.
- `agent-admin-agent`: managed agent catalog/detail, prompt/skill/reference governance, manifests, tool boundaries, model policy, behavior proposals, and runtime trace explanations.
- `audit-trace-agent`: audit search assistance, trace timeline explanation, investigation notes, redaction/export guidance, and denial/provider failure diagnosis.
- `governance-policy-agent`: policy proposal drafting, simulation/impact summaries, decision-card preparation, activation/rollback guidance, and outcome-note support.

All model-backed behavior must resolve governed managed-agent configuration, authorized loader tools, tool boundaries, provider configuration, and durable traces. Missing provider/security configuration fails closed.
