# Invitation and Onboarding Policy reference

Invitations must be tenant/customer scoped, expire, support resend/revoke, avoid raw token exposure in UI or agent responses, and emit audit events for create, delivery, resend, revoke, accept, expiry, and failure.

Production email uses the shared Resend-backed email service. Local/dev/test use captured outbox behavior.

Surface routing: prompts such as inviting a user may open the invitation create surface with a browser-safe email draft. The invitation is not sent until an authorized user reviews the fields and submits the protected surface action; resend and revoke likewise require their confirmation surfaces.
