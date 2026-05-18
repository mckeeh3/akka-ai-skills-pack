# Realization Scope

- phase-1 secure SaaS foundation:
  - runnable backend/frontend
  - auth seam, tenant context, users, memberships, roles, permissions/capabilities, support-access, `/api/me`, and context switching
  - complete invitation lifecycle: invite, email delivery/outbox, resend, revoke, expire, accept, delivery status, delivery attempts, InvitationView, idempotency, and audit
  - admin management: UserDirectoryView list/search, user detail, MembershipView role/membership lifecycle, AdminAuditView search, AccessReviewQueueView, last-admin protection, support-access grant/revoke/expiry, and scoped backend authorization
  - mandatory AI admin offload: governed UserAdminAgent responsibilities or specialized AccessReviewAgent, AdminRiskAgent, InvitationDraftAgent, RoleRecommendationAgent, SupportAccessReviewAgent, AdminAuditSummaryAgent, decision cards for risky admin actions, scoped tools, redaction, and audit/work traces
  - managed-agent foundation: AgentDefinition, PromptDocument/PromptVersion, SkillDocument/SkillVersion, AgentSkillManifest, ToolPermissionBoundary, AgentBehaviorEditorAgent proposals, authorized readSkill(skillId), PromptAssemblyTrace, SkillLoadTrace, and AgentWorkTrace
  - admin UI surfaces for Users, Invitations, Roles/Memberships, Access Review, Support Access, Admin Audit, Tenant/Customer Settings, admin-agent recommendations, agent catalog/detail, prompt/skill governance, skill manifests, tool permissions, edit-agent proposal review, and managed-agent trace exploration
  - foundation tests for invite send/resend/revoke/expire/accept, delivery failure, list/search, membership lifecycle, last-admin protection, AI access review recommendations, decision cards, audit/search views, tenant isolation, forbidden access, disabled users, role/scope denial, frontend secret boundaries, unauthorized prompt/skill/tool changes, disabled-agent denial, unassigned skill denial, trace creation, and approval-required authority expansion
- phase-2 AI-first work substrate:
  - goals, plans, decision cards, traces, mission control UI
  - workflow and view patterns
- phase-3 agent and governance examples:
  - bounded planning/recommendation/evaluation agents
  - policies, approval gates, policy proposals
- phase-4 integration expansions:
  - consumers, timed actions, realtime updates, digest generation
  - optional MCP/gRPC modules if selected
