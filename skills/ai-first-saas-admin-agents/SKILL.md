---
name: ai-first-saas-admin-agents
description: Make AI-assisted user, membership, invitation, support-access, and admin-audit offload mandatory in generated AI-first SaaS foundations, with bounded agents, approval gates, decision cards, and Akka routing.
---

# AI-First SaaS Admin Agents

Use this focused foundation skill whenever a generated SaaS app includes the mandatory core administration baseline from `core-saas-foundation` and `akka-basic-user-admin`.

AI-assisted admin offload is mandatory foundation behavior for initial generated AI-first SaaS apps. It must reduce human admin toil while keeping high-risk access changes under explicit human supervision and backend authorization.

## Required reading

Read first:
- `../../docs/ai-first-saas-application-architecture.md`
- `../../docs/core-ai-first-saas-foundation.md`
- `../../docs/core-saas-identity-tenancy-admin.md`
- `../core-saas-foundation/SKILL.md`
- `../ai-first-saas-agent-team-design/SKILL.md`
- `../ai-first-saas-decision-cards/SKILL.md`
- `../ai-first-saas-policy-governance/SKILL.md`
- `../akka-agents/SKILL.md`
- `../akka-agent-tools/SKILL.md`
- `../akka-agent-orchestration/SKILL.md`
- `../akka-agent-testing/SKILL.md`

Then load component skills for the chosen substrate: `akka-workflows`, `akka-views`, `akka-http-endpoints`, and `akka-web-ui-apps` for admin queues, decision cards, and UI surfaces.

## Mandatory admin offload agents

Initial generated SaaS foundations must include these bounded agent responsibilities or an explicitly equivalent design:

| Agent | Single responsibility | Typical output |
|---|---|---|
| `AccessReviewAgent` | Identify stale invitations, dormant access, orphaned admin coverage, risky role combinations, and overdue review items from scoped admin views. | Access-review recommendations and low-risk cleanup tasks. |
| `AdminRiskAgent` | Score proposed admin actions such as role grants, support-access changes, tenant suspension, identity relink, and bulk operations. | Risk summary, policy triggers, confidence, alternatives, and required approval level. |
| `InvitationDraftAgent` | Draft invite messages, onboarding notes, role rationale, and bulk invite preparation from authorized admin intent. | Draft invitation package, never raw token exposure. |
| `RoleRecommendationAgent` | Recommend roles/capabilities for invited or existing users based on scoped job context and policy. | Role recommendation with evidence and least-privilege explanation. |
| `SupportAccessReviewAgent` | Review support-access grants, expiry, purpose, usage, and revocation candidates. | Support-access risk findings and expiry/revoke recommendations. |
| `AdminAuditSummaryAgent` | Summarize admin audit/search results for supervisors and auditors. | Audit summary with actor, target, scope, policy, decision-card, and trace links. |
| `AdminPolicyProposalAgent` | Optional: draft policy, threshold, prompt, or permission-change proposals from repeated admin corrections. | Draft proposal only; activation requires governed human commit. |

## Autonomous actions allowed

Agents may act autonomously only inside caller authority and explicit backend-enforced capabilities. Allowed autonomous work:

- draft invites, onboarding copy, and role rationale;
- summarize risk, audit events, and access-review findings;
- recommend roles/capabilities using least-privilege policy;
- identify stale/dormant access, expired support access, duplicate or failed invitations, and last-admin risk;
- prepare bulk invite drafts without sending or activating them unless policy grants a narrow approval-free path;
- create low-risk admin tasks such as “review stale invite” or “confirm support-access expiry”;
- create decision cards for risky admin recommendations;
- generate audit summaries and digest entries.

## Actions requiring approval

Agents must not autonomously perform these actions unless an explicit product policy defines a narrow safe boundary and the backend authorization layer enforces it. Default generated foundations require human approval for:

- grant admin roles or expand roles/capabilities;
- remove, suspend, or disable the last admin for a Tenant or Customer;
- expand, extend, or create broad support access;
- suspend tenants or materially affect subscription/service availability;
- bulk disable users, bulk revoke memberships, or bulk send high-impact invitations;
- reset or relink identity subjects;
- change policy, permissions, approval thresholds, prompts, or agent authority;
- access tenant/customer data outside authorized admin-tool scope;
- bypass invitation lifecycle, membership status, role/scope, tenant/customer boundary, or audit requirements.

## Decision-card routing

High-risk admin recommendations must become decision cards instead of silent side effects.

Triggers include:
- admin role grants or removals;
- last admin risk;
- bulk invite or bulk disable operations;
- support-access expansion or unusual usage;
- tenant suspension or billing-impacting admin changes;
- identity relink/reset;
- unusual cross-level memberships;
- low-confidence or policy-bound role recommendations.

Decision cards must include:
- recommended action and requested authority;
- actor, target user, tenant/customer scope, and affected organization;
- evidence from UserDirectoryView, MembershipView, InvitationView, AdminAuditView, and AccessReviewQueueView;
- policy triggers, risk, confidence, impact, and alternatives;
- approve, deny, modify, defer, escalate, or request-evidence actions;
- audit and work-trace links.

## Tool and data boundaries

Admin agents may use only scoped tools that enforce authorization before data access or side effects.

Required tool rules:
- accept or resolve `AuthContext` and selected membership context;
- filter by authorized SaaS Owner, Tenant, or Customer scope;
- redact fields outside caller authority;
- emit audit/work-trace records for protected data access, recommendations, denials, and decision-card creation;
- fail closed for missing AuthContext, disabled users, forbidden scopes, cross-tenant/customer access, or insufficient capabilities;
- never expose raw invitation tokens, provider secrets, private WorkOS configuration, or unredacted tenant/customer data to an unauthorized agent call.

## Akka substrate routing

Use:
- `akka-agents`, `akka-agent-tools`, and `akka-agent-structured-responses` for bounded recommendation, draft, risk, and summary agents;
- `akka-agent-orchestration` plus `akka-workflows` for durable admin review flows, retries, approval gates, and multi-agent coordination;
- `akka-views` and `akka-view-query-patterns` for UserDirectoryView, MembershipView, InvitationView, AdminAuditView, and AccessReviewQueueView inputs;
- `ai-first-saas-decision-cards` for high-risk admin approvals;
- `ai-first-saas-policy-governance` for policy clauses, thresholds, and proposal/commit flows;
- `akka-http-endpoints` for protected admin-agent APIs;
- `akka-web-ui-apps` for access-review, decision-card, audit-summary, and admin-digest surfaces;
- `akka-agent-testing` with deterministic model providers for all agent behavior.

## Required tests

Include tests for:
- deterministic AccessReviewAgent, AdminRiskAgent, InvitationDraftAgent, RoleRecommendationAgent, SupportAccessReviewAgent, and AdminAuditSummaryAgent outputs;
- forbidden cross-tenant/customer tool access;
- disabled caller and missing-capability denials;
- last admin and bulk action recommendations becoming decision cards, not side effects;
- support-access expansion requiring approval;
- raw invitation tokens and secrets never appearing in agent output;
- audit/work-trace facts for protected tool reads, recommendations, decision cards, approvals, denials, and failures;
- AdminPolicyProposalAgent producing proposals only, with human-governed commits.

## Done criteria

A generated SaaS foundation is incomplete if it has user administration but no AI-assisted admin offload. Before domain-specific features are considered generation-ready, verify:
- all mandatory admin agents or equivalent bounded responsibilities are planned;
- autonomous actions and approval-required actions are explicit;
- high-risk recommendations route to decision cards;
- agent tools enforce AuthContext, scope, redaction, and audit mechanically;
- tests cover recommendations, denials, approval gates, audit traces, and forbidden autonomous changes.
