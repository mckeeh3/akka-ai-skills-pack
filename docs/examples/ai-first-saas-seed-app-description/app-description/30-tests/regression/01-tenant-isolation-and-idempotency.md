# Regression Tests: Tenant Isolation and Idempotency

- same object id in different tenants never crosses data boundaries
- user with membership in tenant A cannot read/write tenant B data
- user directory, membership, invitation, admin audit, and access-review queries remain tenant/customer scoped after projection updates
- repeated invite command for the same target email/scope/role uses the documented idempotency behavior and does not create duplicate active invitations
- repeated resend invite updates delivery attempts and audit facts without losing original invitation history
- repeated revoke invite is safe and does not reactivate or delete audit history
- repeated invitation acceptance is safe and does not duplicate active memberships
- expired and revoked invitations remain impossible to accept after retries, projection rebuilds, or service restart
- membership lifecycle retries for add/suspend/reactivate/remove preserve current status and audit facts
- last-admin protection remains enforced after role replacement/removal retries and view rebuilds
- AccessReviewQueueView remains correct for stale invites, dormant access, support-access expiry, and last-admin risk after projection updates
- AdminRiskAgent, AccessReviewAgent, and RoleRecommendationAgent recommendations do not create side effects when replayed or retried
- repeated approval command on an already decided card is rejected or no-op according to command semantics
- repeated workflow resume after a resolved exception does not duplicate side effects
- view queries remain tenant scoped after projection updates
