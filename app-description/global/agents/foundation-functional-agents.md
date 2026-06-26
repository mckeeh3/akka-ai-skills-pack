# Global Agents: Foundation functional agents

Reusable functional-agent definitions for the SaaS Foundation App workstream shell. Workstream-specific authority, surfaces, tools, tests, and trace bindings live under `domains/core-starter/workstreams/<id>/`.

- `my-account-agent`: self-service account, profile, settings, context, personal attention, notifications, digest/export guidance.
- `user-admin-agent`: scoped access-operations guidance for users, memberships, invitations, roles/capabilities, support access, identity exceptions, access reviews, admin audit summaries, and risky-change decision preparation. It may summarize, draft, recommend, and prepare human-confirmed/approval-gated actions, but it cannot autonomously mutate access or expand authority. It uses governed model policy, compact expertise manifests, loader tools, tool boundaries, and traces; missing provider/security configuration fails closed.
- `agent-admin-agent`: SaaS-admin-only AI-assisted editing of all agents' prompts, skills, and skill reference docs, including agent/doc browsing, Markdown-preserving proposed edits, version history, restore, skill/reference lifecycle, and runtime skill/reference read trace explanations.
- `audit-trace-agent`: Audit/Trace workstream navigation and safe explanation assistance. V1 does not grant this agent trace-search, trace-detail, payload-read, export, investigation-note, summary, or mutation tool authority; tenant-admin audit evidence access is through backend-authorized browser surfaces.
- `governance-policy-agent`: simple policy search, effective-policy explanation, SaaS default and tenant override guidance, reset-to-default preparation, policy history summaries, runtime decision trace explanation, and hard-platform-security boundary denial. It cannot autonomously mutate policies, override hard platform controls, or expand authority.

All model-backed behavior must resolve governed managed-agent configuration, authorized loader tools, tool boundaries, provider configuration, and durable traces. Missing provider/security configuration fails closed. Prompt text, expertise content, rail visibility, or browser state never grants capability authority.
