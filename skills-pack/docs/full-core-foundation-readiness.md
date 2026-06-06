# SaaS Foundation App scope and extension readiness

This compatibility document replaces the old readiness-tier split. The current repository model is simpler:

- the repository ships a fully functional **SaaS Foundation App** out of the box;
- that app contains the built-in SaaS foundation domain with five workstreams: My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy;
- users clone or fork the repository and extend the app with business-specific domains, workstreams, surfaces, agents, capabilities, Akka components, frontend assets, app-description artifacts, specs, docs, and tests;
- the foundation domain can be changed like any other domain when product needs require it, while preserving tenant/customer scope, backend authorization, durable traces, and merge-friendly extension seams.

Keep this file name for existing references, but do not introduce new planning language that treats the foundation as competing readiness tiers.

## Expected built-in scope

The out-of-the-box app and foundation-domain maintenance work should preserve these areas unless a task explicitly removes or changes them:

1. Foundation workstreams and surfaces for My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy.
2. Common identity and tenancy types: SaaS Owner, Tenant, Customer, Account, UserProfile, UserSettings, Membership, Role, Permission/Capability, Invitation, AuthContext, and audit metadata.
3. WorkOS/AuthKit browser sign-in, WorkOS JWT validation, request-context extraction, and `/api/me` with browser-safe profile, settings, memberships, selected AuthContext, roles/capabilities, and context switching.
4. Backend authorization enforced for protected routes, component commands, view queries, streams, tools, workflow actions, consumers, timers, and UI capability display.
5. Tenant/customer scoped commands and queries that mechanically reject cross-scope access.
6. Email-invite onboarding using Invitation lifecycle, expiry/reminder behavior where modeled, resend/revoke/acceptance behavior, delivery status, idempotency, and audit.
7. Resend production email delivery plus explicit safe local/dev/test outbox behavior. Missing Resend configuration blocks production email-sending readiness.
8. Admin operations within authority boundaries: list/search/filter users, view details, manage allowed profile fields, memberships, roles, invitations, disabled/reactivated accounts, identity relinking under policy, support access, and last-admin protection.
9. Admin read models such as UserDirectoryView, MembershipView, InvitationView, AdminAuditView, and AccessReviewQueueView where those surfaces are implemented, with backend-authorized filters instead of frontend-only filtering.
10. AdminAuditEvent and work-trace paths for identity, invitation/email, membership/role, support-access, billing, data access, approval, policy, and consequential AI/tool activity.
11. Governed runtime agent foundation: AgentDefinition, PromptDocument/PromptVersion, SkillDocument/SkillVersion, ReferenceDocument/ReferenceVersion, AgentSkillManifest, AgentReferenceManifest, ToolPermissionBoundary, first-install or tenant-bootstrap seed/activation policy, deterministic prompt assembly, authorized `readSkill(skillId)`, authorized `readReferenceDoc(referenceId)`, PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, and AgentWorkTrace.
12. Prompt/skill/reference text remains behavior guidance only; backend authorization, tool boundaries, data boundaries, tenant/customer scope, and approval gates remain authoritative.
13. AI-assisted admin offload through bounded agents and/or autonomous tasks for access review, admin-risk scoring, invitation drafting, role recommendations, support-access review, audit summaries, policy proposal drafting, and similar governed work where enabled. High-risk changes route to decision cards rather than autonomous commits.
14. Workstream shell and foundation surfaces: sign-in, context selection, profile/settings, users, invitations, roles/memberships, access review, support access, admin audit, tenant/customer settings where modeled, Agent Admin, Governance/Policy, recommendation queues, decision cards, and capability-gated actions.
15. Security and readiness tests: tenant isolation, forbidden access, disabled user, role/scope denial, `/api/me`, invitation lifecycle and delivery, admin list/search, membership lifecycle, last-admin protection, audit completeness, support-access lifecycle, admin-agent decision-card boundaries, governed prompt/skill/reference/manifest/tool-boundary traces, billing boundary, surface action authorization, markdown sanitization where used, and frontend secret boundaries.

## Extension-readiness rules

- Do not create a separate baseline app when working in this repository or a downstream fork. Extend the SaaS Foundation App.
- First classify new work by affected domain, workstream, surface, functional/internal agent, governed capability, and Akka component family.
- Business-specific features should land in business/domain extension zones, while reusable platform/security/identity/managed-agent runtime code remains in foundation and built-in five-workstream code remains in the foundation app domain/coreapp areas.
- When modifying the SaaS Foundation App domain itself, treat it as normal product code: preserve authorization, traces, tests, and app-description/spec alignment.
- Do not collapse invitation onboarding, email delivery, admin search, governed runtime agents, AI admin offload, workstream UI, audit/trace, or security tests into vague `auth/admin` or `agent governance` tasks.
- Do not call a feature complete when it is fixture-only, API-only, UI-only, or missing required negative authorization checks for its stated scope.
- WorkOS/AuthKit and Resend remain the supported defaults for user auth and production email unless a task explicitly changes pack policy.

## Prescriptive implementation architecture

The skills pack translates app feature intent through a fixed architecture:

```text
feature intent
→ domain/workstream/surface/agent responsibility
→ governed capability and tool contract
→ selected Akka component families
→ backend implementation, endpoints/tools, traces, tests
→ workstream shell surfaces and frontend/API contracts
→ local Akka/API/UI validation
```

The component family set is intentionally narrow and prescriptive: Event Sourced Entity, Key Value Entity, Workflow, request-based Agent, Autonomous Agent, View, Consumer, Timed Action, HTTP Endpoint, gRPC Endpoint, and MCP Endpoint. These components implement business functionality and also provide the backend-authorized tools that agents may use.
