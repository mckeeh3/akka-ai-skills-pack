# Realization Scope

- phase-1 secure SaaS foundation:
  - runnable backend/frontend
  - auth seam, tenant context, users, memberships, roles, permissions/capabilities, support-access, `/api/me`, and context switching
  - complete invitation lifecycle: invite, email delivery/outbox, resend, revoke, expire, accept, delivery status, delivery attempts, InvitationView, idempotency, and audit
  - admin management: UserDirectoryView list/search, user detail, MembershipView role/membership lifecycle, AdminAuditView search, AccessReviewQueueView, last-admin protection, support-access grant/revoke/expiry, and scoped backend authorization
  - mandatory AI admin offload: AccessReviewAgent, AdminRiskAgent, InvitationDraftAgent, RoleRecommendationAgent, SupportAccessReviewAgent, AdminAuditSummaryAgent, decision cards for risky admin actions, scoped tools, redaction, and audit/work traces
  - admin UI surfaces for Users, Invitations, Roles/Memberships, Access Review, Support Access, Admin Audit, Tenant/Customer Settings, and admin-agent recommendations
  - foundation tests for invite send/resend/revoke/expire/accept, delivery failure, list/search, membership lifecycle, last-admin protection, AI access review recommendations, decision cards, audit/search views, tenant isolation, forbidden access, disabled users, role/scope denial, and frontend secret boundaries
- phase-2 AI-first work substrate:
  - goals, plans, decision cards, traces, mission control UI
  - workflow and view patterns
- phase-3 agent and governance examples:
  - bounded planning/recommendation/evaluation agents
  - policies, approval gates, policy proposals
- phase-4 integration expansions:
  - consumers, timed actions, realtime updates, digest generation
  - optional MCP/gRPC modules if selected
