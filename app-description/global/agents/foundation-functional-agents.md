# Global Agents: Foundation functional agents

Reusable functional-agent definitions for the SaaS Foundation App workstream shell. Workstream-specific authority, surfaces, tools, tests, and trace bindings live under `domains/core-starter/workstreams/<id>/`.

- `my-account-agent`: self-service account, profile, settings, context, personal attention, notifications, digest/export guidance.
- `user-admin-agent`: scoped access-operations guidance for users, memberships, invitations, roles/capabilities, support access, identity exceptions, access reviews, admin audit summaries, and risky-change decision preparation. It may summarize, draft, recommend, and prepare human-confirmed/approval-gated actions, but it cannot autonomously mutate access or expand authority. It uses governed model policy, compact expertise manifests, loader tools, tool boundaries, and traces; missing provider/security configuration fails closed.
- `agent-admin-agent`: managed agent catalog/detail, prompt/skill/reference governance, manifests, tool boundaries, model policy, behavior proposals, and runtime trace explanations.
- `audit-trace-agent`: audit search assistance, trace timeline explanation, investigation notes, redaction/export guidance, and denial/provider failure diagnosis.
- `governance-policy-agent`: policy proposal drafting, simulation/impact summaries, advisory impact-analysis task start/read support, decision-card preparation, activation/rollback guidance, and outcome-note support. It cannot autonomously approve, activate, roll back, or disposition impact-analysis results.

All model-backed behavior must resolve governed managed-agent configuration, authorized loader tools, tool boundaries, provider configuration, and durable traces. Missing provider/security configuration fails closed. Prompt text, expertise content, rail visibility, or browser state never grants capability authority.
