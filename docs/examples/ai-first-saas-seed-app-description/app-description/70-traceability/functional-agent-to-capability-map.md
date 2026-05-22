# Functional Agent to Capability Map

| Functional agent | Surfaces | Capabilities |
|---|---|---|
| Access/Profile | `access-profile-dashboard` | `secure-tenant-user-foundation`, `frontend-shell-integration-patterns` |
| User Admin | `user-admin-dashboard`, `user-admin-user-list`, `user-admin-user-account`, `decision-card`, `audit-trace-explorer` | `secure-tenant-user-foundation` capability family, including `admin.users.dashboard.read`, `admin.users.search`, `admin.users.detail.read`, `admin.users.profile.patch`, `admin.users.disable`, `admin.users.reactivate`, `admin.users.identity_relink.request`, `admin.users.identity_relink.complete`, `admin.invitations.create`, `admin.invitations.resend`, `admin.invitations.revoke`, `admin.memberships.add`, `admin.memberships.suspend`, `admin.memberships.reactivate`, `admin.memberships.remove`, `admin.roles.replace`, `admin.roles.remove`, `admin.support_access.read`, `admin.support_access.grant`, `admin.support_access.revoke`, `admin.support_access.extend`, `admin.access_review.read`, `admin.access_review.resolve`, `admin.audit.read`; approval/risky flows → `governance-decisions-audit`; shell state → `frontend-shell-integration-patterns` |
| Agent Admin | `agent-governance-center`, `decision-card`, `audit-trace-explorer` | `managed-agent-foundation`, `governance-decisions-audit`, `frontend-shell-integration-patterns` |
| Mission Control | `mission-control-briefing`, `decision-card` | `ai-first-work-management`, `governance-decisions-audit`, `frontend-shell-integration-patterns` |
| Governance/Policy | `agent-governance-center`, `decision-card`, policy/governance surfaces | `governance-decisions-audit`, `managed-agent-foundation`, `frontend-shell-integration-patterns` |
| Audit/Trace | `audit-trace-explorer` | `governance-decisions-audit`, `managed-agent-foundation`, `secure-tenant-user-foundation`, `frontend-shell-integration-patterns` |
