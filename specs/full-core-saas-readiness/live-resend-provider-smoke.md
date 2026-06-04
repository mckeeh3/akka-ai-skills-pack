# Live Resend Invite-Email Provider Smoke

- task: TASK-FCSR-08-002
- date: 2026-06-04
- result: passed

## Scope

Validated live invitation-email delivery through the backend-only Resend provider boundary.

No Resend API key, raw invitation token, or provider secret is recorded in this artifact.

## Command

```bash
mvn test -Dtest=InvitationAndUserAdminServiceTest,RealResendProviderSmokeTest -DrealResendProviderSmoke=true
```

Environment variables were supplied by the local shell:

- `RESEND_API_KEY` set, value hidden
- `INVITE_EMAIL_FROM` set, value hidden
- `RESEND_FROM_EMAIL` set, value hidden
- `RESEND_API_BASE_URL` set, value hidden
- `INVITE_EMAIL_SUBJECT` set, value hidden

No explicit smoke recipient variable was set. The live smoke derived a recipient from the configured sender value for this controlled provider-boundary proof.

## Result

The focused test run passed:

- `InvitationAndUserAdminServiceTest`: 11 tests, 0 failures, 0 errors, 0 skipped
- `RealResendProviderSmokeTest`: 1 test, 0 failures, 0 errors, 0 skipped
- Maven build result: `BUILD SUCCESS`

## Runtime path proven

`RealResendProviderSmokeTest` creates a tenant-scoped invitation and queued invitation-email outbox record, then delivers it through production mode:

```text
InvitationService.createInvitation
→ EmailOutboxMessage queued with raw token confined to delivery boundary
→ ResendEmailService.deliver(..., DeliveryMode.PRODUCTION)
→ Resend HTTP provider adapter
→ provider accepted message id
→ InvitationService.recordDeliveryResult
→ Invitation status SENT / EmailDeliveryStatus.SENT
→ Admin audit event INVITATION_DELIVERY_SENT
```

## Secret/token-boundary evidence

The live smoke asserts that the backend-only Resend API key is not present in:

- delivery result DTO;
- invitation state;
- queued outbox message;
- admin audit events.

The existing invitation tests also continue to assert local/test captured outbox behavior, production fail-closed behavior when Resend config is missing, idempotency, lifecycle status transitions, safe browser recovery states, and audit recording.

## Implementation note

The first live attempt returned a safe `resend-http-422` provider response. The provider-boundary request body was corrected so `headers` is sent as a JSON object rather than an invalid string. The focused live smoke then passed.
