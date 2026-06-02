# Realization Scope

- phase-1 secure SaaS foundation:
  - runnable backend/frontend
  - WorkOS/AuthKit authentication, tenant context, users, memberships, roles, permissions/capabilities, support-access, `/api/me`, and context switching
  - complete invitation lifecycle: invite, email delivery/outbox, resend, revoke, expire, accept, delivery status, delivery attempts, event-sourced lifecycle history, InvitationView, idempotency, and audit
  - admin management: UserDirectoryView list/search, user detail, MembershipView role/membership lifecycle, AdminAuditView search, AccessReviewQueueView, last-admin protection, support-access grant/revoke/expiry, and scoped backend authorization
  - mandatory AI admin offload: governed UserAdminAgent responsibilities or specialized AccessReviewAgent, AdminRiskAgent, InvitationDraftAgent, RoleRecommendationAgent, SupportAccessReviewAgent, AdminAuditSummaryAgent, decision cards for risky admin actions, scoped tools, redaction, and audit/work traces
  - managed-agent foundation: AgentDefinition, PromptDocument/PromptVersion, SkillDocument/SkillVersion, AgentSkillManifest, ToolPermissionBoundary, implementation-developed default behavior seed bundle loaded into governed storage on first install/tenant bootstrap, AgentBehaviorEditorAgent proposals, authorized readSkill(skillId), PromptAssemblyTrace, SkillLoadTrace, and AgentWorkTrace
  - admin UI/API surfaces for Users, Invitations, Roles/Memberships, Access Review, Support Access, Admin Audit, Tenant/Customer Settings, admin-agent recommendations, agent catalog/detail, prompt/skill governance, skill manifests, tool permissions, edit-agent proposal review, managed-agent trace exploration, and the provider-neutral digest/export request lifecycle foundation
  - foundation tests for invite send/resend/revoke/expire/accept, delivery failure, list/search, membership lifecycle, last-admin protection, AI access review recommendations, decision cards, audit/search views, tenant isolation, forbidden access, disabled users, role/scope denial, frontend secret boundaries, default behavior seed import, seed idempotency, customization-preserving seed upgrades, unauthorized prompt/skill/tool changes, disabled-agent denial, unassigned skill denial, trace creation, and approval-required authority expansion
- phase-2 AI-first work substrate:
  - goals, plans, decision cards, traces, mission control UI
  - workflow and view patterns
- phase-3 agent and governance examples:
  - bounded planning/recommendation/evaluation agents
  - policies, approval gates, policy proposals
- phase-4 integration expansions:
  - consumers, timed actions, realtime updates, digest generation, export artifact/provider delivery extensions beyond the current redacted local digest/export request handles
  - optional MCP/gRPC modules if selected
