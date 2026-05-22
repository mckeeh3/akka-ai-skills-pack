# Flow: Onboarding and Access

## Invitation lifecycle

1. Tenant admin, Customer Admin, or SaaS Owner Admin invites a user within their authority boundary with one or more roles.
2. System validates scope, role-grant authority, tenant/customer status, last-admin risk, duplicate email state, and idempotency key.
3. System creates or reuses a pending invitation, records target email, roles, expiry, inviter, delivery status, delivery attempts, and emits an AdminAuditEvent.
4. Resend email delivery/outbox sends the invite through Resend (resend.com) in production; local/dev/test uses an explicit captured outbox adapter.
5. Admin can view invitation status in InvitationView, resend invite after delivery failure or stale delivery, and revoke invite before acceptance.
6. Delivery failure remains visible to admins with delivery status and delivery attempts; it is never silently treated as a successful invite.
7. Expired invitations cannot be accepted and are marked by timed action; revoked invitations cannot be accepted and retain audit history.
8. Invited user authenticates through configured identity flow and submits the invite token or acceptance context.
9. System verifies invitation token, expiry, revoked state, tenant/customer status, duplicate acceptance, and role assignment authority.
10. Membership becomes active after acceptance; repeated acceptance is idempotent and does not duplicate memberships.
11. User can switch only among active memberships returned by `/api/me`.
12. Removed/suspended memberships immediately lose tenant/customer access.

## Admin user and access management

1. Admin discovers users through UserDirectoryView list/search without caller-supplied user ids.
2. Admin opens user detail with account/profile, memberships, roles, invitation status, support-access grants, and audit links.
3. Admin may edit allowed profile fields, assign/replace/remove roles, add/suspend/reactivate/remove memberships, disable/reactivate accounts, and reset/relink identity subject only when policy permits.
4. Last-admin protection blocks or routes risky admin removal, suspension, or role removal to decision-card review.
5. AdminAuditView records actor, target user, action type, tenant/customer, role, membership status, invitation status, delivery status, risk, policy metadata, and time range.
6. AccessReviewQueueView surfaces stale invitations, dormant access, risky role combinations, expiring support access, orphaned admin coverage, and last-admin risks.

## AI-assisted admin offload

1. Runtime resolves an active `AgentDefinition` for the admin assistant or `UserAdminAgent`, assembles active governed `PromptDocument`/`PromptVersion` content, includes compact `AgentSkillManifest` entries, enforces `ToolPermissionBoundary`, and creates `PromptAssemblyTrace` before work begins.
2. When the admin asks to open dashboard, `UserAdminAgent` loads `user-admin-dashboard` through scoped dashboard/read capabilities, summarizes invitations, users, support access, access-review risk, and recent audit evidence, and records the surface load in `AgentWorkTrace`.
3. When the admin asks to search users or list users, `UserAdminAgent` calls authorized UserDirectoryView-backed search/list tools for the selected `AuthContext`, explains pagination/redaction/empty/forbidden states, and returns a `user-admin-user-list` surface payload rather than asking the caller for known user ids.
4. When the admin asks to open a user account, `UserAdminAgent` resolves the selected row through scoped detail capability, opens `user-admin-user-account`, explains allowed and denied actions, and links visible account, membership, invitation, support-access, access-review, and audit evidence.
5. Access-review responsibility scans scoped UserDirectoryView, MembershipView, InvitationView, AdminAuditView, and AccessReviewQueueView for stale invites, dormant access, failed delivery, support-access expiry, and last-admin risk.
6. Admin-risk-scoring responsibility scores proposed high-risk access changes and creates decision cards when policy requires human review.
7. Invitation-drafting responsibility returns an `InvitationDraft` with invite copy and role rationale without exposing raw tokens; if granted email tools, it may preview approved Resend templates and may queue email only through governed `@FunctionTool` capability checks, approval policy, idempotency, and traces.
8. Role-recommendation responsibility returns a `RoleRecommendation` with least-privilege roles, evidence, confidence, policy caveats, and escalation warnings.
9. Support-access-review responsibility recommends support-access expiry or revocation candidates.
10. Admin-audit-summary responsibility returns an `AdminAuditSummary` for selected admin audit/search results with links to audit traces and decision cards.
11. Responsibilities may be implemented by one governed skilled `UserAdminAgent` or by specialized agents; in both cases, full skill text loads require authorized `readSkill(skillId)` and create `SkillLoadTrace`.
12. Agents may draft, summarize, recommend, and create low-risk tasks, but consequential mutations default to human-confirmed capability calls or decision-card flows; agents must not autonomously grant admin roles, remove last admins, expand support access, bulk disable users, reset/relink identity, or change policy/permissions.
13. Tool boundaries default to read/list/detail/summarize within the selected `AuthContext`; denied tool calls for cross-tenant data, Customer Admin Tenant-level actions, SaaS Owner no-support-access access, disabled actors, missing roles/capabilities, role escalation, last-admin loss, raw invitation tokens, provider secrets, and unredacted out-of-scope data are safe denials.
14. Consequential recommendations, tool/data access, denials, decisions, approvals, surface/action outcomes, and prompt/skill activity create `AgentWorkTrace`, `PromptAssemblyTrace`, and `SkillLoadTrace` records.

## UserAdminAgent trace and verification obligations

- Prompt assembly tests verify `AgentDefinition`, `PromptDocument`/`PromptVersion`, compact `AgentSkillManifest`, `ToolPermissionBoundary`, and `PromptAssemblyTrace` before dashboard/list/detail work begins.
- Skill-load tests verify authorized and denied `readSkill(skillId)` calls create `SkillLoadTrace` without leaking full skill text outside caller authority.
- Tool-boundary tests verify dashboard read, search/list users, open user account, `InvitationDraft`, `RoleRecommendation`, `AdminAuditSummary`, and decision-card creation are allowed only when the selected `AuthContext` has the matching capability.
- Denial tests cover disabled actor, missing capability, cross-tenant access, Customer Admin Tenant-level action, SaaS Owner without support access, role escalation, last-admin protection, raw token exposure, and provider secret exposure.
- Surface outcome tests verify `user-admin-dashboard`, `user-admin-user-list`, and `user-admin-user-account` carry correlation ids, trace ids, audit links, redaction markers, allowed/denied action metadata, and forbidden/empty/error/stale state behavior.
