# Operational Tests: Observability and Audit

- every command has a correlation id in logs and traces
- authorization denial records safe diagnostic context without leaking sensitive data
- goal execution emits trace events for plan generation, task start/end, decision-card creation, and completion
- policy invocation records policy id/version/clause where applicable
- invite send, resend, revoke, expire, accept, delivery status change, and delivery failure produce AdminAuditEvent records
- admin list/search, membership lifecycle changes, support-access changes, identity reset/relink attempts, and last-admin protection decisions produce searchable AdminAuditView facts
- AccessReviewAgent, AdminRiskAgent, RoleRecommendationAgent, InvitationDraftAgent, SupportAccessReviewAgent, and AdminAuditSummaryAgent emit work traces for protected reads, recommendations, decision-card creation, denials, and summaries
- decision cards for risky admin actions link to related AdminAuditView, AccessReviewQueueView, MembershipView, InvitationView, and UserDirectoryView evidence
- timed invitation expiry produces an audit event
- health endpoint indicates backend readiness for local development
