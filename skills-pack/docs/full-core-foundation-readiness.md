# Full-core foundation readiness

This is the canonical checklist for deciding whether a generated AI-first SaaS app is **full-core ready**. Other skills should reference this file instead of restating the full inventory inline.

## Readiness levels

- **Core app baseline**: a runnable five-core-workstream shell for My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy. It may explicitly defer full onboarding, full admin, governed agent document lifecycle, support access, billing, and complete security coverage.
- **Full-core ready**: the complete secure SaaS foundation below is planned, implemented, tested, and exposed through backend-authorized workstream surfaces before app-specific domain work is treated as ready.
- **App-specific ready**: full-core readiness plus the product/domain functional agents, capabilities, surfaces, tests, and operational reviews.

## Canonical full-core foundation scope

Full-core readiness requires all of these areas unless the task is explicitly non-SaaS reference material:

1. Foundation workstreams and surfaces for My Account, User Admin, Agent Admin, Audit/Trace, Governance/Policy, plus Support Access and Billing where relevant.
2. Common identity and tenancy types: SaaS Owner, Tenant, Customer, Account, UserProfile, UserSettings, Membership, Role, Permission/Capability, Invitation, AuthContext, and audit metadata.
3. WorkOS/AuthKit browser sign-in, WorkOS JWT validation, request-context extraction, and `/api/me` with browser-safe profile, settings, memberships, selected AuthContext, roles/capabilities, and context switching.
4. Backend authorization enforced for protected routes, component commands, view queries, streams, tools, workflow actions, consumers, timers, and UI capability display.
5. Tenant/customer scoped commands and queries that mechanically reject cross-scope access.
6. Complete email-invite onboarding using the mandatory Invitation lifecycle, InvitationWorkflow where workflow coordination is needed, expiry/reminder timers, resend/revoke/acceptance behavior, delivery status, idempotency, and audit.
7. Resend production email delivery plus an explicit safe local/dev/test outbox adapter. Missing Resend configuration blocks production readiness for email-sending features.
8. Full admin operations within authority boundaries: list/search/filter users, view details, manage allowed profile fields, memberships, roles, invitations, disabled/reactivated accounts, identity relinking under policy, support access, and last-admin protection.
9. Required admin read models: UserDirectoryView, MembershipView, InvitationView, AdminAuditView, and AccessReviewQueueView with backend-authorized filters instead of frontend-only filtering.
10. AdminAuditEvent write path for identity, invitation/email, membership/role, support-access, billing, data access, approval, policy, and consequential AI/tool activity.
11. Governed runtime agent foundation: AgentDefinition, PromptDocument/PromptVersion, SkillDocument/SkillVersion, ReferenceDocument/ReferenceVersion, AgentSkillManifest, AgentReferenceManifest, ToolPermissionBoundary, first-install or tenant-bootstrap seed/activation policy, deterministic prompt assembly, authorized readSkill(skillId), authorized readReferenceDoc(referenceId), PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, and AgentWorkTrace.
12. Prompt/skill/reference text remains behavior guidance only; backend authorization, tool boundaries, data boundaries, tenant/customer scope, and approval gates remain authoritative.
13. AI-assisted admin offload through one governed UserAdminAgent or specialized bounded agents for access review, admin-risk scoring, invitation drafting, role recommendations, support-access review, audit summaries, and policy proposal drafting where enabled. High-risk changes route to decision cards rather than autonomous commits.
14. Mandatory web UI shell and foundation surfaces: sign-in, context selection, profile/settings, Users, Invitations, Roles/Memberships, Access Review, Support Access, Admin Audit, Tenant/Customer Settings, Agent Admin, Governance/Policy, recommendation queues, decision cards, and capability-gated actions.
15. Security and readiness tests: tenant isolation, forbidden access, disabled user, role/scope denial, `/api/me`, invitation lifecycle and delivery, admin list/search, membership lifecycle, last-admin protection, audit completeness, support-access lifecycle, admin-agent decision-card boundaries, governed prompt/skill/reference/manifest/tool-boundary traces, billing boundary, surface action authorization, markdown sanitization where used, and frontend secret boundaries.
16. Security review before app-specific domain slices are implemented or marked ready.

## Planning rules

- First SaaS sprint/slice/backlog work must cover the secure foundation before app-specific domain features.
- A core app baseline plan must state that it is not full-core ready and must queue explicit follow-up gates for missing full-core areas.
- Do not collapse Invitation onboarding, email delivery, admin search, governed runtime agents, AI admin offload, workstream UI, audit/trace, or security tests into one vague `auth/admin` or `agent governance` task.
- Do not mark full-core readiness when User Admin is fixture-only, API-only, UI-only, or missing scoped list/search/detail/action paths and negative authorization checks.
- Do not ask provider-selection questions for user auth or production email; WorkOS/AuthKit and Resend are the supported defaults. Ask only for missing runtime settings when needed.
