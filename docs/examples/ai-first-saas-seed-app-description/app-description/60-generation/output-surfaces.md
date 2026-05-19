# Output Surfaces

- backend:
  - Akka Java SDK components and tests
  - HTTP endpoints for browser APIs
  - foundation components for Account, UserProfile, UserSettings, Tenant, Customer, Membership, Role/Permission, complete Invitation lifecycle, support-access, `/api/me`, authorization, and AdminAuditEvent
  - first-slice views: UserDirectoryView, MembershipView, InvitationView, AdminAuditView, and AccessReviewQueueView
  - AI admin agents: AccessReviewAgent, AdminRiskAgent, InvitationDraftAgent, RoleRecommendationAgent, SupportAccessReviewAgent, and AdminAuditSummaryAgent with decision-card routing for risky admin actions
  - optional gRPC/MCP modules in later phases
- frontend:
  - React + Vite + TypeScript agent workstream shell
  - role-authorized functional-agent rail, continuous workstream panel, persistent composer, context/authority indicators, and route/deep-link support
  - typed client and structured surface rendering system
  - reusable UI primitives and app shell
  - Access/Profile, User Admin, Agent Admin, Mission Control, Governance/Policy, and Audit/Trace surfaces
  - admin surfaces for Users, Invitations, Roles/Memberships, Access Review, Support Access, Admin Audit, Tenant/Customer Settings, admin-agent recommendations, and decision-card review
- docs:
  - runnable setup instructions
  - component reference map for skills-pack guidance
  - module-extension guide
