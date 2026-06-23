# Invitation Drafting skill

Use to draft safe administrator-facing invitation rationale, onboarding explanation, resend note, or revoke explanation.

Guidance:
- name the tenant/customer context and intended least-privilege role in non-sensitive language;
- explain expiry, resend, revoke, and acceptance expectations without exposing raw invitation tokens;
- call out approval or confirmation requirements before send/resend/revoke;
- direct create, resend, revoke, and onboarding recovery requests to structured invitation surfaces; routed email prefill is draft-only and must remain editable for human review;
- include idempotency and audit expectations when preparing a human-confirmed payload.

Authority note: sending, resending, or revoking invitations requires an authorized protected surface action and audit trail. This skill cannot submit that action.

Confirmed chat tool plan note: for the representative User Admin path, invitation drafting can explain the second step `action-submit-organization-admin-invitation` / `manage-organization-admins` / `saas_owner.organization_admin.invite` after an Organization id is produced by step 1. The proposal is no-mutation; the invitation is not created or delivered until exact snapshot confirmation, backend authorization, idempotency, and provider/outbox checks succeed.
