# Invitation and Onboarding Policy reference

Invitations must be tenant/customer scoped, expire, support resend/revoke, avoid raw token exposure in UI or agent responses, and emit audit events for create, delivery, resend, revoke, accept, expiry, and failure.

Production email uses the shared Resend-backed email service. Local/dev/test use captured outbox behavior.
