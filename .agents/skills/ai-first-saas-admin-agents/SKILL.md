---
name: ai-first-saas-admin-agents
description: Make AI-assisted user, membership, invitation, support-access, and admin-audit offload mandatory in generated AI-first SaaS foundations, with bounded agents, approval gates, decision cards, and Akka routing.
---

# AI-First SaaS Admin Agents

Use this focused foundation skill whenever a generated SaaS app includes the mandatory core administration baseline from `core-saas-foundation` and `akka-basic-user-admin`.

AI-assisted admin offload is mandatory foundation behavior for initial generated AI-first SaaS apps. It must reduce human admin toil while keeping high-risk access changes under explicit human supervision and backend authorization.

## Lifecycle classification

- Phase role: Interview-phase foundation admin worker modeling with Build/compile handoff constraints for admin governed tools, decisions, surfaces, and traces.
- Graph layer: admin human workers, functional-agent workers, internal/evaluator agent workers, system workers, execution harnesses, actor adapters, governed tools, capabilities, decision cards, and audit traces.
- Canonical chain: `worker → execution harness → actor adapter → governed tool → capability → Akka implementation`.

## Required reading

Read first:
- `../docs/intent-compiler.md`
- `../docs/current-intent-model.md`
- `../docs/intent-to-realization-flow.md`
- `../docs/app-development-lifecycle.md`
- `../docs/app-worker-tool-model.md`
- `../docs/app-description-component-graph.md`
- `../docs/app-description-to-code-compile-contract.md`
- `../docs/workforce-decomposition.md`
- `../docs/agent-workstream-application-architecture.md`
- `../docs/structured-surface-contracts.md`
- `../docs/capability-first-backend-architecture.md`
- `../docs/ai-first-saas-application-architecture.md`
- `../docs/core-ai-first-saas-foundation.md`
- `../docs/core-saas-identity-tenancy-admin.md`
- `../core-saas-foundation/SKILL.md`
- `../ai-first-saas-agent-team-design/SKILL.md`
- `../ai-first-saas-decision-cards/SKILL.md`
- `../ai-first-saas-policy-governance/SKILL.md`
- `../akka-agents/SKILL.md`
- `../akka-agent-tools/SKILL.md`
- `../akka-agent-orchestration/SKILL.md`
- `../akka-agent-testing/SKILL.md`

Then load component skills for the chosen substrate: `akka-workflows`, `akka-views`, `akka-http-endpoints`, and `akka-web-ui-apps` for admin queues, decision cards, and UI surfaces.

## Mandatory admin offload responsibilities

Initial generated SaaS foundations must include these bounded admin offload responsibilities or an explicitly equivalent design. This is a requirement for capability coverage, not a requirement to create one physical agent class per row.

Use the one-agent vs agent-team decision guide in `ai-first-saas-agent-team-design` before choosing physical agent classes. A small foundation may implement a single governed `UserAdminAgent` with an active `AgentDefinition` and an `AgentSkillManifest` that assigns focused skills such as `access-review`, `admin-risk-scoring`, `invitation-drafting`, `role-recommendation`, `support-access-review`, and `audit-summary` when those responsibilities share authority, tool boundary, model config, lifecycle, steward, memory, audit, and approval needs. Larger foundations may implement separate specialized agents such as `AccessReviewAgent` or `AdminRiskAgent` when separate lifecycle, model, prompt, tool boundary, steward, memory, risk, approval, audit, or scaling concerns justify it.

| Responsibility | Typical specialized agent name | Typical output |
|---|---|---|
| Access review | `AccessReviewAgent` | Access-review recommendations and low-risk cleanup tasks for stale invitations, dormant access, orphaned admin coverage, risky role combinations, and overdue review items from scoped admin views. |
| Admin risk scoring | `AdminRiskAgent` | Risk summary, policy triggers, confidence, alternatives, and required approval level for role grants, support-access changes, tenant suspension, identity relink, and bulk operations. |
| Invitation drafting | `InvitationDraftAgent` | Draft invitation package, onboarding notes, role rationale, and bulk invite preparation from authorized admin intent; never raw token exposure. |
| Role recommendation | `RoleRecommendationAgent` | Least-privilege role/capability recommendation with evidence and policy rationale for invited or existing users. |
| Support-access review | `SupportAccessReviewAgent` | Support-access risk findings and expiry/revoke recommendations for grants, expiry, purpose, usage, and revocation candidates. |
| Admin audit summary | `AdminAuditSummaryAgent` | Audit summary with actor, target, scope, policy, decision-card, and trace links for supervisors and auditors. |
| Admin policy proposal drafting | `AdminPolicyProposalAgent` when enabled | Draft policy, threshold, prompt, or permission-change proposal only; activation requires governed human commit. |

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

## Worker, actor-adapter, governed-tool, and data boundaries

Admin agent workers may use only scoped governed tools through explicit actor adapters such as `agent_tool_call`, `workflow_step`, `surface_action`, or confirmed `human_chat_tool_plan`. Each adapter must enforce authorization before data access or side effects and must not inherit human surface authority implicitly.

Required governed-tool rules:
- accept or resolve `AuthContext` and selected membership context;
- filter by authorized SaaS Owner, Tenant, or Customer scope;
- redact fields outside caller authority;
- emit audit/work-trace records for protected data access, recommendations, denials, and decision-card creation;
- fail closed for missing AuthContext, disabled users, forbidden scopes, cross-tenant/customer access, or insufficient capabilities;
- never expose raw invitation tokens, provider secrets, private WorkOS configuration, or unredacted tenant/customer data to an unauthorized agent call.

## Workstream handoff requirements

For generated full-stack SaaS work, admin-agent planning must hand off implementation-ready workstream contracts before component selection:
- owning functional-agent workstream or reusable placement, normally User Admin, My Account, Agent Admin, Support Access, Audit/Trace, and Governance/Policy for admin-risk or policy-proposal flows;
- worker roster for each flow: human admin/reviewer, functional-agent worker, internal/evaluator admin agent worker, workflow/timer/consumer system worker where applicable;
- execution harnesses and actor adapters for each operation, such as `surface_action`, `human_chat_tool_plan`, `agent_tool_call`, `workflow_step`, or `internal_call`;
- structured surface id/type for each user-facing result, such as access-review queue, invitation draft, role recommendation card, support-access review, admin audit summary, decision card, or admin digest;
- surface action and chat/agent adapter list mapped to governed-tool ids and capability ids/classes, including draft invite, recommend role, create decision card, approve/deny risky change, request evidence, revoke support access, or open audit trace;
- `AuthContext`, tenant/customer scope, role/capability rules, approval gates for risky side effects, audit/work-trace fields, redaction rules, and denial behavior;
- downstream Akka, frontend, realtime, agent, and test skills needed for `agent_tool_call` adapters, workflows, views, protected endpoints, structured surface rendering, and tenant-isolation/security checks.

## Akka substrate routing

Use:
- `akka-agents`, `akka-agent-tools`, and `akka-agent-structured-responses` for bounded recommendation, draft, risk, and summary responsibilities, whether implemented as one governed `UserAdminAgent` with multiple assigned skills or as separate specialized agents;
- `akka-agent-orchestration` plus `akka-workflows` for durable admin review flows, retries, approval gates, and multi-agent coordination;
- `akka-views` and `akka-view-query-patterns` for UserDirectoryView, MembershipView, InvitationView, AdminAuditView, and AccessReviewQueueView inputs;
- `ai-first-saas-decision-cards` for high-risk admin approvals;
- `ai-first-saas-policy-governance` for policy clauses, thresholds, and proposal/commit flows;
- `akka-http-endpoints` for protected admin-agent APIs;
- `akka-web-ui-apps` for access-review, decision-card, audit-summary, and admin-digest surfaces;
- `akka-agent-testing` with deterministic model providers for all agent behavior.

## Required tests

Include tests for:
- deterministic outputs for each required admin offload responsibility, either through one `UserAdminAgent` with assigned skills or through specialized AccessReviewAgent, AdminRiskAgent, InvitationDraftAgent, RoleRecommendationAgent, SupportAccessReviewAgent, and AdminAuditSummaryAgent classes;
- forbidden cross-tenant/customer tool access;
- disabled caller and missing-capability denials;
- last admin and bulk action recommendations becoming decision cards, not side effects;
- support-access expansion requiring approval;
- raw invitation tokens and secrets never appearing in agent output;
- audit/work-trace facts for protected tool reads, recommendations, decision cards, approvals, denials, and failures;
- AdminPolicyProposalAgent producing proposals only, with human-governed commits.

## Done criteria

A generated SaaS foundation is incomplete if it has user administration but no AI-assisted admin offload. Before domain-specific features are considered generation-ready, verify:
- all mandatory admin offload responsibilities are planned and mapped either to one governed `UserAdminAgent` with an `AgentSkillManifest` or to equivalent bounded specialized agents using the generalized one-agent vs agent-team decision guide;
- autonomous actions and approval-required actions are explicit;
- high-risk recommendations route to decision cards;
- `agent_tool_call` adapters and the governed tools behind them enforce AuthContext, scope, redaction, and audit mechanically;
- tests cover recommendations, denials, approval gates, audit traces, and forbidden autonomous changes.
