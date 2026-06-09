# Core AI-first SaaS foundation

Use this document set when designing or implementing the common backend foundation for generated AI-first SaaS applications. These features are product-agnostic: they exist before the app-specific business domain is added.

Companion docs:
- `./core-saas-identity-tenancy-admin.md` — actors, tenant/customer organization model, WorkOS identity, account administration, data isolation, support-access pattern.
- `./core-saas-owner-tenant-billing.md` — SaaS Owner to Tenant subscription management and billing boundaries.

## Canonical vocabulary

| Term | Meaning |
|---|---|
| SaaS Owner | The platform operator that sells and operates the SaaS product. |
| Tenant | The SaaS user organization that subscribes to the platform and uses it to serve its own customers. This is the canonical internal/architectural term for “SaaS user.” |
| Customer | An organization served by a Tenant through app-specific online services. Customers always start as organizations in the SaaS Foundation App model. |
| Account | A local Akka-owned authorization record linked to a WorkOS-authenticated human identity. |
| User Profile | Human-facing account information and scoped profile attributes shown in the application, separate from authentication and authorization facts. |
| User Settings | User-controlled preferences that affect application experience, such as UI appearance, without changing authorization. |
| Membership | A scoped relationship between an account and a SaaS Owner, Tenant, or Customer organization. |
| Role | A permission bundle within a scope. Canonical foundation roles are `SAAS_OWNER_ADMIN`, `TENANT_ADMIN`, `TENANT_EMPLOYEE`, `CUSTOMER_ADMIN`, `CUSTOMER_USER`, and `AUDITOR` (or an equivalent scoped auditor capability). App-specific roles extend these roles through permissions/capabilities; they do not replace the foundation role model. |

## Three-level operating model

```text
SaaS Owner
  sells subscriptions to Tenants
  manages Tenant admin accounts and Tenant billing
  has no access to Tenant application data

Tenant
  owns its application data
  provides services to Customer organizations
  manages Tenant employee accounts
  creates and manages Customer admin accounts

Customer
  consumes services provided by a Tenant
  manages its own Customer user accounts
```

## Administration hierarchy

```text
SaaS Owner Admin
  -> creates and maintains Tenant records
  -> creates and maintains initial Tenant Admin accounts
  -> manages Tenant subscription and billing state

Tenant Admin
  -> manages Tenant employee accounts
  -> creates and maintains Customer organizations
  -> creates and maintains Customer Admin accounts
  -> manages Tenant-owned application data and services

Customer Admin
  -> manages Customer user accounts within the Customer organization
  -> supervises Customer-side use of Tenant-provided services
```

The same human/email may have separate local accounts or memberships at multiple levels. For example, a person may be a SaaS Owner Admin for platform operations, a Tenant Admin in a demo tenant, and a Customer Admin in a test customer organization. Authorization must be based on the selected account/membership context, not email alone.

## Non-negotiable boundaries

- SaaS Owner users do not have platform-level access to Tenant application data.
- Tenant data belongs to the Tenant.
- Customer data belongs within a Tenant-owned Customer organization boundary.
- All backend commands and queries must enforce scope mechanically.
- Frontend navigation is only UX; backend authorization is authoritative.
- WorkOS authenticates humans; Akka-owned state authorizes business actions.
- Profiles and settings are user-experience state, not authorization state; they may be scoped, but they must not grant permissions.
- Billing in the core foundation covers only SaaS Owner → Tenant subscriptions.
- Tenant → Customer billing is app-specific and must be modeled by the generated business application when needed.

## AI-first expectations for the core foundation

The SaaS Foundation App should validate the broader AI-first architecture by using core administrative work as real AI-first product behavior, not only as CRUD.

Core AI-first features must include:

- durable audit traces for identity, role, scope, tenant, customer, and billing administration;
- policy-controlled administrative actions, such as role assignment, tenant suspension, and billing-plan changes, using the canonical foundation roles `SAAS_OWNER_ADMIN`, `TENANT_ADMIN`, `TENANT_EMPLOYEE`, `CUSTOMER_ADMIN`, `CUSTOMER_USER`, and `AUDITOR` as the baseline;
- mandatory governed runtime agent foundation: `AgentDefinition`, `PromptDocument`/`PromptVersion`, `SkillDocument`/`SkillVersion`, `AgentSkillManifest`, `ToolPermissionBoundary`, `PromptAssemblyTrace`, `SkillLoadTrace`, and `AgentWorkTrace` for all foundation agents;
- mandatory implementation-developed default agent behavior records created through governed setup on first install or tenant bootstrap as initial approved/active `AgentDefinition`, `PromptDocument`/`PromptVersion`, `SkillDocument`/`SkillVersion`, `AgentSkillManifest`, and `ToolPermissionBoundary` records, with checksums, provenance, idempotency, audit, and upgrade behavior that preserves tenant customizations;
- mandatory AI-assisted admin offload responsibilities: access review, admin risk scoring, invitation drafting, role recommendations, support-access review, admin audit summaries, and policy proposal drafting when the product allows policy/permission/threshold proposals; implement bounded user-facing explanations and immediate workstream turns through request-based agents, and implement durable internal/background offload such as access-review investigations, risk-review batches, audit-summary generation, evaluation/replay loops, monitoring/remediation, and specialist follow-up as Akka `AutonomousAgent` tasks when typed lifecycle, dependencies, notifications, or model-driven iteration are needed; keep either one governed `UserAdminAgent` with focused skills in its `AgentSkillManifest` or specialized governed agent responsibilities such as AccessReviewAgent, AdminRiskAgent, InvitationDraftAgent, RoleRecommendationAgent, SupportAccessReviewAgent, AdminAuditSummaryAgent, and AdminPolicyProposalAgent;
- decision cards for risky or high-impact administration changes;
- anomaly and risk signals for suspicious account, access, or billing events;
- human-supervised recommendations rather than autonomous high-impact changes by default;
- outcome metrics for onboarding, tenant health, support-access usage, access-review completion, and billing workflow quality.

Example AI-first core scenarios:

| Scenario | AI-first behavior |
|---|---|
| Tenant onboarding | Assistant drafts setup checklist, missing configuration summary, and next-best action recommendations. |
| Access review | UserAdminAgent with an `access-review` skill, or a specialized AccessReviewAgent, flags stale admins, excessive roles, unusual cross-level memberships, dormant customer admins, and last-admin risks. |
| Billing issue | Agent summarizes subscription state, payment issue, service impact, prior notices, and recommended action. |
| Tenant-created support account | Agent verifies purpose, expiry, permissions, and audit readiness before activation. |
| Role change | Decision card shows requested role, scope, risk, policy triggers, affected data boundary, and approver actions. |

## Suggested Akka substrate

| Concern | Preferred Akka substrate |
|---|---|
| Tenant, Customer, Account, Membership lifecycle | Event Sourced Entity when audit-grade history is required; Key Value Entity for low-risk current state only. |
| User profiles and settings | Key Value Entity for current editable profile/settings state; Event Sourced Entity if the app requires audit-grade profile or preference history. |
| Subscription lifecycle | Event Sourced Entity for plan changes, trial, activation, suspension, cancellation, and billing state history. |
| Invitations and onboarding | Workflow for mandatory email invite delivery, token/acceptance context, link/activate, resend, revoke/cancel, expiry, idempotency, and multi-step onboarding. |
| Access review and support-access expiry | Timed Actions plus Views and Consumers. |
| Administrative dashboards | Views for scoped lists, queues, tenant health, subscription state, and audit search. |
| AI recommendations and summaries | Mandatory admin offload responsibilities managed through active governed managed-agent `AgentDefinition` records, governed prompt/skill versions, per-agent `AgentSkillManifest` compact entries, `ToolPermissionBoundary`, authorized Akka `@FunctionTool` `readSkill(skillId)`, and read-only access unless explicitly approved. Use request-based Akka `Agent` for bounded user-facing workstream turns and one-shot summaries; use Akka `AutonomousAgent` for durable internal/background reviews, evaluation/replay loops, monitoring/remediation, specialist investigations, task dependencies, notification-backed progress, delegation, or handoff. A single `UserAdminAgent` may carry multiple approved admin skills; `AgentAdminAgent` and other managed agents have their own distinct manifests; separate specialized agents remain available when useful. Default foundation prompts, skills, manifests, tool boundaries, and agent definitions are created as governed records during install/tenant bootstrap. Qualify Akka autonomous `AgentDefinition` separately from governed managed-agent `AgentDefinition` when both are in scope. |
| Policy gates and approvals | Workflows plus decision-card records and audit events. |
| Browser APIs | JWT-protected HTTP endpoints using WorkOS authentication and Akka-owned authorization state. |

## Initial implementation and extension slices

### SaaS Foundation App domain

Use the SaaS Foundation App domain when the requested target is the repository's out-of-the-box foundation app, a starter/basic generated SaaS app, or a chatbot-like bootstrap shell. The domain is defined in `./minimum-ai-first-saas-app.md`: My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy. It is not a User-Admin-only slice, a generic public chatbot, or a separate parallel baseline app.

The SaaS Foundation App domain must include all of these together:

- bootstrap-authorized human user only; no public self-registration and no prompt- or UI-only privilege grant;
- selected local `AuthContext` containing account/user identity, bootstrap scope, roles/capabilities, tenant/customer boundary when applicable, and actor metadata;
- role/capability checks for protected workstream, surface, API, component, and agent-tool actions;
- bounded core functional agents for My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy that answer and guide within established bootstrap scope, deny or defer unsupported SaaS Foundation App actions, and never expand privileges autonomously;
- durable workstream log for requests, `markdown_response` entries, capability/tool results, denials, correlation ids, and trace references, with browser rendering that avoids duplicate generic activity/detail surfaces for the same typed result;
- audit/work trace substrate for identity, authorization, prompt/skill/tool use, capability checks, data access, denials, and response generation;
- capability-first backend contracts before exposing browser actions, agent tools, workflows, timers, consumers, or APIs;
- tests for allowed bootstrap access, forbidden access, missing/disabled authority where modeled, trace creation, markdown sanitization, and frontend secret boundaries.

Any intentionally deferred foundation-domain behavior must become explicit follow-up work, such as WorkOS/AuthKit completion, `/api/me`, invitations/onboarding, Resend/outbox email delivery, User Admin depth, Agent Admin governed documents, Audit/Trace search UI, support access, subscription/billing boundary where relevant, or tenant-isolation/security coverage.

### SaaS Foundation App foundation slices

1. WorkOS-backed `/api/me` account bootstrap, membership selection, base profile, and base user settings.
2. Mandatory invitation lifecycle: `Invitation` record, invite token or acceptance context, status, expiry, resend, revoke/cancel, acceptance, delivery status, delivery attempts, audit trail, and idempotent duplicate handling.
3. Mandatory email delivery foundation: Resend (resend.com) production email service configuration, plus a safe local/dev/test adapter that captures messages in an outbox without external delivery. This reusable foundation sends invitation/account emails first and must support future app-specific email features and governed agent `@FunctionTool` email tools.
4. SaaS Owner Admin tenant creation and initial Tenant Admin invitation.
5. Tenant Admin employee invitation and role management.
6. Tenant Admin customer organization creation and Customer Admin invitation.
7. Customer Admin customer user invitation and role management.
8. User profile and settings APIs, starting with editable display profile fields and named UI theme preferences. Themes are named color-token bundles, not dark/light/system modes.
9. SaaS Owner to Tenant subscription creation, plan assignment, status changes, and billing audit.
10. Cross-scope audit trace and access review views.
11. Governed runtime agent foundation: `AgentDefinition` lifecycle/profile state, governed `PromptDocument`/`PromptVersion`, governed `SkillDocument`/`SkillVersion`, per-agent `AgentSkillManifest` with compact skill ids/names/descriptions/when-to-use hints, `ToolPermissionBoundary`, deterministic prompt assembly, authorized Akka `@FunctionTool` `readSkill(skillId)`, `PromptAssemblyTrace`, `SkillLoadTrace`, and `AgentWorkTrace`, plus first-install/tenant-bootstrap governed setup of implementation-developed default agent behavior documents.
12. AI-assisted admin offload: either one governed `UserAdminAgent` with `AgentSkillManifest` skills for access review, admin risk scoring, invitation drafting, role recommendation, support-access review, audit summary, and optional policy proposal drafting, or separate specialized agents such as AccessReviewAgent, AdminRiskAgent, InvitationDraftAgent, RoleRecommendationAgent, SupportAccessReviewAgent, AdminAuditSummaryAgent, and AdminPolicyProposalAgent when useful; route risky role, support-access, tenant suspension, bulk, identity relink, and billing changes to decision cards.
